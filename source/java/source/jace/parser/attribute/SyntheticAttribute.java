package jace.parser.attribute;

import jace.parser.ConstantPool;
import jace.parser.constant.Constant;
import jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A SyntheticAttribute represents the fact that its target does not appear
 * in the original source code.
 *
 * SyntheticAttributes are used to support inner classes.
 *
 * @author Toby Reyelts
 *
 */
public class SyntheticAttribute implements Attribute {

  /* From the JVM specification.
   *
   * (u1 represents an unsigned byte)
   * (u2 represents an unsigned short)
   * (u4 represents an unsigned int)
   *
   * attribute_info {
   *   u2 attribute_name_index;  // This must be "Synthetic"
   *   u4 attribute_length;      // This must be 0
   * }
   */
  private final static String NAME = "Synthetic";

  /**
   * Creates a new SyntheticAttribute, which automatically adds itself to
   * the constant pool.
   *
   */
  public SyntheticAttribute(ConstantPool pool) {
    this.mLength = 0;
    this.mPool = pool;
    this.mNameIndex = pool.addUTF8(NAME);
  }

  /**
   * Creates a new SyntheticAttribute.
   *
   */
  public SyntheticAttribute(InputStream stream, int nameIndex, ConstantPool pool) throws IOException {

    mPool = pool;
    mNameIndex = nameIndex;

    /* Read the name for this constant.
     * From the VM spec, we know it must be equal to "Synthetic".
     */
    Constant c = mPool.getConstantAt(mNameIndex);

    if (c instanceof UTF8Constant) {

      String name = c.getValue().toString();

      if (!name.equals(NAME)) {
        throw new ClassFormatError("While reading a " + getClass().getName() + ", the name, " + NAME +
          ", was expected, " +
          "but the name " + name + " was encountered.");
      }
    } else {
      throw new ClassFormatError("While reading a SyntheticAttribute, a UTF8Constant was expected, " +
        "but a constant of type " + c.getClass().getName() + " was encountered.");
    }

    DataInputStream input = new DataInputStream(stream);

    /* Read the length of the attribute.
     * From the VM spec, we know that length must = 0.
     */
    mLength = input.readInt();

    if (mLength != 0) {
      throw new ClassFormatError("While reading a SyntheticAttribute, an attribute length of size 0 was expected, " +
        "but an attribute length of size " + mLength + " was encountered.");
    }
  }

  public void write(DataOutputStream output) throws IOException {
    output.writeShort(mNameIndex);
    output.writeInt(mLength);
  }

  /**
   * Returns the name for this Attribute.
   *
   * @return the name for this Attribute
   */
  public String getName() {
    return mPool.getConstantAt(mNameIndex).toString();
  }

  @Override
  public String toString() {
    return getClass().getName();
  }

  /**
   * Returns the length of this Attribute.
   *
   * @return the length of this Attribute
   */
  public int getLength() {
    return mLength;
  }
  private int mNameIndex;
  private int mLength;
  private ConstantPool mPool;
}
