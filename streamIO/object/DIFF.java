package streamIO.object;

import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IReSetAble;

/**
  * Streams only the items of the wrapped (possibly infinite) input that do not occur in a
  * second, finite input stream, implementing set difference over object streams.
  * <p>
  * Title: DIFF.java<p>
  * Description:
  * Implements the DIFF Operation on Object Streams.
  * Creates a streamIO that contains only those Elements 
  * of the first (possibly infinite) streamIO,
  * that is not contained in the second (finite) streamIO;
  *
  * Known SubClasses:
  *
  * @see also:	Union, AND, DIFF, Product
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-06, 07;03;52<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:39:15Z
  * digest: a24a9a35a6a2c22745360d8653c0ef58b29ab57f5b6f5e6aee94507a8c14e377
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
final public class DIFF
extends AFilterIn {

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the second Input streamIO */
	protected  IStreamIn enum2;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator
	  */
	public IIStreamIn Iterator() {
		final DIFF ret = (DIFF) super.Iterator(); //first create the (outer) Filter...
		ret.enum2 = (IStreamIn)  enum2.Iterator(); //...then the inner Stream
		return ret; }

	/** Initializing Constructor	 */
	public DIFF(IIStreamIn _enum, IStreamIn _enum2) {
		super(_enum); //((StreamIn) _enum).Iterator()); //create a Copy not to disturb other Processes
		this.enum2 =(IStreamIn) _enum2.Iterator(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Number of (currently available) Items,
	  * may return 0 at the End of a constituent streamIO. 	 */
	public long availAble() {
		if (in == null) {
			return 0; } //empty Set
		return ((IAvailAble)in).availAble(); }
	
	/** Returns the next Object that is not contained in Enum2.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * This is less explicit, but much faster for a regular Operation
	  * because Exception Handling can be extremely slow.
	  */
	protected Object nextItemInternal() {
	//	Object Item2;
		if (in == null) 
			return null; //empty Set
		while ((EOI != (currItem = in .nextItem())) || in .isValid()) {
			if (enum2 == null) 
				return currItem; // empty Set
			enum2.reSet();
			if (EOI == FIND_NEXT(enum2, currItem)) //when not found...
				return currItem; } //always returns nulls!
		return null; } //except for at the End
	
	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		super.reSet(); //((StreamIn) Enum).reset(); currItem = SOI;
		enum2.reSet(); //curr.Key = Enum.nextItem(); }
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + DIFF.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
