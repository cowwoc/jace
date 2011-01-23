package org.jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'byte'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ByteClass extends PrimitiveMetaClass
{
	/**
	 * Creates a new ByteClass.
	 *
	 * @param isProxy true if the object represents a proxy
	 */
	public ByteClass(boolean isProxy)
	{
		super(isProxy);
	}

	@Override
	protected MetaClass newInstance(boolean isProxy)
	{
		return new ByteClass(isProxy);
	}

	@Override
	public String getSimpleName()
	{
		return "JByte";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ByteClass;
	}

	@Override
	public int hashCode()
	{
		return getSimpleName().hashCode();
	}

	@Override
	public String getJniType()
	{
		return "jbyte";
	}
}
