package org.jace.parser.attribute;

import java.io.IOException;
import java.io.InputStream;
import org.jace.parser.ConstantPool;

/**
 * Reads UnknownAttributes.
 *
 * @author Toby Reyelts
 */
public class UnknownAttributeReader implements AttributeReader
{
	/**
	 * Reads an UnknownAttribute from a class file InputStream.
	 *
	 * @return the UnknownAttribute
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	@Override
	public Attribute readAttribute(InputStream input, int nameIndex, ConstantPool pool)
		throws IOException
	{
		return new UnknownAttribute(input, nameIndex, pool);
	}

	@Override
	public String getName()
	{
		return "UnknownAttribute";
	}
}
