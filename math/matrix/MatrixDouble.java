package math.matrix;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

import math.NumberFormatter;
import math.vector.HunterDouble;
import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.AStreamOut;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.object.AStreamIn;
import function.byref.ByRefDouble;
import function.vector.IBinaryOpFloat;
import function.vector.OpCount;
import function.vector.OpFirst;
import function.vector.OpLast;
import function.vector.OpMax;
import function.vector.OpMin;
import function.vector.OpSum;

/**
  * Dynamic matrix of {@code VectorDouble}-shaped rows, plus a large set of static methods
  * operating directly on non-dynamic {@code double[][]} arrays.
  *
  * <p>Instances of this class operate as matrices that can be used for solving equations by
  * decomposing into upper and lower triangular matrices (A = U*L), mapping vectors with the
  * actual or decomposed form, calculating eigenvalues, and calculating Markov chain
  * transformation probabilities by matrix multiplication.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-08-2002, 01:17 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T13:04:13Z
  * digest: c9776a145dc8badb6d547380cf4c0578b0fc79bc7c1b4bb3163d2519a2a87bb9
  * stale: false
  * tags: [code/matrix_operations, code/matrix_algebra]
  * concepts: [Double-Precision Dense Matrix]
  * facets: {layer: utility, status: broken, complexity: high}
  * -->
  */
