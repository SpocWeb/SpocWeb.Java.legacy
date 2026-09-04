package function.derive.ring.body;

import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.ring.CatDerive;
import function.derive.ring.Inv;
import function.derive.ring.Square;

/**This Class encapsulates the TanH Function.  */
public class TanH
extends AFloatDeriveAble {

	/**Local Reference to the single Instance	 */
	final static public TanH TanH = new TanH();

	static { //Initializer
		TanH.setInverse   (ArTanH.ArTanH);
		TanH.setDerivative(	new CatDerive(Inv      .INV,
							new CatDerive(Square   .SQUARE,
										  CosH     .CosH)));
//		TanH.setDerivative(	new CatDerive(Resid    .Resid ,  //alternative Representation
//							new CatDerive(Square   .Square,  //good for calculating Function
//										  TanH     .TanH))); //and Derivative
		TanH.setIntegral  (	new CatDerive(Logarithm.LOGARITHM,
										  CosH     .CosH));
	}
	
	/**private Constructor for Singleton Implementation
	 * Derivative for TanH Function: 1/CosH^2
	 * The Integral of CosH^2 is (SinH(2x)/2 + x) / 2
	 */
	private TanH() { }
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/**This Function represents the TanH Function.
	 * It always returns the Argument.  */
	public Object Map (Object arg) { return ((MetricBody) arg).TanH(); }
	
	/*	Returns TanH(x) = SinH(x)/CosH(x) = (e^x - e^-x) / (e^x + e^-x)
						= (e^2x - 1) / (e^2x + 1)	 */
	public double Map (double x) {
//		ByRefDouble CosH = new ByRefDouble();
//		return IMeasurAble.SinH_CosH(x, CosH) / CosH.Value();
		double e2xM1 = Exponential.ExpM1(x+x);
		return e2xM1/(e2xM1 + ICountAble.TWO); }

	/** @return The Derivative at x	 */
	public double getDerivative(double x) {
		double tanh = Map(x);
		return ICountAble.ONE-tanh*tanh; }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		double tanh = Map(x);
		derivative.Value = ICountAble.ONE-tanh*tanh;
		return tanh; }

}
