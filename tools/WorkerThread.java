package tools;

/**
  * Title: WorkerThread<p>
  * Description:
  * Purpose:
  * Thread to be safely started and also killed (to prevent Processor starving!)
  * Used in MultiValidator and MultiCaster!
  *
  * It takes its non final Parameters as an Object[] Constructor Parameter
  * and uses the same Array to return its Results.
  * The Problem is that for accessing outer Members they must be final.
  * Return Values would have to be publicly accessible Variables
  * of the WorkerThread which may actually not even be accessible anymore!
  *
  * Design Decisions / Implementation Details:
  * The Parameters are handed over as an Object Array in the Constructor.
  * The Return Value(s) are also handed back via this Array!
  *
  * The Run() Method has to be explicitly implemented!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-23-2002, 11:29 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-04T16:35:47Z
  * digest: 53420e758d9bb4e45a3481524aafa0ee2b948dd3cd9cf7b3cc9685d9940a3fbe
  * stale: false
  * -->
  */
public abstract class WorkerThread
extends Thread {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Container for the Parameters handed over to the Worker Thread
	  * The Parameters have to be handed over in the Constructor,
	  * because of possible Asynchronicity and
	  * because local Variables of an outer Class for an inner Class
	  * need to be final, which often defies the Usability of the outer Class
	  *
	  * This Array can also be used to return any Number of Return Values!
	  */
	protected Object[] Params;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor */
	public WorkerThread(Object[] Params_) {
		this.Params = Params_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	public void startWithTimeOut(long Timeout) throws InterruptedException {
		start(); //if something goes wrong, the remaining Subscribers are not notified!
		join(Timeout); //0 is forever >0 is wait //join is just right!
//		Thread.currentThread().sleep(2000); //sleep() waits longer than necessary!
		if (isAlive()) {
			interrupt(); //try to interrupt it...
			join(Timeout); } //sleep another Timeout!!
		if (isAlive()) { //stopping is unsafe and deprecated though!!!
			stop();  //t.destroy();				
			throw new InterruptedException("Thread had to be killed, because it exceeded the Timeout "+Timeout);
		}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent Thread: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** This Method has to be overridden with an inner Implementation! */
	public abstract void run();

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent Thread: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + WorkerThread.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

