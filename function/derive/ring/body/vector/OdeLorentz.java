package function.derive.ring.body.vector;

import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.AIntRing;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import streamIO.copy.group.ring.metric.body.vector.Tensor;
import streamIO.copy.groupM.IGroupM;

/**ODE (Differentialgleichung) for the chaotic Lorentz curve,
 * welche die Konvektionsrollen zwischen Schichten beschreibt.
 * Eine weitere 'zeitunabhängige' Differentialgleichung.
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

	public void Funktion (IIntRing t, IIntRing x_, IIntRing y_) {	//
		Tensor x = (Tensor) x_;
		Tensor y = (Tensor) y_;
		y.a[0] = (AIntRing)((IGroupM) x.a[1].sub(x.a[0])).mulAt(s);
		y.a[1] = (AIntRing)((IGroup ) x.a[1].sub(((IGroupM)(x.a[2].sub(r))).mulAt(x.a[0])));
		y.a[2] = (AIntRing)((IGroup ) x.a[0].mul (x.a[1])).subAt(x.a[2].mul (b));
	}

}
