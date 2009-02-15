package jace.parser.attribute;

import jace.parser.*;
import java.io.*;

/**
 * Reads CodeAttributes.
 *
 * @author Toby Reyelts
 *
 */
public class CodeAttributeReader implements AttributeReader {

  /**
   * Reads a CodeAttribute from a class file InputStream.
   *
   */
  public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException {
    return new CodeAttribute( input, nameIndex, pool );
  }

  public String getName() {
    return "Code";
  }
}