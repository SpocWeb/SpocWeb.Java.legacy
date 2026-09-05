package asynch;

import function.IProcessor;
import graphs.IValueSetter;
import streamIO.IIStreamIn;

/**
  * Title: SynchExecutor<p>
  * Description:
  * An {@link IExecutor} that runs everything synchronously on the calling Thread: execute()
  * calls run()/nextItem()/MapAt() directly and, where a callback is given, invokes it before
  * returning the resulting Future (so callers relying on Future-then-callback ordering should
  * note the callback fires first).
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-10-2002, 12:14 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:41:58Z
  * digest: 607aff006a45f4c1fca1ca47e097f1218c2999e826ce358649054b0069b691a8
  * stale: false
  * tags: [code/thread_pooling]
  * concepts: [Synchronous Executor]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class SynchExecutor
implements IExecutor {

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IExecutor: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Synchronously executes the run() Method of the Runnable Parameter */
	public void execute(Runnable r) { r.run(); }

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
	 */
	public IFuture execute(IIStreamIn r, IValueSetter callBack) {
		IFuture ret = new Future();
		ret.setVal(r.nextItem());
		callBack.setVal(ret); //unfortunately the Callback is called before the Value is returned...
		return ret; } //But this Race Condition can also happen with asynchronous Calls!

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
		IFuture ret = new Future();
		ret.setVal(r.MapAt(arg));
		callBack.setVal(ret); //unfortunately the Callback is called before the Value is returned...
		return ret; } //But this Race Condition can also happen with asynchronous Calls!

	/** This Method executes the nextItem() Method of the IStreamIn Parameter
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Result is returned directly,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and a Future is returned.
	 */
	public IFuture execute(IIStreamIn r) {
		IFuture ret = new Future();
		ret.setVal(r.nextItem());
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
		ret.setVal(r.MapAt(arg));
		return ret; }

	/** This Method executes the nextItem() Method of the IStreamIn Parameter.
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Result is returned directly,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and a Future is returned
     * or if a Callback is being called asynchronously.
     * This Behavior can be implemented generically using an IExecutor and a Future.
	 */
	public void execute(IValueSetter callBack, IIStreamIn r) {
		callBack.setVal(r.nextItem()); }

	/** This Method executes the mapAt() Method of the Processor Parameter.
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Callback is called synchronously,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and the Callback is being called asynchronously.
     * This Behavior can be implemented generically
     * using an IExecutor and a ValueSetterRef.
	 */
	public void execute(IValueSetter callBack, IProcessor r, Object arg) {
		callBack.setVal(r.MapAt(arg)); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + SynchExecutor.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

