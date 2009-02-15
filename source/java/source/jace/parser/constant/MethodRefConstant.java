
package jace.parser.constant;

import java.io.*;

public class MethodRefConstant implements TypedConstant {

  int mClassIndex;
  int mNameAndTypeIndex;

  public MethodRefConstant( int classIndex, int nameAndTypeIndex ) {
    mClassIndex = classIndex;
    mNameAndTypeIndex = nameAndTypeIndex;
  }

  public int getNameAndTypeIndex() {
    return mNameAndTypeIndex;
  }

  public int getClassIndex() {
    return mClassIndex;
  }
  
  public Object getValue() {
    return "MethodRefConstant.getValue() has not yet been implemented.";
  }

  public int getSize() {
    return 1;
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( MethodRefConstantReader.TAG );
    output.writeShort( mClassIndex );
    output.writeShort( mNameAndTypeIndex );
  }

}
