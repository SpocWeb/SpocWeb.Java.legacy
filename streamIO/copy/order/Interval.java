package streamIO.copy.order;

import streamIO.copy.ICopyAble;

/**
  * This Class defines a Set of Objects by an Interval.
  * Intervals form a Set by defining AND and OR respective Join and Union.
  * The only prerequisite is the order Relation.
  *
  * The Join is no Problem, because for disjoint Sets
  * it results in the empty Set and otherwise a contiguous Set is returned.
  *
  * The Union is a Problem, because for disjoint Sets
  * it needs both Sets to represent the whole, so you end up with fragments!
  * A Union will thus often stay a combined Expression just like algebraic Expressions with Varibles.
  *
  * The Interval can be extended by numerical Operations,
  * because it is a Manifold, this is done in Body.IntervalA.
  *
  * @see streamIO.Copy.IOrder.Interval should not be used for intensive Interval Operations, rather use
  * @see streamIO.Copy.IOrder.IntervalOrd because it speeds up Tests.
  *
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalDblA
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalDbl
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalA
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.IntervalP
  * are derived from AMetricBody to define arithmetic Operations
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:30:32Z
  * digest: c74a90cb3f0e1bc8ed9bf2f9484aa4d5e7d4f1bca296215cb95a3600b70f6237
  * stale: false
  * tags: [code/interval_arithmetic, code/set_operations]
  * concepts: [Interval Arithmetic, Order Relation]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  */
