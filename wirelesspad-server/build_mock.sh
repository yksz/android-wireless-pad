#!/bin/sh

if [ ! -e build ] ; then
    mkdir build
fi
cd build

cmake -DMOCK=ON -DLWS_WITH_SSL=OFF ..
make
