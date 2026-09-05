package streamIO.copy.group.ring.metric;

import streamIO.copy.order.IOrder;

/**This Interface defines all Methods of a Metric defined on a scalar (1D) Type.
 * A Default Implementation is done in 'AScalarMetric'
 * In C++ integrated this Interface into MetricIRing
 * to reduce the Number of Interfaces and Classes.
 *
 * Design Decisions:
 * All the Methods of this Interface are usually defined using an ordered Group.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 38b88e60f9f7062dab8cc740f5fff0f4892c8bdb0fd11937a3d01a08367acb7a
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * So there is no pure virtual base class 'int'. */
public interface IScalarMetric
extends IOrder, INorm //, Group	//taken out, don't want to implement all the Methods of 'Group'
{

	/**Returns the Sign of this Number	 */
	public int Sign();

	/**Returns the Sign of this Number in Place	 */
	public IMetricIRing SignAt();

	/**Returns the Sign of this Number, but also 1 for 0	 */
	public int Zchn();

	/**Returns the Sign of this Number in Place, but also 1 for 0	 */
	public IMetricIRing ZchnAt();

	/**Returns the Position of this Number relative to arg:
	 * -1 for smaller, otherwise +1	 */
	public int Position(Object arg);

	/**Returns the Position of this Number relative to arg in Place:
	 * -1 for smaller, otherwise +1	 */
	public IMetricIRing PositionAt(Object arg);

	/**Returns the exact Position of this Number relative to arg:
	 * -1 for smaller, 0 for equal, otherwise +1	 */
	public int compareTo(Object arg);

	/**Returns the exact Position of this Number relative to arg in Place:
	 * -1 for smaller, 0 for equal, otherwise +1	 */
	public IMetricIRing compareToAt(Object arg);

	/**Returns this Number multiplied by the Sign of arg	 */
	public IMetricIRing mulSign(Object arg);

	/**Returns this Number multiplied in Place by the Sign of arg	 */
	public IMetricIRing mulSignAt(Object arg);

	/**Returns this Number multiplied by the Zchn of arg	 */
	public IMetricIRing mulZchn(Object arg);

	/**Returns this Number multiplied in Place by the Zchn of arg	 */
	public IMetricIRing mulZchnAt(Object arg);

	/**Returns true, if the arg has the opposite Zchn to this Number	 */
	public boolean changeZchn(Object arg);

	/**Returns true, if the arg has the opposite Sign to this Number	 */
	public boolean changeSign(Object arg);

	/**Returns this Number set to the Sign of arg	 */
	public IMetricIRing setSign(Object arg);

	/**Returns this Number set in Place to the Sign of arg	 */
	public IMetricIRing setSignAt(Object arg);

	/**Returns this Number multiplied by the Zchn of arg	 */
	public IMetricIRing setZchn(Object arg);

	/**Returns this Number set in Place to the Zchn of arg	 */
	public IMetricIRing setZchnAt(Object arg);

	/**Returns 'true' when this is a positive Number.	 */
	public boolean positive();

	/**Returns 'true' when this is a negative Number.	 */
	public boolean negative();


	//////////////////////
	//  Scalar Norm:	//
	//////////////////////

	//all other Metrics and Norms are defined by their respective Norm
	/**Square of the absolute Value:		 |x|^2	*/	//public SemiGroupM SqrAbsV();
	/**Square of the absolute Value in Place:|x|^2	*/	//public SemiGroupM SqrAbsVAt();
	/**Square of the absolute Distance:			|x|^2	*/	//public SemiGroupM AbsSqrDist	(Object arg);
	/**Square of the absolute Distance in Place:|x|^2	*/	//public SemiGroupM AbsSqrDistAt	(Object arg);

	/**absolute Value:						 |x|	*/	public IScalarMetric AbsV();
	/**absolute Value in Place:				 |x|	*/	public IScalarMetric AbsVAt();


	//////////////////////
	//  Scalar Metric:	//
	//////////////////////

	/**absolute Distance:						|x|		*/	public IScalarMetric AbsDist		(Object arg);
	/**absolute Distance in Place:				|x|		*/	public IScalarMetric AbsDistAt	(Object arg);
}
