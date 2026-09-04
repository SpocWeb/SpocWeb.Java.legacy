package tools;

import java.io.IOException;

/**
  * Title: LockManager<p>
  * Description: </BR>
  * Extends the ThreadLock Class by Methods to lock this Manager Object itself.
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
  * Known SubClasses: <none> </BR>
  * </BR>
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-01-27, 05;32;10<p>
  * @author	 Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2008-06-29T19:05:40Z
  * digest: d95f5e83196d915be5861ca3b2ebeec6cf190218163e3f9861822ace71a04c9a
  * stale: false
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
	 * Returns the Number of Threads waiting on this Manager
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
	 * Returns the total Number of Threads waiting on any Resource, except for the Manager.
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

	/** Empty Constructor	 */
	public LockManager() { }

	/** Constructor taking the Number of Items to Manage
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
	 * Checks whether the requested record is locked.
	 * The 'locked' Value of the Parent Class is not reused!
	 *
	 * @param record The record number to check for a lock.
	 * @return true iff the Record with the given Number is locked
	 */
	public boolean isLocked() {
		return count < 0; }

	/**
	 * Lock the requested Item.
	 * This method blocks until the lock succeeds.
	 * No timeouts are defined for this.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 *
	 * if lock() is called more often than unlock(),
	 * the DataServer will freeze the last Clients, since no notify() happens.
	 *
	 * @param item The Ressource to lock. If null, this Object is locked
	 */
	public synchronized void lock(Object item) {
		++this.countAll;
		super.lock(item); //default Behavior for a simple Lock
	}

	/**
	 * Unlock the requested record.
	 * Ignored if the caller does not have a current lock on the requested record.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 * Throws IllegalStateException when unlock() is called more often than lock().
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
	 * Implements a global Lock on all Ressources managed by this Lock.
	 * Can only enter this lock when no Ressource is locked.
	 * Blocks locking any other Ressource.
	 * No timeouts are defined for this.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 *
	 * if lock() is called more often than unlock(),
	 * the DataServer will freeze the last Clients, since no notify() happens.
	 *
	 */
	public synchronized void lock() {
		if ((this.countAll >= 0) || //no single Ressource must be locked!
			(this.count	>= 0)) synchronized (this) { //wait for the Lock on this and ALL managed Items
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
	 * Implements a global unlock on all Ressources managed by this Lock.
	 * Can only unlock when all Ressources are locked globally.
	 * Notifies any other Ressource.
	 * Ignored if the caller does not have a current lock on the requested record.
	 * This Mechanism interleaves with the individual Locks
	 * by preferring to allow these and being called on releasing the last individual Lock.
	 *
	 * Must be synchronized to ensure that the current Thread holds the Monitor.
	 * Throws IllegalStateException when unlock() is called more often than lock().
	 */
	public synchronized void unlock() {
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
