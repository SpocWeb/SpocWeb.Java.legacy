package streamIO.copy.group;

import streamIO.copy.TestCopy;

/**Tests all classes in the Package Group	 */
public class TestGroup {

	/**The main entry point for the application.
	 * @param args Array of command line parameters passed to the application.
	 * Here I can test the static Methods of the Classes
	 * and call the testIt() Methods.
	 * The main() Methods of the Super Packages are also called.
	 * As an alternative to having the Classes call the static methods
	 * of their super classes.	 */
	public static void main (String[] args) throws Exception {
		System.out.println("Testing Package Group");
		//AGroup.testIt();
		//ASemiGroup.testIt();
		TestCopy.main(args);
	}

}
