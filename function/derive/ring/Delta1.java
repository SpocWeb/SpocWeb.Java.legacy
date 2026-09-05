package function.derive.ring;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.metric.IMetricIRing;
import function.ICountAble;
import function.byref.ByRefDouble;
import function.derive.ADeriveAble;
import function.derive.CCountAble;
import function.derive.IDeriveAble;

/**This Class encapsulates the Delta1 Function.
 * It jumps to the given Height (Default == 1)
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: a5147d6f5211bf97d99037b3bff8e06725cae06680db97eb36fcd5c5354a1f9f
 * stale: false
 * tags: [code/mathematical_function]
 * concepts: [Function Algebra, Distribution Theory]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * from 0 to 1/Height.  */
public class Delta1
extends ADeriveAble { //AStatic { //

	/**Initializing Constructor	 */
	public Delta1(Object Height) { this(Height, new Step1(Height, null)); }

	/**Initializing Constructor	 */
	public Delta1(Object Height, IDeriveAble Integral) {
		H = Height;
		setIntegral (Integral);
	}

	/**Parameter for Sharpness of the Delta Function	 */
	protected Object H;

	/**This Function represents the Delta Function.
	 * It always returns the Delta of the Argument.  */
	public Object Map (Object arg) { return ((IMetricIRing) arg).Delta1(H); }

	/**This Function represents the Delta Function.  */
	public double Map (double arg) { return DELTA1(arg, H); }

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function.
	 * If H is null (not given), it is assumed to 1. */
	public static IMetricIRing DELTA1(ICopyAble x) {
		return DELTA1AT((IMetricIRing) x.copy ()); }

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function.
	 * If H is null (not given), it is assumed to 1. */
	public static IMetricIRing DELTA1(ICopyAble x, Object H) {
		return DELTA1AT((IMetricIRing) x.copy (), H); }

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function.
	 * If H is null (not given), it is assumed to 1. */
	public static IMetricIRing DELTA1AT(IMetricIRing x) {
		if (x.negative()) 			return (IMetricIRing) x.zeroAt();
		if (x.isMoreThan(CCountAble.One)) return (IMetricIRing) x.zeroAt(); else x.oneAt();	//assume H to 1
		return x; }

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function.
	 * If H is null (not given), it is assumed to 1. */
	public static IMetricIRing DELTA1AT(IMetricIRing x, Object H) {
		if (x.negative()) return (IMetricIRing) x.zeroAt();
		if (H != null) x.mulAt(H);	//assume it to 1
		if (x.isMoreThan(CCountAble.One)) x.zeroAt(); else
		if (H != null) x.copyAt(H); else x.oneAt();	//assume H to 1
		return x; }

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function.
	 * If H is null (not given), it is assumed to 1. */
	public static double DELTA1(double x, Object H) {
		if (x < ICountAble.ZERO) return ICountAble.ZERO;
		if (H == null) return DELTA1(x); //assume it to 1
		return DELTA1(x, ByRefDouble.GET_DOUBLE (H)); }

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function (saves abs()).
	 * If H is null (not given), it is assumed to 1. */
	public static double DELTA1(double x, double H) {
		if ( x    < ICountAble.ZERO) return ICountAble.ZERO;
		if ((x*H) > ICountAble.ONE ) return ICountAble.ZERO;
		return H; }

	/**Fast, but unsmooth, even discontinuous Representation of Delta
	 * as an asymmetric Rectangle Function (saves abs()).
	 * If H is null (not given), it is assumed to 1. */
	public static double DELTA1(double x) {
		if (x < ICountAble.ZERO) return ICountAble.ZERO;
		if (x > ICountAble.ONE ) return ICountAble.ZERO;
		return  ICountAble.ONE; }

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
		return (arg instanceof Delta1) && (H.equals(((Delta1)arg).H)); }

}
