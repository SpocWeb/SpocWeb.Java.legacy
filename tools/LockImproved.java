package tools;

import java.util.Hashtable;

/**
  * Title: LockImproved<p>
  * Description:
  * TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
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
  * mtime: 2003-01-25T23:39:20Z
  * digest: bef9aceb6f360b015bf24d1bb628e4bd52f8537c8b79a60a80035efe5ed83fee
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

	/** Flag indicating Write Locking	 */
	protected int writeLock;

	/** Counter for the Read Locks passed out to the Clients	 */
	protected int cntLocks;

	/** Memory for the individual Read Locks, used as a HashSet	 */
	protected Hashtable ReadLocks = new Hashtable();

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** @return the Number of Read Locks applied on this Object.      */
	public int getNumReadLocks() { return ReadLocks.size(); }

	/**
     * Tries to acquire an (additional) Read Lock on this Object.
     * This Method does not block.
     * @param LockID The ID returned by the getLockRead() Method
     * @return the LockID when the unlock succeeded, LockAble.LOCK_NONE otherwise
     * @throws IllegalArgumentException when the LockID does not match
     * (this should not happen; using Excepion to prevent ignoring the Return Code)
     */
	public int getWriteLock() {
		return getWriteLock(++cntLocks); } //this promotes counting also failed locks!

	/**
     * Tries to acquire an (additional) Read Lock on this Object.
     * This Method does not block.
     * @param LockID The ID returned by the getLockRead() Method
     * @return the LockID when the unlock succeeded, LockAble.LOCK_NONE otherwise
     * @throws IllegalArgumentException when the LockID does not match
     * (this should not happen; using Excepion to prevent ignoring the Return Code)
     */
	protected int getWriteLock(int LockID) {
		if (writeLock != LockAble.LOCK_NONE) {
			return LockAble.LOCK_NONE; }
		return writeLock = LockID; }

	/**
     * Tries to acquire an (additional) Read Lock on this Object.
     * This Method does not block.
     * @param LockID The ID returned by the getLockRead() Method
     * @return the LockID when the unlock succeeded, LockAble.LOCK_NONE otherwise
     * @throws IllegalStateException when the LockID already exists.
     */
	public int getRead_Lock() {
		return getRead_Lock(++cntLocks); } //this promotes counting also failed locks!

	/**
     * Tries to acquire an (additional) Read Lock on this Object.
     * This Method does not block.
     * @param LockID The ID returned by the getLockRead() Method
     * @return the LockID when the unlock succeeded, LockAble.LOCK_NONE otherwise
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
     * Tries to free this Object from the given Write Lock.
     * This Method does not block.
     * @param LockID The ID returned by the getLockRead() Method
     * @return the LockID when the unlock succeeded, LockAble.LOCK_NONE otherwise
     * @throws IllegalArgumentException when the LockID does not match
     * (this should not happen; using Excepion to prevent ignoring the Return Code)
     */
	public int setWriteLock(int LockID) {
		if (writeLock != LockID) {
			throw new IllegalArgumentException("Write Unlock " + LockID + " unknown!"); }
		writeLock = LockAble.LOCK_NONE;
		return LockID; }

	/**
     * Tries to free this Object from the given Read Lock.
     * This Method does not block.
     * @param LockID The ID returned by the getLockRead() Method
     * @return the LockID when the unlock succeeded, LockAble.LOCK_NONE otherwise
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
     * Tries to acquire an (additional) Read Lock on this Object.
     * This Method does not block.
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
     * Tries to free this Object from the given Read or Write Lock.
     * This Method does not block.
     * @param LockID The ID returned by the getLockRead() Method
     * @return the LockID when the unlock succeeded, LockAble.LOCK_NONE otherwise
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
     * Tries to promote a read Lock to a write Lock or vice versa (demote)
     * No other Object can acquire the Lock during this Operation,
     * because it is synchronized.
     * @param promote Flag whether to promote or to demote.
     * @return the same LockID iff the Promotion / Demotion worked, LockAble.LOCK_NONE otherwise
     * Otherwise the Lock Status is left unchanged!
     */
	public synchronized int lockRead2write(boolean promote, int LockID) {
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

	/** Tests all Methods of this Class	 */
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

