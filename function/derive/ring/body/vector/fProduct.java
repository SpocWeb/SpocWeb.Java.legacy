package function.derive.ring.body.vector;

import streamIO.copy.group.ring.metric.body.vector.Tensor;
import streamIO.copy.groupM.IGroupM;
import function.AFunction;

/**This Class implements a Function that assumes arg to be a Vector (Tensor)
 * and returns the Product of all Coordinates.
 * See also fSum, which returns the Sum of all Coordinates.
 */
public class fProduct
extends AFunction {

	/**This Function encapsulates the Product Function.	 */
	public Object Map(final Object arg) {
		final Tensor V = (Tensor) arg;
		int i = V.getDim();
		final IGroupM Prod = (IGroupM) V.a[--i].copy();
		for (; --i >= 0;) 
			Prod.mulAt(V.getAt(i)); // .a[i]); }
		return  Prod; }

}