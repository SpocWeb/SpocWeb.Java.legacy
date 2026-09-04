package streamIO.object.enumer.container.util;

import java.util.Date;

import streamIO.Log;
import streamIO.object.enumer.container.Heap;
import function.IIOrderAble;

/** Value Object for an Event to be executed at the given TimeStamp */
class TimedEvent implements IIOrderAble {

	/** The Event to be executed at the given TimeStamp */
	public Runnable EventObj;

	/** The TimeStamp at which the given Event is to be executed   */
	public long TimeStamp;

	/** @return true, when the TimeStamp
	  * @param arg is less / earlier than this TimeStamp
	  */
	public boolean isLessThan(Object arg) {
		return TimeStamp < ((TimedEvent) arg).TimeStamp; }

	/** Initializing Constructor for the TimedEvent */
	public TimedEvent(Runnable EventObj_, Date TimeStamp_) {
		this.EventObj  = EventObj_;
		this.TimeStamp = TimeStamp_.getTime();
	}

	/** Initializing Constructor for the TimedEvent */
	public TimedEvent(Runnable EventObj_, long TimeStamp_) {
		this.EventObj  = EventObj_;
		this.TimeStamp = TimeStamp_;
	}

	/** Initializing Constructor for the TimedEvent */
	public TimedEvent(long relTime, Runnable EventObj_) {
		this.EventObj  = EventObj_;
		this.TimeStamp = System.currentTimeMillis() + relTime;
	}

	/** @return it's Class and Name */
	public String toString() {
		return EventObj + " at " + TimeStamp; }

}

/**
  * Implements a timed Queue, in which runnable Objects can be inserted
  * and are executed at the given point of Time.
  * This is a CallBack Mechanism relying on a certain Interface.
  * Optionally an extra Thread is created for each Operation to achieve this
  * and run Threads concurrently.
  *
  * Alternatively any other CallBack Function can be added to the List
  *
  * As an Optimization wait() and notify() could be used (requires changed Logic!)
  * instead of sleep() and interrupt(),
  * because the latter is a pretty expensive Operation
  */
public class TimedQueue
//implements Runnable //a private Runnable doesn't expose run()
{

////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////

	/** Switches on creating Threads for the run() Method */
	public boolean createThreads;

	/** The TimeOut after which an Event is not to be executed if something went wrong   */
//	public long TimeOut;

	/** Reference to the Thread running the Timer	*/
	protected Thread mThread;

	/** Reference to the next Event */
	protected Runnable NextEvent;

	/** Reference to the next Event Time */
	protected long NextEventTime;

	/** Reference to the Heap organizing the Sequence of Events	  */
	protected Heap Queue = new Heap();

	/** A private Member does not expose the run() Method like TimedQueue would. */
	protected Runnable mRun = new Runnable() {

		/** Starts this Queue
		  * A Problem happens if there is no Element in the Queue,
		  * then the Thread must be put to sleep for a very long time! */
		public void run() {
//		Log.L.L("Entering run");
			Runnable tmp; //use local Variables to prevent concurrent access.
			long waitTime; //without completely synchronizing the Method.
			do {
				try {
					synchronized(this) { //prevent concurrent Access!
						waitTime = NextEventTime - System.currentTimeMillis();
						tmp = NextEvent; }
					if (waitTime > 0) { //wait for the next Event
						Thread.sleep(waitTime); }
//				Log.L.L("tmp = " + tmp);
					if (tmp != null) { //use a local Variable to run the potentially long Operation
						if (createThreads) {
							new Thread(tmp).start(); //create extra Threads that run al long as necessary.
						} else { tmp.run(); }
					}
					//if it does not sleep, it cannot be interrupted
					synchronized(this) { //get the next Event from the Queue
						TimedEvent nextEvent = (TimedEvent) Queue.nextItem();
//					Log.L.L("Dequeueing " + nextEvent);
						if (nextEvent == null) break; //{ dead = true; return; }
						NextEvent = tmp = nextEvent.EventObj;
						NextEventTime = nextEvent.TimeStamp;
					}
				} catch (InterruptedException x) {
					System.out.println("interrupted!");
				}
			} while (NextEvent != null);
//		Log.L.L("Exiting run");
		}

	};

////////////////////////////////////////////////////////////////////////////
//  Methods, public ones, then private ones (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Adds another timed Event to the Queue.
	  * This Method is usually called by a different concurrent Thread.
	  *
	  * If the Thread has ended, a new one is started.
	  */
	public void addEvent(Runnable EventObj, long TimeStamp) {
		if (TimeStamp < NextEventTime) {	//Check if the Time is already up, if yes, interrupt the Thread
			synchronized(this) { //exchange the Events and TimeStamps
				Runnable tmpObj = NextEvent; NextEvent = EventObj; EventObj = tmpObj;
				long tmpTime = NextEventTime; NextEventTime = TimeStamp; TimeStamp = tmpTime;
				mThread.interrupt();
			}
		}
//		Log.L.L("Enqueueing " + EventObj + " for " + TimeStamp);
		Queue.addItem(new TimedEvent(EventObj, TimeStamp)); //enqueue it
		if ((mThread == null) || !mThread.isAlive()) start(); //possibly start a Thread
	}

	/** Adds another timed Event to the Queue.
	  * This Method is usually called by a different concurrent Thread.
	  */
	public void addEvent(Runnable EventObj, Date TimeStamp) {
		addEvent(EventObj, TimeStamp.getTime()); }

	/** Adds another timed Event to the Queue.
	  * This Method is usually called by a different concurrent Thread.
	  */
	public void addEvent(long relTime, Runnable EventObj) {
		addEvent(EventObj, System.currentTimeMillis() + relTime); }

	/** Restarts this Queue. */
	public void start() {
		if ((mThread != null) && mThread.isAlive()) return;
		mThread = new Thread(mRun); //this);
		mThread.start(); //a thread calls run() only the first Time it is started!
	}

	/** Suspends this Queue immediately. */
	public synchronized void stop() {
		if (mThread == null) return;
		Runnable tmp = NextEvent; NextEvent = null;
//		mThread.stop(); //this is dangerous
		mThread.interrupt(); //stop the Loop, don't stop the Thread by Force.
		NextEvent = tmp; //restore the old Value for restarting the Queue!
	}

////////////////////////////////////////////////////////////////////////////
//	static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + TimedQueue.class.getName());
		TimedQueue timer = new TimedQueue();
//		new Thread(new testRunAble("test")).start(); //
		try{
//			Log.L.L("wait a while for nothing to happen...");
//			Thread.currentThread().sleep(10000); //
			timer.createThreads = true;
			timer.addEvent(30000, new TestRunAble(Log.L, "Second")); //add some Events
			Log.L.n("wait for them to happen...");
			Thread.sleep(10000); //wait a while for nothing to happen...
			Log.L.n("in between add some new Events...");
			Log.L.n("some earlier");
			timer.addEvent(10000, new TestRunAble(Log.L, "First")); //add some Events
			Log.L.n("some later");
			timer.addEvent(30000, new TestRunAble(Log.L, "Third")); //add some Events
			Log.L.n("wait for all of them to happen");
			timer.stop(); //stop the Timer
			Thread.sleep(30000); //not necessary to wait, the Thread lives on!
			timer.start(); //resume the Timer
		} catch (InterruptedException x) {
			System.out.println("Unexpected Interruption!");
		} //IllegalThreadStateException
		System.out.println("Ending Test...");
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
