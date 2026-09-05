package streamIO.copy.group.ring.metric;

import streamIO.copy.CCopyAble;

/**Implements Constants for all Types of WellOrder Classes.
 * This Class inhibits the Use of ...At() Routines
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 29890469122e171ddf801c1d6050f5a475e1dc6f7dfa41872171f33af378f783
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * but still supports all other Methods of the WellOrder Class.	 */
public class CMetric
extends CCopyAble
implements IMetric {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor	 */	public CMetric(IMetric cnst){super(cnst);}

	//////////////////////////
	//	interface Metric	//
	//////////////////////////

	/**This Distance Function defines a Metric on the Elements of IMetric Type.	 */
	public IMetricIRing Dist(Object arg)	{return ((IMetricIRing) inner).Dist(arg);}

	/**p-Metric: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Dist (Object arg, double p)	{return ((IMetricIRing) inner).p_Dist(arg, p);}

	/**Absolute Value-Metric:
	 * Special Case of the p-Metric for p = 1	 */
	public IMetricIRing AbsV_Dist (Object arg)	{return ((IMetricIRing) inner).AbsV_Dist(arg);}

	/**Maximums-Metric
	 * Special Case of the p-Metric for p -> Infinity	 */
	public IMetricIRing Max_Dist (Object arg)	{return ((IMetricIRing) inner).Max_Dist(arg);}

	/**(Euklidische Metric)^2
	 * Special Case of the p-Metric for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrDist(Object arg)	{return ((IMetricIRing) inner).SqrDist(arg);}

}
