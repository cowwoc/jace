package jace.parser.constant;

import jace.parser.ConstantPool;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClassConstant implements Constant {

  int mNameIndex;
  ConstantPool mPool;

  public ClassConstant(int nameIndex, ConstantPool pool) {
    mNameIndex = nameIndex;
    mPool = pool;
  }

  public int getSize() {
    return 1;
  }

  public int getNameIndex() {
    return mNameIndex;
  }

  public void setNameIndex(int index) {
    mNameIndex = index;
  }

  public Object getValue() {
    UTF8Constant c = (UTF8Constant) mPool.getConstantAt(mNameIndex);
    return c.getValue();
  }

  public void write(DataOutputStream output) throws IOException {
    output.writeByte(ClassConstantReader.TAG);
    output.writeShort(mNameIndex);
  }

  @Override
  public String toString() {
    return (String) getValue();
  }
}
