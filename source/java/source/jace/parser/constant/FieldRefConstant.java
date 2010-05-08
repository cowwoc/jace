
package jace.parser.constant;

import java.io.*;

public class FieldRefConstant implements TypedConstant {

  int mClassIndex;
  int mNameAndTypeIndex;

  public FieldRefConstant( int classIndex, int nameAndTypeIndex ) {
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
    return "FieldRefConstant.getValue() has not yet been implemented.";
  }

  public int getSize() {
    return 1;
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( FieldRefConstantReader.TAG );
    output.writeShort( mClassIndex );
    output.writeShort( mNameAndTypeIndex );
  }
}
