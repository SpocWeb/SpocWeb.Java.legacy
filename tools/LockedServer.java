package tools;

/**
  * Title: LockedServer<p>
  * Description:
  * Example Base Class that uses the LockManager
  * and implements the LockAble Interface.
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-26-2002, 02:56 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2003-01-25T23:39:20Z
  * digest: d1c10d0889c343247cff73da04a0f517de7e74d6021f0c164618030144c4fbcc
  * stale: false
  * -->
  */
public class LockedServer
implements LockAble 
{

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Number of Read Locks applied
	 * If this is not managed in this Object you need a @see Bag
	 * to maintain it for a Set of Objects.
	 */
	protected int numReadLocks;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected LockedServer() { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface TODO: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface LockAble: Implementation
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tries to acquire a read or write Lock.
	 * There can be any Number of ReadLocks but only a single WriteLock to synchronize updates.
	 * Only a single ReadLock can be extended to WriteLock.
	 * Any Iterator acquires a ReadLock on the Container.
	 * Any Enumerator tries to acquire a WriteLock on the Container
	 * as soon as it starts to modify it.
	 * @return the LockID to identify the Lock in @see setLock()
	 */
	public int getLock(boolean write) {
		return -1; }

	/**
	 * Tries to release the identified read or write Lock with the given LockID.
	 * There can be any Number of ReadLocks (has to be maintained)
	 * but only a single WriteLock to synchronize updates.
	 * Only a single ReadLock can be extended to WriteLock.
	 * A WriteLock can only be acquired when no ReadLock exists
	 * (except possibly by this Client identified by the LockID)
	 * Any non blocking Iterator acquires a ReadLock on the Container.
	 * Any Enumerator tries to acquire a WriteLock on the Container
	 * as soon as it starts to modify it.
	 * @return the LockID if succeeded, -1 otherwise.
	 */
	public int setLock(boolean write, int LockID) {
		return -1; }

	/**Returns the maximum Lock Level of the Container to support prioritized Locking.
	 * There can be any Number of ReadLocks but only a single WriteLock to synchronize updates.
	 * Only a single ReadLock can be extended to WriteLock.
	 * Any Iterator acquires a ReadLock on the Container.
	 * Any Enumerator tries to acquire a WriteLock on the Container
	 * as soon as it starts to modify it.
	 */
	public int getLock() {
		if (numReadLocks > 0)  {
			return LOCK_READ ; }
		if (ThreadLock.ThreadLock.isLocked(this)) {
			return LOCK_WRITE; }
			return LOCK_NONE ; }

	/**Sets the Lock Level of the Container to support prioritized Locking
	 * and returns the LockID, which is necessary to release the Lock.
	 * There can be any Number of ReadLocks (has to be maintained)
	 * but only a single WriteLock to synchronize updates.
	 * Only a single ReadLock can be extended to WriteLock.
	 * A WriteLock can only be acquired when no ReadLock exists
	 * (except possibly by this Client identified by the LockID)
	 * Any non blocking Iterator acquires a ReadLock on the Container.
	 * Any Enumerator tries to acquire a WriteLock on the Container
	 * as soon as it starts to modify it.
	 */
	public int setLock(byte LockLevel, int LockID) {
		switch (LockLevel) {
			case LOCK_NONE : //either release a READ or a WRITE Lock
			case LOCK_READ : //either add a READ Lock or release a WRITE Lock
				//this blocks when a WRITE Lock exists
			case LOCK_WRITE: //set a WRITE Lock or promote a READ Lock to WRITE,
				//this blocks when a WRITE Lock or a READ Lock already exists.
		}
		if (numReadLocks > 0)  {
			return LOCK_READ ; }
		int ret = LockID;
		return ret; }

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
/*		if (lock) {
			lock  (item);
		} else {
			unlock(item);
		}
*/	}

	boolean writeLocked; 
	
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
	public synchronized void lockWrite() { //must be synchronized...
		if (writeLocked) { //wait for the Unlock
			try { //since no notifyAll() is used, if() can be used instead of while() Loops
				wait(); //wait (w.o. Timeout) until the lock is freed. This creates a DeadLock, when also synchronized on 'this'!
			} catch(InterruptedException x) { //should not happen...
				x.printStackTrace(System.err);
				Thread.currentThread().interrupt(); //propagate the Interrupt
			} //
		}
		writeLocked = true;
	}

	/**
	 * Unlock the requested record.
	 * Ignored if the caller does not have a current lock on the requested record.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 * Throws IllegalStateException when unlock() is called more often than lock().
	 * @param item The managed Ressource. If null, this Object is locked
	 */
	public synchronized void unlockWrite() { //no DeadLock possible, since the Access Order is always the same
		writeLocked = false;
		notify();	 //notify one of the waiting Threads, if there is one.
	} //since only the right Threads wait for this Monitor!

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + LockedServer.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

