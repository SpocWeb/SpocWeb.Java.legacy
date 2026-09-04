package function.vector;

//import Functions.AFunction;
import math.vector.VectorDouble;
import math.vector.VectorFloat;

/**This Class implements a Scalar Field Function that assumes arg to be a Vector (Tensor)
 * and returns the Product of all Coordinates.
 * See also fSum, which returns the Sum of all Coordinates.
 */
public class fProduct
//extends AFunction
implements IFloatScalarField
{

	/** Singleton, because Stateless    */
	public static fProduct fProduct = new fProduct();

	/** protected Constructor to support the Singleton */
	protected fProduct(){}

	/** Scalar Function
	 * @return the Product of all Dimension Values
	 */
	public double Map(double[] V) {
		return VectorDouble.PROD(V); }

	/** Scalar Function
	 * @return the Product of all Dimension Values
	 */
	public float Map(float[] V) {
		return (float) VectorFloat.PROD(V); }

}