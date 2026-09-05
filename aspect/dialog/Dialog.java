/**
 * File  Name: Dialog.java
 * Created on: 05.01.2003
 */
package aspect.dialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import streamIO.Log;

/**
 * Title: Dialog<p>
 * Description:
 * A Dialog holds a Tree of Questions (possibly w. Diamonds!)
 * It allows to lookup the next Question to retrieve
 * and to retrieve the Value of a certain Question!
 *
 * Design Decisions / Implementation Details:
 * Don't inheriting from HashMap, but delegating to it!
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:26:09Z
 * digest: 9a77335c0c995e190e846a5fe75e41754f2a1bd357cf77e5d0ae648b75996b5e
 * stale: false
 * tags: [code/dialog, code/dialog_invocation]
 * concepts: [Dialog Tree Runner]
 * facets: {layer: domain, status: stable, complexity: medium}
 * -->
 */
public class Dialog {

static final String[][] DIALOG = {
{"IS", "s", "Describe briefly initial situation.", "GPF"},
{"GPF", "s", "What your system should do, which function it carries?", "3"},
{"3", "b", "Is it possible to split the function - [GPF] into two subfunctions?", "P1", "6"},
{"P1", "s", "First subfunction of the function [GPF]?", "P2"},
{"P2", "s", "Second subfunction of the function [GPF]?", "GPF"},
{"6", "b", "Further on, we shall solve a problem how to embody function [GPF]. Ok?", "OE", "IS"},
{"OE", "s", "Which negative, undesirable effect arises in your system?", "VOE"},
{"VOE", "s", "Which element of the system causes the undesirable effect [OE].", "9"},
{"9", "b", "Are you quite sure you really need this element [VOE] in the >system and it cannot be removed together with negative effect,>and lay its function on 'other elements' of the system or 'external environment'? >So, is it possible to remove an element [VOE] from the system?", "11", "12"},
{"11", "b", "It means, that [VOE] should be simply removed >and the system should be reconstructed properly. >To formulate the subtasks risen by the reorganization of the system>use the 'refiner' again. Ok?", "IS", "end"},
{"12", "b", "Does it create any positive effect?", "PE", "28"},
{"PE", "s", "Which useful action element [VOE] carries in your system?", "14"},
{"14", "b", "Special terms cause psychological inertia.>Further on, in order to avoid the term 'element of system [VOE]',>we are going to call it 'something for creation of a positive effect [PE]'. >Now this 'something' have positive effect [PE] for performance of function [GPF] >but simultaneously appears negative effect [OE]. >Thus we have: positive effect and associated with it negative effect. Ok?", "PVOE", "29"},
{"PVOE", "s", "Specified problem: Now we are going to find out>how to remove negative effect [OE] preserving positive effect [PE].>We suppose to vary some of the parameters of element [VOE] >in order to weaken its negative effect. Type this parameter.", "NOOE"},
{"NOOE", "s", "What do we need to weaken the negative effect (decrease or to increase [PVOE])?", "NUOE"},
{"NUOE", "s", "What do we need to enforce the negative effect (decrease or to increase [PVOE])?", "21"},
{"21", "b", "Technical contradiction 1: >If [NOOE] parameter [PVOE] of element [VOE] the negative effect [OE] will increase,>but thus positive effect [PE] will decrease. Ok?", "22", "end", ""},
{"22", "b", "Technical contradiction 2: >If on the contrary [NUOE] parameter [PVOE] >of element [VOE] the positive effect [PE] will begin to increase,>but negative effect [OE] will also increase. Ok?", "23", "new_branch"},
{"23", "b", "As we try>a)to reduce the negative effect, the positive effect will also decrease, and >b)on the contrary, increasing the positive effect we will raise the negative.>Now we need to sharpen (emphasize) the problem to avoid obviously weak solutions.>Has [NOOE] meaning 'to increase' for weakening of the positive effect>(because [OE] and [PE] have reverse dependence on [PVOE])?", "PPE", "PPE"},
{"PPE", "s", "[NOOE] has meaning - 'to increase'. >Let us assume that the negative effect does not appear.>Which maximum positive effect you want to have?", "PZP"},
{"PZP", "s", "What qualitative value of parameter [PVOE] of element [VOE]>(in relation to other elements of system or external environment) >needed to achieve maximum positive effect [PPE].", "POE"},
{"POE", "s", "How would transpire negative effect in that case.>What would be maximum negative effect>for [PZP] value of parameter?", "31"},
{"31", "b", "The strengthened conflict: >We can get maximum positive effect [PPE] >if the value of parameter [PVOE] 'something for creation of a positive effect' >[PE] so that [PZP], but then appears limiting negative >(undesirable) effect [POE]. Ok?", "32", "new_branch"},
{"32", "b", "Further on, we shall solve a problem: >How to find such X-element (X-change) that :>a) remove limiting negative effect [POE] while preserving maximum positive effect [PPE] and>b) when the value of parameter of 'something' is [PZP].>Finally we have got the rather sharp formulation of the problem >instead of blurred initial situation. We hope it can>help you to solve the problem and make more clear how to >use other TRIZ tools (for example Table of Contradictions).>Good luck!", "end", "end"},
{"28", "b", "It seems that we are dealing with natural phenomenon >and we need 'to bypass' some law of nature. >It is necessary to rewrite the formulation of the problem. >We need to liquidate negative consequences of the 'low of nature' in condition of its action.>The best way is to turn harm into benefit. >Here you have much to think about, after that you can>return to the beginning of the 'problem sharpener'.>Will you go to the beginning?", "IS", "end"},
{"29", "b", "The problem is solved!>Now it is necessary to reveal subtasks.>No contradiction means - no problem or the formulation of the problem too general, >therefore the solution also too general . It is necessary >to understand why the solution is useless and formulate more specific problem. Ok?", "IS", "end"},
{"LPE", "s", "Now [NOOE] has meaning 'to reduce'. >So while we are increasing the negative effect positive effect increasing also.>Let us imagine that there are no limitation in the relation. >How you want to see maximum (extreme) [PE] positive effect ? >In other words, how you want to see limiting, desirable positive effect?", "34"},
{"34", "b", "We have find out before>that negative effect [OE] reduces with reduction of parameter value [PVOE]>( the parameter of 'something for creation of a positive effect [PE]'). >Let us assume that the value of the parameter equals to zero >or to value of appropriate parameter of 'external environment'. >What would happened with negative effect? Will it disappear?", "35", "new_branch"},
{"35", "b", "The strengthened conflict:>We can considerably reduce or liquidate a negative effect [OE]>if parameter [PVOE] of 'something for creation of a positive effect [PE]'>equals to zero or to value of analogous parameter of 'external environment'.>But in that case positive effect [POE] will significantly decrease>or even disappear. Ok?", "36", "new_branch"},
{"36", "b", "Further on, we shall solve a problem: >How to find such X-element (X-change) that :>a) remove negative effect [OE] while preserving maximum positive effect [PPE] and >b) when the value of parameter of 'something' equals to zero >or to value of analogous parameter of 'external environment'.>Finally we have got the rather sharp formulation of the problem>instead of blurred initial situation.>We hope it can help you to solve the problem and make more >clear how to use other TRIZ tools (for example Table of Contradictions).>Good luck!", "end", "end"},
{"end", "b", "Now you come to final step, >it means that you either solve the problem>or have difficulties with its formulation.>Authors hope that you found the 'problem sharpener' helpful >and appreciate your comments and suggestions.>Back to beginning?", "IS", "IS"},
{"new_branch", "b", "You've come to step that under developing now. >Authors would appreciate your comments or suggestions.>Back to the beginning?", "IS", "IS"},
};

////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////

