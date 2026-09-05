/*
 * File Name: Brent1FloatMinimizer.java
 * Created on: 02.02.2004
 *
 */
package math.minimizer;

import streamIO.Log;
import function.IFloatFunction;
import function.byref.ByRefDouble;
import function.derive.IFloatDeriveAble;

/**
 * Finds the minimum of a one-dimensional function using derivative information (10.3),
 * by Brent's algorithm for one-dimensional minimization (Chapter 10.2).
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see math.refiner.BrentFloatMinimizer which doesn't need the Derivative explicitly. 
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:46:32Z
 * digest: 147efbca1860708a6519f91ec5f791290906baad57993cfd2c28ccbf4b4362e8
 * stale: false
 * tags: [code/minimum_search, code/derivative_calculation, code/optimization]
 * concepts: [Brent's Method Minimizer]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Brent1FloatMinimizer 
extends AFloatMinimizer {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(Brent1FloatMinimizer.class, 0);
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	double yTry; 
	double xTry;
	double dTst;
	double dTry;
	double xd=0; 
	double xe=0;

	/** Empty Constructor 	 */
	public Brent1FloatMinimizer() {}
	
	/**Initializer 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 * @param f1_ Function returning the Derivative 
	 * @param tol Tolerance for the Root to find
	 */
	public void init(final double xl_, final double x_, final double xr_
	, final IFloatFunction f_, final IFloatFunction f1_, final double tol) {
		super.init(xl_, x_, xr_, f_); 
		f01 = null; 
		f1 = f1_; 
		xTry=xTst=xMid; 
		yTry=yTst=yMid; 
		dTry=dTst=dx=f1.Map(xMid); 
		this.tolerance = tol; 
	}

	/**initializing Constructor 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f01_ Function returning the Value and the Derivative in one Step 
	 * @param tol Tolerance for the Root to find
	 */
	public Brent1FloatMinimizer(final double xl_, final double x_, final double xr_, final IFloatDeriveAble f01_, final double tol) {
		init(xl_, x_, xr_, f01_, tol); }

	/**initializing Constructor 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f01_ Function returning the Value and the Derivative in one Step 
	 * @param tol Tolerance for the Root to find
	 */
	public void init(final double xl_, final double x_, final double xr_, final IFloatDeriveAble f01_, final double tol) {
		super.init(xl_, x_, xr_, f01_);
		f01 = f01_;
		f1 = null; 
		xTry=xTst=xMid;
		yTry=yTst=yMid=f01.getFuncDerive(xMid, ref);
		dTry=dTst=dx=ref.Value;
		this.tolerance = tol; 
	}

	/**Initializer 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 * @param f1_ Function returning the Derivative 
	 * @param tol Tolerance for the Root to find
	 */
	public void init(final double xl_, final double x_, final IFloatDeriveAble f01_, final double tol) {
		super.init(xl_, x_, f01_);
		f01 = f01_;
		f1 = null; 
		xTry=xTst=xMid;
		yTry=yTst=yMid=f01.getFuncDerive(xMid, ref);
		dTry=dTst=dx=ref.Value;
		this.tolerance = tol; 
	}
	
	/**initializing Constructor 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 * @param f1_ Function returning the Derivative 
	 * @param tol Tolerance for the Root to find
	 */
	public Brent1FloatMinimizer(final double xl_, final double x_, final double xr_, final IFloatFunction f_, final IFloatFunction f1_, final double tol) {
		init(xl_, x_, xr_, f_, f1_, tol); 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Function returning both the Value and the Derivative	 */
	IFloatDeriveAble f01; 

	/** Function returning the Derivative	 */
	IFloatFunction f1; 

	/** ByRef Helper Object for returning the Derivative	 */
	final ByRefDouble ref = new ByRefDouble(); 
	
	/** The Tolerance for determining Zeros 	 */
	double tolerance = ByRefDouble.DOUBLE_FULL_ACCURACY; 
	
	/**	
	 * find minimum of a function using derivative information (10.3)		
	 * Brent's Algorithm to calculate the Minimum in one Dimension, Chapter 10.2
	 * 
	 * @return the best Ordinate for the Minimum so far 
	 */
	public double refine() {
		return refine(tolerance); 
	}

	/**	
	 * find minimum of a function using derivative information (10.3)		
	 * Brent's Algorithm to calculate the Minimum in one Dimension, Chapter 10.2
	 * @param tol The Tolerance to use 
	 * @return the best Ordinate so far 
	 */
	public double refine(final double tol) {
		//L.n(" x=").l( t).l("  v=").l( v).l("  w=").l( w)
		// .n("fx=").l(ft).l(" yTst=").l(yTst).l(" fw=").l(fw);
		//final double xm=0.5*(xl+xr);
		final double tol1=tol*Math.abs(xMid)+1e-16;
		//final double tol2=tol1+tol1;
		
		/////////////////////////////////////////////////////////////
		// determine a new Trial Point
		//if (Math.abs(xMid-xm) <= (tol2-0.5*(xr-xl))) {
		//	return xMid; } //also stop the Loop then!
		double xNew;
		if (Math.abs(xe) <= tol1) {
			xd=0.5*(xe=(dx >= 0 ? xl-xMid : xr-xMid));
		} else {
			double d1=2*(xr-xl); //initialize these d's...
			double d2=d1; //to out of Bracket Values
			if (dTry != dx) d1=(xTry-xMid)*dx/(dx-dTry); //Secant with one Point 
			if (dTst != dx) d2=(xTst-xMid)*dx/(dx-dTst); //Secant with the other Point
			final double u1=xMid+d1;
			final double u2=xMid+d2;
			//Which Estimate to take is required whether they are...
			final boolean ok1 = (xl-u1)*(u1-xr) > 0 && dx*d1 <= 0; //...within the Bracket...
			final boolean ok2 = (xl-u2)*(u2-xr) > 0 && dx*d2 <= 0; //...AND on the Side where the Derivative points to. 
			final double olde=xe;
			xe=xd;
			if (ok1 || ok2) {
				if (ok1 && ok2) { //if both are acceptible, take the smaller
					xd=(Math.abs(d1) < Math.abs(d2) ? d1 : d2);
				} else if (ok1) {
					xd=d1;
				} else {
					xd=d2;
				} 
				if (Math.abs(xd) <= Math.abs(0.5*olde)) {
					xNew=xMid+xd;
					/*
					if ((xNew-xl < tol2) || (xr-xNew < tol2))
						xd=ByRefDouble.assignSign(tol1, xm-xMid);
					*/
				} else { //BiSection, not Golden Section
					xd=0.5*(xe=(dx >= 0 ? xl-xMid : xr-xMid));
				}
			} else {
				xd=0.5*(xe=(dx >= 0 ? xl-xMid : xr-xMid));
			}
		}
		
		/////////////////////////////////////////////////////////////
		//make a significant Change! 

		final double du, yNew; 
		//if (Math.abs(xd) >= tol1) {
			xNew=xMid+xd;
			if (f01 != null) {
				yNew=f01.getFuncDerive(xNew, ref);
				du = ref.Value; 
			} else {
				yNew=f.Map(xNew);
				du = f1.Map(xNew); 
			}
		/*} else {
			xNew=xMid+ByRefDouble.assignSign(tol1, xd);
			if (f01 != null) {
				yNew=f01.getFuncDerive(xNew, ref);
				du = ref.Value; 
			} else {
				yNew=f.Map(xNew);
				du = f1.Map(xNew); 
			}
			if (yNew > yMid) { //If the minimum Step downhill leads uphill...
				return xMid; } //...we're done. 
		}*/
		if (yNew <= yMid) { //xNew is better: use xNew for xMid AND xMid as Border
			if (xNew > xMid) xl=xMid; else xr=xMid; //TODO: pathological Case to hit the Minimum exactly
			xTst=xTry; xTry=xMid; xMid=xNew;   
			yTst=yTry; yTry=yMid; yMid=yNew;   
			dTst=dTry; dTry=dx  ; dx  =du;
		} else { //xMid is better than xNew 
			if (xNew < xMid) xl=xNew; else xr=xNew;
			if (yNew <= yTry || xTry == xMid) {
				xTst=xTry; yTst=yTry; dTst=dTry; 
				xTry=xNew; yTry=yNew; dTry=du; 
			} else if (yNew < yTst || xTst == xMid || xTst == xTry) {
				xTst=xNew; yTst=yNew; dTst=du; 
			}
		}
		return xNew;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//Testing single Step of Pegasus Step with Quality Control
		L.n("Testing ").l(Brent1FloatMinimizer.class);
		L.n("Searching for the Minimum of y = ").l(TEST_FUNCTION);
		final double xLeft  = 0;
		final double xMid   = 3;
		final double xRight = 6;
		final Brent1FloatMinimizer minStep = new Brent1FloatMinimizer(xLeft, xMid, xRight, (IFloatDeriveAble) TEST_FUNCTION, 1e-16);
		TEST_REFINER(minStep, TEST_MIN_POINT, 7);
		L.n("xMin=").l(minStep.xMid).l("yMin=").l(minStep.yMid); 
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
