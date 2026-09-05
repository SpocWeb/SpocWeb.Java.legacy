/*
 * File Name: InterpolDouble.java
 * Created on: 21.01.2004
 *
 */
package math;

import math.vector.VectorDouble;
import streamIO.Assert;
import streamIO.Log;
import function.IMeasurAble;
import function.byref.ByRefDouble;

/**
 * Implements a polynomial Interpolator that interpolates a Function dependent on a
 * one-dimensional Variable, and also provides static Methods for rational-function and
 * bicubic-spline Interpolation in 2 Dimensions.
 *
 * <p>Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Similar Classes:
 * @see streamIO.copy.group.ring.Interpolator
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:45:36Z
 * digest: 5a5dfc775c4f45cb26a56a0eb5939dc37de4d638c4f9af6ba5d85279a12be4e0
 * stale: false
 * tags: [code/interpolation]
 * concepts: [Double Interpolation]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class InterpolDouble {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(InterpolDouble.class, 0);

	/////////////////////////////////////////////////////////////////////////////////////
	// Bicubic Splines to fit 2D Planes into 3D Space
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static final byte[][] BI_CUBIC_WEIGHTS 
	= { { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
		{-3, 0, 0, 3, 0, 0, 0, 0,-2, 0, 0,-1, 0, 0, 0, 0},
		{ 2, 0, 0,-2, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0},
		{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
		{ 0, 0, 0, 0,-3, 0, 0, 3, 0, 0, 0, 0,-2, 0, 0,-1},
		{ 0, 0, 0, 0, 2, 0, 0,-2, 0, 0, 0, 0, 1, 0, 0, 1},
		{-3, 3, 0, 0,-2,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{ 0, 0, 0, 0, 0, 0, 0, 0,-3, 3, 0, 0,-2,-1, 0, 0},
		{ 9,-9, 9,-9, 6, 3,-3,-6, 6,-6,-3, 3, 4, 2, 1, 2},
		{-6, 6,-6, 6,-4,-2, 2, 4,-3, 3, 3,-3,-2,-1,-1,-2},
		{ 2,-2, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{ 0, 0, 0, 0, 0, 0, 0, 0, 2,-2, 0, 0, 1, 1, 0, 0},
		{-6, 6,-6, 6,-3,-3, 3, 3,-4, 4, 2,-2,-2,-2,-1,-1},
		{ 4,-4, 4,-4, 2, 2,-2,-2, 2,-2,-2, 2, 1, 1, 1, 1}
	};

	/**	construct two-dimensional bicubic Spline (3.6)
	 * between the four Points of a Square given
	 * @param y0 Values of y 
	 * @param y1 Values of dy/dx1
	 * @param y2 Values of dy/dx2
	 * @param y12 Values of ddy/dx1dx2
	 * @param d1 
	 * @param d2
	 * @param c
	 */
	final static public void BI_CUBIC_SPLINE_COEFF(double[] y0, double[] y1, double[] y2, double[] y12, double d1, double d2, double[][] c) {
		final double[] x = new double[16]; 
		final double d1d2=d1*d2;
		for (int i=0; i<4; i++) { //to treat it with the general Matrix Scheme
			x[i]=y0[i];
			x[i+4]=y1[i]*d1;
			x[i+8]=y2[i]*d2;
			x[i+12]=y12[i]*d1d2;
		}
		int l=0;
		for (int i=0; i<4; i++){
			for (int j=0; j<4; j++) {
				double sum=0;
				final byte[] weighti = BI_CUBIC_WEIGHTS[l++]; 
				for (int k=0; k<=15; k++) { //quite ineffective! lots of 0s!
					if (weighti[k] == 0) {
						continue; } //save some inefficiencies
					sum += weighti[k]*x[k]; } 
				c[i][j]=sum; //
			} 
		}
	}

	/**	two-dimensional bicubic interpolation (3.6)	
	 * between the four Points of a Square given
	 * 
	 * @param y Values of y 
	 * @param y1 Values of dy/dx1
	 * @param y2 Values of dy/dx2
	 * @param y12 Values of ddy/dx1dx2
	 * @param x1l lower x1-Value
	 * @param x1u upper x1-Value
	 * @param x2l lower x2-Value
	 * @param x2u upper x2-Value
	 * @param x1 desired x1-Value
	 * @param x2 desired x1-Value
	 * @param ansy Values for y, dy/dx1, dy/dx2
	 */
	final static public void BI_CUBIC_INTERPOL(final double[] y, final double[] y1, final double[] y2, final double[] y12, final double x1l,
	final double x1u, final double x2l, final double x2u, final double x1, final double x2, final double[] ansy) {
		final double[][] c =new double [4][4];
		final double d1=x1u-x1l;
		final double d2=x2u-x2l;
		BI_CUBIC_SPLINE_COEFF(y,y1,y2,y12,d1,d2,c);
		if ((x1u == x1l) || (x2u == x2l)) {
			throw new RuntimeException("The Square has no Extent!");} 
		final double t=(x1-x1l)/d1;
		final double u=(x2-x2l)/d2;
		ansy[0]=ansy[1]=ansy[2]=0;
		for (int i=4; --i>=0; ) {
			ansy[0]=t*ansy[0]+( (c[i][3]*u+  c[i][2])*u+c[i][1])*u+c[i][0];
			ansy[1]=u*ansy[1]+(3*c[3][i]*t+2*c[2][i])*t+c[1][i];
			ansy[2]=t*ansy[2]+(3*c[i][3]*u+2*c[i][2])*u+c[i][1];
		}
		ansy[1]/=d1;
		ansy[2]/=d2;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Polynom Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Polynom Interpolation, Chapter 3.1	
	 * O(n^2) very ineffective because it calculates all Differences for each Interpolation. 
	 * Use it only for a single Interpolation! 
	 * @param xa x-Array
	 * @param ya y-Array 
	 * @param start the Begin (inclusive) of the Values to use for Interpolation 
	 * @param stop  the End   (exclusive) of the Values to use for Interpolation 
	 * @param x the Position to evaluate the Polynom at
	 * @return y the Value inter/extrapolated 
	 * @param error if not null, filled with the estimated Error from the last Correction.  
	 * (assuming the Corrections drop significantly, i.e. the Function is "smooth"!)  
	 */
	final static public double INTERPOL_POLYNOM(final double[] xa, final double[] ya, final int start, final int stop, final double x, final double[] error) {
		if ((stop > xa.length) || (stop > ya.length)) {
			throw new RuntimeException("The Dimensions are not sufficient:"+xa.length+" and "+ya.length); }
		final double[] c=new double[stop]; 
		final double[] d=new double[stop]; 
		int minNdx = FIND_CLOSEST(xa, ya, start, stop, x, c, d);
		double y=ya[minNdx--];
		double dy = 0; 
		for (int m=1; m < stop-start; m++) {
			for (int i=start; i < stop-m; i++) {
				final double ho = xa[i]-x;
				final double hp = xa[i+m]-x;
				final double w = c[i+1]-d[i];
				double den=ho-hp;  
				//if (den == 0) {
				//	throw new RuntimeException("Error in routine polint");} 
				den=w/den;
				d[i]=hp*den;
				c[i]=ho*den;
			}
			y += (dy=(2*minNdx < (stop-1-m) ? c[minNdx+1] : d[minNdx--]));
		}
		SET_VALUE(error, dy);
		return y; 
	}

	/** Polynom Interpolation, Chapter 3.1	
	 * very ineffective because it calculates all Differences for each Interpolation. 
	 * Use it only for a single Interpolation! 
	 * @param xa x-Array
	 * @param ya y-Array 
	 * @param x
	 * @param error if not null, filled with the estimated Error.  
	 * @return y the Value inter/extrapolated 
	 */
	final static public double INTERPOL_POLYNOM(final double[] xa, final double[] ya, final double x, final double[] error) {
		return INTERPOL_POLYNOM(xa, ya, 0, xa.length, x, error); }

	/////////////////////////////////////////////////////////////////////////////////////
	// Rational Function Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**	Rational Function Interpolation, Chapter 3.3
	 * very ineffective because it calculates all Differences for each Interpolation. 
	 * Use it only for a single Interpolation! 
	 * @param xa x-Array
	 * @param ya y-Array 
	 * @param start the Begin (inclusive) of the Values to use for Interpolation 
	 * @param stop  the End   (exclusive) of the Values to use for Interpolation 
	 * @param x the Position to evaluate the Polynom at
	 * @param error if not null, filled with the estimated Error.  
	 * @return y the Value inter/extrapolated 
	 */ 
	final static public double INTERPOL_RATIONAL(final double xa[], final double ya[], final int start, final int stop, final double x, final double[] error) {
		int m,i;
		final double[] c=new double[stop];
		final double[] d=new double[stop];
		int minNdx = FIND_CLOSEST(xa, ya, start, stop, x, c, d);
		double y=ya[minNdx];
		if (xa[minNdx] == x) { //prevent NaNs
			SET_VALUE(error, 0);
			return y; }
		minNdx--;
		double dy = 0;
		for (m=1; m<stop-start; m++) { 
			for (i=start; i< stop-m; i++) {
				final double w=c[i+1]-d[i];
				final double h=xa[i+m]-x;
				final double t=(xa[i]-x)*d[i]/h;
				double dd=t-c[i+1];
				//if (dd == 0) { //otherwise NaN, prevented by a first Test
				//	throw new RuntimeException("Error in routine ratint"); } 
				dd=w/dd;
				d[i]=c[i+1]*dd;
				c[i]=t*dd;
			}
			y += (dy=(2*minNdx < (stop-1-m) ? c[minNdx+1] : d[minNdx--]));
		}
		SET_VALUE(error, dy);
		return y; 
	}

	/** finds the Index of the closest Point to x (by linear Search)
	 * and copies ya into the Arrays c and d, since you have to sweep full anyway... 
	 * @return the Index of the closest Point to x
	 */ 
	private static int FIND_CLOSEST(
		final double[] xa,
		final double[] ya,
		final int start,
		final int stop,
		final double x,
		final double[] c,
		final double[] d) {
		int minNdx = start; 
		double minDiff = Math.abs(x-xa[minNdx]); 
		for (int i=start; i < stop; i++) { 
			final double dift = Math.abs(x-xa[i]);  
			if (minDiff > dift) {
				minDiff=dift; minNdx=i; }
			if (ya== null) { continue; }
			if (c != null) { c[i]=ya[i]; } 
			if (d != null) { d[i]=ya[i]; } 
		}
		return minNdx;
	}

	/**	Rational Function Interpolation, Chapter 3.3
	 * very ineffective because it calculates all Differences for each Interpolation. 
	 * Use it only for a single Interpolation! 
	 * @param xa x-Array
	 * @param ya y-Array 
	 * @param start the Begin (inclusive) of the Values to use for Interpolation 
	 * @param stop  the End   (exclusive) of the Values to use for Interpolation 
	 * @param x the Position to evaluate the Polynom at
	 * @param error if not null, filled with the estimated Error.  
	 * @return y the Value inter/extrapolated 
	 */ 
	final static public double INTERPOL_RATIONAL(final double[] xa, final double[] ya, final double x, final double[] error) {
		return INTERPOL_RATIONAL(xa, ya, 0, xa.length, x, error); }

	private static final void SET_VALUE(final double[] error, final double dy) {
		if ((error != null) && (error.length > 0)) {
			error[0] = Math.abs(dy); } 
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Degree of the Polynom 	 */
	private int mDim = -1;

	/** x-Positions of the Samples */
	private double[] x;

	/** y-Positions of the Samples as divided Differences */
	private double[] t;

	/** Coefficients of the Interpolation Polynom	 */
	private double[] a;

	private void allocate(int n) {
		this.t = new double[n];
		this.x = new double[n];
		this.a = new double[n];
	}

	/**Empty Constructor building an empty Interpolation Polynom of Length 7.	 */
	public InterpolDouble()	{ allocate (7); }

	/**Constructor building an empty Interpolation Polynom of the given Length.	 */
	public InterpolDouble(final int length)	{ allocate (length); }

	/**Constructor building the Interpolation Polynom
	 * from the Samples given in x and y up to the length n.	 */
	public InterpolDouble(final double[] x_, final double[] y_) {
		this (x_, y_, Math.min(x_.length, y_.length));}

	/**Constructor building the Interpolation Polynom
	 * from the Samples given in x and y up to the length n.	 */
	public InterpolDouble(final double[] x_, final double[] y_, int n) {
		if (n > y_.length) n = y_.length;
		if (n > x_.length) n = x_.length;
		allocate (n);
		int i = -1;	//i == mDim!
		while (++i < n) { 
			addPoint(x_[i], y_[i]);} 
	}

	/**Add another Sample Point to the Interpolation Polynom.	 */
	public void addPoint(final double x_, final double y_) {
		int k = ++mDim;
		if (k >= x.length) {	//redimension the Arrays
			int n2 = x.length << 1;	//double the Size
			t = VectorDouble.SET_CAPACITY(n2, t);
			x = VectorDouble.SET_CAPACITY(n2, x);
			a = VectorDouble.SET_CAPACITY(n2, a);
		}

		final double x_i = x[mDim] = x_;
		double t_i = t [mDim] = y_;
		while (--k >= 0) { 
			t_i = t[k] = (t[k] - t_i)/(x[k] - x_i); }
		a[mDim]= t[0];
	}

	/**Value of this Interpolation Polynom at Position arg	 */
	public double Map(final double arg) { return horner (arg); }

	/** evaluates the Interpolation at the given Position using the Horner Scheme */
	public double horner(final double arg) {
		int i = mDim;
		double ret = (double) a[i];
		while (--i >= 0) {
			ret = ret*(arg - x[i]) + a[i]; } 
		return ret; }


	/////////////////////////////////////////////////////////////////////////////////////
	//	Testing	
	/////////////////////////////////////////////////////////////////////////////////////

	/**Method to test all Implementations in this class.	 */
	private static void testInterpolDouble() {	//So far you can test only polynomial and rational Functions
		//Polynoms are interpolated exactly
		L.n("Testing InterpolDouble:");
		L.n("Interpolating the following Polynom to x = 0");
		double[] x1 = {0, 1, 3};
		double[] y1 = {1, 3, 2};
		//The Numbers are derived from ???
//		double[] y2 = {57.28996163, 28.63625328, 19.08113669, 14.30066626, 11.43005230};
//		double[] x2 = {-1.5, -0.5, 0.5, 1.5, 2.5};

		InterpolDouble interpol =  new InterpolDouble(3);
		L.n("X = (" + streamIO.AStreamOut.ARRAY_TO_STRING(x1, ",") + ")");
		L.n("Y = (" + streamIO.AStreamOut.ARRAY_TO_STRING(y1, ",") + ")");
		L.n("Value of the Interpolating Polynom at the Seeding Points: ");
		//Add all the points to the Polygon
		for (int i = x1.length; --i >= 0; ) {
			double x = x1[i];
			double y = y1[i];
			interpol.addPoint(x,y);
			L.n("x=" + x + " ; y=" + interpol.Map(x));
			Assert.EQUALS(y, interpol.Map(x)); 
		}
		//Test, if the old values are still interpolated
		for (int i = x1.length; --i >= 0; ) {
			double x = x1[i];
			L.n("x=" + x + " ; y=" + interpol.Map(x));
			Assert.EQUALS(interpol.Map(x), y1[i]); 
		}

		//rational Functions not at all...
	}

	/** tests ad hoc polynomial Interpolation 	*/
	private static final void testPolynomInt() {
		L.n("generation of interpolation tables");
		L.n(" ... sin(x)    0<x<PI");
		L.n(" ... exp(x)    0<x<1 ");
		L.n("how many entries go in these tables?");
		final int n = 10; //if (scanf("%d",&n) == EOF) return 1;
		final double[] xa = new double[1+n]; 
		final double[] ya = new double[1+n]; 
		for (int nfunc=1; nfunc<=2; nfunc++) {
			if (nfunc == 1) {
				L.n("sine function from 0 to PI");
				for (int i=0; i<=n; i++) {
					xa[i]=i*IMeasurAble.PI/n;
					ya[i]=Math.sin(xa[i]);
				}
			} else {
				L.n("exponential function from 0 to 1");
				for (int i=0; i<=n; i++) {
					xa[i]=i*1.0/n;
					ya[i]=Math.exp(xa[i]);
				}
			}
			final double[] dy = new double[1];
			L.n("x").l("f(x)").l("interpolated").l("error");
			for (int i=1; i<=10; i++) {
				final double f,x,y;
				if (nfunc == 1) {
					x=(-0.05+i/10.0)*IMeasurAble.PI;
					f=Math.sin(x);
				} else {
					x=(-0.05+i/10.0);
					f=Math.exp(x); 
				}
				y = INTERPOL_POLYNOM(xa,ya,0,xa.length,x,dy);
				L.n().l(x).l(f).l(y).l(dy);
				Assert.EQUALS(f, y, 0, 2*dy[0]);
			}
			L.n("\n***********************************\n");
			L.n("press RETURN\n");
		}
	}
	
	private static final double f(final double x, final double eps) { 
		return x*Math.exp(-x)/(ByRefDouble.SQR(x-1)+eps*eps); }

	/** tests ad hoc rational Interpolation 	*/
	private static final void testRationalInt() {
		final int NPT = 6;
		final double EPS = 1;
	
		final double[] x= new double[1+NPT];
		final double[] y= new double[1+NPT];
		for (int i=0; i<=NPT; i++) {
			x[i]=i*2.0/NPT;
			y[i]=f(x[i],EPS);
		}
		L.n("\nDiagonal rational function interpolation\n");
		L.n("x").l("\tinterp.").l("\taccuracy").l("\tactual");
		final double[] dy = new double[1];
		for (int i=1; i<=10; i++) {
			final double xx=0.2*i;
			final double yy = INTERPOL_RATIONAL(x,y,xx,dy);
			final double yExp=f(xx,EPS);
			L.n().l(xx).l(yy).l(dy[0]).l(yExp);
			Assert.EQUALS(yExp, yy, 0, 2*dy[0]);
		}
	}

	private static final void testBiCubicSpline() {
		int i,j;
		double d1,d2,ee,x1x2;
		final double[] y00 = new double[4]; 
		final double[] y01 = new double[4];
		final double[] y02 = new double[4];
		final double[] y12 = new double[4]; 
		final double[][] c=new double [4][4];
		final double[] x1 = {0, 2, 2, 0};
		final double[] x2 = {0, 0, 2, 2};

		d1=x1[1]-x1[0];
		d2=x2[3]-x2[0];
		for (i=0; i<4; i++) {
			x1x2=x1[i]*x2[i];
			ee=Math.exp(-x1x2);
			y00[i]=x1x2*ee;
			y01[i]=x2[i]*(1-x1x2)*ee;
			y02[i]=x1[i]*(1-x1x2)*ee;
			y12[i]=(1-3*x1x2+x1x2*x1x2)*ee;
		}
		BI_CUBIC_SPLINE_COEFF(y00,y01,y02,y12,d1,d2,c);
		L.n("\nCoefficients for bicubic interpolation:\n\n");
		for (i=0; i<4; i++) {
			for (j=0; j<4; j++) {
				L.l(c[i][j]);} 
			L.n();
		}
	}

	private static final void testBiCubicSplineInterpolation() {
		final double[] ansy = new double[3];
		double ey0,ey1,ey2;
		double x1,x1l,x1u,x1x2,x2,x2l,x2u,xxyy;
		final double[] y = new double[4]; 
		final double[] y1 = new double[4];
		final double[] y2 = new double[4];
		final double[] y12 = new double[4]; 
		final double xx[]={0,2,2,0}; //4 Corners of a Square
		final double yy[]={0,0,2,2};

		x1l=xx[0];
		x1u=xx[1];
		x2l=yy[0];
		x2u=yy[3];
		for (int i=0; i<4; i++) { 
			xxyy=xx[i]*yy[i];
			y[i]=xxyy*xxyy; //f(x,y)=x�y�
			y1[i]=2*yy[i]*xxyy; //df/dx=2*y(xy)
			y2[i]=2*xx[i]*xxyy; //df/dy=2*x(xy)
			y12[i]=4*xxyy; //d�f/dxdy=4*x*y
		}
		L.n("x1 \t x2 \t y \t expect \t y1 \t expect \t y2 \t expect");
		for (int i=1;i<=10;i++) {
			x2 = x1 = 0.2*i; //go along the Diagonal
			BI_CUBIC_INTERPOL(y,y1,y2,y12,x1l,x1u,x2l,x2u,x1,x2,ansy);
			x1x2=x1*x2;
			ey0=x1x2*x1x2;
			ey1=2*x2*x1x2;
			ey2=2*x1*x1x2;
			L.n().l(x1).l(x2).l(ansy[0]).l(ey0).l(ansy[1]).l(ey1).l(ansy[2]).l(ey2);
			Assert.EQUALS(ansy[0], ey0);
			Assert.EQUALS(ansy[1], ey1);
			Assert.EQUALS(ansy[2], ey2);
		}
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		L.n("Testing " + InterpolDouble.class.getName());
		testBiCubicSplineInterpolation();
		testBiCubicSpline(); 
		testInterpolDouble(); 
		testRationalInt(); 
		testPolynomInt(); 
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt(args);
	}

}
