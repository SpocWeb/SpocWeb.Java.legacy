package streamIO.copy.group.ring.metric.body.complex;

import streamIO.copy.group.ring.metric.body.TestBody;

/**Manual test-suite entry point that runs this package's self-tests in dependency order,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:28:04Z
 * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
 * stale: false
 * tags: [code/complex_numbers, code/fourier_transform]
 * concepts: [Complex Number Arithmetic and Fourier Transform]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * then hands off to {@link TestBody} to continue testing the parent packages.	 */
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
