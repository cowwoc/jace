package org.jace.parser.attribute;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jace.parser.ConstantPool;

/**
 * An UnknownAttribute represents an Attribute type that isn't recognized
 * because it hasn't been specified in the JVM specification and is a custom
 * attribute. (JACE can be extended to recognize and provide special support
 * for custom Attributes).
 *
 * @author Toby Reyelts
 */
public class UnknownAttribute implements Attribute
{
	/* From the JVM specification.
	 *
	 * (u1 represents an unsigned byte)
	 * (u2 represents an unsigned short)
	 * (u4 represents an unsigned int)
	 *
	 * attribute_info {
	 *   u2 attribute_name_index;
	 *   u4 attribute_length;
	 *   u1 info[attribute_length];
	 * }
	 */
	private int nameIndex;
	private int length;
	private byte[] data;
	private ConstantPool pool;

	/**
	 * Creates a new UnknownAttribute.
	 *
	 * @param stream the stream to read from
	 * @param nameIndex the attribute index in the constant pool
	 * @param pool the constant pool to read from
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	public UnknownAttribute(InputStream stream, int nameIndex, ConstantPool pool) throws IOException
	{
		this.pool = pool;
		this.nameIndex = nameIndex;
		DataInputStream input = new DataInputStream(stream);
		length = input.readInt();
		data = new byte[length];
		input.readFully(data);
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeShort(nameIndex);
		output.writeInt(length);
		output.write(data);
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
	 * Returns the length of the attribute.
	 *
	 * @return the length of the attribute
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * Returns the attribute data.
	 *
	 * @return the attribute data
	 */
	public byte[] getData()
	{
		byte[] result = new byte[data.length];
		System.arraycopy(data, 0, result, 0, data.length);
		return result;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
