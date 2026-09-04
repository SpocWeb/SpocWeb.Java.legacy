package streamIO.object;

import java.util.Enumeration;

import streamIO.exception.OperationNotSupported;

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
	
	public Enumeration2StreamIn(Enumeration Source) { this.Source = Source; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface Enumeration: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** @return The next Item from the Input streamIO */
	public Object nextItem() { return currItem = Source.nextElement(); }
	
	/** @return The current Item from the Input streamIO */
	public Object currItem() { return currItem; }
	
	/** @return the Number of Items (at least) available */
	public long availAble() { return Source.hasMoreElements() ? 0 : -1; }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { throw new OperationNotSupported("Enum cannot be reSet()ted"); } 
	
	/** @return The Order of the Item from the Input streamIO */
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
