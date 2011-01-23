package org.jace.parser.constant;

import org.jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IntegerConstantReader implements ConstantReader
{
	private static final short TAG = 3;

	@Override
	public int getTag()
	{
		return TAG;
	}

	@Override
	public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError
	{
		int value;

		try
		{
			value = new DataInputStream(is).readInt();
		}
		catch (IOException e)
		{
			ClassFormatError exception = new ClassFormatError("Unable to read the Integer Constant");
			exception.initCause(e);
			throw exception;
		}

		return new IntegerConstant(value);
	}
}
