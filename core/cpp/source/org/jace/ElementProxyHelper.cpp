#include "org/jace/ElementProxyHelper.h"

#include "org/jace/Jace.h"
using ::org::jace::proxy::JObject;

BEGIN_NAMESPACE_3(org, jace, ElementProxyHelper)

void assign(const JObject& element, int index, jarray parent)
{
  JNIEnv* env = attach();
  jobjectArray array = static_cast<jobjectArray>(parent);
  env->SetObjectArrayElement(array, index, element);
}

END_NAMESPACE_3(org, jace, ElementProxyHelper)
