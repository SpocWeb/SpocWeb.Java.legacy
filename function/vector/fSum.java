package function.vector;

//import Functions.AFunction;
import math.vector.VectorDouble;
import math.vector.VectorFloat;

/**This Class implements a Function that assumes arg to be a Tensor
 * and returns the Sum of all Coordinates.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:46:35Z
 * digest: 81ce4a0d18328efd3725695e173e11b5c505ddf19a051897d5732993d44a8daa
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * See also {@link fProduct}, which returns the Product of all Coordinates. */
public class fSum
//extends AFunction
implements IFloatScalarField
{

	/** Returns the sum of all coordinates of V. */
	public double Map(double[] V) {
		return VectorDouble.SUM(V); }

	/** Returns the sum of all coordinates of V. */
	public float Map(float[] V) {
		return (float) VectorFloat.SUM(V); }

}
