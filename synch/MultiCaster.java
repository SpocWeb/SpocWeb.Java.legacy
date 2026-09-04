package synch;

/**Importing a whole Package does not increase Code Size! */
import java.util.Enumeration;
import java.util.Vector;

import streamIO.IIStreamOut;
import tools.WorkerThread;

/**
 * Uses a Vector to add and remove Observers.
 * Actually this Strategy does nothing more
 * than to move Maintenance Code from the Publisher to the Vector Class.
 *
 * Uses Class Vector for storing the Subscribers type safe.
 * An alternative and equally fast Strategy (except for the Recursion) is
 * to use linked Lists of PublishSubscribers.
 *
 * A full blown MultiCaster should use the Container Interface
 * and choose the Container Implementation based on the Usage.
 *
 * For only adding uniaural Subscribers the LinkedSubscriber Chain is fastest
 * For Subscribers to several Publishers you have to use MultiCaster.
 * For permanent Subscribers a linked List or a Vector is fastest
 * For transient Subscribers a HashSet is needed (depending on the Frequency!).
 *
 * @see MultiValidator that implements the analogous Publisher Interface
 */
public class MultiCaster
	implements ISubscriber, IPublisher {

	/** Class to handle Exception during Notification. */
	public IIStreamOut RuntimeExceptionHandler;// = Stream.Log.L;

	/**Vector to hold all Subscribers. 	 */
	protected Vector Subscribers = new Vector();

	//////////////////////////////
	//	interface Subscriber	//
	//////////////////////////////

	/**Callback used to update all Subscribers
	 * The Return Value is a Boolean to stop Notification, if true	 */
	public void update(Object Source, Object Value, Object oldVal) {
		notifySubscribers(Source, Value, oldVal); }

	/**Callback used to update all Subscribers
	 * The Return Value is a Boolean to stop Notification, if true	 */
//	public boolean update(Object Source, Object Value, Object oldVal) {
//		return notifySubscribers(Source, Value, oldVal); }

	//////////////////////////////
	//	interface Publisher		//
	//////////////////////////////

	/**Adds a Subscriber to this Publisher.
	 * For UniCaster there can be only one, so an Exception is thrown.
	 * Difficult Decision to use an Exception or a boolean Return Value.
	 * For Consistency I use an Exception	 */
	public void addSubscriber(ISubscriber arg) throws TooManySubscribersException {
		Subscribers.addElement(arg);
	}

	/**Removes the Subscriber from this Publisher
	 * Returns false if this Subscriber was not subscribed at all.	 */
	public ISubscriber  removeSubscriber(ISubscriber arg) {
		if(Subscribers.removeElement(arg)) {
			return arg; }
		return null; }

	/**Returns false if this Subscriber was not subscribed at all.	 */
	public boolean isSubscriber(ISubscriber arg) {
		return  Subscribers.contains(arg); }
//		return (Subscribers.indexOf (arg) >= 0); }

	/**Returns the Number of Subscribers of this Publiser	 */
	public int countSubscribers() {
		return Subscribers.size(); }

	/**Notifies the Subscribers of this Value	 */
	protected void notifySubscribers(Object Value) {
		notifySubscribers(this, Value, null); }

	/**Notifies the Subscribers of this Value	 */
	protected void notifySubscribers(Object Value, Object oldVal) {
		notifySubscribers(this, Value, oldVal); }

	/** Timeout of this MultiCaster
	  * Values <= 0 result in a synchronous Call (an infinite Timeout)
	  * positive Values result in an according Timeout in MilliSeconds.
	  */
	public int Timeout = 0;

	/** Notifies the Subscribers of this Value
	  *
	  * This Routine is safe from Exceptions, also RuntimeExceptions,
	  * but not from Errors!
	  * Even a non returning Notification Routine is being watched for Timeouts
	  * and first interrupted and, when it still loops on after another Timeout, stopped!
	  */
	protected void notifySubscribers(Object Source, Object Value, Object oldVal) {
		if (Timeout <= 0) { //wait infinitely / work synchronously!
			notifySubscribersSafely(Source, Value, oldVal);
		} else { //start a Monitoring Thread
			Object[] params = {Source, Value, oldVal};
			WorkerThread t = new WorkerThread (params) {
				public void run() {
					notifySubscribersSafely(Params[0], Params[1], Params[2]); }
			};
			t.start(); //if something goes wrong, the remaining Subscribers are not notified!
			try { //it is allowed to kill a worker Thread!
				t.join(Timeout); //0 is forever >0 is wait //join is just right!
//				Thread.currentThread().sleep(2000); //sleep() waits longer than necessary!
				if (t.isAlive()) {
					t.interrupt(); //try to interrupt it...
					t.join(Timeout); } //sleep another Timeout!!
				if (t.isAlive()) { //stopping is unsafe and deprecated though!!!
					t.stop();  //t.destroy();
					if (RuntimeExceptionHandler != null) {
						RuntimeExceptionHandler.addItem("Had to stop() a Worker Thread due to Congestion!"); }
				}
			} catch (InterruptedException x) { //except if you use notify()
				if (RuntimeExceptionHandler != null) {
					RuntimeExceptionHandler.addItem(x); }
			} //if this Thread is interrupted, also the Worker Thread should stop!
		}
	}

	/** Notifies the Subscribers of this Value
	  * This Routine is safe from Exceptions, also RuntimeExceptions,
	  * but not from Errors!
	  * The only Vulnerability is a non returning Validator Routine!
	  */
	protected void notifySubscribersSafely(Object Source, Object Value, Object oldVal) {
		Enumeration enm = Subscribers.elements();
		//TODO: a separate Thread could be started to notify the Elements
		//This Thread could watch the new Thread and catch Timeouts!
		ISubscriber sub;
		while (enm.hasMoreElements()) { //propagate the Return Code.
			try{ //handling the Exceptions could also be done by a Filter!
				sub = (ISubscriber) enm.nextElement();
				sub.update(Source, Value, oldVal);
			} catch (RuntimeException x) { //only RuntimeExceptions can be thrown through the update Interface!
				if (RuntimeExceptionHandler != null) { //This can also be Log.L (implements StreamOut!)
					RuntimeExceptionHandler.addItem(x); } //handle(x); }
					//possibly log the whole Context: especially sub, but also Source, Value, oldVal
					//The Problem is that even these don't represent the whole Context!
//			} catch (InterruptedException x) {
			}
		}
	}

	/**Notifies the Subscribers of thie Value	 */
/*	protected boolean notifySubscribers(Object Source, Object Value) {
 		Enumeration enm = Subscribers.elements();
		while (enm.hasMoreElements()) { //propagate the Return Code.
			if (((Subscriber) enm.nextElement()).update(Source, Value)) { return true; }
		} return false; }
*/

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	protected static void infinite() throws InterruptedException {
		int i = 0;
		int j = 0;
		//This Test has to be used when no sleep() is wanted in the Loop.
		while (Thread.interrupted()) { //check the Interrupted Flag regularly!
//			System.out.print(++i);
//			Thread.yield(); //yielding is not sufficient!
//			Thread.sleep(1); //busy Threads cannot be interrupted!! But sleeping is very Kernel expensive!
			//The Time has to be >= 1!
			//additionally the sleep has to be built into the Algorithm!
			j += i++;
		}
//		System.out.println(j);
	}

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) { //throws java.io.IOException {
	System.out.println("Testing " + MultiCaster.class.getName());
	long Timeout = 6000;
	Thread t = new Thread(new Runnable(){ //create a new Thread for adding / handling the Item
		public void run() {
			try {
				infinite();
			} catch (InterruptedException x) {
				System.out.println("Timeout: interrupted");
			}
		} } );
	t.start(); //starts the new Thread
	try { //it is allowed to kill a worker Thread!
		t.join(Timeout); //0 is forever >0 is wait for t to die until Timeout.
		if (t.isAlive()) {
			System.out.println("Before interrupting");
			t.interrupt(); //try to interrupt it...
			t.join(Timeout); //sleep another Timeout!!
			System.out.println("After interrupting");
		}
	} catch (InterruptedException x) {
		System.out.println("interrupted on waiting for the Worker Thread");
	}
	if (t.isAlive()) { //stopping is unsafe and deprecated though!!!
		System.out.println("Before stopping");
		t.stop(); //t.destroy();
		System.out.println("After stopping");
	}
	System.out.println("Ending");
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) { //throws java.io.IOException {
	testIt(args); }

}
