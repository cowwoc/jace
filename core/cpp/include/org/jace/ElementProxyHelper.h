#ifndef ORG_JACE_ELEMENT_PROXY_HELPER
#define ORG_JACE_ELEMENT_PROXY_HELPER

#include "os_dep.h"
#include "namespace.h"
#include "org/jace/proxy/JObject.h"

#include "jni.h"

BEGIN_NAMESPACE_3(org, jace, ElementProxyHelper)

JACE_API void assign(const org::jace::proxy::JObject& element, int index, jarray parent);

END_NAMESPACE_3(org, jace, ElementProxyHelper)

#endif
