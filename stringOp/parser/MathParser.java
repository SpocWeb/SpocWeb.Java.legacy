package stringOp.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;



/**Implements a simple Parser for an LL(1) Grammar with the following Operators:
 * +,-,*,/,\,%,>,<,&,|,!,^
 * Variables have a single Character (otherwise there must be a declaration Section,
 * which feeds the Scanner)
 * and the Multiplication Sign can be skipped in favor of faster notation.
 * The consequence is that only single Character Variables are allowed.
 * Takes a String as Input and gets the next Character.
 * No Backtracking necessary, because of LL(1).
 * Top-Down Implementation.
 * Uses the Scanner Class to read simple Objects. 
 * @see function.Derive.RingFuncs.FuncParser 
 */
public class MathParser {

//	Begin of static Region	//

	/**Constant for the opening Bracket Character	 */
	final static public char CHR_BRACKET_OPEN = '(';

	/**Constant for the closing Bracket Character	 */
	final static public char CHR_BRACKET_CLOSE = ')';

	/**Constant for the Minus Sign Character	 */
	final static public char CHR_SIGN_MINUS = '-';

	/**Constant for the Minus Sign Character	 */
	final static public char CHR_SIGN_PLUS = '+';

	/**Constant for the Multiplication Character	 */
	final static public char CHR_MULTIPLY = '*';

	/**Constant for the Division Character	 */
	final static public char CHR_DIVIDE = '/';

	/**Constant for the Division Character	 */
	final static public char CHR_MODULUS = '%';

	/**Constant for the "Greater" Character	 */
	final static public char CHR_GREATER = '>';

	/**Constant for the "Smaller" Character	 */
	final static public char CHR_SMALLER = '<';

	/**Constant for the Backslash Character	 */
	final static public char CHR_BACKSLASH = '\\';

	/**Constant for the Division Character	 */
	final static public char CHR_POWER = '^';

	/**Constant for the Or Operator Character	 */
	final static public char CHR_OR = '|';

	/**Constant for the And Operator Character	 */
	final static public char CHR_AND = '&';

	/**Constant for the Minus Sign Character	 */
	final static public char CHR_EXCLAMATION = '!';

	/**Constant for the Minus Sign Character	 */
	final static public char CHR_AMPERSAND = '&';

	//////////////////////////////
	//	End of static Region	//
	//////////////////////////////


	/**Determines, whether the Inverse Operations
	 * -, / and ^ are allowed in the Expression	 */
	public boolean Inverse = false;

	/**	Determines, whether the Boolean Operations
	 *	|, & and ! are allowed in the Expression	 */
	public boolean Boolean = false;

	/**	Local Reference to the Iterator giving the next Expression.
	 * This Iterator may already deliver fully parsed Objects like Numbers etc.	 */
	protected InputStream X;
//	protected Iterator X;

	/**	Current Character	 */
	protected int curr;

	/**	Initializing Constructor
	 * 	Sets the Input streamIO and
	 *	reads the first Byte (LL(1) Grammar)	  */
	public MathParser(InputStream IS) throws IOException {
		X = IS; curr = X.read(); }

	/**Parses an Expression E:
	 * E -> T | T + E | T - E	 */
	public void Expression(InputStream IS) throws IOException {
		X = IS; curr = X.read(); Expression(); }

	/**Parses an Expression E:
	 * E -> T | T + E | T - E |   T | E	 */
	public void Expression() throws IOException	{
		Term();
		if				(curr == CHR_SIGN_PLUS ) { curr = X.read(); Expression(); return; }
		if (Boolean &&	(curr == CHR_OR))		 { curr = X.read(); Expression(); return; }
		if (Inverse &&	(curr == CHR_SIGN_MINUS)){ curr = X.read(); Expression(); return; }
	}

	/** Parses a Term T:
	  * T -> F | F * T | F & T | F \ T | F % T | F / T | F < T | F > T | F ^ T	 */
	public void Term() throws IOException {
		FactorTerm();	//The Reason that the Rules are not implemented here, but in Factor ...
		if  (curr == CHR_BRACKET_OPEN || Scanner.IS_LETTER(curr)) Term(); }
		//... is, that Products without * Sign are allowed!

    /** processing T: here is the Code that would normally go into the Term:
      * T -> F | F * T | F & T | F \ T | F % T | F / T | F < T | F > T | F ^ T	 */
	protected void FactorTerm() throws IOException	{
        Factor();
		curr = X.read();
		if				(curr == CHR_MULTIPLY	)	{ curr = X.read(); return; }
		if (Boolean &&	(curr == CHR_AMPERSAND	))	{ curr = X.read(); return; }
		if (Inverse &&	(curr == CHR_DIVIDE		))	{ curr = X.read(); return; }
		if (Inverse &&	(curr == CHR_MODULUS	))	{ curr = X.read(); return; }
		if (Inverse &&	(curr == CHR_GREATER	))	{ curr = X.read(); return; }
		if (Inverse &&	(curr == CHR_SMALLER	))	{ curr = X.read(); return; }
		if (Inverse &&	(curr == CHR_BACKSLASH	))	{ curr = X.read(); return; }
		if (Inverse &&	(curr == CHR_POWER		))	{ curr = X.read(); return; }
    }

	/**Parses a Factor F:
	 * F = (E) | -F | !F | V | Funktion */
	public void Factor() throws IOException	{
		if (curr == CHR_BRACKET_OPEN) { //(E)
			curr = X.read(); //prepare the Parsing of the inner Expression.
			Expression();
			if (curr != CHR_BRACKET_CLOSE) throw new AbstractMethodError("')' expected");
			curr = X.read(); return; }	//expects a closing Bracket after an inner Expression
		if (Inverse && (curr == CHR_SIGN_MINUS  ))	{ curr = X.read(); Factor(); return; } //-F
		if (Boolean && (curr == CHR_EXCLAMATION ))	{ curr = X.read(); Factor(); return; } //!F
		if (! Scanner.IS_LETTER(curr)) throw new AbstractMethodError("Variable expected");
	}	//expects a (single Character) Variable, because no Production fits.

	/**Tests all Methods of this Class	 */
	public static void testIt() throws IOException {
		System.out.println("Testing Parser:");
		MathParser P = new MathParser(new StringBufferInputStream("(A*B+AC)D"));
		P.Expression();	P.Inverse = true;	//also processes Inverses now...
		P.Expression(new StringBufferInputStream("A"));	//
		P.Expression(new StringBufferInputStream("-A"));	//
		P.Expression(new StringBufferInputStream("(A+B)*BC"));	//
		P.Expression(new StringBufferInputStream("(A-B)/B"));	//
		P.Expression(new StringBufferInputStream("(A+B)*B/C"));	//
		P.Expression(new StringBufferInputStream("(A+B)^(B*C)"));	//
	}

}
