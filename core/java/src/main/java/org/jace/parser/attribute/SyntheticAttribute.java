package org.jace.parser.attribute;

import org.jace.parser.ConstantPool;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A SyntheticAttribute represents the fact that its target does not appear
 * in the original source code.
 *
 * SyntheticAttributes are used to support inner classes.
 *
 * @author Toby Reyelts
 *
 */
public class SyntheticAttribute implements Attribute
{
	/* From the JVM specification.
	 *
	 * (u1 represents an unsigned byte)
	 * (u2 represents an unsigned short)
	 * (u4 represents an unsigned int)
	 *
	 * attribute_info {
	 *   u2 attribute_name_index;  // This must be "Synthetic"
	 *   u4 attribute_length;      // This must be 0
	 * }
	 */
	private final static String NAME = "Synthetic";
	private int nameIndex;
	private int length;
	private ConstantPool pool;

	/**
	 * Creates a new SyntheticAttribute, which automatically adds itself to
	 * the constant pool.
	 *
	 * @param pool the constant pool
	 */
	public SyntheticAttribute(ConstantPool pool)
	{
		this.length = 0;
		this.pool = pool;
		this.nameIndex = pool.addUTF8(NAME);
	}

	/**
	 * Creates a new SyntheticAttribute.
	 *
	 * @param stream the stream to read from
	 * @param nameIndex the attribute index in the constant pool
	 * @param pool the constant pool to read from
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	public SyntheticAttribute(InputStream stream, int nameIndex, ConstantPool pool) throws IOException
	{
		this.pool = pool;
		this.nameIndex = nameIndex;

		/* Read the name for this constant.
		 * From the VM spec, we know it must be equal to "Synthetic".
		 */
		Constant c = pool.getConstantAt(nameIndex);

		if (c instanceof UTF8Constant)
		{

			String name = c.getValue().toString();

			if (!name.equals(NAME))
			{
				throw new ClassFormatError("While reading a " + getClass().getName() + ", the name, " + NAME
																	 + ", was expected, " + "but the name " + name
																	 + " was encountered.");
			}
		}
		else
		{
			throw new ClassFormatError("While reading a SyntheticAttribute, a UTF8Constant was expected, "
																 + "but a constant of type " + c.getClass().getName()
																 + " was encountered.");
		}

		DataInputStream input = new DataInputStream(stream);

		// Read the length of the attribute.
		// From the VM spec, we know that length must = 0.
		length = input.readInt();

		if (length != 0)
		{
			throw new ClassFormatError("While reading a SyntheticAttribute, an attribute length of size 0 was expected, "
																 + "but an attribute length of size " + length
																 + " was encountered.");
		}
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeShort(nameIndex);
		output.writeInt(length);
	}

	/**
	 * Returns the name for this Attribute.
	 *
	 * @return the name for this Attribute
	 */
	@Override
	public String getName()
	{
		return pool.getConstantAt(nameIndex).toString();
	}

	@Override
	public String toString()
	{
		return getClass().getName();
	}

	/**
	 * Returns the length of this Attribute.
	 *
	 * @return the length of this Attribute
	 */
	public int getLength()
	{
		return length;
	}
}
