#!/bin/bash
cd src/
find -iname "*.java" | xargs clang-format -i
cd ../nim
find -iname "*.nim" | xargs nimpretty