public class Interval
extends AOrder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Left Border of the Interval	 */
	public IOrder Left;	//protected

	/**Right Border of the Interval	 */
	public IOrder Right;	//protected

	/**Empty Constructor, only for Subclasses.	 */
	public Interval() {super (null); self = this;}	//protected

	/** Defining Constructor.
	 * @param left 'left' Border of the Interval.
	 * @param right 'right' Border of the Interval.
	 */
	public Interval(IOrder left, IOrder right) {
		super (null); self = this;
		Left  = left;
		Right = right; }


	//////////////////
	//	Order	//
	//////////////////

	/** less: '<' Returns True, when 'Self' < arg
		 * @param arg Object to compare to <CODE>this</CODE> Interval.
		 * @return <CODE>true</CODE>, when <CODE>this</CODE> < <CODE>arg</CODE>
		 */
	public boolean isLessThan (Object arg) {
		if (arg instanceof Interval)
			return	 isLessThan(((Interval) arg).Left ) &&
					 isLessThan(((Interval) arg).Right);
		return Left .isLessThan(arg) &&
			   Right.isLessThan(arg);
	}

	/** grtr: '>' Returns True, when 'Self' > arg
	 * @param arg Object to compare to <CODE>this</CODE> Interval.
	 * @return <CODE>true</CODE>, when <CODE>this</CODE> > <CODE>arg</CODE>
	 */
	public boolean isMoreThan (Object arg) {
		if (arg instanceof Interval)
			return	 isMoreThan(((Interval) arg).Left ) &&
					 isMoreThan(((Interval) arg).Right);
		return Left .isMoreThan(arg) &&
			   Right.isMoreThan(arg);
	}

	/** overlaps: Returns True, when 'this' Interval overlaps with the Argument.
	 * works for both Intervals and scalar Objects, where it is equivalent to contains().
	 * @param arg Interval to compare to <CODE>this</CODE> Interval.
	 * @return <CODE>true</CODE>, when <CODE>this</CODE> Interval overlaps with <CODE>arg</CODE>
	 */
	public boolean overlaps(Interval arg) {
		return		contains(arg.Left ) ||
					contains(arg.Right); /*||
				arg.contains(	 Left ) ||
				arg.contains(	 Right);
//				arg.overlaps(this);	//last Term would lead to an infinite Recursion
/*		return (	Left .grtr (arg.Left ) ^	//this is equivalent, but faster
					Right.grtr (arg.Left )) ||	//contains(arg.Left ) ||
			   (	Left .grtr (arg.Right) ^
					Right.grtr (arg.Right)) ||	//contains(arg.Right) ||
			   (arg.Left .grtr (	Left ) ^
				arg.Right.grtr (	Left )) ||
			   (arg.Left .grtr (	Right) ^
				arg.Right.grtr (	Right));
*/	}

	/** contains: Returns True, when 'this' Interval contains the Argument.
 * This corresponds to the 'between(a, b)' Method of 'arg'.
 * @param arg Interval to compare to <CODE>this</CODE> Interval.
 * @return <CODE>true</CODE>, when <CODE>this</CODE> Interval contains <CODE>arg</CODE>
 */
	public boolean contains(Interval arg) {
		return	contains(arg.Left ) &&
				contains(arg.Right); }

	/** contains: Returns True, when 'this' Interval contains the Argument.
				 * This corresponds to the 'between(a, b)' Method of 'arg',
				 * but works for both Scalars and Intervals.
				 * @param arg Object to compare to <CODE>this</CODE> Interval.
				 * @return <CODE>true</CODE>, when <CODE>this</CODE> Interval contains <CODE>arg</CODE>
				 */
	public boolean contains(Object arg)	{
		if (arg instanceof Interval) return	contains((Interval) arg);
		return Left .isMoreThan(arg) ^
			   Right.isMoreThan(arg);
//	return ((orderAble) arg).between(Left, Right); //this is identical, but requires a cast!!!
	}

	/** equals: '=' Returns True, when 'Self' = arg
	 * has two Meanings:
	 * for Intervals it tests, whether the borders are the same.
	 * for Scalars   it tests, whether the Interval is denatured
	 * 	and Scalar is contained in the Interval.
	 * @param arg Object to compare to <CODE>this</CODE> Interval.
	 * @return <CODE>true</CODE>, when <CODE>this</CODE> == <CODE>arg</CODE>
	 */
	public boolean equals (Object arg) {
		if (arg instanceof Interval)
			 return Left .equals(((Interval)arg).Left ) &&
					Right.equals(((Interval)arg).Right);
		if (!  Left.equals(Right)) return false;
		return Left.equals(arg); }


	//////////////////////////////////
	//	Set Operations w. Interval	//
	//////////////////////////////////
	//	the Union of two Intervals is usually no Interval,
	//	you have to start maintaining Sets and unite subsets,
	//	so only superSect and interSect are implemented.

	/** Adds a Point to the Interval in Place thus enlarging it.
		 * This Operation prepares the superSect Operation.
		 * This is used e.g. for finding out the bounding Box in 2 and 3 Dimensions.
		 * @param arg Object to be added to <CODE>this</CODE> Interval.
		 * @return <CODE>this</CODE> after checking and optionally enlarging it to contain <CODE>arg</CODE>
		 */
	public Interval addPointAt (Object arg) {
		if (arg instanceof Interval) return superSectAt((Interval) arg);
		if (Left .isMoreThan(arg)) Left .copyAt(arg); //Left = (Order) arg;
		if (Right.isLessThan(arg)) Right.copyAt(arg); //Right= (Order) arg;
		return this; }

	/** Adds a Point to the Interval in Place thus enlarging it.
		 * This Operation prepares the superSect Operation.
		 * This is used e.g. for finding out the bounding Box in 2 and 3 Dimensions.
		 * @param arg Object to be added to <CODE>this</CODE> Interval.
		 * @return a new Interval large enough to contain both <CODE>this</CODE> and <CODE>arg</CODE>
		 */
	public Interval addPoint_(Object arg) { return ((Interval)copy()).addPointAt(arg); }

	/** Returns the SuperSection Interval in place.
		 * This is used e.g. for finding out the bounding Box in 2 and 3 Dimensions.
		 * @param arg Interval to supersect with <CODE>this</CODE> Interval.
		 * @return the Supersection Interval of <CODE>this</CODE> and <CODE>arg</CODE> in place.
		 */
	public Interval superSectAt(Interval arg) {
		addPointAt(arg.Left );
		addPointAt(arg.Right);
		return this; }

	/** Returns the SuperSection Interval in place.
		 * This is used e.g. for finding out the bounding Box in 2 and 3 Dimensions.
		 * @param arg Object to supersect with <CODE>this</CODE> Interval.
		 * @return the Supersection Interval of <CODE>this</CODE> and <CODE>arg</CODE>
		 */
	public Interval superSect  (Interval arg) {
		return ((Interval)copy()).superSectAt(arg); }

	/**
	  * Returns the Intersection Interval in place.
	  * This is used e.g. for Clipping in 2 and 3 Dimensions.
	  *
	  * It corresponds to applying a Join resp. the AND Operation on Sets resp. Boolean Bonds.
	  * @param arg Object to intersect with <CODE>this</CODE> Interval in Place.
	  * @return the Intersection Interval of <CODE>this</CODE> and <CODE>arg</CODE> in place
	  */
	public Interval ANDAt (Interval arg) {
//	if (arg == null) return this;
		boolean contL =		contains(arg.Left );
		boolean contR =		contains(arg.Right);
		if ( (contL && contR)) return this; //full containment
		// TODO: LOGIC: missing "return this;" here - after collapsing Left onto Right for the no-containment case, execution falls through into the partial-containment branch below, which re-reads the just-mutated Left and can overwrite it again with a wrong border instead of leaving the collapsed (empty-intersection) interval in place.
		if (!(contL || contR)) Left.copyAt(Right); //no containment
		//partial containment: take the two contained points
		if (arg.contains(Left )) { //take this left Point and use the contained other Point;
			if (contL)	Left .copyAt(arg.Left );
			else		Left .copyAt(arg.Right);
			return this; }
			if (contL)	Right.copyAt(arg.Left );
			else		Right.copyAt(arg.Right);
			return this; }

	/** Returns the Intersection Interval.
	 * @param arg Object to intersect with <CODE>this</CODE> Interval.
	 * @return the Intersection Interval of <CODE>this</CODE> and <CODE>arg</CODE>
	 */
	public Interval AND (Interval arg) {
		return ((Interval)copy()).ANDAt(arg); }


	//////////////////////////////////
	//	Redefinition of Position	//
	//////////////////////////////////

	/** Returns the Position of arg relative to the Interval.
	 *
	 * @param arg Object to compare to <CODE>this</CODE> Interval.
	 * @return
	 * -2 for   x		< a[0]	< a[1]
	 * -1 for   x		= a[0]	< a[1]
	 *  0 for a[0]	<   x		< a[1]
	 * +1 for a[0]	< a[1]	=   x
	 * +2 for a[0]	< a[1]	<   x
	 */
	public int compareTo (Object arg) {
		return 	Left .compareTo(arg) +
				Right.compareTo(arg); }

	/** Returns the Position of arg relative to the Interval.
	 *
	 * @param arg Object to compare to <CODE>this</CODE> Interval.
	 * @return
	 * -2 for   x		<  a[0]	< a[1]
	 *  0 for a[0]	<=  x	<  a[1]
	 * +2 for a[0]	< a[1]	<=   x
	 */
	public int Position (Object arg) {
		return  Left .Position(arg) +
				Right.Position(arg); }


	//////////////////
	//	CopyAble	//
	//////////////////

	/** Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 * A scalar Order Object results in a singular Interval,
	 * an Interval is copied to both left and right border.
	 * @param arg Object to copy to <CODE>this</CODE> Interval.
	 * @param Depth Level up to which copying is done instead of referencing.
	 * @return <CODE>this</CODE> after copying the contents from <CODE>arg</CODE>
	 */
	public ICopyAble copyAt(Object arg, int Depth) {
		Left .copyAt(((Interval)arg).Left , --Depth);
		Right.copyAt(((Interval)arg).Right,   Depth);
		return this; }

	/** Creates an uninitalized new Instance of it's class.
	 * When overriding, use newInstance on all Components.
	 * @return an uninitalized new Instance of <CODE>this</CODE> class.
	 */
	public ICopyAble newInstance() {
		return new Interval((IOrder) Left.newInstance(),
							(IOrder) Right.newInstance());}

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
//	public String toString() {
//		return Starter + Left.toString() + Separator + Right.toString() + Stopper;}

	/** Fills this Instance with the Contents read from the String.
	 * @param arg Input streamIO to read the Contents from
	 * @throws IOException raised by InputStream 'arg'
	 * @return <CODE>this</CODE> after reading the Contents from <CODE>arg</CODE>
	 */
//	public CopyAble fromStreamAt(java.io.StreamTokenizer arg) throws java.io.IOException {
//		Left .fromStreamAt(arg);
//		Right.fromStreamAt(arg);
/*		String[] List = parseList(arg);
		Left .fromStringAt(List[0]);
		Right.fromStringAt(List[1]);
*///		return this; }

}
