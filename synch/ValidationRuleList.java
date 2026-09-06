package synch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import streamIO.IIStreamOut;
import tools.WorkerThread;

/**
  * Title: ValidationRuleList<p>
  * Description:
  * Purpose:
  * Composite Container Pattern for a List of IValidationRule Objects.
  * Recursively calls all IValidationRule Methods of all contained Objects.
  *
  * Design Decisions / Implementation Details:
  * Could also use an Array directly,
  * thus reducing Casting and Call Overhead to ArrayList.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * related Classes: 
  * @see synch.MultiValidator which maintains a List of IValidator Objects 
  * and shows how to handle Operation in a separate Thread and by catching all Exceptions! 
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	11-27-2002, 11:50 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:44:11Z
  * digest: 28aa68ace32a2808169134b57d4eb62d571706190454b6874547d82ff3493aa9
  * stale: false
  * tags: [code/validation_rule]
  * concepts: [Validation Rule Chain]
  * facets: {layer: domain, status: broken, complexity: medium}
  * -->
  */
public class ValidationRuleList
implements IValidationRule {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Class to handle Exception during Notification. */
	public IIStreamOut RuntimeExceptionHandler; //Handler Interface is unnecessary!

	/** Timeout of this MultiCaster
	  * Values <= 0 result in a synchronous Call (an infinite Timeout)
	  * positive Values result in an according Timeout in MilliSeconds.
	  */
	public int Timeout = 0;

	/** List of Rules to check:	 */
	protected ArrayList list = new ArrayList();

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** @return the given Rule of the Rule Set:   */
	IValidationRule getRule(int i) { return (IValidationRule) list.get(i); }

	/** sets the given Rule in the Rule Set: 	 */
	void setRule(int i, IValidationRule rule) { list.set(i, rule); }

	/** adds the given Rule to the Rule Set: 	 */
	void addRule(IValidationRule rule) { list.add(rule); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public ValidationRuleList() { }

	/** Constructor, loading all ValidationRules from a ResultSet streamIO */
	public ValidationRuleList(ResultSet rs, int classCol, int methodCol, int paramCol)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException {
		while(rs.next()) {
			list.add(new ValidationRule(rs, classCol, methodCol, paramCol));	}
	}

	/** Constructor, loading all PathValidationRules from a ResultSet streamIO */
	public ValidationRuleList(ResultSet rs, int classCol, int methodCol, int paramCol, int pathStartCol)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException {
		while(rs.next()) {
			list.add(new PathValidationRule(rs, classCol, methodCol, paramCol, pathStartCol));	}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IValidationRule: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Validates the given Object */
	public void validate(Object arg) throws InvalidException {
		if (Timeout <= 0) { //wait infinitely / work synchronously!
			safeValidate(arg);
		} else {
			validateInThread(arg); 
		}
	}

	/** Validates the given Object and catches all RuntimeExceptions! */
	public void safeValidate(Object arg) throws InvalidException {
		for (int i = list.size(); --i >= 0;) {
			try{ //handling the Exceptions could also be done by a Filter!
				((IValidationRule) list.get(i)).validate(arg); 
			} catch(RuntimeException x) { //only RuntimeExceptions can be thrown through the update Interface!
				if (RuntimeExceptionHandler != null) {
					RuntimeExceptionHandler.addItem(x); } //handle(x); }
			}
		}
	}

	/** a separate Thread is started to notify the Elements
	 * This Thread watches the new Thread and catches Timeouts!
	 */
	protected void validateInThread(Object Value)
		throws InvalidException {
		Object[] params = {Value, null}; //local new Object, handed over to the Worker Thread!
		WorkerThread t = new WorkerThread (params) {
			public void run() {
				try {
					safeValidate(Params[0]);
				} catch (InvalidException x) {
					Params[1] = x; //Store the Exception as Return Value, because it does not propagate!
				}
			}
		};
		try { t.startWithTimeOut(Timeout);
		} catch (InterruptedException x) { //except if you use notify()
			if (RuntimeExceptionHandler != null) {
				RuntimeExceptionHandler.addItem(x); }
		} //if this Thread is interrupted, also the Worker Thread should stop!
		if (params[1] != null) { //rethrow the Exception caught on calling the Validator
			throw (InvalidException) params[1]; }
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ValidationRuleList.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

