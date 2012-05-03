#!/bin/bash

#################################
# $1 = build configuration      #
# $2 = boost library directory  #
# $3 = output directory         #
#################################

echo "Copying dependencies to output directory..."
cp "$2/"* "$1/$3"
