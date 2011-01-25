package org.jace.peer;

import com.google.common.collect.Lists;
import org.jace.metaclass.TypeNameFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses byte code enhancement to inject C++ peer lifetime management
 * code into a Java class.
 *
 * For more information about peer enhancement, see the Jace Developer's Guide.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class PeerEnhancer
{
	private static final String newLine = System.getProperty("line.separator");
	private final Logger log = LoggerFactory.getLogger(PeerEnhancer.class);
	private File inputFile;
	private File outputFile;
	private List<String> libraries;
	private String deallocationMethod;
	private final boolean verbose;
	private static final String jaceHandleField = "jaceNativeHandle";
	private static final String jaceCreateInstance = "jaceCreateInstance";
	private static final String jaceDestroyInstance = "jaceDestroyInstance";
	private static final String jaceGetNativeHandle = "jaceGetNativeHandle";
	private static final String jaceSetNativeHandle = "jaceSetNativeHandle";
	private static final String jaceSetVm = "jaceSetVm";
	private static final String jaceDispose = "jaceDispose";
	private static final String jaceUserStaticInit = "jaceUserStaticInit";
	private static final String jaceUserFinalize = "jaceUserFinalize";
	private static final String jaceUserClose = "jaceUserClose";

	/**
	 * Builds a PeerEnhancer.
	 *
	 * @author Gili Tzabari
	 */
	@SuppressWarnings("PublicInnerClass")
	public static final class Builder
	{
		private final File from;
		private final File to;
		private final List<String> libraries = Lists.newArrayList();
		private String deallocationMethod;
		private boolean verbose;

		/**
		 * Creates a new Builder.
		 *
		 * @param from the path of the peer before it has been enhanced
		 * @param to the path of the peer after it has been enhanced
		 * @throws IllegalArgumentException if <code>from</code> or <code>to</code> are null
		 */
		public Builder(File from, File to) throws IllegalArgumentException
		{
			if (from == null)
				throw new IllegalArgumentException("from may not be null");
			if (to == null)
				throw new IllegalArgumentException("to may not be null");
			this.from = from;
			this.to = to;
		}

		/**
		 * Adds a library to be loaded by the peer.
		 *
		 * @param name the library name
		 * @return the Builder
		 */
		public Builder library(String name)
		{
			libraries.add(name);
			return this;
		}

		/**
		 * Indicates the name of the peer deallocation method.
		 *
		 * @param name the method name, or null if there is no deallocation method. The default is null.
		 * @return the Builder
		 * @throws IllegalArgumentException if name is an empty string
		 */
		public Builder deallocationMethod(String name) throws IllegalArgumentException
		{
			if (name != null && name.trim().isEmpty())
				throw new IllegalArgumentException("name may not be an empty String");
			this.deallocationMethod = name;
			return this;
		}

		/**
		 * Indicates if the peer should log lifecycle events.
		 *
		 * @param value true if the peer should log lifecycle events. The default is false.
		 * @return the Builder
		 */
		public Builder verbose(boolean value)
		{
			this.verbose = value;
			return this;
		}

		/**
		 * Enhances the peer.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		public void enhance() throws IOException
		{
			new PeerEnhancer(this).run();
		}
	}

	/**
	 * Creates a new PeerEnhancer.
	 *
	 * @param builder an instance of <code>PeerEnhancer.Builder</code>
	 */
	@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
	private PeerEnhancer(Builder builder)
	{
		assert (builder != null);
		this.inputFile = builder.from;
		this.outputFile = builder.to;
		this.libraries = builder.libraries;
		this.deallocationMethod = builder.deallocationMethod;
		this.verbose = builder.verbose;
	}

	/**
	 * Invoke <code>jaceSetNativeHandle(jaceCreateInstance())</code> before returning from any
	 * non-chaining constructors.
	 *
	 * @param classNode the class to parse
	 */
	private void enhanceConstructors(ClassNode classNode)
	{
		for (Object o: classNode.methods)
		{
			MethodNode method = (MethodNode) o;
			if (method.name.equals("<init>"))
			{
				boolean isChained = false;
				for (Iterator<?> i = method.instructions.iterator(); i.hasNext();)
				{
					AbstractInsnNode instruction = (AbstractInsnNode) i.next();
					if (instruction.getType() != AbstractInsnNode.METHOD_INSN)
						continue;
					MethodInsnNode methodInvocation = (MethodInsnNode) instruction;
					if (methodInvocation.owner.equals(classNode.name)
							&& methodInvocation.name.equals("<init>"))
					{
						isChained = true;
						break;
					}
				}
				if (isChained)
					continue;

				// DESIGN:
				// - This code initializes the native handle used by "org::jace::getPeer(this)"
				// - Ideally we'd like to initialize the native handle after the Java object is fully constructed.
				//   Unfortunately, we have no way of preventing native methods from being invoked by the constructor
				//   itself.
				// - The earliest we can inject code is after a call to super().
				// - We don't have to worry about native methods getting invoked as arguments of super() because
				//   static native methods do not depend upon the native handle.

				boolean matchFound = false;
				for (Iterator<?> i = method.instructions.iterator(); i.hasNext();)
				{
					AbstractInsnNode instruction = (AbstractInsnNode) i.next();
					switch (instruction.getType())
					{
						case AbstractInsnNode.METHOD_INSN:
						{
							MethodInsnNode methodInvocation = (MethodInsnNode) instruction;
							if (methodInvocation.owner.equals(classNode.superName) && methodInvocation.name.equals(
								"<init>"))
								method.instructions.insert(instruction, getJaceSetNativeHandle(classNode.name));
							matchFound = true;
							break;
						}

						case AbstractInsnNode.INSN:
						{
							if (instruction.getOpcode() == Opcodes.RETURN)
								method.instructions.insert(instruction, getJaceSetNativeHandle(classNode.name));
							matchFound = true;
							break;
						}
					}
					if (matchFound)
						break;
				}

				if (!matchFound)
					throw new AssertionError();
			}
		}
	}

	/**
	 * Returns a list of instructions for the following code:
	 *
	 * <code>jaceSetNativeHandle(jaceCreateInstance())</code>.
	 *
	 * @param className the name of the class being enhanced
	 * @return a list of instructions
	 */
	private static InsnList getJaceSetNativeHandle(String className)
	{
		InsnList result = new InsnList();
		// Load "this" onto the stack
		result.add(new VarInsnNode(Opcodes.ALOAD, 0));
		result.add(new InsnNode(Opcodes.DUP));
		// Stack now contains: [this, this]

		// Invoke jaceCreateInstance()
		result.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, className, jaceCreateInstance,
			Type.getMethodDescriptor(Type.LONG_TYPE, new Type[0])));

		// Stack now contains: [this, result of jaceCreateInstance()]
		// Invoke jaceSetNativeHandle(jaceSetNativeHandle())
		result.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, className, jaceSetNativeHandle,
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]
			{
				Type.LONG_TYPE
			})));
		return result;
	}

	/**
	 * Renames the deallocation method to jaceUserClose().
	 *
	 * @param classNode the class to parse
	 * @return null if the method was not found
	 */
	@SuppressWarnings("unchecked")
	private void enhanceDeallocationMethod(ClassNode classNode)
	{
		if (deallocationMethod == null)
			return;
		for (Object o: classNode.methods)
		{
			MethodNode method = (MethodNode) o;
			if (method.name.equals(deallocationMethod))
			{
				method.name = "jaceUserClose";
				classNode.methods.add(createDeallocationMethod(classNode, method));
				return;
			}
		}
		throw new RuntimeException("Unable to locate the method: " + TypeNameFactory.fromPath(
			classNode.name).asIdentifier()
															 + "." + deallocationMethod + "." + newLine
															 + "Peer enhancement will now stop.");
	}

	/**
	 * Create a new deallocation method.
	 *
	 * @param classNode the enclosing class
	 * @param userMethod the user's deallocation method
	 * @return the deallocation method
	 */
	@SuppressWarnings("unchecked")
	private MethodNode createDeallocationMethod(ClassNode classNode, MethodNode userMethod)
	{
		String[] exceptions = (String[]) userMethod.exceptions.toArray(new String[0]);
		MethodNode result = new MethodNode(Opcodes.ACC_PUBLIC, deallocationMethod,
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]),
			null, exceptions);

		// Place copy of 'this' on the stack
		result.visitVarInsn(Opcodes.ALOAD, 0);

		// Invoke the user's deallocation method
		int invocationType;
		if ((userMethod.access & Opcodes.ACC_STATIC) != 0)
			invocationType = Opcodes.INVOKESTATIC;
		else if ((userMethod.access & Opcodes.ACC_PRIVATE) != 0)
			invocationType = Opcodes.INVOKESPECIAL;
		else
			invocationType = Opcodes.INVOKEVIRTUAL;
		result.visitMethodInsn(invocationType, classNode.name, jaceUserClose,
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

		// Place copy of 'this' on the stack
		result.visitVarInsn(Opcodes.ALOAD, 0);

		// Invoke jaceDispose()
		result.visitMethodInsn(Opcodes.INVOKESPECIAL, classNode.name,
			jaceDispose, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

		result.visitInsn(Opcodes.RETURN);
		return result;
	}

	/**
	 * Enhances the class file.
	 *
	 * @throws IOException if an I/O error occurs while enhancing the file
	 */
	private void run() throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(in);
			classReader.accept(classNode, 0);

			// Check whether jaceGetNativeHandle is already defined.
			for (Object o: classNode.methods)
			{
				MethodNode method = (MethodNode) o;
				if (method.name.equals(jaceGetNativeHandle))
				{
					log.info("The class " + inputFile + " has already been enhanced and will not be modified.");
					return;
				}
			}

			enhanceConstructors(classNode);
			enhanceDeallocationMethod(classNode);
			enhanceInitializer(classNode);
			addNativeHandle(classNode);
			addNativeLifetimeMethods(classNode);
			addDeallocation(classNode);
			enhanceFinalizer(classNode);

			File parentPath = outputFile.getParentFile();
			if (parentPath != null && !parentPath.exists() && !parentPath.mkdirs())
			{
				log.warn("Could not create " + parentPath);
				return;
			}

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(classWriter);
			out.write(classWriter.toByteArray());
			out.close();
		}
		finally
		{
			in.close();
		}
	}

	/**
	 * Create the following code:
	 *
	 * private native static void jaceSetVm();
	 *
	 * static
	 * {
	 *   try
	 *   {
	 *     for (String library: libraries) {
	 *       System.loadLibrary( library );
	 *     }
	 *     jaceSetVm();
	 *   }
	 *   catch ( Throwable t ) {
	 *     t.printStackTrace();
	 *     throw new RuntimeException( t.toString() );
	 *   }
	 *
	 *   jaceUserStaticInit(); // The user's initializer. This line is only
	 *                         // present if the class already had an initializer.
	 * }
	 *
	 * @param classNode the class to enhance
	 */
	@SuppressWarnings("unchecked")
	private void enhanceInitializer(ClassNode classNode)
	{
		// Look for an existing class initializer
		boolean userInitializerExists = false;
		for (Object o: classNode.methods)
		{
			MethodNode method = (MethodNode) o;
			if (method.name.equals("<clinit>"))
			{
				// Rename the user's class initializer to jaceUserStaticInit()
				method.name = jaceUserStaticInit;
				method.access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;
				userInitializerExists = true;
				break;
			}
		}

		// Create method "private native static void jaceSetVm()"
		classNode.methods.add(new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_NATIVE
																				 | Opcodes.ACC_STATIC, jaceSetVm,
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null));

		// Create class initializer
		MethodNode classInitializer = new MethodNode(Opcodes.ACC_STATIC, "<clinit>",
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);
		classNode.methods.add(classInitializer);

		// try {
		Label beginTryBlock = new Label();
		classInitializer.visitLabel(beginTryBlock);

		for (String library: libraries)
		{
			if (verbose)
			{
				// Push System.err onto the stack
				classInitializer.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System",
					"err", "Ljava/io/PrintStream;");

				// Push text onto the stack
				classInitializer.visitLdcInsn(classNode.name + ": Loading " + library + "...");

				// Invoke System.err.println
				classInitializer.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
					"println", Type.getMethodDescriptor(Type.VOID_TYPE,
					new Type[]
					{
						Type.getType(String.class)
					}));
			}

			// Push library name onto stack
			classInitializer.visitLdcInsn(library);

			// Invoke System.loadLibrary()
			classInitializer.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
				"loadLibrary", Type.getMethodDescriptor(Type.VOID_TYPE,
				new Type[]
				{
					Type.getType(String.class)
				}));
		}

		if (verbose)
		{
			// Push System.err onto the stack
			classInitializer.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System",
				"err", "Ljava/io/PrintStream;");

			// Push text onto the stack
			classInitializer.visitLdcInsn(classNode.name + ": Done loading native libraries...");

			// Invoke System.err.println
			classInitializer.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
				"println", Type.getMethodDescriptor(Type.VOID_TYPE,
				new Type[]
				{
					Type.getType(String.class)
				}));
		}

		// Invoke jaceSetVm()
		classInitializer.visitMethodInsn(Opcodes.INVOKESTATIC, classNode.name,
			jaceSetVm, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

		// }
		Label endTryBlock = new Label();
		classInitializer.visitLabel(endTryBlock);

		// We've reached the end of the try block, now skip the catch block
		Label endCatchBlock = new Label();
		classInitializer.visitJumpInsn(Opcodes.GOTO, endCatchBlock);

		// catch (Throwable)
		Label beginCatchBlock = new Label();
		classInitializer.visitLabel(beginCatchBlock);
		classInitializer.visitTryCatchBlock(beginTryBlock, endTryBlock, beginCatchBlock,
			"java/lang/Throwable");

		// Stack now contains: [throwable]

		// Save the exception associated with the current catch block at index 1
		classInitializer.visitVarInsn(Opcodes.ASTORE, 1);
		classInitializer.visitVarInsn(Opcodes.ALOAD, 1);

		// Stack now contains: [throwable]

		// Invoke throwable.printStackTrace()
		classInitializer.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
			"java/lang/Throwable", "printStackTrace",
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

		// Stack is now empty

		// Create a new RuntimeException, invoke its constructor, and throw it
		classInitializer.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException");

		// Stack now contains: [RuntimeException]

		// Duplicate RuntimeException object for ATHROW
		classInitializer.visitInsn(Opcodes.DUP);

		// Stack now contains [RuntimeException, RuntimeException]

		// Load the exception associated with the current catch block
		classInitializer.visitVarInsn(Opcodes.ALOAD, 1);

		// Stack now contains: [RuntimeException, RuntimeException, throwable]

		// Invoke throwable.toString()
		classInitializer.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
			"java/lang/Throwable", "toString", Type.getMethodDescriptor(Type.getType(String.class),
			new Type[0]));

		// Stack now contains: [RuntimeException, RuntimeException, throwStr]

		// Construct "RuntimeException(throwStr)"
		classInitializer.visitMethodInsn(Opcodes.INVOKESPECIAL,
			"java/lang/RuntimeException", "<init>", Type.getMethodDescriptor(Type.VOID_TYPE,
			new Type[]
			{
				Type.getType(String.class)
			}));

		// Stack now contains [RuntimeException]
		// Throw the exception
		classInitializer.visitInsn(
			Opcodes.ATHROW);

		// End of catch block
		classInitializer.visitLabel(endCatchBlock);

		// Associate the variable "t" with the catch-block exception
		classInitializer.visitLocalVariable(
			"t", Type.getDescriptor(Throwable.class), null,
			beginCatchBlock, endCatchBlock, 1);

		// Invoke the user's class initializer if necessary
		if (userInitializerExists)
		{
			classInitializer.visitMethodInsn(Opcodes.INVOKESTATIC, classNode.name, jaceUserStaticInit,
				Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));
		}
		if (verbose)
		{
			// Push System.err onto the stack
			classInitializer.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System",
				"err", "Ljava/io/PrintStream;");

			// Push text onto the stack
			classInitializer.visitLdcInsn(classNode.name + ": Exiting class initializer...");

			// Invoke System.err.println
			classInitializer.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
				"println", Type.getMethodDescriptor(Type.VOID_TYPE,
				new Type[]
				{
					Type.getType(String.class)
				}));
		}

		// Return from the method
		classInitializer.visitInsn(Opcodes.RETURN);
	}

	/**
	 * Adds the native handle and its associated methods to the class:
	 *
	 *   private long jaceNativeHandle;
	 *
	 *   private void jaceSetNativeHandle( long nativeHandle ) {
	 *     jaceNativeHandle = nativeHandle;
	 *   }
	 *
	 *   private long jaceGetNativeHandle() {
	 *     return jaceNativeHandle;
	 *   }
	 *
	 * @param classNode the class to enhance
	 */
	@SuppressWarnings("unchecked")
	private void addNativeHandle(ClassNode classNode)
	{
		// Create field
		classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, jaceHandleField, Type.getDescriptor(
			long.class), null, null));

		// Create setter method
		MethodNode setNativeHandle = new MethodNode(Opcodes.ACC_PRIVATE, jaceSetNativeHandle, Type.
			getMethodDescriptor(
			Type.VOID_TYPE,
			new Type[]
			{
				Type.LONG_TYPE
			}),
			null, null);
		classNode.methods.add(setNativeHandle);

		// Push "this" onto the stack
		setNativeHandle.visitVarInsn(Opcodes.ALOAD, 0);

		// Push method argument onto the stack
		setNativeHandle.visitVarInsn(Opcodes.LLOAD, 1);

		// jaceHandle = method argument
		setNativeHandle.visitFieldInsn(Opcodes.PUTFIELD, classNode.name, jaceHandleField,
			Type.getDescriptor(long.class));

		// Return from the method
		setNativeHandle.visitInsn(Opcodes.RETURN);

		// Create getter method
		MethodNode getNativeHandle = new MethodNode(Opcodes.ACC_PRIVATE, jaceGetNativeHandle, Type.
			getMethodDescriptor(
			Type.LONG_TYPE, new Type[0]), null, null);
		classNode.methods.add(getNativeHandle);

		// Push "this" onto the stack
		getNativeHandle.visitVarInsn(Opcodes.ALOAD, 0);

		// Pushes the jaceHandle onto the stack
		getNativeHandle.visitFieldInsn(Opcodes.GETFIELD, classNode.name, jaceHandleField,
			Type.getDescriptor(long.class));

		// Return the handle value
		getNativeHandle.visitInsn(Opcodes.LRETURN);
	}

	/**
	 * Adds the native methods that are used to manage the lifetime
	 * of the native peer.
	 *
	 *  private native long jaceCreateInstance();
	 *  private native void jaceDestroyInstance();
	 *
	 * @param classNode the class to enhance
	 */
	@SuppressWarnings("unchecked")
	private void addNativeLifetimeMethods(ClassNode classNode)
	{
		// Create jaceCreateInstance() method
		classNode.methods.add(new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_NATIVE,
			jaceCreateInstance,
			Type.getMethodDescriptor(Type.LONG_TYPE, new Type[0]), null, null));

		// Create jacerDestroyInstance() method
		classNode.methods.add(new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_NATIVE,
			jaceDestroyInstance,
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null));
	}

	/**
	 * Adds a method which is used to deallocate the native peer.
	 *
	 * private void jaceDispose()
	 * {
	 *   long handle = jaceGetNativeHandle();
	 *   if (handle != 0)
	 *   {
	 *     jaceDestroyInstance();
	 *     jaceSetNativeHandle(0);
	 *   }
	 * }
	 *
	 * @param classNode the class to enhance
	 */
	@SuppressWarnings("unchecked")
	private void addDeallocation(ClassNode classNode)
	{
		// Create method
		MethodNode dispose = new MethodNode(Opcodes.ACC_PRIVATE, jaceDispose,
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);
		classNode.methods.add(dispose);

		Label beginningOfMethod = new Label();
		dispose.visitLabel(beginningOfMethod);

		// Push "this" onto stack
		dispose.visitVarInsn(Opcodes.ALOAD, 0);

		// Invoke the method
		dispose.visitMethodInsn(Opcodes.INVOKESPECIAL, classNode.name,
			jaceGetNativeHandle, Type.getMethodDescriptor(Type.LONG_TYPE, new Type[0]));

		// Duplicate the return value for use in the if statement
		dispose.visitInsn(Opcodes.DUP2);

		// Assign return value to local variable "handle"
		dispose.visitVarInsn(Opcodes.LSTORE, 1);

		// Cast "handle" to an int
		dispose.visitInsn(Opcodes.L2I);

		// If "(int) handle" is equal to zero, jump to handleIsZero
		Label handleIsZero = new Label();
		dispose.visitJumpInsn(Opcodes.IFEQ, handleIsZero);

		// Push "this" onto stack
		dispose.visitVarInsn(Opcodes.ALOAD, 0);

		// Invoke jaceDestroyInstance()
		dispose.visitMethodInsn(Opcodes.INVOKESPECIAL, classNode.name,
			jaceDestroyInstance, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

		// Push "this" onto stack
		dispose.visitVarInsn(Opcodes.ALOAD, 0);

		// Push "0" onto the stack
		dispose.visitInsn(Opcodes.LCONST_0);

		// Invoke jaceSetNativeHandle(0)
		dispose.visitMethodInsn(Opcodes.INVOKESPECIAL, classNode.name, jaceSetNativeHandle,
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]
			{
				Type.LONG_TYPE
			}));

		dispose.visitLabel(handleIsZero);

		// Return from method
		Label endOfMethod = new Label();
		dispose.visitLabel(endOfMethod);
		dispose.visitLocalVariable("handle", Type.getDescriptor(long.class),
			null, beginningOfMethod, endOfMethod, 1);
		dispose.visitInsn(Opcodes.RETURN);
	}

	/**
	 * Renames the user's finalizer to jaceUserFinalize() and creates a new finalizer:
	 *
	 * protected void finalize() throws Throwable
	 * {
	 *   jaceUserFinalize();
	 *   jaceDispose();
	 * }
	 *
	 * @param classNode the class to enhance
	 */
	@SuppressWarnings("unchecked")
	private void enhanceFinalizer(ClassNode classNode)
	{
		boolean finalizerExisted = false;
		for (Object o: classNode.methods)
		{
			MethodNode method = (MethodNode) o;
			if (method.name.equals("finalize"))
			{
				finalizerExisted = true;

				// Rename finalizer to jaceUserFinalize()
				method.name = jaceUserFinalize;
				break;
			}
		}
		if (!finalizerExisted && deallocationMethod != null)
		{
			// Only enhance the finalizer if one already existed or if the user did not specify a deallocation method
			return;
		}

		// Create a new finalizer
		MethodNode finalize = new MethodNode(Opcodes.ACC_PROTECTED, "finalize",
			Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, new String[]
			{
				"java/lang/Throwable"
			});
		classNode.methods.add(finalize);

		// Push 'this' onto stack
		finalize.visitVarInsn(Opcodes.ALOAD, 0);

		if (finalizerExisted)
		{
			// Duplicate 'this' on stack because jaceDispose takes it as an argument
			finalize.visitInsn(Opcodes.DUP);

			// Invoke the old finalizer
			finalize.visitMethodInsn(Opcodes.INVOKEVIRTUAL, classNode.name,
				jaceUserFinalize, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));
		}

		// Invoke jaceDispose
		finalize.visitMethodInsn(Opcodes.INVOKESPECIAL, classNode.name,
			jaceDispose, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

		finalize.visitInsn(Opcodes.RETURN);
	}

	public static String getUsage()
	{
		String usage =
					 "Usage: PeerEnhancer " + newLine + "  <input file>" + newLine + "  <output file>"
					 + newLine
					 + "  <comma-separated list of libraries>" + newLine + "  [options] " + newLine + newLine
					 + "Where options can be:" + newLine + "  -deallocator=<deallocation method>" + newLine
					 + "  -verbose "
					 + newLine;
		return usage;
	}

	/**
	 * Returns the logger associated with the object.
	 *
	 * @return the logger associated with the object
	 */
	private Logger getLogger()
	{
		return log;
	}

	/**
	 * Enhances a Java peer.
	 *
	 * @param args the command-line argument
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args)
	{
		if (args.length < 3)
		{
			System.out.println(getUsage());
			return;
		}

		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		String libraries = args[2];

		String deallocationMethod = null;
		boolean verbose = false;
		for (int i = 3; i < args.length; ++i)
		{
			String option = args[i];

			if (option.equals("-deallocator"))
			{
				String[] tokens = args[i].split("=");
				if (tokens.length == 2)
				{
					deallocationMethod = tokens[1];
					continue;
				}
			}
			else if (option.equals("-verbose"))
			{
				verbose = true;
				continue;
			}
			System.out.println("Not an understood option: [" + option + "]");
			System.out.println();
			System.out.println(getUsage());
			return;
		}

		String tokens[] = libraries.split(",");
		PeerEnhancer.Builder enhancer = new PeerEnhancer.Builder(inputFile, outputFile);
		for (String token: tokens)
			enhancer.library(token);
		enhancer.deallocationMethod(deallocationMethod);
		enhancer.verbose(verbose);
		Logger log = LoggerFactory.getLogger(PeerEnhancer.class);
		log.info("Enhancing " + inputFile + " -> " + outputFile);
		try
		{
			enhancer.enhance();
		}
		catch (IOException e)
		{
			log.error("", e);
		}
	}
}
