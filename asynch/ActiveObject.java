package asynch;

import streamIO.IIStreamOut;
import function.IProcessor;
import graphs.IValueSetter;

/**
  * Title: ActiveObject<p>
  * Description:
  * Active Object of the generic Active Object Pattern.
  * Purpose:
  * implements the Proxy of the Active Object Design Pattern
  * once for a one way Method using a RequestObject
  * once for a two way Method using a ResponseObject with asynch. Callback into the Client and
  * once for a two way Method using a ResponseObject returning a Future.
  *
  * A concrete Implementation should focus on the actual Interface Methods
  * and create strongly typed Versions of these two Call Types.
  * Also the Scheduler is not strongly typed!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 05:29 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:23Z
  * digest: 4bf3d922a526e0c9c8a4e8f816d2fe9d675a818d132c33fd5c1d4ac1ff9b973a
  * stale: false
  * tags: [code/deferred_execution]
  * concepts: [Active Object Pattern]
  * facets: {layer: infrastructure, status: legacy, complexity: medium}
  * -->
  */
public class ActiveObject
implements IProcessor, IValueSetter {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Actually performs the Request.	 */
	protected IProcessor Servant;

	/** Reference to the Scheduler synchronizing the Requests.	 */
	IIStreamOut scheduler;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public ActiveObject(IProcessor Servant_, IIStreamOut scheduler_) {
		this.Servant = Servant_;
		this.scheduler = scheduler_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** Example for an asynchronous Function
	  * that returns the Result via a Callback directly into the Client
	  * @param  arg is being changed and returned in the Course of the Operation.
	  * @param  Callback the setVal() Method of this Object is called
	  * when Calculation is finished.
	  */
	public void MapAt(Object arg, IValueSetter Callback) {
		scheduler.addItem(new ResponseObject(Servant, arg, Callback)); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Processor: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Example for an asynchronous Function that returns a Future
	  * @return arg, mapped in Place by this Object: this.MapAt(arg) this=�arg
	  * @param  arg is being changed and returned in the Course of the Operation.
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.
	  */
	public Object MapAt(Object arg) {
		Future future = new Future();
		scheduler.addItem(new ResponseObject(Servant, arg, future));
		return future; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IStreamOut: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Example for an asynchronous Operation that does not return anything
	  * @param  arg is being changed and returned in the Course of the Operation.
	  * @return this Object Instance
	  */
	public void setVal(Object arg) {
		scheduler.addItem(new RequestObject((IIStreamOut) Servant, arg)); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ActiveObject.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

