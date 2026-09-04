package tester;

/** This is a Helper ITester Implementation to find an Object that is equivalent to a given one
  * according to a certain Equivalence Relation.
  * Later also order Relations will be used.
  * It is used e.g. in Container	*/
final public class TesterEquivalence
extends TesterEquals
implements ITester {

	/** Equivalence Relation to use to compare the Item to.	 */
	private final IEquivalence equality;

	/**Constructor, takes the Item that will be searched for.	 */
	public TesterEquivalence(final Object _itemToFind, final IEquivalence _equal){
		super(_itemToFind);
		this.equality = _equal;  }

	/**Test Method to determine, whether the item is found	 */
	public boolean test(final Object arg){
		return (searchItem == arg) ||   //faster Check!
				equality.equals(arg, searchItem); }

	//TODO: Add tests for order Relations for e.g. binary Search.
}
