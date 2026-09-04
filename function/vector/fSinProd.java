package function.vector;

import function.IMeasurAble;

/**Returns the Product of the Sinusses of all Coordinates times Pi,
 * i.e. the full Period fits into the unit Circle. (Example Function)
 */
public class fSinProd
//extends AFunction
implements IFloatScalarField {

	/** Singleton, because Stateless    */
	public static fSinProd fSinProd = new fSinProd();

	/** protected Constructor to support the Singleton */
	protected fSinProd(){}

	/** Scalar Function
	 * @return the Product of all Sinuses of the Dimension Values
	 */
	public double Map(double[] V) {
		int len = V.length;
		double Prod = 1;
		while (--len >= 0)
			Prod *= Math.sin(V[len] * IMeasurAble.PI);
		return  Prod; }

	/** Scalar Function
	 * @return the Product of all Sinuses of the Dimension Values
	 */
	public float Map(float[] V) {
		int len = V.length;
		double Prod = 1;
		while (--len >= 0)
			Prod *= Math.sin(V[len] * IMeasurAble.PI);
		return (float) Prod; }

}
