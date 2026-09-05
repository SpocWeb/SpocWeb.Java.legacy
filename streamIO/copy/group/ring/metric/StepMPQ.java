package streamIO.copy.group.ring.metric;

import streamIO.copy.ACopyAble;
import streamIO.copy.group.ring.AStepper;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import streamIO.copy.group.ring.StepMP;
import function.byref.ByRefInt;

/**Integrates the given ODE in (x,y) and controls the Step Width
 * by using MultiPoint Formula
 * with Bulirsch-Stoer Extrapolation of Step width to 0.
 * This is exactly the Trapezoidal Rule
 * used for Integration of normal Functions.
 * The previously calculated Points can unfortunately not be re-used
 * (unlike with Trapezoidal Integration).
 *
 * Step() performs a single Step and controls it's width.
 * Run () performs as many Steps as necessary to reach a certain x Value.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 53d6368d4783bc38d993d25896527a440595b955d07bc94750b6f5bcca952cc9
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class StepMPQ
extends      StepMP {

	/**Maximum Number of Points used for Extrapolation	 */
	final static public int MaxGrad = 6;

	/**Default Value chosen by the constructors, if nothing specified.
	 * Rational Extrapolation doesn't work well with Vectors. 	 */
	final static public boolean rational = false;

	/**Default Value chosen by the constructors, if nothing specified	 */
	final static public int Power = 2;	//2 == (quadratic Convergence)

	/** Constructor with all necessary Parameters  */
	public StepMPQ(IIntRing Step, IIntRing x, IIntRing y, IODE f) {
		super(Step, x, y, f);
		xOld = (IIntRing) y.newInstance();
		yOld = (IIntRing) y.newInstance();	//new Tensor(); yOld.Carry = x; yOld.letGrad(y.getDim(), true, false);
	}

	/**Scaling Vector to adjust for different Sizes in some Dimensions	 */
	public IIntRing yScale;

	/**Extrapolator, used to estimate the real y Value	 */
	private ExtraPolValue xTra = new ExtraPolValue(MaxGrad, Power, rational);

	/**Old x Value, if the Step Size was too large	 */
	private IIntRing xOld;

	/**Old y Value, if the Step Size was too large	 */
	private IIntRing yOld;

	/**Bulirsch-Stoer-Schritt mit Beobachtung des lokalen Abbruchfehlers
	 * zur Loesung der DGL in (x,Y),die durch f bestimmt wird. 	 */
	public IIntRing step(IIntRing h) {
		//ByRefInt statt ByRefLong, weil es unwahrscheinlich ist, so gro�e Auswertungen zu fahren.
		ByRefInt n1 = new ByRefInt(2);
		ByRefInt n2 = new ByRefInt(3);
		IIntRing H  = h;
		replace = false; super.step(H);
		derive  = false;
		while (! xTra.addPoint(H, W)) {
			H = (IIntRing) h.div(n1);
			numSteps = (int) n1.Value; super.step(H);
			n1.Value <<= 1;
			ByRefInt tmp = n1; n1 = n2; n2 = tmp;
		}
		y = xTra.getValue();
		xTra.reset(Power, rational);
		x.addAt(h);
		return y; }	//The result is typically much more accurate.


	/**Method to test all Implementations in this class.	 */
	public static void testIt() {
		//Testing single Step of Runge-Kutta Method with Quality Control
		System.out.println("Testing StepRKQ (Runge-Kutta):");
		IIntRing xl = (IIntRing) ACopyAble.testInstance.copy();
//  	super.testIt();	//not possible
		AStepper.testIntRing(new StepRKQ(xl, xl, xl, null));
	}

}
