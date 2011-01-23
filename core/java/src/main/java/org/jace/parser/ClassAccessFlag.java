package org.jace.parser;

import com.google.common.collect.Lists;
import java.util.Collection;

/**
 * Represents an access flag for a Class.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassAccessFlag
{
	// Declared public; may be accessed from outside its package
	public static final ClassAccessFlag PUBLIC = new ClassAccessFlag("public", 0x0001);
	// Declared final; no subclasses allowed
	public static final ClassAccessFlag FINAL = new ClassAccessFlag("final", 0x0010);
	// Treat superclass methods specially when invoked by the invokespecial instruction
	public static final ClassAccessFlag SUPER = new ClassAccessFlag("super", 0x0020);
	// Is an interface, not a class
	public static final ClassAccessFlag INTERFACE = new ClassAccessFlag("interface", 0x0200);
	// Declared abstract; must not be instantiated
	public static final ClassAccessFlag ABSTRACT = new ClassAccessFlag("abstract", 0x0400);
	// Declared synthetic; Not present in the source code
	public static final ClassAccessFlag SYNTHETIC = new ClassAccessFlag("synthetic", 0x1000);
	// Declared as an annotation type
	public static final ClassAccessFlag ANNOTATION = new ClassAccessFlag("annotation", 0x2000);
	// Declared as an enum type
	public static final ClassAccessFlag ENUM = new ClassAccessFlag("enum", 0x4000);
	private final int value;
	private final String name;

	/**
	 * Creates a new ClassAccessFlag with the given name and value.
	 *
	 * @param name the flag name
	 * @param value the flag value
	 */
	protected ClassAccessFlag(String name, int value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * Returns all possible ClassAccessFlags.
	 *
	 * @return all possible ClassAccessFlags
	 */
	public static Collection<ClassAccessFlag> getFlags()
	{
		return Lists.newArrayList(PUBLIC, FINAL, SUPER, INTERFACE, ABSTRACT, ANNOTATION, SYNTHETIC);
	}

	/**
	 * Returns the name used to represent the flag in Java source-code.
	 * For example, "public", "protected", etc ...
	 *
	 * @return the name used to represent the flag in the Java source-code.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the value used to represent the flag in a Java class file.
	 *
	 * @return the value used to represent the flag in a Java class file
	 */
	public int getValue()
	{
		return value;
	}

	@Override
	@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
	public boolean equals(Object o)
	{
		if (!(o instanceof ClassAccessFlag))
			return false;
		ClassAccessFlag other = (ClassAccessFlag) o;
		return name.equals(other.name) && value == other.value;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 37 * hash + this.value;
		hash = 37 * hash + this.name.hashCode();
		return hash;
	}
}
