package function.derive;

import function.IFloatFunction;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;

/**
 * IFloatDeriveAble.java
 *
 * Created on 6. Januar 2001, 18:02
 *
 * @author  Matthias Heuer
 * @version
 */
public interface IFloatDeriveAble
extends IFloatFunction {
    
	/** @return The Derivative at x	 */
	public float getDerivative(final float x);
	
	/** @return The Derivative at x	 */
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
