package org.jace.parser;

import com.google.common.collect.Lists;
import org.jace.metaclass.TypeName;
import org.jace.metaclass.TypeNameFactory;
import org.jace.parser.attribute.Attribute;
import org.jace.parser.attribute.AttributeFactory;
import org.jace.parser.attribute.ConstantValueAttribute;
import org.jace.parser.attribute.SyntheticAttribute;
import org.jace.parser.constant.ClassConstant;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.ConstantFactory;
import org.jace.parser.field.ClassField;
import org.jace.parser.field.FieldAccessFlag;
import org.jace.parser.field.FieldAccessFlagSet;
import org.jace.parser.method.ClassMethod;
import org.jace.parser.method.MethodAccessFlag;
import org.jace.parser.method.MethodAccessFlagSet;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A representation of the java class file format.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassFile
{
	private final Logger log = LoggerFactory.getLogger(ClassFile.class);
	/**
	 * The identifying signature for a class file.
	 * See the JVM specification.
	 */
	private static final int MAGIC_SIGNATURE = 0xCAFEBABE;
	private final ConstantPool constantPool = new ConstantPool();
	private int superclassIndex;
	private TypeName superclassName;
	private int classIndex;
	private TypeName className;
	private List<Integer> interfaceIndices;
	private List<TypeName> interfaces;
	private List<ClassField> fields;
	private List<ClassMethod> methods;
	private List<Attribute> attributes;
	private int minorVersion;
	private int majorVersion;
	private int accessFlags;

	/**
	 * Creates a new ClassFile on the given class definition.
	 *
	 * @param clazz the class stream
	 * @throws ClassFormatError if an error occurs while parsing the class file
	 */
	public ClassFile(InputStream clazz) throws ClassFormatError
	{
		parseClass(clazz);
	}

	/**
	 * Creates a new ClassFile on the given class file.
	 *
	 * @param path the class file path
	 * @throws ClassFormatError if an error occurs while parsing the class file
	 */
	public ClassFile(File path) throws ClassFormatError
	{
		InputStream input = null;

		try
		{
			input = new BufferedInputStream(new FileInputStream(path));
			parseClass(input);
		}
		catch (IOException e)
		{
			ClassFormatError exception = new ClassFormatError("Unable to read the class");
			exception.initCause(e);
			throw exception;
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
	}

	/**
	 * Returns the name of the class.
	 *
	 * @return the name of the class
	 */
	public TypeName getClassName()
	{
		return className;
	}

	/**
	 * Sets the name of the class.
	 *
	 * @param name the name of the class
	 */
	public void setClassName(TypeName name)
	{
		className = name;
	}

	/**
	 * Returns the name of the super class.
	 *
	 * @return null if the class is java.lang.Object
	 */
	public TypeName getSuperClassName()
	{
		return superclassName;
	}

	/**
	 * Sets the name of the super class.
	 *
	 * @param name the name of the super class
	 */
	public void setSuperClassName(TypeName name)
	{
		superclassName = name;
	}

	/**
	 * Returns the names of the implemented interfaces.
	 *
	 * @return the names of the implemented interfaces
	 */
	public Collection<TypeName> getInterfaces()
	{
		return Collections.unmodifiableList(interfaces);
	}

	/**
	 * Returns the constant pool indices of the implemented interfaces.
	 *
	 * @return the constant pool indices of the implemented interfaces
	 */
	public Collection<Integer> getInterfaceIndices()
	{
		return Collections.unmodifiableList(interfaceIndices);
	}

	/**
	 * Returns the ClassFields for this class.
	 *
	 * @return the ClassFields for this class
	 */
	public List<ClassField> getFields()
	{
		return Collections.unmodifiableList(fields);
	}

	/**
	 * Returns the ClassMethods for this class.
	 *
	 * @return the ClassMethods for this class
	 */
	public List<ClassMethod> getMethods()
	{
		return Collections.unmodifiableList(methods);
	}

	/**
	 * Returns the major version.
	 *
	 * @return the major version
	 */
	public int getMajorVersion()
	{
		return majorVersion;
	}

	/**
	 * Returns the minor version.
	 *
	 * @return the minor version
	 */
	public int getMinorVersion()
	{
		return minorVersion;
	}

	/**
	 * Changes the class file version. Internally handles format changes.
	 *
	 * @param major the major version
	 * @param minor the minor version
	 */
	public void setVersion(int major, int minor)
	{
		final int NO_CHANGE = -1;
		final int TO_1_5 = 0;
		final int TO_1_4 = 1;

		int change = NO_CHANGE;

		if (majorVersion < 49 && major >= 49)
			change = TO_1_5;
		else if (majorVersion >= 49 && major < 49)
			change = TO_1_4;

		majorVersion = major;
		minorVersion = minor;

		if (change == TO_1_5)
		{
			// TODO: Change synthetic attributes to access flags
		}
		else if (change == TO_1_4)
		{
			// Change synthetic access flags to attributes for class, methods, and fields
			// Also drop any other specifiers that are not valid.
			ClassAccessFlagSet classFlags = getAccessFlags();
			classFlags.remove(ClassAccessFlag.ANNOTATION);
			classFlags.remove(ClassAccessFlag.ENUM);

			if (classFlags.contains(ClassAccessFlag.SYNTHETIC))
			{
				log.trace("Setting class synthetic attribute.");
				classFlags.remove(ClassAccessFlag.SYNTHETIC);
				setAccessFlags(classFlags);
				attributes.add(new SyntheticAttribute(constantPool));
			}

			for (ClassField field: fields)
			{
				FieldAccessFlagSet flags = field.getAccessFlags();
				flags.remove(FieldAccessFlag.ENUM);
				if (flags.contains(FieldAccessFlag.SYNTHETIC))
				{
					log.trace("Setting field synthetic attribute.");
					flags.remove(FieldAccessFlag.SYNTHETIC);
					field.setAccessFlags(flags);
					field.addAttribute(new SyntheticAttribute(constantPool));
				}
			}

			for (ClassMethod method: methods)
			{
				MethodAccessFlagSet flags = method.getAccessFlags();
				flags.remove(MethodAccessFlag.BRIDGE);
				flags.remove(MethodAccessFlag.VARARGS);
				if (method.getAccessFlags().contains(MethodAccessFlag.SYNTHETIC))
				{
					log.trace("Setting method synthetic attribute.");
					flags.remove(MethodAccessFlag.SYNTHETIC);
					method.setAccessFlags(flags);
					method.addAttribute(new SyntheticAttribute(constantPool));
				}
			}
		}
	}

	/**
	 * Returns the set of access flags for this class.
	 *
	 * @return the set of access flags for this class
	 */
	public ClassAccessFlagSet getAccessFlags()
	{
		return new ClassAccessFlagSet(accessFlags);
	}

	/**
	 * Sets the set of access flags for this class.
	 *
	 * @param set the set of access flags for this class
	 */
	public void setAccessFlags(ClassAccessFlagSet set)
	{
		accessFlags = set.getValue();
	}

	/**
	 * Writes the class file.
	 *
	 * @param path the class path
	 * @throws IOException if an error occurs while writing
	 */
	public void writeClass(String path) throws IOException
	{
		OutputStream output = new BufferedOutputStream(new FileOutputStream(path));
		writeClass(output);
		output.close();
	}

	/**
	 * Writes the class file.
	 *
	 * @param stream the output stream
	 * @throws IOException if an error occurs while writing
	 */
	public void writeClass(OutputStream stream) throws IOException
	{
		DataOutputStream output = new DataOutputStream(stream);

		writeSignature(output);
		writeVersion(output);
		writeConstantPool(output);
		writeAccessFlags(output);
		writeClassName(output);
		writeSuperClass(output);
		writeInterfaces(output);
		writeFields(output);
		writeMethods(output);
		writeAttributes(output);
	}

	/**
	 * Parses the input stream to retrieve the class definition.
	 *
	 * @param stream the input stream
	 * @throws ClassFormatError if an error occurs while parsing the class
	 */
	private void parseClass(InputStream stream) throws ClassFormatError
	{
		DataInputStream input = new DataInputStream(stream);

		try
		{
			readSignature(input);
			readVersion(input);
			readConstantPool(input);
			readAccessFlags(input);
			readClassName(input);
			readSuperClass(input);
			readInterfaces(input);
			readFields(input);
			readMethods(input);
			readAttributes(input);
		}
		catch (IOException e)
		{
			ClassFormatError exception = new ClassFormatError("The class definition ends prematurely");
			exception.initCause(e);
			throw exception;
		}
		finally
		{
			try
			{
				input.close();
			}
			catch (IOException e)
			{
				ClassFormatError exception = new ClassFormatError("Cannot close the class InputStream");
				exception.initCause(e);
				throw exception;
			}
		}
	}

	/**
	 * Read and verify the class file signature.
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while reading the class
	 */
	private void readSignature(DataInputStream input) throws IOException
	{
		int signature = input.readInt();

		if (signature != MAGIC_SIGNATURE)
		{
			throw new ClassFormatError("The class signature isn't correct");
		}
	}

	/**
	 * Writes and verify the class file signature.
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	private void writeSignature(DataOutputStream output) throws IOException
	{
		output.writeInt(MAGIC_SIGNATURE);
	}

	/**
	 * Read the access flags
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while reading the class
	 */
	private void readAccessFlags(DataInputStream input) throws IOException
	{
		accessFlags = input.readUnsignedShort();
	}

	/**
	 * Read in the constant pool.
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while reading the class
	 */
	private void readConstantPool(DataInputStream input) throws IOException
	{
		ConstantFactory constantFactory = ConstantFactory.getInstance();

		final int numEntries = input.readUnsignedShort() - 1;
		if (log.isTraceEnabled())
			log.trace("num entries: " + numEntries);

		for (int entriesRead = 0; entriesRead < numEntries;)
		{
			Constant c = constantFactory.readConstant(input, constantPool);
			constantPool.addConstant(c);
			entriesRead += c.getSize();
		}
		if (log.isTraceEnabled())
			log.debug("num pool entries: " + constantPool.getNumEntries());
	}

	/**
	 * Writes in the constant pool.
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	private void writeConstantPool(DataOutputStream output) throws IOException
	{
		int numEntries = constantPool.getNumEntries();
		output.writeShort(numEntries + 1);
		for (int i = 0; i < constantPool.getSize(); ++i)
		{
			Constant c = constantPool.getConstant(i);
			c.write(output);
		}
	}

	/**
	 * Writes the access flags.
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	public void writeAccessFlags(DataOutputStream output) throws IOException
	{
		output.writeShort(accessFlags);
	}

	/**
	 * Writes the class name.
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	public void writeClassName(DataOutputStream output) throws IOException
	{
		output.writeShort(classIndex);
	}

	/**
	 * Writes the super class extended.
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	public void writeSuperClass(DataOutputStream output) throws IOException
	{
		output.writeShort(superclassIndex);
	}

	/**
	 * Writes the implemented interfaces.
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	public void writeInterfaces(DataOutputStream output) throws IOException
	{
		output.writeShort(interfaceIndices.size());

		for (Integer index: interfaceIndices)
			output.writeShort(index);
	}

	/**
	 * Writes the class fields.
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	public void writeFields(DataOutputStream output) throws IOException
	{
		output.writeShort(fields.size());

		for (ClassField field: fields)
			field.write(output);
	}

	/**
	 * Writes the class methods.
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	public void writeMethods(DataOutputStream output) throws IOException
	{
		output.writeShort(methods.size());

		for (ClassMethod method: methods)
			method.write(output);
	}

	/**
	 * Writes the class attributes.
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	public void writeAttributes(DataOutputStream output) throws IOException
	{
		output.writeShort(attributes.size());

		for (Attribute a: attributes)
			a.write(output);
	}

	/**
	 * Read the major and minor versions
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while writing the class
	 */
	private void readVersion(DataInputStream input) throws IOException
	{
		minorVersion = input.readUnsignedShort();
		majorVersion = input.readUnsignedShort();
	}

	/**
	 * Read the major and minor versions
	 *
	 * @param output the output stream
	 * @throws IOException if an error occurs while writing the class
	 */
	private void writeVersion(DataOutputStream output) throws IOException
	{
		output.writeShort(minorVersion);
		output.writeShort(majorVersion);
	}

	/**
	 * Read the super class.
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while reading the class
	 */
	private void readSuperClass(DataInputStream input) throws IOException
	{
		superclassIndex = input.readUnsignedShort();

		if (superclassIndex == 0)
			superclassName = null;
		else
		{
			ClassConstant superClass = (ClassConstant) constantPool.getConstantAt(superclassIndex);
			superclassName = TypeNameFactory.fromPath(superClass.getValue().toString());
		}
	}

	/**
	 * Reads the class name
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while reading the class
	 */
	private void readClassName(DataInputStream input) throws IOException
	{
		classIndex = input.readUnsignedShort();
		ClassConstant thisClass = (ClassConstant) constantPool.getConstantAt(classIndex);
		if (log.isDebugEnabled())
		{
			log.debug("class index: " + classIndex);
			log.debug("class: " + thisClass);
		}
		className = TypeNameFactory.fromPath(thisClass.getValue().toString());
	}

	/**
	 * Reads the interfaces.
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while reading the class
	 */
	private void readInterfaces(DataInputStream input) throws IOException
	{
		final int interfaceCount = input.readUnsignedShort();

		interfaces = new ArrayList<TypeName>(interfaceCount);
		interfaceIndices = new ArrayList<Integer>(interfaceCount);

		for (int i = 0; i < interfaceCount; ++i)
		{
			interfaceIndices.add(input.readUnsignedShort());
			ClassConstant cInterface = (ClassConstant) constantPool.getConstantAt(interfaceIndices.get(i));
			interfaces.add(TypeNameFactory.fromPath(cInterface.getValue().toString()));
		}
	}

	/**
	 * Reads the fields.
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while reading the class
	 */
	private void readFields(DataInputStream input) throws IOException
	{
		final int fieldCount = input.readUnsignedShort();
		fields = Lists.newArrayListWithCapacity(fieldCount);
		for (int i = 0; i < fieldCount; ++i)
			fields.add(new ClassField(input, constantPool));
	}

	/**
	 * Reads the methods.
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while reading the class
	 */
	private void readMethods(DataInputStream input) throws IOException
	{
		final int methodCount = input.readUnsignedShort();
		methods = Lists.newArrayListWithCapacity(methodCount);
		for (int i = 0; i < methodCount; ++i)
			methods.add(new ClassMethod(input, constantPool));
	}

	/**
	 * Reads the class attribute information.
	 *
	 * @param input the input stream
	 * @throws IOException if an error occurs while reading the class
	 */
	private void readAttributes(DataInputStream input) throws IOException
	{
		final int attributeCount = input.readUnsignedShort();

		attributes = new ArrayList<Attribute>(attributeCount);
		AttributeFactory factory = new AttributeFactory();

		for (int i = 0; i < attributeCount; ++i)
			attributes.add(factory.readAttribute(input, constantPool));
	}

	/**
	 * Returns the Attributes for a class.
	 *
	 * @return the class attributes
	 */
	public List<Attribute> getAttributes()
	{
		return Collections.unmodifiableList(attributes);
	}

	/**
	 * Returns the class constant pool.
	 *
	 * @return the class constant pool.
	 */
	public ConstantPool getConstantPool()
	{
		return constantPool;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("class " + getClassName().asIdentifier());
		if (getSuperClassName() != null)
		{
			result.append(" extends ");
			result.append(getSuperClassName().asIdentifier());
			result.append("\n");
		}
		if (interfaces.size() > 0)
		{
			result.append(" implements");
			for (Iterator<TypeName> i = interfaces.iterator(); i.hasNext();)
			{
				result.append(i.next());
				if (i.hasNext())
					result.append(", ");
			}
		}

		for (ClassField field: fields)
		{
			String fieldAccessFlags = field.getAccessFlags().getName();
			TypeName type = field.getDescriptor();
			String name = field.getName();
			ConstantValueAttribute constantValue = field.getConstant();

			String constantExpression;
			if (constantValue != null)
				constantExpression = " =" + constantValue.getValue().getValue().toString();
			else
				constantExpression = "";

			result.append(fieldAccessFlags);
			result.append(" ");
			result.append(type.asIdentifier());
			result.append(" ");
			result.append(name);
			result.append(constantExpression);
			result.append(";\n");
		}

		for (ClassMethod method: methods)
		{
			result.append(method.getAccessFlags().getName());
			result.append(" ");
			result.append(method.getDescriptor());
			result.append(" ");
			result.append(method.getName());
			Collection<TypeName> exceptions = method.getExceptions();
			if (exceptions.size() > 0)
			{
				result.append("throws ");
				for (Iterator<TypeName> i = exceptions.iterator(); i.hasNext();)
				{
					result.append(i.next().asIdentifier());
					if (i.hasNext())
						result.append(", ");
				}
			}
			result.append(";\n");
		}

		if (attributes.size() > 0)
		{
			result.append("Attributes: ");
			for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
			{
				result.append(i.next());
				if (i.hasNext())
					result.append(", ");
			}
		}
		return result.toString();
	}

	/**
	 * Prints out the detail of the specified class.
	 *
	 * @param args the command-line arguments
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String args[])
	{
		ClassFile cf = new ClassFile(new File(args[0]));
		System.out.println(cf.toString());
	}
}
