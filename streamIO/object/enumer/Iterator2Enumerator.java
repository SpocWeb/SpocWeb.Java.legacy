package streamIO.object.enumer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import streamIO.IReSetAble;

/**
  * Title: Enumeration2StreamIn.java<p>
  * Description:
  * Bridge Class (Filter) from Enumeration to StreamIn
  * The Opposite Direction is implemented in Iterator2Enumeration.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on 06-03-2001, 12:40 AM<p>
  * @author 	Matthias Heuer
  * @version 1.0
  * <!-- docstate
  * tags: [code/enumerator, code/iterator_adapter]
  * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class Iterator2Enumerator
extends AEnumerator
{
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Local Reference to the Input streamIO */
	private Iterator source;
	
	/** Reference to the current Item */
	protected Object currItem;
	
	/** The current Position in the streamIO.
	  * Reset to 0 on Construction and on reset() Commands
	  * Introduced to support reset() Methods also for Iterators that are not resettable
	  * and as a ShortCut for Iterators that have started,
	  * but not reached the given Position!
	  * Makes only sense when concurrently implementing the nextItem() Method
	  * to increase this Position!
	  * This can be enforced by introducing a new protected Delegation Method
	  * and making the nextItem() Method final. */
	protected long position = 0;
	
	/** Returns the current Position, incremented on each {@link #nextItem()} call.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return position; }

	/** Returns -1, since a plain {@link Iterator} does not expose a mark limit.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Creates an Enumerator wrapping the given {@link Iterator}.
	 * @param _source the Iterator to bridge to the Enumerator interface */
	public Iterator2Enumerator(final Iterator _source) {
		super(null);
		this.source = _source; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface Enumerator: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** Advances to and returns the next Item from the wrapped Iterator.
	 * @return The next Item from the Input streamIO */
	public Object nextItem() {
		try {
			return currItem = source.next();
		} catch (NoSuchElementException x) {
			return null;
		}
	}

	/** Returns the current Item without advancing.
	 * @return The current Item from the Input streamIO */
	public Object currItem() { return currItem; }

	/** Tests whether the wrapped Iterator has a next Item.
	 * @return the Number of Items (at least) available */
	public long availAble() { return source.hasNext() ? 1 : -1; }

	/** This Enumerator returns Items unordered, mirroring the wrapped Iterator.
	 * @return the Number of Items (at least) available */
	public byte getOrder() { return ORDER_NONE; }

	/** Removes the current Item via the wrapped Iterator's {@code remove()}.
	 * @return The current Item from the Input streamIO */
	public Object removeCurr() { source.remove(); return currItem; }

	/** Not supported: a plain {@link Iterator} offers no way to replace an Item.
	 * @return The current Item from the Input streamIO */
	public Object replaceCurr(Object Item) {
		throw new UnsupportedOperationException(); }
//		return currItem; }

	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() { //throws NoSuchMethodException{
		reSet (0); return this; }

	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(long _position) { //throws    NoSuchMethodException {
		if (this.position > _position) //a plain Iterator cannot be rewound, see getMaxMarkSize()
		    throw new UnsupportedOperationException(
		    		"cannot reSet backwards from " + position + " to " + _position);
		return position + jump(_position - position);  }
	
	////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Iterator2Enumerator.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
