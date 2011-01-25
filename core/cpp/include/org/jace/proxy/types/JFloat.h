#ifndef ORG_JACE_TYPES_JFLOAT_H
#define ORG_JACE_TYPES_JFLOAT_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/Jace.h"
#include "org/jace/JClass.h"
#include "org/jace/JClassImpl.h"
#include "org/jace/proxy/JValue.h"
#include "org/jace/JNIException.h"


BEGIN_NAMESPACE_4(org, jace, proxy, types)

/** 
 * A representation of the java primitive float.
 *
 * @author Toby Reyelts
 */
class JFloat : public JValue
{
public:
	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JFloat(jvalue value);

	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JFloat(jfloat value);

	/**
	 * Destroys the existing java object.
	 */
	JACE_API virtual ~JFloat();

	/**
	 * Returns the value of this instance.
	 */
	JACE_API operator jfloat() const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator==(const JFloat& value) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator!=(const JFloat& value) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator==(jfloat value) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator!=(jfloat value) const;

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

#endif // #ifndef ORG_JACE_TYPES_JFLOAT_H
