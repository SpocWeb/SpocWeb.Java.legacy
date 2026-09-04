package streamIO.copy.group.ring.metric;

/**Interface for Classes that have a Metric defined that defines a Topology
 * on it's Elements by defining Neighborhoods of Elements close to each other.
 *
 * Metric Space (X,d: X*X ->R)
 * Space X is called metric Space with Metric d when the following is true:
 * 1) d(x,y) > 0 when x!=y    : d is positive definite (otherwise Pseudo-Metric)
 * 2) d(x,x)==0               : For Graphs even this can be > 0  !
 * 3) d(x,y)==d(y,x)          : d is symmetric (Graphs can be asymmetric)
 * 4) d(x,z)<=d(x,y)+d(y,z)   : Triangle-Inequation
 * A Metric on a Group is called translation invariant when the following is true:
 * 5) d(x,y)==d(x+z,y+z)
 *
 * You can define the absolute Value by |x| = d(x,0)
 * With defined Subtraction you can define d by the absolute Value: d(x,y)=|x-y|
 * You can define a Topology t by d
 *
 * A Norm always defines a canonic Metric, but is also homogeneous.
 *
 * Design Decisions:
 * Instead of MetricIRing, also float or double could have been chosen,
 * but this more generic Type may reduce some conversion work
 * in intricate computations and allows for using generic Methods
 * to sort, order and compare the Elements.
 * On the other Hand it creates a recursive Dependency between Metric
 * and MetricIRing, that can only be resolved by iterative Compilation.
 */
public interface IIMetric {

	/** This Distance Function defines a Metric on the Elements of IMetric Type.
	  * @return the Distance between this Object and the given Object. 	 */
	public IMetricIRing Dist(Object arg);

}
