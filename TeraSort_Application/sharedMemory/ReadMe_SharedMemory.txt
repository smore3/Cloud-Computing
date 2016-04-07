1. Create input file using gensort command and save it on a location of /mnt/raid/input1TB.txt
2. change the directory to where the input file is saved
3. Run the script SharedMemory.sh which would then generate the output and store it in log.txt
4. (optional) if the script file does not run then install java manually using sudo apt-get install default-jdk and then compile and run the code. In this case the output will be displayed on the console.

Methods used:

1. createChunk() :This method divides the input file into number of chunks and then sort it individually using the quick sort.
2. The main file implements the merge logic where the chunks are compared by traversing the first elements and then least is written to the output file. This record is then deleted from the chunk

