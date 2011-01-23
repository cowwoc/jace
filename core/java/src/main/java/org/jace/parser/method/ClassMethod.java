package org.jace.parser.method;

import com.google.common.collect.Lists;
import org.jace.metaclass.TypeName;
import org.jace.metaclass.TypeNameFactory;
import org.jace.parser.ConstantPool;
import org.jace.parser.attribute.Attribute;
import org.jace.parser.attribute.AttributeFactory;
import org.jace.parser.attribute.CodeAttribute;
import org.jace.parser.attribute.ExceptionsAttribute;
import org.jace.parser.constant.ClassConstant;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a class or instance method.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassMethod
{
	/**
	 * From the JVM specification:
	 *
	 * method_info {
	 *   u2 access_flags;
	 *   u2 name_index;
	 *   u2 descriptor_index;
	 *   u2 attributes_count;
	 *   attribute_info attributes[attributes_count];
	 *  }
	 */
	private int accessFlags;
	private int nameIndex;
	private int descriptorIndex;
	private final List<Attribute> attributes;
	private final ConstantPool pool;
	private List<TypeName> parameters;
	private TypeName returnType;

	/**
	 * Reads in a ClassMethod from a Java class file.
	 *
	 * @param stream The InputStream from which this ClassMethod will be read.
	 * The stream must be pointing to the beginning of a valid method definition
	 * as detailed in the JVM specification.
	 *
	 * @param pool The ConstantPool for the class file to which this ClassMethod belongs.
	 *
	 * @throws IOException if an error occurs while trying to read the ClassMethod.
	 *
	 */
	public ClassMethod(InputStream stream, ConstantPool pool) throws IOException
	{
		this.pool = pool;

		DataInputStream input = new DataInputStream(stream);
		accessFlags = input.readUnsignedShort();
		nameIndex = input.readUnsignedShort();
		descriptorIndex = input.readUnsignedShort();
		int attributesCount = input.readUnsignedShort();

		attributes = Lists.newArrayListWithCapacity(attributesCount);
		AttributeFactory factory = new AttributeFactory();

		for (int i = 0; i < attributesCount; ++i)
			attributes.add(factory.readAttribute(stream, pool));
		parseDescriptor();
	}

	public void write(DataOutputStream output) throws IOException
	{
		output.writeShort(accessFlags);
		output.writeShort(nameIndex);
		output.writeShort(descriptorIndex);
		output.writeShort(attributes.size());

		for (Attribute a: attributes)
			a.write(output);
	}

	public List<Attribute> getAttributes()
	{
		return Collections.unmodifiableList(attributes);
	}

	public CodeAttribute getCode()
	{
		for (Attribute a: attributes)
		{
			if (a instanceof CodeAttribute)
				return (CodeAttribute) a;
		}
		return null;
	}

	public void addAttribute(Attribute a)
	{
		attributes.add(a);
	}

	/**
	 * Returns the MethodAccessFlagSet for this ClassMethod.
	 *
	 * @return the MethodAccessFlagSet for this ClassMethod
	 */
	public MethodAccessFlagSet getAccessFlags()
	{
		return new MethodAccessFlagSet(accessFlags);
	}

	public void setAccessFlags(MethodAccessFlagSet set)
	{
		accessFlags = set.getValue();
	}

	public int getNameIndex()
	{
		return nameIndex;
	}

	public void setNameIndex(int index)
	{
		nameIndex = index;
	}

	public int getDescriptorIndex()
	{
		return descriptorIndex;
	}

	public void setDescriptorIndex(int index)
	{
		descriptorIndex = index;
	}

	/**
	 * Returns the name of this ClassMethod.
	 *
	 * For example, "execute"
	 *
	 * @throws RuntimeException that should probably instead be ClassFormatError.
	 * @return the name of this ClassMethod
	 */
	public String getName()
	{
		Constant c = pool.getConstantAt(nameIndex);

		if (c instanceof UTF8Constant)
			return c.getValue().toString();
		throw new RuntimeException("Not a UTF8Constant: " + c.getClass().getName());
	}

	/**
	 * Returns the descriptor for this ClassMethod.
	 *
	 * For example, "(Ljava/lang/String;I)[B", which would
	 * have a signature of: byte[] xxx( java.lang.String aString, int anInt )
	 *
	 * @return the method descriptor
	 * @throws ClassFormatError if the descriptor isn't of the right type.
	 * This should probably be thrown during the parsing stage.
	 */
	public String getDescriptor() throws ClassFormatError
	{
		Constant c = pool.getConstantAt(descriptorIndex);

		if (c instanceof UTF8Constant)
			return c.getValue().toString();
		throw new ClassFormatError("Not a UTF8Constant: " + c.getClass().getName());
	}

	/**
	 * Returns the return type for this ClassMethod.
	 *
	 * The return type is formatted in internal format.
	 *
	 * For example,
	 *   Ljava/lang/String or
	 *   [B
	 * @return the return type for this ClassMethod
	 */
	public TypeName getReturnType()
	{
		return returnType;
	}

	/**
	 * Returns the parameter types for this ClassMethod.
	 *
	 * The parameter types are formatted in internal format.
	 *
	 * For example,
	 *   Ljava/lang/String or
	 *   [B
	 *
	 * @return an unmodifiable list of type names
	 *
	 */
	public List<TypeName> getParameterTypes()
	{
		return Collections.unmodifiableList(parameters);
	}

	/**
	 * Sets the parameters and return type for this ClassMethod
	 * by parsing the descriptor.
	 *
	 */
	private void parseDescriptor() throws IOException
	{
		String descriptor = getDescriptor();
		StringReader reader = new StringReader(descriptor);

		// read the opening '('
		if (reader.read() != '(')
		{
			throw new RuntimeException("The descriptor < " + descriptor + " > is invalid. "
																 + "It does not begin its parameter list with a '('");
		}

		// read the parameters
		parameters = Lists.newArrayList();
		try
		{
			while (true)
			{
				String parameter = readType(reader);
				if (parameter == null)
					break;
				parameters.add(TypeNameFactory.fromDescriptor(parameter));
			}
		}
		catch (RuntimeException e)
		{
			throw new RuntimeException("The descriptor < " + descriptor + " > is invalid", e);
		}

		// read the closing ')'
		if (reader.read() != ')')
		{
			throw new RuntimeException("The descriptor < " + descriptor + " > is invalid. "
																 + "It does not end its parameter list with a ')'");
		}

		// read the return type
		try
		{
			returnType = TypeNameFactory.fromDescriptor(readType(reader));
		}
		catch (RuntimeException e)
		{
			throw new RuntimeException("The descriptor < " + descriptor + " > is invalid", e);
		}

		if (returnType == null)
		{
			throw new RuntimeException("The descriptor < " + descriptor + " > is invalid. "
																 + "It does not specify a valid return type.");
		}

		if (reader.read() != -1)
		{
			throw new RuntimeException("The descriptor < " + descriptor + " > is invalid. "
																 + "It does not end after specifying the return type.");
		}
	}

	private String readType(StringReader reader) throws IOException
	{
		final char[] primitiveTypes =
		{
			'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z'
		};

		// we make sure that we can return parsing, back to where it came from
		reader.mark(0);

		// the type may be V (void), so we check for that possibility first
		int c;
		c = reader.read();

		if (c == 'V')
			return "V";
		reader.reset();

		// we may potentially be at the end of a parameter list
		if (c == ')')
			return null;
		// the type can begin with any number of array specifiers
		@SuppressWarnings("StringBufferWithoutInitialCapacity")
		StringBuilder type = new StringBuilder();
		while (true)
		{
			c = reader.read();
			if (c != '[')
				break;
			type.append((char) c);
		}

		// now that we've read the array specifiers, we're going to check to see if the type is a primitive
		for (int i = 0; i < primitiveTypes.length; ++i)
		{
			if (c == primitiveTypes[i])
			{
				type.append((char) c);
				return type.toString();
			}
		}

		if (c != 'L')
		{
			throw new RuntimeException("The descriptor is badly formatted. "
																 + "A type was expected, but none could be found.");
		}

		type.append((char) c);

		// now, we read up to the terminating ';'
		while (true)
		{
			c = reader.read();

			// if we encounter end of stream, something is wrong
			if (c == -1)
			{
				throw new RuntimeException("The descriptor is badly formatted. "
																	 + "The type ends prematurely.");
			}

			type.append((char) c);

			if (c == ';')
				return type.toString();
		}
	}

	/**
	 * Returns the exceptions which have been declared for this ClassMethod.
	 *
	 * @return the exceptions which have been declared for this ClassMethod
	 */
	public Collection<TypeName> getExceptions()
	{
		for (Attribute a: attributes)
		{
			if (a instanceof ExceptionsAttribute)
			{
				ExceptionsAttribute ea = (ExceptionsAttribute) a;
				ClassConstant[] exceptionConstants = ea.getExceptions();
				Collection<TypeName> result = new ArrayList<TypeName>(exceptionConstants.length);
				for (int i = 0; i < exceptionConstants.length; ++i)
					result.add(TypeNameFactory.fromPath(exceptionConstants[i].toString()));
				return result;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Returns a String that contains debugging information for this ClassMethod.
	 *
	 * @return a String that contains debugging information for this ClassMethod
	 */
	@Override
	public String toString()
	{
		return "ClassMethod: \n" + "accessFlags: " + accessFlags + "\n" + "nameIndex: " + nameIndex
					 + "\n" + "descriptorIndex: " + descriptorIndex + "\n" + "attributesCount: " + attributes.
			size();
	}
}
