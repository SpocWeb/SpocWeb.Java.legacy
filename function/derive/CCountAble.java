package function.derive;

import function.ICountAble;
import function.byref.ByRefDouble;
import function.byref.ByRefLong;

/**Title:        CCountAble<p>
 * Description:  <p>
 * This Class encapsulates the Constant (Scalar) Function and Enums.
 * It returns the Constant Long Value to any Argument.
 * If the Value is modifyable, it cannot be protected against Changes.
 * This Function doesn't implement the AIntegrityRing Interface
 * so it cannot be added / subtracted ... directly to other Const Objects.
 * This is done indirectly using the Algebra Class.
 * This is the Base Class of ConstCopyAble, CMeasurAble (and ByRefObject)
 * and for any Enum!
 *
 * Also prepares the Implementation of the Enum Class encoding Enumerations,
 * by using faster Identity Checks instead of equals() Definitions.
 * Enumerations are a common Means to structure and describe limited Size Sets.
 * The actual Value and Order Relation of an Enum is usually not important,
 * only the Fact that it can be used in a switch () Statement.
 * For Enums denoting discrete Sections of a Dimension like Months, Hours etc.
 * the Order Relation is important, but only within a Period!
 *
 * Design Decisions:
 * Use 'short' or 'byte' on purpose to force Users to cast or use predefined Constants!
 * Making the Value writeAble by adding a setValue() Method results in:
 * * allowing to hand back a Result ByRef, although also an Array could do that!
 * * non constant-ness which opens up Complexities in Algorithms and Concurrency!
 * * requires a Runtime Check by Value in setValue()
 * * replaces fast Identity Check with slower equals Method.
 * * Exactly for Enums with its fixed Set of Members (unlike Strings)
 *   Constant Members are ideal! They implement the Flyweight Pattern!
 * * The Flyweight Pattern also saves Memory
 *   as well as expensive Creation and Destruction of Objects.
 *
 * If you wan ByRef Handover, you can use Arrays for that!
 *
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:17:38Z
 * digest: a3bcdf3ae655ecf976eb39855aedf0aceae0e4baec74c1da8b5031d76cba1e9d
 * stale: false
 * tags: [code/constant_function, code/numeric_comparison]
 * concepts: [Function Algebra, Flyweight Pattern]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class CCountAble
extends AConst // Const // ADeriveAble //ByRefLong //bit of an Overhead to use Const, because the Derivative is 0, the Inverse not defined and Map == this;
implements ICountAble {

	///////////////////////////////////////////////////////////////////////////////
	//  static Constants
	///////////////////////////////////////////////////////////////////////////////

	/** Constant representing the Constant (Function) Zero
	  * could also represent the Empty Set, because there is only a single empty Set
	  * but since the other Numbers cannot be represented by a single Set,
	  * this is skipped.
	  */
	final static public CCountAble  Zero    = new CCountAble((int) ZERO);
	/** Constant Function returning the Value 1.	 */
	final static public CCountAble  One     = new CCountAble((int)  ONE);
	/** Constant Function returning the Value -1.	 */
	final static public CCountAble _One     = new CCountAble((int) _ONE);
	/** Constant Function returning the Value 2.	 */
	final static public CCountAble Two      = new CCountAble((int)  TWO);
	/** Constant Function returning the Value 3.	 */
	final static public CCountAble Three    = new CCountAble((int)THREE);
	/** Constant Function returning the Value 4.	 */
	final static public CCountAble Four     = new CCountAble((int) FOUR);
	/** Constant Function returning the Value 5.	 */
	final static public CCountAble Five     = new CCountAble((int) FIVE);
	/** Constant Function returning the Value 10.	 */
	final static public CCountAble Ten      = new CCountAble((int)  TEN);
	/** Constant Function returning the Value 100.	 */
	final static public CCountAble Hundred  = new CCountAble((int)HUNDRED);
	/** Constant Function returning the Value 1000.	 */
	final static public CCountAble Thousand = new CCountAble((int)THOUSAND);

	static { //Initializer
		Zero.Integral = Zero;
//		One .setIntegral(ADeriveAble.Identity); //not necessary, set by Identity!
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Comparison Constants
	////////////////////////////////////////////////////////////////////////////////

	/** Constant denoting the LESS Value in Comparisons	 */
	final static public byte LESS = 0;

	/** Constant denoting the EQUAL Value in Comparisons	 */
	final static public byte EQUAL = 1;

	/** Constant denoting the GRTR Value in Comparisons	 */
	final static public byte GRTR = 2;

	/** Constant denoting the undefined INCMP Value in Comparisons
	  * This is a fundamental State denoting Contradiction
	  * or just Meaninglessness of the Comparison	 */
	final static public byte INCMP = 3;

	/** Constant listing all Values in COMPARE 	 */
	final static public byte[] COMPARISON = {LESS, EQUAL, GRTR, INCMP};

	/** Constant denoting the LESS Value in Comparisons	 */
	final static public Comparison Less = new Comparison(LESS);

	/** Constant denoting the EQUAL Value in Comparisons	 */
	final static public Comparison Equal = new Comparison(EQUAL);

	/** Constant denoting the GRTR Value in Comparisons	 */
	final static public Comparison Grtr = new Comparison(GRTR);

	/** Constant denoting the undefined INCMP Value in Comparisons
	  * This is a fundamental State denoting Contradiction
	  * or just Meaninglessness of the Comparison	 */
	final static public Comparison InCmp = new Comparison(INCMP);

	/** Constant listing all Values in COMPARE 	 */
	final static public Comparison[] Comparison = {Less, Equal, Grtr, InCmp};

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Helper Method for the Ternary and other Enum Classes
	  * Sets the Value and returns the previous Value.
	  * This makes the Method more useful and is only little Overhead
	  * because the Call Overhead weighs much heavier!  	 */
	protected static final long setValue(final CCountAble E, final long MinValue, final long newValue, final long MaxValue) { long ret = E.Value;
		if ((E.Value <  MinValue) ||
		    (E.Value >= MaxValue)) throw new IllegalArgumentException();
		else E.Value  = newValue; return ret; }

	///////////////////////////////////////////////////////////////////////////////
	//  Variables
	///////////////////////////////////////////////////////////////////////////////

	/**Local Storage for the Value	 */
	protected  long Value;

	///////////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////////

	//no empty Constructor and not CopyAble, so it cannot be serialized

	/** Empty Constructor: Value = 0   */
	protected CCountAble() { }

	/**Initializing Constructor   */
	public CCountAble(final long Value) { this.Value = Value; }

	/**Initializing Constructor   */
	public CCountAble( ICountAble Value) { this.Value = Value.getLong(); }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	final public byte	 getByte() { //return Value; }
		byte Val = (byte) Value;
		if (Value != Val) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 16 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	final public short getShort() { //return Value; }
		short Val = (short) Value;
		if (Value != Val) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 32 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	final public int	 getInt() { //return Value; }
		int Val = (int) Value;
		if (Value != Val) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 64 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	final public long  getLong() { return Value; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface IMeasurAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign */
	final public double getDouble() { return Value; }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	final public float  getFloat() { return Value; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface OrderAble: optimized Implementations
	///////////////////////////////////////////////////////////////////////////////

	/** Sloppy (on Equality) but fast 'between' Implementation
	  * @param arg1 : first  Border to compare to <CODE>this</CODE>
	  * @param arg2 : second Border to compare to <CODE>this</CODE>
	  * @return True, when 'Self' is between arg1 and arg2
	  */
	final public boolean isBetween (Object arg1, Object arg2) {
		return isLessThan(arg1) != isLessThan (arg2);}

	/** less Relation: '<'
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' < arg
	 */
	final public boolean isLessThan (Object arg) {
//		if (arg == null) return false;
		if (arg == this) return false;
		return Value < ByRefDouble.GET_DOUBLE(arg); }

	/** greater Relation: '>'
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' > arg
	 */
	final public boolean isMoreThan (Object arg) {
//		if (arg == null) return false;
		if (arg == this) return false;
		return Value > ByRefDouble.GET_DOUBLE(arg); }

	/** greater or equal: '>='
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' >= arg
	 */
	final public boolean notLessThan (Object arg) {
//		if (arg == null) return false;
		if (arg == this) return  true;
		return Value >= ByRefDouble.GET_DOUBLE(arg); }

	/** less or equal: '<='
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return True, when 'Self' <= arg
	 */
	final public boolean notMoreThan (Object arg) {
//		if (arg == null) return false;
		if (arg == this) return  true;
		return Value <= ByRefDouble.GET_DOUBLE(arg); }

	/** Returns the Position of this Object relative to arg:
	  * This Operation is leaner than compareTo.
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return -1 for this < arg
	  *         +1 otherwise
	  */
	final public int Position(Object arg) {
		if ((arg == null) ||
			(arg == this)) return 1;
		return (Value < ByRefDouble.GET_DOUBLE(arg)) ? -1 : 1; }

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
		if     (Value < (Val = ByRefDouble.GET_DOUBLE(arg))) return -1;
		return (Value >  Val) ? +1 : 0; }

	///////////////////////////////////////////////////////////////////////////////
	//	Interface Object
	///////////////////////////////////////////////////////////////////////////////

	//These are the virtual Methods of Object: they cannot be abstracted int AConst!

	/**Returns the decimal string representation of the wrapped Long Value.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() { return Long.toString(Value); } //"CCountAble(" + Value + ")"; }

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
	public int hashCode() { return (int) Value; }

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
		if (arg == this) { return true; }
		return Value == ByRefLong.TO_LONG(arg); }

}

/**Constant IDeriveAble Class with only three possible Values: 0,1,2,3
	 * <!-- docstate
	 * pass: 2
	 * mtime: 2026-09-05T16:17:38Z
	 * digest: 7f6c4f77ff8928a0a8ca1ec33dafa2e6f873e3e8015d61c8bb3c2c566fae795f
	 * stale: false
	 * tags: [code/enum_modeling, code/numeric_comparison]
	 * concepts: [Comparison Result Enum]
	 * facets: {layer: utility, status: legacy, complexity: low}
	 * -->
  * The Values match the Return Value of the compare() and Position Methods +1.     */
class Comparison
extends CCountAble {

	/** Empty Constructor   */
	protected Comparison() { }

	/** Initializing Constructor   */
	protected Comparison(final byte Value) { super(Value); } //setValue(this, LESS, Value, INCMP); }

}

/**Mutable variant of {@link Comparison}, allowing its Value to be changed after construction.
	 * <!-- docstate
	 * pass: 2
	 * mtime: 2026-09-05T16:17:38Z
	 * digest: b817f34ea179447cb18ae995fd71abd78ba92329d58ac0874bb9371a19b357de
	 * stale: false
	 * tags: [code/numeric_comparison]
	 * concepts: [Comparison Result Holder]
	 * facets: {layer: utility, status: legacy, complexity: low}
	 * -->
  * The Values match the Return Value of the compare() and Position Methods +1.     */
class ByRefComparison
extends Comparison {

	/** Initializing Constructor   */
	public ByRefComparison(final byte Value) { setValue(this, LESS, Value, INCMP); }

	/** setting the Value   */
	public long setValue(final long Value) { return setValue(this, LESS, Value, INCMP); }

}

