package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public class UTF8Constant implements Constant
{
	private byte[] mBytes;

	public UTF8Constant(byte[] bytes)
	{
		mBytes = new byte[bytes.length];
		System.arraycopy(bytes, 0, mBytes, 0, bytes.length);
	}

	@Override
	public int getSize()
	{
		return 1;
	}

	@Override
	public Object getValue()
	{
		return new String(mBytes);
	}

	public void setValue(String str)
	{
		mBytes = str.getBytes();
	}

	@Override
	public String toString()
	{
		return (String) getValue();
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new UTF8ConstantReader().getTag());
		output.writeShort(mBytes.length);
		output.write(mBytes);
	}
}
