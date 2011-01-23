package org.jace.parser.constant;

import org.jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NameAndTypeConstantReader implements ConstantReader
{
	private static final short TAG = 12;

	@Override
	public int getTag()
	{
		return TAG;
	}

	@Override
	public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError
	{
		int nameIndex;
		int descriptorIndex;

		try
		{
			nameIndex = new DataInputStream(is).readUnsignedShort();
			descriptorIndex = new DataInputStream(is).readUnsignedShort();
		}
		catch (IOException e)
		{
			ClassFormatError exception = new ClassFormatError("Unable to read the String Constant");
			exception.initCause(e);
			throw exception;
		}

		return new NameAndTypeConstant(nameIndex, descriptorIndex);
	}
}
