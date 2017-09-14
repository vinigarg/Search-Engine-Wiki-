package driver;

import filehandler.*;
import wordprocessing.*;
import java.io.File;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Main {
	
	public static void main(String[] args){
		try {	
			// start time
			long startTime = System.currentTimeMillis();
	        System.out.println("Start time : " + startTime);
	    	
			// StopWords Initialize
			Constants.words = new Words();
			Constants.stem = new Stemming();
			
			// SAX Parser
    		File inputFile = new File("/home/vini/Desktop/eclipseWorkspace/Phase2/resource/pages.xml");
	        SAXParserFactory factory = SAXParserFactory.newInstance();
	        SAXParser saxParser = factory.newSAXParser();
	        FileHandler handler = new FileHandler();
	        Constants.words.initiallize();
        	saxParser.parse(inputFile, handler);     
	
	        // stop time
	        long stopTime = System.currentTimeMillis();
	        
	        //elapsed time
	        long elapsed = stopTime - startTime;
	        System.out.println("End time : " + stopTime);
	        System.out.println("Elapsed time : " + elapsed);
	        
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	} 
}