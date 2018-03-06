#!/bin/bash
echo “protoc 编译”

cd ./protoc/src/main/java/com
protoc --java_out=./ *.proto