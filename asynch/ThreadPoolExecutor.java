package asynch;

//import Stream.IStreamOut;
import streamIO.object.IPipe;

/**
  * Title: ThreadPoolExecutor<p>
  * Description:
  * Purpose:
  * A Thread Pool creating a List of Threads working on a common Queue.
  * This introduces a certain amount of Concurrency
  * without the Overhead of continuously creating and destroying Threads!
  *
  * Design Decisions / Implementation Details:
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
public class ThreadPoolExecutor
extends ThreadExecutor {

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor */
	protected ThreadPoolExecutor(int numThreads, IPipe pipe_) {
		super(false);
		this.pipe = pipe_;
		while (--numThreads >= 0) {
			startThread(); }
	}

	/** starts another Thread working on this Queue */
	protected void startThread() {
//		Runnable runLoop = new Runnable() {
		new Thread() {
			public void run() {
				try {
					while (true) {
						Runnable r = (Runnable) pipe.nextItem(); --numTasks;
						if (r == null) { //Tolerance for both blocking and non blocking Pipes:
							this.wait(); //synchronize on the Pipe or on 'this'
						} else {
							r.run();
						}
					}
				} catch (InterruptedException x) {}
			}
		}.start();
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ThreadPoolExecutor.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

