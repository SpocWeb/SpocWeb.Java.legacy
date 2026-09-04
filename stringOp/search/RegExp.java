package stringOp.search;

import java.util.BitSet;

import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.IStreamIn_Int;
import streamIO.integer.pipe.PipeByte;
import streamIO.object.StringStreamIn;
import stringOp.parser.Scanner;
import tester.ITester;
import tester.TesterContains;
import tester.algebra.TesterNOT;
import function.byref.ByRefInt;

/**
 * Class that parameterizes an Automaton, 
 * so it can search for Patterns described by regular Expressions in a String. 
 * 
 * The Automaton accesses the List only sequentially, 
 * so it is apted for Streams and IStreamIns.
 * The Tests are performed in parallel on the incoming Characters (or Objects) 
 * so no BackTracking or reSett()ing of the Stream is necessary. 
 * The Negation of Expressions has been left out, because it would require resetting. 
 * The Test can find the first or all Matches of a Pattern 
 * in the incoming Stream. 
 * Instead of testing Characters, any Object Type could be Tested
 * when the appropriate ITester Objects are used. 
 * 
 * The Automaton can be modeled as a Network of primitive Automatons. 
 * 
 * The Grammer is defined by the following Productions: 
 * Concatenation:	AB is the concatenation of A and B
 * Or:				A|B	means that either A or B can follow 
 * Closure:			B* means that B is repeated 0..n times.
 * Brackets:		Expressions in Brackets are treated as Atoms
 * Creates a non-deterministic (!) Automaton (with next1 and next2 as Alternatives). 
 * The Automaton is parameterized by the Parser.
 * It is realized using a DeQueue where the Alternatives for the currently 
 * tested Character are added at the Head, so they are tested next, 
 * before moving to the next Character,
 * and the Alternatives for the next Character are added at the Tail
 * (after a Scan Mark to indicate the Necessity of a Move to the next Character). 
 * The Number of Elements on the DeQueue 
 * corresponds to the Number of possible Matches, 
 * i.e. the Number of Not-End-States currently inspected. 
 * 
 * see Sedgewick "Algorithms" Chapters 20 and 21
 */
public class RegExp {
	
	/** Indicator for Zero States combining Mini-Automaton	 */
	final static public ITester EOF = null; 
	
	/** Indicator for Asterisk States representing any Character	 */
	final static public char ASTERISK = Character.MAX_VALUE; 
	
	/** Logger for this Class 	 */
	private static final Log L = new Log(RegExp.class); 
	
	////////////////////////////////////////////////////////////////////////////
	
	/** current State of the Automaton, used on Parsing the regular Expression, 
	 * as well as during the actual Search.
	 * 0 is an End State where the Automaton hallts.  */
	int state;
	
	/** List of the first Alternative for the next State for a Match of the current Character with ch[State]. */
	protected int[] next1;
	
	/** List of the second Alternative for the next State for a Match of the current Character with ch[State]. */
	protected int[] next2;
	
	/** List of the States already tested to avoid infinite Loops! */
	protected final BitSet tested;
	
	/**
	 * Match Operator for the current State of the Automaton. 
	 * A Null indicates just an Addition of Alternatives for the current State, 
	 * a real Test leads to a State for the next Character.
	 */
	protected ITester[] tester;
	
	/** Marker, separates the Test States for the current Position from those for the next Character */
	static final int scan = -1;
	
	/**
	 * DeQueue with the States for the current Character and those for the next Character, separated by the Mark Scan = -1.
	 * A DeQueue allows to first work out all Alternatives for the current Character, 
	 * adding them at the Head of the DeQueue while at the same time 
	 * and to collect Alternatives for the next Character 
	 * at the Tail of the DeQueue.
	 * The maximum Size of the Queue is |Pattern|*|String| 
	 * but only if the Queue is checked that new States are not already in it.
	 */
	protected PipeByte DQ;
	
	/** Initializing Constructor, Parses the Pattern and parameterizes the Automaton. 
	 * 
	 * @param _pattern
	 */
	public RegExp(final String _pattern) { this(_pattern, 20); 
	}

	/** Initializing Constructor, Parses the Pattern and parameterizes the Automaton. 
	 * 
	 * @param _pattern the Pattern to search for
	 * @param _initLength the initial Length of the DeQueue used to track all possible Matches. 
	 */
	public RegExp(final String _pattern, final int _initLength) { //
		this(new StringStreamIn(_pattern), _initLength); 
	}
	
