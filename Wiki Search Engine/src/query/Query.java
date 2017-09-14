package query;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import wordprocessing.Constants;
import wordprocessing.Stemming;
import wordprocessing.Words;

class Term {
	String word;
	long offset;
	
	Term(String w, long o){
		this.offset = o;
		this.word = w;
	}
	
}

class Title {
	long id;
	long offset;
	
	Title(long id, long o){
		this.offset = o;
		this.id = id;
	}
	
}


public class Query {
	private static ArrayList<Title> idTitleArrayList = null;
	
	public static void main(String args[]) {
		// Load stop words and initialize stemmer
		Constants.words = new Words();
		Constants.words.initiallize();
		Constants.stem = new Stemming();

		try {
			// read tertiary inverted index in tertiaryArrayList
			BufferedReader terInvHandle = new BufferedReader(new FileReader(Constants.indexDirec + "TerIndex.txt"));
			ArrayList<Term> tertiaryArrayList = new ArrayList<Term>();
	        Comparator<Term> c = new Comparator<Term>(){
	        	public int compare(Term u1, Term u2){
	        		return u1.word.compareTo(u2.word);
	        	}
	        };
	        
	        String line = terInvHandle.readLine();
	        
	        while (line != null){
	        	String w = line.substring(0, line.indexOf(":"));
	        	long o = Long.parseLong(line.substring(line.indexOf(":")+1));
	        	Term t = new Term(w, o);
	        	tertiaryArrayList.add(t);
	        	line = terInvHandle.readLine();
	        }
	        terInvHandle.close();
	        
	        // read secondary id-title ampping into idTitleArrayList
	        BufferedReader idTitleHandle = new BufferedReader(new FileReader(Constants.indexDirec + "SecTitleIndex2.txt"));
			idTitleArrayList = new ArrayList<Title>();
	        Comparator<Title> c2 = new Comparator<Title>(){
	        	public int compare(Title u1, Title u2){
	        		return (int) ((int) u1.id - u2.id);
	        	}
	        };
	        
	        line = idTitleHandle.readLine();
	        
	        while (line != null){
	        	long id = Long.parseLong(line.substring(0, line.indexOf(":")));
	        	long o = Long.parseLong(line.substring(line.indexOf(":")+1));
	        	Title t = new Title(id, o);
	        	idTitleArrayList.add(t);
	        	line = idTitleHandle.readLine();
	        }
	        idTitleHandle.close();	        
	        
	        /*
	         *  read the input file path from where to read the input queries
	         */
	        BufferedReader qHandle = new BufferedReader(new InputStreamReader(System.in));
	        
	        System.out.println("Enter the number of queries :");

	        // Number of queries to process
	        int queryCount = Integer.parseInt(qHandle.readLine());
	        
	        while (queryCount-- > 0){

	        	String query = qHandle.readLine();
	        	
	        	query = query.replaceAll("!@#$%+^&;*'.><","");
	        	
	        	long startTime = System.currentTimeMillis();
	        	
	        	// tokenizer is faster than split
	        	StringTokenizer token = new StringTokenizer(query, " ");
	        	
	        	HashMap<Long, Long> DocScore = new HashMap<Long, Long> ();
	        	
	        	while (token.hasMoreTokens()){
	        		String tok = token.nextToken().toLowerCase();
	        		
	        		/*
	        		 * Field Query Check, and push up the field , if query is a field query [by a factor of 1000]
	        		 */
	        		int lbcount=2,ltcount=10000,lecount=1,lccount=30,licount=25;
	        		//Check For Field Queries
	        		if(tok.contains("t:")){
	        			ltcount*=1500;
	        			tok=tok.substring(tok.indexOf(":")+1);
	                      
	        		}
	        		if(tok.contains("b:")||tok.contains("r:")){
	        			lbcount*=1000;
	        			tok=tok.substring(tok.indexOf(":")+1);
                      
	        		}
	        		if(tok.contains("e:")){
	        			lecount*=1000;
	        			tok=tok.substring(tok.indexOf(":")+1);
                      
	        		}
	        		if(tok.contains("c:")){
	        			lccount*=1000;
	        			tok=tok.substring(tok.indexOf(":")+1);
                      
	        		}
	        		if(tok.contains("i:")){
	        			licount*=1000;
	        			tok=tok.substring(tok.indexOf(":")+1);
                      
	        		}
	        		tok=tok.trim();
	        		
	        		/*
	        		 * Stop word removal
	        		 */
	        		if (Constants.words.stopWords.contains(tok)){
	        			continue;
	        		}
	        		
	        		/*
	        		 * Stemming
	        		 */
	        		Constants.stem.add(tok.toCharArray(), tok.length());
	        		if (Constants.stem.stem(tok)!=null){
	    				tok = Constants.stem.stem(tok);
	    			}
	    		
	        		/*
	        		 * Search in tertiary file using binary search on tertiaryArrayList 
	        		 */
	        		long startIndex = search(tertiaryArrayList, tok, c);
	        		
	        		/*
	        		 * Search in secondary file using binary search on secArrayList 
	        		 */
	        		RandomAccessFile secFile = new RandomAccessFile(Constants.indexDirec+"SecIndex.txt", "r");
	                secFile.seek(startIndex);
	                ArrayList<Term> secArrayList  = new ArrayList<Term>();
	                line = secFile.readLine();
	                for (int i=1;i<=200 && line!=null; i++){
	                	String w = line.substring(0, line.indexOf(":"));
	                	long o = Long.parseLong(line.substring(line.indexOf(":")+1));
	    	        	Term t = new Term(w, o);
	    	        	secArrayList.add(t);
	    	        	line = secFile.readLine();
	                	
	                }
	        		startIndex = search(secArrayList, tok, c);
	                secArrayList.clear();
	        		secFile.close();
	                
	                /*
	        		 * Search in primary index file and retrieving posting list if present  
	        		 */
	        		RandomAccessFile indexFile = new RandomAccessFile(Constants.indexDirec+"Index.txt", "r");
	        		indexFile.seek(startIndex);
	                line = indexFile.readLine();
	                String postingList = null;
	                
	                for (int i=1;i<=200 && line!=null; i++){
	                	String w = line.substring(0, line.indexOf(":"));
	                	if (w.equals(tok)){
	    	        		postingList = line.substring(line.indexOf(":")+1);
	    	        		break;
	    	        	}
	    	        	line = indexFile.readLine();
	                }
	                if (postingList!=null){
	                	DocScore = wordDocScoring(DocScore, postingList, lbcount, ltcount, lecount, lccount, licount);
	                	
	                }
	                indexFile.close();
	                
	        	}
        	    /*
                 * query processing
                 */
	        	Set<Entry<Long, Long>> set = DocScore.entrySet();
	            List<Entry<Long, Long>> list = new ArrayList<Entry<Long, Long>>(set);
	            Collections.sort( list, new Comparator<Map.Entry<Long, Long>>()
	            {
	                public int compare( Map.Entry<Long, Long> o1, Map.Entry<Long, Long> o2 )
	                {
	                    return (int) (o2.getValue() - o1.getValue());
	                }
	            });
	        	
	        	for (int i=0; i<10 && i<list.size(); i++){
	                System.out.println(mapIDTitle(list.get(i).getKey(), c2));
	        	}
                System.out.println("Response time : " +(System.currentTimeMillis() - startTime) + " milli sec");
	        }
	        
	       
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private static String mapIDTitle(Long key, Comparator<Title> c ) throws Exception {
		/*
		 * Search in primary title file and retrieving title for given ID = key  
		 */
		Long startIndex = search(idTitleArrayList, key, c);
		
		RandomAccessFile file = new RandomAccessFile(Constants.indexDirec+"IdTitle.txt", "r");
		
		file.seek(startIndex);
		
		String line = file.readLine();
		String title = "";
		
		for (int i=1;i<=200 && line!=null ; i++){
			line=file.readLine();
			Long w = Long.parseLong(line.substring(0, line.indexOf(":")));
        	if (w.longValue() == key.longValue()){
        		title = line.substring(line.indexOf(":")+1);
        		break;
        	}
        }
		file.close();
		return title;
	}

	private static long search(ArrayList<Title> list, Long tok, Comparator<Title> c ){
		long tokIndex = Collections.binarySearch(list, new Title(tok, 0), c);
		long startIndex;
		if(tokIndex<0){
			tokIndex*=-1;
			if(tokIndex>2)
				startIndex = list.get((int) (tokIndex - 2)).offset;
			else 
				startIndex=0;
		}
		else if(tokIndex>3)
			startIndex= list.get((int) (tokIndex - 3)).offset;
		else
			startIndex=0;
		
        return startIndex;
	}
	
	private static long search(ArrayList<Term> list, String tok, Comparator<Term> c ){
		long tokIndex = Collections.binarySearch(list, new Term(tok, 0), c);
		long startIndex;
		if(tokIndex<0){
			tokIndex*=-1;
			if(tokIndex>2)
				startIndex = list.get((int) (tokIndex - 2)).offset;
			else 
				startIndex=0;
		}
		else if(tokIndex>3)
			startIndex= list.get((int) (tokIndex - 3)).offset;
		else
			startIndex=0;
		
        return startIndex;
	}
	
	private static HashMap<Long, Long> wordDocScoring(HashMap<Long, Long> DocScore, String postingList, int lbcount, int ltcount, int lecount, int lccount, int licount) {

		if (postingList==null || postingList.length()==0) return DocScore;
		
		StringTokenizer tokens = new StringTokenizer(postingList, ";");
		
		while (tokens.hasMoreTokens()  ){
			String r = tokens.nextToken();

			long tf = 0;
			Long ID = Long.parseLong(r.split("-")[0]);
			r = r.split("-")[1];
			
			int bValue=0, cValue=0, eValue=0, gValue=0, iValue=0, tValue=0, rValue=0;
			int j=0;

			while ( j< r.length() && len<500){
				
				char field = r.charAt(j);
				j++;
				if (field == 'B'){
					
					while (j<r.length() && r.charAt(j)>='0' && r.charAt(j)<='9'){
						bValue  = bValue *10 + (r.charAt(j) -'0');
						j++;
					}
			
				}else if(field == 'C'){
					while (j<r.length() && r.charAt(j)>='0' && r.charAt(j)<='9'){
						cValue  = cValue *10 + (r.charAt(j) -'0');
						j++;
					}
				}else if(field == 'E'){
					while (j<r.length() && r.charAt(j)>='0' && r.charAt(j)<='9'){							
						eValue  = eValue *10 + (r.charAt(j) -'0');
						j++;
					}
					
				}else if(field == 'G'){
					while (j<r.length() && r.charAt(j)>='0' && r.charAt(j)<='9'){
						gValue  = gValue *10 +(r.charAt(j) -'0');
						j++;
					}
			
				}else if(field == 'I'){
					while (j<r.length() && r.charAt(j)>='0' && r.charAt(j)<='9'){							
						iValue  = iValue *10 + (r.charAt(j) -'0');
						j++;
					}
			
				}else if(field == 'R'){
					while (j<r.length() && r.charAt(j)>='0' && r.charAt(j)<='9'){
						rValue  = rValue *10 + (r.charAt(j) -'0');
						j++;
					}
			
				}else if(field == 'T'){
					while (j<r.length() && r.charAt(j)>='0' && r.charAt(j)<='9'){							
						tValue  = tValue *10 + (r.charAt(j) -'0');
						j++;
					}
				}
			
			}
			tf = (bValue*Constants.BODY_WEIGHT * lbcount) + (cValue*Constants.CATEGORY_WEIGHT * lccount) + 
					(eValue*Constants.EXTERNAL_REF_WEIGHT * lecount) + (gValue*Constants.GEOBOX_WEIGHT * 0)+
					(tValue*Constants.TITLE_WEIGHT * ltcount) + (rValue*Constants.REFERENCE_WEIGHT * 16);
			tf = (long) Math.log10(tf);
//			tf *= Math.log10(17640866/postingList.split(";").size();
			DocScore.put(ID, DocScore.getOrDefault(ID, (long) 0) + tf);
			
		}
		return DocScore;
	}
}
