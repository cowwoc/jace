#ifndef ORG_JACE_TYPES_JBYTE_H
#define ORG_JACE_TYPES_JBYTE_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/Jace.h"
#include "org/jace/JClass.h"
#include "org/jace/JClassImpl.h"
#include "org/jace/proxy/JValue.h"
#include "org/jace/JNIException.h"


BEGIN_NAMESPACE_4(org, jace, proxy, types)

/** 
 * A representation of the java primitive byte.
 *
 * @author Toby Reyelts
 */
class JByte : public ::org::jace::proxy::JValue
{
public:
	/**
	 * Creates a new JByte with the given value.
	 */
	JACE_API JByte(jvalue value);

	/**
	 * Creates a new JByte with the given value.
	 */
	JACE_API JByte(jbyte byte);

	/**
	 * Destroys the existing java object.
	 */
	JACE_API virtual ~JByte();

	/**
	 * Returns the byte value of this java byte.
	 */
	JACE_API operator jbyte() const;

	/**
	 * Compares this JByte to another.
	 */
	JACE_API bool operator==(const JByte& _byte) const;

	/**
	 * Compares this JByte to another.
	 */
	JACE_API bool operator!=(const JByte& _byte) const;


	/**
	 * Compares this JByte to a jbyte.
	 */
	JACE_API bool operator==(jbyte val) const;

	/**
	 * Compares this JByte to a jbyte.
	 */
	JACE_API bool operator!=(jbyte val) const;

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

#endif // #ifndef ORG_JACE_TYPES_JBYTE_H

