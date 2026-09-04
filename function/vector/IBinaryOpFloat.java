//package Functions.Derive.RingFuncs;
package function.vector;

/**Interface for an ordinary real valued binary Operation. 
 * Also used for Differential Equations, 
 * where the Functions depend on a single Dimension, usually Time, and it's current Value. 
 * Contains only the single Method to calculate the first Derivatives of y.
 * Also used to define Functions with two Arguments.
 * 
 * Implementations: 
 * @see function.derive.ring.OdeConst  == 0 
 * @see function.derive.ring.OdeLinear == 1
 * @see function.derive.ring.OdeSquare == x
 * @see function.derive.ring.OdeExp    == y
 */
public interface IBinaryOpFloat {

	/**Returns the 1st Derivative in x of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	float Funktion(float x, float y);

	/**Returns the 1st Derivative in x of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	double Funktion(double x, double y);

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @param dydx The Derivative at (x,y)
	 */
	void Funktion(double x, double[] y, double[] dydx);

}
