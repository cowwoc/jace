
package jace.parser.attribute;

import jace.parser.*;
import java.io.*;

/**
 * Reads SignatureAttributes.
 *
 * (A Signature attribute contains new JDK1.5 generics type information).
 *
 * @author Toby Reyelts
 *
 */
public class SignatureAttributeReader implements AttributeReader {

  /**
   * Reads a SignatureAttribute from a class file InputStream.
   *
   */
  public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException {
    return new SignatureAttribute( input, nameIndex, pool );
  }

  public String getName() {
    return "Signature";
  }
}

