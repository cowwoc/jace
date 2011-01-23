package org.jace.metaclass;

import java.util.List;

/**
 * A fully qualified type name.
 *
 * @author Gili Tzabari
 * @see http://java.sun.com/docs/books/jls/third_edition/html/names.html#6.7
 */
public interface TypeName
{
	/**
	 * Returns the identifier representation of the type name (i.e. <code>java.lang.String</code>).
	 *
	 * @return the identifier representation of the type name. Empty string denotes an anonymous class.
	 */
	String asIdentifier();

	/**
	 * Returns the field descriptor representation of the type name (i.e. <code>Ljava/lang/String;</code>).
	 *
	 * @return the field descriptor representation of the type name. Empty string denotes an anonymous class.
	 */
	String asDescriptor();

	/**
	 * Returns the path representation of the type name (i.e. <code>java/lang/String</code>).
	 *
	 * @return the path representation of the type name. Empty string denotes an anonymous class.
	 */
	String asPath();

	/**
	 * Returns the components making up the TypeName.
	 *
	 * @return the components making up the TypeName. Empty list denotes an anonymous class.
	 */
	List<String> getComponents();
}
