#!/bin/bash

rm -f 1000*

java -jar target/random-1.0-SNAPSHOT-shaded.jar 10 1000 1000_edges.txt
java -jar target/random-1.0-SNAPSHOT-shaded.jar 100 10000 10000_edges.txt
java -jar target/random-1.0-SNAPSHOT-shaded.jar 1000 100000 100000_edges.txt
java -jar target/random-1.0-SNAPSHOT-shaded.jar 10000 1000000 1000000_edges.txt
java -jar target/random-1.0-SNAPSHOT-shaded.jar 100000 10000000 10000000_edges.txt
java -jar target/random-1.0-SNAPSHOT-shaded.jar 1000000 100000000 100000000_edges.txt
