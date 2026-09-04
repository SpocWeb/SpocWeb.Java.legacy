package streamIO.copy.group.ring.metric;

/**Defines a Norm on a Set: ||:X -> R with the following Properties:
 * 1) x != 0 => |x| > 0		(positive definite)
 * 2) |k*x| == |k|*|x|		(homogenic)
 * 3) |x+z| <= |x| + |z|	(Triangle Inequation)
 *
 * A Metric defines a Topology on the Set.
 * A Norm || defines a Metric on a Group: d(x,y):= |x-y| and |x| = d(0,x)
 * 				where normally it is required that |a*x| = |a| * |x|
 * A Scalar Product <> defines a Norm on a Vector Space: |x| = <x,x>^.5
 *
 * The Maximum Norm is especially useful, because
 * - it is very easy to calculate for Matrices, Vectors and real Numbers.
 * - it is the Row-Sum Norm of a Matrix,
 * - which again is the adjunct Norm for the Max-Norm of Vectors
 *
 * But is can be shown that all Norms create identical Topologies in all R^n Spaces!
 * In 1-dimensionas Spaces all these Norms are the same.
 */
public interface INorm
extends IINorm, IMetric {

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Norm (double p);

	/**Betrags-Norm:
	 * Special Case of the p-Norm for p = 1
	 * This norm is the fastest to chalculate	 */
	public IMetricIRing AbsV_Norm ();

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 */
	public IMetricIRing Max_Norm ();

	/**Euklidische Norm
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing Norm   ();

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm();

}
