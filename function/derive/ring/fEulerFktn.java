package function.derive.ring;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.IIntRing;
import function.AFunction;
import function.ICountAble;

/** Euler Function N -> R
  * Integer Function with a Limes, that converges to e
  * Used for testing the Limes() Function   */
public class fEulerFktn
extends	AFunction {

	/**Helper Variable, needed to work, when arg is not of Type IIntRing.	 */
	public IIntRing Helper;

	/** (1+1/n)^n  */
	public Object Map(Object arg) {
		int n = ((ICountAble) arg).getInt();
		IIntRing arg_;
		if (arg instanceof IIntRing)
			arg_ = (IIntRing) ((ICopyAble)arg).copy();
		else {
			Helper.copyAt(arg);
			arg_ = Helper; }
		return ((IIntRing)
			   ((IIntRing) arg_.invAt()).inc()).PowAt(n); } // (1+1/n)^n

}
