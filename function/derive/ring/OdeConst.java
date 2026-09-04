package function.derive.ring;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import function.vector.AOdeFloat;

/**ODE Definition for the Constant Functions y = c,
 * The Derivative is always 0 and not dependent on either x or y !
 * @see function.derive.ring.OdeLinear which is always 1. 
 */
final public class OdeConst
extends AOdeFloat 
implements IODE {

	/**Implementation of the Function returning the Derivative in Place	 */
	public void Funktion (final IIntRing x, final IIntRing y, final IIntRing dydx) { dydx.zeroAt(); }

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	public double Funktion(final double x, final double y) { return 0; }

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y) TODO:
	 */
	public void Funktion(final double x, final double[] y, final double[] dydx) {
		java.util.Arrays.fill(dydx, 0, dydx.length, 0); }

}
