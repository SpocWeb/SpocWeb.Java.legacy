package function.vector;

//import Functions.AFunction;
import math.vector.VectorDouble;
import math.vector.VectorFloat;

/**This Class implements a Scalar Field Function that assumes arg to be a Vector (Tensor)
 * and returns the Product of all Coordinates.
 * See also fSum, which returns the Sum of all Coordinates.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 0a78d73725d9c6dff80a6ef6c90c4327484bb2e810434cce2aefb9027033c25d
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
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