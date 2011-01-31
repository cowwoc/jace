package org.jace.metaclass;

/**
 * Represents meta-data about a class.
 *
 * For classes that belong to the 'java' or 'javax' package tree,
 * this class automatically adds 'jace' prefixes to the identifiers.
 *
 * This helps prevent any sort of naming clashes caused by using other tools
 * that work with standard java class libraries.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public interface MetaClass
{
	/**
	 * Returns the name of the class that this MetaClass represents.
	 *
	 * This is the simple name of the class, unadorned by any package prefixes.
	 *
	 * For example:
	 *   String
	 *
	 * @return the name of the class that this MetaClass represents
	 */
	String getSimpleName();

	/**
	 * Returns the fully-qualified name of the class.
	 *
	 * This name includes both the package-prefix and the class name.
	 *
	 * For example:
	 *   java.lang.String
	 *
	 * @param separator The string used to separate the packages and the class name.
	 * @return the fully-qualified name of the class.
	 */
	String getFullyQualifiedName(String separator);

	/**
	 * Returns the ClassPackage for this MetaClass.
	 *
	 * @return the ClassPackage for this MetaClass
	 */
	ClassPackage getPackage();

	/**
	 * Returns a string which begins an include guard.
	 *
	 * For example:
	 *
	 * #ifndef JACE_JAVA_LANG_STRING_H
	 * #define JACE_JAVA_LANG_STRING_H
	 *
	 * @return a string which begins an include guard
	 */
	String beginGuard();

	/**
	 * Returns a String which ends an include guard.
	 *
	 * For example:
	 *
	 * #endif // #ifndef JACE_JAVA_LANG_STRING_H
	 *
	 * @return a String which ends an include guard
	 */
	String endGuard();

	/**
	 * Returns a String which generates an include of the header file for this class.
	 *
	 * For example:
	 *
	 * #include "jace/java/lang/String.h"
	 *
	 * @return a String which generates an include of the header file for this class
	 */
	String include();

	/**
	 * Returns a String which is a using declaration for this class.
	 *
	 * For example, "using jace::java::lang::String;".
	 *
	 * @return a String which is a using declaration for this class
	 */
	String using();

	/**
	 * Returns a String which is a forward declaration for this class.
	 *
	 * For example:
	 *
	 * BEGIN_NAMESPACE_3(jace, java, lang)
	 * class String;
	 * END_NAMESPACE_3(jace, java, lang)
	 *
	 * @return a String which is a forward declaration for this class
	 */
	String forwardDeclare();

	/**
	 * Return a version of this MetaClass with a pre-pended <code>JaceConstants.getProxyPackage()</code> package.
	 *
	 * @return a version of this MetaClass with a pre-pended <code>JaceConstants.getProxyPackage()</code> package
	 */
	MetaClass proxy();

	/**
	 * Return a version of this MetaClass without a pre-pended <code>JaceConstants.getProxyPackage()</code> package.
	 *
	 * @return a version of this MetaClass without a pre-pended <code>JaceConstants.getProxyPackage()</code> package
	 */
	MetaClass unProxy();

	/**
	 * Returns the JNI type for this MetaClass.
	 *
	 * For example, jstring for java.lang.String or jbooleanArray for boolean[].
	 *
	 * @return the JNI type for this MetaClass
	 */
	String getJniType();

	/**
	 * Indicates if the underlying class is a primitive.
	 *
	 * @return true if the underlying class is a primitive
	 */
	boolean isPrimitive();
}
