package streamIO.copy.group.ring;

import streamIO.copy.ICopyAble;

/**Defines the Function pred() and succ(), usually for Integer Types
 * as a complement to inc() and dec() in IInteger.
 * Used when a 1 Element is added to an additive Group, e.g. in the Integrity Ring
 * usually implemented together with the countable Interface.
 * Also used for sequential Access of Data Structures.
 *
 * A Default Implementation is done in 'absInteger'.
 */
public interface integer
extends IInteger, ICopyAble {

	/**Returning Zero: 0	 */	public integer Zero();
	/**Successor: x+1	 */	public integer succ();
	/**Predecessor: x-1	 */	public integer pred();
	/**Residual: 1-x	 */	public integer Resid();
	/**Residual in Place: 1-x	 */	public integer ResidAt();

}
