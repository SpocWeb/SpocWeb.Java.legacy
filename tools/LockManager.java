package tools;

import java.io.IOException;

/**
  * Adds a global Lock over every managed Resource to {@link ThreadLock}'s per-Resource Locks.
  *
  * <p>Extends the ThreadLock Class by Methods to lock this Manager Object itself.
  * This is done in this separate Class for Clarity Purposes. </BR>
  * </BR>
  * Coordinating between a local Lock and a global Lock: </BR>
  * When a local Lock is released either the next Thread on this Lock
  * or a Thread waiting for the global Lock can be notified. </BR>
  * When a global Lock is released either the next Thread on this Lock
  * or all local Threads waiting for their Lock can be notified. </BR>
  * </BR>
  * The necessary Code is remarkably similar to the one in ThreadLock. </BR>
  * </BR>
  *
  * <h2>Invariants</h2>
  *
  * <p>Two Counters carry the whole State, both using -1 for "nobody waiting and nobody
  * holding": {@link #getNumWaiters()} counts Threads queued for the global Lock and
  * {@link #getNumWaitersAll()} counts Threads queued for individual Resources. A global
  * Lock may only be granted while no individual Resource is locked, and no individual Lock
  * may be granted while the global Lock is held. Releasing an individual Lock is what wakes
  * a global Waiter, and {@link #lockThreshold} decides which side gets Priority. Every
  * Method here relies on the Caller holding this Object's Monitor, which the {@code
  * synchronized} Modifiers provide.
  *
  * <h2>Collaborators</h2>
  *
  * <table>
  * <caption>Types this Class works with</caption>
  * <tr><th>Type</th><th>Relationship</th></tr>
  * <tr><td>{@link ThreadLock}</td>
  *     <td>Base Class holding the per-Resource Monitors this Class brackets with a global Lock.</td></tr>
  * <tr><td>{@link LockAble}</td>
  *     <td>The Lock-ID Contract a Client-facing Wrapper such as {@link LockedServer} exposes.</td></tr>
  * </table>
  *
  * Known SubClasses: <none> </BR>
  * </BR>
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-01-27, 05;32;10<p>
  * @author	 Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-04T16:35:47Z
  * digest: b6cf8ae03ead577f575bf7804b19ee2850e947f026103c3907495910bb91190b
  * stale: false
  * tags: [code/locking, code/global_lock]
  * concepts: [Concurrency]
  * facets: {layer: infrastructure, status: broken, complexity: high}
  * -->
  */
