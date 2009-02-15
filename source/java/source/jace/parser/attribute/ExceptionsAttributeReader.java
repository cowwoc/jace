
package jace.parser.attribute;

import jace.parser.*;

import java.io.*;

/**
 * Reads ExceptionsAttributes.
 *
 * @author Toby Reyelts
 *
 */
public class ExceptionsAttributeReader implements AttributeReader {

  /**
   * Reads an ExceptionsAttribute from a class file InputStream.
   *
   */
  public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException {
    return new ExceptionsAttribute( input, nameIndex, pool );
  }
  
  public String getName() {
    return "Exceptions";
  }
}
