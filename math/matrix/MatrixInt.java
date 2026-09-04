/*
 * File Name: MatrixInt.java
 * Created on: 07.01.2004
 *
 */
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
import math.vector.HunterInt;
import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.copy.ICopyAble;
import function.byref.ByRefFloat;

/**
 * Title: MatrixInt<p>
 * Description:
 * Collects Methods for int[][] Arrays 
 * Usually this is used for Polygons 
 *
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * @see graphic.mvc.plane2D.MatrixShort which does the same for short[][], 
 * also for Polygons 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class MatrixInt 
extends AMatrix {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods for Calculations on Polygons
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Negates the Directions of the Planes
	 * Creates a Copy, because the original Points may be copied from somewhere!
	 */
	final static public int[][] REVERT(final int[][] planes, final int offset) {
		final int[][] newPlanes = new int[planes.length][];
		for(int i = planes.length; --i >= 0; ) 
			newPlanes[i] = VectorInt.REVERSE(planes[i], offset); 
		return newPlanes; }

	/**
	 * Used for Bodies, when the Index of the Points is too high/low.
	 * Creates a Copy, because the original Points may be copied from somewhere!
	 */
	final static public int[][] ADD(final int[][] planes, final int offset) {
		int Length = planes.length;
		final int[][] newPlanes = new int[Length][];
		while (--Length >= 0) {
			newPlanes[Length] = VectorInt.ADD(planes[Length], offset); }
		return newPlanes; }

	/** deep-copies the Polygon 	 */
	final static public int [][] COPY_POLYGON(final int [][] _polygon) {
		final int [][] polygon = (int [][]) _polygon.clone();		//clone() does only a shallowCopy()
		for (int i = polygon.length; --i >= 0; ) { //not significantly faster than 
			polygon[i] = (int []) polygon[i].clone(); } 
		return polygon; }

	/** 
	 * @return the Sum of the Items in the square Section of the Matrix
	 * @param nn the Matrix to use
	 */
	public static long SUM(final int[][] nn) {
		return SUM(nn, 0, nn.length, 0, nn[nn.length>>1].length); 
	}

	/** 
	 * @return the Sum of the Items in the square Section of the Matrix
	 * @param nn the Matrix to use
	 * @param rowStart 
	 * @param rowStop
	 * @param colStart
	 * @param colStop
	 */
	public static long SUM(final int[][] nn, final int rowStart, final int rowStop, final int colStart, final int colStop) {
		long sum = 0; 
		for (int i = rowStop; --i >= rowStart; ) {
			sum += VectorInt.SUM(nn[i], colStop, colStart); }
		return sum; 
	}

	/** calculates both Row and Column Sums for the given Matrix 
	 * 
	 * @param nn the Matrix to analyze
	 * @param dimsNotEmpty 
	 * on  Input: the Number of Rows and Columns to consider
	 * on Output: the Number of Rows and Columns with nonzero Sum  
	 * @param sums the Row and Column Sums (one Row each) 
	 * @return the total Sum of all Elements
	 */
	final static public long SUM_ROWS_COLS(
		final int[][] nn,
		int[] dimsNotEmpty,
		int[][] sums) {
		if (dimsNotEmpty == null) {
			dimsNotEmpty =  new int[] {nn.length, nn[0].length}; }
		if (sums == null) {
			sums =  new int[2][]; }
		if (sums[0] == null) {// || (rowSums.length < numRows)) {//would be misleading otherwise!
			sums[0] = new int[dimsNotEmpty[0]]; }
		if (sums[1] == null) {
			sums[1] = new int[dimsNotEmpty[1]]; }
		long sum=0; 
		final int numRows = dimsNotEmpty[0];
		final int numCols = dimsNotEmpty[1];
		for (int i=numRows; --i>=0; ) {
			final int rowSum = (int) VectorInt.SUM(nn[i], numCols, 0);
			if (rowSum != 0) {
				sums[0][i]=rowSum;
				sum += rowSum;
			} else { 
				--dimsNotEmpty[0]; 
			}
		}
		long sum2=0; 
		for (int j=numCols; --j>= 0; ) {
			final int colSum = (int) MatrixInt.COL_SUM(nn, j, 0, numRows);
			if (colSum != 0) {
				sums[1][j]=colSum;
				sum2+=colSum;
			} else {
				--dimsNotEmpty[1]; 
			}
		}
		Assert.EQUALS(sum, sum2); 
		return sum;
	}

	/**
	 * 
	 * @param matrix the Matrix to calculate the Entropy for 
	 * @return the Entropy of the given Matrix 
	 */
	final static public double ENTROPY(final int[][] matrix) {
		return MatrixInt.ENTROPY(matrix, MatrixInt.SUM(matrix)); }

	/**
	 * 
	 * @param matrix the Matrix to calculate the Entropy for 
	 * @param sum the total Sum of Elements in this Matrix
	 * @return the Entropy of the given Matrix 
	 */
	final static public double ENTROPY(final int[][] matrix, final long sum) {
		return MatrixInt.ENTROPY(matrix, matrix.length, matrix[matrix.length >> 1].length, sum); }

	/**
	 * 
	 * @param matrix the Matrix to calculate the Entropy for 
	 * @param numRows the Number of Rows to consider 
	 * @param numCols the Number of Columns to consider 
	 * @param sum the total Sum of Elements in this Matrix
	 * @return the Entropy of the given Matrix 
	 */
	final static public double ENTROPY(
		final int[][] matrix,
		final int numRows,
		final int numCols,
		final long sum_) {
		final int sum = (int) sum_;
		double e = 0; 
		for (int i=0; i<numRows; i++) {
			for (int j=Math.min(numCols, matrix[i].length); --j>=0; ) {
				if (matrix[i][j] == 0) {
					continue; }
				final float p=matrix[i][j]/sum;
				e -= p*Math.log(p);
			}
		}
		return e;
	}
	
	/** 
	 * returns the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 
	 * 
	 * @param arg the Vectors to calculate the Extent for 
	 * @return the Extent of the Polygon
	 */
	final static public int[][] EXTENT(final int[][] arg) { return EXTENT(null, arg, 0, arg.length); } 

	/** 
	 * returns the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 
	 * 
	 * @param min_max an existing Extent can be extended
	 * @param arg the Vectors to calculate the Extent for 
	 * @return the Extent of the Polygon
	 */
	final static public int[][] EXTENT(int[][] min_max, final int[][] arg) {
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
	final static public int[][] EXTENT(int[][] min_max, final int[][] arg, final int start, final int stop) {
		if (stop <= 0) //Optimization: 
			return null; 
		if((min_max == null) || (min_max.length < 2))
			min_max =  new int[2][]; 
		COL_MIN(arg, min_max[0]); //, start, stop);
		COL_MAX(arg, min_max[1]); //, start, stop);
		return min_max;
	}

	/**Calculates the Length of the given Path	 */
	final static public double PATH_LENGTH(final int[][] x, final boolean closed, final int[] order) {
		return PATH_LENGTH(x, closed, order, 0, x.length); }

	/**Calculates the geometric Length of the given (closed) Path
	 * @see graphic.AGraph2D#drawPolygon(int[], int[], boolean)
	 */
	final static public double PATH_LENGTH(final int[][] x, final boolean closed, final int[] order, final int start, final int stop) {
		double path = 0;
		int i = stop;
		int i2 = (order != null ? order[0] : 0);
		if (!closed) { --i; 
			i2 = (order != null ? order[i] : i);
		}
		for (; --i >= start;) {	//the absolute Path doesn't really matter!
			int i1 = i2; i2 = (order != null ? order[i] : 0);
			path += VectorInt.DIST(x[i1],x[i2]);
		}
		return path; }

	/** Returns the Vectors orthogonal to the Points (only for 3Dim Tensors!)
	 * It is only slightly more effective to calculate all Normals at once	 
	 * @param numPoints The total Number of Points i.e. Vertices 
	 * @param planes The Plane Definitions i.e. the Lists of Vertices to each Plane 
	 * @param planeNormals the List of Normals for each Plane
	 * @return a List of Normals 
	 */
	final static public int[][] POINT_NORMALS(final int numPoints
	, final int[][] planes, final int[][] planeNormals) {	//should be protected!
		final int[][] pointNormals = new int[numPoints][];
		//Optimization: O(Pln)*3: just sum up all Normals on the Fly
		//instead of searching for specific Points! 
		for(int i = planes.length; --i >= 0;) { //
			final int[] plane  = planes[i];
			for(int j = plane.length; --j >= 0; ) { 
				final int point = plane[j]; //
				if (pointNormals[point] != null) { 
					VectorInt.ADD_AT(pointNormals[point], planeNormals[i]);
				} else { //Optimization: copy instead of add, if possible
					pointNormals[point] = VectorInt.COPY(planeNormals[i]); 
				}
			}
		}
		/**
		for (int point = numPoints; --point >= 0;) {
			//pointNormals[point] = PointNormal(planes, pointNormals, point); //O(Pt*Pln*3)
			VectorInt.NORMALIZE_AT(pointNormals[point]);
		} */
		return pointNormals; 
	}

	/** Calculates a single Point Normal by summing up the neighboring Plane Normals.  
	 * 
	 * @param planes The Plane Definitions i.e. the Lists of Vertices to each Plane 
	 * @param planeNormals the List of Normals for each Plane
	 * @param point The point for which to calculate the Normal
	 * @return the Normal 
	 */ 
	final static public int[] POINT_NORMAL(
		final int[][] planes,
		final int[][] planeNormals,
		final int point) {
		int[] sum = null;
		for(int i = planes.length; --i >= 0;) { //Search all Planes... 
			final int[] plane  = planes[i];
			for(int j = plane.length; --j >= 0; ) { 
				if (plane[j] == point) { //...for those containing this Point
					if (sum != null) { //Optimization: copy instead of add, if possible
						VectorInt.ADD_AT(sum, planeNormals[i]);
					} else {
						sum = VectorInt.COPY(planeNormals[i]); }
				}
			}
		}
		/*
		if (sum != null) { //due to Norming, the actual Number of Planes 
			VectorInt.NORMALIZE_AT(sum); } //does not matter!
		*/
		return sum; }

	/**
	 * Calculates the Distances of all points to the given PointOfView.
	 * @param PointOfView single fixed Point for which to calculate all Distances for 
	 * @param points the points to calculate the Distances to
	 * @return the Distances of the Points from the given Point
	 */
	final static public int[] ABSV_DIST(final int[] PointOfView, final int[][] points) {
		int[] ret = new int[points.length];
		for(int i = points.length; --i >= 0; ) {
			ret[i] = VectorInt.DIST_ABS(points[i], PointOfView); }
		return ret;
	}

	/**
	 * @param points 
	 * @param plane
	 * @return the middle Points of this Plane
	 */
	public static int[] GET_MID_POINT(final int[][] points, final int[] plane) {
		final int[] mid = VectorInt.COPY(points[plane[0]]);
		for(int j = plane.length; --j > 0;) { 	//Skip the Zero Point
			VectorInt.ADD_AT(mid, points[plane[j]]); }	//subtract the Offset
		return VectorInt.DIV_AT(mid, plane.length);
	}

	/**
	 * @param points 
	 * @param planes
	 * @return the middle Points of the Planes
	 */
	final static public int[][] GET_MID_POINTS(final int[][] points, final int[][] planes) {
		final int[][] mids = new int[planes.length][];
		for(int i = planes.length; --i >= 0; ) {
			final int[] plane = planes[i]; 
			final int[] mid = GET_MID_POINT(points, plane);
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
	final static public int[][] PLANE_NORMALS(final int[][] points
	, final int[][] planes) {
		final int[][] planeNormals = new int[planes.length][];
		final int[] diff1 = new int[planes.length]; //Optimization:
		final int[] diff2 = new int[planes.length]; //reuse 
		for(int i = planes.length; --i >= 0; ) {
			final int[] plane = planes[i];
			if (plane.length < 3) {
				continue; }
			planeNormals[i] = VectorInt.NORMAL(diff1, diff2
			, points[plane[0]], points[plane[1]], points[plane[2]], true);
		}
		return planeNormals;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  return a List of inverse Permutations, i.e. Ranks.
	 * @param listOfPositions
	 * @return a List of inverse Permutations, i.e. Ranks. 
	 */
	final static public int[][] INVERSE(final int[][] listOfPositions) {
		final int[][] ret = new int[listOfPositions.length][];
		for (int i = listOfPositions.length; --i >= 0;) 
			ret[i] = VectorInt.INVERSE(listOfPositions[i]);
		return ret;
	}
	
	/** copy original math.matrix into a newer (larger) one, shift right by 1 
	 * @param a Matrix to shift
	 * @return a new Matrix
	 */
	final static public int[][] SHR(final int[][] a) {
		final int m = a.length;
		final int n = a[0].length;
		final int[][] u=new int[1+m][1+n];
		for (int k=1; k<=m; k++) {
			for (int l=1; l<=n; l++) {
				u[k][l]=a[k-1][l-1];
			}
		}
		return u;
	}

	/** 
	 * Sorts the Rows in this Matrix so the Maximum ends in the last Row. 
	 * This Operation is idempotent and invariant to Column Swaps
	 * @param useSum uses the Sum instead of the maximum Value in each Row
	 * @return a new Matrix sharing the Rows 
	 */
	final static public int[][] SORT_ROWS_BY_MAX(final int[][] a, final boolean useSum) {
		final int[] maxVals = new int[a.length];
		if (useSum) { //Sum of Values (only for positive Values)
			ROW_SUM(a, 0, a.length, maxVals); 
		} else { //actual Maximums
			MAX_VAL(maxVals, a, 0, a.length); 
		}
		//sort by creating an Index to sort the Columns accordingly
		final int[] indexRow = HunterInt.INDEX(maxVals);
		//Sort the Rows into a new Matrix so that tha Maximum Maximum ends up at the Top
		final int[][] tmp = PERMUTE_ROWS(a, indexRow);
		return tmp;
	}

	/** 
	 * Sorts the Rows in this Matrix so the Maximum ends in the last Row.  
	 * This Operation is idempotent and invariant to Row Swaps. 
	 * @param useSum uses the Sum instead of the maximum Value in each Column
	 * @return the same Matrix with swapped Columns 
	 */
	final static public int[][] SORT_COLS_BY_MAX_AT(final int[][] a, final boolean useSum) {
		int[] maxVals = new int[a[0].length];
		if (useSum) {
			COL_SUM(a, 1, a.length, maxVals); //Sum of Values (only for positive Values)
		} else { //take first Row and ...
			System.arraycopy(a[0], 0, maxVals, 0, a[0].length); //... compare only the Rest
			COL_MAX(a, 1, a.length, maxVals); //actual Maximums of Columns
		}
		//sort by creating an Index
		final int[] indexRow = HunterInt.INDEX(maxVals);
		//Sort the Columns within the same Matrix so that the max. Maximum ends up at the Top
		PERMUTE_COLS_AT(a, indexRow);
		//VectorInt.permuteAt(maxVals, indexRow); //just to check...
		return a;
	}

	/** @return the Minimum and Maximum Values of each Column... 
	 * too complex to optimize for now... 
	 * 
	 * Use Min and Max separately, which is clearer too!
	 */
	//	final static public int[][] MIN_MAX(int[][] arr) { }

	/**
	 * @return the Minimum Values of each Column in Place
	 */
	final static public int[] MIN(final int[][] arg) {
		return MIN(null, arg, 0, arg.length); }

	/**
	 * @return the Minimum Values of each Column in Place
	 */
	final static public int[] MIN(final int[] ret, final int[][] arg) {
		return MIN(ret, arg, 0, arg.length); }

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	final static public int[] MIN(final int[][] arg, final int start, final int stop) {
		return MIN(null, arg, start, stop); }

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	final static public int[] MIN(int[] ret, final int[][] arg, final int start, final int stop) {
		//return Min(VectorInt.fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		if (stop <= start) //Optimization: 
			return null; 
		int i = stop-1;
		if (ret == null) 
			ret = VectorInt.COPY(arg[i]);
		else 
			VectorInt.COPY_AT(ret, arg[i]);
		for (; --i >= start;) 
			VectorInt.MIN_AT(ret, arg[i]);
		return ret;
	}

	/** @return the Maximum Values of each Column */
	final static public int[] MAX(final int[][] arg) {
		return MAX(null, arg, 0, arg.length); }
	
	/** @return the Maximum Values of each Column */
	final static public int[] MAX(final int[] ret, final int[][] arg) {
		return MAX(ret, arg, 0, arg.length); }
	
	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	final static public int[] MAX(final int[][] arg, final int start, final int stop) {
		return MAX(null, arg, start, stop); }
	
	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	final static public int[] MAX(int[] ret, final int[][] arg, final int start, final int stop) {
		//return Min(VectorInt.fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		if (stop <= start) //Optimization: 
			return null; 
		int i = stop-1;
		if (ret == null) 
			ret = VectorInt.COPY(arg[i]);
		else
			VectorInt.COPY_AT(ret, arg[i]);
		for (; --i >= start;) 
			VectorInt.MAX_AT(ret, arg[i]);
		return ret;
	}
	
	/**
	 * @see #MAX(int[][]) for the Maximum of multiple Columns 
	 * @param col the Column to determine the Maximum of 
	 * @param matrix the Matrix to determine the Maximum of a single Column 
	 * @return the Maximum of the single Column
	 */
	final static public int MAX(final int[][] matrix, final int col) {
		return MAX(matrix, col, 0, matrix.length); }
	
	/**
	 * @see #MAX(int[][]) for the Maximum of multiple Columns 
	 * @param col the Column to determine the Maximum of 
	 * @param matrix the Matrix to determine the Maximum of a single Column 
	 * @return the Maximum of the single Column
	 */
	final static public int MAX(final int[][] matrix, final int col, final int start, final int stop) {
		int max = Integer.MIN_VALUE; 
		for (int j = stop; --j >= start; ) {
			final int row[] = matrix[j]; 
			if (max < row[col])
				max = row[col]; 
		}
		return max;
	}

	/**
	 * @see #MIN(int[][]) for the Minimum of multiple Columns 
	 * @param col the Column to determine the Maximum of 
	 * @param matrix the Matrix to determine the Maximum of a single Column 
	 * @return the Maximum of the single Column
	 */
	final static public int MIN(final int[][] matrix, final int col) {
		return MIN(matrix, col, 0, matrix.length); }
	
	/**
	 * @see #MIN(int[][]) for the Minimum of multiple Columns 
	 * @param col the Column to determine the Maximum of 
	 * @param matrix the Matrix to determine the Maximum of a single Column 
	 * @return the Maximum of the single Column
	 */
	final static public int MIN(final int[][] matrix, final int col, final int start, final int stop) {
		int min = Integer.MAX_VALUE; 
		for (int j = stop; --j >= start; ) {
			final int row[] = matrix[j]; 
			if (min > row[col])
				min = row[col]; 
		}
		return min;
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public int[][] ADD_AT(int[][] ret, int Increment) {
		return ADD_AT(ret, Increment, 0, ret.length);
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[][] ADD_AT(int[][] ret, int Increment, int start, int stop) {
		while (--stop >= start) 
			VectorInt.ADD_AT(ret[stop], Increment);
		return ret;
	}

	/**
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public int[][] ADD_AT(final int[][] ret, final int[] Decrement) {
		return ADD_AT(ret, Decrement, 0, ret.length);
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[][] ADD_AT(int[][] ret, int[] Decrement, int start, int stop) {
		while (--stop >= start) 
			VectorInt.ADD_AT(ret[stop], Decrement);
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Decrement the Decrement to subtract from 
	  * @return the given Array decremented by the given Decrement
	  */
	final static public int[][] SUB_AT(int[][] ret, int[] decrement) {
		return SUB_AT(ret, decrement, 0, ret.length);
	}

	/**
	  * @return the given Array decremented by the given Decrement
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Decrement the Decrement to subtract from 
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[][] SUB_AT(int[][] ret, int[] Decrement, int start, int stop) {
		while (--stop >= start) 
			VectorInt.SUB_AT(ret[stop], Decrement);
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////

	/**Increases the capacity of this VectorInt, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final static public synchronized int[][] SET_CAPACITY(final int minCapacity, final int[][] items, final int itemCount) {
		final int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) 
			return items;
		final int[][] newData = new int[minCapacity][];
		if (oldCapacity > 0) 
			System.arraycopy(items, 0, newData, 0, itemCount);
		return newData;
	}
	
	/**Ensures the capacity of this VectorInt, 
	 * so that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final static public synchronized int[][] SET_CAPACITY(final int minRows, final int minCols, final int[][] items, int itemCount) {
		int[][] ret = SET_CAPACITY(minRows, items, itemCount); 
		for (int i = minRows; --i >= 0; ) 
			ret[i] = VectorInt.SET_CAPACITY(minCols, ret[i]); 
		return ret; 
	}
	
	/** Returns a resized (larger OR smaller) Copy of the given Array 
	 * @deprecated replace by SET_CAPACITY
	 */
	public static int[][] RESIZE(final int[][] arr, int newRows, final int newCols) {
		int[][] ret = new int[newRows][];
		if (newRows > arr.length) 
			newRows = arr.length;
		while (--newRows >= 0) 
			ret[newRows] = VectorInt.SET_SIZE(arr[newRows], newCols);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** @return an Array filled with the Sum of all Values in each Row.	 */
	final static public int[] ROW_SUM(final int[][] arr) {
		return ROW_SUM(arr, 0, arr.length, new int[arr.length]); }

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public int ROW_SUM(final int[][] arr, final int row) {
		return ROW_SUM(arr, row, 0, arr.length); }

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public int ROW_SUM(final int[][] arr, final int row, 
			final int start, final int stop) {
		return (int) VectorInt.SUM(arr[row], stop, start); }

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public int[] ROW_SUM(final int[][] arr, final int[] ret) {
		return ROW_SUM(arr, 0, arr.length, ret); }

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public int[] ROW_SUM(final int[][] arr, 
			final int start, int stop, final int[] ret) {
		int len = arr[0].length;
		while (--stop >= start) 
			ret[stop] = (int) VectorInt.SUM(arr[stop], len, 0); 
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public int[] COL_SUM(final int[][] arr) {
		return COL_SUM(arr, 0, arr.length, null); }

	/** 
	 * 
	 * @param nn the Matrix to use
	 * @param col the Column to calculate the Sum for
	 * @return the Sum of the Items in the given Column
	 */
	public static long COL_SUM(final int[][] nn, int col) {
		return COL_SUM(nn, col, 0, nn.length); }

	/** 
	 * 
	 * @param nn the Matrix to use
	 * @param col the Column to calculate the Sum for
	 * @param start 
	 * @param stop
	 * @return the Sum of the Items in the given Column
	 */
	public static int COL_SUM(final int[][] nn, final int col, 
			final int start, final int stop) {
		int colSum = 0;//MatrixInt.COL_SUM(nn, j, 1, numRows);
		for (int i=stop; --i>=start; ) {
			if (nn[i].length <= col) { //error tolerant
				continue; }
			colSum += nn[i][col]; } 
		return colSum;
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public int[] COL_SUM(final int[][] arr, final int[] ret) {
		return COL_SUM(arr, 0, arr.length, ret);
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public int[] COL_SUM(final int[][] arr, 
			final int startRow, final int stopRow, int[] ret) {
		if (ret == null) 
			ret  = new int[stopRow]; 
		for(int i = stopRow; --i >= startRow; ) 
			VectorInt.ADD_AT(ret, arr[i], 0, ret.length); 
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** @return The Maximum Vector of all Rows as Values in the Array. 	 */
	final static public int[] COL_MAX(final int[][] arr) {
		return COL_MAX(arr, 1, arr.length, VectorInt.COPY(arr[0]));
	}

	/**
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public int[] COL_MAX(final int[][] arr, final int[] ret) {
		if (ret == null)
			return COL_MAX(arr); //more effective!
		return COL_MAX(arr, 0, arr.length, ret);
	}

	/**
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public int[] COL_MAX(final int[][] arr, 
			final int startRow, int stopRow, final int[] ret) {
		while (--stopRow >= startRow) 
			VectorInt.MAX_AT(ret, arr[stopRow], 0, ret.length);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** @return The Maximum Vector of all Rows as Values in the Array. 	 */
	final static public int[] COL_MIN(final int[][] arr) {
		return COL_MIN(arr, 1, arr.length, VectorInt.COPY(arr[0]));
	}

	/**
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public int[] COL_MIN(final int[][] arr, final int[] ret) {
		if (ret == null)
			return COL_MIN(arr); //more effective!
		return COL_MIN(arr, 0, arr.length, ret);
	}

	/**
	 * @para ret the return Vector. To contain the Maximum, it must be set to -Infinity before!
	 * @return The Maximum Vector of all Rows as Values in the Array.
	 */
	final static public int[] COL_MIN(final int[][] arr, final int startRow, int stopRow, final int[] ret) {
		while (--stopRow >= startRow) 
			VectorInt.MIN_AT(ret, arr[stopRow], 0, ret.length);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** @return a shallow Copy of the given Array */
	public static int[][] SHALLOW_COPY_AT(final int[][] ret, final int[][] arr) {
		int len;
		if (ret.length != (len = arr.length)) 
			throw new IndexOutOfBoundsException("Expected: " + ret.length + " Actual: " + arr.length);
		while (--len >= 0) 
			ret[len] = arr[len];
		return ret;
	}

	/** @return a deep Copy of the given Matrix */
	public static int[][] COPY_AT(final int[][] ret, final int[][] arr) {
		return COPY_AT(ret, arr, 0, arr.length); }

	/** @return a deep Copy of the given Matrix */
	public static int[][] COPY_AT(final int[][] ret, final int[][] arr, final int start, int stop) {
		while (--stop >= start) { //Optimization!
			System.arraycopy(arr[stop], 0, ret[stop], 0, arr[stop].length);
			//VectorDouble.copyAt(ret[stop], arr[stop]); }
		} 
		return ret;
	}

	/** @return the Matrix ret with deep Copie of the given Vector arr in every Row */
	public static int[][] COPY_AT(final int[][] ret, final int[] arr, final int start, int stop) {
		while (--stop >= start) {
			//VectorDouble.copyAt(ret[stop], arr); 
			System.arraycopy(ret[stop], 0, arr, 0, arr.length);
		} //Optimization!
		return ret;
	}

	/** @return a deep Copy of the given Matrix */
	final static public int[][] COPY(final int[][] arr) {
		return COPY(arr, arr.length, false); 
	}

	/** @return a deep Copy of the given Matrix */
	final static public int[][] COPY(final int[][] arr, final int length, final boolean deepCopy) {
		final int[][] ret = new int[length][];
		if (deepCopy)
			for(int len = length; --len >= 0; ) 
				ret[len] = VectorInt.COPY(arr[len]);
		else 
			System.arraycopy(arr, 0, ret, 0, length); 
		return ret;
	}

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public int[][] SET_DIM(final int[][] a, final int rows) {
		if (a.length == rows) 
			return a; 
		final int[][] ret = new int[rows][];
		System.arraycopy(a, 0, ret, 0, a.length);
		//Arrays.fill(ret, a.length, dim, 0);
		return a;
	}

	/**
	 * Setting the Vectors to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vectors fit, they are returned unchanged!
	 */
	final static public int[][] SET_DIM_AT(final int[][] a, final int cols) {
		for (int i = a.length; --i >= 0; ) 
			a[i] = VectorInt.SET_DIM_AT(a[i], cols);
		return a;
	}

	/** @return the given Array multiplied in Place by the given Factor */
	public static int[][] MUL_AT(final int[][] ret, final int Factor) {
		return MUL_AT(ret, Factor, 0, ret.length); }

	/** @return the given Array multiplied in Place by the given Factor */
	public static int[][] MUL_AT(final int[][] ret, final int Factor, 
			final int start, int stop) {
		while (--stop >= start) 
			VectorInt.MUL_AT(ret[stop], Factor); 
		return ret;
	}

	/**
	 * This allows to multiply only a certain rectangular Range in the Target Matrix.
	 * @return the given Array multiplied in Place by the given Factor */
	final static public int[][] MUL_AT(
		final int[][] ret, 
		final int Factor, 
		final int startRow, 
		int stopRow, 
		final int startCol, 
		final int stopCol) {
		while (--stopRow >= startRow) {
			VectorInt.MUL_AT(ret[stopRow], Factor, startCol, stopCol);
		}
		return ret;
	}

	/** @return the Transpose of the given Array  */
	public static int[][] TRP(final int[][] a) {
		//return trpAt(copy(a)); //too slow, doesn't work for non-square Matrices!!!
		int[][] ret = new int[a[0].length][a.length]; //make it rectangular
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
	public static int[][] TRP_AT(final int[][] ret) {
		for (int i = ret.length; --i >= 0; ) {
			for (int j = i + 1; --j >= 0; ) {
				final int tmp = ret[i][j];
				ret[i][j] = ret[j][i];
				ret[j][i] = tmp;
			}
		}
		return ret;
	}

	/** Randomizes all the Weights of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Does NOT require a rectangular Array. 	 */
	final static public int[][] RANDOMIZE_AT(final int[][] arr) {
		for (int j = arr.length; --j >= 0; ) 
			VectorInt.RANDOMIZE_AT(arr[j]);
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
	public static int[][] DIST_SQR_MATRIX(final int[][] ret, final int[][] Vectors) {
		for (int i = Vectors.length; --i >= 0; ) { //symmetric Matrix
			int j = Vectors.length;
			int[] I = Vectors[i]; //initialize the whole Matrix, O(V^2)
			int[] A = ret[i]; //
			A[i] = 0; //not necessary, because new Array contains 0s already!
			while (--j > i) { //symmetric Matrix //calculate only 50%!
				A[j] = ret[j][i] = (int) VectorInt.DIST_SQR(I, Vectors[j]); //Symmetric!
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
	final static public int[][] BinaryImplication(final int[] l, final int[] r) {
		final int[][] ret = new int[l.length][r.length];
		for (int i = l.length; --i >= 0; ) {
			final int li = l[i];
			final int[] reti = ret[i];
			for (int j = r.length; --j >= 0; ) {
				if((reti[j] = r[j]) < li) {
					continue; }
				reti[j] = li;
			}
		}
		return ret;
	}

	/** Cross Product in Place	 */
	final static public int[] MUL_CROSS_AT(int[] ths, int[] arg) {
		return VectorInt.COPY_AT(ths, MUL_CROSS(ths, arg));
	}

	/** Cross Product in R^3 */
	final static public int[] MUL_CROSS(int[] ths, int[] arg) {
		int end = 0;
		int[] Result = new int[3];
		if (ths.length > 3)
			throw new ArrayIndexOutOfBoundsException();
		if (arg.length > 3)
			throw new AbstractMethodError();
		if (ths.length < 2) {
			Result[3] = ths[0] * arg[1];
			return Result;
		}
		if (arg.length < 2) {
			Result[3] = -arg[0] * ths[1];
			return Result;
		}
		if ((ths.length < 3) && (arg.length < 3)) {
			Result[0] = 0;
			Result[1] = 0;
			end = 2;
		}
		int i = 3;
		while (--i >= end) {
			int j = (i == 2) ? 0 : i + 1;
			int k = 3 - i - j;
			Result[i] = (ths[j] * arg[k]) - (ths[k] * arg[j]);
		}
		return Result;
	}

	/**
	  * The dyadic Cross Product of two Vectors
	  * m[i,j] = a[i]*b[j]
	  */
	final static public int[][] DYAD_PROD(int[] l, int[] r) {
		int[][] ret = new int[l.length][r.length];
		int[] reti;
		int li;
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
	final static public int[] MAP(final int[][] b, final int[] a) { //previously named mul()
		return MAP(b, a, 0, b[0].length);
	}

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
	  * @param a the left  Row Vector
	  * @param b the right Matrix
	  * @return the Product Vector A*B
	  */
	final static public int[] MAP(final int[][] b, final int[] a, final int start, final int stop) { //previously named mul()
		final int[] ret = new int[b.length];
		for(int i = b.length; --i >= 0;) { //saves Initialization and one Addition!
			ret[i] = (int) VectorInt.MAP(b[i], a);
		}
		return ret;
	}

	/** @see #MAP(int[], int[], int[][], int, int) 	 */
	final static public int[] MAP(final int[] a, final int[][] b, final int start, final int stop) { //previously named mul()
		return MAP(null, a, b, start, stop); 
	}

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
	final static public int[] MAP(int[] ret, final int[] a, final int[][] b, final int start, final int stop) { //previously named mul()
		if (ret == null) {
			ret = new int[a.length]; }
		int i = b.length;
		if (i > a.length) {
			i = a.length; }
		if (--i < 0) {
			return ret; } //Not necessary initialize to 0...
		VectorInt.MUL(ret, b[i], a[i], start, stop); //...single out the first Operation:
		while (--i >= 0) { //...saves Initialization and one Addition! esp. with low-dim. Calc.
			VectorInt.ADD_PROD_AT(ret, b[i], a[i], start, stop); } //+= Vector * Skalar!
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
	final static public int[] MAP(final int[] a, final int[][] b) { //previously named mul()
		//return MAP(a, b, 0, b[0].length);
		final int[] ret = new int[a.length];
		int i = b.length;
		if (i > a.length) {
			i = a.length; }
		if (--i < 0) {
			return ret; } //Not necessary initialize to 0...
		VectorInt.MUL(ret, b[i], a[i]); //...single out the first Operation:
		while (--i >= 0) { //...saves Initialization and one Addition!
			VectorInt.ADD_PROD_AT(ret, b[i], a[i]); } //+= Vector * Skalar!
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
	final static public int[][] CAT(int[][] a, int[][] b) { //previously named mul()
		final int[][] ret = new int[a.length][];
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
	final static public int[] MAX_MIN_MAP(int[] arg, int[][] a) { //previously named mul()
		int i = a.length;
		if (i > arg.length) 
			i = arg.length;
		--i; //Don't initialize to 0
		final int[] ret = VectorInt.MIN_AT(VectorInt.COPY(a[i]), arg[i]); //Single out the first Operation:
		while (--i >= 0) //saves Initialization and one Addition!
			VectorInt.MAX_MIN_PROD(ret, a[i], arg[i]); //* Skalar!
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
	final static public int[][] MAX_MIN_MAP(final int[][] a, final int[][] arg) { //previously named mul()
		final int[][] ret = new int[a.length][];
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
	final static public boolean IS_SYMMETRIC(final int[][] arr) {
		return IS_SYMMETRIC(arr, arr.length);
	}

	/**
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = a[j][i].
	  */
	final static public boolean IS_SYMMETRIC(final int[][] arr, final int length) {
		for (int i = length; --i >= 0; ) { //Addressing could be even more effective, if the Row Strategy
			final int[] row = arr[i]; //is changed for a Column Strategy in the Middle
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
	final static public boolean IS_ANTI_SYMMETRIC(final int[][] arr) {
		return IS_ANTI_SYMMETRIC(arr, arr.length); }

	/**
	  * @param arr The Array to test for Symmetry
	  * @return true if the given Array is symmetric, i.e. a[i][j] = -a[j][i].
	  */
	final static public boolean IS_ANTI_SYMMETRIC(final int[][] arr, final int length) {
		for (int i = length; --i >= 0; ) { //Addressing could be even more effective, if the Row Strategy
			final int[] row = arr[i]; //is changed for a Column Strategy in the Middle
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
	final static public void FILL_LOWER(final int[][] a, final int value, final int length) {
		for (int j=0; j<length; j++) { //
			VectorInt.FILL_AT(a[j], value, 0, j ); }
	}
	
	/** copy the lower Triangle of this Matrix to it's upper
	 * @param a the Matrix to copy
	 * @param length the valid Length to use
	 */
	final static public void COPY_LOWER_TO_UPPER(final int[][] a) {
		COPY_LOWER_TO_UPPER(a, a.length); }
	
	/**
	  * @param arr The Array to make symmetric
	  * @return the given Array made symmetric by copying the lower Triangle to the upper.
	  */
	final static public int[][] COPY_LOWER_TO_UPPER(final int[][] a, int length) {
		for (int i = length; --i >= 0; ) { //Addressing could be even more effective, if the Row Strategy
			final int[] a_i = a[i]; //is changed for a Column Strategy in the Middle
			for (int j = i; --j >= 0; ) {
				a[j][i] = a_i[j]; }
		}
		return a;
	}
	
	/**
	  * @param arr The Array to be made symmetric
	  * @return the given Array made symmetric.
	  */
	final static public int[][] MAKE_SYMMETRIC(int[][] arr) {
		for (int i = arr.length; --i >= 0; ) {
			final int[] row = arr[i];
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
	final static public int[][] MAKE_ANTI_SYMMETRIC(int[][] arr) {
		for (int i = arr.length; --i >= 0; ) {
			final int[] row = arr[i];
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
	final static public int[][] NEG_AT(int[][] ret) {
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
	final static public int[][] NEG_AT(int[][] ret, int start1, int stop1, int start2, int stop2) {
		while (--stop1 >= start1) 
			VectorInt.NEG_AT(ret[stop1], start2, stop2);
		return ret;
	}

	/**
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[][] ABS_AT(int[][] ret) {
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
	final static public int[][] ABS_AT(int[][] ret, int start1, int stop1, int start2, int stop2) {
		while (--stop1 >= start1) 
			VectorInt.ABS_AT(ret[stop1], start2, stop2);
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Matrix Algebra (Linear Function Algebra)
	///////////////////////////////////////////////////////////////////////////////////

	/**Determines the maximum Degree of the given Dimension
	 * As a preparation for Transposition.
	 */
	final static public int MAX_LENGTH(final int[][] a) {
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
	final static public int[] EXTRACT(final int[] ret, final int[][] a, final int[] cols) {
		return EXTRACT(ret, a, cols, 0, a.length);
	}

	/**
	 * Extracts one Element from each Row at the given Cols 
	 */
	final static public int[] EXTRACT(final int[] ret, final int[][] a, final int[] cols, final int startRow, int stopRow) {
		while (--stopRow >= startRow) 
			ret[stopRow] = a[stopRow][cols[stopRow]];
		return ret;
	}

	/**
	 * Setting to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is null, the Zero Matrix is returned.
	 */
	final static public int[][] DIAG_AT(final int[][] a, final int[] diag_) {
		for (int i = a.length; --i >= 0; ) {
			//VectorInt.diagAt(a[i], (diag == null) ?  1.0 : diag[i]);
			final int[] Row = a[i]; //faster to call it directly
			Arrays.fill(Row, 0);
			if (diag_ != null) 
				Row[i] =  diag_[i]; 
		}
		return a;
	}

	/** adds the value to any diagonal Element */
	final static public int[][] ADD_DIAG_AT(final int[][] matrix, final int value, final int start, int stop) {
		while (--stop >= start) 
			matrix[stop][stop] += value;
		return matrix;
	}
	
	/** fills the whole Matrix with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param value the value to fill with 
	 */
	final static public void FILL(final int[][] a, final int value) {
		for (int row = a.length; --row >= 0; ) 
			FILL_ROW(a, row, value, 0, a.length); 
	}
	
	/** fills the given Row with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param row the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_ROW(final int[][] a, final int row, final int value) {
		FILL_ROW(a, row, value, 0, a.length); }
	
	/** fills the given Row with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param row the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_ROW(final int[][] a, final int row, final int value
	, final int start, final int stop) {
		VectorInt.FILL_AT(a[row], value, start, stop); }
	
	/** fills the given Column with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param col the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_COL(final int[][] a, final int col, final int value) {
		FILL_COL(a, col, value, 0, a.length); }
	
	/** fills the given Column with the given Value 	
	 * 
	 * @param a the Matrix to fill 
	 * @param col the column to fill
	 * @param value the value to fill with 
	 */
	final static public void FILL_COL(final int[][] a, final int col, final int value
	, final int start, final int stop) {
		for (int i = stop; --i >= start;) {
			a[i][col] = value; }
	}
	
	/**
	 * Setting 'a' to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is 1, the Unity Matrix is returned.
	 */
	final static public int[][] FILL_DIAG_AT(int[][] a, int diag_, final boolean clearNonDiag) {
		for (int i = a.length; --i >= 0; ) {
			//VectorInt.diagAt(a[i], (diag == null) ?  1.0 : diag[i]);
			final int[] Row = a[i]; //faster to call it directly
			if (clearNonDiag) {
				Arrays.fill(Row, 0); } 
			Row[i] = diag_;
		} //setting the Unit Vector.
		return a;
	}

	/**
	 * Setting to a diagonal Matrix in Place using the EigenValues given in diag.
	 * If diag is null, the Unity Matrix is returned.
	 */
	final static public int[][] ONE_AT(final int[][] a) {
		return FILL_DIAG_AT(a, 1, true);
	}

	/**
	 * Setting to a full zero Matrix in Place. 
	 */
	final static public int[][] ZERO_AT(final int[][] a) {
		FILL(a, 0); 
		//diagAt(a, null); //less effective
		return a; 
	}

	/**
	 * @return a Zero Matrix (zero Mapping) for the given Dimension.
	 */
	final static public int[][] ZERO(final int dim) {
		return new int[dim][dim];
	}

	/**
	 * Optimization: this is faster, because the Matrix needn't be cleared. 
	 * @return a Unity Matrix (identical Mapping) for the given Dimension.
	 */
	final static public int[][] ONE(final int dim) { //Assume a square Matrix
		return FILL_DIAG_AT(new int[dim][dim], 1, false);
	}  //could be made quite sparse, but for the sake of it...

	/**
	 * Checks whether these Row- Vectors for the Unity Matrix
	 * Makes only Sense for Matrices
	 */
	final static public boolean IS_ONE(int[][] a) { //Assume a square Matrix
		//float eps = IMeasurAble.FLOAT_ACCURACY; //
		for (int i = a.length; --i >= 0; ) {
			if (!VectorInt.IS_ONE(a[i], i)) {
				return false;
			}
		}
		return true;
	}

	/** @see Object#equals(java.lang.Object)	 */
	final static public boolean EQUALS(final int[][] a, final int aLength, final int[][] b, final int bLength) {
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
	final static public boolean EQUALS(final int[][] a, final int[][] b) {
		return EQUALS(a, a.length, b, b.length); 
	}
	
	/** @see Object#equals(java.lang.Object)
	 */
	final static public boolean EQUALS(final int[][] a, final int[][] b, final int start, final int stop) {
		for (int i = stop; --i >= start; ) {
			if (! VectorInt.EQUALS(a[i], b[i])) 
				return false; 
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
	final static public boolean IS_ZERO(final int[][] a) { //Assume a square Matrix
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
	final static public boolean IS_ZERO(final int[][] a, final int start, final int stop) { //Assume a square Matrix
		//float eps = IMeasurAble.FLOAT_ACCURACY; //
		for (int i = stop; --i >= start; ) {
			if (!VectorInt.IS_ZERO(a[i])) {
				return false;
			}
		}
		return true;
	}

	/** @return this Vector with the Rows permuted according to the given Permutation     */
	final static public int[][] PERMUTE_COLS_AT(final int[][] a, final int[] index) {
		int[] tmp, swp = new int[index.length]; //reusing the Array since no Permutation in Place!
		for (int i = a.length; --i >= 0;) {
			tmp = a[i]; a[i] = HunterInt.PERMUTE(swp, tmp, index);
			swp = tmp;
		}
		return a;
	}

	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public int[][] PERMUTE_ROWS(final int[][] a, final int[] index) {
		return PERMUTE_ROWS(new int[a.length][], a, index);
	}

	/** @return this Vector with the Rows permuted according to the given Permutation     */
	final static public int[][] PERMUTE_ROWS(final int[][] ret, final int[][] a, final int[] index) {
		for (int i = index.length; --i >= 0;) 
			ret[i] = a[index[i]];
		return ret;
	}

	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public int[][] PERMUTE_ROWS_AT(final int[][] a, final int[] index) {
		int[][] tmp = new int[a.length][];
		PERMUTE_ROWS(tmp, a, index);
		System.arraycopy(tmp, 0, a, 0, a.length);
		return a;
	}
	
	/// The following Code does not work, because in Place is not possible!
	/*		int[] tmp;	//Undo the Row Permutations!
			int j, k = a.length;
			while (--k >= 0) { //not a proper Permutation! sensitive to Sequence of Processing!
				if (perm[k] == k) {
					continue; }
				tmp = a[k]; a[k] = a[j = perm[k]]; a[j] = tmp; }
			return a; }
	*/
	
	/**Adds Columns to a Tensor to make it square.
	 * This eliminates possible Optimizations due to sparse Matrices,
	 * but is necessary for Operations like LU_DecomposeAt()	 */
	final static public int[][] MAKE_SQUARE_AT(final int[][] a) {
		int i = a.length;
		while (--i >= 0) //Store the Inverse of the Row-Max Norm for Pivoting
			a[i] = VectorInt.SET_DIM_AT(a[i], a.length);
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
	final static public int[][] ORTHO_AT(final int[][] a, int[] SqrNorm) {
		if (SqrNorm == null) {
			SqrNorm = new int[a.length];
		} //now it is actually used by subtPart!
		for (int i = a.length; --i >= 0; ) {
			final int[] iRow = a[i];
			for (int j = a.length; --j > i; ) { //Subtract all lower Row Vectors
				VectorInt.SUB_PART_AT(iRow, a[j], SqrNorm[j]);
			} //a[i] -= <a[i],a[j]> a[j] / <a[j],a[j]>
			SqrNorm[i] = (int) VectorInt.NORM_SQR(iRow);
		}
		return a;
	}

	/**Normalizes these Row- Vectors to (euklidean) Length 1
	 * Makes only Sense for Matrices */
	final static public int[][] ORTHO(final int[][] a) {
		return ORTHO_AT(COPY(a), null);
	}

	/**
	 * @return Maximum Value of the every Row in the Array.
	 */
	final static public int[] MAX_VAL(final int[] ret, final int[][] arr) {
		return MAX_VAL(ret, arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int[] MAX_VAL(final int[] ret, final int[][] arr, 
			final int startRow, int stopRow) {
		while (--stopRow >= startRow) 
			ret[stopRow] = VectorInt.MAX_VAL(arr[stopRow]);
		return ret;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int[] MAX_VAL(
		int[] ret,
		int[][] arr,
		int startRow,
		int stopRow,
		int startCol,
		int stopCol) {
		while (--stopRow >= startRow) {
			ret[stopRow] = VectorInt.MAX_VAL(arr[stopRow], startCol, stopCol);
		}
		return ret;
	}

	/**
	 * Maximum Norm
	 * @return Positions of the Maximum Value in each Row of the Matrix.
	 */
	final static public int[] MAX_POS(int[] ret, int[][] arr) {
		return MAX_POS(ret, arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Positions of the Maximum Value in each Row of the Matrix.
	 */
	final static public int[] MAX_POS(int[] ret, int[][] arr, int startRow, int stopRow) {
		while (--stopRow >= startRow) 
			ret[stopRow] = VectorInt.MAX_POS(arr[stopRow]);
		return ret;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Positions of the Maximum Values in each Row of the Matrix.
	 */
	final static public int[] MAX_POS(int[] ret, int[][] arr, int startRow, int stopRow, int startCol, int stopCol) {
		while (--stopRow >= startRow) {
			ret[stopRow] = VectorInt.MAX_POS(arr[stopRow], startCol, stopCol);
		}
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Re-Composition of LU decomposition	in Place.	*/
	final static public int[][] COMPOSE_LU(int[][] a, int[] perm) {
		return COMPOSE_LU_AT(COPY(a), perm);
	}
	
	/** Re-Composition of LU decomposition in Place.
	  * Undoes the Permutation of Rows also.
	  * This Operation can be done in Place,
	  * if you start from Bottom Left, because this Element == a[i,j]
	  * is only used within this same line.
	  */
	final static public int[][] COMPOSE_LU_AT(final int[][] a, final int[] rows) {
		//if (! LU_Decomposed) return this; LU_Decomposed = false;
		for (int i = a.length; --i > 0;) { //first row is not modified, because L[1,1] = 1
			final int[] iRow = a[i];
			for (int j = a.length; --j >= 0; ) { //for every a[i,j]...
				int Element = iRow[j]; // == a[i,j]
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
	private static void UN_PERMUTE_AT(final int[][] a, final int[] rows) {
		for (int i=rows.length; --i >= 0;) {
			if (rows[i] != i) {
				final int[] tmp = a[i]; a[i] = a [rows[i]]; a [rows[i]] = tmp;}
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
	final static public boolean SPLIT_LU_AT(int[][] a, int[] rows) { //N3/3 Algorithm
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
	final static public boolean SPLIT_LU_AT(int[][] a, int[] rows, int length) { //N3/3 Algorithm
		//if (LU_Decomposed) return this; LU_Decomposed = true;
		MAKE_SQUARE_AT(a); //to create Space for the higher Elements in each Row.
		//int[] Rows =  new int[a.length];
		boolean Sign = false; //The Sign of the Permutation: false = 0; (-1)^0 = 1
		
		final int[] norms = new int[length]; //Contains the Max-Norm of each row
		for(int i = length;--i >= 0;) { //Store the Inverse of the Row-Max Norm for Pivoting
			norms[i] = 1 / VectorInt.MAX_VAL(a[i]); }
		for (int j = -1; ++j < length;) {
			for (int i = -1; ++i < j;) { //Process the lower Rows
				final int[] iRow = a[i];
				int sum = iRow[j];
				for (int k = -1; ++k < i;)
					sum -= iRow[k] * a[k][j];
				iRow[j] = sum;
			}
			int max = 0; //
			int  iMax = -1;
			for (int i = j; i < length; i++) { //Process the upper Rows ...
				final int[] a_i = a[i];
				int sum = a_i[j];
				for (int k = -1; ++k < j;) {
					sum -= a_i[k] * a[k][j]; }
				a_i[j] = sum; //search for the relative Pivot, normalized by the Max-Norm.
				final int dum = Math.abs(sum) * norms[i]; 
				if (max < dum) {
					max = dum; iMax = i; }
			}
			if (iMax == -1) { //Handle Singularities! 
				a[j][j]=1;
				continue; }//no Pivot!
			if (iMax != j) { //Swap the rows
				final int[] tmp = a[iMax]; a[iMax] = a[j]; a[j] = tmp;
				Sign = !Sign;
				norms[iMax] = norms[j];
			}
			rows[j] = iMax; //Don't care for Overflows anymore, using Infinity!
			//if (a[j][j] == 0) { a[j][j] = IMeasurAble.FLOAT_ACCURACY;	//not necessary, work with Infinity
			if (j < length - 1) {
				final int dum = 1 / a[j][j];
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
	final static public int[] MAP(final int[][] decompLU, final int[] rows , final int[] a) { //previously named mul()
		int[] ret = VectorInt.COPY(a);
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
	final static public int[] MAP(final int[] a, final int[][] decompLU, final int[] rows) { //previously named mul()
		int[] ret = VectorInt.COPY(a);
		MAP_AT(ret, decompLU, rows);
		return ret;
	}
			
	/**
	 * maps Vector a from right using the LU decomposed Matrix  
	 * @param decompLU LU decomposed Matrix
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 */
	final static public void MAP_AT(final int[][] decompLU, final int[] rows , final int[] a) { //previously named mul()
		//multiply with U 
		for (int i = -1; ++i < a.length; ) {
			int[] mi = decompLU[i];
			a[i] *= mi[i];
			for (int j = i; ++j < a.length;) {
				a[i] += mi[j]*a[j];
			}
		}
		//multiply with L (Diagonal = 1)
		for (int i = a.length; --i >= 0;) {
			int[] mi = decompLU[i];
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
	protected static final void UN_PERMUTE_AT(final int[] a, final int[] rows) { //previously named mul()
		//Undo the Row Permutations! permuteRowsAt(a, perm); does not work, since not a proper Permutation! 
		for (int i=rows.length; --i >= 0;) {
			if (rows[i] != i) {
				final int tmp = a[i]; a[i] = a [rows[i]]; a [rows[i]] = tmp;}
		}
	}
		
	/**
	 * maps Vector a from left using the LU decomposed Matrix  
	 * @param decompLU LU decomposed Matrix
	 * @param rows info about the Permutation (not a real Permutation)
	 * @param a Vector to map
	 */
	final static public void MAP_AT(final int[] a, final int[][] decompLU, final int[] rows) { //previously named mul()
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
	final static public int[][] SOLVE_LU_AT(final int[][] lu, final int[] rows, final int[][] b) {
		return SOLVE_LU_AT(lu, rows, lu.length, b); }
	
	/**
	 * Solves the linear equation with Matrix B by Backsubstitution after Decomposition
	 * B = A*ret <=> ret = A'*B with
	 * @param a the LU decomposed Matrix of the System.
	 * @param Rows the 'Permutation' of Rows from the LU_Decomposition
	 * @param b is replaced by the Solution in Place.
	 */
	final static public int[][] SOLVE_LU_AT(final int[][] lu, final int[] rows, final int length
	, final int[][] b) {
		int iNonZero = -1;
		for (int i = -1; ++i < length;) { //Process the upper Triangle
			final int rowI = rows[i];
			final int[] bi = b[rowI];
			if (rowI != i) {
				b[rowI] = b[i];
				b[i] = bi;
			} //Redo the Permutation
			final int[] aRow = lu[i];
			if (iNonZero >= 0) { //Optimization: start subtracting only
				for (int j = iNonZero; j < i; j++) { //from the first nonzero Element on!
					VectorInt.SUB_PROD_AT(bi, aRow[j], b[j]);
				}
			} else {
				if (!VectorInt.IS_ZERO(bi)) {
					iNonZero = i; }
			}
		}
		for (int i = length; --i >= 0;) { //Process the lower Triangle
			final int[] bi = b[i];
			final int[] ai = lu[i];
			for (int j = i; ++j < length;) {
				VectorInt.SUB_PROD_AT(bi, ai[j], b[j]); }
			VectorInt.MUL_AT(bi, 1/ai[i]);
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
	final static public int[] SOLVE_LU_AT (final int[][] lu, final int[] rows, final int[] b) {
		return SOLVE_LU_AT (lu, rows, lu.length, b); }

	/**
	 * Solves the linear equation with Vector b by Backsubstitution after Decomposition
	 * b = A*ret = L*U*ret <=> ret = A'*b with Column Vector b
	 * @param a the LU decomposed Matrix of the System.
	 * @param Rows the 'Permutation' of Rows from the LU_Decomposition
	 * @param b is replaced by the Solution in Place.
	 */
	final static public int[] SOLVE_LU_AT (final int[][] lu, final int[] rows, final int length
	, final int[] b) {
		int iNonZero = -1;
		for (int i = -1; ++i < length;) { //Process the upper Triangle
			int rowI = rows[i];
			int bi = b[rowI];
			if (rowI != i) {
				b[rowI] = b[i]; //
			} //partly redo the Permutation (see End of Loop!)
			final int[] ai = lu[i];
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
			int Sum = b[i];
			final int[] iRow = lu[i];
			for (int j = i; ++j < length;) {
				Sum -= b[j] * iRow[j]; }
			b[i] = Sum / iRow[i];
		}
		return b;
	}
	
	//////////////////////////
	//	Matrix Inversion	//
	//////////////////////////
	
	/** Matrix Inversion in Place: 1/x	 */
	final static public int[][] INV_AT(int[][] a, int[] rows) {
		return SHALLOW_COPY_AT(a, INV(a, rows)); }
	
	/** Matrix Inversion with integer Arithmetic: 1/x	 */
	final static public int[][] INV(int[][] a, int[] rows) {
		return TRP_AT(INV_TRP(a, rows)); }
	
	/** Matrix Inversion and Transposition: 1/xT	 */
	final static public int[][] INV_TRP(int[][] a, int[] rows) {
		return SOLVE_LU_AT(a, rows, ONE(a.length)); }
	
	/**Matrix Division and Transposition in Place: / arg^T
	 * Requires arg to be LU Decomposed (considered as decomposed).	 
	 */
	final static public int[][] DIV_TRP_AT(final int[][] lu, final int[] rows, final int[][] arg) {
		return SOLVE_LU_AT(lu, rows, arg); //possible both to solve the whole System with one Call or with several Calls
	}
	
	/**Matrix Division and Transposition: /= arg^T	 */
	final static public int[][] DIV_TRP(int[][] lu, int[] rows, int[][] arg) {
		return DIV_TRP_AT(COPY(lu), rows, arg); }
	
	/** Matrix Division in Place: /= arg	 */
	final static public int[][] DIV_AT(int[][] a, int arg) {
		return MUL_AT(a, 1 / arg); } //Use same Scalar Multiplication as with Polynoms and Manifolds
	
	/** Matrix Division in Place: /= arg	 */
	final static public int[][] DIV_AT(int[][] lu, int[] rows, int[] arg) {
		return DIV_AT(lu, rows, arg);
	} //(Tensor / Vector) or (Matrix / Vector):  ManiFold- Like Division of the Argument by each Item

	/** Division in Place: /= arg	 */
	final static public int[][] DIV_AT(
		final int[][] lu,
		final int[] rows,
		final int[][] arg) { //(Vector / Vector) or (Tensor / Vector) or (Tensor / Tensor)
		return TRP_AT(DIV_TRP_AT(lu, rows, arg));
	} //The Argument must not be decomposed!!!

	/**Division: /	 */
	final static public int[][] DIV(int[][] lu, int[] rows, int[][] arg) {
		return DIV_AT(COPY(lu), rows, arg);
	}

	/**Division: /	 */
	final static public int[][] DIV(int[][] lu, int[] rows, int[] arg) {
		return DIV_AT(COPY(lu), rows, arg);
	}

	/**Division: /	 */
	final static public int[][] DIV(int[][] a, int arg) {
		return DIV_AT(COPY(a), arg);
	}

	/**The Trace of a Matrix is the Sum along it's Diagonal.
	 * It stays constant with orthogonal Transformations.	 */
	final static public int TRACE(final int[][] a) { //Assume that this is a square Matrix.
		if (a.length <= 0) 
			return 0;
		int Trace = a[0][0];
		for (int i = 0; ++i < a.length;)
			if (a[i].length >= i) 
				Trace += a[i][i];
		return Trace;
	}

	/**Returns the Determinant of the (square) Matrix:
	 * The Determinant of a Matrix is the Volume of the Figure
	 * built from it's Row- or Column- Vectors.
	 * It stays constant with orthogonal Transformations.	 */
	final static public int TRACE_PROD(int[][] a) {
		if (a.length <= 0) 
			return 1;
		int Prod = a[0][0]; //saves 1 Multiplication
		for (int i = a.length; --i > 0;) 
			if (a[i].length >= i) 
				Prod *= a[i][i];
		return Prod;
	}

	/** true, when the Matrix is orthogonal, i.e. M*Mt = Mt*M = diag(a, b, c, ...).
	  * If a Matrix contains complex coefficients, it should be checked to be unitarian.
	  */
	final static public boolean IS_ORTHOGONAL(final int[][] a) { //The Optimization here is that you have to 
		for(int i = a.length; --i >= 0;) {
			final int[] ai = a[i];
			final int ai0 = ai[0];
			for (int j = i; --j >= 0; ) { //test only one Triangle
				final int[] aj = a[j];
				if (! ByRefFloat.IS_ZERO(VectorInt.MAP(ai, aj), ai0+aj[0])) { //because the Product is symmetric.
					//Use an Epsilon here that corresponds to any Matrix Norm
					return false;
				}
			}
		}
		return true;
	}

	/**true, when the Matrix is unitarian resp. orthonormal, i.e. M*Mt = Mt*M = 1.
	 * unitarian is the complex equivalent to orthonormal 	 */
	final static public boolean IS_UNITARIAN(int[][] a) { //The Optimization here is that you have to test only one Triangle
		//because the Product is symmetric.
		if (!IS_ORTHOGONAL(a))
			return false;
		for (int i = a.length; --i >= 0; ) {
			if (!ByRefFloat.EQUALS(1, VectorInt.MAP(a[i], a[i]))) {
				return false;
			}
		}
		return true;
	}

	//	final static public boolean  orthoNorm();  == unitaer fuer reelle Matrizen;

	/**true, when the Matrix is hermitean resp. symmetric, i.e. M = Mt.	 */
	final static public boolean IS_HERMITEAN(int[][] a) { //The Optimization here is that you have to test only one Triangle
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
	final static public boolean IS_ANTI_HERMITEAN(int[][] a) { //The Optimization here is that you have to test only one Triangle
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
	final static public boolean IS_NORMAL(final int[][] a) { //The Optimization here is that you have to test only one Triangle
		//because the Product is symmetric.
		final int[][] Trp = TRP(a);
		for (int i = a.length; --i >= 0;) {
			for (int j = i+1; --j >= 0; ) {
				if (!ByRefFloat.EQUALS(
					VectorInt.MAP(a[i], a[j]), 
					VectorInt.MAP(Trp[i], Trp[j]))) {
					return false;
				}
			}
		}
		return true;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**Swaps the Columns of this Tensor in Place	 */
	final static public int[][] SWAP_COLS(final int[][] a, final int Dim1, final int Dim2) {
		return SWAP_COLS_AT(COPY(a), Dim1, Dim2);
	}

	/** Swaps the Columns and Rows of this Tensor in Place	 */
	final static public int[][] SWAP_COLS_ROWS_AT(final int[][] a, final int dim1, final int dim2) {
		final int[][] ret = SWAP_ROWS_AT(a, dim1, dim2);
		return SWAP_COLS_AT(ret, dim1, dim2);
	}

	/**Swaps the Columns of this Tensor in Place	 */
	final static public int[][] SWAP_COLS_AT(final int[][] a, final int dim1, final int dim2) {
		if (dim1 == dim2) 
			return a; 
		for (int i = a.length; --i >= 0;) {
			VectorInt.SWAP_AT(a[i], dim1, dim2); }
		return a;
	}

	/**Swaps the Rows of this Tensor in Place	 */
	final static public int[][] SWAP_ROWS(final int[][] a, final int dim1, final int dim2) {
		return SWAP_ROWS_AT(COPY(a), dim1, dim2); }

	/**Swaps the Rows of this Tensor in Place	 */
	final static public int[][] SWAP_ROWS_AT(final int[][] a, final int dim1, final int dim2) {
		final int[] swap = a[dim1]; a[dim1] = a[dim2]; a[dim2] = swap;
		return a;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods
	///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(final int[][] vals, final PrintStream stream, final char separator) {
		STREAM(vals, stream, 0, vals.length, separator);
	}
	
	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(final int[][] vals, final PrintStream stream) {
		STREAM(vals, stream, 0, vals.length, VectorInt.DEFAULT_SEPARATOR);
	}
	
	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(final int[][] vals) {
		STREAM(vals, System.out, 0, vals.length, VectorInt.DEFAULT_SEPARATOR);
	}
		
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(
		final int[][] vals,
		final PrintStream stream,
		int startRow,
		int stopRow, final char separator) { //, int startCol, int stopCol) {
		//if (startRow >= stopRow) {
		//	return; }
		//VectorInt.stream(vals[startRow], stream);
		for (int i = startRow; ++i < stopRow;) {
			VectorInt.STREAM(vals[i], stream, separator);
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
	final static public void STREAM(final int[][] d, final OutputStream ps, final NumberFormatter formatter, final String colSep) throws IOException {
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
	final static public void STREAM(final int[][] d, final Writer pw, final NumberFormatter formatter, final String colSep) throws IOException {
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
	final static public void STREAM(final int[][] d, final Writer pw, final NumberFormatter formatter, final String colSep, final String rowSep) throws IOException {
		for (int i = -1; ++i < d.length;) {
			VectorInt.STREAM(d[i], pw, formatter, colSep);
			pw.write(rowSep); 
		}
		//return pw; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Backing Value Array for the int[][]	 */
	protected int[][] items;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param original Returns the internal Structure by Reference! 
	 * Should usually be false(Default), except when it is guaranteed, 
	 * that the Array will be used Read-Only.  
	 * @see #toArray() 
	 * @return the Items	 */
	public int[][] getItems(final int depth) {
		if (depth == 0) 
			return items; 
		return COPY(this.items, this.itemCount, depth > 1); } //, this.itemCount); } 
	
	/** @return a Copy of the internal List 	 */
	public int[][] getItems() { return getItems(0); }
	
	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized int[] getVectorAt(final int index) {
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
	public int[] setAt(final int index, final int[] value) {
		int[] ret = null; 
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
		return setAt(index, (int[]) value); 
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
	public void setAt(final int[][] value) {
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
	public MatrixInt(int initialCapacity, int capacityIncrement_) {
		items = new int[initialCapacity][];
		capacityIncrement = capacityIncrement_;
		//		mEnum = new ArrayEnum(Items, ItemCount);
		//		mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty MatrixInt with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the MatrixInt.	 */
	public MatrixInt(final int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty MatrixInt.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public MatrixInt() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/** Constructs an empty MatrixDouble.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public MatrixInt(final int[][] a, boolean copy) {
		if (copy) {
			items = new int[a.length][];
			copyAt(a);
		} else {
			items = a;
		}
	}
	
	/** Constructs an MatrixInt by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public MatrixInt(final Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an MatrixInt from the given Object.	  */
	public MatrixInt(final Object arg, final int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an MatrixInt from the given Object.	  */
	public MatrixInt(final int[][] arg, final int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an MatrixInt from the given Object
	  * and copies the Elements into this MatrixInt.	  */
	public MatrixInt(final int[][] arg) {
		this(arg.length, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public MatrixInt addItem(final int[] item) {
		setAt(itemCount, item);
		return this;
	}

	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
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

	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.	 */
	final public synchronized int[] toArray() {
		int[] Return = new int[itemCount];
		System.arraycopy(items, 0, Return, 0, itemCount);
		return Return;
	}

	/**Trims the capacity of this VectorInt to be the VectorInt's current
	 * size. An application can use this operation to minimize the
	 * storage of a VectorInt.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			int[][] oldData = items;
			items = new int[itemCount][];
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
	}

	/**Returns the current capacity of this VectorInt.
	 *
	 * @return  the current capacity of this VectorInt.	 */
	final public int getCapacity() {
		return items.length;
	}

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
		if (arg instanceof MatrixInt) {
			return equals((MatrixInt) arg); 
		}
		return false; 
	}
	
	/** @see Object#equals(java.lang.Object)	 */
	public boolean equals(final int[][] arg) {
		return EQUALS(items, itemCount, arg, arg.length);
	}
	
	/** @see Object#equals(java.lang.Object)	 */
	public boolean equals(final MatrixInt arg) {
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
	public MatrixInt copyAt(final int[][] arg_) {
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
	public MatrixInt copyAt(final double[][] arg_) {
		itemCount = arg_.length; 
		for (int i = itemCount; --i >= 0;) {
			items[i] = VectorInt.COPY(arg_[i]);
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
		if (arg instanceof MatrixInt) {
			MatrixInt arg_ = (MatrixInt) arg;
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
		if (arg instanceof MatrixInt) {
			MatrixInt arg_ = (MatrixInt) arg;
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
		return new MatrixInt(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Arithmetic Methods for Arrays
	////////////////////////////////////////////////////////////////////////////////

	/** Normalizes this Vector by bringing it into the canonical Form
	 * so that getAt(getInt()) != 0 
	 */
	public MatrixInt normalizeAt() {
		while (items[--itemCount] == null);
		++itemCount;
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixInt addItem(final VectorInt vector, final boolean original) {
		return addItem(vector.getInts(original));
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixInt addAt(final VectorInt vector) {
		return addAt(vector.getInts(true), 0, vector.getInt());
	}

	/** subtracts the given Portion of the values from this Vector */
	public MatrixInt subAt(final VectorInt vector) {
		return subAt(vector.getInts(true), 0, vector.getInt());
	}

	/** @return the Minimum and Maximum Values of each Column... 
	 * too complex to optimize for now... 
	 * 
	 * Use Min and Max separately, which is clearer too!
	 */
	//	final static public int[][] MinMax(int[][] arr) { }

	/**
	 * @return the Minimum Values of each Column 
	 */
	public int[] Min(final int[] ret) {
		return MIN(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	public int[] Min() {
		return MIN(items, 0, itemCount);
	}

	/** @return the Maximum Values of each Column */
	public int[] Max(final int[] ret) {
		return MAX(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	public int[] Max() {
		return MAX(items, 0, itemCount);
	}

	/** subtracts the given Portion of the values from this Vector */
	public MatrixInt negAt() {
		NEG_AT(items);
		return this; 
	}
	
	/** subtracts the given Portion of the values from this Vector */
	public MatrixInt trpAt() {
		TRP_AT(items);
		return this; 
	}
	
	/** subtracts the given Portion of the values from this Vector */
	public MatrixInt subAt(final int[] values, final int start, final int stop) {
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
	public MatrixInt addAt(final int value) {
		MatrixInt.ADD_AT(items, value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixInt subAt(final int value) {
		MatrixInt.ADD_AT(items, -value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixInt addAt(final int[] values, int start, int stop) {
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

	/** @return the Extent of the Polygon
	 * i.e. the Minimum and Maximum Values of each Column in two Vectors 	 */
	public int[][] getExtent() { return EXTENT(null, this.items, 0, this.itemCount); } 

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
	final public MatrixInt read(final ResultSet rs)
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
	final public MatrixInt read(final ResultSet rs, final int columnOffset)
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
	final public MatrixInt read(final ResultSet rs, int maxNumPlanes, 
			final int columnOffset, int lastCol) throws SQLException {
		final VectorInt vector = new VectorInt(10); //TODO: hardcoded Capacity
		while (--maxNumPlanes >= 0) { //
			final VectorInt row; 
			if (null == (row = vector.read(rs, columnOffset, lastCol))) {
				break; }
			this.addItem(row, false); 
		}
		return this;
	}

}
