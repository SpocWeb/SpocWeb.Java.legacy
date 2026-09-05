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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 6c0f5bc83fd076ae3c31e4ca2c0d0d791387f2803b8e5cecd519427a329a42f7
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
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
