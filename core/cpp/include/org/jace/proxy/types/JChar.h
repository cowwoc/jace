#ifndef ORG_JACE_TYPES_JCHAR_H
#define ORG_JACE_TYPES_JCHAR_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/Jace.h"
#include "org/jace/JClass.h"
#include "org/jace/JClassImpl.h"
#include "org/jace/proxy/JValue.h"
#include "org/jace/JNIException.h"

#include <iostream>

BEGIN_NAMESPACE_4(org, jace, proxy, types)


/** 
 * A representation of the java primitive char.
 *
 * @author Toby Reyelts
 */
class JChar : public JValue
{
public:
	/**
	 * Creates a new JChar with the given value.
	 */
	JACE_API JChar(jvalue value);

	/**
	 * Creates a new JChar with the given value.
	 */
	JACE_API JChar(jchar _char);

	/**
	 * Destroys the existing java object.
	 */
	JACE_API virtual ~JChar();

	/**
	 * Returns the char value of this java char.
	 */
	JACE_API operator jchar() const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator==(const JChar& _char) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator!=(const JChar& _char) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator==(jchar val) const;

	/**
	 * Compares this instance to another.
	 */
	JACE_API bool operator!=(jchar val) const;

	/**
	 * Returns the JClass for this class.
	 */
	JACE_API static const ::org::jace::JClass& staticGetJavaJniClass() throw (::org::jace::JNIException);

	/**
	 * Returns the JClass for this instance.
	 */
	JACE_API virtual const ::org::jace::JClass& getJavaJniClass() const throw (::org::jace::JNIException);

	/**
	 * Support printing of characters.
	 */
	JACE_API friend std::ostream& operator<<(std::ostream& stream, const JChar& val);
};

END_NAMESPACE_4(org, jace, proxy, types)

#endif // #ifndef ORG_JACE_TYPES_JCHAR_H
