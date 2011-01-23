package org.jace.parser.attribute;

import java.io.IOException;
import java.io.InputStream;
import org.jace.parser.ConstantPool;

/**
 * Reads ConstantAttributes.
 *
 * @author Toby Reyelts
 */
public class ConstantValueAttributeReader implements AttributeReader
{
	/**
	 * Reads a ConstantAttribute from a class file InputStream.
	 *
	 * @return the ConstantAttribute
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	@Override
	public Attribute readAttribute(InputStream input, int nameIndex, ConstantPool pool)
		throws IOException
	{
		return new ConstantValueAttribute(input, nameIndex, pool);
	}

	@Override
	public String getName()
	{
		return "ConstantValue";
	}
}
