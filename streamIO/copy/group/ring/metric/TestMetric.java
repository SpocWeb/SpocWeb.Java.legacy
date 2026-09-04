package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.TestRing;

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
