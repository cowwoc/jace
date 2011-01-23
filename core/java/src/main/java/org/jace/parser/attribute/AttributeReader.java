package org.jace.parser.attribute;

import java.io.IOException;
import java.io.InputStream;
import org.jace.parser.ConstantPool;

/**
 * An AttributeReader reads an Attribute from a Java class file.
 *
 * @author Toby Reyelts
 */
public interface AttributeReader
{
	/**
	 * Reads an Attribute from the InpuStream.
	 *
	 * @param input The InputStream must be pointing to a ClassFile. Specifically the
	 * InputStream must be position directly after the Attribute's name index.
	 *
	 * @param nameIndex The constant pool index of the name for the attribute.
	 *
	 * @param pool The constant pool for this Attribute.
	 * @return the attribute
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	public Attribute readAttribute(InputStream input, int nameIndex, ConstantPool pool)
		throws IOException;

	/**
	 * Returns the attribute name. For example, "ConstantValue".
	 *
	 * @return the attribute name
	 */
	public String getName();
}
