package streamIO.object;

import graphs.PairVal;
import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IReSetAble;

/** This Class creates a simple rectangular full Cross Product streamIO 
 * of two possibly infinite Input Streams into a streamIO of Pairs. 
 * The Pair Class is implemented lightweight.
 * To multiply more Streams, recursively merge the single Streams.
 * The Result is ordered into Rows and Columns (and Slices with higher Products).
 *
 * The second streamIO must be of finite Size,
 * otherwise only the first Column can be reached in a finite Time!
 * @see streamIO.Object.Cantor
 * for multiplying infinite Streams using Cantor's Diagonal Algorithm. 
 * That Product is ordered into Diagonals, not Rows and Columns,
 * thus reaches every Value in a finite Time!
 * 
 * @see streamIO.object.MultiplexerOut for a Division of Streams.
 * This partitions/maps the Elements into a Set of SubStreams 
 * with possible Remainders. 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:31Z
 * digest: 046c2ba99d800599ae254c98e93e70f93495d31a1af1f330b88ac1ef31b869b1
 * stale: false
 * tags: [code/stream_processing, code/iterator]
 * concepts: [Object Stream Pipeline]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Product
extends AFilterIn {

	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the second Input streamIO */
	protected IStreamIn mIn2;

	/** Maximum Length of the Enum2 streamIO */
	protected long enum2Length;

	/** Flag for the End of the multiplied streamIO  */
//	protected boolean finished = false;

	/** 
	 * Return Element re-used for each Element of the merged Input streamIO 
	 * This Instance is being reused between different Calls of nextItem
	 * This saves Creation / Destruction
	 * and reuse can be tolerated often (especially when flattening the Result),
	 * otherwise a copyFilter should be appended
	 */
	protected PairVal curr = new PairVal();

	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////

	/** Creates new Union */
	public Product (IIStreamIn Stream1, IStreamIn Stream2) throws NoSuchMethodException {
		super  (Stream1); //create a Copy not to disturb other Processes
		this.mIn2 =(IStreamIn) Stream2.Iterator(); //create a distinct new Iterator to operate independently!
		mIn2.reSet(); //goto the Beginning to get the correct Length and fail fast (already in the Constructor)
        enum2Length = mIn2.availAble(); //Minimum Length of this Stream
//    	curr.Key = Enum.nextItem();
	}

	////////////////////////////////////////////////////////////////////////////
	//  Delegation
	////////////////////////////////////////////////////////////////////////////

	/** Returns the Number of (currently available) Items,
	  * may return 0 at the End of a constituent streamIO. 	 */
	public long availAble() {
		if ((curr.Key   == EOI) && !in .isValid()) { return -1; }//first Stream at End
//		if ((curr.Value == EOI) && !Enum2.isValid()) { return -1; }
		return (1+((IAvailAble)in).availAble())*enum2Length; } //Second Stream of Size 0

	/**Returns the next Object (Parent) of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() {
		while (true) { //search for a Pair while available() in both Streams.
//			currItem = (curr = new Pair(curr.Key, currItem = Enum2.nextItem()));
//			if (EOI !=  curr.Value) return currItem;
			if (currItem != SOI) {
				if((EOI == curr.Key) && !in .isValid()) {
					return EOI; } //End of first Stream!
				if (EOI !=(curr.val = mIn2.nextItem()) || mIn2.isValid ()) {
					return curr; } } //Not End of Second Stream ?
			mIn2.reSet(); curr.Key= in .nextItem (); currItem = curr; //End of Second Stream
		} 
	}

	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public long reSet(long Position) { //throws NoSuchMethodException {
		reSet (); return jump(Position); }

	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		super.reSet(); //((StreamIn) Enum).reset(); currItem = SOI;
		mIn2.reSet(); //curr.Key = Enum.nextItem();
		return this; }

}
