package jace.metaclass;

import java.util.Arrays;
import java.util.List;

/**
 * Creates a TypeName.
 *
 * @author Gili Tzabari
 */
public class TypeNameFactory
{
  /**
   * Returns the first non-array character in the name.
   * 
   * @param name the name
   * @return the first non-array character
   */
  private static int getEndOfArray(String name)
  {
    int result = 0;
    for (int len = name.length(); result < len; ++result)
    {
      if (name.charAt(result) != '[')
        break;
    }
    return result;
  }

  /**
   * Converts a primitive type name to a field descriptor.
   *
   * @return null if the name isn't a primitive type
   */
  private static String getPrimitiveDescriptor(String name)
  {
    int endOfArray = getEndOfArray(name);
    String array = name.substring(0, endOfArray);
    String postArray = name.substring(endOfArray);
    if (postArray.equals("byte"))
      return array + "B";
    if (postArray.equals("char"))
      return array + "C";
    if (postArray.equals("double"))
      return array + "D";
    if (postArray.equals("float"))
      return array + "F";
    if (postArray.equals("int"))
      return array + "I";
    if (postArray.equals("long"))
      return array + "J";
    if (postArray.equals("short"))
      return array + "S";
    if (postArray.equals("boolean"))
      return array + "Z";
    if (postArray.equals("void"))
      return array + "V";
    return null;
  }

  /**
   * Constructs a type name from its identifier representation.
   * 
   * @param name the type name
   * @return the TypeName
   * @throws IllegalArgumentException if name is null
   */
  public static TypeName fromIdentifier(final String name)
  {
    if (name == null)
      throw new IllegalArgumentException("name may not be null");
    return new TypeName()
    {
      @Override
      public String asIdentifier()
      {
        return name;
      }

      @Override
      public String asDescriptor()
      {
        String primitive = getPrimitiveDescriptor(name);
        if (primitive != null)
          return primitive;
        return "L" + name + ";";
      }

      @Override
      public String asPath()
      {
        throw new UnsupportedOperationException("There is no reliable way to convert an identifier to a path");
      }

      @Override
      public String toString()
      {
        return name;
      }

      @Override
      public List<String> getComponents()
      {
        return Arrays.asList(name.split("."));
      }
    };
  }

  /**
   * Converts a type name from its file path.
   *
   * @param path the file path
   * @return the TypeName
   * @throws IllegalArgumentException if path is null
   */
  public static TypeName fromPath(final String path)
  {
    if (path == null)
      throw new IllegalArgumentException("path may not be null");
    return new TypeName()
    {
      @Override
      public String asIdentifier()
      {
        StringBuilder result = new StringBuilder(path);
        int index = -1;
        while (true)
        {
          index = result.indexOf("/", index + 1);
          if (index == -1)
            break;
          result.replace(index, index + 1, ".");
        }
        index = result.indexOf("$");
        if (index != -1)
          result.replace(index, index + 1, ".");
        return result.toString();
      }

      @Override
      public String asDescriptor()
      {
        String primitive = getPrimitiveDescriptor(path);
        if (primitive != null)
          return primitive;
        return "L" + path + ";";
      }

      @Override
      public String asPath()
      {
        return path;
      }

      @Override
      public String toString()
      {
        return path;
      }

      @Override
      public List<String> getComponents()
      {
        return Arrays.asList(path.split("/"));
      }
    };
  }

  /**
   * Constructs a type name from a field descriptor.
   *
   * @param descriptor the field descriptor
   * @return the TypeName
   * @see http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#14152
   * @throws IllegalArgumentException if descriptor is null
   */
  public static TypeName fromDescriptor(final String descriptor) throws IllegalArgumentException
  {
    if (descriptor == null)
      throw new IllegalArgumentException("descriptor may not be null");
    return new TypeName()
    {
      @Override
      public String asIdentifier()
      {
        int arrayOffset = 0;
        for (int len = descriptor.length(); arrayOffset < len; ++arrayOffset)
        {
          if (descriptor.charAt(arrayOffset) != '[')
            break;
        }
        String array = descriptor.substring(0, arrayOffset);
        String postArray = descriptor.substring(arrayOffset);
        if (descriptor.length() > arrayOffset + 1)
        {
          if (postArray.charAt(0) != 'L')
            throw new IllegalArgumentException("descriptor must begin with a '[' or L': " + descriptor);
          if (postArray.charAt(postArray.length() - 1) != ';')
            throw new IllegalArgumentException("descriptor must end with a ';': " + descriptor);
          return (array + postArray.substring(1, postArray.length() - 1)).replace("/", ".").replace("$", ".");
        }
        else
        {
          if (postArray.equals("B"))
            return array + "byte";
          else if (postArray.equals("C"))
            return array + "char";
          else if (postArray.equals("D"))
            return array + "double";
          else if (postArray.equals("F"))
            return array + "float";
          else if (postArray.equals("I"))
            return array + "int";
          else if (postArray.equals("J"))
            return array + "long";
          else if (postArray.equals("S"))
            return array + "short";
          else if (postArray.equals("Z"))
            return array + "boolean";
          else if (postArray.equals("V"))
            return array + "void";
          else
            throw new IllegalArgumentException("descriptor is not a valid primitive type: " + descriptor);
        }
      }

      @Override
      public String asDescriptor()
      {
        return descriptor;
      }

      @Override
      public String asPath()
      {
        throw new UnsupportedOperationException("There is no reliable way to convert a descriptor to a path");
      }

      @Override
      public String toString()
      {
        return descriptor;
      }

      @Override
      public List<String> getComponents()
      {
        return Arrays.asList(asIdentifier().split("."));
      }
    };
  }
}
