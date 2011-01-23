package org.jace.parser.attribute;

import org.jace.parser.ConstantPool;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A DeprecatedAttribute represents the fact that its target has been deprecated and superceded.
 *
 * @author Toby Reyelts
 */
public class DeprecatedAttribute implements Attribute
{
	/* From the JVM specification.
	 *
	 * (u1 represents an unsigned byte)
	 * (u2 represents an unsigned short)
	 * (u4 represents an unsigned int)
	 *
	 * attribute_info {
	 *   u2 attribute_name_index;  // This must be "Deprecated"
	 *   u4 attribute_length;      // This must be 0
	 * }
	 */
	private final int nameIndex;
	private final int length;
	private final ConstantPool pool;

	/**
	 * Creates a new DeprecatedAttribute.
	 * 
	 * @param stream the stream to read from
	 * @param nameIndex the attribute index in the constant pool
	 * @param pool the constant pool to read from
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	public DeprecatedAttribute(InputStream stream, int nameIndex, ConstantPool pool)
		throws IOException
	{
		this.pool = pool;
		this.nameIndex = nameIndex;

		// Read the name for this constant.
		// From the VM spec, we know it must be equal to "Deprecated".
		Constant c = pool.getConstantAt(nameIndex);

		if (c instanceof UTF8Constant)
		{
			String name = c.getValue().toString();

			if (!name.equals("Deprecated"))
			{
				throw new ClassFormatError("While reading a DeprecatedAttribute, the name, Deprecated, was expected, "
																	 + "but the name " + name + " was encountered.");
			}
		}
		else
		{
			throw new ClassFormatError("While reading a DeprecatedAttribute, a UTF8Constant was expected, "
																 + "but a constant of type " + c.getClass().getName()
																 + " was encountered.");
		}

		DataInputStream input = new DataInputStream(stream);

		// Read the length of the attribute.
		// From the VM spec, we know that length must = 0.
		length = input.readInt();

		if (length != 0)
		{
			throw new ClassFormatError("While reading a DeprecatedAttribute, an attribute length of size 0 was expected, "
																 + "but an attribute length of size " + length
																 + " was encountered.");
		}
	}

	/**
	 * Returns the attribute name.
	 *
	 * @return the attribute name
	 */
	@Override
	public String getName()
	{
		return pool.getConstantAt(nameIndex).toString();
	}

	/**
	 * Returns the attribute length.
	 *
	 * @return the attribute length
	 */
	public int getLength()
	{
		return 0;
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeShort(nameIndex);
		output.writeInt(length);
	}

	@Override
	public String toString()
	{
		return getClass().getName();
	}
}
