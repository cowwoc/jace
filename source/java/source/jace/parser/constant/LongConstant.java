
package jace.parser.constant;

import jace.parser.*;
import java.io.*;

public class LongConstant implements Constant {

  int mHighByte;
  int mLowByte;

  public LongConstant( int highByte, int lowByte ) {
    mHighByte = highByte;
    mLowByte = lowByte;
  }

  public Object getValue() {
    return "LongConstant.getValue() has not yet been implemented.";
  }

  public int getSize() {
    return 2;
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( LongConstantReader.TAG );
    output.writeInt( mHighByte );
    output.writeInt( mLowByte );
  }
}
