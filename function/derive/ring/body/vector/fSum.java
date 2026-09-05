package function.derive.ring.body.vector;

import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.metric.body.vector.Tensor;
import function.AFunction;

/**This Class implements a Function that assumes arg to be a Tensor
 * and returns the Sum of all Coordinates.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:43:52Z
 * digest: 41dace332e9f448f358b85c6c01d83586c094e6cb4fe7f69fa8dace3d31d13a9
 * stale: false
 * tags: [code/mathematical_function, code/vector_math]
 * concepts: [Vector Calculus]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * See also fProduct, which returns the Product of all Coordinates. */
public class fSum
	extends AFunction {

	/**Returns the Sum of all Coordinates of the given Tensor.	 */
	// TODO: LOGIC: 'Sum' is initialized as a copy of V.a[0], but the loop below then runs i from
	// Dim-1 down to 0 inclusive and adds V.getAt(0) again, so Coordinate 0 is counted twice in the
	// Result for every Tensor of dimension >= 1.
	public Object Map(final Object arg) {
		final Tensor V = (Tensor) arg;
		int i = V.getDim();
		final IGroup Sum = (IGroup) V.a[0].copy();
		for (; --i >= 0;)
			Sum.addAt(V.getAt(i)); // .a[i]); }
		return Sum;
	}
}
