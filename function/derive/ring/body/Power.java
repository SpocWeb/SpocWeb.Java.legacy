package function.derive.ring.body;

//import Stream.Copy.*;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import streamIO.object.IStreamIn;
import function.IInvertAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.Const;
import function.derive.IDeriveAble;
import function.derive.ring.Prod;

/**This Class encapsulates the Power Function for arbitrary H:	x^H
 * It returns the Argument raised to the fixed Power H for rational H,
 * not only integer H	 */
public class Power
extends AFloatDeriveAble {
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Constant Exponent that is used.	 */
	public Object X;
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor	 */
	//protected Power(){}
	
	/**Initializing Constructor	 */
	public Power(final Object Exponent) { X = Exponent; }

	/**Returns the Inverse Function to this one:
	 * Infinite Recursion, if precompiled!	 */
	public IInvertAble getInverse(){
		if (Inverse != null) return Inverse;
		setInverse(new Power(((IGroupM)X).inv()));
		return Inverse; }
	
	/**Returns the Derivative	 */
	public IDeriveAble getDerivative() {
		if (Derivative != null) return Derivative;
		setDerivative(new Prod(new Const(X), new Power(((IIntRing) X).pred()))); //otherwise a Recursion is possible
		return Derivative; }
	
	/**Returns the Integral	 */
	public IDeriveAble getIntegral() {
		if (Integral != null) return Integral;
		IIntRing XP1 =  (IIntRing)
							((IIntRing) X).succ();
		setIntegral(new Prod(new Const(XP1.inv()), new Power(XP1))); //otherwise this would lead to a Recursion
		return Integral; }
	
	/**This Function encapsulates the Power Function.	 */
	public Object Map (Object arg)	{ return ((MetricBody)arg).Pow(X); }
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() {
        if (ByRefDouble.GET_DOUBLE(X) > 0)
            return IStreamIn.ORDER_ASC_STRICT; 
        return IStreamIn.ORDER_DESC_STRICT; 
    }
    
	/**This Function encapsulates the Power Function.	 */
	public double Map (double arg)	{ return Math.pow(arg, ByRefDouble.GET_DOUBLE(X)); }
	
	/** @return The Derivative at x	 */
	public double getDerivative(double arg) {
		double x = ByRefDouble.GET_DOUBLE(X);
		return x*Math.pow(arg, x-1); }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation. 	 */
	public double getFuncDerive(double arg, ByRefDouble derivative) {
		double x = ByRefDouble.GET_DOUBLE(X);
		double ret = Math.pow(arg, x); derivative.Value = x*ret/arg;
		return ret; }

	/**@return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString()	{ return "^" + X;}

}
