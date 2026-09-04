package asynch; //

import streamIO.IIStreamIn;
import function.IProcessor;
import graphs.IValueSetter;

/**
  * Title: IExecutor<p>
  * Description:
  * Defines the Interface for exchanging the Algorithm that
  * executes the run() Method of the runnable Parameter.
  *
  * A generic interface for executing Runnables
  * or calling Functions that return a Value both synchronously and asynch.
  * Enables specific execution frameworks to do the work
  * and avoids hardcoding any specific execution scheme
  * (such as thread-per-request for example).
  * <p>
  * For useful schemes, see "Concurrent Programming in Java: Design Principles
  * and Patterns" by Doug Lea (ISBN 0-201-31009-0).
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-10-2002, 12:06 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IExecutor {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** This Method executes the run() Method of the runnable Parameter
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread, whether a new Thread is spawned
     * or a Thread Pool is being used.
     * and a Future is returned
     * or if a Callback is being called asynchronously.
	 */
	void execute(Runnable r);

	/** This Method executes the nextItem() Method of the IStreamIn Parameter
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Result is returned directly,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and a Future is returned
     * or if a Callback is being called asynchronously.
     * This Behavior can be implemented generically using an IExecutor and a Future.
	 */
	IFuture execute(IIStreamIn r);

	/** This Method executes the mapAt() Method of the Processor Parameter
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Result is returned directly,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and a Future is returned
     * or if a Callback is being called asynchronously.
     * This Behavior can be implemented generically using an IExecutor and a Future.
	 */
	IFuture execute(IProcessor r, Object arg);

	/** This Method executes the nextItem() Method of the IStreamIn Parameter.
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Callback is called synchronously,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and the Callback is being called asynchronously.
     * This Behavior can be implemented generically
     * using an IExecutor and a ValueSetterRef.
	 */
	void execute(IValueSetter callBack, IIStreamIn r);

	/** This Method executes the mapAt() Method of the Processor Parameter.
     * Implementing classes may decide on whether the thread is borrowed from
     * the current Thread and the Callback is called synchronously,
     * or whether a new Thread is spawned or a Thread Pool is being used
     * and the Callback is being called asynchronously.
     * This Behavior can be implemented generically
     * using an IExecutor and a ValueSetterRef.
	 */
	void execute(IValueSetter callBack, IProcessor r, Object arg);

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
	IFuture execute(IIStreamIn r, IValueSetter callBack);

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
	IFuture execute(IProcessor r, Object arg, IValueSetter callBack);

}

