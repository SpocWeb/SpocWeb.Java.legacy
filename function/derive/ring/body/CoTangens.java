package function.derive.ring.body;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import function.derive.AStatic;
import function.derive.IFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.CatDerive;
import function.derive.ring.Inv;
import function.derive.ring.Neg;
import function.derive.ring.Square;
import function.derive.ring.Succ;
import function.derive.ring.Sum;

/**This Class encapsulates the CoTangens Function.
 * It solves this Differential Equation: f' = 1+f^2
 * It has the following Properties:
 * CoTangens    =  Cosinus/Sinus = 1/Tangens
 * CoTangens'   =  1+Tangens^2 = 1/Cosinus^2
 * Int[CoTangens] =  -Log(Cos(x))
 * Inv(CoTangens) =  ArcCot
 *
 * cot(-x) == -cot(x) 		(ASymmetric)
 * cot( x) == cot(x+Pi)		(Periodic)
 * cot( x) == tan(x+Pi/2)
 * cot(n*Pi) == Infinity
 *
 * Addition Theorem
 * cot(x+/-y) == (cot(x)*cot(y) -/+ 1) / (cot(x) +/- cot(y))
 * cot(2*x)*2 == (1-cot^2(x)) / cot(x) == cot(x) - tan(x)
 * cot(3*x) == cot(x)*(cot^2(x) - 3) / (3*cot^2(x) - 1)
 * cot(4*x)*4*cot(x) == (1 - 6*cot^2(x) + cot^4(x)) / (1-cot^2(x))
 * cot(x/2) == +/-SqRt((1+cos(x)) / (1-cos(x)))
 *
 * cot(x) +/- cot(y) == +/- sin(x +/- y)/(sin(x)*sin(y))
 * cot(x)  *  cot(y) ==(cot(x)+cot(y))/(1/cot(x) + 1/cot(y))
 *
 * Power Series (converges for |x| < Pi and better than the one for tan():
 * cot(x) = 1/x - (x/3 + x^3/45 + x^5*2/945 + x^7/4725 + ...
 * 			+ x^(2*n-1)*2^(2n)*Bernoulli[n]/(2n)!
 *
 */
public class CoTangens
extends AStatic
implements IFloatDeriveAble {
    
	/**Local Reference to the single Instance	 */
	final static public CoTangens CoTangens = new CoTangens();
	
	/**Local Reference to the single Instance	 */
	final static public CatDerive
	SqrCoTangens = 	new CatDerive(Square.SQUARE, CoTangens);
	
	static { //Initializer
		//Integral of cot^2
		SqrCoTangens.setIntegral (	new CatDerive(Neg.NEG,
									new Sum(CoTangens,Identity.IDENTITY)));
		CoTangens.simple = new CatDerive(Inv.INV, Tangens.TANGENS);
		CoTangens.ProcessAble = Tangens.class;
		CoTangens.setInverse   (ArcCos.ARC_COS);
		CoTangens.setDerivative(new CatDerive(Neg .NEG ,
								new CatDerive(Succ.SUCC, SqrCoTangens)));	//
		CoTangens.setIntegral  (new CatDerive(Neg .NEG ,
								new CatDerive(Logarithm.LOGARITHM, Sinus.SINUS)));
	}

	/**private Constructor for Singleton Implementation
	 * sets the Inverse: Pi/2-ArcTan(x)
	 * the Derivative:   -(1+cot^2) = -1/sin^2
	 * and the Integral: */
	private CoTangens(){ }
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }
    
	/**This Function represents the CoTangens Function.	 */
	public Object Map (Object arg) { return ((MetricBody) arg).cot(); }
	
	/*	Returns the Cotangens(x) = 1/Tangens(x) for all x 	*/
	public double Map(double x) { return ICountAble.ONE / Math.tan(x); }	//

	/*	Returns the Cotangens(x) = 1/Tangens(x) for all x 	*/
	public float Map(float x) { return 1.0f / (float) Math.tan(x); }	//

	/** @return The Derivative at x	 */
	public float getDerivative(float x) {
		float cot = (float) (ICountAble.ONE / Math.tan(x));
		return ICountAble._ONE-cot*cot; }

	/** @return The Derivative at x	 */
	public double getDerivative(double x) {
		double cot = ICountAble.ONE / Math.tan(x);
		return ICountAble._ONE-cot*cot; }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		double cot = ICountAble.ONE / Math.tan(x);
		derivative.Value = ICountAble.ONE - cot*cot;
		return cot; }	//

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public float getFuncDerive(float x, ByRefFloat derivative) {
		float cot = 1.0f / (float) Math.tan(x);
		derivative.Value = ICountAble._ONE - cot*cot;
		return cot; }	//

}
