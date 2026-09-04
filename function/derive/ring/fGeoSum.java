package function.derive.ring;

import streamIO.copy.group.ring.IIntRing;
import function.AFunction;
import function.ICountAble;

/** Geometric Sum Function: N -> R
  * Series with a Sum, that converges to 1/(1-Radix) (starting from 0)
  * resp. to Radix/(1-Radix) (starting from 1) for Radix < 1	 */
public class fGeoSum
extends AFunction {

	/**Radix for the Series.
	 * Must be |Radix| < 1	 */
	public IIntRing Radix;

	/**Initializing Constructor	 */
	public fGeoSum(IIntRing Radix) { this.Radix = Radix; }

	/**Returns the Elements of the geometric Series.	 */
	public Object Map(Object arg) { return Radix.Pow(((ICountAble) arg).getInt()); }

}
