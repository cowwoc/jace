package org.jace.metaclass;

/**
 * Represents the meta-data for the java primitive, 'void'.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class VoidClass extends PrimitiveMetaClass
{
	/**
	 * Creates a new VoidClass.
	 *
	 * @param isProxy true if the object represents a proxy
	 */
	public VoidClass(boolean isProxy)
	{
		super(isProxy);
	}

	@Override
	protected MetaClass newInstance(boolean isProxy)
	{
		return new VoidClass(isProxy);
	}

	@Override
	public String getSimpleName()
	{
		return "JVoid";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof VoidClass;
	}

	@Override
	public int hashCode()
	{
		return getSimpleName().hashCode();
	}

	@Override
	public String getJniType()
	{
		return "void";
	}
}
