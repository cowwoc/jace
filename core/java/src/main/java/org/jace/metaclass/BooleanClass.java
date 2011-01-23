package org.jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'boolean'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class BooleanClass extends PrimitiveMetaClass
{
	/**
	 * Creates a new BooleanClass.
	 *
	 * @param isProxy true if the object represents a proxy
	 */
	public BooleanClass(boolean isProxy)
	{
		super(isProxy);
	}

	@Override
	protected MetaClass newInstance(boolean isProxy)
	{
		return new BooleanClass(isProxy);
	}

	@Override
	public String getSimpleName()
	{
		return "JBoolean";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof BooleanClass;
	}

	@Override
	public int hashCode()
	{
		return getSimpleName().hashCode();
	}

	@Override
	public String getJniType()
	{
		return "jboolean";
	}
}
