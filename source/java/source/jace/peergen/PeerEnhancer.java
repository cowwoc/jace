package jace.peergen;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;
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
  private boolean classInitializerFound;
  private boolean deallocationMethodFound;
  private boolean finalizerFound;
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
   * Creates a new PeerEnhancer.
   *
   * @param inputFile the input class file
   * @param outputFile the enhanced output file
   * @param libraries native libraries to load at peer initialization time (empty collection denotes none)
   * @param deallocationMethod peer deallocation method (null denotes none)
   * @param verbose true if Java peers should output library names before loading them
   * @throws IllegalArgumentException if inputFile, outputFile, or libraries are null
   */
  public PeerEnhancer(File inputFile, File outputFile, List<String> libraries, String deallocationMethod,
                      boolean verbose) throws IllegalArgumentException
  {
    if (inputFile == null)
      throw new IllegalArgumentException("inputFile may not be null");
    if (outputFile == null)
      throw new IllegalArgumentException("outputFile may not be null");
    if (libraries == null)
      throw new IllegalArgumentException("libraries may not be null");
    if (deallocationMethod != null && deallocationMethod.trim().isEmpty())
      throw new IllegalArgumentException("deallocationMethod may not be an empty String");
    this.inputFile = inputFile;
    this.outputFile = outputFile;
    this.libraries = libraries;
    this.deallocationMethod = deallocationMethod;
    this.verbose = verbose;
  }

  /**
   * This visitor inserts <code>jaceSetNativeHandle(jaceCreateInstance())</code> at the beginning of all
   * non-chaining constructors.
   *
   * @author Gili Tzabari
   */
  private static class NonChainingConstructorEnhancer extends MethodAdapter
  {
    // DESIGN:
    // - This code initializes the native handle used by "jace::helper::getPeer(this)"
    // - Ideally we'd like to initialize the native handle after the Java object is fully constructed.
    //   Unfortunately, we have no way of preventing native methods from being invoked by the constructor
    //   itself.
    // - The earliest we can inject code is after a call to super().
    // - We don't have to worry about native methods getting invoked as arguments of super() because
    //   static native methods do not depend upon the native handle.
    private boolean done;
    private final String className;
    private final String superClassName;

    /**
     * Creates a new NonChainingConstructorEnhancer.
     *
     * @param cv MethodVisitor to delegate to
     * @param className the class containing the method
     * @param superClassName the superclass of the class containing the method
     */
    public NonChainingConstructorEnhancer(MethodVisitor cv, String className, String superClassName)
    {
      super(cv);
      this.className = className;
      this.superClassName = superClassName;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc)
    {
      super.visitMethodInsn(opcode, owner, name, desc);
      if (!done && owner.equals(superClassName) && name.equals("<init>"))
        insertInstruction();
    }

    /**
     * Enhances the return instruction.
     *
     * @param opcode the instruction opcode
     */
    @Override
    public void visitInsn(int opcode)
    {
      if (!done && opcode == Opcodes.RETURN)
        insertInstruction();
      super.visitInsn(opcode);
    }

    /**
     * Inserts <code>jaceSetNativeHandle(jaceCreateInstance())</code>.
     */
    private void insertInstruction()
    {
      done = true;

      // Load "this" onto the stack
      super.visitVarInsn(Opcodes.ALOAD, 0);
      super.visitInsn(Opcodes.DUP);
      // Stack now contains: [this, this]

      // Invoke jaceCreateInstance()
      super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, jaceCreateInstance,
        Type.getMethodDescriptor(Type.LONG_TYPE, new Type[0]));

      // Stack now contains: [this, result of jaceCreateInstance()]
      // Invoke jaceSetNativeHandle(jaceCreateInstance())
      super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, jaceSetNativeHandle,
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]
        {
          Type.LONG_TYPE
        }));
    }
  }

  /**
   * Returns a {@code Set<methodDescriptor>} for all non-chaining constructors.
   *
   * @param classReader the ClassReader to read from
   * @return a {@code Set<methodDescriptor>} for all non-chaining constructors.
   */
  private Set<String> getNonChainingConstructors(ClassReader classReader)
  {
    final Set<String> result = new HashSet<String>();
    classReader.accept(new GetNonChainingConstructors(result), ClassReader.SKIP_DEBUG);
    return result;
  }

  /**
   * Enhances the class file.
   *
   * @throws IOException if an I/O error occurs while enhancing the file
   */
  public void enhance() throws IOException
  {
    final boolean[] alreadyEnhanced = new boolean[1];
    alreadyEnhanced[0] = false;
    classInitializerFound = false;
    deallocationMethodFound = deallocationMethod == null;
    finalizerFound = false;

    BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
    try
    {
      ClassReader classReader = new ClassReader(in);

      /**
       * 1) Check whether jaceGetNativeHandle is already defined.
       */
      classReader.accept(new EmptyVisitor()
      {
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions)
        {
          if (name.equals(jaceGetNativeHandle))
            alreadyEnhanced[0] = true;
          return null;
        }
      }, ClassReader.SKIP_DEBUG);
      if (alreadyEnhanced[0])
      {
        log.info("The class " + inputFile + " has already been enhanced and will not be modified.");
        return;
      }

      final Set<String> nonChainingConstructors = getNonChainingConstructors(classReader);
      ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
      classReader.accept(new EnhanceNonChainingConstructors(classWriter, nonChainingConstructors), 0);

      if (!deallocationMethodFound)
      {
        String msg = "Unable to locate the method: " + deallocationMethod + "." +
                     newLine + "Peer enhancement will now stop.";
        throw new RuntimeException(msg);
      }

      String className = classReader.getClassName();
      enhanceInitializer(classWriter, className, classInitializerFound);
      addNativeHandle(classWriter, className);
      addNativeLifetimeMethods(classWriter);
      addDeallocation(classWriter, className);
      if (deallocationMethod != null)
        enhanceDeallocation(classWriter, className);
      enhanceFinalize(classWriter, className, finalizerFound);

      File parentPath = outputFile.getParentFile();
      if (parentPath != null && !parentPath.exists() && !parentPath.mkdirs())
      {
        log.warn("Could not create " + parentPath);
        return;
      }
      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
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
   * @param classWriter the CllassWriter to write to
   * @param className the class name
   * @param classInitializerFound true if the class initializer already exists
   */
  private void enhanceInitializer(ClassWriter classWriter, String className, boolean classInitializerFound)
  {
    // Create method "private native static void jaceSetVm()"
    final int setVmFlags = Opcodes.ACC_PRIVATE | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_NATIVE;
    classWriter.visitMethod(setVmFlags, jaceSetVm,
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);

    // Create class initializer
    final int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
    MethodVisitor instructions = classWriter.visitMethod(flags, "<clinit>",
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);

    // try {
    Label beginTryBlock = new Label();
    instructions.visitLabel(beginTryBlock);

    for (String library: libraries)
    {
      if (verbose)
      {
        // Push System.err onto the stack
        instructions.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System",
          "err",
          "Ljava/io/PrintStream;");

        // Push text onto the stack
        instructions.visitLdcInsn(className + ": Loading " + library + "...");

        // Invoke System.err.println
        instructions.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
          "println",
          Type.getMethodDescriptor(Type.VOID_TYPE,
          new Type[]
          {
            Type.getType(String.class)
          }));
      }

      // Push library name onto stack
      instructions.visitLdcInsn(library);

      // Invoke System.loadLibrary()
      instructions.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
        "loadLibrary",
        Type.getMethodDescriptor(Type.VOID_TYPE,
        new Type[]
        {
          Type.getType(String.class)
        }));
    }

    if (verbose)
    {
      // Push System.err onto the stack
      instructions.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System",
        "err",
        "Ljava/io/PrintStream;");

      // Push text onto the stack
      instructions.visitLdcInsn(className + ": Done loading native libraries...");

      // Invoke System.err.println
      instructions.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
        "println",
        Type.getMethodDescriptor(Type.VOID_TYPE,
        new Type[]
        {
          Type.getType(String.class)
        }));
    }

    // Invoke jaceSetVm()
    instructions.visitMethodInsn(Opcodes.INVOKESTATIC, className,
      jaceSetVm, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

    // }
    Label endTryBlock = new Label();
    instructions.visitLabel(endTryBlock);

    // We've reached the end of the try block, now skip the catch block
    Label endCatchBlock = new Label();
    instructions.visitJumpInsn(Opcodes.GOTO, endCatchBlock);

    // catch (Throwable)
    Label beginCatchBlock = new Label();
    instructions.visitLabel(beginCatchBlock);
    instructions.visitTryCatchBlock(beginTryBlock, endTryBlock, beginCatchBlock,
      "java/lang/Throwable");

    // Stack now contains: [throwable]

    // Save the exception associated with the current catch block at index 1
    instructions.visitVarInsn(Opcodes.ASTORE, 1);
    instructions.visitVarInsn(Opcodes.ALOAD, 1);

    // Stack now contains: [throwable]

    // Invoke throwable.printStackTrace()
    instructions.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
      "java/lang/Throwable", "printStackTrace",
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

    // Stack is now empty

    // Create a new RuntimeException, invoke its constructor, and throw it
    instructions.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException");

    // Stack now contains: [RuntimeException]

    // Duplicate RuntimeException object for ATHROW
    instructions.visitInsn(Opcodes.DUP);

    // Stack now contains [RuntimeException, RuntimeException]

    // Load the exception associated with the current catch block
    instructions.visitVarInsn(Opcodes.ALOAD, 1);

    // Stack now contains: [RuntimeException, RuntimeException, throwable]

    // Invoke throwable.toString()
    instructions.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
      "java/lang/Throwable", "toString",
      Type.getMethodDescriptor(Type.getType(String.class), new Type[0]));

    // Stack now contains: [RuntimeException, RuntimeException, throwStr]

    // Construct "RuntimeException(throwStr)"
    instructions.visitMethodInsn(Opcodes.INVOKESPECIAL,
      "java/lang/RuntimeException", "<init>",
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]
      {
        Type.getType(String.class)
      }));

    // Stack now contains [RuntimeException]

    // Throw the exception
    instructions.visitInsn(Opcodes.ATHROW);

    // End of catch block
    instructions.visitLabel(endCatchBlock);

    // Associate the variable "t" with the catch-block exception
    instructions.visitLocalVariable("t", Type.getDescriptor(Throwable.class),
      null, beginCatchBlock, endCatchBlock, 1);

    // Make a call to the user's old initializer if there is one.
    if (classInitializerFound)
    {
      instructions.visitMethodInsn(Opcodes.INVOKESTATIC, className, jaceUserStaticInit,
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));
    }

    if (verbose)
    {
      // Push System.err onto the stack
      instructions.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System",
        "err",
        "Ljava/io/PrintStream;");

      // Push text onto the stack
      instructions.visitLdcInsn(className + ": Exiting class initializer...");

      // Invoke System.err.println
      instructions.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
        "println",
        Type.getMethodDescriptor(Type.VOID_TYPE,
        new Type[]
        {
          Type.getType(String.class)
        }));
    }

    // Return from the method
    instructions.visitInsn(Opcodes.RETURN);
    instructions.visitMaxs(0, 0);
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
   * @param classWriter the ClassWriter to write to
   * @param className the class name
   */
  private void addNativeHandle(ClassWriter classWriter, String className)
  {
    // Create field
    int fieldFlags = Opcodes.ACC_PRIVATE;
    classWriter.visitField(fieldFlags, jaceHandleField,
      Type.getDescriptor(long.class), null, null);

    // Create setter method
    int setFlags = Opcodes.ACC_PRIVATE;
    MethodVisitor setInstructions = classWriter.visitMethod(setFlags,
      jaceSetNativeHandle,
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]
      {
        Type.LONG_TYPE
      }),
      null, null);

    // Push "this" onto the stack
    setInstructions.visitVarInsn(Opcodes.ALOAD, 0);

    // Push method argument onto the stack
    setInstructions.visitVarInsn(Opcodes.LLOAD, 1);

    // jaceHandle = method argument
    setInstructions.visitFieldInsn(Opcodes.PUTFIELD, className, jaceHandleField,
      Type.getDescriptor(long.class));

    // Return from the method
    setInstructions.visitInsn(Opcodes.RETURN);
    setInstructions.visitMaxs(0, 0);

    // Create getter method
    int getFlags = Opcodes.ACC_PRIVATE;
    MethodVisitor getInstructions = classWriter.visitMethod(getFlags,
      jaceGetNativeHandle,
      Type.getMethodDescriptor(Type.LONG_TYPE, new Type[0]),
      null, null);

    // Push "this" onto the stack
    getInstructions.visitVarInsn(Opcodes.ALOAD, 0);

    // Pushes the jaceHandle onto the stack
    getInstructions.visitFieldInsn(Opcodes.GETFIELD, className, jaceHandleField,
      Type.getDescriptor(long.class));

    // Return the handle value
    getInstructions.visitInsn(Opcodes.LRETURN);
    getInstructions.visitMaxs(0, 0);
  }

  /**
   * Adds the native methods that are used to manage the lifetime
   * of the native peer.
   *
   *  private native long jaceCreateInstance();
   *  private static native void jaceDestroyInstance( long handle );
   *
   * @param classWriter the ClassWriter to write to
   */
  private void addNativeLifetimeMethods(ClassWriter classWriter)
  {
    // Create jaceCreateInstance() method
    int createFlags = Opcodes.ACC_PRIVATE | Opcodes.ACC_NATIVE;
    classWriter.visitMethod(createFlags, jaceCreateInstance,
      Type.getMethodDescriptor(Type.LONG_TYPE, new Type[0]), null, null);

    // Create jacerDestroyInstance() method
    int destroyFlags = Opcodes.ACC_PRIVATE | Opcodes.ACC_NATIVE | Opcodes.ACC_STATIC;
    classWriter.visitMethod(destroyFlags, jaceDestroyInstance,
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]
      {
        Type.LONG_TYPE
      }), null,
      null);
  }

  /**
   * Adds a method which is used to deallocate the native peer.
   *
   * private void jaceDispose()
   * {
   *   long handle = jaceGetNativeHandle();
   *   if (handle != 0)
   *   {
   *     jaceDestroyInstance(handle);
   *     jaceSetNativeHandle(0);
   *   }
   * }
   * 
   * @param classWriter the ClassWriter to write to
   * @param className the class name
   */
  private void addDeallocation(ClassWriter classWriter, String className)
  {
    // Create method
    int flags = Opcodes.ACC_PRIVATE;
    MethodVisitor instructions = classWriter.visitMethod(flags, jaceDispose,
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);

    Label beginningOfMethod = new Label();
    instructions.visitLabel(beginningOfMethod);

    // Push "this" onto stack
    instructions.visitVarInsn(Opcodes.ALOAD, 0);

    // Invoke the method
    instructions.visitMethodInsn(Opcodes.INVOKESPECIAL, className,
      jaceGetNativeHandle, Type.getMethodDescriptor(Type.LONG_TYPE, new Type[0]));

    // Duplicate the return value for use in the if statement
    instructions.visitInsn(Opcodes.DUP2);

    // Assign return value to local variable "handle"
    instructions.visitVarInsn(Opcodes.LSTORE, 1);

    // Cast "handle" to an int
    instructions.visitInsn(Opcodes.L2I);

    // If "(int) handle" is equal to zero, jump to handleIsZero
    Label handleIsZero = new Label();
    instructions.visitJumpInsn(Opcodes.IFEQ, handleIsZero);

    // Push "handle" onto the stack
    instructions.visitVarInsn(Opcodes.LLOAD, 1);

    // Invoke jaceDestroyInstance(handle)
    instructions.visitMethodInsn(Opcodes.INVOKESTATIC, className,
      jaceDestroyInstance,
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]
      {
        Type.LONG_TYPE
      }));

    // Push "this" onto stack
    instructions.visitVarInsn(Opcodes.ALOAD, 0);

    // Push "0" onto the stack
    instructions.visitInsn(Opcodes.LCONST_0);

    // Invoke jaceSetNativeHandle(0)
    instructions.visitMethodInsn(Opcodes.INVOKESPECIAL, className,
      jaceSetNativeHandle,
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]
      {
        Type.LONG_TYPE
      }));

    instructions.visitLabel(handleIsZero);

    // Return from method
    Label endOfMethod = new Label();
    instructions.visitLabel(endOfMethod);
    instructions.visitLocalVariable("handle", Type.getDescriptor(long.class),
      null, beginningOfMethod, endOfMethod, 1);
    instructions.visitInsn(Opcodes.RETURN);
    instructions.visitMaxs(0, 0);
  }

  /**
   * Enhance deallocation method so it reads:
   *
   * jaceUserClose();
   * jaceDispose();
   * 
   * @param classWriter the ClassWriter to write to
   * @param className the class name
   */
  private void enhanceDeallocation(ClassWriter classWriter, String className)
  {
    // Create the new deallocation method
    final int flags = Opcodes.ACC_PUBLIC;
    MethodVisitor instructions = classWriter.visitMethod(flags, deallocationMethod,
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);

    // Place copy of 'this' on the stack
    instructions.visitVarInsn(Opcodes.ALOAD, 0);

    // Invoke the user's deallocation method
    instructions.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className,
      jaceUserClose, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

    // Place copy of 'this' on the stack
    instructions.visitVarInsn(Opcodes.ALOAD, 0);

    // Invoke jaceDispose()
    instructions.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className,
      jaceDispose, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

    instructions.visitInsn(Opcodes.RETURN);
    instructions.visitMaxs(0, 0);
  }

  /**
   * Enhance class finalizer.
   *
   * @param classWriter the ClassWriter to write to
   * @param className the class name
   * @param finalizerExisted true if the class already has a finalizer defined
   */
  private void enhanceFinalize(ClassWriter classWriter, String className, boolean finalizerExisted)
  {
    if (!finalizerExisted && deallocationMethodFound)
    {
      // Only enhance the finalizer if one already existed or if the user did not specify a deallocation method
      return;
    }

    // Create a new finalizer
    final int flags = Opcodes.ACC_PROTECTED;
    MethodVisitor instructions = classWriter.visitMethod(flags, "finalize",
      Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, new String[]
      {
        "java/lang/Throwable"
      });

    // Push 'this' onto stack
    instructions.visitVarInsn(Opcodes.ALOAD, 0);

    if (finalizerExisted)
    {
      // Duplicate 'this' on stack because jaceDispose takes it as an argument
      instructions.visitInsn(Opcodes.DUP);

      // Invoke the old finalizer
      instructions.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className,
        jaceUserFinalize, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));
    }

    // Invoke jaceDispose
    instructions.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className,
      jaceDispose, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));

    instructions.visitInsn(Opcodes.RETURN);
    instructions.visitMaxs(0, 0);
  }

  public static String getUsage()
  {
    String usage =
           "Usage: PeerEnhancer " + newLine +
           "  <input file>" + newLine +
           "  <output file>" + newLine +
           "  <comma-separated list of libraries>" + newLine +
           "  [options] " + newLine +
           newLine +
           "Where options can be:" + newLine +
           "  -deallocator=<deallocation method>" + newLine +
           "  -verbose " + newLine;
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
    List<String> nativeLibraries = new ArrayList<String>();
    for (String token: tokens)
      nativeLibraries.add(token);
    PeerEnhancer enhancer = new PeerEnhancer(inputFile, outputFile, nativeLibraries, deallocationMethod, verbose);
    Logger log = enhancer.getLogger();
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

  /**
   * Returns a list of non-chaining constructors.
   *
   * @author Gili Tzabari
   */
  private static class GetNonChainingConstructors extends EmptyVisitor
  {
    private final Set<String> result;
    private String className;

    /**
     * Creates a new GetNonChainingConstructors.
     *
     * @param result the Set to populate with results
     */
    public GetNonChainingConstructors(Set<String> result)
    {
      this.result = result;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces)
    {
      className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, final String desc, String signature,
                                     String[] exceptions)
    {
      if (name.equals("<init>"))
      {
        return new EmptyVisitor()
        {
          private boolean constructorIsChained = false;

          @Override
          public void visitMaxs(int maxStack, int maxLocals)
          {
            if (!constructorIsChained)
              result.add(desc);
          }

          @Override
          public void visitMethodInsn(int opcode, String owner, String name, String desc)
          {
            if (className.equals(owner) && name.equals("<init>"))
              constructorIsChained = true;
          }
        };
      }
      return null;
    }
  }

  /**
   * Initializes the native handle in all non-chaining constructors.
   *
   * @author Gili Tzbari
   */
  private class EnhanceNonChainingConstructors extends ClassAdapter
  {
    private final Set<String> nonChainingConstructors;
    private final ClassVisitor classVisitor;
    private String className;
    private String superClassName;

    /**
     * Creates a new EnhanceNonChainingConstructors.
     *
     * @param classVisitor the ClassVisitor to read from
     * @param classWriter the ClassWriter to write to
     * @param nonChainingConstructors a list of the non-chaining constructors in the class
     */
    public EnhanceNonChainingConstructors(ClassVisitor classVisitor, Set<String> nonChainingConstructors)
    {
      super(classVisitor);
      this.classVisitor = classVisitor;
      this.nonChainingConstructors = nonChainingConstructors;
    }

    /**
     * Visits the class header.
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces)
    {
      super.visit(version, access, name, signature, superName, interfaces);
      this.className = name;
      this.superClassName = superName;
    }

    /**
     * 2) Rename the class initializer to jaceUserStaticInit
     * 3) For non-chaining constructors, invoke
     *    jaceSetNativeHandle(jaceCreateInstance()) before returning
     * 4) Renames the user deallocation method to jaceUserClose()
     * 5) Renames the user finalizer to jaceUserFinalize()
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                     String[] exceptions)
    {
      if (name.equals("<clinit>"))
      {
        // Rename class initializer to jaceUserStaticInit()
        classInitializerFound = true;
        int flags = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;
        return classVisitor.visitMethod(flags, jaceUserStaticInit, desc, null, exceptions);
      }
      else if (name.equals("<init>"))
      {
        // Is this a non-chaining constructor?
        if (nonChainingConstructors.contains(desc))
        {
          // Generate the method
          MethodVisitor out = classVisitor.visitMethod(access, name, desc, null, exceptions);
          return new NonChainingConstructorEnhancer(out, className, superClassName);
        }
      }
      else if (name.equals("finalize"))
      {
        // Rename finalizer to jaceUserFinalize
        finalizerFound = true;
        return classVisitor.visitMethod(access, jaceUserFinalize, desc, signature, exceptions);
      }
      else if (!deallocationMethodFound && name.equals(deallocationMethod))
      {
        // Rename deallocation method to jaceUserClose
        deallocationMethodFound = true;
        return classVisitor.visitMethod(access, jaceUserClose, desc, signature, exceptions);
      }
      return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible)
    {
      // Retain all attributes
      return super.visitAnnotation(desc, visible);
    }
  }
}