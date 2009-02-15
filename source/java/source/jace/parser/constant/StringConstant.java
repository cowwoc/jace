
package jace.parser.constant;

import jace.parser.*;
import java.io.*;

public class StringConstant implements Constant {

  int mIndex;
  ConstantPool mPool;

  public StringConstant( ConstantPool pool, int index ) {
    mPool = pool;
    mIndex = index;
  }

  public int getSize() {
    return 1;
  }

  public Object getValue() {
    UTF8Constant c = ( UTF8Constant ) mPool.getConstantAt( mIndex );
    return c.getValue();
  }

  public String toString() {
    return "A StringConstant pointing to a UTF8Constant at index " + mIndex;
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( StringConstantReader.TAG );
    output.writeShort( mIndex );
  }
}
