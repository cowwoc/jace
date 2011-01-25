#ifndef ORG_JACE_TYPES_JVOID
#define ORG_JACE_TYPES_JVOID

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/Jace.h"
#include "org/jace/JClass.h"
#include "org/jace/JClassImpl.h"
#include "org/jace/proxy/JValue.h"
#include "org/jace/JNIException.h"


BEGIN_NAMESPACE_4(org, jace, proxy, types)

/**
 * A class representing the java primitive type, void.
 *
 * @author Toby Reyelts
 */
class JVoid : public JValue
{
public:
	/**
	 * Returns the JClass for the Void type.
	 *
	 * @throw JNIException if an error occurs while trying to retrieve the class.
	 */
	JACE_API virtual const ::org::jace::JClass& getJavaJniClass() const throw (::org::jace::JNIException);

	/**
	 * Returns the JClass for the Void type.
	 *
	 * @throw JNIException if an error occurs while trying to retrieve the class.
	 */
	JACE_API static const ::org::jace::JClass& staticGetJavaJniClass() throw (::org::jace::JNIException);
};

END_NAMESPACE_4(org, jace, proxy, types)

#endif // #ifndef ORG_JACE_TYPES_JVOID
