package function.derive;

import function.IFloatFunction;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;

/**
 * IFloatDeriveAble.java
 *
 * Created on 6. Januar 2001, 18:02
 *
 * <p>Interface for a real-valued Function that can also return its Derivative at a given point,
 * either alone or jointly with the Function value itself.
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:15:08Z
 * digest: 2d56abe1321816e1b24998d3cf8e247333f61e45f04dc6ec645aa7da06ad57b5
 * stale: false
 * tags: [code/derivable_function_contract, code/numerical_differentiation]
 * concepts: [Calculus]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IFloatDeriveAble
extends IFloatFunction {

	/**Returns the Derivative of this Function evaluated at x.
	 * @return The Derivative at x	 */
	public float getDerivative(final float x);

	/**Returns the Derivative of this Function evaluated at x.
	 * @return The Derivative at x	 */
	public double getDerivative(final double x);
	
	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  x the Position at which to calculate Function and Derivative.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(final double x, final ByRefDouble derivative);
	
	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  x the Position at which to calculate Function and Derivative.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public float getFuncDerive(final float x, final ByRefFloat derivative);
	
}
