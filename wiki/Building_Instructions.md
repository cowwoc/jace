# Building Jace

* Install Maven3
    ** Binaries: http://maven.apache.org/download.html
* To clean a previous build, run `mvn -P<profile> clean:clean`
* To build binaries, run `mvn -P<profile> install`
    ** Example: `mvn -Plinux-i386-gcc-debug install`
* The following build profiles are available:
    ** windows-i386-vs10-debug
    ** windows-i386-vs10-release
    ** windows-amd64-vs10-debug
    ** windows-amd64-vs10-release
    ** linux-i386-gcc-debug
    ** linux-i386-gcc-release
    ** linux-amd64-gcc-debug
    ** linux-amd64-gcc-release
* You're done!

See [Building for Ubuntu](Building_for_Ubuntu.md) for Ubuntu-specific instructions.
