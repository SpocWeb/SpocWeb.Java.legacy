/*
 * File Name: PegasusFloatRefiner.java
 * Created on: 01.02.2004
 *
 */
package math.refiner;

import streamIO.Log;
import function.IFloatFunction;

/**
 * Title: PegasusFloatRefiner<p>
 * Description:
 * Pegasus Root Finding  Algorithm is a Melange of several Algorithms:
 * You can choose between the Pegasus- and the Andersson/Bjoerk Algorithm
 * King's Modifikation can be added also.
 * Convergence is guaranteed; Speed is at best of golden Rule Speed (1.618...)
 * but at least linear (BiSection).
 * Requires y to be differentiable and y' to be continuous for best Convergence,
 * but also works for only continuous y(x). 
 * 
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see streamIO.copy.group.ring.metric.PegasusRefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class PegasusFloatRefiner 
extends AFloatRefinerQ {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log L = new Log(PegasusFloatRefiner.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor. 	 */
	public PegasusFloatRefiner(){}

	/**Initializing Constructor.
	 * The Zero of the Function y must be bracketed in the Interval x! 	 */
	public PegasusFloatRefiner(final double xl_, final double xr_, final IFloatFunction f_) {
		super(xl_, xr_, f_); }

	/**Determines, if Pegasus Steps are performed	 */
	public boolean usePegasus = true;

	/**Determines, if Illinois Steps (Bisection every 2nd Step) are performed	 */
	public boolean useBisection = false;

	/**Determines, if Secant Steps are performed.
	 * Switched on alternatively.	 */
	private boolean useSecant;

	/**
	 * Performs a single approximating Step
	 * by doing a Regula Falsi Step or BiSection.
	 * yl is kept positive, so the check can be performed faster. 
	 * @return x, the best Ordinate for the Root so far 
	 */
	public double refine() {
		dx=  xl - xr;
		dy=  yl - yr;
		x =  xl - dx*yl/dy;  //{Regula falsi: new x-Value}
		y = (f.Map(x));
		if (!useSecant && (y < 0)) {
			yl = yr; xl = xr; useSecant = true;
		} else { //{yr und y positiv}
			 //{Illinois ist gemischte Regula-falsi und Bisektion}
			if (useBisection) { 
				yl *= .5;  //Bisection: yl*g=1/2
			} else if (usePegasus) { //Inverse quadratische Interpolation
				yl *= yr/(y + yr);	//{=> yl*g aus (0,1)}
			} else { 	//{Pegasus ist gemischte ? und Bisektion}
				if (y >= yr) { 
					yl *= .5; //out of Bounds, Bisection: yl*g=1/2
				} else {
					yl -= y*yl/yr; 	//{=> yl*g aus (0,1)}
				}
			}
			useSecant = false;	//{modifizierter Schritt}
		}
		yr = y;
		xr = x;
		return x; }

	/**Method to test all Implementations in this class.	 */
	public static void testIt() { //
		L.n("Testing ");
		L.n("Searching for the Root of y = "+TEST_FUNCTION);
		final PegasusFloatRefiner refiner = new PegasusFloatRefiner();
		boolean useSecant = false; 
		do {
			refiner.useSecant = useSecant; 
			boolean useBisection = false; 
			do {
				refiner.useBisection = useBisection;
				boolean usePegasus = false;  
				do {
					refiner.usePegasus = usePegasus; 
					L.n(useSecant?"not ":"").l("using Secants"); 
					L.n(useBisection?"not ":"").l("using Bisection"); 
					L.n(usePegasus?"not ":"").l("using Pegasus"); 
					refiner.init(0, 3, TEST_FUNCTION); 
					TEST_REFINER(refiner, TEST_ZERO_POINT, 9);
				} while(usePegasus = !usePegasus); 
			} while(useBisection = !useBisection); 
		} while(useSecant = !useSecant); 
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
