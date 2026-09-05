package aspect.dialog;

import synch.InvalidException;

/**
  * Title: BoolQuestion<p>
  * Description:
  * A Question that can only be answered Yes or No, branching to a
  * different next-Question name (nextOnTrue/nextOnFalse) depending on the
  * Answer given.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	01-05-2003, 03:54 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:26:00Z
  * digest: fd3a4be4bf2434d3e4250a1f784a2453e5efdc9d6b40278769dbf8bda4b7953d
  * stale: false
  * tags: [code/dialog]
  * concepts: [Console Q&A Model]
  * facets: {layer: domain, status: broken, complexity: low}
  * -->
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

	/** Sets the given boolean Answer directly (bypassing text parsing). */
	public void setAnswer(boolean value) {
		this.answer = value; }

	/** Returns the Answer given.
	  * @return the Answer given, typed as boolean */
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
	 * Returns the Answer boxed as an Object.
	 * @return the Answer, boxed as a Boolean
	 * @see graphs.ICValue#getVal()
	 */
	public Object getVal() { return new Boolean(answer); }

	/**
	 * Parses the first character of the given Value's String representation: 'Y'/'y'/'J'/'j' (also
	 * accepting the German "Ja") set the Answer true, anything else sets it false.
	 * @see aspect.AAspect#setValue(Object)
	 */
	protected void setValue(Object val) throws InvalidException {
		String str = val.toString().trim();
		boolean value = false; //= Boolean.valueOf(str).booleanValue();
		// TODO: LOGIC: str.charAt(0) throws StringIndexOutOfBoundsException when the trimmed input is
		// empty (e.g. the user just presses Enter at the console prompt in AQuestion.ask()/Dialog.ask()),
		// and val.toString() above throws NullPointerException if val is null. Neither is guarded.
		switch(str.charAt(0)) {
			case 'Y':
			case 'y':
			case 'J':
			case 'j': value = true;
		} //switch()
		setAnswer(value); }

	/** Returns the name of the next Question, selected by the Answer given.
	  * @return the name of the next Question: nextOnTrue if answered Yes, nextOnFalse if answered No */
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

