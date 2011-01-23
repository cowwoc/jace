package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public class LongConstant implements Constant
{
	private final int highByte;
	private final int lowByte;

	public LongConstant(int highByte, int lowByte)
	{
		this.highByte = highByte;
		this.lowByte = lowByte;
	}

	@Override
	public Object getValue()
	{
		return "LongConstant.getValue() has not yet been implemented.";
	}

	@Override
	public int getSize()
	{
		return 2;
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new LongConstantReader().getTag());
		output.writeInt(highByte);
		output.writeInt(lowByte);
	}
}
