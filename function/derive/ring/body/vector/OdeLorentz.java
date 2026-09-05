package function.derive.ring.body.vector;

import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.AIntRing;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import streamIO.copy.group.ring.metric.body.vector.Tensor;
import streamIO.copy.groupM.IGroupM;

/**ODE (Differentialgleichung) for the chaotic Lorentz curve,
 * welche die Konvektionsrollen zwischen Schichten beschreibt.
 * Eine weitere 'zeitunabh�ngige' Differentialgleichung.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:43:48Z
 * digest: 2d76a232e5d6104fb866e1271d7219fd64c0d372ca050de24e92657e3eef15b0
 * stale: false
 * tags: [code/differential_integration]
 * concepts: [Ordinary Differential Equations, Chaos Theory]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class OdeLorentz
implements IODE {

	/**Parameter b	 */	protected static final double bDefault =  4.0;
	/**Parameter s	 */	protected static final double sDefault = 16;
	/**Parameter r	 */	protected static final double rDefault = 46;

	/**Parameter b	 */	protected Double b = new Double(bDefault);
	/**Parameter s	 */	protected Double s = new Double(sDefault);
	/**Parameter r	 */	protected Double r = new Double(rDefault);

	/**Empty Constructor, defaults all Parameters of the Lorentz ODE	 */
	public OdeLorentz(){}

	/**Constructor, taking all Parameters of the Lorentz ODE	 */
	public OdeLorentz(double b, double s, double r) {
		this.b = new Double(b);
		this.s = new Double(s);
		this.r = new Double(r);
	}

	/**Computes the Lorentz System's Derivative y at Point x, ignoring the time-invariant Parameter t.	 */
	public void Funktion (IIntRing t, IIntRing x_, IIntRing y_) {	//
		Tensor x = (Tensor) x_;
		Tensor y = (Tensor) y_;
		y.a[0] = (AIntRing)((IGroupM) x.a[1].sub(x.a[0])).mulAt(s);
		y.a[1] = (AIntRing)((IGroup ) x.a[1].sub(((IGroupM)(x.a[2].sub(r))).mulAt(x.a[0])));
		y.a[2] = (AIntRing)((IGroup ) x.a[0].mul (x.a[1])).subAt(x.a[2].mul (b));
	}

}
