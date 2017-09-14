package parser;

import fileio.FileIO;
import wordprocessing.*;
import index.*;

/*
 * Author Vini Garg
 */

public class Parser {	
	
	private Index indexObj = null;
	public static long currentPageID ;
	
	public Parser(){
		this.indexObj =  new Index();
	}
	
	
	/*
	 * isLetter redefined as wanted only english alphabets
	 */
	private boolean isLetter(char c){
		return (c>='a' && c<='z') || (c>='A' && c<='Z');
	}
	
	
	/*
	 * function to set current document ID
	 */
	public void processAsPageID(String s){
		Parser.currentPageID = Long.parseLong(s);
	}	
	
	
	/*
	 * Function to process Title of the document 	
	 */
	public void processAsPageTitle(String s){
		s = Parser.splitCamelCase(s);
		StringBuilder word = new StringBuilder("");
		char[] arr = s.toCharArray();
		for (int i=0; i<s.length(); i++){
			if (isLetter(arr[i]) || Character.isDigit(arr[i])) 
				word.append(arr[i]);
			
			else if (word.length()>0){
				this.indexObj.handlerLocal(word.toString(), Constants.TITLE);
				word.setLength(0);	
			}
		}
		if (word.length() > 0){
			this.indexObj.handlerLocal(word.toString(), Constants.TITLE);
		}
	}
	
	
	/*
	 *  Function to converts string like : "VirtualReality" -> "Virtual Reality"
	 */
	private static String splitCamelCase(String s) {
	   return s.replaceAll( String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])",
			   				"(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])" ), " ");
	}
	
	
	/*
	 * Functions to process InforBox field in body of the document 
	 */
	private void processInfoBox(String infoBoxString){
		// remove {{infobox from start 
		// remove }} from end
		infoBoxString = infoBoxString.substring(9, infoBoxString.length()-2);
		
		//trim
		infoBoxString = infoBoxString.replaceAll("\\s{2,}", " ").trim();
		
		// remove comments from infoBoxString
		infoBoxString = infoBoxString.replaceAll(".*<!--.*-->", "");
		
		//remove citations and GR if any
		infoBoxString = infoBoxString.replaceAll(".*\\{\\{cite.*\\}\\}", "");
					
		StringBuilder word = new StringBuilder();
		int len = infoBoxString.length();
		
		for (int i=0; i<len ; i++){
			char curr = infoBoxString.charAt(i);
			if (isLetter(curr)){
				word.append(curr);
			}else if (word.length()>0){
				this.indexObj.handlerLocal(word.toString(), Constants.INFOBOX);
				word.setLength(0);
			}
		}
		if (word.length() > 0){
			this.indexObj.handlerLocal(word.toString(), Constants.INFOBOX);
		}
	}

	
	/*
	 * Functions to process Categories field in body of the document
	 */
	private void processCategories(String categoryString){		
		long len = categoryString.length();
		StringBuilder word = new StringBuilder();
		for (long i=0; i<len; i++){
			char currCh = categoryString.charAt((int)i);
			if ( isLetter(currCh)){
				word.append(currCh);
			}
			else if (word.length()>0){
				this.indexObj.handlerLocal(word.toString(), Constants.CATEGORY);
				word.setLength(0);
					
			}
		}
		if (word.length() > 0){
			this.indexObj.handlerLocal(word.toString(), Constants.CATEGORY);
		}
	}
	
	/*
	 * Functions to process Geobox field in body of the document
	 */
	private void processGeoBox(String geoBoxString){
		geoBoxString = geoBoxString.substring(8, geoBoxString.length()-2);
		
		long len = geoBoxString.length();
		StringBuilder word = new StringBuilder();
		for (long i=0; i<len; i++){
			char currCh = geoBoxString.charAt((int)i);
			if ( isLetter(currCh)){
				word.append(currCh);
			}
			else if (word.length()>0){
				this.indexObj.handlerLocal(word.toString(), Constants.GEOBOX);
				word.setLength(0);
				
			}

		}
		if (word.length() > 0){
			this.indexObj.handlerLocal(word.toString(), Constants.GEOBOX);
		}
	}
	
	/*
	 * Functions to process Cite field in body of the document
	 */
	@SuppressWarnings("unused")
	private void processCite(String citeString){	}
	
	
	/*
	 * Function to process words of external reference field data
	 */
	
	private void processExternal(String links){
		links = links.toLowerCase();
		String linkArray [] = links.split(" ");
		long len = linkArray.length;
		StringBuilder word = new StringBuilder();
		for (int j=0; j<len; j++){
			links = linkArray[j];
			if (links.matches(".*http:.*") || links.matches(".*https:.*")) continue;
			else{
				this.indexObj.handlerLocal(links.trim(), Constants.EXTERNAL_REF);
			}
		}
	}
	
	/*
	 * Function to process words of external reference field data
	 */
	private void processReferences(String links){
		links = links.toLowerCase();
		String linkArray [] = links.split(" ");
		long len = linkArray.length;
		StringBuilder word = new StringBuilder();
		for (int j=0; j<len; j++){
			links = linkArray[j];
			if (links.matches(".*http:.*") || links.matches(".*https:.*")) continue;
			else{
				this.indexObj.handlerLocal(links.trim(), Constants.REFERENCE);
			}
		}
	}
	
	/*
	 * Functions to parse text in body of the document
	 */
	public void processAsPageText(String text){
		// text processing
		
		long n = text.length();
		StringBuilder word = new StringBuilder();
		StringBuilder extLink = new StringBuilder();
		StringBuilder reference = new StringBuilder();
		boolean bExternalLinks = false;
		boolean bReferences = false;
		
		
		for (int i=0; i < n; i++){
			char currCh = text.charAt(i) ;
			if ( isLetter(currCh)/* ||	Character.isDigit(currCh)*/){
				word.append(currCh);
				
			}
			/*
			 * parsing pattern having { 
			 * infobox , geobox, cite, gr
			 */
			else if (currCh == '{'){
				/*
				 * INFOBOX
				 */
				if (i+9 < n && 
					text.substring(i+1, i+9).toLowerCase().equals("{infobox")){
					
                    StringBuilder infoboxString = new StringBuilder();
                    int countBrack = 0;
                    for ( ; i < n ; i++ ) {
                        currCh = text.charAt(i);
                        infoboxString.append(currCh);
                        if ( currCh == '{') {
                        	countBrack++;
                        }
                        else if ( currCh == '}') {
                        	countBrack--;
                        }
                        if ( countBrack == 0 || 
                        	(currCh == '=' && i+1 < n && text.charAt(i+1) == '=')) {
                            if ( currCh == '=' ) {
                            	infoboxString.deleteCharAt(infoboxString.length()-1);
                            }
                            i--;
                            break;
                        }
                    }
                    processInfoBox(infoboxString.toString());	
				}
				/*
				 * CITE
				 */
				else if (i+6 < n && 
						 text.substring(i+1, i+6).toLowerCase().equals("{cite")){
					
					StringBuilder citeString = new StringBuilder();
                    int countBrack = 0;
                    for ( ; i < n ; i++ ) {

                        currCh = text.charAt(i);
                        citeString.append(currCh);
                        if ( currCh == '{') {
                        	countBrack++;
                        }
                        else if ( currCh == '}') {
                        	countBrack--;
                        }
                        if ( countBrack == 0 || 
                        	(currCh == '=' && i+1 < n && text.charAt(i+1) == '=')) {
                            if ( currCh == '=' ) {
                            	citeString.deleteCharAt(citeString.length()-1);
                            }
                            i--;
                            break;
                        }
                    }
                   // processCite (citeString.toString());
				}
				/*
				 * GEOBOX
				 */
				else if (i+8 < n && 
						 text.substring(i+1, i+8).toLowerCase().equals("{geobox")){
					
					StringBuilder geoString = new StringBuilder();
                    int countBrack = 0;
                    for ( ; i < n ; i++ ) {

                        currCh = text.charAt(i);
                        geoString.append(currCh);
                        if ( currCh == '{') {
                        	countBrack++;
                        }
                        else if ( currCh == '}') {
                        	countBrack--;
                        }
                        if ( countBrack == 0 || 
                        	(currCh == '=' && i+1 < n && text.charAt(i+1) == '=')) {
                            if ( currCh == '=' ) {
                            	geoString.deleteCharAt(geoString.length()-1);
                            }
                            i--;
                            break;
                        }
                    }
                    
                    // comment the  statement below if GEOBOX is to be removed
                     //processGeoBox(geoString.toString());
                    
				}	
				/*
				 * GR
				 */
				else if (i+4 < n && 
						 text.substring(i+1, i+4).toLowerCase().equals("{gr")){
					
					StringBuilder grString = new StringBuilder();
                    int countBrack = 0;
                    for ( ; i < n ; i++ ) {

                        currCh = text.charAt(i);
                        grString.append(currCh);
                        if ( currCh == '{') {
                        	countBrack++;
                        }
                        else if ( currCh == '}') {
                        	countBrack--;
                        }
                        if ( countBrack == 0 || 
                        	(currCh == '=' && i+1 < n && text.charAt(i+1) == '=')) {
                            if ( currCh == '=' ) {
                            	grString.deleteCharAt(grString.length()-1);
                            }
                            i--;
                            break;
                        }
                    }
                    // GR is removed so not process called for it
				}
				/*
				 * COORD 
				 */
				else if (i+7 < n && 
						 text.substring(i+1, i+7).toLowerCase().equals("{coord")){
					
					StringBuilder crdString = new StringBuilder();
                    int countBrack = 0;
                    for ( ; i < n ; i++ ) {

                        currCh = text.charAt(i);
                        crdString.append(currCh);
                        if ( currCh == '{') {
                        	countBrack++;
                        }
                        else if ( currCh == '}') {
                        	countBrack--;
                        }
                        if ( countBrack == 0 || 
                        	(currCh == '=' && i+1 < n && text.charAt(i+1) == '=')) {
                            if ( currCh == '=' ) {
                            	crdString.deleteCharAt(crdString.length()-1);
                            }
                            i--;
                            break;
                        }
                    }
                    //COORD is removed so not process called for it 
				}
			}
			/*
			 * parsing pattern having [ 
			 * files and images are removed
			 * process category
			 */
			else if (currCh == '['){
				if ( i+11 < n && text.substring(i+1,i+11).toLowerCase().equals("[category:") ) {

                    StringBuilder categoryString = new StringBuilder();

                    int count = 0;
                    for ( ; i < n ; i++ ) {
                        currCh = text.charAt(i);
                        categoryString.append(currCh);
                        if ( currCh == '[') {
                            count++;
                        }
                        else if ( currCh == ']') {
                            count--;
                        }
                        if ( count == 0 || (currCh == '=' && i+1 < n && text.charAt(i+1) == '=')) {
                            if ( currCh == '=' ) {categoryString.deleteCharAt(categoryString.length()-1);}
                            i--;
                            break;
                        }
                    }
                    processCategories(categoryString.toString());

                }
				/*
                 * Images to be removed
                 */
                else if ( i+8 < n && text.substring(i+1,i+8).toLowerCase().equals("[image:") ) {
                   
                    int count = 0;
                    for ( ; i < n ; i++ ) {

                        currCh = text.charAt(i);
                        if ( currCh == '[') {
                            count++;
                        }
                        else if ( currCh == ']') {
                            count--;
                        }
                        if ( count == 0 || (currCh == '=' && i+1 < n && text.charAt(i+1) == '=')) {
                            i--;
                            break;
                        }
                    }
                }
                /*
                 * File to be removed
                 */
                else if ( i+7 < n && text.substring(i+1,i+7).toLowerCase().equals("[file:") ) {

                    int count = 0;
                    for ( ; i < n ; i++ ) {

                        currCh = text.charAt(i);

                        if ( currCh == '[') {
                            count++;
                        }
                        else if ( currCh == ']') {
                            count--;
                        }
                        if ( count == 0 || (currCh == '=' && i+1 < n && text.charAt(i+1) == '=')) {
                            i--;
                            break;
                        }
                    }
                }
			}else if(currCh == '<'){
                /*
                 * Comments are removed
                 */
				if ( i+4 < n && text.substring(i+1,i+4).equals("!--") ) {

                    int next = text.indexOf("-->" , i+1);
                    if ( next == -1 || next+2 > n ) {
                        i = (int)n-1;
                    }
                    else {
                        i = next + 2;
                    }

				}
				/*
				 * References List is stored but <ref/> tags are ignored
				 */
				else if ( i+5 < n && text.substring(i+1,i+5).equals("ref>") ) {
					
					int next = text.indexOf("</ref>" , i+1);
                    if ( next == -1 || next + 5 > n ) {
                        i  = (int)n-1;
                    }
                    else {
                        i = next + 6;
                    }
				}
				/*
				 * gallery tag is removed
				 */
				else if ( i+8 < n && text.substring(i+1,i+8).equals("gallery") ) {
					
					int next = text.indexOf("</gallery>" , i+1);
                    if ( next == -1 || next + 9 > n) {
                        i = (int)n-1;
                    }
                    else {
                        i = next + 9;
                    }
				}
			}else if((i+18<n && text.substring(i,i+18).equalsIgnoreCase("==External Links==")) || i+20<n && text.substring(i,i+20).equalsIgnoreCase("== External Links ==")){
				if(text.charAt(i+17)=='='){
    				i=i+17;
    			}
    			else{
    				i=i+19;
    			}
    			bExternalLinks=true;
    			
    		}
    		else if(text.charAt(i)=='*' && bExternalLinks){
    			while(i<n && text.charAt(i)!='['){
    				i++;
    			}
    			if(i<n && text.charAt(i)=='['){
    				while(i<n && text.charAt(i)!=' '){
    					i++;
    				}
    				extLink.setLength(0);
    				while(i<n){
    					
    					if(text.charAt(i)==']'){
    						break;
    					}
    					if(isLetter(text.charAt(i))){
                            extLink.append(text.charAt(i));
    					}
                        else
                        {	
                        	if(extLink.length()>1){
            					processExternal(extLink.toString());
    	    				}
                        	extLink.setLength(0);
                        }
    					i++;
    				}
    				if(extLink.length()>1){
    					processExternal(extLink.toString());
    				}
    				extLink.setLength(0);
    			}
    		}
    		else if((i+14<n && text.substring(i,i+14).equalsIgnoreCase("==references==")) 
    				||
    				(i+26<n && text.substring(i,i+26).equalsIgnoreCase("== Notes and references =="))
    				||
    				i+16<n && text.substring(i,i+16).equalsIgnoreCase("== references ==") ){
    			if(i+13<n && text.charAt(i+13)=='='){
    				i=i+14;
    			}
    			else if (i+16<n && text.charAt(i+16)=='='){
    				i=i+16;
    			}else{
    				i+=26;
    			}
    			while(i<n){
    				char c=text.charAt(i);
					if((i+1<n && text.substring(i,i+2).equalsIgnoreCase("==")) ||(i+11<n && text.substring(i,i+10).equalsIgnoreCase("[[category"))){
						i--;
						break;
					}
					if(i+4<n && text.substring(i,i+4).equalsIgnoreCase("http")){
		    			i=i+4;
		    			while(i<n && text.charAt(i)!=' '){
		    				i++;
		    			}
		    		}
					else if((c>='a' && c<='z') || (c>='A' && c<='Z')){
                        reference.append(c);
					}
                    else
                    {	
                    	if(reference.length()>1){
	    					processReferences(reference.toString());
	    				}
                    	reference.setLength(0);
                    }
					i++;
				}
				if(reference.length()>1){
					processReferences(reference.toString());
				}
				reference.setLength(0);
    		}
			/*
			 * Text Body word
			 */
			else if (word.length() > 0){
				this.indexObj.handlerLocal(word.toString() , Constants.BODY);
                word.setLength(0);
			}
		}
		
		if (word.length() > 0){
			this.indexObj.handlerLocal(word.toString() , Constants.BODY);
            word.setLength(0);
		}
	}

		
	public void writeTempIndexToGlobal(){
		
	}
}