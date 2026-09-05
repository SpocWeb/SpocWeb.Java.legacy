package synch; 

import java.util.ArrayList;

import streamIO.IIStreamOut;
import tools.WorkerThread;

/**
  * Title: MultiValidator<p>
  * Description:
  * Purpose:
  * Implements the Interface for a Validator,
  * but forwards Validation to multiple Validators.
  * Thus implements the Composite Pattern.
  *
  * Implements Interface IValidator because it is a composite IValidator
  * Uses Class ArrayList for storing the Subscribers type safe.
  *
  * Known SubClasses:
  *
  * Known Uses:
  * @see MultiCaster that implements the analogous Publisher Interface
  *
  * related Classes: 
  * @see synch.ValidationRuleList which maintains a List of IValidationRule Objects.
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-02-2002, 06:17 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:32Z
  * digest: f55d467177c579d9ef2f0d57975c2807140f727dfbd798b8cace41ea5aab9230
  * stale: false
  * tags: [code/validation, code/validation_rule]
  * concepts: [Multi-Validator Chain]
  * facets: {layer: domain, status: legacy, complexity: medium}
  * -->
  */
public class MultiValidator
implements IValidator, IConstrained {

	/** Class to handle Exception during Notification. */
	public IIStreamOut RuntimeExceptionHandler; //Handler Interface is unnecessary!

	/**Vector to hold all Validators. 	 */
	protected ArrayList Validators = new ArrayList();

	//////////////////////////////
	//	interface Subscriber	//
	//////////////////////////////

	/**Callback used to update all Validators
	 * The Return Value is a Boolean to stop Notification, if true	 */
	public void validate(Object Source, Object Value, Object oldVal)
		throws InvalidException {
		notifyValidators(Source, Value, oldVal); }

	//////////////////////////////
	//	interface Publisher		//
	//////////////////////////////

	/**Adds a Subscriber to this Publisher.
	 * For UniCaster there can be only one, so an Exception is thrown.
	 * Difficult Decision to use an Exception or a boolean Return Value.
	 * For Consistency I use an Exception	 */
	public void addValidator(IValidator arg) throws TooManySubscribersException {
		Validators.add(arg);
	}

	/**Removes the Subscriber from this Publisher
	 * Returns false if this Subscriber was not subscribed at all.	 */
	public IValidator removeValidator(IValidator arg) {
		if (Validators.remove(arg)) {
			return arg; }
		return null; }

	/**Returns false if this Subscriber was not subscribed at all.	 */
	public boolean isValidator(IValidator arg) {
		return  Validators.contains(arg); }
//		return (Validators.indexOf (arg) >= 0); }

	/**Returns the Number of Validators of this Publiser	 */
	public int countValidators() {
		return Validators.size(); }

	/**Notifies the Validators of thie Value	 */
	public void notifyValidators(Object Value)
		throws InvalidException {
		notifyValidators(this, Value, null); }

	/**Notifies the Subscribers of this Value	 */
	protected void notifyValidators(Object Value, Object oldVal)
		throws InvalidException {
		notifyValidators(this, Value, oldVal); }

	/** Timeout of this MultiCaster
	  * Values <= 0 result in a synchronous Call (an infinite Timeout)
	  * positive Values result in an according Timeout in MilliSeconds.
	  */
	public int Timeout = 0;

	/** Notifies the Validators of this Value
	  * This Routine is safe from Exceptions, also RuntimeExceptions,
	  * but not from Errors!
	  * The only Vulnerability is a non returning Validator Routine!
	  */
	protected void notifyValidators(Object Source, Object Value, Object oldVal)
		throws InvalidException {
		if (Timeout <= 0) { //wait infinitely / work synchronously!
			safeNotifyValidators(Source, Value, oldVal);
		} else {
			notifyValidatorsInThread(Source, Value, oldVal); 
		}
	}
	
	/** a separate Thread is started to notify the Elements
	 * This Thread watches the new Thread and catches Timeouts!
	 */
	protected void notifyValidatorsInThread(Object Source, Object Value, Object oldVal) 
		throws InvalidException {
		Object[] params = {Source, Value, oldVal, null}; //local new Object, handed over to the Worker Thread!
		WorkerThread t = new WorkerThread (params) {
			public void run() {
				try {
					safeNotifyValidators(Params[0], Params[1], Params[2]);
				} catch (InvalidException x) {
					Params[3] = x; //Store the Exception as Return Value, because it does not propagate!
				}
			}
		};
		try { t.startWithTimeOut(Timeout); 
		} catch (InterruptedException x) { //except if you use notify()
			if (RuntimeExceptionHandler != null) {
				RuntimeExceptionHandler.addItem(x); }
		} //if this Thread is interrupted, also the Worker Thread should stop!
		if (params[3] != null) { //rethrow the Exception caught on calling the Validator
			throw (InvalidException) params[3]; }
	}

	/** Notifies the Validators of this Value
	  * This Routine is safe from Exceptions, also RuntimeExceptions,
	  * but not from Errors!
	  * The only Vulnerability is a non returning Validator Routine!
	  */
	protected void safeNotifyValidators(Object Source, Object Value, Object oldVal)
		throws InvalidException {
//		Iterator enm = Validators.iterator();
//		while (enm.hasNext()) { //propagate the Return Code.
		for (int i = Validators.size(); --i >= 0;) {
			try{ //handling the Exceptions could also be done by a Filter!
				((IValidator) Validators.get(i)).validate(Source, Value, oldVal); 
//				((IValidator) enm.next()      ).validate(Source, Value, oldVal);
			} catch(RuntimeException x) { //only RuntimeExceptions can be thrown through the update Interface!
				if (RuntimeExceptionHandler != null) {
					RuntimeExceptionHandler.addItem(x); } //handle(x); }
			}
		}
	}

}
