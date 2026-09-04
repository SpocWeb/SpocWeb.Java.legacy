package streamIO.copy.group.ring;

import streamIO.copy.group.TestGroup;
import streamIO.copy.groupM.TestGroupM;

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
