package function.derive.ring;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import function.vector.AOdeFloat;

/**ODE Definition for the Exponential Functions,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:35:06Z
 * digest: 532c5484e78576c6a44e1cab959a808737380cac873bef52ff4c092e37872bc6
 * stale: false
 * tags: [code/differential_integration, code/exponential_function]
 * concepts: [Ordinary Differential Equations]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * The Derivative is a Copy of the current y Value and not dependent on x ! */
final public class OdeExp
extends AOdeFloat 
implements IODE {

	/**Sets dydx to a Copy of y, since the Exponential's Derivative equals the Function itself.	 */
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
