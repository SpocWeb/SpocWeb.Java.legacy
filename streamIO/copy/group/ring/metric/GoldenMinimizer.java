package streamIO.copy.group.ring.metric;	//Body;

import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.SecantRefiner;
import streamIO.copy.groupM.IGroupM;
import function.IFunction;
import function.IMeasurAble;

/**
 * Title: GoldenMinimizer<p>
 * Description:
 * Finds the local Minimum for the given Function by iterative best bracketing
 * in the manner of the golden Rule:	 xl------xm--xt------xr
 *
 * The Idea is to calculate a Test Point in the larger Interval
 * and to compare it's Function Value to the current Minimum Estimation.
 * If it is smaller, it is chosen as the new MidPoint,
 * else the old one is kept, but the right border is set to the Test Point
 * (because it fulfills the inequation below).
 * The Points are chosen so that the Interval is of same Size in both cases:
 * (xr-xMid) == (xTst-xl)
 * Let's assume, the Algorithm is settled,
 * so that the golden Ratios are realized, then the following is true:
 * (xr-xMid) = g*(xr-xl) and (xMid-xl) == g*(xr-xMid)
 * (the Ratio of the smaller Interval to the larger one is the same as
 *	the Ratio of the larger  Interval to the Sum of both Intervals.)
 * With this is (xTst-xl) == (xr-xMid) == g*(xr-xl)
 *
 * The Reason why the Minimum is searched for is
 * that very often Funtion are positive definite and unlimited (see x^2).
 * To find the Maximum, just negate the Function.
 *
 * Prerequisites:
 * f: R -> R is continuous, but not necessarily differentiable.
 * Minimum is 'bracketed' in xTst > x > xMid so that yMid > y < yTst
 *
 * This is the Equivalent to the BiSection Algorithm in Zero Finding.
 * Very robust, but only linearly converging.  
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see math.refiner.AFloatMinimizer
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 11d87a492cd823106cba41cb070075e01db0b770c91b7d4c7b6c4b528e3556ab
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class GoldenMinimizer
extends SecantRefiner {	//ARefinerQ {	//swaps the Points unnecessarily!

	/**Middle Point x Value	 */	private IMetricIRing xMid;
	/**Middle Point y Value	 */	private IMetricIRing yMid;
	/**Test Point x Value	 */	private IMetricIRing xTst;
	/**Test Point y Value	 */	private IMetricIRing yTst;

	/** Buffer for the evaluation of the new Test Point	 */
	private boolean testForLess;

	/** Switches on Searching for the Maximum instead of the Minimum	 */
	public boolean maximize;

	/**Initializes the Stepper.
	 * Since no middle Point is chosen, it is calculated
	 * (and hopefully smaller/larger than xl and xr)	 */
	public void init(IIntRing xl_, IIntRing xr_, IFunction f_) {
		init((IMetricIRing) xl_, (IMetricIRing)((IMetricIRing) xl_.add(xr_)).halfAt(), (IMetricIRing) xr_, f_); }

	/**Initializes the Stepper	 */
	public void init(final IMetricIRing xl_, IMetricIRing x_, final IMetricIRing xr_, final IFunction f_) {
		super.init(xl_, xr_, f_);	//Evaluation of f at xl and xr not necessary, but used to verify bracketing!
		x_	 = (IMetricIRing) x_.copy();
		yMid = (IMetricIRing) f.Map(x_); 
		maximize = yMid.isMoreThan(yl); 
		if (yMid.isMoreThan(yr) != maximize) { 
			throw new AbstractMethodError((maximize?"Maximum":"Minimum")+" not bracketed by ("+xl+","+yl+"),("+x_+","+yMid+"),("+xr+","+yr+")"); }
		xMid = (IMetricIRing) x_.newInstance();
		xTst = (IMetricIRing) x_.newInstance();
		final IMetricIRing rInt = (IMetricIRing) xr.sub(x_); final boolean rNeg = rInt.negative();
		final IMetricIRing lInt = (IMetricIRing) x_.sub(xl); final boolean lNeg = lInt.negative();
		if (lNeg ^ rNeg) {	//Check if x is between xl and xr.
			throw new AbstractMethodError((maximize?"Maximum":"Minimum")+" not bracketed by ("+xl+","+yl+"),("+xMid+","+yMid+"),("+xr+","+yr+")"); }
		if (rInt.isMoreThan(lInt) ^ lNeg) { //xMid liegt n�her bei xl  // == xr+golden(xMid-xr) //{neuer Punkt wird ermittelt}
			xMid = x_; dx = (IIntRing)xr.sub(xMid); dx.mulAt(IMeasurAble.cGolden); xTst.copyAt(xMid); xTst.addAt(dx);				yTst = (IMetricIRing) f.Map(xTst); 	
		} else { 	// == xl+golden(xTst-xl)
			xTst = x_; dx = (IIntRing)xl.sub(xTst); dx.mulAt(IMeasurAble.cGolden); xMid.copyAt(xTst); xMid.addAt(dx); yTst = yMid;	yMid = (IMetricIRing) f.Map(xMid); 
		}
		testForLess = (yTst.isLessThan(yMid) != maximize);	//{Func braucht NIE wieder an den urspruenglichen Endpunkten ausgewertet zu werden !}
	}

	/**Empty Constructor, init() has to be called to use it! 	 */
	public GoldenMinimizer(){}

	/**Constructor, tests whether the Minimum is really bracketed! 	 */
	public GoldenMinimizer(final IMetricIRing xl, final IMetricIRing x, final IMetricIRing xr, final IFunction f_) {
		init(xl, x, xr, f_); }

	/** Auswertung von f am neuen Punkt	 */
	public IIntRing refine() { //
		IMetricIRing tmp;
		if (testForLess) { //{sonst : Wahl des neuen Intervalles}
			tmp = (IMetricIRing) xl; xl = xMid; xMid = xTst; xTst = tmp; dx = (IIntRing)((IGroupM)xr.sub(xMid)).mulAt(IMeasurAble.cGolden); ((IGroup)xTst.copyAt(xMid)).addAt(dx); yl = yMid; yMid = yTst; yTst = (IMetricIRing) f.Map(xTst); 
		} else {
			tmp = (IMetricIRing) xr; xr = xTst; xTst = xMid; xMid = tmp; dx = (IIntRing)((IGroupM)xl.sub(xTst)).mulAt(IMeasurAble.cGolden); ((IGroup)xMid.copyAt(xTst)).addAt(dx); yr = yTst; yTst = yMid; yMid = (IMetricIRing) f.Map(xMid); 
		}
		if (testForLess = (yTst.isLessThan(yMid) ^ maximize)) {
			return xTst;
		} else { 	//{Besseren Endwert ausgeben}
			return xMid; 
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//Testing single Step of Pegasus Step with Quality Control
		L.n("Testing ").l(GoldenMinimizer.class);
		L.n("Searching for the Minimum of y = ").l(TEST_FUNCTION);
		final IMetricIRing xLeft  = (IMetricIRing) TEST_ZERO_POINT.copy().copyAt(new Double(0));
		final IMetricIRing xMid   = (IMetricIRing) TEST_ZERO_POINT.copy().copyAt(new Double(3));
		final IMetricIRing xRight = (IMetricIRing) TEST_ZERO_POINT.copy().copyAt(new Double(6));
		final GoldenMinimizer minStep = new GoldenMinimizer(xLeft, xMid, xRight, TEST_FUNCTION);
		TEST_REFINER(minStep, TEST_MIN_POINT, 36);
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
