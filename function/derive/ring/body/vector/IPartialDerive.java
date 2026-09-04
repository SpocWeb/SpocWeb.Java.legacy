package function.derive.ring.body.vector;

import function.derive.IDeriveAble;

/**This Interface extends the IDeriveAble Interface
 * to process partial Derivation, Integration and Inversion.
 *
 * Design Decisions:
 * There are two ways to interpret the generic Derivative(), getInverse()
 * and getIntegral() Methods:
 * 1) return the 0th Derivative, Inverse or Integral
 * 2) return a Tensor
 *		containing the all the partial Derivatives, Inverses or Integrals.
 *
 * The Second Solution corresponds to Dimension.map(arg)
 * returning the full Argument and using Dim = -1 for signaling full Operations.
 * The first Solution is so far implemented, except in Function(arg).  */
public interface IPartialDerive
extends IDeriveAble {

	/**Returns the partial Derivative of this Function in Direction Dim	 */
	public IDeriveAble getDerivative(int Dim);
//	public IPartialDerive Derivative(int Dim);

	/**Returns the partial Integral of this Function in Direction Dim	 */
	public IPartialDerive getIntegral(int Dim);

	/**Returns the partial Inverse Function to this one in Direction Dim
	 * i.e. the Function that returns the identical Mapping,
	 * if concatenated with this Function (at least locally)	 */
	public IPartialDerive getInverse(int Dim);

}
