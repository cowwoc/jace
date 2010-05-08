package jace.metaclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    ClassMap.put("B", new ByteClass(false));
    ClassMap.put("C", new CharClass(false));
    ClassMap.put("D", new DoubleClass(false));
    ClassMap.put("F", new FloatClass(false));
    ClassMap.put("I", new IntClass(false));
    ClassMap.put("J", new LongClass(false));
    ClassMap.put("S", new ShortClass(false));
    ClassMap.put("V", new VoidClass(false));
    ClassMap.put("Z", new BooleanClass(false));
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
    String descriptor = typeName.asDescriptor();
    // Check to see if this is an array class. If so, handle it accordingly.
    if (descriptor.charAt(0) == '[')
    {
      TypeName componentType = TypeNameFactory.fromDescriptor(descriptor.substring(1, descriptor.length()));
      MetaClass componentClass = getMetaClass(componentType);
      return new ArrayMetaClass(componentClass);
    }

    // if this is a primitive class, then we are done
    MetaClass primitiveClass = getPrimitiveClass(descriptor);
    if (primitiveClass != null)
      return primitiveClass;

    List<String> packageList = new ArrayList<String>();
    for (String element: typeName.getComponents())
      packageList.add(element);
    assert (packageList.size() > 0): typeName;

    // The last element is the class name
    String name = packageList.remove(packageList.size() - 1);
    return new ClassMetaClass(name, new ClassPackage(packageList));
  }
}
