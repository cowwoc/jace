# Chapter 9
## A VM Loading Example


### Loading done your way

This section demonstrates how you can use Jace to statically or dynamically load your virtual machine. The example, `vm_load_example`
only has a single source file, `main.cpp`, which demonstrates how to load a virtual machine. As before, we'll examine the source code
line by line:

```c++
#include "jace/JNIHelper.h"

#include "jace/OptionList.h"
using jace::OptionList;
using jace::Option;
using jace::ClassPath;
using jace::Verbose;
using jace::CustomOption;

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#ifdef _WIN32
  #include "jace/Win32VmLoader.h"
  using jace::Win32VmLoader;
#else
  #include "jace/UnixVmLoader.h"
  using ::jace::UnixVmLoader;
#endif

#include <iostream>
using std::cout;
using std::endl; 
```

It is `JNIHelper.h` that contains the function, `createVm()`, necessary to load and instantiate the virtual machine. When you call
`createVm()`, you can specify the list of Options to be used in the creation of the virtual machine. All of the different Option types
are defined in `OptionList.h`. When you call `createVm()`, you must also specify a VmLoader, which has the responsibility of loading
the virtual machine library and resolving functions that Jace requires to work with the virtual machine. StaticVmLoader is the default
VmLoader, and works by statically binding to the JVM library. Win32VmLoader is able to search the registry for, and dynamically load
virtual machines on the Windows platform. UnixVmLoader is able to dynamically load virtual machines on generic Unix platforms (those
supporting `dlopen()`).

```c++
int main( int argc, char* argv[] ) {

  #ifdef JACE_WANT_DYNAMIC_LOAD

    if ( argc != 2 ) {
    cout << "Usage: vm_load_example " << endl;
      return -1;
    }

    string path = argv[ 1 ]; 
```

To turn on dynamic loading, you must globally #define `JACE_WANT_DYNAMIC_LOAD`. This keeps the StaticVmLoader from trying to statically
bind to a JVM. Here, we check to see if JACE_WANT_DYNAMIC_LOAD is defined. If it is, then we let the user specify the path to the JVM
shared library that we'll load. If it isn't, then we won't need a path, because we'll be statically linking to the virtual machine.

```c++
    #ifdef _WIN32
      Win32VmLoader loader( path, JNI_VERSION_1_2 );
    #else
      UnixVmLoader loader( path, JNI_VERSION_1_2 );
    #endif 
```

If dynamic loading is turned on, then we use a Win32VmLoader for the Win32 platform. For now, we assume that all other platforms are
Unix-like. In either case, we pass the user supplied path on to the loader.

```c++
  #else
    StaticVmLoader loader( JNI_VERSION_1_2 );
  #endif 
```

In the case that we're doing static loading, we use the StaticVmLoader. We also need to make sure that we have our linker options set
so that we are linking to the JVM library. There is no need to specify a path here.

```c++
  OptionList options;

  options.push_back( ClassPath( "." ) );
  options.push_back( Verbose( Verbose::JNI ) );
  options.push_back( Verbose( Verbose::CLASS ) );
  options.push_back( CustomOption( "-Xmx128M" ) ); 
```

Our choice of options isn't affected at all by the type of loading we perform. Here, we specify that we want a classpath set to the
current directory, we want verbose logging for JNI and class loading, and we assume that we set the max heap to 128M (assuming that
we're loading a Sun virtual machine, or some other virtual machine that supports this custom option).

```c++
  try {
    jace::helper::createVm( loader, options );
  }
  catch ( std::exception& e ) {
    cout << "Unable to create the virtual machine: " << endl;
    cout << e.what();
    return -2;
  }

  cout << "The virtual machine was successfully loaded." << endl; 
```

Finally, we create the virtual machine, specifying both the loader and the options.

### Building and running

This example doesn't require the building of any proxies or peers. Just be careful to link to the JVM library if you want to use the
StaticVmLoader or to globally #define `JACE_WANT_DYNAMIC_LOADING` if you want to use one of the dynamic VmLoaders.

----
[Previous page](Chapter_8.md) - [Next page](Chapter_10.md)
