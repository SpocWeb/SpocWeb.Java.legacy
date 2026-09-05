package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.TestRing;

/**Manual test-suite entry point that runs this package's self-tests, then hands off to {@link TestRing}
  * to continue testing the parent Packages.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:08:30Z
  * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
  * stale: false
  * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
  * concepts: [Metric Spaces - Root Finding and Numerical Integration]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class TestMetric {

	/**The main entry point for the application.
	 * @param args Array of command line parameters passed to the application
	 * After testing this Package, calling the Parent Packages.	 */
	public static void main (String[] args) throws Exception {
		//concrete Testing Object can be defined now! Using BodyDouble!
		System.out.println("Testing Package Metric");
//  	ARefinerQ.testIt();
		GoldenMinimizer.testIt();
		BiSectRefinerQ.testIt();
		ExtraPolValue.testIt();
		MultiStepYQ.testIt();
		MultiStepY.testIt();
		MultiStep.testIt();
		NewtonRefinerQ.testIt();
		PegasusRefiner.testIt();
		FalsiRefinerQ.testIt();
		StepMPQ.testIt();
		StepRKF.testIt();
		StepRKFQ.testIt();
		StepRKQ.testIt();		//Integration of ODEs
		StepTrapezQ.testIt();	//Integration of Functions
		AMetricIRing.testIt();
		AMetric.testIt();
		ANorm.testIt();
		AScalarMetric.testIt();
		//AWellOrder.testIt();

		TestRing.main(args);
	}

}
