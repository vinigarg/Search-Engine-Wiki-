package wordprocessing;

public class Constants {
	
	public final static int ID = 1 ;
	public final static int TITLE =  2;
	public final static int BODY = 4;
	public final static int EXTERNAL_REF = 8;
	public final static int INFOBOX = 16;
	public final static int REFERENCE = 32 ;
	public final static int CATEGORY = 64;
	public final static int GEOBOX = 128;
	
	public final static int TITLE_WEIGHT =  1000;
	public final static int BODY_WEIGHT = 2;
	public final static int EXTERNAL_REF_WEIGHT = 1;
	public final static int INFOBOX_WEIGHT = 25;
	public final static int REFERENCE_WEIGHT = 1 ;
	public final static int CATEGORY_WEIGHT = 20;
	public final static int GEOBOX_WEIGHT =1 ;
	
	public final static int MAX_DOC_SIZE =5000 ;
	public final static int MAX_TITLE_SIZE = 10000 ;
	
	
	public static Words words = null;
	public static Stemming stem = null;
	public static String fileDirec  = "/home/vini/Desktop/eclipseWorkspace/Phase2/resource/files/";
	public static String indexDirec = "/home/vini/Desktop/eclipseWorkspace/Phase2/resource/index/";
}
