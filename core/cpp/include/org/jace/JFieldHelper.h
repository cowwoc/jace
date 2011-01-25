#ifndef ORG_JACE_JFIELD_HELPER_H
#define ORG_JACE_JFIELD_HELPER_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/proxy/JObject.h"
#include "org/jace/JClass.h"

#include "jni.h"
#include <string>

BEGIN_NAMESPACE_2(org, jace)

class JFieldHelper
{
public:
  JACE_API JFieldHelper(const std::string& name, const org::jace::JClass& typeClass);

  JACE_API jvalue getField(org::jace::proxy::JObject& object);
  JACE_API jvalue getField(const org::jace::JClass& jClass);
  JACE_API jfieldID getFieldID(const org::jace::JClass& parentClass, bool isStatic);
  JACE_API jfieldID getFieldID();

private:
	/**
	 * Prevent copying.
	 */
	JFieldHelper& operator=(JFieldHelper&);

  jfieldID mFieldID;
  const std::string mName;
  const JClass& mTypeClass;
};

END_NAMESPACE_2(org, jace)

#endif
