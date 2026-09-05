package streamIO.object.enumer.container;

import function.IIOrderAble;

/**This Class is used for hunting Entries down in large sorted Arrays.
 * It assumes that the positions of consecutive searched Entries are near.
 * It first widens the scope of the Search until the item is included
 * and then uses "findInRange" to find it.
 * Another Optimization is to reuse the previous Increment.
 *
 * It is similar to an Iterator,
 * except for the Fact that you supply an Object to search for.
 * <!-- docstate
 * tags: [code/container, code/hash_table, code/container_iteration]
 * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class Hunter {

	/**Index at which the previous Item was found	 */
	private int lastPosition;

	/**Local Reference to the sorted Array being searched.	 */
	private SortedArray mArray;

	/**Constructor to hand over the Array to be searched.
	 * Determines the Sort Order	*/
	public Hunter(final SortedArray array) {	//don't compare a[0] with a[1], they may be the same.
		mArray = array;
		mArray.ascending = (((IIOrderAble)mArray.items[0]).isLessThan(mArray.items[mArray.getInt()-1])); }

	/** resets the Hunter so it considers the whole Interval 	*/
	public void reset() { lastPosition = -1; }

	/** gives the Hunter the Hint to search at the given Position	*/
	public void hint(final int position) { lastPosition = position; }

	/**Hunts the next Item down based on the last find.	 */
	public int huntInRange(IIOrderAble Item) {
		int inc;
		int upper;

		//last time the item was not found => complete new BiSection, no Hunting
		if (lastPosition < 0 || lastPosition >= mArray.getInt()) { lastPosition = 0; upper = mArray.getInt(); }
		else { //extend the Scope until the Element is bracketed.
			inc = 1; //start with Step Size 1
			if (Item.isLessThan(mArray.items[lastPosition]) != mArray.ascending) {
				if (lastPosition == mArray.getInt()-1) return lastPosition;
				upper=lastPosition+1;
				while (Item.isLessThan(mArray.items[upper]) != mArray.ascending) {
					lastPosition = upper; upper += (inc += inc); 	//double the increment
					if (upper >= mArray.getInt()) { upper = mArray.getInt(); break; }
				} //exceeded the End...
			} else { //TODO: I don't understand this part...
				if (lastPosition == 0) { lastPosition = -1; return lastPosition; }
				upper = lastPosition--;
				while (Item.isLessThan(mArray.items[lastPosition]) == mArray.ascending) {
					upper = lastPosition; inc <<= 1;	//double the increment
					if (inc >= upper) { lastPosition = 0; break; }
					else lastPosition = upper-inc;
				}
			}
		}

		//use BiSection Algorithm for the Rest
		return mArray.firstIndexOf(Item, lastPosition, upper); 
	}

	/**Tests all Methods of this Class		 */
	public void testIt() {
	}

}
