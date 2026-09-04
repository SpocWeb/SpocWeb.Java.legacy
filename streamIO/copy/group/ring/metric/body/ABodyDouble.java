package streamIO.copy.group.ring.metric.body;

import java.io.IOException;
import java.io.StreamTokenizer;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IRing;
import streamIO.copy.group.ring.Interpolator;
import streamIO.copy.group.ring.integer;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import streamIO.copy.group.ring.metric.IWellOrder;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.order.IOrder;
import function.ACountAble;
import function.AOrderAble;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.CCountAble;

/** Concrete, optimized Wrapper-Class for scalar double Precision 
 * Float Point Types to define a Metric Body.
 * defines all the Operations non finally to allow for Extension
 *
 * Subclasses:
 * @see BodyDouble
 * @see AQuantityDouble
 *
 * Double-Properties:
 * Bits:     64 = 8*8Byte
 * Mantissa: 52 = 8*6Byte + 4 Bit	=> 53 Bits ^ 16 Digits Accuracy
 * Exponent: 11 = 8*1Byte + 3 Bit	=> 11 Bits ^ +/- 308 Exponent
 * Sign:      1 =           1 Bit
 *
 * Design Decisions:
 * This Implementation is made 'final' to exploit the resulting benefits. 
 */
public class ABodyDouble //
extends AMetricBody
implements ICountAble, IMeasurAble {

//	double r = Math.IEEEremainder();	//cannot be used, for the same reason as rint
//	double v = Math.random();
//	double x = Math.rint();	//cannot be used, because it rounds to the nearest even integer number
//	int y = Math.round();//or long

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**This Constant represents  1	 */
	final static public CBody  One = new CBody(new ABodyDouble(+1));

	/**This Constant represents -1	 */
	final static public CBody _One = new CBody(new ABodyDouble(-1));

	/**This Constant represents  0	 */
	final static public CBody Zero = new CBody(new ABodyDouble( 0));

	/**Contains the actual Value.
	 * Made public to enable this Class as a Vehicle
	 * to transport double Values by Reference. */
	public double value;

	/**Number of Bits for the double Numbers	 */
	final static public int  ExponentBit  = 52;

	/**Mask for the double Numbers	 */
	final static public long ExponentMask = 1l << ExponentBit;


	//////////////////////////////
	//	IIntRing Methods
	//////////////////////////////

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry(){}

	//Complement, necessary for gAdic Calculation
	/**Complement in Place: ~=	*/
	public IIntRing CmplAt(){throw new AbstractMethodError(); }

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpperAt(){throw new AbstractMethodError(); }


	///////////////////////////////////////////////////////////////////////////
	//	interface intComplex
	///////////////////////////////////////////////////////////////////////////

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt(){ return this; }

	/**Testing Method, should be static or directly tested on the Types.
	 * Normally there are only these two Representations: Complex and Polar	 */
	public boolean isComplex(){return false; }


	///////////////////////////////////////////////////////////////////////////
	//	Interface ICountAble
	///////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	public int getInt() { return ACountAble.getInt(value); }

	/** Returns the Object Value represented by an 16 Bit Integer	 */
	public long getLong() { return ACountAble.getLong(value); }

	/** Returns the Object Value represented by an 32 Bit Integer	 */
	public short getShort()	{ return ACountAble.getShort(value); }

	/** Returns the Object Value represented by an 64 Bit Integer	 */
	public byte getByte() { return ACountAble.getByte(value); }


	///////////////////////////////////////////////////////////////////////////
	//	Interface IMeasurAble
	///////////////////////////////////////////////////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double	getDouble()	{ return (double)value; }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float	getFloat()	{ return (float) value; }


	///////////////////////////////////////////////////////////////////////////
	//	Interface CopyAble
	///////////////////////////////////////////////////////////////////////////

	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use. */
	public ICopyAble copyAt(Object arg, int Depth){ value = convertArg(arg); return this; }

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new ABodyDouble(); }

	/** @see streamIO.copy.IICopyAble#randomizeAt()	 */
	public ICopyAble randomizeAt() { return new ABodyDouble(ByRefDouble.RANDOM_1_1()); } 

	///////////////////////////////////////////////////////////////////////////
	//	Constructors
	///////////////////////////////////////////////////////////////////////////

	/**Constructor that takes any Object as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public ABodyDouble(Object arg){value = convertArg(arg); }

	/**Constructor that takes s String as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public ABodyDouble(StreamTokenizer arg) throws IOException {fromStreamAt(arg); }

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public ABodyDouble(ABodyDouble arg){value = arg.value; }

	/**Constructor that takes 'double' as Input.	 */
	public ABodyDouble(double arg){value = arg; }

	/**Empty Constructor (for newInstance Method).
	 * Does not create Dummy Objects for it's Constituents.
	 * So those Objects are not well-defined, but contain Null Pointers.	 */
	public ABodyDouble(){}


	///////////////////////////////////////////////////////////////////////////
	//	basic operations
	///////////////////////////////////////////////////////////////////////////

	/**Helper Routine to convert to long from any other numeric Type:
	 * RingLong, Number or ICountAble. Even Strings are now supported! 	 */
	private static final double convertArg (Object arg) {
		if (arg instanceof ABodyDouble) {
			return ((ABodyDouble)arg).value; } 
		return ByRefDouble.GET_DOUBLE(arg); }

	public static boolean COMPARE_EXACT = false; 

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
	final public boolean equals(Object arg){
		if (COMPARE_EXACT) { 
			return value == convertArg (arg); 
		}
		return ByRefDouble.EQUALS(value, convertArg (arg)); 
	}

	/**Addition in Place: +=
	 * assumes null to be 0	 
	 * Operation in Place should be defined more loosely 
	 * in that it may decide not to perform the Operation in Place!
	 */
	final public ISemiGroup addAt (Object arg) {
		if ((arg != ABodyDouble.Zero) &&
			(arg != CCountAble.Zero) &&
			(arg !=  ICountAble.Zero) &&
			(arg != null) ) {
				final double argVal = convertArg(arg); 
				if (Double.isNaN(argVal)) {
					if (arg instanceof ISemiGroup) { //a+b == b+a
						if (Double.isNaN(value)) { //prevent infinite Recursion
							return this; }
						return ((ISemiGroup) arg).add(this); //Target Type knows best!
					} //adding a Scalar is always commutative
				}
			value += argVal;
		}  
		return this; }

	/**Subtraction in Place: -=
	 * assumes null to be 0	 */
	final public IGroup subAt(Object arg) {
		if ((arg != ABodyDouble.Zero) &&
			(arg != CCountAble.Zero) &&
			(arg !=  ICountAble.Zero) &&
			(arg != null) ) {
				final double argVal = convertArg(arg); 
				if (Double.isNaN(argVal)) { //a-b == -(b-a)
					if (arg instanceof IGroup) { //adding with a Scalar is always commutative
						if (Double.isNaN(value)) { //prevent infinite Recursion
							return this; }
						return ((IGroup) arg).sub(this).negAt(); //Target Type knows best
					} 
				}
			value -= argVal;
		} 
		return this; }

	/**Multiplication in Place: *=
	 * assumes null to be 1	 */
	final public ISemiGroupM mulAt(Object arg) {
		if ((arg == null) &&
			(arg == ABodyDouble. One) ||
			(arg ==  CCountAble. One) ||
			(arg ==  ICountAble. One)
			) { return this; } 
		if ((arg == ABodyDouble.Zero) ||
			(arg ==  CCountAble.Zero) ||
			(arg ==  ICountAble.Zero)
			) { zeroAt(); return this; }
		if ((arg == ABodyDouble._One) ||
			(arg ==  CCountAble._One) ||
			(arg ==  ICountAble._One)
			) { negAt(); return this; }
		final double argVal = convertArg(arg); 
		if (Double.isNaN(argVal)) { //multiplication with a Scalar  
			if (arg instanceof ISemiGroupM) { //is always commutative
				if (Double.isNaN(value)) { //prevent infinite Recursion
					return this; }
				return ((ISemiGroupM) arg).mul(this);  //Target Type knows best
			} //just return the commuted Product!
		}
		value *= argVal;
		return this; }

	/**Division in Place: /=
	 * assumes null to be 1	 */
	final public IGroupM divAt(final Object arg) {
		if ((arg == null) &&
			(arg == ABodyDouble. One) ||
			(arg == CCountAble. One) ||
			(arg ==  ICountAble. One)
			) return this;
		if ((arg == ABodyDouble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg ==  ICountAble.Zero)
			) { InfinityAt(); return this; }
		if ((arg == ABodyDouble._One) ||
			(arg == CCountAble._One) ||
			(arg ==  ICountAble._One)
			) { negAt(); return this; }
		final double argVal = convertArg(arg); 
		if (Double.isNaN(argVal)) { //possible Recursion here! 
			if (arg instanceof ISemiGroupM) { //synchronized(this) {
				if (Double.isNaN(value)) { //prevent infinite Recursion
					return this; }
				return ((IGroupM) arg).div(this).invAt(); //Target Type knows best
			/*	value = 1/value; //temp. invert Value... not good!
				final SemiGroupM ret = ((SemiGroupM) arg).mul(this); 
				value = 1/value; //undo temp. Change
				return (GroupM) ret; //Target Type knows best
			*/} //multiplication with a Scalar is always commutative
		}
		value /= argVal;
		return this; }

	/**Setting to 0 in Place:	 */
	final public IGroup zeroAt () { value = 0; return this; }

	/**Setting to 1 in Place:	 */
	final public IGroupM oneAt () { value = 1; return this; }


	//////////////////////////
	//	Interface OrderAble	//
	//////////////////////////

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan(Object arg) { return value < convertArg (arg); }


	//////////////////////////
	//	Interface WellOrder	//
	//////////////////////////

	/**Returns the minimum absolute Value (greater than Zero) for this Class in Place.	 */
	public IWellOrder minAbsValueAt() { value = Double.MIN_VALUE; return this; }

	/**Returns the Representation of +Infinity for this Class in Place.	 */
	public IWellOrder InfinityAt() { value = Double.POSITIVE_INFINITY; return this; }

	/**Returns the Representation of -Infinity for this Class.	 */
	public IWellOrder NegInfinityAt() { value = Double.NEGATIVE_INFINITY; return this; }

	/**Returns the Representation of an invalid Number for this Class in Place.	 */
	public IWellOrder NaNAt() { value = Double.NaN; return this; }

	/**Returns the Representation of Infinity for this Class.	 */
	public boolean isInfinite() { return Double.isInfinite(value); }

	/**Returns the Representation of an invalid Number for this Class.	 */
	public boolean isNaN() { return Double.isNaN(value); }


	//////////////////////////////////
	//	Replication intWellOrder:	//
	//////////////////////////////////

	/**Returns the maximum Value  (less than Infinity) for this Class in Place.	 */
	public IWellOrder maxValueAt() { value = Double.MAX_VALUE; return this; }


	//////////////////////////////
	//	Replication IBody:	//
	//////////////////////////////

	/**Returns the largest (closest to positive infinity) Value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IIntRing IntAt()	{ value = (long) value; return this; }

	/**Returns the largest (closest to positive infinity) Value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing FloorAt()	{ value = Math.floor(value); return this; }

	/**Returns the largest (closest to positive infinity) Value,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer.
	 * Optimization. 	 */
	public IMetricIRing Floor()	{ return new ABodyDouble(Math.floor(value)); }


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
//	public void finalize()throws Throwable{}

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
	public int hashCode() {
		return (int)Double.doubleToLongBits(value); }
