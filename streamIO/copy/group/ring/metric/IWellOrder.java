package streamIO.copy.group.ring.metric;

import streamIO.copy.ICopyAble;

/**WellOrder:
 * Interface for a Class whose Objects are well and connex ordered
 * by Relations ">"resp."<".
 * Connex means that these Relations are defined for any two Elements.
 * I.e. there is a largest Element, which is also the maximum Element.
 * In Addition there are maximum and minimum Values for this Class.
 *
 * Design Decisions:
 * minValue is defined as the negative MaxValue.
 * This has two benefits for Integers:
 * 1) The Values form a true binary remainder class
 * 2) There is a special Value indicating 'Null' or 'Overflow'
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 6fbdadd20e58078adfbff656a05716d5f3364361cf9e7c359d744d447d4cc612
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IWellOrder
extends IIWellOrder, ICopyAble {

	/**Sets and returns the minimum Value for this Class in Place.	 */
	public IWellOrder minValueAt();

	/**Returns the minimum Value for this Class.	 */
	public IWellOrder minValue();

	/**Returns the maximum Value for this Class.	 */
	public IWellOrder maxValue();

	/**Returns the minimum absolute Value for this Class.	 */
	public IWellOrder minAbsValue();

	/**Returns the minimum absolute Value (greater than Zero) for this Class in Place.	 */
	public IWellOrder minAbsValueAt();

	/**Returns the Representation of +Infinity for this Class in Place.	 */
	public IWellOrder InfinityAt();

	/**Returns the Representation of +Infinity for this Class.	 */
	public IWellOrder Infinity();

	/**Returns the Representation of -Infinity for this Class.	 */
	public IWellOrder NegInfinityAt();

	/**Returns the Representation of -Infinity for this Class.	 */
	public IWellOrder NegInfinity();

	/**Returns the Representation of an invalid Number for this Class in Place.	 */
	public IWellOrder NaNAt();

	/**Returns the Representation of an invalid Number for this Class.	 */
	public IWellOrder NaN();

	/**Returns the Representation of Infinity for this Class.	 */
	public boolean isInfinite();

	/**Returns the Representation of an invalid Number for this Class.	 */
	public boolean isNaN();

}
