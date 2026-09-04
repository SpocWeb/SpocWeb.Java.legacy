package tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Serializes write Access to arbitrary Objects across several Calls, by their own Monitors.
 *
 * <p>Manages the exclusive, blocking (Write-) Locking
 * on an arbitrary (not predefined) Set of Objects or int Values.
 *
 * <p>Created to synchronize Read/Write Access on Server-side Record Lists.
 *
 * This relieves the individual Object of managing Locks across several Client Calls
 * and in the Case of an int it saves creating a Class and Instances for that Purpose.
 * Call ThreadLock.  lock(obj) at the Start of the Transaction
 * and  ThreadLock.unlock(obj) at the End   of the Transaction instead
 * and all Access will be serialized across several Calls by blocking Threads!
 *
 * To create a Read Lock too, embed the lock() and unlock() Methods into the Server Object,
 * coordinate and delegate them to ThreadLock like in {@link LockImproved}.
 *
 * The Clients are identified by their Thread, because they are blocked anyway,
 * so no single Thread would go back and aquire or release the same Lock again.
 *
 * Manages the threaded Access to an (unknown) Number of (unknown) Items
 * by helping to enforce a single write Access e.g. on the Server Side.
 * This has to be complemented by the Client Code calling the Sequence:
 * lock(obj); modify1(obj); ... modifyN(obj); unlock(obj); </BR>
 * </BR>
 * In Contrast to this, a single Resource can be managed easily </BR>
 * by synchronizing it (for a single Call) or </BR>
 * by assigning a Semaphore to it (for consecutive Calls). </BR>
 * </BR>
 * This Class has been singled out of the DataLocker Class,
 * because it is highly reusable in similar Contexts. </BR>
 * </BR>
 * Design Decisions:  </BR>
 * Using wait() on the Data Server if a Record is locked
 * and notifyAll() when it is unlocked, which is quite inefficient!
 * Especially with a large Number of Users competing on a few Records
 * this leads to a lot of Overhead when each Thread checks it's Condition and fails. </BR>
 * </BR>
 * Using wait() on the Record or a Substitute Object, if it is locked
 * and notify() when it is unlocked, notifies exactly one of the queued Threads,
 * which is very efficient. </BR>
 * </BR>
 * Design Decisions: </BR>
 * The Server Objects don't need to memorize the Threads waiting for it.
 * The Objects' Monitor ist used for that (built in Java Functionality).
 * Only the Number of waiting Threads is stored in a Bag (one Counter for each Server Object)
 * to allow to decide whether to wait() or to acquire the Lock on this Object.
 * </BR>
 * Known SubClasses: </BR>
 * LockManager which extends the Functionality
 * by allowing global Locks on all managed Items and the Manager itself. </BR>
 *
 * <h2>Invariants</h2>
 *
 * <p>The Monitor of a Substitute Object per locked Item - not of the Item itself - is what
 * Threads block on, and a Counter per Item records how many Threads are queued for it. An
 * Item with no Entry is unlocked. A Lock belongs to the Thread that took it, since a
 * blocked Thread cannot re-enter to take or release the same Lock again; the Class
 * therefore stores no Owner and cannot detect a Release by the wrong Thread.
 *
 * <h2>Collaborators</h2>
 *
 * <table>
 * <caption>Types this Class works with</caption>
 * <tr><th>Type</th><th>Relationship</th></tr>
 * <tr><td>{@link LockManager}</td>
 *     <td>Subclass adding a global Lock over every managed Item.</td></tr>
 * <tr><td>{@link LockImproved}</td>
 *     <td>The non-blocking, token-based Alternative referenced above for Read Locks.</td></tr>
 * <tr><td>{@link java.util.HashMap}</td>
 *     <td>Holds the per-Item Substitute Monitors and their Waiter Counts.</td></tr>
 * </table>
 *
 * </BR>
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	2002-01-27, 03;19;22<p>
 * @see LockManager
 * @author	 Matthias Heuer
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:47Z
 * digest: 315276aff7ef4bdf961874d499b22e694647fbd899358180a05ffefc40dc726c
 * stale: false
 * -->
 */
public class ThreadLock {

////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////

	/**
	 * Static Constant Locking Instance,
	 * very useful, since it can manage Locks on anything!
	 */
	final static public ThreadLock ThreadLock = new LockManager();


	/**
	 * Flag that switches on tolerating multiple Unlocks on the same Record.
	 * This should be set to false during Development
	 * to detect any improper lock(); modify(); unlock(); Sequence.
	 */
	public static boolean tolerateUnlocks = false;

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Counter for the Number of waiting Threads for this Instance
	 * -1 means no  Threads are waiting and no one is locking it
	 *  0 means one Thread  is  waiting and	one is locking it
	 *  n means n+1 Threads are waiting and	one is locking it
	 */
	protected boolean locked = false;