//		long bits = Double.doubleToLongBits(Value);
//		return (int)(bits ^ (bits >> 32)); }
//		return new Double(Value).hashCode(); }

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
	public String toString() { 
		return Double.toString(RoundedDigits(value, DisplayDigits)); }

	/**Parses the String to a Double Number. 	 */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg) throws java.io.IOException {
//TODO		this.Value = Parsing.nextNumber(arg, false);
//		this.Value = Double.valueOf(arg).getDouble();
		return this; }


	//////////////////
	//	new Methods	//
	//////////////////

	/**Sets the Number of Digits displayed in toString.
	 * Negative Values result in full Accuracy.	 */
	public static void setDisplayDigits(int n) {
		DisplayDigits = n;
		if ((n > 0) && (n < 18))
			DisplayFactor = (long) Math.pow(10.0, n); }

	/**Determines the number of Digits displayed in toString	 */
	private static long DisplayFactor = 100000000;

	/**Local Storage for the number of Digits displayed in toString	 */
	private static int DisplayDigits = 8;

	/**Rounds a Number to the Number of Digits given by DisplayDigits.
	 * @see AOrderAble	 */
	public static double RoundedDigits (double Value, int n) {
		if ((n > 0) && (n < 18)) {
			long tmp = Math.round(Value*DisplayFactor);
			if ((tmp < Long.MAX_VALUE) && (tmp > Long.MIN_VALUE))// && (tmp != 0.0)) //this last part makes small Items to be displayed in full accuracy
				return ((double) tmp)/DisplayFactor; }
		return Value; }

	/**Rounds the Value to n Decimal Digits in Place.	 */
	public ABodyDouble RoundDigitsAt (int n) {
		value = RoundedDigits(value, n); return this; }

	/**Rounds the Value to n Decimal Digits.	 */
	public ABodyDouble RoundDigits (int n) {
		return ((ABodyDouble)copy()).RoundDigitsAt(n); }


	//////////////////////
	//	Optimizations:	//
	//////////////////////

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 * Not a Container Class, so Copy and ShallowCopy are the same!	 */
	public ICopyAble copyAt(Object arg)	{value = convertArg(arg); return this; }
