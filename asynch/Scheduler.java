package asynch;

import java.util.List;
import java.util.Vector;

import knowledge.IReadyFlag;
import streamIO.IIStreamOut;

/**
  * Title: Scheduler<p>
  * Description:
  * Scheduler of the generic Active Object Pattern.
  * Purpose:
  * Command Processor (Executor)
  * for sequentially scheduling Command Objects in a List.
  * The Order is dependent on the isDirty() Flag in the respective Tasks.
  *
  * An Application for this is e.g. a Hard Drive with many Cylinders,
  * but only a single Read Head.
  * To optimize Throughput it is advantageous to queue and block Requests
  * until their Cylinder is being read and to read Cylinders in ascending
  * and descending Order.
  *
  * see Doug Lea, "Concurrent Programming in Java"
  * This is a special Case of the FifoSemaphore in 3.7.3.2 with only 1 Permit,
  * i.e. a FifoMutex.
  * see POSA2 Book: Active Object Pattern
  *
  * Design Decisions / Implementation Details:
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 08:29 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Scheduler
extends AExecutor
implements Runnable, IIStreamOut, IExecutor {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Class to handle Exception during Notification. */
	public IIStreamOut RuntimeExceptionHandler;// = Stream.Log.L;

	/** Completely synchronized(!) unbounded Buffer.
	  * An alternative is a bounded Buffer in wrap-around Order (Queue)
	  * Synchronization is necessary for Consistency.
	  */
	protected List Operations = new Vector();

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected Scheduler() {
		new Thread(this).start(); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method called by the Scheduler encapsulating which Method to call */
	public void run() {
		Runnable r;
		while (true) { //loop infinitely
			int i = Operations.size(); //don't use an Iterator...
			while (--i >= 0) { //the Index should never be out of Bounds!
				r = (Runnable) Operations.get(i); //because only this Thread removes Items!
				if (r instanceof IReadyFlag) { //The Order is defined by the ReadyToRun Status
					if (((IReadyFlag) r).isDirty()) { //but other Orders are imaginable!
						continue; } }
				try {
					r.run();
				} catch (RuntimeException x) {
					if (RuntimeExceptionHandler != null) {
						RuntimeExceptionHandler.addItem(x); }
				}
				Operations.remove(i);
			}
		}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IStreamOut: Implementation
////////////////////////////////////////////////////////////////////////////////

	public void execute(Runnable arg) {
		Operations.add(arg);
	}

	public void addRequest(ReadyToRun arg) {
		Operations.add(arg);
	}

	public IIStreamOut addItem(Object arg) {
		addRequest((ReadyToRun) arg);
		return this; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Scheduler.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

