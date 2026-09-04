package tester;

/**This is a Helper ITester Class to find an Object that equals a given one.
 * Later also order Relations will be used.
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
