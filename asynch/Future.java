package asynch;

import graphs.IValue;

import java.lang.reflect.InvocationTargetException;

import streamIO.exception.BaseException;
import synch.ACachedProperty;

/**
  * Title: Future<p>
  * Description:
  * Purpose:
  * A Future is a Handle to the Result of an asynchronous Operation. 
  *
  * Unfortunately a simple Callback Function has no direct way to determine
  * what the Origin of this Call is when SEVERAL Threads could call it back!
  * One Solution is to hand over a Cookie during the Call,
  * which should be returned on the Callback!
  *
  * Design Decisions / Implementation Details: 
  * cannot derive from Value, because their Getters and Setters are final for Performance Reasons. 
  * @see ACachedProperty does not allow to set the Value,
  * only to recalculate it!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 09:52 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:43:12Z
  * digest: b2e97b6329f7b1e1b592d613712edda1a487d1680d249cf59076eae0cb29ee55
  * stale: false
  * tags: [code/deferred_execution]
  * concepts: [Future Result Holder]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class Future
//extends Value
implements IFuture, IValue { //IDirtyFlag {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Value of this Pair
	  * Due to the Fact that most Algorithms use the Interface,
	  * and the actual Class is not relied upon,
	  * the public Properties should not be tampered with
	  * and are only for Performance Boosting in critical Situations.
	  */
	public Object val;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Atomic Flag to optimize Synchronization. 
	 * Determines, whether the Cache is valid / the Value has been set
	 * Alternatively the Value 'null' could be used!
	 * Since a boolean Value is atomic, it needn't be synchronized!
	 * This Flag or the isDirty() Method can be used to actively poll the Future:
	 * while(isDirty()) { sleep(50); doSthg(); }
	 */
	public volatile boolean dirty = true;

	/** Reference to the Exception thrown	 */
	protected Throwable exception;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** sets the Exception: 	 */
	public synchronized void setException(final Throwable x) {
		dirty = true ; this.exception = x;
		dirty = false; notifyAll(); } //notify(); } //notify all Threads!
	
	/** Accessor Writer Method, blocks all readers.
	  * @param sets Value of the Pair */
	public synchronized void setVal(final Object Value) {
		dirty = true ; this.val = Value;
		dirty = false; notifyAll(); } //notify(); } //notify all Threads!
	
	/** synchronous Accessor Method with Timeout
	  * Actually multiple Readers could read in parallel Threads
	  * as long as they block the Writer Method and force a Memory Barrier.
	  * @param TimeOut the Timeout to wait for the Result
	  * @return <{Value}> when set
	  * @see Semaphore .lock(ms) for a similar, but more complex Mechanism,
	  *      due to multiple Locks and multiple Shots!
	  */
	public Object getVal(final long TimeOut) 
	throws InterruptedException, InvocationTargetException {
		//only synchronize when still dirty!! This speeds up Access.
		if (dirty) synchronized (this) { //since this is a one-Shot, an if Statement would suffice!
			if (TimeOut > 0) { //wait at most this long...
				wait(TimeOut); //on Timeout or Notification...
			} else if (TimeOut < 0) { //wait infinitely...
				wait(); //Since this is a one Shot Mechanism,
			} //it is sufficient to test the Timeout
			if (dirty) { //check the Condition again
				throw new InterruptedException("Timeout encountered!"); }
		}
		if (exception != null) {
			throw new InvocationTargetException(exception); }
		return val; } //the next Thread will not have Access until this Method releases the Lock though

	/** synchronous Accessor Method with infinite Timeout
	  * @return <{Value}>*/
	public synchronized Object getVal() { //throws InterruptedException {
		try { return getVal(0);
		} catch (final Exception x) {
			throw new BaseException("in Future.getVal()", x);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IDirtyFlag: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reports whether this Future's Result (or Exception) has not yet been set.
	  * @return true, when this Object has been modified, false otherwise */
	public boolean isDirty() { return dirty; }
	
	/** (re-)sets the Dirty Flag 	 */
//	public void setDirty(boolean dirty_) { this.dirty = dirty_; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor
	  * Usually a Future is initialized dirty!	 */
	public Future() { } //dirty = true; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Future.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }
	
}

