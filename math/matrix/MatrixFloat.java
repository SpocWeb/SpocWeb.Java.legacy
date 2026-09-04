package math.matrix;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import math.NumberFormatter;
import math.vector.HunterFloat;
import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import graphs.AGraph;
import graphs.MatrixGraph;

/**
  * Title: MatrixFloat<p>
  * Description:
  * Class implements a dynamic Vector of VectorFloat Elements 
  * each representing a Row. 
  * Also implements many Methods to handle non-dynamic 2-dim Arrays of float[][]. 
  * Instances of this Class operate as Matrices that can be used for...
  * Solving Equations by decomposing into upper and lower Triangle Matrices: A = U*L 
  * Mapping Vectors with the actual or the decomposed Form, 
  * calculating Eigenvalues
  * etc.  
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-08-2002, 01:10 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class MatrixFloat 
extends AMatrix {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log L = new Log(MatrixFloat.class); 
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////

	/** 
	 * Swaps the Rows of both Matrices (instead of each Cell 
	 * @param a first  Matrix
	 * @param b second Matrix
	 */
	public static final void SWAP(final float[][] a, final float[][] b) {
		if (a == b)
			return; 
		for(int i = a.length; --i >= 0;) {
			final float[] tmp = a[i]; a[i] = b[i]; b[i] = tmp;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Matrix Power & Exponentiation
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final float[][] POW(final float[][] matrix, int n) {
		boolean odd; 
		if			(n <  1) return POW(REV(matrix), -n);
		if (odd =  ((n &  1) != 0))
			if		(n == 1) return matrix;
		float[][] sqr  = new float[matrix.length][matrix.length];
		float[][] sng  = COPY(matrix); 
		float[][] self = new float[matrix.length][matrix.length];
		for(;n > 1;){		//Use the Horner Scheme in the Exponent.
			MAP(sng, sng, sqr); 
			if (((n >>= 1) & 1) != 0) { 	//(N1.isOdd())
				if (odd) 
					MAP(self, sqr, sng); //for even Powers
				else {
					COPY_AT(sng, sqr); odd = true; 
				} 	//save one Matrix Multiplication in the beginning here
				final float[][] tmp = sng; sng = self; self = tmp; 
			}
			final float[][] tmp = sng; sng = sqr; sqr = tmp; 
		}	//(! N1.halfAt().IntAt().equals(mZERO))
		return self; 
	}
	
	/**
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final float[][] BXP_AT(final float[][] matrix, final int pow) {
		return BXP_AT(matrix, pow, null); }
	
	/**
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final float[][] BXP_AT(final float[][] matrix, final int pow, final float[][] work) {
		final float[][] power = BXP(matrix, pow, matrix, work); 
		if (power != matrix)
			SWAP(power, matrix); 
		return matrix; }
	
	/**
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final float[][] BXP(final float[][] matrix, final int pow) { 
		return BXP(matrix, pow, null, null); }
	
	/**
	 * @param matrix
	 * @return the Matrix raised to the binary Power of pow: M^(2^pow)
	 */
	public static final float[][] BXP(final float[][] matrix, final int pow, float[][] power, float[][] work2) {
		if (pow == 0)
			return matrix; 
		if (power == null) 
			power  = new float[matrix.length][matrix.length];
		if (work2 == null) 
			work2  = new float[matrix.length][matrix.length];
		float[][] base = matrix; //use matrix directly for the first Iteration (but not later ones!  
		for(int i = pow; --i >= 0;) { //should be sufficient to minimize all other Eigenvalues. 
			MAP(base, base, power); 
			if (IS_MATRIX_SIMPLE(power)) //stop, if the Matrix has a dominant Eigenvalue
				return power;
			if (base == matrix) //superfluous for BXP_AT, since matrix is handed over twice! 
				base  = work2; 
			final float[][] tmp = base; base = power; power = tmp; 
		}
		return base; }

	/**
	 * @param base the Matrix to check
	 * @return true when the Matrix consists only of 'simple' Rows
	 */
	public static final boolean IS_MATRIX_SIMPLE(final float[][] base) {
		for(int j = base.length; --j >= 0;) {
			if (!IS_ROW_SIMPLE(base[j]))
				return false; 
		}
		return true; 
	}
	
	/**
	 * @param row the Row to check
	 * @return true when all Elements of the Row have the same Value. 
	 */
	public static final boolean IS_ROW_SIMPLE(final float[] row) {
		for(int k = row.length; --k > 0;) {
			if (!ByRefDouble.EQUALS(row[k], row [0]))
				return false; 
		}
		return true; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods for Calculations on Polygons
	////////////////////////////////////////////////////////////////////////////////

	/** @return the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 	 */
	public float[][] getExtent() { return EXTENT(null, this.items, 0, this.itemCount); } 

	/** 
	 * returns the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 
	 * 
	 * @param arg the Vectors to calculate the Extent for 
	 * @return the Extent of the Polygon
	 */
	final static public float[][] EXTENT(final float[][] arg) { return EXTENT(null, arg, 0, arg.length); } 

	/** 
	 * returns the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 
	 * 
	 * @param min_max an existing Extent can be extended
	 * @param arg the Vectors to calculate the Extent for 
	 * @return the Extent of the Polygon
	 */
	final static public float[][] EXTENT(float[][] min_max, final float[][] arg) {
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
	final static public float[][] EXTENT(float[][] min_max, final float[][] arg, final int start, final int stop) {
		if (stop <= 0) //Optimization: 
			return null; 
		if((min_max == null) || (min_max.length < 2))
			min_max =  new float[2][]; 
		COL_MIN(arg, min_max[0]); //, start, stop);
		COL_MAX(arg, min_max[1]); //, start, stop);
		return min_max;
	}

	/**Calculates the Length of the given Path	 */
	final static public double PATH_LENGTH(final float[][] x, final boolean closed, final int[] order) {
		return PATH_LENGTH(x, closed, order, 0, x.length); }

	/**Calculates the geometric Length of the given (closed) Path
	 * @see graphic.AGraph2D#drawPolygon(int[], int[], boolean)
	 */
	final static public double PATH_LENGTH(final float[][] x, final boolean closed, final int[] order, final int start, final int stop) {
		double path = 0;
		int i = stop;
		int i2 = (order != null ? order[0] : 0);
		if (!closed) { --i; 
			i2 = (order != null ? order[i] : i);
		}
		for (; --i >= start;) {	//the absolute Path doesn't really matter!
			int i1 = i2; i2 = (order != null ? order[i] : 0);
			path += VectorFloat.DIST(x[i1],x[i2]);
		}
		return path; }

	/** Returns the Vectors orthogonal to the Points (only for 3Dim Tensors!)
	 * It is only slightly more effective to calculate all Normals at once	 
	 * @param numPoints The total Number of Points i.e. Vertices 
	 * @param planes The Plane Definitions i.e. the Lists of Vertices to each Plane 
	 * @param planeNormals the List of Normals for each Plane
	 * @return a List of Normals 
	 */
	final static public float[][] POINT_NORMALS(final int numPoints
	, final int[][] planes, final float[][] planeNormals) {	//should be protected!
		final float[][] pointNormals = new float[numPoints][];
		//Optimization: O(Pln)*3: just sum up all Normals on the Fly
		//instead of searching for specific Points! 
		for(int i = planes.length; --i >= 0;) { //
			final int[] plane  = planes[i];
			for(int j = plane.length; --j >= 0; ) { 
				final int point = plane[j]; //
				if (pointNormals[point] != null) { 
					VectorFloat.ADD_AT(pointNormals[point], planeNormals[i]);
				} else { //Optimization: copy instead of add, if possible
					pointNormals[point] = VectorFloat.COPY(planeNormals[i]); 
				}
			}
		}
		for (int point = numPoints; --point >= 0;) {
			//pointNormals[point] = PointNormal(planes, pointNormals, point); //O(Pt*Pln*3)
			VectorFloat.NORMALIZE_AT(pointNormals[point]);
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
	final static public float[] POINT_NORMAL(
		final int[][] planes,
		final float[][] planeNormals,
		final int point) {
		float[] sum = null;
		for(int i = planes.length; --i >= 0;) { //Search all Planes... 
			final int[] plane  = planes[i];
			for(int j = plane.length; --j >= 0; ) { 
				if (plane[j] == point) { //...for those containing this Point
					if (sum != null) { //Optimization: copy instead of add, if possible
						VectorFloat.ADD_AT(sum, planeNormals[i]);
					} else {
						sum = VectorFloat.COPY(planeNormals[i]); }
				}
			}
		}
		if (sum != null) { //due to Norming, the actual Number of Planes 
			VectorFloat.NORMALIZE_AT(sum); } //does not matter!
		return sum; }

	/**
	 * Calculates the Distances of all points to the given PointOfView.
	 * @param PointOfView single fixed Point for which to calculate all Distances for 
	 * @param points the points to calculate the Distances to
	 * @return the Distances of the Points from the given Point
	 */
	final static public float[] ABSV_DIST(final float[] PointOfView, final float[][] points) {
		float[] ret = new float[points.length];
		for(int i = points.length; --i >= 0; ) {
			ret[i] = VectorFloat.DIST_ABS(points[i], PointOfView); }
		return ret;
	}

	/**
	 * @param points 
	 * @param plane
	 * @return the middle Points of this Plane
	 */
	public static float[] GET_MID_POINT(final float[][] points, final int[] plane) {
		final float[] mid = VectorFloat.COPY(points[plane[0]]);
		for(int j = plane.length; --j > 0;) { 	//Skip the Zero Point
			VectorFloat.ADD_AT(mid, points[plane[j]]); }	//subtract the Offset
		return VectorFloat.MUL_AT(mid, 1.0f / plane.length);
	}

	/**
	 * @param points 
	 * @param planes
	 * @return the middle Points of the Planes
	 */
	final static public float[][] GET_MID_POINTS(final float[][] points, final int[][] planes) {
		final float[][] mids = new float[planes.length][];
		for(int i = planes.length; --i >= 0; ) {
			final int[] plane = planes[i]; 
			final float[] mid = MatrixFloat.GET_MID_POINT(points, plane);
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
	final static public float[][] PLANE_NORMALS(final float[][] points
	, final int[][] planes) {
		final float[][] planeNormals = new float[planes.length][];
		final float[] diff1 = new float[planes.length]; //Optimization:
		final float[] diff2 = new float[planes.length]; //reuse 
		for(int i = planes.length; --i >= 0; ) {
			final int[] plane = planes[i];
			if (plane.length < 3) {
				continue; }
			planeNormals[i] = VectorFloat.NORMAL(diff1, diff2
			, points[plane[0]], points[plane[1]], points[plane[2]], true);
		}
		return planeNormals;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : simulated Annealing for Clustering
	////////////////////////////////////////////////////////////////////////////////
	
	/** Number of initial Moves for Estimation of the necessary Temperature	 */
	public static int NUM_ESTIMATE_MOVES = 10; 
	
	/**Metropolis algorithm, used by anneal() (10.9)
	 * Determines Acceptance, based on the Temperature for Annealing 
	 * The starting Temperature could be determined from the first de, 
	 * so that e.g. the initial estimated Rejection Rate would be 50%, i.e. 
	 * .5 = Math.exp(-de0/t0) <=> -Ln(.5) = de0/t0 <=> t0 = -de0/Ln(.5) = 1.44*de0
	 */
	public static boolean ACCEPT_DELTA_ENERGY(final double de, final float t) {
		return (de < 0) || (AGraph.RANDOM.nextDouble() < Math.exp(-de/t));
	}
	
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
	public float getEnergy() { return ENERGY(this.items); }
	
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
	final static public float ENERGY(final float[][] x) { //, final int n) {
		float ret = 0; 
		for(int i=x.length; --i >= 0;) {
			final float[] x_i = x[i];  
			for (int j= x_i.length; --j >= 0; )
				ret += Math.abs(x_i[j]*(i-j)); 
		}
		return ret; 
	}
	
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
	public float costOfSwap(final int n1, final int n2) { 
		return COST_OF_SWAP(this.items, n1, n2); }
	
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
	final static public float COST_OF_SWAP(final float[][] x, final int n1, final int n2) { //, final int n) {
		//if (n1 == n2)
		//	return 0; 
		float ret = 0; 
		//swap the Columns...
		for(int i=x.length; --i >= 0;) { //...in every Row
			if ((i==n1) || (i==n2)) {
				L.n("don't count the intersection Elements, since they balance out")
				.l("And you would have to consider Row AND Col Swap here. "); 
				continue; 
			}
			final float[] x_i = x[i];  
			ret += 
				Math.abs(x_i[n1]*(i-n2)) + 
				Math.abs(x_i[n2]*(i-n1)) -
				Math.abs(x_i[n1]*(i-n1)) -
				Math.abs(x_i[n2]*(i-n2)); 
		}
		//swap the Rows...
		final float[] x_n1 = x[n1];  
		final float[] x_n2 = x[n2];  
		for(int j=x.length; --j >= 0;) { //...for every Column
			if ((j==n1) || (j==n2)) {
				L.n("don't count the intersection Elements, since they balance out")
				.l("And you would have to consider Row AND Col Swap here. "); 
				continue; 
			}
			ret += 
				Math.abs(x_n1[j]*(j-n2)) + 
				Math.abs(x_n2[j]*(j-n1)) -
				Math.abs(x_n1[j]*(j-n1)) -
				Math.abs(x_n2[j]*(j-n2)); 
		}
		return ret; 
	}
	
	/** 
	 * tests statistical Diagonalization by permuting Rows and Columns 
	 * either for Rows and Columns independently or coupled.  
	 * @param sumNorm
	 * @return the overall Reduction in Energy from permuting the Indices
	 */
	protected float clusterAt(final float[][] weights) { return CLUSTER(this.items); }
	
	/** 
	 * tests statistical Diagonalization by permuting Rows and Columns 
	 * either for Rows and Columns independently or coupled.  
	 * @param sumNorm
	 * @return the overall Reduction in Energy from permuting the Indices
	 */
	protected static final float CLUSTER(final float[][] weights) {
		//Assert.A.FailureHandler = AStreamOut.DevNullOut; 
		float ret = 0; 
		final int numInner=100*weights.length;
		final int maxAccept=10*weights.length;
		int numInits = NUM_ESTIMATE_MOVES; //use the first Iterations to determine the appropriate Temperature. 
		float t=0;	//(initial) Temperature
		for(int j=100; --j>=0; ) { //for cooling the System 
			int numAccepted = 0;
			for (int k=numInner; --k>=0; ) { //choose two Indices
				int n1=(int) ( weights.length   *Math.random());	//
				int n2=(int) ((weights.length-1)*Math.random());	//
				if (n2 >= n1) ++n2;  	//make sure they're not the same
				//final float before = ENERGY(weights); 
				final double de = COST_OF_SWAP(weights, n1, n2); 
				if (--numInits > 0) 
					t+=Math.abs(de); 
				else if (numInits == 0) {
					t/=NUM_ESTIMATE_MOVES; L.n("Initial Temperature = Energy: ").l(t); 
				} else 
					if (ACCEPT_DELTA_ENERGY(de,t)) {
						ret += de; ++numAccepted; 
						SWAP_COLS_AT(weights, n1, n2);
						SWAP_ROWS_AT(weights, n1, n2);
						//final float after = ENERGY(weights); 
						//Assert.EQUALS(after-before, de); 
					}
				if (numAccepted >= maxAccept) 
					break; //too many Changes, reduce Temperature
			}
			t *= 0.9f; 
			L.n("numAccepted = "+numAccepted); 
			if (numAccepted == 0) 
				break; //too few Changes, stop Iteration. 
		}
		return ret; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Permutation Operations
	///////////////////////////////////////////////////////////////////////////
	
	/** copy original math.matrix into a newer (larger) one, shift right by 1 
	 * @param a Matrix to shift
	 * @return a new Matrix
	 */
	final static public float[][] SHR(final float[][] a) {
		final int m = a.length;
		final int n = a[0].length;
		final float[][] u=new float[1+m][1+n];
		for (int k=1; k<=m; k++) {
			for (int l=1; l<=n; l++) {
				u[k][l]=a[k-1][l-1];
			}
		}
		return u;
	}
	
	/**
	 * As an Optimization, a single new Row Vector is used as temporary Storage for the Permutation of all Rows.   
	 * @return this Vector with the Columns permuted according to the given Permutation     */
	final static public float[][] PERMUTE_COLS(final float[][] a, final int[] index) {
		return PERMUTE_COLS(a, index, false); }
	
	/**
	 * As an Optimization, a single new Row Vector is used as temporary Storage for the Permutation of all Rows.   
	 * @return this Vector with the Columns permuted according to the given Permutation     */
	final static public float[][] PERMUTE_COLS(final float[][] a, final int[] index, final boolean reverse) {
		final float[][] ret = new float[a.length][]; 
		for (int i = a.length; --i >= 0;) 
			ret[i] = HunterFloat.PERMUTE(a[i], index, reverse); 
		return ret; 
	}
	
	/** @return this Vector with the Rows permuted according to the given Permutation     */
	final static public float[][] PERMUTE_COLS_AT(final float[][] a, final int[] index) {
		return PERMUTE_COLS_AT(a, index, false); 
	}
	
	/** @return this Vector with the Rows permuted according to the given Permutation     */
	final static public float[][] PERMUTE_COLS_AT(final float[][] a, final int[] index, boolean reverse) {
		float[] tmp, swp = new float[index.length]; //reusing the Array since no Permutation in Place!
		for (int i = a.length; --i >= 0;) {
			tmp = a[i]; a[i] = HunterFloat.PERMUTE(tmp, index, reverse, swp);
			swp = tmp;
		}
		return a;
	}
	
	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public float[][] PERMUTE_ROWS(final float[][] a, final int[] index) {
		return PERMUTE_ROWS(a, index, false); }
	
	/** @return this Vector with the Rows permuted according to the given Permutation     */
	final static public float[][] PERMUTE_ROWS(final float[][] ret, final float[][] a, final int[] index) {
		return PERMUTE_ROWS(ret, a, index, false); }
	
	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public float[][] PERMUTE_ROWS(final float[][] a, final int[] index, final boolean reverse) {
		return PERMUTE_ROWS(null, a, index, reverse);
	}
	
	/** @return this Vector with the Rows permuted according to the given Permutation     */
	final static public float[][] PERMUTE_ROWS(float[][] ret, final float[][] a, final int[] index, final boolean reverse) {
		if (ret == null)
			ret = new float[a.length][]; 
		for (int i = index.length; --i >= 0;) {
			if (reverse)
				ret[index.length-1 - i] = a[index[i]];
			else
				ret[i] = a[index[i]];
		}
		return ret;
	}
	
	/// The following Code does not work, because in Place is not possible!
	/*		float[] tmp;	//Undo the Row Permutations!
			int j, k = a.length;
			while (--k >= 0) { //not a proper Permutation! sensitive to Sequence of Processing!
				if (perm[k] == k) {
					continue; }
				tmp = a[k]; a[k] = a[j = perm[k]]; a[j] = tmp; }
			return a; }
	*/
	
	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public float[][] PERMUTE_ROWS_AT(final float[][] a, final int[] index, boolean reverse) {
		final float[][] tmp = new float[a.length][];
		PERMUTE_ROWS(tmp, a, index, reverse);
		System.arraycopy(tmp, 0, a, 0, a.length);
		return a;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Sorts the Rows in this Matrix so the Row with Maximum Value or Sum ends up last
	 * (or first if reverse = true). 
	 * This Operation is idempotent and invariant to Column Swaps. 
	 * @see #SORT_COLS_BY_MAX_AT(float[][], boolean) can be combined 
	 * to sort both Rows and Columns, especially for CrossTab Query Results, 
	 * where each Cell has the same Dimension and is thus comparable. 
	 * When the Dimensions of the Columns are not comparable 
	 * and possibly have different Weights, it is necessary to normalize Columns
	 * either the COL_SUM or the COL_MAX has to be reduced to the respective Weight. 
	 * @param useSum uses the Sum instead of the maximum Value in each Row
	 * @return a new Matrix sharing the Rows 
	 */
	final static public float[][] SORT_ROWS_BY_MAX(final float[][] a, final boolean useSum, final boolean reverse) {
		final int[] indexRow = INDEX_ROWS_BY_MAX(a, useSum); 
		//Sort the Rows into a new Matrix so that tha Maximum Maximum ends up at the Top
		final float[][] tmp = PERMUTE_ROWS(a, indexRow, reverse);
		return tmp;
	}
	
	/** 
	 * Indexes the Rows in this Matrix so the Row with Maximum Value or Sum ends up last
	 * This Operation is idempotent and invariant to Column Swaps
	 * @see #SORT_COLS_BY_MAX_AT(float[][], boolean) can be combined to sort Rows and Columns
	 * @param useSum uses the Sum instead of the maximum Value in each Row
	 * @return the Index to use for the Array a 
	 */
	final static public int[] INDEX_ROWS_BY_MAX(final float[][] a, final boolean useSum) {
		final float[] maxVals = new float[a.length];
		if (useSum) { //Sum of Values (only for positive Values)
			ROW_SUM(a, 0, a.length, maxVals); 
		} else { //actual Maximums
			MAX_VAL(maxVals, a, 0, a.length); 
		}
		//sort by creating an Index to sort the Columns accordingly
		final int[] indexRow = HunterFloat.INDEX(maxVals);
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
	final static public float[][] SORT_ROWS_BY_WEIGHED_SUM(final float[][] a, final float[] weights, final boolean reverse) {
		final int[] indexRow = INDEX_ROWS_BY_WEIGHED_SUM(a, weights); 
		//Sort the Rows into a new Matrix so that tha Maximum Maximum ends up at the Top
		final float[][] tmp = PERMUTE_ROWS(a, indexRow, reverse);
		return tmp;
	}
	
	/** 
	 * Indexes the Rows in this Matrix so the Row with Maximum weighed Sum (Scalar Product) ends up last
	 * @return the Index to the Rows to be used... 
	 */
	final static public int[] INDEX_ROWS_BY_WEIGHED_SUM(final float[][] a, final float[] weights) {
		final float[] maxVals = MAP(a, weights);
		//sort by creating an Index to sort the Columns accordingly
		final int[] indexRow = HunterFloat.INDEX(maxVals);
		return indexRow;
	}
	
	/** 
	 * Sorts the Columns in this Matrix so the Column with Maximum Value ends up last
	 * (or first if reverse = true). 
	 * This Operation is idempotent and invariant to Row Swaps. 
	 * @see #SORT_ROWS_BY_MAX(float[][], boolean) can be combined to sort Rows and Columns
	 * @param useSum uses the Sum instead of the maximum Value in each Column
	 * @return the same Matrix with swapped Columns 
	 */
	final static public float[][] SORT_COLS_BY_MAX(final float[][] a, final boolean useSum, final boolean reverse) {
		return PERMUTE_COLS(a, INDEX_COLS_BY_MAX(a, useSum), reverse); }
	
	/** 
	 * Sorts the Columns in this Matrix so the Column with Maximum Value ends up last
	 * (or first if reverse = true). 
	 * This Operation is idempotent and invariant to Row Swaps. 
	 * @see #SORT_ROWS_BY_MAX(float[][], boolean) can be combined to sort Rows and Columns
	 * @param useSum uses the Sum instead of the maximum Value in each Column
	 * @return the same Matrix with swapped Columns 
	 */
	final static public float[][] SORT_COLS_BY_MAX_AT(final float[][] a, final boolean useSum, final boolean reverse) {
		final int[] indexRow = INDEX_COLS_BY_MAX(a, useSum); 
		//Sort the Columns within the same Matrix so that the max. Maximum ends up at the Top
		PERMUTE_COLS_AT(a, indexRow, reverse);
		//VectorFloat.permuteAt(maxVals, indexRow); //just to check...
		return a;
	}
	
	/** 
	 * Indexes the Columns in this Matrix so the Column with Maximum Value ends up last. 
	 * This Operation is idempotent and invariant to Row Swaps. 
	 * @see #SORT_ROWS_BY_MAX(float[][], boolean) can be combined to sort Rows and Columns
	 * @param useSum uses the Sum instead of the maximum Value in each Column
	 * @return the same Matrix with swapped Columns 
	 */
	final static public int[] INDEX_COLS_BY_MAX(final float[][] a, final boolean useSum) {
		final float[] maxVals = new float[a[0].length];
		if (useSum) {
			COL_SUM(a, 1, a.length, maxVals); //Sum of Values (only for positive Values)
		} else { //take first Row and ...
			System.arraycopy(a[0], 0, maxVals, 0, a[0].length); //... compare only the Rest
			COL_MAX(a, 1, a.length, maxVals); //actual Maximums of Columns
		}
		//sort by creating an Index
		final int[] indexRow = HunterFloat.INDEX(maxVals); 
		return indexRow; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @return the Minimum and Maximum Values of each Column... 
	 * too complex to optimize for now... 
	 * 
	 * Use Min and Max separately, which is clearer too!
	 */
	//	final static public float[][] MIN_MAX(float[][] arr) { }

	/**
	 * @return the Sum of all Matrix Elements
	 */
	final static public double SUM(final float[][] arg) {
		return SUM(arg, 0, arg.length); }

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	final static public double SUM(final float[][] arg, final int start, final int stop) {
		//return Min(VectorFloat.fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		if (stop <= start) //Optimization: 
			return 0; 
		int i = stop-1;
		double ret = VectorFloat.SUM(arg[i]); 
		for (; --i >= start;) 
			ret += VectorFloat.SUM(arg[i]);
		return ret;
	}

	/**
	 * @return the Minimum Values of each Column in Place
	 */
	final static public float[] MIN(final float[][] arg) {
		return MIN(null, arg, 0, arg.length); }

	/**
	 * @return the Minimum Values of each Column in Place
	 */
	final static public float[] MIN(final float[] ret, final float[][] arg) {
		return MIN(ret, arg, 0, arg.length); }

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	final static public float[] MIN(final float[][] arg, final int start, final int stop) {
		return MIN(null, arg, start, stop); }

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	final static public float[] MIN(float[] ret, final float[][] arg, final int start, final int stop) {
		//return Min(VectorFloat.fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		if (stop <= start) //Optimization: 
			return null; 
		int i = stop-1;
		if (ret == null) 
			ret = VectorFloat.COPY(arg[i]);
		else 
			VectorFloat.COPY(arg[i], ret);
		for (; --i >= start;) 
			VectorFloat.MIN_AT(ret, arg[i]);
		return ret;
	}

	/** @return the Maximum Values of each Column */
	final static public float[] MAX(final float[][] arg) {
		return MAX(null, arg, 0, arg.length); }
	
	/** @return the Maximum Values of each Column */
	final static public float[] MAX(final float[] ret, final float[][] arg) {
		return MAX(ret, arg, 0, arg.length); }
	
	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	final static public float[] MAX(final float[][] arg, final int start, final int stop) {
		return MAX(null, arg, start, stop); }
	
	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	final static public float[] MAX(float[] ret, final float[][] arg, final int start, final int stop) {
		//return Min(VectorFloat.fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		if (stop <= start) //Optimization: 
			return null; 
		int i = stop-1;
		if (ret == null) 
			ret = VectorFloat.COPY(arg[i]);
		else
			VectorFloat.COPY(arg[i], ret);
		for (; --i >= start;) 
			VectorFloat.MAX_AT(ret, arg[i]);
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public float[][] ADD_AT(float[][] ret, double Increment) {
		return ADD_AT(ret, Increment, 0, ret.length);
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[][] ADD_AT(float[][] ret, double Increment, int start, int stop) {
		while (--stop >= start) 
			VectorFloat.ADD_AT(ret[stop], Increment);
		return ret;
	}

	/**
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public float[][] ADD_AT(final float[][] ret, final float[] Decrement) {
		return ADD_AT(ret, Decrement, 0, ret.length);
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[][] ADD_AT(float[][] ret, float[] Decrement, int start, int stop) {
		while (--stop >= start) 
			VectorFloat.ADD_AT(ret[stop], Decrement);
		return ret;
	}

	/**
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public float[][] ADD_AT(final float[][] ret, final double[] Decrement) {
		return ADD_AT(ret, Decrement, 0, ret.length);
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[][] ADD_AT(float[][] ret, double[] Decrement, int start, int stop) {
		while (--stop >= start) {
			VectorFloat.ADD_AT(ret[stop], Decrement);
		}
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Decrement the Decrement to subtract from 
	  * @return the given Array decremented by the given Decrement
	  */
	final static public float[][] SUB_AT(float[][] ret, float[] decrement) {
		return SUB_AT(ret, decrement, 0, ret.length);
	}

	/**
	  * @return the given Array decremented by the given Decrement
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Decrement the Decrement to subtract from 
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[][] SUB_AT(float[][] ret, float[] Decrement, int start, int stop) {
		while (--stop >= start) 
			VectorFloat.SUB_AT(ret[stop], Decrement);
		return ret;
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[][] SUB_AT(float[][] ret, double[] decrement, int start, int stop) {
		while (--stop >= start) 
			VectorFloat.SUB_AT(ret[stop], decrement);
		return ret;
	}
	
	///////////////////////////////////////////////////////////////////////////

	/**Increases the capacity of this MatrixFloat, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final static public synchronized float[][] SET_CAPACITY(final int minCapacity, final float[][] items, final int itemCount) {
		final int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) 
			return items;
		final float[][] newData = new float[minCapacity][];
		if (oldCapacity > 0) 
			System.arraycopy(items, 0, newData, 0, itemCount);
		return newData;
	}
	
	/**Ensures the capacity of this MatrixFloat, 
	 * so that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final static public synchronized float[][] SET_CAPACITY(final int minRows, final int minCols, final float[][] items, int itemCount) {
		float[][] ret = SET_CAPACITY(minRows, items, itemCount); 
		for (int i = minRows; --i >= 0; ) 
			ret[i] = VectorFloat.SET_CAPACITY(minCols, ret[i]); 
		return ret; 
	}
	
	/** Returns a resized (larger OR smaller) Copy of the given Array 
	 * @deprecated replace by SET_CAPACITY
	 */
	public static float[][] RESIZE(final float[][] arr, int newRows, final int newCols) {
		float[][] ret = new float[newRows][];
		if (newRows > arr.length) 
			newRows = arr.length;
		while (--newRows >= 0) 
			ret[newRows] = VectorFloat.SET_SIZE(arr[newRows], newCols);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** @return an Array filled with the Sum of all Values in each Row.	 */
	final static public float[] ROW_SUM(final float[][] arr) {
		return ROW_SUM(arr, 0, arr.length, new float[arr.length]); }

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public float ROW_SUM(final float[][] arr, final int row) {
		return ROW_SUM(arr, row, 0, arr.length); }

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public float ROW_SUM(final float[][] arr, final int row, 
			final int start, final int stop) {
		return (float) VectorFloat.SUM(arr[row], start, stop); }

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public float[] ROW_SUM(final float[][] arr, final float[] ret) {
		return ROW_SUM(arr, 0, arr.length, ret); }

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public float[] ROW_SUM(final float[][] arr, 
			final int start, int stop, final float[] ret) {
		int len = arr[0].length;
		while (--stop >= start) 
			ret[stop] = (float) VectorFloat.SUM(arr[stop], 0, len); 
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public float[] COL_SUM(final float[][] arr) {
		return COL_SUM(arr, 0, arr.length, null); }

	/** @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public float COL_SUM(final float[][] arr, final int col) {
		return COL_SUM(arr, col, 0, arr.length); }

	/** @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public float COL_SUM(final float[][] arr, final int col, 
			final int startRow, final int stopRow) {
		float sum = 0;
		for (int i = stopRow; --i >= startRow; ) {
			sum += arr[i][col]; }
		return sum;
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public float[] COL_SUM(final float[][] arr, final float[] ret) {
		return COL_SUM(arr, 0, arr.length, ret);
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public float[] COL_SUM(final float[][] arr, 
			final int startRow, final int stopRow, float[] ret) {
		if (ret == null) 
			ret  = new float[stopRow]; 
		for(int i = stopRow; --i >= startRow; ) 
			VectorFloat.ADD_AT(ret, arr[i], 0, ret.length); 
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** @return The Maximum Vector of all Rows as Values in the Array. 	 */
	final static public float[] COL_MAX(final float[][] arr) {
		return COL_MAX(arr, 1, arr.length, VectorFloat.COPY(arr[0]));
	}

	/**
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public float[] COL_MAX(final float[][] arr, final float[] ret) {
		if (ret == null)
			return COL_MAX(arr); //more effective!
		return COL_MAX(arr, 0, arr.length, ret);
	}

	/**
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public float[] COL_MAX(final float[][] arr, 
			final int startRow, int stopRow, final float[] ret) {
		while (--stopRow >= startRow) 
			VectorFloat.MAX_AT(ret, arr[stopRow], 0, ret.length);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** @return The Maximum Vector of all Rows as Values in the Array. 	 */
	final static public float[] COL_MIN(final float[][] arr) {
		return COL_MIN(arr, 1, arr.length, VectorFloat.COPY(arr[0]));
	}

	/**
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public float[] COL_MIN(final float[][] arr, final float[] ret) {
		if (ret == null)
			return COL_MIN(arr); //more effective!
		return COL_MIN(arr, 0, arr.length, ret);
	}

	/**
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public float[] COL_MIN(final float[][] arr, final int startRow, int stopRow, final float[] ret) {
		while (--stopRow >= startRow) 
			VectorFloat.MIN_AT(ret, arr[stopRow], 0, ret.length);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** @return a shallow Copy of the given Array */
	public static float[][] SHALLOW_COPY_AT(final float[][] ret, final float[][] arr) {
		int len;
		if (ret.length != (len = arr.length)) 
			throw new IndexOutOfBoundsException("Expected: " + ret.length + " Actual: " + arr.length);
		while (--len >= 0) 
			ret[len] = arr[len];
		return ret;
	}

	/** @return a deep Copy of the given Matrix */
	public static float[][] COPY_AT(final float[][] ret, final float[][] arr) {
		return COPY_AT(ret, arr, 0, arr.length); }

	/** @return a deep Copy of the given Matrix */
	public static float[][] COPY_AT(final float[][] ret, final float[][] arr, final int start, int stop) {
		while (--stop >= start) { //Optimization!
			System.arraycopy(arr[stop], 0, ret[stop], 0, arr[stop].length);
			//VectorDouble.copyAt(ret[stop], arr[stop]); }
		} 
		return ret;
	}

	/** @return the Matrix ret with deep Copie of the given Vector arr in every Row */
	public static float[][] COPY_AT(final float[][] ret, final float[] arr, final int start, int stop) {
		while (--stop >= start) {
			//VectorDouble.copyAt(ret[stop], arr); 
			System.arraycopy(ret[stop], 0, arr, 0, arr.length);
		} //Optimization!
		return ret;
	}

	/** @return the Matrix ret with deep Copie of the given Vector arr in every Row */
	public static float[][] COPY_AT(final float[][] ret, final double[] arr, final int start, int stop) {
		while (--stop >= start) {
			VectorDouble.COPY_AT(ret[stop], arr); } //
		return ret;
	}

	/** @return a deep Copy of the given Matrix */
	final static public float[][] COPY(final float[][] arr) {
		int len;
		float[][] ret = new float[len = arr.length][];
		while (--len >= 0) {
			ret[len] = VectorFloat.COPY(arr[len]); }
		return ret;
	}

	/** @return a deep Copy of the given Matrix */
	final static public float[][] COPY(final double[][] arr) {
		int len;
		float[][] ret = new float[len = arr.length][];
		while (--len >= 0) {
			ret[len] = VectorFloat.COPY(arr[len]); }
		return ret;
	}

	/** @return a deep Copy of the given Matrix */
	public static float[][] COPY(final int[][] arr) {
		int len;
		float[][] ret = new float[len = arr.length][];
		while (--len >= 0) {
			ret[len] = VectorFloat.COPY(arr[len]); }
		return ret;
	}

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public float[][] SET_DIM_AT(final float[][] a, final int dim) {
		if (a.length == dim) 
			return a; 
		float[][] ret = new float[dim][];
		System.arraycopy(a, 0, ret, 0, a.length);
		//Arrays.fill(ret, a.length, dim, 0);
		return a;
	}

	/** @return the given Array multiplied in Place by the given Factor */
	public static float[][] MUL_AT(final float[][] ret, final double Factor) {
		return MUL_AT(ret, Factor, 0, ret.length); }

	/** @return the given Array multiplied in Place by the given Factor */
	public static float[][] MUL_AT(final float[][] ret, final double Factor, 
			final int start, int stop) {
		while (--stop >= start) 
			VectorFloat.MUL_AT(ret[stop], Factor); 
		return ret;
	}

	/**
	 * This allows to multiply only a certain rectangular Range in the Target Matrix.
	 * @return the given Array multiplied in Place by the given Factor */
	final static public float[][] MUL_AT(
		final float[][] ret, 
		final double Factor, 
		final int startRow, 
		int stopRow, 
		final int startCol, 
		final int stopCol) {
		while (--stopRow >= startRow) {
			VectorFloat.MUL_AT(ret[stopRow], Factor, startCol, stopCol);
		}
		return ret;
	}

	/** @return the Transpose of the given Array  */
	public static float[][] TRP(final float[][] a) {
		//return trpAt(copy(a)); //too slow, doesn't work for non-square Matrices!!!
		float[][] ret = new float[a[0].length][a.length]; //make it rectangular
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
	public static float[][] TRP_AT(final float[][] ret) {
		for (int i = ret.length; --i >= 0; ) {
			for (int j = i + 1; --j >= 0; ) {
				final float tmp = ret[i][j];
				ret[i][j] = ret[j][i];
				ret[j][i] = tmp;
			}
		}
		return ret;
	}

	/** Randomizes all the Weights of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Does NOT require a rectangular Array. 	 */
	final static public float[][] RANDOMIZE_AT_1_1(final float[][] arr) {
		for(int j = arr.length; --j >= 0;) 
			VectorFloat.RANDOMIZE_AT_1_1(arr[j]);
		return arr; }

	/** Randomizes all the Weights of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Does NOT require a rectangular Array. 	 */
	final static public float[][] RANDOMIZE_AT(final float[][] arr) {
		for (int j = arr.length; --j >= 0; ) 
			VectorFloat.RANDOMIZE_AT(arr[j]);
		return arr; }

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
	public static float[][] DIST_MATRIX(final float[][] ret, final float[][] Vectors) {
		for (int i = Vectors.length; --i >= 0; ) { //symmetric Matrix
			int j = Vectors.length;
			float[] I = Vectors[i]; //initialize the whole Matrix, O(V^2)
			float[] A = ret[i]; //
			A[i] = 0; //not necessary, because new Array contains 0s already!
			while (--j > i) { //symmetric Matrix //calculate only 50%!
				A[j] = ret[j][i] = (float) Math.sqrt(VectorFloat.DIST_SQR(I, Vectors[j])); //Symmetric!
			}
		}
		return ret;
	}

	/**
	 * The Implication Matrix or FAM (Fuzzy Associative Matrix)  
	 * is the dyadic "Product" of two Fuzzy Vectors, 
	 * also called the "Mamdani Implication" (1977). 
	 * This is used in concatenating Mappings of Fuzzy Sets and in Game Theory.
	 * m[i,j] = a[i] min b[j] 
	 * Each Value of the Matrix is the fuzzy Value of the logical Proposition 
	 * a[i] AND b[i]
	 * 
	 * This Matrix is not necessarily square and has to be defuzzified by some Means, 
	 * to evaluate it for one or more fuzzy for crisp Control Values.  
	 */
	final static public float[][] BinaryImplication(final float[] l, final float[] r) {
		final float[][] ret = new float[l.length][r.length];
		for (int i = l.length; --i >= 0; ) {
			final float li = l[i];
			final float[] reti = ret[i];
			for (int j = r.length; --j >= 0; ) {
				if((reti[j] = r[j]) < li) {
					continue; }
				reti[j] = li;
			}
		}
		return ret;
	}

	/** Cross Product in Place	 */
	final static public float[] MUL_CROSS_AT(float[] ths, float[] arg) {
		return VectorFloat.COPY(MUL_CROSS(ths, arg), ths);
	}

	/** Cross Product in R^3 */
	final static public float[] MUL_CROSS(float[] ths, float[] arg) {
		int end = 0;
		float[] result = new float[3];
		if (ths.length > 3)
			throw new ArrayIndexOutOfBoundsException();
		if (arg.length > 3)
			throw new AbstractMethodError();
		if (ths.length < 2) {
			result[3] = ths[0] * arg[1];
			return result;
		}
		if (arg.length < 2) {
			result[3] = -arg[0] * ths[1];
			return result;
		}
		if ((ths.length < 3) && (arg.length < 3)) {
			result[0] = 0;
			result[1] = 0;
			end = 2;
		}
		int i = 3;
		while (--i >= end) {
			int j = (i == 2) ? 0 : i + 1;
			int k = 3 - i - j;
			result[i] = (ths[j] * arg[k]) - (ths[k] * arg[j]);
		}
		return result;
	}

	/**
	  * The dyadic Cross Product of two Vectors
	  * m[i,j] = a[i]*b[j]
	  */
	final static public float[][] DYAD_PROD(float[] l, float[] r) {
		float[][] ret = new float[l.length][r.length];
		float[] reti;
		float li;
		for(int j, i = l.length; --i >= 0; ) {
			li = l[i];
			reti = ret[i];
			j = r.length;
			while (--j >= 0) {
				reti[j] = li * r[j];
			}
		}
		return ret;
	}

	/** Scalar Product Multiplication: °
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
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
	final static public float[] MAP(final float[][] b, final float[] a) { //previously named mul()
		return MAP(b, a, b[0].length, 0); }
	
	/** multiplies the given Matrices a and b into the optionally given Matrix ret	 */ 
	final static public float[][] MAP(final float[][] a, final float[][] b, float[][] ret) { //previously named mul()
		if ((ret == null) ||
			(ret.length < a.length))
			 ret  = new float[a.length][]; 
		for(int i = a.length; --i >= 0;) 
			ret[i] = MAP(a[i], b, ret[i]); 
		return ret; }
	
	/** Scalar Product Multiplication: °
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * B°A with A being a Column Vector multiplied from the right
	  *
	  * The Distributive Law applies:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
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
	final static public float[] MAP(final float[][] b, final float[] a, final int stop, final int start) { //previously named mul()
		final float[] ret = new float[b.length];
		for(int i = b.length; --i >= 0;) //saves Initialization and one Addition!
			ret[i] = VectorFloat.MAP(b[i], a);
		return ret;
	}

	/** @see #MAP(float[], float[][], int, int, float[]) 	 */
	final static public float[] MAP(final float[] a, final float[][] b, final int stop, final int start) { //previously named mul()
		return MAP(a, b, stop, start, null); }
	
	/** Scalar Product Multiplication: °
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
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
	final static public float[] MAP(final float[] a, final float[][] b, final float[] ret) { //previously named mul()
		return MAP(a, b, b[0].length, 0, ret); }
	
	/** Scalar Product Multiplication: °
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
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
	final static public float[] MAP(final float[] a, final float[][] b, final int stop, final int start, float[] ret) { //previously named mul()
		if (ret == null) 
			ret  = new float[a.length]; 
		int i = b.length;
		if (i > a.length) {
			i = a.length; }
		if (--i < 0) {
			return ret; } //Not necessary initialize to 0...
		VectorFloat.MUL(ret, b[i], a[i], start, stop); //...single out the first Operation:
		while (--i >= 0) { //...saves Initialization and one Addition! esp. with low-dim. Calc.
			VectorFloat.addProdAt(ret, b[i], a[i], start, stop); } //+= Vector * Skalar!
		return ret; }

	/** Scalar Product Multiplication: °
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
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
	final static public float[] MAP(final float[] a, final float[][] b) { //previously named mul()
		//return MAP(a, b, 0, b[0].length);
		final float[] ret = new float[a.length];
		int i = b.length;
		if (i > a.length) {
			i = a.length; }
		if (--i < 0) {
			return ret; } //Not necessary initialize to 0...
		VectorFloat.MUL(ret, b[i], a[i]); //...single out the first Operation:
		while (--i >= 0) { //...saves Initialization and one Addition!
			VectorFloat.addProdAt(ret, b[i], a[i]); } //+= Vector * Skalar!
		return ret;
	}

	/** Scalar Product Multiplication: °
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A consisting of Row Vectors multiplied from the Left
	  *
	  * Distributive Law applies:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
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
	final static public float[][] CAT(float[][] a, float[][] b) { //previously named mul()
		final float[][] ret = new float[a.length][];
		for (int i = a.length; --i >= 0;) {
			ret[i] = MAP(a[i], b);
		}
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
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  */
	final static public float[] MAX_MIN_MAP(float[] arg, float[][] a) { //previously named mul()
		int i = a.length;
		if (i > arg.length) 
			i = arg.length;
		--i; //Don't initialize to 0
		final float[] ret = VectorFloat.MIN_AT(VectorFloat.COPY(a[i]), arg[i]); //Single out the first Operation:
		while (--i >= 0) //saves Initialization and one Addition!
			VectorFloat.MAX_MIN_PROD(ret, a[i], arg[i]); //* Skalar!
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
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
	  *
	  * This Multiplication can also multiply the Transpose Tensors,
	  * by just swapping the Operands!
	  * (and transposing the Result, which is not necessary for Vectors)
	  */
	final static public float[][] MAX_MIN_MAP(final float[][] a, final float[][] arg) { //previously named mul()
		final float[][] ret = new float[a.length][];
		for (int i = a.length; --i >= 0;) 
			ret[i] = MAX_MIN_MAP(a[i], arg);
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Matrix Operations (symmetric/antisymmetric)
	////////////////////////////////////////////////////////////////////////////////

	/**
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = a[j][i].
	  */
	final static public boolean IS_SYMMETRIC(final float[][] arr) {
		return IS_SYMMETRIC(arr, arr.length);
	}

	/**
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = a[j][i].
	  */
	final static public boolean IS_SYMMETRIC(final float[][] arr, final int length) {
		for (int i = length; --i >= 0; ) { //Addressing could be even more effective, if the Row Strategy
			final float[] row = arr[i]; //is changed for a Column Strategy in the Middle
			for (int j = i; --j >= 0; ) {
				if (!ByRefFloat.EQUALS(arr[j][i], row[j])) {
					return false; } 
			}
		}
		return true;
	}

	/**
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = -a[j][i].
	  */
	final static public boolean IS_ANTI_SYMMETRIC(final float[][] arr) {
		return IS_ANTI_SYMMETRIC(arr, arr.length); }

	/**
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = -a[j][i].
	  */
	final static public boolean IS_ANTI_SYMMETRIC(final float[][] arr, final int length) {
		for (int i = length; --i >= 0; ) { //Addressing could be even more effective, if the Row Strategy
			final float[] row = arr[i]; //is changed for a Column Strategy in the Middle
			for (int j = i; --j >= 0; ) {
				if (!ByRefFloat.EQUALS(arr[j][i], -row[j])) {
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
	final static public void FILL_LOWER(final float[][] a, final float value, final int length) {
		for (int j=0; j<length; j++) { //
			VectorFloat.FILL_AT(a[j], value, 0, j ); }
	}
	
	/** copy the lower Triangle of this Matrix to it's upper
	 * @param a the Matrix to copy
	 * @param length the valid Length to use
	 */
	final static public void COPY_LOWER_TO_UPPER(float[][] a) {
		COPY_LOWER_TO_UPPER(a, a.length); }
	
	/**
	  * @param arr The Array to make symmetric
	  * @return the given Array made symmetric by copying the lower Triangle to the upper.
	  */
	final static public float[][] COPY_LOWER_TO_UPPER(final float[][] a, int length) {
		for (int i = length; --i >= 0; ) { //Addressing could be even more effective, if the Row Strategy
			final float[] a_i = a[i]; //is changed for a Column Strategy in the Middle
			for (int j = i; --j >= 0; ) {
				a[j][i] = a_i[j]; }
		}
		return a;
	}
	
	/**
	  * @param arr The Array to be made symmetric
	  * @return the given Array made symmetric.
	  */
	final static public float[][] MAKE_SYMMETRIC(float[][] arr) {
		for (int i = arr.length; --i >= 0; ) {
			final float[] row = arr[i];
			row[i] *= 2; //+=row[i]; 
			for (int j = i; --j >= 0; ) {
				arr[j][i] = (row[j] += arr[j][i]);
			}
		}
		return arr;
	}

	/**
	  * @param arr The Array to be made antisymmetric
	  * @return the given Array made antisymmetric by subtracting the Upper from the Lower Triangle.
	  */
	final static public float[][] MAKE_ANTI_SYMMETRIC(float[][] arr) {
		for (int i = arr.length; --i >= 0; ) {
			final float[] row = arr[i];
			row[i] = 0;
			for (int j = i; --j >= 0; ) {
				arr[j][i] = - (row[j] -= arr[j][i]);
			}
		}
		return arr;
	}

	/**
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[][] NEG_AT(float[][] ret) {
		if (ret.length <= 0) 
			return ret;
		return NEG_AT(ret, 0, ret.length, 0, ret[0].length);
	}

	/**
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start1 Index from  where the outer Array is processed
	  * @param stop1  Index up to where the outer Array is processed (not ret[stop]!)
	  * @param start2 Index from  where the inner Array is processed
	  * @param stop2  Index up to where the inner Array is processed (not ret[stop]!)
	  */
	final static public float[][] NEG_AT(float[][] ret, int start1, int stop1, int start2, int stop2) {
		while (--stop1 >= start1) 
			VectorFloat.NEG_AT(ret[stop1], start2, stop2);
		return ret;
	}

	/**
	  * @return the multiplicative Inverse of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[][] INV_AT(float[][] ret) {
		if (ret.length <= 0) 
			return ret;
		return INV_AT(ret, 0, ret.length, 0, ret[0].length);
	}

	/**
	  * @return the multiplicative Inverse of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start1 Index from  where the outer Array is processed
	  * @param stop1  Index up to where the outer Array is processed (not ret[stop]!)
	  * @param start2 Index from  where the inner Array is processed
	  * @param stop2  Index up to where the inner Array is processed (not ret[stop]!)
	  */
	final static public float[][] INV_AT(float[][] ret, int start1, int stop1, int start2, int stop2) {
		while (--stop1 >= start1) 
			VectorFloat.INV_AT(ret[stop1], start2, stop2);
		return ret;
	}

	/**
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[][] ABS_AT(float[][] ret) {
		if (ret.length <= 0) 
			return ret;
		return ABS_AT(ret, 0, ret.length, 0, ret[0].length);
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
	final static public float[][] ABS_AT(float[][] ret, int start1, int stop1, int start2, int stop2) {
		while (--stop1 >= start1) 
			VectorFloat.ABS_AT(ret[stop1], start2, stop2);
		return ret;
	}

	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the natural Logarithm of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[][] LOG_AT(float[][] ret) {
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
	final static public float[][] LOG_AT(float[][] ret, int start1, int stop1, int start2, int stop2) {
		while (--stop1 >= start1) 
			VectorFloat.LOG_AT(ret[stop1], start2, stop2);
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Matrix Algebra (Linear Function Algebra)
	///////////////////////////////////////////////////////////////////////////////////

	/**Determines the maximum Degree of the given Dimension
	 * As a preparation for Transposition.
	 */
	final static public int MAX_LENGTH(final float[][] a) {
		int len;
		if ((len = a.length) == 0) 
			return 0;
		int tmp, maxGrad = a[--len].length;
		while (--len >= 0) {
			if (maxGrad <(tmp = a[len].length)) 
				maxGrad = tmp;
		}
		return maxGrad;
	}

	/**
	 * Extracts one Element from each Row at the given Cols 
	 */
	final static public float[] EXTRACT(final float[] ret, final float[][] a, final int[] cols) {
		return EXTRACT(ret, a, cols, 0, a.length);
	}

	/**
	 * Extracts one Element from each Row at the given Cols 
	 */
	final static public float[] EXTRACT(final float[] ret, final float[][] a, final int[] cols, final int startRow, int stopRow) {
		while (--stopRow >= startRow) 
			ret[stopRow] = a[stopRow][cols[stopRow]];
		return ret;
	}

	/**
	 * Setting to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is null, the Zero Matrix is returned.
	 */
	final static public float[][] DIAG_AT(final float[][] a, final float[] diag_) {
		for (int i = a.length; --i >= 0; ) {
			//VectorFloat.diagAt(a[i], (diag == null) ?  1.0 : diag[i]);
			final float[] Row = a[i]; //faster to call it directly
			Arrays.fill(Row, 0);
			if (diag_ != null) 
				Row[i] =  diag_[i]; 
		}
		return a;
	}

	/** adds the value to any diagonal Element */
	final static public float[][] ADD_DIAG_AT(final float[][] matrix, final double value, final int start, int stop) {
		while (--stop >= start) 
			matrix[stop][stop] += value;
		return matrix;
	}
	
	/** fills the whole Matrix with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param value the value to fill with 
	 */
	final static public void FILL(final float[][] a, final float value) {
		for (int row = a.length; --row >= 0; ) 
			FILL_ROW(a, row, value, 0, a.length); 
	}
	
	/** fills the given Row with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param row the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_ROW(final float[][] a, final int row, final float value) {
		FILL_ROW(a, row, value, 0, a.length); }
	
	/** fills the given Row with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param row the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_ROW(final float[][] a, final int row, final float value
	, final int start, final int stop) {
		VectorFloat.FILL_AT(a[row], value, start, stop); }
	
	/** fills the given Column with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param col the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_COL(final float[][] a, final int col, float value) {
		FILL_COL(a, col, value, 0, a.length); }
	
	/** fills the given Column with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param col the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_COL(final float[][] a, final int col, final float value
	, final int start, final int stop) {
		for (int i = stop; --i >= start;) {
			a[i][col] = value; }
	}
	
	/**
	 * Setting 'a' to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is 1, the Unity Matrix is returned.
	 */
	final static public float[][] FILL_DIAG_AT(float[][] a, float diag_, final boolean clearNonDiag) {
		for (int i = a.length; --i >= 0; ) {
			//VectorFloat.diagAt(a[i], (diag == null) ?  1.0 : diag[i]);
			final float[] row = a[i]; //faster to call it directly
			if (clearNonDiag) 
				Arrays.fill(row, 0);  
			row[i] = diag_;
		} //setting the Unit Vector.
		return a;
	}

	/**
	 * Setting to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is null, the Unity Matrix is returned.
	 */
	final static public float[][] ONE_AT(final float[][] a) {
		return FILL_DIAG_AT(a, 1, true);
	}

	/**
	 * Setting to a full zero Matrix in Place. 
	 */
	final static public float[][] ZERO_AT(final float[][] a) {
		FILL(a, 0); 
		//diagAt(a, null); //less effective
		return a; 
	}

	/**
	 * @return a Zero Matrix (zero Mapping) for the given Dimension.
	 */
	final static public float[][] ZERO(final int dim) {
		return new float[dim][dim];
	}

	/**
	 * Optimization: this is faster, because the Matrix needn't be cleared. 
	 * @return a Unity Matrix (identical Mapping) for the given Dimension.
	 */
	final static public float[][] ONE(final int dim) { //Assume a square Matrix
		return FILL_DIAG_AT(new float[dim][dim], 1, false);
	}  //could be made quite sparse, but for the sake of it...

	/**
	 * Checks whether these Row- Vectors for the Unity Matrix
	 * Makes only Sense for Matrices
	 */
	final static public boolean IS_ONE(float[][] a) { //Assume a square Matrix
		//float eps = IMeasurAble.FLOAT_ACCURACY; //
		for (int i = a.length; --i >= 0; ) {
			if (!VectorFloat.IS_ONE(a[i], i)) {
				return false;
			}
		}
		return true;
	}

	/** @see Object#equals(java.lang.Object)	 */
	final static public boolean EQUALS(final float[][] a, final int aLength, final float[][] b, final int bLength) {
		if (a == b) {
			return true; }
		if (a == null) {
			return IS_ZERO(b); }
		if (b == null) {
			return IS_ZERO(a); }
		if (aLength > bLength) {
			return EQUALS(a, b, 0, bLength) && IS_ZERO(a, bLength, aLength); 
		} else {
			return EQUALS(a, b, 0, aLength) && IS_ZERO(b, aLength, bLength); 
		} 
	}
	
	/** @see Object#equals(java.lang.Object)	 */
	final static public boolean EQUALS(final float[][] a, final float[][] b) {
		return EQUALS(a, a.length, b, b.length); 
	}
	
	/** @see Object#equals(java.lang.Object)
	 */
	final static public boolean EQUALS(final float[][] a, final float[][] b, final int start, final int stop) {
		for (int i = stop; --i >= start; ) {
			if (! VectorFloat.EQUALS(a[i], b[i])) {
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
	final static public boolean IS_ZERO(final float[][] a) { //Assume a square Matrix
		if (a == null) {
			return true; }
		return IS_ZERO(a, 0, a.length);
	}

	/**
	 * Checks whether this Matrix is the Zero Matrix
	 * @return true when the given Matrix  
	 * all Lines in the given Range
	 * are null 
	 * are of length 0 
	 * contain only 0s
	 */
	final static public boolean IS_ZERO(final float[][] a, final int start, final int stop) { //Assume a square Matrix
		//float eps = IMeasurAble.FLOAT_ACCURACY; //
		for (int i = stop; --i >= start; ) {
			if (!VectorFloat.IS_ZERO(a[i])) {
				return false;
			}
		}
		return true;
	}

	/**Adds Columns to a Tensor to make it square.
	 * This eliminates possible Optimizations due to sparse Matrices,
	 * but is necessary for Operations like LU_DecomposeAt()	 */
	final static public float[][] MAKE_SQUARE_AT(final float[][] a) {
		int i = a.length;
		while (--i >= 0) //Store the Inverse of the Row-Max Norm for Pivoting
			a[i] = VectorFloat.SET_DIM_AT(a[i], a.length);
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
	final static public float[][] ORTHO_AT(final float[][] a, float[] SqrNorm, final boolean normal) {
		float Sqr;
		if (SqrNorm == null) {
			SqrNorm = new float[a.length];
		} //now it is actually used by subtPart!
		for (int i = a.length; --i >= 0; ) {
			final float[] iRow = a[i];
			for (int j = a.length; --j > i; ) { //Subtract all lower Row Vectors
				VectorFloat.SUB_PART_AT(iRow, a[j], SqrNorm[j]);
			} //a[i] -= <a[i],a[j]> a[j] / <a[j],a[j]>
			Sqr = (float) VectorFloat.NORM_SQR(iRow);
			if (normal) {
				VectorFloat.MUL_AT(iRow, 1 / Math.sqrt(Sqr));
				SqrNorm[i] = 1;
			} else {
				SqrNorm[i] = Sqr;
			}
		}
		return a;
	}

	/**Normalizes these Row- Vectors to (euklidean) Length 1
	 * Makes only Sense for Matrices */
	final static public float[][] ORTHO(float[][] a, boolean normal) {
		return ORTHO_AT(COPY(a), null, normal);
	}

	/**
	 * @return Maximum Value of the every Row in the Array.
	 */
	final static public float[] MAX_VAL(float[] ret, float[][] arr) {
		return MAX_VAL(ret, arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public float[] MAX_VAL(float[] ret, float[][] arr, int startRow, int stopRow) {
		while (--stopRow >= startRow) 
			ret[stopRow] = VectorFloat.MAX_VAL(arr[stopRow]);
		return ret;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public float[] MAX_VAL(
		float[] ret,
		float[][] arr,
		int startRow,
		int stopRow,
		int startCol,
		int stopCol) {
		while (--stopRow >= startRow) {
			ret[stopRow] = VectorFloat.MAX_VAL(arr[stopRow], startCol, stopCol);
		}
		return ret;
	}

	/**
	 * Maximum Norm
	 * @return Positions of the Maximum Value in each Row of the Matrix.
	 */
	final static public int[] MAX_POS(int[] ret, float[][] arr) {
		return MAX_POS(ret, arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Positions of the Maximum Value in each Row of the Matrix.
	 */
	final static public int[] MAX_POS(int[] ret, float[][] arr, int startRow, int stopRow) {
		while (--stopRow >= startRow) 
			ret[stopRow] = VectorFloat.MAX_POS(arr[stopRow]);
		return ret;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Positions of the Maximum Values in each Row of the Matrix.
	 */
	final static public int[] MAX_POS(int[] ret, float[][] arr, int startRow, int stopRow, int startCol, int stopCol) {
		while (--stopRow >= startRow) {
			ret[stopRow] = VectorFloat.MAX_POS(arr[stopRow], startCol, stopCol);
		}
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Re-Composition of LU decomposition	in Place.	*/
	final static public float[][] COMPOSE_LU(float[][] a, int[] perm) {
		return COMPOSE_LU_AT(COPY(a), perm);
	}
	
	/** Re-Composition of LU decomposition in Place.
	  * Undoes the Permutation of Rows also.
	  * This Operation can be done in Place,
	  * if you start from Bottom Left, because this Element == a[i,j]
	  * is only used within this same line.
	  */
	final static public float[][] COMPOSE_LU_AT(final float[][] a, final int[] rows) {
		//if (! LU_Decomposed) return this; LU_Decomposed = false;
		for (int i = a.length; --i > 0;) { //first row is not modified, because L[1,1] = 1
			final float[] iRow = a[i];
			for (int j = a.length; --j >= 0; ) { //for every a[i,j]...
				float Element = iRow[j]; // == a[i,j]
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
		UN_PERMUTE_AT(a, rows);
		return a; //
	}

	/** Undo the Row Permutations! 
	 * permuteRowsAt(a, perm); does not work, since rows is not a proper Permutation!
	 * 
	 * @param a the Vector to sort out
	 * @param rows the Log of Permutations to undo
	 */ 
	private static void UN_PERMUTE_AT(final float[][] a, final int[] rows) {
		for (int i=rows.length; --i >= 0;) {
			if (rows[i] != i) {
				final float[] tmp = a[i]; a[i] = a [rows[i]]; a [rows[i]] = tmp;}
		}
	}
	
	/**
	 * Prepares Solution of linear Equations by LU decomposition in Place:
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
	final static public boolean SPLIT_LU_AT(float[][] a, int[] rows) { //N3/3 Algorithm
		return SPLIT_LU_AT(a, rows, a.length); }
	
	/**
	 * Prepares Solution of linear Equations by LU decomposition in Place:
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
	final static public boolean SPLIT_LU_AT(float[][] a, int[] rows, int length) { //N3/3 Algorithm
		//if (LU_Decomposed) return this; LU_Decomposed = true;
		MAKE_SQUARE_AT(a); //to create Space for the higher Elements in each Row.
		//int[] Rows =  new int[a.length];
		boolean Sign = false; //The Sign of the Permutation: false = 0; (-1)^0 = 1
		
		final float[] norms = new float[length]; //Contains the Max-Norm of each row
		for(int i = length;--i >= 0;) { //Store the Inverse of the Row-Max Norm for Pivoting
			norms[i] = 1 / VectorFloat.MAX_VAL(a[i]); }
		for (int j = -1; ++j < length;) {
			for (int i = -1; ++i < j;) { //Process the lower Rows
				final float[] iRow = a[i];
				float sum = iRow[j];
				for (int k = -1; ++k < i;)
					sum -= iRow[k] * a[k][j];
				iRow[j] = sum;
			}
			float max = 0; //
			int  iMax = -1;
			for (int i = j; i < length; i++) { //Process the upper Rows ...
				final float[] a_i = a[i];
				float sum = a_i[j];
				for (int k = -1; ++k < j;) {
					sum -= a_i[k] * a[k][j]; }
				a_i[j] = sum; //search for the relative Pivot, normalized by the Max-Norm.
				final float dum = Math.abs(sum) * norms[i]; 
				if (max < dum) {
					max = dum; iMax = i; }
			}
			if (iMax == -1) { //Handle Singularities! 
				a[j][j]=1;
				continue; }//no Pivot!
			if (iMax != j) { //Swap the rows
				final float[] tmp = a[iMax]; a[iMax] = a[j]; a[j] = tmp;
				Sign = !Sign;
				norms[iMax] = norms[j];
			}
			rows[j] = iMax; //Don't care for Overflows anymore, using Infinity!
			//if (a[j][j] == 0) { a[j][j] = IMeasurAble.FLOAT_ACCURACY;	//not necessary, work with Infinity
			if (j < length - 1) {
				final float dum = 1 / a[j][j];
				for (int i = j; ++i < length;) { //Divide the lower Column by the Diagonal
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
	final static public float[] MAP(final float[][] decompLU, final int[] rows , final float[] a) { //previously named mul()
		float[] ret = VectorFloat.COPY(a);
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
	final static public float[] MAP(final float[] a, final float[][] decompLU, final int[] rows) { //previously named mul()
		float[] ret = VectorFloat.COPY(a);
		MAP_AT(ret, decompLU, rows);
		return ret;
	}
			
	/**
	 * maps Vector a from right using the LU decomposed Matrix  
	 * @param decompLU LU decomposed Matrix
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 */
	final static public void MAP_AT(final float[][] decompLU, final int[] rows , final float[] a) { //previously named mul()
		//multiply with U 
		for (int i = -1; ++i < a.length; ) {
			float[] mi = decompLU[i];
			a[i] *= mi[i];
			for (int j = i; ++j < a.length;) {
				a[i] += mi[j]*a[j];
			}
		}
		//multiply with L (Diagonal = 1)
		for (int i = a.length; --i >= 0;) {
			float[] mi = decompLU[i];
			for (int j = i; --j >= 0;) {
				a[i] += mi[j]*a[j];
			}
		}
		UN_PERMUTE_AT(a, rows);
		//return a; 
	}
	
	/**
	 * Undoes the Row Permutations! permuteRowsAt(a, perm); does not work, since not a proper Permutation! 
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 */
	protected static final void UN_PERMUTE_AT(final float[] a, final int[] rows) { //previously named mul()
		//Undo the Row Permutations! permuteRowsAt(a, perm); does not work, since not a proper Permutation! 
		for (int i=rows.length; --i >= 0;) {
			if (rows[i] != i) {
				final float tmp = a[i]; a[i] = a [rows[i]]; a [rows[i]] = tmp;}
		}
	}
		
	/**
	 * maps Vector a from left using the LU decomposed Matrix  
	 * @param decompLU LU decomposed Matrix
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 */
	final static public void MAP_AT(final float[] a, final float[][] decompLU, final int[] rows) { //previously named mul()
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
	 * @param Rows the 'Permutation' of Rows from the LU_Decomposition
	 * @param b is replaced by the Solution in Place.
	 */
	final static public float[][] SOLVE_LU_AT(final float[][] lu, final int[] rows, final float[][] b) {
		return SOLVE_LU_AT(lu, rows, lu.length, b); }
	
	/**
	 * Solves the linear equation with Matrix B by Backsubstitution after Decomposition
	 * B = A*ret <=> ret = A'*B with
	 * @param a the LU decomposed Matrix of the System.
	 * @param Rows the 'Permutation' of Rows from the LU_Decomposition
	 * @param b is replaced by the Solution in Place.
	 */
	final static public float[][] SOLVE_LU_AT(final float[][] lu, final int[] rows, final int length
	, final float[][] b) {
		int iNonZero = -1;
		for (int i = -1; ++i < length;) { //Process the upper Triangle
			final int rowI = rows[i];
			final float[] bi = b[rowI];
			if (rowI != i) {
				b[rowI] = b[i];
				b[i] = bi;
			} //Redo the Permutation
			final float[] aRow = lu[i];
			if (iNonZero >= 0) { //Optimization: start subtracting only
				for (int j = iNonZero; j < i; j++) { //from the first nonzero Element on!
					VectorFloat.subtProdAt(bi, b[j], aRow[j]);
				}
			} else {
				if (!VectorFloat.IS_ZERO(bi)) {
					iNonZero = i; }
			}
		}
		for (int i = length; --i >= 0;) { //Process the lower Triangle
			final float[] bi = b[i];
			final float[] ai = lu[i];
			for (int j = i; ++j < length;) {
				VectorFloat.subtProdAt(bi, b[j], ai[j]); }
			VectorFloat.MUL_AT(bi, 1/ai[i]);
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
	final static public float[] SOLVE_LU_AT (final float[][] lu, final int[] rows, final float[] b) {
		return SOLVE_LU_AT (lu, rows, lu.length, b); }

	/**
	 * Solves the linear equation with Vector b by Backsubstitution after Decomposition
	 * b = A*ret = L*U*ret <=> ret = A'*b with Column Vector b
	 * @param a the LU decomposed Matrix of the System.
	 * @param Rows the 'Permutation' of Rows from the LU_Decomposition
	 * @param b is replaced by the Solution in Place.
	 */
	final static public float[] SOLVE_LU_AT (final float[][] lu, final int[] rows, final int length
	, final float[] b) {
		int iNonZero = -1;
		for (int i = -1; ++i < length;) { //Process the upper Triangle
			int rowI = rows[i];
			float bi = b[rowI];
			if (rowI != i) {
				b[rowI] = b[i]; //
			} //partly redo the Permutation (see End of Loop!)
			final float[] ai = lu[i];
			if (iNonZero >= 0) { //Optimization: start subtracting only
				for (int j = iNonZero; j <= i - 1; j++) { //from the first nonzero Element on!
					bi -= b[j] * ai[j];
				}
			} else {
				if (bi != 0) {
					iNonZero = i; }
			}
			b[i] = bi; //finish redoing the Permutation
		}
		for (int i = length; --i >= 0; ) { //Process the lower Triangle
			float Sum = b[i];
			final float[] iRow = lu[i];
			for (int j = i; ++j < length;) {
				Sum -= b[j] * iRow[j]; }
			b[i] = Sum / iRow[i];
		}
		return b;
	}
	
	//////////////////////////
	//	Matrix Inversion	//
	//////////////////////////
	
	/** Inversion: 1/a
	 * since a is decomposed, it needs to be copied!	 */
	final static public float[][] REV(final float[][] a) { 
		return REV_AT(COPY(a), null); }
	
	/** Inversion in Place: 1/a
	 * since a is destroyed anyway, it's reordering on Decomposition does not matter.  */
	final static public float[][] REV_AT(final float[][] a, int[] rows) {
		if (rows == null)
			rows  = new int[a.length]; 
		return SHALLOW_COPY_AT(a, REV(a, rows)); }
	
	/** Inversion: 1/a 
	 * a is decomposed in the Process, that's why rows must be given
	 */
	final static public float[][] REV(final float[][] a, final int[] rows) {
		return TRP_AT(REV_TRP(a, rows)); }
	
	/** Matrix Inversion and Transposition: 1/xT	 */
	final static public float[][] REV_TRP(final float[][] a, final int[] rows) {
		return SOLVE_LU_AT(a, rows, ONE(a.length)); }
	
	/**Matrix Division and Transposition in Place: / arg^T
	 * Requires arg to be LU Decomposed (considered as decomposed).	 
	 */
	final static public float[][] DIV_TRP_AT(final float[][] lu, final int[] rows, final float[][] arg) {
		return SOLVE_LU_AT(lu, rows, arg); //possible both to solve the whole System with one Call or with several Calls
	}
	
	/**Matrix Division and Transposition: /= arg^T	 */
	final static public float[][] DIV_TRP(float[][] lu, int[] rows, float[][] arg) {
		return DIV_TRP_AT(COPY(lu), rows, arg); }
	
	/** Matrix Division in Place: /= arg	 */
	final static public float[][] DIV_AT(float[][] a, double arg) {
		return MUL_AT(a, 1 / arg); } //Use same Scalar Multiplication as with Polynoms and Manifolds
	
	/** Matrix Division in Place: /= arg	 */
	final static public float[][] DIV_AT(float[][] lu, int[] rows, float[] arg) {
		return DIV_AT(lu, rows, arg);
	} //(Tensor / Vector) or (Matrix / Vector):  ManiFold- Like Division of the Argument by each Item

	/** Division in Place: /= arg	 */
	final static public float[][] DIV_AT(
		final float[][] lu,
		final int[] rows,
		final float[][] arg) { //(Vector / Vector) or (Tensor / Vector) or (Tensor / Tensor)
		return TRP_AT(DIV_TRP_AT(lu, rows, arg));
	} //The Argument must not be decomposed!!!

	/**Division: /	 */
	final static public float[][] DIV(float[][] lu, int[] rows, float[][] arg) {
		return DIV_AT(COPY(lu), rows, arg);
	}

	/**Division: /	 */
	final static public float[][] DIV(float[][] lu, int[] rows, float[] arg) {
		return DIV_AT(COPY(lu), rows, arg);
	}

	/**Division: /	 */
	final static public float[][] DIV(float[][] a, double arg) {
		return DIV_AT(COPY(a), arg);
	}

	//Calculation of Determinant:
	//Build all Permutations of the Indices is of Order n!
	//Multiply the Coefficients according to the Indices and build the Sum is of order (n+1)!
	//This is numerically not stable, since the Products tend to cancel each other out!

	/**Returns the Determinant of the (square) Matrix:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations.	 */
	final static public double DET(float[][] a, int[] Rows) {
		return DET_AT(COPY(a), Rows);
	}

	/**The Trace of a Matrix is the Sum along it's Diagonal.
	 * It stays constant with orthogonal Transformations.	 */
	final static public double TRACE(final float[][] a) { //Assume that this is a square Matrix.
		if (a.length <= 0) 
			return 0;
		double Trace = a[0][0];
		for (int i = 0; ++i < a.length;)
			if (a[i].length >= i) {
				Trace += a[i][i];
			}
		return Trace;
	}

	/**Returns the Determinant of the (square) Matrix:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations.	 */
	final static public double TRACE_PROD(float[][] a) {
		if (a.length <= 0) 
			return 1;
		double Prod = a[0][0]; //saves 1 Multiplication
		for (int i = a.length; --i > 0;) 
			if (a[i].length >= i) {
				Prod *= a[i][i];
			}
		return Prod;
	}

	/**Returns the Determinant of the (square) Matrix in Place:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations.	 */
	final static public double DET_AT(float[][] a, int[] Rows) { //Assume that this is a square Matrix.
		//The Determinant is the Product of the Diagonal Elements of the Decomposed Matrix
		boolean Sign = SPLIT_LU_AT(a, Rows); //Using Decomposition is very stable and fast! N3/3 instead of n!
		double Prod = TRACE_PROD(a);
		if (Sign)  //preserve Sign from the Decomposition
			return -Prod;
		else
			return  Prod;
	}

	/** true, when the Matrix is orthogonal, i.e. M*Mt = Mt*M = diag(a, b, c, ...).
	  * If a Matrix contains complex coefficients, it should be checked to be unitarian.
	  */
	final static public boolean IS_ORTHOGONAL(final float[][] a) { //The Optimization here is that you have to 
		for(int i = a.length; --i >= 0;) {
			final float[] ai = a[i];
			final float ai0 = ai[0];
			for (int j = i; --j >= 0; ) { //test only one Triangle
				final float[] aj = a[j];
				if (! ByRefFloat.IS_ZERO(VectorFloat.MAP(ai, aj), ai0+aj[0])) { //because the Product is symmetric.
					//Use an Epsilon here that corresponds to any Matrix Norm
					return false;
				}
			}
		}
		return true;
	}

	/**true, when the Matrix is unitarian resp. orthonormal, i.e. M*Mt = Mt*M = 1.
	 * unitarian is the complex equivalent to orthonormal 	 */
	final static public boolean IS_UNITARIAN(float[][] a) { //The Optimization here is that you have to test only one Triangle
		//because the Product is symmetric.
		if (!IS_ORTHOGONAL(a))
			return false;
		for (int i = a.length; --i >= 0; ) {
			if (!ByRefFloat.EQUALS(1, VectorFloat.MAP(a[i], a[i]))) {
				return false;
			}
		}
		return true;
	}

	//	final static public boolean  orthoNorm();  == unitaer fuer reelle Matrizen;

	/**true, when the Matrix is hermitean resp. symmetric, i.e. M = Mt.	 */
	final static public boolean IS_HERMITEAN(float[][] a) { //The Optimization here is that you have to test only one Triangle
		for (int i = a.length; --i >= 0;) {
			for (int j = i; --j >= 0; ) {
				if (!ByRefFloat.EQUALS(a[i][j], a[j][i])) { //Could also test for the Difference to be Zero
					return false;
				}
			}
		}
		return true;
	}

	//	final static public boolean  symmetr  ();  == hermite fuer reelle Matrizen;

	/**true, when the Matrix is anti-hermitean resp. anti-symmetric, i.e. M = -Mt.	 */
	final static public boolean IS_ANTI_HERMITEAN(float[][] a) { //The Optimization here is that you have to test only one Triangle
		for (int i = a.length; --i >= 0;) {
			for (int j = i; --j >= 0; ) {
				if (!ByRefFloat.EQUALS(a[i][j], -a[j][i])) { //Could also test for the Sum to be Zero
					return false;
				}
			}
		}
		return true;
	}

	//	final static public boolean  antiSym  (); ð antiHerm fuer reelle Matrizen;

	/**true, when the Matrix is normal, i.e. M*M^T = M^T*M.
	 * i.e. M*M^T is symmetric
	 * A normal Matrix has a complete Set of orthonormal Eigenvectors. 
	 * Non-normal Matrices may have (right) Eigenvectors, 
	 * but these are not orthogonal to each other, 
	 * only to their corresponding left Eigenvectors. 
	 */
	final static public boolean IS_NORMAL(final float[][] a) { //The Optimization here is that you have to test only one Triangle
		//because the Product is symmetric.
		final float[][] Trp = TRP(a);
		for (int i = a.length; --i >= 0;) {
			for (int j = i+1; --j >= 0; ) {
				if (!ByRefFloat.EQUALS(
					VectorFloat.MAP(a[i], a[j]), 
					VectorFloat.MAP(Trp[i], Trp[j]))) {
					return false;
				}
			}
		}
		return true;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**Swaps the Columns of this Tensor in Place	 */
	final static public float[][] SWAP_COLS(final float[][] a, final int Dim1, final int Dim2) {
		return SWAP_COLS_AT(COPY(a), Dim1, Dim2);
	}

	/** Swaps the Columns and Rows of this Tensor in Place	 */
	final static public float[][] SWAP_COLS_ROWS_AT(final float[][] a, final int dim1, final int dim2) {
		final float[][] ret = SWAP_ROWS_AT(a, dim1, dim2);
		return SWAP_COLS_AT(ret, dim1, dim2);
	}

	/**Swaps the Columns of this Tensor in Place	 */
	final static public float[][] SWAP_COLS_AT(final float[][] a, final int dim1, final int dim2) {
		if (dim1 == dim2) 
			return a; 
		for (int i = a.length; --i >= 0;) {
			VectorFloat.SWAP_AT(a[i], dim1, dim2); }
		return a;
	}

	/**Swaps the Rows of this Tensor in Place	 */
	final static public float[][] SWAP_ROWS(final float[][] a, final int dim1, final int dim2) {
		return SWAP_ROWS_AT(COPY(a), dim1, dim2); }

	/**Swaps the Rows of this Tensor in Place	 */
	final static public float[][] SWAP_ROWS_AT(final float[][] a, final int dim1, final int dim2) {
		final float[] swap = a[dim1]; a[dim1] = a[dim2]; a[dim2] = swap;
		return a;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods
	///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(final float[][] vals, final PrintStream stream, final char separator) {
		STREAM(vals, stream, 0, vals.length, separator);
	}
	
	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(final float[][] vals, final PrintStream stream) {
		STREAM(vals, stream, 0, vals.length, VectorFloat.DEFAULT_SEPARATOR);
	}
	
	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(final float[][] vals) {
		STREAM(vals, System.out, 0, vals.length, VectorFloat.DEFAULT_SEPARATOR);
	}
		
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(
		final float[][] vals,
		final PrintStream stream,
		int startRow,
		int stopRow, final char separator) { //, int startCol, int stopCol) {
		//if (startRow >= stopRow) {
		//	return; }
		//VectorFloat.stream(vals[startRow], stream);
		for (int i = startRow-1; ++i < stopRow;) {
			VectorFloat.STREAM(vals[i], stream, separator);
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
	final static public void STREAM(final float[][] d, final OutputStream ps, final NumberFormatter formatter, final String colSep) throws IOException {
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
	final static public void STREAM(final float[][] d, final Writer pw, final NumberFormatter formatter, final String colSep) throws IOException {
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
	final static public void STREAM(final float[][] d, final Writer pw, final NumberFormatter formatter, final String colSep, final String rowSep) throws IOException {
		for (int i = -1; ++i < d.length;) {
			VectorFloat.STREAM(d[i], pw, formatter, colSep);
			pw.write(rowSep); 
		}
		//return pw; 
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Rotation Matrices and Operations
	///////////////////////////////////////////////////////////////////////////////////

	/**
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
	final static public float[][] ALIGN_MATRIX_AT(final float[] a, final int Dim) {
		if (Dim == 0) {
			return ALIGN_MATRIX_AT(a);
		}
		VectorFloat.SWAP_AT(a, 0, Dim); //Swap the coordinates
		float[][] Matrix = ALIGN_MATRIX_AT(a);
		VectorFloat.SWAP_AT(a, 0, Dim); //undo the Swap of the coordinates...useless anyway
		SWAP_COLS_AT(Matrix, 0, Dim); //Swap the Coordinates back:
		SWAP_ROWS_AT(Matrix, 0, Dim);
		return Matrix;
	}

	/**
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
	final static public float[][] ALIGN_MATRIX_AT(final float[] a) { //Polar[0] = r
		VectorFloat.RECT_2_POLAR_AT(a, a.length); //Convert all Coordinates to polar ones.
		//Create a Matrix with the Angles undone sequentially by negative Rotations:
		//first (x,y), then (x,z) and so on..., Angles start at a[1]!
		float[][] Matrix = ROT_MATRIX(a[1], 0, 1, a.length); //saves one Matrix Multiplication
		int i = 1;
		while (++i < a.length) { //r, a1, a2, a3, ...
			ROTATE_AT(Matrix, a[i], 0, i); //Rotations along different Axes are NOT commutable! (Lie Algebra)
		}
		return Matrix;
	}

	/**Creates a float[][] representing a plane Rotation in the 2 Dimensions given.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 *
	 * Only possible for Matrices.	 */
	protected static final float[][] FILL_ROT_MATRIX_AT(float[][] a, double phi, int Dim1, int Dim2) {
		FILL_DIAG_AT(a, 1, false);
		final float[] c_s = new float[2];
		ByRefFloat.CosSin(phi, c_s);
		a[Dim1][Dim1] = (a[Dim2][Dim2] = c_s[0]);
		a[Dim2][Dim1] = - (a[Dim1][Dim2] = c_s[1]);
		return a;
	}

	/**Creates a float[][] representing a plane Rotation in 2 Dimensions.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 *
	 * Only possible for Matrices.
	 */
	final static public float[][] ROT_MATRIX(double phi, int Dim1, int Dim2, int dimMax) {
		return FILL_ROT_MATRIX_AT(new float[dimMax][dimMax], phi, Dim1, Dim2);
	}

	/**Rotates the float[][] by a plane Rotation in 2 Dimensions in Place.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 *
	 * Only possible for Matrices.
	 */
	final static public float[] ROTATED(float[] a, double phi, int Dim1, int Dim2) {
		return ROTATE_AT(VectorFloat.COPY(a), phi, Dim1, Dim2);
	}

	/**Rotates the float[][] by a plane Rotation in 2 Dimensions in Place.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product). 	 */
	final static public float[] ROTATE_AT(float[] a, double phi, int Dim1, int Dim2) {
		final float[] c_s = new float[2];
		ByRefFloat.CosSin(phi, c_s);
		final float a_Dim1_ = a[Dim1];
		a[Dim1] = a[Dim1] * c_s[0] + a[Dim2] * c_s[1];
		a[Dim2] = a[Dim2] * c_s[0] - a_Dim1_ * c_s[1];
		return a;
	}

	/**Rotates the float[][] by a plane Rotation in 2 Dimensions in Place.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product).
	 */
	final static public float[][] ROTATED(float[][] a, double phi, int Dim1, int Dim2) {
		return ROTATE_AT(COPY(a), phi, Dim1, Dim2);
	}

	/**Rotates the float[][] by a plane Rotation in 2 Dimensions in Place.
	 * Parameters are the two Dimensions and the angle phi.
	 * This is a unitarian (orthogonal) Mapping
	 * that keeps Lengths (Norm) and angles (Scalar Product). 	 */
	final static public float[][] ROTATE_AT(float[][] a, double phi, int Dim1, int Dim2) {
		float[] c_s = new float[2];
		ByRefFloat.CosSin(phi, c_s);
		float[] Row = VectorFloat.COPY(a[Dim1]);
		VectorFloat.BiLinAt(a[Dim1], c_s[0], a[Dim2], c_s[1]);
		VectorFloat.BiLinAt(a[Dim2], c_s[0], Row, - c_s[1]);
		return a;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Backing Value Array for the float[][]	 */
	protected float[][] items;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return the internal List to modify it externally
	 */
	public float[][] getList() { return items; }

	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized float[] getVectorAt(final int index) {
		if (indexInRange(index)) 
			return items[index];
		return null;
	}

	/** @return the item at the given Position as an Object */
	public Object getAt(final int i) { return getVectorAt(i); }
	
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
	public float[] setAt(final int index, final float[] value) {
		float[] ret = null; 
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
		return setAt(index, (float[]) value); 
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
	public void setAt(final float[][] value) {
		if (!indexInRange(value.length-1)) 
			setSize(value.length);
		System.arraycopy(value, 0, items, 0, value.length); 
		if (itemCount < value.length) 
			itemCount = value.length;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/**Constructs an empty MatrixFloat with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the MatrixFloat.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the MatrixFloat overflows.	 */
	public MatrixFloat(int initialCapacity, int capacityIncrement_) {
		items = new float[initialCapacity][];
		capacityIncrement = capacityIncrement_;
		//		mEnum = new ArrayEnum(Items, ItemCount);
		//		mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty MatrixFloat with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the MatrixFloat.	 */
	public MatrixFloat(final int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty MatrixFloat.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public MatrixFloat() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/** Constructs an empty MatrixDouble.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public MatrixFloat(final float[][] a, boolean copy) {
		if (copy) {
			items = new float[a.length][];
			copyAt(a);
		} else {
			items = a;
		}
	}
	
	/** Constructs an MatrixFloat by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public MatrixFloat(final Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an MatrixFloat from the given Object.	  */
	public MatrixFloat(final Object arg, final int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an MatrixFloat from the given Object.	  */
	public MatrixFloat(final float[][] arg, final int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an MatrixFloat from the given Object
	  * and copies the Elements into this MatrixFloat.	  */
	public MatrixFloat(final float[][] arg) {
		this(arg.length, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public MatrixFloat addItem(final float[] item) {
		setAt(itemCount, item);
		return this;
	}

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public MatrixFloat addItem(final VectorFloat item, boolean original) {
		setAt(itemCount, item.getItems(original));
		return this;
	}

	/**Copies the components of this MatrixFloat into the specified array.
	 * The array must be big enough to hold all the objects in this  MatrixFloat.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	final public synchronized void copyInto(final int[] anArray) {
		System.arraycopy(items, 0, anArray, 0, itemCount);
		/*		int i = ItemCount;
				Object elementDataLocal[] = this.Items;
				while (i-- > 0)
					anArray[i] = elementDataLocal[i];
		*/
	}

	/**Copies the components of this MatrixFloat into the specified array.
	 * The array must be big enough to hold all the objects in this  MatrixFloat.
	 *
	 * @param   anArray   the array into which the components get copied.	 */
	final public synchronized int[] toArray() {
		int[] Return = new int[itemCount];
		System.arraycopy(items, 0, Return, 0, itemCount);
		return Return;
	}

	/**Trims the capacity of this MatrixFloat to be the MatrixFloat's current
	 * size. An application can use this operation to minimize the
	 * storage of a MatrixFloat.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			float[][] oldData = items;
			items = new float[itemCount][];
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
	}

	/**Returns the current capacity of this MatrixFloat.
	 *
	 * @return  the current capacity of this MatrixFloat.	 */
	final public int getCapacity() {
		return items.length;
	}

	/**Ensures the capacity of this MatrixFloat, 
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

	/**Ensures the capacity of this MatrixFloat, 
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
		if (arg instanceof MatrixFloat) {
			return equals((MatrixFloat) arg); 
		}
		return false; 
	}
	
	/** @see Object#equals(java.lang.Object)	 */
	public boolean equals(final float[][] arg) {
		return EQUALS(items, itemCount, arg, arg.length);
	}
	
	/** @see Object#equals(java.lang.Object)	 */
	public boolean equals(final MatrixFloat arg) {
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
	public MatrixFloat copyAt(final float[][] arg_) {
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
	public MatrixFloat copyAt(final double[][] arg_) {
		itemCount = arg_.length; 
		for (int i = itemCount; --i >= 0;) {
			items[i] = VectorFloat.COPY(arg_[i]);
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
		if (arg instanceof MatrixFloat) {
			MatrixFloat arg_ = (MatrixFloat) arg;
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
		if (arg instanceof MatrixFloat) {
			MatrixFloat arg_ = (MatrixFloat) arg;
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
		return new MatrixFloat(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Arithmetic Methods for Arrays
	////////////////////////////////////////////////////////////////////////////////

	/** Normalizes this Vector by bringing it into the canonical Form
	 * so that getAt(getInt()) != 0 
	 */
	public MatrixFloat normalizeAt() {
		while (items[--itemCount] == null);
		++itemCount;
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixFloat addAt(final VectorFloat vector) {
		return addAt(vector.getItems(true), 0, vector.getInt());
	}

	/** subtracts the given Portion of the values from this Vector */
	public MatrixFloat subAt(final VectorFloat vector) {
		return subAt(vector.getItems(true), 0, vector.getInt());
	}

	/** @return the Minimum and Maximum Values of each Column... 
	 * too complex to optimize for now... 
	 * 
	 * Use Min and Max separately, which is clearer too!
	 */
	//	final static public float[][] MinMax(float[][] arr) { }

	/**
	 * @return the Minimum Values of each Column 
	 */
	public float[] Min(final float[] ret) {
		return MIN(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	public float[] Min() {
		return MIN(items, 0, itemCount);
	}

	/** @return the Maximum Values of each Column */
	public float[] Max(final float[] ret) {
		return MAX(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	public float[] Max() {
		return MAX(items, 0, itemCount);
	}

	/** subtracts the given Portion of the values from this Vector */
	public MatrixFloat negAt() {
		NEG_AT(items);
		return this; 
	}
	
	/** subtracts the given Portion of the values from this Vector */
	public MatrixFloat trpAt() {
		composeLuAt();
		TRP_AT(items);
		return this; 
	}
	
	/** subtracts the given Portion of the values from this Vector */
	public MatrixFloat subAt(final float[] values, final int start, final int stop) {
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
	public MatrixFloat subAt(final double[] values, final int start, final int stop) {
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
	public MatrixFloat addAt(final double value) {
		MatrixFloat.ADD_AT(items, value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixFloat subAt(final double value) {
		MatrixFloat.ADD_AT(items, -value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixFloat addAt(final float[] values, int start, int stop) {
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
	public MatrixFloat addAt(final double[] values, int start, int stop) {
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
	
	/**
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = a[j][i].
	  */
	public boolean isSymmetric() {
		return IS_SYMMETRIC(items, itemCount); 
	}
	
	/**
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = -a[j][i].
	  */
	public boolean isAntiSymmetric() {
		return IS_ANTI_SYMMETRIC(items, itemCount); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Mapping and Concatenation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Scalar Product Multiplication from the right: °
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A being a Row Vector multiplied from the Left
	  *
	  * The Distributive Law applies:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
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
	public float[] map(final float[] a) { //previously named mul()
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
	public void mapAt(final float[] a) { //previously named mul()
		if (isDecomposedLU()) {
			MAP_AT(items, rows, a);
			//MAP_AT(a, items, rows); //maps from the left
		} else {
			VectorFloat.COPY(MAP(a, items), a);
		}
	}
	
	/** Scalar Product Multiplication: °
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A consisting of Row Vectors multiplied from the Left
	  *
	  * Distributive Law applies:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
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
	public float[][] cat(final float[][] a) { //previously named mul()
		return CAT(a, items);
	}
	
	/** Scalar Product Multiplication: °
	  * This is in fact a non-commutative linear Mapping (thus the Naming):
	  * A*B with A consisting of Row Vectors multiplied from the Left
	  *
	  * Distributive Law applies:
	  * M°(a+b) == M°a + M°b
	  * (x+y)°M == x°M + y°M
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
	public MatrixFloat cat(final MatrixFloat a) { //previously named mul()
		return new MatrixFloat(CAT(a.items, items));
	}
		
	////////////////////////////////////////////////////////////////////////////////
	/// #region : LU (De-)Composition
	////////////////////////////////////////////////////////////////////////////////
		

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
		return SPLIT_LU_AT(items, rows);
	}
			
	/**
	 * Solves the linear equation with Matrix B by Backsubstitution after Decomposition
	 * B = A*ret <=> ret = A'*B with
	 * @param b is replaced by the Solution in Place.
	 */
	public void solveAt(final float[][] b) {
		decomposeLuAt(); //split up in lower and upper Diagonal
		SOLVE_LU_AT(items, rows, b);
	}
		
	/**
	 * Solves the linear equation by Backsubstitution after Decomposition
	 * b = A*ret = L*U*ret <=> ret = A'*b with Column Vector b
	 * @param b is replaced by the Solution in Place.
	 */
	public void solveAt(final float[] b) {
		decomposeLuAt(); //split up in lower and upper Diagonal
		SOLVE_LU_AT(items, rows, b);
	}
		
	/** improves the given Solution s for a in Place, so that M*s=a
	 * 
	 * @param a Vector to solve for
	 * @param solution preliminary Solution
	 */
	public void improve(final float[] a, final float[] solution) {
		//r = M*s-a
		float[] residuum = VectorFloat.SUB_AT(map(solution), a); 
		solveAt(residuum);
		VectorFloat.SUB_AT(solution, residuum); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Methods for 3D Transformation using 4D Coordinates
	////////////////////////////////////////////////////////////////////////////////
	
	/** Rotate avector using the inverse (Transpose) of the math.matrix 
	 */
	public float[] rotate(final float[] fpVec) {
		final float[] tmp = new float[3];
		tmp[0] = fpVec[0]*items[0][0] + fpVec[1]*items[0][1] + fpVec[2]*items[0][2];
		tmp[1] = fpVec[0]*items[1][0] + fpVec[1]*items[1][1] + fpVec[2]*items[1][2];
		tmp[2] = fpVec[0]*items[2][0] + fpVec[1]*items[2][1] + fpVec[2]*items[2][2];
		return tmp;
	}
	
	/** Translate a vector based on the inverse math.matrix
	 * (in homogenous Coordinates)
	 * @param vector the Vector to translate
	 */
	public void translate(final float[] vector) {
		VectorFloat.SUB_AT(vector, items[vector.length]); 
	}
	
	/** @return this Matrix filled with translation Values for the given vector 
	 * (in homogenous Coordinates)
	 * @param the vector to translate with
	 */
	public MatrixFloat setTranslationAt(final float[] vector) {
		setCapacity(vector.length+1, vector.length+1);
		if (itemCount <= vector.length) {
			itemCount  = vector.length+1; 
		}
		System.arraycopy(vector, 0, items[vector.length], 0, vector.length); 
		items[vector.length][vector.length] = 1;
		return this; 
	}
	
	/** @return this Matrix filled with Values for the given Euler Angles 
	 * (in homogenous Coordinates)
	 * @param eulerAngles the angles to rotate with
	 */
	public MatrixFloat setRotationAt(final float[] eulerAngles) {
		setCapacity(eulerAngles.length, eulerAngles.length); 
		if (itemCount < eulerAngles.length) {
			itemCount = eulerAngles.length; 
		}
		final float[] cs = new float[eulerAngles.length << 1];
		for (int i = eulerAngles.length; --i >= 0; ) {
			ByRefFloat.CosSin(eulerAngles[i], cs, i+i, 1+i+i);
		}

		items[0][0] = cs[2] * cs[4];
		items[0][1] = cs[2] * cs[5];
		items[0][2] = -cs[3];

		final float sxsy = cs[1] * cs[3];
		items[1][0] = sxsy*cs[4] - cs[0]*cs[5];
		items[1][1] = sxsy*cs[5] + cs[0]*cs[4];
		items[1][2] = cs[1]*cs[2];

		final float cxsy = cs[0] * cs[3];
		items[2][0] = cxsy*cs[4] + cs[1]*cs[5];
		items[2][1] = cxsy*cs[5] - cs[1]*cs[4];
		items[2][2] = cs[0]*cs[2];

		return this; 
	}

	////////////////////////////////////////////////////////////////////////////////
	/// reading the Data from a ResultSet
	////////////////////////////////////////////////////////////////////////////////
	
	/**
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
	final public MatrixFloat read(final ResultSet rs)
		throws SQLException {
		return read(rs, 0); 
	}

	/**
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
	final public MatrixFloat read(final ResultSet rs, final int columnOffset)
		throws SQLException {
		return read(rs, Integer.MAX_VALUE, columnOffset, -1); 
	}

	/**
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
	final public MatrixFloat read(final ResultSet rs, int maxNumPlanes, 
			final int columnOffset, int lastCol) throws SQLException {
		final VectorFloat vector = new VectorFloat(10); //TODO: hardcoded Capacity
		while (--maxNumPlanes >= 0) { //
			final VectorFloat row; 
			if (null == (row = vector.read(rs, columnOffset, lastCol))) 
				break; 
			this.addItem(row, false); 
		}
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests LU Decomposition 	 */
	protected static void testLUDecomp() { //throws java.io.IOException {
		final float[][] test = { { 
			1, 2, 3, 4, 5 }, {
			2, 3, 4, 5, 1 }, {
			3, 4, 5, 1, 2 }, {
			4, 5, 1, 2, 3 }, {
			5, 1, 2, 3, 4 }
		};
		final int[] Rows = new int[test.length];
		testLUDecomp(test, Rows);
		float[][] a = new float[test.length][test.length];
		float[] diag = new float[test.length];
		for (int i = test.length; --i >= 0;) {
			a[i][i] = diag[i] = i + 1;
		}
		testLUDecomp(a, Rows);
		for (int i = test.length; --i >= 0;) {
			for (int j = i; --j >= 0;) {
				a[i][j] = 2;
			}
		}
		testLUDecomp(a, Rows);
		RANDOMIZE_AT_1_1(a);
		//		diagAt(a, diag);
		testLUDecomp(a, Rows);
	}
	
	/** Tests LU Decomposition 	 */
	private static void testLUDecomp(final float[][] a, final int[] rows) { //throws java.io.IOException {
		final float[][] c = COPY(a);
		L.n("\nOriginal:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, c, "\n ");
		SPLIT_LU_AT(a, rows);
		L.n("\nAfter Decomposition:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, a, "\n ");
		COMPOSE_LU_AT(a, rows);
		L.n("\nAfter Recomposition:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, a, "\n ");
		streamIO.Assert.EQUALS(c, a); //catches any Exception!
		streamIO.Assert.GET_AVAILABLE();
	}
	
	/** Tests LU Decomposition and BackSubstition	 */
	protected static void testLUBackSub() { //throws java.io.IOException {
		int i, j, N = 8;
		int[] Rows = new int[N];
		float[][] a = new float[N][N];
		float[] diag = new float[N];
		float[] v = new float[N];
		i = N;
		while (--i >= 0) {
			a[i][i] = diag[i] = v[i] = i + 1;
		}
		testLUBackSub(a, Rows, v);
		i = N;
		while (--i >= 0) {
			j = i;
			while (--j >= 0) {
				a[i][j] = 2;
			}
		}
		testLUBackSub(a, Rows, v);
		RANDOMIZE_AT_1_1(a);
		VectorFloat.RANDOMIZE_AT_1_1(v);
		testLUBackSub(a, Rows, v);
	}
	
	/** Tests LU Decomposition and BackSubstition	 */
	private static void testLUBackSub(final float[][] a, final int[] Rows, final float[] v) { //throws java.io.IOException {
		float[][] c = COPY(a);
		float[][] t = TRP(a); //
		float[] u = VectorFloat.COPY(v);
		L.n("\nOriginal Vector:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, v, " ");
		L.n("\nOriginal Matrix:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, c, "\n ");
		SPLIT_LU_AT(a, Rows); //split up in lower and upper Diagonal
		float[] w = SOLVE_LU_AT(a, Rows, v); //solve the System A*w=v for the right Side w
		L.n("\nSolution:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, w, " ");
		float[] x = MAP(w, t); //Test the Solution by performing w*A^t==v
		L.n("\nMapped Solution:\n");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, x, " ");
		streamIO.Assert.EQUALS(u, x); //catches any Exception!
		streamIO.Assert.GET_AVAILABLE();
	}
	
	/** Tests the Creation of a Matrix to align a Vector along the second Dimension	 */
	protected static void testAlignment() {
		float[] v = new float[5];
		VectorFloat.RANDOMIZE_AT_1_1(v);
		//		v[1] = v[0] = 1; //e-10f;
		float[] c = VectorFloat.COPY(v);
		float[][] mat = TRP_AT(ALIGN_MATRIX_AT(c, 2));
		float[] ret = MAP(v, mat);
		Log.L(v).n();
		ret = MAP(v, mat); //Unitarian Matrix, transposition inverts the Matrix.
		Log.L(ret).n();
		Log.L(mat).n();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Test Data for Pivoting	 */
	static final float[][] testData = {
		{0.60750854f,	0.17916538f,	0.77458847f,	0.90553355f,	0.6896367f, 	0.68244785f
		},{0.7714014f,	0.55528474f,	0.34901342f,	0.12162702f,	0.44997716f,	0.7381433f
		},{0.2669455f,	0.2133805f,		0.46859112f,	0.73081034f,	0.043221153f,	0.8344698f
		},{0.50790596f,	0.21803415f,	0.24193569f,	0.002972339f,	0.49439335f,	0.43526006f
		},{0.5264185f,	0.03253005f,	0.05609051f,	0.28097278f,	0.026418949f,	0.917079f
		},{0.002476876f,0.488142782f,	0.408345215f,	0.380417043f,	0.116997975f,	0.195798588f
		}
	};
			
	/**
	 * Create a random Matrix
	 * and try to sort the Dimensions via Sorting the Dimensions 
	 * The Maximum Values are aligned on the top left Corner
	 */
	protected static final void testPivoting() {
		testPivoting(true);
		testPivoting(false);
	}
		
	/**
	 * Create a random Matrix
	 * and try to sort the Dimensions via Sorting the Dimensions 
	 * The Maximum Values are aligned on the top left Corner
	 */
	protected static final void testPivoting(final boolean sumNorm) {
		float[][] copy, test = COPY(MatrixFloat.testData); // randomizeAt(new double[6][6]);
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
	 * tests Weighed Sorting by permuting Rows only 
	 */
	protected static final void testWeighedSorting() {
	}
	
	/** 
	 * tests statistical Diagonalization by synchronously permuting Rows and Columns 
	 */
	protected static final void testDiagonalization() {
		L.n("testing Graph Generation"); 
		final int N = 80; 
		final MatrixGraph graph = new MatrixGraph(N, true); 
		//FILL_RANDOM_GRAPH(graph, 2, false); 
		//graph.clear();
		AGraph.FILL_SCALE_FREE_GRAPH(graph, N, 5, 2, true);
		//final int[] outDegree = graph.getOutDegree(); L.n(outDegree);
		final float[] fanOut = graph.getFanOut(); L.n(fanOut);
		final float[][] testData = graph.getList(); 
		L.n("Test that identical Trafos don't change the Energy:"); 
		//for(int i = testData.length; --i >= 0;)
		//	Assert.EQUALS(0, COST_OF_SWAP(testData, i, i));
		L.n("Before sorting:\n"); STREAM(testData, L); 
		L.n("Overall Energy Reduction: ").l(CLUSTER(testData));
		L.n("After  sorting:\n"); STREAM(testData, L); 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() { //
		L.n("Testing " + MatrixFloat.class.getName());
		testWeighedSorting(); 
		testDiagonalization(); 
		testPivoting();
		testAlignment();
		testLUBackSub();
		testLUDecomp();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) { //throws java.io.IOException {
		testIt();
	}
	
}
