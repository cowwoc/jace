# Chapter 10
## A Mapping Example


### Using Maps

This example demonstrates the usage of a Java Map from C++. This example doesn't introduce a lot of new concepts. It just enforces the
existing examples, and draws attention to a few interesting details. Like most of the other examples, this example, `map_example` only
has a single source file, `main.cpp`, which contains the code we'll be examining:

```c++
include "jace/JNIHelper.h"
using jace::OptionList;

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/proxy/java/util/Set.h"
using jace::proxy::java::util::Set;

#include "jace/proxy/java/lang/System.h"
using jace::proxy::java::lang::System;

#include "jace/proxy/java/lang/Object.h"
using ::jace::proxy::java::lang::Object;

#include "jace/proxy/java/lang/Integer.h"
using jace::proxy::java::lang::Integer;

#include "jace/proxy/java/lang/String.h"
using jace::proxy::java::lang::String;

#include "jace/proxy/java/util/Map.h"
using jace::proxy::java::util::Map;

#include "jace/proxy/java/util/HashMap.h"
using jace::proxy::java::util::HashMap;

#include "jace/proxy/java/util/Map.Entry.h"
using jace::proxy::java::util::Map_Entry;

#include "jace/proxy/java/util/Iterator.h"
using jace::proxy::java::util::Iterator;

#include "jace/javacast.h"
using jace::java_cast;

#include <vector>
using std::vector;

#include <iostream>
using std::cout;
using std::endl; 
```

By now, you should be familiar with all of these #includes. However, the include and using directive for `Map_Entry` should catch your
attention. Map_Entry is actually an inner class, Entry, for the outer class java.util.Map. In Java notation, it is referred to as
java.util.Map$Entry. When Jace generates the proxies for nested classes, it generates them as normal C++ classes. Because, '$'
characters are illegal in C++ identifiers, Jace translates all '$' characters to '_' characters. So, for example, a nested, nested
class, Foo$Bar$Baz, would be generated as Foo_Bar_Baz. However, when naming the files, Jace uses '.' characters instead of '$'
characters or '_' characters. This works well, because '$' characters are typically illegal in file names, and the use of '.'
characters instead of '_' characters helps Jace to distinguish between an outer class named Foo_Bar and a nested class named Foo$Bar.

```c++
int main() {

  try {
    StaticVmLoader loader( JNI_VERSION_1_2 );
    jace::helper::createVm( loader, OptionList(), false );

    for ( int i = 0; i < 1000; ++i ) { 
```

As before, we run this code in a loop to demonstrate that Jace manages references in all situations.

```c++
    Map map = jace::java_new<HashMap>(); 
```

Nothing new here. Following good coding style guidelines, we declare our variables to be of an interface type, rather than a concrete
type.

```c++
    map.put( Integer( "1" ), String( "Hello 1" ) );
    map.put( Integer( "2" ), String( "Hello 2" ) );
    map.put( Integer( "3" ), String( "Hello 3" ) ); 
```

Here, we're just adding three entries to the Map. It's necessary to explicitly specify Integer and String, because there is no
meaningful conversion from int or char* to jace::proxy::java::lang::Object - the argument types of Map.put.

```c++
    Set entrySet( map.entrySet() );

    for ( Iterator it( entrySet.iterator() ); it.hasNext(); ) {
      Map_Entry entry = jace::java_cast( it.next() );
      Integer key = jace::java_cast<Integer>( entry.getKey() );
      String value = jace::java_cast<String>( entry.getValue() );
      cout << "key: <" << key << "> value: <" << value << ">" << endl;
    } 
```

We're just iterating through and printing out the Map's entrySet here. Normally, in Java code, you would type-cast from the return
value of it.next() to Map_Entry, but you can't just use C style casts to perform casting of Java objects. Rather, to execute a Java
'type-cast' using Jace, you use the java_cast<> template function. You can use java_cast to cast between two Java types, or between a
Java type and a JNI handle. If the cast fails, java_cast will throw a JNIException. You can also use the instanceof<> template function
in the same way you'd use the instanceof operator in Java to determine if it is safe to perform a cast.

```c++
  }
  catch ( std::exception& e ) {
    cout << e.what() << endl;
    return -1;
  }

  return 0;
} 
```

As always, we make sure to catch any exceptions that might occur.

### Building and running

Like the other examples, you can build this example by running ANT on build.xml. Other than having the JVM in your library path, there
are no special requirements for running this example.

----
[Previous page](Chapter_9.md) - [Next page](Chapter_11.md)
