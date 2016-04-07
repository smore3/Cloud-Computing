1. Create input file on master instance using the gensort command and save it as input.txt
2. transfer the jar file (HadoopAssignment.jar) to master
3. copy the input file to hdfs using command:
hdfs dfs -copyFromLocal /mnt/raid/input.txt /input
4. after starting the hadoop run the following command to start the sorting application
   hadoop jar HadoopAssignment.jar HadoopAssignment /input /output
5. The output file is saved in output folder.   
