package streamIO.copy.group.ring.metric.body;

import streamIO.copy.group.ring.metric.IMetricIRing;

/** Defines a projective Interval in a fully ordered Scalar (1-dim) Metric Space.
  * It's Definition is similar to the Definition of Line in the Tensor Package.
  * It is capable of Interval Arithmetic
  * and can be used for Error- and fuzzy Calculations:
  *
  * It has some new Methods: instead of equals it also defines contains()
  *
  * Computing with Intervals that include 0 are a Problem,
  * because their Inverse doesn't fit into this Metaphor,
  * since it includes Infinity.
  *
  * Therefore IntervalP is derived,
  * where Left > Right is defined to include Infinity!
  * The affine Definition of an Interval is easier and faster,
  * but it is not possible to define the Inverse of Numbers that include 0!
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
  */
final public class IntervalP
extends IntervalA {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Left Value of the IntervalP	 */
	protected IMetricIRing Left;

	/**Right Value of the IntervalP	 */
	protected IMetricIRing Right;

	/**Minimum (not absolute) Value of the IntervalP
	 * for Performance Increase in projective Calculations	 */
	protected IMetricIRing Min;

	/**Maximum (not absolute) Value of the IntervalP
	 * for Performance Increase in projective Calculations	 */
	protected IMetricIRing Max;

	/**Switches Checking for real Results on or off.
	 * Since you can not expect to be a result real, it is typically switched on. 	 */
//	public static boolean bolLazySimplify = true; //false;

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**Initializes this Class AFTER the Constructor
	 * (before Initialization you would use an empty Constructor)	 */
	protected void init() {
		positive = Left .positive() &&
				   Right.positive();	//caching the Results because frequently used!
		negative = Left .negative() &&
				   Right.negative();	//caching the Results because frequently used!
	}

}
