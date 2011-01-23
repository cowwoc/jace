package org.jace.parser.constant;

import org.jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringConstantReader implements ConstantReader
{
	private static final short TAG = 8;

	@Override
	public int getTag()
	{
		return TAG;
	}

	@Override
	public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError
	{
		int index;

		try
		{
			index = new DataInputStream(is).readUnsignedShort();
		}
		catch (IOException e)
		{
			ClassFormatError exception = new ClassFormatError("Unable to read the String Constant");
			exception.initCause(e);
			throw exception;
		}

		return new StringConstant(pool, index);
	}
}
