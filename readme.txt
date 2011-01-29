Jace 1.1.2

This package includes the following:

1) The binary Jace release, which includes

  - Documentation - in release\docs
  - Header files for the Jace library - in release\include
  - Debug and release binaries for the Jace library for a few platforms - in release\lib
  - Jace generation tools in release\bin, including 
      PeerGenerator,
      PeerEnhancer,
      BatchEnhancer,
      AutoProxy, 
      ProxyGenenerator,
      BatchGenerator

  - Example applications using Jace - in release\examples

2) The source used to build the Jace release, which includes

  - The C++ source used to build the core Jace library - in source\c++
  - The Java source used to build the Jace generation tools - in source\java

For questions about Jace, visit http://sourceforge.net/projects/jace/


Definitions
-----------

1) %MAVEN_HOME% refers to the installation directory of Apache Maven
2) %JACE_HOME% refers to the installation directory of Jace


Install Maven
-------------

1) Download Maven 2.2.1 from http://maven.apache.org/download.html
2) Extract Maven into %MAVEN_HOME% and add %MAVEN_HOME%\bin to the system path
3) Copy or merge %JACE_HOME%/settings.xml into %USERPROFILE%/.m2/settings.xml


Building Jace
-------------

1) Open %JACE_HOME%/core in a terminal
2) Run "mvn help:all-profiles" to list available build platforms
3) Run "mvn install -P<platform>" where <platform> is one of the platforms listed in step 3
4) The JAR file generated in %JACE_HOME%/core/cpp/target contains the C++ libraries and include files.
   The JAR file generated in %JACE_HOME%/core/java/target contains the Java libraries.




-------------- Future releases

- Plan on fixing the Solaris link bug
- Plan on adding a new "autopeer" capability (easy-subclassing)
- Plan on adding new support for generics

I need your support! If you can help developing Jace, please contact me at cowwoc@bbs.darktech.org

-------------- New changes in 1.1.2 (pending)

- Jace now depends on Boost Threads. Pre-built Boost binaries are included in the release/lib directory
  but you must download the Boost header files separately from http://www.boost.org/users/download/
  Please set the BOOST_HOME environment variable to the directory containing the Boost source-code.
- Added EnhancePeer, PeerUptodate, GenerateProxies, GeneratePeer ant integration tasks
- Added JNIHelper::attach(const jobject threadGroup, const char* name, const bool daemon)
- AutoProxy now provides more information if a C++ file references a non-existent Java class
- Regression: ProxyGenerator was omitting #include files
- Regression: ProxyGenerator.getDependentClasses() was returning super classes and interfaces contrary to its contract
- PeerGenerator was missing forward-declarations of used classes
- A PeerEnhancer bug would sometimes trigger "VerifyError: Unable to pop operand off an empty stack" errors; fixed.
- Invoking C++ peer methods from within a Java Peer constructor would crash; fixed.
- PeerGenerator was generating the wrong signature for jaceSetVm; fixed.
- Jace was referring to JNIException using an unqualified name, causing conflicts if
  users used the same name; fixed.
- jace::toVector( const JArguments& arguments ) was referred before being declared; fixed.
- Patch #1370101: referencing JFieldProxy<JArray<JByte>> would cause a compiler error
- Patch #1351326: finalize() should be "protected" and throw "Throwable"
- Bug #996630, Patch #1194480: PeerGenerator does not mangle method name
- Patch #1106710: -deplist doesn't work
- Patch #1095109: Allow CMD script to run from any directory
- Patch #1037408: Multiple source/header directory support for AutoProxy
- Nested classes weren't being processed properly; fixed.
- Added Peer.getJavaProxy(), allowing navigation from a Peer object to its Proxy.
- AutoProxy and GenerateCppProxies now allow you to specify the method accessibility to expose.
- Fixed crashes that occurred because some internal methods were catching JNIException instead of std::exception.
- OptionList::Verbose is now more consistent with other options, only accepting a single option at a time.
- Fixed UnsatisfiedLinkError caused by PeerGenerator generating the wrong signature for jaceDestroyInstance().
- It is now possible to convert between java::lang::String and std::wstring.
- Renamed library directories to from win32 and x64 to i386 and amd64 respectively.
- Added jace::helper::toString(T) and jace::helper::toWString(T) to convert any type to a std::string or
  std::wstring respectively.
