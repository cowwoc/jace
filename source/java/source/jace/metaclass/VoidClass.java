package jace.metaclass;

import java.util.Collections;

/**
 * Represents the meta-data for the java primitive, 'void'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class VoidClass implements MetaClass
{
  private final static String newLine = System.getProperty("line.separator");

  public String getName()
  {
    return "JVoid";
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
    return "JACE_TYPES_JVOID_H";
  }

  public String endGuard()
  {
    return "// #ifndef JACE_TYPES_JVOID_H";
  }

  public String include()
  {
    return "#ifndef JACE_TYPES_JVOID_H" + newLine +
           "#include \"" + JaceConstants.getProxyPackage().asPath() + "/types/JVoid.h\"" + newLine +
           "#endif";
  }

  public String using()
  {
    return "using jace::proxy::types::JVoid;";
  }

  public String forwardDeclare()
  {
    String namespace =
           "BEGIN_NAMESPACE_3( jace, proxy, types )" + newLine +
           "class JVoid;" + newLine +
           "END_NAMESPACE_3( jace, proxy, types )";

    return namespace;
  }

  /**
   * Compares this MetaClass to another.
   *
   * Two MetaClasses are equal if they have the same name and belong
   * to the same package.
   */
  public boolean equals(Object obj)
  {
    return (obj instanceof VoidClass);
  }

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
    return "void";
  }

  @Override
  public String toString()
  {
    return getClass().getName();
  }
}
