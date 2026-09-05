package streamIO.copy.group.ring.metric;

import streamIO.copy.CCopyAble;

/**Implements Constants for all Types of WellOrder Classes.
 * This Class inhibits the Use of ...At() Routines
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 6d722afe87e65f20e30fc1801e4187117200cea0f1bf38a8168e79c3008bda64
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * but still supports all other Methods of the WellOrder Class.	 */
public class CWellOrder
extends CCopyAble
implements IWellOrder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor	 */	public CWellOrder(IWellOrder cnst){super(cnst);}

	//////////////////////////
	//	interface WellOrder	//
	//////////////////////////

	/**Sets and returns the minimum Value for this Class in Place.	 */
	public IWellOrder minValueAt() {throw new AbstractMethodError(strConst);}

	/**Returns the minimum Value for this Class.	 */
	public IWellOrder minValue()	{return ((IWellOrder) inner).minValue();}

	/**Sets and returns the maximum Value for this Class in Place.	 */
	public IWellOrder maxValueAt() {throw new AbstractMethodError(strConst);}

	/**Returns the maximum Value for this Class.	 */
	public IWellOrder maxValue()	{return ((IWellOrder) inner).maxValue();}

	/**Returns the minimum absolute Value for this Class.	 */
	public IWellOrder minAbsValue()	{return ((IWellOrder) inner).minAbsValue();}

	/**Returns the minimum absolute Value (greater than Zero) for this Class in Place.	 */
	public IWellOrder minAbsValueAt() {throw new AbstractMethodError(strConst);}

	/**Returns the Representation of +Infinity for this Class in Place.	 */
	public IWellOrder InfinityAt() {throw new AbstractMethodError(strConst);}

	/**Returns the Representation of +Infinity for this Class.	 */
	public IWellOrder Infinity()	{return ((IWellOrder) inner).Infinity();}

	/**Returns the Representation of -Infinity for this Class.	 */
	public IWellOrder NegInfinityAt() {throw new AbstractMethodError(strConst);}

	/**Returns the Representation of -Infinity for this Class.	 */
	public IWellOrder NegInfinity()	{return ((IWellOrder) inner).NegInfinity();}

	/**Returns the Representation of an invalid Number for this Class in Place.	 */
	public IWellOrder NaNAt() {throw new AbstractMethodError(strConst);}

	/**Returns the Representation of an invalid Number for this Class.	 */
	public IWellOrder NaN()	{return ((IWellOrder) inner).NaN();}

	/**Local Cache for the Result of this Test	 */
	protected boolean infinite = ((IWellOrder) inner).isInfinite();

	/**Returns the Representation of Infinity for this Class.	 */
	public boolean isInfinite()	{return infinite;}

	/**Local Cache for the Result of this Test	 */
	protected boolean nan = ((IWellOrder) inner).isNaN();

	/**Returns the Representation of an invalid Number for this Class.	 */
	public boolean isNaN()	{return nan;}

}