//										{return shallowCopyAt(arg); }

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg, int Depth)
	{
//		super.shallowCopyAt(arg);	//not necessary, since all these constants apply only for Integers.
		value = ((ABodyDouble) arg).value;
		return this;
	}

	/**Checking for 0:	 */
	public boolean isZero	(){return value == 0.0; }

	/**Negation in Place: -	 */
	public IGroup negAt		(){value = -value;return this; }

	/**Inversion in Place: 1/x	 */
	public IGroupM invAt		(){value = 1.0 / value; return this; }

	//2.147.483.648 instead of 3.037.000.499 = 2.147.483.648 * SqRt(2)
	/**Maximum Value before e.g. Sqr creates an Overflow.
	 * Important for Fractions when rounding.	 */
	public IMetricIRing SqRtMaxValueAt(){value = Math.sqrt(Double.MAX_VALUE); return this; }

	/**Decrement: x--	 */		public integer dec(){value--; return this; }
	/**Increment: x++	 */		public integer inc(){value++; return this; }
	/**Residual in Place: 1-x	 */
	public integer ResidAt(){value = 1.0 - value; return this; }

	/**Returns true, when this is divisible by 2 (even Number).	 */
	public boolean isEven(){return (((long)value) & 1) == 0; }

	/**Returns true, when this is not divisible by 2 (odd Number).	 */
	public boolean isOdd (){return (((long)value) & 1) != 0; }

	/**Modulo in Place: %=	*/
	public IIntRing ModlAt(Object arg){value %= convertArg (arg);return this; }

	/**Returns 'true' when this is a negative Number.	 */
	public boolean negative(){return (value < 0); }

	/**Returns 'true' when this is a positive Number.	 */
	public boolean positive(){return (value > 0); }

	/**Returns the Constant Pi = 3.14159265359... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt(){value = IMeasurAble.PI; return this; }

	//These Constants allow for Optimizations

	/**Returns -1:	*/	public IIntRing _one() {return _One; }
	/**Returns  0:	*/	public IGroup		 zero() {return Zero; }
	/**Returns  1:	*/	public IGroupM		  one() {return  One; }