public class LockManager
extends ThreadLock {

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Counter for the Number of waiting Threads for this Instance
	 * -1 means no  Threads are waiting and no one is locking it
	 *  0 means one Thread  is  waiting and	one is locking it
	 *  n means n+1 Threads are waiting and	one is locking it
	 */
	private int count = -1;

	/**
	 * Counter for the Number of waiting Threads for all Instances, except this one
	 * -1 means no  Threads are waiting and no one is locking it
	 *  0 means one Thread  is  waiting and	one is locking it
	 *  n means n+1 Threads are waiting and	one is locking it
	 * This is redundant to the total of all Monitor Objects in monitors,
	 * but faster to determine and easier to maintain.
	 * It is necessary to distinguish between count and countAll.
	 */
	private int countAll = -1;

	/**
	 * Threshold for the number of Threads waiting for local Locks
	 * that prevents acquiring the next global Lock on releasing one.
	 * Values <= 0 result in always releasing Threads for local Locks.
	 */
	public int lockThreshold = 0;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns how many Threads are queued for the global Lock right now.
	 *
	 * <p>Returns the Number of Threads waiting on this Manager
	 * -1 means no  Threads are waiting and no one is locking it
	 *  0 means one Thread  is  waiting and	one is locking it
	 *  n means n+1 Threads are waiting and	one is locking it
	 * Access to this Instance should be synchronized to avoid Changes
	 * during or after this Function, otherwise it is only a snapshot.
	 * @return the Number of Threads waiting on any Resource
	 */
	public synchronized int getNumWaiters() {
		return this.count; }

	/**
	 * Returns how many Threads are queued for individual Resources rather than the global Lock.
	 *
	 * <p>Returns the total Number of Threads waiting on any Resource, except for the Manager.
	 * -1 means no  Threads are waiting and no one is locking it
	 *  0 means one Thread  is  waiting and	one is locking it
	 *  n means n+1 Threads are waiting and	one is locking it
	 * Access to this Instance should be synchronized to avoid Changes
	 * during or after this Function, otherwise it is only a snapshot.
	 * @return the Number of Threads waiting on any Resource
	 */
	public synchronized int getNumWaitersAll() {
		return this.countAll; }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Creates a Manager sized by the Base Class's Default Capacity.	 */
	public LockManager() { }

	/** Creates a Manager pre-sized for the given Number of Resources.
	 *
	 * @param numItems the Number of Ressources to Manage.
	 * Doesn't need to be exact,
	 * but should rather be too large than too small.
	 */
	public LockManager(int numItems) {
		super(numItems); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reports whether the global Lock is currently held by some Thread.
	 *
	 * <p>The 'locked' Value of the Parent Class is not reused!
	 * Only a Snapshot unless the Caller holds this Object's Monitor.
	 *
	 * @return true iff the global Lock is held, i.e. the Waiter Count has gone negative
	 */
	public boolean isLocked() {
		return count < 0; }

	/**
	 * Acquires the individual Lock on one Resource, blocking until it is granted.
	 *
	 * <p>Lock the requested Item.
	 * This method blocks until the lock succeeds.
	 * No timeouts are defined for this.
	 *
	 * <p>Must be synchronized to ensure that the current Thread holds the Monitor.
	 *
	 * <p>if lock() is called more often than unlock(),
	 * the DataServer will freeze the last Clients, since no notify() happens.
	 *
	 * @param item The Ressource to lock. If null, this Object is locked
	 */
	// TODO: LOGIC: this Method is synchronized on `this` and then calls super.lock(item),
	// which blocks on the Item's own Monitor. The blocking Thread keeps this Object's
	// Monitor for the whole Wait, so no other Thread can enter lock(), unlock() or the
	// global lock() to release it - a Thread waiting for an Item deadlocks every other
	// Client of this Manager.
	public synchronized void lock(Object item) {
		++this.countAll;
		super.lock(item); //default Behavior for a simple Lock
	}

	/**
	 * Releases one Resource's individual Lock, waking a global Waiter once the last one goes.
	 *
	 * <p>Unlock the requested record.
	 * Ignored if the caller does not have a current lock on the requested record.
	 *
	 * <p>Must be synchronized to ensure that the current Thread holds the Monitor.
	 * An excess Release is reported on {@code System.err} and the Counter clamped, rather
	 * than throwing.
	 *
	 * @param item The Ressource to unlock. If null, this Object is locked
	 */
	public synchronized void unlock(Object item) {
		super.unlock(item); //default Behavior for a simple Lock
		   --this.countAll; //the local Counter is only decremented after a local Thread unlocked it's Ressource!
		if ((this.countAll < 0) &&  //reduce the Number of competing local Threads
			(this.count > 0)) { //reduce the Number of competing global Threads
			 this.count--; //no global lock can exist currently!
			 this.notify(); }	 //notify one of the waiting global Threads, if there is one. (could also call unlock()...)
		if  (this.countAll < -1) { //the opposite Situation can be detected!
			 this.countAll = -1;
			System.err.println("Danger: Calling unlock more often than lock!");
		}
	} //this is very efficient, since only the right Threads wait for this Monitor!

	/**
	 * Acquires the global Lock over every managed Resource, blocking until it is granted.
	 *
	 * <p>Implements a global Lock on all Ressources managed by this Lock.
	 * Can only enter this lock when no Ressource is locked.
	 * Blocks locking any other Ressource.
	 * No timeouts are defined for this.
	 *
	 * <p>Must be synchronized to ensure that the current Thread holds the Monitor.
	 *
	 * <p>if lock() is called more often than unlock(),
	 * the DataServer will freeze the last Clients, since no notify() happens.
	 */
	public synchronized void lock() {
		if ((this.countAll >= 0) || //no single Ressource must be locked!
			(this.count	>= 0)) synchronized (this) { //wait for the Lock on this and ALL managed Items
			// TODO: LOGIC: `if` around wait() instead of `while`. The Comment argues this is
			// safe because no notifyAll() is used, but unlock() does call super.unlock() to
			// "notify ALL of the other waiting Threads", and the JLS permits spurious wakeups
			// regardless - so wait() can return while the Precondition (no Resource locked)
			// is still false, and this Thread then proceeds as if it held the global Lock.
			// The Condition must be re-tested in a while loop.
			try { //since no notifyAll() is used, if() can be used instead of while() Loops
				this.count++;
				this.wait(); //wait (w.o. Timeout) until the lock is freed
			} catch(InterruptedException x) { //should not happen...
				x.printStackTrace(System.err);
				Thread.currentThread().interrupt(); //propagate the Interrupt
			} //
		} //
	}

	/**
	 * Releases the global Lock, handing Priority to Threads queued for individual Resources.
	 *
	 * <p>Implements a global unlock on all Ressources managed by this Lock.
	 * Can only unlock when all Ressources are locked globally.
	 * Notifies any other Ressource.
	 * Ignored if the caller does not have a current lock on the requested record.
	 * This Mechanism interleaves with the individual Locks
	 * by preferring to allow these and being called on releasing the last individual Lock.
	 * {@link #lockThreshold} is the Cut-off: at or above it the individual Waiters are
	 * released, below it a single global Waiter is woken instead.
	 *
	 * <p>Must be synchronized to ensure that the current Thread holds the Monitor.
	 * An excess Release is reported on {@code System.err} and the Counter clamped, rather
	 * than throwing.
	 */
	public synchronized void unlock() {
		// TODO: LOGIC: the Counter is decremented before anything checks that the global Lock
		// is actually held - the Comment on the next line says so and no Check follows. An
		// unmatched unlock() therefore drives count to -2, releases Waiters that hold
		// nothing, and is only noticed by the last branch, which cannot run because the
		// earlier `count >= 0` branch already consumed the common case.
		//must only be unlocked, if it is locked currently!!!
		this.count--;	//always give the single Locks Priority:
		if (this.countAll >= lockThreshold) { //if there are Threads waiting for local Locks...
			super.unlock();	 //notify ALL of the other waiting Threads
		} else if (this.count >= 0) synchronized (this) { //reduce the Number of competing Threads
			 this.notify();	  //notify one of the waiting Threads, if there is one.
		} else if (this.count < -1) { //the opposite Situation can be detected!
			this.count = -1;
			System.err.println("Danger: Calling global unlock more often than global lock!");
		}
	} //this is very efficient, since only the right Threads wait for this Monitor!

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class
	 * @param args Command Line Parameters
	 * @throws IOException on any Error
	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + LockManager.class.getName());
		//TODO write test Routine to check the correct Interaction
		//between several competing global and local Locks.
	}

	/** The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.
	 * @throws IOException on any Error
	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
