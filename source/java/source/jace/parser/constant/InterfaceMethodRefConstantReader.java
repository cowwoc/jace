package jace.parser.constant;

import jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InterfaceMethodRefConstantReader implements ConstantReader {

  final static short TAG = 11;

  public int getTag() {
    return TAG;
  }

  public Constant readConstant(InputStream is, ConstantPool pool) throws ClassFormatError {

    int classIndex;
    int nameAndTypeIndex;

    try {
      classIndex = new DataInputStream(is).readUnsignedShort();
      nameAndTypeIndex = new DataInputStream(is).readUnsignedShort();
    }
    catch (IOException e) {
      ClassFormatError exception = new ClassFormatError("Unable to read the MethodRef Constant");
      exception.initCause(e);
      throw exception;
    }

    return new InterfaceMethodRefConstant(classIndex, nameAndTypeIndex);
  }
}

