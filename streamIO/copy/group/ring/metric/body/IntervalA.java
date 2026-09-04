package streamIO.copy.group.ring.metric.body;

import streamIO.IDeserializer;
import streamIO.copy.CCopyAble;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.integer;
import streamIO.copy.group.ring.metric.CMetricIRing;
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
  * It's Definition is similar to the Definition of Line in the Tensor Package.
  * It is capable of Interval Arithmetic and can be used for Error Estimations
  * and fuzzy Calculations:
  *
  * It has some new Methods: instead of equals it also defines contains()
  *
  * Computing with Intervals that include 0 are a Problem,
  * because their Inverse doesn't fit into this Metaphor,
  * since it includes Infinity which has two Expressions in the affine Model: 
  * positive Infinity and negative Infinity 
  *
  * Therefore the projective IntervalP is derived,
  * where Left  > Right is defined to include Infinity!
  * The affine Definition of an Interval is easier and faster,
  * but it is not possible to define the Inverse of Numbers that include 0!
  *
  * To reduce a Join to a single Interval is no Problem, because for disjoint Sets
  * it results in the empty Set and otherwise a contiguous Set is returned.
  *
  * The Union cannot be represented in a closed Expression, because for disjoint Sets
  * it needs both Sets to represent the whole, so you end up with Fragments!
  * A Union will thus often stay a combined Expression just like algebraic Expressions with Varibles.
  *
  * Design Decisions:
  * Instead of introducing a boolean Flag
  * that switches between affine and projective Geometry everywhere,
  * a new Class is derived. This is more object oriented!
  *
  * This Class is most similar to Complex!
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
	public		 class IntervalA
//	final public class IntervalA
	extends AMetricBody
//	extends IntervalOrd	//this Class also reorders so that Min = Left and Max = Right
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Left  Value of the IntervalA	 */
	protected IMetricIRing Left ;

	/**Right Value of the IntervalA	 */
	protected IMetricIRing Right;

	/**Cache for the Test on positive Values	 */
	protected boolean positive;

	/**Cache for the Test on negative Values	 */
	protected boolean negative;

	/**Local Object for consistent Argument Treatment in convertArg()	 */
	protected IntervalA arg_ = new IntervalA();
	
	/**Returns the left Part of the IntervalA Number.	 */
	public IMetricIRing Left (){return Left ;}

	/**Returns the right Part of the IntervalA Number.	 */
	public IMetricIRing Right(){return Right;}

	/**Returns the conjugate IntervalA Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt(){return this;}

	/**Overrides the false Value from the Implementation in absComplex	 */
	public boolean isComplex(){return false;}

	/**Switches Checking for real Results on or off.
	 * Since you can not expect to be a result real, it is typically switched on. 	 */
