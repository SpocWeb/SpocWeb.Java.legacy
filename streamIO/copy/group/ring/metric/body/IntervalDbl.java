package streamIO.copy.group.ring.metric.body;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.integer;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import streamIO.copy.group.ring.metric.IWellOrder;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.CCountAble;

/**
  * An ordered Set of left and right Coordinates allows for a faster
  * test for the Position of an Item relative to the Interval.
  * Additionally arithmetic Operations become possible.
  * But that is class IntervalDbl directly derived from AMetricBody
  * to inherit all of the Functionality of the abstract Body.
  * 
  * Intervals can also be seen as simplistic Probability Distributions 
  * or as Fuzzy Numbers, depending on their Usage: 
  * Fuzzy Numbers are used for Categorization 
  * Distributions are used for statistical Analysis 
  * 
  * absolute Errors add on add/sub 
  * relative Errors add on mul/div 
  * 
  * 
  * This Class fosters the projective Model of Infinity.
  * This allows capturing Inverse Intervals with the same concise Model:
  * I = [a,b]
  * with a < b contains all numbers between a and b (the Constructor allows only these)
  * with a > b contains all numbers except for those between a and b
  *
  * Constructing the Interval now is sensitive to the Order of Arguments!
  *
  * It has some new Methods: additional to equals() 
  * it also defines overlaps() and contains()
  *
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalDblA
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalDbl
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalA
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalP
  * are derived from AMetricBody to define arithmetic Operations
  *
  * @see streamIO.Copy.IOrder.Interval
  * @see streamIO.Copy.IOrder.IntervalOrd
  * are minimal Definitions for Intervals
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:00:22Z
  * digest: 1c2f311a0b9648b33da42f286f0da1dc609ae72480b0975a486718663020daaf
  * stale: false
  * tags: [code/rational_numbers, code/interval_arithmetic]
  * concepts: [Rational Numbers and Interval Arithmetic]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class IntervalDbl
	extends AMetricBody {
//	extends Order.Interval {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Coordinate of the left Border	 */
	protected double Left;

	/**Coordinate of the right Border	 */
	protected double Right;

	/**Constructor without any Arguments, should be initialized with +/- Infinity	 */
	public IntervalDbl() {
		Left  = Double.NEGATIVE_INFINITY;
		Right = Double.POSITIVE_INFINITY; }

	/**Defining Constructor.
	 * This Constructor allows only real Intervals, not inverted ones.	 */
	public IntervalDbl(double left, double right) {
//		if (left > right) //throw new AbstractMethodError(); //
//				{ Left = right; Right = left ; } //
//		else
				{ Left = left ; Right = right; }
		}

	//////////////////////////
	//	Interface OrderAble	//
	//////////////////////////

	/**Returns true when 'this' is similar to arg,
	 * i.e. this is about the same as arg.
	 * The usual Criterion is that |this-arg| <= Accuracy*(|this| + |arg|)
	 * Here Intervals are tested whether they overlap:	 */
	public boolean isSimilar(IMetricIRing arg) {
		return overlaps(arg);
//		return AbsV_Dist(arg).less( ((MetricIRing)
//									((MetricIRing)AbsV()).addAt(arg.AbsV())).mulAccuracyAt());
	}

	/**equals: '=' Returns True, when 'Self' = arg
	 * has two Meanings:
	 * for Intervals it tests, whether the borders are the same.
	 * for Scalars   it tests, whether the Scalar is contained in the Interval. */
	public boolean equals (double arg) {
		if    (Left != Right) return false;
		return Left == arg; }

	/**equals: '=' Returns True, when 'Self' = arg
	 * has two Meanings:
	 * for Intervals it tests, whether the borders are the same.
	 * for Scalars   it tests, whether the Scalar is contained in the Interval. */
	public boolean equals (Object arg) {
		if (arg instanceof IntervalDbl)
			 return (Left  == ((IntervalDbl)arg).Left ) &&
					(Right == ((IntervalDbl)arg).Right);
		if (Left != Right) return false; //this quick Check can be done already here!
		return Left == ByRefDouble.GET_DOUBLE(arg); }

	/**less: '<' Returns True, when 'Self' < arg
	 * This is not defined with inverted Intervals.	 */
	public boolean less (double arg) {
		if (Left > Right) throw new AbstractMethodError(); // return false; //not defined!
		return Right < arg; }

	/**grtr: '>' Returns True, when 'Self' > arg	 */
	public boolean grtr (double arg) {
		if (Left > Right) throw new AbstractMethodError(); // return false; //not defined!
		return Left  > arg; }

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (Object arg) {
		if (arg instanceof IntervalDbl) {
			IntervalDbl Arg = (IntervalDbl) arg;
			if (Arg.Left > Arg.Right) throw new AbstractMethodError(); // return false; //not defined!
			return less(Arg.Left); }
		return less(ByRefDouble.GET_DOUBLE(arg)); }

	/**grtr: '>' Returns True, when 'Self' > arg	 */
	public boolean isMoreThan (Object arg) {
		if (arg instanceof IntervalDbl) {
			IntervalDbl Arg = (IntervalDbl) arg;
			if (Arg.Left > Arg.Right) throw new AbstractMethodError(); // return false; //not defined!
			return grtr(Arg.Right); }
		return grtr(ByRefDouble.GET_DOUBLE(arg)); }

	/**overlaps: Returns True, when 'this' Interval overlaps with the Argument.	 */
	public boolean overlaps(Object Arg) {
		if (Arg instanceof IntervalDbl) {
			IntervalDbl arg = (IntervalDbl) Arg;
			return	contains(arg.Left ) ||
					contains(arg.Right) ||
				arg.contains(	 Left ) ||
				arg.contains(	 Right); }
		return contains(ByRefDouble.GET_DOUBLE(Arg)); }

	/**contains: Returns True, when 'this' Interval contains the Argument.
	 * This corresponds to the 'between(a, b)' Method of 'arg'.
	 * This Check is not complete without testing
	 * whether this is a normal	or inverted Interval! */
	public boolean contains(double arg) {
		return ((Left  > arg)  ^
				(Right > arg)) ^
			    (Left  > Right); }

	/**contains: Returns True, when 'this' Interval contains the Argument.
	 * This corresponds to the 'between(a, b)' Method of 'arg'.	 */
	public boolean contains(Object arg) {
		if (arg instanceof IntervalDbl)
			return	contains(((IntervalDbl) arg).Left ) &&
					contains(((IntervalDbl) arg).Right);
		return contains(ByRefDouble.GET_DOUBLE(arg)); }



	//////////////////////////
	//	Arithmetic Methods	//
	//////////////////////////

	/**Setting to 0 in Place: exact	 */
	public IGroup zeroAt() {
		Left  = Right = ICountAble.ZERO;
		return this; }

	/**Setting to 1 in Place: exact	 */
	public IGroupM oneAt() {
		Left  = Right = ICountAble.ONE;
		return this; }

	/**Returns the Constant Pi = 3.14159265359... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt() {
		Left = Right = IMeasurAble.PI;
		return this; }

	/**Testing for 0:	 */
	public boolean isZero() {
		return Left  == ICountAble.ZERO &&
			   Right == ICountAble.ZERO; }

	/**Returns an Integer, not a IntervalDbl, this also saves time in further Calculations!	 */
	public IMetricIRing FloorAt() {
		Left  = Math.floor(Left );
		Right = Math.floor(Right);
		if (Left != Right) throw new AbstractMethodError(); //Floor should be an Integer!
		return this; } //Left ;

	/**Returns an Integer, not a IntervalDbl, this also saves time in further Calculations!	 */
	public IIntRing IntAt() {
		Left  = (long) Left ;
		Right = (long) Right;
		if (Left != Right) throw new AbstractMethodError(); //Floor should be an Integer!
		return this; } //Left ;

	/**Returns +Infinity = 1/0	 */
	public IWellOrder maxValueAt() {
		Left  = -Double.MAX_VALUE;
		Right = +Double.MAX_VALUE;
		return this; }


	//////////////////////////////
	//	Arithmetic Operations	//
	//////////////////////////////

	/**Helper Routine to convert to IntervalDbl from any other numeric Type:
	 * RingLong, Number or ICountAble.
	 * Uses ASemiGroup.getLong to do that.
	 * Using this Helper Routine generates Overhead,
	 * because the special optimizations for integer Values are not considered.	 */
