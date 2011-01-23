package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleConstant implements Constant
{
	private final int highByte;
	private final int lowByte;

	public DoubleConstant(int highByte, int lowByte)
	{
		this.highByte = highByte;
		this.lowByte = lowByte;
	}

	@Override
	public Object getValue()
	{
		return new Double(((long) highByte << 32) | lowByte);
	}

	@Override
	public int getSize()
	{
		return 2;
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new DoubleConstantReader().getTag());
		output.writeInt(highByte);
		output.writeInt(lowByte);
	}
}
