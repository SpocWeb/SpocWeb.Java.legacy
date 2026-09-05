package streamIO.object;

import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IReSetAble;

/** Streams the full cross product of two input streams as a stream of {@code Pair}s, using
  * Cantor's diagonal enumeration so both inputs may be infinite.
  * <p>
  * This Class creates a full Cross Product streamIO of two Input Streams
  * into a streamIO of Pairs. The Pair Class is implemented lightweight.
  * To multiply more Streams, recursively merge the single Streams.
  *
  * Both Streams can be of infinite Size.
  * @see streamIO.Object.Product can also be used, if any of both is a finite streamIO
  * Uses Cantor's Diagonal Algorithm:
  *
  * For Sequences a,b,c,d,e...
  * and 1,2,3,4,5...
  *
  * a1,a2,a3,a4...
  * b1,b2,b3,b4...
  * c1,c2,c3...
  * d1,d2,d3...
  * e1...
  *
  * is counted in the following Order:
  * a1
  * b1,a2
  * c1,b2,a3
  * d1,c2,b3,a4
  * e1...
  *
  * To realize this you need a resettable LIFO Store (Stack), to store a,b,c...
  * and you need to be able to reset streamIO 1,2,3... (or you have to store it too)
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:34:10Z
  * digest: 10a4219c053b7d2f6ceb656275fa04302c7e670660a0cf92184f1c8f6eb44de3
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class Cantor
extends Product {
	
	/** Default Value for the Initial Capacity 	*/
	static int initialCapacityDefault = 10;
	
	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////
	
	/** current Position in the LIFO Buffer (Stack)
      * starts with the full used Length
      * and runs down to 0 */
	protected int pos = -1;
	
	/** Start Position in the LIFO Buffer (Stack)
      * starts with 0 and runs up to Length	 */
	protected int start; // = 0;
	
	/** current Length of the LIFO Buffer (Stack), i.e. last Element added 	 */
	protected int length; // = 0;
	
	/** Reference to the LIFO Buffer (Stack) */
	protected Object[] buffer; // = new Object[initialCapacityDefault];
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////
	
	/** Creates new Union with predefined initial Buffer Size */
	public Cantor (IIStreamIn Stream1, IStreamIn Stream2, int BufferSize) throws NoSuchMethodException {
		super  (Stream1, Stream2); //create a Copy not to disturb other Processes
        buffer = new Object[BufferSize];
//    	if ((EOI != (Buffer[0] = curr.Key)) || Enum.isValid()) {
//            currItem = curr; }
//        Length = 1;
        }
	
	/** Creates new Union with Default initial Buffer Size */
	public Cantor (IIStreamIn Stream1, IStreamIn Stream2) throws NoSuchMethodException {
        this(Stream1, Stream2, initialCapacityDefault); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Delegation
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Number of (currently available) Items,
	  * may return 0 at the End of a constituent streamIO. 	 */
	public long availAble() {
		if (currItem == EOI) {
			return -1; }
//		if ((Pos < 0) || ((Pos < Start) && !Enum2.isValid())) return -1; //End of all Items
		long sum = ((IAvailAble)in).availAble() + mIn2.availAble();
		if (sum >= 0) {
			return sum; }
		return 0; }
	
	/** Returns the next Object (Parent) of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * This is less explicit, but much faster for a regular Operation
	  * because Exception Handling can be extremely slow.
	  *
	  * To realize this you need a resettable LIFO Store (Stack), to store a,b,c
	  * and you need to be able to reset 1,2,3...
	  */
	protected Object nextItemInternal() {
//		if (currItem == EOI) { return currItem; } //correct here, because it always contains a Pair!
		while (true) { //search for a Pair while available() in both Streams.
			if (!mIn2.isValid() || (pos < 0)) { //end of one Diagonal, ...
				pos = length; mIn2.reSet(); //...reset Enum2,
				if ((EOI == (buffer[length] = in .nextItem())) && !in.isValid()) { //...get a new Enum.nextItem()
					++start; --pos; //Start later on the Stack next Time and don't use the last Item!
				} else { //
					enlargeBuffer();
				}
				mIn2.jump(start); //skip the first Items of the second Iterator.
			}
			if ((pos < 0) || ((pos < start) && !mIn2.isValid())) { //End of all Items
				return currItem = EOI; }
//			currItem = (curr = new Pair());
			if((curr.val = mIn2.nextItem()) != EOI || mIn2.isValid()) {
				curr.Key = buffer[pos--]; 
				return currItem = curr; 
			}
		}
	}
	
	/** enlarges the Buffer	 */
	private void enlargeBuffer() {
		if (++length >= buffer.length) { //Resize the Stack
			final Object[] tmp =  new Object[length + length]; //double the Space
			System.arraycopy(buffer, 0, tmp, 0, length);
			buffer = tmp; 
		}
	}
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position. 
	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		super.reSet();
//		if ((EOI != (Buffer[0] = curr.Key)) || Enum.isValid()) {
//			currItem = curr; }
		pos = -1; start = 0;
		length = 0;
		return this; }
	
}
