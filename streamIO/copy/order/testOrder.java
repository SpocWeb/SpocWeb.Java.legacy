package streamIO.copy.order;

/**Manual test harness that exercises {@link AOrder}'s comparison and Max/Min Methods via
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:30:29Z
 * digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
 * stale: false
 * tags: [code/manual_test_harness]
 * concepts: [Order Relation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * {@link AOrder#testIt()}. */
public class testOrder {

	/**The main entry point for the application.
	 * @param args Array of command line parameters passed to the application
	 * After testing this Package, calling the Parent Packages.	 */
	public static void main (String[] args) throws java.io.IOException {
		//concrete Testing Object can be defined now! Using BodyDouble!
		System.out.println("Testing Package Order");
		AOrder.testIt();
	}
}
