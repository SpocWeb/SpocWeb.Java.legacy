package math.vector;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import math.NumberFormatter;
import math.matrix.MatrixFloat;
import streamIO.Assert;
import streamIO.IOrdered;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.integer.jdbc.AResultSet;
import streamIO.integer.random.RandomQuick;
import streamIO.real.IStreamIn_Float;
import function.IFloatFunction;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import function.vector.IFloatScalarField;
import function.vector.IFloatVectorField;

/**
 * Provides static Methods and a dynamic Array Type for Vectors and Arrays of primitive float Numbers.
 * Title: VectorFloat<p>
 * Description:
 * Defines only static Methods to treat Vectors and Arrays with float Numbers.
 * TODO: many of the Methods need to be made more robust 
 * against differing Dimensions of Arguments like e.g. addProd(float[], float[], float[])
 * @see streamIO.Copy.IGroup.IRing.IMetric.Body.Vector.VectorDbl
 * @see math.vector.HunterFloat for Methods on Sorting, Searching and Rank Statistics like Median and Percentiles
 * 
 * Float-Properties:
 * Bits:     32 = 8*4Byte
 * Mantissa: 23 ~ 8*3Byte  => 53 Bits ^ 7 Digits Accuracy
 * Exponent:  8 = 8*1Byte  => 11 Bits ^ +/- 38 Exponent
 * Sign:      1 =   1 Bit
 * abs.Range: 1.5e-45 to 3.4028235e+38
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
 * mtime: 2026-09-05T13:49:26Z
 * digest: 17ee4d954b6a5cc8cc0ab5b97284d1c6f25145277e08f62cd4b7623962571610
 * stale: false
 * tags: [code/growable_array, code/array_math]
 * concepts: [Growable float[] Vector]
 * facets: {layer: domain, status: broken, complexity: high}
 * -->
 */
