#ifndef ORG_JACE_JFIELD_PROXY_HELPER_H
#define ORG_JACE_JFIELD_PROXY_HELPER_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/proxy/JObject.h"
#include "org/jace/JClass.h"

#include "jni.h"

BEGIN_NAMESPACE_3(org, jace, JFieldProxyHelper)

JACE_API jobject assign(const org::jace::proxy::JObject& field, jobject parent, jfieldID fieldID);
JACE_API jobject assign(const org::jace::proxy::JObject& field, jclass parentClass, jfieldID fieldID);

END_NAMESPACE_3(org, jace, JFieldProxyHelper)

#endif
