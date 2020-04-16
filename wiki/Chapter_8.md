# Chapter 8
## Hello Peer


### Moving onto Peers

This section covers a demonstration of the peer capabilities of Jace. The example, `peer_example1`, contains a single Java Peer class
`PeerExample.java` which, as you can see, looks like any other Java class. It has one native method, `getResources()`, that we implement
with a Jace C++ Peer. The first step in implementing the Peer is to compile and enhance `PeerExample.class`. If you examine
`build.xml` you can see that after compiling PeerExample, we run the PeerEnhancer on the `PeerExample.class` and place the enhanced Java
Peer class into the enhanced folder. Next, we run the PeerGenerator on the enhanced PeerExample.class. The PeerGenerator generates:

* The header file containing the declaration for the C++ Peer class - `PeerExample.h` (All peers are placed under the `jace::peer`
namespace - Just like proxies are placed under the `jace::proxy` namespace).
* The mapping file containing the code that maps JNI function calls to the C++ Peer's methods - `PeerExampleMappings.cpp` and
* The source file containing the implementation for all of the members of the C++ Peer class - `PeerExample.cpp`. This file does not
contain the native methods which must be implemented by the developer.

Next, we would actually implement the native methods. In this case, it's already been done in `PeerExampleImpl.cpp`. Next, we run the
AutoProxy on the generated files and our own source code, so that all of the necessary C++ Proxy classes are created. Finally, we
compile and build all of the source code.

### Building and running

You can use ant to both build and run this example.

----
[Previous page](Chapter_7.md) - [Next page](Chapter_9.md)
