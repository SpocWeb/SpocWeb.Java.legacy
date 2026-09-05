package asynch;

import graphs.IValueSetter;

/**
  * Title: CallbackFuture<p>
  * Description:
  * A {@link Future} that, once its value is set, also invokes an {@link IValueSetter} callback
  * with itself as the argument, so the original caller can be notified directly instead of (or
  * in addition to) polling/blocking on getVal().
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-14-2002, 09:44 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:41:28Z
  * digest: d90b65a43dca4e1ff41dc08660d522d5f1bd193dd9a0ef4927541cba33b314de
  * stale: false
  * tags: [code/callback_pattern, code/deferred_execution]
  * concepts: [Future with Callback]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class CallbackFuture
extends Future {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Object to write the Result to	 */
	protected IValueSetter valueSetter;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public CallbackFuture(IValueSetter valueSetter_) {
		this.valueSetter = valueSetter_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent Future: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IValueSetter: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method to execute by the Thread.
	  * Unfortunately only the Result is returned,
	  * but the Target has no Way to identify the Origin of this Call!
	  * @param sets Value of this Object */
	public void setVal(Object val) {
		super.setVal(val); //store the Result
		valueSetter.setVal(this); } //do the Callback

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + CallbackFuture.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

