package org.jace.parser.attribute;

import org.jace.parser.ConstantPool;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Toby Reyelts
 */
public class CodeAttribute implements Attribute
{
	/* From the JVM spec:
	 *
	 * Code_attribute {
	 * 	u2 attribute_name_index;
	 * 	u4 attribute_length;
	 * 	u2 max_stack;
	 * 	u2 max_locals;
	 * 	u4 code_length;
	 * 	u1 code[code_length];
	 * 	u2 exception_table_length;
	 * 	{    	u2 start_pc;
	 * 	      	u2 end_pc;
	 * 	      	u2  handler_pc;
	 * 	      	u2  catch_type;
	 * 	}	exception_table[exception_table_length];
	 * 	u2 attributes_count;
	 * 	attribute_info attributes[attributes_count];
	 * }
	 */
	/**
	 * A Java exception.
	 */
	@SuppressWarnings("PublicInnerClass")
	public static class CodeException
	{
		private final int startPc;
		private final int endPc;
		private final int handlerPc;
		private final int catchType;

		public CodeException(DataInputStream input) throws IOException
		{
			startPc = input.readShort();
			endPc = input.readShort();
			handlerPc = input.readShort();
			catchType = input.readShort();
		}

		public void write(DataOutputStream output) throws IOException
		{
			output.writeShort(startPc);
			output.writeShort(endPc);
			output.writeShort(handlerPc);
			output.writeShort(catchType);
		}

		public int startPc()
		{
			return startPc;
		}

		public int endPc()
		{
			return endPc;
		}

		public int handlerPc()
		{
			return handlerPc;
		}

		public int catchType()
		{
			return catchType;
		}
	}
	private int nameIndex;
	private int length;
	private int maxStack;
	private int maxLocals;
	private byte[] code;
	private ArrayList<CodeException> exceptions;
	private ArrayList<Attribute> attributes;
	private ConstantPool pool;

	/**
	 * Creates a new LocalVariableTableAttribute.
	 *
	 * @param stream the stream to read from
	 * @param nameIndex the attribute index in the constant pool
	 * @param pool the constant pool to read from
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	public CodeAttribute(InputStream stream, int nameIndex, ConstantPool pool) throws IOException
	{
		this.pool = pool;
		this.nameIndex = nameIndex;

		Constant c = pool.getConstantAt(nameIndex);

		if (c instanceof UTF8Constant)
		{
			String name = c.getValue().toString();

			if (!name.equals("Code"))
			{
				throw new ClassFormatError("While reading a CodeAttribute, the name Code was expected, "
																	 + "but the name " + name + " was encountered.");
			}
		}
		else
		{
			throw new ClassFormatError("While reading a CodeAttribute, a UTF8Constant was expected, "
																 + "but a constant of type " + c.getClass().getName()
																 + " was encountered.");
		}

		DataInputStream input = new DataInputStream(stream);

		// Read the length of the attribute
		length = input.readInt();

		maxStack = input.readShort();
		maxLocals = input.readShort();
		code = new byte[input.readInt()];
		input.readFully(code);

		int numExceptions = input.readShort();
		exceptions = new ArrayList<CodeException>(numExceptions);

		for (int i = 0; i < numExceptions; ++i)
			exceptions.add(new CodeException(input));

		AttributeFactory factory = new AttributeFactory();

		int numAttributes = input.readShort();
		attributes = new ArrayList<Attribute>(numAttributes);

		for (int i = 0; i < numAttributes; ++i)
			attributes.add(factory.readAttribute(input, pool));
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeShort(nameIndex);
		output.writeInt(length);
		output.writeShort(maxStack);
		output.writeShort(maxLocals);
		output.writeInt(code.length);
		output.write(code);
		output.writeShort(exceptions.size());
		for (CodeException e: exceptions)
			e.write(output);
		output.writeShort(attributes.size());
		for (Attribute a: attributes)
			a.write(output);
	}

	public LocalVariableTableAttribute getLocalVariableTable()
	{
		for (Attribute a: attributes)
		{
			if (a instanceof LocalVariableTableAttribute)
				return (LocalVariableTableAttribute) a;
		}
		return null;
	}

	@Override
	public String getName()
	{
		return pool.getConstantAt(nameIndex).toString();
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

	@Override
	public String toString()
	{
		return getClass().getName();
	}
}
