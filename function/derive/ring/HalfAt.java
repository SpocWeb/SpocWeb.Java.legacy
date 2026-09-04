package function.derive.ring;

//import Stream.Copy.*;
//import Stream.Copy.Group.*;
import streamIO.copy.group.ring.IIntRing;
import function.IFunction;
import function.IMeasurAble;
import function.derive.ADeriveAble;
import function.derive.CCountAble;
import function.derive.CMeasurAble;
import function.derive.Identity;

/**This Class encapsulates the HalfAt Function.  */
final public class HalfAt
extends ADeriveAble {

	/**This Function returns the HalfAt Function.  */
	public static IIntRing halfAt(Object arg) {
		return  ((IIntRing) arg).halfAt(); }

	/**Local Reference to the single Instance, called like the Function,
	 * because it must be implemented in each Subclass anyway.	 */
	final static public HalfAt HalfAt = new HalfAt();

    static { //Initializer
		HalfAt.setInverse   (DoubleAt.DoubleAt);
		HalfAt.setDerivative(CCountAble.Two); if (Square.xx_2 != null)
		HalfAt.setIntegral  (new CatDerive(HalfAt, Square.xx_2));
        //Identity.Identity.getIntegral())); //Int[x/2] = x^2/4
    }

	/**private Constructor for Singleton Implementation	 */
	private HalfAt() {
	}

	/**This Function represents the HalfAt Function: x/2  */
	public Object Map  (Object arg)	{ return ((IIntRing) arg).half  (); } //dbl(arg); } //slower!

	/**This Function represents the HalfAt Function: x/2  */
	public Object MapAt(Object arg)	{ return ((IIntRing) arg).halfAt(); } //dbl(arg); } //slower!

	/**This Function represents the HalfAt Function: x/2  */
	public double Map(double x) { return x*IMeasurAble.HALF; }

	/**Returns an alternative Representation that is easier to simplify	 */
	public IFunction simplify() {
		return new Prod( CMeasurAble.Half, Identity.IDENTITY); }

}
