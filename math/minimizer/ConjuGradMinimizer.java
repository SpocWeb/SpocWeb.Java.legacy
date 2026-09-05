/*
 * File Name: ConjuGradMinimizer.java
 * Created on: 21.03.2004
 *
 */
package math.minimizer;

import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.Log;
import streamIO.object.IStreamIn;
import function.IFloatFunction;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.vector.IFloatScalarField;

/**
 * Minimizes a scalar field in N dimensions by Powell's conjugate-direction method,
 * without requiring derivative information.
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: 
 * @see
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:48:01Z
 * digest: bb3a40401ed41153bc3d726af993e9cb7b33a6ce8ffd05ca8677aba1a6db6227
 * stale: false
 * tags: [code/conjugate, code/optimization]
 * concepts: [Conjugate Gradient Minimizer]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class ConjuGradMinimizer
implements IFloatFunction {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(ConjuGradMinimizer.class, -1);
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Default Value for the Maximum Total Number of Function Evaluations 	 */
	final static public int MAX_ITER_TOTAL_DEFAULT = 1000;

	/** Default for the x-Tolerance of the Minimum	 */	
	static final double TOL_DEFAULT = 2e-4;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Working Vector, Origin of the Ray, 
	 * shared between minimizeAlongRay() and the Dummy @see IFloatFunction#Map(double) 	 */
	double[] rayStart;
	
	/** Working Vector, Direction of the Ray 
	 * shared between minimizeAlongRay() and the Dummy @see IFloatFunction#Map(double) 	 */
	double[] rayDir;
	
	/** new Test Direction, Space retained/cached between Iterations	 */
	double[] dir; 
	
	/** Saved Starting Point for Calc. of average Direction	 */
	final double[] oldStart; 
	
	/** extrapolated Point, Space retained/cached between Iterations	 */
	final double[] extrapol;
	
	/** Working Vector along the current Ray, 
	 * used exclusively by @see IFloatFunction#Map(double) 	 */	
	final double[] ray; //
	
	/** Reference to the Scalar Field to minimize	 */
	final IFloatScalarField scalarField;
	
	/** Reference to the Minimizer to use */
	final BrentFloatMinimizer minimizer = new BrentFloatMinimizer();
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Allocates Working Space for Minimization of a Scalar Field along a Ray. 
	 * 
	 * Design Decisions: 
	 * Which Values are stored in this Class and which ones in the Client, depends on 
	 * - the Cardinalities of the Variables
	 * - Optimizations resulting in cached Values. 
	 * 
	 * @param dim the Dimension of the Scalar Field to minimize 
	 * @param scalarField_ the Scalar Field to minimize 
	 */
	public ConjuGradMinimizer(final int dim, final IFloatScalarField scalarField_) {
		this.scalarField = scalarField_;
		this.oldStart = new double[dim];
		this.extrapol = new double[dim];
		this.rayStart = new double[dim];
		this.rayDir = new double[dim];
		this.ray = new double[dim];
		this.dir = new double[dim]; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**	Powell's heuristic to find Minima in n Dimensions, Chapter 10.5	
	 * starts with Minimizations along the Coordinate Axes. 
	 * A Scalar Function can be approximated by: 
	 * f(x[]) 
	 * = f(x0[]) + dx[]*df/dx[] + dx[]*d�f/dx[]dy[]*dy[] + ...
	 * = c + dx[]*b[] + dx[]*A[][]*dx[] + ...
	 * This does generally not work too well, unless the Principal Axes 
	 * of the Quadratic Form lie on the Coordinate Axes. 
	 * New Directions are chosen based on two Heuristics: 
	 * 1) a very good Direction that takes you directly (steepest) to the Minimum 
	 * 2) 'conjugate' Directions that don't spoil your current Result, 
	 *    which could end up in endless cycling through the Directions. 
	 * Two Vectors u and v are called 'conjugate' when 0 = u*A*v
	 * A Set of mutually conjugate Vectors is called 'conjugate' too. 
	 * A single Cycle through the Set of conjugate Directions 
	 * takes you directly to the Minimum of the Quadratic Form. 
	 * Now, instead of creating the full conjugate Set, 
	 * which tends to fold up into a linearly dependent Set, 
	 * only a few good Directions are kept. 
	 * @param p Starting Vector
	 * @param dirs Starting Directions, can be null
	 * @param maxIterTotal the maximum total Number of Function Evaluations
	 * @return the Number of Evaluations left
	 */
	public double minimize(final double[] p, final double tol) {
		return minimize(p, tol, MAX_ITER_TOTAL_DEFAULT); }
	
	/**	Powell's heuristic to find Minima in n Dimensions, Chapter 10.5	
	 * starts with Minimizations along the Coordinate Axes. 
	 * A Scalar Function can be approximated by: 
	 * f(x[]) 
	 * = f(x0[]) + dx[]*df/dx[] + dx[]*d�f/dx[]dy[]*dy[] + ...
	 * = c + dx[]*b[] + dx[]*A[][]*dx[] + ...
	 * This does generally not work too well, unless the Principal Axes 
	 * of the Quadratic Form lie on the Coordinate Axes. 
	 * New Directions are chosen based on two Heuristics: 
	 * 1) a very good Direction that takes you directly (steepest) to the Minimum 
	 * 2) 'conjugate' Directions that don't spoil your current Result, 
	 *    which could end up in endless cycling through the Directions. 
	 * Two Vectors u and v are called 'conjugate' when 0 = u*A*v
	 * A Set of mutually conjugate Vectors is called 'conjugate' too. 
	 * A single Cycle through the Set of conjugate Directions 
	 * takes you directly to the Minimum of the Quadratic Form. 
	 * Now, instead of creating the full conjugate Set, 
	 * which tends to fold up into a linearly dependent Set, 
	 * only a few good Directions are kept. 
	 * The Number of Evaluations left can be retrieved from maxIter. 
	 * @param p Starting Vector
	 * @param maxIterTotal the maximum total Number of Function Evaluations
	 * @return the Function Value at the found Minimum
	 */
	public double minimize(final double[] p, final int maxIterTotal) {
		return minimize(p, TOL_DEFAULT, maxIterTotal, null); 
	}
	
	
	/**	Powell's heuristic to find Minima in n Dimensions, Chapter 10.5	
	 * starts with Minimizations along the Coordinate Axes. 
	 * A Scalar Function can be approximated by: 
	 * f(x[]) 
	 * = f(x0[]) + dx[]*df/dx[] + dx[]*d�f/dx[]dy[]*dy[] + ...
	 * = c + dx[]*b[] + dx[]*A[][]*dx[] + ...
	 * This does generally not work too well, unless the Principal Axes 
	 * of the Quadratic Form lie on the Coordinate Axes. 
	 * New Directions are chosen based on two Heuristics: 
	 * 1) a very good Direction that takes you directly (steepest) to the Minimum 
	 * 2) 'conjugate' Directions that don't spoil your current Result, 
	 *    which could end up in endless cycling through the Directions. 
	 * Two Vectors u and v are called 'conjugate' when 0 = u*A*v
	 * A Set of mutually conjugate Vectors is called 'conjugate' too. 
	 * A single Cycle through the Set of conjugate Directions 
	 * takes you directly to the Minimum of the Quadratic Form. 
	 * Now, instead of creating the full conjugate Set, 
	 * which tends to fold up into a linearly dependent Set, 
	 * only a few good Directions are kept. 
	 * @param p Starting Vector
	 * @return the Function Value at the found Minimum
	 */
	public double minimize(final double[] p) {
		return minimize(p, TOL_DEFAULT); }
	
	/**	Powell's heuristic to find Minima in n Dimensions, Chapter 10.5	
	 * starts with Minimizations along the Coordinate Axes. 
	 * A Scalar Function can be approximated by: 
	 * f(x[]) 
	 * = f(x0[]) + dx[]*df/dx[] + dx[]*d�f/dx[]dy[]*dy[] + ...
	 * = c + dx[]*b[] + dx[]*A[][]*dx[] + ...
	 * This does generally not work too well, unless the Principal Axes 
	 * of the Quadratic Form lie on the Coordinate Axes. 
	 * New Directions are chosen based on two Heuristics: 
	 * 1) a very good Direction that takes you directly (steepest) to the Minimum 
	 * 2) 'conjugate' Directions that don't spoil your current Result, 
	 *    which could end up in endless cycling through the Directions. 
	 * Two Vectors u and v are called 'conjugate' when 0 = u*A*v
	 * A Set of mutually conjugate Vectors is called 'conjugate' too. 
	 * A single Cycle through the Set of conjugate Directions 
	 * takes you directly to the Minimum of the Quadratic Form. 
	 * Now, instead of creating the full conjugate Set, 
	 * which tends to fold up into a linearly dependent Set, 
	 * only a few good Directions are kept. 
	 * @param p Starting Vector
	 * @param dirs Starting Directions, can be null
	 * @param maxIterTotal the maximum total Number of Function Evaluations
	 * @return the Function Value at the found Minimum
	 */
	public double minimize(final double[] p, final double tol, final int maxIterTotal) {
		return minimize(p, tol, maxIterTotal, null); 
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
	
	/**	Powell's heuristic to find Minima in n Dimensions, Chapter 10.5	
	 * starts with Minimizations along the Coordinate Axes. 
	 * A Scalar Function can be approximated by: 
	 * f(x[]) 
	 * = f(x0[]) + dx[]*df/dx[] + dx[]*d�f/dx[]dy[]*dy[] + ...
	 * = c + dx[]*b[] + dx[]*A[][]*dx[] + ...
	 * This does generally not work too well, unless the Principal Axes 
	 * of the Quadratic Form lie on the Coordinate Axes. 
	 * New Directions are chosen based on two Heuristics: 
	 * 1) a very good Direction that takes you directly (steepest) to the Minimum 
	 * 2) 'conjugate' Directions that don't spoil your current Result, 
	 *    which could end up in endless cycling through the Directions. 
	 * Two Vectors u and v are called 'conjugate' when 0 = u*A*v
	 * A Set of mutually conjugate Vectors is called 'conjugate' too. 
	 * A single Cycle through the Set of conjugate Directions 
	 * takes you directly to the Minimum of the Quadratic Form. 
	 * Now, instead of creating the full conjugate Set, 
	 * which tends to fold up into a linearly dependent Set, 
	 * only a few good Directions are kept. 
	 * @param p Starting Vector
	 * @param dirs Starting Directions, can be null
	 * @param maxIterTotal the maximum total Number of Function Evaluations
	 * @return the Function Value at the found Minimum
	 */
	public double minimize(final double[] p, final double tol, final int maxIterTotal, double[][] dirs) {
		if (dirs == null) {
			dirs = new double[p.length][p.length];
			for (int i=p.length; --i >= 0; ) { //Unit Matrix for the Directions of Gradients
				dirs[i][i]=1; } 
		}
		
		double fRet=scalarField.Map(p);
		//Save your Starting Point (see below) for Calc. of average Direction
		VectorDouble.COPY(p, oldStart); 
		this.numIter = maxIterTotal; 
		for (;;) {
			final double fp=fRet;
			int iBig=-1;
			double del=0;
			//during successive Minimization along all Directions. 
			for (int i=p.length; --i >= 0; ) {
				final double fptt = fRet; fRet = minimizeAlongRay(p, dirs[i], tol);
				final double diff = fptt-fRet; //not necessary to use abs!
				Assert.IS_TRUE(diff >= 0);
				if (del < diff) {
					del = diff; iBig=i; 
				}
			}
			if (ByRefDouble.EQUALS(fp, fRet, tol, ByRefDouble.DOUBLE_ACCURACY)) { //use absolute Tolerance too!
				return fRet; } //finished
			/*if (--iter == 0) {
				throw new RuntimeException("powell exceeding maximum iterations:"+maxIter); 
				//return 0; 
			}*/
			//adjust the Directions
			VectorDouble.SUB(dir, p, oldStart);  //the average Direction of the Sweep through the Directions
			VectorDouble.ADD(extrapol, dir, p);  //construct the quadratically extrapolated Point  
			VectorDouble.COPY(p, oldStart);  //save old Starting Point (see above) for Calc. of average Direction
			//Function at the quadratically extrapolated Point
			final double fptt = scalarField.Map(extrapol);
			if (fptt >= fp) { //no Improvement
				continue; } //not near quadratic Minimum, keep the current Directions...
			final double t=2*(fp-2*fRet+fptt)*ByRefDouble.SQR(fp-fRet-del)-del*ByRefDouble.SQR(fp-fptt);
			if (t >= 0) { //Improvement cannot be attributed to a single Direction mostly
				continue; } //not near quadratic Minimum, keep the current Directions...
			fRet = minimizeAlongRay(p, dir, tol); //minimize again along the average Direction
			final double[] tmp = dirs[iBig]; dirs[iBig]= dirs[p.length-1]; dirs[p.length-1] = dir; dir = tmp;
		}
		//return iter;
	}
	
	/** single minimum Step 
	 * of a function along a ray in N-dimensions 
	 * 
	 * @param origin Origin of the Ray, moved to the Minimum Location
	 * @param direction Direction of the Ray, replaced by the Displacement 
	 * @return the minimum Function Value along this Ray
	 */
	protected double minimizeAlongRay(final double[] origin, final double[] direction, final double tol) {
		rayStart = origin; // Vectors are unchanged
		rayDir   = direction; 
		//Bracket the Minimum! Initial Guess are 0 and 1
		minimizer.init(0, 1, this, tol); 
		numIter = minimizer.solve(numIter, tol, true); //count total Number of Evaluations
		final double xMin=minimizer.xMid;
		final double yMin=minimizer.yMid;
		L.n("Iterations left:",-1).l(numIter,-1).l("xMin=",-1).l(xMin,-1);
		//the actual Solution is returned in Place, no need to calc Displacement separately!
		//VectorDouble.MUL_AT(direction, xMin); 
		//VectorDouble.ADD_AT(origin, direction); 
		VectorDouble.ADD_PROD_AT(origin, direction, xMin); 
		return yMin; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// #region: Implementation @see function.IFloatFunction
	/////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * Returns {@link IStreamIn#ORDER_NONE}: this function carries no derivative order.
     *
     * @see function.IFloatFunction#getOrder()
     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }
    
	/** 
	 * Artificial 1-dim Function to minimize along a Ray
	 * @see function.IFloatFunction#Map(double)	 
	 */
	public double Map(final double x) {
		VectorDouble.ADD_PROD(ray, rayStart, x, rayDir); 
		return scalarField.Map(ray);
	}
	
	/**
	 * Evaluates the scalar field along the current search ray at the point {@code x}.
	 *
	 * @see function.IFloatFunction#Map(float)
	 */
	public float Map(final float x) { return (float) Map((double) x); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Position of the Minimum of a sample Quadratic Form	 */
	static final double[] minPos = {1,1,1};
	
	/** Tests Minimization along a Ray	 */	
	final static public void testLinMin() {
		L.n("Testing Minimization along a Ray");
		L.n("Minimum of a 3-d quadratic centered at").l(minPos);
		L.n("Minimum is found along a series of radials.\n");
		L.n().l("\tx").l("\ty").l("\tz").l("\tminimum");
		final double[] xi=new double[minPos.length];
		final DistSqrDistorted func = new DistSqrDistorted(minPos); 
		final ConjuGradMinimizer minimizer = new ConjuGradMinimizer(xi.length,func);
		int iMin = -1; 
		double fMin = Double.POSITIVE_INFINITY; 
		double[] pMin = null; 
		for (int i=0;i<=10;i++) {
			final double x=IMeasurAble.PI_HALF*i/10;
			final double sr2=Math.sqrt(2);
			xi[0]=sr2*Math.cos(x);
			xi[1]=sr2*Math.sin(x);
			xi[2]=1;
			final double[] p =new double[xi.length];
			minimizer.numIter = 14; //reset it every time 
			final double fRet = minimizer.minimizeAlongRay(p,xi, 1e-4);
			if (fMin > fRet) {
				fMin = fRet;
				iMin = i; 
				pMin = p; }
			L.n(p).l(fRet);
		}
		Assert.EQUALS(iMin, 5); 
		Assert.EQUALS(pMin, minPos); 
	}
	
	/** Start Vector for testing Minimization in N dims	 */	
	static final double[] startVector={1.5,1.5,2.5};
	
	/** Tests Minimization in N dims	 */	
	final static public void testPowell() {
		L.n("Testing Powell's Minimization");
		L.n("Minimum of a 3-d quadratic centered at").l(minPos);
		final DistSqrDistorted func = new DistSqrDistorted(minPos); 
		final ConjuGradMinimizer minimizer = new ConjuGradMinimizer(minPos.length,func);
		double fret = minimizer.minimize(startVector, 55); //24 for 3D
		L.n("Iterations left : ").l(minimizer.numIter);
		L.n("Minimum found at: ").l(startVector);
		L.n("Minimum function value = ").l(fret);
		L.n("True minimum of function is at:").l(minPos);
	}
	
	/** tests all Methods of this Class	 */
	final static public void testIt() {
		testPowell(); 
		testLinMin();
	}
	
	/** Main method to be called from the command line, running {@link #testIt()}. */
	final static public void main(final String[] args) {
		testIt();
	}

}

/**
 * Scalar field returning the squared multidimensional distance to an origin, handed over
 * in the constructor, but with the quadratic squished and its main axes tilted.
 *
 * <p>To demonstrate changing Directions,
 * the Quadratic is squished to make it asymmetric
 * and the Main Axes of the Quadratic are tilted.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:48:01Z
 * digest: f099b3d48c85b28c03b42f0eb40fcdbfb7cf5d983e2be7c9c505f7f5c3d44b18
 * stale: false
 * tags: [code/test_fixture]
 * concepts: [Distorted Squared Distance Test Fixture]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
class DistSqrDistorted
implements IFloatScalarField {

	/** empty Constructor	 */	
	public DistSqrDistorted() { }
	
	/** initializing Constructor	 */	
	public DistSqrDistorted(final double[] origin_) { this.x0 = origin_; }
	
	/** Origin of the Hyper-Parabola	 */
	public double[] x0;
	
	/**
	 * Returns the squished, rotated squared distance from {@code x} to {@link #x0}: the
	 * {@code z} axis is left untouched, the {@code x+y} diagonal is weighted normally and
	 * the {@code x-y} diagonal is weighted 8 times as strongly.
	 *
	 * @see function.vector.IFloatScalarField#Map(double[])
	 */
	public double Map(final double[] x) { //rotate and stretch the
		double ret = ByRefDouble.SQR(x[2]-x0[2]);
		ret += ByRefDouble.SQR(x[1]+x[0]-(x0[1]+x0[0]));
		ret += 8*ByRefDouble.SQR(x[1]-x[0]-(x0[1]-x0[0]));
		return ret;
	}

	/**
	 * Returns the plain (undistorted) squared Euclidean distance from {@code x} to
	 * {@link #x0}, for comparison against {@link #Map(double[])}.
	 *
	 * @see function.vector.IFloatScalarField#Map(double[])
	 */
	public double Map1(final double[] x) { //rotate and stretch the
		return VectorDouble.DIST_SQR(x, x0);
	}

	/**
	 * Returns the plain (undistorted) squared Euclidean distance from {@code x} to
	 * {@link #x0}.
	 *
	 * @see function.vector.IFloatScalarField#Map(float[])
	 */
	public float Map(final float[] x) {
		return (float) VectorFloat.DIST_SQR(x, x0);
	}
	
}