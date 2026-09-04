package function.derive.neuron;

import streamIO.object.IStreamIn;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import function.derive.IFloatDeriveAble;

/** Sigmoid Function with Derivative
  * This Function is one of the most useful Switching Functions, because it is smooth 
  * and Calculation of both Function value and Derivative are quite fast!
  * It rises slowly from (-Infinity, 0) through (0, 1/2) to (+Infinity, 1) 
  * 
  * Caches Results, because frequently
  * both the Function and the Derivative are requested interleaved. */
public class Sigmoid
//extends AFloatDeriveAble
implements IFloatDeriveAble {
    
	/** Reference to the single Instance of this Class	 */
	final static public Sigmoid Sigmoid = new Sigmoid();
	
	/** Empty Constructor, this Function is not scaled,
	  * but implemented as a Singleton.	*/
	protected Sigmoid() { }
	
	/** Caches for the Results	 */	double cacheX = Float.NaN;
	/** Caches for the Results	 */	double cacheY = Float.NaN;
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/** Sigmoid Function: 1/1+e^-x	 */
	public double Map(double x){
		if (cacheX == x) return cacheY;
			cacheX =  x;
		return cacheY = 1/(1+Math.exp(-x)); }
	
	/** Sigmoid Function: 1/1+e^-x	 */
	public float Map(float x){
		if (cacheX == x) return (float)  cacheY;
			cacheX =  x; return (float) (cacheY = 1/(1+Math.exp(-x))); }
	
	/** Derivative: 	 */
	public double getDerivative(double x) {
		double tmp = Map(x);
		return tmp*(1-tmp); }
	
	/** Derivative: 	 */
	public float getDerivative(float x) {
		float  tmp = Map(x);
		return tmp*(1-tmp); }
	
	/** Calculates Function and Derivative at the same time.
	  * This is economic, because both have similar Characteristics
	  * and thus the same characteristic Elements which speeds up calculation.
	  * @param  x the Position at which to calculate Function and Derivative.
	  * @param  derivative ByRef Object used to return the Value of the Derivative at x
	  * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		final double ret = 1/(1+Math.exp(-x));
		derivative.Value = ret*(1-ret);
		return ret; }
	
	/** Calculates Function and Derivative at the same time.
	  * This is economic, because both have similar Characteristics
	  * and thus the same characteristic Elements which speeds up calculation.
	  * @param  x the Position at which to calculate Function and Derivative.
	  * @param  derivative ByRef Object used to return the Value of the Derivative at x
	  * @return Function Value at x 	 */
	public float  getFuncDerive(float  x, ByRefFloat derivative) {
		final float ret = 1/(1+(float) Math.exp(-x));
		derivative.Value = ret*(1-ret);
		return ret; }
	
}
