package org.jace.parser.constant;

import java.io.InputStream;
import org.jace.parser.ConstantPool;

public interface ConstantReader
{
	public int getTag();

	public Constant readConstant(InputStream input, ConstantPool pool) throws ClassFormatError;
}
