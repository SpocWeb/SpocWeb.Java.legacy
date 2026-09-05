/*
 * File Name: BrentFloatRefinerQ.java
 * Created on: 01.02.2004
 *
 */
package math.refiner;

import streamIO.Assert;
import streamIO.Log;
import function.IFloatFunction;
import function.byref.ByRefDouble;
import function.derive.ring.Sign;

/**
 * Implements the Van Wijngaarden-Dekker-Brent method to find the root of a function
 * (Numerical Recipes §9.3), combining bracketing, bisection and inverse quadratic
 * interpolation, using extrapolation to find a bracket when one is not yet available.
 *
 * <p>This is the preferred algorithm when only function values are available, not the
 * derivative. It works even for discontinuous functions, and lets the caller choose between
 * two candidate values (whichever is closer).
 *
 * Similar Classes:
 * @see FalsiFloatRefinerQ  O(1.618... = golden  at best)
 * @see RidderFloatRefinerQ O(1.414... = SqRt(2) at best)
 * @see BrentFloatRefinerQ  O(2 at best without evaluating the Derivative )
 * @see NewtonFloatRefinerQ O(2 at best with    evaluating the Derivative )
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:57:53Z
 * digest: 4fa681c58f98d89cc4e4969c165d08442ba950d7a01277250b0d079fe2723efb
 * stale: false
 * tags: [code/root_finding]
 * concepts: [Brent's Method Root Refiner]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class BrentFloatRefinerQ 
extends AFloatRefinerQ 
implements IFloatImprover {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log L = new Log(BrentFloatRefinerQ.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializes for self-start with default endpoints 0 and 1.
	 */
	public BrentFloatRefinerQ(){
		this.xr = 0;
		this.xl = 1;
		yl=yr=Double.NaN;
	}

	/**
	 * Initializes for self-start from a single guessed point.
	 * @param _xr initial Value for Root finding.
	 */
	public BrentFloatRefinerQ(final double _xr){
		this.xl = _xr; 
		this.xr =(_xr == 0) ? 1 : _xr*1.1;  
		yl=yr=Double.NaN; 
	}
	
	/**Initializes the Regula Falsi Iteration
	 * by giving the Function and two Starting Points.
	 * 
	 * @param xl the  'left' starting Point
	 * @param xr the 'right' starting Point
	 * @param f_ the Function to minimize
	 */
	public BrentFloatRefinerQ(final double xl, final double xr, final IFloatFunction f_) {
		super(xl, xr, f_); 
		x = xr; 
		y = yr;
	}
	
	/**
	 * Initializes the iteration from two starting points and their already-known function
	 * values.
	 */
	public BrentFloatRefinerQ(final double _xl, final double _xr, final double _yl, final double _yr) {
		super(_xl, _xr, _yl, _yr);
		x = xr; 
		y = yr;
	}
	
	/**
	 * Evaluates {@code f} at {@code xr} and performs a single approximating step from it.
	 * @return xr, the best x Value so far...
	 * @see IFloatRefiner#refine()
	 */
	public double refine() { return improve(f.Map(xr)); }

	/**
	 * Performs a single approximating step given the function value at {@code xr}.
	 * @return xr, the best x Value so far, to be evaluated for the next call.
	 * @see IFloatImprover#improve(double)
	 */
	public double improve(final double fnVal) {
		finished(fnVal); return xr;
	}

	/**
	 * Records the given function value as bracketing progress, expanding the interval when
	 * the root is not yet bracketed.
	 * @return true once the root is bracketed between xl/yl and xr/yr
	 */
	protected final boolean bracketed(final double fnVal) {
		if (yr != yr) { //for very late Initialization 
			if (yl != yl) { //both not initialized, swap the x-Coordinates
				yl = fnVal; x = xl; xl = xr; xr = x; 
				return false; 
			} else { //yl already initialized
				if(yl*fnVal > 0) { //same positive Sign, start expanding Selection
					//	throw new IllegalArgumentException("Root is not bracketed!");
					xr  = xl-1.6*(xr-xl); //xl+xl+xl-xr-xr; //3*xl-2*xr; //expand Interval by alternatively jumping left and right.
					return false; 
				}
				y = yr = fnVal; 
			}
		} else {
			yr=fnVal; //evaluate new Trial Root
		}
		return true; 
	}
	
	/**
	 * Performs a single Brent iteration step from the given function value, keeping the
	 * root bracketed by inverse quadratic interpolation or bisection.
	 * @return true once the desired x tolerance has been reached
	 * @see IFloatImprover#finished(double)
	 */
	public boolean finished(final double fnVal) {
		if (!bracketed(fnVal))
			return false; 
		//following two lines just to save Instantiation of new double Variables.
		//L.n("fa="+yl+" fb="+yr+" fc="+y+" "); //yl < 0, yr > 0
		if ((yr > 0) == (y > 0)) { //choose opposite Sign from yr for y
			x=xl;
			y=yl;
			dy=dx=xr-xl; //misusing dy to cache dx!
		}
		//now y and yr have oposite Sign, i.e. y < 0 (if the root WAS bracketed before)
		if (Math.abs(y) < Math.abs(yr)) {
			xl=xr; xr=x; x=xl;
			yl=yr; yr=y; y=yl;
		} //now yr is the smaller absolute Value
		final double x_Tol=ByRefDouble.MUL_ABS_ACCURACY(xr)+xTol; //+0.5*tol;
		final double xm=(x-xr)*0.5; //BiSection
		if ((Math.abs(xm) <= x_Tol) || (yr == 0)) 
			return true; 
		if ((Math.abs(dy) >= x_Tol) && 
			(Math.abs(yl) >  Math.abs(yr))) {
			final double s=yr/yl; //attempt inverse quadratic Interpolation
			double p,q; 
			final double xm_2 = xm+xm; 
			if (xl == x) {
				p=s*xm_2;
				q=1-s;
			} else { final double r; 
				r=yr/y;
				q=yl/y;
				p=s*(xm_2*q*(q-r)-(xr-xl)*(r-1));
				q=(q-1)*(r-1)*(s-1);
			}
			if (p > 0)  //Check whether in Bounds
				q = -q; 
			else 
				p = -p;
			final double min1=(xm+xm_2)*q-Math.abs(x_Tol*q);
			final double min2=Math.abs(dy*q);
			if ((p+p < (min1 < min2 ? min1 : min2))) { //|| 
			//	((yl > 0) == (yr > 0))) { //same Sign, not bracketed
				dy=dx; //Accept Interpolation
				dx=p/q;
			} else {
				dx=xm; //use Bisection instead
				dy=dx; 
			}
		} else { //Bounds shrink too slowly, use BiSection 
			dx=xm; 
			dy=dx; 
		}
		xl=xr; //last best Guess to xl
		yl=yr; 
		xr += (Math.abs(dx) > x_Tol) //improves convergence! 
			?  dx : (xm > 0) //dx too small 
			?  x_Tol 
			: -x_Tol; 
		return false; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// static Testing & Main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testSelfStart(final IFloatFunction fn) {	//RingFuncs only used for testing!
		L.enter();
		L.n("Searching for the Root of ").l(fn.getClass());
		final float xLeft = 3.1f; //-1.3f; 
		//final float xRight = 3; 
		final BrentFloatRefinerQ refiner = new BrentFloatRefinerQ(xLeft); //
		refiner.xTol = 1e-6; 
		for(int iter = 50; --iter >= 0;) {
			L.n("xl").l(refiner.xl).l("	xr=").l(refiner.xr); 
			if (refiner.finished(fn.Map(refiner.xr)))
				return; 
			Assert.IS_TRUE((refiner.yl >= 0) == (refiner.yr <= 0)); 
		}
		Assert.FAIL("Should have converged!"); 
	}
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//RingFuncs only used for testing!
		testBracketed(); 
		testSelfStart(TEST_FUNCTION); 
		testSelfStart(Sign.Sign); 
	}
	
	/**Method to test all Implementations in this class.	 */
	public static void testBracketed() {	//RingFuncs only used for testing!
		L.enter();
		L.n("Searching for the Root of ").l(TEST_FUNCTION.getClass());
		final float xLeft = -1.3f; 
		final float xRight = 3; 
		final BrentFloatRefinerQ refiner = new BrentFloatRefinerQ(xLeft, xRight, TEST_FUNCTION);
		TEST_REFINER(refiner, TEST_ZERO_POINT, 40);
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
