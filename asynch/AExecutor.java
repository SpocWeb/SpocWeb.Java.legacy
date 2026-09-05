package asynch;

import function.IProcessor;
import graphs.IValueSetter;
import streamIO.IIStreamIn;

/**
  * Title: AExecutor<p>
  * Description:
  * Abstract base implementation of {@link IExecutor} that derives the IStreamIn/IProcessor
  * and callback/Future overloads of execute() from a single abstract execute(Runnable),
  * by wrapping the call in a {@link ValueSetterRef}, {@link ProcessorRef}, {@link Future}
  * or {@link CallbackFuture} as appropriate. Subclasses only need to implement execute(Runnable).
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-10-2002, 10:54 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:41:11Z
  * digest: ccc8f720c6dff0e5d2c6dcfbf72bbb75c46f183bdd6cfa1e8162326e7554c4ed
  * stale: false
  * tags: [code/thread_pooling]
  * concepts: [Executor Base Class]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public abstract class AExecutor
implements IExecutor {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IExecutor: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** This Method executes the run() Method of the runnable Parameter
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread, whether a new Thread is spawned
     * or a Thread Pool is being used.
	 */
	public abstract void execute(Runnable r);

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IExecutor: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** This Method executes the nextItem() Method of the IStreamIn Parameter.
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Result is returned directly,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and a Future is returned
     * or if a Callback is being called asynchronously.
     * This Behavior can be implemented generically using an IExecutor and a Future.
	 */
	public synchronized void execute(IValueSetter callBack, IIStreamIn stream) {
		execute(new ValueSetterRef(stream, callBack)); }

	/** This Method executes the mapAt() Method of the Processor Parameter.
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Callback is called synchronously,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and the Callback is being called asynchronously.
     * This Behavior can be implemented generically
     * using an IExecutor and a ValueSetterRef.
	 */
	public void execute(IValueSetter callBack, IProcessor r, Object arg) {
		execute(new ProcessorRef(r, arg, callBack)); }

	/** This Method executes the nextItem() Method of the IStreamIn Parameter
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Result is returned directly,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and a Future is returned.
     * This Behavior can be implemented generically using an IExecutor and a Future.
	 */
	public synchronized IFuture execute(IIStreamIn r) {
		IFuture ret = new Future();
		execute(new ValueSetterRef(r, ret));
		return ret; }

	/** This Method executes the mapAt() Method of the Processor Parameter
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Result is returned directly,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and a Future is returned
     * or if a Callback is being called asynchronously.
     * This Behavior can be implemented generically using an IExecutor and a Future.
	 */
	public IFuture execute(IProcessor r, Object arg) {
		IFuture ret = new Future();
		execute(new ProcessorRef(r, arg, ret));
		return ret; }

	/** This Method executes the nextItem() Method of the IStreamIn Parameter.
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Callback is called synchronously,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and the Callback is being called asynchronously.
     * This Behavior can be implemented generically
     * using an IExecutor and a CallbackFuture.
     *
     * To allow identifying the Origin of the Callback,
     * a Cookie Object (the CallbackFuture) is returned that can be stored
     * and tested for Identity in the Callback!
     *
     * There is a Race Condition between calling the Callback and returning this Future!
	 */
	public synchronized IFuture execute(IIStreamIn r, IValueSetter callBack) {
		IFuture ret = new CallbackFuture(callBack);
		execute(new ValueSetterRef(r, ret));
		return ret; }

	/** This Method executes the mapAt() Method of the Processor Parameter
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Callback is called synchronously,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and the Callback is being called asynchronously.
     * This Behavior can be implemented generically
     * using an IExecutor and a CallbackFuture.
     *
     * To allow identifying the Origin of the Callback,
     * a Cookie Object (the CallbackFuture) is returned that can be stored
     * and tested for Identity in the Callback!
     *
     * There is a Race Condition between calling the Callback and returning this Future!
	 */
	public IFuture execute(IProcessor r, Object arg, IValueSetter callBack) {
		IFuture ret = new CallbackFuture(callBack);
		execute(new ProcessorRef(r, arg, ret));
		return ret; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + AExecutor.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

