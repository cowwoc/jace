#ifndef ORG_JACE_TYPES_JSHORT_H
#define ORG_JACE_TYPES_JSHORT_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/Jace.h"
#include "org/jace/JClass.h"
#include "org/jace/JClassImpl.h"
#include "org/jace/proxy/JValue.h"
#include "org/jace/JNIException.h"


BEGIN_NAMESPACE_4(org, jace, proxy, types)

/** 
 * A representation of the java primitive short.
 *
 * @author Toby Reyelts
 */
class JShort : public JValue
{
public:
	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JShort(jvalue value);

	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JShort(jshort value);

	/**
	 * Destroys the existing java object.
	 */
	JACE_API virtual ~JShort();

	/**
	 * Returns the value of this instance.
	 */
	JACE_API operator jshort() const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator==(const JShort& value) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator!=(const JShort& value) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator==(jshort value) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator!=(jshort value) const;

	/**
	 * Returns the JClass for this class.
	 */
	JACE_API static const ::org::jace::JClass& staticGetJavaJniClass() throw (::org::jace::JNIException);

	/**
	 * Returns the JClass for this instance.
	 */
	JACE_API virtual const ::org::jace::JClass& getJavaJniClass() const throw (::org::jace::JNIException);
};


END_NAMESPACE_4(org, jace, proxy, types)

#endif // #ifndef ORG_JACE_TYPES_JSHORT_H
