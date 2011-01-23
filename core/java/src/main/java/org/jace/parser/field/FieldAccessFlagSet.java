package org.jace.parser.field;

import java.util.Collection;

/**
 * Represents a collection of FieldAccessFlags.
 *
 * This class represents an immutable form of a FieldAccessFlagSet.
 * Use MutableFieldAccessFlagSet to create a set of flags that may be changed.
 *
 * @see MutableFieldAccessFlagSet
 *
 * @author Toby Reyelts
 *
 */
public class FieldAccessFlagSet
{
	/**
	 * Represents the value for this set.
	 * This value is a bitwise or (|) of all the FieldAccessFlag values
	 * contained in this set.
	 */
	private int value;

	/**
	 * Creates a new FieldAccessFlagSet that has the given value.
	 *
	 * @param value the set value
	 */
	public FieldAccessFlagSet(int value)
	{
		this.value = value;
	}

	/**
	 * Returns true if the flag is contained in this set.
	 *
	 * @param flag the flag to check for
	 * @return true if the flag is contained in this set
	 */
	public boolean contains(FieldAccessFlag flag)
	{
		return ((value & flag.getValue()) == flag.getValue());
	}

	/**
	 * Returns the string used to represent the flags in a Java source file.
	 * For example, "private synchronized native".
	 *
	 * @return the string used to represent the flags in a Java source file
	 */
	public String getName()
	{
		Collection<FieldAccessFlag> flags = FieldAccessFlag.getFlags();
		// Assume 16 characters per flag name
		StringBuilder result = new StringBuilder(flags.size() * 16);

		for (FieldAccessFlag flag: flags)
		{
			if (contains(flag))
			{
				result.append(flag.getName());
				result.append(" ");
			}
		}

		// Remove the extra space we added at the end
		if (result.length() > 0)
			result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	public void add(FieldAccessFlag flag)
	{
		value |= flag.getValue();
	}

	public void remove(FieldAccessFlag flag)
	{
		value &= ~flag.getValue();
	}

	/**
	 * Returns the value used to represent the FieldAccessFlagSet in a Java class file.
	 *
	 * @return the value used to represent the FieldAccessFlagSet in a Java class file
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * Sets the value of this set. This value must be a legal combination
	 * of FieldAccessFlags.
	 *
	 * @param value the set value
	 */
	protected void setValue(int value)
	{
		this.value = value;
	}

	/**
	 * Tests FieldAccessFlagSet.
	 *
	 * @param args the command-line argument
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args)
	{
		System.out.println(new FieldAccessFlagSet(Integer.parseInt(args[ 0])).getName());
	}
}
