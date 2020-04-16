# Chapter 5
## Virtual Machine Loading


### Dynamic Statics

Jace provides several options for virtual machine loading. You can create a virtual machine from your C++ code by calling
jace::helper::createVm() found in JNIHelper.h. There, you can specify a VmLoader, and a list of generic or virtual machine
specific options.

To statically load a virtual machine, you must

* statically link with jvm.lib
* use StaticVmLoader in your call to createVm().

To dynamically load a virtual machine, you must

* not statically link with jvm.lib
* globally #define JACE_WANT_DYNAMIC_LOAD (**Preprocessor Definitions** for VisualStudio users). This prevents StaticVmLoader from
  trying to statically bind with jvm.lib.
* use an appropriate dynamic VmLoader for your platform - for example, Win32VmLoader or UnixVmLoader. You may also write your own
dynamic VmLoader if you so choose.

Whether or not you statically or dynamically load your virtual machine, you provide options to it via the same mechanism. You
specify the entire set of virtual machine options in an OptionList which you pass in to the call to createVm(). Finally, your
application must include **jace-runtime.jar** in its classpath at runtime.

----
[Previous page](Chapter_4.md) - [Next page](Chapter_6.md)
