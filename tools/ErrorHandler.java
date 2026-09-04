package tools;

import streamIO.exception.BaseException;

/**
 * ErrorHandler.java
 * Description:
 *
 * This Class demonstrates Error Handling by encapsulating a Function Call and...
 * 1) bounding the Function by two Operation, one before and one after.
 * 2) catching Exceptions and allowing an Ignore, Retry or Abort
 *
 * It demonstrates how to expect and generically handle Exceptions.
 * RuntimeExceptions should be handled, just like Exceptions.
 * Errors should not be handled, they are designed
 * to indicate a severe (Virtual) Machine Problem.
 * Glossing over Errors like this makes the Application fail safe,
 * but it will not work properly and Errors are harder to sort out.
 * On the other hand this allows to easily wrap ANY Call
 * and to apply Error Handling on it.
 * 'Function' does not declare Exceptions, but can throw both
 * Errors and RuntimeExceptions, which will also be caught here!
 *
 * Created on 7. Januar 2001, 18:37
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2006-04-09T22:07:50Z
 * digest: 7af34e996ad6bb0bc270013229b5d1930feef37507dbd1b74c1986a48a9caf9d
 * stale: false
 * -->
 */
public class ErrorHandler
implements CallAble {

////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////

	/**Constant for the Return Value of the User Query   */
	final static public int askAbort = -1;

	/**Constant for the Return Value of the User Query   */
	final static public int askRetry = 1;

	/**Constant for the Return Value of the User Query   */
	final static public int askIgnore = 0;

////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////

	/**Local Reference to the Delegate for the call() Method   */
	protected CallAble Delegate;

	/**Local Reference to the Delegate for the call() Method   */
	protected CallAble BeforeOp;

	/**Local Reference to the Delegate for the call() Method   */
	protected CallAble AfterOp;

////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Initializing Constructor   */
	public ErrorHandler (CallAble Delegate) { this.Delegate = Delegate; }

////////////////////////////////////////////////////////////////////////////
//  Methods, public ones, then private ones (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Any Method Call that takes an Object Argument, returns an Object
	 * and possibly throws any Exception ('Error's are not checked).
	 *
	 * This is most generic, since any Number of Arguments and Return Values
	 * can be encapsulated into a single (Container) Argument
	 * and any type of Exception is derived from this Class.
	 * Even 'Operation's that return no Value are defined
	 * by this Method returning simply 'null'.
	 */
	public Object call(Object arg) throws Throwable {
		int ask = askIgnore;
		Object ret = null;
		BaseException x = null;
		if (null != BeforeOp) BeforeOp.call(arg); //should be guaranteed fail safe
		do
		try{ret = Delegate.call(arg);
		} catch (Exception e) {
			switch (ask) { //ask the User
				case askRetry : break; //see below
				case askIgnore: break;
				case askAbort : x = new IOError(e); break; //throw the same Exception, embellished by more Information
				default       : x = new IOError("Unexpected Return Value: ask = " + ask);
			}
		} finally { //return ret; //never return anything from here!?!
		}
		while (ask == askRetry);
		if (null != AfterOp) AfterOp.call (arg); //should be guaranteed fail safe
		if (null != x) throw x;
		return ret; }

}

