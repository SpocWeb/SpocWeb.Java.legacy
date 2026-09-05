package function.derive;

import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;

/**Title:        ConstIMeasurAble<p>
 * Description:  Concrete Class containing only static Members and Methods<p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author  Matthias Heuer
 * @version 1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:28:44Z
 * digest: 6cfd570b78b644a7174952e4d1545abf4d6646d84a5fa06c73c9648ef352fc70
 * stale: false
 * tags: [code/constant_function, code/mathematical_constants]
 * concepts: [Function Algebra, Numeric Constants]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class CMeasurAble
extends AConst //orderAble // ADeriveAble //ByRefDouble
implements ICountAble { //

	////////////////////////////////////////////////////////////////////////////
	//  static Constants
	////////////////////////////////////////////////////////////////////////////

	/** Constant Function returning the Value 0.5.	 */
	final static public CMeasurAble Half    = new CMeasurAble (HALF);
	/** Constant Function returning the Value 1/3.	 */
	final static public CMeasurAble Third   = new CMeasurAble (THIRD);
	/** Constant Function returning the Value 0.25.	 */
	final static public CMeasurAble Quarter = new CMeasurAble (QUARTER);

	/** Constant Function returning the Value 0.01, for expressing Percentages.	 */
	final static public CMeasurAble Percent = new CMeasurAble (PERCENT);				//For Prozentangaben
	/** Constant Function returning the Value 0.001, for expressing Per-mille Values.	 */
	final static public CMeasurAble Permille= new CMeasurAble (PERMILLE);			   //For Promilleangaben

	/** Relaxation Factor slightly above 1, for Over-Relaxation Iterations.	 */
	final static public CMeasurAble EinsK1  = new CMeasurAble (EINSK1);				  //for Over -Relaxation
	/** Relaxation Factor slightly below 1, for Under-Relaxation Iterations.	 */
	final static public CMeasurAble NullK9  = new CMeasurAble (NULLK9);				  //for Under-Relaxation
	/** Relaxation Factor slightly above 0, for Under-Relaxation Iterations.	 */
	final static public CMeasurAble NullK1  = new CMeasurAble (NULLK1);

	/** Constant Function returning Not-a-Number (NaN).	 */
	final static public CMeasurAble  Nan       = new CMeasurAble ( NAN);
	/** Constant Function returning positive Infinity.	 */
	final static public CMeasurAble  Infinity  = new CMeasurAble ( INFINITY);
	/** Constant Function returning negative Infinity.	 */
	final static public CMeasurAble _Infinity  = new CMeasurAble (_INFINITY);

	///////////////////////////////////////////////////////////////////////////////
	//Transcendental Constants:
	///////////////////////////////////////////////////////////////////////////////

	/** Natural Logarithm of 2 = 1/{@link #Lbe}.	 */
	final static public CMeasurAble Ln2 = new CMeasurAble (LN2);	//.69314718055994530941;	  nat?rlicher Logarithmus von 2=1/Lbe
	/** Binary Logarithm of e (log2(e)).	 */
	final static public CMeasurAble Lbe = new CMeasurAble (LBE);	//1.4426950408889634073;	  Bin�rer Logarithmus von e (s.u.)
	/** Natural Logarithm of 10.	 */
	final static public CMeasurAble Ln10= new CMeasurAble (LN10);	//2.302585093	//Lb10/Lbe;
	/** Binary Logarithm of 10 (log2(10)).	 */
	final static public CMeasurAble Lb10= new CMeasurAble (LB10);	//Ln10/Ln2;	//3.3219280948873623479;	  Bin�rer Logarithmus von 10

	/**Decadic Logarithm of two Lg2	= 0,30102999566398119521373889472449	 */
	final static public CMeasurAble Lg2	= new CMeasurAble (LG2);	//.30102999566398119519;	  dekadischer Logarithmus von 2=

	/**Kreiszahl Pi = 3.1415926535897932386	 */
	final static public CMeasurAble Pi	= new CMeasurAble (PI);

	/**Eulersche Zahl e = 2.71828182845904523536	 */
	final static public CMeasurAble e	= new CMeasurAble (E);

	/**Eulersche Zahl e^2 = 7.389056098930650227230427460575	 */
	final static public CMeasurAble e2	= new CMeasurAble (E2);

	/**Eulersche Zahl e^2*Pi = 23.213404357363387236150345896007	 */
	final static public CMeasurAble e2Pi=new CMeasurAble (E2PI);

	/**Eulersche-Mascheroni-Konstante	 */
	final static public CMeasurAble EulerC= new CMeasurAble (EULER_C);	//0.57721566490152870;

	/**Feigenbaum-Delta :
	 * Grenzwert der Verhaeltnisse der Abstaende
	 * von quadratischen Bifurkationen.	 */
	/** Feigenbaum Delta: the limit ratio of successive bifurcation-interval widths.	 */
	final static public CMeasurAble Feigen		= new CMeasurAble (FEIGEN);
	/** Negative Pi.	 */
	final static public CMeasurAble _Pi			= new CMeasurAble (-PI);
	/** Pi divided by 2.	 */
	final static public CMeasurAble PiHalf		= new CMeasurAble (PI_HALF);
	/** Pi divided by 4.	 */
	final static public CMeasurAble PiQuarter	= new CMeasurAble (PI_QUARTER);
	/** Three quarters of Pi.	 */
	final static public CMeasurAble ThreePiQuarter = new CMeasurAble (THREE_PI_QUARTER);
	/** Two times Pi, a full Circle in Radians.	 */
	final static public CMeasurAble TwoPi		= new CMeasurAble (TWO_PI);
	/** A full Circle expressed in Gon (400 Gon).	 */
	final static public CMeasurAble FullGon		= new CMeasurAble (FULL_GON);
	/** A full Circle expressed in Degrees (360°).	 */
	final static public CMeasurAble FullDeg		= new CMeasurAble (FULL_DEG);
	/** Conversion Factor from Degrees to Radians.	 */
	final static public CMeasurAble Grad		= new CMeasurAble (GRAD);	//Zur Umrechnung in andere Winkelsysteme
	/** Conversion Factor from Gon to Radians.	 */
	final static public CMeasurAble Gon			= new CMeasurAble (GON);
	/** Cubic Root of 2.	 */
	final static public CMeasurAble CbcRt2		= new CMeasurAble (CBCRT2); //Bxp  (Drittel);
	/** Square Root of 2.	 */
	final static public CMeasurAble SqRt2		= new CMeasurAble (SQRT2);
	/** Square Root of 3.	 */
	final static public CMeasurAble SqRt3		= new CMeasurAble (SQRT3);
	/** Square Root of 5.	 */
	final static public CMeasurAble SqRt5		= new CMeasurAble (SQRT5);
	/** Square Root of Pi.	 */
	final static public CMeasurAble SqRtPi		= new CMeasurAble (SQRTPI);
	/** Square Root of 2*Pi.	 */
	final static public CMeasurAble SqRt2Pi		= new CMeasurAble (SQRT2PI);
	/** Square of the Cubic Root of 2.	 */
	final static public CMeasurAble SqrCbcRt2	= new CMeasurAble (SQRCBCRT2);
	/** Golden Ratio Factor (sqrt(5)-1)/2 &#8776; 0.618, the smaller Golden Section.	 */
	final static public CMeasurAble Golden		= new CMeasurAble (GOLDEN); //(SqRt (5)-1)/2=0.681 Verhaeltnis des goldenen Schnittes
	/** Complement of {@link #Golden}: 1 - Golden.	 */
	final static public CMeasurAble cGolden		= new CMeasurAble (CGOLDEN); //1 - Golden = 0.319 ,dessen Komplement und der
	/** {@link #Golden} plus 1, the Interval-enlargement Factor of the Golden Section Search.	 */
	final static public CMeasurAble OneGolden	= new CMeasurAble (ONEGOLDEN); //1 + Golden = 1.681 Faktor zur Vergroesserung e. Intervalles
	/** Natural Logarithm of {@link #OneGolden}.	 */
	final static public CMeasurAble LnOneGolden	= new CMeasurAble (LNONEGOLDEN);
	/** Smallest Value still considered distinguishable from 1, used as a Precision Tolerance.	 */
	final static public CMeasurAble Epsilon		= new CMeasurAble (EPSILON); //2 ^ Genauigkeit;	  //'Kleine' Groesse im Vergleich zu 1

	////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Comments  : Converts the passed integer to Roman numerals
	  * Parameters: intIn - Value to convert
	  * Returns   : String
	  */
