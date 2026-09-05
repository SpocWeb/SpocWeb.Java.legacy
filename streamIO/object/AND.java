package streamIO.object;

import streamIO.IIStreamIn;

/**
  * Streams only the items of the wrapped input that also occur in a second input stream,
  * implementing set intersection over object streams.
  * <p>
  * Title: AND.java<p>
  * Description:
  * Implements the AND Operation / Intersection on Object Streams.
  * Creates a streamIO that contains only those Elements contained in both Streams.
  *
  * XOR and OR cannot be realized in infinite Streams,
  * without using a Random Access, fast find Container like a HashTable,
  * that act as a Set for easier checking of Existence.
  * Rather use a Union or an XUnion that streams into a Set Container to prevent Duplicates.
  *
  * For efficient union and intersection operations,
  * it pays to keep the elements in each subset sorted by an intrinsic 1D Metric,
  * so a linear-time traversal through both subsets identifies all duplicates
  * (i.e. AND and OR are O(N+M) Algorithms instead of an (N*M) Algorithms.
  *  Of course sorting is an O(N log N) Problem of its own, but it still pays off!
  *  Only in infinite Sets it is not possible to sort,
  *  unless the streamIO fulfills the Monotony Criterion!)
  * This corresponds to the divide and conquer Method used for sort and merge.
  *
  * Known SubClasses:
  *
  * @see streamIO.object.Union
  * @see AND
  * @see streamIO.object.DIFF
  * @see streamIO.object.Product
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-06, 07;03;52<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:29:57Z
  * digest: 3bacffe138cb55d58ead36faa83f4d5062b82594e32f0fcd2df60af5d232f2c6
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class AND
extends AFilterIn {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the second Input streamIO */
	protected final IStreamIn enum2;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public AND(final IIStreamIn _enum, final IStreamIn _enum2) {
		super(_enum); //((StreamIn) _enum).Iterator()); //create a Copy not to disturb other Processes
		this.enum2 =(IStreamIn) _enum2.Iterator(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Number of (currently available) Items,
	  * may return 0 at the End of a constituent streamIO. 	 */
	//public long availAble() { return ((IAvailAble)in).availAble(); }
	
	/**Returns the next Object (Parent) of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() {
	//	Object Item2;
		while ((EOI !=(currItem = in .nextItem())) || in .isValid()) { 
			if (EOI != FIND(enum2, currItem)) //when found...
				return currItem; } //never returns nulls!
		return null; } //except for at the End
	/*		//use findFirst here!
			Enum2.reset();
			while (true) {
				if((EOI       ==    (Item2 = Enum2.nextItem())) && !Enum2.isValid()) return currItem;
				if (currItem  ==     Item2) break; //found, stop searching
				if (EOI       ==     Item2) //return currItem;
				if (EOI       !=     Item2)
				if (currItem .equals(Item2))break;
			}
		} return currItem; } //shoud be EOI here!
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Runs a smoke test that prints this class's name to standard output.
	 */
	public static void testIt() throws java.io.IOException {
		System.out.println("Testing " + AND.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		testIt(); }
	
}
