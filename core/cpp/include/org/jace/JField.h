#ifndef ORG_JACE_JFIELD_H
#define ORG_JACE_JFIELD_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/JClass.h"
#include "org/jace/proxy/JObject.h"
#include "org/jace/JNIException.h"
#include "org/jace/JFieldProxy.h"
#include "org/jace/JFieldHelper.h"
#include "org/jace/proxy/types/JBoolean.h"
#include "org/jace/proxy/types/JByte.h"
#include "org/jace/proxy/types/JChar.h"
#include "org/jace/proxy/types/JDouble.h"
#include "org/jace/proxy/types/JFloat.h"
#include "org/jace/proxy/types/JInt.h"
#include "org/jace/proxy/types/JLong.h"
#include "org/jace/proxy/types/JShort.h"

#include <jni.h>

#include <string>

BEGIN_NAMESPACE_2(org, jace)

/**
 * Represents a java field.
 *
 * @author Toby Reyelts
 */
template <class Type> class JField
{
public:
	/**
	 * Creates a new JField representing the field with the
	 * given name.
	 */
	JField(const std::string& name): helper(name, Type::staticGetJavaJniClass())
	{
	}

	/**
	 * Retrieves the field belonging to the given object.
	 *
	 * @throws JNIException if an error occurs while trying to retrieve the field.
	 */
	JFieldProxy<Type> get(::org::jace::proxy::JObject& object)
	{
		jvalue value = helper.getField(object);
		JFieldProxy<Type> fieldProxy(helper.getFieldID(), value, object);
		JNIEnv* env = attach();
		deleteLocalRef(env, value.l);
		return fieldProxy;
	}


	/**
	 * Retrieves the value of the static field belonging to the given class.
	 *
	 * @throws JNIException if an error occurs while trying to retrieve the value.
	 */
	JFieldProxy<Type> get(const ::org::jace::JClass& jClass)
	{
		jvalue value = helper.getField(jClass);
		JFieldProxy<Type> fieldProxy(helper.getFieldID(), value, jClass.getClass());
		JNIEnv* env = attach();
		deleteLocalRef(env, value.l);
		return fieldProxy;
	}

private:
	::org::jace::JFieldHelper helper;

	jfieldID getFieldID(const ::org::jace::JClass& parentClass, bool isStatic = false)
	{
		return helper.getFieldID(parentClass, isStatic);
	}
};

END_NAMESPACE_2(org, jace)

/**
 * For those (oddball) compilers that need the template specialization
 * definitions in the header.
 */
#ifdef PUT_TSDS_IN_HEADER
  #include "org/jace/JField.tsd"
#else
  #include "org/jace/JField.tsp"
#endif

#endif
