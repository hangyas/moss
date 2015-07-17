#!/bin/bash

MOSS=dist/moss

for i in tests/moss/*.moss; do
  filename=$(basename "$i")
  name="${filename%.*}"

  out=$($MOSS "../$i")
  expected=$(cat tests/output/$name.out)

  if [ "$out" == "$expected" ]; then
    echo -e "test $name: \t\t[OK]"
  else
    echo -e "test $name: \t\t[FAILED]"
    echo "$out"
  fi
done