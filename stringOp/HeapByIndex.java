package stringOp;

import streamIO.Assert;
import streamIO.Log;
import function.IOrderAble;

/** Implements a Heap based on an Array of Indices
  * to the actual Array of Elements, which is never changed.
  * The Advantage here is that external Code
  * doesn't have to maintain a separate Copy of the Array of Elements
  * and directly determines the Position k of an Element,
  * which is necessary, when the Elements are not only inserted,
  * but updated and thus have to be moved within the Heap.
  * Even when the Heap Position is stored in the Objects,
  * which is not the proper location, since it could be in several Heaps,
  * you still would have to make sure you find this Object to update it.
  * The Heap can then determine the Position in the Heap l = p[k]
  * or from the Position l in the Heap the Position in the Array k = q[l].
  *
  * A Heap is a complete binary Tree that fulfills the Heap Condition:
  * any Item is larger than the two Items to the left and right of it.
  *
  * The Heap here ist implemented in an Array,
  * because the Completeness(!!!) of the binary Tree
  * allows for a very simple Addressing of the two Children and the Parent:
  * Parent[i] = a[i/2]
  * lChild[i] = a[i*2], rChild[i] = a[i*2+1]
  *
  * The Number of Levels in a Heap is thus always ((int) lb(N))+1
  * a[0] contains a Sentinel that ensures the End of the Operations.
  *
  * Heaps can be used for Sorting and for implementing a Priority Queue.
  * Apart from that a complete binary Tree is quite useless.
  * A Priority Queue knows only which is the largest Element.
  * Sorting the complete Set is as expensive as a good Sorting Routine
  * (e.g. QuickSort), but the real Benefit lies in the Capability of
  * finding out the largest Element in only linear time
  * (also possible with linear Search!),
  * while preparing for getting the next largest Element.
  * Finding the Position of an Element to delete or replace it is a Problem!
  * For this you need the inverse Permutation of the Array p,
  * i.e. a direct Mapping of the Object to the Index.
  * The Permutation can be generated during downHeap, delete, insert and change.
  * It is easiest to store this inverse Mapping
  * by storing the Heap Position in the Object itself like done in 'Heap'! 	
  */
public class HeapByIndex {
	
	private static final Log L = new Log(HeapByIndex.class, 0); 
	
	/**Default initial Size of the Heap when no Size is given	 */
	final static public int DEFAULT_INIT_SIZE = 16;
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Local Storage for the Objects in the Heap.
	 * Indexed by p, inversely by q.	 */
	protected IOrderAble a[];
	
	/**Local Storage for the Permutation resp. Priority of Elements in a[].	 */
	protected int p[];
	
	/**Local Storage for the inverse Permutation to find the Elements in a[].	 */
	protected int q[];
	
	/**Current Number of Elements in the Heap	 */
	protected int length;
	
	/**Initializing Constructor allocating Space for the Queue	 */
	public HeapByIndex ()	{ this(DEFAULT_INIT_SIZE); }
	
	/**Initializing Constructor allocating Space for the Queue	 */
	public HeapByIndex (final int n)	{	//p and q are equally long, a can be longer
		a = new IOrderAble[n+1];
		p = new int[n+1];
		q = new int[n+1];
	}
	
	/**Initializing Constructor being filled from an Array of int Elements.
	 * This is performed with linear Amount	 */
	public HeapByIndex (final IOrderAble[] arr) { this (arr, arr.length); }
	
	/**Initializing Constructor being filled from an Array of int Elements.
	 * This is performed with linear Amount	 */
	public HeapByIndex (final IOrderAble[] arr, final int length_) {
		this(arr.length);
		//a = arr;
		this.length = length_;	
		System.arraycopy(arr, 0 , a, 0, length);
		for (int i = length_; --i > 0; ) 
			p[i] = q[i] = i; 
		for (int i = length_ >> 1; --i >  0;) //The last Half consists of Heaps with Size 1
			downHeap(i); //...which are implicitly heapified!
	}
	
	/**Returns true, when the Queue is empty	 */
	public boolean isZero(){ return (length == 0); }
	
