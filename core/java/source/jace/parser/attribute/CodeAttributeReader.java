package jace.parser.attribute;

import jace.parser.ConstantPool;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads CodeAttributes.
 *
 * @author Toby Reyelts
 */
public class CodeAttributeReader implements AttributeReader
{
	/**
	 * Reads a CodeAttribute from a class file InputStream.
	 *
	 * @return the CodeAttribute
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	@Override
	public Attribute readAttribute(InputStream input, int nameIndex, ConstantPool pool)
		throws IOException
	{
		return new CodeAttribute(input, nameIndex, pool);
	}

	@Override
	public String getName()
	{
		return "Code";
	}
}
