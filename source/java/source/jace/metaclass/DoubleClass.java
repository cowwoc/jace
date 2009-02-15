package jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'double'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class DoubleClass implements MetaClass {

  private final static String newLine = System.getProperty("line.separator");

  public String getName() {
    return "JDouble";
  }

  public String getFullyQualifiedName(String separator) {
    return "jace" + separator + "proxy" + separator + "types" + separator + getName();
  }

  public ClassPackage getPackage() {
    return new ClassPackage(new String[0]);
  }

  public String beginGuard() {
    return "JACE_TYPES_JDOUBLE_H";
  }

  public String endGuard() {
    return "// #ifndef JACE_TYPES_JDOUBLE_H";
  }

  public String include() {
    return "#ifndef JACE_TYPES_JDOUBLE_H" + newLine +
      "#include \"jace/proxy/types/JDouble.h\"" + newLine +
      "#endif";
  }

  public String using() {
    return "using jace::proxy::types::JDouble;";
  }

  public String forwardDeclare() {
    String namespace =
      "BEGIN_NAMESPACE_3( jace, proxy, types )" + newLine +
      "class JDouble;" + newLine +
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
  public boolean equals(Object obj) {
    return (obj instanceof DoubleClass);
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  public MetaClass deProxy() {
    return this;
  }

  public boolean isPrimitive() {
    return true;
  }

  public String getJniType() {
    return "jdouble";
  }

  @Override
  public String toString() {
    return getClass().getName();
  }
}
