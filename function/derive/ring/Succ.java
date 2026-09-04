package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.ring.IIntRing;
import function.ICountAble;
import function.derive.ADeriveAble;
import function.derive.CCountAble;

/**This Class encapsulates the Successor Function.
 * It adds the Constant (Function) 1 to it's Argument.
 * It should work exactly like AddAt, but that is not realized everywhere yet!!  */
public class Succ
extends ADeriveAble {

	/**This Function returns the Square Function in a typed way.  */
	public static IIntRing SUCC(Object arg) {
		return (IIntRing) ((IIntRing) arg).succ(); }

	/**Local Reference to the single Instance	 */
	final static public Succ SUCC = new Succ();

	/**private Constructor for Singleton Implementation	 */
	private Succ() {
		setInverse   (Pred.PRED);
		setDerivative(CCountAble.One); //both Integrals are correct
		setIntegral  (new CatDerive(Square.xx_2, this)); //Identity.Identity.getIntegral(), this));
//		setIntegral  (new Sum(      Identity.Identity, Square.xx_2)); //Identity.Identity.getIntegral());
	}

	/**This Function represents the Succ Function: x+1  */
	public Object Map (Object arg) { return ((IIntRing) arg).succ(); } //Succ(arg); }

	/**This Function represents the Succ Function: x-1  */
	public double Map(double x) { return x+ICountAble.ONE; }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) {
		return (arg instanceof Succ)
			|| (arg instanceof IIntRing)
	;}

}
