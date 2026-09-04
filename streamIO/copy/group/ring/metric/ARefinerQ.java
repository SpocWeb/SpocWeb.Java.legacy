package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.SecantRefiner;
import function.IFunction;

/**
 * Quality Stepper Algorithm:
 * Extends Stepping for a Solution in a 1-dim Varable Space
 * by bounding the Zero Position (continuous f)
 * and iterating until the Zero is hit in x AND y Direction.
 * Requires f to be continuous.
 * The Stepper Routine should keep the right Function Value positive.
 *
 * Known SubClasses: 
 * @see streamIO.copy.group.ring.metric.FalsiRefinerQ
 * @see streamIO.copy.group.ring.metric.NewtonRefinerQ
 * @see streamIO.copy.group.ring.metric.PegasusRefiner
 *
 * These Descendants are used in MultiStepYQ
 * which does a faster Check for Convergence relying on (yr > 0).
 */
public abstract class ARefinerQ
extends SecantRefiner	//SecantRefiner extends ARefiner by xr and yr.
//unfortunately the refine() Method is already preset and will not be required 
{
	/**Determines, whether the Starting Points can be swapped	 */
	//protected boolean swapPoints = true;

	/**Initializes the Stepper	 */
	public void init(final IIntRing xl_, final IIntRing xr_, final IFunction f_) {
		super.init(xl_, xr_, f_);
		final boolean lNeg = ((IScalarMetric)yl).negative();
		final boolean rNeg = ((IScalarMetric)yr).negative();
//		dy = (MetricIRing) yr.subt(yl);	//already Part of SecantRefiner
		if (lNeg == rNeg) {
			throw new AbstractMethodError("Zero is not bracketed between "+yl+" and "+yr);}
		final boolean swapPoints = true; // 
		if (rNeg && swapPoints) {	//{Umkehren,so dass f in xl negativ => schnellerer Test}
			IIntRing tmp;
			tmp = yl; yl = yr; yr = tmp; dy.negAt();
			tmp = xl; xl = xr; xr = tmp; dx.negAt();
		}
	}

	/**Initializing Constructor.
	 * The Zero of the Function f must be bracketed in the Interval x!
	 * The Stepper should keep the Zero bracketed!
	 * yr is kept positive!	 */
	public ARefinerQ() { }

	/**Initializing Constructor.
	 * The Zero of the Function f must be bracketed in the Interval x!
	 * The Stepper should keep the Zero bracketed!
	 * yr is kept positive!	 */
	public ARefinerQ(IMetricIRing xl_, IMetricIRing xr_, IFunction f_) { super(xl_, xr_, f_); }

	/**Old x Value	 */
	protected IMetricIRing x;

	/**Old y Value	 */
	protected IMetricIRing y;

}
