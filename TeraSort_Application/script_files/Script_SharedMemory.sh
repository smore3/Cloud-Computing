#!/bin/bash

echo "start"
echo "Installing java"
sudo apt-get update
yes | ./hadoop.sh
sudo apt-get install default-jdk
echo "Java installed successfully."

javac FileSorter.java
#increasing heap memory to 3GB
java -Xmx3G FileSorter > log.txt

head -10 output10GB.txt >> output.txt
tail -10 output10GB.txt >> output.txt
echo "end"