	/**Returns the Contents of the Heap as a sorted Array.
	 * This is where the main work takes place of Order N*lb(N).
	 * This can also be done in Place by exchanging a[0] and a[N]. 	 */
	public IOrderAble[] getSorted() {
		final IOrderAble[] ret = new IOrderAble[length];
		for (int i = length; --i >= 0;)
			ret[i] = a[get()];
		return ret; }
	
	/**Returns the Index of the sorted Array.
	 * This is where the main work takes place of Order N*lb(N).
	 * This can also be done in Place by exchanging a[0] and a[N]. 	 */
	public int[] getIndex()	{
		final int[] ret = new int[length];
		for (int i = length; --i >= 0;)
			ret[i] = get();
		return ret; }
	
	protected boolean ensureCapacityA(final int n) {
		if (n < a.length) 
			return true;
		final IOrderAble ta[] = new IOrderAble[n+1];
		System.arraycopy(a, 0, ta, 0, a.length); a = ta;
		return false; 
	}

	/**Extends the Length of the Heap by allocating new Space	 */
	public int ensureCapacity(final int n) {
		if (n < p.length) 
			return p.length-1;
		ensureCapacityA(n);
		final int tp[] = new int[n+1]; System.arraycopy(p, 0, tp, 0, p.length); p = tp;
		final int tq[] = new int[n+1]; System.arraycopy(q, 0, tq, 0, q.length); q = tq;
		//Initialisierung der Permutation, notwendig? oder erst beim Insert?
		return n; }

	/**Corrects Errors in the Heap structure
	 * going from the (changed) Node k up to the Root.
	 * This is a lb(N) Operation typically called after appending Data.
	 * Replacing k >>= 1 by k-=1 results in Insertion Sort.	 
	 */
	public void upHeap(int k) {
		final int v = p[k];	//v is the Pivot which has to be sorted into the Heap
		final IOrderAble w = a[v];	//w is the Index of the Pivot which has to be sorted into the Heap
//		a[p[0] = (int)
//			  ((wellOrder)a[p[1]]).maxValue();	//Sentinel to ensure the End of the Loop at a[p[1] and not a[p[0].
		p[0] = w.isMoreThan(a[p[1]]) ? v : p[1];
		int i = k;
		while (a[p[k >>= 1]].isLessThan(w)) {	//whenever the Element is not greater or equal to the Pivot,
		    p[i] = p[k]; q[p[k]] = i; i = k;}	//move it to it's Child.
		    p[i] =   v ; q[  v ] = i; 	//move the Pivot to it's correct Position
	}

	/**Corrects Errors in the Heap structure
	 * going from the Root (or the Element k) down to the Nodes.
	 * This is a lb(N) Operation typically called after removing Data from the Root.
	 */
	public void downHeap(int k) {
		int j;
		int v = p[k];	//v is the Pivot which has to be sorted into the Heap
		IOrderAble w = a[v];	//v is the Pivot which has to be sorted into the Heap
		int n = length >> 1;	//N/2, frequently used for Comparison
		while (k <= n) {
			j = k << 1;
			if (j < length) if (a[p[j]].isLessThan(a[p[j+1]])) j++;	//get the larger of both Children
			if (a[p[j]].notMoreThan(w)) break;	//break, when both Children are smaller than the Pivot
			p[k] = p[j]; q[p[j]] = k; k = j;	//Move the larger Child up the Heap
		}
			p[k] = v;	 q[v] = k;	//move the Pivot to it's correct Position
	}
	
	/**Inserts arg into the Heap structure	 */
	public void insert(final IOrderAble arg) { insert(arg, length+1); }
	
	/**Inserts arg into the Array and the Heap structure and keeps k
	 * as a Reference to an external Array...	 
	 */
	public void insert(final IOrderAble arg, int k) {
		if (++length >= p.length) 
			ensureCapacity(length << 1);
		ensureCapacity(k);
		p[length] = q[length] = k;
		a[k] = arg;
		upHeap(length); 
	}
	
	/** @return the Priority of the Element k in the Heap structure	 
	 */
	public IOrderAble getAt(final int k) { return a[k]; }
	
	/**Removes and returns the Index of the largest Element from the Heap structure	 
	 * @see #getAt(int) to retrieve the Priority of this Element
	 */
	public int get() {
		final int ret = p[1]; p[1] = p[length--];	//replace the first by the last Item
		downHeap(1);	//and restore the Heap Property
		return ret;
	}
	
