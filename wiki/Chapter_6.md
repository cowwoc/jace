# Chapter 6
## Tools

### When all you know how to use is a hammer...

Jace comes with several tools which have already been mentioned in passing. This chapter documents each tool and its options in
greater detail.

### ProxyGenerator

**Usage**: `ProxyGenerator <class file> <header | source> [options]`

Where **options** can be:

  * `-protected`: Generate protected fields and members.
  * `-package`: Generate package fields and methods.
  * `-private`: Generate private fields and methods.

The ProxyGenerator generates the C++ Proxy class for a single Java class. You can specify whether it generates the header or the
source file to standard output. You can also specify at which access level the ProxyGenerator generates member fields and methods.
By default, the ProxyGenerator only generates public members. Normally, developers should prefer using the AutoProxy in preference
to the ProxyGenerator, as the AutoProxy will walk the dependency tree to generate all dependee classes.


### AutoProxy

**Usage**: `AutoProxy`

  `<list of c++ header directories>`

  `<list of c++ source directories>`

  `<destination proxy header directory>`

  `<destination proxy source directory>`

  `<java classpath for proxies>`

  `[options]`

Where **options** can be:
  * `-mindep`
  * `-extraDependencies=<comma-separated list of classes>`
  * `-exportsymbols`

This tool scans c++ header files and source files for #includes that reference Jace C++ Proxies. It then generates the header and
source files for the entire dependency tree for those C++ Proxies. For example, upon seeing an #include for
**jace/proxy/java/lang/RuntimeException.h**, AutoProxy would generate the Proxy classes for RuntimeException, Throwable, Object,
Serializable, String, etc. AutoProxy is also used in conjunction with other tools like BatchGenerator and PeerGenerator. You can use
the recommended new option, "-mindep" to tell Jace to only generate methods for classes that you #include. For example, if you never
#include **jace/proxy/java/lang/String.h** then methods like java.lang.Integer.toString() will be omitted because they return a String.
You can also optionally specify an additional list of unused classes for AutoProxy to process with "-extraDependencies" (useful for
libraries where the full dependency list isn't known ahead of time). Finally, you can use "-exportsymbols" if the proxies are compiled
into a DLL/SO and you want their symbols exported. If "exportsymbols" is true then the JACE_PROXY_EXPORTS precompiler symbol must be
defined in the library exporting the proxies.


### BatchGenerator

**Usage**: `BatchGenerator`

  `<jar or zip file containing classes>`

  `<destination directory for header files>`

  `<destination directory for source files>`

  `[options]`

Where **options** can be:

  * `-protected` : Generate protected fields and methods.
  * `-package` : Generate package fields and methods.
  * `-private` : Generate private fields and methods.


The BatchGenerator is used to generate all of the Proxy C++ classes inside of a jar file. This tool is useful when you are trying to
create a C++ API for an existing Java API. You simply jar up all of your Java classes and then run BatchGenerator on the jar file.
You will also want to run AutoProxy on the resulting C++ Proxy classes, so that all dependee C++ proxy classes are generated.


### PeerEnhancer

**Usage**: `PeerEnhancer`

  `<input file>`

  `<output file>`

  `<comma-separated list of libraries>`

  `[options]`

Where **options** can be:

  * `-deallocator=<deallocation method>`
  * `-verbose`


The PeerEnhancer is used to enhance the bytecode of Java Peer classes with automatic lifetime management code for native C++ Peers.
The **source path** is the path to the class file to be enhanced. The **output path** is the path where the new class file will be
written (it is recommended that these not be the same). **libraries** are the comma-separated names of the native libraries which the
newly enhanced Java Peer will try to load in its static initializer. **deallocation method** is the name of an (already existing)
resource deallocation method (for example, "close" or "dispose"), which will be enhanced to also deallocate the native C++ Peer.
**-verbose** indicates that Java peers should output library names before loading them.


### BatchEnhancer

**Usage**: `BatchEnhancer`

  `<list of sources>`

  `<comma-separated list of libraries>`

  `[options]`

  Where **options** can be:
  * `-deallocator=<deallocation method>`
  * `-verbose`

The BatchEnhancer runs the PeerEnhancer on multiple sources in a single run.


### PeerGenerator

**Usage**: `PeerGenerator`

  `<class file>`

  `<destination_header_directory>`

  `<destination_source_directory>`

  `<user_defined_members = {true|false}>`

The PeerGenerator generates the C++ header file and source code required to implement the native C++ Peer for a Java Peer. The class
file is the path to the Java Peer's class file. The destination_header_directory is the directory where the header containing the
declaration of the C++ Peer class will be written. The destination_source_directory is the directory where the C++ source code for the
JNI mappings and the C++ Peer implementation will be written. If you set user_defined_members to true, the C++ header file will
#include an additional user header file, &lt;peer_class_name&gt;_user.h, where you can include any additional data or function
members which you might require to implement the C++ Peer. For example, you may wish to override Peer::initialize() or Peer::destroy()
in this header file.


### Ant Integration

```xml
<property environment="env"/>
<taskdef resource="org/jace/ant/task.properties">
  <classpath>
    <fileset dir="${env.JACE_HOME}/release/lib" includes="**/*.jar"/>
  </classpath>
</taskdef>

<JavaPeerUptodate inputFile="input.class" outputFile="output.class" property="java.peer.skip"/>

<EnhanceJavaPeer inputFile="input.class" outputFile="output.class" deallocationMethod="dispose">
  <library name="browser"/>
  <library name="tray"/>
</EnhanceJavaPeer>

<CppPeerUptodate inputFile="input.class" outputHeaders="output/include" outputSources="output/source"
property="cpp.peer.skip"/>

<GenerateCppPeer file="peer.class" outputHeaders="output/include" outputSources="output/source" userDefinedMembers="false"/>

<GenerateCppProxies inputHeaders="input/include" inputSources="input/source" outputHeaders="output/include"
 outputSources="output/source" exportSymbols="false" minimizeDependencies="true" classpath="rt.jar" accessibility="PUBLIC">
  <classpath>
    <pathelement location="classes"/>
  </classpath>
  <dependency name="forced.dependency.class"/>
  <inputHeaders dir="input">
    <include name="include1"/>
    <include name="include2"/>
  </inputHeaders>
  <inputSources dir="input">
    <include name="source1"/>
    <include name="source2"/>
  </inputSources>
</GenerateCppProxies>
```

**JavaPeerUptodate** sets a property if a Java peer is up to date.

**EnhanceJavaPeer** enhances a single Java peer.

**CppPeerUptodate** sets a property if a C++ peer is up to date.

**GenerateCppPeer** generates a C++ peer for a Java class.

**GenerateCppProxies** generates C++ proxies for Java classes. accessibility refers to the method **accessibility** to expose.

### Maven Integration

```xml
<project>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>com.googlecode.jace</groupId>
        <artifactId>jace-maven-plugin</artifactId>
        <version>1.2.18</version>
        <executions>
          <execution>
            <id>enhance-classes</id>
            <goals>
              <goal>enhance-java</goal>
            </goals>
            <configuration>
              <inputFile>
                <!-- The path of the input class file -->
              </inputFile>

              <outputFile>
                <!-- The path of the output class file -->
              </outputFile>

              <deallocationMethod>
                <!-- The Java method used to deallocate the Java peer -->
              </deallocationMethod>

              <!-- Indicates if Java peers should output library names before loading them -->
              <verbose>false</verbose>

              <!-- The native libraries to load before initializing the Java peer -->
              <libraries>
                <library>first</library>
                <library>second</library>
              </libraries>
            </configuration>            
          </execution>

          <execution>
            <id>generate-peer</id>
            <goals>
              <goal>generate-cpp-peer</goal>
            </goals>
            <configuration>
              <!-- The Java class to generate a peer for -->
              <classFile>${project.build.outputDirectory}/foo/Bar.class</classFile>

              <!-- The directory of the output header files -->
             <outputHeaders>${project.build.directory}/include</outputHeaders>

              <!-- The directory of the output source files -->
             <outputSources>${project.build.directory}/include</outputSources>

              <!-- Indicates if {peer_class_name}_user.h should be generated in order for allow users to declare additional peer methods -->
              <userDefinedMembers>false</userDefinedMembers>
            </configuration>            
          </execution>

          <execution>
            <id>generate-proxies</id>
            <goals>
              <goal>generate-cpp-proxies</goal>
            </goals>
            <configuration>
              <!-- Directories containing the input header files -->
              <inputHeaders>
                <path>${project.build.sourceDirectory}/include</path>
              </inputHeaders>

              <!-- Directories containing the input source files -->
              <inputSources>
                <path>${project.build.sourceDirectory}/source</path>
              </inputSources>

              <outputHeaders>
                <!-- The directory of the output header files -->
              </outputHeaders>

              <outputSources>
                <!-- The directory of the output source files -->
              </outputSources>

              <!-- The path to search for Java classes -->
              <classpath>
                <path>first</path>
                <path>second</path>
              </classpath>

              <!-- The method accessibility to expose. Acceptable values include: PUBLIC, PROTECTED, PACKAGE or PRIVATE. -->
              <accessibility>
                PUBLIC
              </accessibility>

              <!-- Indicates if the proxy symbols should be exported (for generating DLLs/SOs) -->
              <exportSymbols>false</exportSymbols>

              <!-- Indicates whether classes should be exported even if they are not referenced by the input files -->
              <minimizeDependencies>true</minimizeDependencies>
            </configuration>            
          </execution>
        </executions>
      </plugin>
    </plugins>
    ...
  </build>
  ...
</project>
```

----
[Previous page](Chapter_5.md) - [Next page](Chapter_7.md)
