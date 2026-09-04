package asynch;

//import Knowledge.IReadyFlag;
import streamIO.object.IPipe;

/**
  * Title: QueuedSemaphore<p>
  * Description:
  * Purpose:
  * Semaphore to allow 1 to n Threads to enter a Block.
  * The other Threads are queued and block until a Thread leaves the Block.
  * In its queueing Abilities it is something like a MultiMonitor.
  * Thus this is a mixed queued and blocking Design, related to
  * @see Scheduler for the queueing
  * @see synchronized Blocks, for it is usually used as a Monitor:
  * Mutex   = Semaphore(1)
  * Monitor = QueuedSemaphore(1)
  *
  * Design Decisions / Implementation Details:
  * @see Tools.ThreadLock uses a similar Approach in employing (newly created) Objects
  *      to use their Monitor for blocking the Threads.
  * TODO: check whether you can synchronize a Thread by waiting for itself!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-15-2002, 04:15 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class QueuedSemaphore
extends Semaphore
implements ISynch {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

	/** @return a Monitor Object implemented by the QueuedSemaphore.
	  * This is not the best Choice though,
	  * since Monitors are built into the Java Language
	  * and thus these are easier to use.
	  * They are an Alternative, when queueing must not happen in arbitrary Order!
	  * This Queue can employ any order: LIFO, FIFO or Priority!
	  */
	final static public QueuedSemaphore getMonitor(IPipe pipe) {
		return new QueuedSemaphore(1, pipe); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** The Container storing the Monitors to wait for...	 */
	protected IPipe pipe;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected QueuedSemaphore(int numThreadsAllowed, IPipe pipe) {
		super(numThreadsAllowed);
		this.pipe = pipe; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : ISynch Implementation
////////////////////////////////////////////////////////////////////////////////

	/** acquires the Lock with indefinite Timeout	 */
	public void lock() throws InterruptedException {
		if (Thread.interrupted()) { //busy Method => check the Interrupted Flag
			throw new InterruptedException(); }
		Object monitor = null;
		synchronized(this) {
			if (numPermits > 0) {
				--numPermits;
				return; }
			monitor = Thread.currentThread();
			pipe.addItem(monitor); } //release the Lock on 'this' before locking on node!
		synchronized (monitor) {
//			try {
				monitor.wait(); //node.doWait();
//			} catch (InterruptedException x) {
//				monitor.interrupt(); //set the interrupt Flag!
//			}
		}
	}

	/** tries to acquire the Lock within the given Timeout	 */
	public boolean lock(long ms) throws InterruptedException {
		lock(); return true; }

	/** releases the Lock 	 */
	public void unlock() {
		do { //get the next Monitor with a waiting Task from the Queue...
			Object monitor = pipe.nextItem();
			if (monitor == null) { //Support both blocking ans non blocking Pipes:
				++numPermits; return; } //Queue is empty
			synchronized (monitor) {
//				if (! ((Thread) monitor).isInterrupted()) { //use the interrupted Flag in busy Loops!
					monitor.notify();  } //only notify this single waiting Thread!
//			} //go on looping, because the current node was already released due to Interruption or TimeOut!
		} while (true); //! node.doNotify());
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + QueuedSemaphore.class.getName());
		System.out.println("Testing whether a Thread can wait for its own Monitor:");
		final Thread currThread = Thread.currentThread();
		new Thread(new Runnable() {
			public void run() {
				try { Thread.sleep(2000);
				} catch(InterruptedException x) {
					System.out.println("Interrupted during sleep()");
				}
				synchronized (currThread) {
					currThread.notify(); }
			}
		}).start();
		try {
			synchronized (currThread) {
				currThread.wait(); }
		} catch(InterruptedException x) {
			System.out.println("Interrupted during wait()");
		}
		System.out.println("Success!");
		System.out.println("Finished testing " + QueuedSemaphore.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}


/**
  * Title: WaitNode<p>
  * Description:
  * Purpose:
  * This was used as a Linked List Node in the QueuedSemaphore,
  * but the Queue is realized separately now
  * and synchronization happens on the Thread now...
  * The only remaining Problem is Timeouts or Interruptions during waiting,
  * which are handled using a boolean "released" Flag here!
  *
  * Design Decisions / Implementation Details:
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-15-2002, 05:02 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
class WaitNode {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Flag whether this Node was released due to Timeouts or Interruptions	 */
	protected boolean released = false;

	/** Reference to the next WaitNode for building a singly linked List */
//	protected WaitNode next;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** similar to doWait() */
//	synchronized void doWait(long ms) { }

	/** makes the current Thread wait for this Object */
	synchronized void doWait() throws InterruptedException {
		try {
			while (!released) {
				wait(); }
		} catch (InterruptedException x) {
			if(!released) { //interrupted before notified...
				released = true; throw x; }
			//after notified: ignore Exception, but propagate Status!
			Thread.currentThread().interrupt();
		}
	}

	/** makes the current Thread wake up a single Thread waiting for this Objects Monitor */
	synchronized boolean doNotify() {
		if (released) { //was interrupted or timed out
			return false; }
		released = true;
		notify(); //notify a single Thread because only a single Slot came free!
		return true; }

}

