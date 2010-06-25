
package jace.parser.constant;

import jace.parser.*;
import java.io.*;

public class DoubleConstant implements Constant {

  int mHighByte;
  int mLowByte;

  public DoubleConstant( int highByte, int lowByte ) {
    mHighByte = highByte;
    mLowByte = lowByte;
  }

  public Object getValue() {
    return new Double( ( mHighByte << 32 ) | mLowByte );
  }

  public int getSize() {
    return 2;
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( DoubleConstantReader.TAG );
    output.writeInt( mHighByte );
    output.writeInt( mLowByte );
  }
}

