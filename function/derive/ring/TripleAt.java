package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.ISemiGroup;
import function.IFunction;
import function.derive.ADeriveAble;
import function.derive.CCountAble;
import function.derive.Identity;

/**This Class encapsulates the DoubleAt Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 5d018faaff549ebf9f194aebce5a72c49447d2a7610f67088f419ed4f78dc4d7
 * stale: false
 * tags: [code/mathematical_function]
 * concepts: [Function Algebra]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class TripleAt
extends ADeriveAble {

	/**This Function returns the DoubleAt Function.  */
	public static ISemiGroup trpl(Object arg) { return ((ISemiGroup) arg).trpl(); }

	/**Local Reference to the single Instance	 */
	final static public TripleAt TripleAt = new TripleAt();

    static { //Initializer
		TripleAt.setInverse   (ThirdAt.ThirdAt ); 	//Inv[3x] = x/3
		TripleAt.setDerivative(CCountAble.Three); 	//Der[3x] =   3
		TripleAt.setIntegral  (new CatDerive(TripleAt, Square.xx_2)); 	//Int[3x] = 3*x^2/2
    }

	/**private Constructor for Singleton Implementation	 */
	private TripleAt() {
	}

	/**This Function represents the DoubleAt Function: 2*x  */
	public Object Map (Object arg)	{return ((ISemiGroup) arg).trpl(); } //trpl(arg);}

	/**This Function represents the DoubleAt Function: 2*x  */
	public double Map(double x) { return x*3; }

	/**Returns an alternative Representation that is easier to simplify	 */
	public IFunction simplify() {
		return new Prod( CCountAble.Three, Identity.IDENTITY); }

}