//	/**Returns  2:	*/	public IIntRing  two() {return  Two; }

	/**Returns-1 in Place:	*/	public IIntRing  _oneAt(){value =-1.0; return this; }
	/**Returns 2 in Place:	*/	public IIntRing   twoAt(){value = 2.0; return this; }
	/**Returns 3 in Place:	*/	public IIntRing threeAt(){value = 3.0; return this; }
	/**Returns 4 in Place:	*/	public IIntRing  fourAt(){value = 4.0; return this; }

	/**Returns 1/2 in Place: 0.5	 */	public IIntRing OneHalfAt   () { value = 0.5; return this; }
	/**Returns 1/3 in Place: 0.33..	 */	public IIntRing OneThirdAt  () { value = 0.3333333333333333333; return this; }
	/**Returns 1/4 in Place: 0.25	 */	public IIntRing OneQuarterAt() { value = 0.25; return this; }

	/**Returns the Constant e = exp(1) = 2,7182818284590452353602874713527... in Place	 */
	public MetricBody eAt() { value = IMeasurAble.E; return this; }

	/**Returns the Constant lb(10) = 1/lg(2) = 3,3219280948873623478703194294894... in Place	 */
	public MetricBody lb10At() { value = IMeasurAble.LB10; return this; }

	/**Returns the Constant ln(2) = 1/lb(e) = 0,69314718055994530941723212145818... in Place	 */
	public MetricBody ln2At() { value = IMeasurAble.LN2; return this; }

	/**Returns the Constant lb(e) = 1/ln(2) = 1,4426950408889634073599246810019... in Place	 */
	public MetricBody lbeAt() { value = IMeasurAble.LBE; return this; }

	/**Returns the Constant lg(2) = 1/lb(10) = 0,30102999566398119521373889472449... in Place	 */
	public MetricBody lg2At() { value = IMeasurAble.LG2; return this; }

	/**Returns the Constant 2*Pi = 6,283185307179586476925286766559... in Place
	 * This is the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody twoPiAt() { value = IMeasurAble.TWO_PI; return this; }

	/**Returns the Constant Pi/2 = 1,5707963267948966192313216916398... in Place	 */
	public MetricBody piHalfAt() { value = IMeasurAble.PI_HALF; return this; }

	/**Returns the Constant Pi/4 = 0,78539816339744830961566084581988... in Place	 */
	public MetricBody piQuarterAt() { value = IMeasurAble.PI_QUARTER; return this; }



	/**  Linear Mapping in Place: x *= a + y	*/
	public IRing LinAt (Object a, Object y)
	{value*=convertArg(a); value += convertArg(y);return this; }

	/**  Linear Mapping in Place: x += a * y	*/
	public IRing addProdAt (Object a, Object y)
	{
//		if (b instanceof gAdic)
//			Value += convertArg(((Ring)y).mul(a));
//		else
			value += convertArg(y) * convertArg(a);
		return this;
	}

	/**  Linear Mapping in Place: x -= a * y	*/
	public IRing subtProdAt (Object a, Object y)
	{
		value -= convertArg(y) * convertArg(a);
		return this;
	}

	/**BiLinear Mapping in Place: x*=a + y*b	*/
	public IRing BiLinAt (Object a, Object y, Object b)
	{
		value *= convertArg(a);
//		if (b instanceof gAdic)
//			Value += convertArg(((Ring)y).mul(b));
//		else
			value += convertArg(y) * convertArg(b);
		return this;
	}

	//////////////////////////////////////////
	//	Interface OrderAble	Optimizations	//
	//////////////////////////////////////////

	/**greater: '>' Returns True, when 'Self' > arg	 */
	public boolean isMoreThan(Object arg){return value > convertArg (arg); }

	/**less or equal: '<=' Returns True, when 'Self' <= arg	 */
	public boolean notMoreThan(Object arg){return value <= convertArg (arg); }

	/**greater or equal: '>=' Returns True, when 'Self' >= arg	 */
	public boolean notLessThan(Object arg){return value >= convertArg (arg); }

	/**absolute Value in Place: |x|	 */
	public IScalarMetric AbsVAt(){value = Math.abs(value); return this; }

	/**absolute Value: |x|	 */
	public IScalarMetric AbsV(){return new ABodyDouble(Math.abs(value)); }

	/**absolute Distance in Place:				|x|
	 * This Optimization is only for real Numbers and saves 1/2 Addition*/
	public IScalarMetric AbsDistAt			(Object arg)
	{
		double tmp = convertArg(arg);
		if (value > tmp) value -= tmp; else value = tmp-value;
		return this;
	}

	/**Double in Place: x+=x	*/
