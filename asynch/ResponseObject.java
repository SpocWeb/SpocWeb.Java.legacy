package asynch;

import function.IProcessor;
import graphs.IValueSetter;

/**
  * Title: ResponseObject<p>
  * Description:
  * ResponseObject of the generic Active Object Pattern.
  * Purpose:
  * Request Object that, unlike its parent {@link RequestObject}, also carries the actual
  * two-way call (a Responder plus an IValueSetter/Future) so the Scheduler can invoke it and
  * deliver the return value back to the caller.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 08:12 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:41:52Z
  * digest: ac6dd833779a5d829ee21d00f551cd073ede194720995b8735dd6b338f04afa2
  * stale: false
  * tags: [code/deferred_execution]
  * concepts: [Async Response Object]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class ResponseObject
extends RequestObject {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the actual Processor to call	 */
	protected IProcessor Responder;

	/** Reference to the Future for the Return Value of the call	 */
	protected IValueSetter future;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public ResponseObject(IProcessor Responder_, Object Params_, IValueSetter future_) {
		super(null, Params_);
		this.Responder = Responder_;
		this.future    = future_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method called by the Scheduler encapsulating which Method to call */
	public void run() {
		if (Responder == null) {
			return; }
		try {
			Object ret = Responder.MapAt(Params);
			if (future == null) {
				return; }
			future.setVal(ret);
		} catch (RuntimeException x) {
			if (future instanceof IFuture) {
				((IFuture) future).setException(x); }
		}
	} //either only sets the Value or is an actual Callback into the Client!

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ResponseObject.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

