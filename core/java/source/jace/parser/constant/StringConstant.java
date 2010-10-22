package jace.parser.constant;

import jace.parser.ConstantPool;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringConstant implements Constant
{
  int index;
  ConstantPool mPool;

  public StringConstant(ConstantPool pool, int index)
  {
    mPool = pool;
    this.index = index;
  }

  public int getSize()
  {
    return 1;
  }

  public Object getValue()
  {
    UTF8Constant c = (UTF8Constant) mPool.getConstantAt(index);
    return c.getValue();
  }

  public String toString()
  {
    return "A StringConstant pointing to a UTF8Constant at index " + index;
  }

  public void write(DataOutputStream output) throws IOException
  {
    output.writeByte(StringConstantReader.TAG);
    output.writeShort(index);
  }
}
