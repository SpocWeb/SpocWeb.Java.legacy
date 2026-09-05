package function.derive.ring;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.IIntRing;
import function.AFunction;
import function.ICountAble;

/** Euler Function N -> R
  * Integer Function with a Limes, that converges to e
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: 3cbaa70f8b2a591b8316d39f8d17c58c47d9b79e6138aee56e3aae638b0d2681
  * stale: false
  * tags: [code/mathematical_function, code/exponential_function]
  * concepts: [Numerical Series]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
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
