package graphs;

import java.util.ArrayList;
import java.util.Hashtable;


public class StringIndex {

	/// <summary>Dictionary of all indices</summary>
	protected final Hashtable dict = new Hashtable();

	/// <summary>Dictionary of all indices</summary>
	protected final ArrayList list = new ArrayList();

	/// <summary>Flag to convert all Keys to lower Case (for Case-insensitivity and readability)</summary>
	public final boolean ToLower = true; 

	/**
	 * @return the internal List as an Array Copy
	 */
	public String[] getList() { return (String[]) list.toArray(new String[list.size()]); }
	
	/// <summary>returns the index of the given String</summary>
	/// <param name="key">the String to search for</param>
	/// <returns>the index of the given String</returns>
	public int get(String key) {
		if (ToLower)
			key = key.toLowerCase();
		Object ret = dict.get(key); 
		if (ret != null)
			return ((Integer)ret).intValue(); 
		return Integer.MIN_VALUE; 
	}

	public String UnMap(int that) {
		if (that < 0)
			throw new IndexOutOfBoundsException("Index to low: " + that);
		if (that >= list.size())
			throw new IndexOutOfBoundsException("Index to high: " + that);
		return list.get(that).toString(); 
	}

	/// <summary>returns the Index of the given Key</summary>
	/// <param name="key"></param>
	/// <returns>the Index of the given Key</returns>
	public int set(String key) {
		if (ToLower)
			key = key.toLowerCase();
		Object ret = dict.get(key); 
		if (ret != null)
			return ((Integer)ret).intValue();
		int pos = list.size();
		dict.put(key, new Integer(pos));
		list.add(key);
		return pos; 
	}

	//public int set(string key, int val) {
	//    throw new NotImplementedException();
	//}

	public static void main(String[] args) {
		StringIndex sx = new StringIndex(); 
		int pos; 
		pos=sx.set("Das"); 
		pos=sx.set("ist"); 
		pos=sx.set("das"); 
		pos=sx.set("Haus"); 
	}
	
}
