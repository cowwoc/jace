#include "org/jace/JArrayHelper.h"

using org::jace::JClass;
using org::jace::proxy::types::JInt;

BEGIN_NAMESPACE_3(org, jace, JArrayHelper)

// Returns a local ref
jobjectArray newArray(int size, const JClass& elementClass)
{
  JNIEnv* env = attach();
  jobject obj = env->NewObjectArray(size, elementClass.getClass(), 0);
  jobjectArray array = static_cast<jobjectArray>(obj);
  catchAndThrow();

  return array;
}


JInt getLength(jobject obj)
{
  jarray array = static_cast<jarray>(obj);
  jsize size = attach()->GetArrayLength(array);
  return JInt(size);
}

// Returns a local ref
jvalue getElement(jobject obj, int index)
{
  jobjectArray thisArray = static_cast<jobjectArray>(obj);
  jobject object = attach()->GetObjectArrayElement(thisArray, index);
  jvalue value;
  value.l = object;
  return value;
}

END_NAMESPACE_3(org, jace, JArrayHelper)