	/** Initializing Constructor, Parses the Pattern and parameterizes the Automaton. 
	 * 
	 * @param _pattern the Pattern to search for
	 * @param _initLength the initial Length of the DeQueue used to track all possible Matches. 
	 */
	public RegExp(final IStreamIn_Int stream, final int _initLength) { //String List) {
		final int M = (int) stream.availAble(); 
		DQ = new PipeByte(false, _initLength); //works like a Queue
		final int MM = M + M;
		next1 = new int [MM];
		next2 = new int [MM];
		tester = new ITester[MM];
		tested= new BitSet(MM);
		//State 0 is only the Start and End State
		state = 0; //j = 0; //Parse the Regular Expression...
		addState(state++, EOF, Expression(stream), 0); //...as preparation for Searching
		addState(state++, EOF, 0, 0); //Add the final End State. 
	} //List is only needed for Sizing the Queue.
	
	/**
	 * Helper Routine setting the Alternatives next1 and next2 as well as the Indicator char whether you move to a new Character
	 * for the given State.
	 */
	protected int addTesterState(final String str, final boolean not) {
		final int ret = state++; 
		ITester tester = new TesterContains(str);
		if (not)
			tester = new TesterNOT(tester); 
		addState(state-1, tester, state, state);
		return ret; 
	} //
	
