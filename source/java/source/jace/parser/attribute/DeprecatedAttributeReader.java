
package jace.parser.attribute;

import jace.parser.*;

import java.io.*;

/**
 * Reads DeprecatedAttributes.
 *
 * @author Toby Reyelts
 *
 */
public class DeprecatedAttributeReader implements AttributeReader {

  /**
   * Reads a DeprecatedAttribute from a class file InputStream.
   *
   */
  public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException {
    return new DeprecatedAttribute( input, nameIndex, pool );
  }

  public String getName() {
    return "Deprecated";
  }
}
