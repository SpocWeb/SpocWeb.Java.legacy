package tools;

/**
  * Thread Base Class that exchanges its Parameters and Results through one shared Object Array.
  *
  * <p>Purpose:
  * Thread to be safely started and also killed (to prevent Processor starving!)
  * Used in MultiValidator and MultiCaster!
  *
  * <p>It takes its non final Parameters as an Object[] Constructor Parameter
  * and uses the same Array to return its Results.
  * The Problem is that for accessing outer Members they must be final.
  * Return Values would have to be publicly accessible Variables
  * of the WorkerThread which may actually not even be accessible anymore!
  *
  * <p>Design Decisions / Implementation Details:
  * The Parameters are handed over as an Object Array in the Constructor.
  * The Return Value(s) are also handed back via this Array!
  *
  * <p>The {@link #run()} Method has to be explicitly implemented!
  *
  * <h2>Invariants</h2>
  *
  * <p>The Parameter Array is shared, unsynchronized State: the Worker writes its Results
  * into the same Array the Caller still holds. Reading a Result is only safe after
  * {@link #startWithTimeOut(long)} or a plain {@code join()} has returned, which is what
  * establishes the necessary Happens-Before Relationship. Each Instance is single-use, as
  * every {@link Thread} is.
  *
  * <h2>Collaborators</h2>
  *
  * <table>
  * <caption>Types this Class works with</caption>
  * <tr><th>Type</th><th>Relationship</th></tr>
  * <tr><td>{@link Thread}</td>
  *     <td>Base Class supplying start, join, interrupt and the Liveness Check used here.</td></tr>
  * </table>
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
  * digest: 13fcbe3eaa38951d9c734a4e9b57b7575185443d87fecc5e36f3857127c4f484
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

	/** Binds the Worker to the Array carrying both its Parameters and its Results.
	  *
	  * @param Params_ the shared Array; the Caller keeps the Reference and reads the
	  *        Results back out of it once the Worker has finished
	  */
	public WorkerThread(Object[] Params_) {
		this.Params = Params_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** Starts the Worker and waits for it, escalating from Interrupt to Kill on Timeout.
	  *
	  * <p>Waits up to the Timeout for the Worker to finish on its own, then interrupts it
	  * and waits another Timeout, and only then forces it down. Returning normally means
	  * the Worker has finished and its Results in {@link #Params} are safe to read.
	  *
	  * @param Timeout Milliseconds to wait per Stage; 0 waits forever
	  * @throws InterruptedException when the Worker had to be killed because it outlasted
	  *         both Stages, or when the calling Thread is itself interrupted while waiting
	  */
	public void startWithTimeOut(long Timeout) throws InterruptedException {
		start(); //if something goes wrong, the remaining Subscribers are not notified!
		join(Timeout); //0 is forever >0 is wait //join is just right!
//		Thread.currentThread().sleep(2000); //sleep() waits longer than necessary!
		if (isAlive()) {
			interrupt(); //try to interrupt it...
			join(Timeout); } //sleep another Timeout!!
		if (isAlive()) { //stopping is unsafe and deprecated though!!!
			// TODO: LOGIC: Thread.stop() throws ThreadDeath at an arbitrary Point in the
			// Worker, so it can leave a held Monitor released mid-update and the shared
			// Params Array half written - the Caller then reads a torn Result rather than
			// seeing a Failure. It is also removed from the JDK: on 20 and later this line
			// throws UnsupportedOperationException, making the Timeout Path fail outright.
			stop();  //t.destroy();
			throw new InterruptedException("Thread had to be killed, because it exceeded the Timeout "+Timeout);
		}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent Thread: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** Carries out the Worker's actual Task, reading from and writing back to {@link #Params}.
	  *
	  * <p>This Method has to be overridden with an inner Implementation!
	  */
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

