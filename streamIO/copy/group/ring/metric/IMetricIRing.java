package streamIO.copy.group.ring.metric;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.IIntRing;

/**Interface that integrates the algebraic Operations
 * with the Order and Metrics of a Metric Space.
 * This is the most fundamental Class for all further Operations,
 * because it contains all arithmetic and metric Operations.
 * Only the Name is pretty long. The Alternatives are:
 * 13: MetricIntRing
 * 11: MetricIRing
 * 10: MetricRing
 *  8: MIntRing
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 7906308a2b5d9a05be0be2312fd657a730c83d80d8600af1f335f6bcd3351061
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IMetricIRing
extends IScalarMetric, //The Distinction between ScalarMetric and Metric are solved better in C++!
IIntRing,
IWellOrder {

	/**Returns the Square Root of the MaxValue in Place.
	 * Since MaxValue is a Constant, this should be redefined
	 * in each concrete Child Class.	 */
	public IMetricIRing SqRtMaxValueAt();

	/**Returns the Square Root of the MaxValue in Place.
	 * Since MaxValue is a Constant, this should be redefined
	 * in each concrete Child Class.	 */
	public IMetricIRing SqRtMaxValue();

	/**Multiplies the absolute Value of this Number by the Accuracy in Place
	 * in the most effective Way	 */
	public IMetricIRing mulAbsAccuracyAt();

	/**Multiplies the absolute Value of this Number by the Accuracy
	 * in the most effective Way	 */
	public IMetricIRing mulAbsAccuracy();

	/**Multiplies this Number by the Accuracy in Place in the most effective Way	 */
	public IMetricIRing mulAccuracyAt();

	/**Multiplies this Number by the Accuracy in the most effective Way	 */
	public IMetricIRing mulAccuracy();

	/**Divides the absolute Value of this Number by the Accuracy in Place
	 * in the most effective Way	 */
	public IMetricIRing divAbsAccuracyAt();

	/**Divides the absolute Value of this Number by the Accuracy
	 * in the most effective Way	 */
	public IMetricIRing divAbsAccuracy();

	/**Divides this Number by the Accuracy in Place in the most effective Way	 */
	public IMetricIRing divAccuracyAt();

	/**Divides this Number by the Accuracy in the most effective Way	 */
	public IMetricIRing divAccuracy();

	/**Returns the Accuracy to compare it directly with Coefficienst
	 * for Convergence Check of Functions with O(1)	 */
	public Object Accuracy();

	/**Returns the Inverse of the Accuracy to compare it directly with Coefficienst
	 * for Convergence Check of function with O(1)	 */
	public Object AccuracyInv();

	/**Sets the Number of significant Bits for Accuracy	 */
	public void setMaxAccuracyBits(int AccBits);

	/**Sets the Number of significant Bits for Accuracy	 */
	public void setAccuracyBits(int AccBits);

	/**Returns the Number of significant Bits for Accuracy	 */
	public int getAccuracyBits();

	/**Returns the Number of significant Bits for Accuracy	 */
	public int getMaxAccuracyBits();

	/**Returns true when 'this' is similar to arg,
	 * i.e. this is about the same as arg.
	 * The Criterion is that |this-arg| <= Accuracy*(|this| + |arg|)	 */
	public boolean isSimilar(IMetricIRing arg);

	/**Returns the Square Root of this: x^.5	 */	public IMetricIRing SqRt();

	/**Returns the Square Root of this: x^.5	 */	public IMetricIRing CbcRt();

	/**Returns the Cubic Root of this in Place: x^=.333333333333333	 */
	public IMetricIRing CbcRtAt();

	/**Returns the Square Root of this in Place: x^=.5	*/	public IMetricIRing SqRtAt();

	/**Declaration of mMulAt(), which is intended for Manifold Multiplication
	 * Already used StepRKQ.	 */
	public IMetricIRing mMulAt(Object arg);

	/**Evaluates the continuous Fraction (1/(1/.../n))
	 * Wertet die Kettenbruch-Entwicklung (1/(1/.../n)) aus.	 */
