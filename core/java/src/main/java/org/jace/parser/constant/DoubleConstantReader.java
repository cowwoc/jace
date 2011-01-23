package org.jace.parser.constant;

import org.jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DoubleConstantReader implements ConstantReader
{
	private static final short TAG = 6;

	@Override
	public int getTag()
	{
		return TAG;
	}

	@Override
	public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError
	{
		int highByte;
		int lowByte;

		try
		{
			highByte = new DataInputStream(is).readInt();
			lowByte = new DataInputStream(is).readInt();
		}
		catch (IOException e)
		{
			ClassFormatError exception = new ClassFormatError("Unable to read the Double Constant");
			exception.initCause(e);
			throw exception;
		}

		return new DoubleConstant(highByte, lowByte);
	}
}
