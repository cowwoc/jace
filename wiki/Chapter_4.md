# Chapter 4
## Peer Classes

### Peer To Peer

Jace's C++ Peers are the yin to Jace's C++ Proxies' yang. Whereas the purpose of a C++ Proxy is to provide developers with easy
access to a matching Java class from C++, the purpose of a C++ Peer is to provide developers with the capability to easily implement
the native methods of a matching Java class.

As an example of Peers, consider the Java AWT Peer classes (Frame, Button, Choice, List, etc...). Each of these Peer classes has a
corresponding native Peer class. When the Java Peer class is instantiated, it also instantiates the native Peer. When a native method
is called on the Java Peer, the method call is passed onto the native Peer. When the Java Peer is destroyed (through a call to dispose()
or a similar deallocation method), the native Peer is also destroyed.

Rather then delve deeply into the well known design pattern of Peer classes, I highly suggest that if you're not yet familiar with them,
you read Section 9 of [The Java Native Interface](http://java.sun.com/docs/books/jni/index.html).
[This JDC tech tip](http://developer.java.sun.com/developer/JDCTechTips/2001/tt0612.html#tip2) is also a good tutorial on Peer classes.
With that background out of the way, I can now explain how Jace makes it easy to implement Peer classes. You just follow this simple
recipe:

1. Write your Java Peer class as you normally would any other class. Make sure the methods that you want to implement in your native
Peer are declared as native in your Java Peer.
2. Use the tool, PeerEnhancer, to enhance your Java Peer class to include all of the boilerplate Java Peer code you'd normally have to
write by hand. The PeerEnhancer works by using [BCEL](http://jakarta.apache.org/bcel/index.html) to enhance the bytecode of your Java
Peer class file to include new code, methods, and data. Once your Java Peer class has been enhanced, it will do several new things:
    * Load your native shared library upon class initialization of the Java Peer.
    * Create and store a matching native Peer when the Java Peer is constructed.
    * Destroy the matching native Peer when the Java Peer is garbage collected or when its deallocation method is called.
3. Use the tool, PeerGenerator to generate the boilerplate native Peer code for you. The PeerGenerator will generate several files:
    * A C++ header file which contains the declaration of your native C++ Peer class. This class contains the declarations for all of
    the Java Peer's fields and methods.
    * A C++ source file which contains the Jace implementation of all of the fields and methods declared in the C++ header file - except
    for the native methods. You will implement the native methods with your own code, and you have easy access to all of the Java Peer's
    fields and methods.
    * A C++ source file which contains all of the code required to map between the JNI native method calls and calls to the C++ Peer
    class. This is how calls from the JVM get translated to calls on your native C++ Peer.
4. Implement the native methods declared in the C++ Peer header. It's trivial to implement these methods, because they are declared
totally in terms of C++ Proxy classes. Both the arguments and the return value are C++ Proxy classes. You can even throw C++ proxy
exceptions, which Jace will catch and rethrow to the JVM as real Java exceptions.
5. Use the tool, AutoProxy to generate the necessary C++ Proxy classes. It will automatically generate all C++ Proxy classes that
are required to implement your native Peer.
6. You're all done. Compile your code and take it for a spin.

----
[Previous page](Chapter 3) - [Next page](Chapter 5)
