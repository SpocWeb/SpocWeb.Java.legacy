package streamIO.copy.shift;

import streamIO.copy.ICopyAble;

/**This Interface can be implemented by all random Access Iterators
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 8d3202c1351c5b831993a9125c695f22dc3ad192061f7396881fe18d8d59ac3d
 * stale: false
 * tags: [code/in_place_operation]
 * concepts: [Random-Access Iteration]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
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
