package fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import driver.Main;
import index.*;
import wordprocessing.Constants;

public class FileIO {

	private String localFile = "";
	
	public FileIO(String fileName){
		this.localFile = fileName;
	}
	
	/*
	 * Write writeIdTitleMapping
	 */
	public static void writeIdTitleMapping(TreeMap<Long, String> idTitleMapping, int title_file){
		try{
			File file = new File(Constants.indexDirec + "IdTitle" + title_file+ ".txt");
	    	
			if (!file.exists())
				file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter writer = new BufferedWriter(fw);
			
			// serialize the TreeMap of Indexer class
			for (Map.Entry<Long, String> e : idTitleMapping.entrySet()){
				writer.write( Long.toString(e.getKey()) + ":" +  e.getValue()  + "\n");
			}
			writer.close();
		} catch (Exception e) {
			   // do something
			e.printStackTrace();
		}
	}
	
	private static void add(Record r, int type, int value){
		if (type == Constants.TITLE)
			r.set("T",value);
	
		else if (type == Constants.BODY)
			r.set("B",value);
			
		else if (type == Constants.INFOBOX)
			r.set("I",value);
			
		else if (type == Constants.REFERENCE)
			r.set("R",value);
			
		else if (type == Constants.EXTERNAL_REF)
			r.set("E",value);
			
		else if (type == Constants.CATEGORY)
			r.set("C",value);
			
		else if (type == Constants.GEOBOX)
			r.set("G",value);
	}
	
	/*
	 * Serialise HashMap
	 */
	public static String serialise( HashMap<Long, Record> record){
		
		StringBuilder res = new StringBuilder();
		
		for (Map.Entry<Long, Record> e : record.entrySet()){
			Record r = e.getValue();
			res.append(r.getID());
			res.append("-");
			if (r.get("B")>0) res.append("B"+r.get("B"));
			if (r.get("C")>0) res.append("C"+r.get("C"));
			if (r.get("E")>0) res.append("E"+r.get("E"));
			if (r.get("G")>0) res.append("G"+r.get("G"));
			if (r.get("I")>0) res.append("I"+r.get("I"));
			if (r.get("R")>0) res.append("R"+r.get("R"));
			if (r.get("T")>0) res.append("T"+r.get("T"));	
			res.append(";");
		}
		return res.toString();
	}
	
	/*
	 * Function to write index file of each document in a dump / temporary file 
	 */
	public void write() {
		
		try{
			File file = new File(localFile);
	    	System.out.println("Write in file : "+localFile);
			if (!file.exists())
				file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter writer = new BufferedWriter(fw);
			
			// serialize the TreeMap of Indexer class
			for (Map.Entry<String, HashMap<Long, Record>> e : Index.indexLocal.entrySet()){
				writer.write(e.getKey() + ":" + FileIO.serialise(e.getValue()) + "\n");
			}
			writer.close();
		} catch (Exception e) {
			   // do something
			e.printStackTrace();
		}
	}
	
}