//	public static boolean bolLazySimplify = true; //false;


	//////////////////////
	//	Constructors	//
	//////////////////////

	/**Initializes this Class AFTER the Constructor
	 * (for before Initialization you would use an empty Constructor)	 */
	protected void init() {
		if (Left .isMoreThan(Right)) swapAt();	//this should normally not be necessary
		positive = Left .positive();		//caching the Results because frequently used!
		negative = Right.negative();	//caching the Results because frequently used!
	}

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public IntervalA(IntervalA arg)	{	//copyAt(arg);	//...the same, only faster:
		Left  = (IMetricIRing) arg.Left .copy();
		Right = (IMetricIRing) arg.Right.copy();
		init();
	}

	/**Constructor that takes Constants as Input for left and right Part.
	 * Those Objects should be of the same Type to speed up calculation
	 * or even enabling it (when e.g. one is not ICountAble).
	 * This prevents the indirect Change of the Left  and Imaginary Part,
	 * but that is due to Performance Reasons.
	 * Don't make Copies of these Elements,
	 * since these Copies are no Constants anymore!	 */
	public IntervalA(CMetricIRing Min_, CMetricIRing Max_) {	//
		Left  = Min_;	//an IntervalA where Left  > Right is defined to include Infinity!
		Right = Max_;
		init();
	}

	/**Constructor that takes any Object as Input for real and imaginary Part.
	 * Those Objects should be of the same Type to speed up calculation
	 * or even enabling it (when e.g. one is not ICountAble).	 */
	public IntervalA(Object Min_, Object Max_) {
		boolean MinC;
		boolean MaxC;
		Object buf = null;
		if (MinC = Min_ instanceof IntervalA) {
			buf = Max_;
			Max_ = ((IntervalA) Min_).Right;
			Min_ = ((IntervalA) Min_).Left ;
		}
		if (MaxC = Max_ instanceof IntervalA) {
			buf = Min_;
			Min_ = ((IntervalA) Max_).Left ;
			Max_ = ((IntervalA) Max_).Right;
		}
		Left  = (IMetricIRing) ((ICopyAble)Min_).copy();
		Right = (IMetricIRing) ((ICopyAble)Max_).copy();
		if (MinC & MaxC) throw new AbstractMethodError();
		if (MinC) Right.MaxAt(buf);
		if (MaxC) Left .MinAt(buf);
		init();
	}

	/**Constructor that takes any Object as Input for the real Part.
	 * The imaginary Part is set to 0.	 */
	public IntervalA(Object arg) {
		if (arg instanceof IntervalA) { //this((IntervalA) arg); {
			Left  = (IMetricIRing) ((IntervalA) arg).Left .copy();
			Right = (IMetricIRing) ((IntervalA) arg).Right.copy();
		} else if (arg instanceof ICopyAble) {
			Left  = (IMetricIRing) ((ICopyAble) arg).copy();
			Right = (IMetricIRing) ((ICopyAble) arg).copy();	//Choose the same type -> faster
//			Right = null;	//considerable Performance Increase for exact Values
		} else {
			Left  = new BodyDouble(ByRefDouble.GET_DOUBLE(arg));
			Right = new BodyDouble(ByRefDouble.GET_DOUBLE(arg));	//Choose the same type -> faster
//			Right = null;	//considerable Performance Increase for exact Values
		}
		init();
	}

	/**Constructor that takes a double as Input for the Left .
	 * The Right is set to the same Number.
	 * The Types are defaulted to BodyDouble.	 */
	public IntervalA(double Min_) {
		Left  = new BodyDouble(Min_);
		Right = new BodyDouble(Min_);	//Choose the same type -> faster
//		Right = null;	//considerable Performance Increase for exact Values
		init();
	}

	/**Constructor that takes a double as Input for the real Part.
	 * The imaginary Part is set to 0.
	 * The Types are defaulted to BodyDouble.	 */
	public IntervalA(double Min_, double Max_) {
		Left  = new BodyDouble(Min_);
		Right = new BodyDouble(Max_);
		init();
	}

	/**Constructor that takes an int as Input for the Left  Part.
	 * The imaginary Part is set to 0.	 */
