
package jace.parser.constant;

import java.io.*;

public class InterfaceMethodRefConstant implements TypedConstant {

  int mClassIndex;
  int mNameAndTypeIndex;

  public InterfaceMethodRefConstant( int classIndex, int nameAndTypeIndex ) {
    mClassIndex = classIndex;
    mNameAndTypeIndex = nameAndTypeIndex;
  }

  public int getClassIndex() {
    return mClassIndex;
  }

  public int getNameAndTypeIndex() {
    return mNameAndTypeIndex;
  }
  
  public Object getValue() {
    return "Not yet implemented.";
  }

  public int getSize() {
    return 1;
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( InterfaceMethodRefConstantReader.TAG );
    output.writeShort( mClassIndex );
    output.writeShort( mNameAndTypeIndex );
  }

}