- Bug #1222146: JValue::staticGetJavaJniClass() is not thread-safe.
- PeerEnhancer was stripping exceptions from the deallocation method; fixed.
- ByteClass.unproxy() would always fail; fixed.
- ArrayMetaClass.proxy()/unproxy() would fail for multi-dimensional arrays; fixed.
- Removed logback.xml because according to the author logging should only be configured by applications, not libraries.
- Added new preprocessor symbols JACE_LINK_STATICALLY and JACE_PROXIES_LINK_STATICALLY for when developers wish to
	link against Jace or C++ proxies statically (dynamic linking is the default).
- staticGetJavaJniClass() now initializes class names on demand
- JEnlister now only generated for exception classes
- staticGetJavaJniClass() was not acquiring locks properly (contributed by Otakar Leopold).
- Fixed template specialization warnings under GCC (contributed by Otakar Leopold).
- "JObject::~JObject - Unable to delete the global ref" is no longer printed on JVM shutdown
- Jace now throws a more specific exception (VirtualMachineShutdownError instead of JNIException) if a method is
  invoked after the JVM has already shut down.
- jaceCreateInstance() would crash if the JVM was not created by Jace; fixed.
- PeerEnhancer was generating invalid bytecode (INVOKEVIRTUAL instead of INVOKESPECIAL for private methods); fixed.
- Enum ordinal values are now accessible from C++.

Java: Fruits.APPLE.ordinal()
C++ : Fruits::Ordinals::APPLE

- Native threads attached to the JVM are now assigned the following name: "NativeThread-<id>" where <id>
  denotes the native thread id.


Compatibility breakers:

- Jace now requires Java 1.5 (previously was 1.4)
- BatchGenerator, ProxyGenerator take command-line options -public, -protected, -package, -private.
  In the past these used to refer to keeping only the specific accessibility level but now they
  refer to keeping any items having that accessibility or higher. For example, -package now
  refers to keeping public, protected and package accessibility.
- The BatchEnhancer, PeerEnhancer command-line syntax has changed
  
- In the past Jace always enhanced the Peer finalizer to deallocate the C++ peer. As of version 1.1.2,
  the peer finalizer is only enhanced if:
  
  1) The Java peer already defines a finalizer method, or
  2) The user does not specify the Java Peer dispose method to be enhanced
  
- Class Verbose now takes Verbose::ComponentType as input instead of String
- Dropped support for old versions of Visual C++. We now only support Visual Studio 2008.
- Renamed package jace.peergen to jace.peer, jace.proxygen to jace.proxy, jace.autoproxy to jace.proxy
- The BatchGenerator, ProxyGenerator and ClassSet constructor signatures have changed (added ClassPath parameter)
- Removed LibraryProxies which doesn't seem to be used anywhere
- Replaced jace::helper::shutdown() by jace::helper::destroyVm() and jace::helper::isShutdown() by jace::helper::isRunning().
  jace::helper::destroyVm() invokes DestroyJavaVM() whereas jace::helper::shutdown() did not.
- Replaced jace::helper::getVmLoader(), setVmLoader() by setJavaVM()
- Removed VmLoader::loadVm(), unloadVm(), clone(). The loader constructor now loads the JVM library and the destructor
  unloads it.
- Renamed VmLoader::version() to getJniVersion()
- jace::helper::registerShutdownHook() is now private
- Removed WrapperVmLoader as it was replaced by:

JNIEnv* env = jace::attach();
JavaVM* jvm;
env->GetJavaVM(&jvm);
jace::setJavaVM(jvm)

- Refactored C++ proxy constructors so that Map() creates a Java reference while Map::Factory::create() creates
  a Java object.
  
  Beware! "Map map = HashMap();" or "Map map;" now assigns a null pointer to "map". The code should be refactored as:
  "Map map = jace::java_new<HashMap>()"
- Replaced getJavaJniObject() and getJavaJniValue() by "operator jobject()" and "operator jvalue()" respectively
- Proxy(jobject) constructors are now public (but marked as explicit)
- JBoolean.getBoolean() replaced by JBoolean::operator jboolean()
- JByte.getByte() replaced by JByte::operator jbyte()
- JChar.getChar() replaced by JChar::operator jchar()
- JDouble.getDouble() replaced by JDouble::operator jdouble()
- JFloat.getFloat() replaced by JFloat::operator jfloat()
- JInt.getInt() replaced by JInt::operator jint()
- JShort.getShort() replaced by JShort::operator jshort()
- JLong.getLong() replaced by JLong::operator jlong()
- %JACE_HOME% should now point to /jace/trunk, not /jace/trunk/release

-------------- New changes in 1.1.1

