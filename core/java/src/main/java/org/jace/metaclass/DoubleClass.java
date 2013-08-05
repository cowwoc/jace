package org.jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'double'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class DoubleClass extends NumberClass
{
	/**
	 * Creates a new DoubleClass.
	 *
	 * @param isProxy true if the object represents a proxy
	 */
	public DoubleClass(boolean isProxy)
	{
		super(isProxy);
	}

	@Override
	protected MetaClass newInstance(boolean isProxy)
	{
		return new DoubleClass(isProxy);
	}

	@Override
	public String getSimpleName()
	{
		return "JDouble";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof DoubleClass;
	}

	@Override
	public int hashCode()
	{
		return getSimpleName().hashCode();
	}

	@Override
	public String getJniType()
	{
		return "jdouble";
	}
}
