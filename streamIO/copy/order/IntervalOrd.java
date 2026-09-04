package streamIO.copy.order;

/**
  * This Subclass orders the left and right coordinates in the Constructor
  * to achieve a normed Format.
  *
  * An ordered Set of left and right Coordinates allows for a faster
  * test for the Position of an Item relative to the Interval
  * and since Creation happens only once, but Methods infinitely often...
  *
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalDblA
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalDbl
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalA
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalP
  * are derived from AMetricBody to define arithmetic Operations
  *
  * The Join is no Problem, because for disjoint Sets
  * it results in the empty Set and otherwise a contiguous Set is returned.
  *
  * The Union is a Problem, because for disjoint Sets
  * it needs both Sets to represent the whole, so you end up with fragments!
  * A Union will thus often stay a combined Expression just like algebraic Expressions with Varibles.
  *
  * @see streamIO.Copy.IOrder.Interval
  * @see streamIO.Copy.IOrder.IntervalOrd
  * are minimal Definitions for Intervals
  */
public class IntervalOrd
extends Interval {

	/** Constructor without any Arguments, should be initialized with +/- Infinity	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Defining Constructor.
	  * optionally swaps the Arguments to create an ordered Interval.
	  * @param left left  Border of the Interval
	  * @param right right Border of the Interval
	  */
	public IntervalOrd(IOrder left, IOrder right) {
		if (left.isLessThan(right)) {
			Left = left ; Right = right;
		} else {
			Left = right; Right = left ; } }

	//////////////////////////////////
	//	Optimizations for Order	//
	//////////////////////////////////

	/** less: '<' Returns True, when 'Self' < arg
		 * @param arg Object to compare to <CODE>this</CODE> Interval.
		 * @return <CODE>true</CODE>, when <CODE>this</CODE> < <CODE>arg<CODE>
		 */
	public boolean isLessThan (Object arg){return Right.isLessThan(arg);}

	/** grtr: '>' Returns True, when 'Self' > arg
	 * Optimization for ordered Intervals.
	 * @param arg : Object to compare to <CODE>this</CODE> Interval.
	 * @return <CODE>true</CODE>, when <CODE>this</CODE> > <CODE>arg</CODE>
	 */
	public boolean isMoreThan (Object arg){return Left.isMoreThan(arg);}

	/** Adds a Point to the Interval thus enlarging it.
	 * This Operation prepares the superSect Operation.
	 * @param arg A Scalar Object to be joined to this Interval
	 * @return the SuperSection Interval of <CODE>this</CODE> and <CODE>arg</CODE>.
	 */
	public Interval addPoint (Object arg) {
		if (Left .isMoreThan(arg)) Left .copyAt(arg); else 	//= (Order) arg; else
		if (Right.isLessThan(arg)) Right.copyAt(arg);			//= (Order) arg;
		return this; }

	/** Returns the Intersection Interval in place.
		 * @param arg : Interval to compare to <CODE>this</CODE> Interval.
		 * @return the SuperSection Interval of <CODE>this</CODE> and <CODE>arg</CODE>.
		 */
	public IntervalOrd interSectAt	(IntervalOrd arg) {
		Left .MaxAt(arg.Left );
		Right.MinAt(arg.Right);
		return this; }

	/** Returns the SuperSection Interval in place.
		 * @param arg : Object to compare to <CODE>this</CODE> Interval.
		 * @return the SuperSection Interval of <CODE>this</CODE> and <CODE>arg</CODE> in Place.
		 */
	public IntervalOrd superSectAt(IntervalOrd arg) {
		Left .MinAt(arg.Left );
		Right.MaxAt(arg.Right);
		return this; }

	/** Returns the Intersection Interval in place.
		 * @param arg : Object to compare to <CODE>this</CODE> Interval.
		 * @return the InterSection Interval of <CODE>this</CODE> and <CODE>arg</CODE> in Place.
		 */
	public Interval interSectAt	(Interval arg) {
//		if (arg == null) return this;
		if (arg instanceof IntervalOrd) return interSectAt((IntervalOrd)arg);
		return interSectAt( new IntervalOrd(arg.Left, arg.Right)); }

		/** Returns the SuperSection Interval of <CODE>this</CODE> and <CODE>arg</CODE>.
		 * @param arg : Object to compare to <CODE>this</CODE> Interval.
		 * @return the SuperSection Interval of <CODE>this</CODE> and <CODE>arg</CODE> in Place.
		 */
	public Interval superSectAt(Interval arg) {
		addPoint(arg.Left );
		addPoint(arg.Right);
		return this; }

	/** Returns the Intersection Interval.
		 * @param arg : Object to compare to <CODE>this</CODE> Interval.
		 * @return the InterSection Interval of <CODE>this</CODE> and <CODE>arg</CODE>.
		 */
	public IntervalOrd interSect	(IntervalOrd arg) {
		return ((IntervalOrd)copy()).interSectAt(arg); }

	/** Returns the SuperSection Interval.
		 * @param arg : Object to compare to <CODE>this</CODE> Interval.
		 * @return the SuperSection Interval of <CODE>this</CODE> and <CODE>arg</CODE>.
		 */
	public IntervalOrd superSect	(IntervalOrd arg) {
		return ((IntervalOrd)copy()).superSectAt(arg); }

}
