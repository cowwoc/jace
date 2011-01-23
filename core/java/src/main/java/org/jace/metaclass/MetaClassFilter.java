package org.jace.metaclass;

/**
 * A filter over a collection of types.
 *
 * @author Gili Tzabari
 */
public interface MetaClassFilter
{
	/**
	 * Indicates if a type should be accepted.
	 *
	 * @param candidate the type being evaluated
	 * @return true if the type should be accepted
	 */
	boolean accept(MetaClass candidate);
}
