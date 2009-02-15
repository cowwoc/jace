package jace.parser.attribute;

import jace.parser.ConstantPool;
import jace.parser.constant.Constant;
import jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A DeprecatedAttribute represents the fact that its target has been deprecated and superceded.
 *
 * @author Toby Reyelts
 */
public class DeprecatedAttribute implements Attribute {

  /* From the JVM specification.
   *
   * (u1 represents an unsigned byte)
   * (u2 represents an unsigned short)
   * (u4 represents an unsigned int)
   *
   * attribute_info {
   *   u2 attribute_name_index;  // This must be "Deprecated"
   *   u4 attribute_length;      // This must be 0
   * }
   */
  /**
   * Creates a new DeprecatedAttribute.
   *
   */
  public DeprecatedAttribute(InputStream stream, int nameIndex, ConstantPool pool) throws IOException {

    mPool = pool;
    mNameIndex = nameIndex;

    /* Read the name for this constant.
     * From the VM spec, we know it must be equal to "Deprecated".
     */
    Constant c = mPool.getConstantAt(mNameIndex);

    if (c instanceof UTF8Constant) {

      String name = c.getValue().toString();

      if (!name.equals("Deprecated")) {
        throw new ClassFormatError("While reading a DeprecatedAttribute, the name, Deprecated, was expected, " +
          "but the name " + name + " was encountered.");
      }
    } else {
      throw new ClassFormatError("While reading a DeprecatedAttribute, a UTF8Constant was expected, " +
        "but a constant of type " + c.getClass().getName() + " was encountered.");
    }

    DataInputStream input = new DataInputStream(stream);

    /* Read the length of the attribute.
     * From the VM spec, we know that length must = 0.
     */
    mLength = input.readInt();

    if (mLength != 0) {
      throw new ClassFormatError("While reading a DeprecatedAttribute, an attribute length of size 0 was expected, " +
        "but an attribute length of size " + mLength + " was encountered.");
    }
  }

  /**
   * Returns the name for this Attribute.
   *
   */
  public String getName() {
    return mPool.getConstantAt(mNameIndex).toString();
  }

  /**
   * Returns the length of this Attribute.
   *
   */
  public int getLength() {
    return 0;
  }

  public void write(DataOutputStream output) throws IOException {
    output.writeShort(mNameIndex);
    output.writeInt(mLength);
  }

  public String toString() {
    return getClass().getName();
  }
  private int mNameIndex;
  private int mLength;
  private ConstantPool mPool;
}
