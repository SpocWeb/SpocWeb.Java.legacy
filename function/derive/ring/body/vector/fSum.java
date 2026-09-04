package function.derive.ring.body.vector;

import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.metric.body.vector.Tensor;
import function.AFunction;

/**This Class implements a Function that assumes arg to be a Tensor
 * and returns the Sum of all Coordinates.
 * See also fProduct, which returns the Product of all Coordinates. */
public class fSum
	extends AFunction {

	public Object Map(final Object arg) {
		final Tensor V = (Tensor) arg;
		int i = V.getDim();
		final IGroup Sum = (IGroup) V.a[0].copy();
		for (; --i >= 0;) 
			Sum.addAt(V.getAt(i)); // .a[i]); }
		return Sum;
	}
}
