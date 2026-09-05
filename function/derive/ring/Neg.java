package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import function.derive.ADeriveAble;
import function.derive.CCountAble;

/**This Class encapsulates the Negative Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:35:01Z
 * digest: 084cb8ae337da5f4705f7af814d8ddbafca125697e050dc68f617a736530cee2
 * stale: false
 * tags: [code/mathematical_function, code/invertible_function_contract]
 * concepts: [Function Algebra]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class Neg
extends ADeriveAble	//IPartialDerive //
{
	//static Section Start

	/**Local Reference to the single Instance	 */
	final static public Neg NEG = new Neg();

	/**This Function returns the Negative Function.  */
	public static ISemiGroup NEG(Object arg)	{ return ((IGroup) arg).neg(); }

    static { //Initializer
		NEG.setInverse   (NEG); //like Identity, Cjg and Inv this is it's own Inverse
		NEG.setDerivative(CCountAble._One);
        //make sure the Integral of Identity is set beforehand!
        //        IDeriveAble IdInt; if ((IdInt = Identity.getIntegral()) != null)
//        new Algebra();
//        IDeriveAble tmp = Square.Square;
		NEG.setIntegral  (new CatDerive(NEG, Square.xx_2)); //Identity.Identity.getIntegral()));
    }

	//static Section Stop

	/**private Constructor for Singleton Implementation	 */
	private Neg() {}

	/**This Function represents the additive Neg (Negative) Function.  */
	public Object Map (Object arg)	{ return ((IGroup) arg).neg(); } //neg(arg); }

	/**This Function represents the additive Neg Function.  */
	public double Map (double arg) { return -arg; }

	/**This Function represents the additive Neg Function.  */
	public float  Map (float  arg) { return -arg; }

	/**Returns the textual "-" representation of this Negation Function.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() { return "-"; }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) {
		return (arg instanceof Neg)
//			|| (arg instanceof ICountAble)
//			|| (arg instanceof IMeasurAble)
			|| (arg instanceof IGroup)
	;}

}
