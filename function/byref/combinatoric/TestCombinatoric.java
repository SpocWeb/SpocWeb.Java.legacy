package function.byref.combinatoric;

/**This class can take a variable number of parameters on the command
 * line. Program execution begins with the main() method. The class
 * constructor is not invoked unless an object of type 'Class1'
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
 * stale: false
 * tags: [code/combinatorics, code/special_function]
 * concepts: [Combinatorics]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * created in the main() method. */
public class TestCombinatoric {

	/**The main entry point for the application.
	 * Tests all Classes of this Package.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		ProbFuncs .testIt();
		BesselFuncs.testIt();
		CombiFuncs.testIt();
	}

}
