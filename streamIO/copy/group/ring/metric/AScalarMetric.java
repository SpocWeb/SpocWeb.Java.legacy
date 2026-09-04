package streamIO.copy.group.ring.metric;

import streamIO.copy.group.IGroup;
import streamIO.copy.order.AOrder;
import streamIO.copy.order.IOrder;

/**Implements all the Methods of the Interface 'ScalarMetric',
 * which integrates 1-dim Order with a Metric.
 * Must be concrete, because it is used for Delegation with Template Methods!
 *
 * Design Decisions:
 * Does not extend absGroup anymore, because that had too many virtual Methods,
 * and because the Integration with algebraic Operation comes only in AMetricIRing.
 *
 * Is a Delegator, inherits self from AOrder,
 * doesn't define it's own self, because the Methods are too few and too frequent.	 */
public class AScalarMetric
extends AOrder
implements IScalarMetric {

	/**Local Reference to the Self, initialized by concrete classes.
	 * Used for the Simulation of (multiple) Inheritance with Delegation.
	 * Must be a virtual Interface Type to be able to take any Implementation.	 */
//  private Group self;	//inherits self from AOrder

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**This Constructor is only used in Initialize and Terminate of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	protected AScalarMetric(IOrder self_) { super (self_); }	//Call the Super Constructor with self_ as the Argument.

	//Implementation

	/**Returns true, when this is positive (x > 0).	 */
	public boolean positive() { return self.isMoreThan(((IGroup)self).zero()); }

	/**Returns true, when this is negative (x < 0).	 */
	public boolean negative() { return self.isLessThan(((IGroup)self).zero()); }

	/**Returns true, if the arg has the opposite Zchn to this Number	 */
	public boolean changeZchn(Object arg) { 
		return ((IScalarMetric)self).negative() ^ ((IScalarMetric)arg).negative(); }

	/**Returns true, if the arg has the opposite Sign to this Number	 */
	public boolean changeSign(Object arg) {
		if (((IGroup)self).isZero() || ((IGroup)arg).isZero()) return false;
		return ((IScalarMetric)self).negative() ^ ((IScalarMetric)arg).negative(); }

	/**Returns the Sign of this Number	 */
	public int Sign() {
		return (((IScalarMetric)self).negative() ? -1 : (((IGroup)self).isZero() ? 0 : +1)); }
//  public MetricIRing Sign(){return ((ScalarMetric) self.copy()).SignAt(); }

	/**Returns the Sign of this Number in Place	 */
	public IMetricIRing SignAt() {
		if		(((IScalarMetric)self).negative())	return	 (IMetricIRing)
															((IMetricIRing)
															((IMetricIRing) self).oneAt()).negAt();
		else if	(((IGroup	   )self).isZero())		return   (IMetricIRing) self;
													return	 (IMetricIRing)
															((IMetricIRing) self).oneAt(); }

	/**Returns the exact Position of this Number relative to arg:
	 * -1 if this is smaller than arg, 0 for equal, otherwise +1	 */
	public int compareTo(Object arg) {
		return (self.isLessThan(arg) ?  -1 : (self.equals(arg) ? 0 : 1)); }
//  public MetricIRing compareTo(Object arg) { return ((ScalarMetric) self.copy()).PositionAt(arg); }

	/**Returns the exact Position of this Number relative to arg in Place:
	 * -1 if this is smaller than arg, 0 for equal, otherwise +1	 */
	public IMetricIRing compareToAt(Object arg) {
//  	return ((MetricIRing)((MetricIRing)self).subt(arg)).ZchnAt();
		if		(self.isLessThan	(arg))	((IMetricIRing)	((IMetricIRing) self).oneAt()).negAt();
		else if (self.equals(arg))					((IMetricIRing) self).zeroAt();
		else										((IMetricIRing) self).oneAt();
		return (IMetricIRing) self; }

	/**Returns the Position of this Number relative to arg:
	 * -1 if this is smaller than arg, otherwise +1	 */
	public int Position(Object arg)	{ return (self.isLessThan(arg) ?  -1 : 1); }
//  public MetricIRing compareTo(Object arg) { return ((ScalarMetric) self.copy()).compareToAt(arg); }

	/**Returns the Position of this Number relative to arg in Place:
	 * -1 if this is smaller than arg, otherwise +1	 */
	public IMetricIRing PositionAt(Object arg) {
//  	return ((MetricIRing)((MetricIRing)self).subt(arg)).SignAt();
		if (isLessThan(arg)) ((IMetricIRing)	((IMetricIRing) self).oneAt()).negAt();
		else							((IMetricIRing) self).oneAt();
		return (IMetricIRing) self; }

	/**Returns the Sign of this Number, but also 1 for 0	 */
	public int Zchn()	{ return (((IScalarMetric)self).negative() ? -1 : +1); }
//  public MetricIRing Zchn(){return ((ScalarMetric) self.copy()).ZchnAt(); }

	/**Returns the Sign of this Number in Place, but also 1 for 0	 */
	public IMetricIRing ZchnAt() {
		if (negative()) ((IMetricIRing)	((IMetricIRing) self).oneAt()).negAt();
		else							((IMetricIRing) self).oneAt();
		return (IMetricIRing) self; }

	/**Returns this Number multiplied by the Sign of arg	 */
	public IMetricIRing mulSign(Object arg) {
		return ((IScalarMetric) self.copy()).mulSignAt(arg); }

	/**Returns this Number multiplied in Place by the Sign of arg	 */
	public IMetricIRing mulSignAt(Object arg) {
		if		(((IScalarMetric)arg).negative())((IGroup)self).negAt();
		else if (((IMetricIRing )arg).isZero())	((IGroup)self).zeroAt();
		return (IMetricIRing) self; }

	/**Returns this Number multiplied by the Zchn of arg	 */
	public IMetricIRing mulZchn(Object arg) {
		return ((IScalarMetric) self.copy()).mulZchnAt(arg); }

	/**Returns this Number multiplied in Place by the Zchn of arg	 */
	public IMetricIRing mulZchnAt(Object arg) {
		if (((IScalarMetric)arg).negative()) ((IGroup)self).negAt();
		return (IMetricIRing) self; }

	/**Returns this Number set to the Sign of arg	 */
	public IMetricIRing setSign(Object arg) {
		return ((IScalarMetric) self.copy()).setSignAt(arg); }

	/**Returns this Number set in Place to the Sign of arg	 */
	public IMetricIRing setSignAt(Object arg) {
		if (((IMetricIRing )arg).isZero()) 
			return (IMetricIRing) ((IGroup)self).zeroAt();
		return setZchnAt(arg); }

	/**Returns this Number multiplied by the Zchn of arg	 */
	public IMetricIRing setZchn(Object arg) {
		return ((IScalarMetric) self.copy()).setZchnAt(arg); }

	/**Returns this Number set in Place to the Zchn of arg	 */
	public IMetricIRing setZchnAt(Object arg) {
		if (changeZchn(arg)) 
			((IGroup) self).negAt();
		return (IMetricIRing) self; }

	/**absolute Value:						 |x|	*/
	public IScalarMetric AbsV() {
		return ((IScalarMetric) self.copy()).AbsVAt(); }

	/**absolute Value in Place:				 |x|
	 * Should be redefined for Vectors, Complex etc.,
	 * because this Definition is not valid for that!	 */
	public IScalarMetric AbsVAt() {
		return  ((IScalarMetric) self).negative() ?
				 (IScalarMetric)((IGroup)self).negAt() :
				((IScalarMetric) self); }

	//all other Norms are defined by the Norms and Metrices

	/**Square of the absolute Value:		 |x|^2	 */
//  public SemiGroupM SqrAbsV()	 {return ((ScalarMetric)((CopyAble)self).copy()).SqrAbsVAt(); }

	/**Square of the absolute Value in Place:|x|^2
	 * Should be redefined for Vectors, Complex etc.,
	 * because this Definition is not valid for that!	 */
//  public SemiGroupM SqrAbsVAt(){return ((GroupM) self).sqrAt(); }


	//////////////////////
	//  Scalar Metric:	//
	//////////////////////

	/**absolute Distance:						|x|
	 * Should be redefined for Vectors, Complex etc.,
	 * because this Definition is not valid for that!	 */
	public IScalarMetric AbsDist		(Object arg)//{return (less(arg)) ? ((Group)arg).subt(self) : self.subt(arg); }
	{return ((IScalarMetric) self.copy()).AbsDistAt(arg); }

	/**absolute Distance in Place:				|x|
	 * Should be redefined for Vectors, Complex etc.,
	 * because this Definition is not valid for that!	 */
	public IScalarMetric AbsDistAt	(Object arg)//{return (less(arg)) ? (Group)((CopyAble)self).copyAt(((Group)arg).subt(self)) : self.subt(arg); }
	{return ((IMetricIRing) ((IGroup)self).subAt(arg)).AbsVAt(); }


	/**Square of the absolute Distance:			|x|^2	*/
//  public SemiGroupM AbsSqrDist	(Object arg){return ((ScalarMetric)self.subt  (arg)).SqrAbsVAt(); }

	/**Square of the absolute Distance in Place:|x|^2	*/
//  public SemiGroupM AbsSqrDistAt	(Object arg){return ((ScalarMetric)self.subAt(arg)).SqrAbsVAt(); }

	//////////////
	//  Norm	//
	//////////////

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Norm (double p){return (IMetricIRing)AbsV(); }

	/**Betrags-Norm:
	 * Special Case of the p-Norm for p = 1	 */
	public IMetricIRing AbsV_Norm (){return (IMetricIRing)AbsV(); }

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 */
	public IMetricIRing Max_Norm (){return (IMetricIRing)AbsV(); }

	/**Euklidische Norm
	 * Special Case of the p-Norm for p = 2	 */
	public IMetricIRing Norm   (){return (IMetricIRing)AbsV(); }

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2	 */
	public IMetricIRing SqrNorm(){return (IMetricIRing)AbsV(); }

	//////////////
	//  Norm	//
	//////////////

	/**p-Metric: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Dist (Object arg, double p){return (IMetricIRing)AbsV_Dist(arg); }

	/**Absolute Value-Metric:
	 * Special Case of the p-Metric for p = 1
	 *
	 * Default Implementation for Scalars, not valid for Vectors! Avoids Recursion! 	 */
	public IMetricIRing AbsV_Dist (Object arg){return (IMetricIRing)AbsDist(arg); }

	/**Maximums-Metric
	 * Special Case of the p-Metric for p -> Infinity	 */
	public IMetricIRing Max_Dist (Object arg){return (IMetricIRing)AbsV_Dist(arg); }

	/**Euklidische Metric
	 * Special Case of the p-Metric for p = 2	 */
	public IMetricIRing Dist   (Object arg){return (IMetricIRing)AbsV_Dist(arg); }

	/**(Euklidische Metric)^2
	 * Special Case of the p-Metric for p = 2	 */
	public IMetricIRing SqrDist(Object arg){return (IMetricIRing)AbsV_Dist(arg); }


	//////////////
	//  Testing	//
	//////////////

	/**Method to test all Implementations in this class.	 */
	public static void testIt() throws java.io.IOException
	{
		System.out.println("Testing AScalarMetric:");
		tVorzeichen();
	}

	private static void tVorzeichen() throws java.io.IOException
	{
//		MetricIRing test = (MetricIRing) testInstance;
		IMetricIRing x1 =(IMetricIRing) testInstance.copy();
		IMetricIRing x2 =(IMetricIRing) testInstance.copy(); x2.oneAt();

		System.out.println ();
		System.out.println ("Testing changeZchn and changeSign :");
		int i1 = +2; x1.oneAt();
		while (--i1 >= -1)
		{
			int i2 = +2; x2.oneAt();
			while (--i2 >= -1)
			{
				System.out.println ("x1 = " + x1 + " x2 = " + x2 + " changeSign = " + x1.changeSign(x2) + " changeZchn = " + x1.changeZchn(x2));
				x2.dec();
			}
			System.out.println ("x1 = " + x1 + " Sign = " + x1.Sign() + " Zchn = " + x1.Zchn());
			x1.dec();
		}
		System.in.read(); System.in.read();
	}
}
