Jace 1.2.18

This package includes the following:

Definitions
-----------

1) %MAVEN_HOME% refers to the installation directory of Apache Maven
2) %JACE_HOME% refers to the installation directory of Jace


Install Maven
-------------

1) Download Maven 3.0.4 from http://maven.apache.org/download.html
2) Extract Maven into %MAVEN_HOME% and add %MAVEN_HOME%\bin to the PATH environment variable


Building Jace
-------------

1) Open %JACE_HOME% in a terminal
2) Run "mvn help:all-profiles" to list available build platforms
3) Run "mvn install -P<platform>" where <platform> is one of the platforms listed in the previous step
4) The JAR file generated in %JACE_HOME%/core/cpp/target contains the C++ libraries and include files.
   The JAR file generated in %JACE_HOME%/core/java/target contains the Java libraries.
   Example code is generated in %JACE_HOME%/examples/<name>/target.


I need your support! If you can help developing Jace, please contact me at cowwoc@bbs.darktech.org
