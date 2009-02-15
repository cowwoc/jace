package jace.metaclass;

/**
 * Represents meta-data about a class.
 *
 * Specifically, this class represents an Array-based class.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ArrayMetaClass implements MetaClass {

  /**
   * Constructs a new ArrayMetaClass with the given metaClass
   * as the base type.
   *
   * @param metaClass the meta class
   */
  public ArrayMetaClass(MetaClass metaClass) {
    mBaseClass = metaClass;
  }

  public String getName() {
    return "JArray< " + mBaseClass.getName() + " >";
  }

  public String getFullyQualifiedName(String separator) {
    return "jace::JArray< " + "::" + mBaseClass.getFullyQualifiedName(separator) + " >";
  }

  public ClassPackage getPackage() {
    return mBaseClass.getPackage();
  }

  public String beginGuard() {
    return mBaseClass.beginGuard();
  }

  public String endGuard() {
    return mBaseClass.endGuard();
  }

  public String include() {
    return mBaseClass.include();
  }

  public String using() {
    return mBaseClass.using();
  }

  public String forwardDeclare() {
    return mBaseClass.forwardDeclare();
  }

  /**
   * Compares this MetaClass to another.
   *
   * In this case, we say that an ArrayMetaClass is equal to another
   * MetaClass if they both have the same base class.
   *
   */
  @Override
  public boolean equals(Object obj) {

    MetaClass baseMetaClass = getBaseClass();

    if (obj instanceof ArrayMetaClass) {
      ArrayMetaClass metaClass = (ArrayMetaClass) obj;
      return baseMetaClass.equals(metaClass.getBaseClass());
    }

    if (obj instanceof MetaClass) {
      return baseMetaClass.equals(obj);
    }

    return false;
  }

  public int hashCode() {
    return getName().hashCode();
  }

  public MetaClass getBaseClass() {

    if (mBaseClass instanceof ArrayMetaClass) {
      return ((ArrayMetaClass) mBaseClass).getBaseClass();
    }

    return mBaseClass;
  }

  public MetaClass deProxy() {
    return new ArrayMetaClass(getBaseClass().deProxy());
  }

  public boolean isPrimitive() {
    return false;
  }

  public String getJniType() {

    if (mBaseClass instanceof BooleanClass) {
      return "jbooleanArray";
    } else if (mBaseClass instanceof ByteClass) {
      return "jbyteArray";
    } else if (mBaseClass instanceof CharClass) {
      return "jcharArray";
    } else if (mBaseClass instanceof DoubleClass) {
      return "jdoubleArray";
    } else if (mBaseClass instanceof FloatClass) {
      return "jfloatArray";
    } else if (mBaseClass instanceof LongClass) {
      return "jlongArray";
    } else if (mBaseClass instanceof ShortClass) {
      return "jshortArray";
    } else {
      return "jobjectArray";
    }
  }
  private MetaClass mBaseClass;
}