/*	public SemiGroup dblAt	   ()	//Addition to itself is faster and always safe!
	{	//Check for over / underflow!
		if (Value == 0.0) return this;
		Value = Double.longBitsToDouble(Double.doubleToLongBits (Value) + ExponentMask);
		return this;
	}
*/
	/**	Returns x/2 in Place:	 */
	public IIntRing halfAt()
	{
		if (value == 0.0) return this;
		value = Double.longBitsToDouble(Double.doubleToLongBits (value) - ExponentMask);
		return this;
	}


	/**Multiplication with an Integer Power of 2 in Place:	 */
	public ISemiGroup mul2PowAt(int n)
	{
		if (value == 0.0) return this;
		long ln = n;
		long Val = Double.doubleToLongBits (value);
		long nVal = Val + (ln << ExponentBit);
		if (bolLazySimplify)
			value = Double.longBitsToDouble(nVal);
		else
		{
			if ((Val > 0) ^ (nVal > 0))	//Overflow or Underflow
			{
				if		(n < 0)		value = 0.0;	//Unterlauf, not signed
				else if (Val > 0)	value = Double.POSITIVE_INFINITY; //Überlauf
				else				value = Double.NEGATIVE_INFINITY; //Überlauf
			}
		}
		return this;
	}

	//////////////////////////////////
	//	Analytical Optimizations	//
	//////////////////////////////////

	//It is necessary to redefine most of the analytical Functions in both Ways,
	//e.g. sin() and sinAt(), because they are mostly defined by sin()
	//and the Operation sin()= copyAt().sinAt() is not defined
	//and even is more overhead than defining it directly here!

	/**Returns the natural Logarithm of x in Place: ln(x)
	 * This is the Inverse to the exponential Function exp(x).
	 * For Arguments x near 1 use lnXP1(x) to gain Accuracy.	 */
	public MetricBody lnAt(){ value = Math.log(value); return this; }	//ln() uses lnAt() in AMetricBody

	/**Returns the exponential Function in Place: e^x
	 * This is the Inverse to the natural Logarithm ln().
	 * For small Arguments |x| use expM1At(x) to gain Accuracy.	 */
	public MetricBody expAt()	//exp uses expAt() in AMetricBody.
	{	//rely on fast evaluation to save this test
		if (bolLazySimplify & Double.isInfinite(value))
		{	//Java VM is not able to handle exp(+- Infinity) !
			if (value < 0) 
				value = 0;
//			else return this;	//done below already
		}
		else	
			value = Math.exp(value);
		return this;
	}

	/**Returns the Square Root of this in Place: x^=.5	 */
	public IMetricIRing SqRtAt(){value = Math.sqrt(value); return this; }

	/**Returns the Square Root of this: x^=.5	 */
	public IMetricIRing SqRt(){return new ABodyDouble(Math.sqrt(value)); }

	/**Returns the Arcus Sinus of the Angle x in Place: ArcSin(x)	 */
	public MetricBody ArcSinAt(){value = Math.asin(value); return this; }

	/**Returns the Arcus Sinus of the Angle x: ArcSin(x)	 */
	public MetricBody ArcSin(){return new ABodyDouble(Math.asin(value)); }

	/**Returns the Arcus Cosinus of the Angle x in Place: ArcCos(x)	 */
	public MetricBody ArcCosAt(){value = Math.acos(value); return this; }

	/**Returns the Arcus Cosinus of the Angle x: ArcCos(x)	 */
	public MetricBody ArcCos() { return new ABodyDouble(Math.acos(value)); }

	/** Sinc-Function :      Sin  (x)/x
	 * @return Sinc(x) = Sin(x)/x
	 */
	public MetricBody Sinc() { //already copied
		if (isZero()) return (MetricBody) one(); //ICountAble.ONE;
		MetricBody ret = sin(); ret.divAt(this);
		return ret; }

	/** Airy-Function with Finesse F == 1: 1/(1+Sin^2)
	 * Fastest Implementation with Airy Characteristics, not normed
	 * @return Airy(x) = 1/(1+sin^2(x))
	 */
	public MetricBody Airy() { //already copied
		MetricBody ret = sin(); ret.sqrAt(); ret.inc(); ret.invAt();
		return ret;	}

	/** Airy-Function with Finesse F: 1/(1+F*Sin^2 (x/2))
	 * when the Finesse is null, it is assumed to 1.
	 * @param arg any Number or MetricIRing
	 * @param F Finesse of the System
	 * @return Airy(x, F) = 1/(1+F*sin^2(x))
	 */
	public MetricBody Airy(Object F) { //already copied
		MetricBody x_2 = (MetricBody) half();
		if (F == null) 
			return Airy(x_2);
		x_2.sinAt().sqrAt().mulAt(F); x_2.inc(); x_2.invAt();
		return x_2;	}

	/**Returns the Sinus of the angle x in Place: sin(x)	 */
