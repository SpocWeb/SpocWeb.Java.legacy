package tools; //always define a Package

/**
  * Title: LockAble.java<p>
  * Description:
  * Defines the Interface to be used to maintain Locking of ModifyAble Components.
  * @see ThreadLock
  * @see LockManager
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on 06-02-2001, 08:20 PM<p>
  * @author 	Matthias Heuer
  * @version 1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2006-03-11T01:05:12Z
  * digest: c961b477dbfa4cb63fd91dc4800eee03084f8df2a06818e2382e1a0bcdedd7bd
  * stale: false
  * -->
  */
public interface LockAble {

	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Members
	////////////////////////////////////////////////////////////////////////////

	/** Lock Value indicating no	Lock	*/
	final static public byte LOCK_NONE  = -1;

	/** Lock Value indicating Read  Lock	*/
	final static public byte LOCK_READ  = 0;

	/** Lock Value indicating Write Lock	*/
	final static public byte LOCK_WRITE = 1;

	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Tries to acquire a read or write Lock.
	 * There can be any Number of ReadLocks but only a single WriteLock to synchronize updates.
	 * Only a single ReadLock can be extended to WriteLock.
	 * Any Iterator acquires a ReadLock on the Container.
	 * Any Enumerator tries to acquire a WriteLock on the Container
	 * as soon as it starts to modify it.
	 * @return the LockID to identify the Lock in @see setLock()
	 */
	public int getLock(boolean write);

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
	public int setLock(boolean write, int LockID);

	////////////////////////////////////////////////////////////////////////////
	//  public Methods
	////////////////////////////////////////////////////////////////////////////

}
