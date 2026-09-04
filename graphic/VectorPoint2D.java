/*
 * File Name: VectorPoint2D.java
 * Created on: 07.06.2003
 *
 */
package graphic;

import java.io.PrintStream;
import java.util.Arrays;

import math.vector.AVector;
import streamIO.Log;
import streamIO.copy.ICopyAble;

/**
 * Title: VectorPoint2D<p>
 * Description:
 * Purpose:
 *
 * Defines a typed, dynamically growable Array of Point2D Objects with integer Coordinates. 
 * Could also be a regular ArrayList, but this one is typed better. 
 * Optionally the Elements can be preallocated, 
 * so access is guaranteed to return a Point2D Object and not null! 
 * Since this is a Value Object, 
 * the Objects are not replaced but the Elements are copied. 
 * Additionally all Elements are already filled on Construction. 
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 * @see graphic.mvc.plane2D.VectorPolygon which uses short[][][] Arrays 
 * to store whole Polyhedrons, not only single Polygons.  
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
final public class VectorPoint2D 
extends AVector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** streamIO for logging the Progress of Convergence */
	public static Log L = new Log(VectorPoint2D.class, 1); 

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////

	/** @return a filled List of the given minimum Size 
	 * 
	 * @param list
	 * @param newSize
	 * @return
	 */
	final static public Point2D[] GET_FILLED_ARRAY(int minSize) {
		return GET_FILLED_ARRAY(minSize, null);
	}

	/** @return a filled List of at least the given minimum Size 
	 * 
	 * @param list
	 * @param newSize
	 * @return list if it was large enough, or a new enlarged Array
	 */
	final static public Point2D[] GET_FILLED_ARRAY(int minSize, final Point2D[] list) {
		int listLength = 0;
		if (list != null) 
			if (minSize <= (listLength = list.length)) 
				return list;
		final Point2D[] ret = new Point2D[minSize];
		if (list != null) 
			System.arraycopy(list, 0, ret, 0, list.length);
		while (--minSize >= listLength) 
			ret[minSize] = new Point2D();
		return ret;
	}

	/**Used to identify the clicked Object.
	 * called on the Mouse Button down Event 
	 * 
	 * @param x Coordinate of the Point to search for
	 * @param y Coordinate of the Point to search for
	 * @param points List of Point2D Objects to search in
	 * @param maxDist maximum Distance in Maximum Norm
	 * @return the Index of the Last Point in this List which is closer than this maximum Distance
	 */
	final static public int findIndexOfLastNeighbour(int x, int y, Point2D[] points, int maxDist) {
		return findIndexOfLastNeighbour(x, y, points, maxDist, points.length);
	}

	/**Used to identify the clicked Object.
	 * called on the Mouse Button down Event 
	 * 
	 * @param x Coordinate of the Point to search for
	 * @param y Coordinate of the Point to search for
	 * @param points List of Point2D Objects to search in
	 * @param maxDist maximum Distance in Maximum Norm
	 * @param last Index to start searching from, not included!
	 * @return the Index of the Last Point in this List which is closer than this maximum Distance
	 */
	final static public int findIndexOfLastNeighbour(int x, int y, Point2D[] points, int maxDist, int last) {
		L.n("Searching for last Neighbor of(").l(x).l(",").l(y).l(")"); 
		for (int i = last; --i >= 0;) {
			Point2D point = points[i];
			if (point != null) {
				L.n("point[").l(i).l("]=").l(point); 
				if (point.isNeighbour(x, y, maxDist)) {
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
	//	final static public Point2D[] MinMax(Point2D[] arr) { }

	/**
	 * @return the Minimum Values of each Column 
	 */
	final static public Point2D Min(Point2D ret, Point2D[] arg) {
		for (int i = arg.length; --i >= 0;) {
			ret.MinAt(arg[i]);
		}
		return ret;
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	final static public Point2D Min(Point2D[] arg, int start, int stop) {
		//return Min(fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		//Optimization: 
		int i = stop;
		if (stop <= 0) {
			return null;
		}
		Point2D ret = arg[--i].copy();
		for (; --i >= start;) {
			ret.MinAt(arg[i]);
		}
		return ret;
	}

	/** @return the Maximum Values of each Column */
	final static public Point2D Max(Point2D ret, Point2D[] arg) {
		for (int i = arg.length; --i >= 0;) {
			ret.MaxAt(arg[i]);
		}
		return ret;
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	final static public Point2D Max(Point2D[] arg, int start, int stop) {
		//return Min(fill(Float.POSITIVE_INFINITY, arg[0].length), arg); 
		//Optimization: 
		int i = stop;
		if (stop <= 0) {
			return null;
		}
		Point2D ret = arg[--i].copy();
		for (; --i >= start;) {
			ret.MaxAt(arg[i]);
		}
		return ret;
	}

	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public Point2D[] addAt(Point2D[] ret, int Increment) {
		return addAt(ret, Increment, 0, ret.length);
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public Point2D[] addAt(Point2D[] ret, int Increment, int start, int stop) {
		while (--stop >= start) {
			ret[stop].addAt(Increment);
		}
		return ret;
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public Point2D[] addAt(Point2D[] ret, Point2D Increment, int start, int stop) {
		while (--stop >= start) {
			final Point2D point = ret[stop]; 
			if (point == null) {
				continue; }
			point.addAt(Increment);
		}
		return ret;
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public Point2D[] addAt(Point2D[] ret, int[] Increment, int start, int stop) {
		while (--stop >= start) {
			ret[stop].addAt(Increment);
		}
		return ret;
	}

	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public Point2D[] subAt(Point2D[] ret, Point2D Decrement) {
		return subAt(ret, Decrement, 0, ret.length);
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public Point2D[] subAt(Point2D[] ret, Point2D Decrement, int start, int stop) {
		while (--stop >= start) {
			ret[stop].subAt(Decrement);
		}
		return ret;
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public Point2D[] subAt(Point2D[] ret, int[] Decrement, int start, int stop) {
		while (--stop >= start) {
			ret[stop].subAt(Decrement);
		}
		return ret;
	}

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	public static Point2D[] resize(Point2D[] arr, int newRows) {
		Point2D[] ret = new Point2D[newRows];
		if (newRows > arr.length) {
			newRows = arr.length;
		}
		while (--newRows >= 0) {
			ret[newRows] = new Point2D(arr[newRows]);
		}
		return ret;
	}

	/** 
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public int[] RowSum(Point2D[] arr) {
		return RowSum(arr, 0, arr.length, new int[arr.length]);
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public int[] RowSum(Point2D[] arr, int[] ret) {
		return RowSum(arr, 0, arr.length, ret);
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return the Array ret filled with the Sum of all Values in each Row.
	 */
	final static public int[] RowSum(Point2D[] arr, int Start, int Stop, int[] ret) {
		while (--Stop >= Start) {
			ret[Stop] = arr[Stop].Sum();
		}
		return ret;
	}

	/** @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public Point2D ColSum(Point2D[] arr) {
		return ColSum(arr, 0, arr.length, new Point2D());
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public Point2D ColSum(Point2D[] arr, Point2D ret) {
		return ColSum(arr, 0, arr.length, ret);
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public Point2D ColSum(Point2D[] arr, int startRow, int stopRow, Point2D ret) {
		while (--stopRow >= startRow) {
			ret.addAt(arr[stopRow]);
		}
		return ret;
	}

	/** @return The Sum Vector of all Rows as Values in the Array. 	 */
	final static public Point2D ColMax(Point2D[] arr) {
		return ColMax(arr, 0, arr.length, new Point2D(Integer.MIN_VALUE, Integer.MIN_VALUE));
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public Point2D ColMax(Point2D[] arr, Point2D ret) {
		return ColMax(arr, 0, arr.length, ret);
	}

	/**
	 * @para ret the return Vector. To contain the Sum, it must be cleared before!
	 * @return The Sum Vector of all Rows as Values in the Array.
	 */
	final static public Point2D ColMax(Point2D[] arr, int startRow, int stopRow, Point2D ret) {
		while (--stopRow >= startRow) {
			ret.MaxAt(arr[stopRow]);
		}
		return ret;
	}

	/** @return a shallow Copy of the given Array */
	public static Point2D[] shallowCopyAt(Point2D[] ret, Point2D[] arr) {
		if (ret.length != arr.length) {
			throw new IndexOutOfBoundsException("Expected: " + ret.length + " Actual: " + arr.length);
		}
		System.arraycopy(arr, 0, ret, 0, arr.length);
		//while (--len >= 0) {
		//	ret[len] = arr[len];
		//}
		return ret;
	}

	/** @return a deep Copy of the given Matrix */
	public static Point2D[] copyAt(Point2D[] ret, Point2D[] arr, int Start, int Stop) {
		while (--Stop >= Start) {
			ret[Stop].copyAt(arr[Stop]);
		} //Optimization!
		//			VectorDouble.copyAt(ret[Stop], arr[Stop]); }
		return ret;
	}

	/** @return the Matrix ret with deep Copie of the given Vector arr in every Row */
	public static Point2D[] copyAt(Point2D[] ret, Point2D arr, int Start, int Stop) {
		while (--Stop >= Start) {
			//VectorDouble.copyAt(ret[Stop], arr); 
			ret[Stop].copyAt(arr);
		} //Optimization!
		return ret;
	}

	/** @return the Matrix ret with deep Copie of the given Vector arr in every Row */
	public static Point2D[] copyAt(Point2D[] ret, int[] arr, int Start, int Stop) {
		while (--Stop >= Start) {
			ret[Stop].copyAt(arr);
		} //
		return ret;
	}

	/** @return a deep Copy of the given Array */
	public static Point2D[] copy(Point2D[] arr) {
		int len;
		Point2D[] ret = new Point2D[len = arr.length];
		while (--len >= 0) {
			ret[len] = arr[len].copy();
		}
		return ret;
	}

	/** @return a deep Copy of the given Array */
	public static Point2D[] copy(int[][] arr) {
		int len;
		Point2D[] ret = new Point2D[len = arr.length];
		while (--len >= 0) {
			ret[len] = new Point2D(arr[len]);
		}
		return ret;
	}

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public Point2D[] setDimAt(Point2D[] a, int dim) {
		if (a.length == dim) {
			return a;
		}
		Point2D[] ret = new Point2D[dim];
		System.arraycopy(a, 0, ret, 0, a.length);
		//Arrays.fill(ret, a.length, dim, null);
		return a;
	}

	/** @return the given Array multiplied in Place by the given Factor */
	public static Point2D[] mulAt(Point2D[] ret, int Factor) {
		return mulAt(ret, Factor, 0, ret.length);
	}

	/** @return the given Array multiplied in Place by the given Factor */
	public static Point2D[] mulAt(Point2D[] ret, int Factor, int Start, int Stop) {
		while (--Stop >= Start) {
			ret[Stop].mulAt(Factor);
		}
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for fuzzy Set and Matrix Operations
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Matrix Operations (symmetric/antisymmetric)
	////////////////////////////////////////////////////////////////////////////////

	/**
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start1 Index from  where the outer Array is processed
	  * @param stop1  Index up to where the outer Array is processed (not ret[stop]!)
	  * @param start2 Index from  where the inner Array is processed
	  * @param stop2  Index up to where the inner Array is processed (not ret[stop]!)
	  */
	final static public Point2D[] NegAt(Point2D[] ret, int start1, int stop1) {
		while (--stop1 >= start1) {
			ret[stop1].NegAt();
		}
		return ret;
	}

	/**
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public Point2D[] AbsVAt(Point2D[] ret) {
		if (ret.length <= 0) {
			return ret;
		}
		return AbsVAt(ret, 0, ret.length);
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
	final static public Point2D[] AbsVAt(Point2D[] ret, int start1, int stop1) {
		while (--stop1 >= start1) {
			ret[stop1].AbsVAt();
		}
		return ret;
	}

	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public Point2D[] permuteRows(Point2D[] a, int[] index) {
		return permuteRows(new Point2D[a.length], a, index);
	}
	
	/** @return this Vector with the Rows permuted according to the given Permutation     */
	final static public Point2D[] permuteRows(Point2D[] ret, Point2D[] a, int[] index) {
		for (int i = index.length; --i >= 0;) {
			ret[i] = a[index[i]];
		}
		return ret;
	}
	
	/** 
	 * Don't use this in Vector Operations, because temporary Array is created. 
	 * @return this Vector with the Rows permuted according to the given Permutation     
	 */
	final static public Point2D[] permuteRowsAt(Point2D[] a, int[] index) {
		Point2D[] tmp = new Point2D[a.length];
		permuteRows(tmp, a, index);
		System.arraycopy(tmp, 0, a, 0, a.length);
		return a;
	}
	
	/**
	 * @return Maximum Value of the every Row in the Array.
	 */
	final static public int[] MaxVal(Point2D[] arr) {
		int[] ret = new int[arr.length];
		Arrays.fill(ret, Integer.MIN_VALUE); 
		return MaxVal(ret, arr, 0, arr.length);
	}
	
	/**
	 * @return Maximum Value of the every Row in the Array.
	 */
	final static public int[] MaxVal(int[] ret, Point2D[] arr) {
		return MaxVal(ret, arr, 0, arr.length);
	}
	
	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int[] MaxVal(int[] ret, Point2D[] arr, int startRow, int stopRow) {
		while (--stopRow >= startRow) {
			ret[stopRow] = arr[stopRow].MaxVal();
		}
		return ret;
	}
	
	/**Swaps the Columns of this Tensor in Place	 */
	final static public Point2D[] swapCols(Point2D[] a) {
		return swapColsAt(copy(a));
	}
	
	/**Swaps the Columns of this Tensor in Place	 */
	final static public Point2D[] swapColsAt(Point2D[] a) {
		for (int i = a.length; --i >= 0;) {
			a[i].swapColsAt(); }
		return a;
	}
	
	/**Swaps the Rows of this Tensor in Place	 */
	final static public Point2D[] swapRows(Point2D[] a, int Dim1, int Dim2) {
		return swapRowsAt(copy(a), Dim1, Dim2);
	}
	
	/**Swaps the Rows of this Tensor in Place	 */
	final static public Point2D[] swapRowsAt(Point2D[] a, int Dim1, int Dim2) {
		Point2D c = a[Dim1];
		a[Dim1] = a[Dim2];
		a[Dim2] = c;
		return a;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods
	///////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Streams out the complete given Array. 
	 */
	final static public void stream(Point2D[] vals, PrintStream stream) {
		stream(vals, stream, 0, vals.length);
	}
	
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void stream(
		Point2D[] vals,
		PrintStream stream,
		int startRow,
		int stopRow) { //, int startCol, int stopCol) {
		//		if (startRow >= stopRow) {
		//			return; }
		//		stream(vals[startRow], stream);
		for (int i = startRow; ++i < stopRow;) {
			stream.println(vals[i]);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Backing Value Array for the float[]	 */
	private Point2D[] items;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @see math.AVector#getAt(int)	 */
	public Object getAt(final int i) { return getPointAt(i); }
	
	/**
	 * @return the internal List to modify it externally
	 */
	public Point2D[] getList() {
		return items;
	}
	
	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized Point2D getPointAt(final int index) {
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
	public Object setAt(final int index, final Object value) {
		return setAt(index, (Point2D) value); 
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
	public Point2D setAt(final int index, final Point2D value) {
		L.l("setAt[").l(index).l("]=").l(value);
		Point2D ret = null;
		if (indexInRange(index))
			ret = items[index]; 
		else {
			if (value == null) 
				return   null; //save enlarging!
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
	public void setAt(final int index, final int[] value) {
		setAt(index, value[0], value[1]);
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
	public void setAt(final int index, final int x, final int y) {
		L.n("setAt[").l(index).l("]=(").l(x).l(",").l(y).l(")");
		if (!indexInRange(index)) 
			setSize(index+1);
		if (items[index] == null) 
			items[index] = new Point2D(x, y);
		else 
			items[index].copyAt(x, y);
		if (itemCount<= index) 
			itemCount = index + 1;
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
	public void setAt(final Point2D[] value) {
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
	
	/**Constructs an empty VectorInt with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the VectorInt.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the VectorInt overflows.	 */
	public VectorPoint2D(int initialCapacity, int capacityIncrement_) {
		super();
		items = GET_FILLED_ARRAY(initialCapacity); 
		capacityIncrement = capacityIncrement_;
		//		mEnum = new ArrayEnum(Items, ItemCount);
		//		mEnum = new ArrayIterator(this); 
	} //
	
	/** Constructs an empty VectorPoint2D with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the VectorPoint2D.	 */
	public VectorPoint2D(int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}
	
	/** Constructs an empty VectorPoint2D.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public VectorPoint2D() {
		this(DEFAULT_CAPACITY_INIT);
	}
	
	/** Constructs an VectorPoint2D by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public VectorPoint2D(final Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}
	
	/** Constructs an VectorPoint2D from the given Object.	  */
	public VectorPoint2D(final Object arg, final int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////
	
	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorPoint2D addItem(final Point2D item) {
		setAt(itemCount, item);
		return this;
	}
	
	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorPoint2D addItem(final int[] item) {
		setAt(itemCount, item);
		return this;
	}
	
	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorPoint2D addItem(final int x, final int y) {
		setAt(itemCount, x, y);
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
			Point2D[] oldData = items;
			items = new Point2D[itemCount];
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
	}
	
	/**Returns the current capacity of this VectorInt.
	 *
	 * @return  the current capacity of this VectorInt.	 */
	final public int getCapacity() {
		return items.length;
	}
	
	/**Increases the capacity of this VectorInt, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final public synchronized int setCapacity(final int minCapacity) {
		items = GET_FILLED_ARRAY(minCapacity, items); 
		return items.length;
	}
	
	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 *
	 * The Optimization here is that the Capacity can be ensured before
	 * and that additional Fields can be set.	 */
	public VectorPoint2D copyAt(final Point2D arg_) {
		for(int i = itemCount; --i >= 0; ) {
			items[i].copyAt(arg_); 
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
	public VectorPoint2D copyAt(final Point2D[] arg_) {
		int i = itemCount; 
		if (i > arg_.length) {
			i = arg_.length; 
		}
		for(; --i >= 0; ) {
			items[i].copyAt(arg_[i]); 
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
		if (arg instanceof VectorPoint2D) {
			VectorPoint2D arg_ = (VectorPoint2D) arg;
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
		if (arg instanceof VectorPoint2D) {
			VectorPoint2D arg_ = (VectorPoint2D) arg;
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
		return new VectorPoint2D(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Arithmetic Methods for Arrays
	////////////////////////////////////////////////////////////////////////////////

	/** Normalizes this Vector by bringing it into the canonical Form
	 * so that getAt(getInt()) != 0 
	 */
	public VectorPoint2D normalizeAt() {
		while (items[--itemCount] == null);
		++itemCount;
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorPoint2D addAt(final Point2D vector) {
		return addAt(vector, 0, itemCount); //Recursion!
	}

	/** subtracts the given Portion of the values from this Vector */
	public VectorPoint2D subAt(final Point2D vector) {
		return subAt(vector, 0, itemCount);
	}

	/** @return the Minimum and Maximum Values of each Column... 
	 * too complex to optimize for now... 
	 * 
	 * Use Min and Max separately, which is clearer too!
	 */
	//	final static public Point2D[] MinMax(Point2D[] arr) { }

	/**
	 * @return the Minimum Values of each Column 
	 */
	public Point2D Min(final Point2D ret) {
		return Min(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Minimum Values of each Column 
	 */
	public Point2D Min() {
		return Min(items, 0, itemCount);
	}

	/** @return the Maximum Values of each Column */
	public Point2D Max(final Point2D ret) {
		return Max(ret, items);
	}

	/** 
	 * Since it is not possible to get the Dimensionality of an empty Matrix, 
	 * the Optimization is implemented not to start with +Infinity. 
	 * @return the Maximum Values of each Column 
	 */
	public Point2D Max() {
		return Max(items, 0, itemCount);
	}

	/** subtracts the given Portion of the values from this Vector */
	public VectorPoint2D subAt(final Point2D values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			copyAt(items, values, itemCount, stop);
			subAt(items, values, start, itemCount);
			//			normalizeAt();
		} else if (stop < itemCount) { //don't need to (re-)normalize
			subAt(items, values, start, stop);
		} else {
			subAt(items, values, start, stop);
			//			normalizeAt(); //might be quite improbable though!
		}
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorPoint2D addAt(final int value) {
		addAt(items, value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorPoint2D addAt(final Point2D values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			copyAt(items, values, itemCount, stop);
			addAt(items, values, start, itemCount);
			//normalizeAt();
		} else if (stop < itemCount) { //don't need to (re-)normalize
			addAt(items, values, start, stop);
		} else {
			addAt(items, values, start, stop);
			//normalizeAt();
		}
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorPoint2D addAt(final int[] values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			copyAt(items, values, itemCount, stop);
			addAt(items, values, start, itemCount);
			//normalizeAt();
		} else if (stop < itemCount) { //don't need to (re-)normalize
			addAt(items, values, start, stop);
		} else {
			addAt(items, values, start, stop);
			//normalizeAt();
		}
		return this;
	}

	/**Used to identify the clicked Object.
	 * called on the Mouse Button down Event 
	 * 
	 * @param x Coordinate of the Point to search for
	 * @param y Coordinate of the Point to search for
	 * @param points List of Point2D Objects to search in
	 * @param maxDist maximum Distance in Maximum Norm
	 * @return the Index of the Last Point in this List which is closer than this maximum Distance
	 */
	public int findIndexOfLastNeighbour(int x, int y, int maxDist) {
		return findIndexOfLastNeighbour(x, y, items, maxDist, itemCount);
	}

	/**Used to identify the clicked Object.
	 * called on the Mouse Button down Event 
	 * 
	 * @param x Coordinate of the Point to search for
	 * @param y Coordinate of the Point to search for
	 * @param points List of Point2D Objects to search in
	 * @param maxDist maximum Distance in Maximum Norm
	 * @return the Index of the Last Point in this List which is closer than this maximum Distance
	 */
	public int findIndexOfLastNeighbour(int x, int y, int maxDist, int lastIndex) {
		return findIndexOfLastNeighbour(x, y, items, maxDist, lastIndex);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + VectorPoint2D.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) { //throws java.io.IOException {
		testIt(args);
	}

}
