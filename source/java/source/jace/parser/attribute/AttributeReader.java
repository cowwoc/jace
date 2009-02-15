package jace.parser.attribute;

import jace.parser.*;
import java.io.*;

/**
 * An AttributeReader reads an Attribute from a Java class file.
 *
 * @author Toby Reyelts
 *
 */
public interface AttributeReader {

/**
 * Reads an Attribute from the InpuStream.
 *
 * @param input The InputStream must be pointing to a ClassFile. Specifically the
 * InputStream must be position directly after the Attribute's name index.
 *
 * @param nameIndex The constant pool index of the name for the attribute.
 *
 * @param pool The constant pool for this Attribute.
 *
 */
public Attribute readAttribute( InputStream input, int nameIndex, ConstantPool pool ) throws IOException;

/**
 * Returns the name for this Attribute.
 * For example, "ConstantValue".
 *
 */
public String getName();

}