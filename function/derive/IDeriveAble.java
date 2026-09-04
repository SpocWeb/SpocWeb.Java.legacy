package function.derive;

import function.IInvertAble;

/**Interface indicating that a Function can be derived.
 * This requires it to be continuous and of restricted Variance.
 * A derivable Function can be locally inverted. */
public interface IDeriveAble
extends IInvertAble { //Function {

	/**Reference to the Deriative Function.
	 * Static, because a Function with no Parameters
	 * always has the same Derivative.
	 * Should be overridden by any Subclass. 	 */
//	protected static IFunction Derivative;

	///////////////////////////////////////////////////////////////////////////
	//	Methods
	///////////////////////////////////////////////////////////////////////////

	/** @return the Derivative of this Function	 */
	IDeriveAble getDerivative();

	/**Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.	 */
	void setDerivative(IDeriveAble derivative);

	/** @return the Integral of this Function	 */
	IDeriveAble getIntegral();

	/**Returns the n-th Derivative of this Function
	 * Negative n denote Integration  */
//	public IDeriveAble Derivative(int n);

	/**Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.	 */
	void	setIntegral(IDeriveAble integral);

}


