package jace.parser.constant;

import jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LongConstantReader implements ConstantReader {

  final static short TAG = 5;

  public int getTag() {
    return TAG;
  }

  public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError {

    int highByte;
    int lowByte;

    try {
      highByte = new DataInputStream(is).readInt();
      lowByte = new DataInputStream(is).readInt();
    }
    catch (IOException e) {
      ClassFormatError exception = new ClassFormatError("Unable to read the Long Constant");
      exception.initCause(e);
      throw exception;
    }

    return new LongConstant(highByte, lowByte);
  }
}

