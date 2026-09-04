package tester.stateful;

import tester.ITester;

/**This is a Helper ITester Class to find an Object at a given Position, starting at 0.
 * It returns true #Position times, as determined in the Constructor.
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
