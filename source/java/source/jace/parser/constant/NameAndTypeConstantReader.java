package jace.parser.constant;

import jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NameAndTypeConstantReader implements ConstantReader {

  static final short TAG = 12;

  public int getTag() {
    return TAG;
  }

  public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError {

    int nameIndex;
    int descriptorIndex;

    try {
      nameIndex = new DataInputStream(is).readUnsignedShort();
      descriptorIndex = new DataInputStream(is).readUnsignedShort();
    }
    catch (IOException e) {
      ClassFormatError exception = new ClassFormatError("Unable to read the String Constant");
      exception.initCause(e);
      throw exception;
    }

    return new NameAndTypeConstant(nameIndex, descriptorIndex);
  }
}

