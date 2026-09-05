package function.derive;

import function.IInvertAble;

/**Interface indicating that a Function can be derived.
 * This requires it to be continuous and of restricted Variance.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:14:48Z
 * digest: 4c98f9958842fdce3e1f1133b1da7d7228b7768261d16ca33062bdb2989b1726
 * stale: false
 * tags: [code/derivable_function_contract, code/invertible_function_contract]
 * concepts: [Calculus, Function Composition]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
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

	/**Returns the derivative Function previously bound via {@link #setDerivative(IDeriveAble)}.
	 * @return the Derivative of this Function	 */
	IDeriveAble getDerivative();

	/**Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.	 */
	void setDerivative(IDeriveAble derivative);

	/**Returns the antiderivative Function previously bound via {@link #setIntegral(IDeriveAble)}.
	 * @return the Integral of this Function	 */
	IDeriveAble getIntegral();

	/**Returns the n-th Derivative of this Function
	 * Negative n denote Integration  */
//	public IDeriveAble Derivative(int n);

	/**Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.	 */
	void	setIntegral(IDeriveAble integral);

}


