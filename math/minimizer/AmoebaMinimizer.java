package math.minimizer;

import math.matrix.MatrixDouble;
import math.vector.VectorDouble;
import streamIO.Assert;
import streamIO.Log;
import function.vector.IFloatScalarField;

/**
  * Title: AmoebaMinimizer<p>
  * Description:
  * Purpose:
  * This Class is used to find the Minimum of a given continuous Scalar Field  
  * (not necessarily differentiable) using the "downhill Simplex" Method.
  * This Method is slow (linear Convergence), but very robust.
  * The Scalar Field needs to be continuous only, not differentiable!
  *
  * A Simplex is a Tensor
  * consisting of N+1 N-dimensional Vectors
  * denoting the Corners of the Simplex. 
  * For 1D Spaces a Simplex is a Line.
  * For 2D Spaces a Simplex is a Triangle.
  * For 3D Spaces a Simplex is a Tetragedron.
  * etc.
  * 
  * For smoother Functions better use the Gradient (maximum Descent) Method.
  * For Functions on a discrete Set of Configurations (Combinatorical Minimization) 
  * use simulated Annealing or the Flood Method, since in discrete Topologies 
  * there is no Concept of "Direction" or "Downhill". 
  * 
  * 
  * This AmoebaMinimizer behaves similar to an Amoeba:
  * only one Corner moves with every Iteration
  * * stretching until a Corner reaches a Valley 
  * * oozing downhill along the Valley 
  * * pulling itself together around the Minimum 
  *
  * Implementation Details:
  * Simplices should not be degenerate,
  * i.e. the Difference Vectors should be linearly independent!.
  *
  * The Volume of such a Simplex is 1/N! the Volume of the Spat
  * defined by the Difference Vectors,
  * which can be calculated using their Determinant.
  *
  * Known SubClasses:
  *
  * similar Classes:
  * @see math.minimizer.AnnealingMinimizer, 
  * which allows the Amoeba to move uphill sometimes, according to Temperature. 
  * 
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-30-2002, 11:29 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class AmoebaMinimizer {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(AmoebaMinimizer.class, 1);

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
	protected double[] cornerTry;
	
	/**
	 * Return for the Indices of the MinMax Method,
	 * Also used betwen minimizeStep() and evaluate!
	 */
	protected final int[] minMax = new int[3];
	
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
	public AmoebaMinimizer(final IFloatScalarField scalarFn_, final int dim) { 
		this (scalarFn_, dim, INITIAL_SIZE_DEFAULT); }

	/**
	 * Constructor, defaulting the Corners to the Unit Vectors.
	 * @see #INITIAL_SIZE_DEFAULT the initial Size of the Amoeba  
	 * @param origin of the Simplex / Amoeba 
	 * This Vector is copied and not reused within this Object!
	 */
	public AmoebaMinimizer(final IFloatScalarField ScalarFn_, final double[] origin) { 
		this (ScalarFn_, origin, INITIAL_SIZE_DEFAULT); }

	/**
	 * Constructor, defaulting the initial Position to the Coordinate Origin
	 * and the Amoeba Edges along the Coordinate Axes.  
	 * @param dim the Number of Dimensions for the Simplex. 
	 * @param size the Size of the Simplex, should be a characteristic Length of the Problem.
	 */
	public AmoebaMinimizer(final IFloatScalarField scalarFn_, final int dim, final double size) {
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
	public AmoebaMinimizer(final IFloatScalarField scalarFn_, final double[] origin_, final double size) {
		corners = new double[origin_.length+1][origin_.length];
		MatrixDouble.COPY_AT(corners,     origin_, 0, origin_.length+1);
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
	protected AmoebaMinimizer(final IFloatScalarField scalarFn_, final double[][] Corners_) {
		if (Corners_.length <= Corners_[0].length) {
			throw new IllegalArgumentException("The Simplex must have more than " +
				Corners_[0].length + " Corners, but it has only " + Corners_.length); }
		this.corners = MatrixDouble.COPY(Corners_);
		MatrixDouble.COL_SUM(corners, cornerSum); //once out of the loop!
		setScalarField(scalarFn_);
	}
	
	/** Common Initialization of the two Constructor Strains. 
	 * Precondition: corners is already allocated.  */
	protected void init(final IFloatScalarField scalarFn_, final double size_) {
		values  = new double[corners.length  ];
		cornerSum  = new double[corners.length-1];
		cornerTry    = new double[corners.length-1];
		reinit(scalarFn_, size_);
	}
	
	/**
	 * reinitialize the Simplex to a full HyperTriangle
	 * at the current Position which is calculated as the Average of all Positions.
	 * This is a common Practice to 
	 */
	public AmoebaMinimizer reinitialize
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
	
	/**
	 * Tries to minimize the given Scalar Function with the current Positions
	 * in at most MaxSteps Iterations.
	 * If succeeded, the best Value is placed into the 0th Corner.
	 * Very robust, Function needs to be continuous only, but not differentiable!
	 * @param fTol, The relative Variance of the Function Values of all Amoeba Nodes.  
	 * @param maxSteps the maximum Number of Steps to use
	 * @return the actual absolute Variance of the Function Values for the Minimum. 
	 */
	public double minimize(final double fTol, int maxSteps) {
		double rTol = Double.POSITIVE_INFINITY;
		while (--maxSteps >= 0) {
			rTol = refine();
			if (rTol < fTol) { //put the best Value first.
				final int iLo  = minMax[0];
				final double swap = values [0]; values [0] = values [iLo]; values [iLo] = swap;
				final double[]row = corners[0]; corners[0] = corners[iLo]; corners[iLo] = row ; 
				break;
			}
		}
		return rTol;}
	
	/**
	 * Tries to minimize the given Scalar Function with the current Positions.
	 * Minimize in N-dimensions by downhill simplex method.
	 * Very robust, Function needs to be continuous only, but not differentiable!
	 * @param fTol, The absolute Variance for the Function Values of all Amoeba Nodes.  
	 * @return the actual absolute Variance of the Function Values for the Minimum. 
	 */
	public double refine() { //fractional Tolerance
		//Determine the Indices of the minimum and the two maximum Values.
		VectorDouble.MIN2MAX2POS(values, minMax);
		int iHi  = minMax[2];
		int inHi = minMax[1];
		int iLo  = minMax[0];
			
		if (anneal) {
			final double fluctuation = values[iHi]+temperature*Math.log(Math.random());
			if (values[inHi] > fluctuation) { //every now and then...
				if (values[iLo] < fluctuation) { //
					final int swap = iHi; iHi = inHi; inHi = swap; //...try the second worst Direction! 
				} else {
					final int swap = iHi; iHi = iLo ; iLo  = swap; //...or even the best Direction!
				}
			}
			temperature *= annealFactor; }
		//try new Configurations: 
		//first try to reflect the largest/worst Point through the opposite Hyper-Plane.
		double yTry  = tryToStretch(iHi, -1); //mirroring...
		if (yTry <= values[iLo]) { //if even better than the lowest/best Point...
			yTry  = tryToStretch(iHi,  2); //...try the double Expansion too
		} else
		if (yTry >= values[inHi]) { final double yHi; //otherwise if worse than the second highest,
			yHi   = values[iHi ];
			yTry  = tryToStretch(iHi, 0.5);  //try a 1Dim Contraction along the Orthogonal to the HyperPlane.
		if (yTry >= yHi) { //still worse than the worst; Can't get better, so...
			for (int i = corners.length; --i >= 0;) {//...contract ALL Points around the lowest/best Point
				if (i == iLo) { //leave the lowest Corner out of course
					continue; } 
				VectorDouble.ADD_AT(corners[i], corners[iLo]); 
				VectorDouble.MUL_AT(corners[i], 0.5);
				values[i] = scalarFn.Map(corners[i]);
			} //recalculate the ColSum 
			VectorDouble.FILL_AT  (cornerSum , 0); //
			MatrixDouble.COL_SUM(corners, cornerSum); 
		}
		}
		return 
		  Math.abs(values[iHi] -         values[iLo])/
		 (Math.abs(values[iHi])+Math.abs(values[iLo])); }
	
	/**
	  * evaluates and possibly accepts a trial point; 
	  * Extrapolate the highest / worst Point by a Factor fac through the opposite HyperPlane,
	  * given by the other Points. 
	  * Evaluate this new Point and swap the bad Point with temp, when smaller.
	  * @see #refine() uses this Method exclusively 
	  */
	protected double tryToStretch(final int iHi, final double fac) {
		final double fac1= (1-fac)/cornerTry.length; //arithmetic Mean
		VectorDouble.BI_LIN(cornerTry, cornerSum, fac1, corners[iHi],  fac-fac1, 0, cornerTry.length);
		final double valueTry = scalarFn.Map(cornerTry); 	//build up the Test Vector in temp
		double fluctuatedValue=valueTry; //
		if (anneal) {
			fluctuatedValue+=temperature*Math.log(Math.random()); }
		if (fluctuatedValue < values[iHi]) { //if better...
			values[iHi] = valueTry; //...accept the new Point 
			VectorDouble.ADD_AT(cornerSum, cornerTry);  //correct the ColSum
			VectorDouble.SUB_AT(cornerSum, corners[iHi]);
			final double[] swap = corners[iHi]; corners[iHi] = cornerTry; cornerTry = swap;
		}
		return valueTry; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() { //String[] args) throws Exception {
		L.enter().println();
		final double[] minPosition = {0.5, 0.6, 0.7};
		final IFloatScalarField scalarFn = new SinOfDistDivDist(minPosition); 
		final AmoebaMinimizer smp = new AmoebaMinimizer (scalarFn, minPosition.length);
		for (int i = 20; --i >= 0;) {
			L.n("Positions:\n").l(smp.corners);
			L.n("Values:"     ).l(smp.values );
			L.n("Differences:").l(smp.refine());
		}
		Assert.IS_TRUE(smp.refine() < 1e-6);
		for (int i = smp.corners.length; --i >= 0;) {
			Assert.EQUALS(minPosition, smp.corners[i], 0.06); //very shallow Minimum!
		}
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(); 
	}

}
