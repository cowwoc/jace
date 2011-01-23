package org.jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'int'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class IntClass extends PrimitiveMetaClass
{
	/**
	 * Creates a new IntClass.
	 *
	 * @param isProxy true if the object represents a proxy
	 */
	public IntClass(boolean isProxy)
	{
		super(isProxy);
	}

	@Override
	protected MetaClass newInstance(boolean isProxy)
	{
		return new IntClass(isProxy);
	}

	@Override
	public String getSimpleName()
	{
		return "JInt";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof IntClass;
	}

	@Override
	public int hashCode()
	{
		return getSimpleName().hashCode();
	}

	@Override
	public String getJniType()
	{
		return "jint";
	}
}
