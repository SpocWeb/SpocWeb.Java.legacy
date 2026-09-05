package graphs;

import math.vector.AVector;
import streamIO.Assert;
import streamIO.Log;

/** Builds up a Forest of disjoint Sets by adding Elements
  * and successively adding Parents to the Elements
  * identified by Elements (Edges) of Equivalence Relations.
  *
  * Equivalence (commonality of the set) of two Elements is checked
  * by comparing the root Elements (Representatives) of these two Elements.
  * This Representation doesn't allow Enumeration of the Set Members,
  * since for that you have to enumerate ALL Objects,
  * but it allows for fast tests on a given set of Elements.
  *
  * These Algorithms are implemented by Arrays of Integers representing Objects.
  * An Alternative that can always be chosen is of course to
  * bijectively map Objects to Integers using Order.indexed[] or a HashTable.
  * The Algorithms are also implemented in List.FixLinked and List.Linked.
  *
  * The Forest can also be represented directly by an Array of Pointers,
  * but then you cannot use this Array to store the height
  * or the number of Children to balance the tree (in Java you don't have Ptrs).
  *
  * The Trees can be balanced and flattened, so their traversal gets faster,
  * so all Algorithms here are very fast,
  * running approximately as fast as the Ackermann Function grows.
  *
  * Design Decisions:
  * The Forest has to be stored.
  * This is done either by predimensioning an Array or creating a concatenated List.
  *
  * The Objects can be concatenated directly using Pointers instead of Integers,
  * but then the Height or Weight of an Element cannot be stored anymore.
  *
  * The Indication for the Root is either a Parent of (-1),
  * equivalently the null Pointer or itself as a Parent, which is used here.
  *
  * The Advantage of using an Array is that all Operations can be done in place
  * and Page Faults are very rare.
  * All these Operations can be directly performed on Objects using Linked Lists.
  * @see graphs.ILinked
  * @see streamIO.Object.Enumerator.ListItem
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: 2995c9d885f3f6b7c7359d2060d970dfbecff7bb4c48e55f94a6fea0e7932f5c
  * stale: false
  * tags: [code/disjoint_set, code/union_find]
  * concepts: [Disjoint Set / Union-Find]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
final public class DisJointSet {
	
	/** Reference to the Logger of this Class	 */
	private static final Log L = new Log(DisJointSet.class); 
	
	/**Storage for the Forest by storing the direct parent for each Element.
	 * The Elements is also be used to store either the Height or the Size of this Node's SubTree. 
	 * 
	 * To sort Objects, you first have to put these into an Array
	 * to get the Index as a Cross Reference.
	 * To directly evaluate Object pairs
	 * you have to first search both of them in the existing List
	 * and then create the Reference and possibly simplify it. 	 */
	protected int parent[];
	
	/** Increment for the Array Size	 */
	protected int increment; 
	
	/**Initializing Constructor taking the initial maximum Number of Vertices.	 */
	public DisJointSet (int maxNumObjects) {
		parent = new int[maxNumObjects];	//In the Beginning all Vertices are Roots.
//		while (--maxNumObjects >= 0) parent[maxNumObjects] =  maxNumObjects;	//This Initialization is needed, when you let the Root point to itself, which we don't do!
		while (--maxNumObjects >= 0) parent[maxNumObjects] = -1;	//This Initialization could be avoided, if the Arrays started at 1 and not 0!
	}
	
	/** 
	 * the List is reduced to a height of 1. 
	 * @return the List with the Roots of the Elements
	 * the Roots contain the Number of Elements contained in the Set
	 * as negative Numbers.  
	 */
	public int[] getRoots() { //final boolean removeChildCount) {
		for(int i = parent.length; --i >= 0; ) {
			final int par = parent[i]; 
			if (par >= 0)
				parent[i] = lastItemFastest(par); 
			//else if(removeChildCount)
			//	parent[i] = i; //st�rt lastItemFastest(), weil die Endbedingung nicht mehr erf�llt wird! 
		}
		return parent; 
	}
	
	/**Returns the (current) Root of this Element.
	 * This is the fastest way, but it does not reduce the needed time
	 * for the next Search like the other Implementations. 	 */
	public int lastItemFast(final int x) {
		checkCapacity(x);
		return lastItemFastest(x); 
	}
	
	/**Returns the (current) Root of this Element.
	 * This is the fastest way, but it does not reduce the needed time
	 * for the next Search like the other Implementations. 	 */
	protected int lastItemFastest(int x) {
		for(int p; (p = parent[x]) >= 0; ) 
			x = p; 	//!= x)	//this test is for the Root pointing to itself!
		return x; }
	
	/**checks whether the Capacity is sufficient and, if not, enlarges it. 
	 * @param x
	 */
	private void checkCapacity(final int x) {
		if (x >= parent.length) {
			int newLength = AVector.ENLARGED_CAPACITY(parent.length, increment, x); 
			final int[] newArr = new int[newLength]; 
			System.arraycopy(parent, 0, newArr, 0, parent.length);
			while (--newLength >= parent.length) 
				newArr[newLength] = -1; //newLength; 
			parent = newArr; 
		}
	}
	
	/**Returns the (current) Root of this Element.
	 * Reduces the Height of the Tree by two on every 2nd Level.
	 * This Routine forces the Root to point to itself to work properly (s.a.)!
	 * Otherwise you could use any special Value to mark the Root (e.g. -1 or 0).
	 * Since the Root Values are used for balancing too, this Routine is disabled!	 */
