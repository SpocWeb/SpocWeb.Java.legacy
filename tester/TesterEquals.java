package tester;

/**This is a Helper ITester Class to find an Object that equals a given one.
 * Later also order Relations will be used.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:33Z
 * digest: 40192c10850fb7887e7b8226e795cd20dda8c3c4b27310c7927030b863a21df1
 * stale: false
 * tags: [code/predicate_logic]
 * concepts: [Equality Tester]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * It is used e.g. in Container	*/
public class TesterEquals
implements ITester {

	/**Local Copy of Item to find.	 */
	protected final Object searchItem;

	/**Constructor, takes the Item that will be searched for.	 */
	public TesterEquals(final Object _itemToFind){ this.searchItem = _itemToFind; }

	/**Test Method to determine, whether the item is found	 */
	public boolean test(final Object arg){
        return (searchItem == arg) ||   //faster Check!
                searchItem.equals(arg); }

	//TODO: Add tests for order Relations for e.g. binary Search.
}
