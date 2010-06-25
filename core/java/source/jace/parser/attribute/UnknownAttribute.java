
package jace.parser.attribute;

import jace.parser.*;
import java.io.*;

/**
 * An UnknownAttribute represents an Attribute type that isn't recognized
 * because it hasn't been specified in the JVM specification and is a custom
 * attribute. (JACE can be extended to recognize and provide special support
 * for custom Attributes).
 *
 * @author Toby Reyelts
 *
 */
public class UnknownAttribute implements Attribute {

  /* From the JVM specification.
   *
   * (u1 represents an unsigned byte)
   * (u2 represents an unsigned short)
   * (u4 represents an unsigned int)
   *
   * attribute_info {
   *   u2 attribute_name_index;
   *   u4 attribute_length;
   *   u1 info[attribute_length];
   * }
   */
  
  /**
   * Creates a new UnknownAttribute.
   *
   */
  public UnknownAttribute( InputStream stream, int nameIndex, ConstantPool pool ) throws IOException {
    mPool = pool;
    mNameIndex = nameIndex;
    DataInputStream input = new DataInputStream( stream );
    mLength = input.readInt();
    mInfo = new byte[ mLength ];
    input.readFully( mInfo );
  }
  
  public void write( DataOutputStream output ) throws IOException {
    output.writeShort( mNameIndex );
    output.writeInt( mLength );
    output.write( mInfo );
  }

  /**
   * Returns the name for this Attribute.
   *
   */
  public String getName() {
    return mPool.getConstantAt( mNameIndex ).toString();
  }
  
  /**
   * Returns the length of this Attribute.
   *
   */
  public int getLength() {
    return mLength;
  }
  
  /**
   * Returns the data for this Attribute.
   *
   */
  public byte[] getData() {
    byte[] data = new byte[ mInfo.length ];
    System.arraycopy( mInfo, 0, data, 0, mInfo.length );
    return data;
  }
  
  public String toString() {
    return getName();
  }

  private int mNameIndex;
  private int mLength;
  private byte[] mInfo;
  private ConstantPool mPool;
}
