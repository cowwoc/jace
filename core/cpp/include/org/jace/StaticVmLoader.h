#ifndef ORG_JACE_STATIC_VM_LOADER
#define ORG_JACE_STATIC_VM_LOADER

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/JNIException.h"
#include "org/jace/VmLoader.h"

#include <jni.h>

BEGIN_NAMESPACE_2(org, jace)

/**
 * The default VmLoader which works by statically linking to the JVM.
 * To turn off the StaticVmLoader, you must #define JACE_WANT_DYNAMIC_LOAD
 *
 * StaticVmLoader is totally inlined so that the Jace library can be built
 * orthogonally in reference to dynamic or static VM linking. If StaticVmLoader
 * wasn't defined within the header file, two different binaries of the Jace library
 * would be required.
 *
 * @author Toby Reyelts
 */
class StaticVmLoader: public ::org::jace::VmLoader
{
public:
  StaticVmLoader(jint jniVersion):
		VmLoader(jniVersion)
	{}

  virtual jint getCreatedJavaVMs(JavaVM **vmBuf, jsize bufLen, jsize *nVMs) const
	{
    // Prevent static linking if the user intends on dynamically loading 
    #ifndef JACE_WANT_DYNAMIC_LOAD
      return JNI_GetCreatedJavaVMs(vmBuf, bufLen, nVMs);
    #else
      return -1;
    #endif
  }	

  virtual jint createJavaVM(JavaVM **pvm, void **env, void *args) const
	{
    // Prevent static linking if the user intends on dynamically loading
    #ifndef JACE_WANT_DYNAMIC_LOAD
      return JNI_CreateJavaVM(pvm, env, args);
    #else
      return -1;
    #endif
  }
};

END_NAMESPACE_2(org, jace)

#endif // ORG_JACE_STATIC_VM_LOADER
