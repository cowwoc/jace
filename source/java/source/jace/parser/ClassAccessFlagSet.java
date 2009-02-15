package jace.parser;

import java.util.ArrayList;

/**
 * Represents a collection of ClassAccessFlags.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassAccessFlagSet {

  /**
   * Represents the value for this set.
   * This value is a bitwise or (|) of all the ClassAccessFlag values
   * contained in this set.
   */
  private int mValue;

  /**
   * Creates a new ClassAccessFlagSet that has the given value.
   *
	 * @param value the set value
   */
  public ClassAccessFlagSet(int value) {
    mValue = value;
  }

  /**
   * Returns true if the flag is contained in this set.
   *
	 * @param flag the flag to test for
	 * @return true if the flag is contained in this set
   */
  public boolean contains(ClassAccessFlag flag) {
    return ((mValue & flag.getValue()) == flag.getValue());
  }

  /**
   * Returns the string used to represent the flags in a Java source file.
   * For example, "private synchronized native".
   *
	 * @return the string used to represent the flags in a Java source file
   */
  public String getName() {

    StringBuilder name = new StringBuilder();

    ArrayList<ClassAccessFlag> flags = new ArrayList<ClassAccessFlag>(ClassAccessFlag.getFlags());

    for (int i = 0; i < flags.size(); ++i) {
      ClassAccessFlag flag = flags.get(i);
      if (this.contains(flag)) {
        name.append(flag.getName() + " ");
      }
    }

    // remove the extra space we added at the end
    if (name.length() > 0) {
      name.deleteCharAt(name.length() - 1);
    }

    return name.toString();
  }

  /**
   * Returns the value used to represent the ClassAccessFlagSet in a Java class file.
   *
	 * @return the value used to represent the ClassAccessFlagSet in a Java class file
   */
  public int getValue() {
    return mValue;
  }

  /**
   * Returns the same value as <code> getName </code>.
   *
   */
  public String toString() {
    return getName();
  }

  public void add(ClassAccessFlag flag) {
    mValue |= flag.getValue();
  }

  public void remove(ClassAccessFlag flag) {
    mValue &= ~flag.getValue();
  }

  /**
   * Sets the value of this set. This value must be a legal combination
   * of ClassAccessFlags.
   *
	 * @param value the value of this set
   */
  protected void setValue(int value) {
    mValue = value;
  }

	/**
   * Tests ClassAccessFlagSet.
   *
   * @param args the command-line argument
   */
  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println("Usage: ClassAccessFlagSet [access-flag value]");
      return;
    }

    System.out.println(new ClassAccessFlagSet(Integer.parseInt(args[ 0])));
  }
}