/*	private final void	  convertArg (Object arg) {
		//	private final IntervalDbl convertArg (Object arg) {
		if (arg instanceof IntervalDbl) {
			arg_.Left  = ((IntervalDbl) arg).Left ;
			arg_.Right = ((IntervalDbl) arg).Right;
		}else{
			arg_.Left  = (MetricIRing) arg;
			arg_.Right = (MetricIRing) arg;
//			arg_.Right = null;	//considerable Performance win!
		}
	}

	/**Swaps Left  and Right	 */	protected void swapAt() { double tmp = Left ; Left  = Right; Right = tmp; }

	/**Counter for the Optimizations	 */
	public static int AddOptimizations = 0;

	/**Counter for the normal Operations	 */
	public static int AddOperations = 0;


	/**Counter for the Optimizations	 */
	public static int MulOptimizations = 0;

	/**Counter for the normal Operations	 */
	public static int MulOperations = 0;


	/**Addition in Place: +=
	 * assumes null to be 0	 */
	public ISemiGroup  addAt(Object arg)	{
//		++AddOperations;
//		++AddOptimizations;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero) ||
			(arg == null)) return this;
		if (arg instanceof IntervalDbl) {
			IntervalDbl arg_ = (IntervalDbl) arg;
			Left  += arg_.Left ;
			Right += arg_.Right; }
		else {
			double Value; // = AOrderAble.getDouble(arg);
			Left  +=(Value = ByRefDouble.GET_DOUBLE(arg));
			Right += Value; }
