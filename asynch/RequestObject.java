package asynch;

import streamIO.IIStreamOut;

/**
  * Title: RequestObject<p>
  * Description:
  * Request Object of the generic Active Object Pattern.
  * Purpose:
  * encapsulates calling a Method with Parameters and Return Value.
  * Should be subclassed for encapsulating different Methods to call
  * with different Sets of Parameters, so Type Safety is still achieved.
  * Also should be subclassed to test for different isDirty() Conditions.
  * Methods without Return Values don't require a Future!
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 06:17 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class RequestObject
implements ReadyToRun {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the actual Processor to call	 */
	protected IIStreamOut Servant;

	/** Reference to the Parameters for the call	 */
	protected Object Params;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** @return true, when this Object has been modified / is not ready, false otherwise */
	public boolean isDirty() {
		//check certain Properties of the Responder (or the Param) to determine the Readiness
		return false; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public RequestObject(IIStreamOut Servant_, Object Params_) {
		this.Servant = Servant_;
		this.Params  = Params_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method called by the Scheduler encapsulating which Method to call */
	public void run() {
		Servant.addItem(Params);
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + RequestObject.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

