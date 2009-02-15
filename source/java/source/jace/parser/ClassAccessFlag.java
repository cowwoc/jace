package jace.parser;

import java.util.Arrays;
import java.util.Collection;

/**
 * Represents an access flag for a Class.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassAccessFlag {

  /** Declared public; may be accessed from outside its package
   */
  public static final ClassAccessFlag PUBLIC = new ClassAccessFlag(0x0001, "public");
  /** Declared final; no subclasses allowed.
   */
  public static final ClassAccessFlag FINAL = new ClassAccessFlag(0x0010, "final");
  /** Treat superclass methods specially when invoked by the invokespecial instruction.
   */
  public static final ClassAccessFlag SUPER = new ClassAccessFlag(0x0020, "super");
  /** Is an interface, not a class.
   */
  public static final ClassAccessFlag INTERFACE = new ClassAccessFlag(0x0200, "interface");
  /** Declared abstract; must not be instantiated.
   */
  public static final ClassAccessFlag ABSTRACT = new ClassAccessFlag(0x0400, "abstract");
  /** Declared synthetic; Not present in the source code.
   */
  public static final ClassAccessFlag SYNTHETIC = new ClassAccessFlag(0x1000, "synthetic");
  /** Declared as an annotation type. 
   */
  public static final ClassAccessFlag ANNOTATION = new ClassAccessFlag(0x2000, "annotation");
  /** Declared as an enum type. 
   */
  public static final ClassAccessFlag ENUM = new ClassAccessFlag(0x4000, "enum");

  /**
   * Creates a new ClassAccessFlag with the given name and value.
   *
   */
  protected ClassAccessFlag(int value, String name) {
    mValue = value;
    mName = name;
  }

  /**
   * Returns a collection of the existing ClassAccessFlags.
   *
   */
  public static Collection<ClassAccessFlag> getFlags() {

    ClassAccessFlag[] flags = {
      PUBLIC,
      FINAL,
      SUPER,
      INTERFACE,
      ABSTRACT,
      ANNOTATION,
      SYNTHETIC
    };

    return Arrays.asList(flags);
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
   * Returns true if this ClassAccessFlag is the same as obj.
   * ClassAccessFlags are equal if they have the same value.
   *
   */
  public boolean equals(Object obj) {
    if (obj instanceof ClassAccessFlag) {
      return (mValue == ((ClassAccessFlag) obj).mValue);
    }

    return false;
  }
  private int mValue;
  private String mName;
}
