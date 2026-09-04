/*
 * File Name: NewtonFloatRefinerQ.java Created on: 02.02.2004
 *  
 */
package math.refiner;

import streamIO.Assert;
import streamIO.Log;
import function.IFloatFunction;
import function.byref.ByRefDouble;
import function.derive.IFloatDeriveAble;

/**
 * Title: NewtonFloatRefinerQ
 * <p>
 * Description: O(2) Root Search of with Newton Formula, bracketing and optional
 * BiSection.
 * 
 * Guaranteed linear global Convergence due to BiSection. Works moderately well,
 * also globally and for multiple Zeros, especially when the Multiplicity is
 * known and given (Multiplicity can also act as a Relaxation Parameter!) Works
 * only on R->R Value Functions.
 * 
 * Design Decisions / Implementation Details:
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * similar Classes:
 * 
 * @see math.refiner.FalsiFloatRefinerQ O(1.618... = golden at best)
 * @see math.refiner.RidderFloatRefinerQ O(1.414... = SqRt(2) at best)
 * @see math.refiner.BrentFloatRefinerQ O(2 at best without evaluating the
 *      Derivative )
 * @see math.refiner.NewtonFloatRefinerQ O(2 at best with evaluating the
 *      Derivative )
 * 
 * Copyright: Copyright (c) Matthias Heuer
 * <p>
 * Company: personal
 * <p>
 * Created on 10-26-2002, 12:47 PM
 * <p>
 * @author mheuer
 * @version 1.0
 *  
 */
