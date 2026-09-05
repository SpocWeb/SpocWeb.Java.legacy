package streamIO.object.enumer;



/**
  * Title: Iterator2Enumeration.java<p>
  * Description:
  * Bridge Class (Filter) from StreamIn to Enumeration
  * The Opposite Direction is implemented in Enumeration2StreamIn.
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
final public class Enumerator2Enumeration
	implements java.util.Enumeration
{

	////////////////////////////////////////////////////////////////////////////
	//  Variables (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the Enumerator streamIO */
	private Enumerator Source;

	/** Reference to the current Item 	 */
	protected Object currItem;

	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Creates an {@link java.util.Enumeration} bridge over the given {@link Enumerator}.
	  * @param Source the Enumerator to adapt */
	public Enumerator2Enumeration(Enumerator Source) { this.Source = Source; }

	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Interface Enumeration: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** Returns the Item last returned by {@link #nextElement()}.
	  * @return The current Item from the Input streamIO */
	public Object currItem() { return currItem; }

	/** Advances to and returns the next Item from the underlying {@link Enumerator}.
	  * @return The next Item from the Input streamIO */
	public Object nextElement() { return currItem = Source.nextItem(); }

	/** Reports whether the underlying {@link Enumerator} still has Items available.
	  * @return the Number of Items (at least) available */
	public boolean hasMoreElements() { return Source.availAble() > 0; }

	////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Enumerator2Enumeration.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
