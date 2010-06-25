
#include "jace/Peer.h"

#include <string>
using std::string;


BEGIN_NAMESPACE(jace)

Peer::Peer(jobject obj)
{
  JNIEnv* env = helper::attach();
  weakRef = env->NewWeakGlobalRef(obj);

  if (!weakRef)
	{
    string msg = "Unable to allocate a new weak reference for a Peer.";
    throw JNIException(msg);
  }
}

Peer::~Peer()
{
  JNIEnv* env = helper::attach();
  env->DeleteWeakGlobalRef(weakRef);
}

void Peer::initialize()
{
}
  
void Peer::destroy()
{
}

jobject Peer::getGlobalRef()
{
  JNIEnv* env = helper::attach();
  jobject ref = env->NewGlobalRef(weakRef);

  if (!ref)
	{
    throw JNIException("Unable to allocate a new global reference from a weak reference.\n"
      "It is likely that the weak reference is no longer valid.");
  }
  return ref;
}

void Peer::releaseGlobalRef(jobject ref)
{
  JNIEnv* env = helper::attach();
  helper::deleteGlobalRef(env, ref);
}
 
END_NAMESPACE(jace)