//	final static public int ROMAN_TO_NUMBER(String strIn) { }

	/** Comments  : Converts the passed integer to Roman numerals
	  *             due to the Algorithm a lot of String Garbage is created!
	  * When's the last time you watched a really great old movie and wondered,
	  * as the credits rolled by, when the film was made?
	  * You probably wondered why the copyright notice at the end was in Roman numerals.
	  * Well, long ago, in the early messozoic period of Hollywood,
	  * studio moguls decided that Roman numerals were a clever way
	  * to disguise "older" (and perhaps stale) films
	  * and still allow them to display copyright information.
	  * Parameters: intIn - Value to convert
	  * Returns   : String
	  */
	final static public String NUMBER_TO_ROMAN(int intIn) {
		int counter = 1;
		int digit;
		String strTmp = "";

		//Loop through values in input value
		while (intIn > 0) {
			//Get  the current digit
			digit  = intIn % 10;
			intIn /= 10;

			//Build the temp string. This is a straightforward (dumb) Algorithm
			char c0 = cstrDigits.charAt(counter  );
			char c1 = cstrDigits.charAt(counter+1);
			char c2 = cstrDigits.charAt(counter+2);
			switch (digit) {
				case 1: strTmp = c0 + strTmp;
				case 2: strTmp = c0 + c0 + strTmp;
				case 3: strTmp = c0 + c0 + c0 + strTmp;
				case 4: strTmp = c0 + c1 + strTmp;
				case 5: strTmp = c1 + strTmp;
				case 6: strTmp = c1 + c0 + strTmp;
				case 7: strTmp = c1 + c0 + c0 + strTmp;
				case 8: strTmp = c1 + c0 + c0 + c0 + strTmp;
				case 9: strTmp = c0 + c2 + strTmp;
			}
			counter += 2;
		}
		return strTmp; }

	/**
	  * Comments   : Converts the passed string representation of an octal
	  *              number to a decimal long integer.
	  * Parameters : strOctal - String representation of octal number
	  * @return    : Decimal value
	  * The Classes Integer and Long have Methods to
	  * write the Representation in any Radix: toString(int Value, int Radix)
	  * parse the Value for any Radix: parseInt(String Val, int Radix)
	  */
