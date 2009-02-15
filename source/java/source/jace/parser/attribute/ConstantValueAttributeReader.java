
package jace.parser.attribute;

import jace.parser.*;

import java.io.*;

/**
 * Reads ConstantAttributes.
 *
 * @author Toby Reyelts
 *
 */
public class ConstantValueAttributeReader implements AttributeReader {

  /**
   * Reads a ConstantAttribute from a class file InputStream.
   *
   */
  public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException {
    return new ConstantValueAttribute( input, nameIndex, pool );
  }

  public String getName() {
    return "ConstantValue";
  }
}
