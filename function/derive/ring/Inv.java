package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.groupM.IGroupM;
import function.ICountAble;
import function.derive.ADeriveAble;

/**This Class encapsulates the Double Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:34:06Z
 * digest: 8f55ea31cd8f214d23a90301a11bd5ab78e1ea315e9a3c8800860a5384e7313d
 * stale: false
 * tags: [code/invertible_function_contract, code/mathematical_function]
 * concepts: [Function Algebra]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
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

	/**Returns the textual "1/" prefix representation of this multiplicative Inverse Function.
	 * @return  The string representation of the Function.
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
