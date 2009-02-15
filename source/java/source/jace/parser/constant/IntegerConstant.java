
package jace.parser.constant;

import java.io.*;

public class IntegerConstant implements Constant {

  int mBytes;

  public IntegerConstant( int bytes ) {
    mBytes = bytes;
  }

  public int getSize() {
    return 1;
  }

  public Object getValue() {
    return new Integer( mBytes );
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( IntegerConstantReader.TAG );
    output.writeInt( mBytes );
  }

}
