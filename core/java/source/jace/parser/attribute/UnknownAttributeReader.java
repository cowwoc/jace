
package jace.parser.attribute;

import jace.parser.*;

import java.io.*;

/**
 * Reads UnknownAttributes.
 *
 * @author Toby Reyelts
 *
 */
public class UnknownAttributeReader implements AttributeReader {

  /**
   * Reads an UnknownAttribute from a class file InputStream.
   *
   */
  public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException {
    return new UnknownAttribute( input, nameIndex, pool );
  }

  public String getName() {
    return "UnknownAttribute";
  }
}
