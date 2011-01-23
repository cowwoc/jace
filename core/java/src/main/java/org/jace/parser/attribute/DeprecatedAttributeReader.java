package org.jace.parser.attribute;

import java.io.IOException;
import java.io.InputStream;
import org.jace.parser.ConstantPool;

/**
 * Reads DeprecatedAttributes.
 *
 * @author Toby Reyelts
 */
public class DeprecatedAttributeReader implements AttributeReader
{
	/**
	 * Reads a DeprecatedAttribute from a class file InputStream.
	 *
	 * @return the DeprecatedAttribute
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	@Override
	public Attribute readAttribute(InputStream input, int nameIndex, ConstantPool pool)
		throws IOException
	{
		return new DeprecatedAttribute(input, nameIndex, pool);
	}

	@Override
	public String getName()
	{
		return "Deprecated";
	}
}
