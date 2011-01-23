package org.jace.parser.attribute;

import java.io.IOException;
import java.io.InputStream;
import org.jace.parser.ConstantPool;

/**
 * Reads SignatureAttributes.
 *
 * (A Signature attribute contains new JDK1.5 generics type information).
 *
 * @author Toby Reyelts
 */
public class SignatureAttributeReader implements AttributeReader
{
	/**
	 * Reads a SignatureAttribute from a class file InputStream.
	 *
	 * @return the SignatureAttribute
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	@Override
	public Attribute readAttribute(InputStream input, int nameIndex, ConstantPool pool)
		throws IOException
	{
		return new SignatureAttribute(input, nameIndex, pool);
	}

	@Override
	public String getName()
	{
		return "Signature";
	}
}
