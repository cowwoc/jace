#ifndef ORG_JACE_TYPES_JLONG_H
#define ORG_JACE_TYPES_JLONG_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/Jace.h"
#include "org/jace/JClass.h"
#include "org/jace/proxy/JValue.h"
#include "org/jace/JNIException.h"
#include "org/jace/proxy/types/JInt.h"


BEGIN_NAMESPACE_4(org, jace, proxy, types)
class JInt;

/** 
 * A representation of the java primitive long.
 *
 * @author Toby Reyelts
 */
class JLong: public JValue
{
public:
	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JLong(jvalue value);

	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JLong(jlong _long);

	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JLong(const ::org::jace::proxy::types::JInt& _int);

	/**
	 * Destroys the existing java object.
	 */
	JACE_API virtual ~JLong();

	/**
	 * Returns the value of this instance.
	 */
	JACE_API operator jlong() const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator==(const JLong& _long) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator!=(const JLong& _long) const;


	/**
	 * Compares this instance to a primitive.
	 */
	JACE_API bool operator==(jlong val) const;

	/**
	 * Compares this instance to a primitive.
	 */
	JACE_API bool operator!=(jlong val) const;

	/**
	 * Returns the JClass for this class.
	 */
	JACE_API static const ::org::jace::JClass& staticGetJavaJniClass() throw (::org::jace::JNIException);

	/**
	 * Retrieves the JavaClass for this JObject.
	 *
	 * @throw JNIException if an error occurs while trying to retrieve the class.
	 */
	JACE_API virtual const ::org::jace::JClass& getJavaJniClass() const throw (::org::jace::JNIException);
};


END_NAMESPACE_4(org, jace, proxy, types)

#endif // #ifndef ORG_JACE_TYPES_JLONG_H
