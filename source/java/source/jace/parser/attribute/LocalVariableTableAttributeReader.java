
package jace.parser.attribute;

import jace.parser.*;
import java.io.*;

/**
 * Reads LocalVariableTableAttributes.
 *
 * @author Toby Reyelts
 *
 */
public class LocalVariableTableAttributeReader implements AttributeReader {

  /**
   * Reads a LocalVariableTableAttribute from a class file InputStream.
   *
   */
  public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException {
    return new LocalVariableTableAttribute( input, nameIndex, pool );
  }

  public String getName() {
    return "LocalVariableTable";
  }
}

