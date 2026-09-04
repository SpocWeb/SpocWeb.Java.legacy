package function.vector;

//import Functions.AFunction;
import math.vector.VectorDouble;
import math.vector.VectorFloat;

/**This Class implements a Function that assumes arg to be a Tensor
 * and returns the Product of all Coordinates.
 * See also fSum, which returns the Sum of all Coordinates. */
public class fSum
//extends AFunction
implements IFloatScalarField
{

	public double Map(double[] V) {
		return VectorDouble.SUM(V); }

	public float Map(float[] V) {
		return (float) VectorFloat.SUM(V); }

}
