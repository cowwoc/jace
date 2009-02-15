package jace.metaclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Creates new MetaClasses instances.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class MetaClassFactory {

  private static HashMap<String, MetaClass> ClassMap = new HashMap<String, MetaClass>();


  static {
    ClassMap.put("B", new ByteClass());
    ClassMap.put("C", new CharClass());
    ClassMap.put("D", new DoubleClass());
    ClassMap.put("F", new FloatClass());
    ClassMap.put("I", new IntClass());
    ClassMap.put("J", new LongClass());
    ClassMap.put("S", new ShortClass());
    ClassMap.put("V", new VoidClass());
    ClassMap.put("Z", new BooleanClass());
  }

  /**
   * Prevent construction.
   */
  private MetaClassFactory() {
  }

  /**
   * Returns the MetaClass for a primitive.
   *
   * @param primitiveClass the primitive type
   * @return null in case of no match
   */
  private static MetaClass getPrimitiveClass(String primitiveClass) {
    return ClassMap.get(primitiveClass);
  }

  /**
   * Same as getMetaClass(className, internal, true).
   *
   * @param className
   *
   * Must be a fully qualified class name. The packages may be separated by
   * '/' or by '.'. A class name may be optionally followed by a ';'.
   *
   * For example, all of the following formats are acceptable representations
   * of the class, java.lang.String:
   *   java.lang.String
   *   java.lang.String;
   *   java/lang/String
   *   java/lang/String;
   *
   * @param internal This value should be true, if className is in 'internal' format.
   * A class name is in internal format if it is pre-fixed by an 'L'. Although there
   * is only one format for primitive types, they are accepted as both internal and not internal.
   *
   * For example, java.lang.String can be represented in internal format as:
   *   Ljava/lang/String;
   *
   * @return the MetaClass
   */
  public static MetaClass getMetaClass(String className, boolean internal) {
    return getMetaClass(className, internal, true);
  }

  /**
   * Creates a new MetaClass based off of the given class name.
   *
   * @param className
   *
   * Must be a fully qualified class name. The packages may be separated by
   * '/' or by '.'. A class name may be optionally followed by a ';'.
   *
   * For example, all of the following formats are acceptable representations
   * of the class, java.lang.String:
   *   java.lang.String
   *   java.lang.String;
   *   java/lang/String
   *   java/lang/String;
   *
   * @param internal This value should be true, if className is in 'internal' format.
   * A class name is in internal format if it is pre-fixed by an 'L'. Although there
   * is only one format for primitive types, they are accepted as both internal and not internal.
   *
   * For example, java.lang.String can be represented in internal format as:
   *   Ljava/lang/String;
   *
   * @param setProxy - This value should be true if the meta classes should have the package
   * "jace/proxy" pre-pended to their normal package.
   *
   * @return the MetaClass
   */
  public static MetaClass getMetaClass(String className, boolean internal, boolean setProxy) {

    // Check to see if this is an array class. If so, handle it accordingly.
    if (className.charAt(0) == '[') {
      String componentType = className.substring(1, className.length());
      MetaClass componentClass = getMetaClass(componentType, internal, setProxy);
      return new ArrayMetaClass(componentClass);
    }

    // if this is a primitive class, then we are done
    MetaClass primitiveClass = getPrimitiveClass(className);

    if (primitiveClass != null) {
      return primitiveClass;
    }

    // Remove the leading 'L' from the front of the class if it's in internal format
    if (internal) {

      if (className.charAt(0) != 'L') {
        throw new RuntimeException("The class \"" + className + "\" is supposed to have internal representation, " +
          "but it doesn't begin with an \'L\'");
      }
      if (className.charAt(className.length() - 1) != ';') {
        throw new RuntimeException("The class \"" + className + "\" is supposed to have internal representation, " +
          "but it doesn't end with a \';\'");
      }
      // strip the "L" prefix and ";" suffix if it's in internal format
      className = className.substring(1, className.length() - 1);
    }

    // find which character is used to separate packages
    String separator = (className.indexOf("/") == -1) ? "." : "/";

    List<String> packageList = new ArrayList<String>();
    for (String element : className.split(Pattern.quote(separator))) {
      packageList.add(element);
    }
    assert (packageList.size() > 0);

    // The last element is the class name
    String name = packageList.remove(packageList.size() - 1);

    // if there is a trailing semi-colon, we get rid of it here
    if (name.endsWith(";"))
      name = name.substring(0, name.length() - ";".length());

    if (setProxy) {
      packageList.add(0, "jace");
      packageList.add(1, "proxy");
    }
    return new ClassMetaClass(name, new ClassPackage(packageList));
  }
}
