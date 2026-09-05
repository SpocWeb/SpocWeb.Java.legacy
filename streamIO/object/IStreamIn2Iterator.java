package streamIO.object;

import java.util.Iterator;

import streamIO.IAvailAble;
import streamIO.IIStreamIn;

/**
  * Bridges a {@link IIStreamIn} into the standard {@link Iterator} contract.
  * <p>
  * Title: IStreamIn2Iterator.java<p>
  * Description:
  * Bridge Class (Filter) from StreamIn to Iterator
  * The Opposite Direction is implemented in Iterator2StreamIn
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
  * mtime: 2026-09-05T16:42:11Z
  * digest: 1639d9ec5ae60d801e511e2093f71ed2666c9672595dd83662dca1a407b5780f
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
final public class IStreamIn2Iterator //Enumerator2Iterator
implements Iterator {

	////////////////////////////////////////////////////////////////////////////
	//  Variables (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Local Reference to the Input streamIO */
	private IIStreamIn Source;

	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an iterator that bridges the given input stream.
	 *
	 * @param Source the stream to wrap
	 */
	public IStreamIn2Iterator(IIStreamIn Source) { this.Source = Source; }

	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Interface Iterator: Implementation
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Always fails, since removal is not supported by the wrapped stream.
	 *
	 * @throws UnsupportedOperationException always
	 */
	public void remove() throws UnsupportedOperationException, IllegalStateException {
		throw new UnsupportedOperationException(); }
//		return Source.removeNext(); }

	/** Advances the wrapped stream and returns its next item.
	 * @return The next Item from the Input streamIO */
	public Object next() { return Source.nextItem(); }

	/** Reports whether the wrapped stream still has items available.
	 * @return true, when more Items are available */
	public boolean hasNext() { return ((IAvailAble)Source).availAble() > 0; }

	////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + StreamIn2Enumeration.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
