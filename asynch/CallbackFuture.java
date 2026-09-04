package asynch;

import graphs.IValueSetter;

/**
  * Title: CallbackFuture<p>
  * Description:
  * Purpose:
  * Takes a Callback as Parameter and returns itself on Completion to the Callback together with the stored Result
  * Purpose / Responsibilities of this Class
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
  * Created on	09-14-2002, 09:44 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class CallbackFuture
extends Future {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Object to write the Result to	 */
	protected IValueSetter valueSetter;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public CallbackFuture(IValueSetter valueSetter_) {
		this.valueSetter = valueSetter_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent Future: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IValueSetter: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method to execute by the Thread.
	  * Unfortunately only the Result is returned,
	  * but the Target has no Way to identify the Origin of this Call!
	  * @param sets Value of this Object */
	public void setVal(Object val) {
		super.setVal(val); //store the Result
		valueSetter.setVal(this); } //do the Callback

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + CallbackFuture.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

