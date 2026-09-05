package streamIO.object;

import java.util.Enumeration;

import streamIO.exception.OperationNotSupported;

/**
  * Bridges a legacy {@link Enumeration} into a read-only {@link IStreamIn}.
  * <p>
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
  * pass: 2
  * mtime: 2026-09-05T16:39:29Z
  * digest: 2b5b9a0c10006585437b0fef267967ac4883325a861e0355b9c729343e95e11e
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
final public class Enumeration2StreamIn
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Local Reference to the Input streamIO */
	private Enumeration Source;
	
	/** Reference to the current Item */
	protected Object currItem;
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a stream that bridges the given enumeration.
	 *
	 * @param Source the enumeration to wrap
	 */
	public Enumeration2StreamIn(Enumeration Source) { this.Source = Source; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface Enumeration: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** Advances the wrapped enumeration and returns its next element.
	 * @return The next Item from the Input streamIO */
	public Object nextItem() { return currItem = Source.nextElement(); }

	/** Returns the item cached by the last {@link #nextItem()} call.
	 * @return The current Item from the Input streamIO */
	public Object currItem() { return currItem; }

	/** Reports whether the wrapped enumeration has more elements.
	 * @return the Number of Items (at least) available */
	public long availAble() { return Source.hasMoreElements() ? 0 : -1; }

	/** Reports that marking is not supported, since {@link Enumeration} cannot be rewound.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }

	/** Always fails, since a plain {@link Enumeration} has no position to report.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { throw new OperationNotSupported("Enum cannot be reSet()ted"); }

	/** Reports no known ordering, since a plain {@link Enumeration} carries none.
	 * @return The Order of the Item from the Input streamIO */
	public byte getOrder() { return ORDER_NONE; } //Source.getOrder(); }
	
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
