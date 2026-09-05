package tester.stateful;

import tester.ITester;

/**This is a Helper ITester Class to find an Object at a given Position, starting at 0.
 * It returns true #Position times, as determined in the Constructor.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:33Z
 * digest: 40192c10850fb7887e7b8226e795cd20dda8c3c4b27310c7927030b863a21df1
 * stale: false
 * tags: [code/stateful_algorithm]
 * concepts: [Position-Aware Tester]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * It is used e.g. in Container	*/
final public class TesterPosition
implements ITester {

	/**Local Copy of Item to find.	 */
	private int position;

	/**Constructor, takes the Item that will be searched for.	 */
	public TesterPosition(final int _Position) { this.position = _Position+1; }

	/**Test Method to determine, whether the item is found	 */
	public boolean test(final Object arg) { return (--position == 0); }

}
