package asynch;

/**
  * Title: ISynch.java<p>
  * Description:
  * Defines the Interface for synchronizing Constructs.
  * Since most of these conform to the acquire() / release()
  * resp. lock() / unlock() Protocol, this can be separated into this Interface
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * @see Tools.LockAble for an Interface with read / write Locks.
  * @see Tools.ThreadLock and Tools.LockManager for a single Object
  *      that maintains Locks for other Objects (more effective in Servers!).
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-15-2002, 10:09 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: f67843096be1eddaddc02b2ea07d503096d4cdfbdc9e0a078ebcf6c6358bf8fb
  * stale: false
  * tags: [code/concurrency_primitive]
  * concepts: [Synchronization Primitive Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface ISynch {

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** @return true iff this Semaphore is locked:
	  * This Method doesn't really make Sense,
	  * because independent from this Check,
	  * the Semaphore may be locked already on the next Command!
	  */
//	public boolean isLocked();

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** acquires the Lock with indefinite Timeout	 */
	public void lock() throws InterruptedException;

	/** tries to acquire the Lock within the given Timeout	 */
	public boolean lock(long ms) throws InterruptedException;

	/** releases the Lock 	 */
	public void unlock();

}