public class VectorFloat 
extends AVector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(VectorFloat.class, 1);
	
	/**
	 * The Entropy is between 
	 * 0 when all Values are the same and 
	 * ln(N) when all Values lie equidistant. 
	 * The Vector is assumed to be sorted (ascending or descending) 
	 * Entropy does NOT reflect the Sequence of the Data, only it's Distribution. 
	 * @see VectorInt#ENTROPY(int[]) for the Entropy of binned Data (loss of Information though!)
	 * @return the Entropy of the given continuous Vector of Measurements. 
	 */
	public double Entropy() { return ENTROPY(items, itemCount, 0); }
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Entropy Calculation for continuous sampled Measurements
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The Entropy is between 
	 * 0 when all Values are the same and 
	 * ln(N) when all Values lie equidistant. 
	 * @see VectorInt#ENTROPY(int[]) for the Entropy of binned Data (loss of Information though!)
	 * @param x the Vector has to be sorted (ascending or descending) 
	 * @return the Entropy of the given continuous Vector of Measurements. 
	 */
	static final public double ENTROPY(final float[] x) { return ENTROPY(x, x.length, 0); }
	
	/**
	 * The Entropy is between 
	 * 0 when all Values are the same and 
	 * ln(N) when all Values lie equidistant. 
	 * @see VectorInt#ENTROPY(int[]) for the Entropy of binned Data (loss of Information though!)
	 * @param x the Vector has to be sorted (ascending or descending) 
	 * @return the Entropy of the given continuous Vector of Measurements. 
	 */
	static final public double ENTROPY(final float[] x, final int stop) {
		return ENTROPY(x, stop, 0); }
	
	/**
	 * The Entropy is between 
	 *    0  minimum when all Values are the same and 
	 * ln(N) maximum when all Values lie equidistant. 
	 * @see VectorInt#ENTROPY(int[]) for the Entropy of binned Data (loss of Information though!)
	 * @param x the Vector has to be sorted (ascending or descending) 
	 * @return the Entropy of the given continuous Vector of Measurements. 
	 */
	static final public double ENTROPY(final float[] x, final int stop, final int start) {
		double entropy = 0;	//sort_at()
		final float norm = x[stop-1] - x [start]; //makes it immutable against affine Trafos. 
		for(int i = stop; --i > start; ) { 		//diff_at()
			final float dx = (x[i] - x[i-1])/norm; //<= 1
			if (dx > 0) 		//norm_at()
				entropy += Math.log(dx)*dx; //max. Value: log(N)/N
		} //heuristic Formula; strange that multiplying with dx instead of dividing by it! 
		return entropy; 
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Matrix Trafos: extracting a Column
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**Swaps the Columns of this Tensor in Place	 */
	final static public float[] SWAP_AT(final float[] a, final int dim1, final int dim2) {
		if (a != null) {
			final float swap = a[dim1]; a[dim1] = a[dim2]; a[dim2] = swap; }
		return a;
	}
	
	/** Extracts a single Column from the given rectangular Matrix.
	 * @return the Column at the given Position */
	final static public float[] COLUMN(final float[][] matrix, final int col) {
		float[] ret = new float[matrix.length];
		for (int i = matrix.length; --i >= 0;) {
			ret[i] = matrix[i][col]; }
		return ret;
	}

	/** Transposes the given rectangular Matrix by extracting each Column as a Row.
	 * @return the Column at the given Position */
	final static public float[][] TRANSPOSE(final float[][] matrix) {
		float[][] ret = new float[matrix[0].length][];
		for (int i = ret.length; --i >= 0;) {
			ret[i] = COLUMN(matrix, i); }
		return ret;
	}

	/** 
	 * returns the Normal of the Plane through the given Points
	 * Optimization for reusing the temporary Working Space
	 * @param diff1 a temporary working Array of Length 3
	 * @param diff2 a temporary working Array of Length 3
	 * @param p0 Point in the Plane 
	 * @param p1 Point in the Plane
	 * @param p2 Point in the Plane
	 * @return the Normal of the Plane through the given Points
	 */
	public static float[] NORMAL(
		final float[] diff1,
		final float[] diff2,
		final float[] p0,
		final float[] p1,
		final float[] p2, 
		final boolean normalize) {
		SUB(diff1, p1, p0);
		SUB(diff2, p2, p0);
		final float[] prod = MatrixFloat.MUL_CROSS(diff1, diff2);
		if (normalize) 
			VectorFloat.NORMALIZE_AT(prod); 
		return prod;
	}

	/** 
	 * returns the Normal of the Plane through the given Points
	 * @param diff1 a temporary working Array of Length 3
	 * @param diff2 a temporary working Array of Length 3
	 * @param p0 Point in the Plane 
	 * @param p1 Point in the Plane
	 * @param p2 Point in the Plane
	 * @return the Normal of the Plane through the given Points
	 */
	public static float[] NORMAL(
		final float[] p0,
		final float[] p1,
		final float[] p2, 
		final boolean normalize) {
		return NORMAL(null, null, p0, p1, p2, normalize); 
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Database Operations:
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reads a single Point from the current ResultSet
	 * @param Cols determines the Columns used to read the Coordinates.
	 * It can be null, then the Routine tries to read as many Values as possible,
	 * but provoking and catching Exceptions is expensive and should be avoided!
	 * @return false, if the ResultSet was empty.
	 */
	final static public float[] READ_VECTOR(final ResultSet RS, float[] point, final int[] Cols) 
	throws SQLException {
		if (!RS.next()) 
			return null;
		int len;
		if (point != null) {
			len = point.length;
		} else {
			len = ((AResultSet) RS).getNumCols();
			if (Cols != null) {
				if (len > Cols.length) 
					len = Cols.length;
				point = new float[len];
			}
		}
		int i = -1; //start in the correct Order
		try { //try to read as many Coordinates as possible!
			while (++i < len) {
				if (Cols == null) {
					point[i] = RS.getFloat(i);
				} else {
					point[i] = RS.getFloat(Cols[i]);
				}
			}
		} catch (final Exception x) { //Resize the Array.
			float[] tmp = new float[i];
			System.arraycopy(point, 0, tmp, 0, i);
			point = tmp;
		}
		return point;
	}

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public float[] READ_VECTOR(final ResultSet RS, float[] point) 
	throws SQLException {
		return READ_VECTOR(RS, point, null);
	}

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public float[] READ_VECTOR(final ResultSet RS) 
	throws SQLException {
		return READ_VECTOR(RS, null, null);
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Conversion between Polar and Rectangular Coordinates
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the polar Representation of a Vector
	 * to it's rectangular Representation.
	 * By Convention the Length is the first Coordinate.
	 * By setting dim to lower Values than the maximum Dimension
	 * mixed Coordinate Systems can be calculated like Cylinder Coordinates.
	 */
	final static public float[] POLAR_2_RECT_AT(final float[] ret) {
		return POLAR_2_RECT_AT(ret, ret.length);
	}

	/**
	 * Converts the polar Representation of a Vector up to the given Dimension
	 * to it's rectangular Representation.
	 * By Convention the Length is the first Coordinate.
	 * By setting dim to lower Values than the maximum Dimension
	 * mixed Coordinate Systems can be calculated like Cylinder Coordinates.
	 * 
	 * @param ret
	 * @param dim
	 * @return
	 */
	final static public float[] POLAR_2_RECT_AT(final float[] ret, int dim) {
		float tmp, length = ret[0];
		if (dim > 0) { //transform to Unit Space
			ret[1] -= Math.round(ret[1]/IMeasurAble.TWO_PI)*IMeasurAble.TWO_PI;
			final boolean signOf0 = (Math.abs(ret[1]) > IMeasurAble.PI_HALF);
			while (--dim > 0) {
				ret[dim] = length * (tmp = (float) Math.sin(ret[dim]));
				length *= Math.sqrt(1 - tmp * tmp);
			} //cos()
			//if (Math.abs(ret[1]) < IMeasurAble.PI_HALF) {
			if (signOf0) {
				ret[0] = -length;
			} else {
				ret[0] = length;
			}
		}
		return ret;
	}

	/**
	 * Converts the rectangular Representation of a Vector
	 * to it's polar Representation.
	 *
	 * By Convention the Length is the first Coordinate.
	 * By setting dim to lower Values than the maximum Dimension
	 * mixed Coordinate Systems can be calculated like Cylinder Coordinates.
	 */
	final static public float[] RECT_2_POLAR_AT(float[] ret) {
		return RECT_2_POLAR_AT(ret, ret.length);
	}

	/**
	 * Converts the rectangular Representation of a Vector
	 * to it's polar Representation.
	 *
	 * By Convention the Length is the first Coordinate.
	 * By setting dim to lower Values than the maximum Dimension
	 * mixed Coordinate Systems can be calculated like Cylinder Coordinates.
	 */
	final static public float[] RECT_2_POLAR_AT(float[] ret, int dim) {
		int i = 0;
		double length = ret[0]; //--dim];
		double sqrLength = length * length;
		float tmp;
		while (++i < dim) {
			ret[i] = (float) Math.atan((tmp = ret[i]) / length);
			sqrLength += tmp * tmp;
			length = Math.sqrt(sqrLength);
		}
		if (ret[0] < 0) 
			ret[1] += Math.PI;
		ret[0] = (float) length;
		return ret;
	}

	/**
	 * Calculates the full euklidean Adjacency Vector for the given Row/Point.
	 * generated from all the Distances between this and the other Points.
	 * ret[i] = {Sum(j), Vectors[Row,j]*Vectors[i,j]}
	 *
	 * Actually calculatind the Differences ad hoc for each Point saves Memory,
	 * but cannot exploit the Fact that ret[i][j] = ret[j][i]
	 * and thus doubles the Calculation Effort compared to
	 * @see MatrixDouble.DistMatrix()
	 */
	final static public float[] DISTANCES(float[] ret, float[][] Vectors, int Row) {
		ret[Row] = 0; //not necessary, because new Array contains 0s already!
		float[] I = Vectors[Row]; //initialize the whole Matrix, O(V^2)
		int j = Vectors.length;
		while (--j >= 0) { //symmetric Matrix //calculate only 50%!
			if (j == Row)
				continue;
			ret[j] = (float) Math.sqrt(DIST_SQR(I, Vectors[j])); //Symmetric!
		}
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for dynamic growing Array Operations
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the Value at the given Position in the Array
	 * Returns a resized (larger OR smaller) Copy of the given Array
	 * filled with the given Value at the given Position.
	 */
	final static public float[] SET_AT(float[] arr, final int pos_, final float value_) {
		if (pos_ >= arr.length) 
			arr = SET_CAPACITY(pos_ + 1, arr);
		arr[pos_] = value_;
		return arr;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	final static public float[] SET_SIZE(final float[] arr, final int newExactSize) {
		return RESIZE(arr, newExactSize, arr.length);
	}

	/** Returns a resized (larger) Copy of the given Array */
	final static public float[] SET_CAPACITY(final int newMinSize, final float[] arr) {
		if (arr == null) { 
			return new float[newMinSize]; 
		}
		return RESIZE(arr, ENLARGED_CAPACITY(arr.length, DEFAULT_CAPACITY_INCR, newMinSize), arr.length);
	}

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	final static public float[] RESIZE(final float[] arr, final int newSize, int numToRetain) {
		float[] ret = new float[newSize];
		if (numToRetain > arr.length) {
			numToRetain = arr.length;
		}
		if (numToRetain > ret.length) {
			numToRetain = ret.length;
		}
		if (numToRetain > 0) {
			System.arraycopy(arr, 0, ret, 0, numToRetain);
		}
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Compares the given Range of both Arrays Element by Element for Equality (via ByRefFloat.EQUALS).
	 * @see Object#equals(java.lang.Object) 	 */
	final static public boolean EQUALS(final float[] a, final float[] b, final int start, final int stop) {
		for (int i = stop; --i >= start; ) {
			if (!ByRefFloat.EQUALS(a[i], b[i])) 
				return false; 
		}
		return true; 
	}

	/** Compares both Arrays for Equality, treating missing trailing Elements as 0.
	 * @see Object#equals(java.lang.Object) 	 */
	final static public boolean EQUALS(final float[] a, final float[] b) {
		if (a == b) 
			return true; 
		if (a == null) 
			return IS_ZERO(b); 
		if (b == null) 
			return IS_ZERO(a); 
		if (a.length > b.length) 
			return EQUALS(a, b, 0, b.length) && IS_ZERO(a, b.length, a.length);
		else 
			return EQUALS(a, b, 0, a.length) && IS_ZERO(b, a.length, b.length);
	}
	
	/**Converts an Array of simple Type Contstants
	 * into an Array of the corresponding Object Type
	 * could be programmed slower but more generic using the Reflection API!	 */
	final static public Float[] CONVERT(final float[] arg) {
		int len = arg.length;
		Float[] ret = new Float[len];
		while (--len >= 0)
			ret[len] = new Float(arg[len]);
		return ret; }
	
	/**Converts the float[] Array to a double[] Array	 */
	final static public float[] CONVERT(final double[] data) {
		int i = data.length;
		float[] fData = new float[i];
		while (--i >= 0) {
			fData[i] = (float) data[i]; } 
		return fData; }
	
	/** Returns a Copy of the given Array */
	final static public float[] COPY(final float[] arr, final int length) {
		return COPY(arr, 0, length, null); }

	/** Returns a Copy of the given Array */
	final static public float[] COPY(final float[] arr) {
		return COPY(arr, 0, arr.length, null); }

	/** Returns a Copy of the given Array */
	final static public float[] COPY(final float[] ret, final double[] arr) {
		return COPY(arr, 0, arr.length, null); }

	/** Returns a Copy of the given Array */
	final static public float[] COPY(final int[] arr) {
		return COPY(arr, null); }
	
	/** Returns a Copy of the given Array */
	final static public float[] COPY(final double[] arr) {
		return COPY(arr, null); }
	
	/** Returns a Copy of the given Array */
	final static public float[] COPY(final int[] arr, final float[] _ret) {
		return COPY(arr, 0, arr.length, _ret); }

	/** Returns a Copy of the given Array */
	final static public float[] COPY(final float[] arr, final float[] _ret) {
		return COPY(arr, 0, arr.length, _ret); }

	/** Returns a Copy of the given Array */
	final static public float[] COPY(final float[] arr, final int start, final int stop, float[] _ret) {
		if (_ret == null)
			_ret =  new float[stop]; 
		System.arraycopy(arr, start, _ret, start, stop);
		return _ret;
	}

	/** Returns a Copy of the given Array */
	final static public float[] COPY(final double[] arr, final float[] _ret) {
		return COPY(arr, 0, arr.length, _ret); }

	/** Returns a Copy of the given Array */
	final static public float[] COPY(final double[] arr, final int start, final int stop, float[] _ret) {
		if (_ret == null)
			_ret =  new float[stop]; 
		for(int i = stop; --i >= 0; ) 
			_ret[i] = (float) arr[i]; 
		//System.arraycopy(arr, start, this_, start, stop);
		return _ret;
	}
	
	/** Returns a Copy of the given Array */
	final static public float[] COPY(final int[] arr, final int start, final int stop, float[] _ret) {
		if (_ret == null)
			_ret =  new float[stop]; 
		for(int i = stop; --i >= 0; ) 
			_ret[i] = arr[i]; 
		//System.arraycopy(arr, start, this_, start, stop);
		return _ret;
	}

	/**Converts the Array IMeasurAble[] to float[]      */
	final static public float[] COPY(final IMeasurAble[] arg) {
		return COPY(arg, 0, arg.length, null); }

	/**Converts the Array IMeasurAble[] to float[]      */
	final static public float[] COPY(final IMeasurAble[] arg, final int start, final int stop, float[] _ret) {
		if (_ret == null)
			_ret =  new float[stop]; 
		for(int i = stop; --i >= 0; ) 
			_ret[i] = arg[i].getFloat(); 
		return _ret; 
	}
	
	/**Converts the Array IMeasurAble[] to float[]	  */
	final static public float[] COPY(final Number[] arg) { 
		return COPY(arg, 0, arg.length, null); }
	
	/**Converts the Array IMeasurAble[] to float[]	  */
	final static public float[] COPY(final Number[] arg, final int start, final int stop, float[] _ret) {
		if (_ret == null)
			_ret =  new float[stop]; 
		for(int i = stop; --i >= 0; ) 
			_ret[i] = arg[i].floatValue(); 
		return _ret; }
	
	/**Converts the Array Object[] to float[]	  */
	final static public float[] COPY(final Object[] arg) {
		if (arg instanceof IMeasurAble[])
			return VectorFloat.COPY((IMeasurAble[]) arg);
			return VectorFloat.COPY((Number[]    ) arg); }
	
	/**
	 * Setting the Vectors to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vectors fit, they are returned unchanged!
	 */
	final static public float[][] SET_DIM_AT(final float[][] a, final int dim) {
		for (int i = a.length; --i >= 0; ) 
			a[i] = SET_DIM_AT(a[i], dim);
		return a;
	}

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public float[] SET_DIM_AT(final float[] a, final int dim) {
		if (a.length == dim) 
			return a;
		float[] ret = new float[dim];
		System.arraycopy(a, 0, ret, 0, a.length);
		Arrays.fill(ret, a.length, dim, 0);
		return a;
	}

	/** Sets all Elements of the given Array to 0.
	 * @return the given Array with all Elements set to 0. 	 */
	final static public float[] ZERO_AT(final float[] ret) {
		return ZERO_AT(ret, 0, ret.length); }

	/** Sets the given Range of the Array to 0.
	 * @return the given Array with the Elements from start (inclusive) to stop (exclusive) set to 0. 	 */
	final static public float[] ZERO_AT(float[] ret, int start, int stop) {
		java.util.Arrays.fill(ret, start, stop, 0);
		return ret; }

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag.
	 * i.e. a[dim] = 1 and a[j] = 0 otherwise.
	 */
	final static public float[] ONE_AT(float[] a, int dim) {
		return DIAG_AT(a, 1, dim); }

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag,
	 * i.e. a[dim] = diag and a[j] = 0 otherwise.
	 */
	final static public float[] DIAG_AT(float[] a, float diag, int dim) {
		Arrays.fill(a, 0);
		a[dim] = diag;
		return a;
	}

	/** Sets all Elements of the given Array to 1.
	 * @return the given Array with all Elements set to 1. 	 */
	final static public float[] ONE_AT(float[] ret) {
		return ONE_AT(ret, 0, ret.length);
	}

	/** Sets the given Range of the Array to 1.
	 * @return the given Array with the Elements from start (inclusive) to stop (exclusive) set to 0. 	 */
	final static public float[] ONE_AT(float[] ret, int start, int stop) {
		java.util.Arrays.fill(ret, start, stop, 1);
		return ret;
	}

	/** Fills the whole Array with the given Value.
	 * @return the given Array with all Elements set to the given Value. 	 */
	final static public float[] FILL_AT(float[] ret, float val) {
		return FILL_AT(ret, val, 0, ret.length);
	}

	/**
	 * Fills the given Range of the Array with the given Value.
	 * @return the given Array with the Elements from start (inclusive)
	 * to stop (exclusive) set to the given Value.
	 */
	final static public float[] FILL_AT(float[] ret, float val, int start, int stop) {
		java.util.Arrays.fill(ret, start, stop, val);
		return ret;
	}

	/** Checks whether the whole Array is all-zero, empty, or null.
	 * @return true if
	 * all Values in the Array are zero or
	 * the Array has zero Length or
	 * the Array is null.
	 */
	final static public boolean IS_ZERO(final float[] arr) {
		if ((arr == null) || (arr.length == 0)) {
			return true; }
		return IS_ZERO(arr, 0, arr.length);
	}

	/** Checks whether the given Range of the Array is all-zero.
	 * @return true if
	 * all Values in the Array are zero in the given Range
	 */
	final static public boolean IS_ZERO(final float[] arr, final int start, int stop) {
		while (--stop >= start) {
			if (arr[stop] != 0) { //> IMeasurAble.FLOAT_ACCURACY) 
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether this Vector is a Unity Vector in the given Dimension
	 */
	final static public boolean IS_ONE(final float[] Row, final int dim) { //Assume a square Matrix
		int j = Row.length;
		while (--j >= 0) { //Use an Epsilon here
			if (j == dim) {
				if (Math.abs(Row[j] - 1) > IMeasurAble.FLOAT_ACCURACY) {
					return false;
				}
			} else {
				if (Math.abs(Row[j]) > IMeasurAble.FLOAT_ACCURACY) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * The Order can change in each individual Dimension!
	 * @return true when the middle Vector is between the left and right Vector.
	 */
	final static public boolean BETWEEN(final float[] left, final float[] mid, final float[] right) {
		int i = left.length;
		while (--i >= 0) {
			if (( left[i] < mid[i]) != 
				(right[i] > mid[i])) 
				return false;
		}
		return true;
	}

	/**
	 * Determines the Minimum and Maximum Value of each Column
	 * and sorts them into the Elements of the first and second Argument.
	 */
	final static public void ORDER_AT(final float[] inOutMin, final float[] inOutMax) {
		float tmp;
		int i = inOutMin.length;
		while (--i >= 0) {
			if ((tmp = inOutMin[i]) < inOutMax[i]) 
				continue;
			inOutMin[i] = inOutMax[i];
			inOutMax[i] = tmp;
		}
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Sampling of Functions
	///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads the Elements of this streamIO into the Vector
	 */
	final static public float[] LOAD_STREAM(final IStreamIn_Float stream, final float[] arr) {
		for (int j = -1; ++j < arr.length;) //>= 0) {
			arr[j] = stream.nextFloat(); 
		return arr;
	}

	/** Creates an equidistant Raster on the Interval [x0, x0+Grad*dx]	 */
	final static public float[] RASTER(final float x0, final float dx, final int N) {
		return RASTER(new float[N], x0, dx);
	}

	/** Creates an equidistant Raster on the Interval [x0, x0+Grad*dx]	 */
	final static public float[] RASTER(final float[] ret, float x0, final float dx) {
		if (ret.length == 0) {
			return ret; }
		ret[0] = x0;
		for (int i = 0; ++i < ret.length;) {
			ret[i] = (x0 += dx); }
		return ret;
	}

	/**
	 * Generates a Manifold by rastering over the Raster x
	 * Recursively delegates the Dimensions.
	 * @param x the Raster containing a Row for every Dimension to loop over
	 * @return a multidimensional Array (#Dim = Raster.length) containing the Values of f
	 */
	final static public Object[] RASTER(final float[][] raster) { //preserve Internals of x0
		float[] X = new float[raster.length];
		return RASTER(raster, X, raster.length);
	}

	/**
	 * Generates a Manifold by rastering over the Raster x
	 * Recursively delegates the Dimensions.
	 * @param f the Vector Field Function
	 * @param x the Raster containing a Row for every Dimension to loop over
	 * @param X the Vector being built containing a sample Value for every Dimension to loop over
	 * @param dim the Dimension currently looped over
	 * @return a multidimensional Array (#Dim = Raster.length) containing the Values of f
	 */
	protected static final Object[] RASTER(final float[][] raster, float[] x, int dim) { //preserve Internals of x0
		int i = raster[--dim].length;
		Object[] ret = new Object[i];
		float[] RasterD = raster[dim];
		while (--i >= 0) { //these i make up the MultiIndex to the Raster
			x[dim] = RasterD[i];
			if (dim > 0) { //could also have performed another Recursion,
				ret[i] = RASTER(raster, x, dim); //but faster to directly call it here!
			} else {
				ret[i] = x; //create a Copy for the next Value
				x = COPY(x);
			}
		}
		return ret;
	}

	/**
	 * Generates a Manifold by sampling f over the Raster x
	 * Recursively delegates the Dimensions.
	 * @param f the Vector Field Function
	 * @param x the Raster containing a Row for every Dimension to loop over
	 * @return a multidimensional Array (#Dim = Raster.length) containing the Values of f
	 */
	final static public Object SAMPLE(final IFloatScalarField f, final float[][] raster) { //preserve Internals of x0
		return SAMPLE(f, raster, new float[raster.length], raster.length); }

	/**
	 * Generates a Manifold by sampling f over the Raster x
	 * Recursively delegates the Dimensions.
	 * @param f the Vector Field Function
	 * @param x the Raster containing a Row for every Dimension to loop over
	 * @param X the Vector being built containing a sample Value for every Dimension to loop over
	 * @param dim the Dimension currently looped over
	 * @return a multidimensional Array (#Dim = Raster.length) containing the Values of f
	 */
	protected static final Object SAMPLE(
		final IFloatScalarField f,
		final float[][] raster,
		final float[] x,
		int dim) { //preserve Internals of x0
		int i = raster[--dim].length;
		float[] RasterD = raster[dim];
		if (dim > 0) { //distinguish here, because primitive return Values
			Object[] ret = new Object[i];
			while (--i >= 0) { //these i make up the MultiIndex to the Raster
				x[dim] = RasterD[i];
				ret[i] = SAMPLE(f, raster, x, dim); //but faster to directly call it here!
			}
			return ret;
		}
		final float[] ret = new float[i];
		while (--i >= 0) { //these i make up the MultiIndex to the Raster
			x[dim] = RasterD[i];
			ret[i] = f.Map(x);
		}
		return ret;
	}

	/**
	 * Generates a Manifold by sampling f over the Raster x
	 * Recursively delegates the Dimensions.
	 * @param f the Vector Field Function
	 * @param x the Raster containing a Row for every Dimension to loop over
	 * @param Y the Vector built during looping through Raster
	 * Y.length must match the Return Dimension of the f or be null
	 * @return a multidimensional Array (#Dim = Raster.length) containing the Values of f
	 */
	final static public Object[] SAMPLE(final IFloatVectorField f, final float[][] raster, final float[] y) { //preserve Internals of x0
		final float[] x = new float[raster.length]; //only works for Fields that have the same Output as Input Dimension!
		return SAMPLE(f, raster, x, y, raster.length);
	}

	/**
	 * Generates a Manifold by sampling f over the Raster x
	 * @param f the Vector Field Function
	 * @param x the Raster containing a Row for every Dimension to loop over
	 * @param X the Vector being built containing a sample Value for every Dimension to loop over
	 * @param Y the Vector built during looping through Raster
	 *  Y.length must match the Return Dimension of the f or be null
	 * @param dim the Dimension currently looped over
	 * @return a multidimensional Array (#Dim = Raster.length) containing the Values of f
	 */
	protected static final Object[] SAMPLE(
		final IFloatVectorField f,
		final float[][] raster,
		final float[] x,
		float[] y,
		int dim) { //preserve Internals of x0
		int i = raster[--dim].length;
		Object[] ret = new Object[i];
		float[] RasterD = raster[dim];
		while (--i >= 0) { //these i make up the MultiIndex to the Raster
			x[dim] = RasterD[i];
			if (dim > 0) { //could also have performed another Recursion,
				ret[i] = SAMPLE(f, raster, x, y, dim); //but faster to directly call it here!
			} else {
				ret[i] = f.map(x, y);
				if (y != null) { //create a Copy for the next Value
					y = new float[y.length];
				}
			}
		}
		return ret;
	}

	/** Generates a Manifold by sampling f over x	 */
	final static public float[] SAMPLE(IFloatFunction f, float[] x) { //preserve Internals of x0
		return SAMPLE(new float[x.length], f, x); }

	/** Generates a Manifold by sampling f over x	 */
	final static public float[] SAMPLE(float[] ret, IFloatFunction f, float[] x) { //preserve Internals of x0
		int j = ret.length;
		while (--j >= 0) {
			ret[j] = f.Map(x[j]);
		}
		return ret;
	}

	/** Samples the Function f on the Interval [x0, x0+Grad*dx]	 */
	final static public float[] SAMPLE(IFloatFunction f, float x0, float dx, int numSamples) { //preserve Internals of x0
		return SAMPLE(new float[numSamples], f, x0, dx);
	}

	/** Samples the Function f on the Interval [x0, x0+Grad*dx]	 */
	final static public float[] SAMPLE(
		final float[] ret,
		final IFloatFunction f,
		float x0,
		final float dx) { //preserve Internals of x0
		if (ret.length == 0) {
			return ret; }
		ret[0] = f.Map(x0);
		for (int i = 0; ++i < ret.length;) {
			ret[i] = f.Map(x0 += dx); }
		return ret;
	}

	/** Randomizes all the Values of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Assumes a rectangular Array. 	 */
	final static public float[] RANDOMIZE_AT_1_1(float[] arr) {
		for (int j = arr.length; --j >= 0;) 
			arr[j] = (float) ByRefDouble.RANDOM_1_1(); 
		return arr;
	}
	
	/** used primarily for Testing 
	 * @see streamIO.copy.IICopyAble#randomizeAt()
	 * @param maxLength maximum Length of the Vector
	 * @return a Vector of random Length with random Contents
	 */
	final static public float[] RANDOM(final int maxLength) {
		return RANDOMIZE_AT(new float[RandomQuick.NEXT_INT(maxLength)]); }
	
	/** Creates a new Array of the given Length filled with random Values.
	 * @see streamIO.copy.IICopyAble#randomizeAt()	 */
	final static public float[] RANDOMIZED(final int length) {
		return RANDOMIZE_AT(new float[length]); }
						
	/** Randomizes all the Values of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Assumes a rectangular Array. 	 */
	final static public float[] RANDOMIZE_AT(final float[] arr) {
		for(int j = arr.length; --j >= 0; ) 
			arr[j] = RandomQuick.NEXT_FLOAT(); //  
		return arr;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods returning a single Number from the Array
	////////////////////////////////////////////////////////////////////////////////

	/** Sums up all Values of the Array.
	 * @return The Sum of all Values in the Array. 	 */
	final static public double SUM(final float[] arr) {
		return SUM(arr, 0, arr.length); }

	/** Sums up the first length Values of the Array.
	 * @return The Sum of all Values in the Array. 	 */
	final static public double SUM(final float[] arr, final int length) {
		return SUM(arr, 0, length); }

	/** Sums up the given Range of Values in the Array.
	 * @return The Sum of all Values in the Array.	 */
	final static public double SUM(final float[] arr, final int start, int stop) {
		if (start == stop) {
			return 0; }
		double sum = arr[--stop]; //0;
		while (--stop >= start) {
			sum += arr[stop]; }
		return sum;
	}

	/** Multiplies together all Values of the Array.
	 * @return The Product of all Values in the Array. 	 */
	final static public double PROD(final float[] arr) {
		return PROD(arr, 0, arr.length); }

	/** Multiplies together the given Range of Values in the Array.
	 * @return The Product of all Values in the Array.	 */
	final static public double PROD(final float[] arr, final int start, int stop) {
		if (start == stop) 
			return 1; 
		double Prod = arr[--stop]; //1;
		while (--stop >= start) {
			Prod *= arr[stop];
		}
		return Prod;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public float MIN_VAL(final float[] arr) {
		return arr[MIN_POS(arr)]; }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Position of the Minimum Value of the Array.
	 */
	final static public int MIN_POS(final float[] arr) {
		return MIN_POS(arr, 0, arr.length); }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Minimum Value of the Array.
	 */
	final static public float MIN_VAL(float[] arr, int start, int stop) {
		return arr[MIN_POS(arr, start, stop)]; }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Position of the Minimum Value of the Array.
	 */
	final static public int MIN_POS(final float[] arr, final int start, int stop) {
		int iMin = -1;
		float Min = Float.POSITIVE_INFINITY;
		while (--stop >= start) {
			if (Min > arr[stop]) {
				Min = arr[iMin = stop]; }
		}
		return iMin;
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Minimum Values of the Array.
	 */
	final static public float[] MIN_2_VALs(final float[] arr, final float[] ret) {
		return MIN_2_VALs(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first two Minimum Values of the Array.
	 */
	final static public int[] MIN_2_POS(final float[] arr, final int[] ret) {
		return MIN_2_POS(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Minimum Values of the Array.
	 */
	final static public float[] MIN_2_VALs(final float[] arr, final int start, int stop, float[] ret) {
		int[] pos = new int[ret.length];
		MIN_2_POS(arr, start, stop, pos);
		int i = ret.length;
		if (i > 2) {
			i = 2;
		}
		while (--i >= 0) {
			ret[i] = arr[pos[i]];
		}
		return ret;
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first two Minimum Values of the Array.
	 */
	final static public int[] MIN_2_POS(final float[] arr, final int start, int stop, final int[] ret) {
		int inMin, iMin = inMin = -1; //the n Values contain the higher Maximum!
		double nMin, Min = nMin = Double.POSITIVE_INFINITY;
		while (--stop >= start) {
			if (Min > arr[stop]) { //larger than the second Max?
				if (nMin > arr[stop]) { //even larger than the first Max?
					Min = nMin;
					iMin = inMin;
					nMin = arr[inMin = stop];
				} else {
					Min = arr[iMin = stop];
				}
			}
		}
		ret[0] = inMin;
		ret[1] = iMin;
		return ret;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public float MAX_VAL(final float[] arr) {
		return arr[MAX_POS(arr)]; }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public float MAX_VAL(final float[] arr, final int start, final int stop) {
		return arr[MAX_POS(arr, start, stop)]; }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Position of the Maximum Value of the Array.
	 */
	final static public int MAX_POS(final float[] arr) {
		return MAX_POS(arr, 0, arr.length); }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Positions of the Maximum Value of the Array.
	 */
	final static public int MAX_POS(final float[] arr, final int start, int stop) {
		int iMax = -1;
		float Max = Float.NEGATIVE_INFINITY;
		while (--stop >= start) {
			if (Max < arr[stop]) 
				Max = arr[iMax = stop];
		}
		return iMax;
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return up to the first two Maximum Values of the Array.
	 */
	final static public float[] MAX_2_VALS(final float[] arr, final float[] ret) {
		return MAX_2_VALS(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first two Maximum Values of the Array.
	 */
	final static public int[] MAX_2_POS(float[] arr, int[] ret) {
		return MAX_2_POS(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Maximum Values of the Array.
	 */
	final static public float[] MAX_2_VALS(final float[] arr, final int start, int stop, final float[] ret) {
		int[] pos = new int[ret.length];
		MAX_2_POS(arr, start, stop, pos);
		int i = ret.length;
		if (i > 2) {
			i = 2; }
		while (--i >= 0) {
			ret[i] = arr[pos[i]]; }
		return ret;
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first two Maximum Values of the Array.
	 */
	final static public int[] MAX_2_POS(float[] arr, int start, int stop, int[] ret) {
		int inMax, iMax = inMax = -1; //the n Values contain the higher Maximum!
		double nMax, Max = nMax = Double.NEGATIVE_INFINITY;
		while (--stop >= start) {
			if (Max < arr[stop]) { //larger than the second Max?
				if (nMax < arr[stop]) { //even larger than the first Max?
					Max = nMax;
					iMax = inMax;
					nMax = arr[inMax = stop];
				} else {
					Max = arr[iMax = stop];
				}
			}
		}
		ret[0] = inMax;
		ret[1] = iMax;
		return ret;
	}
	
	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public float[] MIN_MAX_VAL(final float[] arr) {
		return MIN_MAX_VAL(arr, new float[2]); }
	
	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public float[] MIN_MAX_VAL(final float[] arr, final float[] ret) {
		return MIN_MAX_VAL(arr, 0, arr.length, ret); }
	
	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public float[] MIN_MAX_VAL(final float[] arr, final int start, final int stop) {
		return MIN_MAX_VAL(arr, start, stop, new float[2]); }
	
	/**
	 * Determines the Values of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public float[] MIN_MAX_VAL(final float[] arr, final int start, final int stop, final float[] ret) {
		final int[] pos = MIN_MAX_POS(arr, start, stop, ret.length);
		for (int i = ret.length; --i >= 0;) 
			ret[i] = arr[pos[i]]; 
		return ret;
	}
	
	/**
	 * Determines the Indices of the Minimum and the Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN_MAX_POS(final float[] arr) {
		return MIN_MAX_POS(arr, 2); }
	
	/**
	 * Determines the Indices of the Minimum and the Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN_MAX_POS(final float[] arr, final int start, final int stop, final int numItems) {
		return MIN_2_MAX_2_POS(arr, start, stop, new int[numItems]); }
	
	/**
	 * Determines the Indices of the Minimum and the Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN_MAX_POS(final float[] arr, final int numItems) {
		return MIN_2_MAX_2_POS(arr, 0, arr.length, new int[numItems]); }
	
	/**
	 * Determines the Positions of the two Minimum and the two Maximum Values in arr[]
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with the Positions of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN_2_MAX_2_POS(final float[] arr) {
		return MIN_2_MAX_2_POS(arr, 0, arr.length, new int[4]); }
	
	/**
	 * Determines the Indices of the two Minimum and the two Maximum Values in arr[]
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum, 
	 * by first comparing the two consecutive Array Values 
	 * and then comparing the larger with the Maximum and the smaller with the Minimum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN_2_MAX_2_POS(final float[] arr, final int[] MinMax) {
		return MIN_2_MAX_2_POS(arr, 0, arr.length, MinMax); }
	
	/**
	 * Determines the Indices of the two Minimum and the two Maximum Values in arr[]
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum, 
	 * by first comparing the two consecutive Array Values 
	 * and then comparing the larger with the Maximum and the smaller with the Minimum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN_2_MAX_2_POS(final float[] arr, final int start, final int stop, final int[] MinMax) {
		int  iMin,  iMax;
		int inMin, inMax;
		float  Min,  Max;
		float nMin, nMax;
		final boolean xMax = (MinMax.length > 2);
		final boolean xMin = (MinMax.length > 3);
		final boolean odd = ((stop-start) | 1) == 1; 
		int i = stop;
		if (odd) { //odd?
			iMin = iMax = inMin = inMax = --i;
			 Min =  Max =  nMin =  nMax = arr[i];
		} else { //a bit Overhead, but easier!
			iMin = iMax = inMin = inMax = -1; //cannot jump out earlier!
			Min = nMin = Float.POSITIVE_INFINITY;
			Max = nMax = Float.NEGATIVE_INFINITY;
		}
		float tMin,  tMax,  tmp;
		int  iTMin, iTMax, iTmp;
		while (i > start+1) {
			if ((tMin = arr[iTMin = --i]) > //first compare Args
				(tMax = arr[iTMax = --i])) {
				 tmp =  tMin;  tMin =  tMax;  tMax =  tmp;
				iTmp = iTMin; iTMin = iTMax; iTMax = iTmp;
			}
			if (Min > tMin) { //then compare tMin and tMax to Min and Max
				if (xMin && (nMin > tMin)) { //even larger than the first Max?
					 Min =  nMin;  nMin =  tMin;
					iMin = inMin; inMin = iTMin;
				} else {
					Min = tMin; iMin = iTMin;
				}
			}
			if (Max < tMax) { //this saves 1/4 of the Comparisons
				if (xMax && (nMax < tMax)) { //even larger than the first Max?
					 Max =  nMax;  nMax =  tMax;
					iMax = inMax; inMax = iTMax;
				} else {
					Max = tMax; iMax = iTMax;
				}
			}
			if (xMax && (Max < tMin)) { //also check the second Winners!
				Max = tMin; iMax = iTMin;
			}
			if (xMin && (Min > tMax)) { //also check the second Winners!
				Min = tMax; iMin = iTMax;
			}
		}
		int n = 1;
		if (xMin) {
			n = 2;
			MinMax[0] = inMin;
			MinMax[1] = iMin;
		} else {
			MinMax[0] = iMin;
		}
		if (xMax) {
			MinMax[n] = iMax;
			MinMax[n + 1] = inMax;
		} else {
			MinMax[n] = iMax;
		}
		return MinMax;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Calculates the Euclidean Norm (Length) of the given Vector.
	 * @return the euklidean Norm of the given Array
	 */
	final static public double NORM(final float[] arr) {
		return Math.sqrt(NORM_SQR(arr, arr.length)); }

	/**
	 * Rescales the given Array in Place to Unit Length.
	 * @return the euklidean Norm of the given Array
	 * @param the Array is normed to the given Length
	 */
	final static public double NORMALIZE_AT(final float[] arr) {
		return NORMALIZE_AT(arr, 1); }
	
	/**
	 * Rescales the given Array in Place to the given Length.
	 * @return the euklidean Norm of the given Array
	 * @param the Array is normed to the given Length
	 */
	final static public double NORMALIZE_AT(final float[] arr, final double length) {
		final double ret = Math.sqrt(NORM_SQR(arr, arr.length));
		MUL_AT(arr, length / ret);
		return ret; }

	/**
	 * Calculates the squared Euclidean Norm of the whole Vector.
	 * @return the squared euklidean Norm of the given Array
	 */
	final static public double NORM_SQR(final float[] arr) {
		return NORM_SQR(arr, arr.length); }
	
	/**
	 * This Value can well exceed the Range of valid Numbers,
	 * but that should be avoided anyway by renorming.
	 * Accuracy is not affected when using float Point Numbers.
	 *
	 * @return the squared euklidean Norm of the given Array
	 */
	final static public double NORM_SQR(final float[] arr, final int len) {
		return NORM_SQR(arr, len, 0); }
	
	/**
	 * This Value can well exceed the Range of valid Numbers,
	 * but that should be avoided anyway by renorming.
	 * Accuracy is not affected when using float Point Numbers.
	 *
	 * @return the squared euklidean Norm of the given Array
	 */
	final static public double NORM_SQR(final float[] arr, int max, final int min) {
		double norm = 0; //Calculate the Norm
		while (--max >= min) 
			norm += arr[max] * arr[max]; //sqr(arr[max]); 
		return norm;
	}
	
	/**
	 * Calculates the Euclidean Distance between the given Arrays.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the squared euklidean Norm of the Distance between the given Arrays
	 */
	final static public double DIST(final float[] arr1, final float[] arr2) {
		return Math.sqrt(DIST_SQR(arr1, arr2)); }

	/**
	 * Calculates the Euclidean Distance between the first dim Elements of both Arrays.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the squared euklidean Norm of the Distance between the given Arrays
	 */
	final static public double DIST(final float[] arr1, final float[] arr2, int dim) {
		return Math.sqrt(DIST_SQR(arr1, arr2, dim)); }

	/**
	 * Calculates the squared Euclidean Distance between the given Arrays.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the squared euklidean Norm of the Distance between the given Arrays
	 */
	final static public double DIST_SQR(final float[] arr1, final float[] arr2) {
		return DIFF_NORM_SQR(arr1, arr2, null); }

	/**
	 * Calculates the squared Euclidean Distance between the first dim Elements of both Arrays.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the squared euklidean Norm of the Distance between the given Arrays
	 */
	final static public double DIST_SQR(final float[] arr1, final float[] arr2, int dim) {
		return DIFF_NORM_SQR(arr1, arr2, dim, null); }

	/**
	 * Calculates the Euclidean Distance between a float and a double Array.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the squared Norm of the Distance between the given Arrays
	 */
	final static public double DIST(final float[] arr1, final double[] arr2) {
		return Math.sqrt(DIST_SQR(arr1, arr2)); }

	/**
	 * Calculates the Euclidean Distance between the first dim Elements of a float and a double Array.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the squared Norm of the Distance between the given Arrays
	 */
	final static public double DIST(final float[] arr1, final double[] arr2, int dim) {
		return Math.sqrt(DIST_SQR(arr1, arr2, dim)); }

	/**
	 * Calculates the squared Euclidean Distance between a float and a double Array.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the squared Norm of the Distance between the given Arrays
	 */
	final static public double DIST_SQR(final float[] arr1, final double[] arr2) {
		return DIST_SQR(arr1, arr2, arr1.length); }

	/**
	 * Calculates the squared Euclidean Distance between the first dim Elements of a float and a double Array,
	 * tolerating Length Mismatches by adding in the excess Tail's own squared Norm.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the squared Norm of the Distance between the given Arrays
	 */
	final static public double DIST_SQR(final float[] arr1, final double[] arr2, int dim) {
		double norm; //Calculate the Norm
		if (  arr1.length > arr2.length) {
			if (dim > arr2.length) {
				norm = NORM_SQR(arr1, arr2.length, dim); 
				dim = arr2.length;
			} else
				norm = 0; 
		} else {
			if (dim > arr1.length) {
				norm = VectorDouble.NORM_SQR(arr2, arr1.length, dim); 
				dim = arr1.length;
			} else
				norm = 0; 
		}
		for (int i = dim; --i >= 0; ) {
			final double diff = arr1[i] - arr2[i];
			norm += diff * diff;
		}
		return norm;
	}
	
	/**
	 * calculates both the Difference Vector and its Norm. 
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @param diff an optional Output Parameter being filled with the Difference Vector.
	 * @return the squared euklidean Norm of the Difference between the given Arrays
	 */
	final static public double DIFF_NORM_SQR(final float[] arr1, final float[] arr2, final float[] diff) {
		return DIFF_NORM_SQR(arr1, arr2, Math.max(arr1.length, arr2.length), diff); }
	
	/**
	 * @param diff an optional Output Parameter being filled with the Difference Vector.
	 * It must be as large as the larger of both Arguments 
	 * @return the squared euklidean Norm of the Difference between the given Arrays
	 */
	/** Calculates the squared Euclidean Distance between the first numDims Elements of both Arrays, optionally filling diff with the Difference Vector, tolerating Length Mismatches. */
	final static public double DIFF_NORM_SQR(final float[] arr1, final float[] arr2, final int numDims, final float[] diff) {
		double norm; //Calculate the Norm
		int minLength = numDims; 
		//Small Optimization: for the first Dimension don't aggregate! 
		if (minLength > arr2.length) { 
			minLength = arr2.length;
			norm = NORM_SQR(arr1, minLength, numDims); 
			if (diff != null)
				COPY(arr1, minLength, numDims, diff);
		} else
		if (minLength > arr1.length) {
			minLength = arr1.length;
			norm = NORM_SQR(arr2, minLength, numDims); 
			if (diff != null)
				NEG(arr1, minLength, numDims, diff);
		} else {
			norm = 0; //Just to mollify the Compiler...
			if (diff != null)
				FILL_AT(diff, 0, minLength, numDims);
		}
		for (int i = minLength; --i >= 0; ) {
			final float dif = arr1[i] - arr2[i];
			if (diff != null)
				diff[i] = dif; 
			norm += dif * dif;
		}
		return norm;
	}
	
	/**
	 * Calculates the Manhattan (Taxicab) Distance between the given Arrays.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the absolute Norm of the Distance between the given Arrays
	 */
	final static public float DIST_ABS(final float[] arr1, final float[] arr2) {
		float diff, norm = 0; //Calculate the Norm
		for (int i = arr1.length; --i >= 0;) {
			if (0 < (diff = arr1[i] - arr2[i])) {
				norm += diff;
				continue;
			}
			norm -= diff;
		}
		return norm;
	}

	/** 
	 * Calculates both the Difference Vector and its Norm
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @param diff is an Output Parameter being filled with the Difference Vector.
	 * @return the AbsV Norm of the Difference between the given Arrays
	 */
	final static public float DIFF_NORM_ABS(final float[] arr1, final float[] arr2, final float[] diff) {
		float dif, norm = 0; //Calculate the Norm
		int i = arr1.length;
		while (--i >= 0) {
			//norm+=Math.abs(diff[i] = arr1[i]-arr2[i]); }
			if (0 < (dif = diff[i] = arr1[i] - arr2[i])) { //avoid calling expensive Math.abs
				norm += dif;
				continue;
			}
			norm -= dif;
		}
		return norm;
	}
	
	/**
	  * Calculates the Sum of the absolute Values of the given Array.
	  * @return the Scalar AbsV Norm of the given Array
	  */
	final static public float NORM_ABS(final float[] arr) {
		float a, norm = 0; //Calculate the Norm
		int i = arr.length;
		while (--i >= 0) {
			if ((a = arr[i]) > 0) {
				norm += a;
				continue;
			}
			norm -= a;
		}
		return norm;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Selection of Values via (Multi-) Index
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public float GET_AT(final float[] a, final int index) {
		return GET_AT(a, index, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public float GET_AT(final float[] a, final int index, final int stop) {
		return GET_AT(a, index, 0, stop); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public float GET_AT(final float[] a, final int index, final float defaultValue) {
		return GET_AT(a, index, defaultValue, a.length); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public float GET_AT(final float[] a, final int index, final float defaultValue, final int stop) {
		return GET_AT(a, index, defaultValue, stop, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public float GET_AT(final float[] a, final int index, final float defaultValue, final int stop, final int start) {
		if ((index < start) || (index >= stop))
			return defaultValue; 
		return a[index]; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Selection via Multi-Index
	///////////////////////////////////////////////////////////////////////////
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * from integer Space into the real Numbers.
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public float[] GET_AT(final float[] a, final VectorInt index) {
		return GET_AT(a, index.items, null, index.itemCount); 
	}
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * from integer Space into the real Numbers.
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public float[] GET_AT(final float[] a, final VectorInt index, float[] ret) {
		return GET_AT(a, index.items, ret, index.itemCount); 
	}
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public float[] GET_AT(final float[] a, final int[] index) {
		return GET_AT(a, index, null); }  
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public float[] GET_AT(final float[] a, final int[] index, final float[] ret) {
		return GET_AT(a, index, ret, index.length); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public float[] GET_AT(final float[] a, final int[] index, final float[] ret, int stop) {
		return GET_AT(a, index, ret, stop, 0); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public float[] GET_AT(final float[] a, final int[] index, float[] ret, final int stop, final int start) {
		if((ret == null) || (ret.length < stop))
			ret = new float[stop];
		//else if (ret.length > stop) //rather leave the Values alone?!?
		//	Arrays.fill(ret, stop, ret.length, 0); 
		for(int i = stop; --i >= start; )
			ret[i] = (index[i] < a.length) ? a[index[i]] : 0; 
		return ret;
	}
	
	/**
	  * By Definition Elements outside the Array are 0
	  * @return the scalar Product of the given Arrays up to their full Length.
	  */
	final static public float MAP(final float[] arr1, final float[] arr2) {
		int len = arr1.length;
		if (len > arr2.length) {
			len = arr2.length;
		} //use the Minimum, because higher Elements are assumed to be 0.
		return MAP(arr1, arr2, 0, len);
	}
	
	/**
	  * By Definition Elements outside the Array are 0
	  * @return the scalar Product of the given Arrays up to the given Length.
	  */
	final static public float MAP(final float[] arr1, final float[] arr2, final int start, int stop) {
		float ret = 0;
		while (--stop >= start) 
			ret += arr1[stop] * arr2[stop];
		return ret;
	}

	/**
	 * Used e.g. in Game Theory where you calculate with a clever Opponent 
	 * allowing you only to reach the Minimum, which you want to optimize. 
	 * @return the Scalar MaxMin Product of the two Vectors.
	  */
	final static public float MAX_MIN_PROD(final float[] a, final float[] arg) {
		return MAX_MIN_PROD(a, arg, 0, arg.length); }
	
	/**
	 * Used e.g. in Game Theory where you calculate with a clever Opponent 
	 * allowing you only to reach the Minimum, which you want to optimize. 
	 * @return the Scalar MaxMin Product of the two Vectors.
	 */
	final static public float MAX_MIN_PROD(final float[] a, final float[] arg, final int start, int stop) {
		float x, y, max = Float.NEGATIVE_INFINITY; //FALSE; //can also start with any lower Value!
		while (--stop >= start) {
			if ((x = a[stop]) < (y = arg[stop])) { //use the Minimum
				if (max < x) //update the Maximum
					max = x;
			} else {
				if (max < y) //update the Maximum
					max = y;
			}
		}
		return max;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// Linear Least Squares Fitting ("Linear Regression")
	////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * least-squares fit of a straight Line to Data (15.2)
	 * Fitting linear Function a+x*b to the Data x[],y[]
	 * considering individual Standard Deviations!
	 * Using the Standard Deviations allows to weigh the Points individually
	 * and secondly allows to estimate the Probability of the Hypothesis
	 * that the Data actually fits to a straight Line using
	 * GammaP.PROBABILITY_CHI_SQR(ndata-2, chi2)
	 * @param x the x Data 
	 * @param y the y Data 
	 * @param sig the Standard Errors in x, can be null 
	 * @param abSigaSigb returns the Line Parameters (and it's Standard Errors) 
	 * @return Chi� for ndata-2 Degrees of Freedom
	 */
	final static public float LINEAR_FIT(final float[] x, final float[] y
	, final float[] sig, final float[][] abSigaSigb) {
		return LINEAR_FIT(x, y, sig, abSigaSigb, 0, x.length); }
	
	/** 
	 * least-squares fit of a straight Line to Data (15.2)
	 * Fitting linear Function a+x*b to the Data x[],y[]
	 * considering individual Standard Deviations!
	 * Using the Standard Deviations allows to weigh the Points individually
	 * and secondly allows to estimate the Probability of the Hypothesis
	 * that the Data actually fits to a straight Line using
	 * GammaP.PROBABILITY_CHI_SQR(ndata-2, chi2)
	 * @param x the x Data 
	 * @param y the y Data 
	 * @param sig the estimated Standard Errors in x, optional (null allowed) 
	 * @param abSigaSigb returns the Line Parameters a (Offset), b (Inclination) 
	 * and optionally it's Standard Errors.
	 * optional (null allowed, length 1 or 2 allowed) 
	 * @return Chi� for stop-start-2 Degrees of Freedom, 
	 * which allows to test the Quality of the Fit. 
	 * (not the Validity, this also depends on the Number of points fitted)  
	 */
	final static public float LINEAR_FIT(final float[] x, final float[] y
			, final float[] sig, final float[][] abSigaSigb
			, final int start, final int stop) {
		
		//Calculate the Averages to get best Results 
		double sx=0;
		double sy=0;
		double ss;
		if (sig != null) { 
			L.n("accumulate Sums with Weights"); 
			ss=0;
			for (int i=stop; --i>=start; ) { 
				final float weight=1/ByRefFloat.SQR(sig[i]); 
				ss += weight;
				sx += x[i]*weight;
				sy += y[i]*weight;
			}
		} else {
			L.n("accumulate Sums without Weigvfhts"); 
			ss=(stop-start);
			for (int i=stop; --i>=start; ) {
				sx += x[i];
				sy += y[i];
			}
		}
		final float xAvg=(float) (sx/ss); //
		final float yAvg=(float) (sy/ss); //
		
		L.n("Calculate Chi�"); 		
		double sumDy=0;
		double sumSqrD=0;
		if (sig != null) {
			for (int i=stop; --i>=start; ) {
				final float d=(x[i]-xAvg)/sig[i];
				sumSqrD += d*d;
				sumDy += d*y[i]/sig[i];
			}
		} else {
			for (int i=stop; --i>=start; ) {
				final float d=x[i]-xAvg;
				sumSqrD += d*d;
				sumDy += d*y[i];
			}
		}
		final float b = (float) (sumDy/sumSqrD); 
		final float a = yAvg-xAvg*b; 
		if (abSigaSigb != null) {
			abSigaSigb[0][1] = b; //Inclination
			abSigaSigb[0][0] = a; //Offset
			if (abSigaSigb.length > 1) {
				abSigaSigb[1][0] = (float) Math.sqrt((1+sx*sx/(ss*sumSqrD))/ss);
				abSigaSigb[1][1] = (float) Math.sqrt(1/sumSqrD);
			}
		}
		float chi2=0;
		if (sig == null) {
			for (int i=stop; --i>=start; ) {
				chi2 += ByRefFloat.SQR(y[i]-(a+b*x[i])); } 
			if (abSigaSigb != null) 
				if (abSigaSigb.length > 1) {
					final double sigdat=Math.sqrt(chi2/(stop-start-2));
					abSigaSigb[1][0] *= sigdat;
					abSigaSigb[1][1] *= sigdat;
				}
			//q=1;
		} else {
			for (int i=stop; --i>=start; ) {
				chi2 += ByRefFloat.SQR((y[i]-(a+b*x[i]))/sig[i]); }
			//q=GammaP.PROBABILITY_CHI_SQR(ndata-2, chi2);
		}
		return chi2;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// modifying Operations on a single Array
	///////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Negates all Elements of the given Array in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] NEG_AT(final float[] ret) {
		return NEG_AT(ret, 0, ret.length); }

	/**
	  * Negates the given Range of the Array in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] NEG_AT(final float[] ret, final int start, final int stop) {
		return NEG(ret, start, stop, ret); }

	/**
	  * Negates the given Range of x into ret (or a new Array, if ret is null).
	  * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Negative of the given Array
	  */
	final static public float[] NEG(final float[] x, final int start, int stop, float[] ret) {
		if (ret == null)
			ret  = new float[stop]; 
		while (--stop >= start) 
			ret[stop] = -x[stop]; 
		return ret;
	}
	
	/**
	  * Negates the given Range of the double Array x into a float Array ret (or a new Array, if ret is null).
	  * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Negative of the given Array
	  */
	final static public float[] NEG(final double[] x, final int start, int stop, float[] ret) {
		if (ret == null)
			ret  = new float[stop]; 
		while (--stop >= start) 
			ret[stop] = (float) - x[stop];
		return ret;
	}
	
	/**
	  * Negates the whole Array into a new Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] NEG(final float[] x) {
		return NEG(x, 0, x.length, null); }

	/**
	  * Negates the given Range of the Array into a new Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] NEG(final float[] x, final int start, final int stop) {
		return NEG(x, start, stop, null); }

	/**
	  * Negates the whole double Array into a new float Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] NEG(final double[] x) {
		return NEG(x, 0, x.length, null); }

	/**
	  * Negates the given Range of the double Array into a new float Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] NEG(final double[] x, final int start, final int stop) {
		return NEG(x, start, stop, null); }
	
	/**
	  * Applies floor() to all Elements of the given Array in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] FLOOR_AT(final float[] ret) {
		return FLOOR_AT(ret, 0, ret.length); }

	/**
	  * Applies floor() to the given Range of the Array in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] FLOOR_AT(final float[] ret, final int start, final int stop) {
		return FLOOR(ret, start, stop, ret); }

	/**
	  * Applies floor() to the given Range of x into ret.
	  * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Negative of the given Array
	  */
	final static public float[] FLOOR(final float[] x, final int start, int stop, float[] ret) {
		if (ret == null)
			ret = new float[stop]; 
		while (--stop >= start) 
			ret[stop] = (float) Math.floor(x[stop]);
		return ret;
	}

	/**
	  * Applies floor() to the given Range of the double Array x into a float Array ret.
	  * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Negative of the given Array
	  */
	final static public float[] FLOOR(final double[] x, final int start, int stop, float[] ret) {
		if (ret == null)
			ret = new float[stop]; 
		while(--stop >= start) 
			ret[stop] = (float) Math.floor(x[stop]);
		return ret;
	}

	/**
	  * Applies floor() to the whole Array into a new Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] FLOOR(final float[] x) {
		return FLOOR(x, 0, x.length, null); }

	/**
	  * Applies floor() to the given Range of the Array into a new Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] FLOOR(final float[] x, final int start, final int stop) {
		return FLOOR(x, start, stop, null); }

	/**
	  * Applies floor() to the whole double Array into a new float Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] FLOOR(final double[] x) {
		return FLOOR(x, 0, x.length, null); }

	/**
	  * Applies floor() to the given Range of the double Array into a new float Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] FLOOR(final double[] x, final int start, final int stop) {
		return FLOOR(x, start, stop, null); }

	/**
	  * Replaces every Element of the whole Array by its multiplicative Inverse, in Place.
	  * @return the multiplicative Inverse of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] INV_AT(final float[] ret) {
		return INV_AT(ret, 0, ret.length); }

	/**
	  * Replaces every Element of the given Range of the Array by its multiplicative Inverse, in Place.
	  * @return the multiplicative Inverse of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] INV_AT(final float[] ret, final int start, final int stop) {
		return INV(ret, start, stop, ret); }

	/**
	  * Calculates the multiplicative Inverse of the whole Array into a new Array.
	  * @param arg Array with the Values to be processed
	  * @param ret Array with the inverted Values. returned by this Method.
	  * @return the multiplicative Inverse of the given Array
	  */
	final static public float[] INV(final float[] arg) {
		return INV(arg, 0, arg.length, null); }

	/**
	  * Calculates the multiplicative Inverse of the whole Array into ret.
	  * @param arg Array with the Values to be processed
	  * @param ret Array with the inverted Values. returned by this Method.
	  * @return the multiplicative Inverse of the given Array
	  */
	final static public float[] INV(final float[] arg, final float[] ret) {
		return INV(arg, 0, arg.length, ret); }

	/**
	  * Calculates the multiplicative Inverse of the given Range of arg into ret.
	  * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the multiplicative Inverse of the given Array
	  */
	final static public float[] INV(final float[] arg, final int start, int stop, float[] ret) {
		if (ret == null)
			ret = new float[stop]; 
		while(--stop >= start) 
			ret[stop] = 1 / arg[stop];
		return ret;
	}
	
	/**
	  * Replaces all Elements of the given Array by their absolute Value, in Place.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] ABS_AT(final float[] ret) {
		return ABS_AT(ret, 0, ret.length); }

	/**
	  * Calculates the absolute Value of the given Range of arg into ret.
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @return the absolute Value of the Values in the given Array
	  */
	final static public float[] ABS(final float[] arg, final int start, int stop, float[] ret) {
		if (ret == null)
			ret = new float[stop];
		while (--stop >= start) {
			final float tmp; //Calculate the Norm
			// TODO: LOGIC: reads from ret[stop] instead of arg[stop], so the source Array arg is ignored whenever ret != arg (e.g. when called with a fresh/different ret Array the result is all zeros).
			if (0 <= (tmp = ret[stop]))
				ret[stop] =  tmp;
			else
				ret[stop] = -tmp;
		}
		return ret;
	}

	/**
	  * Replaces the given Range of the Array by its absolute Value, in Place.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] ABS_AT(final float[] ret, final int start, int stop) {
		//return ABS(ret, ret, start, stop); 
		float tmp; //Calculate the Norm
		while (--stop >= start) {
			if (0 <= (tmp = ret[stop])) 
				continue; //Optimization
			ret[stop] = -tmp;
		}
		return ret;
	}
	
	/**
	  * Raises every Element of the whole Array to the given Exponent into a new Array.
	  * @param exp the Exponent to use
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Power to the given Exponent of the Values in the given Array
	  */
	final static public float[] POW(final float[] arg, final double exp) {
		return POW(arg, exp, 0, arg.length, null); }

	/**
	  * Raises every Element of the whole Array to the given Exponent into ret.
	  * @param exp the Exponent to use
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Power to the given Exponent of the Values in the given Array
	  */
	final static public float[] POW(final float[] arg, final double exp, final float[] ret) {
		return POW(arg, exp, 0, ret.length, ret); }

	/**
	  * Raises every Element of the given Range of arg to the given Exponent into ret; exp==1 is optimized to a plain copy.
	  * @param exp the Exponent to use
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Power to the given Exponent of the Values in the given Array
	  */
	final static public float[] POW(final float[] arg, final double exp, final int start,
			int stop, float[] ret) {
		if (ret == null)
			ret  = new float[stop]; 
		if (exp == 1) //Optimization: save Exponentiation or copying 
			System.arraycopy(arg, start, ret, start, stop-start); 
		else
			while (--stop >= start) 
				ret[stop] = (float) Math.pow(arg[stop], exp); 
		return ret;
	}
	
	/**
	  * Raises every Element of the whole Array to the given Exponent, in Place.
	  * @return the Power to the given Exponent of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param exp the Exponent to use
	  */
	final static public float[] POW_AT(final float[] ret, final double exp) {
		return POW_AT(ret, exp, 0, ret.length); }

	/**
	  * Raises every Element of the given Range of the Array to the given Exponent, in Place.
	  * @return the Power to the given Exponent of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param exp the Exponent to use
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] POW_AT(final float[] ret, final double exp,
			final int start, int stop) {
		if (exp == 1) //Optimization: save Exponentiation
			return ret; 
		return POW(ret, exp, start, stop, ret); }
	
	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the natural Logarithm of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] LOG_AT(final float[] ret) {
		return LOG(ret, 0, ret.length, ret); }
	
	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the natural Logarithm of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] LOG_AT(final float[] ret, final int start, int stop) {
		return LOG(ret, start, stop, ret); }
	
	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  * @return the natural Logarithm of the Values in the given Array
	  */
	final static public float[] LOG(final float[] arg, final float[] ret) {
		return LOG(arg, 0, arg.length, ret); }
	
	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @return the natural Logarithm of the Values in the given Array
	  */
	final static public float[] LOG(final float[] arg, final int start, int stop, float[] ret) {
		if (ret == null)
			ret  = new float[stop]; 
		while(--stop >= start) 
			ret[stop] = (float) Math.log(arg[stop]);
		return ret;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Binary Operations
	///////////////////////////////////////////////////////////////////////////////////

	/**
	  * Caps the whole Array so no Element exceeds upperLimit, in Place.
	  * @return the Array ret limited by the given Maximum (upper Limit)
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param upperLimit upper Limit to bound the Array Values by
	  */
	final static public float[] MIN_AT(final float[] ret, final float upperLimit) {
		return MIN_AT(ret, upperLimit, 0, ret.length); }

	/**
	  * Caps the given Range of the Array so no Element exceeds upperLimit, in Place.
	  * @return the Array ret limited by the given Maximum (upper Limit)
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param upperLimit upper Limit to bound the Array Values by
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MIN_AT(final float[] ret, final float upperLimit, final int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > upperLimit)
				ret[stop] = upperLimit;
		}
		return ret;
	}

	/**
	  * Caps the whole ret Array Element-wise against arr, in Place.
	  * @return the Value-by-Value Minimum of the given Arrays in ret
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param arr unchanged Array of Values to compare with
	  */
	final static public float[] MIN_AT(float[] ret, float[] arr) {
		return MIN_AT(ret, arr, 0, arr.length); }

	/**
	  * Caps the given Range of ret Element-wise against arr, in Place.
	  * @return the Value-by-Value Minimum of the given Arrays in ret
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param arr unchanged Array of Values to compare with
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MIN_AT(final float[] ret, final float[] arr, final int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > arr[stop])
				ret[stop] = arr[stop];
		}
		return ret;
	}

	/**
	  * Raises the whole Array so no Element is below Limit, in Place.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array bounded by the given Limit
	  */
	final static public float[] MAX_AT(float[] ret, float Limit) {
		return MAX_AT(ret, Limit, 0, ret.length); }
	
	/**
	  * Raises the given Range of the Array so no Element is below Limit, in Place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MAX_AT(float[] ret, float Limit, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < Limit)
				ret[stop] = Limit;
		}
		return ret;
	}

	/**
	  * Raises the whole ret Array Element-wise against arr, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] MAX_AT(final float[] ret, final float[] arr) {
		return MAX_AT(ret, arr, 0, arr.length); }

	/**
	  * Raises the given Range of ret Element-wise against arr, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MAX_AT(final float[] ret, final float[] arr, final int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < arr[stop]) 
				ret[stop] = arr[stop]; 
		}
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public float[] ADD_AT(final float[] ret, final double increment) {
		return ADD_AT(ret, increment, 0, ret.length); }

	/**
	  * Adds the given Increment to every Element of the given Range of ret, in Place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] ADD_AT(final float[] ret, final double increment, final int start, int stop) {
		while (--stop >= start)
			ret[stop] += increment;
		return ret;
	}

	/**
	  * Adds arr Element-wise to the whole ret Array, in Place (tolerating Length Mismatches).
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] ADD_AT(final float[] ret, final float[] arr) {
		return ADD_AT(ret, ret.length, arr, arr.length); }

	/**
	  * Adds the first arrLength Elements of arr to ret, in Place (or falls back to ADD if arr is longer than ret).
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] ADD_AT(final float[] ret, final int retLength, final float[] arr, final int arrLength) {
		if (retLength < arrLength) //make it work (though less effectively)
			return ADD(ret, retLength, arr, arrLength);
		return ADD_AT(ret, arr, 0, arrLength); }

	/**
	  * Adds the double Array arr Element-wise to ret, in Place (or falls back to ADD if arr is longer than ret).
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] ADD_AT(final float[] ret, final double[] arr) {
		if (ret.length < arr.length) //make it work (though less effectively)
			return ADD(ret, arr);
		return ADD_AT(ret, arr, 0, arr.length); }

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Adds arr Element-wise to the given Range of ret, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] ADD_AT(final float[] ret, final float[] arr, final int start, int stop) {
		while (--stop >= start)
			ret[stop] += arr[stop];
		return ret;
	}

	/**
	  * Adds the double Array arr Element-wise to the given Range of ret, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] ADD_AT(final float[] ret, final double[] arr, final int start, int stop) {
		while (--stop >= start) 
			ret[stop] += arr[stop]; 
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Adds two Arrays Element-wise into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] add(final float[] ret, final float[] sum1, final float[] sum2) {
		return ADD(ret, sum1, sum2, 0, sum1.length); }

	/**
	  * Adds two Arrays Element-wise into ret, tolerating Length Mismatches by copying the excess Tail.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] ADD(float[] ret, final float[] sum1, final float[] sum2, final int start, int stop) {
		if (ret == null) {
			ret = new float[stop];
		} else
		if (stop > ret.length) {
			stop = ret.length;
		}
		if (stop > sum1.length) {
			System.arraycopy(sum2, sum1.length, ret, sum1.length, sum2.length - sum1.length);
			stop = sum1.length;
		}
		if (stop > sum2.length) {
			System.arraycopy(sum1, sum2.length, ret, sum2.length, sum1.length - sum2.length);
			stop = sum2.length;
		}
		while (--stop >= start) {
			ret[stop] = sum1[stop] + sum2[stop]; }
		return ret;
	}

	/**
	  * Adds a float and a double Array Element-wise into ret, tolerating Length Mismatches.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] ADD(float[] ret, final float[] sum1, final double[] sum2, final int start, int stop) {
		if (ret == null) {
			ret = new float[stop];
		} else
		if (stop > ret.length) {
			stop = ret.length;
		}
		if (stop > sum1.length) {
			COPY(sum2, sum1.length, stop, ret);
			stop = sum1.length;
		}
		if (stop > sum2.length) {
			COPY(sum1, sum2.length, stop, ret);
			System.arraycopy(sum1, sum2.length, ret, sum2.length, sum1.length - sum2.length);
			stop = sum2.length;
		}
		while (--stop >= start) {
			ret[stop] = sum1[stop] + (float)sum2[stop]; }
		return ret;
	}
	
	/**
	  * Adds the Increment to every Element of sum1 into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] ADD(final float[] ret, final float[] sum1, final float incr) {
		return ADD(ret, sum1, incr, 0, sum1.length); }

	/**
	  * Adds the Increment to the given Range of sum1 into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] ADD(final float[] ret, final float[] sum1, final float incr, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = sum1[stop] + incr; }
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Adds two Arrays Element-wise into a new Array, tolerating Length Mismatches.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] ADD(final float[] sum1, final float[] sum2) {
		return ADD(sum1, sum1.length, sum2, sum2.length); }

	/**
	  * Adds two Arrays (given as prefix Lengths) Element-wise into a new Array, tolerating Length Mismatches.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] ADD(final float[] sum1, final int sum1Length, final float[] sum2, final int sum2Length) {
		final float[] ret; 
		final int minLength; 
		if (sum1Length > sum2Length) {
			minLength = sum2Length; 
			ret = new float[sum1Length];
			//COPY_AT(ret, min, minLength, ret.length);
			System.arraycopy(sum1, minLength, ret, minLength, ret.length - minLength);
		} else {
			minLength = sum1.length; 
			ret = new float[sum2.length];
			//COPY_AT(ret, min, minLength, ret.length);
			System.arraycopy(sum2, minLength, ret, minLength, ret.length - minLength);
		}
		return ADD(ret, sum1, sum2, 0, minLength); 
	}

	/**
	  * Adds a float and a double Array Element-wise into a new Array, tolerating Length Mismatches.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] ADD(final float[] sum1, final double[] sum2) {
		final float[] ret; 
		final int minLength; 
		if (sum1.length > sum2.length) {
			minLength = sum2.length; 
			ret = new float[sum1.length];
			COPY(sum2, minLength, ret.length, ret);
		} else {
			minLength = sum1.length; 
			ret = new float[sum2.length];
			COPY(sum2, minLength, ret.length, ret);
		}
		return ADD(ret, sum1, sum2, 0, minLength); 
	}
	
	/**
	  * Adds the Increment to every Element of sum1 into a new Array.
	  * @return the Sum of the given Array and the Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] ADD(final float[] sum1, final float Incr) {
		return ADD(sum1, Incr, 0, sum1.length); }

	/**
	  * Adds the given Range of two Arrays Element-wise into a new Array.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] ADD(final float[] sum1, final float[] sum2, final int start, int stop) {
		return ADD(new float[stop], sum1, sum2, start, stop); }

	/**
	  * Adds the Increment to the given Range of sum1 into a new Array.
	  * @return the Sum of the given Array and the Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] ADD(final float[] sum1, final float Incr, final int start, int stop) {
		return ADD(new float[stop], sum1, Incr, start, stop); 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Subtracts sub Element-wise from the whole ret Array, in Place (tolerating Length Mismatches).
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] SUB_AT(final float[] ret, final float[] sub) {
		return SUB_AT(ret, ret.length, sub, sub.length); }

	/**
	  * Subtracts the first subLength Elements of sub from ret, in Place (or falls back to SUB if sub is longer than ret).
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] SUB_AT(final float[] ret, final int retLength, final float[] sub, final int subLength) {
		if (retLength < subLength) { //make it work (though less effectively)
			if (ret.length < subLength) { //make it work (though less effectively)
				return SUB(ret, retLength, sub, subLength); }
			NEG(sub, retLength, subLength, ret);
		}
		return SUB_AT(ret, sub, 0, sub.length); }

	/**
	  * Subtracts the double Array sub from ret, in Place (or falls back to SUB if sub is longer than ret).
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] SUB_AT(final float[] ret, final double[] sub) {
		if (ret.length < sub.length) { //make it work (though less effectively)
			return SUB(ret, sub); }
		return SUB_AT(ret, sub, 0, sub.length); }

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Subtracts sub Element-wise from the given Range of ret, in Place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] SUB_AT(final float[] ret, final float[] sub, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= sub[stop]; }
		return ret;
	}

	/**
	  * Subtracts the double Array sub Element-wise from the given Range of ret, in Place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] SUB_AT(final float[] ret, final double[] sub, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= sub[stop]; }
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Subtracts sub Element-wise from min into ret.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] SUB(final float[] ret, final float[] min, final float[] sub) {
		return SUB(ret, min, sub, 0, sub.length); }

	/**
	  * Subtracts the given Range of sub Element-wise from min into ret.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] SUB(float[] ret, final float[] min, final float[] sub, final int start, int stop) {
		if (ret == null)
			ret = new float[stop];
		while (--stop >= start)
			ret[stop] = min[stop] - sub[stop];
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Subtracts sub Element-wise from min into a new Array, tolerating Length Mismatches.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] SUB(final float[] min, final float[] sub) {
		return SUB(min, min.length, sub, sub.length); }

	/**
	  * Subtracts sub (given as prefix Lengths) Element-wise from ths into a new Array, tolerating Length Mismatches.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] SUB(final float[] ths, final int thisLength
			, final float[] sub, final int subLength) {
		final float[] ret; 
		final int minLength; 
		if (thisLength > subLength) {
			minLength = subLength; 
			ret = new float[thisLength];
			//COPY_AT(ret, min, sub.length, min.length);
			System.arraycopy(ths, minLength, ret, minLength, ret.length - minLength);
		} else {
			minLength = thisLength; 
			ret = new float[subLength];
			NEG(sub, minLength, ret.length, ret);
		}
		return SUB(ret, ths, sub, 0, minLength); 
	}
		
	/**
	  * Subtracts the given Range of sub Element-wise from min into a new Array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] SUB(final float[] min, final float[] sub, final int start, final int stop) {
		return SUB(new float[stop], min, sub, start, stop); }

	/**
	  * Subtracts the given Range of the double Array sub Element-wise from min into ret.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] SUB(final float[] ret, final float[] min, final double[] sub, final int start, int stop) {
		while (--stop >= start)
			ret[stop] = (float) (min[stop] - sub[stop]);
		return ret;
	}

	/**
	  * Subtracts the double Array sub from min into a new Array, tolerating Length Mismatches.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] SUB(final float[] min, final double[] sub) {
		final float[] ret; 
		if (min.length > sub.length) {
			ret = new float[min.length];
			//COPY_AT(ret, min, sub.length, min.length);
			System.arraycopy(min, sub.length, ret, sub.length, min.length - sub.length);
		} else {
			ret = new float[sub.length];
			NEG(min, min.length, sub.length, ret);
		}
		return SUB(ret, min, sub, 0, sub.length); 
	}

	/**
	  * Subtracts the given Range of the double Array sub from min into a new Array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] SUB(final float[] min, final double[] sub, final int start, final int stop) {
		return SUB(new float[stop], min, sub, start, stop);
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Multiplies every Element of the whole Array by the given Factor, in Place.
	  * To implement divAt, just invert the Factor
	  * @param Factor the Factor to multiply with
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @return the given Array multiplied by the given Factor
	  */
	final static public float[] MUL_AT(final float[] ret, final double factor) {
		return MUL_AT(ret, factor, 0, ret.length); }

	/**
	  * Multiplies every Element of the given Range of ret by the given Factor, in Place.
	  * @return the Product of the Array with the given Factor
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Factor the Factor to multiply with
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MUL_AT(final float[] ret, final double factor, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] *= factor; }
		return ret;
	}

	/**
	  * Multiplies factor Element-wise into the whole ret Array, in Place (tolerating Length Mismatches).
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] MUL_AT(final float[] ret, final float[] factor) {
		return MUL_AT(ret, ret.length, factor, factor.length); }

	/**
	  * Multiplies factor Element-wise into ret, in Place, over the shorter of both prefix Lengths.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] MUL_AT(final float[] ret, final int retLength, final float[] factor, final int factorLength) {
		return MUL_AT(ret, factor, 0, Math.min(factorLength, retLength)); }

	/**
	  * Multiplies factor Element-wise into the given Range of ret, in Place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MUL_AT(final float[] ret, final float[] factor, final int start, int stop) {
		while (--stop >= start)
			ret[stop] *= factor[stop];
		return ret;
	}

	/**
	  * Multiplies the double Array factor Element-wise into the given Range of ret, in Place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MUL_AT(final float[] ret, final double[] factor, final int start, int stop) {
		while (--stop >= start) 
			ret[stop] *= factor[stop]; 
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Multiplies two Arrays Element-wise into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] MUL(final float[] ret, final float[] f1, final float[] f2) {
		return MUL(ret, f1, f2, 0, f2.length); }

	/**
	  * Multiplies the given Range of two Arrays Element-wise into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MUL(final float[] ret, final float[] f1, final float[] f2, final int start, int stop) {
		while (--stop >= start)
			ret[stop] = f1[stop] * f2[stop];
		return ret;
	}

	/**
	  * Multiplies the given Range of a double and a float Array Element-wise into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MUL(final float[] ret, final double[] f1, final float[] f2, final int start, int stop) {
		while (--stop >= start)
			ret[stop] = (float) f1[stop] * f2[stop];
		return ret;
	}

	/**
	  * Multiplies every Element of f1 by the scalar f2 into a new Array.
	  * @return a new Array containing the Product of the given Array
	  * @param ret Array with the Values to be processed.
	  */
	final static public float[] MUL(final float[] f1, final float f2) {
		return MUL(new float[f1.length], f1, f2, 0, f1.length); }

	/**
	  * Multiplies every Element of f1 by the scalar f2 into ret.
	  * @return a new Array containing the Product of the given Array
	  * @param ret Array with the Values to be processed.
	  */
	final static public float[] MUL(final float[] ret, final float[] f1, final float f2) {
		return MUL(ret, f1, f2, 0, f1.length); }

	/**
	  * Multiplies every Element of the given Range of f1 by the scalar f2 into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MUL(final float[] ret, final float[] f1, final float f2, final int start, int stop) {
	/*	if (ret.length < stop) {
			L.n(ret.length); }
		if (min.length < stop) {
			L.n(min.length); }
	*/	while (--stop >= start) 
			ret[stop] = f1[stop] * f2; 
		return ret;
	}

	/**
	  * Multiplies the given Range of a double Array f1 by the scalar f2 into a float Array ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MUL(final float[] ret, final double[] f1, final float f2, final int start, int stop) {
		while (--stop >= start)
			ret[stop] = (float) f1[stop] * f2;
		return ret;
	}

	/**
	  * Multiplies two Arrays Element-wise into a new Array, over the shorter Length.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] MUL(final float[] f1, final float[] f2) {
		return MUL(f1, f2, 0, Math.min(f2.length, f1.length)); }

	/**
	  * Multiplies the given Range of two Arrays Element-wise into a new Array.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] MUL(final float[] f1, final float[] f2, final int start, final int stop) {
		return MUL(new float[stop], f1, f2, start, stop); }

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Divides the whole ret Array Element-wise by denom, in Place (or falls back to DIV if denom is longer than ret).
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] DIV_AT(final float[] ret, final float[] denom) {
		if (ret.length < denom.length) { //make it work (though less effectively)
			return DIV(ret, denom); }
		return DIV_AT(ret, denom, 0, denom.length); }

	/**
	  * Divides the whole ret Array Element-wise by the double Array denom, in Place (or falls back to DIV if denom is longer than ret).
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] DIV_AT(final float[] ret, final double[] denom) {
		if (ret.length < denom.length) { //make it work (though less effectively)
			return DIV(ret, denom); }
		return DIV_AT(ret, denom, 0, denom.length);
	}

	/**
	  * Divides the given Range of ret Element-wise by the double Array denom, in Place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] DIV_AT(final float[] ret, final double[] denom, final int start, int stop) {
		while (--stop >= start)
			ret[stop] /= denom[stop];
		return ret;
	}

	/**
	  * Divides the given Range of ret Element-wise by denom, in Place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] DIV_AT(final float[] ret, final float[] denom, final int start, int stop) {
		while (--stop >= start) 
			ret[stop] /= denom[stop]; 
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * Divides numer Element-wise by denom into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] DIV(final float[] ret, final float[] numer, final float[] denom) {
		return DIV(ret, numer, denom, 0, denom.length);
	}

	/**
	  * Divides the given Range of numer Element-wise by denom into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] DIV(final float[] ret, final float[] numer, final float[] denom, final int start, int stop) {
		while (--stop >= start)
			ret[stop] = numer[stop] / denom[stop];
		return ret;
	}

	/**
	  * Divides the given Range of numer Element-wise by the double Array denom into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] DIV(final float[] ret, final float[] numer, final double[] denom, final int start, int stop) {
		while (--stop >= start)
			ret[stop] = numer[stop] / (float)denom[stop];
		return ret;
	}

	/**
	  * Divides numer Element-wise by denum into a new Array, filling the excess Tail with Infinity when numer is longer.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] DIV(float[] numer, float[] denum) {
		final float[] ret; 
		final int minLength; 
		if (numer.length > denum.length) {
			minLength = denum.length; 
			ret = new float[numer.length]; //you have to check the Sign of the Infinite Value!
			FILL_AT(ret, Float.POSITIVE_INFINITY, minLength, numer.length);
		} else {
			minLength = numer.length; 
			ret = new float[numer.length]; //don't create NANs for 0/0!
		}
		return DIV(ret, numer, denum, 0, minLength); 
	}

	/**
	  * Divides the float Array numer Element-wise by the double Array denum into a new Array, filling the excess Tail with Infinity when numer is longer.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public float[] DIV(float[] numer, double[] denum) {
		final float[] ret; 
		final int minLength; 
		if (numer.length > denum.length) {
			minLength = denum.length; 
			ret = new float[numer.length]; //you have to check the Sign of the Infinite Value!
			FILL_AT(ret, Float.POSITIVE_INFINITY, minLength, numer.length);
		} else {
			minLength = numer.length; 
			ret = new float[numer.length]; //don't create NANs for 0/0!
		}
		return DIV(ret, numer, denum, 0, minLength); 
	}
	
	/**
	  * Divides the given Range of min Element-wise by sub into a new Array.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public float[] DIV(float[] min, float[] sub, int start, int stop) {
		return DIV(new float[stop], min, sub, start, stop);
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Raises ret Element-wise to the Minimum of a and the scalar y, in the given Range.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public float[] MAX_MIN_PROD(float[] ret, float[] a, float y, int start, int stop) {
		float x; //FALSE; //can also start with any lower Value!
		while (--stop >= start) {
			//			ret[stop] maxAt(a[stop] min b); } //equivalent and faster!
			if ((x = a[stop]) < y) { //use the Minimum
				if (ret[stop] < x) { //update the Maximum
					ret[stop] = x;
				}
			} else {
				if (ret[stop] < y) { //update the Maximum
					ret[stop] = y;
				}
			}
		}
		return ret;
	}

	/** Raises ret Element-wise to the Minimum of a and b, in the given Range.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min b)	  */
	final static public float[] MAX_MIN_PROD(float[] ret, float[] a, float[] b, int start, int stop) {
		float x, y; //FALSE; //can also start with any lower Value!
		while (--stop >= start) {
			//			ret[stop] maxAt(a[stop] min b[stop]); } //equivalent and faster!
			if ((x = a[stop]) < (y = b[stop])) { //use the Minimum
				if (ret[stop] < x) { //update the Maximum
					ret[stop] = x;
				}
			} else {
				if (ret[stop] < y) { //update the Maximum
					ret[stop] = y;
				}
			}
		}
		return ret;
	}

	/** Raises the whole ret Array Element-wise to the Minimum of a and b.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min b)	  */
	final static public float[] MAX_MIN_PROD(float[] ret, float[] a, float[] b) {
		return MAX_MIN_PROD(ret, a, b, 0, ret.length);
	}

	/** Raises the whole ret Array Element-wise to the Minimum of a and the scalar y.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public float[] MAX_MIN_PROD(float[] ret, float[] a, float y) {
		return MAX_MIN_PROD(ret, a, y, 0, ret.length);
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Ring Methods
	///////////////////////////////////////////////////////////////////////////////////

	/// these Methods with scalar Parameters have been removed,
	/// because they can be replaced by their addAt and mulAt Counterparts.
	/**  Linear Mapping in Place: x+=a * y	 replaced by addAt(a*y)  */
	//	final static public float[] addProdAt (float[] ret, float a, float y) {
	/**  Linear Mapping in Place: x-=a * y	 replaced by subAt(a*y)  */
	//	final static public float[] subtProdAt(float[] ret, float a, float y) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by LinAt(a, y*b)  */
	//	final static public float[] BiLinAt   (float[] ret, float a, float y, float b) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by LinAt(a, y*b)  */
	//	final static public float[] BiLinAt   (float[] ret, float[] a, float y, float b) {

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, float[] a, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y;
		}
		return ret;
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, float[] a, float[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y[stop];
		}
		return ret;
	}

	/**  Linear Mapping in Place: x + a*y	 */
	final static public float[] addProd(float[] ret, float[] x, float[] a, float y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] + a[stop] * y;
		}
		return ret;
	}

	/**  Linear Mapping in Place: x + a*y	 */
	final static public float[] addProd(float[] ret, float[] x, float[] a, float[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] + a[stop] * y[stop];
		}
		return ret;
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, double[] a, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y;
		}
		return ret;
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, double a, float[] y, int start, int stop) {
		return addProdAt(ret, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, double a, double[] y, int start, int stop) {
		return addProdAt(ret, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, double a, double[] y) {
		return addProdAt(ret, y, a, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, double[] a, double y) {
		return addProdAt(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, double a, float[] y) {
		return addProdAt(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, float[] a, double y) {
		return addProdAt(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProdAt(float[] ret, float[] a, float[] y) {
		return addProdAt(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProd(float[] ret, float[] x, float a, float[] y, int start, int stop) {
		return addProd(ret, x, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProd(float[] ret, float[] x, float a, float[] y) {
		return addProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProd(float[] ret, float[] x, float[] a, float y) {
		return addProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProd(float[] ret, float[] x, float[] a, float[] y) {
		return addProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProd(float[] x, float a, float[] y, int start, int stop) {
		return addProd(new float[stop], x, y, a, start, stop); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProd(float[] x, float a, float[] y) {
		return addProd(new float[x.length], x, a, y, 0, x.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProd(float[] x, float[] a, float y) {
		return addProd(new float[x.length], x, a, y, 0, x.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public float[] addProd(float[] x, float[] a, float[] y) {
		final float[] ret; 
		final int minProdLength = Math.min(a.length, y.length); 
		final int minLength; 
		if (x.length > minProdLength) {
			minLength = minProdLength; 
			ret = new float[x.length]; 
			System.arraycopy(x, minProdLength, ret, minProdLength, x.length - minProdLength);
		} else {
			minLength = x.length; 
			ret = new float[minProdLength];
			MUL(ret, a, y, x.length, minProdLength);
		}
		return addProd(ret, x, a, y, 0, minLength); 
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public float[] subtProdAt(float[] ret, float[] a, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= a[stop] * y;
		}
		return ret;
	}

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public float[] subtProdAt(float[] ret, float[] a, float[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= a[stop] * y[stop];
		}
		return ret;
	}

	/**  Linear Mapping in Place: x - a*y	 */
	final static public float[] subtProd(float[] ret, float[] x, float[] a, float y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] - a[stop] * y;
		}
		return ret;
	}

	/**  Linear Mapping in Place: x - a*y	 */
	final static public float[] subtProd(float[] ret, float[] x, float[] a, float[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] - a[stop] * y[stop];
		}
		return ret;
	}

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public float[] subtProdAt(float[] ret, double a, float[] y, int start, int stop) {
		return subtProdAt(ret, y, a, start, stop); }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public float[] subtProdAt(float[] ret, double a, float[] y) {
		return subtProdAt(ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public float[] subtProdAt(float[] ret, float[] a, double y) {
		return subtProdAt(ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public float[] subtProdAt(float[] ret, float[] a, float[] y) {
		return subtProdAt(ret, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public float[] subtProd(float[] ret, float[] x, float a, float[] y, int start, int stop) {
		return subtProd(ret, x, y, a, start, stop); }

	/**  Linear Mapping: x - a*y	 */
	final static public float[] subtProd(float[] ret, float[] x, float a, float[] y) {
		return subtProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public float[] subtProd(float[] ret, float[] x, float[] a, float y) {
		return subtProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public float[] subtProd(float[] ret, float[] x, float[] a, float[] y) {
		return subtProd(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public float[] subtProd(float[] x, float a, float[] y, int start, int stop) {
		return subtProd(new float[stop], x, y, a, start, stop);	}

	/**  Linear Mapping: x - a*y	 */
	final static public float[] subtProd(float[] x, float a, float[] y) {
		return subtProd(new float[x.length], x, a, y, 0, x.length);	}

	/**  Linear Mapping: x - a*y	 */
	final static public float[] subtProd(float[] x, float[] a, float y) {
		return subtProd(new float[x.length], x, a, y, 0, x.length);	}

	/**  Linear Mapping: x - a*y	 */
	final static public float[] subtProd(float[] x, float[] a, float[] y) {
		return subtProd(new float[x.length], x, a, y, 0, x.length);	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] LinAt(float[] ret, float a, float y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a + y; }
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] LinAt(float[] ret, float[] a, float y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a[stop] + y; }
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] LinAt(float[] ret, float a, float[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a + y[stop]; }
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] LinAt(float[] ret, float[] a1, float[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a1[stop] + y[stop]; }
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] LinAt(float[] ret, float a1, float y) {
		return LinAt(ret, a1, y, 0, ret.length); }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] LinAt(float[] ret, float[] a1, float y) {
		return LinAt(ret, a1, y, 0, ret.length); }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] LinAt(float[] ret, float a1, float[] y) {
		return LinAt(ret, a1, y, 0, ret.length); }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] LinAt(float[] ret, float[] a1, float[] y) {
		return LinAt(ret, a1, y, 0, ret.length); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**  Linear Mapping: x*=a + y	*/
	final static public float[] Lin(float[] ret, float[] x, float a, float y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop]*a + y; }
		return ret;
	}
	
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] Lin(float[] ret, float[] x, float[] a, float y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a[stop] + y; }
		return ret;
	}
	
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] Lin(float[] ret, float[] x, float a, float[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a + y[stop]; }
		return ret;
	}
	
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] Lin(float[] ret, float[] x, float[] a1, float[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a1[stop] + y[stop]; }
		return ret;
	}
	
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] Lin(float[] x, float a1, float y) {
		return Lin(new float[x.length], x, a1, y, 0, x.length); }
	
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] Lin(float[] x, float[] a, float y) {
		final float[] ret; 
		final int minLength; 
		if (x.length > a.length) {
			minLength = a.length; 
			ret = new float[x.length];
		} else {
			minLength = x.length; 
			ret = new float[a.length];
		}
		FILL_AT(ret, y, minLength, ret.length); //not well defined! 
		return Lin(ret, x, a, y, 0, minLength); 
	}
	
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public float[] Lin(float[] x, float a, float[] y) {
		final float[] ret; 
		final int minLength; 
		if (x.length > y.length) {
			minLength = y.length; 
			ret = new float[x.length];
			MUL(ret, x, a, y.length, ret.length);
		} else {
			minLength = x.length; 
			ret = new float[y.length];
			System.arraycopy(y, x.length, ret, x.length, ret.length - x.length);
		}
		return Lin(ret, x, a, y, 0, minLength); 
	}
	
	/**  Linear Mapping in Place: x*=a + y
	 * most complex Case...
	 */
	final static public float[] Lin(float[] x, float[] a, float[] y) {
		final float[] ret; 
		final int minProdLength = Math.min(a.length, x.length); 
		final int minLength; 
		if (y.length > minProdLength ) {
			minLength = minProdLength ; 
			ret = new float[y.length];
			System.arraycopy(y, minLength, ret, minLength, ret.length - minLength);
		} else {
			minLength = y.length; 
			ret = new float[minProdLength];
			MUL(ret, x, a, minLength, ret.length);
		}
		return Lin(ret, x, a, y, 0, minLength);
	}
		
	/////////////////////////////////////////////////////////////////////////////////////

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float[] a, float[] y, float[] b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a[stop] + y[stop] * b[stop];
		}
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float[] a, float[] y, float b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a[stop] + y[stop] * b;
		}
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float a, float[] y, float[] b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a + y[stop] * b[stop];
		}
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float a, float[] y, float b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a + y[stop] * b;
		}
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float[] a, float y, float[] b, int start, int stop) {
		return BiLin(ret, x, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float a, float[] y, float b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float[] a, float[] y, float b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float[] a, float[] y, float[] b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float a, float[] y, float[] b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] ret, float[] x, float[] a, float y, float[] b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float[] a, float[] y, float[] b, int start, int stop) {
		return BiLin(new float[stop], x, a, b, y, start, stop);
	}

	/**BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float[] a, float[] y, float b, int start, int stop) {
		return BiLin(new float[stop], x, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float a, float[] y, float[] b, int start, int stop) {
		return BiLin(new float[stop], x, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float a, float[] y, float b, int start, int stop) {
		return BiLin(new float[stop], x, a, y, b, start, stop);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float[] a, float y, float[] b, int start, int stop) {
		return BiLin(new float[stop], x, a, b, y, start, stop);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float a, float[] y, float b) {
		final float[] ret; 
		final int minLength; 
		if (y.length > x.length) {
			minLength = x.length; 
			ret = new float[y.length];
			MUL(ret, y, b, minLength, ret.length);
		} else {
			minLength = y.length; 
			ret = new float[x.length];
			MUL(ret, x, a, minLength, ret.length);
		}
		return BiLin(ret, x, a, y, b, 0, minLength);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float[] a, float[] y, float b) {
		final float[] ret; 
		final int minProd1Length = Math.min(a.length, x.length); 
		final int minLength; 
		if (y.length > minProd1Length ) {
			minLength = minProd1Length ; 
			ret = new float[y.length];
			MUL(ret, y, b, minLength, ret.length);
		} else {
			minLength = y.length; 
			ret = new float[minProd1Length];
			MUL(ret, x, a, minLength, ret.length);
		}
		return BiLin(ret, x, a, y, b, 0, minLength);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float[] a, float[] y, float[] b) {
		final float[] ret; 
		final int minProd1Length = Math.min(a.length, x.length); 
		final int minProd2Length = Math.min(b.length, y.length); 
		final int minLength; 
		if (minProd2Length > minProd1Length ) {
			minLength = minProd1Length ; 
			ret = new float[minProd2Length];
			MUL(ret, y, b, minLength, ret.length);
		} else {
			minLength = minProd2Length; 
			ret = new float[minProd1Length];
			MUL(ret, x, a, minLength, ret.length);
		}
		return BiLin(ret, x, a, y, b, 0, minLength);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float a, float[] y, float[] b) {
		return BiLin(y, b, x, a); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public float[] BiLin(float[] x, float[] a, float y, float[] b) {
		return BiLin(x, a, b, y); }

	/////////////////////////////////////////////////////////////////////////////////////

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public float[] BiLinAt(final float[] ret, final float[] a, final float[] y, final float b, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a[stop] + y[stop] * b; }
		return ret;
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public float[] BiLinAt(final float[] ret, final float a, final float[] y, final float[] b, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a + y[stop] * b[stop]; }
		return ret;
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public float[] BiLinAt(final float[] ret, final float a, final float[] y, final float b, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a + y[stop] * b; }
		return ret;
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public float[] BiLinAt(final float[] ret, final float[] a, final float y, final float[] b, final int start, final int stop) {
		return BiLinAt(ret, a, b, y, start, stop); }
		
	/////////////////////////////////////////////////////////////////////////////////////

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public float[] BiLinAt(final float[] ret, final float a, final float[] y, final float b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length); 
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public float[] BiLinAt(final float[] ret, final float[] a, final float[] y, final float b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public float[] BiLinAt(float[] ret, float[] a, float[] y, float[] b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public float[] BiLinAt(float[] ret, float a, float[] y, float[] b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length);
	}

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public float[] BiLinAt(float[] ret, float[] a, float y, float[] b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length);
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Subtracts the Part from a which lies parallel to the normed Vector arg (|arg| = 1).
	  * Used primarily in Orthogonalization.
	  * this -= arg*(arg*this)
	  */
	final static public float[] SUB_PART_AT(final float[] a, float[] arg) {
		return SUB_PART_AT(a, arg, 1);
	}

	/** Subtracts the Part from a which lies parallel to the normed Vector arg (|arg| = 1).
	  * Used primarily in Orthogonalization.
	  * this -= arg*(arg*this)
	  */
	final static public float[] SUB_PART(float[] a, float[] arg) {
		return SUB_PART(a, arg, 1);
	}

	/** Subtracts the Part from a which lies parallel to the Vector arg.
	  * Used primarily in Orthogonalization.
	  * If argSqrNorm == null, it is assumed to be 1 (orthoNormal)
	  * this -= arg*((arg*this)/(arg*arg))
	  */
	final static public float[] SUB_PART(float[] a, float[] arg, float argSqrNorm) {
		float Prod = (float) MAP(a, arg, 0, arg.length) / argSqrNorm;
		return subtProd(a, arg, Prod);
	}

	/** Subtracts the Part from a which lies parallel to the Vector arg.
	  * Used primarily in Orthogonalization.
	  * If argSqrNorm == null, it is assumed to be 1 (orthoNormal)
	  * this -= arg*((arg*this)/(arg*arg))
	  */
	final static public float[] SUB_PART_AT(float[] a, float[] arg, float argSqrNorm) {
		float Prod = (float) MAP(a, arg, 0, arg.length) / argSqrNorm;
		subtProdAt(a, arg, Prod);
		return a;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods
	///////////////////////////////////////////////////////////////////////////////////
	
	/** The Default Separator Character to use for the STREAM Methods */
	final static public char DEFAULT_SEPARATOR = '\t';

	/** Streams out the complete given Array. 
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 * @param separator the Separator Character
	 */
	final static public void STREAM(final float[] vals, final PrintStream stream, final char separator) {
		STREAM(vals, stream, 0, vals.length, separator);
	}

	/** Streams out the complete given Array. 
	 * defaults the separator the Default Separator Character
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 */
	final static public void STREAM(final float[] vals, final PrintStream stream) {
		STREAM(vals, stream, 0, vals.length, DEFAULT_SEPARATOR);
	}
	
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(final float[] vals, final PrintStream stream, final int startCol, final int stopCol, final char separator) {
		if (startCol >= stopCol) {
			return;
		}
		stream.print(vals[startCol]); //omit the last Separator...
		for (int i = startCol; ++i < stopCol;) {
			stream.print(separator);
			stream.print(vals[i]);
		}
	}

	/** streams the Numbers of the given Array out to the Stream using the given Formatter
	 * 
	 * @param d Array to stream out
	 * @param strSep Separator String between Numbers 
	 * @param pw PrintWriter to stream to
	 * @param formatter Number Formatter to use 
	 */
	final static public void STREAM(final float[] d, final Writer pw, final NumberFormatter formatter, final String strSep) throws IOException {
		for (int i = -1; ++i < d.length;) {
			formatter.stream(pw, d[i]);
			pw.write(strSep); 
		}
		//return pw; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Shifting and Rotating
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * rotates the given Vector right by 1, i.e. 
	 * a[0] = a[length-1]
	 * a[1] = a[0]; 
	 * a[2] = a[1]; etc. 
	 * @param vector
	 * @param shift
	 */
	final static public float[] ROR_AT(final float[] vector, int length) {
		final float tmp = vector[--length]; 
		System.arraycopy(vector, 0, vector, 1, length);
		vector[0] = tmp; 
		return vector; 
	}

	/**
	 * shifts the given Vector right by the given Amount, i.e.
	 * a[shift  ] = a[0]; 
	 * a[shift+1] = a[1]; etc. 
	 * @param vector
	 * @param length 
	 * @param shift
	 */
	final static public int SHR_AT(final float[] vector, final int length, final int shift) {
		if (shift < 0) {
			return SHL_AT(vector, length, -shift); }
		System.arraycopy(vector, 0, vector, shift, length);
		FILL_AT(vector, 0, 0, shift); 
		return length + shift; 
	}

	/**
	 * shifts the given Vector left by the given Amount
	 * @param vector
	 * @param shift
	 * @return the new valid Length of the Array
	 */
	final static public int SHL_AT(final float[] vector, int length, final int shift) {
		if (shift < 0) {
			return SHR_AT(vector, length, -shift); }
	/*	int i = vector.length; 	//the Direction is important! 
		for (;--i>=shift;) {	//so you don't overwrite your own Data!
			vector[i] = vector[i-shift]; } 
		for (;--i>=0;) {
			vector[i] = 0; } 
	*/	System.arraycopy(vector, shift, vector, 0, length -= shift);	//large Shift
		return length; 
	}

	/**
	 * shifts the given Vector left by the given Amount
	 * @param vector
	 * @param shift
	 * @return the new valid Length of the Array
	 */
	final static public float[] ROL_AT(final float[] vector, int length) {
		final float tmp = vector[0];
		System.arraycopy(vector, 1, vector, 0, --length);	//large Shift
		vector[length] = tmp;
		return vector; 
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Differentiation and Integration
	///////////////////////////////////////////////////////////////////////////////////
	
	/** Summen werden r�ckw�rts gebildet! 
	 * Dadurch werden die von der Diff-Operation �brig gebliebenen Elemente widerverwendet 
	 * und die Vektoren bleiben einsatzbereit. 
	 * 
	 * @param items the Values to aggregate 
	 */
	final static public void SUMM_AT(final float[] items) { SUM_AT(items, 0, items.length); }
	
	/** Summen werden r�ckw�rts gebildet! 
	 * Dadurch werden die von der Diff-Operation �brig gebliebenen Elemente widerverwendet 
	 * und die Vektoren bleiben einsatzbereit. 
	 * 
	 * @param items the Values to aggregate 
	 * @param start the first Index (exclusive)
	 * @param stop the last Index (exclusive)
	 */
	final static public void SUM_AT(final float[] items, final int start, final int stop) {
		double tmp1 = 0; 
		for (int i = stop;  --i >= start;  ) {
			tmp1 = items[i] += tmp1; }	//items[i].addAt(items[i+1]);
	}
	
	/** calculates the (forward) Difference Vector in Place 
	 * This keeps the Vectors usable after Differentiation 
	 * and the leftover Elements can be reused on Integration. 
	 * 
	 * @param items the Vector to differentiate
	 */
	final static public void DIFF_AT(final float[] items) { DIFF_AT(items, 0, items.length-1); }
	
	/** calculates the (forward) Difference Vector in Place 
	 * This keeps the Vectors usable after Differentiation 
	 * and the leftover Elements can be reused on Integration. 
	 * 
	 * @param items the Vector to differentiate
	 * @param start the first Index (exclusive)
	 * @param stop the last Index (exclusive)
	 */
	final static public void DIFF_AT(final float[] items, final int start, final int stop) {
		//float tmp1, tmp2 = a[0]; //Original
		//int i = 0; while (++i <= itemCount) {
		//	tmp1 = tmp2; tmp2 = a[i]; a[i] -= tmp1; }	//== a[i].subAt(a[i+1]);
		for (int i = start-1; ++i < stop;) { //this Order removes the LAST Item!
			items[i] -= items[i+1]; } //== items[i].subAt(items[i+1]);
	}
	
	/**Returns the Integrated Polynom in Place: int(i)= a(i) + a(i+1)
	 * This is the reverse Operation to diffAt().
	 * The Integral Polynom has one Item more than the original Polynom.
	 * This is either restored from the highest Element
	 * or assumed to Zero. 	 
	 */
	final static public float[] SUMM_POLYNOM_AT(final float[] items, final int itemCount)	{	//first do the Rotation, then the Multiplication, thus a[i]*=i
		ROR_AT(items, itemCount);	//do a large Rotation left (mind the opposite reading Direction!)
		for (int i = 1; ++i < itemCount;) { 
			items[i] /= i;} 
		return items; 
	}

	/**
	 * Calculates the Differentiated Polynomial in Place: the reverse Operation to SUMM_POLYNOM_AT.
	 * @see streamIO.copy.group.ring.metric.body.vector.IManifold#diffAt()
	 */
	final static public float[] DIFF_POLYNOM_AT(final float[] items, final int itemCount) {
		for (int i = 1; ++i < itemCount;) { //Skip multiplying Items 0 and 1
			items[i] *= i; }
		ROL_AT(items, itemCount);	//do a large Rotation right... (mind the opposite reading Direction!)
		return items; } //...preserving highest Item, don't set it to zero
	
	///////////////////////////////////////////////////////////////////////////////////
	// statistical Methods
	///////////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * returns the Counts of the different Values e.g. for a Histogram
	 * @param ret the counts (for Incrementation if already counted)
	 * @param a the Values to count (Values outside the Bounds are not counted) 
	 * @return the Counts of the different Values e.g. for a Histogram
	 */
	final static public int[] COUNT(final int numBins, final float[] a) {
		return COUNT(numBins, a, 0, a.length); }
	
	/** 
	 * returns the Counts of the different Values e.g. for a Histogram
	 * @param a the Values to count (Values outside the Bounds are not counted) 
	 * @param start the first value to count (inclusive) 
	 * @param stop the last value to count (exclusive) 
	 * @return the Counts of the different Values e.g. for a Histogram
	 */
	final static public int[] COUNT(final float[] a, final int numBins, final int start, final int stop) {
		final float[] minMax = VectorFloat.MIN_MAX_VAL(a); 
		return COUNT(null, numBins, a, start, stop, minMax[0], minMax[1]); 
	}
	
	/** 
	 * returns the Counts of the different Values e.g. for a Histogram
	 * @param ret the optional (null allowed) counts (for Incrementation if already counted)
	 * @param min the minimum Value to expect (also the Offset to the Count)
	 * @param max the maximum Value to expect 
	 * @param a the Values to count (Values outside the Bounds are not counted) 
	 * @param start the first value to count (inclusive) 
	 * @param stop the last value to count (exclusive) 
	 * @return the Counts of the different Values e.g. for a Histogram
	 */
	final static public int[] COUNT(int numBins, final float[] a, final int start, final int stop) {
		final float[] minMax = VectorFloat.MIN_MAX_VAL(a, start, stop); 
		return COUNT(null, numBins, a, start, stop, minMax[0], minMax[1]); 
	}
	
	/** 
	 * returns the Counts of the different Values e.g. for a Histogram
	 * @param ret the optional (null allowed) counts (for Incrementation if already counted)
	 * @param a the Values to count (Values outside the Bounds are not counted) 
	 * @param start the first value to count (inclusive) 
	 * @param stop the last value to count (exclusive) 
	 * @param min the minimum Value to expect (also the Offset to the Count)
	 * @param max the maximum Value to expect 
	 * @return the Counts of the different Values e.g. for a Histogram
	 */
	final static public int[] COUNT(int[] ret, int numBins, 
			final float[] a, final int start, final int stop, 
			final double min, final double max) {
		if (ret == null)
			ret  = new int[numBins];
		else
			numBins = ret.length; 
		final double scale = numBins/(max - min); 
		for(int i = stop; --i >= start;) {
			final float val = a[i]; 
			if ((min <= val) &&
				(val <  max))
				++ret[(int)((val-min)*scale)];
		}
		return ret; 
	}
	
	/**
	 * depending on whether the Mean is known a priori or derived from the Sample,
	 * you must divide by N=stop-start or by (N-1). 
	 * But in the rare Case where this Difference Matters, 
	 * you are dealing with too little Data anyway!   
	 * @return the denoted Moment of this Distribution 	 */
	final static public double MOMENT(final float[] items, final int moment, final int start, final int stop, final double mean) {
		if (moment == 0) {
			return stop-start; } 
		double ret = 0; 
		for(int i = stop; --i >= start; ) {
			double diff = items[i]-mean;
			switch (moment) {
				case 0 : return stop-start; 
				case 1 : diff = Math.abs(diff); break; 
				case 2 : diff *= diff; break; 
				case 3 : diff *= diff*diff; break; 
				case 4 : diff *= diff; diff *= diff; break;  
				default : throw new RuntimeException("Higher Moments than 4 are not stable!");  
			}
			ret += diff; 
		} //
		return ret/(stop-start); } //... 
			
	/**
	 * Fills the given Range of Statistical Moments (Count, Absolute Deviation, Variance, Skewness, Curtosis) of the Data.
	 * @param moments the List of Moments to be filled by this Routine
	 * starting from
	 * moments[0] = #Items
	 * moments[1] = Absolute Deviation
	 * moments[2] = Squared Deviation = Variation
	 * moments[3] = cubed Deviation = Skewness
	 * moments[4] = quadrupled Deviation = Curtosis
	 * @return the Mean of this Distribution
	 */
	final static public double MOMENTS(final float[] items, final double[] moments, final int start, int stop) {
		moments[0] = stop-start; 
		final double mean = SUM(items, start, stop)/moments[0]; 
		while (--stop >= start) {
			final double diff = items[stop]-mean;
			double prod = diff; 
			moments[1] += Math.abs(prod);
			if (moments.length < 2) {
				continue; }
			for (int i = 1; ;) {
				moments[i] += prod;
				if (++i >= moments.length) {
					break; } //save last Multiplication
				prod *= diff;  
			}
		}
		for (int i = moments.length; --i >= 1;) {
			moments[i] /= moments[0]; }
		return mean; }
			
	/** Calculates the Covariance between two Distributions given their Means and a common Length.
	 * @return the Variance of this Distribution (most frequent Value)	 */
	final static public double COVARIANCE(final int length,
	final float[] items1, final double mean1, 
	final float[] items2, final double mean2) {
		double cov = 0;
		for (int i = length; --i >= 0;) {
			cov +=(items1[i]-mean1)*(items2[i]-mean2); }
		return cov/(length-1); }
				
	/** Calculates the Covariance between two same-length Distributions given their Means.
	 * @return the Variance of this Distribution (most frequent Value)	 */
	final static public double COVARIANCE(final float[] items1, final double mean1, final float[] items2, final double mean2) {
		if (items1.length != items2.length) {
			throw new RuntimeException("For calculating the CoVariance, the Dimensions must match: "+items1.length+" == "+items2.length); }
		return COVARIANCE(items1.length, items1, mean1, items2, mean2); }
	
	///////////////////////////////////////////////////////////////////////////////////
	/// affine dimension-wise Mapping in n Dimensions
	///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Preparation for affine Mapping:
	 * all Transformations are precalculated:
	 * DV = (OV-O)/OW*DW+OD
	 * DV = OV*W + (OD-O*W) with W = DW/OW
	 * The Destination Vectors are modified!
	 * @param in_Origin       the Origin of the Input Space
	 * @param in_OriginWidth  the Widths of the Input Space
	 * @param outDestin       the Origin of the Destination Space
	 * @param outDestinWidth  the Widths of the Destination Space
	 *
	 * @see graphic.math3D.Line which encodes this Mapping into an Object.
	 */
	final static public void PREPARE_AFFINE_MAP_AT(
		float[] in_Origin,
		float[] in_OriginWidth,
		float[] outDestin,
		float[] outDestinWidth) {
		DIV_AT(outDestinWidth, in_OriginWidth);
		subtProdAt(outDestin, outDestinWidth, in_Origin);
	}

	/**
	 * Maps the given Vector in Place from the Unity Cube to the Cube with the given Origin and Width.
	 * @return the given Vector mapped from the Unity Cube
	 * to the Cube with the given Origin and Width.
	 *
	 * @see graphic.math3D.Line which encodes this Mapping into an Object.
	 */
	final static public float[] AFFINE_MAP_AT(
			final float[] ret, final float[] Origin, final float[] Width) {
		return LinAt(ret, Width, Origin); } //a*x+b = y

	/**
	 * Not very effective Implementation of affine Mapping:
	 * all Transformations are performed explicitly
	 * resulting in double Work:
	 * DV = (OV-O)/OW*DW+OD
	 * DV = OV*W + (OD-O*W) with W = DW/OW
	 *
	 * @see graphic.math3D.Line which encodes this Mapping into an Object.
	 */
	final static public float[] AFFINE_MAP_AT(
		float[] ret,
		float[] Origin,
		float[] OriginWidth,
		float[] Destin,
		float[] DestinWidth) {
		SUB_AT(ret, Origin);
		DIV_AT(ret, OriginWidth);
		LinAt(ret, DestinWidth, Destin);
		return ret;
	} //

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods for ad hoc Adressing convoluted Arrays
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fastest Method to access a List as a Matrix:
	 * @param base  the backing Array of this float[]
	 * @param Cols  the Number of Columns in each Sheet of this float[]
	 * @param Row   the Row    for the Element of this float[]
	 * @param Col   the Column for the Element of this float[]
	 * @return the Element of the Vector interpreted as a Matrix	 */
	final static public float Matrix(final float[] base, final int Cols, final int Row, final int Col) {
		if ((Col >= Cols) || 
			(Col <  0)) 
			throw new IndexOutOfBoundsException("Range: 0.." + Cols + " Actual: " + Col);
		return base[Row * Cols + Col];
	}

	/**
	 * Fastest Method to access a List as a float[]:
	 * @param base  the backing Array of this float[]
	 * @param Rows  the Number of Rows in each Sheet of this float[]
	 * @param Cols  the Number of Columns in each Sheet of this float[]
	 * @param Sheet the Sheet  for the Element of this float[]
	 * @param Row   the Row    for the Element of this float[]
	 * @param Col   the Column for the Element of this float[]
	 * @return the Element of the Vector interpreted as a float[]
	 */
	final static public float Tensor(float[] base, int Rows, int Cols, int Sheet, int Row, int Col) {
		if ((Col >= Cols) || (Col < 0)) 
			throw new IndexOutOfBoundsException("Range: 0.." + Cols + " Actual: " + Col);
		if ((Row >= Rows) || (Row < 0)) 
			throw new IndexOutOfBoundsException("Range: 0.." + Rows + " Actual: " + Row);
		return base[(Sheet * Rows + Row) * Cols + Col];
	}

	/**
	 * Fastest Method to access a List as an n-dimensional float[]:
	 * @param base the backing Array of this float[]
	 * @param Cols the Multi-Index for the Dimensions of this float[]
	 * @param Col  the Multi-Index for this float[]
	 * @return the Element of the Vector interpreted as a float[]
	 */
	final static public float Tensor(final float[] base, final int[] Cols, final int[] Col) {
		int i = Col.length;
		int ndx = Col[--i]; //0; //saves 1 Iteration
		while (--i >= 0) {
			int colsi = Cols[i];
			int coli = Col[i];
			if ((coli > colsi) || 
				(coli < 0)) {
				throw new IndexOutOfBoundsException("Range: 0.." + colsi + " Actual: " + coli);
			}
			ndx *= colsi;
			ndx += coli;
		}
		return base[ndx];
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Backing Value Array for the float[]	 */
	protected float[] items;
	
	/**
	 * Returns the Items of this Vector, either by Reference or as a Copy.
	 * @param original Returns the internal Structure by Reference!
	 * Should usually be false(Default), except when it is guaranteed,
	 * that the Array will be used Read-Only.
	 * @return the Items	 */
	public float[] getItems(final boolean original) {
		if (original) {
			return items; }
		return VectorFloat.COPY(items, new float[itemCount]);
	}

	/** Returns a Copy of the Items of this Vector.
	 * @return the Items	 */
	public float[] getItems() { return getItems(false); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized float getFloatAt(final int index) {
		if (indexInRange(index)) 
			return items[index];
		return 0;
	}

	/** Wraps the primitive float Value at the given Index into a ByRefFloat Object.
	 * @return the item at the given Position as an Object */
	public Object getAt(final int i) {
		if (!indexInRange(i)) { return null; }
		return new ByRefFloat(getFloatAt(i));
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
	public float setAt(final int index, final float value) {
		float ret = 0; //Float.NaN; 
		if (indexInRange(index))
			ret = items[index];
		else {
			if (value == 0)
				return 0;
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
		return new ByRefFloat(setAt(index, (float) ByRefDouble.GET_DOUBLE(value))); 
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
	public float setAt(final int index, final double value) {
		return setAt(index, (float) value);
	}

	/**Inserts the value at the specified index.
	 * All following value in this Container are shifted to the right.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * @param	  value	the Value to insert.
	 * @param	  index   the index of the value to insert at.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 */
	public void insertAt(final int index, final float value) {
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
	// TODO: LOGIC: decrements itemCount unconditionally before the range check, corrupting the Vector's size on an out-of-range access; same defect as VectorChar/VectorLong/VectorShort/VectorInt removeAt.
	public float removeAt(final int index) {
		if (index > --itemCount)  //
			return 0;
		final float ret = items[index];
		System.arraycopy(items, index+1, items, index, itemCount-index);
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX) 
	/// for multidimensional rectangular Arrays 
	////////////////////////////////////////////////////////////////////////////////

	/** Reads a Value from the rectangular 2-dim View of this Vector at the given Row/Column.
	 * @return the Value at the given Position	 */
	public float getAt(int Row, int Col) {
		return items[Row * dimFactors[0] + Col * dimFactors[1]];
	}

	/** sets the given Value 	 */
	public void setAt(int Row, int Col, float Value) {
		items[Row * dimFactors[0] + Col * dimFactors[1]] = Value;
	}

	/** Reads a Value from the rectangular 3-dim View of this Vector at the given Sheet/Row/Column.
	 * @return the Value at the given Position	 */
	public float getAt(int Sheet, int Row, int Col) {
		return items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]];
	}

	/** sets the given Value 	 */
	public void setAt(int Sheet, int Row, int Col, float Value) {
		items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]] = Value;
	}

	/** Reads a Value from the rectangular multi-dim View of this Vector at the given Multi-Index.
	 * @return the Value at the given Position	 */
	public float getAt(int[] Col) {
		return items[multiIndex(Col)];
	}

	/** sets the given Value 	 */
	public void setAt(int[] Col, float Value) {
		items[multiIndex(Col)] = Value;
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
	public VectorFloat(int initialCapacity, int capacityIncrement_) {
		super();
		items = new float[initialCapacity];
		capacityIncrement = capacityIncrement_;
		//		mEnum = new ArrayEnum(Items, ItemCount);
		//		mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty VectorFloat with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the VectorFloat.	 */
	public VectorFloat(int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty VectorFloat.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public VectorFloat() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/** Constructs an VectorFloat by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public VectorFloat(Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an VectorFloat from the given Object.	  */
	public VectorFloat(Object arg, int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorFloat from the given Object.	  */
	public VectorFloat(float[] arg, int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorFloat from the given Object
	  * and copies the Elements into this VectorFloat.	  */
	public VectorFloat(float[] arg) {
		this(arg.length, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorFloat addItem(final float item) {
		setAt(itemCount, item);
		return this;
	}

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorFloat addItem(final double item) {
		setAt(itemCount, item);
		return this;
	}

	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	final public synchronized void copyInto(int[] anArray) {
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
	final public synchronized float[] toArray() {
		return COPY(this.items, this.itemCount); }

	/**Trims the capacity of this VectorInt to be the VectorInt's current
	 * size. An application can use this operation to minimize the
	 * storage of a VectorInt.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			float[] oldData = items;
			items = new float[itemCount];
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
		final int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) 
			return oldCapacity;
		final int newCapacity = ENLARGED_CAPACITY(oldCapacity, capacityIncrement, minCapacity); 
		final float[] oldData = items; items = new float[newCapacity];
		if (itemCount > 0) 
			System.arraycopy(oldData, 0, items, 0, itemCount);
		return newCapacity;
	}

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 *
	 * The Optimization here is that the Capacity can be ensured before
	 * and that additional Fields can be set.	 */
	public VectorFloat copyAt(final float[] arg_) {
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
	public ICopyAble copyAt(final Object arg) {
		if (arg instanceof VectorFloat) {
			VectorFloat arg_ = (VectorFloat) arg;
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
	public ICopyAble shallowCopyAt(final Object arg) {
		if (arg instanceof VectorFloat) {
			VectorFloat arg_ = (VectorFloat) arg;
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
		return new VectorFloat(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Arithmetic Methods for Arrays
	////////////////////////////////////////////////////////////////////////////////

	/** Determines the smallest Value held by this Vector.
	 * @return the Minimum Value in this Vector	 */
	public double MinVal() { return MIN_VAL(items); }

	/** Determines the Index of the smallest Value held by this Vector.
	 * @return the Position of the Minimum Value in this Vector	 */
	public int MinPos() { return MIN_POS(items); }

	/** Determines the largest Value held by this Vector.
	 * @return the Maximum Value in this Vector	 */
	public double MaxVal() { return MAX_VAL(items); }

	/** Determines the Index of the largest Value held by this Vector.
	 * @return the Position of the Maximum Value in this Vector	 */
	public int MaxPos() { return MAX_POS(items); }

	/////////////////////////////////////////////////////////////////////////////////////

	/** Normalizes this Vector by bringing it into the canonical Form
	 * so that getAt(getInt()) != 0 
	 */
	public VectorFloat canonicalizeAt() {
		while (items[--itemCount] == 0);
		++itemCount;
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorFloat addAt(final VectorFloat vector) {
		return addAt(vector.items, 0, vector.itemCount);
	}

	/** subtracts the given Portion of the values from this Vector */
	public VectorFloat subAt(final VectorFloat vector) {
		return subAt(vector.items, 0, vector.itemCount);
	}

	// TODO: LOGIC: calls subAt instead of a multiplicative operation; same defect as VectorChar/VectorLong/VectorShort/VectorInt mulAt(VectorX).
	/** multiplies this Vector by the given Portion of the values */
	public VectorFloat mulAt(final VectorFloat vector) {
		return subAt(vector.items, 0, vector.itemCount);
	}

	// TODO: LOGIC: calls subAt instead of a divisive operation; same defect as VectorChar/VectorLong/VectorShort/VectorInt divAt(VectorX).
	/** divides this Vector by the given Portion of the vector*/
	public VectorFloat divAt(final VectorFloat vector) {
		return subAt(vector.items, 0, vector.itemCount);
	}

	/** subtracts the given Portion of the values from this Vector */
	public VectorFloat subAt(final float[] values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			COPY(values, itemCount, stop, items);
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
	public VectorFloat subAt(final double[] values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			COPY(values, itemCount, stop, items);
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

	/** increments the Values of this Vector by the given Amount	 */
	public VectorFloat addAt(final double value) {
		VectorFloat.ADD_AT(items, value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorFloat subAt(final double value) {
		VectorFloat.ADD_AT(items, -value, 0, itemCount);
		return this;
	}

	// TODO: LOGIC: ignores the value parameter and instead multiplies items by itself element-wise; same defect as VectorChar/VectorLong/VectorShort/VectorInt mulAt(scalar).
	/** multiplies this Vector by the given Portion of the values */
	public VectorFloat mulAt(final double value) {
		return mulAt(items, 0, itemCount);
	}

	// TODO: LOGIC: ignores the value parameter and instead divides items by itself element-wise (yielding all 1s); same defect as VectorChar/VectorLong/VectorShort/VectorInt divAt(scalar).
	/** divides this Vector by the given Portion of the vector*/
	public VectorFloat divAt(final double value) {
		return divAt(items, 0, itemCount);
	}

	/** adds the given Portion of the values to this Vector */
	public VectorFloat addAt(final float[] values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			COPY(values, itemCount, stop, items);
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
	public VectorFloat addAt(final double[] values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			COPY(values, itemCount, stop, items);
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

	/** multiplies the given Portion of the values with this Vector */
	public VectorFloat mulAt(final float[] values, final int start, int stop) {
		while (values[--stop] == 0); //normalize 
		++stop;
		if (stop >= itemCount) {
			stop = itemCount; //all other Values are multiplied by 0
		} else {
			itemCount = stop; //all other Values are multiplied by 0
		}
		MUL_AT(items, values, start, itemCount);
		//normalizeAt(); //don't need to normalize 
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorFloat mulAt(final double[] values, final int start, int stop) {
		while (values[--stop] == 0); //normalize 
		++stop;
		if (stop >= itemCount) {
			stop = itemCount; //all other Values are multiplied by 0
		} else {
			itemCount = stop; //all other Values are multiplied by 0
		}
		MUL_AT(items, values, start, itemCount);
		//normalizeAt(); //don't need to normalize 
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorFloat divAt(final float[] values, final int start, int stop) {
		while (values[--stop] == 0); //normalize 
		++stop;
		if (stop >= itemCount) {
			stop = itemCount; //all other Values are multiplied by 0
		} else { //all other Values are divided by 0 and become Infinity!
			FILL_AT(items, Float.POSITIVE_INFINITY, stop, itemCount);
		}
		DIV_AT(items, values, start, itemCount);
		//normalizeAt(); //don't need to normalize 
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorFloat divAt(final double[] values, final int start, int stop) {
		while (values[--stop] == 0); //normalize 
		++stop;
		if (stop >= itemCount) {
			stop = itemCount; //all other Values are multiplied by 0
		} else { //all other Values are divided by 0 and become Infinity!
			FILL_AT(items, Float.POSITIVE_INFINITY, stop, itemCount);
		}
		DIV_AT(items, values, start, itemCount);
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
			  if (arg instanceof float[]) {
				  float[] arg_ = (float[]) arg;
				  copyAt(Permutation.map(Items, Items.length, arg_, arg_.length));
				  return this; }
			  return super.mulAt(arg); }
	*/
	/**Multiply the Vector by an Object.
	 * This extends the standard Set Multiplication
	 * by the Multiplication with a Permutation.	 */
	/*	  public SemiGroupM mul(Object arg) {
			  if (arg instanceof Permutation) return new VectorFloat(Permutation.map(Items, Items.length, (Permutation) arg), capacityIncrement);
			  if (arg instanceof float[]    ) return new VectorFloat(Permutation.map(Items, Items.length, (float[]	  ) arg), capacityIncrement);
			  return super.mul(arg); }
	*/

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors for usage as a rectangular, multidimensional Array
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor only for use within this Class
	 * to generate different Indexing Schemes on the same Data.
	 * @param Values, the backing Values of the Matrix,
	 *  possibly shared with other Matrices.
	 * @param Factors the Factors for the Items in the float[]
	 */
	protected VectorFloat(final float[] Values, final int[] Factors) {
		this.dimFactors = Factors;
		this.items = Values;
	}

	/**
	 * @param Rows the Numbers of Rows    in the Matrix
	 * @param Cols the Numbers of Columns in the Matrix
	 */
	/*	public VectorFloat(int Rows, int Cols) {
			this.Cols = new int[2];
			this.Cols[0] = Cols;
			this.Cols[1] = Rows;
			IndexFactor = new int[2];
			IndexFactor[1] = 1;
			IndexFactor[2] = Cols;
			items = new float[Rows * Cols];
		}
	*/
	/**
	 * Constructs a rectangular, multidimensional Vector View with the given Sizes per Dimension.
	 * @param Cols the Numbers of Columns in the float[]
	 */
	public VectorFloat(final int[] dimSizes_) {
		this(dimSizes_, null);
	}

	/**
	 * @param Values, the backing Values of the Matrix,
	 *  possibly shared with other Matrices.
	 * @param dimSizes the Sizes of the Dimensions
	 */
	protected VectorFloat(final int[] dimSizes_, final float[] values_) {
		this.dimSizes = dimSizes_;
		int Factor, i = dimSizes.length;
		dimFactors = new int[i];
		Factor = 1; //last Index has smallest Factor
		while (--i >= 0) {
			dimFactors[i] = Factor;
			Factor *= dimSizes[i];
		}
		if (null != values_) {
			items = values_;
		} else {
			items = new float[Factor];
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// statistical Measures
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Determines whether the Items in this Container are strictly ascending, descending, or neither.
	 * @return the Order of the Items in this Container
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	public byte getOrder() {
		int ret = HunterFloat.GET_ORDER(items, 0, itemCount); 
		if (ret <  IOrdered.ORDER_DESC)
			return IOrdered.ORDER_NONE; 
		return (byte) ret; }

	/** Sums up all Items of this Vector.
	 * @return the Sum of the Values in this Distribution 	 */
	public double getSum() { return SUM(items, 0, itemCount); }

	/** Calculates the Average of all Items of this Vector.
	 * @return the Mean of this Distribution (average Value)	 */
	public double getMean() { return getSum()/itemCount; }
	
	/** @return the Mode of this Distribution 
	 * (most frequent Value, although you should rather use Binning for this) 
	 */
	//public double getMode() { return 0; }
	
	/**Side Effect: partially sorts the List 
	 * @return the Median of this Distribution ('middle' Value)	 */
	public double getMedian() {
		return HunterFloat.GET_MEDIAN_FAST(items); }
	
	/** Calculates the 1st Moment (mean absolute Deviation) of this Distribution about the given Mean.
	 * @return the Variance of this Distribution (most frequent Value)	 */
	public double getAbsDeviation(final double mean) {
		return MOMENT(items, 1, 0, itemCount, mean); }

	/** Calculates the 2nd Moment (Variance) of this Distribution about the given Mean.
	 * @return the Variance of this Distribution (most frequent Value)	 */
	public double getVariance(final double mean) {
		return MOMENT(items, 2, 0, itemCount, mean); }

	/** Calculates the Covariance between this and another same-length Distribution given their Means.
	 * @return the Variance of this Distribution (most frequent Value)	 */
	public double getCoVariance(final double mean, final VectorFloat arg, final double argMean) {
		if (this.itemCount != arg.itemCount) {
			throw new RuntimeException("For calculating the CoVariance, the Dimensions must match: "+itemCount+" == "+arg.itemCount); }
		double cov = 0;
		for (int i = itemCount; --i >= 0;) {
			cov +=(items[i]-mean)*(arg.items[i]-argMean); }
		return cov/(itemCount-1); }
	
	/** Calculates the Variance of this Distribution about its own Mean.
	 * @return the Variance of this Distribution (most frequent Value)	 */
	public double getVariance() { return getVariance(getMean()); }

	/** Calculates the Standard Deviation of this Distribution about its own Mean.
	 * @return the Standard Deviation of this Distribution (most frequent Value)	 */
	public double getStdDev() { return getStdDev(getMean()); }

	/** Calculates the Standard Deviation of this Distribution about the given Mean.
	 * @return the Standard Deviation of this Distribution (most frequent Value)	 */
	public double getStdDev(final double mean) {
		return Math.sqrt(getVariance(mean)); }

	/** Calculates the 3rd Moment (Skewness) of this Distribution about the given Mean.
	 * @return the SkewNess of this Distribution (most frequent Value)	 */
	public double getSkewNess(final double mean) {
		return MOMENT(items, 3, 0, itemCount, mean); }

	/** Calculates the Skewness of this Distribution about its own Mean.
	 * @return the SkewNess of this Distribution (most frequent Value)	 */
	public double getSkewNess() { return getSkewNess(getMean()); }

	/** Calculates the Curtosis of this Distribution about its own Mean.
	 * @return the Curtosis of this Distribution (most frequent Value)	 */
	public double getCurtosis() { return getCurtosis(getMean()); }

	/** Calculates the 4th Moment (Curtosis) of this Distribution about the given Mean.
	 * @return the Curtosis of this Distribution (most frequent Value)	 */
	public double getCurtosis(final double mean) {
		return MOMENT(items, 4, 0, itemCount, mean); }

	/** Calculates the given Moment of this Distribution about the given Mean.
	 * @return the denoted Moment of this Distribution 	 */
	public double getMoment(final int moment, final double mean) {
		return MOMENT(items, moment, 0, itemCount, mean); }
	
	/**
	 * Error-tolerant Lookup returning 0 for a null Array or an out-of-Bounds Index.
	 * @param i the Index to evaluate
	 * @param arg the Array to read
	 * @return the Value at the given Index, 0 otherwise.
	 */
	public static float GET_AT(final int i, final float[] arg) {
		if (arg == null) 
			return 0; 
		if (arg.length <= i) 
			return 0; 
		return arg[i]; 
	}
		
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a transposed View sharing this Vector's backing Array, by permuting the Index Factors.
	 * @return a VectorFloat with IndexFactors such
	 *  that the Elements are transposed.
	 */
	public VectorFloat getTranspose() {
		if (dimFactors.length != 2) {
			throw new InvalidParameterException("For float[]s please determine the Dimensions to transpose!");
		}
		int[] Factors = new int[2];
		Factors[0] = dimFactors[1]; //Just permuting the Factors is sufficient!
		Factors[1] = dimFactors[0]; //also for float[]s of higher Degrees!
		return new VectorFloat(items, Factors);
	}

	/**
	 * Reads a row of Values (e.g. the Points of a single Plane) 
	 * from the current ResultSet
	 * @param rs the ResultSet to read from
	 * @param columnOffset the Column to start reading consecutively from 
	 * @return the row read into this .
	 */
	final public VectorFloat read(final ResultSet rs, final int columnOffset)
		throws SQLException {
		return read(rs, columnOffset, -1); 
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
	final public VectorFloat read(final ResultSet rs, int columnOffset, int lastCol)
		throws SQLException {
		if (!rs.next()) 
			return null;
		if (lastCol < 0) 
			lastCol = rs.getMetaData().getColumnCount(); //only known AFTER next()!
		--columnOffset; this.itemCount = 0; //start in the correct Order
		while (++columnOffset <= lastCol) 
			try { //try to read as many Coordinates as possible! 
				addItem(rs.getDouble(columnOffset));
			} catch (final SQLException x) { //Resize the Array to exactly fit the Result.
				L.n().l(x); //skip any Comments etc. 
				return this; 
			}
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests that converting a random Vector to Polar and back to Rectangular Coordinates reproduces the original. */
	public static void testRectPolar() {
		L.n("Tests the Conversion between Rectangular and Polar Coordinates and back.");
		int dim = 4;
		int iter = 10;
		float[] vec = new float[dim];
		float[] copy = new float[dim];
		while (--iter >= 0) {
			RANDOMIZE_AT(vec); 
			System.arraycopy(vec, 0, copy, 0, dim); //create Copy for later Comparison
			L.n("Original: ");
			//AStreamOut.ArrayToStream(System.out, vec, ",");
			//L.n("\ntwice transformed: ");
			POLAR_2_RECT_AT(RECT_2_POLAR_AT(vec));
			//AStreamOut.ArrayToStream(System.out, vec, ",");
			//L.n();
			Assert.EQUALS(copy, vec);
		}
	}

	/** Tests the static Ranking Method of this Class	 */
	public static void testRank() throws java.io.IOException {
		System.out.println("Testing Scrambling and Ranking:");
		float[] flt = new float[10];
		int i = flt.length;
		while (--i >= 0) { //Linear Distribution
			flt[i] = ((float) i) / flt.length;
		}
		System.out.println("Original Vector: ");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, flt, ", ");
		HunterFloat.SCRAMBLE_AT(flt); //Now: scramble the Set to avoid sorted Effects.
		System.out.println("\nVector after Scrambling: ");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, flt, ", ");
		//Testing Min, Max, which don't modify the Array...
		final int[] maxPair = new int[2];
		MAX_2_POS(flt, maxPair);
		System.out.println("\nThe Indices of the two maximum Values: " + maxPair[0] + " and " + maxPair[1]);
		MIN_2_POS(flt, maxPair);
		System.out.println("\nThe Indices of the two minimum Values: " + maxPair[0] + " and " + maxPair[1]);
		MIN_2_MAX_2_POS(flt, maxPair);
		System.out.println("\nThe Indices of the Minimum and Maximum Values: " + maxPair[0] + " and " + maxPair[1]);
		final int[] maxQuad = new int[4];
		MIN_2_MAX_2_POS(flt, maxQuad);
		System.out.println(
			"\nThe Indices of the Minimum and Maximum Values: "
				+ maxQuad[0]
				+ " , "
				+ maxQuad[1]
				+ " and "
				+ maxQuad[2]
				+ " , "
				+ maxQuad[3]);
		//Testing Statistic, which modifies the Array...
		i = 5;
		int pos = HunterFloat.GET_STATISTIC_POS(flt, i);
		System.out.println("\n" + i + "th Element is:" + pos + " with Value " + flt[pos]);
		System.out.println("\nThe Array should have been partially sorted around the returned Index:");
		streamIO.AStreamOut.ARRAY_TO_STREAM(System.out, flt, ", ");
		i = 2;
		pos = HunterFloat.GET_STATISTIC_POS(flt, i, 0, 5);
		System.out.println(i + "th Element is:" + pos + " with Value " + flt[pos]);
		System.out.println("\nVector's i-th Statistic: ");
		i = flt.length + 1;
		while (--i > 0) { //Linear Distribution
			pos = HunterFloat.GET_STATISTIC_POS(flt, i, 0, i - 1);
			System.out.println(i + "th Element is:" + pos + " with Value " + flt[pos]);
		}
	}

	/** 
	 * Result: not significantly faster! 
	 */
	private static final void testCopying() {
		L.n("Testing whether the clone() Operation is significantly faster); "); 
		L.n("than the COPY which uses System.arrayCopy()."); 
		final float[] orig = new float[200000];
		long time; 
		time = System.currentTimeMillis(); 
		for (int i = 100; --i >= 0; ) {
			float[] copy = (float[]) orig.clone(); 
			copy[1] = 0; 
		} 
		L.n("Time for using clone: "+(System.currentTimeMillis()-time));
		time = System.currentTimeMillis(); 
		for (int i = 100; --i >= 0; ) {
			float[] copy = COPY(orig); 
			copy[1] = 0; 
		} 
		L.n("Time for using COPY: "+(System.currentTimeMillis()-time));
	}

	private static void testMultiTernaryOp() {
		for (int i = 200; --i >= 0;) { 
			testTernaryOp(); 
			L.n();
		} 
	}
		
	private static void testTernaryOp() {
		final float[] a0 = RANDOM(20); 
		final float[] a1 = RANDOM(20); 
		final float[] a2 = RANDOM(20); 
		final float[] a3 = RANDOM(20); 
		testTernaryOp(a0, a1, a2, a3, QuaternaryOp.AddOp, ADD (a0, a1));
		testTernaryOp(a0, a1, a2, a3, QuaternaryOp.SubOp, SUB(a0, a1));
		testTernaryOp(a0, a1, a2, a3, QuaternaryOp.MulOp, MUL (a0, a1));
		testTernaryOp(a0, a1, a2, a3, QuaternaryOp.DivOp, DIV (a0, a1));
		testTernaryOp(a0, a1, a2, a3, QuaternaryOp.AddProdOp, addProd(a0, a1, a2));
		testTernaryOp(a0, a1, a2, a3, QuaternaryOp.LinOp, Lin(a0, a1, a2));
		testTernaryOp(a0, a1, a2, a3, QuaternaryOp.BiLinOp, BiLin(a0, a1, a2, a3));
	}

	private static void testTernaryOp(final float[] a0, final float[] a1, final float[] a2, final float[] a3
	, final QuaternaryOp op, final float[] result) {
		final int max = Math.max(a0.length, Math.max(a1.length, Math.max(a2.length, a3.length))); 
		for (int i = max; --i >= 0; ) {
			final double expected = op.op(GET_AT(i, a0), GET_AT(i, a1), GET_AT(i, a2), GET_AT(i, a3));
			final double actual = GET_AT(i, result);  
			if (Double.isNaN(expected) || Double.isInfinite(expected)) {
				if (Double.isNaN(actual) || Double.isInfinite(actual)) {
					continue; } 
			}
			Assert.EQUALS(expected, actual);				
		}
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + VectorFloat.class.getName());
		testRectPolar();
		testRank();
		float[] flt = new float[10];
		RANDOMIZE_AT_1_1(flt);
		ADD_AT(flt, 1);
		LOG_AT(flt);
		NEG_AT(flt);
		testMultiTernaryOp(); 
		testCopying(); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws java.io.IOException {
		testIt(args);
	}

}

/** Iterator for the VectorFloat Class (in reverse Order)
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T13:49:26Z
 * digest: 3bd91f1d650732c36a74487880786585df21ddb2b9cec3d08f658b25d6276e07
 * stale: false
 * tags: [code/functional_interfaces]
 * concepts: [Reverse-Order Float Stream Source]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
final class VectorFloatStreamIn
extends AVectorStreamIn_Float {

	/** The VectorFloat wrapped by this Iterator. */
	final public VectorFloat vector;

	/** Constructs a reverse-order Stream over the given VectorFloat, starting from its current Item Count. */
	public VectorFloatStreamIn(final VectorFloat vector_) {
		this.vector = vector_;
		pos = vector.getInt();
	}

	/** @see Stream.Float.IStreamIn_Float#nextDouble()	 */
	protected double nextDoubleInternal() { return vector.items[--pos]; }

	/** Delegates to the wrapped Vector's Minimum Value.
	 * @see Stream.Float.IStreamIn_Bound_Int#getMinValue()	 */
	public double getMinDouble() { return vector.MinVal(); }

    /** Delegates to the wrapped Vector's current Item Count.
     * @see streamIO.real.AStreamIn_Float#getMaxMarkSize()     */
    public long getMaxMarkSize() { return vector.getInt(); }
    
}
