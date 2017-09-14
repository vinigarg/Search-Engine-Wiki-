package index;

import java.util.HashMap;

interface RecordInterface {
	void set(String s , int y);
	void setID(long x);
	int  get(String c);
	long getID();
	void setWeight(long x);
	long getWeight();
	
}


public class Record implements RecordInterface{
	private HashMap<String, Integer> values = new HashMap<String, Integer> (); 
	long ID;
	long weight=0;
	
	@Override
	public void setID(long x){
		this.ID = x;
	}
	
	@Override
	public void set(String s  , int y) {
		// TODO Auto-generated method stub
		this.values.put(s, this.values.getOrDefault(s, 0) + y);
	}
	
	@Override
	public int get(String s) {
		// TODO Auto-generated method stub
		return this.values.getOrDefault(s, 0);
	}
	
	@Override
	public long getID() {
		return this.ID;
	}

	@Override
	public void setWeight(long x) {
		// TODO Auto-generated method stub
		this.weight = x;
	}

	@Override
	public long getWeight() {
		// TODO Auto-generated method stub
		return this.weight;
	}	
}