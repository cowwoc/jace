package org.jace.parser.field;

import com.google.common.collect.Lists;
import java.util.Collection;

/**
 * Represents an access flag for a ClassField.
 *
 * @author Toby Reyelts
 */
public class FieldAccessFlag
{
	// Declared public; may be accessed from outside its package
	public static final FieldAccessFlag PUBLIC = new FieldAccessFlag("public", 0x0001);
	// Declared private; accessible only within the defining class
	public static final FieldAccessFlag PRIVATE = new FieldAccessFlag("private", 0x0002);
	// Declared protected; may be accessed within subclasses
	public static final FieldAccessFlag PROTECTED = new FieldAccessFlag("protected", 0x0004);
	// Declared static
	public static final FieldAccessFlag STATIC = new FieldAccessFlag("static", 0x0008);
	// Declared final; may not be overridden
	public static final FieldAccessFlag FINAL = new FieldAccessFlag("final", 0x0010);
	// Declared volatile; cannot be cached
	public static final FieldAccessFlag VOLATILE = new FieldAccessFlag("volatile", 0x0040);
	// Declared transient; not written or read by a persistent object manager
	public static final FieldAccessFlag TRANSIENT = new FieldAccessFlag("transient", 0x0080);
	// Declared synthetic; Not present in the source code
	public static final FieldAccessFlag SYNTHETIC = new FieldAccessFlag("synthetic", 0x1000);
	// Declared as an element of an enum
	public static final FieldAccessFlag ENUM = new FieldAccessFlag("enum", 0x4000);
	private final String name;
	private final int value;

	/**
	 * Creates a new FieldAccessFlag with the given name and value.
	 *
	 * @param name the flag name
	 * @param value the flag value
	 */
	protected FieldAccessFlag(String name, int value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * Returns all possible FieldAccessFlags.
	 *
	 * @return all possible FieldAccessFlags
	 */
	public static Collection<FieldAccessFlag> getFlags()
	{
		return Lists.newArrayList(PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, VOLATILE, TRANSIENT, ENUM,
			SYNTHETIC);
	}

	/**
	 * Returns the name used to represent the flag in Java source code.
	 * For example, "public", "protected", etc ...
	 *
	 * @return the flag name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the value used to represent the flag in a Java class file.
	 *
	 * @return the flag value
	 */
	public int getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof FieldAccessFlag))
			return false;
		FieldAccessFlag other = (FieldAccessFlag) o;
		return name.equals(other.getName()) && value == other.getValue();
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 89 * hash + this.name.hashCode();
		hash = 89 * hash + this.value;
		return hash;
	}
}
