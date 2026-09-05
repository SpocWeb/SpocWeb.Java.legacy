package streamIO.copy.group.ring.metric;

import streamIO.copy.CCopyAble;
import streamIO.copy.order.COrder;

/**Implements Constants for all Types of WellOrder Classes.
 * This Class inhibits the Use of ...At() Routines
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 8bfa821c5454e0ed5324a533ef7c0658ec96e972d86d26016acaec9f08033b2e
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * but still supports all other Methods of the WellOrder Class.	 */
public class CScalarMetric
extends COrder
implements IScalarMetric {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor	 */
	public CScalarMetric(IScalarMetric cnst) { super(cnst); }

	//////////////////////////
	//  interface WellOrder	//
	//////////////////////////

	/**Returns the Sign of this Number	 */
	public int Sign()	{ return ((IScalarMetric) self).Sign(); }

	/**Returns the Sign of this Number in Place	 */
	public IMetricIRing SignAt() { throw new AbstractMethodError(CCopyAble.strConst); }

	/**Returns the Sign of this Number, but also 1 for 0	 */
	public int Zchn()	{ return ((IScalarMetric) self).Zchn(); }

	/**Returns the Sign of this Number in Place, but also 1 for 0	 */
	public IMetricIRing ZchnAt() { throw new AbstractMethodError(CCopyAble.strConst); }

	/**Returns the Position of this Number relative to arg:
	 * -1 for smaller, otherwise +1	 */
	public int Position(Object arg)	{ return ((IScalarMetric) self).Position(arg); }

	/**Returns the Position of this Number relative to arg in Place:
	 * -1 for smaller, otherwise +1	 */
	public IMetricIRing PositionAt(Object arg) { throw new AbstractMethodError(CCopyAble.strConst); }

	/**Returns the exact Position of this Number relative to arg:
	 * -1 for smaller, 0 for equal, otherwise +1	 */
	public int compareTo(Object arg)	{ return ((IScalarMetric) self).compareTo(arg); }

	/**Returns the exact Position of this Number relative to arg in Place:
	 * -1 for smaller, 0 for equal, otherwise +1	 */
	public IMetricIRing compareToAt(Object arg) { throw new AbstractMethodError(CCopyAble.strConst); }

	/**Returns this Number multiplied by the Sign of arg	 */
	public IMetricIRing mulSign(Object arg)	{ return ((IScalarMetric) self).mulSign(arg); }

	/**Returns this Number multiplied in Place by the Sign of arg	 */
	public IMetricIRing mulSignAt(Object arg) { throw new AbstractMethodError(CCopyAble.strConst); }

	/**Returns this Number multiplied by the Zchn of arg	 */
	public IMetricIRing mulZchn(Object arg)	{ return ((IScalarMetric) self).mulZchn(arg); }

	/**Returns this Number multiplied in Place by the Zchn of arg	 */
	public IMetricIRing mulZchnAt(Object arg) { throw new AbstractMethodError(CCopyAble.strConst); }

	/**Returns true, if the arg has the opposite Zchn to this Number	 */
	public boolean changeZchn(Object arg)	{ return ((IScalarMetric) self).changeZchn(arg); }

	/**Returns true, if the arg has the opposite Sign to this Number	 */
	public boolean changeSign(Object arg)	{ return ((IScalarMetric) self).changeSign(arg); }

	/**Returns this Number set to the Sign of arg	 */
	public IMetricIRing setSign(Object arg)	{ return ((IScalarMetric) self).setSign(arg); }

	/**Returns this Number set in Place to the Sign of arg	 */
	public IMetricIRing setSignAt(Object arg){ return ((IScalarMetric) self).setSignAt(arg); }

	/**Returns this Number multiplied by the Zchn of arg	 */
	public IMetricIRing setZchn(Object arg)	{ return ((IScalarMetric) self).setZchn(arg); }

	/**Returns this Number set in Place to the Zchn of arg	 */
	public IMetricIRing setZchnAt(Object arg) { throw new AbstractMethodError(CCopyAble.strConst); }

	/**Returns 'true' when this is a positive Number.	 */
	public boolean positive()	{ return ((IScalarMetric) self).positive(); }

	/**Returns 'true' when this is a negative Number.	 */
	public boolean negative()	{ return ((IScalarMetric) self).negative(); }


	//////////////////////
	//  Scalar Norm:	//
	//////////////////////

	/**absolute Value:						 |x|	*/
	public IScalarMetric AbsV()	{ return ((IScalarMetric) self).AbsV(); }

	/**absolute Value in Place:				 |x|	*/
	public IScalarMetric AbsVAt() { throw new AbstractMethodError(CCopyAble.strConst); }


	//////////////////////
	//  Scalar Metric:	//
	//////////////////////

	/**absolute Distance:						|x|		*/
	public IScalarMetric AbsDist	 (Object arg) { return ((IScalarMetric) self).AbsDist(arg); }

	/**absolute Distance in Place:				|x|		*/
	public IScalarMetric AbsDistAt(Object arg) { throw new AbstractMethodError(CCopyAble.strConst); }


	//////////////////////
	//  interface Norm	//
	//////////////////////

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Norm (double p)	{ return ((INorm) self).p_Norm(p); }

	/**Betrags-Norm:
	 * Special Case of the p-Norm for p = 1
	 * This norm is the fastest to chalculate	 */
	public IMetricIRing AbsV_Norm ()	{ return ((INorm) self).AbsV_Norm(); }

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 */
	public IMetricIRing Max_Norm ()	{ return ((INorm) self).Max_Norm(); }

	/**Euklidische Norm
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing Norm   ()	{ return ((INorm) self).Norm(); }

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm()	{ return ((INorm) self).SqrNorm(); }


	//////////////////////////
	//  interface Metric	//
	//////////////////////////

	/**This Distance Function defines a Metric on the Elements of IMetric Type.	 */
	public IMetricIRing Dist(Object arg)	{ return ((IMetricIRing) self).Dist(arg); }

	/**p-Metric: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Dist (Object arg, double p) {
		return ((IMetricIRing) self).p_Dist(arg, p); }

	/**Absolute Value-Metric:
	 * Special Case of the p-Metric for p = 1	 */
	public IMetricIRing AbsV_Dist (Object arg) {
		return ((IMetricIRing) self).AbsV_Dist(arg); }

	/**Maximums-Metric
	 * Special Case of the p-Metric for p -> Infinity	 */
	public IMetricIRing Max_Dist (Object arg) {
		return ((IMetricIRing) self).Max_Dist(arg); }

	/**(Euklidische Metric)^2
	 * Special Case of the p-Metric for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrDist(Object arg) {
		return ((IMetricIRing) self).SqrDist(arg); }

}
