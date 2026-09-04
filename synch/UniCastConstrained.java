package synch;

/**
  * Title: UniCastConstrained<p>
  * Description:
  * Purpose:
  * Extends UniCaster with Methods to enable validating new Values BEFORE publishing them.
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-02-2002, 08:19 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
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

	/** @return false if this Validator was not subscribed at all.	 */
	public boolean isValidator(IValidator arg) {
		if (validator instanceof MultiCaster) {
			return ((MultiValidator) validator).isValidator(arg); }
		return (validator == arg); }

	/** @return the Number of Validators of this Publisher	 */
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

