package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.ARefiner;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IRefiner;

/**Multiple Zero Iteration with the given Refiner
 * Same Restrictions for f as on the Refiner Class.
 * Additionally tests for minimum absolute Value of yl!
 * Performs as many Steps as necessary.	*/
public class MultiStepY
extends MultiStep
implements IRefiner {

	/**Maximum Function Value before Iteration stops	 */
	public IMetricIRing MaxYDiff;

	public void Init(ARefiner Refiner) {
		//necessary, ZeroStep() calculates dx automatically
		super.Init(Refiner);
		MaxYDiff =  ((IMetricIRing)((ARefinerQ)Refiner).dy).mulAbsAccuracy();
	}

	/**Initializing Constructor	 */
	public MultiStepY(ARefiner Refiner)	{super(Refiner);}

	/**Empty Constructor	 */
	public MultiStepY()	{super();}

	/**Performs multiple approximating Steps	 */
	public IIntRing refine() {
		//I could also have re-used the super.ZeroStep(), but that would generate more call overhead!
		while ( (--MaxIter > 0) &&
				(MaxYDiff.isLessThan(((IMetricIRing)Refiner.yl).AbsV())) ||
				(MaxXDiff.isLessThan(((IMetricIRing)Refiner.dx).AbsV())))
			Refiner.refine();
		return Refiner.xl; }

	/**Method to test all Implementations in this class.	 */
	public static void testIt() {
		System.out.println("Testing MultiStepY:");
		MS = new MultiStepY();
		testMultiStep();
	}

}
