package org.jace.parser.constant;

import org.jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FieldRefConstantReader implements ConstantReader
{
	private static final short TAG = 9;

	@Override
	public int getTag()
	{
		return TAG;
	}

	@Override
	public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError
	{
		int classIndex;
		int nameAndTypeIndex;

		try
		{
			classIndex = new DataInputStream(is).readUnsignedShort();
			nameAndTypeIndex = new DataInputStream(is).readUnsignedShort();
		}
		catch (IOException e)
		{
			ClassFormatError exception = new ClassFormatError("Unable to read the FieldRef Constant");
			exception.initCause(e);
			throw exception;
		}

		return new FieldRefConstant(classIndex, nameAndTypeIndex);
	}
}
