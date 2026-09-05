package streamIO.copy.monoid.integer;

import streamIO.IIStreamIn;
import streamIO.copy.boole.ABoole;
import streamIO.copy.boole.Boole;
import streamIO.copy.group.IGroup;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.shift.ShiftAble;

/**Abstract Implementation of a Set, only valid for BitSets!
 * It delegates the Element setting to AND and OR Operations
 * on Masks denoting a single Element.
 *
 * Direct SubClasses:
 *		ASetInteger
 *		Boolean
 * Delegations:
 *		Binary
 *		BitVector
 *
 * Is a Delegator, because it is used by Binary and BitVector,
 * thus it must be concrete.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:33:39Z
 * digest: 541f8d8635402591f6abf146e079472d0e234ac9c4cd5a825707cddd80e0eb24
 * stale: false
 * tags: [code/bit_manipulation, code/delegation]
 * concepts: [Bit Set, Delegation Pattern]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 * ArrStruct cannot use it directly, because Bits are not defined there!	 */
public class ASetInteger
extends ABoole
implements SetInteger {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Constructor for initializing Delegation	 */
	public ASetInteger (SetInteger self_) { super(self_); }

	/**Clears the Entry n	*/
	// TODO: LOGIC: uses XOR (toggle) instead of AND-NOT to clear the bit - if bit n is already 0, this XOR flips it to 1, setting the bit instead of clearing it. Only correct when the caller already knows bit n is set.
	public void  clear(int n) {
		((Boole)self).XORat(((ShiftAble)((IGroupM)self).one()).aslAt(n));}

	/**Sets the Entry n		*/	public void    set(int n) {
		self. ORat(((ShiftAble)((IGroupM)self).one()).aslAt(n));}

	/**Gets the Entry n		*/	public boolean get(int n) {
		return !((IGroup) self.AND  (((ShiftAble)((IGroupM)self).one()).aslAt(n))).isZero();}

    /**Returns an Iterator of the components in this Container.
     *
     * @return  an Iterator of the components in this Container.
     * @see     Math.Iterator     */
    public IIStreamIn Iterator() { throw new AbstractMethodError(); }

}
