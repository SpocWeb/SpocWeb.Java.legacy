package streamIO.copy.group.ring.metric;

import streamIO.copy.ACopyAble;
import streamIO.copy.group.ring.ARefiner;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IRefiner;

/**Multiple Iterations with the given Refiner
 * Same Restrictions for f as on the Refiner Class.
 * Performs as many Steps as necessary to reduce the dx Difference.
 * If you also want to bound the yl Value, use MultiStepY.
 * Design Decisions:
 * The fixed number of Iterations controlled by MaxIter has NOT been separated out,
 * because that would create too much overhead and can be realized in a simple loop.	*/
public class MultiStep
implements IRefiner {

	/**Local Reference to the actual Refiner Class	 */
	protected ARefiner Refiner;

	/**Maximum Number of Iterations	 */
	public int MaxIter;

	/**Maximum Step Width before Iteration stops	 */
	public IMetricIRing MaxXDiff;

	/**Initializes the Refiner 	 */
	public void Init(ARefiner Refiner) {
		Refiner.refine();	//necessary, ARefiner() does not calculate dx automatically on creation
		this.Refiner = Refiner;
		MaxXDiff =  ((IMetricIRing)Refiner.dx).mulAbsAccuracy();
		MaxIter  = AMetricIRing.MaxIteration;
	}

	/**Empty Constructor	 */
	public MultiStep(){}

	/**Initializing Constructor	 */
	public MultiStep(ARefiner Refiner)
	{Init(Refiner);}

	/**Performs multiple approximating Steps	 */
	public IIntRing refine()
	{
		while ( (--MaxIter > 0) && (MaxXDiff.isLessThan(((IMetricIRing)Refiner.dx).AbsV())))
			Refiner.refine();
		return Refiner.xl;
	}

	/**Instance for inherited testIt() Method	 */
	protected static MultiStep MS;

	/**Method to test all Implementations in this class.	 */
	public static void testIt() {
		System.out.println("Testing MultiStep:");
		MS = new MultiStep();
		testMultiStep();
	}

	/**Method to test all Implementations in this class.	 */
	protected static void testMultiStep() {	//RingFuncs only used for testing!
		System.out.println("Testing with NewtonRefiner:");
		System.out.println("Searching for the Root of y = 1-x^2/2, y' = -x ");
		IMetricIRing xl = (IMetricIRing) ACopyAble.testInstance.copy();
		IMetricIRing xr = (IMetricIRing) ACopyAble.testInstance.copy();
		xl.copyAt(new Double(+3));
		xr.copyAt(new Double(+0.2));	//Use a Refiner derived from ARefinerQ to exploit the Positivity of yr!
/*		NewtonRefinerQ NS = new NewtonRefinerQ(xl, xr, RingFuncs.Algebra.f1_xx, //Search the Zero for
					 							 RingFuncs.Neg.Neg);//y = x^2, y' = 2x;
		MS.Init(NS);	//Code commented out to reduce Dependency between Metric and RingFuncs
		MS.refine();
		System.out.println("	xl=" + NS.xl + "	yl=" + NS.yl +
						   "	xr=" + NS.xr + "	yr=" + NS.yr);
*/	}

}
