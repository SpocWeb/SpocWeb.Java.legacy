package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.IIntRing;

/**Multiple Zero Iteration with the given Refiner
 * Same Restrictions for f as on the Refiner Class.
 * Performs as many Steps as necessary to determine x0 with Accuracy.
 * The Check is slightly faster, because yr is expected to stay positive.
 * Therefore the Refiner is of Type 'ARefinerQ',
 * which already sorts Input Bounds as opposed to 'ARefiner'.	*/
public class MultiStepYQ
extends  MultiStepY {

	/**Initializing Constructor	 */
	public MultiStepYQ(ARefinerQ Refiner) { super(Refiner); }

	/**Empty Constructor	 */
	public MultiStepYQ() { super(); }

	/**Performs multiple approximating Steps	 */
	public IIntRing refine() {
		while ( (--MaxIter > 0) &&
				((MaxYDiff.isLessThan(((ARefinerQ)Refiner).dy)) ||	//yr is positive
				 (MaxXDiff.isLessThan(((IMetricIRing)Refiner .dx).AbsV()))))
			Refiner.refine();
		return Refiner.xl;
	}

	/**Method to test all Implementations in this class.	 */
	public static void testIt() {
		System.out.println("Testing MultiStepYQ:");
		MS = new MultiStepYQ();
		testMultiStep();
	}

}
