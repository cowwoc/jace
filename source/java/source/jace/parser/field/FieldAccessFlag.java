package jace.parser.field;

import jace.parser.*;

import java.util.*;


/**
 * Represents an access flag for a ClassField.
 *
 * @author Toby Reyelts
 *
 */
public class FieldAccessFlag {

  /** Declared public; may be accessed from outside its package
   */
  public static final FieldAccessFlag PUBLIC = new FieldAccessFlag( 0x0001, "public" );
  
  /** Declared private; accessible only within the defining class
   */
  public static final FieldAccessFlag PRIVATE = new FieldAccessFlag( 0x0002, "private" );
  
  /** Declared protected; may be accessed within subclasses
   */
  public static final FieldAccessFlag PROTECTED = new FieldAccessFlag( 0x0004, "protected" );
  
  /** Declared static
   */
  public static final FieldAccessFlag STATIC = new FieldAccessFlag( 0x0008, "static" );
  
  /** Declared final; may not be overridden.
   */
  public static final FieldAccessFlag FINAL = new FieldAccessFlag( 0x0010, "final" );
  
  /** Declared volatile; cannot be cached.
   */
  public static final FieldAccessFlag VOLATILE = new FieldAccessFlag( 0x0040, "volatile" );
  
  /** Declared transient; not written or read by a persistent object manager.
   */
  public static final FieldAccessFlag TRANSIENT = new FieldAccessFlag( 0x0080, "transient" );
  
  /** Declared synthetic; Not present in the source code.
   */
  public static final FieldAccessFlag SYNTHETIC = new FieldAccessFlag( 0x1000, "synthetic" );

  /** Declared as an element of an enum.
   */
  public static final FieldAccessFlag ENUM = new FieldAccessFlag( 0x4000, "enum" );

  /**
   * Creates a new FieldAccessFlag with the given name and value.
   *
   */
  protected FieldAccessFlag( int value, String name ) {
    mValue = value;
    mName = name;
  }
  
  /**
   * Returns a collection of the existing FieldAccessFlags.
   *
   */
  public static Collection<FieldAccessFlag> getFlags() {
  
    FieldAccessFlag[] flags = {
      PUBLIC,
      PRIVATE,
      PROTECTED,
      STATIC,
      FINAL,
      VOLATILE,
      TRANSIENT,
      ENUM,
      SYNTHETIC
    };   
  
    return Arrays.asList( flags );
  }
  
  /**
   * Returns the name used to represent the flag in Java source code.
   * For example, "public", "protected", etc ...
   *
   */
  public String getName() {
    return mName;
  }
  
  /**
   * Returns the value used to represent the flag in a Java class file.
   *
   */
  public int getValue() {
    return mValue;
  }
  
  /**
   * Returns true if this FieldAccessFlag is the same as obj.
   * FieldAccessFlags are equal if they have the same value.
   *
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof FieldAccessFlag ) {
      return ( mValue == ( ( FieldAccessFlag ) obj ).mValue );
    }
  
    return false;
  }
  
  private int mValue;
  private String mName;
}