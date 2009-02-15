package jace.parser;

import jace.parser.constant.*;

import java.util.ArrayList;

/**
 * Represents a class file's constant pool.
 *
 * @author Toby Reyelts
 *
 */
public class ConstantPool {

  private ArrayList<Constant> constants = new ArrayList<Constant>();
  private int numEntries = 0;

  /**
   * Searches the constant pool for the UTF8Constant that matches <code>s</code>. If <code>s</code>
   * can be found, it returns the constant pool index for the UTF8Constant. If <code>s</code>
   * can't be found, it creates a new UTF8Constant, adds it to the pool, and returns the
   * index for that new UTF8Constant.
   *
   */
  public int addUTF8( String s ) {

    int index = 1;

    for ( Constant c : constants ) {
      if ( c instanceof UTF8Constant && c.toString().equals( s ) ) {
        return index;
      }
      index += c.getSize();
    }

    addConstant( new UTF8Constant( s.getBytes() ) );
    return index;
  }

  /**
   * Adds a new Constant to the end of the ConstantPool.
   *
   */
  public void addConstant( Constant c ) {
    constants.add( c );
    numEntries += c.getSize();
  }

  /**
   * Retrieve the Constant at the specified <code> index </code>.
   *
   * @param index The index of the Constant. This value must be greater
   * than 0. Some double-sized Constants take up two indices.
   *
   * @return the Constant at <code> index </code> are null if <code> index </code> is 0,
   * points to the middle of a Constant, or is beyond the size of the ConstantPool.
   *
   */
  public Constant getConstantAt( int index ) {

    if ( index == 0 ) {
      return null;
    }

    int count = 1;

    for ( int i = 0; i < constants.size(); ++i ) {

      Constant c = constants.get( i );

      if ( count == index ) {
        return c;
      }

      count += c.getSize();
    }

    return null;
  }

  /**
   * Returns the nth constant at <code>position</code>.
   *
   */
  public Constant getConstant( int position ) {
    return constants.get( position );
  }

  /**
   * Returns the number of constants in the pool.
   *
   */
  public int getSize() {
    return constants.size();
  }

  /**
   * @return a non-negative size. Note that the number of entries
   * in the pool is either equal to or larger than the actual number of Constants in the pool.
   */
  public int getNumEntries() {
    return numEntries;
  }
}


