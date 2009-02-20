package jace.metaclass;

import java.util.Collections;

/**
 * Represents the meta-data for the java primitive, 'short'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ShortClass implements MetaClass
{
  private final static String newLine = System.getProperty("line.separator");

  public String getName()
  {
    return "JShort";
  }

  public String getFullyQualifiedName(String separator)
  {
    return "jace" + separator + "proxy" + separator + "types" + separator + getName();
  }

  public ClassPackage getPackage()
  {
    return new ClassPackage(Collections.<String>emptyList());
  }

  public String beginGuard()
  {
    return "JACE_TYPES_JSHORT_H";
  }

  public String endGuard()
  {
    return "// #ifndef JACE_TYPES_JSHORT_H";
  }

  public String include()
  {
    return "#ifndef JACE_TYPES_JSHORT_H" + newLine +
           "#include \"" + JaceConstants.getProxyPackage().asPath() + "/types/JShort.h\"" + newLine +
           "#endif";
  }

  public String using()
  {
    return "using jace::proxy::types::JShort;";
  }

  public String forwardDeclare()
  {
    String namespace =
           "BEGIN_NAMESPACE_3( jace, proxy, types )" + newLine +
           "class JShort;" + newLine +
           "END_NAMESPACE_3( jace, proxy, types )";

    return namespace;
  }

  /**
   * Compares this MetaClass to another.
   *
   * Two MetaClasses are equal if they have the same name and belong
   * to the same package.
   */
  @Override
  public boolean equals(Object obj)
  {
    return (obj instanceof ShortClass);
  }

  @Override
  public int hashCode()
  {
    return getName().hashCode();
  }

  public MetaClass proxy()
  {
    throw new UnsupportedOperationException();
  }

  public MetaClass unProxy()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isPrimitive()
  {
    return true;
  }

  public String getJniType()
  {
    return "jshort";
  }

  @Override
  public String toString()
  {
    return getClass().getName();
  }
}
