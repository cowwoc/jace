package org.jace.parser;

import com.google.common.collect.Lists;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.UTF8Constant;
import java.util.List;

/**
 * Represents a class file's constant pool.
 *
 * @author Toby Reyelts
 */
public class ConstantPool
{
  private final List<Constant> constants = Lists.newArrayList();
  private int numEntries = 0;

  /**
   * Searches the constant pool for the UTF8Constant that matches <code>constant</code>.
   * If <code>constant</code> can be found, it returns the constant pool index for the UTF8Constant.
   * If <code>constant</code> can't be found, it creates a new UTF8Constant, adds it to the pool,
   * and returns the index for that new UTF8Constant.
   *
   * @param constant the UTF8 constant name
   * @return the index of the constant
   */
  public int addUTF8(String constant)
  {
    int index = 1;

    for (Constant c: constants)
    {
      if (c instanceof UTF8Constant && c.toString().equals(constant))
        return index;
      index += c.getSize();
    }

    addConstant(new UTF8Constant(constant.getBytes()));
    return index;
  }

  /**
   * Adds a new Constant to the end of the ConstantPool.
   *
   * @param constant the constant
   */
  public void addConstant(Constant constant)
  {
    constants.add(constant);
    numEntries += constant.getSize();
  }

  /**
   * Retrieve the Constant at the specified <code> index </code>.
   *
   * @param index The index of the Constant. This value must be greater
   * than 0. Some double-sized Constants take up two indices.
   *
   * @return the Constant at <code> index </code> are null if <code> index </code> is 0,
   * points to the middle of a Constant, or is beyond the size of the ConstantPool.
   */
  public Constant getConstantAt(int index)
  {
    if (index == 0)
      return null;

    int count = 1;
    for (int i = 0; i < constants.size(); ++i)
    {
      Constant c = constants.get(i);

      if (count == index)
        return c;
      count += c.getSize();
    }
    return null;
  }

  /**
   * Returns the constant at the specified <code>index</code>.
   *
   * @param index the index to look up
   * @return the Constant at the index
   */
  public Constant getConstant(int index)
  {
    return constants.get(index);
  }

  /**
   * Returns the number of constants in the pool.
   *
   * @return the number of constants in the pool
   */
  public int getSize()
  {
    return constants.size();
  }

  /**
   * @return a non-negative size. Note that the number of entries
   * in the pool is either equal to or larger than the actual number of Constants in the pool.
   */
  public int getNumEntries()
  {
    return numEntries;
  }
}