//	public static void contFraction  (IIntRing[] Kette, IIntRing Numerator, IIntRing Denominator);

	/**Generates the best rational Approximation using continuous Fractions
	 * (1/(1/.../n)) with Accuracy Check.
	 * Erzeugt die beste rationale Approximation durch fortgesetzte
	 * Kettenbruch-Entwicklung (1/(1/.../n)) mit laufender Genauigkeits-Kontrolle.	 */
	public void rational (IIntRing GanzZahl, IIntRing Zaehler, IIntRing Nenner);

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function.
	 * If H is null (not given), it is assumed to 1. */
	public IMetricIRing Delta1(Object H);

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function.
	 * If H is null (not given), it is assumed to 1. */
	public IMetricIRing Delta1At(Object H);

	/**Continuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	public IMetricIRing Step1(Object H);

	/**Continuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	public IMetricIRing Step1At(Object H);

	/**Discontinuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	public IMetricIRing Step0();

	/**Discontinuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	public IMetricIRing Step0At();

	//////////////////////////////////////
	//	Routines for new Result Types	//
	//////////////////////////////////////

	/**Returns the largest (closest to positive infinity) value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing FloorAt();

	/**Returns the largest (closest to positive infinity) value,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing Floor();

	/**Returns the smallest (closest to negative infinity) value in Place,
	 * that is not less than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing CeilAt ();

	/**Returns the smallest (closest to negative infinity) value,
	 * that is not less than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing Ceil ();

	/**Returns the Remainder of the Division by Modulus in Place.
	 * The Remainder is centered around 0 and allows for Rescaling of periodic Functions.
	 * This is different to the Modl() Method, which returns the signed Modulus,
	 * which is mirrored around 0 and not periodic.
	 * Calculating the Remainder by iteratively subtracting the Modulus
	 * is about 10 times faster and more accurate
	 * than calculating it by Division like in ModDivAt or ModAtDivAt.	 */
	public IMetricIRing RemAt(Object Modulus);

	/**Returns the Remainder of the Division by Modulus.
	 * The Remainder is centered around 0.	 */
	public IMetricIRing Rem(Object Modulus);

	/**Returns the Remainder and the Quotient of the Division by Modulus.
	 * The Remainder is periodic and centered around 0.	 */
	public IMetricIRing RemAtDivAt(Object Modulus, ICopyAble Div);

	/**Returns the Remainder and the Quotient of the Division by Modulus.
	 * The Remainder is periodic and centered around 0.	 */
	public IMetricIRing RemDivAt(Object Modulus, ICopyAble Div);

	/**Returns the Fractional Part of this Number in Place,
	 * and the rounded integer Part in Object.
	 * The Remainder is centered around 0!	 */
	public IMetricIRing roundAtIntAt(ICopyAble Int);

	/**Returns the Fractional Part of this Number,
	 * and the integer Part in Object.
	 * The Remainder is centered around 0!	 */
	public IMetricIRing roundIntAt(ICopyAble Int);

	/**Returns the closest Integer to the argument
	 * i.e. the rounded integer Part of this Number in Place.	 */
	public IMetricIRing roundAt();

	/**Returns the closest Integer to the argument
	 * i.e. the rounded integer Part of this Number.	 */
	public IMetricIRing round();

	/**Returns the closest integer to the argument (as far as accuracy allows). 	 */

	/**Returns the Fractional Part of this Number in Place,
	 * and the rounded integer Part in Object.
	 * The Result is from [-0.5;+0.5]	 */
	public IMetricIRing RemAtIntAt(ICopyAble Int);

	/**Returns the Fractional Part of this Number,
	 * and the integer Part in Object.
	 * The Result is from [-0.5;+0.5]	 */
	public IMetricIRing RemIntAt(ICopyAble Int);

}
