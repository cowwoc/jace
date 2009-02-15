
# A script for linking using gcc 3.0+
cd debug

rm -rf ../../../../../release/lib/linux-gcc/*.so ../../../../../release/lib/linux-gcc/*.a 2> /dev/null

CC=g++

# Flags needed for linking
#
# NOTE - These flags are provided in a particular order. This order must be
#        maintained for the code to be correctly built.
#
#  -g       = produces debugging information
#  -pthread = Support multi-threaded code
#  -fPIC    = Create relocateable code
#  -shared  = Create a shared library
#
LINKER_FLAGS="-g -pthread -fPIC -shared "

# This script expects $JAVA_HOME to be set.
# $JVM_LIB_DIR will be set to the directory containing libjvm.so
#
JVM_LIB_DIR=$JAVA_HOME/jre/lib/i386/client

echo "Linking..."

$CC \
  $LINKER_FLAGS \
  -L$JVM_LIB_DIR \
  -ljvm \
  -o ../../../../../release/lib/linux-gcc/libjace.so \
  *.o


