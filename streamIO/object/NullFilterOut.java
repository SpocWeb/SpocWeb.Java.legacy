package streamIO.object;

import streamIO.FilterOut;
import streamIO.IIStreamOut;

/**
  * Title: NullFilterOut.java<p>
  * Description:
  * TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-03, 07;16;01<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class NullFilterOut
extends FilterOut {

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

/** Empty Constructor	 */
protected NullFilterOut(FilterOut Store) { super(Store); }

////////////////////////////////////////////////////////////////////////////////
//  Interface FilterOut: Implementation
////////////////////////////////////////////////////////////////////////////////

/** adds this Item to the Store in Place: +=
  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
public IIStreamOut addItem(Object arg) {
	if (null != arg) out.addItem(arg);
	return this; }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) throws java.io.IOException {
	System.out.println("Testing " + NullFilterOut.class.getName());
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	testIt(args); }

}
