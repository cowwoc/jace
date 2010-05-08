
package jace.parser.constant;

import jace.parser.*;
import java.io.*;

public class NameAndTypeConstant implements Constant {

  int mNameIndex;
  int mDescriptorIndex;

  public NameAndTypeConstant( int nameIndex, int descriptorIndex ) {
    mNameIndex = nameIndex;
    mDescriptorIndex = descriptorIndex;
  }

  public int getNameIndex() {
    return mNameIndex;
  }

  public void setNameIndex( int index ) {
    mNameIndex = index;
  }

  public int getDescriptorIndex() {
    return mDescriptorIndex;
  }

  public void setDescriptorIndex( int index ) {
    mDescriptorIndex = index;
  }

  public Object getValue() {
    return "NameAndTypeConstant.getValue() has not yet been implemented.";
  }

  public int getSize() {
    return 1;
  }

  public void write( DataOutputStream output ) throws IOException {
    output.writeByte( NameAndTypeConstantReader.TAG );
    output.writeShort( mNameIndex );
    output.writeShort( mDescriptorIndex );
  }

}