/*	public IntervalA(int Min_) {
		Left  = new RingLong(Min_);
		Right = new RingLong(Min_);	//Choose the same type -> faster
		init();
	}
*/
	/**TODO: incorporate this,
	 * although it is very unlikely to calculate with complex Integers.	 */

	/**Empty Constructor (for newInstance Method).
	 * Does not create Dummy Objects for it's Constituents.
	 * So those Objects are not well-defined, but contain Null Pointers.
	 * This Constructor must not be public,
	 * because the Type of it's Parts must be defined!	 */
	protected IntervalA() {
//		BaseAccuracyInv = SqRtMaxValue();
//		BaseAccuracy = BaseAccuracyInv.inv();
//		Left  = new BodyDouble();
//		Right = new BodyDouble();
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
	public boolean equals (Object arg) {
		if (arg instanceof IntervalA)
			 return Left .equals(((Interval)arg).Left ) &&
					Right.equals(((Interval)arg).Right);
		if (!  Left.equals(Right)) return false;
		return Left.equals(arg); }

	/**less: '<' Returns True, when 'Self' < arg
	 * Assumption: Left < Right < arg.Left < arg.Right	 */
	public boolean isLessThan (Object arg) {
		convertArg(arg); return Right.isLessThan(arg_.Left );}	//exploiting the Orderedness!

	/**greater: '>' Returns True, when 'Self' > arg
	 * Assumption: Right > Left > arg.Right > arg.Left
	 * This is necessary, since overlapping Intervals
	 * none of them is less or greater than the other one. 	 */
	public boolean isMoreThan (Object arg) {
		convertArg(arg); return Left .isMoreThan(arg_.Right);}	//exploiting the Orderedness!

	/**overlaps: Returns True, when 'this' Interval overlaps with the Argument.	 */
	public boolean overlaps(Object Arg) {
		if (Arg instanceof IntervalA) {
			IntervalA arg = (IntervalA) Arg;
			return	contains(arg.Left ) ||
					contains(arg.Right) ||
				arg.contains(	 Left ) ||
				arg.contains(	 Right);
//				arg.overlaps(this);	//last Term would lead to an infinite Recursion
		}
/*		return (	Left .grtr (arg.Left ) ^	//this is equivalent, but faster
					Right.grtr (arg.Left )) ||	//contains(arg.Left ) ||
			   (	Left .grtr (arg.Right) ^
					Right.grtr (arg.Right)) ||	//contains(arg.Right) ||
			   (arg.Left .grtr (	Left ) ^
				arg.Right.grtr (	Left )) ||
			   (arg.Left .grtr (	Right) ^
				arg.Right.grtr (	Right));
*/		return contains(Arg);
	}

	/**contains: Returns True, when 'this' Interval contains the Argument.
	 * This corresponds to the 'between(a, b)' Method of 'arg'.	 */
	public boolean contains(Object arg) {
		if (arg instanceof Interval)
			return	contains(((Interval) arg).Left ) &&
					contains(((Interval) arg).Right);
		return Left .isMoreThan(arg) ^
			   Right.isMoreThan(arg);
//		return ((OrderAble) arg).between(Left, Right);
	}


	//////////////////////////
	//	Arithmetic Methods	//
	//////////////////////////

	/**Setting to 0 in Place: exact	 */
	public IGroup zeroAt() {
		Left .zeroAt();
		Right.zeroAt();
		positive = negative = false;
		return this; }

	/**Setting to 1 in Place: exact	 */
	public IGroupM oneAt() {
		Left .oneAt();
		Right.oneAt();
		positive = negative = true;
		return this; }

	/**Returns the Constant Pi = 3.14159265359... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt() {
		((MetricBody)Left ).piAt();
		((MetricBody)Right).piAt();
		positive = negative = true;
		return this; }

	/**Testing for 0:	 */
	public boolean isZero(){return Left .isZero() &&
								   Right.isZero();}

	/**Returns an Integer, not a IntervalA, this also saves time in further Calculations!	 */
	public IMetricIRing FloorAt() {
		Left .FloorAt();
		Right.FloorAt();
		if (! Left .equals(Right)) throw new AbstractMethodError();
		return this; } //Left ;

	/**Returns an Integer, not a IntervalA, this also saves time in further Calculations!	 */
	public IIntRing IntAt() {
		Left .IntAt();
		Right.IntAt();
		if (! Left .equals(Right)) throw new AbstractMethodError();
		return this; } //Left ;

	/**Returns +Infinity = 1/0	 */
	public IWellOrder maxValueAt() {
		Left .maxValueAt();
		Right.maxValueAt();
		positive = negative = true;
		return this; }


	//////////////////////////////
	//	Arithmetic Operations	//
	//////////////////////////////

	/**Helper Routine to convert to IntervalA from any other numeric Type:
	 * RingLong, Number or ICountAble.
	 * Uses ASemiGroup.getLong to do that.
	 * Using this Helper Routine generates Overhead,
	 * because the special optimizations for integer Values are not considered.	 */
	private final void	  convertArg (Object arg) {
		//	private final IntervalA convertArg (Object arg) {
		if (arg instanceof IntervalA) {
			arg_.Left  = ((IntervalA) arg).Left ;
			arg_.Right = ((IntervalA) arg).Right;
		}else{
			arg_.Left  = (IMetricIRing) arg;
			arg_.Right = (IMetricIRing) arg;
//			arg_.Right = null;	//considerable Performance win!
		}
	}

	/**Swaps Left  and Right	 */	protected void swapAt()
	{IMetricIRing tmp = Left ; Left  = Right; Right = tmp;}


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
		convertArg(arg);
		Left .addAt(((IntervalA)arg).Left );
		Right.addAt(((IntervalA)arg).Right);
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
		convertArg(arg);
		Left .subAt(((IntervalA)arg).Left );
		Right.subAt(((IntervalA)arg).Right);
//		--AddOptimizations;
		return this; }

	/**Multiplication in Place: *=
	 * assumes null to be 1	 */
	public ISemiGroupM mulAt(Object arg)	{	//Check if the Argument is a scalar, before converting it to a IntervalA
//		++MulOperations;
//		++MulOptimizations;
		if ((arg ==  ICountAble.One) ||
			(arg == CCountAble.One) ||
			(arg == BodyDouble.One) ||
			(arg == null)) return this;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero)
			 ) { zeroAt(); return this; }
		if ((arg ==  ICountAble._One) ||
			(arg == CCountAble._One) ||
			(arg == BodyDouble._One)
			 ) { negAt(); return this; }
		if  (arg instanceof IntervalA) {
			IntervalA arg_ = (IntervalA) arg;
			boolean Anegative = arg_.negative();
			boolean Apositive = arg_.positive();
			boolean  negative =		 negative();
			boolean  positive =		 positive();
			if (! ((Anegative || Apositive) &&
				   ( negative ||  positive)))
			{	//calculate all possible Products and choose the Left  and Right.
//				throw new AbstractMethodError();	//Multiplication by Zero! Loss of Accuracy
				IMetricIRing t00 = (IMetricIRing) Left .mul  (arg_.Left );
												Left .mulAt(arg_.Right);
				IMetricIRing t10 = (IMetricIRing) Right.mul  (arg_.Left );
												Right.mulAt(arg_.Right);
				if (Left .isMoreThan (Right)) swapAt();
				if (t00.isLessThan (Left )) Left .copyAt(t00); else
				if (t00.isMoreThan (Right)) Left .copyAt(t00);
				if (t10.isLessThan (Left )) Left .copyAt(t10); else
				if (t10.isMoreThan (Right)) Left .copyAt(t10);
				return this; }		//Tausch verhindern
			if (positive) {
				if (Apositive) {
					Left .mulAt(arg_.Left );
					Right.mulAt(arg_.Right);} else
				if (Anegative) {
					Left .mulAt(arg_.Right);
					Right.mulAt(arg_.Left ); swapAt();}	//tauschen
			} else if (negative) {
				if (Anegative) {
					Left .mulAt(arg_.Left );
					Right.mulAt(arg_.Right); swapAt();} else	//tauschen
				if (Apositive) {
					Left .mulAt(arg_.Right);
					Right.mulAt(arg_.Left );}
			}
		} else { 	//exact Argument
			if (((IScalarMetric) arg).negative()) swapAt();
			Left .mulAt(arg);
			Right.mulAt(arg);
		}
