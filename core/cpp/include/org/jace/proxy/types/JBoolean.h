#ifndef ORG_JACE_TYPES_JBOOLEAN_H
#define ORG_JACE_TYPES_JBOOLEAN_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/Jace.h"
#include "org/jace/JClass.h"
#include "org/jace/JClassImpl.h"
#include "org/jace/proxy/JValue.h"
#include "org/jace/JNIException.h"

BEGIN_NAMESPACE_2(org, jace)
template <class T> class ElementProxy;
template <class T> class JFieldProxy;
template <class T> class JField;
END_NAMESPACE_2(org, jace)

BEGIN_NAMESPACE_4(org, jace, proxy, types)


/**
 * A representation of the java primitive boolean.
 *
 * @author Toby Reyelts
 */
class JBoolean: public ::org::jace::proxy::JValue
{
public:
	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JBoolean(jvalue value);

	/**
	 * Creates a new instance with the given value.
	 */
	JACE_API JBoolean(jboolean value);

	/**
	 * Destroys the existing java object.
	 */
	JACE_API virtual ~JBoolean();

	/**
	 * Returns the value of this instance.
	 */
	JACE_API operator jboolean() const;

	/**
	 * Compares this JBoolean to another.
	 */
	JACE_API bool operator==(const JBoolean& _boolean) const;

	/**
	 * Compares this JBoolean to another.
	 */
	JACE_API bool operator!=(const JBoolean& _boolean) const;

	/**
	 * Compares this JBoolean to a jboolean.
	 */
	JACE_API bool operator==(jboolean val) const;

	/**
	 * Compares this JBoolean to a jboolean.
	 */
	JACE_API bool operator!=(jboolean val) const;

	/**
	 * Returns the JClass for this class.
	 */
	JACE_API static const ::org::jace::JClass& staticGetJavaJniClass() throw (::org::jace::JNIException);

	/**
	 * Returns the JClass for this instance.
	 */
	JACE_API virtual const ::org::jace::JClass& getJavaJniClass() const throw (::org::jace::JNIException);

	friend class ::org::jace::ElementProxy<JBoolean>;
	friend class ::org::jace::JFieldProxy<JBoolean>;
	friend class ::org::jace::JField<JBoolean>;
};


END_NAMESPACE_4(org, jace, proxy, types)

#endif // #ifndef ORG_JACE_TYPES_JBOOLEAN_H

