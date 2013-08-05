package org.jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'long'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class LongClass extends NumberClass
{
	/**
	 * Creates a new LongClass.
	 *
	 * @param isProxy true if the object represents a proxy
	 */
	public LongClass(boolean isProxy)
	{
		super(isProxy);
	}

	@Override
	protected MetaClass newInstance(boolean isProxy)
	{
		return new LongClass(isProxy);
	}

	@Override
	public String getSimpleName()
	{
		return "JLong";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof LongClass;
	}

	@Override
	public int hashCode()
	{
		return getSimpleName().hashCode();
	}

	@Override
	public String getJniType()
	{
		return "jlong";
	}
}
