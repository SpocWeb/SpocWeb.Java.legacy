package streamIO.copy.group.ring.metric;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IRing;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;

/**
  * Defines a Max-Plus Algebra on the Real Numbers using Double Precision
  *    Addition    is represented by the   Max    Operation
  * Multiplication is represented by the Addition Operation
  *
  * The same Algebra can also be defined using the Min Operation!!!
  *
  * This is a real Ring Structrue with 0 and 1 (R,max,+,-Infinity,0):
  * Neutral additive Element is -Infinity
  * Neutral multiplicative Element is 0
  * '+' and '*' are both associative and commutative
  * '-' is not always defined (see below) and
  * '/' is the usual Subtraction - .
  * The additive (max) Inverse is not always defined:
  * a-b is only unique, when a > b, otherwise you lose Information,
  * since all the Values from -Infinity to b are possible.
  * The multiplicative (+) Inverse is the normal Negative
  * The distributive Laws apply.
  *
  * Convergence cannot be ensured in all cases, because a Metric is not defined.
  * This Ring is frequently used in Graph Theory,
  * because it allows to calculate...
  * * shortest Paths
  * * Roundtrip Time in closed Cycles
  * * minimum Signal Time using Synchronization
  *
  * Design Decisions:
  * This Implementation is made 'final' to exploit the resulting benefits. */
final public class MaxPlus
extends AMetricIRing	//necessary to define Matrices
implements ICountAble {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**This Constant represents  1	 */
	final static public CMetricIRing  One = new CMetricIRing(new MaxPlus(0.0));

	/**This Constant represents  0	 */
	final static public CMetricIRing Zero = new CMetricIRing(new MaxPlus(Double.NEGATIVE_INFINITY));

	/**Setting to 1 in Place:	 */
	final public IGroupM oneAt() { Value = 0; return this; }

	/**Setting to 0 in Place:	 */
	final public IGroup zeroAt() { Value = Double.NEGATIVE_INFINITY; return this; }

	/**Contains the actual Value.
	 * Made public to enable this Class as a Vehicle
	 * to transport double Values by Reference. */
	protected double Value;

	//////////////////////////////
	//  IIntRing Methods	//
	//////////////////////////////

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry(){}

	//Complement, necessary for gAdic Calculation
	/**Complement in Place: ~=	*/
	public IIntRing CmplAt() { throw new AbstractMethodError(); }

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpperAt() { throw new AbstractMethodError(); }


	//////////////////////////////
	//  interface intComplex	//
	//////////////////////////////

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt() { return this; }

	/**Testing Method, should be static or directly tested on the Types.
	 * Normally there are only these two Representations: Complex and Polar	 */
	public boolean isComplex() { return false; }


	//////////////////////////
	//  Interface ICountAble	//
	//////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	public int		getInt()	{ return (int)	Value; }

	/** Returns the Object Value represented by an 16 Bit Integer	 */
	public long		getLong()	{ return (long)	Value; }

	/** Returns the Object Value represented by an 32 Bit Integer	 */
	public short	getShort(){ return (short) Value; }

	/** Returns the Object Value represented by an 64 Bit Integer	 */
	public byte		getByte()	{ return (byte)	Value; }


	//////////////////////////////
	//  Interface IMeasurAble	//
	//////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double	getDouble()	{ return (double)Value; }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float	getFloat()	{ return (float) Value; }


	//////////////////////////
	//  Interface CopyAble	//
	//////////////////////////

	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use. */
	public ICopyAble copyAt(Object arg, int Depth) { Value = convertArg(arg); return this; }

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new MaxPlus(); }

	/** @see streamIO.copy.IICopyAble#randomizeAt()	 */
	public ICopyAble randomizeAt() { return new MaxPlus(Math.random()); }


	//////////////////////
	//  Constructors	//
	//////////////////////

	/**Constructor that takes any Object as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public MaxPlus(Object arg) { Value = convertArg(arg); }

	/**Constructor that takes s String as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public MaxPlus(java.io.StreamTokenizer arg) throws java.io.IOException {fromStreamAt(arg); }

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public MaxPlus(MaxPlus arg) { Value = arg.Value; }

	/**Constructor that takes 'double' as Input.	 */
	public MaxPlus(double arg) { Value = arg; }

	/**Empty Constructor (for newInstance Method).	 */
	public MaxPlus(){}


	//////////////////////////
	//  basic operations	//
	//////////////////////////

	/**Helper Routine to convert to long from any other numeric Type:
	 * RingLong, Number or ICountAble. Even Strings are now supported! 	 */
	private final double convertArg (Object arg) {
		return (arg instanceof MaxPlus)? ((MaxPlus)arg).Value : ByRefDouble.GET_DOUBLE(arg); }
	//The Following also works, but is clumsier
