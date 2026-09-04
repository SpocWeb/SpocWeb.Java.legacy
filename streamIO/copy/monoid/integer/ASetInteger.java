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

	/**Clears the Entry n	*/	public void  clear(int n) {
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
