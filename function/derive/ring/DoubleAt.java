package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.ISemiGroup;
import function.IFunction;
import function.derive.ADeriveAble;
import function.derive.CCountAble;
import function.derive.Identity;

/**This Class encapsulates the DoubleAt Function.  */
final public class DoubleAt
extends ADeriveAble {

	/**This Function returns the DoubleAt Function.  */
	public static ISemiGroup dbl(Object arg) { return ((ISemiGroup) arg).dbl(); }

	/**Local Reference to the single Instance	 */
	final static public DoubleAt DoubleAt = new DoubleAt();

    static { //Initializer
		DoubleAt.setInverse   (HalfAt.HalfAt ); 	//Inv[2x] = x/2
		DoubleAt.setDerivative(CCountAble.Two); 	//Der[2x] =   2
		DoubleAt.setIntegral  (Square.SQUARE ); 	//Int[2x] = x^2
    }

	/**private Constructor for Singleton Implementation	 */
	private DoubleAt() {
	}

	/**This Function represents the DoubleAt Function: 2*x  */
	public Object Map (Object arg)	{return ((ISemiGroup) arg).dbl(); } //dbl(arg);}

	/**This Function represents the DoubleAt Function: 2*x  */
	public double Map(double x) { return x+x; }

	/**Returns an alternative Representation that is easier to simplify	 */
	public IFunction simplify() {
		return new Prod( CCountAble.Two, Identity.IDENTITY); }

}
