import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class FileSorter extends Thread {
	static int numofFiles = 0;
	static int numofthreads;
	int id;
	int numofFilesperTh;
	static BufferedReader buffReader;
	public FileSorter(int id) {
		// TODO Auto-generated constructor stub
		this.id = id;
	}
	public void run()
	{
		createChunk();
	}
	//method to divide large file into small chunks with much limited number of records so that the file could be sorted within RAM
	public void createChunk()
	{
		ArrayList<String> lineList = new ArrayList<String>();
		FileWriter fileWrt;
		Iterator<String> iter;
		String  setRec=null;
		int numofLinesread=0;
		try
		{
			setRec = buffReader.readLine();
			while(setRec != null  )
			{
				if(lineList.contains(setRec))
				{
					continue;
				}
				else
				{
				lineList.add(setRec); //each line is from input is read by different threads so that one particular chunk is sorted by different threads simultaneously
				if(lineList.size() == 900000) //this defines the chunk size in terms of number of records
				{
					fileWrt = new FileWriter(new File("/mnt/raid/temp/temp-" +id+"-"+numofFilesperTh+".txt")); //this creates a temp file per thread
					lineList = this.sort(lineList);
					iter = lineList.iterator();
					while(iter.hasNext()) {
						fileWrt.write(iter.next()+"\n");
					}
					fileWrt.close();
					//the below would be synchronized so that all the threads rename one after the other
					synchronized (this) {
						new File("/mnt/raid/temp/temp-" +id+"-"+numofFilesperTh+".txt").renameTo(new File("/mnt/raid/temp/temp-" +numofFiles+".txt"));
						numofFiles++;
					}
					numofFilesperTh++;
					lineList.clear();
					lineList = new ArrayList<String>();
				}
				setRec = buffReader.readLine();
				numofLinesread++;
				}
			}
		}
		catch(Exception e)
		{System.out.println(e);}
		//The finally block would be executed in the case where the chunk file records are not multiples of the total number of records. Thus the remaining records would simply be put in the last chunk irrespective of the number of records
		finally
		{
			try {
					if(lineList.size() > 0)
					{
						fileWrt = new FileWriter(new File("/mnt/raid/temp/temp-" +id+numofFilesperTh+".txt"));
						lineList = this.sort(lineList);
						iter = lineList.iterator();
				//every thread then writes the data to its respective temp file						
						while(iter.hasNext()) {
							fileWrt.write(iter.next()+"\n");
						}
						synchronized (this) {
							new File("/mnt/raid/temp/temp-" +id+numofFilesperTh+".txt").renameTo(new File("/mnt/raid/temp/temp-" +numofFiles+".txt"));
							numofFiles++;
						}
						fileWrt.close();
						buffReader.close();
					}
				} catch (IOException e) {System.out.println(e);}
		}
		
	}
public static void main(String[] args)  {
		long t1 = new Date().getTime();
		String tempRec;
		int fileIter;
		Map<String, Integer> RecordMerger = new TreeMap<String, Integer>();
		BufferedReader[] buffReaderArr;
		int nextKey;
		BufferedWriter buffwriter;
		//numoflines = 10000000;
		numofthreads=2;
		
		try {
			buffReader = new BufferedReader(new FileReader(new File("/mnt/raid/temp/input10GB.txt"))); //reading input file
			buffwriter = new BufferedWriter(new FileWriter(new File("/mnt/raid/temp/output10GB.txt")));
		
		//Starting threads		
		
		FileSorter th1 = new FileSorter(0);
		FileSorter th2 = new FileSorter(1);
		FileSorter th3 = new FileSorter(2);
		FileSorter th4 = new FileSorter(3);
		//FileSorter th5 = new FileSorter(0);
		//FileSorter th6 = new FileSorter(1);
		//FileSorter th7 = new FileSorter(2);
		//FileSorter th8 = new FileSorter(3);
		th2.start();
		th1.start();
		th3.start();
		th4.start();
		//th5.start();
		//th6.start();
		//th7.start();
		//th8.start();
		th2.join();
		th1.join();
		th3.join();
		th4.join();
		//th5.join();
		//th6.join();
		//th7.join();
		//th8.join();
		//buffReader.close();

		//Merge Logic 

		buffReaderArr = new BufferedReader[numofFiles+1];
		for(fileIter=0; fileIter<numofFiles; fileIter++) {
			buffReaderArr[fileIter] = new BufferedReader(new FileReader("/mnt/raid/temp/temp-" +fileIter+".txt"));			
			RecordMerger.put(buffReaderArr[fileIter].readLine(), fileIter);
		}
		// the logic of merge is to compare the first element of all chunk files, write the least one to output file and remove it from the chunk 
		while(RecordMerger.size() >0) {
			tempRec = RecordMerger.keySet().iterator().next();
			buffwriter.write(tempRec+" \n");
			nextKey = RecordMerger.get(tempRec);
			RecordMerger.remove(tempRec); //once the least record is written to output file then it is removed from chunk file
			tempRec = buffReaderArr[nextKey].readLine();
			if(tempRec != null ) {	
				RecordMerger.put(tempRec, nextKey);
			}
		}
		buffwriter.close();
		for(fileIter=0; fileIter<buffReaderArr.length; fileIter++) {
			buffReaderArr[fileIter].close();
			new File("/mnt/raid/temp/temp-" +fileIter+".txt").delete();
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long t2 = new Date().getTime();
		System.out.println("Time taken for 1TB with  4 threads= " +(t2-t1)/1000 + " sec");
	}

//Quick sort to sort the individual chunks

public ArrayList<String> sort(ArrayList<String> list) {
    if (list.size() <= 1) 
        return list;
    int pivot = list.size()/2;
    String pivotelement = list.get(pivot);
    list.remove(pivot);
    ArrayList<String> lower = new ArrayList<String>();
    ArrayList<String> higher = new ArrayList<String>();
    for (String num : list)
        if (num.compareTo(pivotelement) < 0)
            lower.add(num);
        else
            higher.add(num);
    sort(lower);
    sort(higher);

    list.clear();
    list.addAll(lower);
    list.add(pivotelement);
    list.addAll(higher);
    return list;
}
}

