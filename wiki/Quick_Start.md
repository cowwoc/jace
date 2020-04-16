# An overview of the Jace code-generation framework

## Overview

Jace is a toolkit designed to make it easy to write JNI-based programs. Jace consists of a C++ runtime library, and a set of tools written in Java. Because Jace is not a framework, it leaves all possible options open to the developer.

Jace's C++ runtime library provides an easy to use API that allows user to manipulate Java objects as if they were C++ objects. Behind the scenes, the runtime library automatically manages the lifetimes of Java references, the mapping of Java exceptions to C++ exceptions, and the binding of C++ threads to the JVM.

Jace's Java tools can automatically generate C++ Proxy classes and C++ Peer classes from Java classes. This means that you can easily access any Java object or implement any Java native function from your C++ code.

## What platforms does Jace run on?

Jace is based on standard C++ and JNI 1.2. Jace has been built for Windows, Solaris, HP-UX, and Linux. The only practical limitations on porting Jace to any platform are the availabilities of up to date JDKs and conforming C++ compilers.

## Where can I get Jace?

The binaries can be found on [Maven Central](https://search.maven.org/search?q=g:com.googlecode.jace).

## Can I see some sample code written using Jace?

Jace comes with some very well documented example code. Your best bet is to check out the Developer's Guide.

## Who can I ask questions of or give comments or feedback to?

Tools for the Jace user community are hosted on Bitbucket. If you go there you can browse through or post to the forums, join the mailing list, or even submit bugs or feature requests. On the other hand, you can just chat with the architect and primary author of Jace, Toby Reyelts, or co-author, Gili Tzabari directly.

----
[Developer Guide](Developer_Guide.md)
