package jace.parser.constant;

import jace.parser.ConstantPool;
import java.io.InputStream;

public interface ConstantReader
{
	public int getTag();

	public Constant readConstant(InputStream input, ConstantPool pool) throws ClassFormatError;
}
