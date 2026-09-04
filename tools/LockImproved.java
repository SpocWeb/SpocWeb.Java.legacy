package tools;

import java.util.Hashtable;

/**
  * Non-blocking {@link LockAble} that hands out numbered Read Locks and one exclusive Write Lock.
  *
  * <p>Every Acquisition attempt returns immediately: a Caller that cannot get the Lock is
  * told so via {@link LockAble#LOCK_NONE} rather than being made to wait, which is the
  * Difference to {@link ThreadLock}, whose Write Lock blocks until it is granted.
  * Read Locks are tracked individually in a Hashtable used as a Set, so any Number of them
  * can coexist and each Holder releases exactly its own.
  *
  * <h2>Invariants</h2>
  *
  * <p>Lock IDs are drawn from a single monotonically increasing Counter and are never
  * reused, so an ID identifies its Holder unambiguously. A Write Lock and a Read Lock are
  * mutually exclusive. {@link #getLock(boolean)} and {@link #setLock(boolean, int)} are
  * synchronized, but the underlying accessors are not, so the Interface Methods are the
  * only safe Entry Points for concurrent Callers.
  *
  * <h2>Collaborators</h2>
  *
  * <table>
  * <caption>Types this Class works with</caption>
  * <tr><th>Type</th><th>Relationship</th></tr>
  * <tr><td>{@link LockAble}</td>
  *     <td>Interface implemented, and the Source of the LOCK_NONE Sentinel.</td></tr>
  * <tr><td>{@link ThreadLock}</td>
  *     <td>The blocking Alternative this Class contrasts with.</td></tr>
  * </table>
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-27-2002, 06:43 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-04T16:35:47Z
  * digest: 63e8f740337bd665f16f0f1f47193e3da21b866f84ad3e18cf073776b9981543
  * stale: false
  * -->
  */
