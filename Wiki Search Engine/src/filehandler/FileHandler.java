package filehandler;

import wordprocessing.Constants;
import index.*;
import merge.Merging;
import parser.*;

import java.util.HashMap;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fileio.FileIO;

public class FileHandler extends  DefaultHandler{
	
	int id = 0;
	boolean bPage = false;
	boolean bPageId = false;
	boolean bTitle = false;
	boolean bText = false;
	StringBuilder str = new StringBuilder();
	TreeMap<Long, String> IdTitleMapping = new TreeMap<Long, String> ();
	String title = "";
	int docs=0;
	int files = 1;
	int title_files = 1;
	
	private Parser parser = null;
	
	public FileHandler() {
		// TODO Auto-generated constructor stub
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
	         throws SAXException {
		
		if (qName.equals("page")){
			bPage = true;
			id = 0;
			
		}
		else if (qName.equals("id") && bPage && id==0){
			bPageId = true;
			parser = new Parser();
			str.setLength(0);
			id++;
			
		}
		else if (qName.equals("title") && bPage){
			bTitle = true;
			str.setLength(0);
		
		}
		else if (qName.equals("text") && bPage){
			bText = true;
			str.setLength(0);
			docs++;
		}		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("id")  && bPageId && id==1){
			bPageId = false;
			this.parser.processAsPageID (str.toString());
			this.parser.processAsPageTitle (title.toString());
			IdTitleMapping.put(Long.parseLong(str.toString()), title.toString());
			if (IdTitleMapping.size() % Constants.MAX_TITLE_SIZE == 0){
				FileIO.writeIdTitleMapping(IdTitleMapping, title_files);
				IdTitleMapping.clear();
				title_files++;
			}
			this.title = "";
		}
		else if (qName.equals("title") && bTitle ){
			bTitle = false;
			this.title = str.toString();
			
		}
		else if (qName.equals("text") && bText ){
			bText = false;
			this.parser.processAsPageText (str.toString());
			if (docs % Constants.MAX_DOC_SIZE == 0){
				// write to file 
				FileIO io = new FileIO(Constants.fileDirec + "file" +files+".txt");
				io.write();
				Index.indexLocal.clear();
				files++;
			}
			
		}else if (qName.equals("page")){
			bPage = false;
			this.parser.writeTempIndexToGlobal();
					
		}
			
	}
	

	@Override
	public void characters(char ch[],int start, int length) throws SAXException {
		String s = new String(ch,start,length);
		str.append(s);
	}	
	
	
	@Override
	public void endDocument() throws SAXException {
		//this.parser.writeInvertedIndex();
		if (!Index.indexLocal.isEmpty()){
			//System.out.println("write in file : "+ Constants.fileDirec +  "file" +files+".txt");
			FileIO io = new FileIO(Constants.fileDirec + "file" +files+".txt");
			io.write();
			Index.indexLocal.clear();
		}
		
		Merging.mergeFiles(files);
		
		if (!IdTitleMapping.isEmpty()){
			FileIO.writeIdTitleMapping(IdTitleMapping, title_files);
			IdTitleMapping.clear();
		}
		
		Merging.mergeTitle(title_files);
	}
}