//		--AddOptimizations;
		return this; }

	/**Subtraction in Place: -=
	 * assumes null to be 0	 */
	public IGroup     subAt(Object arg)	{
//		++AddOperations;
//		++AddOptimizations;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero) ||
			(arg == null)) return this;
		if (arg instanceof IntervalDbl) {
			IntervalDbl arg_ = (IntervalDbl) arg;
			Left -= arg_.Left ;
			Right-= arg_.Right; }
		else {
			double Value; // = AOrderAble.getDouble(arg);
			Left -=(Value = ByRefDouble.GET_DOUBLE(arg));
			Right-= Value; }
//		--AddOptimizations;
		return this; }

	/**Multiplication in Place: *=
	 * assumes null to be 1	 */
	public ISemiGroupM mulAt(Object arg)	{	//Check if the Argument is a scalar, before converting it to a IntervalDbl
//		++MulOperations;
//		++MulOptimizations;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero)
			 ) { zeroAt(); return this; }
		if ((arg ==  ICountAble.One) ||
			(arg == CCountAble.One) ||
			(arg == BodyDouble.One) ||
			(arg == null)) return this;
		if ((arg ==  ICountAble._One) ||
			(arg == CCountAble._One) ||
			(arg == BodyDouble._One)
			 ) { negAt(); return this; }
		if  (arg instanceof IntervalDbl) {
			IntervalDbl arg_ = (IntervalDbl) arg;
			boolean Anegative = arg_.negative();
			boolean Apositive = arg_.positive();
			boolean  negative =		 negative();
			boolean  positive =		 positive();
			if (! ((Anegative || Apositive) &&
				   ( negative ||  positive)))
			{	//calculate all possible Products and choose the Left  and Right.
//				throw new AbstractMethodError();	//Multiplication by Zero! Loss of Accuracy
				double t00 = Left *  arg_.Left ;
							 Left *= arg_.Right;
				double t10 = Right*  arg_.Left ;
							 Right*= arg_.Right;
				if (Left > Right) swapAt();
				if (t00  < Left ) Left = t00; else
				if (t00  > Right) Right= t00;
				if (t10  < Left ) Left = t10; else
				if (t10  > Right) Right= t10;
				return this; }		//Tausch verhindern
			if (positive) {
				if (Apositive) {
					Left  *= arg_.Left ;
					Right *= arg_.Right; } else
				if (Anegative) {
					Left *= arg_.Right;
					Right*= arg_.Left ; swapAt(); }	//tauschen
			} else if (negative) {
				if (Anegative) {
					Left *=arg_.Left ;
					Right*=arg_.Right; swapAt(); } else	//tauschen
				if (Apositive) {
					Left *=arg_.Right;
					Right*=arg_.Left ; }
			}
		} else {	//exact Argument
			double Value;
			if   ((Value = ByRefDouble.GET_DOUBLE(arg)) < ICountAble.ZERO) swapAt();
			Left *=Value;
			Right*=Value;
		}
