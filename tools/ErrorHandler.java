package tools;

import streamIO.exception.BaseException;

/**
 * Decorates a {@link CallAble} with generic Exception Handling and optional bounding Operations.
 *
 * <p>This Class demonstrates Error Handling by encapsulating a Function Call and...
 * <ol>
 * <li>bounding the Function by two Operation, one before and one after.
 * <li>catching Exceptions and allowing an Ignore, Retry or Abort
 * </ol>
 *
 * <p>It demonstrates how to expect and generically handle Exceptions.
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
 * <h2>Invariants</h2>
 *
 * <p>The Delegate is fixed at Construction and never replaced. The bounding Operations are
 * expected to be fail safe, since a Throwable from either of them escapes uncaught and
 * bypasses the Handling this Class exists to provide. As currently written the Class is
 * incomplete: see the marked Defects in {@link #call(Object)}.
 *
 * <h2>Collaborators</h2>
 *
 * <table>
 * <caption>Types this Class works with</caption>
 * <tr><th>Type</th><th>Relationship</th></tr>
 * <tr><td>{@link CallAble}</td>
 *     <td>Both the Interface implemented and the Type of the wrapped Delegate.</td></tr>
 * <tr><td>{@link IOError}</td>
 *     <td>Unchecked Wrapper this Class throws instead of the original checked Exception.</td></tr>
 * <tr><td>{@link streamIO.exception.BaseException}</td>
 *     <td>Common Base of the Exception actually rethrown, held while the Retry Loop runs.</td></tr>
 * <tr><td>{@link FilterCallTransAction}</td>
 *     <td>Reuses this Class's askAbort/askRetry/askIgnore Constants for the same Decision.</td></tr>
 * </table>
 *
 * Created on 7. Januar 2001, 18:37
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:47Z
 * digest: 3196360d8e83e0b43c3a8ec16164af7ead6da84ff7f2933038029c58199b3b5b
 * stale: false
 * tags: [code/error_handling, code/decorator, code/retry_logic]
 * concepts: [Error Handling]
 * facets: {layer: infrastructure, status: broken, complexity: medium}
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

	/**The wrapped Operation whose Exceptions this Class handles.   */
	protected CallAble Delegate;

	/**Optional Operation run before the Delegate, expected to be fail safe.   */
	// TODO: LOGIC: never assigned - no Constructor Parameter and no Setter reaches this
	// field, so it is always null and the documented "bounding by two Operations" feature
	// is unreachable for every Caller outside the package.
	protected CallAble BeforeOp;

	/**Optional Operation run after the Delegate, expected to be fail safe.   */
	protected CallAble AfterOp;

////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Wraps the given Operation so its Exceptions are handled by {@link #call(Object)}.
	 *
	 * @param Delegate the Operation to wrap; must not be null, since it is never checked
	 */
	public ErrorHandler (CallAble Delegate) { this.Delegate = Delegate; }

////////////////////////////////////////////////////////////////////////////
//  Methods, public ones, then private ones (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Runs the Delegate between the bounding Operations, handling any Exception it throws.
	 *
	 * <p>Any Method Call that takes an Object Argument, returns an Object
	 * and possibly throws any Exception ('Error's are not checked).
	 *
	 * <p>This is most generic, since any Number of Arguments and Return Values
	 * can be encapsulated into a single (Container) Argument
	 * and any type of Exception is derived from this Class.
	 *
	 * @param arg the Argument forwarded unchanged to the Delegate and both bounding Operations
	 * @return whatever the Delegate returned, or {@code null} when it threw and the Failure
	 *         was ignored
	 * @throws Throwable an {@link IOError} wrapping the Delegate's Exception when the
	 *         Decision is to abort, or anything a bounding Operation throws
	 */
	public Object call(Object arg) throws Throwable {
		// TODO: LOGIC: `ask` is initialised to askIgnore and never reassigned - the "ask the
		// User" Query the switch below is written for does not exist - so askAbort and the
		// default Branch are unreachable, the do/while never retries, and every Exception
		// from the Delegate is swallowed silently while call() returns null. That is the
		// opposite of this Class's stated Purpose.
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

