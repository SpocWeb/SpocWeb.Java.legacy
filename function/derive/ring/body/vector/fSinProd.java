package function.derive.ring.body.vector;

import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.group.ring.metric.body.vector.Tensor;
import streamIO.copy.groupM.IGroupM;
import function.AFunction;
import function.IMeasurAble;

/**Returns the Product of the Sinusses of all Coordinates times Pi,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:43:49Z
 * digest: bbf25575f8194e5e23bbdbb3f5363a1ff6f5b1a03f6d8a05637bc224e940d627
 * stale: false
 * tags: [code/mathematical_function, code/vector_math]
 * concepts: [Vector Calculus, Trigonometric Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * i.e. the full Period fits into the unit Circle. (Example Function)  */
public class fSinProd
extends AFunction {

	/**Returns the Product of Sin(Pi * Coordinate) over every Coordinate of the given Tensor.	 */
	public Object Map(Object arg) {
		Tensor V = (Tensor) arg;
		int Length = V.getDim();
		int i = 0;
		IGroupM Prod =  ((MetricBody) V.a[0].mul(IMeasurAble.pi)).sin();
		while (++i < Length)
			Prod.mulAt(((MetricBody) V.a[i].mul(IMeasurAble.pi)).sin());
		return  Prod; }

}
