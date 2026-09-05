package streamIO.copy.group.ring.metric;

import streamIO.copy.ICopyAble;

/**Defines a Metric on a Set: d:X*X -> R with the following Properties:
 * 1) x != y => d(x,y) > 0      (positive definite, not for Graphs)
 * 2) d(x,x) == 0               (not for Graphs!)
 * 3) d(x,y) == d(y,x)          (symmetric, not for Graphs)
 * 4) d(x,z) <= d(x,y) + d(y,z) (Triangle Inequation, not for Graphs)
 *
 * You can define an absolute Value by |x| = d(x,0)
 *
 * A Metric defines a certain Kind of Topology on the Set.
 * A Norm || defines a Metric on a Group: d(x,y):= |x-y|
 * A Scalar Product <> defines a Norm on a Vector Space: |x| = <x,x>^.5
 *
 * More generic Topologies can be defined using Graphs:
 * not symmetric
 * not positive definite
 * possibly violating the Triangle Inequation
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 4a14b852017719f3e36e4e3a23db958e0ce4ff33847fa4ba64a8b9ee6e2a5088
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IMetric
extends IIMetric, ICopyAble {

	/**
	 * p-Metric: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases of this one.
	 *
	 * The p_Dist also defines all Types of "Average": Avg(x) = |x|/N
	 * the   Maximum 'Average' corresponds to the Max Norm: p==Infinity
	 * the  quadratic Average  corresponds to the Sqr Norm: p==2
	 * the arithmetic Average  corresponds to the Abs Norm: p==1
	 * the   harmonic Average  corresponds to p==-1 (no Norm: Triangle Inequation violated!)
	 *
	 * In 1-dimensional Spaces all Norms fall together.
	 */
	public IMetricIRing p_Dist (Object arg, double p);

	/**Absolute Value-Metric:
	 * Special Case of the p-Metric for p = 1	 */
	public IMetricIRing AbsV_Dist (Object arg);

	/**Maximums-Metric
	 * Special Case of the p-Metric for p -> Infinity	 */
	public IMetricIRing Max_Dist (Object arg);

	/**Euklidische Metric
	 * Special Case of the p-Metric for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing Dist   (Object arg);

	/**(Euklidische Metric)^2
	 * Special Case of the p-Metric for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrDist(Object arg);

}
