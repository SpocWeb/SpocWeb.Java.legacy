package asynch;


/**
  * Title: ForkJoinTask<p>
  * Description:
  * Purpose:
  * Task Class, more lightweight than a Thread or even a Process
  * Supposed to be subclassed and implemented.
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
  * Created on	09-15-2002, 12:51 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class ForkJoinTask
implements Runnable {

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Purpose: TODO  */
	final static public void yield() {
	}
	
	final static public void invoke() {
	}
	
	final static public void coInvoke(ForkJoinTask fjt) {
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return true iff the Task is finished:   */
	public abstract boolean isDone();

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** */
	public abstract void cancel();

	/** */
	public abstract void fork();

	/** */
	public abstract void start();

	/** */
	public abstract void join();

	/** */
	public abstract void reset();

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Runnable: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
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