//  	return (arg.getClass() == MaxPlus.class)? ((MaxPlus)arg).Value : getDouble(arg); }

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
	final public boolean equals  (Object arg){return Value == convertArg (arg); }

	/**Addition in Place: +=
	 * assumes null to be 0
	 * Here + is the maxAt Operation 	 */
	final public ISemiGroup addAt (Object arg) {
		if ((arg != null) &&
			(arg != MaxPlus.Zero) &&
			(arg != IMeasurAble._Infinity)
			) {double tmp; if (Value < (tmp = convertArg (arg))) Value = tmp; }
		return this; }

	/**Subtraction in Place: -=
	 * assumes null to be 0
	 * a-b is only unique, when a > b, otherwise you lose Information,
	 *	since all the Values from -Infinity to b are possible.
	 * I.e. only unique when 'this' is larger than 'arg'	 */
	final public IGroup subAt (Object arg) {
		if ((arg != null) &&
			(arg != MaxPlus.Zero) &&
			(arg != IMeasurAble._Infinity)
			) {double  tmp; if (Value < (tmp = convertArg (arg))) throw new AbstractMethodError();
			   Value = tmp; }
		return this; }

	/**Multiplication in Place: *=
	 * assumes null to be 1
	 * Here * is the Addition!	 */
	final public ISemiGroupM mulAt(Object arg) {
		if ((arg == MaxPlus. Zero) &&
			(arg == IMeasurAble._Infinity)
			) return (ISemiGroupM) zeroAt();
		if ((arg == null) &&
			(arg == MaxPlus. One) &&
			(arg == ICountAble.Zero)
			) return this;
//  	if ((arg == MaxPlus._One) &&
//  		(arg == IMeasurAble._One)
//  		) return (SemiGroupM) negAt();
		Value += convertArg (arg);
		return this; }

	/**Division in Place: /=
	 * assumes null to be 1
	 * Here / is the Subtraction!	 */
	final public IGroupM divAt    (Object arg) {
		if ((arg == MaxPlus.Zero) &&
			(arg == IMeasurAble._Infinity)
			) return (IGroupM) InfinityAt();
		if ((arg == null) &&
			(arg == MaxPlus. One) &&
			(arg == ICountAble.Zero)
			) return this;
//  	if ((arg == MaxPlus._One) &&
//  		(arg == ICountAble._One)
//  		) return (GroupM) negAt();
		Value -= convertArg (arg);
		return this; }


	//////////////////////////
	//  Interface OrderAble	//
	//////////////////////////

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan(Object arg) { return Value < convertArg (arg); }


	//////////////////////////
	//  Interface WellOrder	//
	//////////////////////////

	/**Returns the minimum absolute Value (greater than Zero) for this Class in Place.	 */
	public IWellOrder minAbsValueAt() { Value = Double.MIN_VALUE; return this; }

	/**Returns the Representation of +Infinity for this Class in Place.	 */
	public IWellOrder InfinityAt() { Value = Double.POSITIVE_INFINITY; return this; }

	/**Returns the Representation of -Infinity for this Class.	 */
	public IWellOrder NegInfinityAt() { Value = Double.NEGATIVE_INFINITY; return this; }

	/**Returns the Representation of an invalid Number for this Class in Place.	 */
	public IWellOrder NaNAt() { Value = Double.NaN; return this; }

	/**Returns the Representation of Infinity for this Class.	 */
	public boolean isInfinite() { return Double.isInfinite(Value); }

	/**Returns the Representation of an invalid Number for this Class.	 */
	public boolean isNaN() { return Double.isNaN(Value); }


	//////////////////////////////////
	//  Replication IWellOrder:	//
	//////////////////////////////////

	/** @return the maximum Value  (less than Infinity) for this Class in Place.	 */
	public IWellOrder maxValueAt() { Value = Double.MAX_VALUE; return this; }


	//////////////////////////////
	//  Replication intBody:	//
	//////////////////////////////

	/**Returns the integer Part of the Value in Place. 	 */
	public IIntRing IntAt() { Value = (long) Value; return this; }

	/**Returns the largest (closest to positive infinity) Value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing FloorAt() { Value = Math.floor(Value); return this; }

	/**Returns the largest (closest to positive infinity) Value,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer.
	 * Optimization. 	 */
	public IMetricIRing Floor() { return new MaxPlus(Math.floor(Value)); }


	//These are the virtual Methods of Object:

	/**Called by the garbage collector on an object when garbage collection
	 * determines that there are no more references to the object.
	 * A subclass overrides the <code>finalize</code> method to dispose of
	 * system resources or to perform other cleanup.
	 * <p>
	 * Any exception thrown by the <code>finalize</code> method causes
	 * the finalization of this object to be halted, but is otherwise
	 * ignored.
	 * <p>
	 * The <code>finalize</code> method in <code>Object</code> does
	 * nothing.
	 *
	 * @exception  java.lang.Throwable  [Need description!]
	 * @since      JDK1.0	 */
