package jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'int'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class IntClass implements MetaClass {

  private final static String newLine = System.getProperty("line.separator");

  public String getName() {
    return "JInt";
  }

  public String getFullyQualifiedName(String separator) {
    return "jace" + separator + "proxy" + separator + "types" + separator + getName();
  }

  public ClassPackage getPackage() {
    return new ClassPackage(new String[0]);
  }

  public String beginGuard() {
    return "JACE_TYPES_JINT_H";
  }

  public String endGuard() {
    return "// #ifndef JACE_TYPES_JINT_H";
  }

  public String include() {
    return "#ifndef JACE_TYPES_JINT_H" + newLine +
      "#include \"jace/proxy/types/JInt.h\"" + newLine +
      "#endif";
  }

  public String using() {
    return "using jace::proxy::types::JInt;";
  }

  public String forwardDeclare() {
    String namespace =
      "BEGIN_NAMESPACE_3( jace, proxy, types )" + newLine +
      "class JInt;" + newLine +
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
    return (obj instanceof IntClass);
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
    return "jint";
  }

  @Override
  public String toString() {
    return getClass().getName();
  }
}
