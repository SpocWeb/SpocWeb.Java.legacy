/*
 * Created on 17.10.2005
 *
 * Represents a Patricia-Trie Node. 
 */
package stringOp;

import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.IOrdered;
import streamIO.Log;
import streamIO.integer.AStreamIn_Int;
import streamIO.integer.AStreamWriteAble;
import streamIO.integer.IStreamIn_Int;
import streamIO.integer.IStreamOutStruct;
import streamIO.integer.IStreamWriteAble;
import streamIO.object.AStreamIn;
import function.index.AIndexer;

/**
 * Represents a Patricia-Trie (Root with empty Constructor) Node.
 * A Patricia Trie can hold any Data with unique(!) String (Bit-Sequence) Representations
 * and return it in ascending or descending Order using an effective, nearly balanced Tree. 
 * Any Search requires only a single Test on each relevant Bit of the Key. 
 * The Tree Structure depends only on the Keys themselves, not the Order of their Addition. 
 * 
 * This Class also allows to collect Objects or Indices with duplicate Keys, but not both!  
 * If negative Indices are used together with Object Arrays as Values, 
 * this Class gets mixed up, since it mistakes this Combination for a List of duplicate Objects! 
 * 
 * The empty String is equivalent to Null and must not be used as Key. 
 * This Class is ideally suited to store the Primary Key for a DB Table. 
 * Unlike a TreeMap or a HashMap it cannot store Relations, only Functions. 
 * 
 * Patricia-Tries are especially well suited for long Keys with variable Length. 
 * They encode only the specific Differences between Keys, 
 * so they are equally effective for Keys that differ only in a few Bits. 
 * They can be used as an Alternative to HashMaps, 
 * since they analyze the Key-Differences and nearly balance out for average Keys. 
 * They only fail to balance for identical Keys or Keys like 1, 10, 100, 1000, 10000...
 * but since the Depth of the Trie is then limited by the Key Length, this is fine too. 
 * 
 * The Keys must be unique, Patricia Tries don't store multiple identical Keys! 
 * 
 * Design Decisions / Implementation Details:
 * Each Node doubles as an inner (branching) or outer (leaf) Node of the Trie: an inner Node's
 * left/right Children (l, r) point further down the Bit Radix, while a Node whose Child points
 * back to itself acts as its own Terminator, avoiding a separate null Sentinel.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * 
 * Similar Classes:
 * @see streamIO.object.enumer.container.tree.TreeMapEntry 
 * also has next and prev Properties and stores key, val and ndx
 * but also a Relation Type and a Weight (doesn't apply here). 
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:43:17Z
 * digest: 42bd4905599841631014af6d867ac28ef6658445212fe3f32b4458919f577ef6
 * stale: false
 * tags: [code/patricia_trie]
 * concepts: [Patricia Trie Node]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class PatriciaNode 
extends AIndexer 
implements IStreamWriteAble {
	
	private static final Log L = new Log(PatriciaNode.class); 
	
	/** The Index Value representing NULL, i.e. a non-existing Index 	 */
	final static public int NULL = Integer.MIN_VALUE; 
	
	protected static final String ROOT_KEY = new String(new char[] { Character.MAX_VALUE }); //""; //null;
	
	//firstKey.toString().compareTo(last_Key.toString())
	/**
	 * Method to centralize the Comparison of Keys 
	 * @param first the first Object to compare 
	 * @param last the second Object to compare 
	 * @param eval the optional Evaluation Function to use. 
	 * @return the Sign of the first Key minus the second Key 
	 */
	final static public int COMPARE_TO(final Object first, final Object last, final IStringValue eval) {
		if (eval != null)
			return eval.getString(first).compareTo(eval.getString(last)); 
		return first.toString().compareTo(last.toString()); 
	}
	
	/**
	 * Method to centralize the Test for Equality 
	 * @param str the Value  to test for 
	 * @param obj the Object to test
	 * @param eval the optional Evaluation Function to use. 
	 * @return true when the String Representation of obj equals str
	 */
	final static public boolean EQUALS(final String str, final Object obj, final IStringValue eval) {
		if (eval != null)
			return str.equals(eval.getString(obj)); 
		if (obj instanceof StringBuffer) 
			return EQUALS(str, (StringBuffer) obj); 
		return str.equals(obj.toString()); 
	}
	
	/**
	 * to centralize the Test for Equality 
	 * @param str the Value  to test for 
	 * @param obj the Object to test
	 * @return true when the String Representation of obj equals str
	 */
	final static public boolean EQUALS(final String str, final StringBuffer obj) {
		//return str.equals(obj.toString()); 
		return str.contentEquals(obj); 
		/*
		int i  = str.length(); 
		if (i != obj.length())
			return false; 
		while(--i >= 0)
			if (str.charAt(i) != obj.charAt(i))
				return false; 
		return true;
		*/ 
	}
	
	/**
	 * return the Value of the i-th Bit in the given String 
	 * @param strValue the String to check
	 * @param i the Index of the Bit to retrieve from the String 
	 * @return the Value of the i-th Bit in the given String 
	 */
	final static public boolean STRING2BIT_VECTOR(final String strValue, final int i) {
		if(strValue == null)
			return false; 
		final int chr = i / 16; 
		final int bit = 15 - (i & 15);
		if(strValue.length() <= chr)
			return false; 
		return 1 == ((strValue.charAt(chr) >> bit) & 1); 
	}
	
	/**
	 * return the Value of the i-th Bit in the given String 
	 * @param strValue the String to check
	 * @param i the Index of the Bit to retrieve from the String 
	 * @return the Value of the i-th Bit in the given String 
	 */
	final static public boolean STRING2BIT_VECTOR(final StringBuffer strValue, final int i) {
		if(strValue == null)
			return false; 
		final int chr = i / 16; 
		final int bit = 15 - (i & 15);
		if(strValue.length() <= chr)
			return false; 
		return 1 == ((strValue.charAt(chr) >> bit) & 1); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** the Position of the Bit in the String to test, 
	 * that decides between branching to the left or the right Child 	 */
	final protected int Bit; 
	
	/** The Key for this Node. 
	 * It has to be stored completely, 
	 * to be able to compare and restructure the Tree
	 * on newly inserted Keys. 
	 * The Key is not related to the Bit! 
	 * It is only relevant when this Node is referenced from a Node with a higher Bit! 	 
	 */
	final public String key; 
	
	/** The Key for this Node. 	 */
	final public String getKey() { return key; } 
	
	/** optional (null allowed) Reference to the Evaluation Function to use; 
	 * alternatively just toString() is used. 
	 * Actually the eval Function needs to be stored only in the Root Node! 
	 */
	final public IStringValue eval; 
	
	/** optional (null allowed) Reference to the Evaluation Function to use; 	 */
	final public IStringValue getEval() { return eval; } 
	
	/** The Position of / Index to the Object represented by this Node	 */
	public int ndx = NULL; 
	
	/** The Value associated to the Key stored in this Node */
	public Object val; // = null; //not necessary!  
	
	/** The Type of Association of the Key to the Value
	 * not used here, since Keys cannot be duplicate 
	 * (only Functions, no Relations would be possible) 
	 */
	//public Object typ; 
	
	/** Reference to the left Child Node	 */
	protected PatriciaNode l = this; 
	
	/** Reference to the right Child Node	 */
	protected PatriciaNode r = this; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors 
	///////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor for a new Patricia Tree (Root Node) 	 */
	public PatriciaNode() {
		this.eval= null; 
		this.key = ROOT_KEY; 
		this.Bit = Character.MIN_VALUE; 
		this.l = this; 
		this.r = this; 
	//	this.ndx = NULL; 
	//	this.val = null; 
	}
	
	/**
	 * initializing Constructor for inner Elements
	 * @param _key
	 * @param _bit
	 * @param p
	 * @param c
	 */
	protected PatriciaNode(final String _key, final char _bit, final IStringValue _eval, 
			final PatriciaNode p, final PatriciaNode c) {
		this.eval= _eval; 
		this.key = _key; 
		this.Bit = _bit; 
		if (STRING2BIT_VECTOR(key, this.Bit)) {
			this.r = this; this.l = c; 
		} else {
			this.l = this; this.r = c; 
		}
		//if (p == null) {
		//	r = l = this;
		//} else 
		if (STRING2BIT_VECTOR(key, p.Bit)) 
			p.r = this; 
		else 
			p.l = this; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// searching
	///////////////////////////////////////////////////////////////////////////
	
	/** Searches for the Value associated with the given Key */
	final public Object map(final Object key) {
		final PatriciaNode test = search(key); 
		if (test.equals(key)) // 
			return test.val;
		return null; 
	}
	
	/**
	 * Tests whether obj's String Representation (via eval, if set) equals this Node's Key.
	 * @return true when the String Representation of obj equals the Key of this Node.
	 */
	final public boolean equals(final Object obj) { return EQUALS(key, obj, eval); }

	/**
	 * Compares two Objects using this Node's eval Function (or their natural String Representation).
	 * @param obj the first Object to compare
	 * @param arg the second Object to compare
	 * @return the Sign of the first Key minus the second Key
	 */
	final public int compareTo(final Object obj, final Object arg) { return COMPARE_TO(obj, arg, eval); }
	
	/** Searches for the Child Node	that matches the Pattern in v */
	final public int getIndexOf(final Object key) {
		final PatriciaNode test = search(key); 
		if (test.equals(key)) 
			return test.ndx;
		return NULL; 
	}
	
	/** Searches for the Child Node	that matches the Pattern in v */
	final public boolean contains(final Object key) {
		final PatriciaNode test = search(key); 
		return test.equals(key); //to avoid Conversion of a StringBuffer into a String, the Key should be able to compare itself to a StringBuffer 
	} // 
	
	/** Searches for the Child Node	that best matches the Pattern in v 
	 * You still have to compare the whole Key to check whether it matches completely! */
	final public PatriciaNode search(final Object _key) {
		if (_key instanceof StringBuffer)
			return search( (StringBuffer) _key); //save the toString() Operation!  
		final String key = _key.toString(); 
		PatriciaNode p, c = this; //could also recurse down 
		do { p = c; c = (STRING2BIT_VECTOR(key, c.Bit) ? c.r : c.l);  
		} while(p.Bit < c.Bit);
		return c; 
	}
	
	/** Searches for the Child Node	that best matches the Pattern in v
	 * This Method avoids converting the StringBuffer into a String.  
	 * You still have to compare the whole Key to check whether it matches completely! 
	 */
	final public PatriciaNode search(final StringBuffer key) {
		PatriciaNode p, c = this; //could also recurse down 
		do { p = c; c = (STRING2BIT_VECTOR(key, c.Bit) ? c.r : c.l);  
		} while(p.Bit < c.Bit);
		return c; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// inserting
	///////////////////////////////////////////////////////////////////////////
	
	/** Replaces or Inserts the Child Node that matches the Pattern key 
	 * When _val is a VectorInt, this indicates a List of Duplicates. 
	 * 
	 * @param _key the Key to the Object to insert or replace 
	 * @param _index the Index Value to insert or replace 
	 * @return the previous Value or NULL if it didn't exist yet. 
	 */
	final public int setIndexOf(final Object _key, final int _index) {
		final PatriciaNode node = insert(_key); //either a new or an existing Node
		if (node.val != null) { 
			((VectorInt) node.val).addInt(_index); //+NULL);  //Offset prevents Optimization for not elongating the List with 0s
		} else if (node.ndx != NULL) { //indicates an old Element
			final VectorInt list = new VectorInt(); //VectorInt also maintains it's Count! 
			list.addInt(node.ndx); //+NULL); //otherwise the Count could be maintaind in the ndx Field,
			list.addInt(  _index); //+NULL); //..but that would prevent the Fallback Replace Operation
			node.val = list; 
		}
		final int ret = node.ndx; node.ndx = _index; 
		return ret; 
	}
	
	/** Default Initial Length of Lists for duplicate Keys	 */
	public static char DEFAULT_LIST_LENGTH = 3; 
	
	/** Replaces or Inserts the Child Node that matches the Pattern key 
	 * When _val is VectorObject this indicates a List of Duplicates. 
	 * @param _key the Key to the Object to insert
	 * @param _val the Value to insert
	 * @return the previous Value or null if it didn't exist yet. 
	 */
	final public Object insert(final Object _key, final Object _val) {
		final PatriciaNode node = insert(_key); //either a new or an existing Node
		if (node.ndx == NULL) {//indicates a new Node
			node.ndx = -1; //count the Objects 
			node.val = _val; 
			return null; 
		}
		if (node.ndx >= 0) { //index is used externally, cannot collect duplicate Keys!  
			final Object ret = node.val; node.val = _val; //regular Replace Operation
			return ret; 
		}
		//indexes for multiple identical Keys cannot be collected, so don't use a VectorObject!
		if (node.val instanceof Object[]) { //rather maybe a dynamic index/Object Pair or two synchronized Arrays
			Object[] list = (Object[]) node.val; 
			if (-(--node.ndx) >= list.length) { //enlarge the Array
				final Object[] tmp = new Object[list.length << 1]; 
				System.arraycopy(list, 0, tmp, 0, list.length); 
				list = tmp; 
			}
			list[-node.ndx-1] = _val; 
		} else { //index can be used for counting...
			final Object[] list = new Object[DEFAULT_LIST_LENGTH]; 
			list[0] = node.val; 
			list[1] = _val; 
			node.val = list; 
			node.ndx = -2; //count the Objects 
		}
		return node.val; //indicate the duplicate Key by returning the Array
	}
	
	/** Retrieves or inserts the Child Node that matches the Pattern key 
	 * Duplicate Keys can be handled only, if the Index is not used! 
	 * If negative Indices are used together with Object Arrays as Values though, 
	 * this Class gets mixed up, since it mistakes this Combination for a List of duplicate Objects! 
	 * 
	 * @param _key the Object to insert
	 * @return the existing or the new Node, so that Index and Value can be set externally. 
	 */
	final public PatriciaNode insert(final Object _key) {
		final String key = (eval != null) ? eval.getString(_key) : _key.toString(); //need a constant Object; StringBuffer not suitable here!  
		PatriciaNode p = this.search(key);
		if(p.equals(key)) //exact Match...
			return p; //instead of trying to manage duplicate Keys, you could start a linked List in the Val Property, since l and r are already in use! 
		char bit = 0; //prepare Insertion...
		while(STRING2BIT_VECTOR(  key, bit) ==
			  STRING2BIT_VECTOR(p.key, bit)) 
			  ++bit; 
		PatriciaNode c = this; //could also recourse down
		do { p = c; c = (STRING2BIT_VECTOR(key, c.Bit) ? c.r : c.l);  
		} while ((c.Bit < bit) && (p.Bit < c.Bit)); 
		return new PatriciaNode(key, bit, eval, p, c); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 *  return a new Iterator on this Trie.
	 * @return a new Iterator on this Trie.
	 */
	public PatriciaIterator Iterator() { return new PatriciaIterator(this); }
	
	/**
	 * Returns a debugging String of this single Node's Key, Index, and Value.
	 * Use {@link #toString(StringBuffer)} instead to render every Key stored in the whole Trie.
	 * @return a String Representation of this single Node.
	 */
	public String toString() { return key + '@' + ndx + '=' + val; }
	
	/** an in-Order Tree-Walk returns the Keys in ascending Order	 */
	public void toString(StringBuffer ret) {
		if (ret == null)
			ret  = new StringBuffer(); 
		if (l.Bit > Bit) l.toString(ret); else ret.append('[').append(l.key).append('@').append(l.ndx).append(']'); 
		if (r.Bit > Bit) r.toString(ret); else ret.append('[').append(r.key).append('@').append(r.ndx).append(']');
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// IStreamWriteAble
	///////////////////////////////////////////////////////////////////////////
	
	/** Serializes this single Node (delegating to the generic {@link AStreamWriteAble#WRITE_TO} helper).
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct, java.lang.String)	 */
	public void writeTo(final IStreamOutStruct stream, final String name) {
		AStreamWriteAble.WRITE_TO(this, stream, name);
	}

	/** Field Name used when serializing a Node's Bit Position.	 */
	public static final String STR_BIT = "bit";
	/** Field Name used when serializing a Node's Key.	 */
	public static final String STR_KEY = "key";
	/** Field Name used when serializing a Node's Index.	 */
	public static final String STR_NDX = "index";
	/** Field Name used when serializing a Node's Value.	 */
	public static final String STR_VAL = "value";

	/** Writes this Node's Bit, Key, and (if present) Index/Value fields as a named Struct.
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct)	 */
	public void writeNodeTo(final IStreamOutStruct stream, final String name) {
		stream.open_Struct(name);
		stream.writeName(STR_BIT); stream.addInt(this.Bit); 
		stream.writeNameValuePair(STR_KEY, this.key); 
		if (ndx != NULL) {
			stream.writeName(STR_NDX); stream.addInt(this.ndx); } 
		if (val != null) {
			stream.writeName(STR_VAL); stream.addItem(this.val); } 
		stream.closeStruct(name);
	}
	
	/** serializes the Nodes inOrder Sequence  
	 * @see streamIO.integer.IStreamWriteAble#writeTo(streamIO.integer.IStreamOutStruct)	 */
	public void writeTo(final IStreamOutStruct stream) {
		if (l.Bit > Bit) l.writeTo(stream); else l.writeNodeTo(stream, "l"); 
		if (r.Bit > Bit) r.writeTo(stream); else r.writeNodeTo(stream, "r");
	}
    
	/////////////////////////////////////////////////////////////////////////////////////
	// Testing and main() Methods	
	/////////////////////////////////////////////////////////////////////////////////////
	
	protected static final String EXAMPLE = "ASERCHINGXMPL"; 
	
	protected static final String EXAMPLE2 = "TZUAKKEjks02934"; 
	
	/**Tests all Methods of this Class	 */
	public static void testIt()	{
		L.enter(); 
		testDuplicateObjects(EXAMPLE); 
		testDuplicateNumbers(EXAMPLE); 
		testPatricia(EXAMPLE, EXAMPLE ); 
		testPatricia(EXAMPLE, EXAMPLE2); 
		final PatriciaNode root = new PatriciaNode(); //test1.substring(0, 1), (char) 0, null, null);
		root.setIndexOf("A", 1); 
	}
	
	/** tests how the Trie handles duplicate Keys	 */
	private static void testDuplicateNumbers(final String test1) {
		L.enter().l("test1: ").l(test1); 
		final PatriciaNode root = new PatriciaNode(); //test1.substring(0, 1), (char) 0, null, null);
		for (int k = 3; --k >= 0;) {
			for (int i = -1; ++i < test1.length();) 
				root.setIndexOf(test1.substring(i, i+1), k);
			final IndexIterator iter = new IndexIterator(root, true); 
			for (int i; (i = iter.nextInt()) != NULL;) {
				L.n(iter.currNode()).l(i); 
				final String curr = iter.currNode().key; 
			}
		}
		L.n(root); 
		//ObjectIterator; 
	}
	
	/** tests how the Trie handles duplicate Keys	 */
	private static void testDuplicateObjects(final String test1) {
		L.enter().l("test1").l(test1); 
		final PatriciaNode root = new PatriciaNode(); //test1.substring(0, 1), (char) 0, null, null);
		for (int k = 3; --k >= 0;) {
			for (int i = -1; ++i < test1.length();) 
				root.insert(test1.substring(i, i+1), new Integer(k));
			final ObjectIterator iter = new ObjectIterator(root); 
			String last = ""; 
			for (Object val; (val = iter.nextItem()) != null;) {
				L.n(iter.currNode()).l(val); 
				final String curr = iter.currNode().key; 
				Assert.NOT_NEGATIVE(curr.compareTo(last)); 
				last = curr; 
			}
		}
		L.n(root).n();  
	}
	
	private static void testPatricia(final String test1, final String test2) {
		L.enter().l("test1='").l(test1).l("';test2='").l(test2); 
		final PatriciaNode root = new PatriciaNode(); //test1.substring(0, 1), (char) 0, null, null);
		for (int i = -1; ++i < test1.length();) 
			root.insert(test1.substring(i, i+1)); 
		L.n(root).n();  
		PatriciaIterator iter = new PatriciaIterator(root);  
		String lastKey, nextKey = ""; //root.key; 
		for(PatriciaNode node; null != (node = iter.nextNode());) {
			lastKey = nextKey; L.l(nextKey = node.key); 
			Assert.NOT_NEGATIVE(nextKey.compareTo(lastKey)); 
		}
		L.n(); 
		iter = new PatriciaIterator(root, "L", false, "E", false); 
		nextKey = root.key; 
		for(PatriciaNode node; null != (node = iter.nextNode());) {
			lastKey = nextKey; L.l(nextKey = node.key); 
			Assert.NOT_POSITIVE(nextKey.compareTo(lastKey)); 
		}
		for (int i = -1; ++i < test1.length();) 
			Assert.IS_TRUE(root.contains(test1.substring(i, i+1))); 
		for (int i = -1; ++i < test2.length();) {
			final String test = test2.substring(i, i+1); 
			Assert.EQUALS(test1.indexOf(test) >= 0, root.contains(test)); 
		}
		L.n(AStreamWriteAble.TO_STRING(root, "root")).n(); 
	}
	
	/**
	 *The main entry point for the application.
	 * @param args Array of parameters passed to the application via the command line.
	 */
	public static void main(final String[] args) { //throws java.io.IOException {
		testIt();
	}
	
}


/**
 * In-Order Patricia Object Iterator.
 * Returns the next Object, also for duplicate Keys. 
 * 
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * 
 * @see streamIO.object.enumer.container.tree.TreeMap 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:43:17Z
 * digest: da0a62e9fc6bbc445d2525b858d9b2c7b7d30aa2f3788f1a21a97334e1c9ec69
 * stale: false
 * tags: [code/patricia_trie]
 * concepts: [Patricia Trie Object Iterator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
class ObjectIterator 
extends AStreamIn
{

	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor 	 */
	public ObjectIterator(final PatriciaNode root) {
		this(root, null, false, null, false, false, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 	 */
	public ObjectIterator(final PatriciaNode root, boolean reverse) {
		this(root, null, false, null, false, reverse, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 	 */
	public ObjectIterator(final PatriciaNode root, final char initialStackSize) {
		this(root, null, false, null, false, false, initialStackSize); 
	}
	
	/** Initializing Constructor 	 */
	public ObjectIterator(final PatriciaNode root, 
			final Object firstKey, final Object last_Key) {
		this(root, firstKey, true, last_Key, false, false, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 	 */
	public ObjectIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, 
			final Object last_Key, final boolean includeLast) {
		this(root, firstKey, includeFirst, last_Key, includeLast, false, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	public ObjectIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, final boolean reverse) {
		this(root, firstKey, includeFirst, null, false, reverse, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	public ObjectIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, final boolean reverse, 
			final char initialStackSize) {
		this(root, firstKey, includeFirst, null, false, reverse, initialStackSize); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	public ObjectIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, 
			final Object last_Key, final boolean includeLast, final char initialStackSize) {
		this(root, firstKey, includeFirst, last_Key, includeLast, false, initialStackSize); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	protected ObjectIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, 
			final Object last_Key, final boolean includeLast, 
			final boolean _reverse, final char initialStackSize) {
		iter = new PatriciaIterator(root, 
				firstKey, includeFirst, 
				last_Key, includeLast, 
				_reverse, initialStackSize); 
	}
	
	/** Reference to the Iterator	 */
	final protected PatriciaIterator iter; 
	
	/** The Index to the VectorInt of Indices	 */
	protected int index; 
	
	/** return the current PatriciaNode 
	 * @return the current PatriciaNode 
	 */
	public PatriciaNode currNode() { return iter.currNode; }
	
	/** Returns the Value at the current Iterator Position, resolving into the duplicate-Key Array via index when set.
	 * @see streamIO.object.AStreamIn#currItem()	 */
	public Object currItem() {
		if (index >= 0) //the expensive DownCast could be avoided by caching the cast Reference
			return ((Object[]) iter.currNode.val)[index];
		if (iter.currNode == null)
			return null;
		return iter.currNode.val;
	}

	/** Advances to the next Value, walking through a duplicate-Key Array before moving the underlying Trie Iterator.
	 * @see streamIO.object.AStreamIn#nextItem()	 */
	public Object nextItem() {
		if (--index < 0) {
			if  (iter.nextNode() == null)
				 return null; 
			if ((iter.currNode.ndx < 0) && 
				(iter.currNode.val instanceof Object[])) 
				index = -iter.currNode.ndx-1; 
		}
		return currItem(); 
	}
	
	/** Delegates to the underlying {@link PatriciaIterator}'s traversal Order (ascending or descending).
	 * @see streamIO.integer.AStreamIn_Int#getOrder()	 */
	public byte getOrder() { return iter.getOrder(); }

	/** Not implemented: the Trie Iterator has no linear Position to report.
	 * @see streamIO.integer.AStreamIn_Int#getPosition()	 */
	public long getPosition() { //return index; }
		throw new RuntimeException("Not implemented!"); }

	/** Delegates to the underlying {@link PatriciaIterator}'s remaining Node count.
	 * @see streamIO.integer.AStreamIn_Int#availAble()	 */
	public long availAble() { return iter.availAble(); }

	/** mark()in would require to memorize the whole Stack.
	 * @see streamIO.integer.AStreamIn_Int#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return 0; }

}

/**
 * In-Order Patricia Index Iterator.
 * Returns the next index, also for duplicate Keys. 
 * 
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * 
 * @see streamIO.object.enumer.container.tree.TreeMap 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:43:17Z
 * digest: b94d9c967f1b160703cf1f03263cc6e2746ca2c5a3da1371b38ea05e9d96de3d
 * stale: false
 * tags: [code/patricia_trie]
 * concepts: [Patricia Trie Index Iterator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
class IndexIterator 
extends AStreamIn_Int //PatriciaIterator //
implements IStreamIn_Int
{
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor 	 */
	public IndexIterator(final PatriciaNode root) {
		this(root, null, false, null, false, false, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 	 */
	public IndexIterator(final PatriciaNode root, boolean reverse) {
		this(root, null, false, null, false, reverse, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 	 */
	public IndexIterator(final PatriciaNode root, final char initialStackSize) {
		this(root, null, false, null, false, false, initialStackSize); 
	}
	
	/** Initializing Constructor 	 */
	public IndexIterator(final PatriciaNode root, 
			final Object firstKey, final Object last_Key) {
		this(root, firstKey, true, last_Key, false, false, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 	 */
	public IndexIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, 
			final Object last_Key, final boolean includeLast) {
		this(root, firstKey, includeFirst, last_Key, includeLast, false, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	public IndexIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, final boolean reverse) {
		this(root, firstKey, includeFirst, null, false, reverse, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	public IndexIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, final boolean reverse, 
			final char initialStackSize) {
		this(root, firstKey, includeFirst, null, false, reverse, initialStackSize); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	public IndexIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, 
			final Object last_Key, final boolean includeLast, final char initialStackSize) {
		this(root, firstKey, includeFirst, last_Key, includeLast, false, initialStackSize); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	protected IndexIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, 
			final Object last_Key, final boolean includeLast, 
			final boolean _reverse, final char initialStackSize) {
		iter = new PatriciaIterator(root, 
				firstKey, includeFirst, 
				last_Key, includeLast, 
				_reverse, initialStackSize); 
	}
	
	/** Reference to the Iterator	 */
	final protected PatriciaIterator iter; 
	
	/** The Index to the VectorInt of Indices	 */
	protected int index; 
	
	//public Object currItem() { return iter.currNode.val; }
	
	/** return the current PatriciaNode 
	 * @return the current PatriciaNode 
	 */
	public PatriciaNode currNode() { return iter.currNode; }
	
	/** @see streamIO.integer.AStreamIn_Int#nextLongInternal()	 */
	protected long nextLongInternal() {
		if (index > 0) //the expensive DownCast could be avoided by caching the cast Reference
			return ((VectorInt) iter.currNode.val).getIntAt(--index); //-PatriciaNode.NULL; 
		if (iter.nextNode() == null)
			return PatriciaNode.NULL; 
		if (iter.currNode.val instanceof VectorInt) {
			index = ((VectorInt) iter.currNode.val).getInt(); 
			return nextLongInternal(); }
		return iter.currNode.ndx; 
	}
	
	/** Returns the Index stored at the Iterator's configured last Key boundary.
	 * @see streamIO.integer.AStreamIn_Int#getMinDouble()	 */
	public double getMinDouble() { return iter.last.ndx; }

	/** Delegates to the underlying {@link PatriciaIterator}'s traversal Order (ascending or descending).
	 * @see streamIO.integer.AStreamIn_Int#getOrder()	 */
	public byte getOrder() { return iter.getOrder(); }

	/** Not implemented: the Trie Iterator has no linear Position to report.
	 * @see streamIO.integer.AStreamIn_Int#getPosition()	 */
	public long getPosition() { //return index; }
		throw new RuntimeException("Not implemented!"); }

	/** Delegates to the underlying {@link PatriciaIterator}'s remaining Node count.
	 * @see streamIO.integer.AStreamIn_Int#availAble()	 */
	public long availAble() { return iter.availAble(); }

	/** mark()in would require to memorize the whole Stack.
	 * @see streamIO.integer.AStreamIn_Int#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return 0; }

}


/**
 * In-Order Patricia Trie Iterator.
 * Returns the empty Root Node as the very first Item
 * and null only after all TreeNodes, so no additional Check is necessary!  
 * 
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * 
 * @see streamIO.object.enumer.container.tree.TreeMap 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:43:17Z
 * digest: edbd0643a228658efb87f2054b133d1c5398e2895d31c3259e9440cd8cb940f8
 * stale: false
 * tags: [code/patricia_trie]
 * concepts: [Patricia Trie Iterator Base]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
class PatriciaIterator {
	
	/** The Size is derived from the Reasoning that 
	 * Identifiers are typically up to 20 8-Bit Characters long. 
	 * Every Level requires 2 Parameters. 
	 */ 
	final static public char DEFAULT_INITIAL_SIZE = 160; 
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Fields & Accessors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** the current Node, returned by the last nextNode Operation. */
	protected PatriciaNode currNode; 
	
	/**
	 *  return the current Node, returned by the last nextNode Operation. 
	 * @return the current Node, returned by the last nextNode Operation. 
	 */
	public PatriciaNode currNode() { return currNode; } //stack[SP]; }
	
	/** 
	 * The Tree-Structure requires a Stack, since you cannot navigate upwards. 	 
	 * The Stack contains a Pair of Nodes for each Level: the Parent and it's Child   
	 */
	protected PatriciaNode[] stack; 
	
	/** To serialize a recursive Structure you need a Stack 	 */
	protected int SP; 
	
	/** Reference to the last Node to use in this Iterator 	 */
	final PatriciaNode last; 
	
	/** flag whether the last Node is in the Iteration or not (if it exists) 	 */
	final boolean inclusive; 
	
	/** flag whether the Iteration is reversed 	 */
	final boolean reverse; 
	
	/** 
	 *  return the Order of this Iterator: strictly ascending Keys. 
	 * @return the Order of this Iterator: strictly ascending Keys. 
	 */
	public byte getOrder() { return reverse ? IOrdered.ORDER_DESC_STRICT : IOrdered.ORDER_ASC_STRICT; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor 	 */
	public PatriciaIterator(final PatriciaNode root) {
		this(root, null, false, null, false, false, DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 	 */
	public PatriciaIterator(final PatriciaNode root, boolean reverse) {
		this(root, null, false, null, false, reverse, PatriciaIterator.DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 	 */
	public PatriciaIterator(final PatriciaNode root, final char initialStackSize) {
		this(root, null, false, null, false, false, initialStackSize); 
	}
	
	/** Initializing Constructor 	 */
	public PatriciaIterator(final PatriciaNode root, 
			final Object firstKey, final Object last_Key) {
		this(root, firstKey, true, last_Key, false, false, DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 	 */
	public PatriciaIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, 
			final Object last_Key, final boolean includeLast) {
		this(root, firstKey, includeFirst, last_Key, includeLast, false, DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	public PatriciaIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, final boolean reverse) {
		this(root, firstKey, includeFirst, null, false, reverse, DEFAULT_INITIAL_SIZE); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	public PatriciaIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, final boolean reverse, 
			final char initialStackSize) {
		this(root, firstKey, includeFirst, null, false, reverse, initialStackSize); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	public PatriciaIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, 
			final Object last_Key, final boolean includeLast, final char initialStackSize) {
		this(root, firstKey, includeFirst, last_Key, includeLast, false, initialStackSize); 
	}
	
	/** Initializing Constructor 
	 * Allows to rapidly select connected SubSets ("Intervals") from the stored Data 
	 * and traverse them either in regular or reverse Order.   
	 * @param root the Root Patricia Node to start from 
	 * @param firstKey the left Key to start from 
	 * @param includeFirst flag whether the left Key is in the Iteration or not (if it exists) 
	 * @param last_Key the right Key to end with 
	 * @param includeLast flag whether the right Key is in the Iteration or not (if it exists)
	 * @param initialStackSize initial Size of the Seach Stack 
	 */
	protected PatriciaIterator(final PatriciaNode root, 
			final Object firstKey, final boolean includeFirst, 
			final Object last_Key, final boolean includeLast, 
			final boolean _reverse, final char initialStackSize) {
		this.stack = new PatriciaNode[(initialStackSize+1) << 1];
		if (last_Key != null) {
			last = root.search(last_Key); 
			inclusive = includeLast || !last.equals(last_Key); 
		} else {
			last = null; 
			inclusive = includeLast; 
		}
		final String lastId = (last_Key == null) ? null : last_Key.toString();
		PatriciaNode p = root; //could also recurse down
		PatriciaNode c =(root.l.Bit > root.Bit) ? root.l : root.r;
		if (firstKey == null) 
			this.reverse = _reverse;  
		else { 
			final String first_key = firstKey.toString(); //slightly ineffective for StringBuffers
			if (last != null) 
				reverse = (root.compareTo(last.key, first_key) < 0); 
			else 
				reverse = _reverse;  
			do { //fill the Stack with the Nodes to traverse
				p = c; 
				if (PatriciaNode.STRING2BIT_VECTOR(first_key, c.Bit)) {
					if (reverse) {
						stack[SP++] = p; //exclude smaller Elements from the Recursion
						stack[SP++] = c.l; 
					} c = c.r; 
				} else {
					if (!reverse) {
						stack[SP++] = p; 
						stack[SP++] = c.r; 
					} c = c.l; //also add the Parent, since the right Node has to be returned too. 
				}
			} while(p.Bit < c.Bit);	
		}   
		if ((firstKey == null) || includeFirst || !c.equals(firstKey)) {
			stack[SP++] = p; 
			stack[SP++] = c; 
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Since this Iterator never returns null-s, except at the End, 
	 * this Operation is not used. 
	 *  return the minimum Number of available Values in this Iterator. 
	 * @return the minimum Number of available Values in this Iterator. 
	 */
	public int availAble() { return SP>>1; }
	
	/**
	 * Since this Iterator never returns null, except at the End, 
	 * this Operation is not used. 
	 *  return true if there are Values available in this Iterator. 
	 * @return true if there are Values available in this Iterator. 
	 */
	public boolean isValid() { return SP >= 0; }
	
	/**
	 *  return the next Node in Order. 
	 * @return the next Node in Order. 
	 */
	public PatriciaNode nextNode() {
		if (SP < 2)
			return currNode = null; 
		final PatriciaNode node = stack[--SP]; 
		final PatriciaNode prnt = stack[--SP]; //only required to test the bit
		if (prnt.Bit >= node.Bit) { //an End Node
			if (last == node) { 
				SP = 0;
				if (!inclusive)
					return currNode = null; 
			}
			return currNode = node; 
		}
		if (stack.length < SP+4) { //enlarge the Stack
			final PatriciaNode[] temp = new PatriciaNode[stack.length << 1]; 
			System.arraycopy(stack, 0, temp, 0, stack.length);
			stack = temp; 
		}
		stack[SP++] = node; stack[SP++] = reverse ? node.l : node.r; 
		stack[SP++] = node; stack[SP++] = reverse ? node.r : node.l;  
		return nextNode();
	}
	
	/** an in-Order Tree-Walk returns the Keys in ascending Order	 */
	public static void toString(final PatriciaNode node, final StringBuffer ret) {
		if (node.l.Bit > node.Bit) node.l.toString(ret); else ret.append(node.l.key); 
		if (node.r.Bit > node.Bit) node.r.toString(ret); else ret.append(node.r.key);  
	}
	
}
