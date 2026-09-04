package function.vector;

//import Functions.AFunction;
import math.vector.VectorDouble;
import math.vector.VectorFloat;

/**This Class implements a Function that assumes arg to be a Tensor
 * and returns the Square of the Euklidean Length of all Coordinates.
 * @see fSum, which returns the Sum of all Coordinates.
 * @see fProduct, which returns the Sum of all Coordinates.
 */
public class fLength
//extends AFunction
implements IFloatScalarField
{

	/** this Vector is subtracted from the Argument if not null */
	public double[] V0;

	public double Map(double[] V) {
		if (V0 == null) {
			return VectorDouble.NORM_SQR(V    ); }
			return VectorDouble.DIST_SQR(V, V0); }

	public float Map(float[] V) {
		if (V0 == null) {
			return (float) VectorFloat.NORM_SQR(V    ); }
			return (float) VectorFloat.DIST_SQR(V, V0); }

}