//	public MetricBody sinAt() { Value = Math.sin(Value); return this; }

	/**Returns the Sinus of the angle x: sin(x)	 */
//	public MetricBody sin  () { return new ABodyDouble(Math.sin(Value)); }

	/**Returns the Cosinus of the angle x in Place: cos(x)	 */
	public MetricBody cosAt() { value = Math.cos(value); return this; }

	/**Returns the Cosinus of the angle x: cos(x)	 */
	public MetricBody cos  () {  
		return new ABodyDouble(Math.cos(value)); }

	/**Returns the Tangens of the angle x in Place: tan == sin / cos == sin/(1-sin^2)^1/2	*/
	public MetricBody tanAt() { value = Math.tan(value); return this; }

	/**Returns the Tangens of the angle x in Place: tan == sin / cos == sin/(1-sin^2)^1/2	*/
	public MetricBody tan  () { return new ABodyDouble(Math.tan(value)); }

	/**Returns the Maximum of both Operands	 */
	public IOrder Max (Object arg) { return new ABodyDouble(Math.max(value, convertArg(arg))); }

	/**Returns the Maximum of both Operands in Place	 */
	public IOrder MaxAt (Object arg) { value = Math.max(value, convertArg(arg)); return this; }

	/**Returns the Minimum of both Operands	 */
	public IOrder Min (Object arg){return new ABodyDouble(Math.min(value, convertArg(arg))); }

	/**Returns the Minimum of both Operands in Place	 */
	public IOrder MinAt (Object arg){value = Math.min(value, convertArg(arg)); return this; }

	/**Returns this number raised to the Power of arg in Place: this^=arg
	 * For small Arguments |x| use PowM1(arg) to gain Accuracy.	 */
	public MetricBody PowAt(Object arg){value = Math.pow(value, convertArg(arg)); return this; }

	/**Returns this number raised to the Power of arg: this^arg
	 * For small Arguments |x| use PowM1(arg) to gain Accuracy.	 */
	public MetricBody Pow(Object arg)
	{return new ABodyDouble (Math.pow(value, convertArg(arg))); }

	/**Returns the angle in the full Range of -pi to pi
	 * that is given by the two coordinated x and y in Place.
	 * The Condition x^2+y^2 = 1 needn't be fulfilled.	 */
	public MetricBody ArcTgAt (Object x){value = Math.atan2(value, convertArg(x)); return this; }

	/**Returns the angle in the full Range of -pi to pi
	 * that is given by the two coordinated x and y.
	 * The Condition x^2+y^2 = 1 needn't be fulfilled.	 */
