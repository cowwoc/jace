#!/bin/bash

#############################
# $1 = jace home directory  #
# $2 = output directory     #
#############################

echo "Copying dependencies to output directory..."
cp "$1/"*.so "$2/"
cp "$1/"jace-core-runtime-*.jar "$2/jace-runtime.jar"
