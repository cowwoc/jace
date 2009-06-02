package jace.parser.method;

import java.util.ArrayList;

/**
 * Represents a collection of MethodAccessFlags.
 *
 * This class represents an immutable form of a MethodAccessFlagSet.
 * Use MutableMethodAccessFlagSet to create a set of flags that may be changed.
 *
 * @see MutableMethodAccessFlagSet
 *
 * @author Toby Reyelts
 *
 */
public class MethodAccessFlagSet
{
  /** Represents the value for this set.
   * This value is a bitwise or (|) of all the MethodAccessFlag values
   * contained in this set.
   */
  private int mValue;

  /**
   * Creates a new MethodAccessFlagSet that has the given value.
   *
   * @param value the set value
   */
  public MethodAccessFlagSet(int value)
  {
    mValue = value;
  }

  /**
   * Returns true if the flag is contained in this set.
   *
   * @param flag the flag to check for
   * @return true if the flag is contained in this set
   */
  public boolean contains(MethodAccessFlag flag)
  {
    return ((mValue & flag.getValue()) == flag.getValue());
  }

  /**
   * Returns the string used to represent the flags in a Java source file.
   * For example, "private synchronized native".
   *
   * @return the string used to represent the flags in a Java source file
   */
  public String getName()
  {
    StringBuilder name = new StringBuilder();

    ArrayList<MethodAccessFlag> flags = new ArrayList<MethodAccessFlag>(MethodAccessFlag.getFlags());

    for (int i = 0; i < flags.size(); ++i)
    {
      MethodAccessFlag flag = flags.get(i);
      if (this.contains(flag))
      {
        name.append(flag.getName() + " ");
      }
    }

    // remove the extra space we added at the end
    if (name.length() > 0)
    {
      name.deleteCharAt(name.length() - 1);
    }

    return name.toString();
  }

  public void add(MethodAccessFlag flag)
  {
    mValue |= flag.getValue();
  }

  public void remove(MethodAccessFlag flag)
  {
    mValue &= ~flag.getValue();
  }

  /**
   * Returns the value used to represent the MethodAccessFlagSet in a Java class file.
   *
   * @return the value used to represent the MethodAccessFlagSet in a Java class file
   */
  public int getValue()
  {
    return mValue;
  }

  /**
   * Sets the value of this set. This value must be a legal combination 
   * of MethodAccessFlags.
   *
   * @param value the set value
   */
  protected void setValue(int value)
  {
    mValue = value;
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
  public static void main(String[] args)
  {
    System.out.println(new MethodAccessFlagSet(Integer.parseInt(args[ 0])).getName());
  }
}
