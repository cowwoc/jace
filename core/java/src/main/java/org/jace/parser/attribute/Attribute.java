package org.jace.parser.attribute;

import java.io.*;

/**
 * Represents an attribute for a class, a field, a method, or code.
 *
 * @author Toby Reyelts
 *
 */
public interface Attribute
{
	/**
	 * Returns the name of this attribute. For example, "ConstantValue".
	 *
	 * @return the attribute name
	 */
	public String getName();

	/**
	 * Writes the Attribute out in class file format.
	 *
	 * @param output the stream to write to
	 * @throws IOException if an I/O error occurs while writing to the stream
	 */
	public void write(DataOutputStream output) throws IOException;
}
