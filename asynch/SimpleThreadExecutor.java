package asynch;

/**
  * Title: SimpleThreadExecutor<p>
  * Description:
  * Purpose:
  * Asynchronously executes the run() Method of the given Runnable Parameter
  * by starting a new Thread.
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
public class SimpleThreadExecutor
extends AExecutor {

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IExecutor: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Asynchronously executes the run() Method of the Runnable Parameter
	 * by starting a new Thread.
	 * This is quite inefficient, especially for short Tasks.
	 * The Thread is not being reused or pooled.
	 */
	public void execute(Runnable r) { new Thread(r).start(); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + SimpleThreadExecutor.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