//		--MulOptimizations;
		return this; }

	/**Division in Place: /=
	 * assumes null to be 1
	 * obige Implementation vermeidet Genauigkeitsverlust und einen �berlauf durch die Quadrierung
	 * und spart au�erdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IGroupM divAt(Object arg) {
//		++MulOperations;
//		++MulOptimizations;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero)
			 ) { InfinityAt(); return this; }
		if ((arg ==  ICountAble.One) ||
			(arg == CCountAble.One) ||
			(arg == BodyDouble.One) ||
			(arg == null)) return this;
		if ((arg ==  ICountAble._One) ||
			(arg == CCountAble._One) ||
			(arg == BodyDouble._One)
			 ) { negAt(); return this; }
		if  (arg instanceof IntervalDbl) {
			IntervalDbl arg_ = (IntervalDbl) arg;
			boolean  negative =		 negative();
			boolean  positive =		 positive();
			boolean Anegative = arg_.negative();
			boolean Apositive = arg_.positive();
			if (! ((Anegative || Apositive) &&
				   ( negative ||  positive)))	//if not both Arguments are definite...
			{	//calculate all possible Products and choose the Left  and Right.
//				throw new AbstractMethodError();	//Division by Zero! Loss of Accuracy
				double t00 =Left / arg_.Left ;
							Left /=arg_.Right;
				double t10 =Right/ arg_.Left ;
							Right/=arg_.Right;
				if (Left  > Right) swapAt();
				if (t00 < Left ) Left =t00; else
				if (t00 > Right) Left =t00;
				if (t10 < Left ) Left =t10; else
				if (t10 > Right) Left =t10;
			} else //for definite Arguments, the Result is definite!
			if (positive) {
				if (Apositive) {
					Left /=arg_.Right;
					Right/=arg_.Left ; }
				if (Anegative) {
					Left /=arg_.Left ;
					Right/=arg_.Right; swapAt(); }	//tauschen
			} else if (negative) {
				if (Anegative) {
					Left /=arg_.Right;
					Right/=arg_.Left ; swapAt(); }	//tauschen
				if (Apositive) {
					Left /=arg_.Left ;
					Right/=arg_.Right; }}
		} else {	//exact Argument
			double Value;
			if   ((Value = ByRefDouble.GET_DOUBLE(arg)) < ICountAble.ZERO) swapAt();
			Left /=Value;
			Right/=Value; }
//		--MulOptimizations;
		return this; }

	/**Inversion in Place: 1/x
	 * If 0 is included, the Result MUST include Infinity!	 */
	public IGroupM invAt() {
		if (!(positive() || negative())) throw new AbstractMethodError();	//Inversion of Zero! Loss of Accuracy!
		double tmp;
		tmp  = ICountAble.ONE / Right;
		Right= ICountAble.ONE / Left;
		Left = tmp;	//always swap!
		return this; }


	//virtual Methods of Object

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new IntervalDbl(); }

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Constituents.
	 * It keeps the current reference, so any Argument has to be converted to IntervalDbl!	 */
	public ICopyAble copyAt(Object arg, int Depth) { return shallowCopyAt(arg); }

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg)
	{	//don't rely on the Argument being a IntervalDbl
//		super.shallowCopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		if (arg instanceof IntervalDbl) {
			IntervalDbl arg_ = (IntervalDbl) arg;
			Left  = arg_.Left ;
			Right = arg_.Right; } else
			Left  = Right = ByRefDouble.GET_DOUBLE(arg);
		return this; }

	/**Returns a hash code value for the object. This method is
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
	 * @return  a hash code value for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.util.Hashtable
	 * @since   JDK1.0	 */
	public int hashCode() { return (int)(
		Double.doubleToLongBits (Right) +
		Double.doubleToLongBits (Left )); }

	/**Separator String, see also in ACopyAble.Separator	 */
	public static String Separator = ";";

	/**Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommended that all subclasses override this method.
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
		return "[" + Left + "," + Right + "]"; }
//		return Starter + Left .toString() + Separator + Right.toString() + Stopper; }

	/**Parses the String with the full Description to a IntervalDbl Number.
	 * of two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation. */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg)
		throws java.io.IOException {
		while (arg.nextToken() != java.io.StreamTokenizer.TT_NUMBER); Left  = arg.nval;
		while (arg.nextToken() != java.io.StreamTokenizer.TT_NUMBER); Right = arg.nval;
/*		String[] List = parseList(arg);
		Left .fromStringAt(List[0]);
		Right.fromStringAt(List[1]);
*/		return this; }

	//These are Optimizations:

	/**Negation in Place: -
	 * always swaps	 */
	public IGroup negAt	   () {  double tmp = -Left; Left = -Right; Right = tmp; return this; }

	/**Double in Place: x+=x == x*=2
	 * never swaps	*/
	public ISemiGroup dblAt () {  Left += Left; Right += Right; return this; }

	/**Triple in Place: x+=x+x == x*=3
	 * never swaps	*/
	public ISemiGroup trplAt() {  Left*=ICountAble.THREE; Right*=ICountAble.THREE; return this; }

	/**Decrements the Value by 1
	 * never swaps	*/
	public integer dec() { Left-=ICountAble.ONE; Right-=ICountAble.ONE; return this; }

	/**Inrements the Value by 1
	 * never swaps	*/
	public integer inc() { Left+=ICountAble.ONE; Right+=ICountAble.ONE; return this; }

	/**Residual in Place: 1-x
	 * always swaps	 */
	public integer ResidAt() { double
		tmp  = ICountAble.ONE-Left;
		Left = ICountAble.ONE-Right;
		Right= tmp; return this; }

	//Only the real part counts, this is at least valid for the Calculation of ArcTan

	/**Returns true only if the Number is completely negative, i.e. Left  < Right < 0	 */
	public boolean negative() { return Right < ICountAble.ZERO; }

	/**Returns true only if the Number is completely positive, i.e. Right > Left  > 0	 */
	public boolean positive() { return Left > ICountAble.ZERO; }

	/**Returns the exact two in Place.	 */
	public IIntRing twoAt() { Left = Right = ICountAble.TWO; return this; }

	/**Returns the exact three in Place.	 */
	public IIntRing threeAt() { Left = Right = ICountAble.THREE; return this; }

	/**Half in Place: x/=2	*/
	public IIntRing halfAt () { Left *= IMeasurAble.HALF; Right *= IMeasurAble.HALF; return this; }

	/**Third in Place: x/=3	*/
	public IIntRing thirdAt() { Left *=IMeasurAble.THIRD; Right*=IMeasurAble.THIRD; return this; }

	/**Square in Place: x^2 == x*=x
	 * Always gives positive Results, but if the Number includes 0,
	 * the Result must always include 0 too.	 */
	public ISemiGroupM sqrAt() {
		boolean negative =  negative();
		if (!  (negative || positive())) throw new AbstractMethodError();
		Left *=Left ;
		Right*=Right; if (negative) swapAt();
		return this; } 	//No ggT for Left  or Right => only rounding.

	/**Cubic in Place: x*=x^3	 */
	public ISemiGroupM cbcAt() {
		Left *=Left *Left ;
		Right*=Right*Right;
		return this; }

	/**Checks if the IntervalDbl Number is even.
	 * i.e. both real and imaginary part are even	 */
	public boolean isEven() { //the first Tests are obsolete, since they are covered by the second Test too!
		double tmp;
//		if (Math.floor(Left ) != Left ) return false; //not integer
//		if (Math.floor(Right) != Right) return false; //not integer
		tmp = Left *IMeasurAble.HALF; if (Math.floor(tmp  ) != tmp  ) return false;
		tmp = Right*IMeasurAble.HALF; if (Math.floor(tmp  ) != tmp  ) return false;
		return true; }

	/**Checks if the IntervalDbl Number is odd.
	 * i.e. both real and imaginary part are odd	 */
	public boolean isOdd () {
		double tmp;
//		if (Math.floor(Left ) != Left ) return false; //not integer
//		if (Math.floor(Right) != Right) return false; //not integer
		tmp = Left *IMeasurAble.HALF; if ((tmp - Math.floor(tmp  )) != IMeasurAble.HALF) return false;
		tmp = Right*IMeasurAble.HALF; if ((tmp - Math.floor(tmp  )) != IMeasurAble.HALF) return false;
		return true; }

	//left out: ModAtDivAt, ModlAt, kgV, ggT, IntAt == FloorAt

	/**Returns the Square Root of this in Place: x^=.5	*/
	public IMetricIRing SqRtAt()	{
		if (!positive()) throw new AbstractMethodError();
		Left = Math.sqrt(Left );
		Right= Math.sqrt(Right);
		return this; }

	/**absolute Value in Place:				 |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsVAt() {
		Left = Right = Math.abs(Right) + Math.abs(Left); return this; }

	/**absolute Value: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsV() { //
		return new BodyDouble(Math.abs(Right) + Math.abs(Left)); }

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry() { }

	//Complement, necessary for gAdic Calculation
	/**Complement in Place: ~=	*/
	public IIntRing CmplAt() { throw new AbstractMethodError(); }

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpperAt() { throw new AbstractMethodError(); }


	//////////////////////////////
	//	Interface ICountAble		//
	//////////////////////////////

	//Taken out, because Information gets lost!

	/** Returns the Object Value represented by an 8 Bit Integer	 */
