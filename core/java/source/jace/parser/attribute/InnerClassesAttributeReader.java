
package jace.parser.attribute;

import jace.parser.ConstantPool;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads InnerClassesAttributes.
 *
 * @author Toby Reyelts
 */
public class InnerClassesAttributeReader implements AttributeReader
{
	/**
	 * Reads an InnerClassesAttribute from a class file InputStream.
	 * 
	 * @return the InnerClassesAttribute
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	@Override
	public Attribute readAttribute(InputStream input, int nameIndex, ConstantPool pool)
		throws IOException
	{
		return new InnerClassesAttribute(input, nameIndex, pool);
	}

	@Override
	public String getName()
	{
		return "InnerClasses";
	}
}
