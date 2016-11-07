#!/bin/bash

ndk-build

cp -r libs/* jniLibs/
rm -r libs/*
zip -9 -r -v obj.zip obj/