	/**
	 * Helper Routine setting the Alternatives next1 and next2 as well as the Indicator char whether you move to a new Character
	 * for the given State.
	 */
	protected void addState(final int _state, final ITester _str, final int _next1, final int _next2) {
		tester[_state] = _str;
		next1[_state] = _next1;
		next2[_state] = _next2;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Parsing the regular Expression
	///////////////////////////////////////////////////////////////////////////
	
	/** Parses an Expression E: 
	 * E -> T | T | E 
	 * The Alternative is paramteterized in the Automaton 
	 * by adding two possible Outcomes 
	 */
	public int Expression(final IStreamIn_Int stream) {
		final int t1;
		int expression = t1 = Term(stream); //E -> T only
		if (stream.availAble() > 0)
			if (stream.nextInt() == '|') { //E -> T|E
				expression = ++state; ++state;
				addState(expression  , EOF, Expression(stream), t1   ); 
				addState(expression-1, EOF, state             , state); 
			} else 
				stream.pushBack(); 
		return expression; }
	
	/** Parses a Term T: T -> F | FT  */
	public int Term(final IStreamIn_Int stream) {
		final int term = Factor(stream); //T -> F
		if (stream.availAble() > 0) { //T -> FT, recursively called
			final int chr = stream.nextInt(); stream.pushBack(); //this is a peek
			if ((chr == '(') || Scanner.IS_LETTER(chr)) 
				Term(stream); //This is the Recursion, ignore the Result...
		}
		return term; 
	}
	
	/** Parses a Factor F: F -> (E) | V | ^V | [abcd] | [^abcd] */
	public int Factor(final IStreamIn_Int stream) {
		final int t1 = state;
		final int t2; 
		final int chr = stream.nextInt();
		if (chr == '\\') { //
			final char[] strArr = {(char)  stream.nextInt()}; 
			t2 = addTesterState(new String(strArr), false); 
		} else if (chr == '(') { //F -> (E)
			t2 = Expression(stream); 
			if (stream.nextInt() != ')') 
				throw new RuntimeException("No closing Bracket at "+stream.getPosition()); 
		} else if (chr == '[') { //F -> [string] is equivalent to (s|t|r|i|n|g)
			final StringBuffer strBuf = new StringBuffer();
			int chr3, chr2 = stream.nextInt(); 
			chr3 =(chr2 != '^') 
				 ? chr2 
				 : stream.nextInt();
			while (chr3 != ']'){ 
				strBuf.append((char) chr3); chr3 = stream.nextInt(); } 
			final String str = strBuf.toString(); 
			t2 = addTesterState(new String(str), chr2 == '^'); 
		} else if (chr == '^') { //
			final char[] strArr = {(char)  stream.nextInt()}; 
			t2 = addTesterState(new String(strArr), true); 
		} else { //if (Scanner.isLetter(chr)) { //F -> V
			final char[] strArr = {(char)  chr}; 
			t2 = addTesterState(new String(strArr), false); 
		} //else
		//	throw new AbstractMethodError("No allowed Character encountered: '"+chr+"'");
		
		if (stream.nextInt() != '*') {
			stream.pushBack(); 
			return t2; }
		//F -> F* create the Closure (* Operation, as many Repetitions as you like...)
		addState(state, EOF, state + 1, t2);
		final int factor = state; next1[t1 - 1] = state;
		++state;
		return factor; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Matching
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the last Character of the Regular Expression found in string, 
	 * when starting the search at Character pos.
	 * When there is no Match, pos-1 is returned.
	 *  
	 * Pattern matching is done with a parameterized non-deterministic Automaton.
	 * The Capacity of the DeQueue used is at most 2^N (Pattern (A*A)* in List A*),
	 * if States are not re-used, else O(M*N^2) are sufficient. 
	 * The Automaton is parameterized by the Parser above.
	 * 
	 * @param string 
	 * @param pos starting Position of the Search
	 * @return the first Character after the first / shortest Regular Expression Match 
	 * found in string. 
	 * -1 is returned when there is no Match at all. 
	 */
	public int matchFirst(final String string) { return matchFirst(string, 0); }
	
	/**
	 * Returns the last Character of the Regular Expression found in string, 
	 * when starting the search at Character pos.
	 * When there is no Match, pos-1 is returned.
	 *  
	 * Pattern matching is done with a parameterized non-deterministic Automaton.
	 * The Capacity of the DeQueue used is at most 2^N (Pattern (A*A)* in List A*),
	 * if States are not re-used, else O(M*N^2) are sufficient. 
	 * The Automaton is parameterized by the Parser above.
	 * 
	 * @param string 
	 * @param pos starting Position of the Search
	 * @return the first Character after the first / shortest Regular Expression Match 
	 * found in string. 
	 * -1 is returned when there is no Match at all. 
	 */
	public int matchFirst(final String string, final int pos) {
		final IStreamIn_Int stream = new StringStreamIn(string); 
		stream.reSet(pos);
		return matchFirst(stream, pos); 
	}
	
	/**
	 * Returns the last Character of the Regular Expression found in string, 
	 * when starting the search at Character pos.
	 * When there is no Match, pos-1 is returned.
	 *  
	 * Pattern matching is done with a parameterized non-deterministic Automaton.
	 * The Capacity of the DeQueue used is at most 2^N (Pattern (A*A)* in List A*),
	 * if States are not re-used, else O(M*N^2) are sufficient. 
	 * The Automaton is parameterized by the Parser above.
	 * 
	 * @param string 
	 * @param pos starting Position of the Search
	 * @return the first Character after the first / shortest Regular Expression Match 
	 * found in string. 
	 * -1 is returned when there is no Match at all. 
	 */
	public int matchFirst(final IStreamIn_Int stream, final int pos) {
		DQ.clear();
		DQ.write(next1[0]); //the Start State
		DQ.write(scan); //'put()' always works the same...
		tested.clear(); 
		return matchNext(stream, pos); 
	}
	
	/**
	 * Returns the last Character of the Regular Expression found in string, 
	 * when starting the search at Character pos.
	 * When there is no Match, pos-1 is returned.
	 *  
	 * Pattern matching is done with a parameterized non-deterministic Automaton.
	 * The Capacity of the DeQueue used is at most 2^N (Pattern (A*A)* in List A*),
	 * if States are not re-used, else O(M*N^2) are sufficient. 
	 * The Automaton is parameterized by the Parser above.
	 * 
	 * @param string 
	 * @param pos starting Position of the Search
	 * @return the first Character after the next Regular Expression Match found in string. 
	 * -1 is returned when there is no Match at all. 
	 */
	public int matchNext(final String string, int pos) {
		final IStreamIn_Int stream = new StringStreamIn(string); 
		stream.reSet(pos); 
		return matchNext(stream, pos); 
	}
	
	/**
	 * Returns the last Character of the Regular Expression found in string, 
	 * when starting the search at Character pos.
	 * When there is no Match, pos-1 is returned.
	 *  
	 * Pattern matching is done with a parameterized non-deterministic Automaton.
	 * The Capacity of the DeQueue used is at most 2^N (Pattern (A*A)* in List A*),
	 * if States are not re-used, else O(M*N^2) are sufficient. 
	 * The Automaton is parameterized by the Parser above.
	 * 
	 * @param string 
	 * @param pos starting Position of the Search
	 * @return the first Character after the next Regular Expression Match found in string. 
	 * -1 is returned when there is no Match at all. 
	 */
	public int matchNext(final IStreamIn_Int stream) {
		return matchNext(stream, (int) stream.getPosition()); 
	}
	
	final public ByRefInt currChar = new ByRefInt(); 
	
	/**
	 * Returns the last Character of the Regular Expression found in string, 
	 * when starting the search at Character pos.
	 * When there is no Match, pos-1 is returned.
	 *  
	 * Pattern matching is done with a parameterized non-deterministic Automaton.
	 * The Capacity of the DeQueue used is at most 2^N (Pattern (A*A)* in List A*),
	 * if States are not re-used, else O(M*N^2) are sufficient. 
	 * The Automaton is parameterized by the Parser above.
	 * 
	 * @param string 
	 * @param pos starting Position of the Search
	 * @return the first Character after the next Regular Expression Match found in string. 
	 * -1 is returned when there is no Match at all. 
	 */
	public int matchNext(final IStreamIn_Int stream, int pos) {
		currChar.Value = stream.nextInt(); //could also be enforced by adding 'scan' to DQ
		do { //if the scan Mark is reached again, ...
			state = DQ.read(); //only the 'get()' changes
			if (state == 0) //reached the End of the Pattern (i.e. State 0)
				return pos; //all longer Matches are ignored. 
			if (state == scan) { //...all choices for this Position are through...
				++pos; 
				if ((currChar.Value = stream.nextInt()) == IStreamIn_Int.EOF)
					return -1; 
				DQ.write(scan); tested.clear(); //...and you have to try the next character 
			} else if (tester[state] == EOF) { //mark Zero States
				final int n1 = next1[state]; //Alternative, puts two new States at the Head of the Queue...
				final int n2 = next2[state]; //so they will be tested right now...
				if(!tested.get(n1)) { 
					tested.set(n1); 
					DQ.putHead(n1); }
				//if (n1 != n2) {
				if(!tested.get(n2)) {
					tested.set(n2); 
					DQ.putHead(n2); } //...but only if they are new / different
			} else if(tester[state].test(currChar))
				DQ.write(next1[state]); //match; put next State at the End...
			//...so you first test the other possibilities for this character
		} while (!DQ.isZero()); //no more testing Possibilities...not found. 
		return -1; 
	}
	
	/** Searches for a Match on all Positions of the String and gives out each Match. */
	public void matchAll(final String string) {
		for (int m, i = -1; ++i < string.length();) {
			if ((m = matchFirst(string, i)) >= i) {
				L.n().l(i).l(string.substring(i, m+1)); }
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Testing 
	///////////////////////////////////////////////////////////////////////////
	
	static final int[] expectedPos = { 6, 7, 8, 9, 12 }; 
	
	static final String[] expectedVal = {"AAABD", "AABD", "ABD", "BD", "ACD"}; 
	
	/** Tests all Methods of this Class */
	public static void testRegular() {
		final String pattern = "(A*B|AC)D";
		final String string = "CDAABCAAABDDACDAAC";
		final RegExp RE = new RegExp(pattern, string.length()); //List);
		for (int m, k = 0, i = -1; ++i < string.length();) {
			if ((m = RE.matchFirst(string, i)) >= i) { //TODO: isn't there a better way than to offset the Data Vector? 
				Assert.EQUALS(expectedPos[k], i); 
				Assert.EQUALS(expectedVal[k], string.substring(i, m)); 
				while(0 <= (m = RE.matchNext(string, m)) )
					L.n().l(i).l(string.substring(i, m)); 
				++k; 
			}
		}
	}
	
	/** Tests all Methods of this Class */
	public static void testRegular(final String pattern, final String string) {
		final RegExp RE = new RegExp(pattern, string.length()); //List);
		for (int m, i = -1; ++i < string.length();) {
			if ((m = RE.matchFirst(string, i)) >= i) {
				L.n().l(i).l(string.substring(i, m)); 
				while(0 <= (m = RE.matchNext(string, m)) )
					L.n().l(i).l(string.substring(i, m)); 
			}
		}
	}
	
	/** Tests the Behavior when encountering a pathologic Case: 
	 * The Pattern (A*)* is ambiguous and leads to a State Explosion
	 * with 2^(string.length()) States if Duplicates are not checked.   */
	public static void testPathologic() {
		final String pattern = "A**";
		final String string = "AAAAAAA"; 
		final RegExp RE = new RegExp(pattern, string.length()); //List);
		for (int i = -1; ++i < string.length();) {
			Assert.EQUALS(i, RE.matchFirst(string, i)); //first the empty Match
			for (int m = i, k = i; ++k < string.length();) 
				Assert.EQUALS(k, m = RE.matchNext(string, m)); 
		}
		L.n(RE.DQ); 
	}
	
	/** Tests all Methods of this Class */
	public static void testIt() {
		testRegular(); 
		testRegular("A**", "DAAADAAD");
		testPathologic(); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		if (args.length == 0)
			testIt(); 
	}
	
}
