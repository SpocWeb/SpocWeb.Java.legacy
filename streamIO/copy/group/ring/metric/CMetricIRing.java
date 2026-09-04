package streamIO.copy.group.ring.metric;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.CIntRing;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.order.IOrder;
import streamIO.exception.ReadOnlyException;

/**Implements Constants for all Types of IIntRing Classes.
 * This Class inhibits the Use of ...At() Routines
 * but still supports all other Methods of the IIntRing Class.
 * It prevents Class Proliferation by not separating out the copyAt() Interface	 */
public class CMetricIRing
extends CIntRing
implements IMetricIRing {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor	 */	public CMetricIRing(IMetricIRing cnst){super(cnst);}

	/**Returns the Square Root of the MaxValue in Place.
	 * Since MaxValue is a Constant, this should be redefined
	 * in each concrete Child Class.	 */
	public IMetricIRing SqRtMaxValueAt() {throw new ReadOnlyException(strConst);}

	/**Returns the Square Root of the MaxValue in Place.
	 * Since MaxValue is a Constant, this should be redefined
	 * in each concrete Child Class.	 */
	public IMetricIRing SqRtMaxValue()	{return ((IMetricIRing) inner).SqRtMaxValue();}

	/**Returns the Accuracy to compare it directly with Coefficienst
	 * for Convergence Check of Functions with O(1)	 */
	public Object Accuracy()			{return ((IMetricIRing) inner).Accuracy();}

	/**Returns the Inverse of the Accuracy to compare it directly with Coefficienst
	 * for Convergence Check of Functions with O(1)	 */
	public Object AccuracyInv()			{return ((IMetricIRing) inner).AccuracyInv();}

	/**Sets the Number of significant Bits for Accuracy	 */
	public void setMaxAccuracyBits(int AccBits)	{((IMetricIRing) inner).setMaxAccuracyBits(AccBits);}

	/**Sets the Number of significant Bits for Accuracy	 */
	public void setAccuracyBits(int AccBits)	{((IMetricIRing) inner).setAccuracyBits(AccBits);}

	/**Returns the Number of significant Bits for Accuracy	 */
	public int getAccuracyBits()		{return ((IMetricIRing) inner).getAccuracyBits();}

	/**Returns the Number of significant Bits for Accuracy	 */
	public int getMaxAccuracyBits()		{return ((IMetricIRing) inner).getMaxAccuracyBits();}

	/**Multiplies the absolute Value of this Number by the Accuracy in Place
	 * in the most effective Way	 */
	public IMetricIRing mulAbsAccuracyAt() {throw new ReadOnlyException(strConst);}

	/**Multiplies the absolute Value of this Number by the Accuracy
	 * in the most effective Way	 */
	public IMetricIRing mulAbsAccuracy()	{return ((IMetricIRing) inner).mulAbsAccuracy();}

	/**Multiplies this Number by the Accuracy in Place in the most effective Way	 */
	public IMetricIRing mulAccuracyAt() {throw new ReadOnlyException(strConst);}

	/**Multiplies this Number by the Accuracy in the most effective Way	 */
	public IMetricIRing mulAccuracy()	{return ((IMetricIRing) inner).mulAccuracy();}

	/**Divides the absolute Value of this Number by the Accuracy in Place
	 * in the most effective Way	 */
	public IMetricIRing divAbsAccuracyAt() {throw new ReadOnlyException(strConst);}

	/**Divides the absolute Value of this Number by the Accuracy
	 * in the most effective Way	 */
	public IMetricIRing divAbsAccuracy()	{return ((IMetricIRing) inner).divAbsAccuracy();}

	/**Divides this Number by the Accuracy in Place in the most effective Way	 */
	public IMetricIRing divAccuracyAt() {throw new ReadOnlyException(strConst);}

	/**Divides this Number by the Accuracy in the most effective Way	 */
	public IMetricIRing divAccuracy()	{return ((IMetricIRing) inner).divAccuracy();}

	/**Returns true when 'this' is similar to arg,
	 * i.e. this is about the same as arg.
	 * The usual Criterion is that |this-arg| <= Accuracy*(|this| + |arg|)	 */
	public boolean isSimilar(IMetricIRing arg)
	{return ((IMetricIRing) inner).isSimilar(arg);}

	/**Returns the Square Root of this: x^.5	 */
	public IMetricIRing SqRt()	{return ((IMetricIRing) inner).SqRt();}

	/**Returns the Square Root of this: x^.5	 */
	public IMetricIRing CbcRt()	{return ((IMetricIRing) inner).CbcRt();}

	/**Returns the Cubic Root of this in Place: x^=.333333333333333	 */
	public IMetricIRing CbcRtAt() {throw new ReadOnlyException(strConst);}

	/**Returns the Square Root of this in Place: x^=.5	*/
	public IMetricIRing SqRtAt() {throw new ReadOnlyException(strConst);}

	/**Declaration of mMulAt(), which is intended for Manifold Multiplication
	 * Already used StepRKQ.	 */
	public IMetricIRing mMulAt(Object arg)	{throw new ReadOnlyException(strConst);}

	/**Returns the largest (closest to positive infinity) value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing FloorAt() { throw new ReadOnlyException(strConst); }

	/**Returns the largest (closest to positive infinity) value,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing Floor() { return ((IMetricIRing) inner).Floor(); }

	/**Returns the smallest (closest to negative infinity) value in Place,
	 * that is not less than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing CeilAt() { throw new ReadOnlyException(strConst); }

	/**Returns the smallest (closest to negative infinity) value,
	 * that is not less than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing Ceil(){ return ((IMetricIRing) inner).Ceil(); }

	/**Division and Modulo Operation in Place:
	 * Using this to calculate both is faster than calculating both separately.
	 * The Quotient is rounded, so the Remainder is centered around 0.	 */
	public IMetricIRing RemAtDivAt(Object Modulus, ICopyAble Div) {throw new ReadOnlyException(strConst); }

	/**Division and Modulo Operation in Place:
	 * Using this to calculate both is faster than calculating both separately.
	 * The Quotient is rounded, so the Remainder is centered around 0.	 */
	public IMetricIRing RemDivAt(Object Modulus, ICopyAble Div){return ((IMetricIRing) inner).RemDivAt(Modulus, Div); }

	/**Returns the Fractional Part of this Number in Place,
	 * and the rounded integer Part in Object.
	 * The Result is from [-0.5;+0.5]	 */
	public IMetricIRing RemAtIntAt(ICopyAble Int) {throw new ReadOnlyException(strConst); }

	/**Returns the Fractional Part of this Number,
	 * and the integer Part in Object.
	 * The Result is from [-0.5;+0.5]	 */
	public IMetricIRing RemIntAt(ICopyAble Int) { return ((IMetricIRing) inner).RemIntAt(Int); }

	/**The Remainder of the Division by the Modulus is centered around 0.	 */
	public IMetricIRing RemAt(Object Modulus) {throw new ReadOnlyException(strConst); }

	/**The Remainder of the Division by the Modulus is centered around 0.	 */
	public IMetricIRing Rem(Object Modulus){return ((IMetricIRing) inner).Rem(Modulus); }

	/**Returns the Fractional Part of this Number in Place,
	 * and the rounded integer Part in Object.
	 * The Remainder is centered around 0!	 */
	public IMetricIRing roundAtIntAt(ICopyAble Int) { throw new ReadOnlyException(strConst); }

	/**Returns the Fractional Part of this Number,
	 * and the integer Part in Object.
	 * The Remainder is centered around 0!	 */
	public IMetricIRing roundIntAt(ICopyAble Int) { return ((IMetricIRing) inner).roundIntAt(Int); }

	/**Returns the closest Integer to the argument
	 * i.e. the rounded integer Part of this Number in Place.	 */
	public IMetricIRing roundAt() { throw new ReadOnlyException(strConst); }

	/**Returns the closest Integer to the argument
	 * i.e. the rounded integer Part of this Number.	 */
	public IMetricIRing round() { return ((IMetricIRing) inner).round(); }

	/**Evaluates the continuous Fraction (1/(1/.../n))
	 * Wertet die Kettenbruch-Entwicklung (1/(1/.../n)) aus.	 */
//  public static void contFraction  (IIntRing[] Kette, IIntRing Numerator, IIntRing Denominator);

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function.
	 * If H is null (not given), it is assumed to 1. */
	public IMetricIRing Delta1(Object H)	{return ((IMetricIRing) inner).Delta1(H);}

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function.
	 * If H is null (not given), it is assumed to 1. */
	public IMetricIRing Delta1At(Object H)	{throw new ReadOnlyException(strConst);}

	/**Continuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	public IMetricIRing Step1(Object H)	{return ((IMetricIRing) inner).Step1(H);}

	/**Continuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	public IMetricIRing Step1At(Object H) { throw new ReadOnlyException(strConst);}

	/**Discontinuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	public IMetricIRing Step0() { return ((IMetricIRing) inner).Step0();}

	/**Discontinuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	public IMetricIRing Step0At() { throw new ReadOnlyException(strConst);}

	//////////////////////////
	//  interface WellOrder	//
	//////////////////////////

	/**Sets and returns the minimum Value for this Class in Place.	 */
	public IWellOrder minValueAt() { throw new ReadOnlyException(strConst);}

	/**Returns the minimum Value for this Class.	 */
	public IWellOrder minValue() { return ((IWellOrder) inner).minValue();}

	/**Sets and returns the maximum Value for this Class in Place.	 */
	public IWellOrder maxValueAt() { throw new ReadOnlyException(strConst);}

	/**Returns the maximum Value for this Class.	 */
	public IWellOrder maxValue() { return ((IWellOrder) inner).maxValue();}

	/**Returns the minimum absolute Value for this Class.	 */
	public IWellOrder minAbsValue() { return ((IWellOrder) inner).minAbsValue();}

	/**Returns the minimum absolute Value (greater than Zero) for this Class in Place.	 */
	public IWellOrder minAbsValueAt() { throw new ReadOnlyException(strConst);}

	/**Returns the Representation of +Infinity for this Class in Place.	 */
	public IWellOrder InfinityAt() { throw new ReadOnlyException(strConst);}

	/**Returns the Representation of +Infinity for this Class.	 */
	public IWellOrder Infinity() { return ((IWellOrder) inner).Infinity();}

	/**Returns the Representation of -Infinity for this Class.	 */
	public IWellOrder NegInfinityAt() { throw new ReadOnlyException(strConst);}

	/**Returns the Representation of -Infinity for this Class.	 */
	public IWellOrder NegInfinity() { return ((IWellOrder) inner).NegInfinity();}

	/**Returns the Representation of an invalid Number for this Class in Place.	 */
	public IWellOrder NaNAt() { throw new ReadOnlyException(strConst);}

	/**Returns the Representation of an invalid Number for this Class.	 */
	public IWellOrder NaN() { return ((IWellOrder) inner).NaN(); }

	/**Local Cache for the Result of this Test	 */
	protected boolean infinite = ((IWellOrder) inner).isInfinite();

	/**Returns the Representation of Infinity for this Class.	 */
	public boolean isInfinite() { return infinite;}

	/**Local Cache for the Result of this Test	 */
	protected boolean nan = ((IWellOrder) inner).isNaN();

	/**Returns the Representation of an invalid Number for this Class.	 */
	public boolean isNaN() { return nan; }

	/**This Distance Function defines a Metric on the Elements of IMetric Type.	 */
	public IMetricIRing Dist(Object arg) { return ((IMetricIRing) inner).Dist(arg);}

	/**p-Metric: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Dist (Object arg, double p) { return ((IMetricIRing) inner).p_Dist(arg, p);}

	/**Absolute Value-Metric:
	 * Special Case of the p-Metric for p = 1	 */
	public IMetricIRing AbsV_Dist (Object arg) { return ((IMetricIRing) inner).AbsV_Dist(arg);}

	/**Maximums-Metric
	 * Special Case of the p-Metric for p -> Infinity	 */
	public IMetricIRing Max_Dist (Object arg) { return ((IMetricIRing) inner).Max_Dist(arg);}

	/**(Euklidische Metric)^2
	 * Special Case of the p-Metric for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrDist(Object arg) { return ((IMetricIRing) inner).SqrDist(arg);}


	//////////////////////
	//  interface Norm	//
	//////////////////////

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Norm (double p) { return ((INorm) inner).p_Norm(p);}

	/**Betrags-Norm:
	 * Special Case of the p-Norm for p = 1
	 * This norm is the fastest to chalculate	 */
	public IMetricIRing AbsV_Norm () { return ((INorm) inner).AbsV_Norm();}

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 */
	public IMetricIRing Max_Norm () { return ((INorm) inner).Max_Norm();}

	/**Euklidische Norm
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing Norm   () { return ((INorm) inner).Norm();}

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm() { return ((INorm) inner).SqrNorm();}


	//////////////////////////
	//  interface WellOrder	//
	//////////////////////////

	/**Returns the Maximum of both Operands in Place	 */
	public IOrder MaxAt (Object arg) { throw new ReadOnlyException(strConst);}

	/**Returns the Minimum of both Operands in Place	 */
	public IOrder MinAt (Object arg) { throw new ReadOnlyException(strConst); }

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (Object arg) { return ((IOrder) inner).isLessThan (arg); }

	/**between: returns True, when 'Self' is between arg1 and arg2	 */
	public boolean isBetween (Object arg1, Object arg2) {
		return ((IOrder) inner).isBetween(arg1, arg2);}

	/**greater: '>' Returns True, when 'Self' > arg	 */
	public boolean isMoreThan (Object arg) { return ((IOrder) inner).isMoreThan (arg);}

	/**greater or equal: '>=' Returns True, when 'Self' >= arg	 */
	public boolean notLessThan (Object arg) { return ((IOrder) inner).notLessThan (arg);}

	/**less or equal: '<=' Returns True, when 'Self' <= arg	 */
	public boolean notMoreThan (Object arg) { return ((IOrder) inner).notMoreThan (arg);}

	/**Returns the Maximum of both Operands	 */
	public IOrder Max (Object arg) { return ((IOrder) inner).Max (arg);}

	/**Returns the Minimum of both Operands	 */
	public IOrder Min (Object arg) { return ((IOrder) inner).Min (arg);}

	/**Returns the Position of this Object relative to arg:
	 * -1 for smaller, otherwise +1	 */
	public int Position(Object arg) { return ((IOrder) inner).Position(arg);}

	/**Returns the exact Position of this Object relative to arg:
	 * -1 for smaller, 0 for equal, otherwise +1	 */
	public int compareTo(Object arg) { return ((IOrder) inner).compareTo(arg);}


	//////////////////////////
	//  interface WellOrder	//
	//////////////////////////

	/**Returns the Sign of this Number	 */
	public int Sign()	{return ((IScalarMetric) inner).Sign();}

	/**Returns the Sign of this Number in Place	 */
	public IMetricIRing SignAt() {throw new ReadOnlyException(strConst);}

	/**Returns the Sign of this Number, but also 1 for 0	 */
	public int Zchn()	{return ((IScalarMetric) inner).Zchn();}

	/**Returns the Sign of this Number in Place, but also 1 for 0	 */
	public IMetricIRing ZchnAt() {throw new ReadOnlyException(strConst);}

	/**Returns the Position of this Number relative to arg in Place:
	 * -1 for smaller, otherwise +1	 */
	public IMetricIRing PositionAt(Object arg) {throw new ReadOnlyException(strConst);}

	/**Returns the exact Position of this Number relative to arg in Place:
	 * -1 for smaller, 0 for equal, otherwise +1	 */
	public IMetricIRing compareToAt(Object arg) {throw new ReadOnlyException(strConst);}

	/**Returns this Number multiplied by the Sign of arg	 */
	public IMetricIRing mulSign(Object arg)	{return ((IScalarMetric) inner).mulSign(arg);}

	/**Returns this Number multiplied in Place by the Sign of arg	 */
	public IMetricIRing mulSignAt(Object arg) {throw new ReadOnlyException(strConst);}

	/**Returns this Number multiplied by the Zchn of arg	 */
	public IMetricIRing mulZchn(Object arg)	{return ((IScalarMetric) inner).mulZchn(arg);}

	/**Returns this Number multiplied in Place by the Zchn of arg	 */
	public IMetricIRing mulZchnAt(Object arg) {throw new ReadOnlyException(strConst);}

	/**Returns true, if the arg has the opposite Zchn to this Number	 */
	public boolean changeZchn(Object arg)	{return ((IScalarMetric) inner).changeZchn(arg);}

	/**Returns true, if the arg has the opposite Sign to this Number	 */
	public boolean changeSign(Object arg)	{return ((IScalarMetric) inner).changeSign(arg);}

	/**Returns this Number set to the Sign of arg	 */
	public IMetricIRing setSign(Object arg)	{return ((IScalarMetric) inner).setSign(arg);}

	/**Returns this Number set in Place to the Sign of arg	 */
	public IMetricIRing setSignAt(Object arg)	{return ((IScalarMetric) inner).setSignAt(arg);}

	/**Returns this Number multiplied by the Zchn of arg	 */
	public IMetricIRing setZchn(Object arg)	{return ((IScalarMetric) inner).setZchn(arg);}

	/**Returns this Number set in Place to the Zchn of arg	 */
	public IMetricIRing setZchnAt(Object arg) {throw new ReadOnlyException(strConst);}

	/**Returns 'true' when this is a positive Number.	 */
	public boolean positive() { return ((IScalarMetric) inner).positive();}

	/**Returns 'true' when this is a negative Number.	 */
	public boolean negative() { return ((IScalarMetric) inner).negative();}


	//////////////////////
	//  Scalar Norm:	//
	//////////////////////

	/**absolute Value:			 |x|	*/	public IScalarMetric AbsV()	{return ((IScalarMetric) inner).AbsV();}
	/**absolute Value in Place:	 |x|	*/	public IScalarMetric AbsVAt() {throw new ReadOnlyException(strConst);}


	//////////////////////
	//  Scalar Metric:	//
	//////////////////////

	/**absolute Distance:			|x|	*/	public IScalarMetric AbsDist   (Object arg) { return ((IScalarMetric) inner).AbsDist(arg);}
	/**absolute Distance in Place:	|x|	*/	public IScalarMetric AbsDistAt (Object arg) { throw new ReadOnlyException(strConst);}


	/**Generates the best rational Approximation using continuous Fractions
	 * (1/(1/.../n)) with Accuracy Check.
	 * Erzeugt die beste rationale Approximation durch fortgesetzte
	 * Kettenbruch-Entwicklung (1/(1/.../n)) mit laufender Genauigkeits-Kontrolle.	 */
	public void rational (IIntRing GanzZahl, IIntRing Zaehler, IIntRing Nenner)
	{((IMetricIRing) inner).rational (GanzZahl, Zaehler, Nenner);}

}
