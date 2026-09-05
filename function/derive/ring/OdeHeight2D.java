package function.derive.ring;

import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import function.vector.IBinaryOpFloat;

/**Conversion of a Height Function to an ODE for constant Height Trajectories.
 * This is a special case for a time independent ODE in two Dimensions only.
 * A more general case is implemented with Tensors in the local class 'HeightOde'
 * of Stepper class 'Vector.StepConstant', but this is faster.
 * A Solution for time dependent Equipotential Lines is given
 * in just keeping all up to two Dimensions constant,
 * by zeroing out the Derivatives.
 * This is done in "VectorFuncs.OdeHeight"
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: abb387d4ee389b5c5527aed6409385c825abc445e1e4a2543971830902ceb678
 * stale: false
 * tags: [code/differential_integration]
 * concepts: [Ordinary Differential Equations]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class OdeHeight2D
implements IODE {

	/**Local Reference to the Force Field ODE	 */
	protected IODE ODE;

	/**Local Reference to the Force Field ODE	 */
	protected IBinaryOpFloat OdeFloat;

	/**Initializing Constructor: takes the Force Field ODE
	 * (not Time dependant)	 */
	public OdeHeight2D(IODE ODE_) { this.ODE = ODE_; }

	/**Initializing Constructor: takes the Force Field ODE
	 * (not Time dependant)	 */
	public OdeHeight2D(IBinaryOpFloat OdeFloat_) { this.OdeFloat = OdeFloat_; }

	/**Changes the Force Field ODE into a Height ODE by creating a Vector as Derivative,
	 * that is orthogonal to the Force Field Vector.
	 * All coordinates except for the 0 and 1 are kept exactly the same.
	 * Thus you would have to swap Coordinates before and after Calculation.	 */
	public void Funktion(IIntRing x, IIntRing y, IIntRing dydx) {
		ODE.Funktion(x, y, dydx);
		((IGroup) dydx.invAt()).negAt();	//Invert and change Sign for Equipotentiality.
	}

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	double Funktion(double x, double y) {
		return -1.0/OdeFloat.Funktion(x, y); }

}
