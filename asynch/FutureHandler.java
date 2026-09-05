package asynch;

import streamIO.IIStreamIn;

/**
  * Title: FutureHandler<p>
  * Description:
  * Purpose:
  * Holds the Reference to the Future and implements the Runnable Interface.
  * This THE single universal Bridge Class
  * between the Runnable and the IStreamIn Interface whose Result is stored in the Future.
  * @deprecated due to the Combination of Future and ValueSetterRef which perform the same
  * 			but separates the IValueSetter Interface from t
  *
  * Design Decisions / Implementation Details:
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-10-2002, 10:30 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: cf02121f17607b0ec43ec76d702165fbb21cf07c27bd83e6333de870efd45ff1
  * stale: false
  * tags: [code/callback_pattern, code/deferred_execution]
  * concepts: [Future Completion Handler]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class FutureHandler
extends Future //not holding a IValueSetter Callback, because it makes it easy to use this Future!
implements Runnable {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Object to retrieve the Result from	 */
	protected IIStreamIn streamIn;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public FutureHandler(IIStreamIn streamIn_) {
		this.streamIn = streamIn_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method to call by the Thread */
	public void run() {
		setVal(streamIn.nextItem()); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + FutureHandler.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

