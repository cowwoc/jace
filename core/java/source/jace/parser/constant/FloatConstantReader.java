package jace.parser.constant;

import jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FloatConstantReader implements ConstantReader {

  final static short TAG = 4;

  public int getTag() {
    return TAG;
  }

  public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError {

    int value;

    try {
      value = new DataInputStream(is).readInt();
    }
    catch (IOException e) {
      ClassFormatError exception = new ClassFormatError("Unable to read the Integer Constant");
      exception.initCause(e);
      throw exception;
    }

    return new FloatConstant(value);
  }
}
