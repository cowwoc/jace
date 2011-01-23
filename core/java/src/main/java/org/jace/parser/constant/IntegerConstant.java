package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public class IntegerConstant implements Constant
{
	private final int bytes;

	public IntegerConstant(int bytes)
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
		output.writeByte(new IntegerConstantReader().getTag());
		output.writeInt(bytes);
	}
}
