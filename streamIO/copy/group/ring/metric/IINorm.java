package streamIO.copy.group.ring.metric;

/**Normed Space (X,||: X -> R)
 *
 * VectorSpace X is called Normed Space with Norm || when the following is true:
 * 1) |x| > 0 when x!=0   : d is positive definite (otherwise Pseudo-Norm)
 * 3) |k*x|==|k|*|x|      : d is homogenic
 * 4) |x+y|<=|x|+|y|      : Triangle-Inequation
 *
 * You can define a Metric d and Topology t by defining d(x,y)=|x-y|.
 * A scalar Product defines a canonic Norm on X with |x|:=<x,x>.
 *
 * A strict connex Order on a Group X, that flips with the Sign defines a Norm:
 * the absolute Value |x| = +x f�r x>=0, -x fur x < 0
 * This fulfills the Triangle InEquation (Proof: Look at each of the 4 Cases)
 * and is homogenic, so it is a Norm.
 *
 * An Order Relation and a Group allow for a standard Definition
 * of the Absolute Value
 * Of course you can also define the Absolute Value as sqrt(sqr(x)),
 * if you have a multiplicative Group.
 *
 * Design Decisions:
 * Chose 'MetricIRing' as Return Type. This may save some conversions.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 32c881ca953669c36e8065533acbcdd9f7266594fe21547aae92831f7d731478
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IINorm {

	/**Returns the Norm of the Object: ||x||	 */
	public IMetricIRing Norm();
}
