package synch;

/**
  * Title: UniCastConstrained<p>
  * Description:
  * Extends {@link UniCaster} with a single {@link IValidator} slot, transparently
  * upgraded to a {@link MultiValidator} on a second registration, so new Values can
  * be validated (and vetoed via {@link InvalidException}) before they are published.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-02-2002, 08:19 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:44:01Z
  * digest: a6112ae44596bcd812643732ccfaca7ad26ce1cf00b589cc01a37d0296df8b68
  * stale: false
  * tags: [code/observer_pattern, code/validation]
  * concepts: [Constrained Publisher]
  * facets: {layer: domain, status: broken, complexity: medium}
  * -->
  */
public class UniCastConstrained
extends UniCaster
implements IConstrained {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the single Validator, automatically upgraded to a MultiValidator */
	protected IValidator validator;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Constrained: Implementation
////////////////////////////////////////////////////////////////////////////////

	/**Adds a Subscriber to this Publisher.
	 * For UniCaster there can be only one, so an Exception is thrown.
	 * Difficult Decision to use an Exception or a boolean Return Value.
	 * For Consistency I use an Exception
	 * This Class transparently creates a MultiCaster if necessary.	 */
	public void addValidator(IValidator arg)
		throws TooManySubscribersException	{
		// TODO: LOGIC: this checks 'instanceof MultiCaster', but 'validator' is only ever
		// null, a plain IValidator, or a MultiValidator -- never a MultiCaster (a distinct
		// class implementing ISubscriber/IPublisher, not IValidator). So this branch is
		// dead code and the "already composite" fast path never runs.
		// Worse, the else branch below only re-wraps the *existing* validator into a new
		// MultiValidator and never adds 'arg' to it, so from the second call onward every
		// newly added Validator is silently dropped -- only the very first one registered
		// ever actually gets consulted. Compare the correct pattern in
		// UniCaster.ADD_SUBSCRIBER(), which calls ret.addSubscriber(arg) after wrapping.
		if  (validator instanceof MultiCaster) {
			((MultiValidator) validator).addValidator(arg); return; }
		if  (validator == null) { //throw new TooManySubscribersException();
			 validator =  arg;
		} else {
			 MultiValidator tmp = new MultiValidator();
			 tmp.addValidator(validator);
			 validator = tmp; }
	}

	/**Removes the Validator from this Publisher
	 * @return false if this Validator was not subscribed at all.	 */
	public IValidator removeValidator(IValidator arg)	{
		if (validator instanceof MultiCaster) { //never remove the Multicaster!
			return ((MultiValidator) validator).removeValidator(arg); }
		if (validator == arg) {
			validator =  null;
			return arg; }
		return null; }

	/** Checks whether the given Validator is currently registered, delegating to the
	  * MultiValidator when this instance has been upgraded to hold more than one.
	  * @return true if the given Validator is currently registered.	 */
	public boolean isValidator(IValidator arg) {
		if (validator instanceof MultiCaster) {
			return ((MultiValidator) validator).isValidator(arg); }
		return (validator == arg); }

	/** Reports how many Validators are currently registered, delegating to the
	  * MultiValidator when this instance has been upgraded to hold more than one.
	  * @return the current Number of registered Validators	 */
	public int countValidators() {
		if (validator instanceof MultiCaster) {
			return ((MultiValidator) validator).countValidators(); }
		return (validator == null) ? 0 : 1; }

	/** Notifies the Validators of thie Value and returns their Answer
	  * as an Exception, because that cannot be ignored!	 */
	protected void notifyValidators(Object Value, Object oldVal)
		throws InvalidException {
		validator.validate(this, Value, oldVal); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + UniCastConstrained.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

