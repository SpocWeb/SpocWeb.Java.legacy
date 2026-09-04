/*
 * File Name: ConjuGrad1Minimizer.java
 * Created on: 25.04.2004
 *
 */
package math.minimizer;

import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.Log;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.vector.AFloatVectorField;
import function.vector.IFloatScalarField;
import function.vector.IFloatVectorField;

/**
 * Title: ConjuGrad1Minimizer<p>
 * Description:
 * TODO: this Function doesn't work properly (yet) 
 * Implements a Conjugate Gradient Method which uses the 1st Derivative (Gradient). 
 * Actually also this Algorithm is easily tricked into local Minima. 
 * More FailSafe Algorithms are implemented using the Annealing Family: 
 * Annealing, Threshold and Flood Algorithm. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see math.minimizer.ConjuGradMinimizer which minimizes a Function not using the Derivative. 
 * @see math.minimizer.AnnealingMinimizer which minimizes a continuous Function NOT using the Gradient. 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class ConjuGrad1Minimizer 
extends AFloatDeriveAble {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(ConjuGrad1Minimizer.class, -1);
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Scalar Field to minimize	 */
	final IFloatScalarField scalarField;
	
	/** Reference to the Gradient Vector Field to minimize	 */
	final IFloatVectorField vectorField;
	
	final int ncom;
	final double[] rayStart;
	final double[] rayDir;
	final double[] ray;
	
	/** cached Working Variables to pass to the VectorField	 */
	final double[] xt;
	
	/** cached Working Variables to retrieve the Gradient from the VectorField	 */
	final double[] df;
	
	/** Reference to the Minimizer to use */
	final Brent1FloatMinimizer minimizer = new Brent1FloatMinimizer();
	
	/**
	 * 
	 */
	public ConjuGrad1Minimizer(final int dim, final IFloatScalarField scalarField_, final IFloatVectorField vectorField_) {
		this.scalarField=scalarField_;
		this.vectorField=vectorField_;
		this.ncom=dim;
		this.rayStart =new double[1+dim];
		this.rayDir=new double[1+dim];
		this.ray=new double[1+dim];
		this.xt=new double[1+dim];
		this.df=new double[1+dim];
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	static final int ITMAX = 200; 
	static final double EPS = 1e-10;

	/*	FRPRMN  minimize in N-dimensions by conjugate gradient (10.6)
		Conjugate Gradient Methods in R^n, Chapter 10.6	*/
	double frprmn(final double p[], final int n, final double ftol, final int iter) {
		double gg,gam,fp,dgg, fret;

		final double[] g=new double[1+n];
		final double[] h=new double[1+n];
		fp=scalarField.Map(p);
		vectorField.map(p,ray);
		for (int j=1;j<=n;j++) {
			g[j] = -ray[j];
			ray[j]=h[j]=g[j];
		}
		this.numIter = iter;
		for (;;) {
			fret=minimizeAlongRay(p,ray,n,ftol);
			if (ByRefDouble.EQUALS(fret, fp, ftol, ByRefDouble.DOUBLE_ACCURACY)) {
				return fret; }
			fp=scalarField.Map(p);
			vectorField.map(p,ray);
			dgg=gg=0;
			for (int j=1;j<=n;j++) {
				gg += g[j]*g[j];
				dgg += (ray[j]+g[j])*ray[j]; //improved Polak-Ribiere
				//dgg += ByRefDouble.SQR(ray[j]); //Fletcher-Reeves
			}
			if (gg == 0) { //Gradient = 0
				return fret; } //unlikely though...
			gam=dgg/gg;
			for (int j=1;j<=n;j++) {
				g[j] = -ray[j];
				ray[j]=h[j]=g[j]+gam*h[j];
			}
		}
		//throw new RuntimeException("Too many iterations in frprmn");
	}
	
	/** the current Number of Iterations left 
	 * shared between the Minimizer Method Calls, 
	 * so it counts the actual Number of Function Evaluations, 
	 * not the Number of linear Minimizations. 
	 * This reflects more the actual Runtime. 
	 * On the other Hand, you could count the Evaluations in the Function directly
	 * and throw the Exception there. 
	 * Can also be set to a negative Value to break Iteration externally. 
	 */	
	public int numIter;
	
	/*	LINMIN  minimum of a function along a ray in N-dimensions (10.5)
		linear Minimization along a straight Line, Chapter 10.6	*/
	double minimizeAlongRay(double p[], double xi[], int n, final double tol) {
		for (int j=1;j<=n;j++) {
			rayStart[j]=p[j];
			rayDir[j]=xi[j];
		}
		minimizer.init(0, 1, this, tol); 
		numIter = minimizer.solve(numIter, tol, true); //count total Number of Evaluations
		final double xMin=minimizer.xMid;
		final double yMin=minimizer.yMid;
		L.n("Iterations left:",-1).l(numIter,-1).l("xMin=",-1).l(xMin,-1);
		//mnbrak(&ax,&xx,&bx,&fa,&fx,&fb,f1dim);
		//final double fret=dbrent(ax,xx,bx,f1dim,df1dim,TOL,xmin);
		for (int j=1;j<=n;j++) {
			xi[j] *= xMin;
			p[j] += xi[j];
		}
		return yMin;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Implementation of IFloatDeriveAble Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @see function.derive.AFloatDeriveAble#Map(double)	 */
	public double Map(final double x) {
		VectorDouble.ADD_PROD(ray, rayStart, x, rayDir); 
		return scalarField.Map(ray);
	}

	/** @see function.derive.AFloatDeriveAble#getDerivative(double)	 */
	public double getDerivative(final double x) {
		for (int j=1;j<=ncom;j++) { //TODO reuse 'ray' here and merge Map() and getDerivative()
			xt[j]=rayStart[j]+x*rayDir[j]; } 
		vectorField.map(xt, df);
		double df1=0;
		for (int j=1;j<=ncom;j++) {
			df1 += df[j]*rayDir[j]; } 
		return df1;
	}

	/** @see function.derive.AFloatDeriveAble#getFuncDerive(double, function.byref.ByRefDouble)	 */
	public double getFuncDerive(final double x, final ByRefDouble derivative) {
		derivative.Value = getDerivative(x); // TODO Auto-generated method stub
		return Map(x);
	}

	/** @see function.IFunction#Map(java.lang.Object)	 */
	public Object Map(final Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Position of the Minimum of a sample Quadratic Form	 */
	static final double[] minPos = {0,1,1,1};
	
	/** Tests Minimization along a Ray	 */	
	final static public void testLinMin() {
		L.n("Testing Minimization along a Ray");
		L.n("Minimum of a 3-d quadratic centered at").l(minPos);
		L.n("Minimum is found along a series of radials.\n");
		L.n().l("\tx").l("\ty").l("\tz").l("\tminimum");
		final double[] xi=new double[minPos.length];
		final DistSqr func = new DistSqr(minPos); 
		final ConjuGrad1Minimizer minimizer = new ConjuGrad1Minimizer(xi.length, func, func);
		int iMin = -1; 
		double fMin = Double.POSITIVE_INFINITY; 
		double[] pMin = null; 
		for (int i=0;i<=10;i++) {
			final double x=IMeasurAble.PI_HALF*i/10;
			final double sr2=Math.sqrt(2);
			xi[1]=sr2*Math.cos(x);
			xi[2]=sr2*Math.sin(x);
			xi[3]=1;
			final double[] p =new double[xi.length];
			minimizer.numIter = 140; //reset it every time 
			final double fRet = minimizer.minimizeAlongRay(p, xi, xi.length-1, 1e-4); 
			if (fMin > fRet) {
				fMin = fRet;
				iMin = i; 
				pMin = p; }
			L.n(p).l(fRet);
		}
		Assert.EQUALS(iMin, 5); 
		Assert.EQUALS(pMin, minPos); 
	}
	
	static final int NDIM = 3;
	static final double FTOL = 1e-6;
	static final double PIO2 = 1.5707963;

	static final void testFrPrMin() {
		int iter=0,k;
		double angl,fret;

		double[] p=new double[1+NDIM];
		L.n("Program finds the minimum of a function\n");
		L.n("with different trial starting vectors.\n");
		L.n("True minimum is (0.5,0.5,0.5)\n");
		for (k=0;k<=4;k++) {
			angl=PIO2*k/4;
			p[1]=2*Math.cos(angl);
			p[2]=2*Math.sin(angl);
			p[3]=0.0;
			L.n("\nStarting vector: ").l(p);
			final ConjuGrad1Minimizer minimizer = new ConjuGrad1Minimizer(p.length, null, null); 
			fret = minimizer.frprmn(p,NDIM,FTOL,iter);//,func,dfunc);
			L.n("Iterations: ").l(iter);
			L.n("Solution vector: ").l(p); 
			L.n("Func. value at solution: ").l(fret);
		}
	}
	
	/** tests all Methods of this Class	 */
	final static public void testIt() {
		//testPowell(); 
		testLinMin();
	}
	
	final static public void main(final String[] args) {
		testIt();
	}
	
}

/** 
 * 
 * Title: DistSqr<p>
 * Description:
 * Scalar Field returning the Squared multidimensional Distance 
 * to the Origin, handed over in the Constructor. 
 *
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
class DistSqr 
extends AFloatVectorField 
implements IFloatScalarField {

	/** empty Constructor	 */	
	public DistSqr() { }
	
	/** initializing Constructor	 */	
	public DistSqr(final double[] origin_) { this.x0 = origin_; }
	
	/** Origin of the Hyper-Parabola	 */
	public double[] x0;
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Implementation of IFloatVectorField
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @see function.vector.AFloatVectorField#map(double[], double[])	 */
	public double[] map(double[] x, double[] out) {
		for (int i=1; i<=3; i++) {
			out[i]=2*(x[i]-x0[i]); } 
		return out;
	}

	/** @see function.vector.AFloatVectorField#map(float[], float[])	 */
	public float[] map(float[] x, float[] out) {
		for (int i=1; i<=3; i++) {
			out[i]=2*(x[i]-(float)x0[i]); } 
		return out;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Implementation of IFloatScalarField
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @see function.vector.IFloatScalarField#Map(double[])	 */
	public double Map(final double[] x) { //rotate and stretch the 
		return VectorDouble.DIST_SQR(x, x0);
	}

	/** @see function.vector.IFloatScalarField#Map(float[])	 */
	public float Map(final float[] x) {
		return (float) VectorFloat.DIST_SQR(x, x0);
	}
	
}