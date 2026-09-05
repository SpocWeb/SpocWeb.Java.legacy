package aspect.dialog;

import synch.InvalidException;

/**
  * Title: StringQuestion<p>
  * Description:
  * Implements the Model for a Text Question: a free-form String Answer with a fixed next-Question Name.
  *
  * Design Decisions / Implementation Details:
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	01-05-2003, 03:38 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:26:23Z
  * digest: 412d0ead6285240368a85982bcf5e776ddf0939ad70198a9c3e92af92cf23245
  * stale: false
  * tags: [code/dialog]
  * concepts: [Console Q&A Model]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public class StringQuestion
extends AQuestion {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	protected String answer; 

	/** Name of the next Question, possibly dependant on the Answer.
	 * If null, the Dialog ends... */
	String next;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor	 */
	public StringQuestion(String name, String question_) { super(name, question_); }

	/** Constructor	 */
	public StringQuestion(String name, String question_, String next_) { 
		super(name, question_);
		this.next = next_; }

	/** Constructor	 */
//	public StringQuestion(String name, String question_, String next_, String default_) { }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent AQuestion: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the given Answer boxed as an Object.
	 * @return the given Answer, typed as Object
	 * @see graphs.ICValue#getVal()
	 */
	public Object getVal() { return answer; }// throws InvalidException {	}

	/** Returns the given Answer.
	  * @return the given Answer as a String (its native Type for this Question) */
	public String getAnswer() { return answer; }// throws InvalidException {

	/**
	 * @see aspect.AAspect#setValue(Object)
	 */
	protected void setValue(Object val) throws InvalidException {
		this.answer = val.toString(); }

	/** sets and validates the given Answer in its original Type */
	public void setAnswer(String answer_) { // throws InvalidException {
		this.answer = answer_; }

	/** Returns the fixed name of the next Question.
	  * @return the fixed Name of the next Question (does not branch on the Answer, unlike BoolQuestion)
	  * @see aspect.dialog.AQuestion#getNext()	 */
	public String getNext() { return next; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent AQuestion: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + StringQuestion.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

