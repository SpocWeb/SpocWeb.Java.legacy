package streamIO.object;

import java.util.Enumeration;

import streamIO.IAvailAble;
import streamIO.IIStreamIn;

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
  */
final public class StreamIn2Enumeration
implements Enumeration {

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

	public StreamIn2Enumeration(IIStreamIn Source) { this.Source = Source; }

	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Interface Enumeration: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** @return The next Item from the Input streamIO */
	public Object nextElement() { return Source.nextItem(); }

	/** @return true, when more Items are available */
	public boolean hasMoreElements() { return ((IAvailAble)Source).availAble() > 0; }

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
