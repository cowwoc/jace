package jace.parser.constant;

import jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringConstantReader implements ConstantReader {

  static final short TAG = 8;

  public int getTag() {
    return TAG;
  }

  public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError {

    int index;

    try {
      index = new DataInputStream(is).readUnsignedShort();
    }
    catch (IOException e) {
      ClassFormatError exception = new ClassFormatError("Unable to read the String Constant");
      exception.initCause(e);
      throw exception;
    }

    return new StringConstant(pool, index);
  }
}