public class LockImproved
implements LockAble {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** ID of the Client currently holding the Write Lock, or LOCK_NONE while it is free.	 */
	// TODO: LOGIC: default-initialised to 0, but "free" is encoded as LockAble.LOCK_NONE
	// (-1), so a freshly constructed Instance looks permanently write-locked by Client 0:
	// getWriteLock(int) and getRead_Lock(int) both take their `writeLock != LOCK_NONE`
	// early exit and return LOCK_NONE forever. No Lock of either kind can ever be acquired
	// until this field is initialised to LOCK_NONE.
	protected int writeLock;

	/** Counter for the Read Locks passed out to the Clients	 */
	protected int cntLocks;

	/** Memory for the individual Read Locks, used as a HashSet	 */
	protected Hashtable ReadLocks = new Hashtable();

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Reports how many Read Locks are currently held.
     *
     * @return the Number of Read Locks applied on this Object.
     */
	public int getNumReadLocks() { return ReadLocks.size(); }

	/**
     * Tries to acquire the exclusive Write Lock under a freshly issued ID.
     * This Method does not block.
     *
     * @return the newly issued LockID when the Write Lock was granted,
     *         {@link LockAble#LOCK_NONE} when it is already held
     */
	public int getWriteLock() {
		return getWriteLock(++cntLocks); } //this promotes counting also failed locks!

	/**
     * Tries to acquire the exclusive Write Lock under the given, caller-chosen ID.
     * This Method does not block.
     *
     * @param LockID the ID to record as the Holder of the Write Lock
     * @return LockID when the Write Lock was granted,
     *         {@link LockAble#LOCK_NONE} when it is already held
     */
	protected int getWriteLock(int LockID) {
		if (writeLock != LockAble.LOCK_NONE) {
			return LockAble.LOCK_NONE; }
		return writeLock = LockID; }

	/**
     * Tries to acquire an (additional) Read Lock under a freshly issued ID.
     * This Method does not block.
     *
     * @return the newly issued LockID when the Read Lock was granted,
     *         {@link LockAble#LOCK_NONE} while the Write Lock is held
     */
	public int getRead_Lock() {
		return getRead_Lock(++cntLocks); } //this promotes counting also failed locks!

	/**
     * Tries to acquire an (additional) Read Lock under the given, caller-chosen ID.
     * This Method does not block.
     *
     * @param LockID the ID to register as one of the Read Lock Holders
     * @return LockID when the Read Lock was granted,
     *         {@link LockAble#LOCK_NONE} while the Write Lock is held
     * @throws IllegalStateException when the LockID already exists.
     */
	protected int getRead_Lock(int LockID) {
		if (writeLock != LockAble.LOCK_NONE) {
			return LockAble.LOCK_NONE; }
		Integer ReadLock = new Integer(LockID);
		if (null != ReadLocks.put(ReadLock, ReadLock)) {
			throw new IllegalStateException("Duplicate LockID:" + LockID); }
		return LockID; }

	/**
     * Releases the Write Lock held under the given ID.
     * This Method does not block.
     *
     * @param LockID The ID returned by the {@link #getWriteLock()} Method
     * @return LockID once the Write Lock has been released
     * @throws IllegalArgumentException when the LockID does not match
     * (this should not happen; using Excepion to prevent ignoring the Return Code)
     */
	public int setWriteLock(int LockID) {
		if (writeLock != LockID) {
			throw new IllegalArgumentException("Write Unlock " + LockID + " unknown!"); }
		writeLock = LockAble.LOCK_NONE;
		return LockID; }

	/**
     * Releases the one Read Lock registered under the given ID.
     * This Method does not block.
     *
     * @param LockID The ID returned by the {@link #getRead_Lock()} Method
     * @return LockID once that Read Lock has been removed
     * @throws IllegalArgumentException when the LockID does not match
     * (this should not happen; using Excepion to prevent ignoring the Return Code)
     */
	public int setRead_Lock(int LockID) {
		if (null == ReadLocks.remove(new Integer(LockID))) {
			throw new IllegalArgumentException("Read Unlock " + LockID + " unknown!"); }
		return LockID; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface LockAble: Implementation
////////////////////////////////////////////////////////////////////////////

	/**
     * {@inheritDoc}
     *
     * <p>Acquires either the exclusive Write Lock or an additional Read Lock, without
     * blocking: a Caller that cannot have it is told so rather than made to wait.
     *
     * @return LockAble.LOCK_NONE when the lock failed, the Lock Number otherwise!
     */
	public synchronized int getLock(boolean write) {
		if (write) {
			return getWriteLock();
		} else {
			return getRead_Lock();
		}
	}

	/**
     * {@inheritDoc}
     *
     * <p>Releases the Read or Write Lock held under the given ID, without blocking.
     *
     * @param LockID The ID returned by the {@link #getLock(boolean)} Method
     * @return LockID once the Lock has been released
     * @throws IllegalArgumentException when the LockID does not match
     * (this should not happen; using Excepion to prevent ignoring the Return Code)
     */
	public synchronized int setLock(boolean write, int LockID) {
		if (write) {
			return setWriteLock(LockID);
		} else {
			return setRead_Lock(LockID);
		}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/**
     * Converts a held Read Lock into the Write Lock, or the Write Lock back into a Read Lock.
     *
     * <p>Tries to promote a read Lock to a write Lock or vice versa (demote).
     * No other Object can acquire the Lock during this Operation,
     * because it is synchronized. On Failure the released Lock is re-acquired, so the
     * Caller keeps whatever it had.
     *
     * @param promote Flag whether to promote or to demote.
     * @param LockID the ID of the Lock this Caller already holds
     * @return the same LockID iff the Promotion / Demotion worked, LockAble.LOCK_NONE otherwise
     * Otherwise the Lock Status is left unchanged!
     * @throws IllegalArgumentException when LockID names no Lock this Object handed out
     */
	public synchronized int lockRead2write(boolean promote, int LockID) {
		// TODO: LOGIC: the rollback Guards below are unreachable for a wrong LockID -
		// setRead_Lock/setWriteLock throw IllegalArgumentException instead of returning
		// LOCK_NONE, so the Caller gets an Exception rather than the documented LOCK_NONE,
		// and the released Lock is never restored on that Path.
		if (promote) { //for promoting this also prevents it.
			if (setRead_Lock(LockID) == LockID) { //try to free the Read Lock
				if (getWriteLock(LockID) == LockID) {
					return LockID;
				} else { //undo freeing the Read Lock
					getRead_Lock(LockID);
				}
			}
		} else {
			if (setWriteLock(LockID) == LockID) { //try to free the Write Lock
				if (getRead_Lock(LockID) == LockID) {
					return LockID;
				} else { //undo freeing the Write Lock
					getWriteLock(LockID);
				}
			}
		} return LockAble.LOCK_NONE; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Placeholder Self-Test that currently only announces itself.
	 *
	 * @param args ignored; present so the Method matches the main() Signature
	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + LockImproved.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

