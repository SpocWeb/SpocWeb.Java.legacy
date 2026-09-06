package function.vector;

/**ODE (Differentialgleichung) for the chaotic Lorentz curve,
 * welche die Konvektionsrollen zwischen Schichten beschreibt.
 * Eine weitere 'zeitunabh�ngige' Differentialgleichung.
 *
 * The Range for the Lorentz Curve is
 * x [-13,+13]
 * y [-24,+24]
 * z [ 25, 40]
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:46:42Z
 * digest: 78e12c66f675563f27a9cf79c5289c1c0b22208c9b833fab701fec0d66ec57bb
 * stale: false
 * tags: [code/differential_integration, code/vector_math]
 * concepts: [ODE Integration]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class OdeLorentz
extends AOdeFloat {

	/**Parameter b	 */	protected static final double bDefault =  4.0;
	/**Parameter s	 */	protected static final double sDefault = 16;
	/**Parameter r	 */	protected static final double rDefault = 46;

	/**Parameter b	 */	protected double b = bDefault;
	/**Parameter s	 */	protected double s = sDefault;
	/**Parameter r	 */	protected double r = rDefault;

	/**Empty Constructor, defaults all Parameters of the Lorentz ODE	 */
	public OdeLorentz() { }

	/**Constructor, taking all Parameters of the Lorentz ODE	 */
	public OdeLorentz(double b, double s, double r) {
		this.b = b;
		this.s = s;
		this.r = r;
	}

	/**
     * Lorentz' ODE for describing the Behavior of Convection Rolls
     * in a reduced Coordinate System:
     * x' = (y-x)*s
     * y' = -y-x*(z-r) == x*(r-z)-y
     * z' = x*y-(z*b)
     *
     * The Dimensions have the following Meaning:
     */
	public void Funktion (double t, double[] x, double[] y) {	//
		y[0] =(x[1]-x[0])*s;
		y[1] =-x[1]-x[0] *(x[2]-r);
		y[2] =(x[0]*x[1])-(x[2]*b);
	}

////////////////////////////////////////////////////////////////////////////
/// #region : Interface IOdeFloat : Implementation
////////////////////////////////////////////////////////////////////////////

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	public double Funktion(double x, double y) { throw new AbstractMethodError(); }

}