//		--MulOptimizations;
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
			(arg == BodyDouble.One) ||
			(arg == null)) return this;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero)
			 ) { InfinityAt(); return this; }
		if ((arg ==  ICountAble._One) ||
			(arg == CCountAble._One) ||
			(arg == BodyDouble._One)
			 ) { negAt(); return this; }
		if  (arg instanceof IntervalA) {
			IntervalA arg_ = (IntervalA) arg;
			boolean  negative =		 negative();
			boolean  positive =		 positive();
			boolean Anegative = arg_.negative();
			boolean Apositive = arg_.positive();
			if (! ((Anegative || Apositive) &&
				   ( negative ||  positive)))	//if not both Arguments are definite...
			{	//calculate all possible Products and choose the Left  and Right.
//				throw new AbstractMethodError();	//Division by Zero! Loss of Accuracy
				IMetricIRing t00 = (IMetricIRing) Left .div  (arg_.Left );
												Left .divAt(arg_.Right);
				IMetricIRing t10 = (IMetricIRing) Right.div  (arg_.Left );
												Right.divAt(arg_.Right);
				if (Left .isMoreThan (Right)) swapAt();
				if (t00.isLessThan (Left )) Left .copyAt(t00); else
				if (t00.isMoreThan (Right)) Left .copyAt(t00);
				if (t10.isLessThan (Left )) Left .copyAt(t10); else
				if (t10.isMoreThan (Right)) Left .copyAt(t10);
			} else //for definite Arguments, the Result is definite!
			if (positive) {
				if (Apositive) {
					Left .divAt(arg_.Right);
					Right.divAt(arg_.Left );}
				if (Anegative) {
					Left .divAt(arg_.Left );
					Right.divAt(arg_.Right); swapAt();}	//tauschen
			} else if (negative) {
				if (Anegative) {
					Left .divAt(arg_.Right);
					Right.divAt(arg_.Left ); swapAt();}	//tauschen
				if (Apositive) {
					Left .divAt(arg_.Left );
					Right.divAt(arg_.Right);}}
		}
		else {	//exact Argument
			if (((IScalarMetric) arg).negative()) swapAt();
			Left .divAt(arg);
			Right.divAt(arg);}
