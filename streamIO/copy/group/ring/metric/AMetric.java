package streamIO.copy.group.ring.metric;

import streamIO.copy.ACopyAble;
import streamIO.copy.ICopyAble;

/**Defines a Metric on a Set: d:X*X -> R with the following Properties:
 * 1) x != y => d(x,y) > 0		(positive definite)
 * 2) d(x,x) == 0
 * 3) d(x,y) == d(y,x)			(symmetric)
 * 4) d(x,z) <= d(x,y) + d(y,z)	(Triangle Inequation)
 *
 * You can define an absolute Value by |x| = d(x,0)
 *
 * A Metric defines a Topology on the Set.
 * A Norm || defines a Metric on a Group: d(x,y):= |x-y|
 * A Scalar Product <,> defines a Norm on a Vector Space: |x| = <x,x>^.5
 *
 * Here the Metric d (Distance) is defined as the Norm of the Difference.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: aef8a84eea25c51c6963b5ea8bc6ce5626205173a309223ac40b31c14aff2517
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * A Norm can only be defined, when the Metric is homogeneous. |x| = d(x,0) */
public class AMetric
extends ACopyAble
implements IMetric {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Local Reference to the Self, initialized by concrete classes.
	 * Used for the Simulation of (multiple) Inheritance with Delegation.
	 * Must be a virtual Interface Type to be able to take any Implementation.
	 * Using MetricIRing prevents the Use for any other Subtree */
	protected IMetricIRing self;	//Norm self;

	/**This Constructor is only used in 'Initialize' and 'Terminate' of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	protected AMetric (IMetricIRing self_)	{ self = self_; }	//sets the 'self' Reference for Delegation

	/**p-Metric: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Dist (Object arg, double p) {
		return ((ANorm)self.sub(arg)).p_Norm(p); }

	/**Absolute Value-Metric:
	 * Special Case of the p-Metric for p = 1	 */
	public IMetricIRing AbsV_Dist (Object arg) {
		return ((ANorm)self.sub(arg)).AbsV_Norm(); }

	/**Maximums-Metric
	 * Special Case of the p-Metric for p -> Infinity	 */
	public IMetricIRing Max_Dist (Object arg) {
		return ((ANorm)self.sub(arg)).Max_Norm(); }

	/**Euclidean Metric
	 * Special Case of the p-Metric for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing Dist   (Object arg) {
		return ((ANorm)self.sub(arg)).Norm(); }

	/**(Euclidean Metric)^2
	 * Special Case of the p-Metric for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrDist(Object arg) {
		return ((ANorm)self.sub(arg)).SqrNorm(); }


	//////////////////////////////
	//	Replication intCopyAble	//
	//////////////////////////////

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg, int Depth) { throw new AbstractMethodError(); }

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { throw new AbstractMethodError(); }

	/**Fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg) { throw new AbstractMethodError(); }


	//////////////
	//	Testing	//
	//////////////

	/**Method to test all Implementations in this class.	 */
	public static void testIt() {
		System.out.println("Testing : AMetric");
//		Metric test = (Metric) testInstance.copy();
//		Metric test1 =(Metric) testInstance.newInstance();
		//only deriving the Dist Methods from the Norm Methods here.
	}

}
