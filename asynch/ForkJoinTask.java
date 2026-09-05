package asynch;


/**
  * Title: ForkJoinTask<p>
  * Description:
  * Unfinished sketch of a lightweight, subclassable task abstraction (fork/join style),
  * intended to be cheaper than a full Thread. All lifecycle methods (fork/start/join/reset/cancel)
  * are currently unimplemented stubs; see the "TODO" on yield() and the empty run().
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-15-2002, 12:51 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:43:08Z
  * digest: 77d25188d54a57e07772b48f802bcc1b5801aab03685b085e6784e44c027b56a
  * stale: false
  * tags: [code/deferred_execution]
  * concepts: [Fork-Join Task]
  * facets: {layer: infrastructure, status: legacy, complexity: medium}
  * -->
  */
public abstract class ForkJoinTask
implements Runnable {

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Not implemented: intended to yield the current task's Thread to other pending tasks. */
	final static public void yield() {
	}

	/** Not implemented: intended to run a task and block until it completes. */
	final static public void invoke() {
	}

	/** Not implemented: intended to run the given task concurrently with the caller. */
	final static public void coInvoke(ForkJoinTask fjt) {
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reports whether the Task has finished executing.
	  * @return true iff the Task has finished executing. */
	public abstract boolean isDone();

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Cancels this task, whether it is pending, running, or forked. */
	public abstract void cancel();

	/** Forks off this task for asynchronous, concurrent execution. */
	public abstract void fork();

	/** Starts running this task. */
	public abstract void start();

	/** Blocks the calling thread until this task has finished executing. */
	public abstract void join();

	/** Resets this task to its initial, not-yet-run state so it can be reused. */
	public abstract void reset();

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Runnable: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Not implemented: currently a no-op stub. */
//	public abstract void run() {
	public void run() {
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ForkJoinTask.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

