package aspect.dialog;

import synch.InvalidException;

/**
  * Title: BoolQuestion<p>
  * Description:
  * Purpose:
  * stores a boolean Question that can only be answered by Yes or No
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
  * Created on	01-05-2003, 03:54 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class BoolQuestion
extends AQuestion {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Value of the actual Answer given */
	protected boolean answer;

	/** Path to the next Question if answered 'Yes' */
	protected String nextOnTrue;

	/** Path to the next Question if answered 'No' */
	protected String nextOnFalse;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	public void setAnswer(boolean value) {
		this.answer = value; }

	public boolean getBoolAnswer() { return answer; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor	 */
	public BoolQuestion(String name, String question_) { super(name, question_); }

	/** Constructor, defaults the 'No' Path to 'null' resulting in the End of the Questionnaire	 */
	public BoolQuestion(String name, String question_, String nextOnTrue_) {
		super(name, question_);
		this.nextOnTrue = nextOnTrue_;
	}

	/** Constructor	 */
	public BoolQuestion(String name, String question_, String nextOnTrue_, String nextOnFalse_) {
		super(name, question_);
		this.nextOnTrue  = nextOnTrue_ ;
		this.nextOnFalse = nextOnFalse_;
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent AQuestion: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

	/**
	 * @see graphs.ICValue#getVal()
	 */
	public Object getVal() { return new Boolean(answer); }

	/**
	 * @see aspect.AAspect#setValue(Object)
	 */
	protected void setValue(Object val) throws InvalidException {
		String str = val.toString().trim();
		boolean value = false; //= Boolean.valueOf(str).booleanValue();
		switch(str.charAt(0)) {
			case 'Y':
			case 'y':
			case 'J':
			case 'j': value = true;
		} //switch()
		setAnswer(value); }

	/** @return the Name of the next Question, possibly dependant on the Answer */
	public String getNext() {
		return answer ? nextOnTrue : nextOnFalse; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + BoolQuestion.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

