package tools;

/**
  * Title: LockedSimple<p>
  * Description:
  * Example for a simple Strategy for synchronized Access:
  * asynchronous non-blocking Locks.
  * These work similar to Semaphores, i.e. they rely on correct Client Use .
  * Without Locking it is not guaranteed that the Number of Unlocks
  * matches the Number of Locks without noticing.
  * This is especially relevant for Read Locking which is designed for Multiple Use.
  *
  * An Improvement can only happen when this Object gives out individual Lock Tokens
  * that are being identified on Release!
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-26-2002, 07:16 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-04T16:35:47Z
  * digest: b087577b23536c6a3e242fe9216dfe5576f91ea0437a27340287b6aa8e74d4d4
  * stale: false
  * -->
  */
public class LockedSimple
//implements LockAble //supports a simpler Locking Model
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

	/** Flag indicating Write Locking	 */
	protected boolean writeLocked;

	/** Counter for the Read Locks	 */
	protected int numReadLocks;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** @return the Number of Read Locks applied on this Object.      */
	public int getNumReadLocks() { return numReadLocks; }

	/**
     * Tries to apply an additional Read (un-)Lock on this Object.
     * This Method does not block.
     * @return true when the lock succeeded.
     */
	public synchronized boolean lockRead(boolean lock) {
		if (lock) {
			if (writeLocked) {
				return false; }
			++numReadLocks;
			return true; }
		if (--numReadLocks < 0) {
			throw new IllegalStateException("More Read Unlocks than Locks!"); }
		return true; }

	/**
     * Tries to apply a Write (un-)Lock on this Object.
     * This Method does not block.
     * @return true when the lock succeeded.
     */
	public synchronized boolean lockWrite(boolean lock) {
		if (lock) {
			if (writeLocked || (numReadLocks > 0)) {
				return false; }
			return writeLocked = true; }
		if ((!writeLocked) || (numReadLocks > 0)) {
			throw new IllegalStateException("Write Unlock despite of no Lock!"); }
		writeLocked = false;
		return true; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected LockedSimple() { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/**
     * Tries to promote a read Lock to a write Lock or vice versa (demote)
     * No other Object can acquire the Lock during this Operation,
     * because it is synchronized.
     * @param promote Flag whether to promote or to demote.
     * @return true iff the Promotion / Demotion worked.
     * Otherwise the Lock Status is left unchanged!
     */
	public synchronized boolean lockRead2write(boolean promote) {
		if (numReadLocks > 1) { //should not happen for demoting
			if (promote) { //for promoting this also prevents it.
				if (lockRead (false)) { //free the Read Lock
					if (lockWrite(true )) {
						return true;
					} else {
						lockRead (true); } //re-acquire the read Lock
				}
			} else { //demoting
				if (lockWrite(false)) {
					if (lockRead (true )) {
						return true;
					} else {
						lockWrite(true); } //re-acquire the read Lock
				}
			}
		} return false; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface LockAble: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface TODO: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + LockedSimple.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}


/**
 * Helper Class for testing Class ThreadLock
 * Opened up in its own Thread to demonstrate concurrent Access.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:47Z
 * digest: 05b55d35d6baa1d7f03e96a7151c3cc608124f216be1a49c2e3d8f4ebc95f7d3
 * stale: false
 * -->
 */
class LockTester
	implements Runnable {

	private LockedSimple Locker;

	/** Prepares for testing the given ThreadLock
	 * on the given Ressource.
	 * @param Locker_ The Lock Manager to test
	 * @param Item_ The Item to lock
	 */
	public LockTester(LockedSimple Locker_) {
		this.Locker = Locker_; }

	/** Method of the Runnable Interface	 */
	public void run() {
		System.out.println("Acquiring lock on " + Locker);
		Locker.lockWrite(true); //local Lock
		System.out.println("Lock on " + Locker + " acquired");
		try {
			Thread.sleep(5000); //do something or just wait...
		} catch (InterruptedException x) {
			x.printStackTrace(System.err);
		}
		System.out.println("Releasing lock on " + Locker);
		Locker.lockWrite(false); //local Lock
		System.out.println("Lock on " + Locker + " released");
	}
}

