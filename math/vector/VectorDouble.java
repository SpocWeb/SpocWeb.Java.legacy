package math.vector;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import math.NumberFormatter;
import streamIO.Assert;
import streamIO.IOrdered;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.integer.jdbc.AResultSet;
import streamIO.real.IStreamIn_Float;
import function.ICountAble;
import function.IFloatFunction;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefInt;
import function.derive.IFloatDeriveAble;
import function.vector.IFloatScalarField;
import function.vector.IFloatVectorField;

/**
 * Provides static Methods and a dynamic Array Type for Vectors and Arrays of primitive double Numbers.
 * Title: VectorDouble<p>
 * Description:
 * Defines only static Methods to treat Vectors and Arrays with double Numbers.
 * @see streamIO.Copy.IGroup.IRing.IMetric.Body.Vector.VectorDbl
 * @see math.vector.HunterDouble for Methods on Sorting, Searching and Rank Statistics like Median and Percentiles
 *
 * Double-Properties:
 * Bits:     64 = 8*8Byte
 * Mantissa: 52 = 8*6Byte + 4 Bit  => 53 Bits ^ 16 Digits Accuracy
 * Exponent: 11 = 8*1Byte + 3 Bit  => 11 Bits ^ +/- 307 Exponent
 * Sign:      1 =           1 Bit
 * abs.Range: 4.9e-324 to 1.7976931348623157e+308
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
 * mtime: 2026-09-05T16:01:01Z
 * digest: df8971a8441e0f41ab04f313b531254a0e3a935547b778146219e28762238b8e
 * stale: false
 * tags: [code/growable_array, code/array_math]
 * concepts: [Growable double[] Vector]
 * facets: {layer: domain, status: broken, complexity: high}
 * -->
 */
