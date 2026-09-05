package asynch;

/**
  * Title: Semaphore<p>
  * Description:
  * Purpose:
  * Implements a Semaphore,
  * i.e. a Thread Lock to restrict the Number of Threads entering a Code Section.
  * A Mutex is a special kind of Semaphore (most frequently used)
  * allowing only a single Thread within the Code Section.
  *
  * Design Decisions / Implementation Details:
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-15-2002, 10:18 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:43:23Z
  * digest: 576127292b3e4c8f6935bbd7b3ef8c93eedab7330b9d2716f5022d0b2015206b
  * stale: false
  * tags: [code/concurrency_primitive]
  * concepts: [Counting Semaphore]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class Semaphore
implements ISynch {

////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////

	/** @return a Mutex
	  * which is essentially a Semaphore with only a single Thread allowed
	  */
	/** Creates a Mutex, i.e. a Semaphore that allows only a single Thread at a time.
	  * @return a new Mutex, i.e. a Semaphore that allows only a single Thread at a time. */
	final static public Semaphore getMutex() {
		return new Semaphore(1); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Number of Threads (left) allowed in this Semaphore	 */
	protected int numPermits;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Reports whether this Semaphore currently has no permits left.
	  * @return true iff this Semaphore is locked:
	  * This Method doesn't really make Sense,
	  * because independent from this Check,
	  * the Semaphore may be locked already on the next Command!
	  */
	public boolean isLocked() { return numPermits == 0; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public Semaphore(int numThreadsAllowed) { this.numPermits = numThreadsAllowed; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface ISynch: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** acquires the Lock with infinite Timeout	 */
	public void lock() throws InterruptedException {
//		lock(0); } //unfortunately 0 doesn't mean: don't wait, but infinite wait!
		if (Thread.interrupted()) {
			throw new InterruptedException(); }
		synchronized(this) {
			try {
				while(numPermits <= 0) { //isn't an if Statement sufficient?
					wait(); }
				--numPermits;
			} catch (InterruptedException x) {
				notify();
				throw x;
			}
		}
	}

	/** tries to acquire the Lock within the given Timeout
	  * @see Future where a similar Mechanism is employed:
	  * A Join with (or without) Timeout,
	  * but for a single Lock and a single Shot!
	  */
	public boolean lock(long ms) throws InterruptedException {
		if (ms <  0) { //wait infinitely...
			lock();
			return true; }
		if (Thread.interrupted()) {
			throw new InterruptedException(); }
		synchronized(this) {
			if (numPermits > 0) {
				--numPermits;
				return true;
			} else if (ms == 0) { //don't wait
				return false;
			}
			try {
				long startTime = System.currentTimeMillis();
				long  waitTime = ms;
				while(waitTime > 0) { //wait at most this long...
					wait(waitTime); //on Timeout or Notification...
					if (numPermits > 0) { //check the Condition and exit the Loop!
						--numPermits;
						return true; }
					waitTime = ms-(System.currentTimeMillis() - startTime); }
				return false;
			} catch (InterruptedException x) {
				notify();
				throw x;
			}
		}
	}

	/** releases the Lock 	 */
	public void unlock() {
		++numPermits;  //not necessary to use the more expensive notifyAll(),
		notify(); } //because only a single Thread has been freed!

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Semaphore.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

