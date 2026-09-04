//package function.derive.ring.body.vector;
package function.vector;

/**
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

