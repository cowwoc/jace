
package jace.parser.attribute;

import jace.parser.*;

import java.io.*;

/**
 * Reads InnerClassesAttributes.
 *
 * @author Toby Reyelts
 *
 */
public class InnerClassesAttributeReader implements AttributeReader {

  /**
   * Reads an InnerClassesAttribute from a class file InputStream.
   *
   */
  public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException {
    return new InnerClassesAttribute( input, nameIndex, pool );
  }

  public String getName() {
    return "InnerClasses";
  }

}
