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
 * This Implementation assumes and creates a translation invariant Metric,
 * because it calculates the Norm as the Distance of the Points. |x| = d(x,0)
 *
 * You can equivalently always define a translation invariant and
 * homogeneous Metric as the Norm of the Difference: d(x,y) = |x-y|
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 89bd1ad748e9ca678c425ac6f815fb7585b6621405d682ceb55fd2ad9ed08ea7
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * The Implementations here are not very efficient and should be overwritten. */
public class ANorm
extends AMetric
implements INorm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**This Constructor is only used in 'Initialize' and 'Terminate' of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	protected ANorm (IMetricIRing self_) { super(self_); }	//sets the 'self' Reference for Delegation


	//////////////
	//	Norm	//
	//////////////

	//in 1-dim Spaces all Norms have the same Value

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Norm (double p) { return self.Norm(); }

	/**Betrags-Norm:
	 * Special Case of the p-Norm for p = 1	 */
	public IMetricIRing AbsV_Norm () { return self.Norm(); }

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 */
	public IMetricIRing Max_Norm () { return self.Norm(); }

	/**Euklidische Norm
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant in cartesian Systems.	 */
	public IMetricIRing Norm() { throw new AbstractMethodError(); }

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm() { return (IMetricIRing)self.Norm().sqr(); }

	//////////////
	//	Testing	//
	//////////////

	/**Method to test all Implementations in this class.	 */
	public static void testIt() {
		System.out.println("Testing : ANorm");
//		Norm test = (Norm) testInstance.copy();
//		Norm test1 =(Norm) testInstance.newInstance();
		//only deriving the Dist Methods from the Norm Methods here.
	}

}
