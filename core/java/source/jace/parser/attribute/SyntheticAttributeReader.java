
package jace.parser.attribute;

import jace.parser.ConstantPool;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads SyntheticAttributes.
 *
 * @author Toby Reyelts
 */
public class SyntheticAttributeReader implements AttributeReader
{
	/**
	 * Reads a SyntheticAttribute from a class file InputStream.
	 *
	 * @return the SyntheticAttribute
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	@Override
	public Attribute readAttribute(InputStream input, int nameIndex, ConstantPool pool)
		throws IOException
	{
		return new SyntheticAttribute(input, nameIndex, pool);
	}

	@Override
	public String getName()
	{
		return "Synthetic";
	}
}
