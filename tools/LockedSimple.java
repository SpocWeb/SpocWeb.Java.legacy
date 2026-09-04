package tools;

/**
  * Counting, non-blocking Read/Write Lock that trusts its Clients to unlock exactly once.
  *
  * <p>Example for a simple Strategy for synchronized Access:
  * asynchronous non-blocking Locks.
  * These work similar to Semaphores, i.e. they rely on correct Client Use .
  * Without Locking it is not guaranteed that the Number of Unlocks
  * matches the Number of Locks without noticing.
  * This is especially relevant for Read Locking which is designed for Multiple Use.
  *
  * <p>An Improvement can only happen when this Object gives out individual Lock Tokens
  * that are being identified on Release! {@link LockImproved} is exactly that Improvement.
  *
  * <h2>Invariants</h2>
  *
  * <p>The Write Flag and the Read Counter are mutually exclusive: a Write Lock is only
  * granted while the Read Counter is zero, and a Read Lock only while the Write Flag is
  * clear. Because Locks carry no Identity, the Counter is only correct as long as every
  * Client releases exactly what it took; an extra Release is detected only when it drives
  * the Counter negative.
  *
  * <h2>Collaborators</h2>
  *
  * <table>
  * <caption>Types this Class works with</caption>
  * <tr><th>Type</th><th>Relationship</th></tr>
  * <tr><td>{@link LockImproved}</td>
  *     <td>The token-based Successor that fixes the Identity Weakness described above.</td></tr>
  * <tr><td>{@link LockAble}</td>
  *     <td>Deliberately NOT implemented: the commented-out Declaration records that this
  *         Class supports a simpler Model without Lock IDs.</td></tr>
  * </table>
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
  * digest: bf5ce80fb5da03de8d39f8cf92cc492fb76fbc322829c912e7a41e679ecb871c
  * stale: false
  * tags: [code/locking, code/reference_counting, code/non_blocking]
  * concepts: [Concurrency]
  * facets: {layer: infrastructure, status: broken, complexity: medium}
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

	/** Reports how many Read Locks are currently held.
     *
     * @return the Number of Read Locks applied on this Object.
     */
	public int getNumReadLocks() { return numReadLocks; }

	/**
     * Takes or returns one Read Lock, refusing to take one while the Write Lock is held.
     * This Method does not block.
     *
     * @param lock {@code true} to take a Read Lock, {@code false} to return one
     * @return true when the lock succeeded.
     * @throws IllegalStateException on a Release that drives the Read Counter negative
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
     * Takes or returns the exclusive Write Lock, refusing it while any Read Lock is held.
     * This Method does not block.
     *
     * @param lock {@code true} to take the Write Lock, {@code false} to release it
     * @return true when the lock succeeded.
     * @throws IllegalStateException on a Release while the Write Lock is not held, or
     *         while a Read Lock exists
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

	/** Creates an unlocked Instance; protected so only Subclasses expose Locking. */
	protected LockedSimple() { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/**
     * Converts a held Read Lock into the Write Lock, or the Write Lock back into a Read Lock.
     *
     * <p>Tries to promote a read Lock to a write Lock or vice versa (demote).
     * No other Object can acquire the Lock during this Operation,
     * because it is synchronized. Whichever Lock was released is re-acquired if the second
     * Step fails, so a failed Conversion is not observable.
     *
     * @param promote Flag whether to promote or to demote.
     * @return true iff the Promotion / Demotion worked.
     * Otherwise the Lock Status is left unchanged!
     */
	public synchronized boolean lockRead2write(boolean promote) {
		// TODO: LOGIC: this Guard makes the whole Method a no-op that always returns false.
		// Promotion needs exactly ONE Read Lock (the Caller's own), but `> 1` only enters
		// when at least two exist - and then lockWrite(true) is refused because a Read Lock
		// remains. Demotion needs the Write Lock held, which implies numReadLocks == 0, so
		// the else Branch is unreachable. The Conditions should be numReadLocks == 1 for
		// promote and numReadLocks == 0 for demote.
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

	/** Placeholder Self-Test that currently only announces itself.
	 *
	 * @param args ignored; present so the Method matches the main() Signature
	 */
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
 * Holds a {@link LockedSimple}'s Write Lock for five Seconds from its own Thread.
 *
 * <p>Helper Class for testing Class ThreadLock
 * Opened up in its own Thread to demonstrate concurrent Access.
 * Each Step is printed, so running two Instances against one Lock shows on the Console
 * whether the second one was actually kept out.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:47Z
 * digest: b1e7b5030c1e9c3203fa2a17aa606851c34b8eb7a145ede462342e80554c06f6
 * stale: false
 * tags: [code/manual_test_harness, code/locking]
 * concepts: [Concurrency]
 * facets: {layer: utility, status: experimental, complexity: low}
 * -->
 */
class LockTester
	implements Runnable {

	/** The Lock this Tester acquires and releases. */
	private LockedSimple Locker;

	/** Prepares for testing the given ThreadLock
	 * on the given Ressource.
	 *
	 * @param Locker_ The Lock Manager to test
	 */
	public LockTester(LockedSimple Locker_) {
		this.Locker = Locker_; }

	/** Takes the Write Lock, holds it for five Seconds, then releases it.
	 *
	 * <p>Method of the Runnable Interface.
	 */
	// TODO: LOGIC: lockWrite(true)'s Result is discarded, so a refused Lock is treated as
	// acquired: the Thread prints "Lock acquired", sleeps, and then calls lockWrite(false),
	// which releases a Lock it never held - or throws IllegalStateException. That makes the
	// Test report Success in exactly the contended Case it exists to exercise.
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

