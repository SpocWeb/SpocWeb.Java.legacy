package streamIO.copy.group.ring;

import streamIO.copy.group.TestGroup;
import streamIO.copy.groupM.TestGroupM;

/**Manual test-suite entry point that runs this package's self-tests, then hands off to {@link TestGroup}/
  * {@link TestGroupM} to continue testing the parent Packages.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:14:05Z
  * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
  * stale: false
  * tags: [code/ring_theory, code/ode_solver]
  * concepts: [Ring Algebra and ODE Solvers]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class TestRing {

	/**The main entry point for the application.
	 * @param args Array of command line parameters passed to the application	 */
	public static void main (String[] args) throws Exception {
		System.out.println("Testing Package Ring");
		Interpolator.testIt(null);
		Extrapolator.testIt();
//		AStepper.testIt();	//only abstract, tested in StepMP and StepRK
		StepRK.testIt();
		StepMP.testIt();
		StepTrapez.testIt();
//		ARefiner.testIt();	//Refiner tested in the next Classes
		SecantRefiner.testIt();
		FixPtRefiner.testIt();
		NewtonRefiner2.testIt();
		NewtonRefiner.testIt();
		ARing.main(args);
		AIntRing.main(args);
//		AInteger.testIt();	//not used for Delegation (yet)
		TestGroup .main(args);
		TestGroupM.main(args);
	}
}