public class NewtonFloatRefinerQ 
extends BrentFloatRefinerQ // AFloatRefinerQ 
{
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log	L	= new Log(NewtonFloatRefinerQ.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Initialisierung einer neuen Suche */
	public void init(final double xl, final double xr, final IFloatFunction f0,
			final IFloatFunction f1) {
		super.init(xl, xr, f0); //=> f(xl), f(xr), dx and dy calculated
		this.x = xl;
		this.y = yl;
		this.df.Value = f1.Map(x);
		this.f01 = null;
		this.f1 = f1;
	}

	/** Initialisierung einer neuen Suche */
	public void init(final double xl, final double xr,
			final IFloatDeriveAble f01_) {
		super.init(xl, xr, f01_); //=> f(xl), f(xr), dx and dy calculated
		this.x = xl;
		this.y = f01_.getFuncDerive(x, df);
		this.f1 = null;
		this.f01 = f01_;
	}

	/**
	 * @param _xr
	 */
	public NewtonFloatRefinerQ(double _xr) { super(_xr); }
	
	/**
	 * @param _xl
	 * @param _xr
	 * @param _yl
	 * @param _yr
	 */
	public NewtonFloatRefinerQ(double _xl, double _xr, double _yl, double _yr) {
		super(_xl, _xr, _yl, _yr);
	}
	
	/**
	 * @param xl
	 * @param xr
	 * @param f_
	 */
	public NewtonFloatRefinerQ(double xl, double xr, IFloatFunction f_) {
		super(xl, xr, f_);
	}
	
	/** Constructor, ordering xl and xr so that yr is positive */
	public NewtonFloatRefinerQ() {}

	/** Constructor, ordering xl and xr so that yr is positive */
	public NewtonFloatRefinerQ(final double xl, final double xr,
			IFloatFunction f0, IFloatFunction f1) {
		init(xl, xr, f0, f1);
	}

	/** Constructor, ordering xl and xr so that yr is positive */
	public NewtonFloatRefinerQ(final double xl, final double xr,
			IFloatDeriveAble f01_) {
		init(xl, xr, f01_);
	}
	
	/** Parameter for handing back the Derivative */
	protected final ByRefDouble	df			= new ByRefDouble();
	
	/** New x Value */
	private IFloatFunction		f1;
	
	/** New x Value */
	private IFloatDeriveAble	f01;
	
	/** Flag to switch assigning yr and yl... */
	//public boolean assignYlYr = false; 
	
	/**
	 * @see refiner.IFloatRefiner#refine()
	 * Performs a single approximating Step, when the Function is given.
	 * @return x, the best x Value so far...
	 */
	public double refine() { 
		if (f01 != null) {
			return improve(f01.getFuncDerive(x, df), df.Value); //
		} else {
			return improve(f.Map(x), f1.Map(x));
		}
	}
	
	/**
	 * @see refiner.IFloatRefiner#refine()
	 * Performs a single approximating Step.
	 * @return x, the best x Value so far, to be evaluated for the next call.
	 */
	public double improve(final double fnVal, final double df) {
		definitelyFinished(fnVal, df); 
		return x; 
	}
	
	/** 
	 * Performs a single approximating Step, 
	 * including self-starting Bracketing, if necessary.  
	 * 
	 * @param f  Function Value at x 
	 * @param df Derivative at x
	 * @return true, when the desired Accuracy is reached. 
	 */
	public boolean finished(final double f, final double df) {
		final int  finished = definitelyFinished(f, df); 
		if (finished ==  1)
			return true; 
		if (finished == -1)
			return false; 
		return (Math.abs(dx) <= ByRefDouble.MUL_ABS_ACCURACY(x)+xTol); }
	
	/** 
	 * Performs a single approximating Step, 
	 * including self-starting Bracketing, if necessary.  
	 * 
	 * @param f  Function Value at x 
	 * @param df Derivative at x
	 * @return true, when the desired Accuracy is reached. 
	 */
	protected int definitelyFinished(final double f, final double df) { 
		if (!bracketed(y = f)) 
			return -1; 
		if (y < 0) { //if (assignYlYr) //also maintain y
			yl = y; xl = x; 
		} else { //if (assignYlYr)
			yr = y; xr = x;
		}
		
		final double ddx = y / df;
		x -= ddx; //anticipate regular Newton Step
		final double prod = (x - xr) * (x - xl); 
		if ((prod >= 0) || //leads out of the Interval
		//if (((x > xr) == (x > xl)) || //leads out of the Interval
			//(Math.abs(y+y) > Math.abs(dy * df))) { //
			  (Math.abs(ddx + ddx) > Math.abs(dy))) { //use BiSection
			if (prod == 0) { //converged up to Machine Accuracy
				//if (assignYlYr)
				yl = yr = y; 
				xl = xr = x; 
				return 1; 
			}
			dy = dx; dx = (xr-xl) * 0.5; //
			x = xl + dx; //or is slower than BiSection
		} else { //regular Newton Step
			dy = dx; dx = ddx; //misuse dy for dxOld
		}
		return 0; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// static Testing & Main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Method to test all Implementations in this class. */
	public static void testBracketed() { //RingFuncs only used for testing!
		L.enter();
		L.n("Searching for the Solution of y = 0 = ").l(TEST_FUNCTION);
		final NewtonFloatRefinerQ refiner = new NewtonFloatRefinerQ(
				(double) TEST_FIX_POINT, (double) TEST_MIN_POINT,
				(IFloatDeriveAble) TEST_FUNCTION); //
		TEST_REFINER(refiner, TEST_ZERO_POINT, 6); //extremely well-behaved!
	}
	
	/**Method to test all Implementations in this class.	 */
	public static void testSelfStart(final IFloatDeriveAble fn) {	//RingFuncs only used for testing!
		L.enter();
		L.n("Searching for the Root of ").l(fn.getClass());
		final float xLeft = 3.1f; //-1.3f; 
		//final float xRight = 3; 
		final NewtonFloatRefinerQ refiner = new NewtonFloatRefinerQ(xLeft); //
		refiner.xTol = 1e-12; 
		final ByRefDouble dy = new ByRefDouble(); 
		for(int iter = 125; --iter >= 0;) {
			L.n("xl").l(refiner.xl).l("	xr=").l(refiner.xr); 
			if (refiner.finished(fn.getFuncDerive(refiner.xr, dy), dy.Value))
				return; 
		}
		Assert.FAIL("Should have converged!"); 
	}
	
	/** Method to test all Implementations in this class. */
	public static void testIt() { //RingFuncs only used for testing!
		testBracketed(); 
		testSelfStart((IFloatDeriveAble)TEST_FUNCTION); 
	}
	
	/** Main Method to be called from the Command Line */
	public static void main(final String[] args) throws Exception {
		testIt();
	}

}