public class VectorDouble 
extends AVector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(VectorDouble.class, 0);

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Builds the Coefficients of the linear Factor (1*x-zeroPos) as a Polynom.
	 * @param zeroPos
	 * @return the LinearFactor (1*x-zeroPos) as a Polynom
	 */
	final static public double[] GET_LF(final double zeroPos) {
		return new double[] {zeroPos, 1}; }

	/**
	 * Builds the Coefficients of the quadratic Factor for a Pair of complex-conjugate Zeros as a Polynom.
	 * @param real
	 * @param imag
	 * @return the LinearFactor (x-real-i*imag)*(x-real+i*imag)=x�-2*real*x+real�+imag� as a Polynom
	 */
	final static public double[] GET_LF(final double real, final double imag) {
		return new double[] {real*real+imag*imag, real+real, 1}; }

	//////////////////////
	//	Optimizations	//
	//////////////////////
	
	/** Returns /=2 in Place	 */
	public VectorDouble halfAt() { mulAt(IMeasurAble.HALF); return this; }
	
	/** Returns /=3 in Place	 */
	public VectorDouble	thirdAt() { mulAt(IMeasurAble.THIRD); return this; }
	
	/** Returns /=4 in Place	 */
	public VectorDouble	quarterAt() { mulAt(IMeasurAble.QUARTER); return this; }
	
	/** Returns *=2 in Place	 */
	public VectorDouble	dblAt() { addAt(this); return this; }
	
	/** Returns *=3 in Place	 */
	public VectorDouble	trplAt() { mulAt(ICountAble.THREE); return this; }

	/** Returns *=4 in Place	 */
	public VectorDouble	quadAt() { mulAt(ICountAble.FOUR); return this; }

	/** Returns *=n in Place	 */
	public VectorDouble	mulAt(final int n) { mulAt(items, n); return this; }

	/** Returns *=2^n in Place	 */
	public VectorDouble  mul2PowAt(final int n) {
		mulAt(n >= 0 ? 1 << n : 1.0/(1 << -n)); return this; 
	}
	
	/** less: '<'
	  * @return  True, when 'Self' < arg	*/
	public boolean less (final VectorDouble arg) {
//		int largeGrad;
		int smallGrad = arg.itemCount;
		if (smallGrad > itemCount) { //P1 is larger,
//			largeGrad = smallGrad;
			smallGrad = itemCount; }
		int i =    itemCount +1; while (--i > smallGrad) if (   items[i] >= 0) return false; ++i;
		int j = arg.itemCount +1; while (--j > smallGrad) if (arg.items[j] <= 0) return false;
		while (--i >= 0) { 
			if (items[i] >= arg.items[i]) return false; } 
		return true; }

	/**grtr: '<' Returns True, when 'Self' > arg	*/
	public boolean grtr (final VectorDouble arg) {
//		int largeGrad;
		int smallGrad = arg.itemCount;
		if (smallGrad > itemCount) { //P1 is larger,
//			largeGrad = smallGrad;
			smallGrad = itemCount; }
		int i =    itemCount +1; while (--i > smallGrad) if (   items[i] <= 0) return false; ++i;
		int j = arg.itemCount +1; while (--j > smallGrad) if (arg.items[j] >= 0) return false;
		while (--i >= 0) if (items[i] <= arg.items[i]) return false;
		return true; }

	/**Returns the Minimum of this and the Operand in Place	 */
	public VectorDouble MaxAt (final VectorDouble P1) {
		if (P1 == null) 
			return this; 
		int largeGrad =     itemCount;
		int smallGrad =  P1.itemCount;
		if (smallGrad >     itemCount) { //P1 is larger,
			largeGrad = smallGrad;
			smallGrad =     itemCount;
			setSize(largeGrad); } 	//ReDim, extend the Array and initialize it to Zero!
		int i = largeGrad +1;
		while (--i >= 0)
			if (items[i] < P1.items[i]) {
				items[i] = P1.items[i]; } 
		canonicalizeAt();
		return this; }

	/**Returns the Minimum of this and the Operand in Place	 */
	public VectorDouble MinAt (final VectorDouble P1) {
		if (P1 == null) 
			return this; 
		int largeGrad =     itemCount;
		int smallGrad =  P1.itemCount;
		if (smallGrad >     itemCount) { //P1 is larger,
			largeGrad = smallGrad;
			smallGrad =     itemCount;
			setSize(largeGrad); } 	//ReDim, extend the Array and initialize it to Zero!
		for (int i = largeGrad +1; --i >= 0; ) {
			if (items[i] > P1.items[i]) {
				items[i] = P1.items[i]; }
		}
		canonicalizeAt();
		return this; }

	/**Sets and returns the maximum Value for this Class in Place.	 */
	public VectorDouble maxValueAt() {
		this.copyAt(Double.MAX_VALUE);
		return this; }

	/**Sets and returns the minimum Value for this Class in Place.
	 * Usually for symmetric Types this is about the negative maxValue.	 */
	public VectorDouble minValueAt() {
		this.copyAt(-Double.MAX_VALUE); 
		return this; }

	/**Returns the Representation of -Infinity for this Class in Place.	 */
	public VectorDouble NegInfinityAt() {
		this.copyAt(Double.NEGATIVE_INFINITY); 
		return this; }

	/**Returns true when the Value of this Object is Infinity.	 */
	public boolean  isInfinite() {
		for (int j = itemCount; --j >= 0; ) {
			if (! Double.isInfinite(items[j])) {
				return false; } } 
		return true; }

	/**Returns true when the Value is equal to this Object.	 */
	public boolean equals(final double arg) {
		for (int j = itemCount; --j >= 0; ) {
			if (!ByRefDouble.EQUALS(items[j], arg)) {
				return false; } } 
		return true; }

	/** less: '<'
	  * @return  True, when 'Self' < arg	*/
	public boolean less(double arg)	{
		for (int i = itemCount; --i >= 0; ) {
			if (items[i] >= arg) {
				return false; } } 
		return true; }

	/** less or equal: '<'
	  * @return  True, when 'Self' <= arg	*/
	public boolean lessEq(double arg) {
		for (int i = itemCount; --i >= 0; ) {
			if (items[i] > arg) {
				return false; }
		} 
		return true; }

	/** greater: '>'
	  * @return  True, when 'Self' > arg	*/
	public boolean grtr(double arg)	{
		for (int i = itemCount; --i >= 0; ) {
			if (items[i] <= arg) { 
				return false; }
		} 
		return true; }

	/** greater or equal: '>='
	  * @return  True, when 'Self' >= arg	*/
	public boolean grtrEq(double arg)	{
		for (int i = itemCount; --i >= 0; ) {
			if (items[i] < arg) {
				return false; } 
		}
		return true; }

	/** Raises this Vector in Place so no Item is below arg.
	 * @return the Maximum in Place: 	*/
	public VectorDouble MaxAt(double arg) {
		for (int i = itemCount; --i >= 0; ) {
			if (items[i] < arg) {
				items[i] = arg; } 
		}
		return this; }

	/** Caps this Vector in Place so no Item exceeds arg.
	 * @return the Minimum in Place: 	*/
	public VectorDouble MinAt (double arg) {
		for (int i = itemCount; --i >= 0; ) {
			if (items[i] > arg) {
				items[i] = arg; } 
		}
		return this; }

	/** Returns a new Vector holding the per-Item Minimum of this Vector and arg.
	 * @return the Minimum: 	*/
	public VectorDouble Min(double arg) {
		return new VectorDouble(MIN(items, itemCount, arg)); }

	/** Returns a new Vector holding the per-Item Maximum of this Vector and arg.
	 * @return the Maximum: 	*/
	public VectorDouble Max(final double arg) {
		return new VectorDouble(MAX(items, itemCount, arg)); }
	
	/// Vector Operations
	
	/** Computes the per-Item Minimum of arr[i] and arg into a fresh Array.
	 * @return the Minimum Values of arr[i] and arg 	*/
	final static public double[] MIN(final double[] arr, final int length, final double arg) {
		return MIN(null, arr, length, arg); }

	/** Computes the per-Item Minimum of arr[i] and arg into a fresh Array sized to arr.
	 * @return the Minimum Values of arr[i] and arg 	*/
	final static public double[] MIN(final double[] arr, final double arg) {
		return MIN(null, arr, arr.length, arg); }

	/** Computes the per-Item Minimum of arr[i] and arg, reusing ret when large enough.
	 * @return the Minimum Values of arr[i] and arg 	*/
	final static public double[] MIN(double[] ret
			, final double[] arr, final int length, final double arg) {
		if ((ret == null) || 
			(ret.length < length))
			 ret = new double[length];
		for (int i = length; --i >= 0; ) {
			if (arr[i] < arg) {
				ret[i] = arr[i];
			}else{
				ret[i] = arg;
			}
		}
		return ret;
	}
	
	/** Computes the per-Item Maximum of arr[i] and arg into a fresh Array.
	 * @return the Maximum Values of arr[i] and arg 	*/
	final static public double[] MAX(final double[] arr, final int length, final double arg) {
		return MAX(null, arr, length, arg); }

	/** Computes the per-Item Maximum of arr[i] and arg into a fresh Array sized to arr.
	 * @return the Maximum Values of arr[i] and arg 	*/
	final static public double[] MAX(final double[] arr, final double arg) {
		return MAX(null, arr, arr.length, arg); }

	/** Computes the per-Item Maximum of arr[i] and arg, reusing ret when large enough.
	 * @return the Maximum Values of arr[i] and arg 	*/
	final static public double[] MAX(double[] ret
			, final double[] arr, final int length, final double arg) {
		if ((ret == null) || 
			(ret.length < length))
			 ret = new double[length];
		for (int i = length; --i >= 0; ) {
			if (arr[i] > arg) {
				ret[i] = arr[i];
			}else{
				ret[i] = arg;
			}
		}
		return ret;
	}
	
	/**Returns the minimum absolute Value (greater than Zero) for this Class in Place.	 */
	public VectorDouble minAbsValueAt() {
		this.copyAt(Double.MIN_VALUE);
		return this; }

	/**Returns the Representation of Infinity for this Class in Place.
	 * The resulting Complex Infinity is projective (not affine),
	 * it has indefinite Length (1/0) and no phase! (0/0)	 */
	public VectorDouble InfinityAt() {
		this.copyAt(Double.POSITIVE_INFINITY);
		return this; }

	/**Returns the Representation of an invalid Number for this Class in Place.	 */
	public VectorDouble NaNAt() {
		this.copyAt(Double.NaN);
		return this; }
	
	/**
	 * The Entropy is between 
	 * 0 when all Values are the same and 
	 * ln(N) when all Values lie equidistant. 
	 * The Vector has to be sorted (ascending or descending) 
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
	static final public double ENTROPY(final double[] x) { return ENTROPY(x, x.length, 0); }
	
	/**
	 * The Entropy is between 
	 * 0 when all Values are the same and 
	 * ln(N) when all Values lie equidistant. 
	 * @see VectorInt#ENTROPY(int[]) for the Entropy of binned Data (loss of Information though!)
	 * @param x the Vector has to be sorted (ascending or descending) 
	 * @return the Entropy of the given continuous Vector of Measurements. 
	 */
	static final public double ENTROPY(final double[] x, final int stop) {
		return ENTROPY(x, stop, 0); }
	
	/**
	 * The Entropy is between 
	 *    0  minimum when all Values are the same and 
	 * ln(N) maximum when all Values lie equidistant. 
	 * @see VectorInt#ENTROPY(int[]) for the Entropy of binned Data (loss of Information though!)
	 * @param x the Vector has to be sorted (ascending or descending) 
	 * @return the Entropy of the given continuous Vector of Measurements. 
	 */
	static final public double ENTROPY(final double[] x, final int stop, final int start) {
		double entropy = 0; 
		final double norm = x[stop-1] - x [start]; //makes it immutable against affine Trafos. 
		for(int i = stop; --i > start; ) {
			final double dx = (x[i] - x[i-1])/norm; //<= 1
			if (dx > 0)
				entropy += Math.log(dx)*dx; //max. Value: log(N)/N
		} //heuristic Formula; strange that multiply with dx instead of dividing by it! 
		return entropy; 
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Matrix Trafos: extracting a Column
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Extracts one Column of a Matrix into a new Vector.
	 * @return the Column at the given Position */
	final static public double[] COLUMN(final double[][] matrix, final int col) {
		double[] ret = new double[matrix.length];
		for (int i = ret.length; --i >= 0;) {
			ret[i] = matrix[i][col];
		}
		return ret;
	}

	/** Transposes a rectangular Matrix by extracting each Column via {@link #COLUMN(double[][], int)}.
	 * @return the transposed Matrix */
	final static public double[][] TRANSPOSE(final double[][] matrix) {
		double[][] ret = new double[matrix[0].length][];
		for (int i = ret.length; --i >= 0;) {
			ret[i] = COLUMN(matrix, i);
		}
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods : Reading
	///////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * skips the stream until it encounters the end of the Line
	 * @param st the Tokenizer; @see StreamTokenizer#eolIsSignificant(boolean) must be set!
	 * @throws IOException on any Error
	 */
	final static public void skipLine(final StreamTokenizer st) throws IOException {
		skipUntilToken(st, StreamTokenizer.TT_EOL);
	}

	/**
	 * skips the stream until it encounters the given Token
	 * @param st the Tokenizer
	 * @throws IOException on any Error
	 */
	final static public void skipUntilToken(final StreamTokenizer st, final int token) throws IOException {
		while (token != st.nextToken());
	}

	/**
	 * reads a Vector from the given Tokenizer
	 * @param st StreamTokenizer to use 
	 * @param n Number of Values to read
	 * @return a new Array of Size n 
	 * @throws IOException on any Error
	 */
	final static public double[] readVector
	( final StreamTokenizer st, final int n) throws IOException {
		return readVector(st, 0, n);
	}

	/**
	 * reads a Vector from the given Tokenizer
	 * @param st StreamTokenizer to use 
	 * @param nStart Start Index
	 * @param nEnd End Index 
	 * @return a new Array of Size nEnd 
	 * @throws IOException on any Error
	 */
	final static public double[] readVector
	( final StreamTokenizer st, final int nStart, final int nEnd
	) throws IOException {
		return readVector(st, nStart, nEnd, new double[nEnd]);
	}

	/**
	 * reads a Vector from the given Tokenizer
	 * @param st StreamTokenizer to use 
	 * @param nStart Start Index
	 * @param nEnd End Index 
	 * @param ret Array to fill
	 * @return ret
	 * @throws IOException on any Error
	 */
	final static public double[] readVector(final StreamTokenizer st, final int nStart, final int nEnd, final double[] ret) throws IOException {
		for (int i = nStart-1; i < nEnd;) {
			switch (st.nextToken()) {
				case StreamTokenizer.TT_EOF: //fall through...
				case StreamTokenizer.TT_EOL:    return ret;
				case StreamTokenizer.TT_NUMBER: ret[++i] = st.nval; 
				case StreamTokenizer.TT_WORD:   break;
				default :
					//throw new RuntimeException("unexpected Token:"+st.ttype);
			} 
		}
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods : Writing
	///////////////////////////////////////////////////////////////////////////////////

	/** The Default Separator Character to use for the STREAM Methods */
	final static public char DEFAULT_SEPARATOR = '\t';

	/** Default Stream if none is given... */
	public static PrintStream DEFAULT_STREAM = System.out;

	/** Streams out the complete given Array. 
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 * @param separator the Separator Character
	 */
	final static public void STREAM(final double[] vals, final PrintStream stream, final char separator) {
		STREAM(vals, stream, 0, vals.length, separator);
	}

	/** Streams out the complete given Array. 
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 * @param separator the Separator Character
	 */
	final static public void STREAM(final double[] vals, final PrintStream stream, final char separator, final int offset) {
		STREAM(vals, stream, 0, vals.length, separator, offset);
	}

	/** Streams out the complete given Array. 
	 * defaults the separator the Default Separator Character
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 */
	final static public void STREAM(final double[] vals, final PrintStream stream) {
		STREAM(vals, stream, 0, vals.length, DEFAULT_SEPARATOR);
	}
	
	/** Streams out the complete given Array. 
	 * defaults the separator the Default Separator Character
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 */
	final static public void STREAM(final double[] vals, final PrintStream stream, final int offset) {
		STREAM(vals, stream, 0, vals.length, DEFAULT_SEPARATOR, offset);
	}
	
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(final double[] vals, final PrintStream stream, final int startCol, final int stopCol, final char separator) {
		STREAM(vals, stream, startCol, stopCol, separator, 0);
	}
	
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(final double[] vals, final PrintStream stream, final int startCol, final int stopCol, final char separator, final int offset) {
		if (startCol >= stopCol) {
			return;
		}
		stream.print(vals[startCol]); //omit the last Separator...
		for (int i = startCol; ++i < stopCol;) {
			stream.print(separator);
			stream.print(vals[i]+offset);
		}
	}

	/** Streams out the complete given Array. 
	 * 
	 * @param vals the Array to stream
	 * @param stream the Stream to stream to
	 */
	final static public void STREAM(final double[] vals) {
		STREAM(vals, DEFAULT_STREAM, 0, vals.length);
	}
	
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(final double[] vals, final PrintStream stream, final int startCol, int stopCol) {
		if (startCol >= stopCol) 
			return;
		stream.print(vals[startCol]);
		for (int i = startCol; ++i < stopCol;) {
			stream.write(DEFAULT_SEPARATOR);
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
	final static public void STREAM(final double[] d, final Writer pw, final NumberFormatter formatter, final String strSep) throws IOException {
		for (int i = -1; ++i < d.length;) {
			formatter.stream(pw, d[i]);
			pw.write(strSep); 
		}
		//return pw; 
	}

	/** streams the Numbers of the given Array out to the Stream using the given Formatter
	 * 
	 * @param d Array to stream out
	 * @param strSep Separator String between Numbers 
	 * @param pw PrintWriter to stream to
	 * @param formatter Number Formatter to use 
	 */
	final static public void STREAM(final double[] d, final OutputStream ps, final NumberFormatter formatter, final String strSep) throws IOException {
		final Writer pw = new OutputStreamWriter(ps);
		STREAM(d, pw, formatter, strSep);
		pw.flush(); 
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
	public static double[] NORMAL(
		final double[] diff1,
		final double[] diff2,
		final double[] p0,
		final double[] p1,
		final double[] p2, 
		final boolean normalize) {
		SUB(diff1, p1, p0);
		SUB(diff2, p2, p0);
		final double[] prod = MUL_CROSS(diff1, diff2);
		if (normalize) 
			NORMALIZE_AT(prod); 
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
	public static double[] NORMAL(
		final double[] p0,
		final double[] p1,
		final double[] p2, 
		final boolean normalize) {
		return NORMAL(null, null, p0, p1, p2, normalize); 
	}

	/** Cross Product in Place	 */
	final static public double[] MUL_CROSS_AT(final double[] ths, final double[] arg) {
		return COPY(MUL_CROSS(ths, arg), ths);
	}

	/** Cross Product in R^3 */
	final static public double[] MUL_CROSS(final double[] ths, final double[] arg) {
		int end = 0;
		final double[] result = new double[3];
		if (ths.length > 3)
			throw new ArrayIndexOutOfBoundsException();
		if (arg.length > 3)
			throw new AbstractMethodError();
		if (ths.length < 2) {
			result[2] = ths[0] * arg[1];
			return result;
		}
		if (arg.length < 2) {
			result[2] = -arg[0] * ths[1];
			return result;
		}
		if ((ths.length < 3) && (arg.length < 3)) {
			result[0] = 0;
			result[1] = 0;
			end = 2;
		}
		for (int i = 3; --i >= end; ) {
			final int j = (i == 2) ? 0 : i + 1;
			final int k = 3 - i - j;
			result[i] = (ths[j] * arg[k]) - (ths[k] * arg[j]);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Database Operations:
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public double[] readVector
	( final ResultSet RS, double[] point, final int[] cols
	) throws SQLException {
		if (!RS.next()) 
			return null;
		int len = (cols != null) ? cols.length : ((AResultSet) RS).getNumCols();
		if (point == null) 
			point = new double[len];
		int i = -1; //start in the correct Order
		try { //try to read as many Coordinates as possible!
			while (++i < len) {
				if (cols == null) {
					point[i] = RS.getDouble(i);
				} else {
					point[i] = RS.getDouble(cols[i]);
				}
			}
		} catch (Exception x) { //Resize the Array.
			double[] tmp = new double[i];
			System.arraycopy(point, 0, tmp, 0, i);
			point = tmp;
		}
		return point;
	}

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public double[] readVector(java.sql.ResultSet RS, double[] point) throws java.sql.SQLException {
		return readVector(RS, point, null);
	}

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public double[] readVector(java.sql.ResultSet RS) throws java.sql.SQLException {
		return readVector(RS, null, null);
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Conversion between Polar and Rectangular Coordinates
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the polar Representation of a Vector with arbitrary Dimensions
	 * to it's rectangular Representation.
	 * By Convention the Length is the first Coordinate.
	 * By setting dim to lower Values than the maximum Dimension
	 * mixed Coordinate Systems can be calculated like Cylinder Coordinates.
	 */
	final static public double[] Polar2RectAt(double[] ret) {
		return Polar2RectAt(ret, ret.length);
	}

	/**
	 * Converts the polar Representation of a Vector with arbitrary Dimensions
	 * up to the given Dimension to it's rectangular Representation.
	 * By Convention the Length is the first Coordinate.
	 * By setting dim to lower Values than the maximum Dimension
	 * mixed Coordinate Systems can be calculated like Cylinder Coordinates.
	 */
	final static public double[] Polar2RectAt(double[] ret, int dim) {
		double tmp, length = ret[0];
		while (--dim > 0) {
			ret[dim] = length * (tmp = Math.sin(ret[dim]));
			length *= Math.sqrt(1 - tmp * tmp);
		} //cos()
		if (Math.abs(ret[1]) < IMeasurAble.PI_HALF) {
			ret[0] = length;
		} else {
			ret[0] = -length;
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
	final static public double[] Rect2PolarAt(double[] ret) {
		return Rect2PolarAt(ret, ret.length);
	}

	/**
	 * Converts the rectangular Representation of a Vector
	 * to it's polar Representation.
	 *
	 * By Convention the Length is the first Coordinate.
	 * By setting dim to lower Values than the maximum Dimension
	 * mixed Coordinate Systems can be calculated like Cylinder Coordinates.
	 */
	final static public double[] Rect2PolarAt(double[] ret, int dim) {
		int i = 0;
		double tmp, length = ret[0]; //--dim];
		double sqrLength = length * length;
		while (++i < dim) {
			ret[i] = Math.atan((tmp = ret[i]) / length);
			sqrLength += tmp * tmp;
			length = Math.sqrt(sqrLength);
		}
		if (ret[0] < 0) {
			ret[1] += Math.PI;
		}
		ret[0] = length;
		return ret;
	}

	/**
	 * Calculates the full euklidean Adjacency Vector for the given Row/Point.
	 * generated from all the Distances between this and the other Points:
	 * ret[i] = {Sum(j), Vectors[Row,j]*Vectors[i,j]}
	 *
	 * Actually this saves Memory,
	 * but cannot exploit the Fact that ret[i][j] = ret[j][i]
	 * and thus doubles the Calculation Effort compared to
	 * @see MatrixDouble.DistMatrix()
	 */
	final static public double[] DISTANCES(final double[] ret, final double[][] vectors, final int rowNum) {
		ret[rowNum] = 0; //not necessary, because new Array contains 0s already!
		final double[] row = vectors[rowNum]; //initialize the whole Matrix, O(V^2)
		for(int j = vectors.length; --j >= 0;) { //symmetric Matrix //calculate only 50%!
			if (j == rowNum) 
				continue; //faster to directly loop on...
			ret[j] = Math.sqrt(DIST_SQR(row, vectors[j])); //Symmetric!
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
	final static public double[] SET_AT(double[] arr, int pos_, double value_) {
		if (pos_ >= arr.length) {
			arr = SET_CAPACITY(pos_ + 1, arr);
		}
		arr[pos_] = value_;
		return arr;
	}

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	final static public double[] SET_SIZE(final double[] arr, final int newExactSize) {
		return RESIZE(arr, newExactSize, arr.length);
	}

	/** Returns a resized (larger) Copy of the given Array */
	final static public double[] SET_CAPACITY(final int newMinSize, final double[] arr) {
		return RESIZE(arr, ENLARGED_CAPACITY(arr.length, DEFAULT_CAPACITY_INCR, newMinSize), arr.length);
	}

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	final static public double[] RESIZE(final double[] arr, final int newSize, int numToRetain) {
		double[] ret = new double[newSize];
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

	/** Compares two Arrays for Element-wise Equality over the given Range.
	 * @see Object#equals(java.lang.Object) 	 */
	final static public boolean EQUALS(final double[] a, final double[] b, final int start, final int stop) {
		for (int i = stop; --i >= start; ) {
			if (!ByRefDouble.EQUALS(a[i], b[i])) 
				return false; 
		}
		return true; 
	}
	
	/** Compares two Arrays of possibly different Length for Equality, treating a missing Array as all-Zero.
	 * @see Object#equals(java.lang.Object) 	 */
	final static public boolean EQUALS(final double[] a, final double[] b) {
		final double cmp = a[0];
		if (a == b) 
			return true; 
		if (a == null) 
			return IS_ZERO(b, cmp); 
		if (b == null) 
			return IS_ZERO(a, cmp); 
		if (a.length > b.length) 
			return EQUALS(a, b, 0, b.length) && IS_ZERO(a, cmp, b.length, a.length);
		else 
			return EQUALS(a, b, 0, a.length) && IS_ZERO(b, cmp, a.length, b.length);
	}
	
	/** Returns a Copy of the given Array */
	final static public double[] COPY(final double[] arr, final int start, final int stop) {
		return COPY(arr, start, stop, null); }
	
	/** Returns a Copy of the given Array */
	final static public double[] COPY(final double[] arr, final int start, final int stop, double[] _ret) {
		if (_ret == null)
			_ret  = new double[stop];
		System.arraycopy(arr, start, _ret, start, stop-start);
		return _ret;
	}

	/** Returns a Copy of the given Array */
	final static public double[] COPY(final double[] arr) {
		return COPY(arr, 0, arr.length); }
	
	/** Returns a Copy of the given Array */
	final static public double[] COPY(final float[] arr) {
		return COPY(arr, null); }

	/** Returns a Copy of the given Array */
	final static public double[] COPY(final float[] arr, double[] ret) {
		if (ret == null) {
			ret = new double[arr.length]; } 
		for(int len = arr.length; --len >= 0; ) 
			ret[len] = arr[len];
		//System.arraycopy(arr, 0, ret, 0, arr.length); //ArrayStoreException!!!
		return ret;
	}

	/** Returns a Copy of the given Array */
	final static public double[] COPY(final int[] arr) {
		return COPY(arr, 0, arr.length, null); }

	/** Returns a Copy of the given Array 
	 * @param start TODO
	 * @param stop TODO*/
	final static public double[] COPY(final int[] arr, final int start, final int stop, double[] ret) {
		if (ret == null) 
			ret = new double[stop]; 
		for(int i = stop; --i >= start; ) 
			ret[i] = arr[i];
		//System.arraycopy(arr, 0, ret, 0, arr.length); //ArrayStoreException!!!
		return ret;
	}
	
	/**Converts the Array IMeasurAble[] to double[]      */
	final static public double[] COPY(final IMeasurAble[] arg) {
		return COPY(arg, arg.length, 0, null); }
	
	/**Converts the Array IMeasurAble[] to double[]      */
	final static public double[] COPY(final IMeasurAble[] arg, final int stop, final int start, double[] ret) {
		if (ret == null)
			ret  = new double [stop];
		for(int i = stop; --i >= start; ) 
			ret[i] = arg[i].getFloat(); 
		return ret; 
	}
	
	/**Converts the Array IMeasurAble[] to double[]	  */
	final static public double[] COPY(final Number[] arg) {
		return COPY(arg, arg.length, 0, null); }
	
	/**Converts the Array IMeasurAble[] to double[]	  */
	final static public double[] COPY(final Number[] arg, final int stop, final int start, double[] ret) {
		if (ret == null)
			ret  = new double [stop];
		for(int i = stop; --i >= start; ) 
			ret[i] = arg[i].floatValue();  
		return ret; }
	
	/**Converts the Array Object[] to float[]	  */
	final static public double[] COPY(final Object[] arg) {
		if (arg instanceof IMeasurAble[])
			return VectorDouble.COPY((IMeasurAble[]) arg);
			return VectorDouble.COPY((Number[]     ) arg); }
	
	/** Returns a Copy of the given Array */
	final static public double[] COPY(double[] _arr, double[] _ret) {
		System.arraycopy(_arr, 0, _ret, 0, _arr.length);
		return _ret;
	}

	/** Returns a Copy of the given Array */
	final static public double[] COPY(double[] this_, float[] arr, int start, int stop) {
		if (this_ == null) {
			this_ = new double[stop]; }
		for (int i = stop; --i >= start;) {
			this_[i] = arr[i]; }
		//System.arraycopy(arr, start, this_, start, stop-start); //Type Mismatch!
		return this_;
	}

	/**
	 * Setting the Vectors to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vectors fit, they are returned unchanged!
	 */
	final static public double[][] SET_DIM_AT(final double[][] a, final int dim) {
		int i = a.length;
		while (--i >= 0) {
			a[i] = SET_DIM_AT(a[i], dim); }
		return a;
	}

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public double[] SET_DIM_AT(final double[] a, final int dim) {
		if (a.length == dim) {
			return a; }
		double[] ret = new double[dim];
		System.arraycopy(a, 0, ret, 0, a.length);
		Arrays.fill(ret, a.length, dim, 0.0);
		return ret;
	}

	/** Zeroes out an entire Array in Place.
	 * @return the given Array with all Elements set to 0. 	 */
	final static public double[] ZERO_AT(double[] ret) {
		return ZERO_AT(ret, 0, ret.length); }

	/** Zeroes out an Element Range of an Array in Place.
	 * @return the given Array with the Elements from Start (inclusive) to Stop (exclusive) set to 0. 	 */
	final static public double[] ZERO_AT(double[] ret, int Start, int Stop) {
		java.util.Arrays.fill(ret, Start, Stop, 0);
		return ret; }

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag.
	 * i.e. a[dim] = 1 and a[j] = 0 otherwise.
	 */
	final static public double[] ONE_AT(double[] a, int dim) {
		return DIAG_AT(a, 1, dim); }

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag,
	 * i.e. a[dim] = diag and a[j] = 0 otherwise.
	 */
	final static public double[] DIAG_AT(double[] a, double diag, int dim) {
		Arrays.fill(a, 0.0);
		a[dim] = diag;
		return a;
	}

	/** Sets an entire Array in Place to all-ones.
	 * @return the given Array with all Elements set to 1. 	 */
	final static public double[] ONE_AT(double[] ret) {
		return ONE_AT(ret, 0, ret.length); }

	/** Sets an Element Range of an Array in Place to 1.
	 * @return the given Array with the Elements from Start (inclusive) to Stop (exclusive) set to 1. 	 */
	final static public double[] ONE_AT(double[] ret, int start, int stop) {
		java.util.Arrays.fill(ret, start, stop, 1);
		return ret;
	}

	/** Fills an entire Array in Place with the given Value.
	 * @return the given Array with all Elements set to the given Value. 	 */
	final static public double[] FILL_AT(double[] ret, double val) {
		return FILL_AT(ret, val, 0, ret.length); }

	/**
	 * Fills an Element Range of an Array in Place with the given Value.
	 * @return the given Array with the Elements from Start (inclusive)
	 * to Stop (exclusive) set to the given Value.
	 */
	final static public double[] FILL_AT(double[] ret, double val, int Start, int Stop) {
		java.util.Arrays.fill(ret, Start, Stop, val);
		return ret;
	}

	/** Tests whether every Element of an Array is Zero.
	 * @return true iif all the Values in the Array are Zero. 	 */
	final static public boolean IS_ZERO(final double[] arr, final double cmp) {
		return IS_ZERO(arr, cmp, 0, arr.length);
	}

	/** Tests whether every Element in the given Range of an Array is Zero.
	 * @return true iif the Values in the Array from Start (inclusive) to Stop (exclusive) are Zero. 	 */
	final static public boolean IS_ZERO(final double[] arr, final double cmp, final int start, int stop) {
		while (--stop >= start) {
			if (!ByRefDouble.IS_ZERO(arr[stop], cmp)) { //> IMeasurAble.DOUBLE_ACCURACY
				return false; }
		}
		return true;
	}

	/**
	 * Checks whether this Vector is a Unity Vector in the given Dimension
	 */
	final static public boolean IS_ONE(final double[] Row, final int dim) { //Assume a square Matrix
		int j = Row.length;
		while (--j >= 0) { //Use an Epsilon here
			if (j == dim) {
				if (Math.abs(Row[j] - 1) > IMeasurAble.DOUBLE_ACCURACY) {
					return false; }
			} else {
				if (Math.abs(Row[j]) > IMeasurAble.DOUBLE_ACCURACY) {
					return false; }
			}
		}
		return true;
	}

	/**
	 * The Order can change in each individual Dimension!
	 * @return true when the middle Vector is between the left and right Vector.
	 */
	final static public boolean BETWEEN(double[] left, double[] mid, double[] right) {
		int i = left.length;
		while (--i >= 0) {
			if ((left[i] < mid[i]) != (right[i] > mid[i])) {
				return false; }
		}
		return true;
	}

	/**
	 * Determines the Minimum and Maximum Value
	 * and sorts them into the first and second Argument.
	 */
	final static public void ORDER_AT(double[] inOutMin, double[] inOutMax) {
		for (int i = inOutMin.length; --i >= 0; ) {
			final double tmp = inOutMin[i];
			if (tmp < inOutMax[i]) {
				continue;
			}
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
	final static public double[] loadStream(IStreamIn_Float stream, double[] arr) {
		int j = -1; //arr.length;
		while (++j < arr.length) { //>= 0) {
			arr[j] = stream.nextDouble();
		}
		return arr;
	}

	/** Creates an equidistant Raster on the Interval [x0, x0+Grad*dx]	 */
	final static public double[] RASTER(double x0, double dx, int N) {
		return RASTER_AT(new double[N], x0, dx); }

	/** Creates an equidistant Raster on the Interval [x0, x0+Grad*dx]	 */
	final static public double[] RASTER_AT(double[] ret, double x0, double dx) {
		if (ret.length == 0) {
			return ret;
		}
		int i = 0;
		ret[0] = x0;
		while (++i <= ret.length) {
			ret[i] = (x0 += dx);
		}
		return ret;
	}

	/**
	 * Generates a Manifold by rastering over the Raster x
	 * Recursively delegates the Dimensions.
	 * @param x the Raster containing a Row for every Dimension to loop over
	 * @return a multidimensional Array (#Dim = Raster.length) containing the Values of f
	 */
	final static public Object[] RASTER(double[][] Raster) { //preserve Internals of x0
		double[] X = new double[Raster.length];
		return RASTER(Raster, X, Raster.length);
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
	protected static final Object[] RASTER(double[][] raster, double[] x, int dim) { //preserve Internals of x0
		int i = raster[--dim].length;
		Object[] ret = new Object[i];
		double[] RasterD = raster[dim];
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
	 * @param Y the Vector built during looping through Raster
	 * Y.length must match the Return Dimension of the f or be null
	 * @return a multidimensional Array (#Dim = Raster.length) containing the Values of f
	 */
	final static public Object[] SAMPLE(
		IFloatVectorField f,
		double[][] Raster,
		double[] Y) { //preserve Internals of x0
		double[] X = new double[Raster.length];
		return SAMPLE(f, Raster, X, Y, Raster.length);
	}

	/**
	 * Generates a Manifold by sampling f over the Raster x
	 * Recursively delegates the Dimensions.
	 * @param f the Vector Field Function
	 * @param x the Raster containing a Row for every Dimension to loop over
	 * @param X the Vector being built containing a sample Value for every Dimension to loop over
	 * @param Y the Vector built during looping through Raster
	 *  Y.length must match the Return Dimension of the f or be null
	 * @param dim the Dimension currently looped over
	 * @return a multidimensional Array (#Dim = Raster.length) containing the Values of f
	 */
	protected static final Object[] SAMPLE(
		IFloatVectorField f,
		double[][] Raster,
		double[] X,
		double[] Y,
		int dim) { //preserve Internals of x0
		int i = Raster[--dim].length;
		Object[] ret = new Object[i];
		double[] RasterD = Raster[dim];
		while (--i >= 0) { //these i make up the MultiIndex to the Raster
			X[dim] = RasterD[i];
			if (dim > 0) { //could also have performed another Recursion,
				ret[i] = SAMPLE(f, Raster, X, Y, dim); //but faster to directly call it here!
			} else {
				ret[i] = f.map(X, Y);
				if (Y != null) { //create a Copy for the next Value
					Y = new double[Y.length];
				}
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
	final static public Object SAMPLE(final IFloatScalarField f, final double[][] Raster) { //preserve Internals of x0
		double[] X = new double[Raster.length];
		return SAMPLE(f, Raster, X, Raster.length);
	}

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
		final double[][] Raster,
		final double[] X,
		int dim) { //preserve Internals of x0
		int i = Raster[--dim].length;
		double[] RasterD = Raster[dim];
		if (dim > 0) { //distinguish here, because primitive return Values
			Object[] ret = new Object[i];
			while (--i >= 0) { //these i make up the MultiIndex to the Raster
				X[dim] = RasterD[i];
				ret[i] = SAMPLE(f, Raster, X, dim); //but faster to directly call it here!
			}
			return ret;
		}
		double[] ret = new double[i];
		while (--i >= 0) { //these i make up the MultiIndex to the Raster
			X[dim] = RasterD[i];
			ret[i] = f.Map(X);
		}
		return ret;
	}

	/** Generates a Manifold by sampling f over x	 */
	final static public double[] SAMPLE(final IFloatFunction f, final double[] x, final int xLen) { //preserve Internals of x0
		return SAMPLE_AT(new double[xLen], f, x); }

	/** Generates a Manifold by sampling f over x	 */
	final static public double[] SAMPLE(final IFloatFunction f, final double[] x) { //preserve Internals of x0
		return SAMPLE(f, x, x.length); }

	/** Generates a Manifold by sampling f over x	 */
	final static public double[] SAMPLE_AT(final double[] ret, final IFloatFunction f, final double[] x) { //preserve Internals of x0
		return SAMPLE_AT(ret, f, x, ret.length); }

	/** Generates a Manifold by sampling f over x	 */
	final static public double[] SAMPLE_AT(final double[] ret, final IFloatFunction f, final double[] x, final int len) { //preserve Internals of x0
		for (int j = len; --j >= 0; ) {
			ret[j] = f.Map(x[j]); }
		return ret;
	}

	/** Samples the Function f on the Interval [x0, x0+Grad*dx]	 */
	final static public double[] SAMPLE(IFloatFunction f, double x0, double dx, int N) { //preserve Internals of x0
		return SAMPLE_AT(new double[N], f, x0, dx);
	}

	/** Samples the Function f on the Interval [x0, x0+Grad*dx]	 */
	final static public double[] SAMPLE_AT(
		final double[] ret,
		final IFloatFunction f,
		double x0,
		final double dx) { //preserve Internals of x0
		if (ret.length == 0) {
			return ret;
		}
		ret[0] = f.Map(x0);
		for (int i = 0; ++i < ret.length;) {
			ret[i] = f.Map(x0 += dx); }
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Builds a new Array of random Length (up to maxLength) filled with uniform random Values in [0,1).
	 * @see streamIO.copy.IICopyAble#randomizeAt()	 */
	final static public double[] RANDOM(final int maxLength) {
		return RANDOMIZE_AT(new double[(int) (Math.random()*maxLength)]); }

	/** Builds a new Array of the given Length filled with uniform random Values in [0,1).
	 * @see streamIO.copy.IICopyAble#randomizeAt()	 */
	final static public double[] RANDOMIZED(final int length) {
		return RANDOMIZE_AT(new double[length]); }
							
	/** Randomizes all the Values of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Assumes a rectangular Array. 	 */
	final static public double[] RANDOMIZE_AT_1_1(double[] arr) {
		int j = arr.length;
		while (--j >= 0) 
			arr[j] = ByRefDouble.RANDOM_1_1(); 
		return arr;
	}

	/** Randomizes all the Values of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Assumes a rectangular Array. 	 */
	final static public double[] RANDOMIZE_AT_1_1(int length) {
		return RANDOMIZE_AT_1_1(new double[length]); 
	}
	
	/** Randomizes all the Values of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Assumes a rectangular Array. 	 */
	final static public double[] RANDOMIZE_AT(double[] arr) {
		for(int j = arr.length; --j >= 0;) 
			arr[j] = Math.random(); 
		return arr;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods returning a single Number from the Array
	////////////////////////////////////////////////////////////////////////////////

	/** Sums all the Values of the given Array.
	 * @return The Sum of all Values in the Array. 	 */
	final static public double SUM(final double[] arr) {
		return SUM(arr, 0, arr.length); }

	/** Sums the Values of the given Array over the Range [start, stop).
	 * @return The Sum of all Values in the Array.	 */
	final static public double SUM(final double[] arr, final int start, int stop) {
		if (start == stop) 
			return 0; 
		double Sum = arr[--stop]; //0;
		while (--stop >= start) {
			Sum += arr[stop]; }
		return Sum;
	}

	/** Multiplies all the Values of the given Array together.
	 * @return The Product of all Values in the Array. 	 */
	final static public double PROD(final double[] arr) {
		return PROD(arr, 0, arr.length); }

	/** Multiplies the Values of the given Array together over the Range [start, stop).
	 * @return The Product of all Values in the Array.	 */
	final static public double PROD(final double[] arr, final int start, int stop) {
		if (start == stop) {
			return 1; }
		double Prod = arr[--stop]; //1;
		while (--stop >= start) {
			Prod *= arr[stop]; }
		return Prod;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public double MIN_VAL(double[] arr) {
		return arr[MIN_POS(arr)]; }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Position of the Minimum Value of the Array.
	 */
	final static public int MIN_POS(double[] arr) {
		return MIN_POS(arr, 0, arr.length); }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Minimum Value of the Array.
	 */
	final static public double MIN_VAL(double[] arr, int Start, int Stop) {
		return arr[MIN_POS(arr, Start, Stop)];
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public int MIN_POS(double[] arr, int Start, int Stop) {
		int iMin = -1;
		double Min = Double.POSITIVE_INFINITY;
		while (--Stop >= Start) {
			if (Min > arr[Stop]) {
				Min = arr[iMin = Stop];
			}
		}
		return iMin;
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Minimum Values of the Array.
	 */
	final static public double[] MIN2VAL(double[] arr, double[] ret) {
		return MIN2VAL(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first two Minimum Values of the Array.
	 */
	final static public int[] MIN2POS(double[] arr, int[] ret) {
		return MIN2POS(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Minimum Values of the Array.
	 */
	final static public double[] MIN2VAL(double[] arr, int Start, int Stop, double[] ret) {
		int[] pos = new int[ret.length];
		MIN2POS(arr, Start, Stop, pos);
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
	final static public int[] MIN2POS(double[] arr, int Start, int Stop, int[] ret) {
		int inMin, iMin = inMin = -1; //the n Values contain the higher Maximum!
		double nMin, Min = nMin = Double.POSITIVE_INFINITY;
		while (--Stop >= Start) {
			if (Min > arr[Stop]) { //larger than the second Max?
				if (nMin > arr[Stop]) { //even larger than the first Max?
					Min = nMin;
					iMin = inMin;
					nMin = arr[inMin = Stop];
				} else {
					Min = arr[iMin = Stop];
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
	final static public double MAX_VAL(double[] arr) {
		return arr[MAX_POS(arr)];
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int MAX_POS(double[] arr) {
		return MAX_POS(arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public double MAX_VAL(double[] arr, int Start, int Stop) {
		return arr[MAX_POS(arr, Start, Stop)];
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Index of the Maximum Value of the Array.
	 */
	final static public int MAX_POS(double[] arr, int Start, int Stop) {
		int iMax = -1;
		double Max = Double.NEGATIVE_INFINITY;
		while (--Stop >= Start) {
			if (Max < arr[Stop]) {
				Max = arr[iMax = Stop];
			}
		}
		return iMax;
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return up to the first two Maximum Values of the Array.
	 */
	final static public double[] MAX2VAL(double[] arr, double[] ret) {
		return MAX2VAL(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first up to two Maximum Values of the Array.
	 */
	final static public int[] MAX2POS(double[] arr, int[] ret) {
		return MAX2POS(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Maximum Values of the Array.
	 */
	final static public double[] MAX2VAL(double[] arr, int Start, int Stop, double[] ret) {
		int[] pos = new int[ret.length];
		MAX2POS(arr, Start, Stop, pos);
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
	 * @return the Indices of the first up to two Maximum Values of the Array.
	 */
	final static public int[] MAX2POS(double[] arr, int Start, int Stop, int[] ret) {
		int inMax, iMax = inMax = -1; //the n Values contain the higher Maximum!
		double nMax, Max = nMax = Double.NEGATIVE_INFINITY;
		while (--Stop >= Start) {
			if (Max < arr[Stop]) { //larger than the second Max?
				if (nMax < arr[Stop]) { //even larger than the first Max?
					Max = nMax;
					iMax = inMax;
					nMax = arr[inMax = Stop];
				} else {
					Max = arr[iMax = Stop];
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
	final static public double[] MIN_MAX_VAL(final double[] arr, final int start, final int stop) {
		return MIN_MAX_VAL(arr, start, stop, new double[2]); }
	
	/**
	 * Determines the Values of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public double[] MIN_MAX_VAL(final double[] arr, final double[] ret) {
		return MIN_MAX_VAL(arr, 0, arr.length, ret); }
	
	/**
	 * Determines the Values of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public double[] MIN_MAX_VAL(final double[] arr, final int start, final int stop, final double[] ret) {
		final int[] pos = MIN_MAX_POS(arr, start, stop, ret.length);
		for(int i  = ret.length; --i >= 0;) 
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
	final static public int[] MIN_MAX_POS(final double[] arr) {
		return MIN_MAX_POS(arr, 2); }
	
	/**
	 * Determines the Indices of the Minimum and the Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN_MAX_POS(final double[] arr, final int numItems) {
		return MIN_MAX_POS(arr, 0, arr.length, numItems); }
	
	/**
	 * Determines the Indices of the Minimum and the Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN_MAX_POS(final double[] arr, final int start, final int stop, final int numItems) {
		return MIN2MAX2POS(arr, start, stop, new int[numItems]); }
	
	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public double[] MIN2MAX2VAL(final double[] arr) {
		return MIN2MAX2VAL(arr, new double[2]); }

	/**
	 * Determines the Indices of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public double[] MIN2MAX2VAL(final double[] arr, final double[] MinMax) {
		final int[] pos = new int[MinMax.length];
		MIN2MAX2POS(arr, pos);
		for (int i = MinMax.length; --i >= 0;) 
			MinMax[i] = arr[pos[i]];
		return MinMax;
	}
	
	/**
	 * Determines the two Minimum and two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum
	 * and 1 less than for determining both individually.
	 * @return the MinMax Array
	 * with the first two Elements filled with the Indices of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN2MAX2POS(final double[] arr) {
		return MIN2MAX2POS(arr, new int[4]); }
	
	/**
	 * Determines the two Minimum and two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum
	 * and 1 less than for determining both individually.
	 * @return the MinMax Array
	 * with the first two Elements filled with the Indices of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN2MAX2POS(final double[] arr, final int[] ret) {
		return MIN2MAX2POS(arr, 0, arr.length, ret); }
	
	/**
	 * Determines the two Minimum and two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum
	 * and 1 less than for determining both individually.
	 * @return the MinMax Array
	 * with the first two Elements filled with the Indices of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN2MAX2POS(final double[] arr, final int start, final int stop) {
		return MIN2MAX2POS(arr, start, stop, new int[2]); }

	/**
	 * Determines the Indices of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum
	 * and 1 less than for determining both individually.
	 * But the Algorithm is considerably more complex! 
	 * @return the MinMax Array
	 * with the first two Elements filled with the Indices of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN2MAX2POS(final double[] arr, final int start, final int stop, final int[] MinMax) {
		int  iMin,  iMax;
		int inMin, inMax;
		double  Min,  Max;
		double nMin, nMax;
		boolean xMax = (MinMax.length > 2);
		boolean xMin = (MinMax.length > 3);
		int i = stop; 
		if (((stop - start) | 1) == 1) { //odd?
			iMin = iMax = inMin = inMax = --i;
			Min = Max = nMin = nMax = arr[i];
		} else { //a bit Overhead, but easier and also for empty Arrays!
			iMin = iMax = inMin = inMax = -1; //cannot jump out earlier!
			Min = nMin = Double.POSITIVE_INFINITY;
			Max = nMax = Double.NEGATIVE_INFINITY;
		}
		double tMin, tMax, tmp;
		int iTMin, iTMax, iTmp;
		while (i > start+1) {
			if ((tMin = arr[iTMin = --i]) > //first compare Args
				(tMax = arr[iTMax = --i])) {
				tmp = tMin; tMin = tMax; tMax = tmp;
				iTmp = iTMin; iTMin = iTMax; iTMax = iTmp;
			}
			if (Min > tMin) { //then compare tMin and tMax to Min and Max
				if (xMin && (nMin > tMin)) { //even larger than the first Max?
					Min = nMin; nMin = tMin;
					iMin = inMin; inMin = iTMin;
				} else {
					Min = tMin;
					iMin = iTMin;
				}
			}
			if (Max < tMax) { //this saves 1/4 of the Comparisons
				if (xMax && (nMax < tMax)) { //even larger than the first Max?
					Max = nMax; nMax = tMax; 
					iMax = inMax; inMax = iTMax;
				} else {
					Max = tMax;
					iMax = iTMax;
				}
			}
			if (xMax && (Max < tMin)) { //even larger than the first Max?
				Max = tMin;
				iMax = iTMin;
			}
			if (xMin && (Min > tMax)) { //even larger than the first Max?
				Min = tMax;
				iMin = iTMax;
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
	  * Computes the euklidean Norm (Length) of the given Array.
	  * @return the Norm of the given Array
	  * Overflow is possible, since not normed by the maximum Element
	  */
	final static public double NORM(final double[] arr) {
		return Math.sqrt(NORM_SQR(arr, arr.length)); }

	/**
	  * Renormalizes the Array in Place to unit Length and returns its original Length.
	  * @return the Norm of the given Array
	  * @param the Array is normed to the given Length
	  */
	final static public double NORM_AT(final double[] arr) {
		return NORM_AT(arr, 1); }

	/**
	  * Renormalizes the Array in Place to the given Length and returns its original Length.
	  * @return the Norm of the given Array
	  * @param the Array is normed to the given Length
	  */
	final static public double NORM_AT(double[] arr, double length) {
		double len = Math.sqrt(NORM_SQR(arr, arr.length));
		MUL_AT(arr, length / len);
		return len;
	}

	/**
	  * Computes the squared euklidean Norm of the given Array.
	  * @return the squared Norm of the given Array
	  */
	final static public double NORM_SQR(final double[] arr) {
		return NORM_SQR(arr, arr.length); }

	/**
	  * This Value can well exceed the Range of valid Numbers,
	  * but that should be avoided anyway by renorming.
	  * Accuracy is not affected when using float Point Numbers.
	  *
	  * @return the squared Norm of the given Array
	  */
	final static public double NORM_SQR(final double[] arr, final int max) {
		return NORM_SQR(arr, 0, max); }

	/**
	  * This Value can well exceed the Range of valid Numbers,
	  * but that should be avoided anyway by renorming.
	  * Accuracy is not affected when using float Point Numbers.
	  *
	  * @return the squared Norm of the given Array
	  */
	final static public double NORM_SQR(final double[] arr, final int min, int max) {
		double norm = 0; //Calculate the Norm
		while (--max >= min) {
			norm += arr[max] * arr[max];
		} //sqr(arr[len]); }
		return norm;
	}
	
	/**
	  * Computes the euklidean Distance between two Arrays.
	  * @return the squared Distance between the given Arrays
	  */
	final static public double DIST(final double[] arr1, final double[] arr2) {
		return Math.sqrt(DIST_SQR(arr1, arr2)); }

	/**
	  * Computes the euklidean Distance between two Arrays, considering only the first dim Dimensions.
	  * @return the squared Distance between the given Arrays
	  */
	final static public double DIST(final double[] arr1, final double[] arr2, int dim) {
		return Math.sqrt(DIST_SQR(arr1, arr2, dim)); }

	/**
	 * Computes the squared euklidean Distance between two Arrays.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @return the squared Distance between the given Arrays
	 */
	final static public double DIST_SQR(final double[] arr1, final double[] arr2) {
		return DIFF_NORM_SQR(arr1, arr2, null); }

	/**
	 * Computes the squared euklidean Distance between two Arrays, considering only the first dim Dimensions.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @param dim the maximum Number of Dimensions to consider
	 * @return the squared Distance between the given Arrays
	 */
	final static public double DIST_SQR(final double[] arr1, final double[] arr2, int dim) {
		return DIFF_NORM_SQR(arr1, arr2, dim, null); }
	
	/**
	 * calculates both the Difference Vector and its Norm. 
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @param diff optional Output Parameter being filled with the Difference Vector.
	 * @return the squared euklidean Norm of the Difference between the given Arrays
	 */
	final static public double DIFF_NORM_SQR(final double[] arr1, final double[] arr2, final double[] diff) {
		return DIFF_NORM_SQR(arr1, arr2, Math.max(arr1.length, arr2.length), diff); }
	
	/** Computes both the Difference Vector and its squared euklidean Norm, considering only the first numDims Dimensions.
	 * @param arr1 first  Vector, not modified.
	 * @param arr2 second Vector, not modified.
	 * @param diff optional Output Parameter being filled with the Difference Vector.
	 * It must be as large as numDims
	 * @return the squared euklidean Norm of the Difference between the given Arrays
	 */
	final static public double DIFF_NORM_SQR(final double[] arr1, final double[] arr2, final int numDims, final double[] diff) {
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
			final double dif = arr1[i] - arr2[i];
			if (diff != null)
				diff[i] = dif; 
			norm += dif * dif;
		}
		return norm;
	}
	
	/**
	  * Computes the L1 (Manhattan) Distance between two Arrays.
	  * @param arr1 first  Vector, not modified.
	  * @param arr2 second Vector, not modified.
	  * @return the absolute Norm of the Distance between the given Arrays
	  */
	final static public double DIST_ABS(final double[] arr1, final double[] arr2) {
		double norm; //Calculate the Norm
		final int minLength; 
		if (arr1.length > arr2.length) {
			minLength = arr2.length;
			norm = NORM_SQR(arr1, minLength, arr1.length); 
		} else {
			minLength = arr1.length;
			norm = NORM_SQR(arr2, minLength, arr2.length); 
		}
		for (int i = minLength; --i >= 0; ) {
			final double diff = arr1[i] - arr2[i]; 
			if (diff > 0) {
				norm += diff;
				continue;
			}
			norm -= diff;
		}
		return norm;
	}

	/**
	  * Computes both the Difference Vector and its L1 (absolute) Norm.
	  * @param diff is an Output Parameter being filled with the Difference Vector.
	  * @return the absolute Norm of the Difference between the given Arrays
	  */
	final static public double DIFF_NORM_ABS(final double[] arr1, final double[] arr2, final double[] diff) {
		double norm; //Calculate the Norm
		final int minLength; 
		if (arr1.length > arr2.length) {
			minLength = arr2.length;
			norm = NORM_ABS(arr1, minLength, arr1.length); 
			COPY(arr1, minLength, arr1.length, diff);
		} else {
			minLength = arr1.length;
			norm = NORM_ABS(arr2, minLength, arr2.length); 
			NEG(arr1, minLength, arr1.length, diff);
		}
		for (int i = minLength; --i >= 0; ) {
			//norm+=Math.abs(diff[i] = arr1[i]-arr2[i]); }
			final double dif = diff[i] = arr1[i] - arr2[i];
			if (dif > 0) { //avoid calling expensive Math.abs
				norm += dif;
				continue;
			}
			norm -= dif;
		}
		return norm;
	}

	/**
	  * Computes the L1 (absolute) Norm of the given Array.
	  * @return the absolute Norm of the given Array
	  */
	final static public double NORM_ABS(final double[] arr) {
		return NORM_ABS(arr, 0, arr.length); }

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 
	 */
	final static public double NORM_MAX(final double[] a, final int start, final int stop) {
		double max = Math.abs(a[stop]);
		for (int j = stop; --j >= start; ) {
			final double tmp = Math.abs(a[j]); 
			if (max < tmp) {
				max = tmp;} 
		}
		return max; }
		
	/**
	  * Computes the L1 (absolute) Norm of the given Array over the Range [start, stop).
	  * @return the absolute Norm of the given Array
	  */
	final static public double NORM_ABS(final double[] arr, final int start, final int stop) {
		double norm = 0; //Calculate the Norm
		for (int i = stop; --i >= start; ) {
			if (arr[i] > 0) {
				norm += arr[i];
				continue;
			}
			norm -= arr[i];
		}
		return norm;
	}
	
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
	final static public double GET_AT(final double[] a, final int index) {
		return GET_AT(a, index, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public double GET_AT(final double[] a, final int index, final int stop) {
		return GET_AT(a, index, 0, stop); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public double GET_AT(final double[] a, final int index, final double defaultValue) {
		return GET_AT(a, index, defaultValue, a.length); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public double GET_AT(final double[] a, final int index, final double defaultValue, final int stop) {
		return GET_AT(a, index, defaultValue, stop, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public double GET_AT(final double[] a, final int index, final double defaultValue, final int stop, final int start) {
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
	final static public double[] GET_AT(final double[] a, final VectorInt index) {
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
	final static public double[] GET_AT(final double[] a, final VectorInt index, double[] ret) {
		return GET_AT(a, index.items, ret, index.itemCount); 
	}
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public double[] GET_AT(final double[] a, final int[] index) {
		return GET_AT(a, index, null); }  
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public double[] GET_AT(final double[] a, final int[] index, final double[] ret) {
		return GET_AT(a, index, ret, index.length); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public double[] GET_AT(final double[] a, final int[] index, final double[] ret, int stop) {
		return GET_AT(a, index, ret, stop, 0); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public double[] GET_AT(final double[] a, final int[] index, double[] ret, final int stop, final int start) {
		if((ret == null) || (ret.length < stop))
			ret = new double[stop];
		//else if (ret.length > stop) //rather leave the Values alone?!?
		//	Arrays.fill(ret, stop, ret.length, 0); 
		for(int i = stop; --i >= start; )
			ret[i] = (index[i] < a.length) ? a[index[i]] : 0; 
		return ret;
	}
	
	/**this is a linear Mapping (Scalar Product) 
	 * from Vector Space into the real Numbers.
	 * @return the Scalar Product of the two Vectors,
	 * even with Dimension Mismatch.
	 */
	final static public double MAP(final double[] a, final double[] arg) {
		int len = arg.length;
		if (len > a.length) {
			len = a.length;
		} //use the Minimum, because higher Elements are assumed to be 0.
		return MAP(a, arg, 0, len);
	}
	
	/** this is a linear Mapping (Scalar Product) 
	 * from Vector Space into the real Numbers.
	 * By Definition Elements outside the Array are 0
	 * @return the scalar Product of the given Arrays up to the given Length.
	 */
	final static public double MAP(final double[] a, final double[] arg, final int start, int stop) {
		double ret = 0;
		while (--stop >= start) {
			ret += a[stop] * arg[stop];
		}
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Computes the Scalar MaxMin Product of two Vectors, i.e. the Maximum of the per-Item Minima.
	  * By Definition Elements outside the Array are 0
	  * @return the Scalar MaxMin Product of the two Vectors.
	  * i.e. the Maximum of the Minima in each Column
	  */
	final static public double MAX_MIN_PROD(double[] a, double[] arg) {
		return MAX_MIN_PROD(a, arg, 0, arg.length);
	}

	/**
	  * Computes the Scalar MaxMin Product of two Vectors over the given Range.
	  * @return the Scalar MaxMin Product of the two Vectors.
	  */
	final static public double MAX_MIN_PROD(double[] a, double[] arg, int start, int stop) {
		double x, y, max = Double.NEGATIVE_INFINITY; //FALSE; //can also start with any lower Value!
		while (--stop >= start) {
			if ((x = a[stop]) < (y = arg[stop])) { //use the Minimum
				if (max < x) { //update the Maximum
					max = x;
				}
			} else {
				if (max < y) { //update the Maximum
					max = y;
				}
			}
		}
		return max;
	}

	/** Computes ret[i] = max(ret[i], min(a[i], y)) in Place over the given Range.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public double[] MAX_MIN_PROD(double[] ret, double[] a, double y, int start, int stop) {
		double x; //FALSE; //can also start with any lower Value!
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
	
	/** Computes ret[i] = max(ret[i], min(a[i], b[i])) in Place over the given Range.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min b)	  */
	final static public double[] MAX_MIN_PROD(final double[] ret, final double[] a, final double[] b, final int start, int stop) {
		double x, y;
		while (--stop >= start) {
			//ret[stop] maxAt(a[stop] min b[stop]); } //equivalent and faster!
			if ((x = a[stop]) < (y = b[stop])) { //use the Minimum
				if (ret[stop] < x) //update the Maximum
					ret[stop] = x;
			} else {
				if (ret[stop] < y) //update the Maximum
					ret[stop] = y;
			}
		}
		return ret;
	}
	
	/** Computes ret[i] = max(ret[i], min(a[i], b[i])) in Place over the whole Array.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min b)	  */
	final static public double[] MAX_MIN_PROD(double[] ret, double[] a, double[] b) {
		return MAX_MIN_PROD(ret, a, b, 0, ret.length); }

	/** Computes ret[i] = max(ret[i], min(a[i], y)) in Place over the whole Array.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public double[] MAX_MIN_PROD(double[] ret, double[] a, double y) {
		return MAX_MIN_PROD(ret, a, y, 0, ret.length); }
	
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
	final static public double LINEAR_FIT(final double[] x, final double[] y
	, final double[] sig, final double[][] abSigaSigb) {
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
	 * @param sig the Standard Errors in x, optional (null allowed) 
	 * @param abSigaSigb returns the Line Parameters (and it's Standard Errors)
	 * optional (null allowed, length 1 or 2 allowed) 
	 * @return Chi� for stop-start-2 Degrees of Freedom
	 */
	final static public double LINEAR_FIT(final double[] x, final double[] y
			, final double[] sig, final double[][] abSigaSigb
			, final int start, final int stop) {
		
		//Calculate the Averages to get best Results 
		double sx=0;
		double sy=0;
		double ss;
		if (sig != null) { 
			L.n("accumulate Sums with Weights"); 
			ss=0;
			for (int i=stop; --i>=start; ) { 
				final double weight=1/ByRefDouble.SQR(sig[i]); 
				ss += weight;
				sx += x[i]*weight;
				sy += y[i]*weight;
			}
		} else {
			L.n("accumulate Sums without Weights"); 
			ss=(stop-start);
			for (int i=stop; --i>=start; ) {
				sx += x[i];
				sy += y[i];
			}
		}
		final double xAvg= sx/ss; //
		final double yAvg= sy/ss; //
		
		L.n("Calculate Chi�"); 		
		double sumDy=0;
		double sumSqrD=0;
		if (sig != null) {
			for (int i=stop; --i>=start; ) {
				final double d=(x[i]-xAvg)/sig[i];
				sumSqrD += d*d;
				sumDy += d*y[i]/sig[i];
			}
		} else {
			for (int i=stop; --i>=start; ) {
				final double d=x[i]-xAvg;
				sumSqrD += d*d;
				sumDy += d*y[i];
			}
		}
		final double b = sumDy/sumSqrD; 
		final double a = yAvg-xAvg*b; 
		if (abSigaSigb != null) {
			abSigaSigb[0][1] = b;
			abSigaSigb[0][0] = a;
			if (abSigaSigb.length > 1) {
				abSigaSigb[1][0] = Math.sqrt((1+sx*sx/(ss*sumSqrD))/ss);
				abSigaSigb[1][1] = Math.sqrt(1/sumSqrD);
			}
		}
		double chi2=0;
		if (sig == null) {
			for (int i=stop; --i>=start; ) {
				chi2 += ByRefDouble.SQR(y[i]-(a+b*x[i])); } 
			if (abSigaSigb != null) 
				if (abSigaSigb.length > 1) {
					final double sigdat=Math.sqrt(chi2/(stop-start-2));
					abSigaSigb[1][0] *= sigdat;
					abSigaSigb[1][1] *= sigdat;
				}
			//q=1;
		} else {
			for (int i=stop; --i>=start; ) {
				chi2 += ByRefDouble.SQR((y[i]-(a+b*x[i]))/sig[i]); }
			//q=GammaP.PROBABILITY_CHI_SQR(ndata-2, chi2);
		}
		return chi2;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// modifying Operations on a single Array
	///////////////////////////////////////////////////////////////////////////////////
	
	/** Negates an entire Array in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] NEG_AT(final double[] ret) {
		return NEG_AT(ret, 0, ret.length); }

	/** Negates an Element Range of an Array in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] NEG_AT(final double[] ret, final int start, final int stop) {
		return NEG(ret, start, stop, ret); }

	/** Negates an Element Range of a double[] Array into ret (or a new Array when ret is null).
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Negative of the given Array
	 */
	final static public double[] NEG(final double[] x, final int start, int stop, double[] ret) {
		if (ret == null)
			ret  = new double[stop]; 
		while (--stop >= start) 
			ret[stop] = -x[stop]; 
		return ret;
	}
	
	/** Negates an Element Range of a float[] Array into ret (or a new Array when ret is null).
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Negative of the given Array
	 */
	final static public double[] NEG(final float[] x, final int start, int stop, double[] ret) {
		if (ret == null)
			ret  = new double[stop];
		while (--stop >= start)
			ret[stop] = - x[stop];
		return ret;
	}

	/** Negates an entire double[] Array into a fresh Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] NEG(final double[] x) {
		return NEG(x, 0, x.length, null); }

	/** Negates an Element Range of a double[] Array into a fresh Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] NEG(final double[] x, final int start, final int stop) {
		return NEG(x, start, stop, null); }

	/** Negates an entire float[] Array into a fresh double[] Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] NEG(final float[] x) {
		return NEG(x, 0, x.length, null); }

	/** Negates an Element Range of a float[] Array into a fresh double[] Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] NEG(final float[] x, final int start, final int stop) {
		return NEG(x, start, stop, null); }

	/** Rounds an entire Array down in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] FLOOR_AT(final double[] ret) {
		return FLOOR_AT(ret, 0, ret.length); }

	/** Rounds an Element Range of an Array down in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] FLOOR_AT(final double[] ret, final int start, final int stop) {
		return FLOOR(ret, start, stop, ret); }

	/** Rounds an Element Range of a double[] Array down into ret (or a new Array when ret is null).
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Negative of the given Array
	 */
	final static public double[] FLOOR(final double[] x, final int start, int stop, double[] ret) {
		if (ret == null)
			ret = new double[stop]; 
		while (--stop >= start) 
			ret[stop] = Math.floor(x[stop]);
		return ret;
	}

	/** Applies floor() to an Element Range of a float[] Array into ret (or a new Array when ret is null).
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Values of the given Array rounded down
	 */
	final static public double[] FLOOR(final float[] x, final int start, int stop, double[] ret) {
		if (ret == null)
			ret = new double[stop];
		while(--stop >= start)
			ret[stop] = Math.floor(x[stop]);
		return ret;
	}

	/** Rounds an entire double[] Array down into a fresh Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] FLOOR(final double[] x) {
		return FLOOR(x, 0, x.length, null); }

	/** Rounds an Element Range of a double[] Array down into a fresh Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] FLOOR(final double[] x, final int start, final int stop) {
		return FLOOR(x, start, stop, null); }

	/** Rounds an entire float[] Array down into a fresh double[] Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] FLOOR(final float[] x) {
		return FLOOR(x, 0, x.length, null); }

	/** Rounds an Element Range of a float[] Array down into a fresh double[] Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] FLOOR(final float[] x, final int start, final int stop) {
		return FLOOR(x, start, stop, null); }

	/** Inverts (1/x) an entire Array in Place.
	  * @return the multiplicative Inverse of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] INV_AT(final double[] ret) {
		return INV_AT(ret, 0, ret.length); }

	/** Inverts (1/x) an Element Range of an Array in Place.
	  * @return the multiplicative Inverse of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] INV_AT(final double[] ret, final int start, final int stop) {
		return INV(ret, start, stop, ret); }

	/** Inverts (1/x) an entire Array into ret.
	  * @return the multiplicative Inverse of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] INV(final double[] ret, final double[] x) {
		return INV(x, 0, ret.length, ret); }

	/** Inverts (1/x) an Element Range of an Array into ret (or a new Array when ret is null).
	  * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the multiplicative Inverse of the given Array
	  */
	final static public double[] INV(final double[] x, final int start, int stop, double[] ret) {
		if (ret == null)
			ret = new double[stop]; 
		while(--stop >= start) 
			ret[stop] = 1 / x[stop];
		return ret;
	}
	
	/** Takes the absolute Value of an entire Array in Place.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ABS_AT(final double[] ret) {
		return ABS_AT(ret, 0, ret.length); }
	
	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @return the absolute Value of the Values in the given Array
	  */
	final static public double[] ABS(final double[] arg, final int start, int stop, double[] ret) {
		if (ret == null)
			ret = new double[stop];
		while (--stop >= start) {
			final double tmp; //Calculate the Norm
			if (0 <= (tmp = arg[stop]))
				ret[stop] =  tmp;
			else
				ret[stop] = -tmp;
		}
		return ret;
	}
	
	/** Takes the absolute Value of an Element Range of an Array in Place.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] ABS_AT(final double[] ret, final int start, int stop) {
		//return ABS(ret, ret, start, stop); 
		double tmp; //Calculate the Norm
		while (--stop >= start) {
			if (0 <= (tmp = ret[stop])) 
				continue; //Optimization
			ret[stop] = -tmp;
		}
		return ret;
	}
	
	/** Raises every Element of arg to the given Exponent into a fresh Array.
	  * @param exp the Exponent to use
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Power to the given Exponent of the Values in the given Array
	  */
	final static public double[] POW(final double[] arg, final double exp) {
		return POW(arg, exp, 0, arg.length, null); }

	/** Raises every Element of arg to the given Exponent into ret.
	  * @param exp the Exponent to use
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Power to the given Exponent of the Values in the given Array
	  */
	final static public double[] POW(final double[] arg, final double exp, final double[] ret) {
		return POW(arg, exp, 0, ret.length, ret); }

	/** Raises an Element Range of arg to the given Exponent into ret (or a new Array when ret is null).
	 * @param exp the Exponent to use
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @return the Power to the given Exponent of the Values in the given Array
	 */
	final static public double[] POW(final double[] arg, final double exp,
			final int start, int stop, double[] ret) {
		if (ret == null)
			ret = new double[stop]; 
		if (exp == 1) //Optimization: save Exponentiation or copying 
			System.arraycopy(arg, start, ret, start, stop-start); 
		else
			while (--stop >= start) 
				ret[stop] = Math.pow(arg[stop], exp); 
		return ret;
	}
	
	/** Raises an entire Array to the given Exponent in Place.
	  * @return the Power to the given Exponent of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param exp the Exponent to use
	  */
	final static public double[] POW_AT(final double[] ret, final double exp) {
		return POW_AT(ret, exp, 0, ret.length); }

	/** Raises an Element Range of an Array to the given Exponent in Place.
	  * @return the Power to the given Exponent of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param exp the Exponent to use
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] POW_AT(final double[] ret, final double exp,
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
	final static public double[] LOG_AT(final double[] ret) {
		return LOG(ret, 0, ret.length, ret); }
	
	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the natural Logarithm of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] LOG_AT(final double[] ret, final int start, int stop) {
		return LOG(ret, start, stop, ret); }
	
	/**
	 * This is used e.g. in deriving the Distances of Poisson Distributions
	 * from the given Probabilities.
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @return the natural Logarithm of the Values in the given Array
	 */
	final static public double[] LOG(final double[] arg, final double[] ret) {
		return LOG(ret, 0, ret.length, ret); }
	
	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	 * @param start Index from  where the Array is processed
	 * @param stop  Index up to where the Array is processed (not ret[stop]!)
	 * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @return the natural Logarithm of the Values in the given Array
	  */
	final static public double[] LOG(final double[] arg, 
			final int start, int stop, double[] ret) {
		if (ret == null)
			ret = new double[stop]; 
		while(--stop >= start) 
			ret[stop] = Math.log(arg[stop]);
		return ret;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// modifying Operations on a single Array
	///////////////////////////////////////////////////////////////////////////////////
	
	/** Normalizes the given Array in Place to unit Length.
	  * @return the given Array normalized to 1
	  */
	final static public double[] NORMALIZE_AT(final double[] arr) {
		return NORMALIZE_AT(arr, 1); }

	/** Normalizes the given Array in Place to the given Length.
	  * @return the given Array normalized to the given Length
	  */
	final static public double[] NORMALIZE_AT(final double[] arr, final double length) {
		return MUL_AT(arr, length / Math.sqrt(NORM_SQR(arr))); } //Calculate the Norm
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Binary Operations
	///////////////////////////////////////////////////////////////////////////////////

	/** Caps every Element of the whole Array in Place so no Item exceeds Limit.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Limit the Value to limit to
	  * @return the given Array ret with the common Minimum Value of ret and Limit.
	  */
	final static public double[] MIN_AT(final double[] ret, final double Limit) {
		return MIN_AT(ret, Limit, 0, ret.length);
	}

	/** Caps an Element Range of an Array in Place so no Item exceeds Limit.
	  * @return the given Array ret with the common Minimum Value of ret and Limit.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Limit the upper Value to limit to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MIN_AT(final double[] ret, final double Limit, final int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > Limit) 
				ret[stop] = Limit; 
		}
		return ret;
	}

	/** Caps every Element of the whole Array in Place to the common Minimum with arr.
	  * @return the given Array ret with the common Minimum Values of ret and arr.
	  * @param arr Array with the Values to be processed.
	  */
	final static public double[] MIN_AT(final double[] ret, final double[] arr) {
		return MIN_AT(ret, arr, 0, arr.length);
	}

	/** Caps an Element Range of an Array in Place to the common Minimum with arr.
	  * @return the given Array ret with the common Minimum Values of ret and arr.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MIN_AT(final double[] ret, final double[] arr, final int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > arr[stop]) 
				ret[stop] = arr[stop]; 
		}
		return ret;
	}

	/** Floors every Element of the whole Array in Place so no Item falls below Limit.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Limit the lower Value to limit to
	  * @return the given Array ret limited by the given lower Limit
	  */
	final static public double[] MAX_AT(final double[] ret, final double Limit) {
		return MAX_AT(ret, Limit, 0, ret.length);
	}

	/** Floors an Element Range of an Array in Place so no Item falls below Limit.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Limit the lower Value to limit to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  * @return the given Array ret limited by the given lower Limit
	  */
	final static public double[] MAX_AT(final double[] ret, final double Limit, final int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < Limit) {
				ret[stop] = Limit; }
		}
		return ret;
	}

	/** Floors every Element of the whole Array in Place to the common Maximum with arr.
	  * @return the given Array ret with the common Maximum Values of ret and arr.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] MAX_AT(final double[] ret, final double[] arr) {
		return MAX_AT(ret, arr, 0, arr.length);
	}

	/** Floors an Element Range of an Array in Place to the common Maximum with arr.
	  * @return the given Array ret with the common Maximum Values of ret and arr.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MAX_AT(final double[] ret, final double[] arr, final int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < arr[stop]) 
				ret[stop] = arr[stop]; 
		}
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Adds a constant Increment to every Element of the whole Array in Place.
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public double[] ADD_AT(final double[] ret, final double Increment) {
		return ADD_AT(ret, Increment, 0, ret.length);
	}

	/** Adds a constant Increment to an Element Range of an Array in Place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] ADD_AT(double[] ret, double increment, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += increment; }
		return ret;
	}

	/** Adds arr to ret element-wise in Place, extending ret when arr is longer.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD_AT(final double[] ret, final double[] arr) {
		return ADD_AT(ret, ret.length, arr, arr.length); }

	/** Adds arr to ret element-wise in Place, filling ret's tail from arr when retLength &lt; arrLength.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD_AT(final double[] ret, final int retLength, final double[] arr, final int arrLength) {
		if (retLength < arrLength) { //make it work (though less effectively)
			if (ret.length < arrLength) { //make it work (though less effectively)
				return ADD(ret, retLength, arr, arrLength); }
			COPY(arr, retLength, arrLength, ret);
		}
		return ADD_AT(ret, arr, 0, arrLength);
	}

	/** Adds arr to ret element-wise in Place, extending ret when arr is longer.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD_AT(final double[] ret, final float[] arr) {
		return ADD_AT(ret, ret.length, arr, arr.length);
	}

	/** Adds arr to ret element-wise in Place, filling ret's tail from arr when retLength &lt; arrLength.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD_AT(final double[] ret, final int retLength, final float[] arr, final int arrLength) {
		if (retLength < arrLength) { //make it work (though less effectively)
			if (ret.length < arrLength) { //make it work (though less effectively)
				return ADD(ret, retLength, arr, arrLength); }
			COPY(ret, arr, retLength, arrLength);
		}
		return ADD_AT(ret, arr, 0, arrLength);
	}
	
	/** Adds an Element Range of arr into ret in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] ADD_AT(double[] ret, float[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += arr[stop]; }
		return ret;
	}

	/** Adds an Element Range of arr into ret in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] ADD_AT(double[] ret, double[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += arr[stop]; }
		return ret;
	}

	/** Adds two whole Arrays into ret, or a fresh Array when ret is null.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD(double[] ret, double[] sum1, double[] sum2) {
		return ADD(ret, sum1, sum2, 0, sum1.length);
	}

	/** Adds an Element Range of two Arrays into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] ADD(double[] ret, double[] sum1, double[] sum2, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = sum1[stop] + sum2[stop]; }
		return ret;
	}

	/** Adds an Element Range of a double[] and a float[] Array into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] ADD(double[] ret, double[] sum1, float[] sum2, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = sum1[stop] + sum2[stop]; }
		return ret;
	}
	
	/** Adds a constant Increment to every Element of sum1 into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD(double[] ret, double[] sum1, double Incr) {
		return ADD(ret, sum1, Incr, 0, sum1.length);
	}

	/** Adds a constant Increment to an Element Range of sum1 into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] ADD(double[] ret, double[] sum1, double Incr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = sum1[stop] + Incr;
		}
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Adds two whole Arrays into a fresh Array, sized to the longer Operand.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD(final double[] sum1, final double[] sum2) {
		return ADD(sum1, sum1.length, sum2, sum2.length); }

	/** Adds two differently-sized Arrays into a fresh Array, copying the longer Operand's tail through unchanged.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD(final double[] sum1, final int sum1Length, final double[] sum2, final int sum2Length) {
		final double[] ret; 
		final int minLength; 
		if (sum1Length > sum2Length) {
			minLength = sum2Length; 
			ret = new double[sum1Length];
			//COPY_AT(ret, min, sub.length, min.length);
			System.arraycopy(sum1, minLength, ret, minLength, ret.length - minLength);
		} else {
			minLength = sum1Length; 
			ret = new double[sum2Length];
			//COPY_AT(ret, min, sub.length, min.length);
			System.arraycopy(sum2, minLength, ret, minLength, ret.length - minLength);
		}
		return ADD(ret, sum1, sum2, 0, minLength); 
	}

	/** Adds a double[] and a float[] Array into a fresh Array, sized to the longer Operand.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD(final double[] sum1, final float[] sum2) {
		return ADD(sum1, sum1.length, sum2, sum2.length); }

	/** Adds a differently-sized double[] and float[] Array into a fresh Array, copying the longer Operand's tail through unchanged.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD(final double[] sum1, final int sum1Length, final float[] sum2, final int sum2Length) {
		final double[] ret; 
		final int minLength; 
		if (sum1Length > sum2Length) {
			minLength = sum2Length; 
			ret = new double[sum1Length];
			//COPY_AT(ret, min, sub.length, min.length);
			System.arraycopy(sum1, minLength, ret, minLength, ret.length - minLength);
		} else {
			minLength = sum1Length; 
			ret = new double[sum2Length];
			//COPY_AT(ret, min, sub.length, min.length);
			System.arraycopy(sum2, minLength, ret, minLength, ret.length - minLength);
		}
		return ADD(ret, sum1, sum2, 0, minLength); 
	}
		
	/** Adds an Element Range of two Arrays into a fresh Array.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] ADD(double[] sum1, double[] sum2, int start, int stop) {
		return ADD(new double[stop], sum1, sum2, start, stop);
	}

	/** Adds a constant Increment to an Element Range of sum1 into a fresh Array.
	  * @return the Sum of the given Array and the Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] ADD(double[] sum1, double Incr, int start, int stop) {
		return ADD(new double[stop], sum1, Incr, start, stop);
	}

	/** Adds a constant Increment to every Element of sum1 into a fresh Array.
	  * @return the Sum of the given Array and the Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] ADD(double[] sum1, double Incr) {
		return ADD(sum1, Incr, 0, sum1.length); }
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Subtracts sub from ret element-wise in Place, extending ret when sub is longer.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB_AT(final double[] ret, final double[] sub) {
		return SUB_AT(ret, ret.length, sub, sub.length); }

	/** Subtracts sub from ret element-wise in Place, filling ret's tail with sub's negated tail when retLength &lt; subLength.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB_AT(final double[] ret, final int retLength, final double[] sub, final int subLength) {
		if (retLength < subLength) { //make it work (though less effectively)
			if (ret.length < subLength) { //make it work (though less effectively)
				return SUB(ret, retLength, sub, subLength); }
			NEG(sub, retLength, subLength, ret);
		}
		return SUB_AT(ret, sub, 0, subLength);
	}

	/** Subtracts sub from ret element-wise in Place, extending ret when sub is longer.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB_AT(double[] ret, float[] sub) {
		return SUB_AT(ret, ret.length, sub, sub.length); }

	/** Subtracts sub from ret element-wise in Place, filling ret's tail with sub's negated tail when retLength &lt; subLength.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB_AT(double[] ret, final int retLength, float[] sub, final int subLength) {
		if (retLength < subLength) { //make it work (though less effectively)
			if (ret.length < subLength) { //make it work (though less effectively)
				return SUB(ret, retLength, sub, subLength); }
			NEG(sub, retLength, subLength, ret);
		}
		return SUB_AT(ret, sub, 0, subLength);
	}

	/** Subtracts an Element Range of arr from ret in Place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] SUB_AT(final double[] ret, final double[] arr, final int start, final int stop) {
		for (int i = stop; --i >= start;) {
			ret[i] -= arr[i]; }
		return ret;
	}

	/** Subtracts an Element Range of arr from ret in Place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] SUB_AT(double[] ret, float[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= arr[stop];
		}
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Subtracts sub from min into ret.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB(final double[] ret, final double[] min, final double[] sub) {
		return SUB(ret, min, sub, 0, sub.length); }

	/** Subtracts sub from min into ret, or copies min into ret when sub is null.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB(final double[] ret, final float[] min, final double[] sub) {
		if (sub == null) {
			return COPY(ret, min, 0, min.length); }
		return SUB(ret, min, sub, 0, sub.length); }

	/** Subtracts sub from min into ret.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB(final double[] ret, final double[] min, final float[] sub) {
		return SUB(ret, min, sub, 0, sub.length); }
	
	/** Subtracts an Element Range of sub from min into ret (or a new Array when ret is null).
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] SUB(double[] ret, double[] min, double[] sub, int start, int stop) {
		if (ret == null) {
			ret = new double[stop]; }
		while (--stop >= start) {
			ret[stop] = min[stop] - sub[stop];
		}
		return ret;
	}

	/** Subtracts an Element Range of sub from min into ret (or a new Array when ret is null).
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] SUB(double[] ret, double[] min, float[] sub, int start, int stop) {
		if (ret == null) {
			ret = new double[stop]; }
		while (--stop >= start) {
			ret[stop] = min[stop] - sub[stop];
		}
		return ret;
	}

	/** Subtracts an Element Range of sub from min into ret (or a new Array when ret is null).
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] SUB(double[] ret, float[] min, double[] sub, int start, int stop) {
		if (ret == null) {
			ret = new double[stop]; }
		while (--stop >= start) {
			ret[stop] = min[stop] - sub[stop];
		}
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Subtracts sub from min into a fresh Array, sized to the longer Operand.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB(final double[] min, final double[] sub) {
		return SUB(min, min.length, sub, sub.length); }

	/** Subtracts sub from a differently-sized ths into a fresh Array, copying the longer Operand's tail through (negated when sub is longer).
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB(final double[] ths, final int thisLength, final double[] sub, final int subLength) {
		final double[] ret; 
		final int minLength; 
		if (thisLength > subLength) {
			minLength = subLength; 
			ret = new double[thisLength];
			//COPY_AT(ret, min, sub.length, min.length);
			System.arraycopy(ths, minLength, ret, minLength, ret.length - minLength);
		} else {
			minLength = thisLength; 
			ret = new double[subLength];
			NEG(sub, minLength, ret.length, ret);
		}
		return SUB(ret, ths, sub, 0, minLength); 
	}

	/** Subtracts sub from min into a fresh Array, sized to the longer Operand.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB(final double[] min, final float[] sub) {
		return SUB(min, min.length, sub, sub.length); }

	/** Subtracts sub from a differently-sized ths into a fresh Array, copying the longer Operand's tail through (negated when sub is longer).
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] SUB(final double[] ths, final int thisLength, final float[] sub, final int subLength) {
		final double[] ret; 
		final int minLength; 
		if (thisLength > subLength) {
			minLength = subLength; 
			ret = new double[thisLength];
			//COPY_AT(ret, min, sub.length, min.length);
			System.arraycopy(ths, minLength, ret, minLength, ret.length - minLength);
		} else {
			minLength = thisLength; 
			ret = new double[subLength];
			NEG(sub, minLength, ret.length, ret);
		}
		return SUB(ret, ths, sub, 0, minLength); 
	}
	
	/** Subtracts an Element Range of sub from min into a fresh Array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] SUB(final double[] min, final double[] sub, final int start, final int stop) {
		return SUB(new double[stop], min, sub, start, stop);
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	  * To implement divAt, just invert the Factor
	  * @param Factor the Factor to multiply with
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @return the given Array multiplied by the given Factor
	  */
	final static public double[] MUL_AT(double[] ret, double Factor) {
		return MUL_AT(ret, Factor, 0, ret.length);
	}

	/** Multiplies an Element Range of an Array by the given Factor in Place.
	  * @return the Product of the Array with the given Factor
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Factor the Factor to multiply with
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MUL_AT(double[] ret, double Factor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] *= Factor; }
		return ret;
	}

	/** Multiplies ret by arr element-wise in Place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] MUL_AT(final double[] ret, final double[] arr) {
		return MUL_AT(ret, ret.length, arr, arr.length); }

	/** Multiplies ret by arr element-wise in Place, over only the shorter Operand's Length.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] MUL_AT(final double[] ret, final int retLength, final double[] arr, final int arrLength) {
		final int common = Math.min(retLength, arrLength);
		MUL_AT(ret, arr, 0, common);
		FILL_AT(ret, 0, common, retLength); //The upper Elements must be set to 0 or the Size limited!
		return ret;
	}

	/** Multiplies ret by arr element-wise in Place, extending ret when arr is longer.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] MUL_AT(final double[] ret, final float[] arr) {
		return MUL_AT(ret, ret.length, arr, arr.length); }

	/** Multiplies ret by arr element-wise in Place, over only the shorter Operand's Length.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] MUL_AT(final double[] ret, final int retLength, final float[] arr, final int arrLength) {
		final int common = Math.min(arrLength, retLength);
		MUL_AT(ret, arr, 0, common);
		FILL_AT(ret, 0, common, retLength); //The upper Elements must be set to 0 or the Size limited!
		return ret; }
	
	/** Multiplies an Element Range of ret by arr in Place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MUL_AT(double[] ret, double[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] *= arr[stop]; }
		return ret;
	}

	/** Multiplies an Element Range of ret by arr in Place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MUL_AT(double[] ret, float[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] *= arr[stop]; }
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Multiplies every Element of min by the given Factor into a fresh Array.
	  * @param min Array with the Values to be processed.
	  * @return a new Array as the Product of the given Array with the Factor
	  */
	final static public double[] MUL(final double[] min, final double factor) {
		return MUL(new double[min.length], min, factor, 0, min.length);
	}

	/** Multiplies min by sub element-wise into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] MUL(final double[] ret, final double[] min, final double[] sub) {
		return MUL(ret, min, sub, 0, sub.length);
	}

	/** Multiplies an Element Range of min by sub into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MUL(final double[] ret, final double[] min, final double[] sub, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = min[stop] * sub[stop]; }
		return ret;
	}

	/** Multiplies min by the given Factor into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] MUL(final double[] ret, final double[] min, final double factor) {
		return MUL(ret, min, factor, 0, min.length); }

	/** Multiplies an Element Range of min by the given Factor into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MUL(double[] ret, double[] min, double factor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = min[stop] * factor;
		}
		return ret;
	}
	
	/** Multiplies an Element Range of min by the given Factor into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MUL(double[] ret, float[] min, double factor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = min[stop] * factor;
		}
		return ret;
	}
	
	/** Multiplies two Arrays element-wise into a fresh Array, sized to the shorter Operand.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] MUL(double[] f1, double[] f2) {
		return MUL(f1, f2, 0, Math.min(f2.length, f1.length)); }

	/** Multiplies an Element Range of two Arrays into a fresh Array.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] MUL(double[] min, double[] sub, int start, int stop) {
		return MUL(new double[stop], min, sub, start, stop); 
	}
		
	/////////////////////////////////////////////////////////////////////////////////////

	/** Divides ret by denom element-wise in Place, extending ret when denom is longer.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] DIV_AT(final double[] ret, final float[] denom) {
		return DIV_AT(ret, ret.length, denom, denom.length); }

	/** Divides ret by denom element-wise in Place, filling ret's tail with positive infinity when retLength &gt;= denomLength.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] DIV_AT(final double[] ret, int retLength, final float[] denom, final int denomLength) {
		if (retLength < denomLength) { //make it work (though less effectively)
			if (ret.length < denomLength) { //make it work (though less effectively)
				return DIV(ret, retLength, denom, denomLength); }
		} else {
			FILL_AT(ret, Double.POSITIVE_INFINITY, denomLength, retLength); 
			retLength = denomLength; 
		}
		return DIV_AT(ret, denom, 0, retLength); }
	
	/** Divides ret by denom element-wise in Place, extending ret when denom is longer.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] DIV_AT(final double[] ret, final double[] denom) {
		return DIV_AT(ret, ret.length, denom, denom.length); }

	/** Divides ret by denom element-wise in Place, filling ret's tail with positive infinity when retLength &gt;= denomLength.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] DIV_AT(final double[] ret, int retLength, final double[] denom, final int denomLength) {
		if (retLength < denomLength) { //make it work (though less effectively)
			if (ret.length < denomLength) { //make it work (though less effectively)
				return DIV(ret, retLength, denom, denomLength); }
		} else {
			FILL_AT(ret, Double.POSITIVE_INFINITY, denomLength, retLength); 
			retLength = denomLength; 
		}
		return DIV_AT(ret, denom, 0, retLength); }

	/** Divides an Element Range of ret by divisor in Place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] DIV_AT(double[] ret, double[] divisor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] /= divisor[stop]; }
		return ret;
	}

	/** Divides an Element Range of ret by divisor in Place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] DIV_AT(double[] ret, float[] divisor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] /= divisor[stop]; }
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Divides min by sub element-wise into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] DIV(double[] ret, double[] min, double[] sub) {
		return DIV(ret, min, sub, 0, sub.length); }

	/** Divides an Element Range of divident by divisor into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] DIV(double[] ret, double[] divident, double[] divisor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = divident[stop] / divisor[stop]; }
		return ret;
	}

	/** Divides an Element Range of divident by divisor into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] DIV(double[] ret, double[] divident, float[] divisor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = divident[stop] / divisor[stop]; }
		return ret;
	}
	
	/** Divides numer by denum element-wise into a fresh Array, sized to the longer Operand.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] DIV(final double[] numer, final float[] denum) {
		return DIV(numer, numer.length, denum, denum.length); }

	/** Divides a differently-sized numer by denum into a fresh Array, filling the excess Length with positive infinity when numer is longer.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] DIV(final double[] numer, final int numerLength, final float[] denum, final int denumLength) {
		final double[] ret; 
		final int minLength; 
		if (numerLength > denumLength) {
			minLength = denumLength; 
			ret = new double[numerLength]; //you have to check the Sign of the Infinite Value!
			FILL_AT(ret, Double.POSITIVE_INFINITY, minLength, ret.length);
		} else {
			minLength = numerLength; 
			ret = new double[numerLength]; //don't create NANs for 0/0 Values! not well defined anyway...
		}
		return DIV(ret, numer, denum, 0, minLength); 
	}
	
	/** Divides numer by denum element-wise into a fresh Array, sized to the longer Operand.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] DIV(final double[] numer, final double[] denum) {
		return DIV(numer, numer.length, denum, denum.length); }

	/** Divides a differently-sized numer by denum into a fresh Array, filling the excess Length with positive infinity when numer is longer.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public double[] DIV(final double[] numer, final int numerLength, final double[] denum, final int denumLength) {
		final double[] ret; 
		final int minLength; 
		if (numerLength > denumLength) {
			minLength = denumLength; 
			ret = new double[numerLength]; //you have to check the Sign of the Infinite Value!
			FILL_AT(ret, Double.POSITIVE_INFINITY, minLength, ret.length);
		} else {
			minLength = numerLength; 
			ret = new double[numerLength]; //don't create NANs for 0/0 Values! not well defined anyway...
		}
		return DIV(ret, numer, denum, 0, minLength); 
	}

	/** Divides an Element Range of min by divisor into a fresh Array.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public double[] DIV(double[] min, double[] divisor, int start, int stop) {
		return DIV(new double[stop], min, divisor, start, stop);
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Ring Methods
	///////////////////////////////////////////////////////////////////////////////////

	/// these Methods with scalar Parameters have been removed,
	/// because they can be replaced by their addAt and mulAt Counterparts.
	/**  Linear Mapping in Place: x+=a * y	 replaced by addAt(a*y)  */
	//	final static public double[] addProdAt (double[] ret, double a, double y) {
	/**  Linear Mapping in Place: x-=a * y	 replaced by subAt(a*y)  */
	//	final static public double[] subtProdAt(double[] ret, double a, double y) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by LinAt(a, y*b)  */
	//	final static public double[] BiLinAt   (double[] ret, double a, double y, double b) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by BiLinAt(a, b*y)  */
	//	final static public double[] BiLinAt   (double[] ret, double[] a, double y, double b) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by BiLinAt(a, b*y)  */
	//	final static public double[] BiLinAt   (double[] ret, double[] a, double y, double[] b) {

	/** Linear Mapping in Place: x+=a*y over an Element Range.
	  * @return Linear Mapping in Place: x+=a * y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final double[] a, final double y, final int start, final int stop) {
		return ADD_PROD_AT(ret, a, y, start, stop, 0); }

	/** Linear Mapping in Place: x+=a*y over an Element Range.
	  * @return Linear Mapping in Place: x+=a * y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final float[] a, final double y, final int start, final int stop) {
		return ADD_PROD_AT(ret, a, y, start, stop, 0); }

	/** Linear Mapping in Place: x+=a*y over an Element Range, writing into ret at retOffset.
	  * @return Linear Mapping in Place: x+=a * y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final double[] a, final double y, final int start, int stop, final int retOffset) {
		while (--stop >= start) {
			ret[stop+retOffset] += a[stop] * y; }
		return ret;
	}

	/** Linear Mapping in Place: x+=a*y over an Element Range, writing into ret at retOffset.
	  * @return Linear Mapping in Place: x+=a * y	 */
	final static public double[] ADD_PROD_AT(double[] ret, final float[] a, final double y, final int start, int stop, final int retOffset) {
		while (--stop >= start) {
			ret[stop+retOffset] += a[stop] * y; }
		return ret;
	}
		
	/** Linear Mapping in Place: x+=a*y over an Element Range, with a and y both Arrays.
	  * @return Linear Mapping in Place: x+=a * y	 */
	final static public double[] ADD_PROD_AT(double[] ret, double[] a, double[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y[stop];
		}
		return ret;
	}
	
	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(double[] ret, final double[] x, final double[] a, final double y, final int start, int stop) {
		if (ret == null) {
			ret = new double[stop]; }
		while (--stop >= start) {
			ret[stop] = x[stop] + a[stop] * y; }
		return ret;
	}
		
	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(double[] ret, final double[] x, final float[] a, final double y, final int start, int stop) {
		if (ret == null) {
			ret = new double[stop]; }
		while (--stop >= start) {
			ret[stop] = x[stop] + a[stop] * y; }
		return ret;
	}
		
	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(double[] ret, double[] x, double[] a, double[] y, int start, int stop) {
		if (ret == null) {
			ret = new double[stop]; }
		while (--stop >= start) {
			ret[stop] = x[stop] + a[stop] * y[stop];
		}
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Linear Mapping in Place over an Element Range: x += a*y, with a a scalar and y an Array.
	  * @return Linear Mapping in Place: x += a*y	 */
	final static public double[] ADD_PROD_AT(double[] ret, double a, double[] y, int start, int stop) {
		return ADD_PROD_AT(ret, y, a, start, stop); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final double a, final double[] y) {
		return ADD_PROD_AT(ret, ret.length, y, y.length, a); } //commutative!
	
	/**  Linear Mapping in Place: x += a*y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final int retLength, final double a, final double[] y, final int yLength) {
		return ADD_PROD_AT(ret, ret.length, y, y.length, a); } //commutative!
	
	/**  Linear Mapping in Place: x += a*y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final double[] a, final double y) {
		return ADD_PROD_AT(ret, ret.length, a, a.length, y); }
	
	/**  Linear Mapping in Place: x += a*y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final float[] a, final double y) {
		return ADD_PROD_AT(ret, ret.length, a, a.length, y); }
	
	/**  Linear Mapping in Place: x += a*y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final int retLength, final double[] a, final int aLength, final double y) {
		final int prodLength = aLength; 
		if (retLength < prodLength) { //make it work (though less effectively)
			if (ret.length < prodLength) { //make it work (though less effectively)
				return ADD_PROD(ret, retLength, a, aLength, y); }
			MUL(ret, a, y, retLength, prodLength);
		}
		return ADD_PROD_AT(ret, a, y, 0, prodLength);
	}
	
	/**  Linear Mapping in Place: x += a*y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final int retLength, final float[] a, final int aLength, final double y) {
		final int prodLength = aLength; 
		if (retLength < prodLength) { //make it work (though less effectively)
			if (ret.length < prodLength) { //make it work (though less effectively)
				return ADD_PROD(ret, retLength, a, aLength, y); }
			MUL(ret, a, y, retLength, prodLength);
		}
		return ADD_PROD_AT(ret, a, y, 0, prodLength);
	}
	
	/**  Linear Mapping in Place: x += a*y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final double[] a, final double[] y) {
		return ADD_PROD_AT(ret, ret.length, a, a.length, y, y.length); }

	/**  Linear Mapping in Place: x += a*y	 */
	final static public double[] ADD_PROD_AT(final double[] ret, final int retLength, final double[] a, final int aLength, final double[] y, final int yLength) {
		final int prodLength = Math.min(aLength, yLength); 
		if (retLength < prodLength) { //make it work (though less effectively)
			if (ret.length < prodLength) { //make it work (though less effectively)
				return ADD_PROD(ret, retLength, a, aLength, y, yLength); }
			MUL(ret, a, y, retLength, prodLength);
		}
		return ADD_PROD_AT(ret, a, y, 0, prodLength);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(double[] ret, double[] x, double a, double[] y, int start, int stop) {
		return ADD_PROD(ret, x, y, a, start, stop);
	}

	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(double[] ret, double[] x, double a, double[] y) {
		return ADD_PROD(ret, x, a, y, 0, y.length);
	}

	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(final double[] ret, final double[] x, final double[] a, final double y) {
		return ADD_PROD(ret, x, a, y, 0, a.length);
	}

	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(final double[] ret, final double[] x, final double[] a, final double[] y) {
		return ADD_PROD(ret, x, a, y, 0, a.length);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(final double[] x, final double a, final double[] y, final int start, final int stop) {
		return ADD_PROD(new double[stop], x, y, a, start, stop);
	}

	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(final double[] x, final double a, final double[] y) {
		return ADD_PROD(x, y, a);
	}

	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(final double[] x, final double[] a, final double y) {
		return ADD_PROD(x, x.length, a, a.length, y); }
	
	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(final double[] x, final int xLength, final double[] a, final int aLength, final double y) {
		final double[] ret; 
		final int minProdLength = aLength; 
		final int minLength; 
		if (xLength > minProdLength) {
			minLength = minProdLength; 
			ret = new double[xLength]; 
			System.arraycopy(x, minProdLength, ret, minProdLength, xLength - minProdLength);
		} else {
			minLength = xLength; 
			ret = new double[minProdLength];
			MUL(ret, a, y, xLength, minProdLength);
		}
		return ADD_PROD(ret, x, a, y, 0, minLength); 
	
	}
	
	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(final double[] x, final int xLength, final float[] a, final int aLength, final double y) {
		final double[] ret; 
		final int minProdLength = aLength; 
		final int minLength; 
		if (xLength > minProdLength) {
			minLength = minProdLength; 
			ret = new double[xLength]; 
			System.arraycopy(x, minProdLength, ret, minProdLength, xLength - minProdLength);
		} else {
			minLength = xLength; 
			ret = new double[minProdLength];
			MUL(ret, a, y, xLength, minProdLength);
		}
		return ADD_PROD(ret, x, a, y, 0, minLength); 
	
	}
	
	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(final double[] x, final double[] a, final double[] y) {
		return ADD_PROD(x, x.length, a, a.length, y, y.length); }

	/**  Linear Mapping: x + a*y	 */
	final static public double[] ADD_PROD(final double[] x, final int xLength, final double[] a, final int aLength, final double[] y, final int yLength) {
		final double[] ret; 
		final int minProdLength = Math.min(aLength, yLength); 
		final int minLength; 
		if (xLength > minProdLength) {
			minLength = minProdLength; 
			ret = new double[xLength]; 
			System.arraycopy(x, minProdLength, ret, minProdLength, xLength - minProdLength);
		} else {
			minLength = xLength; 
			ret = new double[minProdLength];
			MUL(ret, a, y, xLength, minProdLength);
		}
		return ADD_PROD(ret, x, a, y, 0, minLength); 
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Linear Mapping in Place over an Element Range: x -= a*y, with a an Array and y a scalar.
	  * @return Linear Mapping in Place: x -= a*y	 */
	final static public double[] SUB_PROD_AT(double[] ret, double[] a, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= a[stop] * y; }
		return ret;
	}

	/** Linear Mapping in Place over an Element Range: x -= a*y, with a and y both Arrays.
	  * @return Linear Mapping in Place: x -= a*y	 */
	final static public double[] SUB_PROD_AT(double[] ret, double[] a, double[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= a[stop] * y[stop]; }
		return ret;
	}

	/**  Linear Mapping in Place: x - a*y	 */
	final static public double[] SUB_PROD(double[] ret, double[] x, double[] a, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] - a[stop] * y; }
		return ret;
	}

	/**  Linear Mapping in Place: x - a*y	 */
	final static public double[] SUB_PROD(double[] ret, double[] x, double[] a, double[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] - a[stop] * y[stop]; }
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Linear Mapping in Place over an Element Range: x -= a*y, with a a scalar and y an Array.
	  * @return Linear Mapping in Place: x -= a*y	 */
	final static public double[] SUB_PROD_AT(double[] ret, double a, double[] y, int start, int stop) {
		return SUB_PROD_AT(ret, y, a, start, stop); }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public double[] SUB_PROD_AT(double[] ret, double a, double[] y) {
		return SUB_PROD_AT(ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public double[] SUB_PROD_AT(double[] ret, double[] a, double y) {
		return SUB_PROD_AT(ret, a, y, 0, ret.length); }

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public double[] SUB_PROD_AT(double[] ret, double[] a, double[] y) {
		return SUB_PROD_AT(ret, a, y, 0, ret.length); }
		
	/////////////////////////////////////////////////////////////////////////////////////

	/**  Linear Mapping: x - a*y	 */
	final static public double[] SUB_PROD(double[] ret, double[] x, double a, double[] y, int start, int stop) {
		return SUB_PROD(ret, x, y, a, start, stop); }

	/**  Linear Mapping: x - a*y	 */
	final static public double[] SUB_PROD(double[] ret, double[] x, double a, double[] y) {
		return SUB_PROD(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public double[] SUB_PROD(double[] ret, double[] x, double[] a, double y) {
		return SUB_PROD(ret, x, a, y, 0, ret.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public double[] SUB_PROD(double[] ret, double[] x, double[] a, double[] y) {
		return SUB_PROD(ret, x, a, y, 0, ret.length); }
		
	/////////////////////////////////////////////////////////////////////////////////////

	/**  Linear Mapping: x - a*y	 */
	final static public double[] SUB_PROD(double[] x, double a, double[] y, int start, int stop) {
		return SUB_PROD(new double[stop], x, y, a, start, stop); }

	/**  Linear Mapping: x - a*y	 */
	final static public double[] SUB_PROD(double[] x, double a, double[] y) {
		return SUB_PROD(new double[x.length], x, a, y, 0, x.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public double[] SUB_PROD(double[] x, double[] a, double y) {
		return SUB_PROD(new double[x.length], x, a, y, 0, x.length); 
	}

	/**  Linear Mapping: x - a*y	 */
	final static public double[] SUB_PROD(final double[] x, final double[] a, final double[] y) {
		return SUB_PROD(x, x.length, a, a.length, y, y.length); }

	/**  Linear Mapping: x - a*y	 */
	final static public double[] SUB_PROD(final double[] x, final int xLength, final double[] a, final int aLength, final double[] y, final int yLength) {
		final double[] ret; 
		final int minProdLength = Math.min(aLength, yLength); 
		final int minLength; 
		if (xLength > minProdLength) {
			minLength = minProdLength; 
			ret = new double[xLength]; 
			System.arraycopy(x, minProdLength, ret, minProdLength, ret.length - minProdLength);
		} else {
			minLength = xLength; 
			ret = new double[minProdLength];
			MUL(ret, a, y, xLength, ret.length);
			NEG_AT(ret, xLength, ret.length);
		}
		return SUB_PROD(ret, x, a, y, 0, minLength); } 

	/////////////////////////////////////////////////////////////////////////////////////

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(double[] ret, double a1, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a1 + y; }
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(double[] ret, double a1, double[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a1 + y[stop]; }
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(double[] ret, double[] a1, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a1[stop] + y; }
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(double[] ret, double[] a1, double[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = ret[stop] * a1[stop] + y[stop]; }
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(final double[] ret, final double a, final double y) {
		return LIN_AT(ret, ret.length, a, y); }
	
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(final double[] ret, final int retLength, final double a, final double y) {
		return LIN_AT(ret, a, y, 0, ret.length); 
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(final double[] ret, final double[] a, final double y) {
		return LIN_AT(ret, ret.length, a, a.length, y); }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(final double[] ret, final int retLength, final double[] a, final int aLength, final double y) {
		final int minProdLength;
		final int maxProdLength;
		if (aLength > retLength) {
			if (aLength > ret.length) {
				return LIN(ret, retLength, a, aLength, y); }
			minProdLength = retLength;
			maxProdLength = aLength; 
		} else {
			minProdLength = aLength;
			maxProdLength = retLength; 
		}
		FILL_AT(ret, y, minProdLength, maxProdLength);
		return LIN_AT(ret, a, y, 0, minProdLength); 
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(final double[] ret, final double a, final double[] y) {
		return LIN_AT(ret, ret.length, a, y, y.length); }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(final double[] ret, final int retLength, final double a, final double[] y, final int yLength) {
		final int minProdLength = retLength;
		final int minLength; 
		if (yLength > minProdLength) {
			minLength = minProdLength; 
			if (yLength > ret.length) {
				return LIN(ret, retLength, a, y, yLength); }
			COPY(y, minProdLength, yLength, ret);
		} else {
			minLength = yLength; 
			MUL_AT(ret, a, minLength, minProdLength); 
		}
		return LIN_AT(ret, a, y, 0, minLength); 
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(final double[] ret, final double[] a, final double[] y) {
		return LIN_AT(ret, ret.length, a, a.length, y, y.length); }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN_AT(final double[] ret, final int retLength, final double[] a, final int aLength, final double[] y, final int yLength) {
		final int minProdLength = Math.min(retLength, aLength);
		final int minLength; 
		if (yLength > minProdLength) {
			minLength = minProdLength; 
			if (yLength > ret.length) {
				return LIN(ret, retLength, a, aLength, y, yLength); }
			COPY(y, minProdLength, yLength, ret);
		} else {
			minLength = yLength; 
			MUL_AT(ret, a, minLength, minProdLength); 
		}
		return LIN_AT(ret, a, y, 0, minLength); 
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**  Linear Mapping: x*=a + y	*/
	final static public double[] LIN(final double[] ret, final double[] x, final double a, final double y) {
		return LIN(ret, x, a, y, 0, x.length); }
	
	/**  Linear Mapping: x*=a + y	*/
	final static public double[] LIN(final double[] ret, final double[] x, final double a, final double y, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop]*a + y; }
		return ret;
	}
	
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN(final double[] ret, final double[] x, double[] a, final double y) {
		return LIN(ret, x, a, y, 0, x.length); } //TODO: respect the individual Lengths of the Arrays
	
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN(final double[] ret, final double[] x, double[] a, final double y, final int start, int stop) {
		while (--stop >= start) { 
			ret[stop] = x[stop] * a[stop] + y; }
		return ret;
	}
		
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN(final double[] ret, final double[] x, final double a, final double[] y, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a + y[stop]; }
		return ret;
	}
		
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN(final double[] ret, final double[] x, final double[] a1, final double[] y, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a1[stop] + y[stop]; }
		return ret;
	}
		
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN(final double[] x, final double a1, final double y) {
		return LIN(new double[x.length], x, a1, y, 0, x.length); 
	}
		
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN(final double[] x, final double[] a, final double y) {
		return LIN(x, x.length, a, a.length, y); }
		
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN(final double[] x, final int xLength, final double[] a, final int aLength, final double y) {
		final double[] ret; 
		final int minLength; 
		if (xLength > aLength) {
			minLength = aLength; 
			ret = new double[xLength];
		} else {
			minLength = xLength; 
			ret = new double[aLength];
		}
		FILL_AT(ret, y, minLength, ret.length); //not well defined! 
		return LIN(ret, x, a, y, 0, minLength); 
	}
		
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN(final double[] x, final double a, final double[] y) {
		return LIN(x, x.length, a, y, y.length); }
		
	/**  Linear Mapping in Place: x*=a + y	*/
	final static public double[] LIN(final double[] x, final int xLength, final double a, final double[] y, final int yLength) {
		final double[] ret; 
		final int minLength; 
		if (xLength > yLength) {
			minLength = yLength; 
			ret = new double[xLength];
			MUL(ret, x, a, minLength, ret.length);
		} else {
			minLength = xLength; 
			ret = new double[yLength];
			System.arraycopy(y, minLength, ret, minLength, ret.length - minLength);
		}
		return LIN(ret, x, a, y, 0, minLength); 
	}
		
	/**  Linear Mapping in Place: x*=a + y
	 * most complex Case...
	 */
	final static public double[] LIN(final double[] x, final double[] a, final double[] y) {
		return LIN(x, x.length, a, a.length, y, y.length); }
		
	/**  Linear Mapping in Place: x*=a + y
	 * most complex Case...
	 */
	final static public double[] LIN(final double[] x, final int xLength
	, final double[] a, final int aLength, final double[] y, final int yLength) {
		final double[] ret; 
		final int minProdLength = Math.min(aLength, xLength); 
		final int minLength; 
		if (yLength > minProdLength ) {
			minLength = minProdLength ; 
			ret = new double[yLength];
			System.arraycopy(y, minLength, ret, minLength, ret.length - minLength);
		} else {
			minLength = yLength; 
			ret = new double[minProdLength];
			MUL(ret, x, a, minLength, ret.length);
		}
		return LIN(ret, x, a, y, 0, minLength);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] ret, final double[] x, final double[] a, final double[] y,
		final double[] b, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a[stop] + y[stop] * b[stop]; }
		return ret;
	}

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] ret, final double[] x, final double[] a, final double[] y,
		final double b, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a[stop] + y[stop] * b; }
		return ret;
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] ret, final double[] x, final double a, final double[] y,
		final double[] b, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a + y[stop] * b[stop]; }
		return ret;
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] ret, double[] x, double a, double[] y, double b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = x[stop] * a + y[stop] * b; }
		return ret;
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final  double[] ret, final double[] x, final double[] a, final double y,
		final double[] b, final int start, final int stop) {
		return BI_LIN(ret, x, a, b, y, start, stop);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] ret, final double[] x, final double a, final double[] y, final double b) {
		return BI_LIN(ret, x, x.length, a, y, y.length, b); }
	
	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] ret, final double[] x, final int xLength, final double a
	, final double[] y, final int yLength, final double b) {
		final int minLength; 
		if (yLength > xLength) {
			minLength = xLength; 
			if((ret == null) || (ret.length < yLength)) { 
				ret = new double[yLength]; } 
			MUL(ret, y, b, minLength, yLength);
		} else {
			minLength = yLength; 
			if((ret == null) || (ret.length < xLength)) { 
				ret = new double[xLength]; }
			MUL(ret, x, a, minLength, xLength);
		}
		return BI_LIN(ret, x, a, y, b, 0, minLength); 
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] ret, final double[] x, final double[] a, final double[] y, final double b) {
		return BI_LIN(ret, x, x.length, a, a.length, y, y.length, b); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] ret, final double[] x, final int xLength, final double[] a, final int aLength, final double[] y, final int yLength, final double b) {
		final int minProd1Length = Math.min(aLength, xLength); 
		final int minLength; 
		if (yLength > minProd1Length ) {
			minLength = minProd1Length ; 
			if((ret == null) || (ret.length < yLength)) { 
				ret = new double[yLength]; } 
			MUL(ret, y, b, minLength, yLength);
		} else {
			minLength = yLength; 
			if((ret == null) || (ret.length < minProd1Length)) { 
				ret = new double[minProd1Length]; }
			MUL(ret, x, a, minLength, minProd1Length);
		}
		return BI_LIN(ret, x, a, y, b, 0, minLength);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] ret, final double[] x, final double[] a, final double[] y, final double[] b) {
		return BI_LIN(ret, x, x.length, a, a.length, y, y.length, b, b.length); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] ret, final double[] x, final int xLength
	, final double[] a, final int aLength, final double[] y, final int yLength, final double[] b, final int bLength) {
		final int minProd1Length = Math.min(aLength, xLength); 
		final int minProd2Length = Math.min(bLength, yLength); 
		final int minLength; 
		if (minProd2Length > minProd1Length ) {
			minLength = minProd1Length ; 
			if((ret == null) || (ret.length < minProd2Length)) {
				ret = new double[minProd2Length]; } 
			MUL(ret, y, b, minLength, minProd2Length);
		} else {
			minLength = minProd2Length; 
			if((ret == null) || (ret.length < minProd1Length)) {
				ret = new double[minProd1Length]; } 
			MUL(ret, x, a, minLength, minProd1Length);
		}
		return BI_LIN(ret, x, a, y, b, 0, minLength);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] ret, double[] x, double a, double[] y, double[] b) {
		return BI_LIN(ret, x, a, y, b, 0, ret.length); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] ret, double[] x, double[] a, double y, double[] b) {
		return BI_LIN(ret, x, a, y, b, 0, ret.length); }
	
	/////////////////////////////////////////////////////////////////////////////////////

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] x, double[] a, double[] y, double[] b, int start, int stop) {
		return BI_LIN(new double[x.length], x, a, b, y, start, stop); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] x, double[] a, double[] y, double b, int start, int stop) {
		return BI_LIN(new double[x.length], x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] x, double a, double[] y, double[] b, int start, int stop) {
		return BI_LIN(new double[x.length], x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] x, double a, double[] y, double b, int start, int stop) {
		return BI_LIN(new double[x.length], x, a, y, b, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(double[] x, double[] a, double y, double[] b, int start, int stop) {
		return BI_LIN(new double[x.length], x, a, b, y, start, stop); 
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/** BiLinear Mapping in Place: x*a + y*b */
	final static public double[] BI_LIN(final double[] x, final double a, final double[] y, final double b) {
		return BI_LIN(x, x.length, a, y, y.length, b); }
	
	/** BiLinear Mapping in Place: x*a + y*b */
	final static public double[] BI_LIN(final double[] x, final int xLength, final double a
	, final double[] y, final int yLength, final double b) {
		return BI_LIN(null, x, xLength, a, y, yLength, b); 
	}
	
	/** BiLinear Mapping in Place: x*a + y*b */
	final static public double[] BI_LIN(final double[] x, final double[] a, final double[] y, final double b) {
		return BI_LIN(x, x.length, a, a.length, y, y.length, b); }
	
	/** BiLinear Mapping in Place: x*a + y*b */
	final static public double[] BI_LIN(final double[] x, final int xLength, final double[] a, final int aLength
	, final double[] y, final int yLength, final double b) {
		return BI_LIN(null, x, xLength, a, aLength, y, yLength, b); 
	}
	
	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] x, final double[] a, final double[] y, final double[] b) {
		return BI_LIN(x, x.length, a, a.length, y, y.length, b, b.length); }
	
	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] x, final int xLength, final double[] a, final int aLength
	, final double[] y, final int yLength, final double[] b, final int bLength) {
		return BI_LIN(null, x, xLength, a, aLength, y, yLength, b, bLength); 
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] x, final double a, final double[] y, final double[] b) {
		return BI_LIN(y, b, x, a); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN(final double[] x, final double[] a, final double y, final double[] b) {
		return BI_LIN(x, a, b, y); }
	
	/////////////////////////////////////////////////////////////////////////////////////

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(double[] ret, double[] a, double[] y, double b, int start, int stop) {
		return BI_LIN(ret, ret, a, b, y, start, stop); } //using BI_LIN, because no Optimization possible by working in Place

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(double[] ret, double a, double[] y, double[] b, int start, int stop) {
		return BI_LIN(ret, ret, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(double[] ret, double a, double[] y, double b, int start, int stop) {
		return BI_LIN(ret, ret, a, y, b, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(double[] ret, double[] a, double y, double[] b, int start, int stop) {
		return BI_LIN(ret, ret, a, b, y, start, stop); }
		
	/////////////////////////////////////////////////////////////////////////////////////

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(double[] ret, double a, double[] y, double b) {
		return BI_LIN_AT(ret, ret.length, a, y, y.length, b); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(final double[] ret, final int retLength
	, final double a, final double[] y, final int yLength, final double b) {
		return BI_LIN(ret, ret, retLength, a, y, yLength, b); }
	
	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(double[] ret, double[] a, double[] y, double b) {
		return BI_LIN_AT(ret, ret.length, a, a.length, y, y.length, b); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(final double[] ret, final int retLength
	, final double[] a, final int aLength, final double[] y, final int yLength, final double b) {
		return BI_LIN(ret, ret, retLength, a, aLength, y, yLength, b); }
	
	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(double[] ret, double[] a, double[] y, double[] b) {
		return BI_LIN_AT(ret, ret.length, a, a.length, y, y.length, b, b.length); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(final double[] ret, final int retLength
	, final double[] a, final int aLength, final double[] y, final int yLength, final double[] b, final int bLength) {
		return BI_LIN(ret, ret, retLength, a, aLength, y, yLength, b, bLength); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(double[] ret, double a, double[] y, double[] b) {
		return BI_LIN_AT(ret, ret.length, a, y, y.length, b, b.length); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(double[] ret, double[] a, double y, double[] b) {
		return BI_LIN_AT(ret, ret.length, a, a.length, b, b.length, y); } //commutative
	
	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(final double[] ret, final int retLength
	, final double a, final double[] y, final int yLength, final double[] b, final int bLength) {
		return BI_LIN(ret, y, yLength, b, bLength, ret, retLength, a); }
	
	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(final double[] ret, final double a, final double y, final double[] b) {
		return BI_LIN_AT(ret, ret.length, a, y, b, b.length); }
	
	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public double[] BI_LIN_AT(final double[] ret, final int retLength, final double a, final double y, final double[] b, final int bLength) {
		return BI_LIN(ret, ret, retLength, a, b, bLength, y); }
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** Subtracts the Part from a which lies parallel to the normed Vector arg (|arg| = 1).
	  * Used primarily in Orthogonalization.
	  * this -= arg*(arg*this)
	  */
	final static public double[] SUB_PART_AT(double[] a, double[] arg) {
		return SUB_PART_AT(a, arg, 1);
	}

	/** Subtracts the Part from a which lies parallel to the Vector arg.
	  * Used primarily in Orthogonalization.
	  * If argSqrNorm == null, it is assumed to be 1 (orthoNormal)
	  * this -= arg*((arg*this)/(arg*arg))
	  */
	final static public double[] SUB_PART_AT(double[] a, double[] arg, double argSqrNorm) {
		double Prod = MAP(a, arg, 0, arg.length) / argSqrNorm;
		SUB_PROD_AT(a, arg, Prod);
		return a;
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
	final static public double[] ROR_AT(final double[] vector, int length) {
		final double tmp = vector[--length]; 
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
	final static public int SHR_AT(final double[] vector, final int length, final int shift) {
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
	final static public int SHL_AT(final double[] vector, int length, final int shift) {
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
	final static public double[] ROL_AT(final double[] vector, int length) {
		final double tmp = vector[0];
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
	final static public void SUMM_AT(final double[] items) { SUMM_AT(items, 0, items.length); }
	
	/** calculates the (forward) Difference Vector in Place 
	 * This keeps the Vectors usable after Differentiation 
	 * and the leftover Elements can be reused on Integration. 
	 * 
	 * @param items the Vector to differentiate
	 */
	final static public void DIFF_AT(final double[] items) { DIFF_AT(items, 0, items.length-1); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** calculates the (forward) Difference Vector in Place 
	 * This keeps the Vectors usable after Differentiation 
	 * and the leftover Elements can be reused on Integration. 
	 * 
	 * @param items the Vector to differentiate
	 * @param start the first Index (exclusive)
	 * @param stop the last Index (exclusive)
	 */
	final static public void DIFF_AT(final double[] items, final int start, final int stop) {
		//double tmp1, tmp2 = a[0]; //Original
		//int i = 0; while (++i <= itemCount) {
		//	tmp1 = tmp2; tmp2 = a[i]; a[i] -= tmp1; }	//== a[i].subAt(a[i+1]);
		for (int i = start-1; ++i < stop;) { //this Order removes the LAST Item!
			items[i] -= items[i+1]; } //== items[i].subAt(items[i+1]);
	}
	
	/** Differentiates the Polynom represented by items in Place, one degree lower.
	 * @see streamIO.copy.group.ring.metric.body.vector.IManifold#diffAt()	 */
	final static public double[] DIFF_POLYNOM_AT(final double[] items, final int itemCount) {
		for (int i = 1; ++i < itemCount;) { //Skip multiplying Items 0 and 1
			items[i] *= i; }
		ROL_AT(items, itemCount);	//do a large Rotation right... (mind the opposite reading Direction!)
		return items; } //...preserving highest Item, don't set it to zero
			
	/**Returns the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	 * This is the reverse Operation to diffAt().
	 * The Integral Polynom has one Item more than the original Polynom.
	 * This is either restored from the highest Element
	 * or assumed to Zero. 	 */
	final static public double[] SUMM_POLYNOM_AT(final double[] items, final int itemCount)	{	//first do the Rotation, then the Multiplication, thus a[i]*=i
		ROR_AT(items, itemCount);	//do a large Rotation left (mind the opposite reading Direction!)
		for (int i = 1; ++i < itemCount;) { 
			items[i] /= i;} 
		return items; }
	
	/** Summen werden r�ckw�rts gebildet! 
	 * Dadurch werden die von der Diff-Operation �brig gebliebenen Elemente widerverwendet 
	 * und die Vektoren bleiben einsatzbereit. 
	 * 
	 * @param items
	 * @param start
	 * @param stop
	 */
	final static public void SUMM_AT(final double[] items, final int start, final int stop) {
		double tmp1 = 0; 
		for (int i = stop;  --i >= start;  ) {
			tmp1 = items[i] += tmp1; }	//items[i].addAt(items[i+1]);
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	// statistical Methods
	///////////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * returns the Counts of the different Values e.g. for a Histogram
	 * @param ret the counts (for Incrementation if already counted)
	 * @param a the Values to count (Values outside the Bounds are not counted) 
	 * @return the Counts of the different Values e.g. for a Histogram
	 */
	final static public int[] COUNT(final double[] a, int numBins) {
		return COUNT(numBins, a, 0, a.length); }
	
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
	final static public int[] COUNT(int numBins, final double[] a, final int start, final int stop) {
		final double[] minMax = VectorDouble.MIN_MAX_VAL(a, start, stop); 
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
	final static public int[] COUNT(int[] ret, int numBins, final double[] a, final int start, 
			final int stop, final double min, final double max) {
		if (ret == null)
			ret  = new int[numBins];
		else
			numBins = ret.length; 
		final double scale = numBins/(max - min); 
		for(int i = stop; --i >= start;) {
			final double val = a[i]; 
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
	final static public double MOMENT(final double[] items, final int moment, final int start, final int stop, final double mean) {
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
	 * @param moments the List of Moments to be filled by this Routine 
	 * starting from 
	 * moments[0] = #Items
	 * moments[1] = Absolute Deviation
	 * moments[2] = Squared Deviation = Variation 
	 * moments[3] = cubed Deviation = Skewness
	 * moments[4] = quadrupled Deviation = Curtosis
	 * @return the Mean of this Distribution 	 
	 */
	/** Fills moments with the raw central Moments of an Element Range of items about their Mean.
	 * @param moments the List of Moments to be filled by this Routine
	 * starting from
	 * moments[0] = #Items
	 * moments[1] = Absolute Deviation
	 * moments[2] = Squared Deviation = Variation
	 * moments[3] = cubed Deviation = Skewness
	 * moments[4] = quadrupled Deviation = Curtosis
	 * @return the Mean of this Distribution
	 */
	final static public double MOMENTS(final double[] items, final double[] moments, final int start, int stop) {
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
			
	/** Computes the sample Covariance between the first length Elements of two Distributions, given both means.
	 * @return the Covariance of the two given Distributions	 */
	final static public double COVARIANCE(final int length,
	final double[] items1, final double mean1,
	final double[] items2, final double mean2) {
		double cov = 0;
		for (int i = length; --i >= 0;) {
			cov +=(items1[i]-mean1)*(items2[i]-mean2); }
		return cov/(length-1); }

	/** Computes the sample Covariance between two equally-sized Distributions, given both means.
	 * @return the Covariance of the two given Distributions	 */
	final static public double COVARIANCE(
			final double[] items1, final double mean1,
			final double[] items2, final double mean2) {
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
		double[] in_Origin,
		double[] in_OriginWidth,
		double[] outDestin,
		double[] outDestinWidth) {
		DIV_AT(outDestinWidth, in_OriginWidth);
		SUB_PROD_AT(outDestin, outDestinWidth, in_Origin);
	}

	/** Maps ret in Place from the Unity Cube to the Cube with the given Origin and Width, using the precalculated Transform.
	 * @return the given Vector mapped from the Unity Cube
	 * to the Cube with the given Origin and Width.
	 *
	 * @see graphic.math3D.Line which encodes this Mapping into an Object.
	 */
	final static public double[] AFFINE_MAP_AT(double[] ret, double[] Origin, double[] Width) {
		return LIN_AT(ret, Width, Origin);
	} //a*x+b = y

	/**
	 * Not very effective Implementation of affine Mapping:
	 * all Transformations are performed explicitly
	 * resulting in double Work:
	 * DV = (OV-O)/OW*DW+OD
	 * DV = OV*W + (OD-O*W) with W = DW/OW
	 *
	 * @see graphic.math3D.Line which encodes this Mapping into an Object.
	 */
	final static public double[] AFFINE_MAP_AT(
		double[] ret,
		double[] Origin,
		double[] OriginWidth,
		double[] Destin,
		double[] DestinWidth) {
		SUB_AT(ret, Origin);
		DIV_AT(ret, OriginWidth);
		LIN_AT(ret, DestinWidth, Destin);
		return ret;
	} //

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods for ad hoc Adressing convoluted Arrays
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fastest Method to access a List as a Matrix:
	 * @param base  the backing Array of this Tensor
	 * @param Cols  the Number of Columns in each Sheet of this Tensor
	 * @param Row   the Row    for the Element of this Tensor
	 * @param Col   the Column for the Element of this Tensor
	 * @return the Element of the Vector interpreted as a Matrix	 */
	final static public double MATRIX(double[] base, int Cols, int Row, int Col) {
		if ((Col >= Cols) || (Col < 0)) {
			throw new IndexOutOfBoundsException("Range: 0.." + Cols + " Actual: " + Col);
		}
		return base[Row * Cols + Col];
	}

	/**
	 * Fastest Method to access a List as a Tensor:
	 * @param base  the backing Array of this Tensor
	 * @param Rows  the Number of Rows in each Sheet of this Tensor
	 * @param Cols  the Number of Columns in each Sheet of this Tensor
	 * @param Sheet the Sheet  for the Element of this Tensor
	 * @param Row   the Row    for the Element of this Tensor
	 * @param Col   the Column for the Element of this Tensor
	 * @return the Element of the Vector interpreted as a Tensor
	 */
	final static public double TENSOR(double[] base, int Rows, int Cols, int Sheet, int Row, int Col) {
		if ((Col >= Cols) || (Col < 0)) {
			throw new IndexOutOfBoundsException("Range: 0.." + Cols + " Actual: " + Col);
		}
		if ((Row >= Rows) || (Row < 0)) {
			throw new IndexOutOfBoundsException("Range: 0.." + Rows + " Actual: " + Row);
		}
		return base[(Sheet * Rows + Row) * Cols + Col];
	}

	/**
	 * Fastest Method to access a List as an n-dimensional Tensor:
	 * @param base the backing Array of this Tensor
	 * @param Cols the Multi-Index for the Dimensions of this Tensor
	 * @param Col  the Multi-Index for this Tensor
	 * @return the Element of the Vector interpreted as a Tensor
	 */
	final static public double TENSOR(double[] base, int[] Cols, int[] Col) {
		int i = Col.length;
		int ndx = Col[--i]; //0; //saves 1 Iteration
		while (--i >= 0) {
			int colsi = Cols[i];
			int coli = Col[i];
			if ((coli > colsi) || (coli < 0)) {
				throw new IndexOutOfBoundsException("Range: 0.." + colsi + " Actual: " + coli);
			}
			ndx *= colsi;
			ndx += coli;
		}
		return base[ndx];
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Array Conversion of primitive and Object Types
	////////////////////////////////////////////////////////////////////////////////
			
	/**Converts an Array of simple Type Contstants
	 * into an Array of the corresponding Object Type
	 * could be programmed slower but more generic using the Reflection API!	 */
	final static public Double[] const2Const(double[] arg) {
		int len = arg.length;
		Double[] ret = new Double[len];
		while (--len >= 0)
			ret[len] = new Double(arg[len]);
		return ret; }
		
	/**Converts the float[] Array to a double[] Array	 */
	final static public double[] float2double(float[] data) {
		int i = data.length;
		double[] dData = new double[i];
		while (--i >= 0) {
			dData[i] = data[i]; } 
		return dData; }
		
	/**Converts the Array IMeasurAble[] to double[]	  */
	final static public double[] GET_DOUBLES(final IMeasurAble[] arg) {
		int i;
		double[] ret = new double[i = arg.length];
		while (--i >= 0) {
			ret[i] = arg[i].getDouble();
		}
		return ret; }
		
	/**Converts the Array IMeasurAble[] to double[]	  */
	final static public double[] GET_DOUBLES(Number[] arg) {
		int i;
		double[] ret = new double[i = arg.length];
		while (--i >= 0) {
			ret[i] = arg[i].doubleValue();
		}
		return ret; }
		
	/**Converts the Array Object[] to double[]	  */
	final static public double[] GET_DOUBLES(final Object[]  arg) {
		if (arg instanceof IMeasurAble[])
			return VectorDouble.GET_DOUBLES((IMeasurAble[]) arg);
			return VectorDouble.GET_DOUBLES((Number[]    ) arg); }
			
			
	/** 
	 * Function to simulate the generic Meaning 
	 * of safely querying an Array for a Value out of Range. 
	 * An Alternative would be 
	 * to return NaN for negative or too high Indices,  
	 * @param i the Index to evaluate 
	 * @param arg the Array to read
	 * @return the Value at the given Index, 0 otherwise. 
	 */
	final static public double GET_AT(final int i, final double[] arg) {
		if (arg == null) {
			return 0; }
		if ((arg.length > i) && (i >= 0)){
		return arg[i]; }
		return 0; 
	}
		
			
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Backing Value Array for the Tensor	 */
	protected double[] items;

	/** Returns the backing Array of Item Values, either directly or as a defensive Copy.
	 * @param original Returns the internal Structure by Reference!
	 * Should usually be false(Default), except when it is guaranteed,
	 * that the Array will be used Read-Only.
	 * @return the Items	 */
	public double[] getItems(final boolean original) {
		if (original) {
			return items; }
		return COPY(items, new double[itemCount]);
	}

	/** Returns a defensive Copy of the backing Array of Item Values.
	 * @return the Items	 */
	public double[] getItems() { return getItems(false); }

	/** Zero-padding String reused when formatting a Number to a fixed width. */
	final static public String STR_ZEROS = "0000000000000000000";

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX) 
	/// for multidimensional rectangular Arrays 
	////////////////////////////////////////////////////////////////////////////////

	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized double getDoubleAt(final int index) {
		if (indexInRange(index)) 
			return items[index]; 
		return 0;
	}

	/** Returns the Item at the given Position boxed as an Object, or null when the Position is out of range.
	 * @return the item at the given Position as an Object */
	public Object getAt(final int i) {
		if (!indexInRange(i)) { return null; }
		return new ByRefDouble(getDoubleAt(i));
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
	public double setAt(final int index, final double value) {
		double ret = 0; //Double.NaN; 
		if (indexInRange(index))
			ret = items[index]; 
		else {
			if (value == 0)
				return   0; 
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
		return new ByRefDouble(setAt(index, ByRefDouble.GET_DOUBLE(value))); 
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
	public void insertAt(final int index, final double value) {
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
	public double removeAt(final int index) {
		if (index < 0 || index > itemCount - 1)  //
			return 0;
		--itemCount;
		final double ret = items[index]; 
		System.arraycopy(items, index+1, items, index, itemCount-index); 
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX) 
	/// for multidimensional rectangular Arrays 
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the Value of a 2-dimensional (Row, Col) Position mapped onto the flat backing Array.
	 * @return the Value at the given Position	 */
	public double getAt(int Row, int Col) {
		return items[Row * dimFactors[0] + Col * dimFactors[1]];
	}

	/** sets the given Value 	 */
	public void setAt(int Row, int Col, double Value) {
		items[Row * dimFactors[0] + Col * dimFactors[1]] = Value;
	}

	/** Returns the Value of a 3-dimensional (Sheet, Row, Col) Position mapped onto the flat backing Array.
	 * @return the Value at the given Position	 */
	public double getAt(int Sheet, int Row, int Col) {
		return items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]];
	}

	/** sets the given Value 	 */
	public void setAt(int Sheet, int Row, int Col, double Value) {
		items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]] = Value;
	}

	/** Returns the Value of an arbitrary-dimensional Position mapped onto the flat backing Array.
	 * @return the Value at the given multidimensional Position	 */
	public double getAt(int[] Col) { return items[multiIndex(Col)]; }
	
	/** sets the given Value at the given multidimensional Position	 */
	public void setAt(int[] Col, double Value) { items[multiIndex(Col)] = Value; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/**Constructs an empty VectorInt with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the VectorInt.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the VectorInt overflows.	 */
	public VectorDouble(int initialCapacity, int capacityIncrement_) {
		super();
		items = new double[initialCapacity];
		capacityIncrement = capacityIncrement_;
		//mEnum = new ArrayEnum(Items, ItemCount);
		//mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty VectorDouble with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the VectorDouble.	 */
	public VectorDouble(int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty VectorDouble.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public VectorDouble() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/** Constructs an VectorDouble by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public VectorDouble(Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an VectorDouble from the given Object.	  */
	public VectorDouble(Object arg, int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorDouble from the given Object
	  * and copies the Elements into this VectorDouble.	  */
	public VectorDouble(final double[] arg) {
		this(arg, true, arg.length); } //rather be safe! can use the Parameter later!
		
	/** Constructs an VectorDouble from the given Object
	  * and copies the Elements into this VectorDouble.	  */
	public VectorDouble(final double[] arg, boolean copy) {
		this(arg, copy, arg.length);
	}
	
	/** Constructs an VectorDouble from the given Object.	  */
	public VectorDouble(final double[] arg, boolean copy, int itemCount_) {
		this(arg, copy, itemCount_, DEFAULT_CAPACITY_INCR);
	}
	
	/** Constructs an VectorDouble from the given Object.	  */
	public VectorDouble(final double[] arg, boolean copy, int itemCount_, int capacityIncrement_) {
		this.capacityIncrement = capacityIncrement_; 
		if (copy) {
			copyAt(arg, itemCount_);
		} else {
			this.items = arg; 
			this.itemCount = itemCount_; 
		}
	}
	
	//////////////////////////////
	//	Sampling of a Function	//
	//////////////////////////////
	
	/**Generates a Manifold by sampling f across x	 */
	public VectorDouble(final IFloatFunction f, final VectorDouble x) {	//preserve Internals of x0
		this (f, x.items, x.itemCount); }
		
	/**Generates a Manifold by sampling f across x	 */
	public VectorDouble(final IFloatFunction f, final VectorDouble x, final int xLen) {	//preserve Internals of x0
		this (f, x.items, xLen); }
		
	/**Generates a Manifold by sampling f across x	 */
	public VectorDouble(final IFloatFunction f, final double[] x, final int xLen) {	//preserve Internals of x0
		this (SAMPLE(f, x, xLen)); }
		
	/** Samples the Function f on the Interval [x0, x0+Grad*dx]	 */
	public VectorDouble(final IFloatFunction f, double x0, final double dx, final int xLen) {	//preserve Internals of x0
		this (SAMPLE(f, x0, dx, xLen)); }
	
	/** Creates an equidistant Raster on the Interval [x0, x0+Grad*dx]	 */
	public VectorDouble(double x0, final double dx, final int xLen) {
		this (new double[xLen]); x0 -= dx; //Compensation
		for (int i = xLen; --i >= 0; ) { 
			items[i] = (x0 += dx); }
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorDouble addItem(final double item) {
		setAt(itemCount, item);
		return this;
	}

	/** Inserts the given Item into the List, also enlarges the List.	 */
	final public VectorDouble insertItemAt(final int pos, final double item) {
		if (pos >= itemCount) {
			setAt(pos, item); 
		} else {
			System.arraycopy(items, pos, items, pos+1, itemCount - pos);
			++itemCount;
		}
		items[pos] = item; 
		return this;
	}
		
	/** Inserts the given Item into the List, also enlarges the List.	 */
	final public double removeItemAt(final int pos) {
		if (pos >= itemCount) {
			return 0; } 
		final double ret = items[pos];
		--itemCount;
		System.arraycopy(items, pos+1, items, pos, itemCount - pos);
		return ret;
	}
		
	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	final public synchronized double[] copyInto(double[] arg) {
		if ((arg == null) || (arg.length < itemCount))
			arg = new double[itemCount]; 
		System.arraycopy(items, 0, arg, 0, itemCount);
		/*		int i = ItemCount;
				Object elementDataLocal[] = this.Items;
				while (i-- > 0)
					arg[i] = elementDataLocal[i];
		*/
		return arg; 
	}

	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.	 */
	final public synchronized double[] toArray() { return copyInto(null); }

	/**Trims the capacity of this VectorInt to be the VectorInt's current
	 * size. An application can use this operation to minimize the
	 * storage of a VectorInt.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			double[] oldData = items;
			items = new double[itemCount];
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
		final double[] oldData = items; items = new double[newCapacity];
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
	public VectorDouble copyAt(final double[] arg) {
		return copyAt(arg, arg.length); 
	}
	
	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 *
	 * The Optimization here is that the Capacity can be ensured before
	 * and that additional Fields can be set.	 */
	public VectorDouble copyAt(final double[] arg, final int argLength) {
		copyAt(arg, 0, argLength); 
		itemCount = argLength;
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
	public VectorDouble copyAt(final double[] arg, final int start, final int stop) {
		if ((items == null) || (itemCount < stop)) {
			if ((items == null) ||(items.length < stop)) {
				items = new double[stop]; }
			itemCount = stop; 
		}
		System.arraycopy(arg, start, items, start, stop-start);
		return this;
	}

	/** type-safe Copy Operation	 */
	public VectorDouble copyOrig() {
		return new VectorDouble(items, true, itemCount);
	}
	
	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 *
	 * The Optimization here is that the Capacity can be ensured before
	 * and that additional Fields can be set.	 */
	public VectorDouble copyAt(final VectorDouble arg) {
		capacityIncrement = arg.capacityIncrement;
		setCapacity(arg.itemCount); 
		itemCount = arg.itemCount; 
		System.arraycopy(arg.items, 0, items, 0, itemCount); //sharing is not reliable...
		return this;  //...because the Array is resized! 
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
		if (arg instanceof VectorDouble) {
			copyAt((VectorDouble) arg);
		} else
			super.copyAt(arg); //no need to use a recursive DeepCopy like with Tensor
		return this;
	}

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) {
		if (arg instanceof VectorDouble) {
			VectorDouble arg_ = (VectorDouble) arg;
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
		return new VectorDouble(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Arithmetic Methods for Arrays
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the smallest Value held by this Vector.
	 * @return the Minimum Value in this Vector	 */
	public double MinVal() { return MIN_VAL(items); }

	/** Returns the Index of the smallest Value held by this Vector.
	 * @return the Position of the Minimum Value in this Vector	 */
	public int MinPos() { return MIN_POS(items); }

	/** Returns the largest Value held by this Vector.
	 * @return the Maximum Value in this Vector	 */
	public double MaxVal() { return MAX_VAL(items); }

	/** Returns the Index of the largest Value held by this Vector.
	 * @return the Position of the Maximum Value in this Vector	 */
	public int MaxPos() { return MAX_POS(items); }

	/////////////////////////////////////////////////////////////////////////////////////

	/** Global Switch enabling automatic canonicalization; currently unused by this class's own Methods. */
	public static boolean CANONICALIZE = false; // = true;

	/** Normalizes this Vector by bringing it into the canonical Form
	 * so that getAt(getInt()) != 0 
	 * @return the final Length of this Vector. 
	 */
	public int canonicalizeAt() {
		//if (!CANONICALIZE) 
		//	return this; 
		final double cmp = items[0]; 
		while (--itemCount >= 0) {
			if(!ByRefDouble.IS_ZERO(items[itemCount], cmp)) {
				break; }
		}
		return ++itemCount;
	}

	/** negates the values of this Vector */
	public VectorDouble negAt() { NEG_AT(items, 0, itemCount); return this; }
	
	/** inverts the values of this Vector */
	public VectorDouble invAt() { INV_AT(items, 0, itemCount); return this; }
			
	/** inverts the values of this Vector */
	public VectorDouble FloorAt() { FLOOR_AT(items, 0, itemCount); return this; }
			
	/** adds the given Portion of the values to this Vector */
	public VectorDouble addAt(final VectorDouble vector) {
		return addAt(vector.items, vector.itemCount); }
	
	/** adds the given Portion of the values to this Vector */
	public VectorDouble add(final VectorDouble vector) {
		final VectorDouble ret = new VectorDouble(ADD(items, itemCount, vector.items, vector.itemCount));
		if (CANONICALIZE && (itemCount == vector.itemCount)) {
			ret.canonicalizeAt(); }
		return ret; }
	
	/** subtracts the given Portion of the values from this Vector */
	public VectorDouble subAt(final VectorDouble vector) {
		return subAt(vector.items, vector.itemCount); }

	/** subtracts the given Portion of the values from this Vector */
	public VectorDouble subt(final VectorDouble vector) {
		final VectorDouble ret = new VectorDouble(SUB(items, itemCount, vector.items, vector.itemCount)); 
		if (CANONICALIZE && (itemCount == vector.itemCount)) {
			ret.canonicalizeAt(); }
		return ret; }
	
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble mulAt(final VectorDouble vector) {
		return mulAt(vector.items, vector.itemCount); }
	
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble mul(final VectorDouble vector) {
		return new VectorDouble(MUL(items, vector.items, 0, Math.min(itemCount, vector.itemCount))); }
			
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble divAt(final VectorDouble vector) {
		return divAt(vector.items, vector.itemCount); }

	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble div(final VectorDouble vector) {
		return new VectorDouble(DIV(items, itemCount, vector.items, vector.itemCount)); }
				
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble LinAt(final VectorDouble a, final VectorDouble y) {
		return LinAt(a.items, a.itemCount, y.items, y.itemCount); }
	
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble LinAt(final double a, final VectorDouble y) {
		return LinAt(a, y.items, y.itemCount); }
		
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble LinAt(final VectorDouble a, final double y) {
		return LinAt(a.items, a.itemCount, y); }
		
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble Lin(final VectorDouble a, final VectorDouble y) {
		return new VectorDouble(LIN(items, itemCount, a.items, a.itemCount, y.items, y.itemCount)); }

	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble Lin(final double a, final VectorDouble y) {
		return new VectorDouble(LIN(items, itemCount, a, y.items, y.itemCount)); }
	
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble Lin(final VectorDouble a, final double y) {
		return new VectorDouble(LIN(items, itemCount, a.items, a.itemCount, y)); }
	
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble addProdAt(final VectorDouble a, final VectorDouble y) {
		return addProdAt(a.items, a.itemCount, y.items, y.itemCount); }
		
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble addProdAt(final double a, final VectorDouble y) {
		return addProdAt(a, y.items, y.itemCount); }
		
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble addProdAt(final VectorDouble a, final double y) {
		return addProdAt(a.items, a.itemCount, y); }
		
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble addProd(final VectorDouble a, final VectorDouble y) {
		return new VectorDouble(ADD_PROD(items, itemCount, a.items, a.itemCount, y.items, y.itemCount)); }
		
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble addProd(final double a, final VectorDouble y) {
		return new VectorDouble(ADD_PROD(items, itemCount, y.items, y.itemCount, a)); }
			
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble addProd(final VectorDouble a, final double y) {
		return new VectorDouble(ADD_PROD(items, itemCount, a.items, a.itemCount, y)); }
	
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble biLinAt(final VectorDouble a, final VectorDouble y, final VectorDouble b) {
		return biLinAt(a.items, a.itemCount, y.items, y.itemCount, b.items, b.itemCount); }
	
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble biLinAt(final double a, final VectorDouble y, final VectorDouble b) {
		return biLinAt(a, y.items, y.itemCount, b.items, b.itemCount); }
	
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble biLinAt(final VectorDouble a, final double y, final VectorDouble b) {
		return biLinAt(a.items, a.itemCount, y, b.items, b.itemCount); }
	
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble biLinAt(final VectorDouble a, final VectorDouble y, final double b) {
		return biLinAt(a.items, a.itemCount, y.items, y.itemCount, b); }
	
	/** divides this Vector by the given Portion of the vector*/
	public VectorDouble biLinAt(final double a, final VectorDouble y, final double b) {
		return biLinAt(a, y.items, y.itemCount, b); }
				
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble biLin(final VectorDouble a, final VectorDouble y, final VectorDouble b) {
		return new VectorDouble(BI_LIN(items, itemCount, a.items, a.itemCount, y.items, y.itemCount, b.items, b.itemCount)); }
		
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble biLin(final double a, final VectorDouble y, final VectorDouble b) { //addition is commutative
		return new VectorDouble(BI_LIN(y.items, y.itemCount, b.items, b.itemCount, items, itemCount, a)); }
			
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble biLin(final VectorDouble a, final double y, final VectorDouble b) {
		return new VectorDouble(BI_LIN(items, itemCount, a.items, a.itemCount, b.items, b.itemCount, y)); }
			
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble biLin(final VectorDouble a, final VectorDouble y, final double b) {
		return new VectorDouble(BI_LIN(items, itemCount, a.items, a.itemCount, y.items, y.itemCount, b)); }
	
	/** multiplies this Vector by the given Portion of the values */
	public VectorDouble biLin(final double a, final VectorDouble y, final double b) {
		return new VectorDouble(BI_LIN(items, itemCount, a, y.items, y.itemCount, b)); }
	
	/////////////////////////////////////////////////////////////////////////////////////
					
	/** subtracts the given Portion of the values from this Vector */
	public VectorDouble subAt(final float[] values, final int stop) {
		items = VectorDouble.SUB_AT(items, itemCount, values, stop);
		if (itemCount < stop) { //Most probable Test first
			itemCount = stop; 
		} else  
		if (itemCount == stop) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}

	/** subtracts the given Portion of the values from this Vector */
	public VectorDouble subAt(final double[] values, final int stop) {
		items = VectorDouble.SUB_AT(items, itemCount, values, stop);
		if (itemCount < stop) { //Most probable Test first
			itemCount = stop; 
		} else  
		if (itemCount == stop) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** adds the given Value to this Vector */
	public VectorDouble addAt(final double value) {
		VectorDouble.ADD_AT(items, value, 0, itemCount);
		if (CANONICALIZE) { //possibly eliminated the top Value(s)
			canonicalizeAt(); } 
		return this; }
	
	/** adds the given Value to this Vector */
	public VectorDouble add(final double value) {
		final VectorDouble ret = new VectorDouble(ADD(items, value, 0, itemCount));
		if (CANONICALIZE) { //possibly eliminated the top Value(s)
			ret.canonicalizeAt(); } 
		return ret; }

	/** subtracts the given Value from this Vector */
	public VectorDouble subAt(final double value) {
		VectorDouble.ADD_AT(items, -value, 0, itemCount);
		if (CANONICALIZE) { //possibly eliminated the top Value(s)
			canonicalizeAt(); } 
		return this; }
	
	/** subtracts the given Value from this Vector */
	public VectorDouble subt(final double value) {
		final VectorDouble ret = new VectorDouble(ADD(items, -value, 0, itemCount));
		if (CANONICALIZE) { //possibly eliminated the top Value(s)
			ret.canonicalizeAt(); } 
		return this; }
		
	/** multiplies this Vector by the given Values */
	public VectorDouble mulAt(final double value) {
		if (value == 0) {
			itemCount = 0; return this; }
		VectorDouble.MUL_AT(items, value, 0, itemCount); 
		return this; }
	
	/** multiplies this Vector by the given Values */
	public VectorDouble mul(final double value) {
		if (value == 0) {
			return null; }
		return new VectorDouble(MUL(items, value)); }
		
	/** divides this Vector by the given Value 	 */
	public VectorDouble divAt(final double value) {
		if (Double.isInfinite(value)) {
			itemCount = 0; return this; }
		VectorDouble.MUL_AT(items, 1/value, 0, itemCount); 
		return this; }

	/** divides this Vector by the given Value 	 */
	public VectorDouble div(final double value) {
		if (Double.isInfinite(value)) {
			return null; }
		return new VectorDouble(MUL(items, 1/value)); }
		
	/////////////////////////////////////////////////////////////////////////////////////

	/** adds the given Portion of the values to this Vector */
	public VectorDouble addAt(final float[] values, final int stop) {
		items = VectorDouble.ADD_AT(items, itemCount, values, stop);
		if (itemCount < stop) { //Most probable Test first
			itemCount = stop; 
		} else  
		if (itemCount == stop) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorDouble addAt(final double[] values, final int stop) {
		items = VectorDouble.ADD_AT(items, itemCount, values, stop);
		if (itemCount < stop) { //Most probable Test first
			itemCount = stop; 
		} else  
		if (itemCount == stop) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorDouble mulAt(final float[] values, final int stop) {
		if (itemCount > stop) {
			itemCount = stop; }
		items = VectorDouble.MUL_AT(items, values, 0, stop);
		//normalizeAt(); //don't need to normalize when both were before
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorDouble mulAt(final double[] values, final int stop) {
		if (itemCount > stop) {
			itemCount = stop; }
		items = VectorDouble.MUL_AT(items, values, 0, itemCount);
		//normalizeAt(); //don't need to normalize when both were before
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorDouble divAt(final float[] values, final int stop) {
		items = VectorDouble.DIV_AT(items, itemCount, values, stop);
		if (itemCount < stop) { //Most probable Test first
			itemCount = stop; } 
		//normalizeAt(); //don't need to normalize when both were before
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorDouble divAt(final double[] values, final int stop) {
		items = VectorDouble.DIV_AT(items, itemCount, values, stop);
		if (itemCount < stop) { //Most probable Test first
			itemCount = stop; } 
		if (itemCount > items.length) { //Case of more values than items
			itemCount = items.length; } 
		//normalizeAt(); //don't need to normalize when both were before
		return this;
	}
	
	/** linear Combination x *= a + y	 */
	public VectorDouble LinAt(final double[] a, final int aLength, final double[] y, final int yLength) {
		items = VectorDouble.LIN_AT(items, itemCount, a, aLength, y, yLength);
		if (itemCount > aLength) { //min(this, a) 
			itemCount = aLength; } //prodLength
		if (itemCount < yLength) { //Most probable Test first
			itemCount = yLength;   //Max(min(this, a), y)
		} else  
		if (itemCount == yLength) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}
	
	/** linear Combination x *= a + y	 */
	public VectorDouble LinAt(final double a, final double[] y, final int yLength) {
		items = VectorDouble.LIN_AT(items, itemCount, a, y, yLength);
		if (itemCount < yLength) { //Most probable Test first
			itemCount = yLength;   //Max(min(this, a), y)
		} else  
		if (itemCount == yLength) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}
		
	/** linear Combination x *= a + y	 */
	public VectorDouble LinAt(final double[] a, final int aLength, final double y) {
		items = VectorDouble.LIN_AT(items, itemCount, a, aLength, y);
		//itemCount does not change!
		//if (itemCount > aLength) { //min(this, a) 
		//	itemCount = aLength; } //prodLength
		if (CANONICALIZE) {
			canonicalizeAt(); }
		return this;
	}
	
	/** linear Combination x += a * y	 */
	public VectorDouble addProdAt(final double[] a, final int aLength, final double[] y, final int yLength) {
		items = VectorDouble.ADD_PROD_AT(items, itemCount, a, aLength, y, yLength);
		final int prodLength = Math.min(aLength, yLength); 
		if (itemCount < prodLength) { //Most probable Test first
			itemCount = prodLength; 
		} else  
		if (itemCount == prodLength) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}
	
	/** linear Combination x += a * y	 */
	public VectorDouble addProdAt(final double[] a, final int aLength, final double y) {
		items = VectorDouble.ADD_PROD_AT(items, itemCount, a, aLength, y);
		final int prodLength = aLength; 
		if (itemCount < prodLength) { //Most probable Test first
			itemCount = prodLength; 
		} else  
		if (itemCount == prodLength) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}
			
	/** linear Combination x += a * y	 */
	public VectorDouble addProdAt(final double a, final double[] y, final int yLength) {
		return addProdAt(y, yLength, a); } //commutative!
				
	/** linear Combination x = a * x + b * y	 */
	public VectorDouble biLinAt(final double[] a, final int aLength, final double[] y, final int yLength, final double[] b, final int bLength) {
		items = VectorDouble.BI_LIN_AT(items, itemCount, a, aLength, y, yLength, b, bLength);
		final int prodLength = Math.min(bLength, yLength); 
		if (itemCount > aLength) { //min(this, a) 
			itemCount = aLength; }
		if (itemCount < prodLength) { //Most probable Test first
			itemCount = prodLength; 
		} else  
		if (itemCount == prodLength) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}
			
	/** linear Combination x = a * x + b * y	 */
	public VectorDouble biLinAt(final double a, final double[] y, final int yLength, final double[] b, final int bLength) {
		items = VectorDouble.BI_LIN_AT(items, itemCount, a, y, yLength, b, bLength);
		final int prodLength = Math.min(bLength, yLength); 
		if (itemCount < prodLength) { //Most probable Test first
			itemCount = prodLength; 
		} else  
		if (itemCount == prodLength) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}
				
	/** linear Combination x = a * x + b * y	 */
	public VectorDouble biLinAt(final double[] a, final int aLength, final double y, final double[] b, final int bLength) {
		return biLinAt(a, aLength, b, bLength, y); }
				
	/** linear Combination x = a * x + b * y	 */
	public VectorDouble biLinAt(final double[] a, final int aLength, final double[] y, final int yLength, final double b) {
		items = VectorDouble.BI_LIN_AT(items, itemCount, a, aLength, y, yLength, b);
		final int prodLength = yLength; 
		if (itemCount > aLength) { //min(this, a) 
			itemCount = aLength; }
		if (itemCount < prodLength) { //Most probable Test first
			itemCount = prodLength; 
		} else  
		if (itemCount == prodLength) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}
				
	/** linear Combination x = a * x + b * y	 */
	public VectorDouble biLinAt(final double a, final double[] y, final int yLength, final double b) {
		items = VectorDouble.BI_LIN_AT(items, itemCount, a, y, yLength, b);
		final int prodLength = yLength; 
		if (itemCount < prodLength) { //Most probable Test first
			itemCount = prodLength; 
		} else  
		if (itemCount == prodLength) {
			if (CANONICALIZE) {
				canonicalizeAt(); } 
		}
		return this;
	}
				
	/**Multiply the Vector by an Object in Place.
	 * This extends the standard Set Multiplication
	 * by the Multiplication with a Permutation.	 */
	/*public SemiGroupM mulAt(Object arg)	{
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
	public VectorDouble mulPolynom(final VectorDouble arg) {
		//this.canonicalizeAt(); 
		//arg .canonicalizeAt(); 
		final int sum = arg.itemCount+this.itemCount;
		final double[] ret = new double[sum]; 
		for(int i = arg.itemCount; --i >= 0;) {
			VectorDouble.ADD_PROD_AT(ret, items, arg.items[i], 0, this.itemCount, i); }
		//ret.canonicalizeAt();
		return new VectorDouble(ret); }
	
	/**
	 * divides the Polynom in Place with Remainder;  
	 * 
	 * Despite it's similar Structure, 
	 * Polynom Division is very different from g-adic Division, 
	 * which is very simple for g=2, but quite complicated for larger gs!!!
	 * With Rollover the Algorithm needs a lot more than the simple Polynom Division.
	 * You ALWAYS (except for g=2) have an Uncertainty in the last Digits
	 * that could affect the first Digits by a Ripple Carry!
	 * 
	 * Could be even faster (for higher Dimensions) 
	 * when performed in Fourier Space, where it reduces to a simple, 
	 * element-wise Division. 
	 * 
	 * A more efficient Algorithm is described in the numerical Recipes, 
	 * where the Newton Algorithm is used to solve the Equation this = quot*arg+mod for quot and mod
	 * @see  streamIO.copy.group.ring.IIntRing#ModAtDivAt(java.lang.Object, streamIO.copy.group.ring.IIntRing)
	 */ 
	public VectorDouble modAtDivAt(final VectorDouble arg, final VectorDouble quotient) {
		if ((arg == null) || (arg.isZero())) {
			throw new ArithmeticException("Division of "+this+" by Zero:"+arg); }
		final int quotDim = this.itemCount - arg.itemCount + 1;
		if (quotDim <= 0) {	//Divisor is smaller than Dividend?
			quotient.setSize(0);	//=> Quotient = 0
			return this; }	//=> Remainder == Original 
		quotient.setSize(quotDim);	//Make Space for the maximum Degree of the Quotient
		MOD_AT_DIV_AT(this.items, this.itemCount, arg.items, arg.itemCount, quotient.items);
		canonicalizeAt();
		quotient.canonicalizeAt(); //.getInt() = arg..getInt(); 
		return this; }

	/**
	 * divides the Polynom in Place with Remainder;  
	 * 
	 * Despite it's similar Structure, 
	 * Polynom Division is very different from g-adic Division, 
	 * which is very simple for g=2, but quite complicated for larger gs!!!
	 * With Rollover the Algorithm needs a lot more than the simple Polynom Division.
	 * You ALWAYS (except for g=2) have an Uncertainty in the last Digits
	 * that could affect the first Digits by a Ripple Carry!
	 * 
	 * Could be even faster (for higher Dimensions) 
	 * when performed in Fourier Space, where it reduces to a simple, 
	 * element-wise Division. 
	 * 
	 * A more efficient Algorithm is described in the numerical Recipes, 
	 * where the Newton Algorithm is used to solve the Equation this = quot*arg+mod for quot and mod
	 * @see  streamIO.copy.group.ring.IIntRing#ModAtDivAt(java.lang.Object, streamIO.copy.group.ring.IIntRing)
	 */ 
	final static public void MOD_AT_DIV_AT(final double[] ths, final int thsLength, final double[] arg, final int argLength, final double[] quotient) {
		final double divisor = arg[argLength-1]; //The Divisor always stays the same
		for (int i = thsLength, iq = thsLength-argLength+1; --iq >= 0; ) { //
			final double quot = quotient[iq] = ths[--i]/divisor;
			//This is the basic Polynom Division: Subtract the multiplied Polynom from this one...
			VectorDouble.ADD_PROD_AT(ths, arg, -quot, 0, argLength, iq);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors for usage as a rectangular, multidimensional Array
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor only for use within this Class
	 * to generate different Indexing Schemes on the same Data
	 * when using a 1-dim Array for rectangular Addressing.
	 * @param _values, the backing Values of the Matrix,
	 *  possibly shared with other Matrices.
	 * @param dimFactors the Factors for the Items in the Tensor
	 */
	protected VectorDouble(final double[] _values, final int[] _dimFactors) {
		this.dimFactors = _dimFactors;
		this.items = _values;
	}
	
	/**
	 * @param Rows the Numbers of Rows    in the Matrix
	 * @param Cols the Numbers of Columns in the Matrix
	 */
	/*	public VectorDouble(int Rows, int Cols) {
			this.dimSizes = new int[2];
			this.dimSizes[0] = Cols;
			this.dimSizes[1] = Rows;
			dimFactors = new int[2];
			dimFactors[1] = 1;
			dimFactors[2] = Cols;
			items = new double[Rows * Cols];
		}
	*/
	/** Constructs a Tensor of the given per-Dimension Column Counts, computing the row-major Strides into dimFactors.
	 * @param colCounts the Numbers of Columns in the Tensor
	 */
	public VectorDouble(final int[] colCounts) {
		this.dimSizes = colCounts;
		dimFactors = new int[colCounts.length];
		int dimFactor = 1; //last Index has smallest Factor
		for (int i = colCounts.length; --i >= 0; ) {
			dimFactors[i] = dimFactor;
			dimFactor *= colCounts[i];
		}
		items = new double[dimFactor];
	}
	
	/** Determines whether the Items of this Vector are monotonically ordered.
	 * @return the Order of the Items in this Container
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	public byte getOrder() {
		int ret = HunterDouble.GET_ORDER(items, 0, itemCount);
		if (ret <  IOrdered.ORDER_DESC)
			return IOrdered.ORDER_NONE; 
		return (byte) ret; }
	
	/**Returns true, when the Items in the Container are ordered ascending
	 * from the i-th Item on (monotonous Sequence)	 */
	public boolean isOrdered(final int start, final int stop){
		return HunterDouble.IS_ORDERED(items, start, stop); }
	
	/**Returns true, when the Items in the Container are ordered ascending
	 * from the i-th Item on (monotonous Sequence)	 */
	public boolean isOrdered(){ return HunterDouble.IS_ORDERED(items, 0, itemCount); }
	
	/**Arithmetic Shift right by one position in Place: x>>=1	*/
	public int shrAt(final int shift) {
		return itemCount = SHR_AT(items, itemCount, shift); }
	
	/**Arithmetic Shift left  by one position in Place: x<<=1	*/
	public int shlAt(final int shift) {
		return itemCount = SHL_AT(items, itemCount, shift); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Combines all Item bit-patterns, order-sensitively, into a single hash Value.
	 * @return  a hash code value for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.lang.Object#hashCode()
	 */
	public int hashCode(){
		int Sum = 0; //not really useful to hash for double()!!!
		for (int i = itemCount; --i >= 0;) { //take the Order into Account!
			Sum = (Sum + (int) Double.doubleToLongBits(items[i])) >> 1; }
		return Sum; }

	/** Delegates to {@link #equals(VectorDouble)} when arg is one, otherwise to arg's own equals().
	 * @return true when arg is equal to this Vector, as decided by whichever side's equals() runs;
	 *  false for a null arg.
	 */
	public boolean equals(final Object arg) {
		if (arg == null) {
			return false; }
		if (arg instanceof VectorDouble) {
			return equals((VectorDouble) arg); }
		return arg.equals(this);
	}

	/** Compares this Vector to arg element-wise, canonicalizing both first when their Item Counts differ.
	 * @return true when both Vectors have the same canonical Length and equal Items at every Position.
	 */
	public boolean equals(final VectorDouble arg) {
		if (arg.itemCount != itemCount) {
			arg.canonicalizeAt(); //usually only a single Comparison!
			canonicalizeAt(); 
		}
		if (arg.itemCount != itemCount) {
			return false; }
		for (int i = itemCount; --i >= 0; ) {
			if (!ByRefDouble.EQUALS(items[i], arg.items[i])) {
				return false; } //TODO: find a global Criterion... 
		} //and not one per Dimension, because Rotation mixes Dimensions! 
		return true; }
	
	/**Testing for 0:			*/
	public boolean isZero()	{ return canonicalizeAt() == 0; }
	
	/**Testing for the given Value:			*/
	public boolean isValue(final double val) { 
		for (int i = itemCount; --i >= 0; ) {
			if (!ByRefDouble.EQUALS(val, items[i])) {
				return false; }
		} return true; }
		
	/**Testing for the given Value:			*/
	public VectorDouble copyAt(final double val) { 
		FILL_AT(items, val, 0, itemCount); 
		return this; }
		
	/** Squares every Item of this Vector in Place.
	 * @return the Square in Place: x*=x	*/
	public VectorDouble sqrAt () {
		for (int i = itemCount; --i >= 0; ) {
			items[i] *= items[i];
		} return this; }
	
	/** Cubes every Item of this Vector in Place.
	 * @return the Cubic in Place: x*=x^2	*/
	public VectorDouble cbcAt () {
		for (int i = itemCount; --i >= 0; ) {
			items[i] *= items[i]*items[i];
		} return this; }
	
	/**absolute Value in Place: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm
	 * Leaves Vectors with only 1 Dim behind, which contain the Abs-Norm of this Row.	 */
	public VectorDouble AbsVAt() { 
		itemCount = 1; items[0] = NORM_ABS(items); 
		//ABSV_AT(items, 0, itemCount); //TODO 
		return this; 
	}
	
	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 
	 */
	public double MaxNorm() { return NORM_MAX(items, 0, itemCount); }
	
	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public double SqrNorm() { return NORM_SQR(items, 0, itemCount); }
	
	/**Sum: Returns the Sum of all Elements in the Tensor	 */
	public double Sum() { return SUM(items, 0, itemCount); }
	
	/**Prod: Returns the Product of all Elements in the Tensor	 */
	public double Prod() { return PROD(items, 0, itemCount); }
	
	/**Swaps the Elements i and j of the Array in Place.	 */
	public VectorDouble swapAt(final int i, final int j) {
		HunterDouble.SWAP_AT(items, i, j);
		return this; }
	
	/** Builds a transposed View of this 2-dimensional Tensor by permuting its Index Factors, sharing the same backing Array.
	 * @return a VectorDouble with IndexFactors such
	 *  that the Elements are transposed.
	 * Only useful when simulating a rectangular Tensor on a 1 dim Array.
	 */
	public VectorDouble getTranspose() {
		if (dimFactors.length != 2) {
			throw new InvalidParameterException("For Tensors please determine the Dimensions to transpose!");
		}
		int[] Factors = new int[2];
		Factors[0] = dimFactors[1]; //Just permuting the Factors is sufficient!
		Factors[1] = dimFactors[0]; //also for Tensors of higher Degrees!
		return new VectorDouble(items, Factors);
	}

	/** Writes this Vector's Items, comma-separated and parenthesized, to the given Writer.
	 * @return  a string representation of the object.
	 *  @see Object#toString()
	 */
	public Writer toStream(final Writer stream) throws IOException
	{	//either return it as a long or in VectorDbl Representation
		//TODO: Write toStream
	//	if (Modul > 0) return RingLongValue().toString();	//Number Representation
	//	else {	//Polynomic Representation
		stream.write(String.valueOf(itemCount));
		stream.write("(");
		stream.write(String.valueOf(items[0]));
		for (int i = 0; ++i < itemCount; ) {	//most important Coefficients first
			stream.write(",");
			stream.write(String.valueOf(items[i])); }
		stream.write(")");
		return stream; }
		
	/////////////////////////////////////////////////////////////////////////////////////
	/// IManifold: discrete Differentiation and Integration	
	/////////////////////////////////////////////////////////////////////////////////////
		
	/** Level of Differentiation, used to determine, to which point the original Vector waa valid	 */
	protected int diffLevel = 0;
		
	/** Factorial of diffLevel, used to scale the upper Values,
	  * so they can directly be used for inter/extrapolation	 */
	protected ByRefInt factorial = new ByRefInt(1);
		
	/** Flag to indicate periodic Bounds	 */
	public boolean periodic;
	
	/** Returns the forward-Difference Vector of a Copy of this Manifold, leaving this Vector unchanged.
	 * @return the Difference Vector of this Manifold in Place: diff(i)= a(i) - a(i+1)
	  * The Difference Vector has one Item less than the original Vector.
	  * For complete Reversibility the last Item is preserved.	 */
	public VectorDouble diff() { return copyOrig().diffAt(); }

	/** Differentiates this Vector in Place, scaling the preserved last Item by the running Factorial.
	 * @return the Difference Vector of this Manifold in Place: diff(i)= a(i) - a(i+1)
	  * The Difference Vector has one Item less than the original Vector.
	  * For complete Reversibility the last Item is preserved.	 */
	public VectorDouble diffAt() {
		if (itemCount <= 0) {
			return this; } 
		DIFF_AT(items, 0, itemCount);
		items[itemCount--] /= factorial.Value; //divide the last Item by the Factorial, so it can be used directly for ... TODO
		factorial.Value *= ++diffLevel;
		return this; }
	
	/** Differentiates the Polynom represented by this Vector in Place, one degree lower.
	 * @see streamIO.copy.group.ring.metric.body.vector.IManifold#diffAt()	 */
	public VectorDouble diffPolynomAt() {
		DIFF_POLYNOM_AT(items, itemCount); 
		--itemCount;	//reduce the Degree, preserve highest Item
		return this; }
	
	/**Returns the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	 * This is the reverse Operation to diffAt().
	 * The Integral Polynom has one Item more than the original Polynom.
	 * This is either restored from the highest Element
	 * or assumed to Zero. 	 */
	public VectorDouble summPolynomAt()	{	//first do the Rotation, then the Multiplication, thus a[i]*=i
		setSize(itemCount+1); //++itemCount; 	//preserve highest Item, don't set it to zero
		items = SUMM_POLYNOM_AT(items, itemCount); 
		return this; }
	
	/** Integrates this Vector in Place, the reverse Operation to {@link #diffAt()}.
	  * @return  the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	  * This is the reverse Operation to diffAt().
	  *
	  * It is used e.g.
	  * to calculate the accumulated Probability of a discrete Distribution
	  *
	  * The Integral has one Item more than this Vector.
	  * This last Item is new and initialized to zero, if it was not preserved
	  * from a previous diff Operation or initialized before.
	  * If you want to start Integration from a certain Value,
	  * it is faster to modify this start Value by modifying the last Item.	 */
	public VectorDouble summAt() {
		//	boolean startIs0;
		double tmp1;
		setSize(itemCount+1);	//preserve the higher Items, but don't set them to zero.
		if (diffLevel > 0) {
			factorial.Value /= diffLevel--; tmp1 = (items[itemCount] *= factorial.Value);
		} else { 
			tmp1 = items[itemCount]; 
		} //items[i];
		SUMM_AT(items,  0,  itemCount); 
		return this; }
	
	/** Repeatedly differentiates this Vector in Place down to all Derivatives.
	 * @return the full Difference Vector of this Manifold in Place
	  * The full Difference Vector consists of all Derivatives.
	  * It can be used to calculate inter- and extrapolations with Horner(). 	 */
	public VectorDouble fullDiffAt() {
	//	ByRefInt fact = new ByRefInt(1);
		while (itemCount > 0) {
			diffAt(); //items[itemCount+1].divAt(fact); fact.Value*=(++i);}
		}
	//	items[0].divAt(fact);
		return this; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Adds a Point (y0) to the Manifold.
	  * If the Manifold has been differentiated,
	  * all Points are differentiated 	 */
	public VectorDouble addPointAt(double y0) {
		itemCount += diffLevel;
		setSize(itemCount+1);
		double tmp2, tmp1 = (items[itemCount] = y0);
		int i = diffLevel;
		while (--i > 0) {
			tmp2 = tmp1; tmp1 = (items[i] -= tmp2);}
		itemCount -= diffLevel;
		return this; }
	
	/** Adds a Point (y0, x0) to the Difference Vector.
	  * The x Coordinate is given implicitly by the inverse Coordinate Differences
	  * in invDiffX. 	 */
	public VectorDouble addPointAt(double y0, double x0, VectorDouble x) {
		x.addPointAt(x0);
		itemCount += diffLevel;
		setSize(itemCount + 1);
		double tmp2, tmp1 = items[itemCount] = y0;
		int i = diffLevel;
		while (--i > 0) {
			tmp2 = tmp1; tmp1 = (items[i] -= tmp2); 
			items[i] /= x.items[i]; } //.getAt(i); }
		itemCount -= diffLevel;
		return this; }
	
	/** Calculates the Value of this Manifold at the Point x,
	  * using the already calculated Differences at equidistant Points.
	  * Gives best results, if the Manifold has been differenced all through,
	  * because only the higher Coefficients are used.
	  * This is well suited for a single interpolated Value, 
	  * but for the repetitive Calculation of interpolating Values,
	  * it is better to use Inter/Extrapolation
	  * with either Polynomial or Rational Functions.
	  * The Division by the factorials is done once, when this function is differenced! 	 */
	public double Horner(double x, double x0, double h) {
		int i = itemCount; 	//coordinate independent transformed coordinate!
		int j = i;	//Because of using backward differences, I have to use (x0-x)
		double t = (x0-x)/h;//t is the normed Argument Space
	//	if (i >= 0) x.addAt(j); //??? never used again!
		double result = items[++i];
		j = diffLevel;
		while (--j > 0) {
			result = result*(++t) + items[++i]; }
		return result; }
	
	/**
	 * multiplies the LinearFactor (x-zeroPos) into this Polynom 
	 * creating a Radix at zeroPos. 
	 * @param zeroPos the Position of the Radix
	 * @return this Polynom in Place
	 */
	public VectorDouble mulLfAt(final double zeroPos) {
		shrAt(1);
		int i = itemCount;  
		double items_i = items[--i]; 
		for (; --i > 0;) {
			final double tmp = items[i]; 
			items[i] += items_i*zeroPos; 
			items_i = tmp;
		}
		items[0] = items_i;
		return this; 
	}
	
	/**
	 * Reads a row of Values (e.g. the Points of a single Plane) 
	 * from the current ResultSet
	 * @param rs the ResultSet to read from
	 * @param columnOffset the Column to start reading consecutively from 
	 * @return the row read into this .
	 */
	final public VectorDouble read(final ResultSet rs, final int columnOffset)
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
	final public VectorDouble read(final ResultSet rs, int columnOffset, int lastCol)
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

	/** Testing the Differential Operations of this class	 */
	public static void testDiff() {
		double[] x1 = {0.0, 1.0, 3.0};
		double[] y1 = {1.0, 3.0, 2.0};
	
		VectorDouble x1_ = new VectorDouble(x1, false);
		VectorDouble y1_ = new VectorDouble(y1, false);
		VectorDouble dx_ = (VectorDouble) x1_.diff().invAt(); //creates the Factors for
	//	y.fullDiffAt(x); //TODO: Differencing on non equidistant Raster!
	//	y.Horner(new BodyDouble(2.0), x);  //TODO: test the Horner Scheme on a non equidistant Raster!
		System.out.println(y1_); 
		System.out.println(dx_); 
	
		IFloatDeriveAble fktn = function.derive.ring.body.Cosinus.Cosinus;	//RingFuncs.IdentityCopy();	//RingFuncs.fSquare(); //IdentityCopy(); //Cosinus(); //CosHMinus1(); //CosinusMinus1(); //Cosinus(); //Sinus();
		double x0  = 0  ;//-Math.PI / 3);
		double ddx = 0.2;//Math.PI / 12);
		VectorDouble x = new VectorDouble(x0, ddx, 5);
		VectorDouble Sample = new VectorDouble(fktn, x);
	
		System.out.println(" Interpolation with equidistant Sample Points");
		System.out.println(" x = " + x);
		VectorDouble dx = (VectorDouble) x.diff();
		System.out.println(" dx = " + dx);
		System.out.println(" y = f (x) = " + Sample);
		Sample.fullDiffAt();
		System.out.println(" Values at the Sample Points: approx == exakt ");
		for (int i = -1; ++i <= x.getInt();) {
			double z = x.items[i];//new BodyDouble(Math.random());
			System.out.print(" z = " + z + "; f (z) = "); 
			System.out.print(Sample.Horner(z, x0, ddx) + " == ");
			System.out.print(fktn.Map(z)); 
			System.out.println(); }
		System.out.println(" Values at random Points: approx == exakt ");
		for (int i = -1; ++i <= x.getInt(); ) {
			double z = Math.random();
			System.out.print(" z = " + z + "; f (z) = "); 
			System.out.print(Sample.Horner(z, x0, ddx) + " == "); 
			System.out.print(fktn.Map(z)); 
			System.out.println(); }
	}
	
	/**
	 * Tests the Conversion between Rectangular and Polar Coordinates
	 */
	public static void testRectPolar() {
		int dim = 4;
		double[] vec = new double[dim];
		double[] copy = new double[dim];
		for (int iter = 10; --iter >= 0; ) {
			RANDOMIZE_AT(vec); 
			System.arraycopy(vec, 0, copy, 0, dim); //create Copy for later Comparison
			//L.n("Original: ");
			//AStreamOut.ArrayToStream(System.out, vec, ",");
			//L.n("\ntwice transformed: ");
			Polar2RectAt(Rect2PolarAt(vec));
			//AStreamOut.ArrayToStream(System.out, vec, ",");
			//L.n("\n");
			Assert.EQUALS(copy, vec);
		}
	}

	private static final void testMultiTernaryOp() {
		for (int i = 200; --i >= 0;) { 
			testTernaryOp(); 
			L.n();
		} 
	}
			
	private static final void testTernaryOp() {
		final double[] a0 = RANDOM(20); 
		final double[] a1 = RANDOM(20); //new double[0];// 
		final double[] a2 = RANDOM(20); 
		final double[] a3 = RANDOM(20); 
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.AddOp, ADD(a0, a1), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.SubOp, SUB(a0, a1), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.MulOp, MUL(a0, a1), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.DivOp, DIV(a0, a1), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.LinOp, LIN(a0, a1, a2), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.AddProdOp, ADD_PROD(a0, a1, a2), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.BiLinOp, BI_LIN(a0, a1, a2, a3), 4);

		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.AddOp, ADD_AT(COPY(a0), a1), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.SubOp, SUB_AT(COPY(a0), a1), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.MulAtOp, MUL_AT(COPY(a0), a1), Math.min(a0.length, a1.length));
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.DivOp, DIV_AT(COPY(a0), a1), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.LinAtOp, LIN_AT(COPY(a0), a1, a2), Math.max(Math.min(a0.length, a1.length), a2.length));
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.AddProdOp, ADD_PROD_AT(COPY(a0), a1, a2), 4);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.BiLinAtOp, BI_LIN_AT(COPY(a0), a1, a2, a3), Math.max(Math.min(a0.length, a1.length), Math.min(a2.length, a3.length))); 
		
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.AddOp, ADD(a0, Math.PI), 0);
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.MulOp, MUL(a0, Math.PI), 0);
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.LinOp, LIN(a0, Math.PI, a2), 0);
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.AddProdOp, ADD_PROD(a0, Math.PI, a2), 0);
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.BiLinOp, BI_LIN(a0, Math.PI, a2, a3), 0);

		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.AddOp, ADD_AT(COPY(a0), Math.PI), 0);
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.MulOp, MUL_AT(COPY(a0), Math.PI), 0);
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.LinOp, LIN_AT(COPY(a0), Math.PI, a2), 0);
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.AddProdOp, ADD_PROD_AT(COPY(a0), Math.PI, a2), 0);
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.BiLinOp, BI_LIN_AT(COPY(a0), Math.PI, a2, a3), 0);

		checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.LinOp, LIN(a0, a1, Math.PI), 0);
		checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.AddProdOp, ADD_PROD(a0, a1, Math.PI), 0);
		checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.BiLinOp, BI_LIN(a0, a1, Math.PI, a3), 0);

		checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.LinOp, LIN_AT(COPY(a0), a1, Math.PI), 0);
		checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.AddProdOp, ADD_PROD_AT(COPY(a0), a1, Math.PI), 0);
		checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.BiLinAtOp, BI_LIN_AT(COPY(a0), a1, Math.PI, a3), Math.max(Math.min(a0.length, a1.length), a3.length));

		checkTernaryOp(a0, 0, a1, 0, a2, 0, null, Math.PI, QuaternaryOp.BiLinOp, BI_LIN(a0, a1, a2, Math.PI), 0);
		checkTernaryOp(a0, 0, a1, 0, a2, 0, null, Math.PI, QuaternaryOp.BiLinAtOp, BI_LIN_AT(a0, a1, a2, Math.PI), 0);

		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, null, Math.PI, QuaternaryOp.BiLinOp, BI_LIN(a0, Math.PI, a2, Math.PI), 0);
		checkTernaryOp(a0, 0, null, Math.PI, a2, 0, null, Math.PI, QuaternaryOp.BiLinAtOp, BI_LIN_AT(a0, Math.PI, a2, Math.PI), 0);
	}
	
	/** @param max the maximum Index up to where to check, because adding scalar Values is not well defined!
	 */
	private static final void checkTernaryOp(final double[] a0, double a0Val, final double[] a1, double a1Val
	, final double[] a2, double a2Val, final double[] a3, double a3Val
	, final QuaternaryOp op, final double[] result, int max) {
		final int numArgs = op.numArgs();
		if ((numArgs > 0) && (a0 != null) && (max < a0.length)) {
			max = a0.length; } 
		if ((numArgs > 1) && (a1 != null) && (max < a1.length)) {
			max = a1.length; } 
		if ((numArgs > 2) && (a2 != null) && (max < a2.length)) {
			max = a2.length; } 
		if ((numArgs > 3) && (a3 != null) && (max < a3.length)) {
			max = a3.length; } 
		for (int i = max; --i >= 0; ) {
			if (a0 != null) {
				a0Val = GET_AT(i, a0); }
			if (a1 != null) {
				a1Val = GET_AT(i, a1); }
			if (a2 != null) {
				a2Val = GET_AT(i, a2); }
			if (a3 != null) {
				a3Val = GET_AT(i, a3); }
			final double expected = op.op(a0Val, a1Val, a2Val, a3Val);
			final double actual = GET_AT(i, result);  
			if (Double.isNaN(expected) || Double.isInfinite(expected)) {
				if (Double.isNaN(actual) || Double.isInfinite(actual)) {
					continue; } 
			}
			Assert.EQUALS(expected, actual);				
		}
	}
	
	private static final void testMultiTernaryVectorOp() {
		for (int i = TEST_DIMS*TEST_DIMS; --i >= 0;) { //*TEST_DIMS*TEST_DIMS 
			testTernaryVectorOp(); 
			L.n();
		} 
	}
	
	private static final int TEST_DIMS = 12; 
	
	private static final void checkTernaryOp(final VectorDouble a0, double a0Val, final VectorDouble a1, double a1Val
	, final VectorDouble a2, double a2Val, final VectorDouble a3, double a3Val
	, final QuaternaryOp op, final VectorDouble result, int max) {
		final int numArgs = op.numArgs();
		if ((numArgs > 0) && (a0 != null) && (max < a0.getInt())) {
			max = a0.getInt(); } 
		if ((numArgs > 1) && (a1 != null) && (max < a1.getInt())) {
			max = a1.getInt(); } 
		if ((numArgs > 2) && (a2 != null) && (max < a2.getInt())) {
			max = a2.getInt(); } 
		if ((numArgs > 3) && (a3 != null) && (max < a3.getInt())) {
			max = a3.getInt(); } 
		for (int i = max; --i >= 0; ) {
			if (a0 != null) {
				a0Val = a0.getDoubleAt(i); }
			if (a1 != null) {
				a1Val = a1.getDoubleAt(i); }
			if (a2 != null) {
				a2Val = a2.getDoubleAt(i); }
			if (a3 != null) {
				a3Val = a3.getDoubleAt(i); }
			final double expected = op.op(a0Val, a1Val, a2Val, a3Val);
			final double actual = result.getDoubleAt(i);  
			if (Double.isNaN(expected) || Double.isInfinite(expected)) {
				if (Double.isNaN(actual) || Double.isInfinite(actual)) {
					continue; } 
			}
			Assert.EQUALS(expected, actual);				
		}
	}
	
	private static final void testTernaryVectorOp() {
		final VectorDouble a0 = new VectorDouble(VectorDouble.RANDOM(VectorDouble.TEST_DIMS)); // RANDOMIZED(3));//
		final VectorDouble a1 = new VectorDouble(VectorDouble.RANDOM(VectorDouble.TEST_DIMS)); // RANDOMIZED(4));// 
		final VectorDouble a2 = new VectorDouble(VectorDouble.RANDOM(VectorDouble.TEST_DIMS)); 
		final VectorDouble a3 = new VectorDouble(VectorDouble.RANDOM(VectorDouble.TEST_DIMS));
		//final VectorDouble a0Copy = (VectorDouble) a0.copy();  
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.AddOp, a0.add (a1), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.SubOp, a0.subt(a1), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.MulOp, a0.mul (a1), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.DivOp, a0.div (a1), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.AddProdOp, a0.addProd(a1, a2), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.LinOp, a0.Lin(a1, a2), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.BiLinOp, a0.biLin(a1, a2, a3), 4);
			
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.AddOp, a0.copyOrig().addAt (a1), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.SubOp, a0.copyOrig().subAt(a1), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.MulOp, a0.copyOrig().mulAt (a1), 4); //Math.min(a0.getInt(), a1.getInt()));
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.DivOp, a0.copyOrig().divAt (a1), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.LinOp, a0.copyOrig().LinAt (a1, a2), 4); //Math.max(Math.min(a0.getInt(), a1.getInt()), a2.getInt()));
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.AddProdOp, a0.copyOrig().addProdAt(a1, a2), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, a3, 0, QuaternaryOp.BiLinOp, a0.copyOrig().biLinAt(a1, a2, a3), 4); //Math.max(Math.min(a0.getInt(), a1.getInt()), Math.min(a2.getInt(), a3.getInt()))); 
			
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.AddOp, a0.add(Math.PI), 0);
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.MulOp, a0.mul(Math.PI), 4);
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.LinOp, a0.Lin(Math.PI, a2), 4);
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.AddProdOp, a0.addProd(Math.PI, a2), 4);
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.BiLinOp, a0.biLin(Math.PI, a2, a3), 4);
		
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.AddOp, a0.copyOrig().addAt(Math.PI), 0);
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.MulOp, a0.copyOrig().mulAt(Math.PI), 4);
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.LinOp, a0.copyOrig().LinAt(Math.PI, a2), 4);
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.AddProdOp, a0.copyOrig().addProdAt(Math.PI, a2), 4);
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, a3, 0, QuaternaryOp.BiLinOp, a0.copyOrig().biLinAt(Math.PI, a2, a3), 4);
		
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.LinOp, a0.Lin(a1, Math.PI), 0);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.AddProdOp, a0.addProd(a1, Math.PI), 4);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.BiLinOp, a0.biLin(a1, Math.PI, a3), 4);
		
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.LinAtOp, a0.copyOrig().LinAt(a1, Math.PI), 0);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.AddProdOp, a0.copyOrig().addProdAt(a1, Math.PI), 2);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, null, Math.PI, a3, 0, QuaternaryOp.BiLinOp, a0.copyOrig().biLinAt(a1, Math.PI, a3), 0);
		
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, null, Math.PI, QuaternaryOp.BiLinOp, a0.biLin(a1, a2, Math.PI), 0);
		VectorDouble.checkTernaryOp(a0, 0, a1, 0, a2, 0, null, Math.PI, QuaternaryOp.BiLinOp, a0.copyOrig().biLinAt(a1, a2, Math.PI), 0);
		
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, null, Math.PI, QuaternaryOp.BiLinOp, a0.biLin(Math.PI, a2, Math.PI), 0);
		VectorDouble.checkTernaryOp(a0, 0, null, Math.PI, a2, 0, null, Math.PI, QuaternaryOp.BiLinOp, a0.copyOrig().biLinAt(Math.PI, a2, Math.PI), 0);
	}
	
	/** Tests all Methods of this Class */
	public static void testEntropyContinuous() {
		final double[] x = new double[50]; 
		double entropy = 0; 
		entropy = ENTROPY(x);
		L.n("Entropy of singular Distribution").l(entropy); 
		for(int i = x.length; --i >= 0; ) 
			x[i] = i; 
		entropy = ENTROPY(x);
		L.n("Entropy of equal Distribution").l(ENTROPY(x)); 
		//for(int i = x.length; --i > 0; ) 
		//	L.n("Entropy of equal Distribution with length").l(i).l(ENTROPY(x, i)); 
		for(int i = x.length; --i >= 0; ) 
			x[i] = Math.random(); 
		Arrays.sort(x); 
		entropy = ENTROPY(x);
		L.n("Entropy of random, but uniform Distribution").l(entropy); 		
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		L.n("Testing " + VectorDouble.class.getName());
		testEntropyContinuous(); 
		testDiff();
		testRectPolar();
		double[] flt = new double[10];
		RANDOMIZE_AT_1_1(flt);
		ADD_AT(flt, 1);
		LOG_AT(flt);
		NEG_AT(flt);
		testMultiTernaryOp();
		testMultiTernaryVectorOp(); 
		Assert.IS_TRUE(new VectorDouble(new double[] {0,1,2,3,4}).isOrdered());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt(args);
	}

}

/** Iterates a {@link VectorDouble} backwards from its current Position down to Index 0.
 * could also be substituted by any IndexStreamIn
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:01:01Z
 * digest: 9f182c959d6360d3467f896b7ce1b50c012bf6ac37cff2142326df32ff244790
 * stale: false
 * tags: [code/functional_interfaces]
 * concepts: [Reverse-Order Double Stream Source]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * since the @see IIndexed  */
final class VectorDoubleStreamIn
extends AVectorStreamIn_Float {

	/** The Vector being iterated. */
	final public VectorDouble vector;

	/** Wraps the given Vector, starting the Iteration Position at its current Length. */
	public VectorDoubleStreamIn(final VectorDouble vector_) {
		this.vector = vector_;
		pos = vector.getInt();
	}

	/** @see Stream.Float.IStreamIn_Float#nextDouble()	 */
	protected double nextDoubleInternal() { return vector.items[--pos]; }

	/** Returns the smallest Value held by the wrapped Vector.
	 * @see Stream.Float.IStreamIn_Bound_Int#getMinValue()	 */
	public double getMinDouble() { return vector.MinVal(); }

    /** Returns the wrapped Vector's current Item Count as the maximum Mark Size.
     * @see streamIO.real.AStreamIn_Float#getMaxMarkSize()     */
    public long getMaxMarkSize() { return vector.getInt(); }

}
