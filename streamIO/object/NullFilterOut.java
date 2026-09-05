package streamIO.object;

import streamIO.FilterOut;
import streamIO.IIStreamOut;

/**
  * Filter that silently discards {@code null} items, forwarding every other item unchanged to
  * the wrapped output.
  * <p>
  * Title: NullFilterOut.java<p>
  * Description:
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-03, 07;16;01<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:43:22Z
  * digest: 08bb1925920bbe1de957e7a8668d54e9575cc023dd73740447238a030878f4bf
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
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
