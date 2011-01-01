
# A compilation script for gcc 3.0+ 
mkdir debug 2> /dev/null
cd debug

rm -rf *.o 2> /dev/null

CC=g++

# Flags needed for compilation
#
# NOTE - These flags are provided in a particular order. This order must be
#        maintained for the code to be correctly built.
#
#  -g       = produces debugging information
#  -pthread = Support multi-threaded code
#  -c       = Compile only
#  -Wall    = All warnings
#  -fPIC    = Create relocateable code
#
COMPILER_FLAGS="-g -pthread -c -Wall -fPIC"

# The directory containg the JDK header files.
#
JDK_INCLUDE=$JAVA_HOME/include

# The directory containing the operating system specific JDK header files.
#
JDK_OS_INCLUDE=$JDK_INCLUDE/linux

JACE_ROOT=../../..

echo "Compiling..."

$CC \
 $COMPILER_FLAGS \
 -I$JACE_ROOT/include \
 -I$JDK_INCLUDE \
 -I$JDK_OS_INCLUDE \
 $JACE_ROOT/source/jace/*.cpp  \
 $JACE_ROOT/source/jace/proxy/*.cpp  \
 $JACE_ROOT/source/jace/proxy/types/*.cpp  
