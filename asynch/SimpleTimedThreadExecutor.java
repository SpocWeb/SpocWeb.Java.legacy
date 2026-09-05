package asynch;

/**
  * Title: SimpleTimedThreadExecutor<p>
  * Description:
  * A {@link SimpleThreadExecutor} variant that delays execution: it spawns a new Thread that
  * sleeps for a configurable Timer (or a per-call delay) before running the given Runnable.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-15-2002, 07:28 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:43:28Z
  * digest: 8320b81962276e6f69f9d30c4c7cda51016719ca7f2c1319f1ff1279dfa610c6
  * stale: false
  * tags: [code/thread_pooling]
  * concepts: [Timed Thread Executor]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class SimpleTimedThreadExecutor
extends SimpleThreadExecutor {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Timer' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds Timer after which the next execute() Action is triggered   */
protected long Timer;

/** Returns the currently configured delay applied before running a submitted task.
  * @return Timer after which the next execute() Action is triggered  */
public long getTimer() {
	return Timer; }

/** Sets Timer after which the next execute() Action is triggered  */
public void setTimer(long Timer_) {
	this.Timer = Timer_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected SimpleTimedThreadExecutor() { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IExecutor: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Asynchronously executes the run() Method of the Runnable Parameter
	 * by starting a new Thread.
	 * This is quite inefficient, especially for short Tasks.
	 * The Thread is not being reused or pooled.
	 */
	public void execute(final Runnable r) {
		execute(r, Timer); }

	/** Asynchronously executes the run() Method of the Runnable Parameter
	 * by starting a new Thread.
	 * This is quite inefficient, especially for short Tasks.
	 * The Thread is not being reused or pooled.
	 */
	public void execute(final Runnable r, final long Timer) {
		new Thread() {
			public void run() {
				try {
					if (Timer > 0) {
						sleep(Timer); }
					r.run();
				} catch (InterruptedException x) {
				} //ignore the Exception, because r.run() isn't called anyway!
			};
		}.start(); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + SimpleTimedThreadExecutor.class.getName());
		new SimpleTimedThreadExecutor().execute(
			new Runnable() {
				public void run() {
					System.out.println("Timed Task executing now!");
				}
			}, 6000);
		System.out.println("Finished testing " + SimpleTimedThreadExecutor.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

