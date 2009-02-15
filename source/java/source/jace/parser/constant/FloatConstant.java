
package jace.parser.constant;

import java.io.*;

public class FloatConstant implements Constant {

  int mBytes;

  public FloatConstant( int bytes ) {
    mBytes = bytes;
  }

  public int getSize() {
    return 1;
  }

  public Object getValue() {
    return new Integer( mBytes );
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( FloatConstantReader.TAG );
    output.writeInt( mBytes );
  }
}