public class MatrixDouble 
extends AMatrix {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(MatrixDouble.class, -0);
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////

	/** 
	 * Swaps the Rows of both Matrices (instead of each Cell 
	 * @param a first  Matrix
	 * @param b second Matrix
	 */
	public static final void SWAP(final double[][] a, final double[][] b) {
		if (a == b)
			return; 
		for(int i = a.length; --i >= 0;) {
			final double[] tmp = a[i]; a[i] = b[i]; b[i] = tmp;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Matrix Power & Exponentiation
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the matrix raised to the given integer power via binary exponentiation.
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final double[][] POW(final double[][] matrix, int n) {
		boolean odd; 
		if			(n <  1) return POW(RCP(matrix), -n);
		if (odd =  ((n &  1) != 0))
			if		(n == 1) return matrix;
		double[][] sqr  = new double[matrix.length][matrix.length];
		double[][] sng  = MatrixDouble.COPY(matrix); 
		double[][] self = new double[matrix.length][matrix.length];
		for(;n > 1;){		//Use the Horner Scheme in the Exponent.
			MatrixDouble.MAP(sng, sng, sqr); 
			if (((n >>= 1) & 1) != 0) { 	//(N1.isOdd())
				if (odd) 
					MatrixDouble.MAP(self, sqr, sng); //for even Powers
				else {
					MatrixDouble.COPY_AT(sng, sqr); odd = true; 
				} 	//save one Matrix Multiplication in the beginning here
				final double[][] tmp = sng; sng = self; self = tmp; 
			}
			final double[][] tmp = sng; sng = sqr; sqr = tmp; 
		}	//(! N1.halfAt().IntAt().equals(mZERO))
		return self; 
	}
	
	/** Raises the matrix in place to the binary power, stopping early once a dominant eigenvalue emerges.
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final double[][] BXP_AT(final double[][] matrix, final int pow) {
		return BXP_AT(matrix, pow, null); }

	/** Raises the matrix in place to the binary power, using the given scratch matrix.
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final double[][] BXP_AT(final double[][] matrix, final int pow, final double[][] work) {
		final double[][] power = BXP(matrix, pow, matrix, work); 
		if (power != matrix)
			SWAP(power, matrix); 
		return matrix; }
	
	/** Returns the matrix raised to the binary power, stopping early once a dominant eigenvalue emerges.
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final double[][] BXP(final double[][] matrix, final int pow) {
		return BXP(matrix, pow, null, null); }

	/** Returns the matrix raised to the binary power using the given scratch matrices, stopping
	 * early once a dominant eigenvalue emerges.
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final double[][] BXP(final double[][] matrix, final int pow, double[][] power, double[][] work2) {
		if (pow == 0)
			return matrix; 
		if (power == null) 
			power  = new double[matrix.length][matrix.length];
		if (work2 == null) 
			work2  = new double[matrix.length][matrix.length];
		double[][] base = matrix; //use matrix directly for the first Iteration (but not later ones!  
		for(int i = pow; --i >= 0;) { //should be sufficient to minimize all other Eigenvalues. 
			MatrixDouble.MAP(base, base, power); 
			if (IS_MATRIX_SIMPLE(power)) //stop, if the Matrix has a dominant Eigenvalue
				return power;
			if (base == matrix) //superfluous for BXP_AT, since matrix is handed over twice! 
				base  = work2; 
			final double[][] tmp = base; base = power; power = tmp; 
		}
		return base; }

	/** Returns whether every row of the matrix is 'simple' (all elements equal).
	 * @param base the Matrix to check
	 * @return true when the Matrix consists only of 'simple' Rows
	 */
	public static final boolean IS_MATRIX_SIMPLE(final double[][] base) {
		for(int j = base.length; --j >= 0;) {
			if (!IS_ROW_SIMPLE(base[j]))
				return false; 
		}
		return true; 
	}
	
	/** Returns whether every element of the row equals its first element.
	 * @param row the Row to check
	 * @return true when all Elements of the Row have the same Value.
	 */
	public static final boolean IS_ROW_SIMPLE(final double[] row) {
		for(int k = row.length; --k > 0;) {
			if (!ByRefDouble.EQUALS(row[k], row [0]))
				return false; 
		}
		return true; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods for Calculations on Polygons
	////////////////////////////////////////////////////////////////////////////////

	/** Returns this polygon's extent as a two-row array of per-column minimum and maximum values.
	 * @return the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 	 */
	public double[][] getExtent() { return EXTENT(null, this.items, 0, this.itemCount); }

	/** 
	 * returns the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 
	 * 
	 * @param arg the Vectors to calculate the Extent for 
	 * @return the Extent of the Polygon
	 */
	final static public double[][] EXTENT(final double[][] arg) { return EXTENT(null, arg, 0, arg.length); } 

	/** 
	 * returns the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 
	 * 
	 * @param min_max an existing Extent can be extended
	 * @param arg the Vectors to calculate the Extent for 
	 * @return the Extent of the Polygon
	 */
	final static public double[][] EXTENT(double[][] min_max, final double[][] arg) {
		return EXTENT(min_max, arg, 0, arg.length);
	} 

	/** 
	 * returns the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 
	 * 
	 * @param min_max an existing Extent can be extended
	 * @param arg the Vectors to calculate the Extent for 
	 * @param start first Index (included)
	 * @param stop   last Index (excluded)
	 * @return the Extent of the Polygon
	 */
	final static public double[][] EXTENT(double[][] min_max, final double[][] arg, final int start, final int stop) {
		if (stop <= 0) //Optimization: 
			return null; 
		if((min_max == null) || (min_max.length < 2))
			min_max =  new double[2][]; 
		COL_MIN(arg, min_max[0]); //, start, stop);
		COL_MAX(arg, min_max[1]); //, start, stop);
		return min_max;
	}

	/**Calculates the Length of the given Path	 */
	final static public double PATH_LENGTH(final double[][] x, final boolean closed, final int[] order) {
		return PATH_LENGTH(x, closed, order, 0, x.length); }

	/**Calculates the geometric Length of the given (closed) Path
	 * @see graphic.AGraph2D#drawPolygon(int[], int[], boolean)
	 */
	final static public double PATH_LENGTH(final double[][] x, final boolean closed, final int[] order, final int start, final int stop) {
		double path = 0;
		int i = stop;
		int i2 = (order != null ? order[0] : 0);
		if (!closed) { --i; 
			i2 = (order != null ? order[i] : i);
		}
		for (; --i >= start;) {	//the absolute Path doesn't really matter!
			int i1 = i2; i2 = (order != null ? order[i] : 0);
			path += VectorDouble.DIST(x[i1],x[i2]);
		}
		return path; }

	/** Returns the Vectors orthogonal to the Points (only for 3Dim Tensors!)
	 * It is only slightly more effective to calculate all Normals at once	 
	 * @param numPoints The total Number of Points i.e. Vertices 
	 * @param planes The Plane Definitions i.e. the Lists of Vertices to each Plane 
	 * @param planeNormals the List of Normals for each Plane
	 * @return a List of Normals 
	 */
	final static public double[][] POINT_NORMALS(final int numPoints
	, final int[][] planes, final double[][] planeNormals) {	//should be protected!
		final double[][] pointNormals = new double[numPoints][];
		//Optimization: O(Pln)*3: just sum up all Normals on the Fly
		//instead of searching for specific Points! 
		for(int i = planes.length; --i >= 0;) { //
			final int[] plane  = planes[i];
			for(int j = plane.length; --j >= 0; ) { 
				final int point = plane[j]; //
				if (pointNormals[point] != null) { 
					VectorDouble.ADD_AT(pointNormals[point], planeNormals[i]);
				} else { //Optimization: copy instead of add, if possible
					pointNormals[point] = VectorDouble.COPY(planeNormals[i]); 
				}
			}
		}
		for (int point = numPoints; --point >= 0;) {
			//pointNormals[point] = PointNormal(planes, pointNormals, point); //O(Pt*Pln*3)
			VectorDouble.NORMALIZE_AT(pointNormals[point]);
		} //
		return pointNormals; 
	}

	/** Calculates a single Point Normal by summing up the neighboring Plane Normals.  
	 * 
	 * @param planes The Plane Definitions i.e. the Lists of Vertices to each Plane 
	 * @param planeNormals the List of Normals for each Plane
	 * @param point The point for which to calculate the Normal
	 * @return the Normal 
	 */ 
	final static public double[] POINT_NORMAL(
		final int[][] planes,
		final double[][] planeNormals,
		final int point) {
		double[] sum = null;
		for(int i = planes.length; --i >= 0;) { //Search all Planes... 
			final int[] plane  = planes[i];
			for(int j = plane.length; --j >= 0; ) { 
				if (plane[j] == point) { //...for those containing this Point
					if (sum != null) { //Optimization: copy instead of add, if possible
						VectorDouble.ADD_AT(sum, planeNormals[i]);
					} else {
						sum = VectorDouble.COPY(planeNormals[i]); }
				}
			}
		}
		if (sum != null) //due to Norming, the actual Number of Planes 
			VectorDouble.NORMALIZE_AT(sum); //does not matter!
		return sum; }

	/**
	 * Calculates the Distances of all points to the given PointOfView.
	 * @param PointOfView single fixed Point for which to calculate all Distances for 
	 * @param points the points to calculate the Distances to
	 * @return the Distances of the Points from the given Point
	 */
	final static public double[] ABSV_DIST(final double[] PointOfView, final double[][] points) {
		double[] ret = new double[points.length];
		for(int i = points.length; --i >= 0; ) 
			ret[i] = VectorDouble.DIST_ABS(points[i], PointOfView); 
		return ret;
	}

	/** Returns the arithmetic mean of the plane's vertex points.
	 * @param points
	 * @param plane
	 * @return the middle Points of this Plane
	 */
	public static double[] GET_MID_POINT(final double[][] points, final int[] plane) {
		final double[] mid = VectorDouble.COPY(points[plane[0]]);
		for(int j = plane.length; --j > 0;) { 	//Skip the Zero Point
			VectorDouble.ADD_AT(mid, points[plane[j]]); }	//subtract the Offset
		return VectorDouble.MUL_AT(mid, 1.0f / plane.length);
	}

	/** Returns the arithmetic mean point of each plane's vertices.
	 * @param points
	 * @param planes
	 * @return the middle Points of the Planes
	 */
	final static public double[][] GET_MID_POINTS(final double[][] points, final int[][] planes) {
		final double[][] mids = new double[planes.length][];
		for(int i = planes.length; --i >= 0; ) {
			final int[] plane = planes[i]; 
			final double[] mid = GET_MID_POINT(points, plane);
			mids[i] = mid; //arithmetic Mean
		}
		return mids;
	}

	/** calculates the Normal of each Plane using the Cross Product 
	 * from the Difference Vectors of their first three Points. 
	 * @param points
	 * @param planes
	 * @return the Normal of each Plane 
	 */
	final static public double[][] PLANE_NORMALS(final double[][] points
	, final int[][] planes) {
		final double[][] planeNormals = new double[planes.length][];
		final double[] diff1 = new double[planes.length]; //Optimization:
		final double[] diff2 = new double[planes.length]; //reuse 
		for(int i = planes.length; --i >= 0; ) {
			final int[] plane = planes[i];
			if (plane.length < 3) {
				continue; }
			planeNormals[i] = VectorDouble.NORMAL(diff1, diff2
			, points[plane[0]], points[plane[1]], points[plane[2]], true);
		}
		return planeNormals;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : simulated Annealing for Clustering
	////////////////////////////////////////////////////////////////////////////////
	
	/** Number of initial Moves for Estimation of the necessary Temperature	 */
	protected static int NUM_ESTIMATE_MOVES = 10; 
	
	/**Metropolis algorithm, used by anneal() (10.9)
	 * Determines Acceptance, based on the Temperature for Annealing 
	 * The starting Temperature could be determined from the first de, 
	 * so that e.g. the initial estimated Rejection Rate would be 50%, i.e. 
	 * .5 = Math.exp(-de0/t0) <=> -Ln(.5) = de0/t0 <=> t0 = -de0/Ln(.5) = 1.44*de0
	 */
	public static boolean ACCEPT_DELTA_ENERGY(final double de, final float t) {
		return (de < 0) || (Math.random() < Math.exp(-de/t));}
	
	/** Don't need to know the absolute Cost, 
	 * the relative Change is completely sufficient
	 * The Cost of every Cell is it's Weight 
	 * multiplied with the Distance from the Diagonal 
	 * determined by the Difference of Row and Column  
	 * @param x
	 * @param n1
	 * @param n2
	 * @return
	 */
	final static public double COST_OF_SWAP(final double[][] x, final int n1, final int n2) {
		//if (n1 == n2)
		//	return 0; 
		double ret = 0; 
		for(int i=x.length; --i >= 0;) {
			final double[] x_i = x[i];  
			ret += 
				Math.abs(x_i[n1]*(i-n1)) + 
				Math.abs(x_i[n2]*(i-n2)) -
				Math.abs(x_i[n1]*(i-n2)) -
				Math.abs(x_i[n2]*(i-n1)); 
		}
		final double[] x_n1 = x[n1];  
		final double[] x_n2 = x[n2];  
		for(int j=x.length; --j >= 0;) {
			if ((j==n1) || (j==n2))
				continue; //avoid double counting the intersection Elements
			ret += 
				Math.abs(x_n1[j]*(j-n1)) + 
				Math.abs(x_n2[j]*(j-n2)) -
				Math.abs(x_n1[j]*(j-n2)) -
				Math.abs(x_n2[j]*(j-n1)); 
		}
		return ret; 
	}
	
	/** 
	 * tests statistical Diagonalization by permuting Rows and Columns 
	 * either for Rows and Columns independently or coupled.  
	 * @param sumNorm
	 * @return the overall Reduction in Energy from permuting the Indices
	 */
	protected static final double CLUSTER(final double[][] weights) {
		double ret = 0; 
		final int nover=100*weights.length;
		final int nlimit=10*weights.length;
		int numInits = NUM_ESTIMATE_MOVES; //use the first Iterations to determine the appropriate Temperature. 
		float t=0;	//(initial) Temperature
		for (int j=100; --j>=0; ) { //for cooling the System 
			int numAccepted = 0;
			for (int k=nover; --k>=0; ) { //choose two Indices
				int n1=(int) ( weights.length   *Math.random());	//
				int n2=(int) ((weights.length-1)*Math.random());	//
				if (n2 >= n1) ++n2;  	//make sure they're not the same
				final double de = COST_OF_SWAP(weights, n1, n2); 
				if (--numInits > 0) 
					t+=Math.abs(de); 
				else if (numInits == 0) {
					t/=NUM_ESTIMATE_MOVES; L.n("Initial Energy: ").l(t); 
				} else
					if (ACCEPT_DELTA_ENERGY(de,t)) {
						ret += de; ++numAccepted; 
						SWAP_COLS_AT(weights, n1, n2);
						SWAP_ROWS_AT(weights, n1, n2); 
					}
				if (numAccepted >= nlimit) {
					break; } 
			}
			t *= 0.9f;
			if (numAccepted == 0) break;
		}
		return ret; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Permutation Methods
	////////////////////////////////////////////////////////////////////////////////

	/** copy original math.matrix into a newer (larger) one, 
	 * shifts 1 Row down and 1 Column right 
	 * @param a Matrix to shift
	 * @return a new Matrix
	 */
	final static public double[][] SHR(final double[][] a) {
		final int m = a.length;
		final int n = a[0].length;
		final double[][] u=new double[1+m][1+n];
		for (int k=1; k<=m; k++) {
			for (int l=1; l<=n; l++) 
				u[k][l]=a[k-1][l-1];
		}
		return u;
	}
	
	/**
	 * As an Optimization, a single new Row Vector is used as temporary Storage for the Permutation of all Rows.   
	 * @return this Vector with the Columns permuted according to the given Permutation     */
	final static public double[][] PERMUTE_COLS(final double[][] a, final int[] index) {
		return PERMUTE_COLS(a, index, false); }
	
	/**
	 * As an Optimization, a single new Row Vector is used as temporary Storage for the Permutation of all Rows.   
	 * @return this Vector with the Columns permuted according to the given Permutation     */
	final static public double[][] PERMUTE_COLS(final double[][] a, final int[] index, final boolean reverse) {
		final double[][] ret = new double[a.length][]; 
		for (int i = a.length; --i >= 0;) 
			ret[i] = HunterDouble.PERMUTE(a[i], index, reverse); 
		return ret; 
	}
	
	/**
	 * As an Optimization, a single new Row Vector is used as temporary Storage for the Permutation of all Rows.   
	 * @return this Vector with the Columns permuted according to the given Permutation     */
	final static public double[][] PERMUTE_COLS_AT(final double[][] a, final int[] index) {
		return PERMUTE_COLS_AT(a, index, false); 
	}
	
	/**
	 * As an Optimization, a single new Row Vector is used as temporary Storage for the Permutation of all Rows.   
	 * @return this Vector with the Columns permuted according to the given Permutation     */
	final static public double[][] PERMUTE_COLS_AT(final double[][] a, final int[] index, final boolean reverse) {
		double[] tmp, swp = new double[index.length];
		for (int i = a.length; --i >= 0;) {
			tmp = a[i]; a[i] = HunterDouble.PERMUTE(tmp, index, reverse, swp);
			swp = tmp;
		}
		return a;
	}

	/** 
	 * The Row Vectors are being reused, so no real Copy is created! 
	 * Don't use this in Vector Operations, because a temporary Array is created. 
	 * @return this Matrix with the Rows permuted according to the given Permutation     
	 */
	final static public double[][] PERMUTE_ROWS(final double[][] a, final int[] index, final boolean reverse) {
		return PERMUTE_ROWS(null, a, index, reverse);
	}

	/** 
	 * The Row Vectors are being reused, so no real Copy is created! 
	 * Don't use this in Vector Operations, because a temporary Array is created. 
	 * @return this Matrix with the Rows permuted according to the given Permutation    
	 */
	final static public double[][] PERMUTE_ROWS(double[][] ret, final double[][] a, final int[] index, final boolean reverse) {
		if (ret == null)
			ret = new double[a.length][]; 
		for (int i = index.length; --i >= 0;) {
			if (reverse)
				ret[index.length-1 - i] = a[index[i]];
			else
				ret[i] = a[index[i]];
		}
		return ret;
	}

	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public double[][] PERMUTE_ROWS_AT(final double[][] a, final int[] index, final boolean reverse) {
		final double[][] tmp = new double[a.length][];
		PERMUTE_ROWS(tmp, a, index, reverse);
		System.arraycopy(tmp, 0, a, 0, a.length);
		return a;
	}
	/// The following Code does not work, because in Place is not possible!
	/*		double[] tmp;	//Undo the Row Permutations!
			int j, k = a.length;
			while (--k >= 0) { //not a proper Permutation! sensitive to Sequence of Processing!
				if (perm[k] == k) {
					continue; }
				tmp = a[k]; a[k] = a[j = perm[k]]; a[j] = tmp; }
			return a; }
	*/
	
	/** 
	 * Sorts the Rows in this Matrix into a new Matrix so the Maximum ends in the last Row. 
	 * This Operation is idempotent and invariant to Column Swaps. 
	 * The Row Vectors are being reused, so no real Copy is created! 
	 * @param useSum uses the Sum instead of the maximum Value in each Row
	 * @return a new Matrix sharing the Rows 
	 */
	final static public double[][] SORT_ROWS_BY_MAX(final double[][] a, final boolean useSum, final boolean reverse) {
		return PERMUTE_ROWS(a, INDEX_ROWS_BY_MAX(a, useSum), reverse);
	}

	/** 
	 * Sorts the Rows in this Matrix so the Maximum ends in the last Row. 
	 * This Operation is idempotent and invariant to Column Swaps
	 * @param useSum uses the Sum instead of the maximum Value in each Row
	 * @return a new Matrix sharing the Rows 
	 */
	final static public int[] INDEX_ROWS_BY_MAX(final double[][] a, final boolean useSum) {
		final double[] maxVals = new double[a.length];
		if (useSum) {
			ROW_SUM(a, 0, a.length, maxVals); //Sum of Values (only for positive Values)
		} else {
			MAX_VAL(maxVals, a, 0, a.length); //actual Maximums
		}
		//sort by creating an Index to sort the Columns accordingly
		final int[] indexRow = HunterDouble.INDEX(maxVals);
		return indexRow; 
	}
	
	/** 
	 * Sorts the Rows in this Matrix so the Row with Maximum weighed Sum (Scalar Product) ends up last
	 * (or first if reverse = true). 
	 * This Mechanism is recommended also for the so-called 'Balanced Scorecard'
	 * @param a the Matrix to sort 
	 * @param weights the Weights to apply to each Column
	 * @param reverse Flag to revert the Sort Order 
	 * @return a new Matrix with the same Rows sorted in ascending weighed Order 
	 */
	final static public double[][] SORT_ROWS_BY_WEIGHED_SUM(final double[][] a, final double[] weights, final boolean reverse) {
		final int[] indexRow = INDEX_ROWS_BY_WEIGHED_SUM(a, weights); 
		//Sort the Rows into a new Matrix so that tha Maximum Maximum ends up at the Top
		final double[][] tmp = PERMUTE_ROWS(a, indexRow, reverse);
		return tmp;
	}
	
	/** 
	 * Indexes the Rows in this Matrix so the Row with Maximum weighed Sum (Scalar Product) ends up last
	 * @return the Index to the Rows to be used... 
	 */
	final static public int[] INDEX_ROWS_BY_WEIGHED_SUM(final double[][] a, final double[] weights) {
		final double[] maxVals = MAP(a, weights);
		//sort by creating an Index to sort the Columns accordingly
		final int[] indexRow = HunterDouble.INDEX(maxVals);
		return indexRow;
	}
	
	/** 
	 * Sorts the Rows in this Matrix so the Maximum ends in the last Row.  
	 * This Operation is idempotent and invariant to Row Swaps. 
	 * @param useSum uses the Sum instead of the maximum Value in each Column
	 * @return the same Matrix with swapped Columns 
	 */
	final static public double[][] SORT_COLS_BY_MAX(final double[][] a, final boolean useSum, final boolean reverse) {
		return PERMUTE_COLS(a, INDEX_COLS_BY_MAX(a, useSum), reverse); }

	/** 
	 * Sorts the Rows in this Matrix so the Maximum ends in the last Row.  
	 * This Operation is idempotent and invariant to Row Swaps. 
	 * @param useSum uses the Sum instead of the maximum Value in each Column
	 * @return the same Matrix with swapped Columns 
	 */
	final static public double[][] SORT_COLS_BY_MAX_AT(final double[][] a, final boolean useSum, final boolean reverse) {
		return PERMUTE_COLS_AT(a, INDEX_COLS_BY_MAX(a, useSum), reverse);
		//VectorDouble.permuteAt(maxVals, indexRow); //just to check...
	}

	/** 
	 * Sorts the Rows in this Matrix so the Maximum ends in the last Row.  
	 * This Operation is idempotent and invariant to Row Swaps. 
	 * @param useSum uses the Sum instead of the maximum Value in each Column
	 * @return the same Matrix with swapped Columns 
	 */
	final static public int[] INDEX_COLS_BY_MAX(final double[][] a, final boolean useSum) {
		final double[] maxVals = new double[a[0].length];
		if (useSum) {
			COL_SUM(a, 1, a.length, maxVals); //Sum of Values (only for positive Values)
		} else { //take first Row and ...
			System.arraycopy(a[0], 0, maxVals, 0, a[0].length); //... compare only the Rest
			COL_MAX(a, 1, a.length, maxVals); //actual Maximums of Columns
		}
		//sort by creating an Index
		final int[] indexRow = HunterDouble.INDEX(maxVals);
		return indexRow; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Swaps the Columns of this Tensor in Place	 */
	final static public double[][] SWAP_COLS(final double[][] a, final int Dim1, final int Dim2) {
		return SWAP_COLS_AT(COPY(a), Dim1, Dim2);
	}

	/**Swaps the Columns of this Tensor in Place	 */
	final static public double[][] SWAP_COLS_AT(final double[][] a, final int dim1, final int dim2) {
		if (dim1 == dim2) 
			return a; 
		for (int i = a.length; --i >= 0;) {
			HunterDouble.SWAP_AT(a[i], dim1, dim2); }
		return a;
	}

	/** Swaps the Columns and Rows of this Tensor in Place	 */
	final static public double[][] SWAP_ROWS_COLS_AT(final double[][] a, final int dim1, final int dim2) {
		final double[][] ret = SWAP_ROWS_AT(a, dim1, dim2);
		return SWAP_COLS_AT(ret, dim1, dim2);
	}

	/**Swaps the Rows of this Tensor in Place	 */
	final static public double[][] SWAP_ROWS(final double[][] a, final int dim1, final int dim2) {
		return SWAP_ROWS_AT(COPY(a), dim1, dim2); }

	/**Swaps the Rows of this Tensor in Place	 */
	final static public double[][] SWAP_ROWS_AT(final double[][] a, final int dim1, final int dim2) {
		final double[] c = a[dim1];
		a[dim1] = a[dim2];
		a[dim2] = c;
		return a;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** @return the Minimum and Maximum Values of each Column... 
	 * too complex to optimize for now... 
	 * 
	 * Use Min and Max separately, which is clearer too!
	 */
	//	final static public double[][] MIN_MAX(double[][] arr) { }

	/** Returns the minimum value of each column across all rows.
	 * @return the Minimum Values of each Column in Place
	 */
	final static public double[] MIN(final double[][] arg) {
		return MIN(null, arg, 0, arg.length); }

	/** Returns the minimum value of each column across all rows, into the given array.
	 * @return the Minimum Values of each Column in Place
	 */
	final static public double[] MIN(final double[] ret, final double[][] arg) {
		return MIN(ret, arg, 0, arg.length); }

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	final static public double[] MIN(final double[][] arg, final int start, final int stop) {
		return MIN(null, arg, start, stop); }

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	final static public double[] MIN(double[] ret, final double[][] arg, final int start, final int stop) {
		//return Min(.fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		if (stop <= 0) //Optimization: 
			return null; 
		int i = stop-1;
		if (ret == null) 
			ret = VectorDouble.COPY(arg[i]);
		else 
			VectorDouble.COPY(arg[i], ret);
		for (; --i >= start;) 
			VectorDouble.MIN_AT(ret, arg[i]);
		return ret;
	}

	/** Returns the maximum value of each column across all rows.
	 * @return the Maximum Values of each Column */
	final static public double[] MAX(final double[][] arg) {
		return MAX(null, arg, 0, arg.length); }

	/** Returns the maximum value of each column across all rows, into the given array.
	 * @return the Maximum Values of each Column */
	final static public double[] MAX(final double[] ret, final double[][] arg) {
		return MAX(ret, arg, 0, arg.length); }
	
	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	final static public double[] MAX(final double[][] arg, final int start, final int stop) {
		return MAX(null, arg, start, stop); }
	
	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	final static public double[] MAX(double[] ret, final double[][] arg, final int start, final int stop) {
		//return Min(VectorDouble.fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		if (stop <= 0)  //Optimization: 
			return null; 
		int i = stop-1;
		if (ret == null) 
			ret = VectorDouble.COPY(arg[i]);
		else 
			VectorDouble.COPY(arg[i], ret);
		
		for (; --i >= start;) 
			VectorDouble.MAX_AT(ret, arg[i]);
		
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Adds {@code increment * factor}, row by row, to the matrix in place.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public double[][] ADD_PROD_AT(final double[][] ret, final double[][] increment, final double factor) {
		return ADD_PROD_AT(ret, increment, factor, 0, ret.length); }

	/** Adds {@code increment * factor}, row by row, to the matrix within the given row range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[][] ADD_PROD_AT(final double[][] ret, final double[][] increment, final double factor, final int start, int stop) {
		while (--stop >= start) 
			VectorDouble.ADD_PROD_AT(ret[stop], increment[stop], factor);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Adds {@code increment * factor} to every row, in place.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public double[][] addProdAt(final double[][] ret, final double[] increment, final double factor) {
		return ADD_PROD_AT(ret, increment, factor, 0, ret.length); }

	/** Adds {@code increment * factor} to every row in the given range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[][] ADD_PROD_AT(final double[][] ret, final double[] increment, final double factor, final int start, int stop) {
		while (--stop >= start) 
			VectorDouble.ADD_PROD_AT(ret[stop], increment, factor);
		return ret;
	}

	/** Adds {@code increment * factor} (given as a float vector) to every row, in place.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public double[][] ADD_PROD_AT(final double[][] ret, final float[] increment, final double factor) {
		return ADD_PROD_AT(ret, increment, factor, 0, ret.length); }

	/** Adds {@code increment * factor} (given as a float vector) to every row in the given range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[][] ADD_PROD_AT(final double[][] ret, final float[] increment, final double factor, final int start, int stop) {
		while (--stop >= start) 
			VectorDouble.ADD_PROD_AT(ret[stop], increment, factor);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public double[][] ADD_AT(final double[][] ret, final double increment) {
		return ADD_AT(ret, increment, 0, ret.length);
	}

	/** Adds the given scalar increment to every row in the given range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[][] ADD_AT(final double[][] ret, final double increment, final int start, int stop) {
		while (--stop >= start) 
			VectorDouble.ADD_AT(ret[stop], increment);
		return ret;
	}

	/** Adds the given vector to every row, in place.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public double[][] ADD_AT(final double[][] ret, final double[] increment) {
		return ADD_AT(ret, increment, 0, ret.length); }

	/** Adds the given vector to every row in the given range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[][] ADD_AT(final double[][] ret, final double[] increment, final int start, int stop) {
		while (--stop >= start) 
			VectorDouble.ADD_AT(ret[stop], increment);
		return ret;
	}

	/** Adds the given float vector to every row, in place.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public double[][] ADD_AT(final double[][] ret, final float[] increment) {
		return ADD_AT(ret, increment, 0, ret.length); }

	/** Adds the given float vector to every row in the given range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[][] ADD_AT(final double[][] ret, final float[] increment, final int start, int stop) {
		while (--stop >= start) 
			VectorDouble.ADD_AT(ret[stop], increment);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Subtracts the given vector from every row, in place.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public double[][] SUB_AT(final double[][] ret, final double[] decrement) {
		return SUB_AT(ret, decrement, 0, ret.length); }

	/** Subtracts the given vector from every row in the given range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[][] SUB_AT(final double[][] ret, final double[] decrement, final int start, int stop) {
		while (--stop >= start) 
			VectorDouble.SUB_AT(ret[stop], decrement);
		return ret;
	}

	/** Subtracts the given float vector from every row in the given range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[][] SUB_AT(final double[][] ret, final float[] decrement, final int start, int stop) {
		while (--stop >= start) 
			VectorDouble.SUB_AT(ret[stop], decrement);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Increases the capacity of this VectorInt, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final static public synchronized double[][] SET_CAPACITY(
			final int minCapacity, final double[][] items, final int itemCount) {
		final int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) 
			return items;
		final double[][] newData = new double[minCapacity][];
		if (oldCapacity > 0) 
			System.arraycopy(items, 0, newData, 0, itemCount);
		return newData;
	}

	/**Ensures the capacity of this VectorInt, 
	 * so that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final static public synchronized double[][] SET_CAPACITY(
			final int minRows, final int minCols, 
			final double[][] items, final int itemCount) {
		double[][] ret = SET_CAPACITY(minRows, items, itemCount); 
		for (int i = minRows; --i >= 0; ) 
			ret[i] = VectorDouble.SET_CAPACITY(minCols, ret[i]); 
		return ret; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Returns a new array holding the sum of every row's values.
	 * @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public double[] ROW_SUM(final double[][] arr) {
		return ROW_SUM(arr, 0, arr.length, new double[arr.length]);
	}

	/** Fills the given array with the sum of every row's values.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public double[] ROW_SUM(final double[][] arr, final double[] ret) {
		return ROW_SUM(arr, 0, arr.length, ret);
	}

	/** Fills the given array with the sum of each row's values within the given row range.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public double[] ROW_SUM(final double[][] arr, final int start, int stop, double[] ret) {
		int len = arr[0].length;
		while (--stop >= start) 
			ret[stop] = VectorDouble.SUM(arr[stop], 0, len); 
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////
	
	/** Returns a new array holding the sum of every column's values.
	 * @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public double[] COL_SUM(final double[][] arr) {
		return COL_SUM(arr, 1, arr.length, VectorDouble.COPY(arr[0]));
	}

	/** Fills the given array with the sum of every column's values.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public double[] COL_SUM(final double[][] arr, final double[] ret) {
		return COL_SUM(arr, 0, arr[0].length, ret);
	}

	/** Fills the given array with the sum of every column's values within the given row range.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public double[] COL_SUM(double[][] arr, int startRow, int stopRow, double[] ret) {
		while (--stopRow >= startRow) {
			VectorDouble.ADD_AT(ret, arr[stopRow], 0, ret.length); }
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////
	
	/** Returns a new array holding the maximum value of every column.
	 * @return The Maximum Vector of all Rows as Values in the Array. 	 */
	final static public double[] COL_MAX(final double[][] arr) {
		return COL_MAX(arr, 1, arr.length, VectorDouble.COPY(arr[0]));
	}

	/** Fills the given array with the maximum value of every column, or delegates when {@code ret} is null.
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public double[] COL_MAX(final double[][] arr, final double[] ret) {
		if (ret == null)
			return COL_MAX(arr); //more effective!
		return COL_MAX(arr, 0, arr.length, ret);
	}

	/** Fills the given array with the maximum value of every column within the given row range.
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public double[] COL_MAX(final double[][] arr, final int startRow, int stopRow, final double[] ret) {
		while (--stopRow >= startRow) 
			VectorDouble.MAX_AT(ret, arr[stopRow], 0, ret.length);
		return ret;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Returns a new array holding the minimum value of every column.
	 * @return The Maximum Vector of all Rows as Values in the Array. 	 */
	final static public double[] COL_MIN(final double[][] arr) {
		return COL_MIN(arr, 1, arr.length, VectorDouble.COPY(arr[0]));
	}

	/** Fills the given array with the minimum value of every column, or delegates when {@code ret} is null.
	 * @para ret the return Vector. To contain the Maximum, it must be set to Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public double[] COL_MIN(final double[][] arr, final double[] ret) {
		if (ret == null)
			return COL_MIN(arr); //more effective!
		return COL_MIN(arr, 0, arr.length, ret);
	}

	/** Fills the given array with the minimum value of every column within the given row range.
	 * @para ret the return Vector. To contain the Maximum, it must be set to Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public double[] COL_MIN(final double[][] arr, final int startRow, int stopRow, final double[] ret) {
		while (--stopRow >= startRow) 
			VectorDouble.MIN_AT(ret, arr[stopRow], 0, ret.length);
		return ret;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Copies each row reference (not the row contents) from {@code arr} into {@code ret}.
	 * @return a shallow Copy of the given Matrix */
	final static public double[][] SHALLOW_COPY_AT(final double[][] ret, final double[][] arr) {
		int len;
		if (ret.length != (len = arr.length)) 
			throw new IndexOutOfBoundsException("Expected: " + ret.length + " Actual: " + arr.length);
		while (--len >= 0) 
			ret[len] = arr[len];
		return ret;
	}

	/** Copies every row's values from {@code arr} into the matching row of {@code ret}.
	 * @return a deep Copy of the given Matrix */
	final static public double[][] COPY_AT(final double[][] ret, final double[][] arr, final int start, int stop) {
		while (--stop >= start) {
			System.arraycopy(arr[stop], 0, ret[stop], 0, arr[stop].length);
		} //Optimization!
		//			VectorDouble.copyAt(ret[stop], arr[stop]); }
		return ret;
	}

	/** Copies the given row's values into every row of {@code ret} within the given range.
	 * @return the Matrix ret with deep Copie of the given Vector arr in every Row */
	final static public double[][] COPY_AT(final double[][] ret, final double[] arr, final int start, int stop) {
		while (--stop >= start) {
			//VectorDouble.copyAt(ret[stop], arr);
			System.arraycopy(arr, 0, ret[stop], 0, arr.length);
		} //Optimization!
		return ret;
	}

	/** Copies the given float row's values into every row of {@code ret} within the given range.
	 * @return the Matrix ret with deep Copie of the given Vector arr in every Row */
	final static public double[][] COPY_AT(final double[][] ret, final float[] arr, final int start, int stop) {
		while (--stop >= start) {
			VectorDouble.COPY_AT(ret[stop], arr); 
		} 
		return ret;
	}

	/** Returns a deep copy of the given double matrix.
	 * @return a deep Copy of the given Matrix */
	final static public double[][] COPY(final double[][] arr) {
		int len;
		final double[][] ret = new double[len = arr.length][];
		while (--len >= 0) {
			ret[len] = VectorDouble.COPY(arr[len]);
		}
		return ret;
	}

	/** Returns a new double matrix converted (widened) from the given float matrix.
	 * @return a deep Copy of the given Matrix */
	final static public double[][] COPY(final float[][] arr) {
		int len;
		final double[][] ret = new double[len = arr.length][];
		while (--len >= 0) {
			ret[len] = VectorDouble.COPY(arr[len]);
		}
		return ret;
	}

	/** Returns a new double matrix converted from the given int matrix.
	 * @return a deep Copy of the given Matrix */
	final static public double[][] COPY(final int[][] arr) {
		int len;
		double[][] ret = new double[len = arr.length][];
		while (--len >= 0) {
			ret[len] = VectorDouble.COPY(arr[len]);
		}
		return ret;
	}

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public double[][] SET_DIM_AT(final double[][] a, final int dim) {
		if (a.length == dim) 
			return a;
		double[][] ret = new double[dim][];
		System.arraycopy(a, 0, ret, 0, a.length);
		//		Arrays.fill(ret, a.length, dim, 0);
		return a;
	}

	/** Multiplies every row by the given scalar factor, in place.
	 * @return the given Array multiplied in Place by the given Factor */
	final static public double[][] MUL_AT(final double[][] ret, final double factor) {
		return MUL_AT(ret, factor, 0, ret.length);
	}

	/** Multiplies every row in the given range by the given scalar factor, in place.
	 * @return the given Array multiplied in Place by the given Factor */
	final static public double[][] MUL_AT(final double[][] ret, final double factor, final int start, int stop) {
		while (--stop >= start) {
			VectorDouble.MUL_AT(ret[stop], factor); }
		return ret;
	}

	/** Returns {@code arr} scaled by the given factor, written into {@code ret}.
	 * @return the given Array multiplied in Place by the given Factor */
	final static public double[][] MUL(final double[][] ret, final double[][] arr, final double factor) {
		return MUL(ret, arr, factor, 0, arr.length); }

	/** Returns a new matrix holding {@code arr} scaled by the given factor.
	 * @return the given Array multiplied in Place by the given Factor */
	final static public double[][] MUL(final double[][] arr, final double factor) {
		return MUL(null, arr, factor, 0, arr.length); }

	/** Fills {@code ret} (or a new array) with {@code arr}'s rows in the given range scaled by the given factor.
	 * @return the given Array multiplied in Place by the given Factor */
	final static public double[][] MUL(double[][] ret, final double[][] arr, final double factor, final int start, int stop) {
		if (ret == null) {
			ret =  new double[stop][]; }
		while (--stop >= start) {
			ret[stop] = VectorDouble.MUL(arr[stop], factor); }
		return ret;
	}

	/**
	 * This allows to multiply only a certain rectangular Range in the Target Matrix.
	 * @return the given Array multiplied in Place by the given Factor */
	final static public double[][] MUL_AT(
		final double[][] ret, final double Factor,
		final int StartRow,       int StopRow,
		final int StartCol, final int StopCol) {
		while (--StopRow >= StartRow) 
			VectorDouble.MUL_AT(ret[StopRow], Factor, StartCol, StopCol);
		return ret;
	}

	/** Returns a new matrix holding the transpose of the given (possibly non-square) matrix.
	 * @return the Transpose of the given Array  */
	public static double[][] TRP(final double[][] a) {
		//return trpAt(copy(a)); //too slow, doesn't work for non-square Matrices!!!
		double[][] ret = new double[a[0].length][a.length]; //make it rectangular
		for(int i = ret.length; --i >= 0; ) {
			for (int j = a.length; --j >= 0; ) {
				ret[i][j] = a[j][i];
			}
		}
		return ret;
	}

	/**
	 * transposes the given Array in Place
	 * works only for square Matrices.
	 * Use trp otherwise.
	 */
	public static double[][] TRP_AT(final double[][] ret) {
		for (int i = ret.length; --i >= 0; ) {
			for (int j = i + 1; --j >= 0; ) {
				final double tmp = ret[i][j];
				ret[i][j] = ret[j][i];
				ret[j][i] = tmp;
			}
		}
		return ret;
	}

	/** Randomizes all the Weights of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Assumes a rectangular Array. 	 */
	final static public double[][] RANDOMIZE_AT_1_1(final double[][] arr) {
		int j = arr.length;
		while (--j >= 0) 
			VectorDouble.RANDOMIZE_AT_1_1(arr[j]);
		return arr;
	}

	/** Randomizes all the Weights of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Assumes a rectangular Array. 	 */
	final static public double[][] RANDOMIZE_AT(final double[][] arr) {
		int j = arr.length;
		while (--j >= 0) 
			VectorDouble.RANDOMIZE_AT(arr[j]);
		return arr;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for fuzzy Set and Matrix Operations
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Calculates the full euklidean Adjacency Matrix
	 * generated from all the Distances between the given Points.
	 * Calculates all possible Distances from all Points to all others:
	 *
	 * ret[i,k] = {Sum(j), Vectors[i,j]*Vectors[k,j]}
	 */
	public static double[][] DIST_MATRIX(final double[][] ret, final double[][] Vectors) {
		for (int i = Vectors.length;--i >= 0;) { //symmetric Matrix
			int j = Vectors.length;
			double[] I = Vectors[i]; //initialize the whole Matrix, O(V^2)
			double[] A = ret[i]; //
			A[i] = 0; //not necessary, because new Array contains 0s already!
			while (--j > i) { //symmetric Matrix //calculate only 50%!
				A[j] = ret[j][i] = Math.sqrt(VectorDouble.DIST_SQR(I, Vectors[j])); //Symmetric!
			}
		}
		return ret;
	}

	/**
	 * This Implication Matrix or FAM (Fuzzy Associative Matrix)  
	 * is the dyadic "Product" of two Fuzzy Vectors, 
	 * also called the "Mamdani Implication" (1977) (72 different Implications are proposed) 
	 * This is used in concatenating Mappings of Fuzzy Sets and in Game Theory.
	 * m[i,j] = a[i] min b[j] 
	 * Each Value of the Matrix is the fuzzy Value of the logical Proposition 
	 * a[i] AND b[i]
	 * 
	 * This Matrix is not necessarily square and has to be defuzzified by some Means, 
	 * to evaluate it for one or more fuzzy for crisp Control Values. 
	 * To this End you take the Implications defined in your Rule Base 
	 * and average, sum or max them up to get a Fuzzy Membership Function 
	 * for the Output 
	 * float Measurement -> discrete Fuzzy Variables is simple (e.g. FuzzyNumber) 
	 * Fuzzy Variables -> crisp output is harder: 
	 * you have to integrate the possible Output Functions with their Rules: 
	 * These Rules should have a Truth Value of 1 (maximized), 
	 * but you can also assign Weights to them! 
	 * a & b => c <=> (a AND b) IMP c <=> NOT(c AND NOT(a AND b))
	 * <=>  NOT c OR (a AND b) == 1 <=> (1-c) max (a min b) == 1
	 * 
	 * Using the fuzzy Centrouid you determine the actual Value by 
	 * integrating the Function just like for the Center of Mass: 
	 * Sum(weight*ordinate)/Sum(weight)
	 * 
	 * N continuous Input Variables (usually 2) 
	 * each determine the Memberships of an (odd) Number of Characters. 
	 * (to allow for a Center with no Control Action) 
	 * The Memberships are multiplied into the Rule Base(s) 
	 * consisting of a Tensor(s) of Nth Degree. 
	 * Each Tensor determines one fuzzy Control Action. 
	 * To defuzzify this Action back into a (continuous) Output Variable, 
	 * different Algorithms are possible:
	 * MinMax Algorithm: select the Center of the Control with the highest Membership
	 * 		this might lead to discontinuous Output. 
	 * The MaxDot Algorithm: every Control Membership Function 
	 * 		is scaled with their Tensor Membership, summed up 
	 * 		and the common Center of Weight is chosen as Output. 
	 * The Centroid Algorithm: every Control Membership Function 
	 * 		is clipped to it's Tensor Membership, summed up 
	 * 		and the common Center of Weight is chosen as Output. 
	 * 		Since the Control Value is usually bounded, 
	 * 		Summation is guaranteed to be finite and not to dominate all others
	 * 		even with minimal Values of Tensor Membership. 
	 * Actually the concrete Method doesn't matter so much. 
	 * Neither does the Shape of the Membership Function (Triangles or Trapezoids) 
	 * For Defuzzification with the MaxDot Algorighm, since it is a linear Function, 
	 * you can possibly pre-calculate the Center and Weight of each fuzzy Number 
	 * and scale
	 */
	final static public double[][] BinaryImplication(final double[] l, final double[] r) {
		final double[][] ret = new double[l.length][r.length];
		for (int i = l.length; --i >= 0; ) {
			final double li = l[i];
			final double[] reti = ret[i];
			for (int j = r.length; --j >= 0; ) {
				if((reti[j] = r[j]) < li) {
					continue; }
				reti[j] = li;
			}
		}
		return ret;
	}

	/**
	  * The dyadic Cross Product of two Vectors
	  * m[i,j] = a[i]*b[j]
	  */
	final static public double[][] DYAD_PROD(final double[] l, final double[] r) {
		double[][] ret = new double[l.length][r.length];
		double[] reti;
		double li;
		int j, i = l.length;
		while (--i >= 0) {
			li = l[i];
			reti = ret[i];
			j = r.length;
			while (--j >= 0) {
				reti[j] = li * r[j];
			}
		}
		return ret;
	}

	/** Scalar Product Multiplication: �
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is identical to the Derivative Jacobian Matrix of this linear Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  * The last  Index of the first Argument A has to match
	  * the first Index of the last  Argument B
	  * @param a the left  Row Vector
	  * @param b the right Matrix
	  * @return the Product Vector A*B
	  */
	final static public double[] MAP(final double[][] b, final double[] a) { //previously named mul()
		return MAP(b, a, b[0].length, 0);
	}
	
	/** Scalar Product Multiplication: �
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * B�A with A being a Column Vector multiplied from the right
	  *
	  * The Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is identical to the Derivative Jacobian Matrix of this linear Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  * The last  Index of the first Argument A has to match
	  * the first Index of the last  Argument B
	 * @param b the right Matrix
	 * @param a the left  Row Vector
	  * @return the Product Vector A*B
	  */
	final static public double[] MAP(final double[][] b, final double[] a, final int stop, final int start) { //previously named mul()
		final double[] ret = new double[b.length];
		for(int i = stop; --i >= start;) //saves Initialization and one Addition!
			ret[i] = VectorDouble.MAP(b[i], a);
		return ret;
	}

	/** Scalar Product Multiplication: �
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is identical to the Derivative Jacobian Matrix of this linear Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  * The last  Index of the first Argument A has to match
	  * the first Index of the last  Argument B
	  * @param a the left  Row Vector
	  * @param b the right Matrix
	  * @return the Product Vector A*B
	  */
	final static public double[] MAP(final double[] a, final double[][] b) { //previously named mul()
		return MAP(a, b, b[0].length, 0); }

	/** Scalar Product Multiplication: �
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is identical to the Derivative Jacobian Matrix of this linear Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  * The last  Index of the first Argument A has to match
	  * the first Index of the last  Argument B
	  * @param a the left  Row Vector
	  * @param b the right Matrix
	  * @return the Product Vector A*B
	  */
	final static public double[] MAP(final double[] a, final double[][] b, final int stop, final int start) { //previously named mul()
		return MAP(a, b, stop, start, null); }
	
	/** Scalar Product Multiplication: �
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is identical to the Derivative Jacobian Matrix of this linear Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  * The last  Index of the first Argument A has to match
	  * the first Index of the last  Argument B
	  * @param a the left  Row Vector
	  * @param b the right Matrix
	  * @return the Product Vector A*B
	  */
	final static public double[] MAP(final double[] a, final double[][] b, final double[] ret) { //previously named mul()
		return MAP(a, b, b[0].length, 0, ret); }
	
	/** multiplies the given Matrices a and b into the optionally given Matrix ret	 */ 
	final static public double[][] MAP(final double[][] a, final double[][] b, double[][] ret) { //previously named mul()
		if ((ret == null) ||
			(ret.length < a.length))
			 ret  = new double[a.length][]; 
		for(int i = a.length; --i >= 0;) 
			ret[i] = MAP(a[i], b, ret[i]); 
		return ret; }
	
	/** Scalar Product Multiplication: �
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is identical to the Derivative Jacobian Matrix of this linear Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  * The last  Index of the first Argument A has to match
	  * the first Index of the last  Argument B
	  * @param a the left  Row Vector
	  * @param b the right Matrix
	  * @return the Product Vector A*B
	  */
	final static public double[] MAP(final double[] a, final double[][] b, final int stop, final int start, double[] ret) { //previously named mul()
		int i = stop; //b.length;
		if (i > b.length) 
			i = b.length;
		if (i > a.length) 
			i = a.length;
		if ((ret == null) || 
			(ret.length < i))
			 ret = new double[i];
		if (--i < 0) 
			return ret;
		//Not necessary initialize ret to 0..
		VectorDouble.MUL(ret, b[i], a[i], start, stop); //Single out the first Operation, saves Initialization and Addition
		while (--i >= 0) //saves Initialization and one Addition!
			VectorDouble.ADD_PROD_AT(ret, b[i], a[i], start, stop); //* Skalar!
		return ret;
	}

	/** Scalar Product Multiplication: �
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A consisting of Row Vectors multiplied from the Left
	  *
	  * Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is the Derivative Jacobian Matrix of the Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  * The last  Index of the first Argument A has to match
	  * the first Index of the last  Argument B
	  * @param a the left  Row Vector
	  * @param b the right Matrix
	  * @return the Product Vector A*B
	  */
	final static public double[][] CAT(final double[][] a, final double[][] b) { //previously named mul()
		final double[][] ret = new double[a.length][];
		for (int i = a.length; --i >= 0;) 
			ret[i] = MAP(a[i], b);
		return ret;
	}

	/**
	  * The Max(Min) Composition Mapping.
	  * This is used in concatenating Mappings of Fuzzy Sets
	  * and in Game Theory.
	  * c[i,k] = { max(j), a[i,j] min b[j,k] }
	  *
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * arg is a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  */
	final static public double[] MAX_MIN_MAP(final double[] arg, final double[][] a) { //previously named mul()
		int i = a.length;
		if (i > arg.length) 
			i = arg.length;
		--i; //Don't initialize to 0
		final double[] ret = VectorDouble.MIN_AT(VectorDouble.COPY(a[i]), arg[i]); //Single out the first Operation:
		while (--i >= 0) //saves Initialization and one Addition!
			VectorDouble.MAX_MIN_PROD(ret, a[i], arg[i]); //* Skalar!
		return ret;
	}

	/**
	  * The Max(Min) Composition Mapping.
	  * This is used in concatenating Mappings of Fuzzy Sets
	  * and in Game Theory.
	  * c[i,k] = { max(j), a[i,j] min b[j,k] }
	  *
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * arg is a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  */
	final static public double[][] MAX_MIN_MAP(final double[][] a, final double[][] arg) { //previously named mul()
		final double[][] ret = new double[a.length][];
		for (int i = a.length; --i >= 0;) 
			ret[i] = MAX_MIN_MAP(a[i], arg);
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Matrix Operations (symmetric/antisymmetric)
	////////////////////////////////////////////////////////////////////////////////

	/** Returns whether the given matrix is symmetric.
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = a[j][i].
	  */
	final static public boolean IS_SYMMETRIC(final double[][] arr) {
		return IS_SYMMETRIC(arr, arr.length);
	}

	/** Returns whether the leading square section of the given matrix is symmetric.
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = a[j][i].
	  */
	final static public boolean IS_SYMMETRIC(final double[][] arr, final int length) {
		for (int i = length; --i >= 0; ) { //Addressing could be even more effective, if the Row Strategy
			final double[] row = arr[i]; //is changed for a Column Strategy in the Middle
			for (int j = i; --j >= 0; ) {
				if (!ByRefDouble.EQUALS(arr[j][i], row[j])) {
					return false; } 
			}
		}
		return true;
	}

	/** Returns whether the given matrix is anti-symmetric.
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = -a[j][i].
	  */
	final static public boolean IS_ANTI_SYMMETRIC(final double[][] arr) {
		return IS_ANTI_SYMMETRIC(arr, arr.length); }

	/** Returns whether the leading square section of the given matrix is anti-symmetric.
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = -a[j][i].
	  */
	final static public boolean IS_ANTI_SYMMETRIC(final double[][] arr, final int length) {
		for (int i = length; --i >= 0; ) { //Addressing could be even more effective, if the Row Strategy
			final double[] row = arr[i]; //is changed for a Column Strategy in the Middle
			for (int j = i; --j >= 0; ) {
				if (!ByRefDouble.EQUALS(arr[j][i], -row[j])) {
					return false; } 
			}
		}
		return true;
	}

	/** fill the lower Matrix with the given Value
	 * 
	 * @param a the Matrix to fill
	 * @param value the value to fill with 
	 * @param length the maximum Row / Column (exclusive)
	 */
	final static public void FILL_LOWER(final double[][] a, final double value, final int length) {
		for (int j=0; j<length; j++) { //
			VectorDouble.FILL_AT(a[j], value, 0, j ); }
	}
	
	/** copy the lower Triangle of this Matrix to it's upper
	 * @param a the Matrix to copy
	 * @param length the valid Length to use
	 */
	final static public void COPY_LOWER_TO_UPPER(double[][] a) {
		COPY_LOWER_TO_UPPER(a, a.length); }
	
	/** Overwrites the upper triangle with the lower triangle's values, in place.
	  * @param a The Array to make symmetric
	  * @return the given Array made symmetric by copying the lower Triangle to the upper.
	  */
	final static public double[][] COPY_LOWER_TO_UPPER(final double[][] a, int length) {
		for (int i = length; --i >= 0; ) { //Addressing could be even more effective, if the Row Strategy
			final double[] a_i = a[i]; //is changed for a Column Strategy in the Middle
			for (int j = i; --j >= 0; ) {
				a[j][i] = a_i[j]; }
		}
		return a;
	}
	
	/** Makes the matrix symmetric in place by adding each off-diagonal pair together.
	  * @param arr The Array to be made symmetric
	  * @return the given Array made symmetric by adding the opposite Elements
	  */
	final static public double[][] MAKE_SYMMETRIC(final double[][] arr) {
		for (int i = arr.length; --i >= 0;) {
			final double[] row = arr[i];
			row[i] *= 2; //+=row[i]; 
			for (int j = i; --j >= 0; ) {
				arr[j][i] = (row[j] += arr[j][i]);
			}
		}
		return arr;
	}

	/** Makes the matrix anti-symmetric in place by subtracting each off-diagonal pair.
	  * @param arr The Array to be made antisymmetric
	  * @return the given Array made antisymmetric by subtracting the Upper from the Lower Triangle.
	  */
	final static public double[][] MAKE_ANTI_SYMMETRIC(final double[][] arr) {
		for (int i = arr.length; --i >= 0; ) {
			final double[] row = arr[i];
			row[i] = 0;
			for (int j = i; --j >= 0; ) {
				arr[j][i] = - (row[j] -= arr[j][i]);
			}
		}
		return arr;
	}

	/** Negates every element of the matrix, in place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[][] NEG_AT(final double[][] ret) {
		if (ret.length <= 0) 
			return ret; 
		return NEG_AT(ret, 0, ret.length, 0, ret[0].length);
	}

	/** Negates the elements within the given row and column range, in place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start1 Index from  where the outer Array is processed
	  * @param stop1  Index up to where the outer Array is processed (not ret[stop]!)
	  * @param start2 Index from  where the inner Array is processed
	  * @param stop2  Index up to where the inner Array is processed (not ret[stop]!)
	  */
	final static public double[][] NEG_AT(final double[][] ret, final int start1, int stop1, final int start2, final int stop2) {
		while (--stop1 >= start1) 
			VectorDouble.NEG_AT(ret[stop1], start2, stop2);
		return ret;
	}

	/** negates the Matrix Column in Place 	 */
	final static public void NEG_COL_AT(final double[][] a, int negCol, final int startRow, final int stopRow) {
		for (int i=startRow; i<stopRow; i++) { //correct for the Minus Sign
			a[i][negCol] = -a[i][negCol]; } 
	}

	/** Replaces every element with its multiplicative inverse, in place.
	  * @return the multiplicative Inverse of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[][] INV_AT(final double[][] ret) {
		if (ret.length <= 0) 
			return ret; 
		return INV_AT(ret, 0, ret.length, 0, ret[0].length);
	}

	/** Replaces the elements within the given row and column range with their multiplicative inverse, in place.
	  * @return the multiplicative Inverse of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start1 Index from  where the outer Array is processed
	  * @param stop1  Index up to where the outer Array is processed (not ret[stop]!)
	  * @param start2 Index from  where the inner Array is processed
	  * @param stop2  Index up to where the inner Array is processed (not ret[stop]!)
	  */
	final static public double[][] INV_AT(final double[][] ret, final int start1, int stop1, final int start2, final int stop2) {
		while (--stop1 >= start1) {
			VectorDouble.INV_AT(ret[stop1], start2, stop2); }
		return ret;
	}
	
	/** Replaces every element with its absolute value, in place.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[][] ABSV_AT(final double[][] ret) {
		if (ret.length <= 0) 
			return ret; 
		return ABSV_AT(ret, 0, ret.length, 0, ret[0].length);
	}

	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the natural Logarithm of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start1 Index from  where the outer Array is processed
	  * @param stop1  Index up to where the outer Array is processed (not ret[stop]!)
	  * @param start2 Index from  where the inner Array is processed
	  * @param stop2  Index up to where the inner Array is processed (not ret[stop]!)
	  */
	final static public double[][] ABSV_AT(final double[][] ret, 
			final int start1, int stop1, 
			final int start2, final int stop2) {
		while (--stop1 >= start1) 
			VectorDouble.ABS_AT(ret[stop1], start2, stop2);
		return ret;
	}

	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the natural Logarithm of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[][] LOG_AT(final double[][] ret) {
		if (ret.length <= 0) 
			return ret; 
		return LOG_AT(ret, 0, ret.length, 0, ret[0].length);
	}

	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the natural Logarithm of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start1 Index from  where the outer Array is processed
	  * @param stop1  Index up to where the outer Array is processed (not ret[stop]!)
	  * @param start2 Index from  where the inner Array is processed
	  * @param stop2  Index up to where the inner Array is processed (not ret[stop]!)
	  */
	final static public double[][] LOG_AT(final double[][] ret
	, final int startRow,       int stopRow
	, final int startCol, final int stopCol) {
		while (--stopRow >= startRow) 
			VectorDouble.LOG_AT(ret[stopRow], startCol, stopCol);
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Matrix Algebra (Linear Function Algebra)
	///////////////////////////////////////////////////////////////////////////////////

	/**Determines the maximum Degree of the given Dimension
	 * As a preparation for Transposition.
	 */
	final static public int MAX_GRAD(final double[][] a) {
		int len;
		if ((len = a.length) == 0) 
			return 0;
		int tmp, maxGrad = a[--len].length;
		while (--len >= 0) {
			if (maxGrad < (tmp = a[len].length)) 
				maxGrad = tmp;
		}
		return maxGrad;
	}

	/**
	 * Extracts one Element from each Row at the given Cols 
	 */
	final static public double[] EXTRACT(final double[] ret, final double[][] a, final int[] cols) {
		return EXTRACT(ret, a, cols, 0, a.length);
	}

	/**
	 * Extracts one Element from each Row at the given Cols 
	 */
	final static public double[] EXTRACT(final double[] ret, final double[][] a, final int[] cols, final int startRow, int stopRow) {
		while (--stopRow >= startRow) {
			ret[stopRow] = a[stopRow][cols[stopRow]];
		}
		return ret;
	}

	/**
	 * Setting to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is null, the Zero Matrix is returned.
	 */
	final static public double[][] DIAG_AT(final double[][] a, final double[] diag_) {
		for (int i = a.length; --i >= 0; ) {
			//VectorDouble.diagAt(a[i], (diag == null) ?  1 : diag[i]);
			final double[] Row = a[i]; //faster to call it directly
			Arrays.fill(Row, 0);
			if (diag_ != null) {
				Row[i] =  diag_[i]; }
		}
		return a;
	}

	/** adds the value to any diagonal Element */
	final static public double[][] ADD_DIAG_AT(final double[][] matrix, final double value, final int start, int stop) {
		while (--stop >= start) 
			matrix[stop][stop] += value; 
		return matrix;
	}
	
	/** fills the whole Matrix with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param value the value to fill with 
	 */
	final static public void FILL(final double[][] a, final double value) {
		for (int row = a.length; --row >= 0; ) 
			FILL_ROW(a, row, value, 0, a.length); 
	}
	
	/** fills the given Row with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param row the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_ROW(final double[][] a, final int row, final double value) {
		FILL_ROW(a, row, value, 0, a.length); }
	
	/** fills the given Row with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param row the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_ROW(final double[][] a, final int row, final double value
	, final int start, final int stop) {
		VectorDouble.FILL_AT(a[row], value, start, stop); }
	
	/** fills the given Column with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param col the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_COL(final double[][] a, final int col, final double value) {
		FILL_COL(a, col, value, 0, a.length); }

	/** fills the given Column with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param col the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_COL(final double[][] a, final int col, final double value
	, final int start, final int stop) {
		for (int i = stop; --i >= start;) {
			a[i][col] = value; }
	}

	/**
	 * Setting 'a' to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is null, the Unity Matrix is returned.
	 */
	final static public double[][] FILL_DIAG_AT(final double[][] a, final double diag_, final boolean clearNonDiag) {
		for (int i = a.length; --i >= 0; ) {
			//VectorDouble.diagAt(a[i], (diag == null) ?  1 : diag[i]);
			final double[] Row = a[i]; //faster to call it directly
			if (clearNonDiag) 
				Arrays.fill(Row, 0);  
			Row[i] = diag_;
		} //setting the Unit Vector.
		return a;
	}

	/**
	 * Setting to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is null, the Unity Matrix is returned.
	 */
	final static public double[][] ONE_AT(final double[][] a) {
		return FILL_DIAG_AT(a, 1, true);
	}

	/**
	 * Setting to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is null, the Unity Matrix is returned.
	 */
	final static public double[][] ZERO_AT(final double[][] a) {
		return DIAG_AT(a, null); }

	/** Returns a new all-zero square matrix of the given dimension.
	 * @return a Zero Matrix (zero Mapping) for the given Dimension.
	 */
	final static public double[][] ZERO(final int dim) {
		return new double[dim][dim];
	}

	/**
	 * Optimization: this is faster, because the Matrix needn't be cleared. 
	 * @return a Unity Matrix (identical Mapping) for the given Dimension.
	 */
	final static public double[][] ONE(final int dim) { //Assume a square Matrix
		return FILL_DIAG_AT(new double[dim][dim], 1, false);
	}  //could be made quite sparse, but for the sake of it...

	/**
	 * Checks whether these Row- Vectors for the Unity Matrix
	 * Makes only Sense for Matrices
	 */
	final static public boolean IS_ONE(final double[][] a) { //Assume a square Matrix
		//double eps = IMeasurAble.DOUBLE_ACCURACY; //
		for (int i = a.length; --i >= 0; ) {
			if (!VectorDouble.IS_ONE(a[i], i)) 
				return false;
		}
		return true;
	}

	/** Returns whether two matrices of possibly different declared lengths are equal, treating
	 * any excess rows in the longer one as required to be zero.
	 * @see Object#equals(java.lang.Object)	 */
	final static public boolean EQUALS(final double[][] a, final int aLength, final double[][] b, final int bLength) {
		final double cmp = a[0][0]; 
		if (a == b) {
			return true; }
		if (a == null) {
			return IS_ZERO(b, cmp); }
		if (b == null) {
			return IS_ZERO(a, cmp); }
		if (aLength > bLength) {
			return EQUALS(a, b, 0, bLength) && IS_ZERO(a, cmp, bLength, aLength); 
		} else {
			return EQUALS(a, b, 0, aLength) && IS_ZERO(b, cmp, aLength, bLength); 
		} 
	}
	
	/** Returns whether the two given matrices are equal.
	 * @see Object#equals(java.lang.Object)	 */
	final static public boolean EQUALS(final double[][] a, final double[][] b) {
		return EQUALS(a, a.length, b, b.length); 
	}
	
	/** Returns whether the two matrices are equal within the given row range.
	 * @see Object#equals(java.lang.Object)
	 */
	final static public boolean EQUALS(final double[][] a, final double[][] b, final int start, final int stop) {
		for (int i = stop; --i >= start; ) {
			if (! VectorDouble.EQUALS(a[i], b[i])) {
				return false; 
			}
		}
		return true;
	}
		
	/**
	 * Checks whether this Matrix is the Zero Matrix
	 * @return true when the given Matrix  
	 * is null 
	 * is of Length 0 
	 * all Lines 
	 * are null 
	 * are of length 0 
	 * contain only 0s
	 */
	final static public boolean IS_ZERO(final double[][] a, final double cmp) { //Assume a square Matrix
		if (a == null) {
			return true; }
		return IS_ZERO(a, cmp, 0, a.length);
	}

	/**
	 * Checks whether this Matrix is the Zero Matrix
	 * @return true when the given Matrix  
	 * all Lines in the given Range
	 * are null 
	 * are of length 0 
	 * contain only 0s
	 */
	final static public boolean IS_ZERO(final double[][] a, final double cmp, final int start, final int stop) { //Assume a square Matrix
		//float eps = IMeasurAble.FLOAT_ACCURACY; //
		for (int i = stop; --i >= start; ) {
			if (!VectorDouble.IS_ZERO(a[i], cmp)) {
				return false;
			}
		}
		return true;
	}

	/**Adds Columns (not Rows) to a Tensor to make it square.
	 * This eliminates possible Optimizations due to sparse Matrices,
	 * but is necessary for Operations like LU_DecomposeAt()	 */
	final static public double[][] MAKE_SQUARE_AT(final double[][] a) {
		int i = a.length;
		while (--i >= 0) //Store the Inverse of the Row-Max Norm for Pivoting
			a[i] = VectorDouble.SET_DIM_AT(a[i], a.length); 
		return a;
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Normalization, Orthogonalization
	////////////////////////////////////////////////////////////////////////////////

	/**Makes these Row- Vectors othogonal
	 * and can bring their (euklidean) Length to 1 in Place.
	 * Makes only Sense for Matrices
	 * @param SqrNorm is an OUT Parameter being filled with the Square Norms of the Row Vectors.
	 *        this can be used to normalize these Vectors further.
	 */
	final static public double[][] ORTHO_AT(final double[][] a, double[] SqrNorm, final boolean normal) {
		double Sqr;
		if (SqrNorm == null) { //
			SqrNorm = new double[a.length];
		} //now it is actually used by subtPart!
		for (int i = a.length; --i >= 0; ) {
			final double[] iRow = a[i];
			for (int j = a.length; --j > i; ) { //Subtract all lower Row Vectors
				VectorDouble.SUB_PART_AT(iRow, a[j], SqrNorm[j]);
			} //a[i] -= <a[i],a[j]> a[j] / <a[j],a[j]>
			Sqr = VectorDouble.NORM_SQR(iRow);
			if (normal) {
				VectorDouble.MUL_AT(iRow, 1 / Math.sqrt(Sqr));
				SqrNorm[i] = 1;
			} else {
				SqrNorm[i] = Sqr;
			}
		}
		return a;
	}

	/**Normalizes these Row- Vectors to (euklidean) Length 1
	 * Makes only Sense for Matrices */
	final static public double[][] ORTHO(double[][] a, boolean normal) {
		return ORTHO_AT(COPY(a), null, normal);
	}

	/** Fills the given array with the maximum value of every row.
	 * @return Maximum Value of the every Row in the Array.
	 */
	final static public double[] MAX_VAL(double[] ret, double[][] arr) {
		return MAX_VAL(ret, arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public double[] MAX_VAL(double[] ret, double[][] arr, int start, int stop) {
		while (--stop >= start) 
			ret[stop] = VectorDouble.MAX_VAL(arr[stop]);
		return ret;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public double[] MAX_VAL(
		final double[] ret,
		final double[][] arr,
		final int startRow,       int stopRow,
		final int startCol, final int stopCol) {
		while (--stopRow >= startRow) 
			ret[stopRow] = VectorDouble.MAX_VAL(arr[stopRow], startCol, stopCol);
		return ret;
	}

	/**
	 * Maximum Norm
	 * @return Positions of the Maximum Value in each Row of the Matrix.
	 */
	final static public int[] MAX_POS(final int[] ret, final double[][] arr) {
		return MAX_POS(ret, arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Positions of the Maximum Value in each Row of the Matrix.
	 */
	final static public int[] MAX_POS(final int[] ret, final double[][] arr, 
			final int startRow, int stopRow) {
		while (--stopRow >= startRow) 
			ret[stopRow] = VectorDouble.MAX_POS(arr[stopRow]);
		return ret;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Positions of the Maximum Values in each Row of the Matrix.
	 */
	final static public int[] MAX_POS(final int[] ret, final double[][] arr, final int startRow, int stopRow, final int startCol, int stopCol) {
		while (--stopRow >= startRow) 
			ret[stopRow] = VectorDouble.MAX_POS(arr[stopRow], startCol, stopCol);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**linear equation solution by LU decomposition	in Place.	
	 * 
	 * @param a Matrix to decompose
	 * @param Rows Permutation of the pivoted Rows
	 * @return the Sign of the Permutation
	 */
	final static public boolean DECOMPOSE_LU(final double[][] a, final int[] rows) {
		return DECOMPOSE_LU_AT(COPY(a), rows);
	}
	
	/** Re-Composition of LU decomposition	in Place.	*/
	final static public double[][] COMPOSE_LU(final double[][] a, final int[] perm) {
		return COMPOSE_LU_AT(COPY(a), perm);
	}
		
	/** Re-Composition of LU decomposition in Place.
	  * Undoes the Permutation of Rows also.
	  * This Operation can be done in Place,
	  * if you start from Bottom Left, because this Element == a[i,j]
	  * is only used within this same line.
	  */
	final static public double[][] COMPOSE_LU_AT(final double[][] a, final int[] rows) {
		//if (! LU_Decomposed) return this; LU_Decomposed = false;
		for (int i = a.length; --i > 0;) { //first row is not modified, because L[1,1] = 1
			final double[] iRow = a[i];
			for (int j = a.length; --j >= 0; ) { //for every a[i,j]...
				double Element = iRow[j]; // == a[i,j]
				int k = i;
				if (j < i) { //below Diagonal: because lower left Elements of U == 0
					k = j;
					Element *= a[j][j];
				} //because Diagonal Elements of U == 1
				while (--k >= 0) {
					Element += iRow[k] * a[k][j];
				}
				iRow[j] = Element;
			}
		}
		UN_PERMUTE_ROWS_AT(a, rows);
		return a; //
	}

	/** Undo the Row Permutations! 
	 * permuteRowsAt(a, perm); does not work, since rows is not a proper Permutation!
	 * 
	 * @param a the Vector to sort out
	 * @param rows the Log of Permutations to undo
	 */ 
	protected static void UN_PERMUTE_ROWS_AT(final double[][] a, final int[] rows) {
		for (int i=rows.length; --i >= 0;) {
			if (rows[i] != i) {
				final double[] tmp = a[i]; a[i] = a [rows[i]]; a [rows[i]] = tmp;}
		}
	}
	
	/**
	 * Undoes the Row Permutations! 
	 * permuteRowsAt(a, perm); does not work, since not a proper Permutation! 
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 */
	protected static final void UN_PERMUTE_AT(final double[] a, final int[] rows) { //previously named mul()
		//Undo the Row Permutations! permuteRowsAt(a, perm); does not work, since not a proper Permutation! 
		for (int i=rows.length; --i >= 0;) {
			if (rows[i] != i) {
				final double tmp = a[i]; a[i] = a [rows[i]]; a [rows[i]] = tmp;}
		}
	}
	
	/**
	 * Prepares Solution of linear Equations by Lower-Upper (LU) decomposition in Place:
	 * An LU Decomposed Matrix is represented by an upper triangle Matrix
	 * and a lower triangle Matrix with only 1s in the Diagonal.
	 * The Diagonal itself belongs to the upper Triangle Matrix:
	 * A = L*U
	 * Is an n3/3 Algorithm, i.e. 3 times faster than calculating the Inverse
	 * Sufficient for calculating the Determinant also!
	 * The Matrix is replaced in Place by it's decomposed Matrix,
	 * The Index Vector 'Rows' keeps track of the Row Permutations.
	 * @param a the Matrix is returned as changed into LU Form
	 * @param Rows Array with Capacity of a.length to hold the 'Permutation' of Rows.
	 *        Actually this is not a real Permutation!
	 * @return the Sign of the Permutation in Rows
	 */
	final static public boolean DECOMPOSE_LU_AT(final double[][] a, final int[] rows) { //N3/3 Algorithm
		//if (LU_Decomposed) return this; LU_Decomposed = true;
		MAKE_SQUARE_AT(a); //to create Space for the higher Elements.
		//int[] Rows =  new int[a.length];
		boolean Sign = false; //The Sign of the Permutation: false = 0; (-1)^0 = 1
		
		final double[] norms = new double[a.length]; //Contains the Max-Norm of each row
		
		for(int i = a.length;--i >= 0;) { //Store the Inverse of the Row-Max Norm for Pivoting
			norms[i] = 1 / VectorDouble.MAX_VAL(a[i]); }
		for (int j = -1; ++j < a.length;) {
			for (int i = -1; ++i < j;) { //Process the lower Rows
				final double[] iRow = a[i];
				double sum = iRow[j];
				for (int k = -1; ++k < i;)
					sum -= iRow[k] * a[k][j];
				iRow[j] = sum;
			}
			double max = 0;
			int  imax = -1;
			for (int i = j;	i < a.length; i++) { //Process the upper Rows 
				final double[] iRow = a[i]; //and search for the relative Pivot, normalized by the Max-Norm.
				double sum = iRow[j];
				for (int k = -1; ++k < j;) {
					sum -= iRow[k] * a[k][j];
				}
				iRow[j] = sum;
				final double dum = Math.abs(sum) * norms[i];
				if (max < dum) {
					max = dum;
					imax = i;
				}
			}
			if (j != imax) { //Swap the rows
				final double[] tmp = a[imax]; a[imax] = a[j]; a[j] = tmp;
				Sign = !Sign;
				norms[imax] = norms[j];
			}
			rows[j] = imax; //Don't care for Overflows anymore, using Infinity!
			//if (a[j][j] == 0) { a[j][j] = IMeasurAble.DOUBLE_ACCURACY;	//not necessary, work with Infinity
			if (j < a.length - 1) {
				final double dum = 1 / a[j][j];
				for (int i = j; ++i < a.length;) { //Divide the lower Column by the Diagonal
					a[i][j] *= dum;
				}
			}
		}
		return Sign;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/// Mapping with LU decomposed Matrix 
	///////////////////////////////////////////////////////////////////////////////////////

	/**
	 * maps Vector a from right using the LU decomposed Matrix  
	 * @param decompLU LU decomposed Matrix
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 * @return LU*a
	 */
	final static public double[] MAP(final double[][] decompLU, final int[] rows , final double[] a) { //previously named mul()
		double[] ret = VectorDouble.COPY(a);
		MAP_AT(decompLU, rows, ret);
		return ret;
	}
		
	/**
	 * maps Vector a from left using the LU decomposed Matrix  
	 * @param decompLU LU decomposed Matrix
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 * @return a*LU
	 */
	final static public double[] MAP(final double[] a, final double[][] decompLU, final int[] rows) { //previously named mul()
		double[] ret = VectorDouble.COPY(a);
		MAP_AT(ret, decompLU, rows);
		return ret;
	}
			
	/**
	 * maps Vector a from right using the LU decomposed Matrix  
	 * @param decompLU LU decomposed Matrix
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 */
	final static public void MAP_AT(final double[][] decompLU, final int[] rows , final double[] a) { //previously named mul()
		//multiply with U 
		for (int i = -1; ++i < a.length; ) {
			double[] mi = decompLU[i];
			a[i] *= mi[i];
			for (int j = i; ++j < a.length;) {
				a[i] += mi[j]*a[j];
			}
		}
		//multiply with L (Diagonal = 1)
		for (int i = a.length; --i >= 0;) {
			double[] mi = decompLU[i];
			for (int j = i; --j >= 0;) {
				a[i] += mi[j]*a[j];
			}
		}
		UN_PERMUTE_AT(a, rows);
		//return a; 
	}
	
	/**
	 * maps Vector a from left using the LU decomposed Matrix  
	 * @param decompLU LU decomposed Matrix
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 */
	final static public void MAP_AT(final double[] a, final double[][] decompLU, final int[] rows) { //previously named mul()
		//multiply with L (Diagonal = 1)
		for (int i = -1; ++i < a.length; ) {
			for (int j = i; ++j < a.length;) {
				a[i] += a[j]*decompLU[j][i];
			}
		}
		//multiply with U 
		for (int i = a.length; --i >= 0;) {
			a[i] *= decompLU[i][i];
			for (int j = i; --j >= 0;) {
				a[i] += a[j]*decompLU[j][i];
			}
		}
		UN_PERMUTE_AT(a, rows);
		//return a; 
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/// Un-Mapping with LU decomposed Matrix 
	///////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Solves the linear equation with Matrix B by Backsubstitution after Decomposition
	 * B = A*ret <=> ret = A'*B with
	 * @param a the LU decomposed Matrix of the System.
	 * @param rowPerm the 'Permutation' of Rows from the LU_Decomposition
	 * @param b is replaced by the Solution in Place.
	 */
	final static public double[][] SOLVE_LU_AT( final double[][] a, 
			final int[] rowPerm, final double[][] b) {
		//if (rowPerm == null) //if one is only interested in ???
		//	rowPerm  = new int[a.length]; //does this make sense at all?
		int iNonZero = -1; 
		for (int i = -1; ++i < a.length;) { //Process the upper Triangle
			final int ip = rowPerm[i];
			final double[] bi = b[ip];
			if (ip != i) { //Redo the Permutation
				b[ip] = b[i];
				b[i ] = bi; } //
			final double[] ai = a[i];
			if (iNonZero >= 0) { //Optimization: start subtracting only
				for (int j = iNonZero; j < i; j++) { //from the first nonzero Element on!
					VectorDouble.SUB_PROD_AT(bi, b[j], ai[j]);
				}
			} else {
				if (!VectorDouble.IS_ZERO(bi, ai[0])) {
					iNonZero = i;
				}
			}
		}
		for (int i = a.length; --i >= 0;) { //Process the lower Triangle
			final double[] bi = b[i];
			final double[] ai = a[i];
			for (int j = i; ++j < a.length;) {
				VectorDouble.SUB_PROD_AT(bi, b[j], ai[j]);
			}
			VectorDouble.MUL_AT(bi, 1 / ai[i]);
		}
		return b;
	}
	
	/**
	 * Solves the linear equation with Vector b by Backsubstitution after Decomposition
	 * b = A*ret = L*U*ret <=> ret = A'*b with Column Vector b
	 * @param a the LU decomposed Matrix of the System.
	 * @param Rows the 'Permutation' of Rows from the LU_Decomposition
	 * @param b is replaced by the Solution in Place.
	 */
	final static public void SOLVE_LU_AT
	(final double[][] a, final int[] rows, final double[] b) {
		int iNonZero = -1;
		for (int i = -1; ++i < a.length;) { //Process the upper Triangle
			final int ip = rows[i];
			double Sum = b[ip];
			if (ip != i) {
				b[ip] = b[i];
			} //partly redo the Permutation (see End of Loop!)
			final double[] iRow = a[i];
			if (iNonZero >= 0) { //Optimization: start subtracting only
				for (int j = iNonZero; j <= i - 1; j++) { //from the first nonzero Element on!
					Sum -= b[j] * iRow[j];
				}
			} else {
				if (Sum != 0) {
					iNonZero = i;
				}
			}
			b[i] = Sum; //finish redoing the Permutation
		}
		for (int i = a.length;--i >= 0;) { //Process the lower Triangle
			double Sum = b[i];
			final double[] iRow = a[i];
			for (int j = i; ++j < a.length;) {
				Sum -= b[j] * iRow[j];
			}
			b[i] = Sum / iRow[i];
		}
		//return b;
	}
	
	//////////////////////////
	//	Matrix Inversion	//
	//////////////////////////
	
	/** Inversion in Place: 1/a
	 * since a is destroyed anyway, it's reordering on Decomposition does not matter.  */
	final static public double[][] RCP_AT(final double[][] a) {
		return RCP_AT(a, null); }
	
	/** Inversion in Place: 1/a
	 * since a is destroyed anyway, it's reordering on Decomposition does not matter.  */
	final static public double[][] RCP_AT(final double[][] a, int[] rows) {
		if (rows == null)
			rows  = new int[a.length]; 
		return SHALLOW_COPY_AT(a, RCP(a, rows)); }
	
	/** Inversion: 1/a
	 * since a is decomposed, it needs to be copied!	 */
	final static public double[][] RCP(final double[][] a) { 
		return RCP_AT(COPY(a), null); }
	
	/** Inversion: 1/a 
	 * a is decomposed in the Process, that's why rows must be given
	 */
	final static public double[][] RCP(final double[][] a, final int[] rows) {
		return TRP_AT(RCP_TRP(a, rows)); }
	
	/** Inversion and Transposition: 1/xT	 */
	final static public double[][] RCP_TRP(final double[][] a, final int[] rows) {
		return SOLVE_LU_AT(a, rows, ONE(a.length)); }
	
	/**Division and Transposition in Place: /= arg^T
	 * Requires arg to be LU Decomposed (considered as decomposed).	 
	 */
	final static public double[][] DIV_TRP_AT(final double[][] a, final int[] rows, 
			final double[][] arg) {
		return SOLVE_LU_AT(a, rows, arg); //possible both to solve the whole System with one Call or with several Calls
	}

	/**Division and Transposition: a / arg^T	 */
	final static public double[][] DIV_TRP(final double[][] a, 
			final int[] Rows, final double[][] arg) {
		return DIV_TRP_AT(COPY(a), Rows, arg); }

	/** Division in Place: /= arg	 */
	final static public double[][] DIV_AT(final double[][] a, final double arg) {
		return MUL_AT(a, 1 / arg); } //Use same Scalar Multiplication as with Polynoms and Manifolds

	/** Division in Place: /= arg	 */
	final static public double[][] DIV_AT(final double[][] a, final int[] Rows, final double[] arg) {
		return DIV_AT(a, Rows, arg);
	} //(Tensor / Vector) or (Matrix / Vector):  ManiFold- Like Division of the Argument by each Item

	/** Division in Place: /= arg	 */
	final static public double[][] DIV_AT(
		final double[][] a,
		final int[] Rows,
		final double[][] arg) { //(Vector / Vector) or (Tensor / Vector) or (Tensor / Tensor)
		return TRP_AT(DIV_TRP_AT(a, Rows, arg));
	} //The Argument must not be decomposed!!!
	
	/**Division: /	 */
	final static public double[][] DIV(final double[][] a, final int[] Rows, final double[][] arg) {
		return DIV_AT(COPY(a), Rows, arg); }
	
	/**Division: /	 */
	final static public double[][] DIV(final double[][] a, final int[] Rows, final double[] arg) {
		return DIV_AT(COPY(a), Rows, arg); }
	
	/**Division: /	 */
	final static public double[][] DIV(double[][] a, double arg) {
		return DIV_AT(COPY(a), arg); }
	
	//Calculation of Determinant:
	//Build all Permutations of the Indices is of Order n!
	//Multiply the Coefficients according to the Indices and build the Sum is of order (n+1)!
	//This is numerically not stable, since the Products tend to cancel each other out!

	/**Returns the Determinant of the (square) Matrix:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations.	 */
	final static public double DET(final double[][] a, final int[] rows) {
		return DET_AT(COPY(a), rows); }
	
	/**The Trace of a Matrix is the Sum along it's Diagonal.
	 * It stays constant with orthogonal Transformations.	 */
	final static public double TRACE(final double[][] a) { //Assume that this is a square Matrix.
		if (a.length <= 0) 
			return 0;
		double Trace = a[0][0];
		for (int i = 0; ++i < a.length;)
			if (a[i].length >= i) 
				Trace += a[i][i];
		return Trace; }
	
	/**Returns the Determinant of the (square) Matrix:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations. 
	 * 
	 * Since the Determinant grows exponentially with the Number of Dimensions, 
	 * it makes sense to only sum up the Logarithms and return the Sign separately. 
	 */
	final static public double TRACE_LN_PROD(final double[][] a, boolean[] negative) {
		if (a.length <= 0) 
			return 0;
		double Prod = Math.log(a[0][0]); //saves 1 Multiplication
		boolean sign = negative[0];  
		for (int i = 0; ++i < a.length;) { 
			double a_ii = a[i][i]; 
			if (a_ii < 0) {
				a_ii = -a_ii; 
				sign = !sign; 
			}
			Prod += Math.log(a_ii);
		}
		negative[0] = sign; 
		return Prod;
	}

	/**Returns the Determinant of the (square) Matrix:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations.	 */
	final static public double TRACE_PROD(final double[][] a) {
		if (a.length <= 0) 
			return 1;
		double Prod = a[0][0]; //saves 1 Multiplication
		for (int i = 0; ++i < a.length;) 
			Prod *= a[i][i];
		return Prod;
	}

	/**Returns the Determinant of the (square) Matrix in Place:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations.	 */
	final static public double DET_AT(final double[][] a, final int[] Rows) { //Assume that this is a square Matrix.
		//The Determinant is the Product of the Diagonal Elements of the Decomposed Matrix
		boolean Sign = DECOMPOSE_LU_AT(a, Rows); //Using Decomposition is very stable and fast! N3/3 instead of n!
		double Prod = TRACE_PROD(a);
		if (Sign) //preserve Sign from the Decomposition
			return -Prod;
		return Prod;
	}

	/** true, when the Matrix is orthogonal, i.e. M*Mt = Mt*M = diag(a, b, c, ...).
	  * If a Matrix contains complex coefficients, it should be checked to be unitarian.
	  */
	final static public boolean IS_ORTHOGONAL(final double[][] a) { //The Optimization here is that you have to 
		for(int i = a.length; --i >= 0;) {
			final double[] ai = a[i];
			final double ai0 = ai[0];
			for (int j = i; --j >= 0; ) { //test only one Triangle
				final double[] aj = a[j];
				if (!ByRefDouble.IS_ZERO(VectorDouble.MAP(ai, aj), ai0+aj[0])) { //because the Product is symmetric.
					//Use an Epsilon here that corresponds to any Matrix Norm
					return false;
				}
			}
		}
		return true;
	}
	
	/**true, when the Matrix is unitarian resp. orthonormal, i.e. M*Mt = Mt*M = 1.
	 * unitarian is the complex equivalent to orthonormal 	 */
	final static public boolean IS_UNITARIAN(final double[][] a) { //The Optimization here is that you have to test only one Triangle
		//because the Product is symmetric.
		if (!IS_ORTHOGONAL(a))
			return false;
		for (int i = a.length; --i >= 0;) {
			if (!ByRefDouble.EQUALS(1, VectorDouble.MAP(a[i], a[i]))) {
				return false;
			}
		}
		return true;
	}

	//	final static public boolean  orthoNorm();  == unitaer fuer reelle Matrizen;

	/**true, when the Matrix is hermitean resp. symmetric, i.e. M = Mt.	 */
	final static public boolean IS_HERMITEAN(double[][] a) { //The Optimization here is that you have to test only one Triangle
		for (int i = a.length; --i >= 0;) {
			for (int j = i; --j >= 0; ) {
				if (!ByRefDouble.EQUALS(a[i][j], a[j][i])) { //Could also test for the Difference to be Zero
					return false;
				}
			}
		}
		return true;
	}

	//	final static public boolean  symmetr  ();  == hermite fuer reelle Matrizen;

	/**true, when the Matrix is anti-hermitean resp. anti-symmetric, i.e. M = -Mt.	 */
	final static public boolean IS_ANTI_HERMITEAN(double[][] a) { //The Optimization here is that you have to test only one Triangle
		for (int i = a.length; --i >= 0;) {
			for (int j = i; --j >= 0; ) {
				if (!ByRefDouble.EQUALS(a[i][j], -a[j][i])) { //Could also test for the Sum to be Zero
					return false;
				}
			}
		}
		return true;
	}

	//	final static public boolean  antiSym  (); � antiHerm fuer reelle Matrizen;

	/**true, when the Matrix is normal, i.e. M*M^T = M^T*M.
	 * i.e. M*M^T is symmetric
	 * A normal Matrix has a complete Set of orthonormal Eigenvectors. 
	 * Non-normal Matrices may have (right) Eigenvectors, 
	 * but these are not orthogonal to each other, 
	 * only to their corresponding left Eigenvectors. 
	 */
	final static public boolean IS_NORMAL(final double[][] a) { //The Optimization here is that you have to test only one Triangle
		//because the Product is symmetric.
		final double[][] Trp = TRP(a);
		for (int i = a.length; --i >= 0;) {
			for (int j = i+1; --j >= 0; ) {
				if (!ByRefDouble.EQUALS(
					VectorDouble.MAP(a[i], a[j]), 
					VectorDouble.MAP(Trp[i], Trp[j]))) {
					return false;
				}
			}
		}
		return true;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods
	///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(final double[][] vals, final PrintStream stream, final char separator) {
		STREAM(vals, stream, 0, vals.length, separator);
	}

	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(final double[][] vals, final PrintStream stream) {
		STREAM(vals, stream, 0, vals.length, VectorFloat.DEFAULT_SEPARATOR);
	}
	
	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(final double[][] vals) {
		STREAM(vals, L, 0, vals.length, VectorFloat.DEFAULT_SEPARATOR);
	}
			
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(
		final double[][] vals,
		final PrintStream stream,
		int startRow,
		int stopRow, final char separator) { //, int startCol, int stopCol) {
		//if (startRow >= stopRow) {
		//	return; }
		//VectorDouble.stream(vals[startRow], stream);
		for (int i = startRow-1; ++i < stopRow;) {
			VectorDouble.STREAM(vals[i], stream);
			stream.println();
		}
	}

	/** streams the Numbers of the given Array out to the Stream using the given Formatter
	 * 
	 * @param d Array to stream out
	 * @param pw PrintWriter to stream to
	 * @param formatter Number Formatter to use 
	 * @param colSep Separator String between Columns 
	 * @param rowSep Separator String between Rows
	 */
	final static public void STREAM(final double[][] d, final OutputStream ps, final NumberFormatter formatter, final String colSep) throws IOException {
		final Writer pw = new OutputStreamWriter(ps);
		STREAM(d, pw, formatter, colSep, "\n");
		pw.flush(); 
	}
	
	/** streams the Numbers of the given Array out to the Stream using the given Formatter
	 * 
	 * @param d Array to stream out
	 * @param pw PrintWriter to stream to
	 * @param formatter Number Formatter to use 
	 * @param colSep Separator String between Columns 
	 * @param rowSep Separator String between Rows
	 */
	final static public void STREAM(final double[][] d, final Writer pw, final NumberFormatter formatter, final String colSep) throws IOException {
		STREAM(d, pw, formatter, colSep, "\n");
	}
	
	/** streams the Numbers of the given Array out to the Stream using the given Formatter
	 * 
	 * @param d Array to stream out
	 * @param pw PrintWriter to stream to
	 * @param formatter Number Formatter to use 
	 * @param colSep Separator String between Columns 
	 * @param rowSep Separator String between Rows
	 */
	final static public void STREAM(final double[][] d, final Writer pw, final NumberFormatter formatter, final String colSep, final String rowSep) throws IOException {
		for (int i = -1; ++i < d.length;) {
			VectorDouble.STREAM(d[i], pw, formatter, colSep);
			pw.write(rowSep); 
		}
		//return pw; 
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Rotation Matrices and Operations
	///////////////////////////////////////////////////////////////////////////////////

	/** Returns a rotation matrix aligning this vector with the axis at the given dimension.
	 * @return a Rotation Matrix defined by the Direction given by this Vector.
	 *
	 * The Vector is converted in Place into Polar Coordinates.
	 * The Matrix is defined so it maps the given Vector to (0,0,0....,1,0,0...)
	 * with the 1 at the given Dimension.
	 *
	 * This is achieved by undoing the Angles sequentially by negative Rotations.
	 * The View Vector is aligned with the Axis along Dimension Dim.
	 *
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 *
	 * To align the Vector to the last Dimension
	 * (saves calculating the last Coordinate, which is just the Distance),
	 * you have to switch the 0 and last Coordinate,
	 * as well as the respective Rows and Columns in the Matrix. */
	final static public double[][] alignMatrixAt(final double[] a, final int Dim) {
		if (Dim == 0) {
			return alignMatrixAt(a);
		}
		HunterDouble.SWAP_AT(a, 0, Dim); //Swap the coordinates
		double[][] Matrix = alignMatrixAt(a);
		HunterDouble.SWAP_AT(a, 0, Dim); //undo the Swap of the coordinates...useless anyway
		SWAP_COLS_AT(Matrix, 0, Dim); //Swap the Coordinates back:
		SWAP_ROWS_AT(Matrix, 0, Dim);
		return Matrix;
	}

	/** Returns a rotation matrix aligning this vector with the x-axis, converting the vector to polar coordinates in place.
	 * @return a Rotation Matrix defined by the Direction given by this Vector.
	 *
	 * The Vector is converted in Place into Polar Coordinates.
	 * The Matrix is defined so it maps the given Vector to (1,0,0,0....)
	 *
	 * This is achieved by undoing the Angles sequentially by negative Rotations.
	 * The View Vector is aligned with the x-Axis (0. Dimension) then.
	 *
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 * It's Inverse equals it's Transpose!
	 *
	 * The Projection to 2 Dimensions can then be done by a (planar) Projection
	 * of (y, z) to (x, y) by (y/x, z/x).
	 *
	 * To align the Vector to the last Dimension
	 * (saves calculating the last Coordinate, which is just the Distance),
	 * you have to switch the 0 and last Coordinate,
	 * as well as the respective Rows and Columns in the Matrix.	 */
	final static public double[][] alignMatrixAt(final double[] a) { //Polar[0] = r
		VectorDouble.Rect2PolarAt(a, a.length); //Convert all Coordinates to polar ones.
		//Create a Matrix with the Angles undone sequentially by negative Rotations:
		//first (x,y), then (x,z) and so on..., Angles start at a[1]!
		double[][] Matrix = RotMatrix(a[1], 0, 1, a.length); //saves one Matrix Multiplication
		for (int i = 1; ++i < Matrix.length;) { //r, a1, a2, a3, ...
			RotateAt(Matrix, a[i], 0, i); //Rotations along different Axes are NOT commutable!
		}
		return Matrix;
	}

	/**Creates a double[][] representing a plane Rotation in 2 Dimensions.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 *
	 * Only possible for Matrices.	 */
	final static public double[][] fillRotMatrixAt(double[][] a, double phi, int Dim1, int Dim2) {
		FILL_DIAG_AT(a, 1, false);
		final double[] c_s = new double[2];
		ByRefDouble.COS_SIN(phi, c_s);
		a[Dim1][Dim1] = (a[Dim2][Dim2] = c_s[0]);
		a[Dim2][Dim1] = - (a[Dim1][Dim2] = c_s[1]);
		return a;
	}

	/**Creates a double[][] representing a plane Rotation in 2 Dimensions.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 *
	 * Only possible for Matrices.
	 */
	final static public double[][] RotMatrix(double phi, int Dim1, int Dim2, int dimMax) {
		return fillRotMatrixAt(new double[dimMax][dimMax], phi, Dim1, Dim2);
	}

	/**Rotates the double[][] by a plane Rotation in the given 2 Dimensions in Place.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 *
	 * Only possible for Matrices.
	 */
	final static public double[] Rotate(final double[] a, final double phi, final int Dim1, final int Dim2) {
		return RotateAt(VectorDouble.COPY(a), phi, Dim1, Dim2);
	}

	/**Rotates the double[][] by a plane Rotation in 2 Dimensions in Place.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product). 	 */
	final static public double[] RotateAt(final double[] a, final double phi, final int Dim1, final int Dim2) {
		final double[] c_s = new double[2];
		ByRefDouble.COS_SIN(phi, c_s);
		double Row = a[Dim1];
		a[Dim1] = a[Dim1] * c_s[0] + a[Dim2] * c_s[1];
		a[Dim2] = a[Dim2] * c_s[0] - Row * c_s[1];
		return a;
	}

	/**Rotates the double[][] by a plane Rotation in 2 Dimensions in Place.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 */
	final static public double[][] Rotate(final double[][] a, final double phi, final int Dim1, final int Dim2) {
		return RotateAt(COPY(a), phi, Dim1, Dim2);
	}

	/**Rotates the double[][] by a plane Rotation in 2 Dimensions in Place.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product). 	 */
	final static public double[][] RotateAt(final double[][] a, final double phi, final int Dim1, final int Dim2) {
		double[] c_s = new double[2];
		ByRefDouble.COS_SIN(phi, c_s);
		double[] Row = VectorDouble.COPY(a[Dim1]);
		VectorDouble.BI_LIN_AT(a[Dim1], c_s[0], a[Dim2], c_s[1]);
		VectorDouble.BI_LIN_AT(a[Dim2], c_s[0], Row, -c_s[1]);
		return a;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Backing Value Array for the double[]	 */
	protected double[][] items;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the live backing array so it can be modified externally.
	 * @return the internal List to modify it externally
	 */
	public double[][] getList() {
		return items;
	}

	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized double[] getVectorAt(final int index) {
		if (indexInRange(index)) 
			return items[index];
		return null;
	}
	
	/** Returns the row at the given position, boxed as a plain {@code Object}.
	 * @return the item at the given Position as an Object */
	public Object getAt(final int i) { return getVectorAt(i); }

	/** Returns the value at the given row and column.
	 * @return the item at the given Position as an Object */
	public double getAt(final int rowNum, final int colNum) {
		double[] row = getVectorAt(rowNum);
		if ((row == null) || 
			(row.length <= colNum)) {
			return 0; 
		} 
		return row[colNum]; 
	}
	
	/**Sets (adds or replaces, sums, multiplies, maximizes or minimizes etc.) 
	 * the Value at the specified indices with the new Value.
	 * also applicable to Vectors and Tensors. 
	 * Used to create aggregated Cross-/Pivot- Tables 
	 * from raw Data (e.g. Event Lists)
	 * 
	 * The only Problem remaining is to map Row Values (Strings and Numbers) to Columns.
	 * For Numbers and Dates you can probably specify an int Function().  
	 * For Strings and IDs you would need an Array or better a Mapping String => int 
	 * like an Indexer. 
	 * @return the former Value 
	 * @param rowNum the Row Number 
	 * @param colNum the Column Number 
	 * @param value the value to set 
	 * @param op the (optional, null allowed) Operation to combine the previous and current Values
	 * if null, just keeps the first Value, which corresponds to OpFirst. 
	 * @see function.vector.OpSum for summing up the Values 
	 * @see function.vector.OpCount for just counting up the Values 
	 * @see function.vector.OpMax for determining the maximum Value 
	 * @see function.vector.OpMin for determining the minimum Value 
	 * for more complex Functions like Avg and Var 
	 * two Arrays need to be updated: one for the Sums and one for the Counts. 
	 * These cannot be maintained in the same double Value! 
	 */
	public double opAt(final int rowNum, final int colNum, final double value, final IBinaryOpFloat op) {
		//if (op == null)
		//	op  = OpFirst.OpFirst; //saves one Check below
		double[] row = getVectorAt(rowNum); 
		final double ret; 
		if ((row == null) || 
			(row.length <= colNum)) {
			final double[] tmp = new double[colNum+1]; //TODO: no larger pre-allocation...
			if (row != null) //...O(n�) with ascending Indices!  
				System.arraycopy(row, 0, tmp, 0, row.length); 
			row = tmp; 
			setAt(rowNum, row); 
			ret = 0; //Double.NaN; 
		} else {
			ret = row[colNum]; 
		}
		row[colNum] = op.Funktion(ret, value); 
		return ret; 
	}
	
	/**Sets (adds or replaces) the Value at the specified indices.
	 * 
	 * @param rowNum the Row Number 
	 * @param colNum the Column Number 
	 * @param value the value to set 
	 * @return the former Value 
	 */
	public double setAt(final int rowNum, final int colNum, final double value) {
		return opAt(rowNum, colNum, value, OpLast.OpLast); }
	
	/**Sets (adds or replaces) the component at the specified index.
	 * All other components in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * @param	  value	the component to set (add or replace).
	 * @param	  index   the index of the object to remove.
	 * @return	 the component replaced by 'Item'.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public double[] setAt(final int index, final double[] value) {
		double[] ret = null; 
		if (indexInRange(index)) 
			ret = items[index];
		else {
			if (value == null)
				return   null; 
			setSize(index+1);
		}
		items[index] = value;
		return ret; 
	}
	
	/**Sets (adds or replaces) the component at the specified index.
	 * All other components in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * @param	  Item	the component to set (add or replace).
	 * @param	  index   the index of the object to remove.
	 * @return	 the component replaced by 'Item'.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public Object setAt(final int index, final Object value) {
		return setAt(index, (double[]) value); 
	}
	
	/**Sets (adds or replaces) the component at the specified index.
	 * All other components in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * @param	  value	the component to set (add or replace).
	 * @param	  index   the index of the object to remove.
	 * @return	 the component replaced by 'Item'.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public void setAt(final double[][] value) {
		if (!indexInRange(value.length-1)) {
			setSize(value.length);
		}
		System.arraycopy(value, 0, items, 0, value.length); 
		if (itemCount < value.length) {
			itemCount = value.length;
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/**Constructs an empty VectorInt with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the VectorInt.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the VectorInt overflows.	 */
	public MatrixDouble(final int initialCapacity, final int capacityIncrement_) {
		super();
		items = new double[initialCapacity][];
		capacityIncrement = capacityIncrement_;
		//		mEnum = new ArrayEnum(Items, ItemCount);
		//		mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty MatrixDouble with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the MatrixDouble.	 */
	public MatrixDouble(final int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty MatrixDouble.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public MatrixDouble() {
		this(DEFAULT_CAPACITY_INIT);
	}
	
	/** Constructs an empty MatrixDouble.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public MatrixDouble(final double[][] a, boolean copy) {
		if (copy) {
			items = new double[a.length][];
			copyAt(a);
		} else {
			items = a;
		}
	}
		
	/** Constructs an MatrixDouble by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public MatrixDouble(final Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}
	
	/** Constructs an MatrixDouble from the given Object.	  */
	public MatrixDouble(final Object arg, final int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}
	
	/** Constructs an MatrixDouble from the given Object.	  */
	public MatrixDouble(final double[][] arg, final int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}
	
	/** Constructs an MatrixDouble from the given Object
	  * and copies the Elements into this MatrixDouble.	  */
	public MatrixDouble(final double[][] arg) {
		this(arg.length, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}
	
	/** Constructs an MatrixDouble from the given Object.	  */
	public MatrixDouble(final float[][] arg, final int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}
	
	/** Constructs an MatrixDouble from the given Object
	  * and copies the Elements into this MatrixDouble.	  */
	public MatrixDouble(final float[][] arg) {
		this(arg.length, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1 dim Array Use
	////////////////////////////////////////////////////////////////////////////////
	
	/** Adds the given Item (Vector) to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public MatrixDouble addItem(final double[] item) {
		setAt(itemCount, item);
		return this;
	}
	
	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 * @param item the Item to add 
	 * @param original Flag whether to clone the inner Items
	 * @return
	 */
	final public MatrixDouble addItem(final VectorDouble item, final boolean original) {
		setAt(itemCount, item.getItems(original));
		return this;
	}
	
	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	final public synchronized void copyInto(final double[][] anArray) {
		System.arraycopy(items, 0, anArray, 0, itemCount);
		/*		int i = ItemCount;
				Object elementDataLocal[] = this.Items;
				while (i-- > 0)
					anArray[i] = elementDataLocal[i];
		*/
	}

	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.	 */
	final public synchronized double[][] toArray() {
		double[][] Return = new double[itemCount][];
		System.arraycopy(items, 0, Return, 0, itemCount);
		return Return;
	}
	
	/**Trims the capacity of this VectorInt to be the VectorInt's current
	 * size. An application can use this operation to minimize the
	 * storage of a VectorInt.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			double[][] oldData = items;
			items = new double[itemCount][];
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
	}

	/**Returns the current capacity of this VectorInt.
	 *
	 * @return  the current capacity of this VectorInt.	 */
	final public int getCapacity() { return items.length; }

	/**Ensures the capacity of this VectorInt, 
	 * so that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 * 
	 * @param   minCapacity   the desired minimum capacity.	 
	 * @return the actual Capacity; 
	 */
	final public synchronized int setCapacity(final int minCapacity) {
		items = SET_CAPACITY(minCapacity, items, itemCount);
		return items.length; 
	}

	/**Ensures the capacity of this VectorInt, 
	 * so that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final public synchronized void setCapacity(final int minRows, final int minCols) {
		items = SET_CAPACITY(minRows, minCols, items, itemCount); 
	}
	
	/** Check for equality
	 * operator ==
	 * @param arg the Object to compare with 
	 * @return true when both Objects are the same
	 * @see Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object arg) {
		if (arg instanceof MatrixDouble) {
			return equals((MatrixDouble) arg); 
		}
		return false; 
	}
	
	/** Returns whether this matrix's current rows equal the given raw array.
	 * @see Object#equals(java.lang.Object)	 */
	public boolean equals(final double[][] arg) {
		return EQUALS(items, itemCount, arg, arg.length);
	}

	/** Returns whether this matrix's current rows equal the given matrix's.
	 * @see Object#equals(java.lang.Object)	 */
	public boolean equals(final MatrixDouble arg) {
		return EQUALS(items, itemCount, arg.items, arg.itemCount); 
	}
	
	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 *
	 * The Optimization here is that the Capacity can be ensured before
	 * and that additional Fields can be set.	 */
	public MatrixDouble copyAt(final double[][] arg_) {
		itemCount = arg_.length;
		System.arraycopy(arg_, 0, items, 0, itemCount);
		return this;
	}

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 *
	 * The Optimization here is that the Capacity can be ensured before
	 * and that additional Fields can be set.	 */
	public MatrixDouble copyAt(final float[][] arg_) {
		itemCount = arg_.length; 
		for (int i = itemCount; --i >= 0;) {
			items[i] = VectorDouble.COPY(arg_[i]);
		}
		return this;
	}
	
	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 *
	 * The Optimization here is that the Capacity can be ensured before
	 * and that additional Fields can be set.	 */
	public ICopyAble copyAt(Object arg) {
		if (arg instanceof MatrixDouble) {
			MatrixDouble arg_ = (MatrixDouble) arg;
			capacityIncrement = arg_.capacityIncrement;
			setCapacity(arg_.itemCount);
			itemCount = arg_.itemCount;
			System.arraycopy(arg_.items, 0, items, 0, itemCount);
		} else {
			super.copyAt(arg); //no need to use a recursive DeepCopy like with Tensor
		}
		return this;
	}

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) {
		if (arg instanceof MatrixDouble) {
			MatrixDouble arg_ = (MatrixDouble) arg;
			capacityIncrement = arg_.capacityIncrement;
			itemCount = arg_.itemCount;
			items = arg_.items;
		} else {
			super.copyAt(arg);
		}
		return this;
	}

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		return new MatrixDouble(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Arithmetic Methods for Arrays
	////////////////////////////////////////////////////////////////////////////////

	/** Normalizes this Vector by bringing it into the canonical Form
	 * so that getAt(getInt()) != 0 
	 */
	public MatrixDouble normalizeAt() {
		while (items[--itemCount] == null);
		++itemCount;
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixDouble addAt(final VectorFloat vector) {
		return addAt(vector.getItems(true), 0, vector.getInt()); }

	/** subtracts the given Portion of the values from this Vector */
	public MatrixDouble subAt(final VectorFloat vector) {
		return subAt(vector.getItems(true), 0, vector.getInt()); }

	/** @return the Minimum and Maximum Values of each Column... 
	 * too complex to optimize for now... 
	 * 
	 * Use Min and Max separately, which is clearer too!
	 */
	//	final static public double[][] MinMax() { }

	/** Fills the given array with the minimum value of every column.
	 * @return the Minimum Values of each Column
	 */
	public double[] Min(final double[] ret) {
		return MIN(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	public double[] Min() {
		return MIN(items, 0, itemCount);
	}

	/** Fills the given array with the maximum value of every column.
	 * @return the Maximum Values of each Column */
	public double[] Max(final double[] ret) {
		return MAX(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	public double[] Max() {
		return MAX(items, 0, itemCount);
	}

	/** subtracts the given Portion of the values from this Vector */
	public MatrixDouble negAt() {
		NEG_AT(items);
		return this; 
	}
	
	/** subtracts the given Portion of the values from this Vector */
	public MatrixDouble trpAt() {
		composeLuAt();
		TRP_AT(items);
		return this; 
	}
	
	/** subtracts the given Portion of the values from this Vector */
	public MatrixDouble subAt(final float[] values, final int start, final int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			COPY_AT(items, values, itemCount, stop);
			SUB_AT(items, values, start, itemCount);
			//			normalizeAt();
		} else if (stop < itemCount) { //don't need to (re-)normalize
			SUB_AT(items, values, start, stop);
		} else {
			SUB_AT(items, values, start, stop);
			//			normalizeAt(); //might be quite improbable though!
		}
		return this;
	}

	/** subtracts the given Portion of the values from this Vector */
	public MatrixDouble subAt(final double[] values, final int start, final int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			COPY_AT(items, values, itemCount, stop);
			SUB_AT(items, values, start, itemCount);
			//			normalizeAt();
		} else if (stop < itemCount) { //don't need to (re-)normalize
			SUB_AT(items, values, start, stop);
		} else {
			SUB_AT(items, values, start, stop);
			//			normalizeAt(); //might be quite improbable though!
		}
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixDouble addAt(final double value) {
		MatrixDouble.ADD_AT(items, value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixDouble subAt(final double value) {
		MatrixDouble.ADD_AT(items, -value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixDouble addAt(final float[] values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			COPY_AT(items, values, itemCount, stop);
			ADD_AT(items, values, start, itemCount);
			//normalizeAt();
		} else if (stop < itemCount) { //don't need to (re-)normalize
			ADD_AT(items, values, start, stop);
		} else {
			ADD_AT(items, values, start, stop);
			//normalizeAt();
		}
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixDouble addAt(final double[] values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			COPY_AT(items, values, itemCount, stop);
			ADD_AT(items, values, start, itemCount);
			//normalizeAt();
		} else if (stop < itemCount) { //don't need to (re-)normalize
			ADD_AT(items, values, start, stop);
		} else {
			ADD_AT(items, values, start, stop);
			//normalizeAt();
		}
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// Characterization of the Matrix
	////////////////////////////////////////////////////////////////////////////////
	
	/** true, when the Matrix is orthogonal, i.e. M*Mt = Mt*M = diag(a, b, c, ...).
	  * If a Matrix contains complex coefficients, it should be checked to be unitarian.
	  */
	final public boolean isOrthogonal() { //
		return IS_ORTHOGONAL(items);
	}
	
	/**true, when the Matrix is unitarian resp. orthonormal, i.e. M*Mt = Mt*M = 1.
	 * unitarian is the complex equivalent to orthonormal 	 */
	final public boolean isUnitarian() { //The Optimization here is that you have to test only one Triangle
		return IS_UNITARIAN(items); 
	}
		
	/**true, when the Matrix is hermitean resp. symmetric, i.e. M = Mt.	 */
	final public boolean isHermitean() { //The Optimization here is that you have to test only one Triangle
		return IS_HERMITEAN(items); 
	}
			
	/**true, when the Matrix is anti-hermitean resp. anti-symmetric, i.e. M = -Mt.	 */
	final public boolean isAntiHermitean() { //The Optimization here is that you have to test only one Triangle
		return IS_ANTI_HERMITEAN(items);
	}
		
	/**true, when the Matrix is normal, i.e. M*M^T = M^T*M.
	 * i.e. M*M^T is symmetric
	 * A normal Matrix has a complete Set of orthonormal Eigenvectors. 
	 * Non-normal Matrices may have (right) Eigenvectors, 
	 * but these are not orthogonal to each other, 
	 * only to their corresponding left Eigenvectors. 
	 */
	public boolean isNormal() { //
		return IS_NORMAL(items); 
	}
	
	/** Returns whether this matrix is symmetric.
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = a[j][i].
	  */
	public boolean isSymmetric() {
		return IS_SYMMETRIC(items, itemCount);
	}

	/** Returns whether this matrix is anti-symmetric.
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = -a[j][i].
	  */
	public boolean isAntiSymmetric() {
		return IS_ANTI_SYMMETRIC(items, itemCount); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Mapping and Concatenation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Scalar Product Multiplication from the right: �
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is the Derivative Jacobian Matrix of the Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  * The last  Index of the first Argument A has to match
	  * the first Index of the last  Argument B
	  * @param a the left  Row Vector
	  * @return the Product Vector A*B
	  */
	public double[] map(final double[] a) { //previously named mul()
		//composeLuAt(); //can also map in decomposed Form!
		//can be performed in Place!
		if (isDecomposedLU()) {
			return MAP(items, rows, a);
		} else {
			return MAP(a, items);
		}
	}

	/** maps the Vector in Place from the right
	 * 
	 * @param a the Vector to multiply with
	 */	
	public void mapAt(final double[] a) { //previously named mul()
		if (isDecomposedLU()) {
			MAP_AT(items, rows, a);
			//MAP_AT(a, items, rows); //maps from the left
		} else {
			VectorDouble.COPY(MAP(a, items), a);
		}
	}
	
	/** Scalar Product Multiplication: �
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A consisting of Row Vectors multiplied from the Left
	  *
	  * Distributive Law applies:
	  * M�(a+b) == M�a + M�b
	  * (x+y)�M == x�M + y�M
	  *
	  * The Matrix itself is the Derivative Jacobian Matrix of the Mapping:
	  * (A*x)' = A
	  *
	  * Here the Tensor Multiplication is defined recursively
	  * between two Tensors of these Dimensions: (n,m)*(m,k) => (n,k)
	  * The innermost (last ) Dimension of the left  Argument has to match
	  * the outermost (first) Dimension of the right Argument.
	  * The Transposition is done implicitly, only the Complement
	  * has to be done in place manually, if needed for complex coefficients.
	  *
	  * Since higher Coefficients are assumed to be null resp. Zero,
	  * it is sufficient to multiply only to the lesser Degree of both Vectors.
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  * The last  Index of the first Argument A has to match
	  * the first Index of the last  Argument B
	  * @param a the left  Row Vector
	  * @param b the right Matrix
	  * @return the Product Vector A*B
	  */
	public double[][] cat(final double[][] a) { //previously named mul()
		return CAT(a, items);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : LU (De-)Composition
	////////////////////////////////////////////////////////////////////////////////
		

	/**linear equation solution by LU decomposition	in Place.	
	 * @return the Sign of the Permutation
	 */
	final public boolean decomposeLU() {
		return DECOMPOSE_LU(items, rows);
	}
				
	/** Re-Composition of LU decomposition	in Place.	*/
	final public double[][] composeLU() {
		return COMPOSE_LU(items, rows);
	}
					
	/** Re-Composition of LU decomposition in Place.
	  * Undoes the Permutation of Rows also.
	  * This Operation can be done in Place,
	  * if you start from Bottom Left, because this Element == a[i,j]
	  * is only used within this same line.
	  */
	final public void composeLuAt() {
		if (! isDecomposedLU()) {
			return; }
		COMPOSE_LU_AT(items, rows);
		rows = null;
	}
					
	/**
	 * Prepares Solution of linear Equations by Lower-Upper (LU) decomposition in Place:
	 * An LU Decomposed Matrix is represented by an upper triangle Matrix
	 * and a lower triangle Matrix with only 1s in the Diagonal.
	 * The Diagonal itself belongs to the upper Triangle Matrix:
	 * A = L*U
	 * Is an n3/3 Algorithm, i.e. 3 times faster than calculating the Inverse
	 * Sufficient for calculating the Determinant also!
	 * The Matrix is replaced in Place by it's decomposed Matrix,
	 * The Index Vector 'Rows' keeps track of the Row Permutations.
	 * @param a the Matrix is returned as changed into LU Form
	 * @param Rows Array with Capacity of a.length to hold the 'Permutation' of Rows.
	 *        Actually this is not a real Permutation!
	 * @return the Sign of the Permutation in Rows
	 */
	public boolean decomposeLuAt() { //N3/3 Algorithm
		if (isDecomposedLU()) {
			return sign; }
		rows = new int[itemCount];
		return DECOMPOSE_LU_AT(items, rows);
	}
			
	/**
	 * Solves the linear equation with Matrix B by Backsubstitution after Decomposition
	 * B = A*ret <=> ret = A'*B with
	 * @param b is replaced by the Solution in Place.
	 */
	public void solveAt(final double[][] b) {
		decomposeLuAt(); //split up in lower and upper Diagonal
		SOLVE_LU_AT(items, rows, b);
	}
		
	/**
	 * Solves the linear equation by Backsubstitution after Decomposition
	 * b = A*ret = L*U*ret <=> ret = A'*b with Column Vector b
	 * @param b is replaced by the Solution in Place.
	 */
	public void solveAt(final double[] b) {
		decomposeLuAt(); //split up in lower and upper Diagonal
		SOLVE_LU_AT(items, rows, b);
	}
	
	/** improves the given Solution s for a in Place, so that M*s=a
	 * 
	 * @param a Vector to solve for
	 * @param solution preliminary Solution
	 */
	public void improve(final double[] a, final double[] solution) {
		//r = M*s-a
		double[] residuum = VectorDouble.SUB_AT(map(solution), a); 
		solveAt(residuum);
		VectorDouble.SUB_AT(solution, residuum); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// reading the Data from a ResultSet
	////////////////////////////////////////////////////////////////////////////////
	
	/**Graphics DeSerialization Routine: 
	 * Reads the Order of the Points of a single Plane from the current ResultSet
	 * @param rs the ResultSet to read from
	 * @param numPointsColumn 
	 * 		if positive Column that contains the Number of Points in this Plane
	 * 		if positive the Number of Points to read. 
	 * @param columnOffset the Column to start reading from when Cols == null
	 * @param columns the List of Column Indices to read, null when consecutive!
	 * @param Plane the Plane returned; when null, a new Plane is created
	 * @return the Plane read.
	 */
	final public MatrixDouble read(final ResultSet rs)
		throws SQLException {
		return read(rs, 0); 
	}

	/**Graphics DeSerialization Routine: 
	 * Reads the Order of the Points of a single Plane from the current ResultSet
	 * @param rs the ResultSet to read from
	 * @param numPointsColumn 
	 * 		if positive Column that contains the Number of Points in this Plane
	 * 		if positive the Number of Points to read. 
	 * @param columnOffset the Column to start reading from when Cols == null
	 * @param columns the List of Column Indices to read, null when consecutive!
	 * @param Plane the Plane returned; when null, a new Plane is created
	 * @return the Plane read.
	 */
	final public MatrixDouble read(final ResultSet rs, final int columnOffset)
		throws SQLException {
		return read(rs, Integer.MAX_VALUE, columnOffset, -1); 
	}

	/**Graphics DeSerialization Routine: 
	 * Reads the Order of the Points of a single Plane from the current ResultSet
	 * @param rs the ResultSet to read from
	 * @param numPointsColumn 
	 * 		if positive Column that contains the Number of Points in this Plane
	 * 		if positive the Number of Points to read. 
	 * @param columnOffset the Column to start reading from when Cols == null
	 * @param columns the List of Column Indices to read, null when consecutive!
	 * @param Plane the Plane returned; when null, a new Plane is created
	 * @return the Plane read.
	 */
	final public MatrixDouble read(final ResultSet rs, int maxNumPlanes, 
			final int columnOffset, int lastCol) throws SQLException {
		final VectorDouble vector = new VectorDouble(10); //TODO: hardcoded Capacity
		while (--maxNumPlanes >= 0) { //
			final VectorDouble row; 
			if (null == (row = vector.read(rs, columnOffset, lastCol))) 
				break; 
			this.addItem(row, false); 
		}
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** non-singular Test Matrix	 */
	private static final double[][] testMatrix = 
		{ { 1, 2, 3, 4, 5 }, {
			2, 3, 4, 5, 1 }, {
			3, 4, 5, 1, 2 }, {
			4, 5, 1, 2, 3 }, {
			5, 1, 2, 3, 4 }
	};
	
	/** Test Vectors for the Test Matrix	 */
	private static final double[][] testVectors = 
		{ { 1, 2, 3, 4, 5 }, {
			1, 1, 1, 1, 1 }
	};
		
	/** Returns a copy of the fixed sample test matrix used by this class's self-tests.
	 * @return a Copy of the Test Matrix above	 */
	final static public double[][] getTestMatrix() {
		return COPY(testMatrix);
	}

	/** Returns a copy of the fixed sample test vectors used by this class's self-tests.
	 * @return a Copy of the Test Matrix above	 */
	final static public double[][] getTestVectors() {
		return COPY(testVectors); 
	}
	
	/** testing LU Decomposition	 */
	public static void testLUDecomposition() { //throws java.io.IOException {
		L.n("\ntesting LU Decomposition");
		final int N = 8;
		//Test Matrix
		//Decomposition
		testLUDecomposition(testMatrix);
		
		//final double[] diag = new double[N];
		//2nd Test Matrix
		final double[][] a = new double[N][N];
		for(int i = N;--i >= 0;) {
			a[i][i] = i + 1;
			//diag[i] = i+1
		}
		testLUDecomposition(a);
		
		//3rd Test Matrix
		for(int i = N;--i >= 0;) {
			int j = i;
			while (--j >= 0) {
				a[i][j] = 2;
			}
		}
		testLUDecomposition(a);
		
		//4th Test Matrix
		RANDOMIZE_AT_1_1(a);
		//diagAt(a, diag); //ensure the Matrix is not singular
		testLUDecomposition(a);
	}

	/** Tests Decomposition of the Matrix into Lower and Upper Matrix.
	 * 
	 * @param a Matrix to decompose
	 * @param Rows Permutation of Rows
	 */
	public static void testLUDecomposition(final double[][] a) { //throws java.io.IOException {
		testList();  
		L.n("\ntesting LU Decomposition:");
		double[][] c = COPY(a);
		double[][] inv = RCP(a);
		double[][] prod = MAP(a, inv, null);  
			
		MatrixDouble matrix = new MatrixDouble(a); 
		L.n("\nOriginal:");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, c, "\n ");
		matrix.decomposeLuAt();
		L.n("\nAfter Decomposition:");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, a, "\n ");
		matrix.composeLuAt();
		L.n("\nAfter Recomposition:\n");
		AStreamOut.ARRAY_TO_STREAM(L, a, "\n ");
		Assert.EQUALS(c, a); //catches any Exception!
		//Assert.GET_AVAILABLE();
	}

	/**
	 * 
	 */
	private static void testList() {
		Vector v = new Vector();
	}

	/** Tests the symmetric Product v*A*v	 */
	public static void testSymmetricProduct() { //
		L.n("\nTesting LU Backsubstition");
		final int N = 8;
		final double[][] a = new double[N][N];
		final double[] v = new double[N];
		
		//3rd Test Matrix: random
		RANDOMIZE_AT_1_1(a);
		VectorDouble.RANDOMIZE_AT_1_1(v);
		final double[][] aCopy = COPY(a);
		final MatrixDouble matrix = new MatrixDouble(a); 
		L.n("\nOriginal Vector:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, v, " ");
		L.n("\nOriginal Matrix:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, aCopy, "\n ");
		final double[] solution = VectorDouble.COPY(v);
		matrix.solveAt(solution); //solve the System A*w=v for the right Side w
		L.n("\nSolution:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, solution, " ");
		final double prod1 = VectorDouble.MAP(solution, v);
		L.n("Prod1:").l(prod1);
	}
	
	/** Tests Backsubstition	 */
	public static void testLUBackSubstition() { //throws Exception {
		L.n("\nTesting LU Backsubstition");
		final int N = 8;
		final double[][] a = new double[N][N];
		final double[] v = new double[N];
		
		//1st Test Matrix: non-singular diagonal Matrix
		for(int i = N;--i >= 0;) {
			a[i][i] = v[i] = i + 1;
		}
		testLUBackSub(a, v);
		
		//2nd Test Matrix: Lower Triangle
		for(int i = N;--i >= 0;) {
			int j = i;
			while (--j >= 0) {
				a[i][j] = 2;
			}
		}
		testLUBackSub(a, v);
		
		//3rd Test Matrix: random
		RANDOMIZE_AT_1_1(a);
		VectorDouble.RANDOMIZE_AT_1_1(v);
		testLUBackSub(a, v);
	}
	
	/** Tests Backsubstition
	 * 
	 * @param a Matrix to decompose
	 * @param Rows Permutation Vector 
	 * @param v Vector to solve for
	 */
	private static final void testLUBackSub
	( final double[][] a, final double[] v) { //throws java.io.IOException {
		L.n("\nTesting LU Backsubstition:");
		final double[][] aCopy = COPY(a);
		final double[][] aTrp = TRP(a); //
		final double[] vCopy = VectorDouble.COPY(v);
		final MatrixDouble matrix = new MatrixDouble(a);
		
		L.n("\nOriginal Vector:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, v, " ");
		L.n("\nOriginal Matrix:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, aCopy, "\n ");
		final double[] solution = VectorDouble.COPY(v);
		matrix.solveAt(solution); //solve the System A*w=v for the right Side w
		L.n("\nSolution:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, solution, " ");
		
		//add some Noise to the Result
		for(int i = solution.length; --i >= 0;) {
			solution[i] += Math.random()*0.1;
		}
		L.n("\nSolution with Noise:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, solution, " ");
		matrix.improve(v, solution);
		L.n("\nimproved Solution:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(L, solution, " ");
		
		//Test the Solution by performing w*A^t==v with cached Copy
		double[] mapdSoln = MAP(solution, aTrp); 
		L.n("\nMapped Solution:\n");
		AStreamOut.ARRAY_TO_STREAM(L, mapdSoln, " ");
		Assert.EQUALS(vCopy, mapdSoln); //catches any Exception!
		double[] mapdSoln2 = matrix.map(solution); //w*A^t==v with A = LU decomposed directly
		L.n("\nMapped Solution:\n");
		AStreamOut.ARRAY_TO_STREAM(L, mapdSoln2, " ");
		Assert.EQUALS(mapdSoln, mapdSoln2); //catches any Exception!
		//Assert.GET_AVAILABLE();
	}

	/** Tests the Alignment of a Vector along	 */
	public static void testAlignment() {
		L.n("\nTesting Alignment");
		double[] v = new double[5];
		VectorDouble.RANDOMIZE_AT_1_1(v);
		//v[1] = v[0] = 1; //e-10f;
		double[] c = VectorDouble.COPY(v);
		double[][] mat = TRP_AT(alignMatrixAt(c, 2));
		double[] ret = MAP(v, mat);
		Log.L(v).n();
		ret = MAP(v, mat); //Unitarian Matrix, transposition inverts the Matrix.
		Log.L(ret).n();
		Log.L(mat).n();
	}

	/**
	 * Create a random Matrix
	 * and try to sort the Dimensions via Sorting the Dimensions 
	 * The Maximum Values are aligned on the top left Corner
	 */
	private static final void testPivoting() {
		testPivoting(true);
		testPivoting(false);
	}

	/**
	 * Create a random Matrix
	 * and try to sort the Dimensions via Sorting the Dimensions 
	 * The Maximum Values are aligned on the top left Corner
	 */
	private static final void testPivoting(final boolean sumNorm) {
		L.n("\ntesting Pivoting (Sorting a Matrix along Rows and/or Columns");
		double[][] copy, test = COPY(MatrixFloat.testData); // randomizeAt(new double[6][6]);
		final String criterion = sumNorm?"Sum":"max. Value"; 
		L.n("Sorting by "+criterion+": ");
		L.n("Before sorting:\n");                           STREAM(test, L); test = SORT_COLS_BY_MAX_AT(test, sumNorm, true); 
		L.n("Cols sorted by their ").l(criterion).l(":\n"); STREAM(test, L); test = SORT_ROWS_BY_MAX   (test, sumNorm, true); 
		L.n("Rows sorted by their ").l(criterion).l(":\n"); STREAM(test, L); copy = SORT_COLS_BY_MAX   (test, sumNorm, true); Assert.EQUALS(test, copy, 0, 0.0, "Sorting should be idempotent!"); 
		L.n("Cols sorted by their ").l(criterion).l(":\n"); STREAM(copy, L); copy = SORT_ROWS_BY_MAX   (copy, sumNorm, true); Assert.EQUALS(test, copy, 0, 0.0, "Sorting should be idempotent!"); 
		L.n("Rows sorted by their ").l(criterion).l(":\n"); STREAM(copy, L);
		L.n();
	}
	
	/** 
	 * tests statistical Diagonalization by permuting Rows and Columns 
	 * either for Rows and Columns independently or coupled.  
	 * @param sumNorm
	 */
	protected static final void testDiagonalization() {
		final double[][] testData = COPY(MatrixFloat.testData); 
		for(int i = testData.length; --i >= 0;)
			Assert.EQUALS(0, COST_OF_SWAP(testData, i, i));
		L.n("Before sorting:\n"); STREAM(testData, L); 
		L.n("Overall Energy Reduction: ").l(CLUSTER(testData));
		L.n("After  sorting:\n"); STREAM(testData, L); 
	}
	
	/** Tests all Pivot-Table Methods for a Matrix	 */
	public static void testPivot() { //throws java.io.IOException {
		final int MAX_ROW = 10;  
		final int MAX_VALS = 1000;  
		final double[][] firstVals = new double[MAX_ROW][MAX_ROW]; 
		final MatrixDouble   min = new MatrixDouble(); 
		final MatrixDouble   max = new MatrixDouble(); 
		final MatrixDouble   sum = new MatrixDouble(); 
		final MatrixDouble count = new MatrixDouble(); 
		final MatrixDouble first = new MatrixDouble(); 
		final MatrixDouble  last = new MatrixDouble(); 
		for(int i = MAX_VALS; --i >= 0;) {
			int rowNum = (int) (MAX_ROW*Math.random()); 
			int colNum = (int) (MAX_ROW*Math.random());
			double value = 1-Math.random();
			min  .opAt(rowNum, colNum,-value, OpMin  .OpMin  ); 
			max  .opAt(rowNum, colNum, value, OpMax  .OpMax  ); 
			sum  .opAt(rowNum, colNum, value, OpSum  .OpSum  ); 
			count.opAt(rowNum, colNum, value, OpCount.OpCount); 
			first.opAt(rowNum, colNum, value, OpFirst.OpFirst); 
			last .opAt(rowNum, colNum, value, OpLast .OpLast ); 
			double firstVal = firstVals[rowNum][colNum]; 
			if (firstVal == 0) 
				firstVal  = firstVals[rowNum][colNum] = value; 
			Assert.EQUALS (firstVal, first.getAt(rowNum, colNum)); 
			Assert.EQUALS ( value  ,  last.getAt(rowNum, colNum));
			Assert.IS_TRUE(-value >=   min.getAt(rowNum, colNum));
			Assert.IS_TRUE( value <=   max.getAt(rowNum, colNum));
			Assert.IS_TRUE( value <=   sum.getAt(rowNum, colNum));
		}
		double[] RowCounts = count.rowSums();
		double   SumCounts = VectorDouble.SUM(RowCounts); 
		Assert.EQUALS(MAX_VALS, SumCounts); 
	}
	
	/** Returns a new array holding the sum of every row's values. */
	public double[] rowSums() {
		final double[] ret = new double[this.itemCount];
		for(int i = ret.length; --i >= 0; )
			ret[i] = VectorDouble.SUM(this.items[i]);
		return ret;
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) { //throws java.io.IOException {
		L.n("\nTesting " + MatrixDouble.class.getName());
		testLUDecomposition();
		testLUBackSubstition();
		testPivot(); 
		testDiagonalization(); 
		testSymmetricProduct();
		testPivoting();
		testAlignment();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) { //throws java.io.IOException {
		testIt(args);
	}

}

/** Iterator for the MatrixDouble Class (in reverse Order)
 * to iterate over the Row Vectors.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T13:04:13Z
 * digest: 3c8e7c897244549b353e1f5c6aa7c1b8a4413c0be34cb3769c4491afc0b8578c
 * stale: false
 * tags: [code/matrix_operations]
 * concepts: [Double Matrix Row Stream Iterator]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 */
final class MatrixDoubleStreamIn
extends AStreamIn {

	protected int currItem;

	final MatrixDouble matrix;

	/** Creates a reverse-order row iterator positioned just past the last row of {@code matrix_}. */
	public MatrixDoubleStreamIn(final MatrixDouble matrix_) {
		this.matrix = matrix_;
		currItem = matrix.getInt();
	}

	/** Returns the row vector at the current iteration position. */
	public double[] currVector() {
		if (currItem >= matrix.getInt())
			throw new IllegalStateException("no row read yet: call nextVector() first");
		return matrix.items[currItem]; }

	/** Decrements the iteration position and returns the row vector now at it. */
	public double[] nextVector(){ return matrix.items[--currItem]; }

	/** Returns the next row vector as an untyped item.
	 * @see Stream.IFactory#nextItem()	 */
	public Object nextItem() { return nextVector(); }

	/** Returns the current row vector as an untyped item.
	 * @see Stream.Object.StreamIn#currItem()	 */
	public Object currItem() { return currVector(); }

	/** Returns the number of rows still available before the start of the matrix.
	 * @see Stream.IAvailAble#availAble()	 */
	public long availAble() { return currItem; }

	/** Returns the total number of rows in the underlying matrix.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return matrix.getInt(); }

	/** Returns how many rows have been consumed since iteration started.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return matrix.getInt()-currItem; }

	/** Moves the iteration position by the given offset and returns the distance moved.
	 * @see streamIO.IReSetAble#jump(long)	 */
	public long jump(final long _position) {
		return currItem-(currItem-=_position); }

}
