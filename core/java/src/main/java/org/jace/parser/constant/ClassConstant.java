package org.jace.parser.constant;

import org.jace.parser.ConstantPool;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClassConstant implements Constant
{
	private int nameIndex;
	private final ConstantPool pool;

	public ClassConstant(int nameIndex, ConstantPool pool)
	{
		this.nameIndex = nameIndex;
		this.pool = pool;
	}

	@Override
	public int getSize()
	{
		return 1;
	}

	public int getNameIndex()
	{
		return nameIndex;
	}

	public void setNameIndex(int index)
	{
		this.nameIndex = index;
	}

	@Override
	public Object getValue()
	{
		UTF8Constant c = (UTF8Constant) pool.getConstantAt(nameIndex);
		return c.getValue();
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new ClassConstantReader().getTag());
		output.writeShort(nameIndex);
	}

	@Override
	public String toString()
	{
		return (String) getValue();
	}
}
