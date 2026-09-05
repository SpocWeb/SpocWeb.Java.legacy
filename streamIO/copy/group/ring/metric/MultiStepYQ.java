package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.IIntRing;

/**Multiple Zero Iteration with the given Refiner
 * Same Restrictions for f as on the Refiner Class.
 * Performs as many Steps as necessary to determine x0 with Accuracy.
 * The Check is slightly faster, because yr is expected to stay positive.
 * Therefore the Refiner is of Type 'ARefinerQ',
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 4bc09dd159a4761b565713b1a0b0aefe70f12572f52ecb286d6fb5e76c6df1f8
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
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
