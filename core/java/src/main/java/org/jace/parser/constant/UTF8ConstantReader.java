package org.jace.parser.constant;

import org.jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UTF8ConstantReader implements ConstantReader
{
	private static final short TAG = 1;

	@Override
	public int getTag()
	{
		return TAG;
	}

	@Override
	public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError
	{
		byte[] bytes;

		try
		{
			DataInputStream input = new DataInputStream(is);
			int length = input.readUnsignedShort();
			bytes = new byte[length];

			for (int i = 0; i < length; ++i)
			{
				int val = input.read();

				if (val == -1)
				{
					String msg = "Unexpected end of input while trying to read a UTF8 Constant.";
					throw new ClassFormatError(msg);
				}
				bytes[i] = (byte) val;
			}
		}
		catch (IOException e)
		{
			ClassFormatError exception = new ClassFormatError("Unable to read the String Constant");
			exception.initCause(e);
			throw exception;
		}

		return new UTF8Constant(bytes);
	}
}
