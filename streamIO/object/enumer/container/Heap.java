package streamIO.object.enumer.container;

import streamIO.IIStreamOut;
import streamIO.copy.ICopyAble;
import function.IIOrderAble;
import function.byref.ByRefChar;
import function.index.IIndexAble;

/** Implements a Heap based on an Array of Objects (Pointers).
  *
  * A Heap is a Priority Queue that can thus be used for sorting etc.
  * The advantage of using a Heap is that the Sorting Effort is distributed
  * between the Insert Operation
  * and the Extract Operation, so it can also work streaming!
  * 
  * Putting a Heap with given max. Capacity into a Stream 
  * partially sorts it descending. 
  * The Priority Queue always returns the max. Element 
  * and the incoming Elements are sorted into the Tree. 
  * Newly incoming higher Elements can disturb the Order 
  * and need to be ignored or cached for the next Tranche. 
  * Given a uniform Distribution the Probability for a higher Element 
  * falls with the number of processed Elements (cached or delivered), 
  * so Processing can go on almost undisturbed after an initial Number is cached. 
  * 
  * When the Number of ignored Elements exceeds a certain Limit 
  * it should be evaluated. 
  * 
  * Thus intermediate Changes in the Data (like concurrent adding and removing)
  * does not result in too much overhead in restructuring.
  * Especially the Insert Operation is relatively quick, only O(log N).
  * The Problem with Pointers is that no level of Indirection is possible
  * to maintain the Priority List like with HeapIndx.
  * This is used best when the Elements don't change their Value
  * or are maintained in a different structure in the calling Code.
  * 
  * If the Heap items are updated (Value is changed),
  * it's Priority has to change too and for this you have to call
  * change(k) with the Heap Position k
  * which could be stored in the Object itself, when of Interface 'indexed'.
  * The foreign Code still has to handle the Objects to be able to find it.
  * HeapIndx keeps them in an Array which allows for indexed Access.
  *
  * A Heap is a complete binary Tree that fulfills this Heap Condition:
  * any Item is larger than the two Items to the left and right of it.
  * Thus the Item at the Top is the largest in the Tree. 
  *
  * The Heap here can be implemented in an Array,
  * because the Completeness(!!!) of the binary Tree (not so with red-black Trees)
  * allows for a very simple Addressing of the two Children and the Parent:
  * Parent[i] = a[i/2]
  * lChild[i] = a[i*2], rChild[i] = a[i*2+1]
  *
  * The Number of Levels in a Heap is thus always ((int) lb(N))+1
  * a[0] contains a Sentinel that ensures the End of the Operations.
  *
  * Heaps can be used for (pre-)Sorting and for implementing a Priority Queue.
  * Apart from that a complete binary Tree is quite useless.
  * A Priority Queue knows only which is the largest Element.
  * Sorting the complete Set is as expensive as a good Sorting Routine
  * (e.g. QuickSort), but the real Benefit lies in the Capability of
  * finding out the largest Element in only linear time
  * (also possible with linear Search!),
  * while preparing for getting the next largest Element.
  * Finding the Position of an Element to delete or replace it is a Problem!
  * For this you need the inverse Permutation of the Array a,
  * i.e. a direct Mapping of the Object to the Index.
  * The Permutation can be generated during downHeap, delete, put and change.
  * It is easiest to store this inverse Mapping
  * by storing the Heap Position in the Object itself!
  *
  * If the Set is completely unordered (less and grtr always return false),
  * this Class works like a Stack. If grtr is not implemented,
  * it works like a Mixture between Heap and Stack, i.e. the first Half
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  * is returned in Order and the second Half in Reverse. 	 */
public class Heap
extends AContainer
//implements TestStore //, IPQueue
{
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Default Size of the Heap when no Size is given	 */
	final static public int defaultSize = 16;
	
	/** Default Order for the Sorting	*/
	final static public boolean defaultOrderUp = true;
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Flag determining whether this Heap is sorted for Maximum or Minimum
	  * can alsobe simulated by negating the Comparison Function...	 */
	protected boolean Minimum = true;

	/** Local Storage for the Heap	 */
	protected IIOrderAble[] a;

	/** Current Number of Elements in the Heap	 */
	protected int N;

	/** If set, maintains the Index structure of the Heap Elements	 */
	public boolean isIndexed;

	/** Initializing Constructor allocating Space for the Queue	 */
	public Heap () { this(defaultSize, defaultOrderUp); }

	/** Initializing Constructor allocating Space for the Queue	 */
	public Heap (int n) { this(n, defaultOrderUp); }

	/**Initializing Constructor being filled from an Array of IOrderAble Elements.
	 * This is performed with linear Amount	 */
	public Heap (IIOrderAble[] arr) { this(arr, defaultOrderUp); }

	/**Initializing Constructor allocating Space for the Queue	 */
	public Heap (boolean OrderUp) { this(defaultSize, OrderUp); }

	/**Initializing Constructor allocating Space for the Queue	 */
	public Heap (int n, boolean OrderUp) { a = new IIOrderAble[n+1]; Minimum = OrderUp; }

	/**Initializing Constructor being filled from an Array of IOrderAble Elements.
	 * This is performed with linear Amount	 */
	public Heap (IIOrderAble[] arr, boolean OrderUp) {
		Minimum = OrderUp;
		int i = N = arr.length;
		a = new IIOrderAble[i+1];
		System.arraycopy(arr, 0 , a, 0, i);
		i >>= 1; while (--i >  0) downHeap(i);	//The last Half consists of Heaps with Size 1, they are already heapified!
//				 while (--i >= 0) put(arr[i]);	//on average O(N), but at worst O(N*lb(N)) !!!
	}

	/** Tests, whether this Object exists in the Set,
	  * @return the Object, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findFirst(final Object item) { //throws NoSuchMethodException {
		int i = N+1;
		while(--i >= 0) {
			if (item.equals(a[i])) {
				return a[i];
			}
		}
		return EOI; 
	}	

	/** Tests, by linear Search, whether an equal Object is stored in this Heap.
	  * @return true when this Object is contained in this Container
	  * This is the same Operation as (findFirst() != EOI) || (available() >= 0)
	  * @see Sub() and SubEq() for the according Container Methods,
	  * The Name contains() is only to be used for single Elements
	  */
	public boolean contains(final Object item) { return findFirst(item) != EOI; }

	/** Creates a new, empty Heap with the default Size and Order.
	 * @return a new (uninitialized) Instance of this Class	  */
	public ICopyAble newInstance() {
		return new Heap(); }

	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////

	/** Returns the current Element Count.
	 * @return the Number of Items in the Queue	 */
	public long availAble() { return N; }

	/** Returns the Contents of the Heap as a reverse sorted Array.
	  * This is where the main work takes place of Order N*lb(N).
	  * This can also be done in Place by exchanging a[0] and a[N]. 	 */
	public IIOrderAble[] getSorted() {
		IIOrderAble[] Return = new IIOrderAble[N];
		int i = N;
		while (--i >= 0) //reverses the Order!
			Return[i] = (IIOrderAble) nextItem();
		return Return; }

	/**Sets the Index of each Element within the Heap.
	 * Useful to determine the Permutation and the Inverse.
	 * For this each Item must be of Type 'indexed'	 */
	public void setIndex() {
		int i = 0;
		while (++i <= N)
			((IIndexAble) a[i]).setNdx(i);
	}

	/** Extends the Length of the Heap by allocating new Space
	  * @return the actual Capacity which should be higher than N	 */
	public int setCapacity(int N) {
		if (N < a.length) return a.length-1;
		IIOrderAble t[] = new IIOrderAble[N+1];
		System.arraycopy(a, 0, t, 0, a.length);
		a = t;
		return N; }

	/**
	  * Returns the current Element Count, not the backing Array's allocated Length.
	  * @return the actual Capacity of this Heap	 */
	public int getCapacity() { return N; }

	/**Corrects Errors in the Heap structure
	 * going from the (changed) Node k up to the Root.
	 * This is a lb(N) Operation being used on put().
	 * Replacing k >>= 1 by k-=1 results in Insertion Sort.	 */
	public void upHeap(int k) {
		IIOrderAble v = a[k];	//v is the Pivot which has to be sorted into the Heap
//		a[0] = (IOrderAble)
//			  ((wellOrder)a[1]).maxValue();	//Sentinel to ensure the End of the Loop at a[1] and not a[0].
		a[0] = (a[1].isLessThan(v) ^ Minimum) ? v : a[1];
		int l = k;
		while(a[k >>= 1].isLessThan(v) ^ Minimum) { 	//while the Elements are not greater or equal to the Pivot,
			a[l] = a[k]; if (isIndexed) ((IIndexAble) a[l]).setNdx(l); if ((l = k) == 0) break; }	//move it to it's Child.
		 	a[l] = v   ; if (isIndexed) ((IIndexAble) a[l]).setNdx(l); //move the Pivot to it's correct Position
	}

	/**Ensures the Heap Property after importing a new Array
	 * or changing large Portions	 */
	public void heapify() { //The last Half consists of Heaps with Size 1,
        int i = N >> 1; while (--i >  0) downHeap(i);}	//they are already heapified!

	/** Corrects Errors in the Heap structure
	  * going from the Root (or the Element k) down to the Nodes.
	  * This is a lb(N) Operation being used on get(). 	 */
	public void downHeap(int k)	{
		int j;
		IIOrderAble v = a[k];	//v is the Pivot which has to be sorted into the Heap
		int n = N >> 1;	//N/2, frequently used for Comparison
		while (k <= n) {
			j = k << 1;
			if (j < N) if (a[j].isLessThan(a[j+1]) ^ Minimum) j++;	//get the larger of both Children
//			if (! ((orderAble)a[j]).grtr(v) ^ Minimum) break;	//break, when both Children are smaller than the Pivot
//			if((a[j].less(v) ^ Minimum) || a[j].equals(v)) break;	//
			if (a[j].isLessThan(v) ^ Minimum) break;	//a bit faster than above, since equals is not used.
			a[k] = a[j]; if (isIndexed) ((IIndexAble) a[k]).setNdx(k); k = j;	//Move the larger Child up the Heap
		}
			a[k] = v   ; if (isIndexed) ((IIndexAble) a[k]).setNdx(k); 	//move the Pivot to it's correct Position
	}

	/** Inserts arg into the Heap structure	 */
	public IIStreamOut addItem(Object arg) {
		if (++N >= a.length) {
			setCapacity(N << 1);
		}
		a[N] = (IIOrderAble) arg; //if (indexed) ((indexed) a[N]).setIndex(N);	//not necessary!
		upHeap(N);
        return this; }

	/** Removes and returns the largest Element from the Heap structure
	  * Since there is no Enumerator on the Heap, this is directly executed. */
	public Object nextItem() {
		if (N < 1) {
			return null;
		}
		IIOrderAble Return = a[1]; a[1] = a[N--];	//replace the first by the last Item
		downHeap(1);	//and restore the Heap Property
		return Return; }	//This makes the Heap work like a Stack when grtr and less always return false

	/**Returns the Flag that indicates, whether the Operations above
	 * work in LIFO (Stack) Fashion (true )
	 * or	in FIFO (Queue) Fashion (false)	 */
	public boolean getStack(){return false;}

	/**Clears the Store	 */
	public void clear(){N = 0;}

	/**Returns the next Item of the Store without removing it.	 */
	public Object peek(){ return a[1]; }

	/**Replaces the largest Element by arg in the Heap structure	 */
	public IIOrderAble replace(IIOrderAble arg) {
		IIOrderAble Return = a[0]; a[0] = arg;		//insert the new Element
		downHeap(0);	//and restore the Heap Property,
		return Return; }	//using the degenerated downHeap: the Children of a[0] are a[0] and a[1]!

	/**Deletes the Element at Position k from the Heap structure.
	 * The Problem is finding out the Position k of an arbitrary Object o.	 */
	public IIOrderAble delete(int k) {
		IIOrderAble tmp = a[N--];
		return change(tmp, k); }	//Replace Element k by the last Element and remove that one.

	/**Changes the Value of the Element at Position k to arg
	 * and restores the Heap structure.
	 * The Problem is finding out the Position k of an arbitrary Object o.	 */
	public IIOrderAble change(IIOrderAble arg, int k) {
		IIOrderAble Return = a[k]; a[k] = arg ;	//set the Value
		if (arg.isLessThan(Return) ^ Minimum)
			   upHeap(k);	//depending on the actual Change
		else downHeap(k);	//either move the new Value up or down the Heap
		return Return; }

	/**Updates the Value of the Element at Position k to Priority arg
	 * and restores the Heap structure.
	 * The Problem is finding out the Position k of an arbitrary Object o.	 */
	public boolean update(IIOrderAble arg, int k) {
/*		int i = N+1;
		while (--i > 0)
			if (arg.equals(a[i]))
				{boolean Return; if (Return = (i > k)) change(arg, k); return Return;}
		put(arg);
		return true;	//not found, inserted
*/		if (k > N){ addItem(arg); return true;}
		if((a[k].isLessThan  (arg) ^ Minimum) ||
			a[k].equals(arg)) return false;
			a[k]	=   arg ;	//replace the Value, if larger
		downHeap(k);	//either move the new Value down the Heap (because it is smaller)
		return true; }

	/**Returns a String Representation of this Object	 */
	public String toString() {
		StringBuffer S = new StringBuffer();
		int i = N+1;
		while(--i > 0) S.append(a[i].toString()).append(',');
		return S.toString(); }

	/**Returns the number of Item stored in the Store	 */
	public int getInt() { return N; }

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
			
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Heap.class.getName());
		String Example	= "asortingexample";
		String Expected = "xtpgsonaeraimle";
		String Sorted	= "aaeegilmnoprstx";
		System.out.println("Testing Heap:");
		int i = -1;
//		ByRefChar c;
		Heap HP = new Heap(Example.length());
				while (++i < Example.length())
					HP.addItem(new ByRefChar(Example.charAt(i)));
		System.out.println("Heap Structure:	expected: " + Expected);
		i =  0;	while (++i <= Example.length()) System.out.print(HP.a[i]);
												System.out.println();
		System.out.println("Sorted String: expected: " + Sorted);
		IIOrderAble[] arr = HP.getSorted();
		i = -1;	while (++i < Example.length())  System.out.print(arr[i]);
												System.out.println();
	}

}
