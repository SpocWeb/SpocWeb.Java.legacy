package streamIO.copy.groupM;

import streamIO.copy.TestCopy;

/**Manual test harness entry point for the {@code groupM} package, delegating to
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:32:09Z
 * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
 * stale: false
 * tags: [code/manual_test_harness]
 * concepts: [Algebraic Group]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * {@link TestCopy#main(String[])}. */
public class TestGroupM {

	/**The main entry point for the application.
	 * @param args Array of command line parameters passed to the application.
	 * Here I can test the static Methods of the Classes
	 * and call the testIt() Methods.	 */
	public static void main (String[] args) throws Exception {
		System.out.println("Testing Package Group");
//		AGroupM.testIt();
//		ASemiGroupM.testIt();
		TestCopy.main(args);
	}

}