//	final static public long OctalStringToDecimal(String strOctal) { }

	/**
	  * Comments  : Converts a "zoned overpunch" number to a regular number
	  * Many mainframe and minicomputer systems written in COBOL
	  * and other languages use a numeric format known as zoned overpunch.
	  * The numbers consist of normal ASCII characters,
	  * except that the last character indicates the sign of the number.
	  *
	  * For example, the following "number":
	  * 12345F is actually:  123456 and
	  * 12345O is actually: -123456
	  *
	  * Decimal places are implied.
	  * A positive zero is "{" and a number ending in a negative zero is "}".
	  * @param  strNum - Zoned overpunch value to convert
	  * @return converted number
	  */
	final static public long OVERPUNCHED_STRING_TO_NUMBER(String strNum) {
		int len = strNum.trim().length(); //Get the length of the string
		char last = strNum.charAt(len-1); //Get the last character
		boolean neg;

		//Decide how to convert the last character
			if  ((last >= 'A') &&
				 (last <= 'I')){ neg = false; last -= 'A' + '0' - 1;
		}else if((last >= 'J') &&
				 (last <= 'R')){ neg = true ; last -= 'J' + '0' - 1;
		}else if (last == '{') { neg = false;
		}else if (last == '}') { neg = true ;
		}else                  { neg = false; last =  9; strNum = "9999999999999";
		}
		long ret = Long.parseLong(strNum.substring(0, len - 1) + last);
		if (neg) {
			return -ret; }
			return  ret; }

	/**
	  * Comments  : Converts a phone number letter to a number
	  * Parameters: chrIn - Letter to check. Must be in the range a-p
	  *             or r-y. Q and Z are not valid phone letters.
	  * Returns   : Integer number
	  */
	final static public int PHONE_LETTER_TO_DIGIT(char chr) {
		chr = Character.toUpperCase(chr);
		//Make sure its a letter
		if ((chr <  'A') ||
			(chr >  'Y') ||
			(chr == 'Q')){
			throw new RuntimeException("No valid PhoneLetter!"); }
		//For historical reasons, Q is not a valid letter on a phone.
		//Z is also left out.
		if (chr > 'Q') { --chr; }
		return (chr - 'A') / 3 + 2; }

	///////////////////////////////////////////////////////////////////////////////
	//  Variables
	///////////////////////////////////////////////////////////////////////////////

	/**Local Storage for the Value	 */
	protected double _Value;

	///////////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////////

	//no empty Constructor and not CopyAble, so it cannot be serialized

	/**Initializing Constructor   */
	public CMeasurAble( double Value) { this._Value = Value; }

	/**Initializing Constructor   */
	public CMeasurAble( IMeasurAble Value) { this._Value = Value.getDouble(); }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface OrderAble: optimized Implementations
	///////////////////////////////////////////////////////////////////////////////

	/** Sloppy (on Equality) but fast 'between' Implementation
	  * @param arg1 : first  Border to compare to <CODE>this</CODE>
	  * @param arg2 : second Border to compare to <CODE>this</CODE>
	  * @return True, when 'Self' is between arg1 and arg2
	  */
	public boolean isBetween (Object arg1, Object arg2) {
		return isLessThan(arg1) ^ isLessThan (arg2);}

	/** less Relation: '<'
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' < arg
	 */
	public boolean isLessThan (Object arg) {
		if ((arg == null) ||
			(arg == this)) return false;
		return _Value < ByRefDouble.GET_DOUBLE(arg); }

	/** greater Relation: '>'
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' > arg
	 */
	public boolean isMoreThan (Object arg) {
		if ((arg == null) ||
			(arg == this)) return false;
		return _Value > ByRefDouble.GET_DOUBLE(arg); }

	/** greater or equal: '>='
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' >= arg
	 */
	public boolean notLessThan (Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		return _Value >= ByRefDouble.GET_DOUBLE(arg); }

	/** less or equal: '<='
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' <= arg
	 */
	public boolean notMoreThan (Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		return _Value <= ByRefDouble.GET_DOUBLE(arg); }

	/** Returns the Position of this Object relative to arg:
	  * This Operation is leaner than compareTo.
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return -1 for this < arg
	  *         +1 otherwise
	  */
	public int Position(Object arg) {
		if ((arg == null) ||
			(arg == this)) return 1;
		return (_Value < ByRefDouble.GET_DOUBLE(arg)) ? -1 : 1; }

	/** Returns the exact Position of this Object relative to arg:
	 * The Java 1.2 Interface 'Comparable' calls this 'compareTo'
	 * The Java 1.2 Interface 'Comparable' defines an Operator with 'compare'
	 * -1 for smaller, 0 for equal, otherwise +1
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return -1 for this <  arg
	 *          0 for this == arg
	 *         +1 otherwise
	 */
	public int compareTo(Object arg) {
		if (arg == null) return 1;
		if (arg == this) return 0;
		double Val;
		if     (_Value < (Val = ByRefDouble.GET_DOUBLE(arg))) return -1;
		return (_Value >  Val) ? +1 : 0; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public byte	 getByte() { //return Value; }
		byte Val  = (byte) _Value;
		if  (Val !=        _Value) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 16 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public short getShort() { //return Value; }
		short Val  = (short) _Value;
		if   (Val !=         _Value) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 32 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public int	 getInt() { //return Value; }
		int  Val  = (int) _Value;
		if  (Val !=       _Value) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 64 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public long  getLong() { //return Value; }
		long Val  = (long) _Value;
		if  (Val !=        _Value) throw new IllegalArgumentException();
		return Val; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface IMeasurAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign */
	public double getDouble() { return _Value; }
/*		double Val = (double) Value;
		if (Value != Val) throw new IllegalArgumentException();
		return Val; }
*/

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float  getFloat() { //return Value; }
		float Val  = (float) _Value;
		if   (Val !=         _Value) throw new IllegalArgumentException();
		return Val; }

	//////////////////////////
	//  Interface CopyAble
	//////////////////////////

	//These are the virtual Methods of Object: they cannot be abstracted int AConst!

	/**Returns the decimal string representation of the wrapped Double Value.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() { return Double.toString(_Value); } //"CMeasurAble(" + value + ")"; }

	/**Returns a hash code inner for the object. This method is
	 * supported for the benefit of hashtables such as those provided by
	 * <code>java.util.Hashtable</code>.
	 * <p>
	 * The general contract of <code>hashCode</code> is:
	 * <ul>
	 * <li>Whenever it is invoked on the same object more than once during
	 * an execution of a Java application, the <code>hashCode</code> method
	 * must consistently return the same integer. This integer need not
	 * remain consistent from one execution of an application to another
	 * execution of the same application.
	 * <li>If two objects are equal according to the <code>equals</code>
	 * method, then calling the <code>hashCode</code> method on each of the
	 * two objects must produce the same integer result.
	 * </ul>
	 *
	 * @return  a hash code inner for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 */
	public int hashCode() {
		return (int)Double.doubleToLongBits(_Value); }
//		long bits = Double.doubleToLongBits(value);
//		return (int)(bits ^ (bits >> 32)); }
//		return new Double(value).hashCode(); }

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see     java.lang.Boolean#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 	 */
	public boolean equals  (Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		return _Value == ByRefDouble.GET_DOUBLE(arg); }

	///////////////////////////////////////////////////////////////////////////
	//	static Methods for converting IMeasurAble and ICountAble
	///////////////////////////////////////////////////////////////////////////

	/**Converts an Array of simple Type Contstants
	 * into an Array of the corresponding Object Type	 */
	final static public CMeasurAble[] const2Const(float[] arg) {
		int len;
		CMeasurAble[] ret = new CMeasurAble[len = arg.length];
		while (--len >= 0)
			ret[len] = new CMeasurAble (arg[len]);
		return ret; }

	/**Converts an Array of simple Type Contstants
	 * into an Array of the corresponding Object Type	 */
	final static public CMeasurAble[] const2Const(double[] arg) {
		int len;
		CMeasurAble[] ret = new CMeasurAble[len = arg.length];
		while (--len >= 0)
			ret[len] = new CMeasurAble (arg[len]);
		return ret; }

}
