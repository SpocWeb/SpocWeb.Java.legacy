package asynch;

import streamIO.IIStreamOut;

/**
  * Title: SimpleThreadPoolExecutor<p>
  * Description:
  * Purpose:
  * A simple Thread Pool creating a List of ThreadExecutors does not work properly,
  * because the Executors may be blocking or just queueing during assignment of a Task
  * and the Feedback Mechanism via getNumTasks() to the Executor is not reliable!
  * On Dispatch it iterates over all of them trying to find an available one.
  * If not, it waits until the first Thread is available and puts it on this Thread.
  *
  * Design Decisions / Implementation Details:
  * This is quite inefficient, especially for short Tasks.
  * The Thread is not being reused or pooled.
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
  */
public class SimpleThreadPoolExecutor
extends AExecutor {

////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////

	/**
	 * Either use Delegation or Inheritance.
	 * Here Delegation seems to be more appropriate,
	 * on the other Hand the Executor can also behave like a Thread...
	 * Since this is always Thread.currentThread, the Parameter seems unnecessary,
	 * but it is easier to access and control here.
	 */
	protected ThreadExecutor[] threads;

	public void setInterruptionHandler(IIStreamOut InterruptionHandler) {
		int i = threads.length;
		while (--i >= 0) {
			threads[i].InterruptionHandler = InterruptionHandler; }
	}

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor */
	protected SimpleThreadPoolExecutor(int numThreads, int Capacity) {
		threads = new ThreadExecutor[numThreads];
		while (--numThreads >= 0) {
			threads[numThreads] = new ThreadExecutor(Capacity); }
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** stops the Processing Loop and ends this Thread
	  * after completing the current Request.
	  * Without stopping the Thread stays blocked
	  * and keeps this Executor alive too!
	  */
	public void stop() {
		int i = threads.length;
		while (--i >= 0) {
			threads[i].stop(); }
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IExecutor: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Asynchronously executes the run() Method of the Runnable Parameter
	 * by starting a new Thread.
	 * This is quite inefficient, especially for short Tasks.
	 * The Thread is not being reused or pooled.
	 *
	 * Distribute the Tasks between the Threads of the Pool.
	 * Simply passing around the Token in Round Robin Fashion
	 * does not really help, because the Duration could be quite different.
	 * It is better to distribute the Tasks based on the Number of Tasks
	 * left in the Thread, i.e. by adding it to the Thread with the least Number of Tasks.
	 */
	public synchronized void execute(Runnable r) {
		int numTasks, i;
		int minTasks = Integer.MAX_VALUE;
		int minThread = i = threads.length;
		while (--i >= 0) {
			if (minTasks > (numTasks = threads[i].getNumTasks())) {
				minTasks  = numTasks;
				minThread = i; }
		}
		threads[minThread].execute(r);
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + SimpleThreadPoolExecutor.class.getName());

	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

