package org.jace.metaclass;

/**
 * Represents the meta-data for a numeric java primitive.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public abstract class NumberClass extends PrimitiveMetaClass
{
	/**
	 * Creates a new NumberClass.
	 *
	 * @param isProxy true if the object represents a proxy
	 */
	public NumberClass(boolean isProxy)
	{
		super(isProxy);
	}
}
