#ifndef ORG_JACE_JCONSTRUCTOR_H
#define ORG_JACE_JCONSTRUCTOR_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/JClass.h"

BEGIN_NAMESPACE_2(org, jace)
class JArguments;


/**
 * Represents a java constructor.
 *
 * @author Toby Reyelts
 */
class JConstructor
{
public:
	/**
	 * Creates a new JConstructor for the given JClass.
	 */
	JACE_API JConstructor(const ::org::jace::JClass& javaClass);

	/**
	 * Invokes the constructor with the given JArguments.
	 *
	 * @throws JNIException if an error occurs while trying to invoke the constructor.
	 * @throws a matching C++ proxy, if a java exception is thrown by the constructor.
	 */
	JACE_API jobject invoke(const JArguments& arguments);

private:
	/**
	 * Prevent assignment.
	 */
	JConstructor& operator=(JConstructor&);
	/**
	 * Gets the method id matching the given arguments.
	 */
	jmethodID getMethodID(const ::org::jace::JClass& jClass, const JArguments& arguments);

	const ::org::jace::JClass& mClass;
	jmethodID mMethodID;
};

END_NAMESPACE_2(org, jace)

#endif
