package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import function.ICountAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.CCountAble;

/**Implements a non continuous Version of the Sign Function,
 * which rises from -1 to 1 instantaneously at x = 0.
 * The Width of the Interval in which this Function rises
 * is assumed to 0, so this symbolizes a real discontinuous Function
 * It could be derived to a Delta Function, but that would not scale properly
 * since any Value of Infinity is not comparable. 	 */
public class Sign
extends AFloatDeriveAble {
	
	/**Local Reference to the single Instance	 */
	final static public Sign Sign = new Sign();
	
    static { //Initializer
		//Inverse is not defined, no Function
		Sign.setDerivative(new CatDerive(DoubleAt.DoubleAt, new Delta1(null)));
		Sign.setIntegral  (AbsV.AbsV);
	}
    
	/**private Constructor for Singleton Implementation	 */
	private Sign() {
		//there is no simple Representation
	}
	
	/**This Function represents the Sign Function.
	 * It always returns the Sign of the Argument.  */
	public Object Map (final Object arg) {
//		return ((MetricIRing) arg).Sign();
		if (((IScalarMetric)arg).negative())
			return CCountAble._One;
			return CCountAble. One; }
	
	/**This Function represents the Sign Function.
	 * It always returns the Sign of the Argument.  */
	public Object MapAt (final Object arg) {
		return ((IMetricIRing) arg).SignAt(); }

	/**This Function represents the Sign Function.  */
	public double Map (final double arg) {
		if (arg > ICountAble.ZERO) return ICountAble. ONE; //most probable
		if (arg < ICountAble.ZERO) return ICountAble._ONE;
								   return ICountAble.ZERO; } //least probable

	/** @see function.derive.AFloatDeriveAble#getDerivative(double)	 */
	public double getDerivative(final double x) { 
		if (x == 0) //this is not exact, rather use a more or less smooth Delta Representation. 
			return Double.POSITIVE_INFINITY; 
		return 0; }
	
	/** @see function.derive.AFloatDeriveAble#getFuncDerive(double, function.byref.ByRefDouble)	 */
	public double getFuncDerive(final double x, final ByRefDouble derivative) {
		if (x == 0) { //this is not exact, rather use a more or less smooth Delta Representation.
			derivative.Value = Double.POSITIVE_INFINITY; 
			return 0;
		}
		if (x    > ICountAble.ZERO) 
			return ICountAble. ONE; //most probable
		    return ICountAble._ONE;
	}

}
