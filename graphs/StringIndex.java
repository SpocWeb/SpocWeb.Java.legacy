package graphs;

import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Bidirectional mapping between Strings and dense zero-based int Indices, assigned in
 * first-seen Order. Used to intern String Keys (e.g. Node Names) as small Integers for
 * use as Array Indices in the Graph representations.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:42:46Z
 * digest: 03fa22289d0a39bc00a876c954c43e4b3a37c7f4ecb167a8d9e830bc11e379a2
 * stale: false
 * tags: [code/graph_dictionaries]
 * concepts: [String Interning Index]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class StringIndex {

	/** Maps each interned String Key to its assigned Index.	 */
	protected final Hashtable dict = new Hashtable();

	/** Reverse mapping: the String Key at each assigned Index, in Order of Insertion.	 */
	protected final ArrayList list = new ArrayList();

	/** Flag to convert all Keys to lower Case (for Case-insensitivity and readability)	 */
	public final boolean ToLower = true;

	/**
	 * Returns all interned Strings as an Array, ordered by their assigned Index.
	 * @return the internal List as an Array Copy
	 */
	public String[] getList() { return (String[]) list.toArray(new String[list.size()]); }
	
	/** Looks up the index of the given String without adding it.
	 * @param key the String to search for
	 * @return the index of the given String, or Integer.MIN_VALUE if not yet interned	 */
	public int get(String key) {
		if (ToLower)
			key = key.toLowerCase();
		Object ret = dict.get(key); 
		if (ret != null)
			return ((Integer)ret).intValue(); 
		return Integer.MIN_VALUE; 
	}

	/** Reverse lookup: returns the String Key that was assigned the given Index.
	 * @param that the Index to look up
	 * @return the String Key at this Index
	 * @throws IndexOutOfBoundsException if the Index is out of the current Range	 */
	public String UnMap(int that) {
		if (that < 0)
			throw new IndexOutOfBoundsException("Index to low: " + that);
		if (that >= list.size())
			throw new IndexOutOfBoundsException("Index to high: " + that);
		return list.get(that).toString(); 
	}

	/** Interns the given String, assigning it the next free Index if not already present.
	 * @param key the String to intern
	 * @return the (new or existing) Index of the given Key	 */
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

	/** Smoke-tests interning by adding a handful of Strings (including a case-differing duplicate) to a fresh StringIndex.
	 * @param args unused	 */
	public static void main(String[] args) {
		StringIndex sx = new StringIndex(); 
		int pos; 
		pos=sx.set("Das"); 
		pos=sx.set("ist"); 
		pos=sx.set("das"); 
		pos=sx.set("Haus"); 
	}
	
}
