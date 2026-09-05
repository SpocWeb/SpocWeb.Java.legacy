package streamIO.copy.group;

import streamIO.copy.TestCopy;

/**Tests all classes in the Package Group
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
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
