/*
 * File Name: MatrixShort.java
 * Created on: 26.12.2003
 *
 */
package graphic.mvc.plane2D;

import java.io.PrintStream;
import java.util.Arrays;

import math.vector.AVector;
import math.vector.VectorShort;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import function.IIOrderAble;
import graphic.IGraphText;
import graphic.IPalette;
import graphic.Point2D;
import graphic.mvc.IPainter;

/**
 * Title: MatrixShort<p>
 * Description:
 * Structure (Polygon) to hold a dynamically growable Set of short[][] 
 * Point Objects with integer Coordinates and arbitrary Dimension 
 * (e.g. x,y,z Coordinates, Colors, Normals etc. )
 * 
 * Dynamic Array for holding short[][] Arrays usually used as Polygons. 
 * The short Type is large enough even for large Graphics Contexts 
 * and still saves Space compared to an int which is completely oversized! 
 *
 * Since the short[][] can contain arbitrary many Points 
 * and the Points can contain arbitrary many Coordinates 
 * (x,y,z,u,v,color(r,g,b),bone,Line- and/or Point-Size, etc.)
 * this Schema is most extensible. 
 * 
 * @see math.MatrixDouble which also holds a Structure similar to short[][], 
 * but is used for Matrix Calculations, not for holding Polygon Info.
 * @see graphic.mvc.plane2D.VectorPolygon which holds a short[][][] 
 * modeled as a growable Vector of MatrixShort 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:49:27Z
 * digest: 8896c4bbc3d18a82bc0b7adb2b093106e526612bc600a65cd1fb0e545678a362
 * stale: false
 * tags: [code/matrix_operations, code/polygon_matrix]
 * concepts: [Growable short[][] Polygon Matrix]
 * facets: {layer: utility, status: broken, complexity: high}
 * -->
 */
