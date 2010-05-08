package jace.metaclass;

/**
 * Represents meta-data about a class.
 *
 * Specifically, this class represents an Array-based class.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ArrayMetaClass implements MetaClass
{
  private MetaClass elementType;

  /**
   * Constructs a new ArrayMetaClass with the given metaClass
   * as the base type.
   *
   * @param metaClass the meta class
   */
  public ArrayMetaClass(MetaClass metaClass)
  {
    this.elementType = metaClass;
  }

  public String getSimpleName()
  {
    return "JArray< " + elementType.getSimpleName() + " >";
  }

  public String getFullyQualifiedName(String separator)
  {
    return "jace::JArray< " + "::" + elementType.getFullyQualifiedName(separator) + " >";
  }

  public ClassPackage getPackage()
  {
    return elementType.getPackage();
  }

  public String beginGuard()
  {
    return elementType.beginGuard();
  }

  public String endGuard()
  {
    return elementType.endGuard();
  }

  public String include()
  {
    return elementType.include();
  }

  public String using()
  {
    return elementType.using();
  }

  public String forwardDeclare()
  {
    return elementType.forwardDeclare();
  }

  /**
   * Compares this MetaClass to another.
   *
   * In this case, we say that an ArrayMetaClass is equal to another
   * MetaClass if they both have the same base class.
   * 
   * @param o the object to compare to
   */
  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof ArrayMetaClass))
      return false;
    ArrayMetaClass other = (ArrayMetaClass) o;
    return getElementType().equals(other.getElementType());
  }

  public int hashCode()
  {
    return getSimpleName().hashCode();
  }

  /**
   * Returns the array element type.
   *
   * @return the array element type
   */
  public MetaClass getElementType()
  {
    return elementType;
  }

  /**
   * Returns the type of the innermost element of the array.
   *
   * @return the type of the innermost element of the array
   */
  public MetaClass getInnermostElementType()
  {
    MetaClass result = elementType;
    while (result instanceof ArrayMetaClass)
      result = ((ArrayMetaClass) result).getElementType();
    return result;
  }

  public MetaClass proxy()
  {
    return new ArrayMetaClass(getElementType().proxy());
  }

  public MetaClass unProxy()
  {
    return new ArrayMetaClass(getElementType().unProxy());
  }

  public boolean isPrimitive()
  {
    return false;
  }

  public String getJniType()
  {
    if (elementType instanceof BooleanClass)
      return "jbooleanArray";
    if (elementType instanceof ByteClass)
      return "jbyteArray";
    if (elementType instanceof CharClass)
      return "jcharArray";
    if (elementType instanceof DoubleClass)
      return "jdoubleArray";
    if (elementType instanceof FloatClass)
      return "jfloatArray";
    if (elementType instanceof LongClass)
      return "jlongArray";
    if (elementType instanceof ShortClass)
      return "jshortArray";
    return "jobjectArray";
  }

  @Override
  public String toString()
  {
    return getClass().getName() + "[" + getElementType() + "]";
  }
}
