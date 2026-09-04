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
import streamIO.copy.order.Interval;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.CCountAble;

/**
  * Defines an affine Interval in a fully ordered Scalar (1-dim) Metric Space.
  * It's Definition is similar to the Definition of 'Line' in the Tensor Package.
  * It is capable of Interval Arithmetic +, - and * with non negative Numbers
  * but can not be used for Error Estimations and fuzzy Calculations:
  *
  * To speed up Tests, the Coordinates are ordered so that 'left' < 'right'.
  * It has some new Methods: instead of equals it also defines overlaps() and contains()
  *
  * GroupM Operations with Intervals are a Problem, because Inversion
  * doesn't fit with the Order Relation.
  * (Infinity which has two Expressions in the affine Model)
  *
  * Therefore another Class 'IntervalDbl' is derived from the same Parent,
  * where Left > Right is defined to include Infinity!
  * The affine Definition of an Interval is easier and faster for any
  * Order Relation and Set Operation, but not for full Arithmetics.
  *
  * Design Decisions:
  * Instead of introducing a boolean Flag
  * that switches between affine and projective Geometry everywhere,
  * a new Class is derived. This is more object oriented!
  *
  * This Class is practically completely copied from Complex!
  * Any change here should also be done in Complex!
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
  */
public class IntervalDblA
extends AMetricBody {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Left  Value of the IntervalDblA	 */
	protected double Left ;

	/** Right Value of the IntervalDblA	 */
	protected double Right;

	/** Cache for the Test on positive Values	 */
	protected boolean positive;

	/** Cache for the Test on negative Values	 */
	protected boolean negative;

	/** Local Object for consistent Argument Treatment in convertArg()	 */
	protected IntervalDblA arg_ = new IntervalDblA();

	/** Returns the conjugate IntervalDblA Number in Place:
	  * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt() { return this; }

	/** Overrides the false Value from the Implementation in absComplex	 */
	public boolean isComplex() { return false; }

	/** Switches Checking for real Results on or off.
	  * Since you can not expect to be a result real, it is typically switched on. 	 */
//	public static boolean bolLazySimplify = true; //false;


	//////////////////////
	//	Constructors	//
	//////////////////////

	/** Initializes this Class AFTER the Constructor
	  * (for BEFORE Initialization you would use an empty Constructor)
	  * The Coordinates are swapped to enable faster Checking,
	  * because Creation typically happens less often than other Operations!  */
	protected void init() {
		if (Left > Right) swapAt();	//this should normally not be necessary
		if  (positive = (Left  > 0)) negative = false;		//caching the Results because frequently used!
		else negative = (Right < 0);	//caching the Results because frequently used!
	}

	/** Constructor that takes an Object of the same Class as Input(Copy Constructor).
	  * Uses the Copy Constructors of the Constituents.	 */
	public IntervalDblA(IntervalDblA arg)	{	//copyAt(arg);	//...the same, only faster:
		Left  = arg.Left ;
		Right = arg.Right;
		init();
	}

	/** Constructor that takes any Object as Input for real and imaginary Part.
	  * Those Objects should be of the same Type to speed up calculation
	  * or even enabling it (when e.g. one is not ICountAble).	 */
	public IntervalDblA(Object Min_, Object Max_) {
		boolean MinC; //double dMin_;
		boolean MaxC; //double dMax_;
		double buf = 0;
		if (MinC = Min_ instanceof IntervalDblA) {
			buf = ByRefDouble.GET_DOUBLE(Max_);
			Right = ((IntervalDblA) Min_).Right;
			Left  = ((IntervalDblA) Min_).Left ; }
		if (MaxC = Max_ instanceof IntervalDblA) {
			buf = ByRefDouble.GET_DOUBLE(Max_);
			Left  = ((IntervalDblA) Max_).Left ;
			Right = ((IntervalDblA) Max_).Right; }
		if (MinC & MaxC) throw new AbstractMethodError();
		if (MinC && Right < buf) Right = buf;
		if (MaxC && Left  > buf) Left  = buf;
		init();
	}

	/** Constructor that takes any Object as Input for the real Part.
	  * The imaginary Part is set to 0.	 */
	public IntervalDblA(Object arg) {
		if (arg instanceof IntervalDblA) { //this((IntervalDblA) arg); {
			Left  = ((IntervalDblA) arg).Left ;
			Right = ((IntervalDblA) arg).Right;
		} else {
			Left  = ByRefDouble.GET_DOUBLE(arg);
			Right = ByRefDouble.GET_DOUBLE(arg);	//Choose the same type -> faster
//			Right = null;	//considerable Performance Increase for exact Values
		}
		init();
	}

	/** Constructor that takes a double as Input for the Left .
	  * The Right is set to the same Number.
	  * The Types are defaulted to BodyDouble.	 */
	public IntervalDblA(double Min_) {
		Left  = Min_;
		Right = Min_;	//Choose the same type -> faster
//		Right = null;	//considerable Performance Increase for exact Values
		init();	}

	/** Constructor that takes a double as Input for the real Part.
	  * The imaginary Part is set to 0.
	  * The Types are defaulted to BodyDouble.	 */
	public IntervalDblA(double Min_, double Max_) {
		Left  = Min_;
		Right = Max_;
		init();	}

	/**Constructor that takes an int as Input for the Left  Part.
	 * The imaginary Part is set to 0.	 */
/*	public IntervalDblA(int Min_) {
		Left  = new RingLong(Min_);
		Right = new RingLong(Min_);	//Choose the same type -> faster
		init();	}
*/
	/**TODO: incorporate this,
	 * although it is very unlikely to calculate with complex Integers.	 */

	/**Empty Constructor (for newInstance Method).
	 * Does not create Dummy Objects for it's Constituents.
	 * So those Objects are not well-defined, but contain Null Pointers.
	 * This Constructor must not be public,
	 * because the Type of it's Parts must be defined!	 */
	protected IntervalDblA() {
//		BaseAccuracyInv = SqRtMaxValue();
//		BaseAccuracy = BaseAccuracyInv.inv();
//		Left  = new BodyDouble();
//		Right = new BodyDouble();
	}

	//////////////////////////
	//	Interface OrderAble	//
	//////////////////////////

	/**equals: '=' Returns True, when 'Self' = arg
	 * has two Meanings:
	 * for Intervals it tests, whether the borders are the same.
	 * for Scalars   it tests, whether the Scalar is contained in the Interval. */
	public boolean equals (double arg) {
		if (   Left != Right) return false;
		return Left == arg; }

	/**equals: '=' Returns True, when 'Self' = arg
	 * has two Meanings:
	 * for Intervals it tests, whether the borders are the same.
	 * for Scalars   it tests, whether the Scalar is contained in the Interval. */
	public boolean equals (Object arg) {
		if (arg instanceof IntervalDblA)
			 return (Left  == ((IntervalDblA)arg).Left ) &&
					(Right == ((IntervalDblA)arg).Right);
		if (   Left != Right) return false;
		return Left == ByRefDouble.GET_DOUBLE(arg); }

	/**less: '<' Returns True, when 'Self' < arg
	 * Assumption: Left < Right < arg.Left < arg.Right	 */
	public boolean isLessThan (Object arg) {
		convertArg(arg); return Right < arg_.Left; } 	//exploiting the Orderedness!

	/**greater: '>' Returns True, when 'Self' > arg
	 * Assumption: Right > Left > arg.Right > arg.Left
	 * This is necessary, since overlapping Intervals
	 * none of them is less or greater than the other one. 	 */
	public boolean isMoreThan (Object arg) {
		convertArg(arg); return Left > arg_.Right; } 	//exploiting the Orderedness!

	/**overlaps: Returns True, when 'this' Interval overlaps with the Argument.	 */
	public boolean overlaps(Object Arg) {
		if (Arg instanceof IntervalDblA) {
			IntervalDblA arg = (IntervalDblA) Arg;
			return	contains(arg.Left ) ||
					contains(arg.Right) ||
				arg.contains(	 Left ) ||
				arg.contains(	 Right);
//				arg.overlaps(this);	//last Term would lead to an infinite Recursion
		}
/*		return (	Left  >  (arg.Left ) ^	//this is equivalent, but faster
					Right >  (arg.Left )) ||	//contains(arg.Left ) ||
			   (	Left  >  (arg.Right) ^
					Right >  (arg.Right)) ||	//contains(arg.Right) ||
			   (arg.Left  >  (	Left ) ^
				arg.Right >  (	Left )) ||
			   (arg.Left  >  (	Right) ^
				arg.Right >  (	Right));
*/		return contains(Arg);
	}

	/**contains: Returns True, when 'this' Interval contains the Argument.
	 * This corresponds to the 'between(a, b)' Method of 'arg'.	 */
	public boolean contains(Object arg) {
		if (arg instanceof Interval)
			return	contains(((Interval) arg).Left ) &&
					contains(((Interval) arg).Right);
		return contains(ByRefDouble.GET_DOUBLE(arg));
//		return ((OrderAble) arg).between(Left, Right);
	}

	/**less: '<' Returns True, when 'Self' < arg
	 * Assumption: Left < Right < arg.Left < arg.Right	 */
	public boolean less (double arg) { return Right < arg; } 	//exploiting the Orderedness!

	/**greater: '>' Returns True, when 'Self' > arg
	 * Assumption: Right > Left > arg.Right > arg.Left
	 * This is necessary, since overlapping Intervals
	 * none of them is less or greater than the other one. 	 */
	public boolean grtr (double arg) { return Left > arg; } 	//exploiting the Orderedness!

	/**contains: Returns True, when 'this' Interval contains the Argument.
	 * This corresponds to the 'between(a, b)' Method of 'arg'.	 */
	public boolean contains(double arg) {
		return Left  > arg ^ //this Expression stays correct for swapped Borders
			   Right > arg;	}//but not for the projective Definition.
//		return ((OrderAble) arg).between(Left, Right);


	//////////////////////////
	//	Arithmetic Methods	//
	//////////////////////////

	/**Setting to 0 in Place: exact	 */
	public IGroup zeroAt() {
		Left  = ICountAble.ZERO;
		Right = ICountAble.ZERO;
		positive = negative = false;
		return this; }

	/**Setting to 1 in Place: exact	 */
	public IGroupM oneAt() {
		Left  = ICountAble.ONE;
		Right = ICountAble.ONE;
		positive = negative = true;
		return this; }

	/**Returns the Constant Pi = 3.14159265359... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt() {
		Left  = IMeasurAble.PI;
		Right = IMeasurAble.PI;
		positive = negative = true;
		return this; }

	/**Testing for 0:	 */
	public boolean isZero(){
		return Left  == ICountAble.ZERO &&
			   Right == ICountAble.ZERO;}

	/**Returns an Integer, not a IntervalDblA, this also saves time in further Calculations!	 */
	public IMetricIRing FloorAt() {
		Left  = Math.floor(Left );
		Right = Math.floor(Right);
		if (Left != Right) throw new AbstractMethodError();
		return this; } //Left ;

	/**Returns an Integer, not a IntervalDblA, this also saves time in further Calculations!	 */
	public IIntRing IntAt() {
		Left  = (long) Left ;
		Right = (long) Right;
		if (Left != Right) throw new AbstractMethodError();
		return this; } //Left ;

	/**Returns +Infinity = 1/0	 */
	public IWellOrder maxValueAt() {
		Left  = Double.MAX_VALUE;
		Right = Double.MAX_VALUE;
		positive = negative = true;
		return this; }

//	public SemiMonoid mapAt(SemiMonoid arg) { arg.copyAt (this); return arg; }

	//////////////////////////////
	//	Arithmetic Operations	//
	//////////////////////////////

	/**Helper Routine to convert to IntervalDblA from any other numeric Type:
	 * RingLong, Number, Interval, IntervalA, IntervalP or ICountAble.
	 * Uses ASemiGroup.getLong to do that.
	 * Using this Helper Routine generates Overhead,
	 * because the special optimizations for integer Values are not considered.	 */
	private final IntervalDblA convertArg (Object arg) {
		if (arg instanceof IntervalDblA) {
			arg_.Left  = ((IntervalDblA) arg).Left ;
			arg_.Right = ((IntervalDblA) arg).Right; }
		else
			return null;
//			arg_.Left  = arg_.Right = AOrderAble.getDouble(arg);
		return arg_; }

	/**Swaps Left  and Right	 */	protected void swapAt() {
		double tmp = Left ; Left  = Right; Right = tmp; }


	/**Counter for the Optimizations	 */
	public static int AddOptimizations = 0;

	/**Counter for the normal Operations	 */
	public static int AddOperations = 0;


	/**Counter for the Optimizations	 */
	public static int MulOptimizations = 0;

	/**Counter for the normal Operations	 */
	public static int MulOperations = 0;

	/**Addition in Place: +=	 */
	public IntervalDblA addAt(double arg)	{
		Left  += arg;
		Right += arg;
		return this; }

	/**Addition in Place: +=
	 * assumes null to be 0	 */
	public ISemiGroup  addAt(Object arg)	{
//		++AddOperations;
//		++AddOptimizations;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero) ||
			(arg == null)) return this;
		if (arg instanceof IMeasurAble)
			return addAt(((IMeasurAble)arg).getDouble());
		convertArg(arg);
		Left  += arg_.Left ;
		Right += arg_.Right;
//		--AddOptimizations;
		return this; }

	/**Subtraction in Place: -=	 */
	public IntervalDblA subAt(double arg)	{
		Left  -= arg;
		Right -= arg;
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
		if (arg instanceof IMeasurAble)
			return subAt(((IMeasurAble)arg).getDouble());
		convertArg(arg);
		Left  -= ((IntervalDblA)arg).Left ;
		Right -= ((IntervalDblA)arg).Right;
//		--AddOptimizations;
		return this; }

	/**Multiplication in Place: *=	 */
	public IntervalDblA mulAt(double arg)	{
		if (arg < ICountAble.ZERO) swapAt();
		Left  *= arg;
		Right *= arg;
		return this; }

	/**Multiplication in Place: *=
	 * assumes null to be 1	 */
	public ISemiGroupM mulAt(Object arg)	{	//Check if the Argument is a scalar, before converting it to a IntervalDblA
//		++MulOperations;
//		++MulOptimizations;
		if ((arg ==  ICountAble.One) ||
			(arg == CCountAble.One) ||
			(arg == BodyDouble.One)
			 ) return this;
		if ((arg == null) ||
			(arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero)
			 ) { zeroAt(); return this; }
		if ((arg ==  ICountAble._One) ||
			(arg == CCountAble._One) ||
			(arg == BodyDouble._One)
			 ) { negAt(); return this; }
		if (arg instanceof IMeasurAble)
			return mulAt(((IMeasurAble)arg).getDouble());
		if  (arg instanceof IntervalDblA) {
			IntervalDblA arg_ = (IntervalDblA) arg;
			boolean Anegative = arg_.negative();
			boolean Apositive = arg_.positive();
			boolean  negative =		 negative();
			boolean  positive =		 positive();
			if (! ((Anegative || Apositive) &&
				   ( negative ||  positive)))
			{	//calculate all possible Products and choose the Left  and Right.
//				throw new AbstractMethodError();	//Multiplication by Zero! Loss of Accuracy
				double t00 = Left  *  arg_.Left ;
							 Left  *= arg_.Right;
				double t10 = Right *  arg_.Left ;
							 Right *= arg_.Right;
				if (Left > Right) swapAt();
				if (t00  < Left ) Left = t00; else
				if (t00  > Right) Left = t00;
				if (t10  < Left ) Left = t10; else
				if (t10  > Right) Left = t10;
				return this; }		//Tausch verhindern
			if (positive) {
				if (Apositive) {
					Left  *= arg_.Left ;
					Right *= arg_.Right; } else
				if (Anegative) {
					Left  *= arg_.Right;
					Right *= arg_.Left ; swapAt();}	//tauschen
			} else if (negative) {
				if (Anegative) {
					Left  *= arg_.Left ;
					Right *= arg_.Right; swapAt();} else	//tauschen
				if (Apositive) {
					Left  *= arg_.Right;
					Right *= arg_.Left ;}
			}
		}
		else { throw new AbstractMethodError();	}
//		--MulOptimizations;
		return this; }

	/**Division in Place: /=
	 * assumes null to be 1
	 * obige Implementation vermeidet Genauigkeitsverlust und einen ‹berlauf durch die Quadrierung
	 * und spart auﬂerdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IntervalDblA divAt(double arg) {
		if (arg < ICountAble.ZERO) swapAt();
		Left  /= arg;
		Right /= arg;
		return this; }

	/**Division in Place: /=
	 * assumes null to be 1
	 * obige Implementation vermeidet Genauigkeitsverlust und einen ‹berlauf durch die Quadrierung
	 * und spart auﬂerdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IGroupM divAt(Object arg) {
//		++MulOperations;
//		++MulOptimizations;
		if ((arg ==  ICountAble.One) ||
			(arg == CCountAble.One) ||
			(arg == BodyDouble.One)
			 ) return this;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero) ||
			(arg == null)) { InfinityAt(); return this; }
		if ((arg ==  ICountAble._One) ||
			(arg == CCountAble._One) ||
			(arg == BodyDouble._One)
			 ) { negAt(); return this; }
		if (arg instanceof IMeasurAble)
			return divAt(((IMeasurAble)arg).getDouble());
		if  (arg instanceof IntervalDblA) {
			IntervalDblA arg_ = (IntervalDblA) arg;
			boolean  negative =		 negative();
			boolean  positive =		 positive();
			boolean Anegative = arg_.negative();
			boolean Apositive = arg_.positive();
			if (! ((Anegative || Apositive) &&
				   ( negative ||  positive)))	//if not both Arguments are definite...
			{	//calculate all possible Products and choose the Left  and Right.
//				throw new AbstractMethodError();	//Division by Zero! Loss of Accuracy
				double t00 = Left  /  arg_.Left ;
							 Left  /= arg_.Right;
				double t10 = Right /  arg_.Left ;
							 Right /= arg_.Right;
				if (Left>  (Right)) swapAt();
				if (t00 <  (Left )) Left  = t00; else
				if (t00 >  (Right)) Left  = t00;
				if (t10 <  (Left )) Left  = t10; else
				if (t10 >  (Right)) Left  = t10;
			} else //for definite Arguments, the Result is definite!
			if (positive) {
				if (Apositive) {
					Left  /= arg_.Right;
					Right /= arg_.Left ;}
				if (Anegative) {
					Left  /= arg_.Left ;
					Right /= arg_.Right; swapAt();}	//tauschen
			} else if (negative) {
				if (Anegative) {
					Left  /= arg_.Right;
					Right /= arg_.Left ; swapAt();}	//tauschen
				if (Apositive) {
					Left  /= arg_.Left ;
					Right /= arg_.Right;}}
		}
		else { throw new AbstractMethodError();	}
//		--MulOptimizations;
		return this; }

	/**Inversion in Place: 1/x
	 * If 0 is included, the Result MUST include Infinity!	 */
	public IGroupM invAt() {
		if (!(positive() || negative)) throw new AbstractMethodError();	//Inversion of Zero! Loss of Accuracy!
		double tmp	= ICountAble.ONE / Left;
		Left		= ICountAble.ONE / Right;
		Right		= tmp; 	//always swap
		return this; }


	//virtual Methods of Object

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		return new IntervalDblA();	}

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Constituents.
	 * It keeps the current reference, so any Argument has to be converted to IntervalDblA!	 */
	public ICopyAble copyAt(Object arg, int Depth)
	{	//DeepCopy, ripples through, by using copyAt() on all Elements.
//		super = arg);	//not necessary, since all these Fields apply only to Integers.
		convertArg(arg);
		Right = arg_.Right;	//Right = (IntervalDblA) arg_.Right.copy();	//Doesn't matter,
		Left  = arg_.Left ;	//Left  = (IntervalDblA) arg_.Left .copy();	//only for Performance!
		return this; }

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg)
	{	//don't rely on the Argument being a IntervalDblA
//		super.shallowCopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		convertArg(arg);
		Left  = arg_.Left ;
		Right = arg_.Right;
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
	public int hashCode(){return ((int)Double.doubleToLongBits(Right)) +
								 ((int)Double.doubleToLongBits(Left )); }

	/**Separator String, see also in ACopyAble.Separator	 */
	public static String Separator = ";";

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
	public String toString(){
		return "[" + Left + ", " + Right + "]"; }
//		return Starter + Left + Separator + Right + Stopper; }

	/**Parses the String with the full Description to a IntervalDblA Number.
	 * of two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation. */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg)
		throws java.io.IOException {
//TODO		Left  = Parsing.nextNumber(arg, false);
//TODO		Right = Parsing.nextNumber(arg, false);
/*		String[] List = parseList(arg);
		Left .fromStringAt(List[0]);
		Right.fromStringAt(List[1]);
*/		return this; }

	//These are Optimizations:

	/**Negation in Place: -
	 * always swaps	 */
	public IGroup negAt	   () {
		double tmp	= -Left ;
		Left		= -Right;
		Right		= tmp; return this; }

	/**Double in Place: x+=x == x*=2
	 * never swaps	*/
	public ISemiGroup dblAt () {
		Left += Left ;
		Right+= Right; return this;}

	/**Triple in Place: x+=x+x == x*=3
	 * never swaps	*/
	public ISemiGroup trplAt() {
		Left  *= ICountAble.THREE;
		Right *= ICountAble.THREE; return this;}

	/**Decrements the Value by 1
	 * never swaps	*/
	public integer dec() {
		--Left ;
		--Right; return this;}

	/**Inrements the Value by 1
	 * never swaps	*/
	public integer inc() {
		Left ++;
		Right++; return this;}

	/**Residual in Place: 1-x
	 * always swaps	 */
	public integer ResidAt() {
		double tmp	= ICountAble.ONE-Left ;
		Left		= ICountAble.ONE-Right;
		Right		= tmp; return this; }

	//Only the real part counts, this is at least valid for the Calculation of ArcTan

	/**Returns true only if the Number is completely negative, i.e. Left  < Right < 0	 */
	public boolean negative() { return (Right < ICountAble.ZERO);}

	/**Returns true only if the Number is completely positive, i.e. Right > Left  > 0	 */
	public boolean positive() { return (Left  > ICountAble.ZERO);}

	/**Returns the exact two in Place.	 */
	public IIntRing twoAt() {
		Left  = ICountAble.TWO;
		Right = ICountAble.TWO; return this;}

	/**Returns the exact three in Place.	 */
	public IIntRing threeAt() {
		Left  = ICountAble.THREE;
		Right = ICountAble.THREE; return this;}

	/**Half in Place: x/=2	*/
	public IIntRing halfAt () {
		Left  *= IMeasurAble.HALF;
		Right *= IMeasurAble.HALF; return this;}

	/**Third in Place: x/=3	*/
	public IIntRing thirdAt() {
		Left *= IMeasurAble.THIRD;
		Right*= IMeasurAble.THIRD; return this;}

	/**Square in Place: x^2 == x*=x
	 * Always gives positive Results, but if the Number includes 0,
	 * the Result must always include 0 too.	 */
	public ISemiGroupM sqrAt() {
		boolean negative =  negative();
		if (!  (negative || positive())) throw new AbstractMethodError();
		Left  *= Left ;
		Right *= Right; if (negative) swapAt();
		return this; } 	//No ggT for Left  or Right => only rounding.

	/**Cubic in Place: x*=x^3	 */
	public ISemiGroupM cbcAt() {
		Left  *= Left *Left ;
		Right *= Right*Right;
		return this; }

	/**Checks if the IntervalDblA Number is even.
	 * i.e. both real and imaginary part are even	 */
	public boolean isEven() { return (Left  == Right) && ((Right % ICountAble.TWO) == ICountAble.ZERO); }

	/**Checks if the IntervalDblA Number is odd.
	 * i.e. both real and imaginary part are odd	 */
	public boolean isOdd () { return (Left  == Right) && ((Right % ICountAble.TWO) != ICountAble.ZERO); }

	//left out: ModAtDivAt, ModlAt, kgV, ggT, IntAt == FloorAt

	/**Returns the Square Root of this in Place: x^=.5	*/
	public IMetricIRing SqRtAt()	{
		if (!positive()) throw new AbstractMethodError();
		Left  = Math.sqrt(Left );
		Right = Math.sqrt(Right);
		return this; }

	/**absolute Value in Place:				 |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsVAt() {	//((CopyAble)Left ).ShallowCopyAt(AbsV());
		Right = Left =	Math.abs(Left ) +
						Math.abs(Right); 	//still keep this a sensible Representation!
//		return (ScalarMetric) Left;
		return this; }

	/**absolute Value: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsV() { //{return (Group) ((MetricIRing)SqrAbsV()).SqRt();}
		return new BodyDouble(	Math.abs(Left ) +
								Math.abs(Right)); }

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry(){}

	//Complement, necessary for gAdic Calculation
	/**Complement in Place: ~=	*/
	public IIntRing CmplAt(){throw new AbstractMethodError();}

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpperAt(){throw new AbstractMethodError();}


	//////////////////////////////
	//	Interface ICountAble		//
	//////////////////////////////

	//Taken out, because Information gets lost!

	/** Returns the Object Value represented by an 8 Bit Integer	 */
//	public byte	  getByte(){return Real.getByte();}

	/** Returns the Object Value represented by an 16 Bit Integer	 */
//	public short getShort(){return Real.getShort();}

	/** Returns the Object Value represented by an 32 Bit Integer	 */
//	public int	   getInt(){return Real.getInt();}

	/** Returns the Object Value represented by an 64 Bit Integer	 */
//	public long   getLong(){return Real.getLong();}

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
		IntervalDblA test = new IntervalDblA(new BodyDouble(), new BodyDouble());
		testInstance = test;	//defined in ACopyAble to test the abstract Methods
	}

}
