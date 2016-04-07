
import java.util.*; 
import java.io.IOException; 
import org.apache.hadoop.fs.Path;  
import org.apache.hadoop.io.*; 
import org.apache.hadoop.mapred.*;

public class HadoopAssignment 
{ 
   @SuppressWarnings("deprecation")
public static class FileSortMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text> 
   {  
      public void map(LongWritable key, Text value,OutputCollector<Text, Text> op,Reporter r) throws IOException 
      { 
         String [] lines = value.toString().split("\n"); 
         for(int i=0;i<lines.length;i++)
         {
        	 op.collect(new Text(lines[i].substring(0, 9)), new Text(lines[i].substring(10)));
         }
          
      } 
   } 
   //Reducer class 
   @SuppressWarnings("deprecation")
public static class FileSortReducer extends MapReduceBase implements Reducer< Text, Text, Text, Text > 
   {  
      //Reduce function 
      public void reduce( Text key, Iterator <Text> values,OutputCollector<Text, Text> op, Reporter r) throws IOException 
         { 
    	  Text val=null;
    	  while(values.hasNext())
    	  {
    		  val = values.next();
    	  }
          op.collect(key,val);  
 
         } 
   }   
   @SuppressWarnings("deprecation")
public static void main(String args[])throws Exception 
   { 
      JobConf MapReduceJob = new JobConf(HadoopAssignment.class); 
      MapReduceJob.setJobName("HadoopAssignment"); 
      MapReduceJob.setOutputKeyClass(Text.class);
      MapReduceJob.setOutputValueClass(Text.class); 
      MapReduceJob.setMapperClass(FileSortMapper.class); 
      MapReduceJob.setCombinerClass(FileSortReducer.class); 
      MapReduceJob.setReducerClass(FileSortReducer.class); 
      MapReduceJob.setInputFormat(TextInputFormat.class); 
      MapReduceJob.setOutputFormat(TextOutputFormat.class); 
      FileInputFormat.setInputPaths(MapReduceJob, new Path(args[0])); 
      FileOutputFormat.setOutputPath(MapReduceJob, new Path(args[1])); 
      JobClient.runJob(MapReduceJob); 
   } 
} 
