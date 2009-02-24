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
public class MetaClassFactory
{
  private static HashMap<String, MetaClass> ClassMap = new HashMap<String, MetaClass>();


  static
  {
    ClassMap.put("byte", new ByteClass(false));
    ClassMap.put("char", new CharClass(false));
    ClassMap.put("double", new DoubleClass(false));
    ClassMap.put("float", new FloatClass(false));
    ClassMap.put("int", new IntClass(false));
    ClassMap.put("long", new LongClass(false));
    ClassMap.put("short", new ShortClass(false));
    ClassMap.put("void", new VoidClass(false));
    ClassMap.put("boolean", new BooleanClass(false));
  }

  /**
   * Prevent construction.
   */
  private MetaClassFactory()
  {
  }

  /**
   * Returns the MetaClass for a primitive.
   *
   * @param primitiveClass the primitive type
   * @return null in case of no match
   */
  private static MetaClass getPrimitiveClass(String primitiveClass)
  {
    return ClassMap.get(primitiveClass);
  }

  /**
   * Creates a MetaClass for a type name.
   *
   * @param typeName a fully qualified type name
   * @return the MetaClass
   */
  public static MetaClass getMetaClass(TypeName typeName)
  {
    String identifier = typeName.asIdentifier();
    assert (!identifier.contains("/") && !identifier.contains(";")): identifier;
    // Check to see if this is an array class. If so, handle it accordingly.
    if (identifier.charAt(0) == '[')
    {
      TypeName componentType = TypeNameFactory.fromIdentifier(identifier.substring(1, identifier.length()));
      MetaClass componentClass = getMetaClass(componentType);
      return new ArrayMetaClass(componentClass);
    }

    // if this is a primitive class, then we are done
    MetaClass primitiveClass = getPrimitiveClass(identifier);
    if (primitiveClass != null)
      return primitiveClass;

    List<String> packageList = new ArrayList<String>();
    for (String element: identifier.split(Pattern.quote(".")))
      packageList.add(element);
    assert (packageList.size() > 0);

    // The last element is the class name
    String name = packageList.remove(packageList.size() - 1);
    return new ClassMetaClass(name, new ClassPackage(packageList));
  }
}
