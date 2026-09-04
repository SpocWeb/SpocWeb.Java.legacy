package streamIO.object;

import java.util.Iterator;

/**
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

	public Iterator2StreamIn(Iterator Source) { this.iter = Source; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface Enumeration: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** @return The next Item from the Input streamIO */
	public Object nextItem() { return currItem = iter.next(); }
	
	/** @return The current Item from the Input streamIO */
	public Object currItem() { return currItem; }
	
	/** @return the Number of Items (at least) available */
	public long availAble() { return iter.hasNext() ? 1 : -1; }
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }
	
	/** @see streamIO.IAvailAble#getPosition()	 */
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
