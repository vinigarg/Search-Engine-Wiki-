package index;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import driver.Main;
import wordprocessing.*;
import parser.*;

public class Index {
	
	public static Map<String, HashMap<Long, Record>> indexLocal = new TreeMap<String,HashMap<Long,Record>>();   

	/*
	 * Constructor : Initialize index map per document  
	 */
	public Index(){
	}
	
	/*
	 * Add 2 record of same document ID
	 */
	private Record adding (Record r, int type){
		if (type == Constants.TITLE)
			r.set("T",1);
	
		else if (type == Constants.BODY)
			r.set("B",1);
			
		else if (type == Constants.INFOBOX)
			r.set("I",1);
			
		else if (type == Constants.REFERENCE)
			r.set("R",1);
			
		else if (type == Constants.EXTERNAL_REF)
			r.set("E",1);
			
		else if (type == Constants.CATEGORY)
			r.set("C",1);
			
		else if (type == Constants.GEOBOX)
			r.set("G",1);
		return r;
	}
	

	/*
	 * Word handler for title and other fields
	 */
	public void handlerLocal (String str, int type){
		if (str.length()<=1) return;
		if (type == Constants.ID)
			return;
		
		str = str.toLowerCase();
		if (Constants.words.stopWords.contains(str))
			return;
		
		if (!str.matches(".*\\d+.*")){
			
			Constants.stem.add(str.toCharArray(), str.length());
			
			if (Constants.stem.stem(str)!=null){
				str = Constants.stem.stem(str);
			}
		
			if (str.length()<3 || Constants.words.stopWords.contains(str)){
				return;
			}
		}
		// index the word and increase the count
		Record r = null;
		HashMap<Long, Record> posting = null;
		if (indexLocal.containsKey(str)){
			 posting = indexLocal.get(str);
			if (posting.containsKey(Parser.currentPageID)){
				r = posting.get(Parser.currentPageID);
			}else{
				r = new Record();
				r.setID(Parser.currentPageID);
			}
		}else{
			posting = new HashMap<Long, Record>();
			r = new Record();
			r.setID(Parser.currentPageID);
		}
		r = adding(r, type);
		posting.put(Parser.currentPageID, r);
		indexLocal.put(str, posting);
	}
}