	/**
	* The Store for locked Records.
	* Using a dynamic Container (no Array) for simplicity.
	* Since the Integer Record Numbers represent an evenly distributed Hash Function,
	* they are very good for being hashed.
	* Since it is not necessary to have a sorted Iterator TreeSet is not used.
	* It is initialized in the Constructor to allow for presetting the Capacity.
	* The Container doesn't need to be synchronized,
	* because it is only accessed in this Class
	* and only within synchronized Methods (lock() and unlock())
	*/
	private HashMap monitors;// = new HashMap(); //not initializing here,

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public ThreadLock() {
		monitors = new HashMap(); }

	/** Constructor taking the Number of Items to Manage
	 * @param numItems The Number of Ressources to manage.
	 * Doesn't need to be exact,
	 * but should be rather larger than smaller.
	 */
	public ThreadLock(int numItems) {
		monitors = new HashMap(numItems); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////
/// Locks on an abstract Item denoted by an int Number
///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks whether the requested record is locked.
	 *
	 * @param record The record number to check for a lock.
	 * @return true iff the Record with the given Number is locked
	 */
	public boolean isLocked(int record) {
		return isLocked(new Integer(record)); }

	/**
	 * Returns the Number of Threads waiting on this Resource
	 * -1 means no  Threads are waiting and no one is locking it
	 * 0 means one Thread  is  waiting and	one is locking it
	 * n means n+1 Threads are waiting and	one is locking it
	 * Access to this Instance should be synchronized to avoid Changes
	 * during or after this Function.
	 * @return the Number of Threads waiting on this Resource
	 * @param record The Number of this Ressource
	 */
	public int getNumWaiters(int record) {
		return getNumWaiters(new Integer(record)); }

	/**
	 * Lock the requested record.
	 * If the argument is -1, lock the whole database.
	 * This method blocks until the lock succeeds.
	 * No timeouts are defined for this.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 *
	 * if lock() is called more often than unlock(),
	 * the DataServer will freeze the last Clients, since no notify() happens.
	 *
	 * @param record The record number to lock.
	 * @param lock Flag indicating whether to (un-) lock the Item.
	 */
	public void setLocked(int record, boolean lock) {
		setLocked(new Integer(record), lock); }

	/**
	 * Lock the requested record.
	 * If the argument is -1, lock the whole database.
	 * This method blocks until the lock succeeds.
	 * No timeouts are defined for this.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 *
	 * if lock() is called more often than unlock(),
	 * the DataServer will freeze the last Clients, since no notify() happens.
	 *
	 * @param record The record number to lock.
	 */
	public void lock(int record) {
		lock(new Integer(record)); }

	/**
	 * Unlock the requested record.
	 * Ignored if the caller does not have a current lock on the requested record.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 * Throws IllegalStateException when unlock() is called more often than lock().
	 * @param record The Number of this Ressource
	 */
	public void unlock(int record) {
		unlock(new Integer(record)); }

///////////////////////////////////////////////////////////////////////////////////
/// Locks on a certain Object
///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks whether the requested record is locked.
	 * Equivalent to getNumWaiters(item) >= 0
	 *
	 * @param item The record Object to check for a lock. Checks 'this' if null.
	 * @return true iff the Object item is locked
	 */
	public synchronized boolean isLocked(Object item) {
		Monitor monitor = (Monitor) monitors.get(item); //
		if (monitor == null) {
			return false; } //
			return (monitor.count >= 0); }

	/**
	 * Returns the Number of Threads waiting on this Resource
	 * -1 means no Threads are waiting and no one is locking it
	 * 0 means one Thread  is  waiting and    one is locking it
	 * n means n+1 Threads are waiting and    one is locking it
	 * Access to this Instance should be synchronized to avoid Changes
	 * during or after this Function.
	 * @return the Number of Threads waiting on this Resource
	 * @param item The managed Ressource
	 */
	public int getNumWaiters(Object item) {
		Monitor monitor = (Monitor) monitors.get(item); //remove the Record from the Locked List.
		if  (monitor == null) {
			return 0; } //or issue a Runtime Exception to warn about using the wrong Protocol...
			return monitor.count; }

	/**
	 * Lock or Unlock the requested record.
	 * If the argument is null, lock the whole database.
	 * This method blocks until the lock succeeds.
	 * No timeouts are defined for this.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 *
	 * if lock() is called more often than unlock(),
	 * the DataServer will freeze the last Clients, since no notify() happens.
	 *
	 * @param item The Object item to lock.
	 * @param lock Flag indicating whether to (un-) lock the Item.
	 */
	public void setLocked(Object item, boolean lock) {
		if (lock) {
			lock  (item);
		} else {
			unlock(item);
		}
	}

	/**
	 * Lock the requested Item.
	 * This method blocks until the lock succeeds.
	 * No timeouts are defined for this.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 *
	 * if lock() is called more often than unlock(),
	 * the Server would freeze the last Clients, since no notify() happens.
	 *
	 * @param item The Ressource to lock. If null, this Object is locked
	 */
	public void lock(Object item) { //parts must be synchronized...
		Monitor monitor; //synchronization lets the Client wait...
		synchronized (this) { //...to  prevent Interference with other lock() or unlock() Threads!
			monitor = (Monitor) monitors.get(item); //the Monitor's Count Property is atomic, but the cached Value is not necessarily the same on two Processors!
			if (monitor == null) { //no Monitor yet...
				monitor = new Monitor(); //monitor.count == 0 here! No Increment necessary
				monitors.put(item, monitor); //...create one and lock the Record.
				if (!locked) { return; } //'return' to make clear that processing proceeds from here, if not globally locked!
				monitor.count = -1; //prepare for the next Thread to wait...
			} //put() is not atomic at all, but synchronizing on 'this' helps and does not hurt here!
		}
		//there is nothing bad that could happen during
		//releasing the Monitor for this ... and
		//acquiring the Monitor for monitor,
		//since the monitor Instance is shared
		//and it's single count Property is atomic!

		//If there is a Monitor, check whether there are Threads waiting for this Object
		synchronized (monitor) { //synchronizing results in the actual count Value Object
			if ((++monitor.count > 0) || (locked)) {//increase the Number of waiting Threads
				try { //since no notifyAll() is used, if() can be used instead of while() Loops
					monitor.wait(); //wait (w.o. Timeout) until the lock is freed. This creates a DeadLock, when also synchronized on 'this'!
				} catch(InterruptedException x) { //should not happen...
					x.printStackTrace(System.err);
					Thread.currentThread().interrupt(); //propagate the Interrupt
				} //
			} //
		}
	}

	/**
	 * Unlock the requested record.
	 * Ignored if the caller does not have a current lock on the requested record.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 * Throws IllegalStateException when unlock() is called more often than lock().
	 * @param item The managed Ressource. If null, this Object is locked
	 */
	public synchronized void unlock(Object item) { //no DeadLock possible, since the Access Order is always the same
		Monitor monitor = (Monitor) monitors.get(item); //remove the Record from the Locked List.
		if   (monitor == null) return; //or issue a Runtime Exception to warn about using the wrong Protocol...
		if((--monitor.count >= 0) && (!locked)) synchronized (monitor) { //reduce the Number of competing Threads
			  monitor.notify();	 //notify one of the waiting Threads, if there is one.
		} else if (monitor.count < -1) { //tolerate or notify superfluous unlock() Calls
			monitor.count = -1;  //correct the Count
			if (!tolerateUnlocks) { //the opposite Situation in lock() cannot be detected ...
				System.err.println("Danger: Calling unlock more often than lock! Count is " + monitor.count);
			}	//...it eventually leads to a lock up of the whole Manager!
		} //this Implementation is very efficient,
	} //since only the right Threads wait for this Monitor!

///////////////////////////////////////////////////////////////////////////////////
/// Locks on 'this', the current Object
///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks whether the requested record is locked.
	 *
	 * @param record The record number to check for a lock.
	 * @return true iff the Record with the given Number is locked
	 */
	public boolean isLocked() {
		return locked; }

	/**
	 * Locks this whole Lock Object unconditionally.
	 * The Locking is not managed, so it doesn't block,
	 * but throws an Exception if applied twice!
	 * For a managed Lock use LockManager.
	 * All current Threads are still running,
	 * but no new Thread will be able to acquire a lock on any managed Object.
	 *
	 * Alternatively, locks could be acquired sequentially on ALL Managed Objects.
	 * But that could take a long time!
	 *
	 *
	 * Throws IllegalStateException if the Manager is not locked.
	 */
	public synchronized void lock() {
		if (locked) {
			throw new IllegalStateException("Already locked!"); }
		locked = true; }

	/**
	 * Unlocks this whole Lock Object unconditionally.
	 * For a managed Lock use LockManager.
	 * Also notifies the first waiting Thread of any blocked Resource.
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 * Used by the Child Class LockManager
	 * to unlock any waiting Thread for single managed Objects.
	 * A Problem would be any Threads currently Having the Lock on single managed Objects!
	 * But since the global Lock is only acquired when there is no single Lock,
	 * of any single Lock exactly one Thread can be woken up!
	 * Throws IllegalStateException if the Manager is not locked.
	 */
	public synchronized void unlock() {
		if (!locked) {
			throw new IllegalStateException("Not locked!"); }
		locked = false;
		Monitor monitor;
		Iterator iter = monitors.values().iterator();
		while (iter.hasNext()) {
			monitor = (Monitor) iter.next();
			if (monitor.count >= 0) synchronized (monitor) { //reduce the Number of competing Threads
				monitor.notify(); }	//notify one of the waiting Threads, if there is one.
		}
	}

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Starts a new Thread with ThreadLockTester Instance on the given Lock Manager and Ressource
	 * @param Locker Lock Manager to test
	 * @param Item Ressource to lock for this Test
	 */
	public static void startThreadLockTester(ThreadLock Locker, int Item) {
		new Thread(new ThreadLockTester(Locker, Item)).start();
		try { //sleep a while to make things more reproduceable.
			Thread.sleep(200);
		} catch (InterruptedException x) {
			x.printStackTrace(System.err);
		}
	}

	/**
	 * Tests all Methods of this Class
	 * This Test Routine checks the correctness of competing local Locks
	 * and the total locking by a single global Lock.
	 * @param args Command Line Parameters
	 * @throws IOException on any Error
	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + ThreadLock.class.getName());
		//write
		ThreadLock Locker = new ThreadLock();
		startThreadLockTester(Locker,  0); //start some concurrent Threads on the same Lock
		startThreadLockTester(Locker,  0); //multiple local Locks, competing...
		startThreadLockTester(Locker, -1); //single global Lock, blocks all other Threads...
		startThreadLockTester(Locker,  0); //none of the following Threads will acquire a Lock
		startThreadLockTester(Locker,  1); //until the global Lock is released
		startThreadLockTester(Locker,  2); //nonetheless e.g. Lock 0 can be released in between.
		startThreadLockTester(Locker,  3); //since the global Lock does not care for local Locks yet.
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

/**
 * Holds one {@link ThreadLock} Lock for five Seconds from its own Thread, printing each Step.
 *
 * <p>Helper Class for testing Class ThreadLock
 * Opened up in its own Thread to demonstrate concurrent Access.
 * A negative Item selects the global Lock instead of an individual one, so running several
 * Instances shows on the Console whether local and global Locks exclude each other.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:47Z
 * digest: a6270f791ff6ac126251f21602260e2b3033eae015dd2d38109924509cc60d5b
 * stale: false
 * -->
 */
class ThreadLockTester
	implements Runnable {

	/** The Item to lock, or a negative Value to take the global Lock instead. */
	private int Item;

	/** The Lock under Test. */
	private ThreadLock Locker;

	/** Prepares for testing the given ThreadLock
	 * on the given Ressource.
	 *
	 * @param Locker_ The Lock Manager to test
	 * @param Item_ The Item to lock, or a negative Value for the global Lock
	 */
	public ThreadLockTester(ThreadLock Locker_, int Item_) {
		this.Locker = Locker_;
		this.Item   = Item_; }

	/** Takes the Lock, holds it for five Seconds, then releases it.
	 *
	 * <p>Method of the Runnable Interface.
	 */
	public void run() {
		System.out.println("Acquiring lock on " + Item);
		if (Item >= 0) {
			Locker.lock(Item); //local Lock
		} else {
			Locker.lock(); //global Lock
		}
		System.out.println("Lock on " + Item + " acquired");
		try {
			Thread.sleep(5000); //do something or just wait...
		} catch (InterruptedException x) {
			x.printStackTrace(System.err);
		}
		System.out.println("Releasing lock on " + Item);
		if (Item >= 0) {
			Locker.unlock(Item); //local Lock
		} else {
			Locker.unlock(); //global Lock
		}
		System.out.println("Lock on " + Item + " released");
	}
}

/**
 * Mutable int Holder serving as one Item's Waiter Count and as the Monitor Threads block on.
 *
 * <p>Helper Class for the ThreadLock Class.
 * Provides a modifyable Wrapper for public primitive int Values.
 * Used as a Counter for the Number of waiting Threads similar to Bag Elements.
 * No Methods are used, so they are left Default.
 *
 * Alternatively also an int[1] could have been used,
 * but that would have been less intuitive
 * and Range Checking on Access is potentially slower than accessing count here.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:47Z
 * digest: baf135b4448d65fb02fbde8d2d307ac937af6f59162de3cc8447bd996d337047
 * stale: false
 * -->
 */
final class Monitor {

	/**
	 * Counter for the Number of waiting Threads for this and for managed Objects
	 * -1 means no  Threads are waiting and no one is locking it
	 *  0 means one Thread  is  waiting and	one is locking it
	 *  n means n+1 Threads are waiting and	one is locking it
	 * made public for simplicity, since this is a local Class
	 */
	public int count; }

