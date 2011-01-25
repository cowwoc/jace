#include "org/jace/JFieldProxyHelper.h"

using org::jace::proxy::JObject;
using org::jace::JClass;

#include "org/jace/Jace.h"

BEGIN_NAMESPACE_3(org, jace, JFieldProxyHelper)


jobject assign(const JObject& field, jobject parent, jfieldID fieldID)
{
  JNIEnv* env = attach();
  jobject object = static_cast<jobject>(field);
  env->SetObjectField(parent, fieldID, object);
  return object;
}

jobject assign(const JObject& field, jclass parentClass, jfieldID fieldID)
{
  JNIEnv* env = attach();
  jobject object = static_cast<jobject>(field);
  env->SetStaticObjectField(parentClass, fieldID, object);
  return object;
}


END_NAMESPACE_3(org, jace, JFieldProxyHelper)
