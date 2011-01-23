package org.jace.parser;

import java.util.Collection;

/**
 * Represents a collection of ClassAccessFlags.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassAccessFlagSet
{
	/**
	 * Represents the value for this set.
	 * This value is a bitwise or (|) of all the ClassAccessFlag values
	 * contained in this set.
	 */
	private int value;

	/**
	 * Creates a new ClassAccessFlagSet that has the given value.
	 *
	 * @param value the set value
	 */
	public ClassAccessFlagSet(int value)
	{
		this.value = value;
	}

	/**
	 * Returns true if the flag is contained in this set.
	 *
	 * @param flag the flag to test for
	 * @return true if the flag is contained in this set
	 */
	public boolean contains(ClassAccessFlag flag)
	{
		return (value & flag.getValue()) == flag.getValue();
	}

	/**
	 * Returns the string used to represent the flags in a Java source file.
	 * For example, "private synchronized native".
	 *
	 * @return the string used to represent the flags in a Java source file
	 */
	public String getName()
	{
		Collection<ClassAccessFlag> flags = ClassAccessFlag.getFlags();
		// Assume 16 characters per flag name
		StringBuilder result = new StringBuilder(flags.size() * 16);

		for (ClassAccessFlag flag: flags)
		{
			if (contains(flag))
			{
				result.append(flag.getName());
				result.append(" ");
			}
		}

		// remove the extra space we added at the end
		if (result.length() > 0)
			result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	/**
	 * Returns the value used to represent the ClassAccessFlagSet in a Java class file.
	 *
	 * @return the value used to represent the ClassAccessFlagSet in a Java class file
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * Returns the same value as <code> getName </code>.
	 */
	@Override
	public String toString()
	{
		return getName();
	}

	public void add(ClassAccessFlag flag)
	{
		value |= flag.getValue();
	}

	public void remove(ClassAccessFlag flag)
	{
		value &= ~flag.getValue();
	}

	/**
	 * Sets the value of this set. This value must be a legal combination
	 * of ClassAccessFlags.
	 *
	 * @param value the value of this set
	 */
	protected void setValue(int value)
	{
		this.value = value;
	}

	/**
	 * Tests ClassAccessFlagSet.
	 *
	 * @param args the command-line argument
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: ClassAccessFlagSet [access-flag value]");
			return;
		}

		System.out.println(new ClassAccessFlagSet(Integer.parseInt(args[ 0])));
	}
}