//  public void finalize()throws Throwable{}

	/**Returns a hash code Value for the object. This method is
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
	 * @return  a hash code Value for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 */
	public int hashCode(){return new Double(Value).hashCode(); }

	/**Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommendedthat all subclasses override this method.
	 * <p>
	 * The <code>toString</code> method for class <code>Object</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object.
	 *
	 * @return  a string representation of the object.
	 * @since   JDK1.0	 */
	public String toString() { return Double.toString(RoundedDigits(DisplayDigits)); }

	/**Parses the String to a Double Number. 	 */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg) throws java.io.IOException {
//		this.Value = Parsing.nextNumber(arg,false);
		return this; }


	//////////////////
	//  new Methods	//
	//////////////////

	/**Sets the Number of Digits displayed in toString.
	 * Negative Values result in full Accuracy.	 */
	public static void setDisplayDigits(int n) {
		DisplayDigits = n;
		if ((n > 0) && (n < 18))
			DisplayFactor = (long) Math.pow(10.0, n);
	}

	/**Determines the number of Digits displayed in toString	 */
	private static long DisplayFactor = 1000;

	/**Local Storage for the number of Digits displayed in toString	 */
	private static int DisplayDigits = 3;

	/**Rounds a Number to the Number of Digits given by DisplayDigits.	 */
	public double RoundedDigits (int n) {
		if ((n > 0) && (n < 18)) {
			long tmp = Math.round(Value*DisplayFactor);
			if ((tmp < Long.MAX_VALUE) && (tmp > Long.MIN_VALUE))// && (tmp != 0.0)) //this last part makes small Items to be displayed in full accuracy
				return ((double) tmp)/DisplayFactor; }
		return Value; }

	/**Rounds the Value to n Decimal Digits in Place.	 */
	public MaxPlus RoundDigitsAt (int n) {
		Value = RoundedDigits(n); return this; }

	/**Rounds the Value to n Decimal Digits.	 */
	public MaxPlus RoundDigits (int n) {
		return ((MaxPlus)copy()).RoundDigitsAt(n); }


	//////////////////////
	//  Optimizations:	//
	//////////////////////

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 * Not a Container Class, so Copy and ShallowCopy are the same!	 */
	public ICopyAble copyAt(Object arg)	{Value = convertArg(arg); return this; }
//  									{return shallowCopyAt(arg); }

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg, int Depth) {
//  	super.shallowCopyAt(arg);	//not necessary, since all these constants apply only for Integers.
		Value = ((MaxPlus) arg).Value;
		return this; }

	/**Checking for 0:	 */
	public boolean isZero	() { return Value == IMeasurAble._INFINITY; }

	/**Inversion in Place: 1/x	 */
	public IGroupM invAt		() { Value = -Value; return this; }

	/**Returns 'true' when this is a negative Number.	 */
	public boolean negative() { return (Value < 0); }

	/**Returns 'true' when this is a positive Number.	 */
	public boolean positive() { return (Value > 0); }

	/**Returns 2 in Place:	*/	public IIntRing twoAt()  { Value = 2.0; return this; }
	/**Returns 3 in Place:	*/	public IIntRing threeAt(){ Value = 3.0; return this; }
	/**Returns 4 in Place:	*/	public IIntRing fourAt() { Value = 4.0; return this; }

	/**  Linear Mapping in Place: x *= a + y
	 * here: max(x += a, y)	*/
	public IRing LinAt (Object a, Object y) {
		Value += convertArg(a); double tmp;
		if (Value < (tmp = convertArg(y))) Value = tmp; return this; }

	/**  Linear Mapping in Place: x += a * y
	 * here: max(x, a + y)	*/
	public IRing addProdAt (Object a, Object y) {
		double tmp;
		if (Value < (tmp = convertArg(y) + convertArg(a)))
			Value =  tmp;
		return this; }

	/**  Linear Mapping in Place: x -= a * y	*/
