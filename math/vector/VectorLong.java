package math.vector;

import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import streamIO.copy.ICopyAble;
import streamIO.integer.jdbc.AResultSet;
import streamIO.object.IStreamIn;
import function.IOrderAble;
import function.byref.ByRefInt;
import function.byref.ByRefLong;

/**
  * Growable, index-addressable array of primitive {@code long} elements, plus a large
  * library of static array-level operations (arithmetic, min/max, negation, linear
  * combinations) shared by every method of this class and its instances alike.
  *
  * <p>Defines some static Methods to treat Vectors and Arrays with Chars.
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.Vector.VectorDbl
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-16-2002, 11:35 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T13:19:50Z
  * digest: c265f18ea4c5a7a2396d8e1b740d3d00f587289128ef38c22545fc38ef3cb8b3
  * stale: false
  * tags: [code/growable_array, code/array_math]
  * concepts: [Growable long[] Vector]
  * facets: {layer: domain, status: broken, complexity: high}
  * -->
  */
public class VectorLong
extends AVector {

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Column Operations:
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Collects the column at the given position from every row of the matrix.
	 * @return the Column at the given Position
	 */
	final static public long[] COLUMN(long[][] matrix, int col) {
		long[] ret = new long[matrix.length];
		for (int i = matrix.length; --i >= 0;) {
			ret[i] = matrix[i][col]; }
		return ret; }

	/**
	 * Returns the maximum value found in the column at the given position.
	 * @return the Maximum of the Column at the given Position
	 */
	final static public long MAX(long[][] matrix, int col) {
		long max = Long.MIN_VALUE; 
		for (int i = matrix.length; --i >= 0;) {
			if (max < matrix[i][col]) {
				max = matrix[i][col]; }
		} return max; }

	/**
	 * Returns the minimum value found in the column at the given position.
	 * @return the Minimum of the Column at the given Position
	 */
	final static public long MIN(long[][] matrix, int col) {
		long min = Long.MAX_VALUE;
		for (int i = matrix.length; --i >= 0;) {
			if (min > matrix[i][col]) {
				min = matrix[i][col]; }
		} return min; }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * from integer Space into the real Numbers.
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public long[] MAP(final long[] a, final int[] index) {
		return MAP(a, index, null); }

	/**this is a linear Mapping (Projection along the Dimension)  
	 * from integer Space into the real Numbers.
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public long[] MAP(final long[] a, final int[] index, long[] ret) {
		if((ret == null) || (ret.length < index.length))
			ret = new long[index.length];
		//else if (ret.length > index.length) //rather leave the Values alone?!?
		//	Arrays.fill(ret, index.length, ret.length, 0); 
		for(int i = index.length; --i >= 0; )
			ret[i] = (index[i] < a.length) ? a[index[i]] : 0; 
		return ret;
	}
	
	/** Fills up an Array with the given Mapping: 
	 * mapping[i][0] -> mapping[i][1]
	 * This is replaces a Loop through the Elements with a faster Array Lookup!
	 * @param mapping the List of Long Mappings to encode
	 * @param maxChar the maximum Long in the encoding
	 */
	final static public long[] MAP(long[][] mapping) {
		return MAP(mapping, (int) MAX(mapping, 0)); }
	
	/** Fills up an Array with the given Mapping: 
	 * mapping[i][0] -> mapping[i][1]
	 * This is replaces a Loop through the Elements with a faster Array Lookup!
	 * @param mapping the List of Long Mappings to encode
	 * @param maxChar the maximum Long in the encoding
	 */
	final static public long[] MAP(long[][] mapping, int maxChar) {
		final long[] ret = Identity(maxChar+1);
		for(int i = mapping.length; --i >= 0; ) {
			ret[(int) mapping[i][0]] = mapping[i][1]; } //
		return ret; 
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Database Operations:
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reads a single Plane from the current ResultSet
	 * @param RS the ResultSet to read from
	 * @param numPointCol Column that contains the Number of Points in this Plane
	 * @param ColOffset the Column to start reading from when Cols == null
	 * @param Cols the List of Columns to read, null when consecutive!
	 * @param Plane the Plane returned; when null, a new Plane is created
	 * @return the Plane read.
	 */
	final static public long[] readVector(ResultSet RS,
		int numPointCol, int ColOffset, long[] Plane, long[] Cols)
		throws SQLException {
		if (!RS.next()) {
			return null; }
		int len;
		if (Plane != null) {
			len = Plane.length;
			if (Cols != null) {
				if (len > Cols.length)
					len = Cols.length; }
		} else {
			if (numPointCol >= 0) { //Number of Points given in the Table itself
				len = RS.getInt(numPointCol);
			} else { //numPointCol < 0, fixed Number of Points
				len = -numPointCol;
				if (Cols != null) {
					if (len > Cols.length)
						len = Cols.length;
				} else {
					if (len > ((AResultSet) RS).getNumCols() - ColOffset)
						len = ((AResultSet) RS).getNumCols() - ColOffset;
				}
			}
			Plane  = new long[len];
		}
		int i = -1; //start in the correct Order
		try { //try to read as many Coordinates as possible!
			while (++i < len) {
				if (Cols == null) { //read consecutive Values
					Plane[i] = (long) RS.getInt(i+ColOffset);
				} else {
					Plane[i] = (long) RS.getInt((int)Cols[i]); }
			}
		} catch (Exception x) { //Resize the Array.
			long[] tmp = new long[i];
			System.arraycopy(Plane, 0, tmp, 0, i);
			Plane = tmp;
		} return Plane; }

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public long[] readVector(java.sql.ResultSet RS,
		int numPointCol,
		int ColOffset,
		long[] Plane)
		throws java.sql.SQLException {
		return readVector(RS, numPointCol, ColOffset, Plane, null); }

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public long[] readVector(java.sql.ResultSet RS,
		int numPointCol, int ColOffset)
		throws java.sql.SQLException {
		return readVector(RS, numPointCol, ColOffset, null, null); }

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for scalar (1Dim) Values:
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public long MIN_VAL(final long[] arr) {
		return arr[MIN_POS(arr)]; }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public long MIN_VAL(final long[] arr, final int Start, int Stop) {
		long Min = Long.MAX_VALUE;
		while (--Stop >= Start) {
			if (Min > arr[Stop]) 
				Min = arr[Stop];
		}
		return Min;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Position of the Minimum Value of the Array.
	 */
	final static public int MIN_POS(final long[] arr) {
		return MIN_POS(arr, 0, arr.length); }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public int MIN_POS(final long[] arr, final int start, int stop) {
		int iMin = -1;
		long Min = Integer.MAX_VALUE;
		while (--stop >= start) {
			if (Min > arr[stop]) 
				Min = arr[iMin = stop];
		}
		return iMin;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public long MAX_VAL(final long[] arr) {
		return arr[MAX_POS(arr)]; }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int MAX_POS(final long[] arr) {
		return MAX_POS(arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public long MAX_VAL(final long[] arr, final int Start, final int Stop) {
		return arr[MAX_POS(arr, Start, Stop)];
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Index of the Maximum Value of the Array.
	 */
	final static public int MAX_POS(final long[] arr, final int Start, int Stop) {
		int iMax = -1;
		long Max = Integer.MIN_VALUE;
		while (--Stop >= Start) {
			if (Max < arr[Stop]) 
				Max = arr[iMax = Stop];
		}
		return iMax;
	}

	/**
	 * Returns the smaller of the two given values.
	 * @return the Minimum of both Values
	 */
	final static public long MIN (final long x, final long y) { return (x < y) ? x : y; }

	/**
	 * Returns the larger of the two given values.
	 * @return the Maximum of both Values
	 */
	final static public long MAX (final long x, final long y) { return (x > y) ? x : y; }

	/**Defines the Sqr for simple Types	 */
	final static public long SQR	(final long x) { return x*x; }

	/**Defines the Cbc for simple Types	 */
	final static public long CBC	(final long x) { return x*x*x; }

	/**Returns the Sign of x as an integer, i.e.
	 * -1 for negative x
	 *  0 for x == 0
	 * +1 for positive x	 */
	final static public long SIGN(final long x) {
		return (x > 0) ? (long) 1 : (long) 0; }

	/**Checks if the Interval contains x.
	 * This Implementation is unsymmetric, always a[0] < a[1] assumed
	 * there is no fast correct Solution, only Compromises !}	 */
	final static public boolean CONTAINS (final long x, final long left, final long right) {
		return (left <= x) ^ (right < x); }

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the inverse Permutation in Place
	  * Assumption is that this is a complete Permutation,
	  * i.e. Max(this_) == this_.length
	  * @return the inverse Permutation
	  * Cannot be calculated in Place!
	  */
	public static long[] Inverse(long[] this_) {
		return Inverse(this_, new long[this_.length], this_.length); }

	/** Returns the inverse Permutation in Place
	  * @return the inverse Permutation
	  * Cannot be calculated in Place!
	  */
	public static long[] Inverse(long[] this_, int thisLength) {
		return Inverse(this_, new long[thisLength], thisLength); }

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	public static long[] Inverse(long[] this_, long[] ret) {
		return Inverse(this_, ret, this_.length); }

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	public static long[] Inverse(final long[] this_, final long[] ret, int thisLength) {
		while (--thisLength >= 0) {
			ret[(int) this_[thisLength]] = thisLength; }
		return ret; }

	/** Reverts a complete Encoding Table for Bytes
	  * This is equivalent to inverting the Permutation
	  * in Class streamIO.Copy.Monoid.SetInteger.Permutation
	  * @param encoding the List of Characters to encode
	  * @param length the maximum Long in the encoding
	  */
	final static public long[] FullInverse(final long[] encoding) {
		return Inverse(encoding, (int) Max(encoding)); }

	/** Returns the identical Permutation up to the given Length, i.e. id[i] = i
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	public static long[] Identity(final int length) {
		return Identity(new long[length]); }

	/** Returns the identical Permutation in Place, i.e. id[i] = i
	 * @return the identical Permutation in Place 
	 */
	public static long[] Identity(final long[] this_) {
		for(int len = this_.length; --len > 0;) 
			this_[len] = len; 
		return this_; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for dynamic growing Array Operations
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return this Vector with the Elements permuted according to the given Permutation     */
	/**final static public long[] permuteAt(long[] a, long[] perm) {
		long tmp;	//Undo the Row Permutations!
		int j, k = a.length;
		while (--k > 0) { 	//first row is not modified, because L[1,1]=1
			if (perm[k] != k) {
				tmp = a[k]; a[k] = a[j = perm[k]]; a[j] = tmp; }
		}
		return a; }
	 */

	/**
	 * Sets the Value at the given Position in the Array
	 * Returns a resized (larger OR smaller) Copy of the given Array
	 * filled with the given Value at the given Position.
	 */
	public static long[] SET_AT(long[] arr, int Pos, long Value) {
		if (Pos >= arr.length) {
			arr = RESIZE(arr, arr.length << 1); }
		arr[Pos] = Value;
		return arr; }

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	public static long[] RESIZE(long[] arr, int newSize) {
		long[] ret = new long[newSize];
		if (newSize > arr.length) 
			newSize = arr.length; 
		System.arraycopy(arr, 0, ret, 0, newSize);
		return ret; }

	/** Returns a Copy of the given Array */
	public static long[] COPY(long[] arr) {
		long[] ret = new long[arr.length];
		System.arraycopy(arr, 0, ret, 0, arr.length);
		return ret; }

	/** Returns a Copy of the given Array */
	public static long[] COPY(float[] arr) {
		final long[] ret = new long[arr.length];
		for (int i = arr.length; --i >= 0; ) 
			ret[i] = (long) arr[i]; 
//		System.arraycopy(arr, 0, ret, 0, arr.length); //doesn't work: "ArrayStoreException"
		return ret; }

	/** Returns a Copy of the given Array */
	public static long[] COPY(double[] arr) {
		final long[] ret = new long[arr.length];
		for (int i = arr.length; --i >= 0; ) 
			ret[i] = (long) arr[i]; 
//		System.arraycopy(arr, 0, ret, 0, arr.length); //doesn't work: "ArrayStoreException"
		return ret; }

	/** Returns a Copy of the given Array */
	public static long[] copyAt(long[] this_, long[] arr) {
		System.arraycopy(arr, 0, this_, 0, arr.length);
		return this_; }

	/** Returns a Copy of the given Array */
	public static long[] copyAt(long[] this_, long[] arr, int Start, int Stop) {
		System.arraycopy(arr, Start, this_, Start, Stop);
		return this_; }

	/** Returns a Copy of the given Array */
	public static long[] copyAt(long[] this_, double[] arr) {
		return copyAt(this_, arr, 0, arr.length); }

	/** Returns a Copy of the given Array */
	public static long[] copyAt(long[] this_, float[] arr) {
		return copyAt(this_, arr, 0, arr.length); }

	/** Returns a Copy of the given Array */
	public static long[] copyAt(long[] this_, double[] arr, int Start, int Stop) {
		int i = arr.length;
		while (--i >= 0) {
			this_[i] = (long) arr[i]; }
//		System.arraycopy(arr, Start, this_, Start, Stop); //ArrayTypeException!
		return this_; }

	/** Returns a Copy of the given Array */
	public static long[] copyAt(long[] this_, float[] arr, int Start, int Stop) {
		int i = arr.length;
		while (--i >= 0) {
			this_[i] = (long) arr[i]; }
//		System.arraycopy(arr, Start, this_, Start, Stop); //ArrayTypeException!
		return this_; }

	/**
	 * Setting the Vectors to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vectors fit, they are returned unchanged!
	 */
	final static public long[][] setDimAt(long[][] a, int dim) {
		int i = a.length;
		while (--i >= 0) {
			a[i] = setDimAt(a[i], dim); }
		return a; }

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public long[] setDimAt(long[] a, int dim) {
		if (a.length == dim) {
			return a; }
		long[] ret = new long[dim];
		System.arraycopy(a, 0, ret, 0, a.length);
		Arrays.fill(ret, a.length, dim, (long) 0);
		return a; }

	/**
	 * Sets every element of the given array to 0.
	 * @return the given Array with all Elements set to 0. 	 */
	final static public long[] zeroAt(long[] ret) {
		return zeroAt(ret, 0, ret.length); }

	/**
	 * Sets the elements in the given range of the array to 0.
	 * @return the given Array with the Elements from Start (inclusive) to Stop (exclusive) set to 0. 	 */
	final static public long[] zeroAt(long[] ret, int Start, int Stop) {
		java.util.Arrays.fill(ret, Start, Stop, (long) 0);
		return ret; }

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag.
	 * i.e. a[dim] = 1 and a[j] = 0 otherwise.
	 */
	final static public long[] oneAt(long[] a, int dim) {
		return diagAt(a, (long) 1, dim); }

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag,
	 * i.e. a[dim] = diag and a[j] = 0 otherwise.
	 */
	final static public long[] diagAt(long[] a, long diag, int dim) {
		Arrays.fill(a, (long) 0);
		a[dim] = diag;
		return a; }

	/**
	 * Sets every element of the given array to 1.
	 * @return the given Array with all Elements set to 1. 	 */
	final static public long[] oneAt(long[] ret) {
		return oneAt(ret, 0, ret.length); }

	/**
	 * Sets the elements in the given range of the array to 1.
	 * @return the given Array with the Elements from Start (inclusive) to Stop (exclusive) set to 1. 	 */
	final static public long[] oneAt(long[] ret, int Start, int Stop) {
		java.util.Arrays.fill(ret, Start, Stop, (long) 1);
		return ret; }

	/**
	 * Fills every element of the given array with the given value.
	 * @return the given Array with all Elements set to the given Value. 	 */
	final static public long[] fillAt(long[] ret, long val) {
		return fillAt(ret, val, 0, ret.length); }

	/**
	 * Fills the elements in the given range of the array with the given value.
	 * @return the given Array with the Elements from Start (inclusive)
	 * to Stop (exclusive) set to the given Value.
	 */
	final static public long[] fillAt(long[] ret, long val, int Start, int Stop) {
		java.util.Arrays.fill(ret, Start, Stop, val);
		return ret; }

	/**
	 * Returns whether every element of the array is zero.
	 * @return true when every element of the Array is zero.
	 */
	final static public boolean isZero(long[] arr) {
		return isZero(arr, 0, arr.length); }

	/**
	 * Returns whether every element in the given range of the array is zero.
	 * @return true when every element in the given range is zero.
	 */
	final static public boolean isZero(long[] arr, int Start, int Stop) {
		while (--Stop >= Start) {
			if (arr[Stop] != 0.0) {
				return false; }
		} return true; }

	/**
	 * Checks whether this Vector is a Unity Vector in the given Dimension
	 */
	final static public boolean isOne(long[] Row, int dim) { 	//Assume a square Matrix
		int j  = Row.length;
		while(--j >= 0) { 	//Use an Epsilon here
			if (j == dim) { if (Row[j] != 1) { return false; }
			} else        { if (Row[j] != 0) { return false; } }
		}
		return true; }

	/**
     * The Order can change in each individual Dimension!
     * @return true when the middle Vector is between the left and right Vector.
     */
	final static public boolean between(long[] left, long[] mid, long[] right) {
		int i = left.length;
		while (--i >= 0) {
			if ((left [i] < mid[i]) !=
				(right[i] > mid[i])) {
				return false; }
		} return true; }

	/**
	 * Determines the Minimum and Maximum Value
	 * and sorts them into the first and second Argument.
	 */
	final static public void orderAt(long[] inOutMin, long[] inOutMax) {
		long tmp;
		int i = inOutMin.length;
		while (--i >= 0) {
			if ((tmp = inOutMin[i]) < inOutMax[i]) {
				continue; }
			inOutMin[i] = inOutMax[i]; inOutMax[i] = tmp; }
	}

////////////////////////////////////////////////////////////////////////////////
//  static Methods returning a single Number from the Array
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the sum of all values in the array.
	 * @return The Sum of all Values in the Array. 	 */
	final static public long Sum(long[] arr) {
		return Sum(arr, 0, arr.length); }

	/**
	 * Returns the sum of the values in the given range of the array.
	 * @return The Sum of all Values in the Array.	 */
	final static public long Sum(long[] arr, int Start, int Stop) {
		if (Start == Stop) { return 0; }
		long Sum = arr[--Stop]; //0;
		while (--Stop >= Start) {
			Sum += arr[Stop]; }
		return Sum; }

	/**
	 * Returns the product of all values in the array.
	 * @return The Product of all Values in the Array. 	 */
	final static public long Prod(long[] arr) {
		return Prod(arr, 0, arr.length); }

	/**
	 * Returns the product of the values in the given range of the array.
	 * @return The Product of all Values in the Array.	 */
	final static public long Prod(long[] arr, int Start, int Stop) {
		if (Start == Stop) { return 1; }
		long Prod = arr[--Stop];//1;
		while (--Stop >= Start) {
			Prod *= arr[Stop]; }
		return Prod; }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public long Min(long[] arr) {
		return Min(arr, 0, arr.length); }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public long Min(long[] arr, int Start, int Stop) {
		long Min = Long.MAX_VALUE;
		while (--Stop >= Start) {
			if (Min > arr[Stop]) {
				Min = arr[Stop]; }
		} return Min; }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public long Max(long[] arr) {
		return Max(arr, 0, arr.length); }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array in the given Range
	 */
	final static public long Max(long[] arr, int Start, int Stop) {
		long Max = Long.MIN_VALUE;
		while (--Stop >= Start) {
			if (Max < arr[Stop]) {
				Max = arr[Stop]; }
		} return Max; }

	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public long[] MinMax(long[] arr) {
		return MinMax(arr, new long[2]); }

	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public long[] MinMax(long[] arr, long[] MinMax) {
		int i;
		long Min, Max;
		if (((i = arr.length) | 1) == 1) { //odd?
			Min = Max = arr[--i];
		} else {
			Min = Long.MAX_VALUE;
			Max = Long.MIN_VALUE;
		}
		long tMin, tMax, tmp;
		while (i > 1) {
			if ((tMin = arr[--i]) > //first compare Args
				(tMax = arr[--i])) {
				tmp = tMin; tMin = tMax; tMax = tmp; }
			if (Min > tMin) { //then compare tMin and tMax to Min and Max
				Min = tMin; }
			if (Max < tMax) { //this saves 1/4 of the Comparisons
				Max = tMax; } }
		MinMax[0] = Min;
		MinMax[1] = Max;
		return MinMax; }

	/**
	  * Returns the Euclidean norm (square root of the sum of squares) of the given array.
	  * @return the Norm of the given Array
	  */
	final static public double Norm(long[] arr) {
		return Math.sqrt(SqrNorm(arr, arr.length)); }

	/**
	  * Returns the squared Euclidean norm (sum of squares) of the given array.
	  * @return the squared Norm of the given Array
	  */
	final static public double SqrNorm(long[] arr) {
		return SqrNorm(arr, arr.length); }

	/**
	  * This Value can well exceed the Range of valid Numbers,
	  * but that should be avoided anyway by renorming.
	  * Accuracy is not affected when using int Point Numbers.
	  *
	  * @return the squared Norm of the given Array
	  */
	final static public long SqrNorm(long[] arr, int len) {
		long norm = 0; //Calculate the Norm
		while (--len >= 0) {
			norm += arr[len]*arr[len]; } //sqr(arr[len]); }
		return norm; }

	/**
	  * Returns the squared Euclidean distance between the two given arrays.
	  * @param arr1 first  Vector, not modified.
	  * @param arr2 second Vector, not modified.
	  * @return the squared Norm of the Distance between the given Arrays
	  */
	final static public long SqrDist(long[] arr1, long[] arr2) {
		long diff, norm = 0; //Calculate the Norm
		int i = arr1.length;
		while (--i >= 0) {
			diff = arr1[i]-arr2[i];
			norm += diff*diff; }
		return norm; }

	/**
	  * Returns the sum of the absolute differences between corresponding elements
	  * of the two given arrays.
	  * @param arr1 first  Vector, not modified.
	  * @param arr2 second Vector, not modified.
	  * @return the absolute Norm of the Distance between the given Arrays
	  */
	final static public long AbsVDist(long[] arr1, long[] arr2) {
		long diff, norm = 0; //Calculate the Norm
		int i = arr1.length;
		while (--i >= 0) {
			if (0 < (diff = arr1[i] - arr2[i])) {
				norm += diff; continue; }
				norm -= diff; }
		return norm; }

	/**
	  * Returns the sum of absolute differences between the two given arrays.
	  * @param diff is an Output Parameter being filled with the Difference Vector.
	  * @return the squared Norm of the given Array
	  */
	final static public long AbsDiffNorm(final long[] arr1, final long[] arr2, final long[] diff) {
		long dif;
		long norm = 0; //Calculate the Norm
		int i = arr1.length;
		while (--i >= 0) {
//			norm+=Math.abs(diff[i] = arr1[i]-arr2[i]); }
			if (0 < (dif = arr1[i]-arr2[i])) { //avoid calling expensive Math.abs
				diff[i] = (long) dif;
				norm += dif; continue; }
				diff[i] = -dif;
				norm -= dif;
		} return norm; }

	/**
	  * Returns the sum of the absolute values of the array's elements.
	  * @return the sum of the absolute Values of the given Array
	  */
	final static public long AbsV_Norm(long[] arr) {
		long a, norm = 0; //Calculate the Norm
		int i = arr.length;
		while (--i >= 0) {
			if ((a = arr[i]) > 0) {
				norm += a; continue; }
				norm -= a; }
		return norm; }

	/**
	  * By Definition Elements outside the Array are 0
	  * @return the scalar Product of the given Arrays up to the given Length.
	  */
	final static public long ScalarProd(long[] arr1, long[] arr2) {
		int len = arr1.length;
		if (len > arr2.length) {
			len = arr2.length; } //use the Minimum, because higher Elements are assumed to be 0.
		return ScalarProd(arr1, arr2, 0, len); }

	/**
	  * By Definition Elements outside the Array are 0
	  * @return the scalar Product of the given Arrays up to the given Length.
	  */
	final static public long ScalarProd(long[] arr1, long[] arr2, int start, int stop) {
		long ret = 0;
		while (--stop >= start) {
			ret += arr1[stop]*arr2[stop]; }
		return ret; }

	/**
	  * Returns the largest of the pairwise (elementwise) minimums of the two given arrays.
	  * @return the Scalar Product of the two Vectors.
	  */
	final static public long MaxMinProd(final long[] a, final long[] arg) {
		return MaxMinProd(a, arg, 0, arg.length); }

	/**
	  * Returns the largest of the pairwise (elementwise) minimums of the two given
	  * arrays, over the given range.
	  * @return the MaxMin Product of the two Vectors.
	  */
	final static public long MaxMinProd(final long[] a, final long[] arg, final int start, int stop) {
		long x, y, max = Integer.MIN_VALUE; //FALSE; //can also start with any lower Value!
		while (--stop >= start) {
			if ((x = a  [stop]) <
				(y = arg[stop])) { //use the Minimum
				if (max < x) { //update the Maximum
					max = x; }
			} else {
				if (max < y) { //update the Maximum
					max = y; }
			}
		} return max; }

	/**
	  * Negates every element of the given array, in place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] NegAt(long[] ret) {
		return NegAt(ret, 0, ret.length); }

	/**
	  * Negates the elements in the given range of the array, in place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] NegAt(long[] ret, int start, int stop) {
		return Neg(ret, ret, start, stop); }

	/**
	  * Writes the negation of x into ret, over the given range.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] Neg(long[] ret, long[] x, int start, int stop) {
		while (--stop >= start) {
			ret[stop]  = (long) -x[stop]; }
		return ret; }

	/**
	  * Writes the negation of the given double array into ret, over the given range.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] Neg(long[] ret, double[] x, int start, int stop) {
		while (--stop >= start) {
			ret[stop]  = (long) -x[stop]; }
		return ret; }

	/**
	  * Writes the negation of the given float array into ret, over the given range.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] Neg(long[] ret, float[] x, int start, int stop) {
		while (--stop >= start) {
			ret[stop]  = (long) -x[stop]; }
		return ret; }

	/**
	  * Returns a new array containing the negation of the given array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] Neg(long[] x) {
		return Neg(new long[x.length], x, 0, x.length); }

	/**
	  * Returns a new array containing the negation of the given range of the array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] Neg(long[] x, int start, int stop) {
		return Neg(new long[x.length], x, start, stop); }

	/**
	  * Returns a new long array containing the negation of the given double array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] Neg(double[] x) {
		return Neg(new long[x.length], x, 0, x.length); }

	/**
	  * Returns a new long array containing the negation of the given range of the
	  * given double array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] Neg(double[] x, int start, int stop) {
		return Neg(new long[x.length], x, start, stop); }

	/**
	  * Returns a new long array containing the negation of the given float array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] Neg(float[] x) {
		return Neg(new long[x.length], x, 0, x.length); }

	/**
	  * Returns a new long array containing the negation of the given range of the
	  * given float array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] Neg(float[] x, int start, int stop) {
		return Neg(new long[x.length], x, start, stop); }

	/**
	  * Replaces every element of the given array with its absolute value, in place.
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] AbsVAt(long[] ret) {
		return AbsVAt(ret, 0, ret.length); }

	/**
	  * Replaces the elements in the given range of the array with their absolute
	  * value, in place.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] AbsVAt(long[] ret, int start, int stop) {
		long tmp; //Calculate the Norm
		while (--stop >= start) {
			if (0 <= (tmp = ret[stop])) { continue; }
			ret[stop] = (long) -tmp; }
		return ret; }

///////////////////////////////////////////////////////////////////////////////////
/// Binary Operations
///////////////////////////////////////////////////////////////////////////////////

	/**
	  * Clamps every element of the given array to at most the given limit, in place.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public long[] MinAt(long[] ret, long Limit) {
		return MinAt(ret, Limit, 0, ret.length); }

	/**
	  * Clamps the elements in the given range to at most the given limit, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] MinAt(long[] ret, long Limit, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > Limit) {
				ret[stop] = Limit; }
		} return ret; }

	/**
	  * Replaces each element of ret with the elementwise minimum of ret and arr, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] MinAt(long[] ret, long[] arr) {
		return MinAt(ret, arr, 0, arr.length); }

	/**
	  * Replaces the elements of ret in the given range with the elementwise
	  * minimum of ret and arr, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] MinAt(long[] ret, long[] arr, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > arr[stop]) {
				ret[stop] = arr[stop]; }
		} return ret; }

	/**
	  * Clamps every element of the given array to at least the given limit, in place.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public long[] MaxAt(long[] ret, long Limit) {
		return MaxAt(ret, Limit, 0, ret.length); }

	/**
	  * Clamps the elements in the given range to at least the given limit, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] MaxAt(long[] ret, long Limit, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < Limit) {
				ret[stop] = Limit; }
		} return ret; }

	/**
	  * Replaces each element of ret with the elementwise maximum of ret and arr, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] MaxAt(long[] ret, long[] arr) {
		return MaxAt(ret, arr, 0, arr.length); }

	/**
	  * Replaces the elements of ret in the given range with the elementwise
	  * maximum of ret and arr, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] MaxAt(long[] ret, long[] arr, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < arr[stop]) {
				ret[stop] = arr[stop]; }
		} return ret; }

	/**
	  * Adds the given increment to every element of the array, in place.
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public long[] addAt(long[] ret, int Increment) {
		return addAt(ret, Increment, 0, ret.length); }

	/**
	  * Adds the given increment to the elements in the given range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] addAt(long[] ret, int Increment, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += Increment; }
		return ret; }

	/**
	  * Adds arr elementwise into ret, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] addAt(long[] ret, long[] arr) {
		return addAt(ret, arr, 0, arr.length); }

	/**
	  * Adds arr elementwise into ret over the given range, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] addAt(long[] ret, long[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += arr[stop]; }
		return ret; }

	/**
	  * Writes the elementwise sum of sum1 and sum2 into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] add(long[] ret, long[] sum1, long[] sum2) {
		return add(ret, sum1, sum2, 0, sum1.length); }

	/**
	  * Writes the elementwise sum of sum1 and sum2 into ret over the given range,
	  * copying the longer operand's tail through when the two arrays' lengths differ.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] add(long[] ret, long[] sum1, long[] sum2, int start, int stop) {
		if (stop > ret .length) {
			stop = ret .length; }
		if (stop > sum1.length) { System.arraycopy(sum2, sum1.length, ret, sum1.length, sum2.length - sum1.length);
			stop = sum1.length; }
		if (stop > sum2.length) { System.arraycopy(sum1, sum2.length, ret, sum2.length, sum1.length - sum2.length);
			stop = sum2.length; }
		while (--stop >= start) {
			ret[stop] = (long) (sum1[stop] + sum2[stop]); }
		return ret; }

	/**
	  * Writes sum1 incremented by Incr into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] add(long[] ret, long[] sum1, int Incr) {
		return add(ret, sum1, Incr, 0, sum1.length); }

	/**
	  * Writes sum1 incremented by Incr into ret over the given range.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] add(long[] ret, long[] sum1, int Incr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (sum1[stop] + Incr); }
		return ret; }

	/**
	  * Returns a new array containing the elementwise sum of the two given arrays.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] add(long[] sum1, long[] sum2) {
		return add(sum1, sum2, 0, sum1.length); }

	/**
	  * Returns a new array of length stop containing the elementwise sum of the two
	  * given arrays over the given range.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] add(long[] sum1, long[] sum2, int start, int stop) {
		return add(new long[stop], sum1, sum2, start, stop); }

	/**
	  * Subtracts arr elementwise from ret, in place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] subAt(long[] ret, long[] arr) {
		return subAt(ret, arr, 0, arr.length); }

	/**
	  * Subtracts arr elementwise from ret over the given range, in place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] subAt(long[] ret, long[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= arr[stop]; }
		return ret; }

	/**
	  * Writes the elementwise difference (min - sub) into ret.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] subt(long[] ret, long[] min, long[] sub) {
		return subt(ret, min, sub, 0, sub.length); }

	/**
	  * Writes the elementwise difference (min - sub) into ret over the given range.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] subt(long[] ret, long[] min, long[] sub, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (min[stop] - sub[stop]); }
		return ret; }

	/**
	  * Returns a new array containing the elementwise difference min - sub.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] subt(long[] min, long[] sub) {
		return subt(min, sub, 0, sub.length); }

	/**
	  * Returns a new array containing the elementwise difference min - sub over
	  * the given range.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] subt(long[] min, long[] sub, int start, int stop) {
		return subt(new long[stop], min, sub, start, stop); }

	/**
	  * Writes the elementwise difference (min - sub) into ret over the given range,
	  * where min is a double array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] subt(long[] ret, double[] min, long[] sub, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (min[stop] - sub[stop]); }
		return ret; }

	/**
	  * Writes the elementwise difference (min - sub) into ret over the given range,
	  * where min is a float array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] subt(long[] ret, float[] min, long[] sub, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (min[stop] - sub[stop]); }
		return ret; }

	/**
	  * Returns a new array containing the elementwise difference min - sub,
	  * where min is a double array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] subt(double[] min, long[] sub) {
		return subt(min, sub, 0, sub.length); }

	/**
	  * Returns a new array containing the elementwise difference min - sub over the
	  * given range, where min is a double array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] subt(double[] min, long[] sub, int start, int stop) {
		return subt(new long[stop], min, sub, start, stop); }

	/**
	  * Returns a new array containing the elementwise difference min - sub,
	  * where min is a float array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] subt(float[] min, long[] sub) {
		return subt(min, sub, 0, sub.length); }

	/**
	  * Returns a new array containing the elementwise difference min - sub over the
	  * given range, where min is a float array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] subt(float[] min, long[] sub, int start, int stop) {
		return subt(new long[stop], min, sub, start, stop); }


	/**
	  * Multiplies every element of the given array by the given factor, in place.
	  * To implement divAt, just invert the Factor
	  * @param Factor the Factor to multiply with
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @return the given Array multiplied by the given Factor
	  */
	final static public long[] mulAt(long[] ret, int Factor) {
		return mulAt(ret, Factor, 0, ret.length); }

	/**
	  * Multiplies the elements in the given range by the given factor, in place.
	  * @return the Product of the Array with the given Factor
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Factor the Factor to multiply with
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] mulAt(long[] ret, int Factor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] *= Factor; }
		return ret; }

	/**
	  * Multiplies ret elementwise by arr, in place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] mulAt(long[] ret, long[] arr) {
		return mulAt(ret, arr, 0, arr.length); }

	/**
	  * Multiplies ret elementwise by arr over the given range, in place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] mulAt(long[] ret, long[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] *= arr[stop]; }
		return ret; }

	/**
	  * Writes the elementwise product of min and sub into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] mul(long[] ret, long[] min, long[] sub) {
		return mul(ret, min, sub, 0, sub.length); }

	/**
	  * Writes the elementwise product of min and sub into ret over the given range.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] mul(long[] ret, long[] min, long[] sub, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (min[stop] * sub[stop]); }
		return ret; }

	/**
	  * Returns a new array containing min scaled by the given factor.
	  * @return a new Array containing the Product of the given Array
	  * @param ret Array with the Values to be processed.
	  */
	final static public long[] mul(long[] min, int factor) {
		return mul(new long[min.length], min, factor, 0, min.length); }

	/**
	  * Writes min scaled by the given factor into ret.
	  * @return a new Array containing the Product of the given Array
	  * @param ret Array with the Values to be processed.
	  */
	final static public long[] mul(long[] ret, long[] min, int factor) {
		return mul(ret, min, factor, 0, min.length); }

	/**
	  * Writes min scaled by the given factor into ret over the given range.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] mul(long[] ret, long[] min, int factor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (min[stop] * factor); }
		return ret; }

	/**
	  * Returns a new array containing the elementwise product of the two given arrays.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] mul(long[] min, long[] sub) {
		return mul(min, sub, 0, sub.length); }

	/**
	  * Returns a new array containing the elementwise product of the two given
	  * arrays over the given range.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] mul(long[] min, long[] sub, int start, int stop) {
		return mul(new long[stop], min, sub, start, stop); }

	/**
	  * Divides ret elementwise by arr, in place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] divAt(long[] ret, long[] arr) {
		return divAt(ret, arr, 0, arr.length); }

	/**
	  * Divides ret elementwise by arr over the given range, in place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] divAt(long[] ret, long[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] /= arr[stop]; }
		return ret; }

	/**
	  * Writes the elementwise quotient of min divided by sub into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] div(long[] ret, long[] min, long[] sub) {
		return div(ret, min, sub, 0, sub.length); }

	/**
	  * Writes the elementwise quotient of min divided by sub into ret over the given range.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] div(long[] ret, long[] min, long[] sub, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (min[stop] / sub[stop]); }
		return ret; }

	/**
	  * Returns a new array containing the elementwise quotient of the two given arrays.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public long[] div(long[] min, long[] sub) {
		return div(min, sub, 0, sub.length); }

	/**
	  * Returns a new array containing the elementwise quotient of the two given
	  * arrays over the given range.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public long[] div(long[] min, long[] sub, int start, int stop) {
		return div(new long[stop], min, sub, start, stop); }


	/**
	 * Updates each element of ret, over the given range, to the greater of itself
	 * and the minimum of the corresponding element of a and the scalar y.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public long[] MaxMin(long[] ret, long[] a, long  y, int start, int stop) {
		long x; //FALSE; //can also start with any lower Value!
		while (--stop >= start) {
//			ret[stop] maxAt(a[stop] min b); } //equivalent and faster!
			if ((x = a[stop]) < y) { //use the Minimum
				if (ret[stop] < x) { //update the Maximum
					ret[stop] = x; }
			} else {
				if (ret[stop] < y) { //update the Maximum
					ret[stop] = y; }
			}
		} return ret; }

	/**
	 * Updates each element of ret, over the given range, to the greater of itself
	 * and the minimum of the corresponding elements of a and b.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min b)	  */
	final static public long[] MaxMin(long[] ret, long[] a, long[] b, int start, int stop) {
		long  x, y; //FALSE; //can also start with any lower Value!
		while (--stop >= start) {
//			ret[stop] maxAt(a[stop] min b[stop]); } //equivalent and faster!
			if ((x = a[stop]) <
				(y = b[stop])) { //use the Minimum
				if (ret[stop] < x) { //update the Maximum
					ret[stop] = x; }
			} else {
				if (ret[stop] < y) { //update the Maximum
					ret[stop] = y; }
			}
		} return ret; }

	/**
	 * Updates each element of ret to the greater of itself and the minimum of
	 * the corresponding elements of a and b, over the full array.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min b)	  */
	final static public long[] MaxMin(long[] ret, long[] a, long[] b) {
		return MaxMin(ret, a, b, 0, ret.length); }

	/**
	 * Updates each element of ret to the greater of itself and the minimum of
	 * the corresponding element of a and the scalar y, over the full array.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public long[] MaxMin(long[] ret, long[] a, long  y) {
		return MaxMin(ret, a, y, 0, ret.length); }


///////////////////////////////////////////////////////////////////////////////////
/// Ring Methods
///////////////////////////////////////////////////////////////////////////////////

	/// these Methods with scalar Parameters have been removed,
	/// because they can be replaced by their addAt and mulAt Counterparts.
	/**  Linear Mapping in Place: x+=a * y	 replaced by addAt(a*y)  */
//	final static public long[] addProdAt (long[] ret, int a, int y) {
	/**  Linear Mapping in Place: x-=a * y	 replaced by subAt(a*y)  */
//	final static public long[] subtProdAt(long[] ret, int a, int y) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by LinAt(a, y*b)  */
//	final static public long[] BiLinAt   (long[] ret, int a, int y, int b) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by LinAt(a, y*b)  */
//	final static public long[] BiLinAt   (long[] ret, long[] a, int y, int b) {

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, long[] a, int y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y; }
		return ret; }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, long[] a, long[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y[stop]; }
		return ret; }

	/**  Linear Mapping in Place: x + a*y	 */
	final static public long[] addProd(long[] ret, long[] x, long[] a, long y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (x[stop] + a[stop] * y); }
		return ret; }

	/**  Linear Mapping in Place: x + a*y	 */
	final static public long[] addProd(long[] ret, long[] x, long[] a, long[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (x[stop] + a[stop] * y[stop]); }
		return ret; }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, double[] a, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y; }
		return ret; }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, float[] a, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y; }
		return ret; }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, int a, long[] y, int start, int stop) {
		return addProdAt(ret, y, a, start, stop); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, double a, double[] y, int start, int stop) {
		return addProdAt(ret, y, a, start, stop); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, double a, double[] y) {
		return addProdAt (ret, y, a, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, double[] a, double y) {
		return addProdAt (ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, int a, long[] y) {
		return addProdAt (ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, long[] a, int y) {
		return addProdAt (ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProdAt (long[] ret, long[] a, long[] y) {
		return addProdAt (ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProd(long[] ret, long[] x, long a, long[] y, int start, int stop) {
		return addProd(ret, x, y, a, start, stop); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProd(long[] ret, long[] x, long a, long[] y) {
		return addProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProd(long[] ret, long[] x, long[] a, long y) {
		return addProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProd(long[] ret, long[] x, long[] a, long[] y) {
		return addProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProd(long[] x, long a, long[] y, int start, int stop) {
		return addProd(new long[stop], x, y, a, start, stop); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProd(long[] x, long a, long[] y) {
		return addProd(new long[x.length], x, a, y, 0, x.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProd(long[] x, long[] a, long y) {
		return addProd(new long[x.length], x, a, y, 0, x.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public long[] addProd(long[] x, long[] a, long[] y) {
		return addProd(new long[x.length], x, a, y, 0, x.length); }


	/**  Linear Mapping in Place: x -= a*y	 */
	final static public long[] subtProdAt(long[] ret, long[] a, int y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= a[stop] * y; }
		return ret; }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public long[] subtProdAt(long[] ret, long[] a, long[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= a[stop] * y[stop]; }
		return ret; }

	/**  Linear Mapping in Place: x - a*y	 */
	final static public long[] subtProd(long[] ret, long[] x, long[] a, int y, int start, int stop) {
		while (--stop >= start) {
			ret[stop]  = (long) (x[stop] - a[stop] * y); }
		return ret; }

	/**  Linear Mapping in Place: x - a*y	 */
	final static public long[] subtProd(long[] ret, long[] x, long[] a, long[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop]  = (long) (x[stop] - a[stop] * y[stop]); }
		return ret; }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public long[] subtProdAt(long[] ret, int a, long[] y, int start, int stop) {
		return subtProdAt(ret, y, a, start, stop); }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public long[] subtProdAt(long[] ret, int a, long[] y) {
		return subtProdAt(ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public long[] subtProdAt(long[] ret, long[] a, int y) {
		return subtProdAt(ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public long[] subtProdAt(long[] ret, long[] a, long[] y) {
		return subtProdAt(ret, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public long[] subtProd(long[] ret, long[] x, int a, long[] y, int start, int stop) {
		return subtProd(ret, x, y, a, start, stop); }

	/**  Linear Mapping: x - a*y	 */
	final static public long[] subtProd(long[] ret, long[] x, int a, long[] y) {
		return subtProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public long[] subtProd(long[] ret, long[] x, long[] a, int y) {
		return subtProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public long[] subtProd(long[] ret, long[] x, long[] a, long[] y) {
		return subtProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public long[] subtProd(long[] x, int a, long[] y, int start, int stop) {
		return subtProd(new long[stop], x, y, a, start, stop); }

	/**  Linear Mapping: x - a*y	 */
	final static public long[] subtProd(long[] x, int a, long[] y) {
		return subtProd(new long[x.length], x, a, y, 0, x.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public long[] subtProd(long[] x, long[] a, int y) {
		return subtProd(new long[x.length], x, a, y, 0, x.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public long[] subtProd(long[] x, long[] a, long[] y) {
		return subtProd(new long[x.length], x, a, y, 0, x.length); }


	/**  Linear Mapping in Place: x*=a + y	*/
	final static public long[] LinAt  (long[] ret, int a, int y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (ret[stop] * a + y); }
		return ret; }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public long[] LinAt  (long[] ret, long[] a, int y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (ret[stop] * a[stop] + y); }
		return ret; }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public long[] LinAt  (long[] ret, int a, long[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (ret[stop] * a + y[stop]); }
		return ret; }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public long[] LinAt  (long[] ret, long[] a1, long[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (ret[stop] * a1[stop] + y[stop]); }
		return ret; }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public long[] LinAt  (long[] ret, int a1, int y) {
		return LinAt  (ret, a1, y, 0, ret.length); }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public long[] LinAt  (long[] ret, long[] a1, int y) {
		return LinAt  (ret, a1, y, 0, ret.length); }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public long[] LinAt  (long[] ret, int a1, long[] y) {
		return LinAt  (ret, a1, y, 0, ret.length); }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public long[] LinAt  (long[] ret, long[] a1, long[] y) {
		return LinAt  (ret, a1, y, 0, ret.length); }


	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, long[] a, long[] y, long[] b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (x[stop] * a[stop] + y[stop] * b[stop]); }
		return ret; }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, long[] a, long[] y, int b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (x[stop] * a[stop] + y[stop] * b); }
		return ret; }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, int a, long[] y, long[] b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (x[stop] * a + y[stop] * b[stop]); }
		return ret; }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, int a, long[] y, int b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (long) (x[stop] * a + y[stop] * b); }
		return ret; }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, long[] a, int y, long[] b, int start, int stop) {
		return BiLin(ret, x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, int a, long[] y, int b) {
		return BiLin(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, long[] a, long[] y, int b) {
		return BiLin(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, long[] a, long[] y, long[] b) {
		return BiLin(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, int a, long[] y, long[] b) {
		return BiLin(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] ret, long[] x, long[] a, int y, long[] b) {
		return BiLin(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, long[] a, long[] y, long[] b, int start, int stop) {
		return BiLin(new long[x.length], x, a, b, y, start, stop); }

	/**BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, long[] a, long[] y, int b, int start, int stop) {
		return BiLin(new long[x.length], x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, int a, long[] y, long[] b, int start, int stop) {
		return BiLin(new long[x.length], x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, int a, long[] y, int b, int start, int stop) {
		return BiLin(new long[x.length], x, a, y, b, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, long[] a, int y, long[] b, int start, int stop) {
		return BiLin(new long[x.length], x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, int a, long[] y, int b) {
		return BiLin(new long[x.length], x, a, y, b, 0, x.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, long[] a, long[] y, int b) {
		return BiLin(new long[x.length], x, a, y, b, 0, x.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, long[] a, long[] y, long[] b) {
		return BiLin(new long[x.length], x, a, y, b, 0, x.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, int a, long[] y, long[] b) {
		return BiLin(new long[x.length], x, a, y, b, 0, x.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public long[] BiLin(long[] x, long[] a, int y, long[] b) {
		return BiLin(new long[x.length], x, a, y, b, 0, x.length); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public long[] BiLinAt(long[] ret, long[] a, long[] y, int b, int start, int stop) {
		return BiLin(ret, ret, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public long[] BiLinAt(long[] ret, int a, long[] y, long[] b, int start, int stop) {
		return BiLin(ret, ret, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public long[] BiLinAt(long[] ret, int a, long[] y, int b, int start, int stop) {
		return BiLin(ret, ret, a, y, b, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public long[] BiLinAt(long[] ret, long[] a, int y, long[] b, int start, int stop) {
		return BiLin(ret, ret, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public long[] BiLinAt(long[] ret, int a, long[] y, int b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public long[] BiLinAt(long[] ret, long[] a, long[] y, int b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public long[] BiLinAt(long[] ret, long[] a, long[] y, long[] b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public long[] BiLinAt(long[] ret, int a, long[] y, long[] b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public long[] BiLinAt(long[] ret, long[] a, int y, long[] b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length); }


	////////////////////////////////////////////////////////////////////////////////
	//	QuickSort Algorithm on Object[], @see Stream.Object.Enumerator.Container.Array
	////////////////////////////////////////////////////////////////////////////////

//	private static final Random ran = Math.random();	//for randomizing the Pivot Element and avoiding Overhead with nearly sorted Arrays.

	/**Divide and Conquer Method:
	 * A Separator Element is determined and all other Elements ordered around it.
	 * a[p..r] -> a[p..q] <= a[q+1..r]
	 * The Elements of the Items Array are expected to be of Type OrderAble	 */
	protected static int Partition(Object[] Items, int p, int r)	{
		Object tmp;			//for swapping
		IOrderAble Item;	//for comparing, chose OrderAble to avoid further Casting!!!
//		if (randomize)	Item = (OrderAble) Items[p + (r-p)*Random()];
//		else
						Item = (IOrderAble) Items[p];
		int i = p-1;
		int j = r+1;
		while (true) {	//swap all Items around the selected one
			do ; while (Item.isMoreThan(Items[--j])); //search for a greater Item
			do ; while (Item.isLessThan(Items[++i])); //search for a smaller Item
			if (i < j) {tmp = Items[j]; Items[j] = Items[i]; Items[i] = tmp;}	//swap both
			else return j;	//finished: all Elements left of j are smaller than those right of j
		}
	}

	/**QuickSort Algorithm:
	 * Divide and Conquer Method:
	 * The Array is divided into two, of which both are again sorted.	 */
	public static void QuickSort(Object[] Items) {
		QuickSort(Items, 0 , Items.length-1); }

	/**QuickSort Algorithm:
	 * Divide and Conquer Method:
	 * The Array is divided into two, of which both are again sorted.	 */
	public static void QuickSort(Object[] Items, int p, int r) {
		if (p >= r) return; // Items; //not effective to return since recursive!
		int q = Partition (Items, p,r);
		QuickSort(Items, p  ,q);
		QuickSort(Items, q+1,r);
	}

	/**Creates the i-th Order Statistic using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum	 */
	public static Object Statistic(Object[] Items, int i) {
		return Statistic(Items, 0, Items.length-1, i); }

	/**Creates the i-th Order Statistic using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum	 */
	public static Object Statistic(Object[] Items, int p, int r, int i) {
		if (p >= r) return Items[p];
		int q = Partition(Items, p, r);	//after this all Elements
		int k = q-p+1;
		if (i <= k) return Statistic(Items, p  , q, i  );	//
		else		return Statistic(Items, q+1, r, i-k);
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////////

	long[] items;
	
	////////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////////

	/**Constructs an empty VectorLong with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the VectorLong.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the VectorLong overflows.	 */
	public VectorLong(final int initialCapacity, final int capacityIncrement_) {
		this.items = new long[initialCapacity];
		this.capacityIncrement = capacityIncrement_;
		//		mEnum = new ArrayEnum(Items, ItemCount);
		//		mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty VectorLong with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the VectorLong.	 */
	public VectorLong(final int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty VectorLong.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public VectorLong() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/** Constructs an VectorLong by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public VectorLong(final Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an VectorLong from the given Object.	  */
	public VectorLong(final Object arg, final int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorLong from the given Object.	  */
	public VectorLong(final long[] arg, final int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorLong from the given Object
	  * and copies the Elements into this VectorLong.	  */
	public VectorLong(long[] arg) {
		this(arg.length, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns whether this vector's elements are strictly ascending, strictly
	 * descending, or in no consistent order.
	 * @return the Order of the Items in this Container
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	public byte getOrder() {
		int i = itemCount;
		long first = items[0];
		long  last = items[--i];
		boolean asc = (last > first);
		for (; --i >= 0;) {
			last = first;
			first = items[i];
			if (asc != (last > first)) {
				return IStreamIn.ORDER_NONE;
			}
		}
		return asc ? IStreamIn.ORDER_ASC : IStreamIn.ORDER_DESC;
	}

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorLong addItem(final long item) {
		setAt(itemCount, item);
		return this;
	}

	/**Copies the components of this VectorLong into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorLong.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	final public synchronized void copyInto(long[] anArray) {
		System.arraycopy(items, 0, anArray, 0, itemCount);
		/*		int i = ItemCount;
				Object elementDataLocal[] = this.Items;
				while (i-- > 0)
					anArray[i] = elementDataLocal[i];
		*/
	}

	/**Copies the components of this VectorLong into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorLong.
	 *
	 * @param   anArray   the array into which the components get copied.	 */
	final public synchronized long[] toArray() {
		long[] Return = new long[itemCount];
		System.arraycopy(items, 0, Return, 0, itemCount);
		return Return;
	}

	/**Trims the capacity of this VectorLong to be the VectorLong's current
	 * size. An application can use this operation to minimize the
	 * storage of a VectorLong.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			long[] oldData = items;
			items = new long[itemCount];
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
	}

	/**Returns the current capacity of this VectorLong.
	 *
	 * @return  the current capacity of this VectorLong.	 */
	final public int getCapacity() {
		return items.length;
	}

	/**Increases the capacity of this VectorLong, if necessary, 
	 * to ensure that it can hold at least the number of components 
	 * specified by the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final public synchronized int setCapacity(final int minCapacity) {
		final int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) 
			return oldCapacity;
		final int newCapacity = ENLARGED_CAPACITY(oldCapacity, capacityIncrement, minCapacity); 
		final long[] oldData = items; items = new long[newCapacity];
		if (itemCount > 0) 
			System.arraycopy(oldData, 0, items, 0, itemCount);
		return newCapacity;
	}

	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was
	 *			 given.	 */
	public synchronized long getLongAt(final int index) {
		if (indexInRange(index)) 
			return items[index];
		return 0;
	}

	/**
	 * Returns the item at the given position boxed as a {@link ByRefLong}.
	 * @return the item at the given Position as an Object
	 */
	public Object getAt(final int index) {
		return new ByRefLong(getLongAt(index)); }

	/**
	 * Sets the element at the given index from the given boxed value, growing the
	 * vector when the index is out of range.
	 * @return the previous value boxed as a {@link ByRefLong}, or null when the index
	 * was out of range and item was null
	 */
	public Object setAt(final int index, final Object item) {
		//return new ByRefChar(setAt(index, (long) ByRefInt.GET_INT(value))); 
		Object ret = null; //allows to distinguish between 0 and Out of Range
		if (indexInRange(index))
			ret = new ByRefLong(items[index]);
		else {
			if (item == null)
				return  null;
			setSize(index+1);
		}
		items[index] = (long) ByRefInt.TO_INT(item); 
		return ret; }

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
	public long setAt(final int index, final long value) {
		long ret = 0; 
		if (indexInRange(index)) 
			ret = items[index];
		else 
			setSize(index+1);
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
	public void insertAt(final int index, final long value) {
		if (index >= itemCount) { //
			setAt(index, value);
		} else {
			setCapacity(++itemCount);
			System.arraycopy(items, index, items, index+1, itemCount-index); 
			items[index] = value;
		}
	}

	/**removes the Value at the specified index.
	 * All following components in this Container are shifted to the left.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * @param	  index   the index of the object to remove.
	 * @return	 the value removed.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 */
	public long removeAt(final int index) {
		if (index < 0 || index > itemCount - 1)  //
			return 0;
		--itemCount;
		final long ret = items[index]; 
		System.arraycopy(items, index+1, items, index, itemCount-index); 
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX) 
	/// for multidimensional rectangular Arrays 
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value at the given row and column, viewing this vector's backing
	 * array as a rectangular matrix.
	 * @return the Value at the given Position	 */
	public long getAt(final int Row, final int Col) {
		return items[Row * dimFactors[0] + Col * dimFactors[1]]; }

	/** sets the given Value 	 */
	public void setAt(final int Row, final int Col, final long Value) {
		items[Row * dimFactors[0] + Col * dimFactors[1]] = Value; }

	/**
	 * Returns the value at the given sheet, row and column, viewing this vector's
	 * backing array as a rectangular 3D tensor.
	 * @return the Value at the given Position	 */
	public long getAt(final int Sheet, final int Row, final int Col) {
		return items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]]; }

	/** sets the given Value 	 */
	public void setAt(final int Sheet, final int Row, final int Col, final long Value) {
		items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]] = Value; }

	/**
	 * Returns the value at the given multi-dimensional index of this vector viewed
	 * as a rectangular multi-index array.
	 * @return the Value at the given Position	 */
	public long getAt(final int[] Col) {
		return items[multiIndex(Col)]; }

	/** sets the given Value 	 */
	public void setAt(final int[] Col, final long Value) {
		items[multiIndex(Col)] = Value; }

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
	public VectorLong copyAt(final long[] arg_) {
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
	public ICopyAble copyAt(Object arg) {
		if (arg instanceof VectorLong) {
			VectorLong arg_ = (VectorLong) arg;
			capacityIncrement = arg_.capacityIncrement;
			setCapacity(arg_.itemCount);
			itemCount = arg_.itemCount;
			System.arraycopy(arg_.items, 0, items, 0, itemCount);
		} else
			super.copyAt(arg); //no need to use a recursive DeepCopy like with Tensor
		return this;
	}

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) {
		if (arg instanceof VectorLong) {
			VectorLong arg_ = (VectorLong) arg;
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
		return new VectorLong(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Multiplication with a Permutation
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the minimum value in this vector.
	 * @return the Minimum Value in this Vector	 */
	public long MinVal() { return MIN_VAL(items); }

	/**
	 * Returns the position of the minimum value in this vector.
	 * @return the Position of the Minimum Value in this Vector	 */
	public int MinPos() { return MIN_POS(items); }

	/**
	 * Returns the maximum value in this vector.
	 * @return the Maximum Value in this Vector	 */
	public long MaxVal() { return MAX_VAL(items); }

	/**
	 * Returns the position of the maximum value in this vector.
	 * @return the Position of the Maximum Value in this Vector	 */
	public int MaxPos() { return MAX_POS(items); }
	
	///////////////////////////////////////////////////////////////////////////
	/// arithmetic Operations
	///////////////////////////////////////////////////////////////////////////
	
	/** Normalizes this Vector by bringing it into the canonical Form
	 * so that getAt(getInt()) != 0 
	 * 
	 * Mind that the Canonical Form for Permutations 
	 * asserts that getAt(i) = i for large i
	 */
	public VectorLong normalizeAt() {
		while (items[--itemCount] == 0);
		++itemCount;
		return this;
	}
	
	/** adds the given Portion of the values to this Vector */
	public VectorLong addAt(final VectorLong vector) {
		return addAt(vector.items, 0, vector.itemCount); }

	/** subtracts the given Portion of the values from this Vector */
	public VectorLong subAt(final VectorLong vector) {
		return subAt(vector.items, 0, vector.itemCount); }

	/** multiplies this Vector by the given Portion of the values */
	public VectorLong mulAt(final VectorLong vector) {
		return mulAt(vector.items, 0, vector.itemCount); }

	/** divides this Vector by the given Portion of the vector*/
	public VectorLong divAt(final VectorLong vector) {
		return divAt(vector.items, 0, vector.itemCount); }

	/** subtracts the given Portion of the values from this Vector */
	public VectorLong subAt(final long[] values, int start, int stop) {
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
	public VectorLong addAt(final int value) {
		VectorLong.addAt(items, value, 0, itemCount);
		return this; }

	/** adds the given Portion of the values to this Vector */
	public VectorLong subAt(final int value) {
		VectorLong.addAt(items, -value, 0, itemCount);
		return this; }

	/** multiplies this Vector by the given Portion of the values */
	public VectorLong mulAt(final int value) {
		VectorLong.mulAt(items, value, 0, itemCount);
		return this; }

	/** divides this Vector by the given Portion of the vector*/
	public VectorLong divAt(final int value) {
		for (int i = itemCount; i-- > 0;)
			items[i] /= value;
		return this; }

	/** adds the given Portion of the values to this Vector */
	public VectorLong addAt(final long[] values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			copyAt(items, values, itemCount, stop);
			addAt(items, values, start, itemCount);
			//			normalizeAt();
		} else if (stop < itemCount) { //don't need to (re-)normalize
			addAt(items, values, start, stop);
		} else {
			addAt(items, values, start, stop);
			//			normalizeAt();
		}
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorLong mulAt(final long[] values, final int start, int stop) {
		while (values[--stop] == 0); //normalize 
		++stop;
		if (stop >= itemCount) {
			stop = itemCount; //all other Values are multiplied by 0
		} else {
			itemCount = stop; //all other Values are multiplied by 0
		}
		mulAt(items, values, start, itemCount);
		//normalizeAt(); //don't need to normalize 
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorLong divAt(final long[] values, final int start, int stop) {
		while (values[--stop] == 0); //normalize 
		++stop;
		if (stop >= itemCount) {
			stop = itemCount; //all other Values are multiplied by 0
		} else {
			itemCount = stop; //all other Values are divided by 0 and become Infinity!
		}
		divAt(items, values, start, itemCount);
		//normalizeAt(); //don't need to normalize 
		return this;
	}

	/**Multiply the Vector by an Object in Place.
	 * This extends the standard Set Multiplication
	 * by the Multiplication with a Permutation.	 */
	/*	  public SemiGroupM mulAt(Object arg)	{
			  if (arg instanceof Permutation) {
				  copyAt(Permutation.map(Items, Items.length, (Permutation) arg));
				  return this; }
			  if (arg instanceof long[]) {
				  long[] arg_ = (long[]) arg;
				  copyAt(Permutation.map(Items, Items.length, arg_, arg_.length));
				  return this; }
			  return super.mulAt(arg); }
	*/
	/**Multiply the Vector by an Object.
	 * This extends the standard Set Multiplication
	 * by the Multiplication with a Permutation.	 */
	/*	  public SemiGroupM mul(Object arg) {
			  if (arg instanceof Permutation) return new VectorLong(Permutation.map(Items, Items.length, (Permutation) arg), capacityIncrement);
			  if (arg instanceof long[]	  ) return new VectorLong(Permutation.map(Items, Items.length, (long[]	  ) arg), capacityIncrement);
			  return super.mul(arg); }
	*/

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors for usage as a rectangular, multidimensional Array
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor only for use within this Class
	 * to generate different Indexing Schemes on the same Data
	 * when using a 1-dim Array for rectangular Addressing.
	 * @param Values, the backing Values of the Matrix,
	 *  possibly shared with other Matrices.
	 * @param Factors the Factors for the Items in the Tensor
	 */
	protected VectorLong(final long[] Values, final int[] Factors) {
		this.dimFactors = Factors;
		this.items = Values;
	}

	/**
	 * @param Rows the Numbers of Rows    in the Matrix
	 * @param Cols the Numbers of Columns in the Matrix
	 */
	/*	public VectorLong(int Rows, int Cols) {
			this.dimSizes = new long[2];
			this.dimSizes[0] = Cols;
			this.dimSizes[1] = Rows;
			dimFactors = new long[2];
			dimFactors[1] = 1;
			dimFactors[2] = Cols;
			items = new double[Rows * Cols];
		}
	*/
	/**
	 * @param Cols the Numbers of Columns in the Tensor
	 */
/*	public VectorLong(long[] Cols) {
		this.dimSizes = Cols;
		int Factor, i = Cols.length;
		dimFactors = new long[i];
		Factor = 1; //last Index has smallest Factor
		while (--i >= 0) {
			dimFactors[i] = Factor;
			Factor *= Cols[i];
		}
		items = new double[Factor];
	}
*/
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a view of this vector's backing array with row and column index
	 * factors swapped, sharing the same underlying data.
	 * @return a VectorLong with IndexFactors such
	 *  that the Elements are transposed.
	 * Only useful when simulating a rectangular Tensor on a 1 dim Array.
	 */
	public VectorLong getTranspose() {
		if (dimFactors.length != 2) 
			throw new InvalidParameterException("For Tensors please determine the Dimensions to transpose!");
		int[] Factors = new int[2];
		Factors[0] = dimFactors[1]; //Just permuting the Factors is sufficient!
		Factors[1] = dimFactors[0]; //also for Tensors of higher Degrees!
		return new VectorLong(items, Factors);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Optimizations
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + VectorLong.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

	/**Converts an Array of simple Type Contstants
	 * into an Array of the corresponding Object Type
	 * could be programmed slower but more generic using the Reflection API!	 */
	final static public Long[] const2Const(final long[] arg) {
		int len = arg.length;
		Long[] ret = new Long[len];
		while (--len >= 0)
			ret[len] = new Long(arg[len]);
		return ret; }

}

/** Iterator for the MatrixFloat Class (in reverse Order)
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T13:19:50Z
 * digest: 6ce97631064df1ad0c3887dabb0e50d219c28125fcc58810e01c0c90fc444d99
 * stale: false
 * tags: [code/functional_interfaces]
 * concepts: [Reverse-Order Long Stream Source]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
final class VectorLongStreamIn 
extends AVectorStreamIn_Int
{
	
	final VectorLong vector;

	/**
	 * Constructs a reverse-order iterator over the given vector's elements.
	 * @param vector_ the vector to iterate over
	 */
	public VectorLongStreamIn(final VectorLong vector_) {
		super((int) vector_.MaxVal()); //
		this.vector = vector_;
		pos = vector.getInt();
	}

	/**
	 * Returns the minimum value in the wrapped vector.
	 * @see Stream.Float.IStreamIn_Bound_Int#getMinValue()	 */
	public long getMinValue() { return vector.MinVal(); }

	/** @see Stream.Float.IStreamIn_Int#nextInt()	 */
	protected long nextLongInternal() { return vector.items[--pos]; }

    /**
     * Returns the number of elements in the wrapped vector, used as the maximum mark size.
     * @see streamIO.real.AStreamIn_Float#getMaxMarkSize()     */
    public long getMaxMarkSize() { return vector.getInt(); }
    
}
