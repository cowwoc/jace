#!/bin/bash

#############################
# $1 = build configuration  #
# $2 = jace home directory  #
# $3 = output directory     #
#############################

echo "Copying dependencies to output directory..."
cp "$2/"* "$1/$3"
cp "$2/../../../../java/target/jace-examples-peer-example1*.jar" "%3/%1/peer_example1.jar"
cp "$2/jace-core-runtime-*" "$3/$1/"