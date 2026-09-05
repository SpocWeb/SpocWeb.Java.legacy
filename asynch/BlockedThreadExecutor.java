package asynch;

//import Stream.IStreamIn;
//import Stream.Object.Pipe;
import streamIO.IIStreamOut;

/**
  * Title: BlockedThreadExecutor<p>
  * Description:
  * Purpose:
  * Asynchronously executes the run() Method of the given Runnable Parameter
  * by waking up an inner Thread.
  * Very Simple Implementation without Queueing.
  * Only handles a single Request, blocks the other Requests.
  *
  * Design Decisions / Implementation Details:
  * This is quite inefficient, especially for short Tasks.
  * The Thread is not being reused or pooled.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-10-2002, 12:14 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:07Z
  * digest: b1578a03aff4e2bcdbe8641ee04048c565425c65f1ac3ccc2a9c698e728b0737
  * stale: false
  * tags: [code/thread_pooling]
  * concepts: [Blocking Task Executor]
  * facets: {layer: infrastructure, status: broken, complexity: medium}
  * -->
  */
public class BlockedThreadExecutor
extends AExecutor
implements Runnable {

	/** Default Capacity of new BlockedThreadExecutors */
	public static int DefaultCapacity = 7;

////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////

	/**
	 * Either use Delegation or Inheritance.
	 * Here Delegation seems to be more appropriate,
	 * on the other Hand the Executor can also behave like a Thread...
	 * Since this is always Thread.currentThread, the Parameter seems unnecessary,
	 * but it is easier to access and control here.
	 */
	protected Thread t = new Thread(this);

	/** Cached Runnable for handing over the Parameter to the new Thread	 */
	protected Runnable r;

	/** Flag for stopping the Thread	 */
	protected boolean stopped;

	/** Sink that InterruptedExceptions encountered by the worker Thread are reported to. */
	public IIStreamOut InterruptionHandler;

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor */
	public BlockedThreadExecutor() {
		t.start(); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** stops the Processing Loop and ends this Thread
	  * after completing the current Request.
	  * Without stopping the Thread stays blocked
	  * and keeps this Executor alive too!
	  */
	public void stop() {
		stopped = true; }

	/* Infinite Loop for the Worker Thread
	 * can be stopped by setting the Parameter to true.
	 * Should not be called publicly!
	 */
	/** Worker loop: runs the currently assigned Runnable, then waits to be handed the next one. */
	public synchronized void run() {
		while (!stopped) {
			// TODO: LOGIC: 'r' is never reset to null after being run, so on every subsequent wakeup
			// (including a spurious one) the same stale Runnable is executed again; combined with
			// stop() never calling notify(), a thread parked in wait() below also has no way to wake
			// up and observe 'stopped', so it can block forever.
			if (r != null) {
				r.run(); }
				t.notify(); //(try to) wake up blocked Dispatcher Thread(s)
			try { wait(); //wait for the Dispatcher to hand back Control
			} catch (InterruptedException x) {
				InterruptionHandler.addItem(x);
			}
		}
		//iterate through the Rest of the Items adding to the ErrorHandler
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IExecutor: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Asynchronously executes the run() Method of the Runnable Parameter
	 * by starting a new Thread.
	 * This is quite inefficient, especially for short Tasks.
	 * The Thread is not being reused or pooled.
	 */
	public synchronized void execute(Runnable r) {
		if (this.r != null) {
			try { wait(); //don't allow parallel Calls, serialize them
			} catch (InterruptedException x) {
				InterruptionHandler.addItem(x);
			}
		} this.r = r; t.notify(); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + BlockedThreadExecutor.class.getName());

	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

