#ifndef ORG_JACE_UNIX_VM_LOADER
#define ORG_JACE_UNIX_VM_LOADER

#include "org/jace/os_dep.h"

#ifdef JACE_GENERIC_UNIX

#include "org/jace/namespace.h"
#include "org/jace/VmLoader.h"
#include "org/jace/JNIException.h"

#include <jni.h>

#include <string>

BEGIN_NAMESPACE_2(org, jace)

/**
 * A generic Virtual Machine loader for Unix based operating systems.
 * This simple loader should work fine on most Unices.
 *
 * @author Toby Reyelts
 */
class UnixVmLoader: public ::org::jace::VmLoader
{
public:
  /**
   * Creates a new VM loader for the specified VM.
   * The VM to be loaded is specified by the path to the shared library.
   *
   * @param path - The path to the shared library implementing the VM.
   *
   * @param jniVersion - The version of JNI to use. For example, JNI_VERSION_1_2 or
   * JNI_VERSION_1_4.
   *
	 * @throws JNIException if an error occurs while loading the JVM library
   */
  JACE_API UnixVmLoader(std::string path, jint jniVersion) throw (JNIException);
	JACE_API virtual ~UnixVmLoader();

  JACE_API jint createJavaVM(JavaVM **pvm, void **env, void *args) const;
  JACE_API jint getCreatedJavaVMs(JavaVM **vmBuf, jsize bufLen, jsize *nVMs) const;

private:
  typedef jint (JNICALL *CreateJavaVM_t)(JavaVM **pvm, void **env, void *args);
  typedef jint (JNICALL *GetCreatedJavaVMs_t)(JavaVM **vmBuf, jsize bufLen, jsize *nVMs);

  CreateJavaVM_t createJavaVMPtr;
  GetCreatedJavaVMs_t getCreatedJavaVMsPtr;

  std::string path;
  void* lib;
};

END_NAMESPACE_2(org, jace)

#endif // ORG_JACE_GENERIC_UNIX

#endif // ORG_JACE_UNIX_VM_LOADER

