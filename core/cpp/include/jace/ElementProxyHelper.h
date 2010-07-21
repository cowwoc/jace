#ifndef JACE_ELEMENT_PROXY_HELPER
#define JACE_ELEMENT_PROXY_HELPER

#include "os_dep.h"
#include "namespace.h"
#include "jace/proxy/JObject.h"

#include "jni.h"

BEGIN_NAMESPACE_2(jace, ElementProxyHelper)

JACE_API void assign(const jace::proxy::JObject& element, int mIndex, jarray parent);

END_NAMESPACE_2(jace, ElementProxyHelper)

#endif
