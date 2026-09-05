/*
 * File Name: AnnealingMinimizer.java
 * Created on: 18.03.2004
 *
 */
package math.minimizer;

import math.matrix.MatrixDouble;
import math.vector.VectorDouble;
import streamIO.Assert;
import streamIO.Log;
import function.vector.IFloatScalarField;

/**
 * Finds the minimum of a continuous, multidimensional function (not necessarily
 * differentiable) by simulated annealing over a downhill-simplex search.
 *
 * <p>Implemented by modifying the {@link AmoebaMinimizer} approach to generate random
 * fluctuations in the function values, proportional to the temperature (see Numerical
 * Recipes 10.9, Continuous Minimization by simulated Annealing).
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Similar Classes: 
 * @see math.minimizer.AmoebaMinimizer which oozes only downhill
 * @see math.matrix.MatrixFloat  uses Annealing for Clustering 
 * @see math.matrix.MatrixDouble uses Annealing for Clustering 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:01Z
 * digest: ff53fb1716493b325109155b0f4087b361ce82b31e0cfcf1f08a57f6bdc9c951
 * stale: false
 * tags: [code/simulated_annealing, code/annealing, code/optimization]
 * concepts: [Simulated Annealing Minimizer]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class AnnealingMinimizer {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(AnnealingMinimizer.class, 1);

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The Default characteristic Length of the Problem
	 * Initial Size of the Amoeba
	 * 
	 * Must never be 0!
	 */
	public static double INITIAL_SIZE_DEFAULT = 1;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The Corners of this Simplex
	 * There always have to be N+1 Corners with N Dimensions!
	 * This is checked in the Constructors.
	 */
	protected double[][] corners;
	
	/**
	 * The Values of the Scalar Function at the Corners of this Simplex
	 * There are N+1 Corners and Values with N Dimensions!
	 */
	protected double[] values;
	
	/** The Sums of the Corners' Columns in this Simplex	 */
	protected double[] cornerSum;
	
	/** The Flag switching on Annealing, to save testing float Values */
	protected boolean anneal = false;
	
	/** The Annealing Temperature, which must be non-negative and is reduced by the 	 */
	protected double temperature = 0;
	
	/** The Annealing Factor which reduces the Temperature with each Iteration */
	protected double annealFactor = 0.99;
	
	/** Reference to the Scalar Function to be minimized */
	protected IFloatScalarField scalarFn;
	
	/** Temporary Vector used solely in AmoTry	 */
	protected transient double[] cornerTry;
	
	/**
	 * Return for the Indices of the MinMax Method,
	 * Also used betwen minimizeStep() and evaluate!
	 */
	protected transient int[] minMax = new int[3];
	
	/** points to any of the existing Corners	 */
	double[] bestCorner;
	
	/** best Value encountered so far	 */
	double bestValue=Double.POSITIVE_INFINITY; 
	
	/** for Communication between 
	 * @see #amebsa(int, double, int[], double)
	 * @see #tryToStretch(int, int, double[], double)
	 */
	double yHi;
	
	/** counts the Number of Function Evaluations	 */
	protected int numFnEvals;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Safe to return the Array, since it is structurally immutable (unlike other Containers)
	 * @return the given Corner of the Simplex, null otherwise.
	 */
	public double[] getCorner(final int i) { return corners[i]; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructor, defaulting the initial Position to the Coordinate Origin. 
	 * The Corners are defaulted to the Unit Vectors
	 * @see #INITIAL_SIZE_DEFAULT the initial Size of the Amoeba  
	 * @param dim the Number of Dimensions for the Simplex
	 */
	public AnnealingMinimizer(final IFloatScalarField scalarFn_, final int dim) { 
		this (scalarFn_, dim, INITIAL_SIZE_DEFAULT); }

	/**
	 * Constructor, defaulting the Corners to the Unit Vectors.
	 * @see #INITIAL_SIZE_DEFAULT the initial Size of the Amoeba  
	 * @param origin of the Simplex / Amoeba 
	 * This Vector is copied and not reused within this Object!
	 */
	public AnnealingMinimizer(final IFloatScalarField ScalarFn_, final double[] origin) { 
		this (ScalarFn_, origin, INITIAL_SIZE_DEFAULT); }

	/**
	 * Constructor, defaulting the initial Position to the Coordinate Origin
	 * and the Amoeba Edges along the Coordinate Axes.  
	 * @param dim the Number of Dimensions for the Simplex. 
	 * @param size the Size of the Simplex, should be a characteristic Length of the Problem.
	 */
	public AnnealingMinimizer(final IFloatScalarField scalarFn_, final int dim, final double size) {
		corners = new double[dim+1][dim];
		init(scalarFn_, size);
	}
	
	/**
	 * Constructor, defaulting the Corners to the Unit Vectors
	 * multiplied by size which should be a characteristic Length of the Field Structure. 
	 * @see #INITIAL_SIZE_DEFAULT the initial Size of the Amoeba  
	 * @param origin of the Simplex
	 * This Vector is copied and not reused within this Object!
	 * @param size the Size of the Simplex, should be a characteristic Length of the Problem.
	 */
	public AnnealingMinimizer(final IFloatScalarField scalarFn_, final double[] origin_, final double size) {
		corners = new double[origin_.length+1][origin_.length];
		MatrixDouble.COPY_AT(corners, origin_, 0, origin_.length+1);
		init(scalarFn_, size);
	}
	
	/**
	 * Constructor
	 * @param corners the initial Corners of the Simplex.
	 * Their Number must be 1 higher than the highest Dimension of any Corner Vector.
	 * By Default the Origin is put last in the Simplex.
	 * This Matrix is copied and not reused within this Object,
	 * because Initialization happens only O(1)!
	 * The Simplex is checked for non Degeneracity
	 */
	protected AnnealingMinimizer(final IFloatScalarField scalarFn_, final double[][] corners_) {
		if (corners_.length <= corners_[0].length) {
			throw new IllegalArgumentException("The Simplex must have more than " +
				corners_[0].length + " Corners, but it has only " + corners_.length); }
		this.corners = MatrixDouble.COPY(corners_);
		MatrixDouble.COL_SUM(corners, cornerSum); //once out of the loop!
		setScalarField(scalarFn_);
	}
	
	/** Common Initialization of the two Constructor Strains. 
	 * Precondition: corners is already allocated.  */
	protected void init(final IFloatScalarField scalarFn_, final double size_) {
		values    = new double[corners.length  ];
		cornerSum = new double[corners.length-1];
		cornerTry = new double[corners.length-1];
		reinit(scalarFn_, size_);
	}
	
	/**
	 * reinitialize the Simplex to a full HyperTriangle
	 * at the current Position which is calculated as the Average of all Positions.
	 * This is a common Practice to 
	 */
	public AnnealingMinimizer reinitialize
	( final IFloatScalarField scalarFn_, final double size_) {
		VectorDouble.FILL_AT  (cornerSum , 0); //
		reinit(scalarFn_, size_);
		return this; 
	}
	
	/**
	 * reinitialize the Simplex to a full HyperTriangle
	 * at the current Position which is calculated as the Average of all Positions.
	 * This is a common Practice to 
	 */
	private void reinit
	( final IFloatScalarField scalarFn_, final double size_) {
		MatrixDouble.ADD_DIAG_AT(corners, size_, 0, corners.length-1); //Stretch the Amoeba along the Coordinate Axes
		MatrixDouble.COL_SUM   (corners, cornerSum); //initialize the Sum of all Corners
		setScalarField(scalarFn_);
	}
	
	/**
	 * Initializes the ScalarField to be minimized
	 * and calculates all Function Values at the Simplex' Corners...
	 */
	public void setScalarField(final IFloatScalarField scalarFn_) {
		this.scalarFn = scalarFn_;
		for(int i = corners.length; --i >= 0;) {
			values[i] = scalarFn.Map(corners[i]); }
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** evaluate a trial point, used by  (10.9)
	 * 
	 * @param iHi the Point to move, typically the one with the highest/worst Value.
	 * @param factor the Stretching Factor to use 
	 * @return the Value of the new Point
	 */
	protected double tryToStretch(final int iHi, final double factor) {
		final double fac1=(1-factor)/(cornerTry.length-1);
		VectorDouble.BI_LIN(cornerTry, cornerSum, fac1, corners[iHi],  factor-fac1, 1, cornerTry.length);
		final double tryValue=scalarFn.Map(cornerTry); ++numFnEvals;
		if (tryValue <= bestValue) { //if better than the best...
			bestCorner = cornerTry; //...record this
			bestValue=tryValue; }
		final double fluctuatedValue=tryValue-temperature*Math.log(Math.random());
		if (fluctuatedValue < yHi) { //if better...
			values[iHi]=tryValue; //...accept the new Point
			yHi = fluctuatedValue;
			VectorDouble.ADD_AT(cornerSum, cornerTry);  //correct the ColSum
			VectorDouble.SUB_AT(cornerSum, corners[iHi]);
			final double[] swap = corners[iHi]; corners[iHi] = cornerTry; cornerTry = swap;
		}
		return fluctuatedValue;
	}
	
	/** 
	 * single Step for simulated annealing in continuous spaces 
	 */
	public double refine() {
		//Determine Minimum, Maximum and 2nd Maximum and their Positions
		int iLo=1;
		int iHi=2;
		double ylo, ynHi=ylo=values[1]+temperature*Math.log(Math.random());
		yHi=values[2]+temperature*Math.log(Math.random());
		if (ylo > yHi) {
			iHi=1;
			iLo=2;
			ynHi=yHi; yHi=ylo; ylo=ynHi;
		}
		for (int i=3; i<values.length; i++) {
			final double yt=values[i]+temperature*Math.log(Math.random());
			if (yt <= ylo) {
				iLo=i;
				ylo=yt;
			}
			if (yt > yHi) {
				ynHi=yHi; yHi=yt; iHi=i;
			} else if (yt > ynHi) {
				ynHi=yt;
			}
		}
		minMax[0] = iLo; 
			
		//try to improve
		double ytry=tryToStretch(iHi,-1);
		if (ytry <= ylo) {
			ytry=tryToStretch(iHi,2);
		} else if (ytry >= ynHi) {
			final double ysave=yHi;
			ytry=tryToStretch(iHi,0.5f);
			if (ytry >= ysave) {
				for (int i=1; i<=cornerSum.length; i++) {
					if (i != iLo) {
						for (int j=1;j<cornerSum.length;j++) {
							cornerSum[j]=0.5f*(corners[i][j]+corners[iLo][j]);
							corners[i][j]=cornerSum[j];
						}
						values[i]=scalarFn.Map(cornerSum);
					}
				}
				numFnEvals += cornerSum.length;
				VectorDouble.FILL_AT(cornerSum, 0); 
				MatrixDouble.COL_SUM(corners, 1, cornerSum.length, cornerSum);
			}
		} 
		return 2*Math.abs(yHi-ylo)/(Math.abs(yHi)+Math.abs(ylo));
	}
	
	/** simulated annealing in continuous spaces (10.9)
	 * 
	 * @param ftol the fractional Tolerance for an early Return
	 * @param iter byRef the Number of maximum and Iterations left (when > 0)
	 * @param temptr The Temperature of the Process, which should be reduced to 0. 
	 */
	double anneal(final double fTol, final int iter, final double temptr) {
		temperature = -temptr;
		double rtol = Double.POSITIVE_INFINITY;
		for (numFnEvals = 0; numFnEvals < iter;) {
			if ((rtol = refine()) < fTol) {
				break; }
		}
		final int iLo  = minMax[0];
		final double[] row=corners[1]; corners[1]=corners[iLo]; corners[iLo]=row;
		final double swap = values[1];  values[1]= values[iLo];  values[iLo]=swap;
		return rtol;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	static final int NP = 4; 
	static final int MP = 5; 
	static final double FTOL = 1E-6f;
	
	static final double[] minPosition = {0.0, 0.6, 0.7, 0.8, 0.9};
	static final double[] xoff={0,0,0,0,0}; //0,2,2,2,2}; //

	/** Main method to be called from the command line, running {@link #testIt()}. */
	final static public void main(final String[] args) {
		testIt(); }

	/** Runs simulated annealing against {@link SinOfDistDivDist} and asserts convergence to {@code minPosition}. */
	final static public void testIt() {
		L.enter().println();
		L.n("Input t, iiter:\n"); 
		int iiter=1; 
		double temptr =0;//.005f;//1.1f; 
		final IFloatScalarField scalarFn = new SinOfDistDivDist(minPosition); 
		final AnnealingMinimizer anneal = new AnnealingMinimizer(scalarFn, xoff); 
		double ybb = Double.POSITIVE_INFINITY;
		int nit=0;
		L.n("#iter").l("\ttemperature").l("\tbestValue").l("\tbestCorner");
		for (int jiter=1; jiter<=100; jiter++) {
			temptr *= 1-iiter*.01;
			nit += iiter;
			final double rTol = anneal.anneal(FTOL,iiter,temptr);
			if (anneal.bestValue < ybb) {
				ybb=anneal.bestValue;
				L.n().l(nit).l(temptr).l(anneal.bestValue).l(anneal.bestCorner);
			}
			if (rTol < FTOL) {
				break; } 
		}
		L.n(); 
		L.n("Vertices of final 3-D simplex and values at the vertices:");
		L.n("i").l("\tfunction").l("\tx[i]").l("\ty[i]").l("\tz[i]");
		for (int i=1;i<=MP;i++) {
			L.n().l(i).l(anneal.values[i]).l(anneal.corners[i]); }
		L.n().l("best Result ever:").l(anneal.bestValue).l(anneal.bestCorner);
		L.n();
		for (int i = anneal.corners.length; --i > 0;) {
			Assert.EQUALS(minPosition, anneal.corners[i], 4*Math.sqrt(Math.sqrt(FTOL))); }//very shallow Minimum!
	}
	
}


/**This Class implements a smooth Test Function
 * that returns the negative Sine of the Square of the Euklidean Distance of the Input
 * to a certain Vector or the Origin divided by this Distance.
 * This Function has a local Minimum in the Origin
 * surrounded by Rings of lesser Minima and Maxima.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:01Z
 * digest: 2c751133445b1e958b2e1dcf89f54cb1f5de840d0b477aa4b78d57fb685fe4fe
 * stale: false
 * tags: [code/test_fixture]
 * concepts: [Scalar Field Test Fixture]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
class TestScalarField
implements IFloatScalarField {

	static final int N = 4;
	static final float RAD = 0.3f;
	static final float AUG = 2;
	static final float[] width={0, 1, 3, 10, 30};

	/** this Vector is subtracted from the Argument if not null */
	public double[] V0;

	/**
	 * Returns a synthetic, ring-shaped landscape value for the given point, weighting each
	 * coordinate by {@link #width} and penalizing distance from the nearest integer grid point.
	 *
	 * @param p the point to evaluate, indexed {@code 1..N}
	 * @return the landscape value at {@code p}
	 */
	public float Map(final float[] p) {
		float sumd=0, sumr=0;
		for (int j=1; j<=N; j++) {
			final double q=p[j]*width[j];
			final double r=(q >= 0 ? (int)(q+0.5) : -(int)(0.5-q));
			sumr += q*q;
			sumd += (q-r)*(q-r);
		}
		return 1+sumr*(1+(sumd > RAD*RAD ? AUG : AUG*sumd/(RAD*RAD)));
	}

	/**
	 * Returns a synthetic, ring-shaped landscape value for the given point, weighting each
	 * coordinate by {@link #width} and penalizing distance from the nearest integer grid point.
	 *
	 * @param p the point to evaluate, indexed {@code 1..N}
	 * @return the landscape value at {@code p}
	 */
	public double Map(final double[] p) {
		double sumd=0, sumr=0;
		for (int j=1;j<=N;j++) {
			final double q=p[j]*width[j];
			final double r=(q >= 0 ? (int)(q+0.5) : -(int)(0.5-q));
			sumr += q*q;
			sumd += (q-r)*(q-r);
		}
		return 1+sumr*(1+(sumd > RAD*RAD ? AUG : AUG*sumd/(RAD*RAD)));
	}

}
