# A script for linking using gcc 3.0+
cd debug

rm -rf *.so *.a 2> /dev/null

CC=g++

# Flags needed for linking
#
# NOTE - These flags are provided in a particular order. This order must be
#        maintained for the code to be correctly built.
#
#  -mt     = Support multi-threaded code
#  -shared = Create a shared library
#  -fPIC   = Create relocateable code
#
LINKER_FLAGS="-g -mthread -fPIC "

# The directory containing libjvm.so
#
JACE_LIB_DIR=../../../../../../lib/linux/gcc

echo "Linking..."

$CC \
  $LINKER_FLAGS \
  -L$JACE_LIB_DIR \
  -ljaced \
  -o vm_load_example \
  *.o

