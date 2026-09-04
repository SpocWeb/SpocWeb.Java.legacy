package streamIO.copy.group.ring.metric.body;

import streamIO.copy.group.ring.IIntRing;

/**Operations for a Body that complement the basic Operation in IBody.
 *
 * Design Decisions:
 * All basic Operations are already defined in IIntRing.
 */
public interface Body
extends IBody, IIntRing {

	//////////////////
	//	Constants	//
	//////////////////

	//Constants Declarations moved to IIntRing
	/**Returns 1/2: 0.5	 */				//public MetricBody OneHalf ();
	/**Returns 1/2 in Place: 0.5	 */	//public MetricBody OneHalfAt ();

	/**Returns 1/3: 0.333333333...	 */			//public MetricBody OneThird();
	/**Returns 1/3 in Place: 0.33..	 */	//public MetricBody OneThirdAt();

	/**Returns 1/4: 0.25	 */			//public MetricBody OneQuarter ();
	/**Returns 1/4 in Place: 0.25	 */	//public MetricBody OneQuarterAt ();


	//////////////////////////////////////////////////////
	//	Operations dealing with Separation to Integers	//
	//////////////////////////////////////////////////////

	/**Returns the Fractional Part of this Number in Place,
	 * and the integer Part in Object.	 */
//	public MetricBody FracAtIntAt(copyAble Int);

	/**Returns the Fractional Part of this Number,
	 * and the integer Part in Object.	 */
//	public MetricBody FracIntAt(copyAble Int);

	/**Returns the Fractional Part of this Number in Place,
	 * and the rounded integer Part in Object.
	 * The Remainder is centered around 0!	 */
//	public MetricBody roundAtIntAt(copyAble Int);

	/**Returns the Fractional Part of this Number,
	 * and the integer Part in Object.
	 * The Remainder is centered around 0!	 */
//	public MetricBody roundIntAt(copyAble Int);

	/**Returns the closest Integer to the argument
	 * i.e. the rounded integer Part of this Number in Place.	 */
//	public MetricIRing roundAt();

	/**Returns the closest Integer to the argument
	 * i.e. the rounded integer Part of this Number.	 */
//	public MetricIRing round();

	/**Returns the smallest (closest to negative infinity) value in Place,
	 * that is not less than the argument
	 * and is equal to a mathematical integer. 	 */
//	public MetricIRing CeilAt ();

	/**Returns the Fractional Part of a float Number in Place.	 */
//	public MetricBody FracAt ();

	/**Returns the largest (closest to positive infinity) value,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
//	public MetricIRing Floor();

	/**Returns the smallest (closest to negative infinity) value,
	 * that is not less than the argument
	 * and is equal to a mathematical integer. 	 */
//	public MetricIRing Ceil ();

	/**Returns the Fractional Part of a float Number.	 */
//	public MetricBody Frac ();

	/**The Remainder of the Division by the Modulus is centered around 0.	 */
//	public MetricBody RemAt(MetricBody Modulus);

	/**The Remainder of the Division by the Modulus is centered around 0.	 */
//	public MetricBody Rem(MetricBody Modulus);

	/**Division and Modulo Operation in Place:
	 * Using this to calculate both is faster than calculating both separately.
	 * The Quotient is rounded, so the Remainder is centered around 0.	 */
//	public MetricBody RemAtDivAt(MetricBody Modulus, MetricBody Div);

	/**Division and Modulo Operation in Place:
	 * Using this to calculate both is faster than calculating both separately.
	 * The Quotient is rounded, so the Remainder is centered around 0.	 */
//	public MetricBody RemDivAt(MetricBody Modulus, MetricBody Div);
}
