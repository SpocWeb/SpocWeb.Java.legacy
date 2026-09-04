package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.StepTrapez;
import function.IFunction;

/**Integrates the Function in x, but does not control the Step Width,
 * by using the Trapez Formula with Bulirsch-Stoer Extrapolation of Step width to 0.
 * The previously calculated Points are re-used (not possible for ODEs)
 * That's why the corresponding ODE Method StepMP has been singled out.
 *
 * Step() performs a single Step with fixed width.
 * Run () performs as many Steps as necessary to reach a certain x Value.
 * When too many Points are necessary to calculate a certain Step,
 * the Step Size should be reduced around the critical Area.	*/
public class StepTrapezQ
extends      StepTrapez {

	/**Maximum Number of Points used for Extrapolation	 */
	final static public int MaxGrad = 6;

	/**Default Value chosen by the constructors, if nothing specified.
	 * Rational Extrapolation doesn't work well with Vectors. 	 */
	final static public boolean rational = false;

	/**Default Value chosen by the constructors, if nothing specified	 */
	final static public int Power = 2;	//2 == (quadratic Convergence)

	/**Initializes the Stepper to new Values:
	 * Stepsize, Start-X, Start-Y and Function f	 */
	public void Init(IIntRing Step, IIntRing x_, IIntRing y_, IFunction f) { //, intDGL f_) {
		//Allocate Space to save iterative Allocation and Destruction!
		super.Init(Step, x_, y_, f);		//Create the Storage for the Helper Vectors:
		x1   = (IIntRing) x.newInstance();
		H    = (IIntRing) x.newInstance();
		xOld = (IIntRing) y.newInstance();
		yOld = (IIntRing) y.newInstance();
	}

	/** Empty Constructor with all necessary Parameters  */
	protected StepTrapezQ(){}

	/** Constructor with all necessary Parameters  */
	public StepTrapezQ(IIntRing Step, IIntRing x, IIntRing y, IFunction f) {
		super(Step, x, y, f);
//  	Init (Step, x, y, f);
	}

	/**Scaling Vector to adjust for different Sizes in some Dimensions	 */
	public IIntRing yScale;

	/**Extrapolator, used to estimate the real y Value	 */
	private ExtraPolValue xTra = new ExtraPolValue(MaxGrad, Power, rational);

	/**Old x Value, if the Step Size was too large	 */
	private IIntRing xOld;

	/**Old y Value, if the Step Size was too large	 */
	private IIntRing yOld;

	/**Holds the double Distance	 */
	private IIntRing H;

	/**Holds the End x Value	 */
	private IIntRing x1;

	/**Bulirsch-Stoer-Schritt mit Beobachtung des lokalen Abbruchfehlers
	 * zur Integration der Funktion f. 	 */
	public IIntRing step(IIntRing h) {
		numSteps = 0;
		h  = (IIntRing) h.copy();
		replace = false; super.step(h); derive  = false;
		numSteps = 1; x1.copyAt(x);
		IIntRing Target = (IIntRing) x3.copy();
		IIntRing tmp = x; x = x3; x3 = tmp;	//x mit x3 tauschen
		while (! xTra.addPoint(h, y2)) {
			tmp = yOld; yOld = y2; y2 = tmp;
			H.copyAt(h);
			h.halfAt(); x.subAt(h);
			super.step(H);
			numSteps <<= 1;
			y2.addAt(yOld); y2.halfAt(); }
		y.addAt(xTra.getValue());
		derive  = true; replace = true;
		numSteps = 0;
		xTra.reset(Power, rational);
		x = Target;	//tmp = x; x = x1; x1 = tmp;	//x mit x1 tauschen
		return y; } 	//The result is typically much more accurate.

	/**Method to test all Implementations in this class.	 */
	public static void testIt() { 	//Testing single Step of quality controlled Trapez Method
		TZ = new StepTrapezQ();	//use this Instance now to test the Stepper.
		System.out.println("Testing StepTrapezQ:");
		StepTrapez.testIt(); }

}
