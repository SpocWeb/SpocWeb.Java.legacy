package asynch;

/**
  * Title: SimpleTimedThreadExecutor<p>
  * Description:
  * Purpose:
  * asynchronously executes the given Task with the given Delay by creating a new Thread
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
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

/** @return Timer after which the next execute() Action is triggered  */
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

