# Building under Ubuntu 10.04

* Install Maven3
    * Binaries: http://maven.apache.org/download.html
    * Instructions: http://maven.apache.org/download.html#Unix-based_Operating_Systems_Linux_Solaris_and_Mac_OS_X
    * Setting persistent environment variables: https://help.ubuntu.com/community/EnvironmentVariables#Persistent_environment_variables
* To clean a previous build, run `mvn -P<profile> clean:clean`
* To build binaries, run `mvn -P<profile> install`
    * Example: `mvn -Plinux-i386-gcc-debug install`
* The following build profiles are available:
    * linux-i386-gcc-debug
    * linux-i386-gcc-release
    * linux-amd64-gcc-debug
    * linux-amd64-gcc-release
* You're done!
