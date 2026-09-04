package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.groupM.IGroupM;
import function.ICountAble;
import function.derive.ADeriveAble;

/**This Class encapsulates the Double Function.  */
final public class Inv
extends ADeriveAble {

	/**This Function returns the Double Function.  */
	final static public IGroupM INV(Object arg) { return ((IGroupM) arg).inv(); }

	/**Local Reference to the single Instance	 */
	final static public Inv INV = new Inv();

    static { //Initializer
		INV.setInverse   (INV);
		INV.setDerivative(new CatDerive(   Neg.NEG,
						  new CatDerive(   Inv.INV,
										Square.SQUARE)));	//Define -1/x^2 Function
    }

	/**private Constructor for Singleton Implementation	 */
	private Inv() { }

	/**This Function represents the multiplicative Inv Function.  */
	public Object Map (Object arg) { return ((IGroupM) arg).inv(); } //inv(arg); }

	/**This Function represents the multiplicative Inv Function.  */
	public double Map (double arg) { return ICountAble.ONE / arg; }

	/**This Function represents the multiplicative Inv Function.  */
	public float  Map (float  arg) { return ICountAble.ONE / arg; }

	/**@return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() { return "1/"; }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) {
		return (arg instanceof Inv)
//			|| (arg instanceof ICountAble)
//			|| (arg instanceof IMeasurAble)
			|| (arg instanceof IGroupM)
	;}

}
