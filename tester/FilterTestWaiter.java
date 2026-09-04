package tester;

/**
 * FilterTestWaiter
 * 
 * Wraps an ITester Object in Filter Manner.
 * Performs a Test on the given Object arg until it returns true 
 * or it times out, returning false.
 * Works synchronous and puts the current Thread to sleep in between.
 * 
 * Created on 29. Januar 2001, 23:35
 * 
 * @author  Matthias Heuer
 * @version
 */
public class FilterTestWaiter
implements ITester {

	/** Delegate ITester which is tested in a while loop with timed waits and timeout */
	protected ITester Delegate;

	/** Timeout during which Delegate is tested in a while loop with timed waits  */
	protected long TimeOut;

	/** Creates new Waiter */
	public FilterTestWaiter(final ITester Delegate, final long TimeOut) {
		this.Delegate = Delegate;
		this.TimeOut  = TimeOut ;
	}

	/**
	 * This is the Test working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * The Wait Time may be interrupted or stopped by notify() thus it is recalculated
	 * and another Thread is waked up.
	 * When the Object arg is changed, it should notify this ITester
	 * within a synchronized Method to prevent a Thread starting to wait
	 * before the actual Changes.
	 * The Test() Method should also be the first Call in a synchronized Method,
	 * to maintain State Integrity (before and after Method) and
	 * to prevent Changes between the Test and the Rest of the Method.
	 */
	public boolean test (final Object arg) {
	    final long start = System.currentTimeMillis();
		long waitTime = TimeOut;
		while(true) {
			if (Delegate.test (arg)) return true; //break; //when the Condition succeeds
			if (waitTime <= 0) return false; //throw new TimeOutException(); //when timed out
			try {
				wait(waitTime); //notify the next Thread on Interruption, because the Condition also holds for that one
			} catch (InterruptedException e) {
				notify(); return false; } //throw e; }
			waitTime = TimeOut - (System.currentTimeMillis()-start); //reduce the wait Time
		} //return true;
//		notifyAll(); //notify waiting Objects that this Task has been performed!
	}

}

/**Helper Exception Class distinguishable from InterruptedException */
//class TimeOutException extends InterruptedException { }
