package org.jace.parser.field;

import org.jace.metaclass.TypeName;
import org.jace.metaclass.TypeNameFactory;
import org.jace.parser.ConstantPool;
import org.jace.parser.attribute.Attribute;
import org.jace.parser.attribute.AttributeFactory;
import org.jace.parser.attribute.ConstantValueAttribute;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a class or instance field.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassField
{
	/* From the JVM specification:
	 *
	 * (u2 represents an unsigned short)
	 *
	 * field_info {
	 *   u2 access_flags;
	 *   u2 name_index;
	 *   u2 descriptor_index;
	 *   u2 attributes_count;
	 *   attribute_info attributes[attributes_count];
	 * }
	 */
	private final Logger log = LoggerFactory.getLogger(ClassField.class);
	/**
	 * The field modifiers.
	 */
	private int accessFlags;
	/**
	 * The name of this field.
	 * A CONSTANT_Utf8_info in the constant pool
	 */
	private int nameIndex;
	/**
	 * The type of this field.
	 * A CONSTANT_Utf8_info in the constant pool
	 */
	private int descriptorIndex;
	/**
	 * The attributes for this ClassField.
	 * These attributes may only be ConstantValue, Synthetic, or Deprecated.
	 */
	private final List<Attribute> attributes;
	/**
	 * The ConstantPool for this ClassField.
	 */
	private final ConstantPool pool;

	/**
	 * Reads a field from the given InputStream.
	 *
	 * @param stream
	 * @param pool
	 *
	 * @throws IOException if a reading error occurs
	 */
	public ClassField(InputStream stream, ConstantPool pool) throws IOException
	{
		this.pool = pool;

		DataInputStream input = new DataInputStream(stream);

		accessFlags = input.readUnsignedShort();
		nameIndex = input.readUnsignedShort();
		descriptorIndex = input.readUnsignedShort();
		int attributesCount = input.readUnsignedShort();

		attributes = new ArrayList<Attribute>(attributesCount);

		AttributeFactory factory = new AttributeFactory();

		for (int i = 0; i < attributesCount; ++i)
			attributes.add(factory.readAttribute(stream, pool));

		if (log.isDebugEnabled())
		{
			log.debug("accessFlags: " + accessFlags);
			log.debug("nameIndex: " + nameIndex);
			log.debug("descriptorIndex: " + descriptorIndex);
			log.debug("attributesCount: " + attributesCount);
		}
	}

	public void write(DataOutputStream output) throws IOException
	{
		output.writeShort(accessFlags);
		output.writeShort(nameIndex);
		output.writeShort(descriptorIndex);
		output.writeShort(attributes.size());

		for (Attribute a: attributes)
			a.write(output);
	}

	/**
	 * Returns the FieldAccessFlagSet for this ClassField.
	 *
	 * @return the FieldAccessFlagSet for this ClassField
	 */
	public FieldAccessFlagSet getAccessFlags()
	{
		return new FieldAccessFlagSet(accessFlags);
	}

	public void setAccessFlags(FieldAccessFlagSet set)
	{
		accessFlags = set.getValue();
	}

	public int getNameIndex()
	{
		return nameIndex;
	}

	public void setNameIndex(int index)
	{
		nameIndex = index;
	}

	public int getDescriptorIndex()
	{
		return descriptorIndex;
	}

	public void setDescriptorIndex(int index)
	{
		descriptorIndex = index;
	}

	/**
	 * Returns the name of this ClassField.
	 *
	 * For example, "aVariable".
	 *
	 * @return the name of this ClassField
	 */
	public String getName()
	{
		Constant c = pool.getConstantAt(nameIndex);

		if (c instanceof UTF8Constant)
			return c.getValue().toString();
		throw new RuntimeException("Not a UTF8Constant: " + c.getClass().getName());
	}

	/**
	 * Returns the descriptor for this ClassField.
	 *
	 * For example, "Ljava/lang/String;".
	 *
	 * @return the field type
	 */
	public TypeName getDescriptor()
	{
		Constant c = pool.getConstantAt(descriptorIndex);
		if (c instanceof UTF8Constant)
			return TypeNameFactory.fromDescriptor(c.getValue().toString());
		throw new RuntimeException("Not a UTF8Constant: " + c.getClass().getName());
	}

	/**
	 * Returns the ConstantValueAttribute for this ClassField.
	 *
	 * Only ClassFields which are flagged as FieldAccessFlag.STATIC
	 * have a ConstantValueAttribute. This method returns null if it is
	 * invoked on a ClassField  which isn't flagged as FieldAccessFlag.STATIC.
	 *
	 * @return the ConstantValueAttribute for this ClassField
	 */
	public ConstantValueAttribute getConstant()
	{
		for (Attribute a: attributes)
		{
			if (a instanceof ConstantValueAttribute)
				return (ConstantValueAttribute) a;
		}
		return null;
	}

	/**
	 * Returns all of the Attributes for this ClassField.
	 *
	 * @return all of the Attributes for this ClassField
	 */
	public Collection<Attribute> getAttributes()
	{
		return Collections.unmodifiableList(attributes);
	}

	public void addAttribute(Attribute attr)
	{
		attributes.add(attr);
	}

	@Override
	public String toString()
	{
		return getDescriptor() + " " + getName();
	}
}