	/** 
	 *  return the Top of the Heap i.e. the next Item to be returned by get()
	 * @return the Top of the Heap i.e. the next Item to be returned by get() 
	 */
	public int peek() { return p[1]; }
	
	/**Replaces the Top Element in the Heap structure by arg 	 */
	public int replace(final int arg) {
		final int ret = p[0]; p[0] = arg;		//replace the top Element
		downHeap(0);	//and restore the Heap Property,
		return ret; }	//using the degenerated downHeap: the Children of a[p[0]] are a[p[0]] and a[p[1]]!
	
	/**Deletes the Element at Position k from the Heap structure.
	 * The Problem is finding out the Position k of an arbitrary Object o.	 */
	public int delete(final int k) {
		final int tmp = p[length--]; //Replace Element k by the last Element...
		return change(tmp, k); }	//...and remove that one.
	
	/**Changes the Value of the Element at Position k to the one at Position arg
	 * and restores the Heap structure.
	 */
	public int change(final int arg, final int k) {
		final int ret = p[k]; p[k] = arg ;	//set the Value
		if (a[ret].isMoreThan(a[arg]))
				upHeap(k);	//depending on the actual Change
		else  downHeap(k);	//either move the new Value up or down the Heap
		return ret; }
	
	/**Changes the Value of the Element at Position k to Position arg
	 * and restores the Heap structure.
	 * The Problem is finding out the Position k of an arbitrary Object o.	 */
	public IOrderAble change(final IOrderAble arg, final int k) {
		final IOrderAble ret = a[k]; a[k] = arg ;	//set the Value
//		boolean grtr;
//		if (grtr = (Return.grtr(arg)))
		if (arg.isMoreThan(ret))	//if new Value is greater
				upHeap(q[k]);	//move it up the Heap
		else  downHeap(q[k]);	//else move the new Value down the Heap
		return ret; }
	
	/**Updates the Priority of the Element at Position k to arg
	 * to at most Prio (Priority can only decrease by new Information)
	 * and restores the Heap structure.
	 * The Problem is finding out the Position k of an arbitrary Object o
	 * in the Array and the Position in the Heap using q[k].	 
	 * 
	 * @param arg the new Priority at Position k
	 * @param k the Position of the Object with the updated Priority 
	 * @return arg, if it was higher than the existing value and nothing changed, 
	 * the latter otherwise (null when not existing)  
	 */
	public IOrderAble update(final IOrderAble arg, final int k) {
		if (!ensureCapacityA(k) || (a[k] == null)) {
			insert(arg, k); return null; }
		if (a[k].notLessThan(arg)) 
			return arg;	//if old Value is larger, no change
		return change(arg, k);
		/*
		final OrderAble ret = a[k]; a[k] = arg;	//replace the Value, if smaller,
		downHeap(q[k]);	//move the new Value down the Heap (because it is smaller)
		return ret;
		*/ 
	}
	
	/**Returns a String Representation of this Object	 */
	public String toString() {
		final StringBuffer S = new StringBuffer();
		for (int i = length+1; --i > 0;) 
			S.append(a[p[i]].toString()).append('[').append(p[i]).append("],");
		return S.toString(); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Testing and main() Methods
	/////////////////////////////////////////////////////////////////////////////////////

	static String Example	= "asortingexample";
	static String Expected = "xtpgsonaeraimle";
	static String Sorted	= "aaeegilmnoprstx";
	
	/**Tests all Methods of this Class	 */
	public static void testIt() {
		L.enter();
		L.n("Input String:").l(Example);
		final HeapByIndex HP = new HeapByIndex(Example.length());
		for (int i = -1; ++i < Example.length();)
			HP.insert(new function.byref.ByRefChar(Example.charAt(i)));
		L.n("expected Heap Structure:");
		for (int i =  0; ++i <= Example.length();) 
			Assert.EQUALS(new function.byref.ByRefChar(Expected.charAt(i-1)), HP.a[HP.p[i]]);
		IOrderAble[] ret = HP.getSorted(); 
		for (int i =  0; ++i < Example.length();) 
			Assert.EQUALS(new function.byref.ByRefChar(Sorted.charAt(i)), ret[i]);
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args)	{
		HeapByIndex   .testIt();
	}
	
}
