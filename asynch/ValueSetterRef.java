package asynch;

import graphs.IValueSetter;
import streamIO.IIStreamIn;

/**
  * Title: ValueSetterRef<p>
  * Description:
  * Purpose:
  * Holds a Reference to an IValueSetter and implements the Runnable Interface
  * Thus it acts as a Bridge between those two Interfaces.
  * This THE single universal Bridge Class
  * between the Runnable and the IStreamIn Interface whose Result is stored in the Future.
  * It acts as the Reference for a Callback that is notified asynchronously.
  *
  * Unfortunately the Callback Function has no direct way to determine
  * what the Origin of this Call is when SEVERAL Threads could call it back!
  * The Solution is to hand over this ValueSetterRef Object as a Cookie during the Call,
  * which is returned on the Callback!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 10:15 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:01Z
  * digest: 890166c399ced0d5779916ece79c7d2a5ebcf11329065c21f0827a5e38388aad
  * stale: false
  * tags: [code/deferred_execution]
  * concepts: [Value Setter Reference]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class ValueSetterRef
extends AReadyToRun {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Object to retrieve the Result from	 */
	protected IIStreamIn streamIn;

	/** Reference to the Object to temporarily store the Result to	 */
	protected IValueSetter valueSetter;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public ValueSetterRef(IIStreamIn streamIn_, IValueSetter valueSetter_) {
		this.streamIn = streamIn_;
		this.valueSetter = valueSetter_;}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method to execute by the Thread.
	  * Unfortunately only the Result is returned,
	  * but the Target has no Way to identify the Origin of this Call!
	  */
	public void run() {
		try {
			valueSetter.setVal(streamIn.nextItem());
		} catch (RuntimeException x) {
			if (valueSetter instanceof IFuture) {
				((IFuture) valueSetter).setException(x);
			}
		}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ValueSetterRef.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

