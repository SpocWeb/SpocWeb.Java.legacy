package function.vector;

import math.vector.VectorDouble;

/**Helper Class to integrate ODEs that describe a Force Field, derived from a Potential,
 * i.e. (X',Y') == (dX/dt,dY/dt) == (dS/dx, dS/dy).
 * To be integrable, the Force Field must fulfill the equation:
 * dX'/dy = ddS/(dx*dy) = dY'/dx
 *
 * This ODE can also be used to integrate Equipotential Lines in higher Dimensions:
 * By keeping (n-2) Coordinates constant and integrating only in x and y.
 * This can be achieved by swapping these two coordinates with the wanted, e.g.
 * keep z constant, keep x constant(swap x with z), keep y constant (swap y with z).
 *
 * There are two ways to integrate a Scalar Function S(x,y):
 *
 * Here an artificial (Time) Coordinate t is introduced
 * and the following ODE considered:  (X',Y') == (+dS/dy, -dS/dx)
 * That way dS == dS/dx*dx + dS/dy*dy == X'*dx + Y'*dy == 0
 * Here you can use either an ODE (space & time coordinates) or a
 * normal Function (only Space) that describes the Force Field.
 *
 * The other, more effective Way is to directly integrate Y'=-1/y' = -x'/y'
 * and change the integration Coordinate between x and y
 * as soon as the Derivative is larger than 1.
 * That saves the Calculation for one Coordinate.
 * This needs a special Stepper Routine to keep Track of these Changes.
 * It is implemented in 'Vector.StepConstant'
 *
 * The actual Problem is that the integration of the Force Field Vector
 * to get a Trajectory is easy, but Equipotential HyperPlanes are defined
 * by being just normal to the Force Field Vector and thus you have to
 * define an additional Direction, usually by defining the two free Dimensions
 * and keeping the Rest of the Dimensions constant.
 * To draw these HyperPlanes, you have to Step along a Trajectory in two Dimensions
 * (full circle) and branch off to the third Dimension (also full circle)
 * and connect each inner circle with the previous one, by choosing the nearest Point.
 */
public class OdeHeight
extends AOdeFloat {

	/**Local Reference to the Force Field	 */
	protected IFloatVectorField Field;

	/**Local Reference to the Force Field ODE	 */
	protected IBinaryOpFloat ODE;

	/**X-Dimension along which the ODE is integrated along	 */
	public int dimX;

	/**Y-Dimension which the ODE is integrated in.	 */
	public int dimY;

	/**Initializing Constructor: takes the Force Field ODE
	 * (can be Time dependant)
	 */
	public OdeHeight(IBinaryOpFloat ODE, int dimX, int dimY) {
		this.ODE = ODE;
		this.dimX = dimX;
		this.dimY = dimY;
		if ((dimX == dimY) || (dimX < 0) || (dimY < 0)) throw new AbstractMethodError();
	}

	/**Initializing Constructor: takes the Force Field
	 * (must not be Time dependant)
	 */
	public OdeHeight(IFloatVectorField Field, int dimX, int dimY) {
		this.Field = Field;
		this.dimX = dimX;
		this.dimY = dimY;
		if ((dimX == dimY) || (dimX < 0) || (dimY < 0)) throw new AbstractMethodError();
	}

////////////////////////////////////////////////////////////////////////////
/// #region : Interface IOdeFloat : Implementation
////////////////////////////////////////////////////////////////////////////

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	public double Funktion(double x, double y) { throw new AbstractMethodError(); }

	/**
	 * Changes the Force Field ODE into a Height ODE by creating the Derivative,
	 * as a Vector that is orthogonal to the Force Field Vector.
	 * All coordinates except for dimX and dimY are kept constant.
	 * Thus you don't have to swap Coordinates before and after Calculation.
	 *
	 * Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @param dydx The Derivative at (x,y)
	 */
	public void Funktion(double x, double[] y, double[] dYdX) {
		if (Field == null) {
			ODE.Funktion(x, y, dYdX);
		} else { //x Component ignored!
			Field.map(y, dYdX); }	//saves copying...
		//save two Dimensions...
		double tmpX = dYdX[dimX];
		double tmpY = dYdX[dimY];
		VectorDouble.ZERO_AT(dYdX);
		//swap the two Dimensions...
		dYdX[dimX] = -tmpY; //...and change Sign
		dYdX[dimY] =  tmpX; //...for Equipotentiality.
	}

}
