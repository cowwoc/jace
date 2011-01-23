package org.jace.parser.constant;

import org.jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClassConstantReader implements ConstantReader
{
	private final static short TAG = 7;

	@Override
	public int getTag()
	{
		return TAG;
	}

	@Override
	public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError
	{
		int nameIndex;

		try
		{
			nameIndex = new DataInputStream(is).readUnsignedShort();
		}
		catch (IOException e)
		{
			ClassFormatError exception = new ClassFormatError("Unable to read the Class Constant");
			exception.initCause(e);
			throw exception;
		}

		return new ClassConstant(nameIndex, pool);
	}
}