//	public byte	  getByte() { return Real.getByte(); }

	/** Returns the Object Value represented by an 16 Bit Integer	 */
//	public short getShort() { return Real.getShort(); }

	/** Returns the Object Value represented by an 32 Bit Integer	 */
//	public int	   getInt() { return Real.getInt(); }

	/** Returns the Object Value represented by an 64 Bit Integer	 */
//	public long   getLong() { return Real.getLong(); }

	//////////////////////////////
	//	Interface IMeasurAble	//
	//////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double getDouble()	{
		if (Right != Left) throw new AbstractMethodError();
		return Left; }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float   getFloat() {
		if (Right != Left) throw new AbstractMethodError();
		return (float) Left; }

	/**Tests the Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		IntervalDbl test = new IntervalDbl();
		testInstance = test;	//defined in ACopyAble to test the abstract Methods
	}

	//////////////////////////////////
	//	Optimizations for OrderAble	//
	//////////////////////////////////

	/**Adds a Point to the Interval thus enlarging it.
	 * This Operation prepares the superSect Operation. 	 */
	public IntervalDbl addPoint (Object arg) {
		double arg_ = ByRefDouble.GET_DOUBLE(arg);
		if (Left  > arg_) Left =arg_; else 	//= (OrderAble) arg; else
		if (Right < arg_) Right=arg_;			//= (OrderAble) arg;
		return this; }

	/**Returns the Intersection Interval in place. 	 */
	public IntervalDbl interSectAt	(IntervalDbl arg) {
		if (Left  < arg.Left )
			Left  = arg.Left ;
		if (Right > arg.Right)
			Right = arg.Right;
		return this; }

	/**Returns the SuperSection Interval in place. 	 */
	public IntervalDbl superSectAt(IntervalDbl arg) {
		if (Left  > arg.Left )
			Left  = arg.Left ;
		if (Right < arg.Right)
			Right = arg.Right;
		return this; }

	/**Returns the Intersection Interval. 	 */
	public IntervalDbl interSect	(IntervalDbl arg) {
		return ((IntervalDbl)copy()).interSectAt(arg); }

	/**Returns the SuperSection Interval. 	 */
	public IntervalDbl superSect	(IntervalDbl arg) {
		return ((IntervalDbl)copy()).superSectAt(arg); }

	/**overlaps: Returns True, when 'this' Interval overlaps with the Argument.	 */
	public boolean overlaps(IntervalDbl arg) {
		return		contains(arg.Left ) ||
					contains(arg.Right); /*||
				arg.contains(	 Left ) ||
				arg.contains(	 Right);
//				arg.overlaps(this);	//last Term would lead to an infinite Recursion
/*		return (	Left  > arg.Left ) ^	//this is equivalent, but faster
					Right > arg.Left )) ||	//contains(arg.Left ) ||
			   (	Left  > arg.Right) ^
					Right > arg.Right)) ||	//contains(arg.Right) ||
			   (arg.Left  > 	Left ) ^
				arg.Right > 	Left )) ||
			   (arg.Left  > 	Right) ^
				arg.Right > 	Right));
*/	}

	/**contains: Returns True, when 'this' Interval contains the Argument.
	 * This corresponds to the 'between(a, b)' Method of 'arg'.	 */
	public boolean contains(IntervalDbl arg) {
		return	contains(arg.Left ) &&
				contains(arg.Right); }


	//////////////////////////////////
	//	Set Operations w. Interval	//
	//////////////////////////////////
	//	the Union of two Intervals is usually no Interval,
	//	you have to start maintaining Sets and unite subsets,
	//	so only superSect and interSect are implemented.

	/**Adds a Point to the Interval in Place thus enlarging it.
	 * This Operation prepares the superSect Operation.
	 * This is used e.g. for finding out the bounding Box in 2 and 3 Dimensions.	 */
	public IntervalDbl addPointAt (double arg) {
		if (Left  > arg) Left =arg; //Left = (OrderAble) arg;
		if (Right < arg) Right=arg; //Right= (OrderAble) arg;
		return this; }

	/**Adds a Point to the Interval in Place thus enlarging it.
	 * This Operation prepares the superSect Operation.
	 * This is used e.g. for finding out the bounding Box in 2 and 3 Dimensions.	 */
	public IntervalDbl addPointAt (Object arg) {
		if (arg instanceof IntervalDbl)
			return superSectAt((IntervalDbl) arg);
		return addPointAt(ByRefDouble.GET_DOUBLE(arg)); }

	/**Adds a Point to the Interval in Place thus enlarging it.
	 * This Operation prepares the superSect Operation.
	 * This is used e.g. for finding out the bounding Box in 2 and 3 Dimensions.	 */
	public IntervalDbl addPoint_(Object arg) { return ((IntervalDbl)copy()).addPointAt(arg); }


	//////////////////////////////////
	//	Redefinition of Position	//
	//////////////////////////////////

	/**Returns the Position of arg relative to the Interval.
	 * -2 for x		< a[0]	< a[1]
	 * -1 for x		= a[0]	< a[1]
	 *  0 for a[0]	< x		< a[1]
	 * +1 for a[0]	< a[1]	= x
	 * +2 for a[0]	< a[1]	< x	 */
	public int compareTo (Object arg) { return
		ByRefDouble.SIGN(Left ) +
		ByRefDouble.SIGN(Right); }

	/**Returns the Position of arg relative to the Interval.
	 * -2 for x		< a[0]	< a[1]
	 *  0 for a[0]	<= x	< a[1]
	 * +2 for a[0]	< a[1]	<= x	 */
	public int Position (Object arg) { return
		ByRefDouble.ZCHN(Left ) +
		ByRefDouble.ZCHN(Right); }


	//////////////////
	//	CopyAble	//
	//////////////////

	/**No-op: an Interval has no imaginary Part to conjugate.	 */
	public IIntRing cjgAt() { return this; }

	/**Returns false: an Interval is always real-valued, never complex.	 */
	public boolean isComplex() { return false; }

//	public SemiMonoid mapAt(SemiMonoid arg) { ((CopyAble) arg).copyAt(this); return arg; }

}
