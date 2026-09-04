package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.ring.IIntRing;
import function.ICountAble;
import function.derive.ADeriveAble;
import function.derive.CCountAble;

/**This Class encapsulates the Predecessor Function.
 * It subtracts the Constant (Function) 1 form it's Argument.
 * It should work exactly like AddAt, but that is not realized everywhere yet!!  */
public class Pred
extends ADeriveAble {

	/**This Function returns the Square Function in a typed way.  */
	public static IIntRing PRED(Object arg) {
		return (IIntRing) ((IIntRing) arg).pred(); }

	/**Local Reference to the single Instance	 */
	final static public Pred PRED = new Pred();

	/**private Constructor for Singleton Implementation	 */
	private Pred() {
		setInverse   (Succ.SUCC);
		setDerivative(CCountAble.One);
		setIntegral  (new CatDerive(Square.xx_2, this)); //Identity.Identity.getIntegral(), this));
//				   new Diff( Identity.Identity.getIntegral(),
//							 Identity.Identity);}
	}

	/**This Function represents the Pred Function: x-1  */
	public Object Map (Object arg) { return ((IIntRing) arg).pred(); } //Pred(arg); }

	/**This Function represents the Pred Function: x-1  */
	public double Map(double x) { return x-ICountAble.ONE; }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) {
		return (arg instanceof Pred)
			|| (arg instanceof IIntRing); }

}
