package streamIO.copy.group.ring.metric.body.complex;

import streamIO.copy.group.ring.metric.body.TestBody;

public class TestComplex {

	/**The main entry point for the application.
	 * @param args Array of command line parameters passed to the application
	 * After testing this Package, calling the Parent Packages.	 */
	public static void main (String[] args) throws Exception {
		Fourier.testIt();
		Complex.testIt();
//		Polar.testIt();
		FourierFuncs.testIt();
		TestBody.main(args);
	}

}