public class MatrixShort 
extends AVector 
implements Comparable, IIOrderAble, IPainter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** streamIO for logging the Progress of Convergence */
	public static Log L = new Log(MatrixShort.class, 0); 

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the double Area of the Triangle defined by the first three Points.	 */
	final static public int AREA(final short[][] points) {
		if (points == null) 
			return 0; //also works for Lines and Points! 
		if (points.length < 3) 
			return 0; //also works for Lines and Points! 
		return //dx2*dy1-dx1*dy2
		((points[2][1] - points[0][1]) * (points[1][0] - points[0][0])) - 
		((points[2][0] - points[0][0]) * (points[1][1] - points[0][1]));
	}

	/** Allocates a new list of the given minimum size, with every row filled to the given
	 * dimension.
	 * @return a filled List of the given minimum Size
	 *
	 * @param list
	 * @param newSize
	 * @return
	 */
	final static public short[][] GET_FILLED_ARRAY(final int minSize, final int dim) {
		return GET_FILLED_ARRAY(minSize, null, dim);
	}

	/** Reuses the given list when it already has at least the minimum size, otherwise grows
	 * it into a new array, filling every added row to the given dimension.
	 * @return a filled List of the given minimum Size
	 *
	 * @param list
	 * @param newSize
	 * @return
	 */
	final static public short[][] GET_FILLED_ARRAY(int minSize, final short[][] list, final int dim) {
		int listLength = 0;
		if (list != null) {
			if (minSize <= (listLength = list.length)) {
				return list;
			}
		}
		short[][] ret = new short[minSize][];
		if (list != null) {
			System.arraycopy(list, 0, ret, 0, list.length);
		}
		while (--minSize >= listLength) {
			ret[minSize] = new short[dim];
		}
		return ret;
	}

	/**Used to identify the clicked Object.
	 * called on the Mouse Button down Event 
	 * 
	 * @param x Coordinate of the Point to search for
	 * @param y Coordinate of the Point to search for
	 * @param points List of short[] Objects to search in
	 * @param maxDist maximum Distance in Maximum Norm
	 * @return the Index of the Last Point in this List which is closer than this maximum Distance
	 */
	final static public int findIndexOfLastNeighbour(final short[] position, final short[][] points, final int maxDist, final int dim) {
		return findIndexOfLastNeighbour(position, points, maxDist, points.length, dim);
	}

	/**Used to identify the clicked Object.
	 * called on the Mouse Button down Event 
	 * 
	 * @param x Coordinate of the Point to search for
	 * @param y Coordinate of the Point to search for
	 * @param points List of short[] Objects to search in
	 * @param maxDist maximum Distance in Maximum Norm
	 * @param last Index to start searching from, not included!
	 * @return the Index of the Last Point in this List which is closer than this maximum Distance
	 */
	final static public int findIndexOfLastNeighbour(final short[] position, final short[][] points, final int maxDist, final int last, final int dim) {
		L.n("Searching for last Neighbor of(").l(position).l(")"); 
		for (int i = last; --i >= 0;) {
			short[] point = points[i];
			if (point != null) {
				L.n("poshort[").l(i).l("]=").l(point); 
				if (VectorShort.AbsVDist(point, position) < maxDist) {
					L.n("found Neighbor:").l(i); 
					return i;
				}
			}
		}
		L.n("found NO Neighbor!"); 
		return -1;
	}

	/** @return the Minimum and Maximum Values of each Column... 
	 * too complex to optimize for now... 
	 * 
	 * Use Min and Max separately, which is clearer too!
	 */
	//	final static public short[][] MinMax(short[][] arr) { }

	/**
	 * Computes the column-wise minimum over the whole array into ret.
	 * @return the Minimum Values of each Column
	 */
	final static public short[] MIN_AT(final short[] ret, final short[][] arg) {
		return MIN_AT(ret, arg, 0, arg.length);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	final static public short[] MIN(final short[][] arg, final int start, final int stop) {
		//return Min(fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		//Optimization: 
		if (stop <= 0) {
			return null; }
		int i = stop;
		final short[] ret = VectorShort.COPY(arg[--i]);
		return MIN_AT(ret, arg, start, i);
	}

	/** Computes the column-wise minimum over the given row range into ret. */
	final static public short[] MIN_AT(final short[] ret, short[][] arg, int start, int stop) {
		for (; --stop >= start;) {
			VectorShort.MinAt(ret, arg[stop]);
		}
		return ret;
	}

	/** Computes the column-wise maximum over the whole array into ret.
	 * @return the Maximum Values of each Column */
	final static public short[] MAX(final short[] ret, final short[][] arg) {
		for (int i = arg.length; --i >= 0;) {
			VectorShort.MaxAt(arg[i], ret);
		}
		return ret;
	}

	/**
	 * Computes the column-wise maximum over the given row range.
	 * Since it is not possible to get the Dimensionality of an empty Matrix,
	 * the Optimization is implemented not to start with +Infinity.
	 * @return the Maximum Values of each Column
	 */
	final static public short[] MAX(final short[][] arg, final int start, final int stop) {
		//return Min(fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		//Optimization: 
		int i = stop;
		if (stop <= 0) {
			return null;
		}
		final short[] ret = VectorShort.COPY(arg[--i]);
		return MAX_AT(ret, arg, start, i); 
	}

	private static short[] MAX_AT(final short[] ret, final short[][] arg, final int start, int stop) {
		for (; --stop >= start;) {
			VectorShort.MaxAt(ret, arg[stop]);
		}
		return ret;
	}

	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public short[][] ADD_AT(final short[][] ret, final int Increment) {
		return ADD_AT(ret, Increment, 0, ret.length);
	}

	/**
	  * Adds the given scalar increment in place to every row in the given range.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[][] ADD_AT(final short[][] ret, final int Increment, final int start, int stop) {
		while (--stop >= start) 
			VectorShort.ADD_AT(ret[stop], Increment);
		return ret;
	}

	/**
	  * Adds the given vector increment in place to every row of the array.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  */
	final static public short[][] ADD_AT(final short[][] ret, final short[] increment) {
		return ADD_AT(ret, increment, 0, ret.length); }

	/**
	  * Adds the given vector increment in place to every non-null row in the given range.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[][] ADD_AT(final short[][] ret, final short[] increment, final int start, int stop) {
		while (--stop >= start) {
			final short[] point = ret[stop]; 
			if (point == null) { //assume 0 == null
				continue; }
			VectorShort.ADD_AT(point, increment);
		}
		return ret;
	}

	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public short[][] SUB_AT(short[][] ret, short[] Decrement) {
		return SUB_AT(ret, Decrement, 0, ret.length);
	}

	/**
	  * Subtracts the given vector decrement in place from every non-null row in the given range.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[][] SUB_AT(final short[][] ret, final short[] Decrement, final int start, int stop) {
		while (--stop >= start) {
			final short[] point = ret[stop]; 
			if (point == null) { //assume 0 == null
				continue; }
			VectorShort.subAt(point, Decrement);
		}
		return ret;
	}

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	final static public short[][] RESIZE(final short[][] arr, int newRows) {
		short[][] ret = new short[newRows][];
		if (newRows > arr.length) {
			newRows = arr.length;
		}
		while (--newRows >= 0) {
			ret[newRows] = VectorShort.COPY(arr[newRows]);
		}
		return ret;
	}

	/**
	 * Sums every row of the array into a freshly allocated result vector.
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public short[] ROW_SUM(final short[][] arr) {
		return ROW_SUM(new short[arr.length], arr, 0, arr.length);
	}

	/**
	 * Sums every row of the array into the given result vector.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public short[] ROW_SUM(final short[] ret, final short[][] arr) {
		return ROW_SUM(ret, arr, 0, arr.length);
	}

	/**
	 * Sums each row in the given range of the array into the corresponding slot of ret.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public short[] ROW_SUM(final short[] ret, final short[][] arr, final int Start, int Stop) {
		while (--Stop >= Start) 
			ret[Stop] = (short) VectorShort.Sum(arr[Stop]);
		return ret;
	}

	/** Sums each column across all rows into a freshly allocated result vector.
	 * @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public short[] COL_SUM(final short[][] arr) {
		return COL_SUM(new short[arr[0].length], arr, 0, arr.length);
	}

	/**
	 * Sums each column across all rows into the given result vector.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public short[] COL_SUM(final short[] ret, final short[][] arr) {
		return COL_SUM(ret, arr, 0, arr.length);
	}

	/**
	 * Sums a single column across all rows.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public int COL_SUM(final short[][] arr, final int col) {
		if (arr == null)
			return 0;
		return COL_SUM(arr, 0, arr.length, col);
	}

	/**
	 * Sums each column across the given row range into the given result vector.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public short[] COL_SUM(final short[] ret, final short[][] arr, final int startRow, int stopRow) {
		while (--stopRow >= startRow)
			VectorShort.ADD_AT(ret, arr[stopRow]);
		return ret;
	}

	/**
	 * Sums a single column across the given row range.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public int COL_SUM(final short[][] arr, final int startRow, int stopRow, final int col) {
		int sum = 0;
		while (--stopRow >= startRow) 
			sum += arr[stopRow][col];
		return sum;
	}

	/** Computes each column's maximum across all rows into a freshly allocated result vector.
	 * @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public short[] COL_MAX(final short[][] arr) {
		return COL_MAX(VectorShort.fillAt(new short[arr[0].length], Short.MIN_VALUE), arr, 0, arr.length);
	}

	/**
	 * Computes each column's maximum across all rows into the given result vector.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public short[] COL_MAX(final short[] ret, final short[][] arr) {
		return COL_MAX(ret, arr, 0, arr.length);
	}

	/**
	 * Computes each column's maximum across the given row range into the given result vector.
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public short[] COL_MAX(final short[] ret, final short[][] arr, final int startRow, int stopRow) {
		while (--stopRow >= startRow) {
			VectorShort.MaxAt(ret, arr[stopRow]);
		}
		return ret;
	}

	/** Copies row references (not their contents) from arr into ret.
	 * @return a shallow Copy of the given Array */
	final static public short[][] SHALLOW_COPY_AT(short[][] ret, short[][] arr) {
		if (ret.length != arr.length) 
			throw new IndexOutOfBoundsException("Expected: " + ret.length + " Actual: " + arr.length);
		System.arraycopy(arr, 0, ret, 0, arr.length);
		//while (--len >= 0) {
		//	ret[len] = arr[len];
		//}
		return ret;
	}

	/** Deep-copies each row in the given range from arr into ret.
	 * @return a deep Copy of the given Matrix */
	final static public short[][] COPY_AT(final short[][] ret, final short[][] arr, final int Start, int Stop) {
		while (--Stop >= Start) 
			VectorShort.COPY_AT(ret[Stop], arr[Stop]); //Optimization!
		return ret;
	}

	/** Copies the given single row's values into every row of ret in the given range.
	 * @return the Matrix ret with deep Copie of the given Vector arr in every Row */
	final static public short[][] COPY_AT(final short[][] ret, final short[] arr, final int Start, int Stop) {
		while (--Stop >= Start) 
			VectorShort.COPY_AT(ret[Stop], arr); //Optimization!
		return ret;
	}

	/** Deep-copies every row of the given array into a newly allocated matrix.
	 * @return a deep Copy of the given Array */
	final static public short[][] COPY(final short[][] arr) {
		int len = arr.length;
		final short[][] ret = new short[len][];
		while (--len >= 0) 
			ret[len] = VectorShort.COPY(arr[len]);
		return ret;
	}

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public short[][] SET_DIM_AT(final short[][] a, final int dim) {
		if (a.length == dim)
			return a;
		short[][] ret = new short[dim][];
		System.arraycopy(a, 0, ret, 0, a.length);
		//Arrays.fill(ret, a.length, dim, null);
		// TODO: LOGIC: returns the original array 'a' instead of the resized 'ret' that was
		// just allocated and filled above; every caller expecting a length-'dim' array back
		// (dim != a.length) silently gets the unchanged original-length array instead.
		return a;
	}

	/** Multiplies every row of the array in place by the given factor.
	 * @return the given Array multiplied in Place by the given Factor */
	public static short[][] MUL_AT(short[][] ret, int Factor) {
		return MUL_AT(ret, Factor, 0, ret.length);
	}

	/** Multiplies each row in the given range in place by the given factor.
	 * @return the given Array multiplied in Place by the given Factor */
	public static short[][] MUL_AT(short[][] ret, int Factor, int Start, int Stop) {
		while (--Stop >= Start) 
			VectorShort.MUL_AT(ret[Stop], Factor);
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for fuzzy Set and Matrix Operations
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Matrix Operations (symmetric/antisymmetric)
	////////////////////////////////////////////////////////////////////////////////

	/**
	  * Negates every value in the given row range in place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start1 Index from  where the outer Array is processed
	  * @param stop1  Index up to where the outer Array is processed (not ret[stop]!)
	  * @param start2 Index from  where the inner Array is processed
	  * @param stop2  Index up to where the inner Array is processed (not ret[stop]!)
	  */
	final static public short[][] NEG_AT(short[][] ret, int start1, int stop1) {
		while (--stop1 >= start1) 
			VectorShort.NegAt(ret[stop1]);
		return ret;
	}

	/**
	  * Replaces every value in the array in place with its absolute value.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[][] ABSV_AT(short[][] ret) {
		if (ret.length <= 0) 
			return ret;
		return ABSV_AT(ret, 0, ret.length);
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
	final static public short[][] ABSV_AT(short[][] ret, int start1, int stop1) {
		while (--stop1 >= start1) 
			VectorShort.ABSV_AT(ret[stop1]);
		return ret;
	}

	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public short[][] PERMUTE_ROWS(short[][] a, short[] index) {
		return PERMUTE_ROWS(new short[a.length][], a, index);
	}

	/** Reorders rows from a into ret according to the given index permutation.
	 * @return this Vector with the Rows permuted according to the given Permutation     */
	final static public short[][] PERMUTE_ROWS(short[][] ret, short[][] a, short[] index) {
		for(int i = index.length; --i >= 0;) 
			ret[i] = a[index[i]];
		return ret;
	}

	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public short[][] PERMUTE_ROWS_AT(short[][] a, short[] index) {
		short[][] tmp = new short[a.length][];
		PERMUTE_ROWS(tmp, a, index);
		System.arraycopy(tmp, 0, a, 0, a.length);
		return a;
	}

	/**
	 * Finds the maximum element of every row into a freshly allocated result vector.
	 * @return Maximum Value of the every Row in the Array.
	 */
	final static public short[] MAX_VAL(short[][] arr) {
		short[] ret = new short[arr.length];
		Arrays.fill(ret, Short.MIN_VALUE);
		return MAX_VAL(ret, arr, 0, arr.length);
	}

	/**
	 * Finds the maximum element of every row into the given result vector.
	 * @return Maximum Value of the every Row in the Array.
	 */
	final static public short[] MAX_VAL(short[] ret, short[][] arr) {
		return MAX_VAL(ret, arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public short[] MAX_VAL(short[] ret, short[][] arr, int startRow, int stopRow) {
		while (--stopRow >= startRow) 
			ret[stopRow] = VectorShort.MAX_VAL(arr[stopRow]);
		return ret;
	}

	/**Swaps the Columns of this Tensor in Place	 */
	final static public short[][] SWAP_COLS(short[][] a, final int dim1, final int dim2) {
		return SWAP_COLS_AT(COPY(a), dim1, dim2);
	}

	/**Swaps the Columns of this Tensor in Place	 */
	final static public short[][] SWAP_COLS_AT(final short[][] a, final int dim1, final int dim2) {
		for (int i = a.length; --i >= 0;) 
			VectorShort.SWAP_COLS_AT(a[i], dim1, dim2); 
		return a;
	}

	/**Swaps the Rows of this Tensor in Place	 */
	final static public short[][] SWAP_ROWS(final short[][] a, final int dim1, final int dim2) {
		return SWAP_ROWS_AT(COPY(a), dim1, dim2);
	}

	/**Swaps the Rows of this Tensor in Place	 */
	final static public short[][] SWAP_ROWS_AT(final short[][] a, final int dim1, final int dim2) {
		short[] swap = a[dim1]; a[dim1] = a[dim2]; a[dim2] = swap;
		return a;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods
	///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Streams out the complete given Array. 
	 */
	final static public void STREAM(short[][] vals, PrintStream stream) {
		STREAM(vals, stream, 0, vals.length);
	}

	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(
		short[][] vals,
		PrintStream stream,
		int startRow,
		int stopRow) { //, int startCol, int stopCol) {
		//		if (startRow >= stopRow) {
		//			return; }
		//		stream(vals[startRow], stream);
		// TODO: LOGIC: the loop pre-increments i before the bound check, so vals[startRow]
		// itself is never printed - only rows startRow+1..stopRow-1 are streamed out.
		for (int i = startRow; ++i < stopRow;) {
			stream.println(vals[i]);
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** used only for Debugging 	 */	
	//public int numPlane; 
	
	/** Reference to the Palette used 	 */	
	public IPalette palette; 
	
	/** Flag whether this Polygon is oriented 	*/
	public boolean oriented = true;  

	/** Backing Value Array for the float[]	 */
	private short[][] items;

	/** Column for the Sum of Columns in @see #colSum	*/
	private short column = 2; 

	/** Cache for the Sum of the Columns in the given @see #column	*/
	private int colSum = Short.MIN_VALUE;  

	/** Returns the cached sum of {@link #column}, computing and caching it first if invalid.
	 * @return the Sum of the Columns in the given @see #column	*/
	public int getColSum() {
		if (colSum == Short.MIN_VALUE) 
			colSum = COL_SUM(items, column); 
		return colSum; 
	}

	/** set the internal Constants to uninitialized Value	*/
	public void setInvalid() {
		colSum = Short.MIN_VALUE;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the row at the given index as an Object.
	 * @see math.AVector#getAt(int)	 */
	public Object getAt(final int i) { return getArrayAt(i); }

	/**
	 * Exposes the backing row array directly for external modification.
	 * @return the internal List to modify it externally
	 */
	public short[][] getList() { return items; }

	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized short[] getArrayAt(final int index) {
		if (indexInRange(index)) 
			return items[index];
		return null;
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
	public short[] setAt(final int index, final short[] value) {
		L.l("setAt[").l(index).l("]=(").l(value).l(")");
		short[] ret = null; 
		if (indexInRange(index)) 
			ret = items[index]; 
		else {
			if (value == null) 
				return   null; //save enlarging!
			setSize(index+1);
		}
		//could also reuse the given Value instead of copying
		items[index] = value; //VectorShort.COPY_AT(items[index], value); //
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
		return setAt(index, (short[]) value); 
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
	public void setAt(final short[][] value) {
		L.l("setAt(").l(value).l(")");
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

	/**Constructs an empty VectorShort with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the VectorShort.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the VectorShort overflows.	 */
	public MatrixShort(final int initialCapacity, final int capacityIncrement_, final int dim) {
		super();
		items = GET_FILLED_ARRAY(initialCapacity, dim); 
		capacityIncrement = capacityIncrement_;
		//mEnum = new ArrayEnum(Items, ItemCount);
		//mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty MatrixShort with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the MatrixShort.	 */
	public MatrixShort(final int initialCapacity, final int dim) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR, dim);
	}

	/** Constructs an empty MatrixShort.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public MatrixShort(final int dim) {
		this(DEFAULT_CAPACITY_INIT, dim);
	}

	/** Constructs an MatrixShort by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public MatrixShort(final Object arg) {
		// TODO: LOGIC: resolves to MatrixShort(int initialCapacity, int dim) - so
		// DEFAULT_CAPACITY_INCR is passed as the row dimension 'dim', not as a capacity
		// increment, silently giving every row the wrong width.
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Very fast Constructor reusing the given Array
	 * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public MatrixShort(final short[][] arg, final boolean copy, final boolean oriented_) {
		if (copy) {
			items = new short[arg.length][];
			copyAt(arg);
		} else {
			items = arg; 
		}
		this.oriented = oriented_;
	}

	/** Constructs an MatrixShort from the given Object.	  */
	public MatrixShort(final Object arg, final int capacityIncrement_, final int dim) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_, dim);
		copyAt(arg);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public MatrixShort addItem(final short[] item) {
		setAt(itemCount, item);
		return this;
	}

	/**Copies the components of this VectorShort into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorShort.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	final public synchronized void copyInto(short[] anArray) {
		System.arraycopy(items, 0, anArray, 0, itemCount);
		/*		int i = ItemCount;
				Object elementDataLocal[] = this.Items;
				while (i-- > 0)
					anArray[i] = elementDataLocal[i];
		*/
	}

	/**Copies the components of this VectorShort into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorShort.
	 *
	 * @param   anArray   the array into which the components get copied.	 */
	final public synchronized short[] toArray() {
		short[] Return = new short[itemCount];
		System.arraycopy(items, 0, Return, 0, itemCount);
		return Return;
	}

	/**Trims the capacity of this VectorShort to be the VectorShort's current
	 * size. An application can use this operation to minimize the
	 * storage of a VectorShort.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			short[][] oldData = items;
			items = new short[itemCount][];
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
	}

	/**Returns the current capacity of this VectorShort.
	 *
	 * @return  the current capacity of this VectorShort.	 */
	final public int getCapacity() {
		return items.length;
	}

	/**Increases the capacity of this VectorShort, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final public synchronized int setCapacity(final int minCapacity) {
		items = GET_FILLED_ARRAY(minCapacity, items, items[0].length); 
		return items.length;
	}

	/** Compares this matrix's column sum to another's after casting the argument.
	 * @see function.IIOrderAble#isLessThan(java.lang.Object)	 */
	public boolean isLessThan(final Object arg) {
		return less((MatrixShort) arg);
	}

	/** Reports whether this matrix's cached column sum is less than the given one's.
	 * @see function.IIOrderAble#isLessThan(java.lang.Object)	 */
	public boolean less(final MatrixShort arg) {
		return getColSum() < arg.getColSum();
	}

	/** Compares this matrix to another after casting the argument.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)	 */
	public int compareTo(final Object o) {
		return compareTo((MatrixShort) o);
	}

	/** Orders matrices by their cached column sum.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)	 */
	public int compareTo(final MatrixShort o) {
		final int cs1 = getColSum();
		final int cs2 = o.getColSum();
		if (cs1 > cs2) {
			return 1; }
		if (cs1 < cs2) {
			return -1; }
		return 0;
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Interface ICopyAble
	////////////////////////////////////////////////////////////////////////////////

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 *
	 * The Optimization here is that the Capacity can be ensured before
	 * and that additional Fields can be set.	 */
	public MatrixShort copyAt(final short[] arg_) {
		for(int i = itemCount; --i >= 0; ) {
			VectorShort.COPY_AT(items[i], arg_); 
		}
//		System.arraycopy(arg_, 0, items, 0, itemCount);
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
	public MatrixShort copyAt(final short[][] arg_) {
		int i = itemCount; 
		if (i > arg_.length) {
			i = arg_.length; 
		}
		for(; --i >= 0; ) {
			VectorShort.COPY_AT(items[i], arg_[i]); 
		}
//		System.arraycopy(arg_, 0, items, 0, itemCount);
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
		if (arg instanceof MatrixShort) {
			MatrixShort arg_ = (MatrixShort) arg;
			capacityIncrement = arg_.capacityIncrement;
			setCapacity(arg_.itemCount);
			itemCount = arg_.itemCount;
			copyAt(arg_.items);
		} else
			super.copyAt(arg); //no need to use a recursive DeepCopy like with Tensor
		return this;
	}

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) {
		if (arg instanceof MatrixShort) {
			MatrixShort arg_ = (MatrixShort) arg;
			capacityIncrement = arg_.capacityIncrement;
			itemCount = arg_.itemCount;
			items = arg_.items;
		} else
			super.copyAt(arg);
		return this;
	}

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		// TODO: LOGIC: resolves to MatrixShort(int initialCapacity, int dim) - so the
		// existing capacityIncrement value is passed as the row dimension 'dim' of the new
		// instance rather than as a capacity increment, silently giving it the wrong row width.
		return new MatrixShort(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Arithmetic Methods for Arrays
	////////////////////////////////////////////////////////////////////////////////

	/** Normalizes this Vector by bringing it into the canonical Form
	 * so that getAt(getInt()) != 0 
	 */
	public MatrixShort normalizeAt() {
		// TODO: LOGIC: when itemCount is already 0 (or every item is null), this decrements
		// itemCount past 0 to -1 and indexes items[-1], throwing
		// ArrayIndexOutOfBoundsException instead of leaving an empty matrix normalized.
		while (items[--itemCount] == null);
		++itemCount;
		return this;
	}

	/** @return the Minimum and Maximum Values of each Column... 
	 * too complex to optimize for now... 
	 * 
	 * Use Min and Max separately, which is clearer too!
	 */
	//	final static public short[][] MinMax(short[][] arr) { }

	/**
	 * Computes the column-wise minimum over this matrix's own rows into ret.
	 * @return the Minimum Values of each Column
	 */
	public short[] Min(final short[] ret) {
		return MIN_AT(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	public short[] Min() {
		return MIN(items, 0, itemCount);
	}

	/** Computes the column-wise maximum over this matrix's own rows into ret.
	 * @return the Maximum Values of each Column */
	public short[] Max(final short[] ret) {
		return MAX(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	public short[] Max() {
		return MAX(items, 0, itemCount);
	}

	/** subtracts the given Portion of the values from this Vector */
	public MatrixShort subAt(final short[] values, int start, int stop) {
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
	public MatrixShort addAt(final short[] vector) {
		return addAt(vector, 0, itemCount); //Recursion!
	}

	/** subtracts the given Portion of the values from this Vector */
	public MatrixShort subAt(final short[] vector) {
		return subAt(vector, 0, itemCount);
	}

	/** subtracts the given Portion of the values from this Vector
	 * can also be cached...
	 */
	public int area() { return AREA(items);	}

	/** adds the given Portion of the values to this Vector */
	public MatrixShort addAt(final int value) {
		ADD_AT(items, value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public MatrixShort addAt(final short[] values, int start, int stop) {
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

	/**Used to identify the clicked Object.
	 * called on the Mouse Button down Event 
	 * 
	 * @param x Coordinate of the Point to search for
	 * @param y Coordinate of the Point to search for
	 * @param points List of short[] Objects to search in
	 * @param maxDist maximum Distance in Maximum Norm
	 * @return the Index of the Last Point in this List which is closer than this maximum Distance
	 */
	public int findIndexOfLastNeighbour(short[] point, int maxDist) {
		return findIndexOfLastNeighbour(point, items, maxDist, itemCount);
	}

	/**Used to identify the clicked Object.
	 * called on the Mouse Button down Event 
	 * 
	 * @param x Coordinate of the Point to search for
	 * @param y Coordinate of the Point to search for
	 * @param points List of short[] Objects to search in
	 * @param maxDist maximum Distance in Maximum Norm
	 * @return the Index of the Last Point in this List which is closer than this maximum Distance
	 */
	public int findIndexOfLastNeighbour(final short[] point, final int maxDist, final int lastIndex) {
		return findIndexOfLastNeighbour(point, items, maxDist, lastIndex);
	}

	/** draws this Polygon with the given Graphics Context 	 */
	public void draw(final IGraphText g) {
		if (g == null) {
			return; }
		if (oriented && (area() < 0)) { //an Area of 0 (Point or Line) is still being drawn!
			return; }
		g.fillPolygon(items, palette);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + MatrixShort.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) { //throws java.io.IOException {
		testIt(args);
	}

	/** Converts an array of {@link Point2D}s into the short[][] coordinate form used by this class.
	 * @return a short[][] Representation of the given Polygon	*/
	final static public short[][] getPolygon(final Point2D[] polygon) {
		final short[][] pol = new short[polygon.length][2];
		for (int j = polygon.length; --j >= 0;) {
			polygon[j].getCoords(pol[j]);
		}
		return pol;
	}

}
