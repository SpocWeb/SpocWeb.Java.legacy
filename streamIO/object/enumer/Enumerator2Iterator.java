package streamIO.object.enumer;

import java.util.Iterator;

import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.object.ModificationException;

/**
  * Title: StreamIn2Enumeration.java<p>
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
  * tags: [code/enumerator, code/iterator_adapter]
  * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
final public class Enumerator2Iterator //Enumerator2Iterator
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

	/** Creates a {@link java.util.Iterator} bridge over the given input streamIO.
	  * @param Source the input streamIO to adapt */
	public Enumerator2Iterator(IIStreamIn Source) { this.Source = Source; }

	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Interface Iterator: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** Removes the Item last returned by {@link #next()} from the Source, which must be
	  * a {@link ReverseEnumerator}.
	  * @throws IllegalStateException when the Source refuses the Modification */
	public void remove() throws IllegalStateException {
//		throw new UnsupportedOperationException(); }
		try { ((ReverseEnumerator) Source).removeCurr();
		} catch (ModificationException x) {
			throw new IllegalStateException(x.toString()); } }

	/** Returns the next Item from the Input streamIO.
	 * @return The next Item from the Input streamIO */
	public Object next() { return Source.nextItem(); }

	/**This is not the exact Opposite of isEmpty(),
	 * because isEmpty()  should be called AFTER  nextItem(),
	 * while hasNext() is typically called BEFORE nextItem().
	 * @return true, when more Items are available */
	public boolean hasNext() { return ((IAvailAble)Source).availAble() > 0; }

	////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Enumerator2Iterator.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
