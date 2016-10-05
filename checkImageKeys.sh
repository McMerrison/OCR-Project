#!/bin/sh

touch t1 t2
cat ImageKeys/* > t1
grep -v "Demolm"[0-9][0-9]* imageKeys.txt > t2
diff t1 t2
rm t1 t2