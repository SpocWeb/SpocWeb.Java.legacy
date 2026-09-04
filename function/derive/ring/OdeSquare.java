package function.derive.ring;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import function.vector.AOdeFloat;

/**ODE Definition for the Square Functions x^2+c /2,
 * The Derivative is a Copy of the current x Value and not dependent on y ! 
 * @see function.derive.ring.OdeExp where the Derivative is a Copy of y. 
 */
final public class OdeSquare
extends AOdeFloat 
implements IODE {

	/**Implementation of the Function returning the Derivative	 */
	public void Funktion (IIntRing x, IIntRing y, IIntRing dydx) { dydx.copyAt(x); }

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	public double Funktion(double x, double y) { return x; }

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y) TODO:
	 */
	public void Funktion(double x, double[] y, double[] dydx) {
		java.util.Arrays.fill(dydx, 0, dydx.length, x); }

}
