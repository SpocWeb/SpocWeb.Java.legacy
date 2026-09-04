/*
 * Created on 19.03.2005
 *
 */
package function.index;

import streamIO.Assert;
import streamIO.object.enumer.container.SortedArray;
import streamIO.object.enumer.container.ContainerSet;
import tester.IOrderator;
import tester.OrderatorComparable;

/**
 * Implements an Index, which efficiently maps Objects or Strings to Numbers
 * and can return the Values or their Index in sorted Order as Arrays in O(N). 
 * 
 * Design Decisions: 
 * To avoid the Overhead of creating an Association AND a ByRefInt (or int[1]), 
 * I rather use a variant of Association, IndexElement 
 * stored in a TreeSorted, ArraySorted or a HashSet. 
 * ArraySorted allows to find Objects by Interpolation which is very fast, 
 * but Insertion is a slow Operation requiring O(N²) Movements, though very fast ones. 
 * A better Solution would be a Tree. 
 * 
 * Essentially this is only a type-safe Wrapper around a Container. 
 * 
 * Unfortunately the java.util.TreeSet Implementation does not return the Object, 
 * only a boolean Flag whether it exists. 
 * Alternatively the Index could be maintained separately in an int[] 
 * by memorizing the Indices at which Elements are inserted, 
 * but then you have to maintain two Arrays and keep them synch-ed. 
 * 
 * It is tedious and slow to nest a ByRefInt or int[1] in an Association 
 * which again is nested in a TreeNode or a HashEntry, but it works and is very flexible, 
 * rather than keeping two Arrays synch-ed. 
 * 
 * @author heuerm
 * @see streamIO.object.enumer.container.SortedArray returns the sorted Elements, 
 * but has slow Insertion, requiring reordering. 
 * @see java.util.HashSet can not return the Elements in a sorted Order 
 * @see java.util.TreeMap can do both, but it has the Mapping Overhead
 */
public class Indexer 
extends AIndexer {
	
	final static public int NOT_IN_INDEX = Integer.MIN_VALUE; 
	
	final static public String[] TEST_STRINGS = new String[] {
		//"Fischer's", "Fritz", "fischt", "frische", "Fische", 
		"Bolle", "reiste", "jüngst", "zu", "Pfingsten", 
		"nach", "Pankow", "war", "sein", "Ziel", 
		"da", "verlor", "er", "seinen", "Jüngsten",
		"janz", "plötzlich", "im", "Jewühl", 
		"'ne", "volle", "halbe", "Stunde", 
		"hat", "er ", "nach ", "ihm", "jespührt", 
		"aber", "dennoch", "hat ", "sich", "Bolle ", 
		"janz ", "köstlich", "amüsiert"
	};
	
	public static void testIt() {
		main(TEST_STRINGS);
	}
	
	/**
	 * outputs the Indices of the sorted Strings to System.out separated by Tabs. 
	 * @param args a List of Strings to index
	 */
	public static void main(final String[] args) {
		if (args.length == 0)
			testIt(); 
		else {
			final Indexer index = new Indexer(args.length, OrderatorComparable.Orderator); 
			for (int i = args.length; --i >= 0; ) 
				index.setIndexOf(args[i], i); 
			for (int i = -1; ++i < args.length; ) {
				final int ndx = index.getIndexOf(args[i]);
				if (args == TEST_STRINGS)
					Assert.EQUALS(ndx, i);
				else {
					System.out.print(ndx);
					System.out.print('\t'); 
				}
			}
			/*
			final int[] sorted = index.getIndex();
			for (int i = -1; ++i < args.length; ) {
				System.out.println(args[sorted[i]]);
			}
			*/
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @see ContainerSet allows to retrieve Objects 
	 * @see HashSet Implementations of Map 
	 * @see SortedSet an Interface that guarantees a consecutive Iterator, 
	 * which is good if you also want to sort by the Objects.   
	 * @see TreeSet implements SortedMap 
	 */
	protected final SortedArray set; // HashContainer set; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructor
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param _initialCapacity
	 */
	public Indexer(final int _initialCapacity) { 
		this.set = new SortedArray(_initialCapacity); } //
	
	/**
	 * 
	 * @param _initialCapacity a (higher) Estimate for the Capacity to save resizing Operations 
	 * @param _orderator 
	 */
	public Indexer(final int _initialCapacity, final IOrderator _orderator) { 
		this.set = new SortedArray(_initialCapacity, _orderator); } //
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * returns the items added, in their Sort Order. 
	 * @return the items added, in their Sort Order. 
	 */
	public Object[] getSorted() {
		final Object[] ret = new Object[set.getInt()]; 
		for(int i = ret.length; --i >= 0;) 
			ret[i] = ((IndexEntry)set.getAt(i)).key; 
		return ret; 
	}
	
	/**
	 * returns the Indices added, in their Values' Sort Order. 
	 * @return the Indices added, in their Values' Sort Order. 
	 */
	public int[] getIndex() {
		final int[] ret = new int[set.getInt()]; 
		for(int i = ret.length; --i >= 0;) 
			ret[i] = ((IndexEntry)set.getAt(i)).index; 
		return ret; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * returns the Number of Objects in this Index. 
	 * @param arg
	 * @return NOT_IN_INDEX if the Object is not in the List.  
	 */
	public int getInt() { return set.getInt(); }

	/**
	 * adds the Object to the Index at the Position of the current Counter
	 * @param arg the Object to place into the Index
	 * @return the Position of the Object in the Index 
	 * (the position in the external Medium is assumed to be the Number of Elements added.  
	 */
	public void addAt(final Object arg) { setIndexOf(arg, set.getInt()); }
	
	/** 
	 * adds the Object and it's Position to the Index
	 * @param arg the Object to place into the Index
	 * @param position the Position of arg in the Medium to index.  
	 * @return 
	 * the ArrayList can even return the Position where the Item was added, 
	 * but neither HashMap nor SortedTree can...
	 */
	public int setIndexOf(final Object arg, final int position) {
		final IndexEntry add = new IndexEntry(arg, position); 
		final IndexEntry old = (IndexEntry) set.find(add); //search whether the item yet exists
		if (old != null) {
			final int ret = old.index; old.index = position; 
			return ret; }
		set.addItem(add); 
		return -1; 
	}
	
	/**
	 * returns the Index of the given Object
	 * @param arg the Object to retrieve the Index for 
	 * @return -1 if the Object is not in this Index.  
	 */
	public int getIndexOf(final Object arg) { 
		final IndexEntry elm = (IndexEntry) set.find(new IndexEntry(arg, 0)); 
		if (elm == null) 
			return NOT_IN_INDEX; 
		//don't confuse the Position of the Object in the sorted Array 
		//with the Position of the Object on the indexed Medium!   
		return elm.index; 
	}
	
	public int remove(final Object arg) {
		final IndexEntry elm = (IndexEntry) set.removeItem(arg); 
		if (elm == null)
			return NOT_IN_INDEX; 
		return elm.index; 
	}
	
}
