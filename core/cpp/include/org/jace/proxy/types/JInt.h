#ifndef ORG_JACE_TYPES_JINT_H
#define ORG_JACE_TYPES_JINT_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/Jace.h"
#include "org/jace/JClass.h"
#include "org/jace/proxy/JValue.h"
#include "org/jace/JNIException.h"
#include "org/jace/proxy/types/JByte.h"

#include <iostream>


BEGIN_NAMESPACE_4(org, jace, proxy, types)
class JByte;

/** 
 * A representation of the java primitive int.
 *
 * @author Toby Reyelts
 */
class JInt: public JValue
{
public:
	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JInt(jvalue value);

	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JInt(const jint _int);

	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JInt(const ::org::jace::proxy::types::JByte& _byte);

	/**
	 * Destroys the existing java object.
	 */
	JACE_API virtual ~JInt();

	/**
	 * Returns the value of this instance.
	 */
	JACE_API operator jint() const;

	/**
	 * Compares this JInt to another.
	 */
	JACE_API bool operator==(const JInt& _int) const;

	/**
	 * Compares this JInt to another.
	 */
	JACE_API bool operator!=(const JInt& _int) const;

	/**
	 * Compares this JInt to a jint.
	 */
	JACE_API bool operator==(jint val) const;

	/**
	 * Compares this JInt to a jint.
	 */
	JACE_API bool operator!=(jint val) const;

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

	JACE_API friend std::ostream& operator<<(std::ostream& stream, const JInt& val);
};


END_NAMESPACE_4(org, jace, proxy, types)

#endif // #ifndef ORG_JACE_TYPES_JINT_H
