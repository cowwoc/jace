#ifndef ORG_JACE_JARRAY_HELPER
#define ORG_JACE_JARRAY_HELPER

#include "os_dep.h"
#include "namespace.h"
#include "org/jace/proxy/JObject.h"
#include "org/jace/JClass.h"
#include "org/jace/proxy/types/JInt.h"

#include "jni.h"

BEGIN_NAMESPACE_3(org, jace, JArrayHelper)

JACE_API jobjectArray newArray(int size, const org::jace::JClass& elementClass);
JACE_API org::jace::proxy::types::JInt getLength(jobject obj);
JACE_API jvalue getElement(jobject obj, int index);

END_NAMESPACE_3(org, jace, JArrayHelper)

#endif
