package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;
import org.jace.parser.ConstantPool;

public class StringConstant implements Constant
{
	private final int index;
	private final ConstantPool pool;

	public StringConstant(ConstantPool pool, int index)
	{
		this.pool = pool;
		this.index = index;
	}

	@Override
	public int getSize()
	{
		return 1;
	}

	@Override
	public Object getValue()
	{
		UTF8Constant c = (UTF8Constant) pool.getConstantAt(index);
		return c.getValue();
	}

	@Override
	public String toString()
	{
		return "A StringConstant pointing to a UTF8Constant at index " + index;
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new StringConstantReader().getTag());
		output.writeShort(index);
	}
}
