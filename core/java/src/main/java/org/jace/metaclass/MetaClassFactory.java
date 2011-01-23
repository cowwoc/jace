package org.jace.metaclass;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

/**
 * Creates new MetaClasses instances.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class MetaClassFactory
{
  private static final Map<String, MetaClass> classMap = Maps.newHashMap();

  static
  {
    classMap.put("B", new ByteClass(false));
    classMap.put("C", new CharClass(false));
    classMap.put("D", new DoubleClass(false));
    classMap.put("F", new FloatClass(false));
    classMap.put("I", new IntClass(false));
    classMap.put("J", new LongClass(false));
    classMap.put("S", new ShortClass(false));
    classMap.put("V", new VoidClass(false));
    classMap.put("Z", new BooleanClass(false));
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
    return classMap.get(primitiveClass);
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
      TypeName componentType = TypeNameFactory.fromDescriptor(descriptor.substring(1, descriptor.
        length()));
      MetaClass componentClass = getMetaClass(componentType);
      return new ArrayMetaClass(componentClass);
    }

    // if this is a primitive class, then we are done
    MetaClass primitiveClass = getPrimitiveClass(descriptor);
    if (primitiveClass != null)
      return primitiveClass;

    List<String> packageList = Lists.newArrayListWithCapacity(typeName.getComponents().size());
    for (String element: typeName.getComponents())
      packageList.add(element);
    assert (packageList.size() > 0): typeName;

    // The last element is the class name
    String name = packageList.remove(packageList.size() - 1);
    return new ClassMetaClass(name, new ClassPackage(packageList));
  }
}
