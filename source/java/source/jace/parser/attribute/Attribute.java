package jace.parser.attribute;

import java.io.*;

/**
 * Represents an attribute for a class, a field, a method, or code.
 *
 * @author Toby Reyelts
 *
 */
public interface Attribute {

  /**
   * Returns the name of this Attribute. For example, "ConstantValue".
   *
   */
  public String getName();

  /**
   * Writes the Attribute out in class file format.
   *
   */
  public void write( DataOutputStream output ) throws IOException;
}