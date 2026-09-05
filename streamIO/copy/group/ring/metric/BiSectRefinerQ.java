package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IRing;
import function.IFunction;

/**
 * Title: BiSectRefinerQ<p>
 * Description:
 * Performs a Search by Bisection.
 * The Algorithm uses only Ring Properties to divide the Interval in half 
 * and Metric Properties to determine the next Interval.
 * Works only on 1-dim fully ordered Sets R->R
 * It can also be used on discrete fully ordered Sets to find an Item,
 * by using the Item(Index) Funtion on x from N (integer Numbers).
 * This is equivalent to a binary Search implemented on discrete Sets.  
 * 
 * Convergence is guaranteed; Speed is exactly linear (BiSection).
 * Requires y to be continuous. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see streamIO.copy.group.ring.metric.PegasusRefiner 
 * which combines BiSection with Secant and Pegasus Algorithm.
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
 * digest: 414beed65c010979841956e1a3d37b2f648c0d4bab568a7f14ac15e7659f0b9b
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class BiSectRefinerQ
extends ARefinerQ {

	/**Empty Constructor. 	 */
	public BiSectRefinerQ(){}

	/**Initializing Constructor.
	 * The Zero of the Function f must be bracketed in the Interval x!
	 * The Stepper should keep the Zero bracketed!
	 * yr is kept positive!	 */
	public BiSectRefinerQ(final IMetricIRing xl_, final IMetricIRing xr_, final IFunction f_) {
		init(xl_, xr_, f_);}

	/**Initialization of a new Stepper Search	 */
	public void init(IIntRing xl_, IIntRing xr_, IFunction f_) {
		super.init(xl_, xr_, f_);	//=> f(xl), f(xr), dx and dy calculated
		x = (IMetricIRing) xl.newInstance();
	}

	/**Performs a single approximating Step
	 * until the item is bounded with sufficient Accuracy.
	 * And keeps the Zero bounded by keeping yr positive!
	 * Rotation: (x,y)->(xr, yr)->(xl,yl) 	 */
	public IIntRing refine() {
		IIntRing tmp;	//calculate the middle Point
		((IIntRing)((IRing)x.copyAt(xr)).addAt(xl)).halfAt();
		y = (IMetricIRing) f.Map(x);
		if (y.positive()) { //choose the new Interval
			tmp = xr; xr = x; x = (IMetricIRing) tmp;
			tmp = yr; yr = y; y = (IMetricIRing) tmp;
		} else {
			tmp = xl; xl = x; x = (IMetricIRing) tmp;
			tmp = yl; yl = y; y = (IMetricIRing) tmp;
		}
		return x; }

	/**Method to test all Implementations in this class.	 */
	public static void testIt() {
		//Testing single Step of Pegasus Step with Quality Control
		L.n("Testing ").l(PegasusRefiner.class);
		L.n("Searching for the Root of y = "+TEST_FUNCTION);
		final IMetricIRing xLeft  = (IMetricIRing) TEST_ZERO_POINT.copy();
		final IMetricIRing xRight = (IMetricIRing) TEST_ZERO_POINT.copy();
		xLeft .copyAt(new Double(0));
		xRight.copyAt(new Double(3));
		final BiSectRefinerQ refiner = new BiSectRefinerQ(xLeft, xRight, TEST_FUNCTION);
		TEST_REFINER(refiner, TEST_ZERO_POINT, 23);
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