	/** Holds the Mapping of Names to Questions */
	protected HashMap questions = new HashMap();

	/** Question to start the Dialog with */
	protected String start;

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Constructor for Dialog. Defaults the Start Question to the first Question added.	 */
	public Dialog() { }

	/** Constructor for Dialog.	 */
	public Dialog(String start_) {
		start = start_;
	}

	/** Character marking the start of a variable reference to substitute inside a Question's text (see REPLACE_BY_MAP) */
	public char startChar = '[';

	/** Character marking the end of a variable reference to substitute inside a Question's text (see REPLACE_BY_MAP) */
	public char stopChar = ']';

////////////////////////////////////////////////////////////////////////////
/// #region : Methods, public ones, then private ones (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Looks up a previously registered Question by Name.
	  * @return the Question registered under the given Name, or null if none was added */
	public AQuestion getQuestion(String name) {
		return (AQuestion) questions.get(name); }

	/** Registers the given Question under its own Name.
	  * @return this Dialog to allow for concatenating adds! */
	public Dialog addQuestion(AQuestion question) {
		questions.put(question.getName(), question);
		return this; }

	/** replaces Strings enclosed in Characters by the Mapping */
	final static public StringBuffer REPLACE_BY_MAP(String strQuestion, Map map, char startChar, char stopChar) {
		StringBuffer buffer = new StringBuffer(strQuestion);
		for(int startPos = strQuestion.length(), pos;
			0 <=(pos = strQuestion.lastIndexOf( stopChar, startPos));) { //searching for all possible Variables...
			startPos = strQuestion.lastIndexOf(startChar, pos);
			if (startPos < 0) {
				break; }
			String str = strQuestion.substring(startPos+1, pos); //use a generic Reflection Function here!
			buffer.replace(startPos, pos+1, ((AQuestion) map.get(str)).getVal().toString());
		} //or searching for specific Variables...
		return buffer;
	}

	/** Performs asking the Question standalone by replacing the Variables in the Question by the Results of the Question!
	 * @return the Name of the next Question to ask!
	 */
	public String ask(PrintStream out, InputStream in, AQuestion question) throws IOException {
		out.print(REPLACE_BY_MAP(question.getQuestion(), questions, startChar, stopChar));
		question.setVal(Log.READ_STRING());
		return question.getNext(); }

	/** runs through the whole Dialog */
	public void run(PrintStream out, InputStream in) throws IOException {
		for(String str = start; start != null;) {
			str = ask(out, in, (AQuestion) questions.get(str));
		}
	}

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) throws java.io.IOException {
	System.out.println("Testing " + Dialog.class.getName());
	Dialog dialog = new Dialog(DIALOG[0][0]);
	for (int i = DIALOG.length; --i >= 0;) {
		String[] curr = DIALOG[i];
		AQuestion q;
		if ("b".equals(curr[1])) {
			q = new BoolQuestion(curr[0], curr[2], curr[3], curr[4]);
		} else {
			q = new StringQuestion(curr[0], curr[2], curr[3]);
		}
		dialog.addQuestion(q);
	}
	dialog.run(System.out, System.in);
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	testIt(args); }

}
