//package function.derive.ring.body.vector;
package function.vector;

/**
  * Defines a function that writes a vector-valued result for a scalar input into a caller-owned
  * output array, in either {@code double} or {@code float} precision.
  *
  * Title: IFloatVectorFunction<p>
  * Description:
  * Defines the Interface for a Function that returns a Vector from a float Number
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Known Uses: @see streamIO.Copy.Group.Ring.StepTrapez
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-16-2002, 06:33 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:46:57Z
  * digest: aafebf33ea7975b1776b3ca43adb6be79b3fce8f31f1b4124fd470a07d28bf3a
  * stale: false
  * tags: [code/vector_math, code/function_composition]
  * concepts: [Vector Field Function]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface IFloatVectorFunction {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Function Value at the point x 	 */
	void map(final double x, final double[] yOut);

	/** Returns the Function Value at the point x 	 */
	void map(final double x, final float[] yOut);

}

