package org.jace.metaclass;

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
	private final MetaClass elementType;

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

	@Override
	public String getSimpleName()
	{
		return "JArray< " + elementType.getSimpleName() + " >";
	}

	@Override
	public String getFullyQualifiedName(String separator)
	{
		return "jace::JArray< " + "::" + elementType.getFullyQualifiedName(separator) + " >";
	}

	@Override
	public ClassPackage getPackage()
	{
		return elementType.getPackage();
	}

	@Override
	public String beginGuard()
	{
		return elementType.beginGuard();
	}

	@Override
	public String endGuard()
	{
		return elementType.endGuard();
	}

	@Override
	public String include()
	{
		return elementType.include();
	}

	@Override
	public String using()
	{
		return elementType.using();
	}

	@Override
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

	@Override
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

	@Override
	public MetaClass proxy()
	{
		return new ArrayMetaClass(getElementType().proxy());
	}

	@Override
	public MetaClass unProxy()
	{
		return new ArrayMetaClass(getElementType().unProxy());
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}

	@Override
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
