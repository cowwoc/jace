#!/bin/bash

#################################
# $1 = boost library directory  #
# $2 = output directory         #
#################################

echo "Copying dependencies to output directory..."
cp "$1/"*.so "$2/"
