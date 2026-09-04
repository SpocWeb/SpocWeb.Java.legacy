package streamIO.copy.shift;

import streamIO.copy.ICopyAble;

/**This Interface can be implemented by all random Access Iterators */
public interface SwapAble
extends ICopyAble {

	/**Swaps the i-th and j-th Item in Place	 */
	public SwapAble swapAt(int i, int j);

	/**Swaps the i-th and j-th Item and returns a copy 	 */
	public SwapAble swap  (int i, int j);

	/**Returns the Number of Items in the Container	 */
//	public int getInt();

	/**Returns the maximum Index in the Container == Number of Items -1	 */
	public int getDim();

	/**Returns true, when the Items in the Container are ordered
	 * from the i-th Item on	 */
	public boolean ordered(int i);

}
