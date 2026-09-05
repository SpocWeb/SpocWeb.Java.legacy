package asynch;

import streamIO.IIStreamOut;
import streamIO.object.IPipe;
import streamIO.object.enumer.container.DeQueueArr;

/**
  * Title: ThreadExecutor<p>
  * Description:
  * Purpose:
  * Worker Thread Object, asynchronously executes the run() Method
  * of the given Runnable Parameter by waking up the inner Thread.
  *
  * Design Decisions / Implementation Details:
  * This is more efficient than SimpleThreadExecutor,
  * because the Thread is being reused.
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
  * mtime: 2026-09-05T10:43:31Z
  * digest: 64a6bd8e0e79efb3fb945d7ba89d4342e34b761ae6461e33a554090484a286d5
  * stale: false
  * tags: [code/thread_pooling]
  * concepts: [Thread Executor]
  * facets: {layer: infrastructure, status: broken, complexity: medium}
  * -->
  */
public class ThreadExecutor
extends AExecutor
implements Runnable {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/** Default Capacity of new ThreadExecutors */
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
//	protected Thread t;

	/** Cached Runnable for handing over the Parameter to the new Thread	 */
//	protected Runnable r;

	/** Cached Runnables for handing over the Parameter to the new Thread
	 *  Using a Queue to serialize Access.
	 *  Generally a Container should be used to be able to use
	 *  FIFOs, LIFOs, Priority or other Containers.
	 */
	protected IPipe pipe;

	/** Flag for stopping the Thread	 */
	protected boolean stopped;

	/** Counter for the Number of Tasks */
	protected int numTasks = 0;

	/** Number of Milliseconds to wait before trying to interrupt or stop the current Thread. */
	protected long Timeout = -1;

	/** Handler for InterrutionExceptions */
	public IIStreamOut InterruptionHandler;

////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////

	/** Returns the current task-count bookkeeping value used for load balancing.
	  * @return the Number of Tasks currently queued or running in this Executor
	  * (see the TODO on run() below: this counter can drift due to a bookkeeping bug). */
	public int getNumTasks() {
		return numTasks; }

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor for Subclass ThreadPoolExecutor not to start this Thread! */
	protected ThreadExecutor(boolean arg) { }

	/** Empty Constructor defaulting both the Container Type and the Capacity */
	public ThreadExecutor() {
		this(DefaultCapacity); }

	/** Constructor taking the Capacity defaulting the Container Type */
	public ThreadExecutor(int Capacity) {
		this (new DeQueueArr(Capacity)); }

	/** Constructor taking the Container */
	public ThreadExecutor(IPipe p) {
		pipe = p;
		Thread t = new Thread(this); t.start();
	}

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
	 * Must not be called publicly!
	 */
	/** Worker loop: repeatedly takes the next Runnable from the pipe and runs it, waiting when empty. */
	public synchronized void run() {
		while (!stopped) {
			// TODO: LOGIC: numTasks is decremented unconditionally even when pipe.nextItem() below
			// returns null (nothing to do), so numTasks drifts negative whenever this worker goes idle.
			// SimpleThreadPoolExecutor.execute() relies on getNumTasks() for load balancing, so a
			// corrupted (negative) count defeats that balancing.
			numTasks--;
			Runnable item = (Runnable) pipe.nextItem();
			if (item != null) {
				item.run();
				//TODO: implement a Timeout Mechanism by setting up a Watcher Thread
				//using an Approach similar to MultiCaster and MultiValidator!
				// only here you cannot join() or check for isAlive()!
				// instead you must check and thus reset the Thread.interrupted() Status
				// before calling run() and ignore InterruptedException!
//				this.notify(); //(try to) wake up blocked Dispatcher Thread(s)
			} else {
				try { this.wait(); //wait for the Dispatcher to hand back Control using 'this' or the Pipe
				} catch (InterruptedException x) {
					InterruptionHandler.addItem(x);
				}
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
		pipe.addItem(r); ++numTasks;
		this.notify(); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ThreadExecutor.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