//	public MetricBody ArcTg (Object x)
//	{return new ABodyDouble (Math.atan2(Value, convertArg(x))); }

	/**Returns the Arcus Tangens of the Angle x: ArcTan(x)	 */
//	public MetricBody ArcTan()	{return new ABodyDouble (Math.atan(Value)); }

	/**Returns the Arcus Tangens of the Angle x in Place: ArcTan(x)	 */
	public MetricBody ArcTanAt()	{value = Math.atan(value); return this; }

	/**Returns the smallest (closest to negative infinity) value in Place,
	 * that is not less than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing CeilAt(){value = Math.ceil(value); return this; }

	/**Returns the smallest (closest to negative infinity) value,
	 * that is not less than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing Ceil(){return new ABodyDouble (Math.ceil(value)); }

	/**Returns the closest Integer to the argument in Place
	 * (as far as accuracy allows). 	 */
	public IMetricIRing roundAt(){value = Math.rint(value); return this; }

	//	Accuracy is now set in AMetricIRing	//

	/**This static Method sets the BaseAccuracy and it's Inverse
	 * based on the number of valid binary Digits.	 */
//	public void setAccuracy() { setAccuracyBits(BaseAccuracyBits); } //could be static

	/**This static Method sets the BaseAccuracy and it's Inverse
	 * based on the number of valid binary Digits.	 */
