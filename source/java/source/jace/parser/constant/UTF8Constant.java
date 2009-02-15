
package jace.parser.constant;

import java.io.*;

public class UTF8Constant implements Constant {

  byte[] mBytes;

  public UTF8Constant( byte[] bytes ) {
    mBytes = new byte[ bytes.length ];
    System.arraycopy( bytes, 0, mBytes, 0, bytes.length );
  }

  public int getSize() {
    return 1;
  }

  public Object getValue() {
    return new String( mBytes );
  }

  public void setValue( String str ) {
    mBytes = str.getBytes();
  }

  public String toString() {
    return ( String ) getValue();
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( UTF8ConstantReader.TAG );
    output.writeShort( mBytes.length );
    output.write( mBytes );
  }
}
