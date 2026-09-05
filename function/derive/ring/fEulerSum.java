package function.derive.ring;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.metric.IMetricIRing;
import function.AFunction;

/** Ruler Sum Function: N -> R
  * Series with a Sum, that converges to e.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: 0840d965e784a132bf3c3845753f1cf9f2a90556b3c2f2ecb70bc17ccf87f506
  * stale: false
  * tags: [code/mathematical_function]
  * concepts: [Numerical Series]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  * e = Sum (n, 1/n!) 	 */
public class fEulerSum
extends AFunction {

	/**Helper Variable, needed to work, when arg is not of Type IIntRing.	 */
	public IMetricIRing Helper;

	/**Returns the Elements of a Series whose Sum converges to e	 */
	public Object Map(Object arg) {
//		int n = ((ICountAble) arg).getInt();
		IMetricIRing arg_;
		if (arg instanceof IMetricIRing)
			arg_ = (IMetricIRing) ((ICopyAble)arg).copy();
		else {
			Helper.copyAt(arg);
			arg_ = Helper;
		}
		return arg_.Fact().invAt(); }	//define the Factorial already in IIntRing

}
