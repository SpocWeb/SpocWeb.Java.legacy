package function.derive.ring;

//import Stream.Copy.*;
//import Stream.Copy.Group.*;
import streamIO.copy.group.ring.IIntRing;
import function.ICountAble;
import function.IFunction;
import function.derive.ADeriveAble;
import function.derive.CCountAble;
import function.derive.CMeasurAble;
import function.derive.Identity;

/**This Class encapsulates the ThirdAt Function.  */
final public class ThirdAt
extends ADeriveAble {

	/**This Function returns the ThirdAt Function.  */
	public static IIntRing thirdAt(Object arg) {
		return  ((IIntRing) arg).thirdAt(); }

	/**Local Reference to the single Instance, called like the Function,
	 * because it must be implemented in each Subclass anyway.	 */
	final static public ThirdAt ThirdAt = new ThirdAt();

	/**private Constructor for Singleton Implementation	 */
	private ThirdAt() {
		setInverse   (TripleAt.TripleAt);
		setDerivative(CCountAble.Three);
		setIntegral  (new CatDerive( this, Square.xx_2)); //Identity.Identity.getIntegral()));
		//Int[x/3] = x^2/6
	}

	/**This Function represents the ThirdAt Function: x/3  */
	public Object Map (Object arg)	{ return ((IIntRing) arg).third  (); } //slower!

	/**This Function represents the ThirdAt Function: x/3  */
	public Object MapAt(Object arg)	{ return ((IIntRing) arg).thirdAt(); } //slower!

	/**This Function represents the ThirdAt Function: x/3  */
	public double Map(double x) { return x/ICountAble.THREE; }

	/**Returns an alternative Representation that is easier to simplify	 */
	public IFunction simplify() {
		return new Prod( CMeasurAble.Half, Identity.IDENTITY); }

}
