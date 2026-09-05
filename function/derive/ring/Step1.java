package function.derive.ring;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.metric.IMetricIRing;
import function.ICountAble;
import function.byref.ByRefDouble;
import function.derive.ADeriveAble;
import function.derive.CCountAble;
import function.derive.IDeriveAble;

/*import Stream.Copy.*;
import Functions.*;
import Functions.Derive.*;
import Stream.Copy.Group.Ring.Metric.*;
*/

/**Implements a non continuous Version of the Step (Heaviside) Function,
 * which rises from 0 to 1 instantaneously at x=0.
 * The Width of the Interval in which this Function rises
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:34:46Z
 * digest: d932a815b9981f430a5863767705eba4bf40cdf475e3a08888187d05531d4675
 * stale: false
 * tags: [code/mathematical_function]
 * concepts: [Function Algebra, Distribution Theory]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * is proportional to 1/H	 */
public class Step1
extends ADeriveAble { //AStatic { //

	/**Initializing Constructor	 */
	public Step1(Object Height) { this(Height, new Delta1(Height, null)); }

	/**Constructor preventing Recursion	 */
	public Step1(Object Height, IDeriveAble Derivative) {
		H = Height;
		setDerivative(Derivative);
	}

	/**Parameter for Sharpness of the Delta Function	 */
	protected Object H;

	/**This Function represents the Sinus Function.
	 * It always returns the Sine of the Argument.  */
	public Object Map (Object arg) { return ((IMetricIRing) arg).Step1(H); }

	/**This Function represents the continuous Step Function:   */
	public double Map(double x) { return STEP1(x, H); }

	/**Continuous Step Function,
	 * Rises from 0 to 1 in the Interval [0,1]
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	final static public double STEP1(double x) {
		if (x < ICountAble.ZERO) return ICountAble.ZERO;
		if (x > ICountAble.ONE ) return ICountAble.ONE ;	//assume it to 1
		return x; }

	/**Continuous Step Function,
	 * Rises from 0 to 1 in the Interval [0,H]
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	final static public double STEP1(double x, double H) {
		if ( x     < ICountAble.ZERO) return ICountAble.ZERO;
		if ((x*=H) > ICountAble.ONE ) return ICountAble.ONE ;	//assume it to 1
		return x; }

	/**Continuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	final static public double STEP1(double x, Object H) {
		if (x < ICountAble.ZERO) return ICountAble.ZERO;
		if (H != null) x*=ByRefDouble.GET_DOUBLE (H);	//assume it to 1
		if (x > ICountAble.ONE ) return ICountAble.ONE ;	//
		return x; }

	/**Continuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	final static public IMetricIRing STEP1(ICopyAble x, Object H) {
		return STEP1AT((IMetricIRing) x.copy (), H); }

	/**Continuous Step Function,
	 * returns 1 for positive and 0 for negative Numbers.
	 * Is related to the Sign Function.	 */
	final static public IMetricIRing STEP1AT(IMetricIRing x, Object H) {
		if (x.negative()) return (IMetricIRing) x.zeroAt();
		if (H != null) x.mulAt(H);	//assume it to 1
		if (x.isMoreThan(CCountAble.One)) x.oneAt();	//assume it to 1
		return x; }

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see     java.lang.Boolean#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 	 */
	public boolean equals  (Object arg) {
		return (arg instanceof Step1) && (H.equals(((Step1)arg).H)); }

}
