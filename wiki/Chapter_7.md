# Chapter 7
## Hello World


### A simple example

The most tried and true method of explaining concepts is through demonstration, which is what the following chapters are all about.
In this first section, I'm going to explain how `example1` works. First, we'll examine, `example1.cpp` line by line. We begin with
the #include's:

```c++
#include "jace/JNIHelper.h"

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JArray.h"
using jace::JArray;

#include "jace/JNIException.h"
using jace::JNIException;

#include "jace/proxy/java/lang/String.h"
#include "jace/proxy/java/lang/System.h"
#include "jace/proxy/java/io/PrintWriter.h"
#include "jace/proxy/java/io/IOException.h"
#include "jace/proxy/java/io/PrintStream.h"

using namespace jace::proxy::java::lang;
using namespace jace::proxy::java::io;

#include <string>
using std::string;

#include <exception>
using std::exception;

#include <iostream>
using std::cout;
using std::endl;
`

All generated Jace Proxies are placed into the jace::proxy namespace under their Java package. For example, as can be seen above, the
C++ Proxy for `java.lang.String` is located in the jace::proxy::java::lang namespace. Since this code will also be making use of the
`java.lang.System` and `java.io.PrintWriter` Java classes, it also includes their C++ Proxies. Jace can also generate Proxies for Java
exception classes. In this case, we're #including IOException, because we need to catch it if an exception is thrown. Following that,
we #include `JNIHelper.h` which declares the utility functions that the JRL makes available for developers. If you examine,
`JNIHelper.h` you will see that the utility functions are declared in the jace::helper namespace. To create a virtual machine, we need
to specify a loader and virtual machine options, so we #include `StaticVmLoader.h` and `OptionList.h` Finally, we #include the string,
exception, and iostream classes for use in this example.

```c++
int main() {

  try {
    StaticVmLoader loader( JNI_VERSION_1_2 );
    OptionList list;
    list.push_back( jace::CustomOption( "-Xcheck:jni" ) );
    list.push_back( jace::CustomOption( "-Xmx16M" ) );
    jace::helper::createVm( loader, list, false );
```

Here we define the standard C++ main() function. The very first step we take is to create a new Java Virtual Machine using the helper
function, `createVM()`. For this example, we statically link to the JVM library, so we need to use the `StaticVmLoader`. Here we also
demonstrate the use of `CustomOptions` to turn on extra JNI checking and a 16M heap limit. If we were to create the virtual machine on
our own by not using `createVm()`, we would need to make sure to call `jace::helper::setVmLoader()` so that Jace is able to locate the
virtual machine.

```c++
    for ( int i = 0; i < 1000; ++i ) { 
```

We run this code in a for loop to demonstrate that Jace is managing local references. Jace programs can run indefinitely, because it
automatically deletes local references making objects available for garbage collection.

```c++
    String s1 = "Hello World";
    cout << s1 << endl; 
```

The first line creates a new live `java.lang.String` object in the JVM. This line demonstrates that you can create a String from a C++
`char*`. The second line prints the String out to the console. The String class overloads operator<<() to write the result of
toString() to the output stream. In fact, you can use operator<<() on any Jace Proxy class to write it out to an output stream.

```c++
    String s2 = std::string( "Hello World" );
    cout << s2 << endl; 
```

These two lines of code do the exact same thing, only this time, the String is being constructed by a `std::string` instead of a
`char*`. You can get a std::string from a String by calling `String::operator` `std::string()`. Take a look at `String.h` to see all
of the ways that you can use String Proxies with C++ strings.

```c++
    String s3( "Hello World" );
    PrintStream out( System::out() );
    out.println( s3 );
```

Guess what this code does... You've got it - It also prints out "Hello World". This time, though, it uses Java's `System.out` static
PrintStream member to print to standard out. With Jace, you can access class fields by calling a member function of the same name.
In this case, we want to access System.out, so we call `System::out()`.

```c++
    PrintWriter writer( System::out() );
    writer.println( "Hello World" ); 
    writer.flush(); 
```

I bet you would have never imagined there were so many different ways to do "Hello World". In this final example, we create a new
PrintWriter, again using `System.out`. Also notice how the `char*`, "Hello World", is automatically converted to a `java.lang.String`
- without any effort on our part.

```c++
     cout << i << endl;
    }

    return 0;
  }
  catch ( IOException& ioe ) {
    cout << ioe << endl;
    return -1;
  } 
```

If the JVM throws an `IOException` during any of this code, the JRL will automatically catch it, clear the pending exception, locate a
matching C++ Proxy class, and throw it. Jace Proxy exceptions are just like any other Jace Proxy class, except that they ultimately
derive from std::exception in addition to Object.

```c++
  catch ( JNIException& jniException ) {
    cout << "An unexpected JNI error occured." << jniException.what() << endl;
    return -2;
  } 
```

If any kind of JNI exception occurs during execution, the JRL detects it and throws a JNIException.

```c++
  catch ( std::exception& e ) {
    cout << "An unexpected C++ error occured." << e.what() << endl;
    return -3;
  } 
```

Finally, every good C++ developer is going to make sure that no uncaught exception escapes his program.
Generating the Proxies

You'll notice that example1 makes use of many C++ Proxies. You could run the ProxyGenerator for each and every C++ proxy you need to
generate. That can become very tedious, though. Especially considering that you'll need to also generate all of the dependee classes.
(For example, `IOException` depends upon Exception, so you would also have to generate Exception). To avoid all of this hassle, you can
use the AutoProxy. This wonderful little utility searches through your C++ header and source files looking for #includes of C++ Proxies.
It then generates all of those Proxies and all of their dependent Proxies. I generated all of the Proxies for example1 (in the Proxies
directory) by running

```
  autoproxy 
    C:\data\projects\jace\release\examples\example1\include 
    C:\data\projects\jace\release\examples\example1\source 
    C:\data\projects\jace\release\examples\example1\proxies\include 
    C:\data\projects\jace\release\examples\example1\proxies\source 
    C:\java\jdk1.4\jre\lib\rt.jar
    -mindep 
```

Well, actually, I just have an ANT script, build.xml which does that for me.

This tells the AutoProxy to recursively scan the `example1\include` and `example1\source` directories for proxy #includes, to generate
the C++ headers and source files to proxies\include and proxies\source, and to use the JDK's rt.jar as the classpath to search for
class definitions for the Proxies.

### Building and running

In order to build example1, you'll need to adjust the Makefile settings so that the include and library directories point to your
installation of the JDK. After that, you can run the build and generate the example1 program for your own platform. To run example1,
you will need the JVM library to be in your path (for example, put jvm.dll in your PATH on Windows, or libjvm.so in your
LD_LIBRARY_PATH on Linux or Solaris). If everything runs correctly, you'll get the output:

```
    Hello World
    Hello World
    Hello World
    Hello World 
```

Generally speaking, whenever you build with the Jace library, you'll need to enable RTTI and multithreading for your compiler. For
VC++ release build in particular, `jace.lib` was built using the "Multithread DLL" options. You'll have to use those exact same
options when you link with those libraries.

----
[Previous page](Chapter_6.md) - [Next page](Chapter_8.md)
