
package jace.parser.attribute;

import jace.parser.*;

import java.io.*;

/**
 * Reads SyntheticAttributes.
 *
 * @author Toby Reyelts
 *
 */
public class SyntheticAttributeReader implements AttributeReader {

  /**
   * Reads a SyntheticAttribute from a class file InputStream.
   *
   */
  public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException {
    return new SyntheticAttribute( input, nameIndex, pool );
  }
  
  public String getName() {
    return "Synthetic";
  }
}
