package streamIO.object;

import java.util.Iterator;

/**
  * Bridges a standard {@link Iterator} into a read-only {@link IStreamIn}.
  * <p>
  * Title: Iterator2StreamIn.java<p>
  * Description:
  * Bridge Class (Filter) from StreamIn to Enumeration
  * The Opposite Direction is implemented in Enumeration2StreamIn
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on 06-03-2001, 12:40 AM<p>
  * @author 	Matthias Heuer
  * @version 1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:42:18Z
  * digest: 808d472163372a04244a8b376b37f36b90fc71c95a8e1162add59f0ea4c39420
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
final public class Iterator2StreamIn
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the Input streamIO */
	protected Iterator iter;

	/** Reference to the current Item */
	protected Object currItem;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a stream that bridges the given iterator.
	 *
	 * @param Source the iterator to wrap
	 */
	public Iterator2StreamIn(Iterator Source) { this.iter = Source; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface Enumeration: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** Advances the wrapped iterator and returns its next element.
	 * @return The next Item from the Input streamIO */
	public Object nextItem() { return currItem = iter.next(); }

	/** Returns the item cached by the last {@link #nextItem()} call.
	 * @return The current Item from the Input streamIO */
	public Object currItem() { return currItem; }

	/** Reports whether the wrapped iterator has more elements.
	 * @return the Number of Items (at least) available */
	public long availAble() { return iter.hasNext() ? 1 : -1; }

	/** Reports that marking is not supported, since a plain {@link Iterator} cannot rewind.
	 * @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }

	/** Always returns 0, since a plain {@link Iterator} has no position to report.
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return 0; } //iter.; }
	
	////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Enumeration2StreamIn.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
