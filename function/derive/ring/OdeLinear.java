package function.derive.ring;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import function.vector.AOdeFloat;

/**ODE Definition for the linear Functions y = x+c,
 * The Derivative is always 1 and neither x- nor y- dependent! 
 * @see function.derive.ring.OdeConst which is always 0. 
 */
final public class OdeLinear
extends AOdeFloat 
implements IODE {

	/**Implementation of the Function returning the Derivative	 */
	public void Funktion(IIntRing x, IIntRing y, IIntRing dydx) {
		dydx.oneAt(); }

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	public double Funktion(double x, double y) { return 1; }

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y) TODO:
	 */
	public void Funktion(double x, double[] y, double[] dydx) {
		java.util.Arrays.fill(dydx, 0, dydx.length, 1); }

}
