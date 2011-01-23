package org.jace.parser.attribute;

import com.google.common.collect.Maps;
import org.jace.parser.ConstantPool;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * An AttributeFactory reads Attributes from a Java class file.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class AttributeFactory
{
	/**
	 * Contains a mapping of an attribute name, to an AttributeReader.
	 */
	private static final Map<String, AttributeReader> readerMap = Maps.newHashMap();
	private static final UnknownAttributeReader unknownAttributeReader = new UnknownAttributeReader();

	static
	{
		addReader(new ConstantValueAttributeReader());
		addReader(new DeprecatedAttributeReader());
		addReader(new ExceptionsAttributeReader());
		addReader(new SyntheticAttributeReader());
		addReader(new InnerClassesAttributeReader());
		addReader(new LocalVariableTableAttributeReader());
		addReader(new CodeAttributeReader());
	}

	/**
	 * Reads an attribute from the given InputStream.
	 *
	 * @param stream This InputStream must be open on a valid class file,
	 * and must be positioned directly at an Attribute.
	 *
	 * @param pool The ConstantPool for the class file to which this Attribute
	 * belongs.
	 *
	 * (See the JVM specification for more details about Attributes).
	 *
	 * @return the attribute
	 * @throws IOException if an error occurs while reading from the stream
	 */
	public Attribute readAttribute(InputStream stream, ConstantPool pool) throws IOException
	{
		DataInputStream input = new DataInputStream(stream);
		int nameIndex = input.readUnsignedShort();
		Constant c = pool.getConstantAt(nameIndex);

		if (c instanceof UTF8Constant)
		{
			String name = c.getValue().toString();
			AttributeReader reader = getReader(name);
			return reader.readAttribute(stream, nameIndex, pool);
		}
		throw new ClassFormatError("While trying to read an Attribute, a UTF8Constant was expected. Instead, the "
															 + "following constant type was encountered: "
															 + c.getClass().getName());
	}

	private static void addReader(AttributeReader reader)
	{
		readerMap.put(reader.getName(), reader);
	}

	private AttributeReader getReader(String name)
	{
		AttributeReader reader = readerMap.get(name);
		if (reader == null)
			return unknownAttributeReader;
		return reader;
	}
}
