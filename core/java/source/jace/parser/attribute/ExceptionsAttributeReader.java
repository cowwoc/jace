
package jace.parser.attribute;

import jace.parser.ConstantPool;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads ExceptionsAttributes.
 *
 * @author Toby Reyelts
 */
public class ExceptionsAttributeReader implements AttributeReader
{
	/**
	 * Reads an ExceptionsAttribute from a class file InputStream.
	 *
	 * @return the ExceptionsAttribute
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	@Override
	public Attribute readAttribute(InputStream input, int nameIndex, ConstantPool pool)
		throws IOException
	{
		return new ExceptionsAttribute(input, nameIndex, pool);
	}

	@Override
	public String getName()
	{
		return "Exceptions";
	}
}
