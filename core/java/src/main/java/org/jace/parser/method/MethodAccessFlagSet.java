package org.jace.parser.method;

import java.util.Collection;

/**
 * Represents a collection of MethodAccessFlags.
 *
 * This class represents an immutable form of a MethodAccessFlagSet.
 * Use MutableMethodAccessFlagSet to create a set of flags that may be changed.
 *
 * @see MutableMethodAccessFlagSet
 *
 * @author Toby Reyelts
 */
public class MethodAccessFlagSet
{
	/**
	 * Represents the value for this set.
	 * This value is a bitwise or (|) of all the MethodAccessFlag values
	 * contained in this set.
	 */
	private int value;

	/**
	 * Creates a new MethodAccessFlagSet that has the given value.
	 *
	 * @param value the set value
	 */
	public MethodAccessFlagSet(int value)
	{
		this.value = value;
	}

	/**
	 * Returns true if the flag is contained in this set.
	 *
	 * @param flag the flag to check for
	 * @return true if the flag is contained in this set
	 */
	public boolean contains(MethodAccessFlag flag)
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
		Collection<MethodAccessFlag> flags = MethodAccessFlag.getFlags();
		// Assume 16 characters per flag name
		StringBuilder result = new StringBuilder(flags.size() * 16);

		for (MethodAccessFlag flag: flags)
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

	public void add(MethodAccessFlag flag)
	{
		value |= flag.getValue();
	}

	public void remove(MethodAccessFlag flag)
	{
		value &= ~flag.getValue();
	}

	/**
	 * Returns the value used to represent the MethodAccessFlagSet in a Java class file.
	 *
	 * @return the value used to represent the MethodAccessFlagSet in a Java class file
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * Sets the value of this set. This value must be a legal combination
	 * of MethodAccessFlags.
	 *
	 * @param value the set value
	 */
	protected void setValue(int value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * Tests MethodAccessFlagSet.
	 *
	 * @param args the command-line argument
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args)
	{
		System.out.println(new MethodAccessFlagSet(Integer.parseInt(args[ 0])).getName());
	}
}
