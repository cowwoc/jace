package org.jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'char'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class CharClass extends PrimitiveMetaClass
{
	/**
	 * Creates a new CharClass.
	 *
	 * @param isProxy true if the object represents a proxy
	 */
	public CharClass(boolean isProxy)
	{
		super(isProxy);
	}

	@Override
	protected MetaClass newInstance(boolean isProxy)
	{
		return new CharClass(isProxy);
	}

	@Override
	public String getSimpleName()
	{
		return "JChar";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof CharClass;
	}

	@Override
	public int hashCode()
	{
		return getSimpleName().hashCode();
	}

	@Override
	public String getJniType()
	{
		return "jchar";
	}
}
