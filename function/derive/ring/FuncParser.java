package function.derive.ring;

import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.StringBufferInputStream;

import stringOp.parser.Scanner;
import function.IFunction;
import function.derive.CMeasurAble;

/**Implements a Parser for an LL(1) Grammar with the following Operators:
 * +,-,*,/,\,%,>,<,&,|,!,^
 * Bracketing overrides any Operator Precedence!
 * Variables are not defined and the Multiplication Sign can not be skipped.
 * Top-Down Implementation.	No Backtracking necessary, because of LL(1).
 * @see stringOp.parser.MathParser
 */
public class FuncParser
extends Scanner {
	
	/**Switches the extended Operators -,/ and ^ on.	 */
	public boolean Inverse = false;
	
	/**Switches the boolean Operators |, & and ! on.	 */
	public boolean Boolean = false;
	
	/**Initializing Constructor	 */
	public FuncParser(InputStream IS) throws IOException {
		super(IS, "");
		Expression(); }

	/**Current Function 	 */
	protected IFunction currFunction;

	/**Parses a boolean Expression E:
	 * B -> E | E = B | E != B | E < B | E <= B | E == B | E >= B | E > B	 */
	public IFunction bolExpression() throws IOException {
		Expression();
		boolean eq = false;
		IFunction B =	currFunction;
			 if (testChar('!')) {;}
		else if (testChar('<')) {
			eq = testChar('=');
			bolExpression();
			if (construct) { B = null; } }	//Comparison: either < or <=
		else if (testChar('>')) {
			eq = testChar('=');
			bolExpression();
			if (construct) { B = null; } }	//Comparison: either > or >=
		else if (testChar('=')) {
			eq = testChar('=');
			bolExpression();
			if (construct) { B = null; } }	//either Assignment or Comparison
		else if (Inverse &&
				 testChar('!')) {
			checkChar('=');
			bolExpression();		//Test for Inequality!
			if (construct) { B = null; } }//new Diff(E, currFunction);}
		if (eq);
		return currFunction = B; }

	/**Parses an Expression E:
	 * E -> T | T + E | T - E	 */
	public IFunction Expression() throws IOException {
		Term();
		IFunction E =	currFunction;
			 if (Boolean &&
				 testChar('|'));	//Implementation missing...
		else if (testChar('+')) {Expression(); if (construct) E = new Sum (E, currFunction);}
		else if (Inverse &&
				 testChar('-')) {Expression(); if (construct) E = new Diff(E, currFunction);}
		return currFunction = E; }

	/**Parses a Term T -> F | F * T | F / T | F ^ T | F @ E	 */
	public IFunction Term() throws IOException {
		Factor();
		IFunction T =	currFunction;
		if (testChar('*')){Term();	 if (construct) T = new Prod(T, currFunction);}
		if (Inverse &&
			testChar('/')){Term();	 if (construct) T = new Quot(T, currFunction);}
		if (Inverse &&	//choosing the @ Notation more harmoneously integrates into the other Production Rules.
			testChar('@')){Factor(); if (construct) T = new CatDerive(T, currFunction);}	//choosing Factor here results in only the very next Argument to be chosen, instead of the next Product with Term or even the next Sum with Expression!
/*		if (Inverse &&
			testChar('^')){Factor(); if (construct) T = new BodyFuncs.fPower(T, currFunction);}
		if (Inverse &&
			testChar('%')) ;
		if (Inverse &&
			testChar('>')) ;
		if (Inverse &&
			testChar('<')) ;
		if (Inverse &&
			testChar('\\')) ;
		if (Inverse &&
			testChar('&')) ;
		if (testChar('(') || (isLetter(curr))) Term();
*/		return currFunction = T; }

	/**Parses a Factor F:
	 * F = (E) | V | -V	| !V | f | f @ g
	 * Bracketing is not necessary, but then the normal Precedences work:
	 * highest: () or @	 Concatenation can be expressed with @ Sign OR with (...)
	 * medium:	* /
	 * low:		+ -	 */
	public IFunction Factor() throws IOException {
		IFunction F = null;
		if (testChar('(')) {	//prepare the Parsing of the inner Expression.
			Expression(); F = currFunction;
			checkChar(')');
		}	//expects a closing Bracket after an inner Expression
		else if(Inverse && testChar('-')) {Factor(); if (construct) F = new CatDerive(Neg.NEG, currFunction);}
		else if(Boolean && testChar('!'))  Factor();
		else if (IS_LETTER(currChar)) {	//Function or Variable (which is a constant Function): assemble the Name and
			String fStr = String.valueOf((char) currChar); clearString(); //TODO: since the new Parser already adds to the String on Character()...
			fullIdentifier(); //...you have to re-add it here!
			fStr += getResult();	//use it for searching or even creating the foreign Base.
			if (construct) //get the first of a Series of Functions
				if (null == (F = findFunction(fStr)))
					try{ F = (IFunction) Class.forName(fStr).newInstance(); } catch (Exception x){};
			if(Inverse && testChar('(')) { Factor(); F = new CatDerive(F, currFunction).simplify (); } //if used on Constants, they can be simplified here!
			if(Inverse && testChar('@')) { Factor(); F = new CatDerive(F, currFunction).simplify (); }
			//choosing Factor here results in only the very next Argument to be chosen, instead of the next Product with Term or even the next Sum with Expression!
			//Bracket() Notation is more often used.
			//° or @ Notation more harmoneously integrates into the other Production Rules, but precedence is a Question!
		}
		else if (IS_DIGIT(currChar)) {	//Function or Constant or Variable (which is a constant Function)
			if (construct) F = new Algebra(new CMeasurAble(Real())); // Body.BodyDouble(Real()));	//use it for searching or even creating the foreign Base.
		}
		else throw new AbstractMethodError("Variable" + strExpected); 	//expects a Variable, because nothing else fits.
		return currFunction = F; }

	/**Should be overridden by inheriting classes of later Packages
	 * to provide short names for their own Functions.
	 * This is necessary e.g. in C++.
	 * In Java use the Reflection API to dynamically create Functions.	*/
	protected IFunction findFunction(String fStr) {
		IFunction Return = null;
//		if (fStr.equals("Sinus")){Return = Sinus.Sinus;}
		return Return; }

	/** Tests all Methods of this Class	 */
	public static void testIt() throws IOException {
		System.out.println("Testing FuncParser:");
		FuncParser P = new FuncParser(new StringBufferInputStream("(A*B+AC)D"));
		System.out.println(P.currFunction);
		P.Inverse = true;	//also processes Inverses now...
		InputStream In = new StringBufferInputStream("BodyFuncs.Cosinus(Sinus)");
		StreamTokenizer ST = new StreamTokenizer(In);
		System.out.println(ST.nextToken() + ST.sval);	//doesn't Break the Function apart!
		System.out.println(ST.nextToken() + ST.sval);
		System.out.println(ST.nextToken() + ST.sval);
		In.reset();
		P = new FuncParser(In);	//Can the StreamTokenizer help me???
		P = new FuncParser(new StringBufferInputStream("(A)"));	//
		P = new FuncParser(new StringBufferInputStream("-3")); P.currFunction.simplify();	//
		P = new FuncParser(new StringBufferInputStream("((A))"));	//
		P = new FuncParser(new StringBufferInputStream("Sinus@Sinus"));	//Concatenation works with this Notation
		P = new FuncParser(new StringBufferInputStream("(A)B"));	//If the Parser cannot interpret the Symbols, but the setInput is well formed, it returns null
		P = new FuncParser(new StringBufferInputStream("Sinus(Sinus)"));	//Concatenation works with this Notation too
		P = new FuncParser(new StringBufferInputStream("(((Sinus)))"));	//Bracketing shouldn't create Overhead
		P = new FuncParser(new StringBufferInputStream("-A"));	//
		P = new FuncParser(new StringBufferInputStream("(A+B)*BC"));	//
		P = new FuncParser(new StringBufferInputStream("(A-B)/B"));	//
		P = new FuncParser(new StringBufferInputStream("(A+B)*B/C"));	//
		P = new FuncParser(new StringBufferInputStream("(A+B)^(B*C)"));	//
	}

}
