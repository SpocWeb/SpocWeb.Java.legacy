package streamIO.copy.monoid.integer;

import streamIO.IIterAble;
import streamIO.copy.boole.Boole;

/**Set consisting of Bits. It has the same Functionality as java.util.BitSet
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 1dfc44d302eb744b9b2bea106c7f581d535530605abf6987af21db5ba08e37e0
 * stale: false
 * tags: [code/bit_manipulation]
 * concepts: [Bit Set]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
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
