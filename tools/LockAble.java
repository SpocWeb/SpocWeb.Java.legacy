package tools; //always define a Package

/**
  * Contract for a Component whose Readers and Writers coordinate through explicit Lock IDs.
  *
  * <p>Defines the Interface to be used to maintain Locking of ModifyAble Components.
  * A Lock ID returned by {@link #getLock(boolean)} is the Caller's only Handle on its own
  * Lock, and is what {@link #setLock(boolean, int)} needs in order to release or upgrade it.
  *
  * <h2>Invariants</h2>
  *
  * <p>There can be any Number of Read Locks but only a single Write Lock, so that Updates
  * are serialized; only a single Read Lock can be extended to a Write Lock, and a Write
  * Lock can only be acquired while no other Client holds a Read Lock.
  *
  * <h2>Collaborators</h2>
  *
  * <table>
  * <caption>Types implementing or using this Interface</caption>
  * <tr><th>Type</th><th>Relationship</th></tr>
  * <tr><td>{@link ThreadLock}</td>
  *     <td>Provides the blocking Write-Lock Mechanism this Interface exposes.</td></tr>
  * <tr><td>{@link LockManager}</td>
  *     <td>Extends ThreadLock so the Manager Object itself can be locked.</td></tr>
  * <tr><td>{@link LockedServer}</td>
  *     <td>Example Base Class implementing this Interface on top of a LockManager.</td></tr>
  * </table>
  *
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
  * mtime: 2026-09-04T16:35:47Z
  * digest: 6766c96ec19e37bb231427110229f55fbe0332af8008893a4382afe1529389f5
  * stale: false
  * tags: [code/locking, code/interface_contract]
  * concepts: [Concurrency]
  * facets: {layer: infrastructure, status: stable, complexity: low}
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
	 * Tries to acquire a read or write Lock and returns the ID identifying it.
	 *
	 * <p>There can be any Number of ReadLocks but only a single WriteLock to synchronize updates.
	 * Only a single ReadLock can be extended to WriteLock.
	 * Any Iterator acquires a ReadLock on the Container.
	 * Any Enumerator tries to acquire a WriteLock on the Container
	 * as soon as it starts to modify it.
	 *
	 * @param write {@code true} for an exclusive Write Lock, {@code false} for a shared Read Lock
	 * @return the LockID to identify the Lock in {@link #setLock(boolean, int)}
	 */
	public int getLock(boolean write);

	/**
	 * Tries to release or change the Lock previously acquired under the given LockID.
	 *
	 * <p>There can be any Number of ReadLocks (has to be maintained)
	 * but only a single WriteLock to synchronize updates.
	 * Only a single ReadLock can be extended to WriteLock.
	 * A WriteLock can only be acquired when no ReadLock exists
	 * (except possibly by this Client identified by the LockID)
	 * Any non blocking Iterator acquires a ReadLock on the Container.
	 * Any Enumerator tries to acquire a WriteLock on the Container
	 * as soon as it starts to modify it.
	 *
	 * @param write {@code true} to hold or take the Write Lock, {@code false} for a Read Lock
	 * @param LockID the ID a previous {@link #getLock(boolean)} returned to this Client
	 * @return the LockID if succeeded, {@link #LOCK_NONE} otherwise.
	 */
	public int setLock(boolean write, int LockID);

	////////////////////////////////////////////////////////////////////////////
	//  public Methods
	////////////////////////////////////////////////////////////////////////////

}
