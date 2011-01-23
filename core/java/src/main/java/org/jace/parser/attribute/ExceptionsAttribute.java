package org.jace.parser.attribute;

import org.jace.parser.ConstantPool;
import org.jace.parser.constant.ClassConstant;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An ExceptionsAttribute represents the checked exceptions which may
 * be thrown from its target method.
 *
 * @author Toby Reyelts
 */
public class ExceptionsAttribute implements Attribute
{
	/* From the JVM specification.
	 *
	 * (u1 represents an unsigned byte)
	 * (u2 represents an unsigned short)
	 * (u4 represents an unsigned int)
	 *
	 *
	 * Exceptions_attribute {
	 *   u2 attribute_name_index;
	 *   u4 attribute_length;
	 *   u2 number_of_exceptions;
	 *   u2 exception_index_table[number_of_exceptions];
	 * }
	 */
	private final int nameIndex;
	private final int length;
	private final int numberOfExceptions;
	/**
	 * A list of indices to ClassConstant's in the constant pool.
	 *
	 * From the JVM specification:
	 *
	 *  Each value in the exception_index_table array must be a valid index into the
	 *  constant_pool table. The constant_pool entry referenced by each table item
	 *  must be a CONSTANT_Class_info (§4.4.1) structure representing a class type
	 *  that this method is declared to throw.
	 */
	private final int[] exceptionIndexTable;
	private final ConstantPool pool;

	/**
	 * Creates a new ExceptionsAttribute.
	 *
	 * @param stream the stream to read from
	 * @param nameIndex the attribute index in the constant pool
	 * @param pool the constant pool to read from
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	public ExceptionsAttribute(InputStream stream, int nameIndex, ConstantPool pool)
		throws IOException
	{
		this.pool = pool;
		this.nameIndex = nameIndex;

		// Read the name for this constant.
		// From the VM spec, we know it must be equal to "Exceptions".
		Constant c = pool.getConstantAt(nameIndex);

		if (c instanceof UTF8Constant)
		{
			String name = c.getValue().toString();

			if (!name.equals("Exceptions"))
			{
				throw new ClassFormatError("While reading an ExceptionsAttribute, the name, Exceptions, was expected, "
																	 + "but the name " + name + " was encountered.");
			}
		}
		else
		{
			throw new ClassFormatError("While reading an ExceptionsAttribute, a UTF8Constant was expected, "
																 + "but a constant of type " + c.getClass().getName()
																 + " was encountered.");
		}

		DataInputStream input = new DataInputStream(stream);

		// Read the length of the attribute.
		// While we don't know exactly what the length must be,
		// we do know that it must be 2 or greater to include the length of the
		// number_of_exceptions value.
		this.length = input.readInt();

		if (length < 2)
		{
			throw new ClassFormatError("While reading an ExceptionsAttribute, an attribute length of size 0 was expected, "
																 + "but an attribute length of size " + length
																 + " was encountered.");
		}

		// Read the exceptions
		numberOfExceptions = input.readUnsignedShort();
		exceptionIndexTable = new int[numberOfExceptions];
		for (int i = 0; i < numberOfExceptions; ++i)
		{
			int index = input.readUnsignedShort();
			exceptionIndexTable[i] = index;
		}
	}

	/**
	 * Returns the name for this Attribute.
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
		return length;
	}

	/**
	 * Returns a list of the exceptions declared for this ExceptionAttribute.
	 *
	 * @return the list of the exceptions declared for this ExceptionAttribute
	 * @throws ClassCastException if any exception index doesn't point
	 *   to a ClassConstant. This should probably be reported, instead, as a
	 *   ClassFormatError at the time of creation of this ExceptionsAttribute.
	 */
	public ClassConstant[] getExceptions()
	{

		ClassConstant[] exceptions = new ClassConstant[numberOfExceptions];

		for (int i = 0; i < numberOfExceptions; ++i)
		{
			exceptions[i] = (ClassConstant) pool.getConstantAt(exceptionIndexTable[i]);
		}

		return exceptions;
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeShort(nameIndex);
		output.writeInt(length);
		output.writeShort(exceptionIndexTable.length);

		for (int i = 0; i < exceptionIndexTable.length; ++i)
		{
			output.writeShort(exceptionIndexTable[i]);
		}
	}

	@Override
	public String toString()
	{
		return getClass().getName();
	}
}