/*	public int lastItem2(int x)	{
		int p;
		while ((p = parent[x]) != x)
			x = (parent[x] = parent[p]);
		return x;
	}
*/	
	/**Returns the (current) Root of this Element.
	 * Reduces the Height of the Tree by one on every Level ("Halving").
	 * Uses two indexed accesses, so it is very fast too!	 */
	public int lastItem(int x) {
		checkCapacity(x);
		int p;
		if ((p = parent[x]) < 0) 
			return x;
		for (int pp; (pp = parent[p]) >= 0; ) { //&& (pp != x)) {	//this test is for both the Root pointing to itself and weighted Roots!
		    parent[x] = pp; x = p; p = pp; }
		return p; }

	/**Returns the (current) Root of this Element.
	 * Reduces the Height of the Tree to 1 on every Element of the Search Tree.
	 * Uses Recursion, so it is slower than lastItem on the first Operation,
     * because these Array Operations are relatively cheap.	 */
	public int lastItemTotal(final int x) {
		checkCapacity(x);
		final int p;
		if ((p = parent[x]) < 0) 
			return x;
		final int pp; 
        parent[x] = (pp = lastItemTotal(p)); //find the root and set it to all Elements on the way back.
		return pp; }
	
	/**Checks both Elements for Equivalence by comparing their Roots. 	 */
	public boolean equals(int x, int y) { return equals(x, y, false); }
	
	/**Checks both Elements for Equivalence by comparing their Roots.
	 * After this they are optionally united using the gathered Information.	 
	 * Simple Implementation, not weighting the Tree, so it can be imbalanced! */
	public boolean equals(int x, int y, final boolean union) {
        boolean ret = ((x == y) ||
				(x = lastItem(x)) ==	//x = lastItemFast(x);
				(y = lastItem(y)));		//y = lastItemFast(y);
		if ((union) &&  !ret)
            parent[x] = y; //or parent[y] = x; ...
		return  ret; }
	
	/**Checks both Elements for Equivalence by comparing their Roots.
	 * After this they are optionally united using the gathered Information.
	 * The union is done in a balanced way, storing the Number of child Elements
	 * as negative Values in parent[].
	 * This leads to a reduced average time on the next Search 
	 * and ensures a Runtime linear to the Number of Edges!	 */
	public boolean equalsAVG(int x, int y, boolean union) {
        boolean ret = ((x == y) ||
				(x = lastItem(x)) ==	//x = lastItemFast(x);
				(y = lastItem(y)));		//y = lastItemFast(y);
		if (union &&  !ret) {	//concatenate both trees so the resulting Tree is wider.
			if  (parent[x] < parent[y])
				{parent[x]+= parent[y] -1; parent[y] = x; }
			else{parent[y]+= parent[x] -1; parent[x] = y; }
		} return ret; }
	
	/**Checks both Elements for Equivalence by comparing their Roots.
	 * After this they are optionally united using the gathered Information.
	 * The union is done in a balanced way, storing the Height of the Subtree
	 * as negative Values in parent[].
	 * This leads to a reduced maximum time on the next Search (worst case).	 */
	public boolean equalsMAX(int x, int y, boolean union) {
		x = lastItem(x);
		y = lastItem(y);
		if (union && (x != y)) {	//concatenate both trees so the resulting Tree is smaller.
			if  (parent[x] < parent[y])
				{parent[x]--;parent[y] = x;}
			else{parent[y]--;parent[x] = y;}
		} return (x == y); }
	
	///////////////////////////////////////////////////////////////////////////
	
	private static final char[][] Edges ={
			{'A','G'},
			{'A','B'},
			{'A','C'},
			{'L','M'},
			{'J','M'},
			{'J','L'},
			{'J','K'},
			{'E','D'},
			{'F','D'},
			{'H','I'},
			{'F','E'},
			{'A','F'},
			{'G','E'},
	//};
	//private static final char[][] Edges2={
			{'G','C'},	//neu Vereinigung zu einer einzigen Komponente
			{'G','H'}, 
			{'J','G'},		//these last two Elements
			{'J','L'}};	//differ from the Example in MatrixGraph!!!
	
	private static final boolean[] expected = {
			false, false, false, false, false, true, 
			false, false, false, false, true, false, true ,
			true, false, false, true
	}; 
	
	/**Tests all Methods of this Class	 */
	public static void testIt() {
		L.n("\nTesting DisJointSet:");
		L.n("Testing 'equals':");
		DisJointSet EQ = new DisJointSet(13);
		for(int i = -1; ++i < Edges .length; ) 
            Assert.EQUALS(expected[i], EQ.equals(
            		Edges [i][1]-'A',
					Edges [i][0]-'A', true));
		L.n("\nTesting 'equalsAvg':");
		EQ = new DisJointSet(13);
		for(int i = -1; ++i < Edges .length; ) 
            Assert.EQUALS(expected[i], EQ.equalsAVG(
					Edges [i][1]-'A',
					Edges [i][0]-'A', true));
		L.n("\nTesting 'equalsMax':");
		EQ = new DisJointSet(13);
		for(int i = -1; ++i < Edges .length; ) 
            Assert.EQUALS(expected[i], EQ.equalsMAX(
					Edges [i][1]-'A', 
					Edges [i][0]-'A', true));
		L.n(); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
		if (args.length == 0)
			testIt();
	}
	
}