- AutoProxy and BatchEnhancer did not generate new proxies unless the target filename already existed
- Upgraded to ASM 3.1 and Retroweaver 2.0.6

-------------- New changes in 1.1
- Jace will register a shutdown hook jace.util.ShutdownHook
  to prevent interaction with the JVM after it has shutdown (otherwise we get a core-dump). If you register your own
  shutdown hooks that use Jace, you should invoke "Runtime.removeShutdownHook(jace.util.ShutdownHook.getInstance());"
  on startup and "jace.util.ShutdownHook.getInstance().run();" at the end of your shutdown hook.
  
- Upgraded to Retroweaver 1.2.2. You must now include retroweaver-rt-1.2.2.jar in your classpath.

So in summary, when using Jace to generate proxies or peers you must include jace-1.3.jar and
retroweaver-rt-1.2.2.jar in your classpath. And at runtime, your application must include jace-runtime.jar in its classpath.

-------------- New changes in 1.1rc1_05

- Applications must now include jace-runtime.jar in their classpath.  
- Fixed some doc bugs
- ProxyGenerator no longer overwrites existing files (so the C++ compiler doesn't end up recompiling unchanged files)
- Added JNIHelper::setClassLoader() which allows one to specify which ClassLoader Jace should use. If you use
  Java Webstart you must use this method to tell Jace to use the special Webstart classloader.
- Fixed crash if java.lang.Exception is never included by the application but JNIException is caught
  (this was caused by the fact that jenlister did not contain java.lang.Exception)
- [797422] Fixed JArray compilation error
- [892227] template specialization in javacast.tsd
- [921351] AutoProxy -deplist switch does not work
- [943053] Inconsistent CRLF on PeerGenerator output; fixed
- [999579] jni FindClass bug, affects JClassImpl::getClass; fixed
- [860380] AutoProxy generation bug on array
- [860384] C++ namespace limitation
- [868480] autoproxy does not filter files in source or header dirs
- [1012595] Need to remove dead code from "specialized-away" ElementProxy methods that are causing compilation errors
- [868564] Scripts require cwd to be $JACEDIR/release/bin


-------------- Changes since 1.1b3

- Fixed many, many, many bugs, including all those listed in SourceForge, and many not listed in SourceForge.
- Added local reference management. Now you can run your programs forever without leaking any memory or resorting to Push/PopLocalFrame, DeleteLocalRef, or DetachCurrentThread calls.
- Added a new -mindep option to AutoProxy. With this new option, AutoProxy now only generates the bare minimum set of classes for your code to correctly compile. This reduces the number of classes generated to 1/10th the original on average. For example, map_example now contains 7 auto-generated classes, down from 75.
- Added guard checking for faster compilation. Now Jace builds in 20 seconds, and all of the shipped examples build in under 30 seconds.
- Added JACE_CHECK_NULLS - You can turn this on to get exceptions when you dereference null Java objects. See the new array example.
- Added JACE_CHECK_ARRAYS - You can turn this on to get exceptions when you do silly things with arrays. See the new array example.
- Added java_cast and instanceof template functions that act like their Java counterparts. Check out javacast.h and the map_example. These functions type-safely replace JObject.getJavaJniObject(), JObject( jobject ), and JObject( jvalue ).
- Added STL conforming random access iterators to JArray. See the new array example.
- Fixed support for VC++.NET (now works out of the box with VC++7.0 and VC++7.1)
- Fixed support for gcc 3.2 (now works out of the box)
- Added support for Comeau
- Added two new examples - array_example and peer_singleton
- Added a new BatchEnhancer utility
- Updated documentation - Also in PDF
- Added explicit support for Sun's Forte C++ compiler again, including some work arounds for broken namespace behavior
and template template member functions (at least on the older 5.2 compiler)
- Fixed some JArray::Interface post increment operator issues
- Changed the PeerGenerator so that peer source files are named differently to prevent typical object file name conflicts when the Peer and Proxy object files are compiled to the same folder.
(This fixes the "unresolved linker error" with gcc that was occuring in 1.1rc1_03).
- Added a new performance test example. It gives a basic idea of the small overhead involved when using Jace. Can be used in the future to track down hotspots. You can also test the relative performance of different compilers.

-------------- Release Notes

- Jace Peers incur unresolved C++ standard library linker errors at runtime with the Sun Forte C++ compiler. (This problem does not exist with any of the other compilers). Users are encouraged to help me track this behavior down.
- Although Jace works with VC++6.0, users are encouraged to use more recent versions of VC++ to get better performance and conformance. 