//		--MulOptimizations;
		return this; }

	/**Inversion in Place: 1/x
	 * If 0 is included, the Result MUST include Infinity!	 */
	public IGroupM invAt() {
		if (!(positive() || negative)) throw new AbstractMethodError();	//Inversion of Zero! Loss of Accuracy!
		Right.invAt();
		Left .invAt(); swapAt();	//always swap
		return this; }


	//virtual Methods of Object

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		IntervalA tmp = new IntervalA(new BodyDouble(), new BodyDouble());
		tmp.Left  = (IMetricIRing) ((ICopyAble)Left ).newInstance();
		tmp.Right = (IMetricIRing) ((ICopyAble)Right).newInstance();
		return tmp;	}

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Constituents.
	 * It keeps the current reference, so any Argument has to be converted to IntervalA!	 */
	public ICopyAble copyAt(Object arg, int Depth)
	{	//DeepCopy, ripples through, by using copyAt() on all Elements.
//		super.CopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		convertArg(arg);
		Right.copyAt(arg_.Right, --Depth);	//Right = (IntervalA) arg_.Right.copy();	//Doesn't matter,
		Left .copyAt(arg_.Left ,   Depth);	//Left  = (IntervalA) arg_.Left .copy();	//only for Performance!
		return this; }

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg)
	{	//don't rely on the Argument being a IntervalA
//		super.shallowCopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		convertArg(arg);
		if ((Left  instanceof CCopyAble) ||
			(Right instanceof CCopyAble)) throw new AbstractMethodError(CCopyAble.strConst);
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
	public int hashCode(){return Right.hashCode() + Left .hashCode() ;}

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
		return "[" + Left .toString() + ", " + Right.toString() + "]"; }
