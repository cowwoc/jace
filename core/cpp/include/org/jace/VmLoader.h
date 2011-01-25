#ifndef ORG_JACE_VM_LOADER
#define ORG_JACE_VM_LOADER

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/JNIException.h"

#include <jni.h>

BEGIN_NAMESPACE_2(org, jace)

/**
 * The base interface for virtual machine loaders.
 * 
 * To create a virtual machine using org::jace::createVm(),
 * you need to specify a VmLoader.
 *
 * The default VmLoader is DefaultVmLoader. This loader statically links
 * to the VM, and thus works on every platform, although it limits your choice
 * to the virtual machine you compiled and linked against.
 *
 * In order to dynamically load a virtual machine, you must #define
 * JACE_WANT_DYNAMIC_LOAD. This keeps DefaultVmLoader from trying to statically
 * link with a VM. Then, you must instantiate your platform specific VmLoader.
 *
 * For Windows, you can use Win32VmLoader. It is capable of querying the registry
 * to discover different flavors of installed virtual machines.
 *
 * For generic flavors of Unix, you can use UnixVmLoader. It uses the standard
 * dlsym function to load a virtual machine.
 *
 * You may also subclass VmLoader for yourself if your platform isn't supported
 * or if you want tighter control over the loading and unloading process.
 *
 * @author Toby Reyelts
 */
class VmLoader
{
public:
	/**
	 * Creates a new VmLoader.
	 *
	 * @param jniVersion the JNI version the JVM must support
	 */
	JACE_API VmLoader(jint jniVersion);

  /**
   * Returns the the JNI version the JVM must support.
   */
  JACE_API virtual jint getJniVersion() const;

  /**
   * Invokes JNI_CreateJavaVM.
   */
  JACE_API virtual jint createJavaVM(JavaVM** pvm, void** env, void* args) const = 0;

  /**
   * Invokes JNI_GetCreatedJavaVMs.
   */
  JACE_API virtual jint getCreatedJavaVMs(JavaVM** vmBuf, jsize bufLen, jsize* nVMs) const = 0;

	JACE_API virtual ~VmLoader() {}
private:
	/**
	 * Prevent copying.
	 */
	VmLoader& operator=(VmLoader&);
	const jint jniVersion;
};

END_NAMESPACE_2(org, jace)

#endif // ORG_JACE_VM_LOADER
