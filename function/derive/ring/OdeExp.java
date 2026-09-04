package function.derive.ring;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import function.vector.AOdeFloat;

/**ODE Definition for the Exponential Functions,
 * The Derivative is a Copy of the current y Value and not dependent on x ! */
final public class OdeExp
extends AOdeFloat 
implements IODE {

	public void Funktion (IIntRing x, IIntRing y, IIntRing dydx) { dydx.copyAt(y); }

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	public double Funktion(double x, double y) { return y; }

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y) TODO:
	 */
	public void Funktion(double x, double[] y, double[] dydx) {
		System.arraycopy(y, 0, dydx, 0, dydx.length); }

}
