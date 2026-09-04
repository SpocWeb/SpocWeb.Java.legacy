package function.derive.ring;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.metric.IMetricIRing;
import function.AFunction;

/** Ruler Sum Function: N -> R
  * Series with a Sum, that converges to e.
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
