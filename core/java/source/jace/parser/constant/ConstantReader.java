
package jace.parser.constant;

import jace.parser.*;

import java.io.InputStream;

public interface ConstantReader {

public int getTag();

public Constant readConstant( InputStream input, ConstantPool pool ) throws ClassFormatError;

}
