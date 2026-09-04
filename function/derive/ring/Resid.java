package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.ring.IIntRing;
import function.ICountAble;
import function.derive.ADeriveAble;
import function.derive.CCountAble;
import function.derive.Identity;

/**This Class encapsulates the Resid Function: 1 - x
 * It adds the Constant (Function) 1 to it's Argument.
 * It should work exactly like Negative concatenated with AddAt,
 * but that is not realized everywhere yet!!  */
public class Resid
extends ADeriveAble {

	/**This Function returns the Square Function in a typed way.  */
	public static IIntRing RESID(Object arg) {
		return(IIntRing)
			 ((IIntRing) arg).Resid(); }

	/**Local Reference to the single Instance	 */
	final static public Resid RESID = new Resid();

	/**private Constructor for Singleton Implementation	 */
	private Resid() {
		setInverse   (this);
		setDerivative(CCountAble._One);
		setIntegral  (new Diff(Identity.IDENTITY, Square.xx_2)); //Identity.Identity.getIntegral()));
	}

	/**This Function represents the Resid Function: 1-x  */
	public Object Map(Object arg) { return ((IIntRing) arg).Resid(); } //Resid(arg); }

	/**This Function represents the Resid Function: 1-x  */
	public double Map(double x) { return ICountAble.ONE-x; }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) {
		return (arg instanceof Resid)
			|| (arg instanceof IIntRing); }

}
