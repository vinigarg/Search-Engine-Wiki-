package merge;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import wordprocessing.*;
class Term{
    String word;
    String list;
    int doc_id;
}

class Title{
    Long ID;
    String Title;
    int doc_id;
}

class Comp implements Comparator<Term> { 
   public int compare(Term x, Term y) { 
       return x.word.compareTo(y.word);
   } 
}

class Comp2 implements Comparator<Title> { 
	   public int compare(Title x, Title y) { 
	       return x.ID.compareTo(y.ID);
	   } 
	}

public class Merging {
	
	public static void mergeTitle(int val){
		try{
		  System.out.println("Merging Titles");
		  int isnull=0;
		  long charCount=0;
		  
		  Comparator<Title> com = new Comp2();
		  PriorityQueue<Title> queue =  new PriorityQueue<Title>(val+2,com);
		  BufferedReader br2[] = new BufferedReader[val+2];
		  
		  for(int j=1;j<=val;j++)
			  br2[j] = new BufferedReader(new FileReader(Constants.indexDirec + "IdTitle"+j+".txt"));

      
		  //Initialize variables, boolean array arr to mark completeness of a text file
		  boolean[] arr = new boolean[val+2];
		  int recWritePointer=0;
      
		  File out_file = new File(Constants.indexDirec + "IdTitle.txt");
      
		  if (!out_file.exists())
			  out_file.createNewFile();
      
      
		  FileWriter fw = new FileWriter(out_file.getAbsoluteFile());
		  BufferedWriter bw = new BufferedWriter(fw);
          
		  //Secondary Index file
		  File out_file1 = new File(Constants.indexDirec + "SecTitleIndex.txt");
		  if (!out_file1.exists()) 
		  {
			  out_file1.createNewFile();
		  }
      
		  FileWriter fw1 = new FileWriter(out_file1.getAbsoluteFile());
		  BufferedWriter bw1 = new BufferedWriter(fw1);     
		  
		  
		  for(int k=1;k<=val+1;k++)	arr[k]=true;
		  int last;
		  String read;
		  for(int j=1;j<=val;j++){
			  if(arr[j]){
				  read = br2[j].readLine();
				  if(read==null){
					  isnull++;
					  arr[j]=false;    
					  br2[j].close();
				  }
				  else{
					  String ID = read.substring(0,read.indexOf(':'));
					  read=read.substring(read.indexOf(':')+1);                    
					  Title t = new Title();
					  t.Title=read;
					  t.ID= Long.parseLong(ID);
					  t.doc_id=j;
					  queue.add(t);
				  }
			  }
		  }
      
		  //Seek and Write top element
		  Title top = queue.poll();
      
		  last=top.doc_id;
		  String title = top.Title;
		  Long out_word = top.ID;
		  String lTitle=null;
		  Long lID=null;
      
		  while(isnull<val){
			  if(!arr[last]){
				  Title ltop = queue.poll();
				  last=ltop.doc_id;
				  lTitle=ltop.Title;
				  lID=ltop.ID;
              
			  }
			  else{
				  read = br2[last].readLine();
				  if(read==null){
					  isnull++;
					  arr[last]=false;    
					  br2[last].close();
                 
				  }
				  else{
					  String ID = read.substring(0,read.indexOf(':'));
					  read=read.substring(read.indexOf(':')+1);
                  
					  Title lt = new Title();
					  lt.Title=read;
					  lt.ID = Long.parseLong(ID);
					  lt.doc_id=last;
					  queue.add(lt);
					  Title ltop1 = queue.poll();
					  lTitle=ltop1.Title;
					  lID=ltop1.ID;
					  last=ltop1.doc_id;
				  }
			  
			  }
			  if(lID == out_word && lID!=0){
				  //merging 2 posting list
				  title+=lTitle;
               
			  }
			  else{                 
				  if(recWritePointer%100==0 && out_word>0){
					  bw1.write(Long.toString(out_word));
					  bw1.write(":"+charCount);
					  bw1.write("\n");
				  }
			  
				  
				  if (out_word>0){
					  bw.write(out_word+":");
					  bw.write(title);
					  bw.write("\n");
					  charCount+=Long.toString(out_word).length()+title.length()+2;
					  recWritePointer++;
				  }
				  out_word=lID;
				  title=lTitle;
				  lID=(long) 0;
				  lTitle="";
          }	
      }
		  bw.close();
		  bw1.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void mergeFiles(int val){
		try{
		  System.out.println("Merging Files");
		  int isnull=0;
		  long charCount=0;
		  
		  Comparator<Term> com = new Comp();
		  PriorityQueue<Term> queue =  new PriorityQueue<Term>(val+2,com);
		  BufferedReader br2[] = new BufferedReader[val+2];
		  
		  for(int j=1;j<=val;j++)
			  br2[j] = new BufferedReader(new FileReader(Constants.fileDirec + "file"+j+".txt"));

        
		  //Initialize variables, boolean array arr to mark completeness of a text file
		  boolean[] arr = new boolean[val+2];
		  int recWritePointer=0;
        
		  File out_file = new File(Constants.indexDirec + "Index.txt");
        
		  if (!out_file.exists())
			  out_file.createNewFile();
        
        
		  FileWriter fw = new FileWriter(out_file.getAbsoluteFile());
		  BufferedWriter bw = new BufferedWriter(fw);
            
		  //Secondary Index file
		  File out_file1 = new File(Constants.indexDirec + "SecIndex.txt");
		  if (!out_file1.exists()) 
		  {
			  out_file1.createNewFile();
		  }
        
		  FileWriter fw1 = new FileWriter(out_file1.getAbsoluteFile());
		  BufferedWriter bw1 = new BufferedWriter(fw1);     
		  
		  //Tertiary Index file
		  File out_file2 = new File(Constants.indexDirec + "TerIndex.txt");
		  if (!out_file2.exists()) 
		  {
			  out_file2.createNewFile();
		  }
        
		  FileWriter fw2 = new FileWriter(out_file2.getAbsoluteFile());
		  BufferedWriter bw2 = new BufferedWriter(fw2);     
		 
		  int lineCountSecFile = 0;
		  long charCountSecFile = 0;
		  
		  for(int k=1;k<=val+1;k++)	arr[k]=true;
		  int last;
		  String read;
		  for(int j=1;j<=val;j++){
			  if(arr[j]){
				  read = br2[j].readLine();
				  if(read==null){
					  isnull++;
					  arr[j]=false;    
					  br2[j].close();
				  }
				  else{
					  String word = read.substring(0,read.indexOf(':'));
					  read=read.substring(read.indexOf(':')+1);                    
					  Term t = new Term();
					  t.list=read;
					  t.word=word;
					  t.doc_id=j;
					  queue.add(t);
				  }
			  }
		  }
        
		  //Seek and Write top element
		  Term top = queue.poll();
        
		  last=top.doc_id;
		  String postingList = top.list, out_word = top.word;
		  String lpostingList=null, lWord=null;
        
		  while(isnull<val){
			  if(!arr[last]){
				  Term ltop = queue.poll();
				  last=ltop.doc_id;
				  lpostingList=ltop.list;
				  lWord=ltop.word;
                
			  }
			  else{
				  read = br2[last].readLine();
				  if(read==null){
					  isnull++;
					  arr[last]=false;    
					  br2[last].close();
                   
				  }
				  else{
					  String word = read.substring(0,read.indexOf(':'));
					  read=read.substring(read.indexOf(':')+1);
                    
					  Term lt = new Term();
					  lt.list=read;
					  lt.word=word;
					  lt.doc_id=last;
					  queue.add(lt);
					  Term ltop1 = queue.poll();
					  lpostingList=ltop1.list;
					  lWord=ltop1.word;
					  last=ltop1.doc_id;
				  }
			  
			  }
			  if(lWord.equals(out_word)&&out_word!=null){
				  //merging 2 posting list
				  postingList=postingList+lpostingList;
                 
			  }
			  else{                 
				  if(recWritePointer%100==0){
					  //Write into secondary index
					  if (out_word.length()>0){
						  bw1.write(out_word);
						  bw1.write(":"+charCount);
						  bw1.write("\n");
					  
						  charCountSecFile+=out_word.length() + 1 + Long.toString(charCount).length()+1;
						  if (lineCountSecFile%10 == 0){
							  bw2.write(out_word);
							  bw2.write(":"+charCountSecFile);
							  bw2.write("\n");
						  }
						  
						  lineCountSecFile++;
					  }
				  } 
				  
				  if (out_word.length()>0){
					  postingList =  ranking(postingList);
					  bw.write(out_word+":");
					  bw.write(postingList);
					  bw.write("\n");
					  charCount+=out_word.length()+postingList.length()+2;
					  recWritePointer++;
					  
				  }
				  
				  out_word=lWord;
				  postingList=lpostingList;
				  lWord="";
				  lpostingList="";
            }	
        }
		  bw.close();
		  bw1.close();
		  bw2.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/*
	*	Using TF-IDF Method
	*/
	public static String ranking(String postingList){
		String res = "";
		
		TreeMap<Long, ArrayList<String> >posting = new TreeMap<Long, ArrayList<String> >(Collections.reverseOrder());
		
		if (postingList.length()==0) return res;
		String recs[] = postingList.split(";");
		for (int i=0; i< recs.length; i++){
			String r = recs[i].split("-")[1];
			long tf = 0;

			int bValue=0, cValue=0, eValue=0, gValue=0, iValue=0, tValue=0, rValue=0;
			char field='A';
			for (int j=0; j< r.length(); j++){
				char c = r.charAt(j);
				if (c>='A' && c<='Z'){
					field = c;
				}else{
					if (field == 'B'){
						bValue  = bValue *10 + (c-'0');
						
					}else if(field == 'C'){
						cValue  = cValue *10 + (c-'0');
						
					}else if(field == 'E'){
						eValue  = eValue *10 + (c-'0');
						
					}else if(field == 'G'){
						gValue  = gValue *10 + (c-'0');
						
					}else if(field == 'I'){
						iValue  = iValue *10 + (c-'0');
						
					}else if(field == 'R'){
						rValue  = rValue *10 + (c-'0');
						
					}else if(field == 'T'){
						tValue  = tValue *10 + (c-'0');
						
					}
				}
			}

			tf = (bValue*Constants.BODY_WEIGHT) + (cValue*Constants.CATEGORY_WEIGHT) + 
				 (eValue*Constants.EXTERNAL_REF_WEIGHT) + (gValue*Constants.GEOBOX_WEIGHT)+
				 (tValue*Constants.TITLE_WEIGHT) + (rValue*Constants.REFERENCE_WEIGHT);

			ArrayList<String> t = null;
			if (posting.containsKey(tf)){
				t  = posting.get(tf);
			}else{
				t = new ArrayList<String>();
			}
			t.add(recs[i]);
			posting.put(tf, t);
			
		}
		return serialise(posting);		
	}
	
	public static String serialise( TreeMap<Long, ArrayList<String> > record){
		StringBuilder res = new StringBuilder();
		for (Map.Entry<Long, ArrayList<String> > e : record.entrySet())
			for (int i=0; i<e.getValue().size(); i++)
				res.append(e.getValue().get(i)+";");
		return res.toString();
	}	
}