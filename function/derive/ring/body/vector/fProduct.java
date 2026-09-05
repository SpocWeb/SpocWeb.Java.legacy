package function.derive.ring.body.vector;

import streamIO.copy.group.ring.metric.body.vector.Tensor;
import streamIO.copy.groupM.IGroupM;
import function.AFunction;

/**This Class implements a Function that assumes arg to be a Vector (Tensor)
 * and returns the Product of all Coordinates.
 * See also fSum, which returns the Sum of all Coordinates.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 4baa9f6361d1276ff0e828d0c2a9e2238af484e97a1851994232993201f43fef
 * stale: false
 * tags: [code/mathematical_function, code/vector_math]
 * concepts: [Vector Calculus]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
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