//		return Starter + Left .toString() + Separator + Right.toString() + Stopper; }

	/**Parses the String with the full Description to a IntervalA Number.
	 * of two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation. */
	public ICopyAble fromStreamAt(IDeserializer arg)
		throws java.io.IOException {
		Left .fromStreamAt(arg);
		Right.fromStreamAt(arg);
/*		String[] List = parseList(arg);
		Left .fromStringAt(List[0]);
		Right.fromStringAt(List[1]);
*/		return this; }

	//These are Optimizations:

	/**Negation in Place: -
	 * always swaps	 */
	public IGroup negAt	   (){Left .negAt(); Right.negAt(); swapAt(); return this;}

	/**Double in Place: x+=x == x*=2
	 * never swaps	*/
	public ISemiGroup dblAt (){Left .dblAt(); Right.dblAt(); return this;}

	/**Triple in Place: x+=x+x == x*=3
	 * never swaps	*/
	public ISemiGroup trplAt(){Left .trplAt(); Right.trplAt(); return this;}

	/**Decrements the Value by 1
	 * never swaps	*/
	public integer dec(){Left .dec(); Right.dec(); return this;}

	/**Inrements the Value by 1
	 * never swaps	*/
	public integer inc(){Left .inc(); Right.inc(); return this;}

	/**Residual in Place: 1-x
	 * always swaps	 */
	public integer ResidAt(){Left .ResidAt(); Right.ResidAt(); swapAt(); return this;}

	/**Returns true only if the Number is completely negative, i.e. Left  < Right < 0	 */
	public boolean negative(){return (Right.negative());}

	/**Returns true only if the Number is completely positive, i.e. Right > Left  > 0	 */
	public boolean positive(){return (Left .positive());}

	/**Returns the exact two in Place.	 */
	public IIntRing twoAt()  {Left .twoAt  (); Right.twoAt  (); return this;}

	/**Returns the exact three in Place.	 */
	public IIntRing threeAt(){Left .threeAt(); Right.threeAt(); return this;}

	/**Half in Place: x/=2	*/
	public IIntRing halfAt (){Left .halfAt (); Right.halfAt ();return this;}

	/**Third in Place: x/=3	*/
	public IIntRing thirdAt(){Left .thirdAt(); Right.thirdAt();return this;}

	/**Square in Place: x^2 == x*=x
	 * Always gives positive Results, but if the Number includes 0,
	 * the Result must always include 0 too.	 */
	public ISemiGroupM sqrAt() {
		boolean negative =  negative();
		if (!  (negative || positive())) throw new AbstractMethodError();
		Left .sqrAt();
		Right.sqrAt(); if (negative) swapAt();
		return this; } 	//No ggT for Left  or Right => only rounding.

	/**Cubic in Place: x*=x^3	 */
	public ISemiGroupM cbcAt() {
		Left .cbcAt();
		Right.cbcAt();
		return this; }

	/**Checks if the IntervalA Number is even.
	 * i.e. both real and imaginary part are even	 */
	public boolean isEven() {return Left .equals(Right) && Right.isEven();}

	/**Checks if the IntervalA Number is odd.
	 * i.e. both real and imaginary part are odd	 */
	public boolean isOdd () {return Left .equals(Right) && Right.isOdd ();}

	//left out: ModAtDivAt, ModlAt, kgV, ggT, IntAt == FloorAt

	/**Returns the Square Root of this in Place: x^=.5	*/
	public IMetricIRing SqRtAt()	{
		if (!positive()) throw new AbstractMethodError();
		Left .SqRtAt();
		Right.SqRtAt();
		return this; }

	/**absolute Value in Place:				 |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsVAt() {	//((CopyAble)Left ).ShallowCopyAt(AbsV());
		((IGroup)	Left.AbsVAt())
			.addAt(Right.AbsVAt());
		Right.copyAt(Left);	//still keep this a sensible Representation!
		return (IScalarMetric) Left;
//		return this;
	}

	/**absolute Value: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsV()//{return (Group) ((MetricIRing)SqrAbsV()).SqRt();}
	{return (IScalarMetric)((IGroup)Left .AbsV()).addAt(Right.AbsV());}

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
		if (! Right.equals(Left)) throw new AbstractMethodError();
		return ((IMeasurAble)Left ).getDouble(); }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float   getFloat() {
		if (! Right.equals(Left)) throw new AbstractMethodError();
		return ((IMeasurAble)Left ).getFloat(); }

//	public SemiMonoid mapAt(SemiMonoid arg) { arg.copyAt(this); return arg; }

	/**Tests the Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		IntervalA test = new IntervalA(new BodyDouble(), new BodyDouble());
		testInstance = test;	//defined in ACopyAble to test the abstract Methods
	}

}
