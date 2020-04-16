# Chapter 1
## Before You Begin

### Getting Started

If you're interested in learning how to develop with Jace, you've come to the right place, but first things first. If you've never used JNI before, you're going to be absolutely lost. No class library or set of tools can be an adequate substitute for the knowledge that you'll gain by reading through the freely available Java Native Interface Programmer's Guide and Java Native Interface Specification. Your time will have been well spent.

### The Jace License

Jace is made available under the BSD license, which roughly means that you're free to do whatever you want to with it. Here's the fine print:

> Copyright (c) 2002, Toby Reyelts
> All rights reserved.
>
> Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:
> 
> Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
Neither the name of Toby Reyelts nor the names of his contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.
> 
> THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

### Compatibility

You've probably already got a development platform in mind, or perhaps two or three. Jace is excellent for cross-platform development, because it's built using only the standard C++ library and a JDK 1.4 or later compiler. Here's a matrix of all of the known Jace compatible compilers, operating systems, and architectures. If your favorite compiler/os/arch isn't listed, download the source and give the build a try. You're likely to experience a pleasant surprise.

|             | Windows | Linux | MaxOSX |
|-------------|---------|-------|--------|
| Visual C++  |    X    |       |        |
| GCC         |    X    |   X   |    X   |

### Source-Code

Jace uses a [[http://tortoisehg.bitbucket.org/|Mercurial]] source-code repository. If you haven't already, you may check out the source-code from [[https://bitbucket.org/cowwoc/jace/src|https:~//bitbucket.org/cowwoc/jace/src]]

This is what you should see:

The root folder contains all of the binaries and tools you need to use Jace. It consists of the following folders:

* bin - The proxygen, batchgen, autoproxy, and peergen code-generation tools.
* docs - The documentation necessary to use Jace - including what you're staring at right now.
* examples - Several example programs used to demonstrate the features available in Jace.

This folder also contains the source used to build the JRL (Java Runtime Library) and code-generation tools. This is where you can go to build your own version of the JRL if Jace didn't come with pre-built binaries for your platform. The source directory has the following folders:

* core/cpp/src/main/cpp - Jace's C++ source and header files.
* core/java/src/main/java - Jace's Java source code.

### Binaries

You don't need to build Jace yourself. Simply locate a version at [Maven Central](https://search.maven.org/search?q=g:com.googlecode.jace) and download the following components:

* [jace-core-java.jar](http://search.maven.org/remotecontent?filepath=com/googlecode/jace/jace-core-java/1.2.14/jace-core-java-1.2.14.jar) contains Ant tasks and command-line tools for generating C++ peers.
* [jace-core-cpp.jar](https://search.maven.org/artifact/com.googlecode.jace/jace-core-cpp/1.2.14/jar) contains C++ header files and libraries to link against.
  * Add [jace-runtime.jar](http://search.maven.org/remotecontent?filepath=com/googlecode/jace/jace-runtime/1.2.14/jace-runtime-1.2.14.jar) to your Java classpath.

### Contributing

If you want to contribute back to this project, please see [Building Instructions](Building_Instructions.md) to get started.

----
[Previous page](Developer_Guide.md) - [Next page](Chapter_2.md)
