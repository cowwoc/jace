package org.jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'short'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ShortClass extends PrimitiveMetaClass
{
	/**
	 * Creates a new ShortClass.
	 *
	 * @param isProxy true if the object represents a proxy
	 */
	public ShortClass(boolean isProxy)
	{
		super(isProxy);
	}

	@Override
	protected MetaClass newInstance(boolean isProxy)
	{
		return new ShortClass(isProxy);
	}

	@Override
	public String getSimpleName()
	{
		return "JShort";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ShortClass;
	}

	@Override
	public int hashCode()
	{
		return getSimpleName().hashCode();
	}

	@Override
	public String getJniType()
	{
		return "jshort";
	}
}
