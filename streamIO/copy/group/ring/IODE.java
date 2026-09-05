package streamIO.copy.group.ring;

/**Interface for an ordinary Differential Equation (ODE).
 * Contains only the single Method to calculate the first Derivatives of y.
 * Also used to define Functions with two Arguments.
 *
 * Design Decisions:
 * Used 'IRing' instead of 'Tensor' for y, to facilitate both
 * scalar and Vector Differntial Equations.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: c016f92b06cb03d4f5e7de4c8b2ead36a9417ae2a6ded068b5a5dc2ff4f962d7
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IODE {

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @param derivative1 The first Derivative at (x,y)
	 */
	void Funktion(IIntRing x, IIntRing y, IIntRing derivative1);

}