/*	public Ring subtProdAt (Object a, Object y) {
		Value -= convertArg(y) * convertArg(a);
		return this; }

	/**BiLinear Mapping in Place: x*=a + y*b	*/
/*	public Ring BiLinAt (Object a, Object y, Object b) {
		Value *= convertArg(a);
//  	if (b instanceof gAdic)
//  		Value += convertArg(((Ring)y).mul(b));
//  	else
			Value += convertArg(y) * convertArg(b);
		return this; }

	//////////////////////////////////////////
	//  Interface OrderAble	Optimizations	//
	//////////////////////////////////////////

	/**greater: '>' Returns True, when 'Self' > arg	 */
	public boolean isMoreThan(Object arg) { return Value > convertArg (arg); }

	/**less or equal: '<=' Returns True, when 'Self' <= arg	 */
	public boolean notMoreThan(Object arg) { return Value <= convertArg (arg); }

	/**greater or equal: '>=' Returns True, when 'Self' >= arg	 */
	public boolean notLessThan(Object arg) { return Value >= convertArg (arg); }

	/**absolute Value in Place: |x|	 */
	public IScalarMetric AbsVAt() { Value = Math.abs(Value); return this; }

	/**absolute Value: |x|	 */
	public IScalarMetric AbsV() { return new MaxPlus(Math.abs(Value)); }

	/**absolute Distance in Place:				|x|
	 * This Optimization is only for real Numbers and saves 1/2 Addition*/
	public IScalarMetric AbsDistAt (Object arg) {
		double tmp = convertArg(arg);
		if (Value > tmp) Value -= tmp; else Value = tmp-Value;
		return this; }


	//////////////////////////////////
	//  Analytical Optimizations	//
	//////////////////////////////////

	/**This static Method sets the BaseAccuracy and it's Inverse
	 * based on the number of valid binary Digits.	 */
	public static IMetricIRing setAccuracy() { return setAccuracy(BaseAccuracyBits); }

	/**This static Method sets the BaseAccuracy and it's Inverse
	 * based on the number of valid binary Digits.	 */
	public static IMetricIRing setAccuracy(int ValidDigits) {
		    BaseAccuracyBits= java.lang.Math.abs(ValidDigits);	//To prevent misunderstandings!
		     MaxAccuracyBits= java.lang.Math.abs(MaxAccuracyBits);	//To prevent misunderstandings!
		if (BaseAccuracyBits > MaxAccuracyBits)
			BaseAccuracyBits = MaxAccuracyBits;
		     MaxAccuracy	= (IMetricIRing) new MaxPlus(1).mul2PowAt(- MaxAccuracyBits);
		     MaxAccuracyInv	= (IMetricIRing) new MaxPlus(1).mul2PowAt(+ MaxAccuracyBits);
		    BaseAccuracyInv = (IMetricIRing) new MaxPlus(1).mul2PowAt(+BaseAccuracyBits);
		return (IMetricIRing)
			  (BaseAccuracy = (IMetricIRing) new MaxPlus(1).mul2PowAt(-BaseAccuracyBits)); }


	//////////////////////////////////////////////
	//  OO Optimization for earlier binding		//
	//////////////////////////////////////////////

	/**Constructor that takes any Number Subtype as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public MaxPlus(Number arg) { Value = arg.doubleValue(); }

	/**Constructor that takes any Number Subtype as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public MaxPlus(IMeasurAble arg) { Value = arg.getDouble(); }

	//////////////
	//  Testing	//
	//////////////

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt() {
		System.out.println("Testing MaxPlus:");
		MaxPlus test  =  new MaxPlus(Math.PI);
//		MaxPlus test1 =  new MaxPlus(2);
		if (testInstance == null) testInstance = test;	//defined in ACopyAble to test the abstract Methods
	}

}