/*	public static MetricIRing setAccuracy(int ValidDigits) {
		    BaseAccuracyBits= java.lang.Math.abs(ValidDigits);	//To prevent misunderstandings!
		     MaxAccuracyBits= java.lang.Math.abs(MaxAccuracyBits);	//To prevent misunderstandings!
		if (BaseAccuracyBits > MaxAccuracyBits) BaseAccuracyBits = MaxAccuracyBits;
		     MaxAccuracy	= (MetricIRing) new ABodyDouble(1).mul2PowAt(- MaxAccuracyBits);
		     MaxAccuracyInv	= (MetricIRing) new ABodyDouble(1).mul2PowAt(+ MaxAccuracyBits);
		    BaseAccuracyInv = (MetricIRing) new ABodyDouble(1).mul2PowAt(+BaseAccuracyBits);
		return (MetricIRing)
			  (BaseAccuracy = (MetricIRing) new ABodyDouble(1).mul2PowAt(-BaseAccuracyBits));
	}
*/
	/**Constructor building the Interpolation Polynom
	 * from the Samples given in x and y.	 */
	public static Interpolator Interpolator(double [] x_, double [] y_, int Length) {
		if (Length > y_.length) Length = y_.length;
		Interpolator tmp = new Interpolator(Length);
		int i = -1;	//i == mDim!
		while (++i < Length) tmp.addPoint(new ABodyDouble (x_[i]), new ABodyDouble (y_[i]));
		return tmp;
	}

	//////////////////////////////////////////////
	//	OO Optimization for earlier binding		//
	//////////////////////////////////////////////

	/**Constructor that takes any Number Subtype as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public ABodyDouble(Number arg){value = arg.doubleValue(); }

	/**Constructor that takes any Number Subtype as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public ABodyDouble(IMeasurAble arg){value = arg.getDouble(); }

	//////////////
	//	Monoid	//
	//////////////

	/**Returns this=°arg  ==  this(arg) 	*/
//	public SemiMonoid mapAt(SemiMonoid arg) { arg.copyAt (this); return arg; }

	//////////////
	//	Testing	//
	//////////////

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt() {
		System.out.println("Testing ABodyDouble:");
		ABodyDouble test  =  new ABodyDouble(Math.PI);
//		ABodyDouble test1 =  new ABodyDouble(2);
		if (testInstance == null) testInstance = test;	//defined in ACopyAble to test the abstract Methods

	}

}
