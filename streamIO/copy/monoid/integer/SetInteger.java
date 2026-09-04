package streamIO.copy.monoid.integer;

import streamIO.IIterAble;
import streamIO.copy.boole.Boole;

/**Set consisting of Bits. It has the same Functionality as java.util.BitSet
 * It has the same Methods as Container.Set, only for integer Arguments! */
public interface SetInteger
extends Boole, IIterAble {

	/**Clears the Entry n	 */
	public void  clear(int n);

	/**Sets the Entry n	 */
	public void    set(int n);

	/**Gets the Entry n	 */
	public boolean get(int n);

}
