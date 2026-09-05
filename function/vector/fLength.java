package function.vector;

//import Functions.AFunction;
import math.vector.VectorDouble;
import math.vector.VectorFloat;

/**This Class implements a Function that assumes arg to be a Tensor
 * and returns the Square of the Euklidean Length of all Coordinates.
 * @see fSum, which returns the Sum of all Coordinates.
 * @see fProduct, which returns the Sum of all Coordinates.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:46:22Z
 * digest: b63d671cae87d632aaefa230a7925ee961a5f8365dff8ac234a8dc77bf17daf6
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class fLength
//extends AFunction
implements IFloatScalarField
{

	/** this Vector is subtracted from the Argument if not null */
	public double[] V0;

	/** Returns the squared Euclidean length of V, or its squared distance to {@link #V0} if set. */
	public double Map(double[] V) {
		if (V0 == null) {
			return VectorDouble.NORM_SQR(V    ); }
			return VectorDouble.DIST_SQR(V, V0); }

	/** Returns the squared Euclidean length of V, or its squared distance to {@link #V0} if set. */
	public float Map(float[] V) {
		if (V0 == null) {
			return (float) VectorFloat.NORM_SQR(V    ); }
			return (float) VectorFloat.DIST_SQR(V, V0); }

}
