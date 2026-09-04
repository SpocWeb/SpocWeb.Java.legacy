package function.byref.combinatoric;

/**This class can take a variable number of parameters on the command
 * line. Program execution begins with the main() method. The class
 * constructor is not invoked unless an object of type 'Class1'
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
