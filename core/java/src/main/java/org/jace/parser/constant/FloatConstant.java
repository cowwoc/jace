package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public class FloatConstant implements Constant
{
	private final int bytes;

	public FloatConstant(int bytes)
	{
		this.bytes = bytes;
	}

	@Override
	public int getSize()
	{
		return 1;
	}

	@Override
	public Object getValue()
	{
		return Integer.valueOf(bytes);
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new FloatConstantReader().getTag());
		output.writeInt(bytes);
	}
}
