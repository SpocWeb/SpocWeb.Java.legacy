package streamIO.object; //

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import streamIO.IMarkAble;

/**
  * Lightweight read-only {@link IStreamIn} over a {@link Collection} or {@link Iterator},
  * recursing into a nested collection or iterator by wrapping it in a fresh instance rather
  * than flattening it.
  * <p>
  * Title: CollectionStreamIn<p>
  * Description:
  * Simple, lightweight read only Iterator for Collections.
  *
  * Implements the advanced recursive streamIO Protocol for Sub-Arrays.
  * @see streamIO.Object.Enumeration.ArrayEnumerator replaces this Class.
  * @see streamIO.Object.Parser.Array2Stream implements this and the Output Interface.
  *
  * Known SubClasses: <none>
  *
  * Design Decisions:
  * Instead of recursively creating new Instances of CollectionStreamIn
  * I could reuse the current Instance by caching both
  * the current Object[] and the current Position on a Stack.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-06, 10;39;48<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:34:07Z
  * digest: 7c3f30f804056e481153fd8706a39703446046e0f36a1fac858cc51393f92ac1
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class CollectionStreamIn
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Current Record	 */
	protected int curr = -1;
	
	/** Local Cache for the Value mark()ed	 */
	protected int markValue = -1;
	
	/** Reference to the Array being iterated.	 */
	protected Iterator arr;
	
	/** Local Cache for the Order of the Array	 */
	protected byte order = ORDER_NONE;
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns the current index into the wrapped iterator.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return curr; }

	/** Reports an unbounded mark size, since the wrapped {@link Iterator} cannot rewind.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; } // arr.; }

	/** Returns the order the collection was constructed with.
	 * @return the Order of the Elements in this streamIO.	  */
	public byte getOrder() { return order; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Constructor	 */
	public CollectionStreamIn(Collection arr_) { this.arr = arr_.iterator(); }
	
	/** Constructor	 */
	public CollectionStreamIn(Iterator arr_) { this.arr = arr_; }
	
	/** Constructor	 */
	public CollectionStreamIn(Iterator arr_, byte Order_) {
		this.order = Order_;
		this.arr = arr_; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the (minimum) Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 *
	 * Nearly equivalent is currItem != null
	 * (when the Container does not contain null Entries, like e.g. HashTables)
	 */
	public long availAble() { return arr.hasNext() ? 1 : -1; }
	
	/** Item returned from the last nextItem() Call
	 * necessary again due to the Creation of a new ArrayStream
	 */
	protected Object currItem;
	
	/**Returns the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	 * This is less explicit, but much faster because Exception Handling can be extremely slow.
	 * Alternatively this Method can block until new Data is available,
	 * but this should always have a TimeOut to avoid DeadLocks.
	 */
	public Object nextItem() {
		if (!arr.hasNext()) {
			return currItem = EOI; }
		currItem = arr.next();
		if (currItem instanceof Iterator) {
			currItem = new CollectionStreamIn((Iterator) currItem); }
		if (currItem instanceof Collection) {
			currItem = new CollectionStreamIn(((Collection) currItem).iterator()); }
		return currItem; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Input streamIO of the Objects in this Container.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	//public IStreamIn Iterator() { return new CollectionStreamIn(arr); }
	
	/** Returns the current Object without moving.
	  * This is just a caching Functionality and should be done
	  * at the Client Process, for faster Access.	 */
	public Object currItem() {
		return currItem; }
	
	//Marking and Resetting a Stream (for re-Processing, if supported)
	
	/**Skips over and discards n Items from this Iterator.
	 * Returns the actual number of bytes skipped.
	 * This dumb Implementation just reads all Elements and discards them.	 */
	public long jump(final long _position) {
		curr += _position; return _position; }
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public long reSet(final long _position) { //throws NoSuchMethodException {
		curr = (int) (markValue + _position); return _position; }
	
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public IMarkAble mark(final long _readLimit) { //throws NoSuchMethodException {
		markValue = curr; return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws IOException {
		System.out.println("Testing " + CollectionStreamIn.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws IOException {
		testIt(); }

}
