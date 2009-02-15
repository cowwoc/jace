package jace.parser.constant;

import jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClassConstantReader implements ConstantReader {

  final static short TAG = 7;

  public int getTag() {
    return TAG;
  }

  public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError {

    int nameIndex;

    try {
      nameIndex = new DataInputStream(is).readUnsignedShort();
    }
    catch (IOException e) {
      ClassFormatError exception = new ClassFormatError("Unable to read the Class Constant");
      exception.initCause(e);
      throw exception;
    }

    return new ClassConstant(nameIndex, pool);
  }
}

