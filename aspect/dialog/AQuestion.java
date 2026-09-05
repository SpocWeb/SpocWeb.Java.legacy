package aspect.dialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import streamIO.Log;
import aspect.AAspect;

/**
  * Title: AQuestion<p>
  * Description:
  * Abstract base class for a single Question in a console-driven Dialog:
  * an Aspect that holds the question text, can be asked standalone (print
  * the question, read an answer, store it via setVal()), and knows the
  * name of the next Question to proceed to.
  *
  * Design Decisions / Implementation Details:
  * Using the Question Name for the Result Variable Name is a Problem,
  * because the same Variable could be filled by several Questions
  * without having to branch into the same Process!
  *
  * Known SubClasses: <none>
  * StringQuestion
  * BoolQuestion
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	01-05-2003, 03:26 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:25:46Z
  * digest: bee3b646f5a943b57c89e893a280b5a99385381fbfe93e2e83781e8b641ade94
  * stale: false
  * tags: [code/dialog]
  * concepts: [Console Q&A Model]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public abstract class AQuestion
extends AAspect
//	implements  IQuestion
	{

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Name of this Question, used to retrieve the next Question by Name
	 * Similarly the name can be used for retrieving the Value from the Dialog!
	 */
//	protected String name;

	/** Text to display to the User */
	protected String question;

	/** Name of the next Question, possibly dependant on the Answer.
	 * If null, the Dialog ends... */
//	protected String next;

	/** Tip for the User */
//	protected String tip;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Text of this Question.
	  * @return the Text of this Question to display to the User */
	public String getQuestion() { return question; }

	/** Returns the name of the Question to proceed to next.
	  * @return the Name of the next Question to proceed to, which may depend on the Answer given, or null to end the Dialog */
	public abstract String getNext(); // { return next; }

	/** @return the Name of the next Question, possibly dependant on the Answer */
//	public abstract String getAnswer(); // { return answer; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor	 */
/*	protected AQuestion(String name_, String question_, String next_) {
		super(name_);
		this.question = question_;
		this.next = next_;
	}
*/
	/** Constructor	 */
	protected AQuestion(String name_, String question_) {
		super(name_);
		this.question = question_;
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** Performs asking the Question standalone
	 * @return the Name of the next Question to ask!
	 */
	public String ask(PrintStream out, InputStream in) throws IOException {
		out.print(getQuestion()); //the Question can be extended to translate Variables from Questions!
		setVal(Log.READ_STRING());
		return getNext(); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + AQuestion.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

