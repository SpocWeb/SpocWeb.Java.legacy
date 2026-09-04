package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.groupM.ISemiGroupM;
import function.IFunction;
import function.derive.ADeriveAble;
import function.derive.Identity;

/**This Class encapsulates the Square Function.  */
final public class Square
extends ADeriveAble {

	/**This Function returns the Square Function in a typed way.  */
	public static ISemiGroupM sqr(Object arg) { return ((ISemiGroupM) arg).sqr(); }

	/**Local Reference to the single Instance	 */
	final static public Square SQUARE = new Square();

	/**Constant containing the Function x^2/2
	 * Integral of Identity	 */
	final static public CatDerive xx_2 = new CatDerive(HalfAt.HalfAt, SQUARE);

    static { //Initializer
		SQUARE.setInverse   (SqRt.SQRT);
		SQUARE.setDerivative(DoubleAt.DoubleAt);
		SQUARE.setIntegral  (new CatDerive(ThirdAt.ThirdAt, new Prod(SQUARE, Identity.IDENTITY)));
    }

	/**private Constructor for Singleton Implementation of Sqr == x*x == x^2
	 * Also sets Inverse: SqRt()
	 * the Derivative:   2*x
	 * and the Integral: x^3/3  */
	private Square() {
	}

	/**This Function represents the Square Function: x^2  */
	public Object Map (Object arg) { return ((ISemiGroupM) arg).sqr(); } //sqr(arg); }

	/**This Function represents the Square Function: x^2  */
	public double Map   (double x) { return x*x; }

	/**This Function represents the Square Function: x^2  */
	public static double SQUARE(double x) { return x*x; }

	/**Returns the alternative Representation which can be simplified */
	public IFunction simplify() { return new Prod( Identity.IDENTITY, Identity.IDENTITY); }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) {	return arg instanceof ISemiGroupM; }

}
