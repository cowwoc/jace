# Chapter 11
## Arrays in Action


### Arrays and Iterators

This example demonstrates the usage of Java arrays from C++. Like most of the other examples, this example, `array_example` only has
a single source file, `array_example.cpp`, which contains the code we'll be examining:

```c++
#define JACE_CHECK_ARRAYS 
```

Jace has an optional array checking mechanism that you can turn on by #defining JACE_CHECK_ARRAYS in your code. This must be turned
on or off for your entire project. When JACE_CHECK_ARRAYS is turned on, Jace checks for out of bounds indices and iterators.

```c++
#include "jace/JNIHelper.h"

#include "jace/proxy/types/JInt.h"
using jace::proxy::types::JInt;

#include "jace/proxy/java/lang/String.h"
using jace::proxy::java::lang::String;

#include "jace/proxy/java/lang/Integer.h"
using jace::proxy::java::lang::Integer;

#include "jace/proxy/java/lang/Object.h"
using jace::proxy::java::lang::Object;

#include "jace/JArray.h"
using jace::JArray;

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JNIException.h"
using jace::JNIException;

#include <string>
using std::string;

#include <exception>
using std::exception;

#include <iostream>
using std::cout;
using std::endl;

#include <algorithm>
using std::for_each;

#include <functional>
using std::unary_function;
```

The new thing to notice here is the include of the algorithm and functional headers. Jace arrays can be used as Standard C++
compliant containers with random access iterators.

```c++
struct print : public unary_function {
  void operator()( Object obj ) {
  cout << "[print] " << obj << endl;
  }
}; 
```

Here, we're just defining a functor that prints its parameter, for later use with for_each.

```c++
int main() {

  try {
    // Standard Vm setup
    StaticVmLoader loader( JNI_VERSION_1_2 );
    OptionList list;
    list.push_back( jace::CustomOption( "-Xcheck:jni" ) );
    list.push_back( jace::CustomOption( "-Xmx16M" ) );
    jace::helper::createVm( loader, list, false );

    typedef JArray<String> StringArray; 

Same old, same old along with a convenience typedef.

    // Creates a new array of Java String with 1000 null elements
    StringArray strArray( 1000 );

    // Fills the array with hello strings.
    for ( int i = 0; i < strArray.length(); ++i ) {
      strArray[ i ] = "Hello " + String::valueOf( JInt( i ) );
    }

    // Prints the contents of the array.
    // You can use JArray::operator[] for random access,
    for ( int j = 0; j < strArray.length(); ++j ) {
      cout << strArray[ j ] << endl;
    } 
```

Whenever you create a new JArray, the array members get initialized the same way they do in Java, with the default value for the
element type. Once we create the array, we assign each element a new String value. Note that we're using the assignment operator with
the array index operator. Also note that we can retrieve the length of an array using JArray::length(). The length is cached, so it's
not expensive to retrieve often.

```c++
    // Traverse the array again, but this time with JArray::Iterator
    // JArray::Iterator is preferred for non-random access,
    // because it allows Jace to perform smart caching.
    for ( StringArray::Iterator it = strArray.begin(); it != strArray.end(); ++it ) {
      *it = *it + " again";
      cout << *it << endl;
    } 
```

JArray is similar to std::vector in that it allows random access through both an index operator and through a nested iterator class.
JArray::Iterator is random access like vector::iterator, but it has some additional semantics attached to it. Because of the interface
of an iterator, Jace can make some assumptions about caching that aren't as easy to make through an index operator. Users can also
supply hints to the iterator to indicate, for example, how many elements are being iterated through. Therefore, the preferred means of
accessing a JArray is through JArray::Iterator.

```c++
    // JArray::Iterator conforms to the Standard C++ Library's concept
    // of a random access iterator, and can be used that way.
    for_each( strArray.begin(), strArray.end(), print() ); 
```

A demonstration of the use of a JArray as a standard C++ container using a standard C++ algorithm.

```c++
    // Demonstrate some more random access iterator usage
    for ( StringArray::Iterator it2 = strArray.begin(); it2 < strArray.end() - 2; ) {
      it2 += 2;
      cout << "Forward two: " << *it2 << endl;
      it2 -= 1;
      cout << "Back one: " << *it2 << endl;
    } 
```

Just some more random access iteration.

```c++
    // As stated above, Jace can check for bad array access
    // Here, we demonstrate some array access checking
    #ifdef JACE_CHECK_ARRAYS

      try {
        StringArray::Iterator it = strArray.begin( strArray.length() + 1 );
      }
      catch ( JNIException& e ) {
        cout << "Caught a bad iterator construction:" << endl;
        cout << e.what() << endl;
      }

      try {
        StringArray::Iterator it = strArray.end();
        ++it;
      }
      catch ( JNIException& e ) {
        cout << "Caught a bad iterator advancement." << endl;
        cout << e.what() << endl;
      }

      try {
        StringArray::Iterator it = strArray.begin();
        --it;
      }
      catch ( JNIException& e ) {
        cout << "Caught a bad iterator rewind." << endl;
        cout << e.what() << endl;
      }
 
      try {
        cout << strArray[ strArray.length() ] << endl;
      }
      catch ( JNIException& e ) {
        cout << "Caught a bad array index." << endl;
        cout << e.what() << endl;
      }
    #endif 
```

Here we actually demonstrate Jace's handling of bad array accesses, both through iterators and through the index operator.

```c++
 }
  catch ( exception& e ) {
  cout << e.what() << endl;
    return -1;
  }

  return 0;
} 
```

Exception handling as usual.

### Building and running

Like the other examples, you can build this example by running ANT on `build.xml`. Other than having the JVM in your library path,
there are no special requirements for running this example.

----
[Previous page](Chapter_10.md)
