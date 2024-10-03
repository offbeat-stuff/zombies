#!/bin/bash
cd src/
# find -iname "*.java" | xargs clang-format -i
find -iname "*.java" | xargs google-java-format -i #--fix-imports-only -i
cd ../nim
find -iname "*.nim" | xargs nimpretty