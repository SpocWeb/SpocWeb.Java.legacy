package streamIO.copy.group.ring.metric;

/**Implements Constants for all Types of IIntRing Classes.
 * This Class inhibits the Use of ...At() Routines
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 8c050ce6eae9d9b24814a16fa3a4ebc60a26b6c61159920769af0aa4ff9c71ce
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * but still supports all other Methods of the IIntRing Class.	 */
public class CNorm
extends CMetric
implements INorm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor	 */	public CNorm(INorm cnst){super(cnst);}

	//////////////////////
	//	interface Norm	//
	//////////////////////

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Norm (double p)	{return ((INorm) inner).p_Norm(p);}

	/**Betrags-Norm:
	 * Special Case of the p-Norm for p = 1
	 * This norm is the fastest to chalculate	 */
	public IMetricIRing AbsV_Norm ()	{return ((INorm) inner).AbsV_Norm();}

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 */
	public IMetricIRing Max_Norm ()	{return ((INorm) inner).Max_Norm();}

	/**Euklidische Norm
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing Norm   ()	{return ((INorm) inner).Norm();}

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm()	{return ((INorm) inner).SqrNorm();}

}
