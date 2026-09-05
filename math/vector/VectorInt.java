package math.vector;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import math.NumberFormatter;
import streamIO.AStreamOut;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.integer.jdbc.AResultSet;
import streamIO.integer.random.RandomQuick;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.IOrderAble;
import function.byref.ByRefInt;

/**
  * Provides static Methods and a dynamic Array Type for Vectors and Arrays of primitive int Numbers.
  * Title: VectorInt<p>
  * Description:
  * Defines static Methods to treat Vectors and Arrays with Integer Numbers.
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.Vector.VectorDbl
  *
  * Implements a dynamic Array of primitive int Type,
  * which grows (and optionally shrinks) linearly or geometrically
  * and thus saves wrapping open-ended Streams of Primitives.
  * @see java.util.ArrayList
  * @see streamIO.Object.Enumerator.Container.Array
  * from which most of the non static Methods were copied.
  *
  * Especially arithmetic Operations are performed
  * taking the actual Lengths of the Operands into Account!
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
  * mtime: 2026-09-05T13:35:49Z
  * digest: cb108c5ff451d39832c3b2bc694d99f9c28526080f83a8470c319be27313f573
  * stale: false
  * tags: [code/growable_array, code/array_math]
  * concepts: [Growable int[] Vector]
  * facets: {layer: domain, status: broken, complexity: high}
  * -->
  */
final public class VectorInt
extends AVector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger to be used for Output 	 */
	private static final Log L = new Log(VectorInt.class, 1); 

	////////////////////////////////////////////////////////////////////////////////
	/// Conversion Routines
	////////////////////////////////////////////////////////////////////////////////

	/**Converts an Array of simple Type Contstants
	 * into an Array of the corresponding Object Type
	 * could be programmed slower but more generic using the Reflection API!	 */
	final static public Long[] const2Const(final long[] arg) {
		int len = arg.length;
		final Long[] ret = new Long[len];
		while (--len >= 0)
			ret[len] = new Long(arg[len]);
		return ret; }

	/**Formats the String to the given Length (left or right aligned)	 */
	final static public String FORMAT(int x, int Length) {
		return VectorString.FORMAT(String.valueOf(x), Length); }

	/** Converts an array of primitive ints into an array of boxed Integer objects.
	 * @return the Column at the given Position */
	final static public Integer[] TO_INTEGER(final int[] vector) {
		final Integer[] ret = new Integer[vector.length];
		for (int i=vector.length; --i >= 0;) {
			ret[i] = new Integer(vector[i]);
		}
		return ret; 
	}

	/** Converts an array of boxed Integer objects back into an array of primitive ints.
	 * @return the Column at the given Position */
	final static public int[] TO_ARRAY(final Integer[] vector, final int defaultForNulls) {
		final int[] ret = new int[vector.length];
		for (int i=vector.length; --i >= 0;) {
			final Integer value = vector[i];
			ret[i] = (value == null) ? defaultForNulls : value.intValue();
		}
		return ret; 
	}

	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 */
	final static public int[] TO_ARRAY(Object[] arg) {
		final int[] ret = new int[arg.length];
		for(int i = arg.length; --i >= 0; ) {
			ret[i] = ByRefInt.TO_INT(arg[i]); }
		return ret; }

	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 */
	final static public int[] TO_ARRAY(final ICountAble[] arg) {
		int[] Return = new int[arg.length];
		for(int i = arg.length; --i >= 0; ) {
			Return[i] = arg[i].getInt(); }
		return Return; }

	/** Calculates the Shannon Entropy of the given Vector, treating it as absolute Frequencies.
	 * @see VectorFloat#ENTROPY(float[]) which returns the Entropy of a Set of unbinned Measurements.
	 * @see math.matrix.MatrixInt#ENTROPY(int[][]) which returns the correlated Entropy
	 * @param arr the Vector to calculate the Entropy of
	 * @return the Entropy of this Vector
	 */
	final static public double ENTROPY(final int[] arr) {
		return VectorInt.ENTROPY(arr, arr.length); }

	/** Calculates the Shannon Entropy of the first numItems Elements of the given Vector.
	 * @see VectorFloat#ENTROPY(float[]) which returns the Entropy of a Set of unbinned Measurements.
	 * @see math.matrix.MatrixInt#ENTROPY(int[][]) which returns the correlated Entropy
	 * @param arr the Vector to calculate the Entropy of
	 * @param numItems the Number of Elements to consider
	 * @return the Entropy of this Vector
	 */
	final static public double ENTROPY(final int[] arr, final int numItems) {
		return VectorInt.ENTROPY(arr, numItems, VectorInt.SUM(arr, numItems, 0)); }

	/** 
	 * The Entropy of a Distribution is defined as 
	 * the Sum of the Entropies of it's Outcomes 
	 * Entropy does NOT reflect the Sequence of the Data, only it's Distribution. 
	 * @see VectorFloat#ENTROPY(float[]) which returns the Entropy of a Set of unbinned Measurements. 
	 * @see math.matrix.MatrixInt#ENTROPY(int[][]) which returns the correlated Entropy
	 *  
	 * @param arr the Vector with the absolute Frequencies of Events to calculate the Entropy of 
	 * @param numItems the Number of Events / Elements / Channels to consider
	 * @param sum the total Sum of the Elements' Frequencies 
	 * @return the Entropy of this Vector
	 */
	final static public double ENTROPY(final int[] arr, final int numItems, final long sum) {
		final float norm = 1f/sum; 
		double ex = 0; 
		for (int i=numItems; --i >= 0;) {
			if (arr[i] == 0) 
				continue; 
			final float p=arr[i]*norm;
			ex -= p*Math.log(p);
		}
		return ex;
	}

	/**
	 * Ackermann Function:
	 * Simple example for a recursive, but not primitive Recursive Function
	 * that grows faster than any other primitive Recursive Function p,
	 * i.e. for any p from P there is an n from N so that
	 * p(m) < Ackermann(n, m) for all m.
	 * A(1, 1) =  3
	 * A(2, 2) =  7
	 * A(3, 3) = 61
	 */
	final static public long Ackermann(long n, long m) {
		if (n == 0)
			return m + 1;
		if (m == 0)
			return Ackermann(n - 1, 1);
		return Ackermann(n - 1, Ackermann(n, m - 1));
	}

	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reverts the Sequence of Numbers. 
	 * This negates the Direction of a Plane. 
	 * Creates a Copy, because the original Points may be copied from somewhere!
	 * @see VectorInt.Invert()
	 */
	final static public int[] REVERSE(final int[] Plane, final int offset) {
	//	return invertPlaneAt(copy());
		int len = Plane.length;
		int Length = len-1;
		final int[] newPlane = new int[len];
		while (--len >= 0) {
			newPlane[len] = Plane[Length-len] + offset; }
		return newPlane; }

	/** 
	 * Negates the Orientation of a Plane	 
	 * @return the Array with Items in reverse Order
	 */
	final static public int[] REVERSE_AT(final int[] plane) {
		return REVERSE_AT(plane, 0); }

	/** 
	 * Negates the Orientation of a Plane	 
	 * @return the Array with Items in reverse Order and offset by a given Offset
	 */
	final static public int[] REVERSE_AT(final int[] plane, final int offset) {
		final int length = plane.length >> 1;
		for (int len = length; --len >= 0; ) {
			final int tmp = plane[len]+offset; 
			plane[len] = plane[len+length]+offset; 
			plane[len+length] = tmp; }
		return plane; }

	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods
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
	final static public void STREAM(final int[] vals, final PrintStream stream, final char separator) {
		STREAM(vals, stream, 0, vals.length, separator);
	}

	/** Streams out the complete given Array. 
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 * @param separator the Separator Character
	 */
	final static public void STREAM(final int[] vals, final PrintStream stream, final char separator, final int offset) {
		STREAM(vals, stream, 0, vals.length, separator, offset);
	}

	/** Streams out the complete given Array. 
	 * defaults the separator the Default Separator Character
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 */
	final static public void STREAM(final int[] vals, final PrintStream stream) {
		STREAM(vals, stream, 0, vals.length, DEFAULT_SEPARATOR);
	}
	
	/** Streams out the complete given Array. 
	 * defaults the separator the Default Separator Character
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 */
	final static public void STREAM(final int[] vals, final PrintStream stream, final int offset) {
		STREAM(vals, stream, 0, vals.length, DEFAULT_SEPARATOR, offset);
	}
	
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(final int[] vals, final PrintStream stream, final int startCol, final int stopCol, final char separator) {
		STREAM(vals, stream, startCol, stopCol, separator, 0);
	}
	
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(final int[] vals, final PrintStream stream, final int startCol, final int stopCol, final char separator, final int offset) {
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
	final static public void STREAM(final int[] vals) {
		STREAM(vals, DEFAULT_STREAM, 0, vals.length);
	}
	
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(final int[] vals, final PrintStream stream, final int startCol, int stopCol) {
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
	final static public void STREAM(final int[] d, final OutputStream ps, final NumberFormatter formatter, final String strSep) throws IOException {
		final Writer pw = new OutputStreamWriter(ps);
		STREAM(d, pw, formatter, strSep);
		pw.flush(); 
	}

	/** streams the Numbers of the given Array out to the Stream using the given Formatter
	 * 
	 * @param d Array to stream out
	 * @param strSep Separator String between Numbers 
	 * @param pw PrintWriter to stream to
	 * @param formatter Number Formatter to use 
	 */
	final static public void STREAM(final int[] d, final Writer pw, final NumberFormatter formatter, final String strSep) throws IOException {
		for (int i = -1; ++i < d.length;) {
			formatter.stream(pw, d[i]);
			pw.write(strSep); 
		}
		//return pw; 
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Boolean Trafos: so far there is no 'VectorBool' 
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Converts an array of primitive booleans into an array of boxed Boolean objects.
	 * @return the Vector converted from primitives to Objects */
	final static public Boolean[] TO_BOOLEAN(final boolean[] vector) {
		final Boolean[] ret = new Boolean[vector.length];
		for (int i=vector.length; --i >= 0;) {
			ret[i] = new Boolean(vector[i]);
		}
		return ret; 
	}

	/** Converts an array of boxed Boolean objects back into an array of primitive booleans.
	 * @return the Vector converted Objects from to primitives */
	final static public boolean[] TO_BOOLEAN(final Boolean[] vector, final boolean defaultForNulls) {
		final boolean[] ret = new boolean[vector.length];
		for (int i=vector.length; --i >= 0;) {
			final Boolean value = vector[i];
			ret[i] = (value == null) ? defaultForNulls : value.booleanValue();
		}
		return ret; 
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Matrix Trafos: extracting a Column, so far there is no 'MatrixInt' 
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Extracts a single Column from the given rectangular Matrix.
	 * @return the Vector converted from primitives to Objects */
	final static public int[] COLUMN(final int[][] matrix, final int col) {
		int[] ret = new int[matrix.length];
		for (int i = matrix.length; --i >= 0;) {
			ret[i] = matrix[i][col];
		}
		return ret;
	}

	/** Transposes the given rectangular Matrix by extracting each Column as a Row.
	 * @return the Vector converted Objects from to primitives */
	final static public int[][] TRANSPOSE(final int[][] matrix) {
		int[][] ret = new int[matrix[0].length][];
		for (int i = ret.length; --i >= 0;) {
			ret[i] = COLUMN(matrix, i);
		}
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for Database Operations:
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reads the Order of the Points of a single Plane from the current ResultSet
	 * @param RS the ResultSet to read from
	 * @param numPointsColumn 
	 * 		if positive Column that contains the Number of Points in this Plane
	 * 		if positive the Number of Points to read. 
	 * @param columnOffset the Column to start reading from when Cols == null
	 * @param columns the List of Column Indices to read, null when consecutive!
	 * @param Plane the Plane returned; when null, a new Plane is created
	 * @return the Plane read.
	 */
	final static public int[] READ_VECTOR(final ResultSet RS, final int numPointsColumn, final int columnOffset, int[] plane, final int[] columns)
		throws SQLException {
		if (!RS.next()) 
			return null;
		int len;
		if (plane != null) {
			len = plane.length;
			if (columns != null) {
				if (len > columns.length)
					len = columns.length;
			}
		} else {
			if (numPointsColumn >= 0) { //Number of Points given in the Table itself
				len = RS.getInt(numPointsColumn);
			} else if (numPointsColumn < -1) { // fixed Number of Points
				len = -numPointsColumn;
				if (columns != null) {
					if (len > columns.length)
						len = columns.length;
				} else {
					if (len > ((AResultSet) RS).getNumCols() - columnOffset)
						len = ((AResultSet) RS).getNumCols() - columnOffset;
				}
			} else { //numPointsColumn == -1 use the ResultSet Size
				len = ((AResultSet) RS).getNumCols() - columnOffset;
			}
			plane = new int[len];
		}
		int i = -1; //start in the correct Order
		try { //try to read as many Coordinates as possible!
			while (++i < len) {
				if (columns == null) { //read consecutive Values
					plane[i] = RS.getInt(i + columnOffset);
				} else {
					plane[i] = RS.getInt(columns[i]);
				}
			}
		} catch (final SQLException x) { //Resize the Array to exactly fit the Result.
			L.n().l(x);
			final int[] tmp = new int[i];
			System.arraycopy(plane, 0, tmp, 0, i);
			plane = tmp;
		}
		return plane;
	}

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public int[] READ_VECTOR(final java.sql.ResultSet RS, int numPointCol, int ColOffset, int[] Plane)
		throws java.sql.SQLException {
		return READ_VECTOR(RS, numPointCol, ColOffset, Plane, null);
	}

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public int[] READ_VECTOR(java.sql.ResultSet RS, int numPointCol, int ColOffset)
		throws java.sql.SQLException {
		return READ_VECTOR(RS, numPointCol, ColOffset, null, null);
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// Methoden f�r polynomiale Rechnungen
	////////////////////////////////////////////////////////////////////////////////

	/** Trims the whole Vector by the given Module.
	 * @see #TRIM_AT(int[], int, int)	 */
	final static public int TRIM_AT(final int[] vector, final int module) {
		return TRIM_AT(vector, module, vector.length); }

	/** Trims the first num Elements of the Vector by the given Module.
	 * @see #TRIM_AT(int[], int, int)	 */
	final static public int TRIM_AT(final int[] vector, final int module, final int num) {
		return TRIM_AT(vector, module, 0, num); }
	
	/**
	 * Trims the Elements of this Vector by the given Module. 
	 * Superfluous Values are shifted up the Vector. 
	 * @see #SHL_AT(int[], int) which works similarly
	 * Addition and Subtraction of Vectors 
	 * can lead to intermediate negative Element Values, 
	 * since borrows cannot be pre-calculated, 
	 * except by keeping a separate Sign and checking Argument Sizes. 
	 * Check if these are handled appropriately. 
	 * The canonical Representation of Values here 
	 * carries the Sign in the highest (nonzero) Element. 
	 * A canonic Representation is necessary for quickly comparing 
	 * Objects for Equality. 
	 * This Implementation is optimized for Speed, not Readability!
	 * @param vector the Vector to shift
	 * @param module the Module to cut the Vector Elements by
	 * @param num the Number of valid Elements in this Vector. 
	 * @return the Carry / Borrow of this Operation. 
	 * If the Carry is non-zero the Array does not fit, a new Vector should be created!
	 */	
	final static public int TRIM_AT(final int[] vector, final int module, final int start, final int stop) {
		int val, carryOver = 0;
		val = vector[start]; 
		for (int i = start; i < stop;) {
			//carryOver = Math.floor(val / module);
			if (val < 0) { //make vector[i] non-negative, i.e. canonic
				carryOver = ((val+1) / module)-1; //
			} else {
				carryOver = val / module; //
			}
			if (carryOver == 0) {
				if (++i >= stop) {
					return 0; }
				val = vector[i]; 
				continue; 
			}
			val -= carryOver*module;
			vector[i] = val;
			val = vector[++i] += carryOver; 
		}
		return carryOver;
	}
/*
		if (carryOver == 0) {
			return vector; }
		//Enlarge the Array
		final int[] ret = new int[vector.length+1]; 
		System.arraycopy(vector, 0, ret, 0, vector.length);
		ret[vector.length] = carryOver;
		return ret; 
	}
*/
	/**
	 * Both Vectors need to be normed, i.e. 
	 * -all Elements must be non-negative
	 * -the highest Element must be positive
	 * @see Comparable
	 * @see IOrderAble
	 * @param a positive Vector of integer Numbers
	 * @param b positive Vector of integer Numbers
     * @return the Sign of (a-b):
     *  a negative integer, zero, or a positive integer as vector a
     *		is less than, equal to, or greater than vector b.
	 */
	final static public int COMPARE_TO(final int[] a, final int aNum, final int[] b, final int bNum) {
		if (aNum > bNum) {
			return 1; } 
		if (aNum < bNum) {
			return -1; } 
		for (int i = aNum; --i >= 0;) {
			if (a[i] > b[i]) {
				return 1; } 
			if (a[i] < b[i]) {
				return -1; } 
		}
		return 0; 
	}

	////////////////////////////////////////////////////////////////////////////////
	/// Shifting Methoden 
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shifts the Bits of this Vector by the given Number of Bits to the right
	 * @param vector the Vector to shift
	 * @param module the Module to cut the Vector Elements by
	 * @param shift the number of Bits to shift by 
	 * @return an Array cointaining the shifted Vector
	 * if the Vector does not fit, it returns a new Vector
	 */
	final static public int[] SHR_AT(final int[] vector, final int module, final int shift) {
		if (shift < 0) {
			return SHL_AT(vector, module, -shift); }
		if (shift == 0) {
			return vector; }
		//Very expensive, can be made considerably faster by blocking by module!
		for(int i = shift; --i >= 0;) {
			SHR_AT(vector, module);
		}
		return vector;
	}

	/**
	 * Shifts the Bits of this Vector by the given Number of Bits to the right
	 * You can shift first and then do the Carryover in a second Sweep, though this is ineffective!
	 * @param vector the Vector to shift
	 * @param module the Module to cut the Vector Elements by
	 * @param shift the number of Bits to shift by 
	 * @return an Array cointaining the shifted Vector
	 * if the Vector does not fit, it returns a new Vector
	 */
	final static public int[] SHL_AT(final int[] vector, final int module, final int shift) {
		if (shift < 0) {
			return SHR_AT(vector, module, -shift); }
		if (shift == 0) {
			return vector; }
		//Very expensive, can be made considerably faster by blocking by module!
		for(int i = shift; --i >= 0;) {
			SHL_AT(vector, module);
		}
		return vector;
	}

	/**
	 * Shifts the Bits of this Vector by a single Bit to the left. 
	 * Unfortunately you cannot shift first and then do the Carryover in a second Sweep, 
	 * unless you work with an Offset at the lower Edge, which is confusing and slower!
	 * @param vector the Vector to shift
	 * @param shift the number of Bits to shift by 
	 * @return an Array cointaining the shifted Vector
	 * if the Vector does not fit, it returns a new Vector
	 */
	final static public int[] SHR_AT(final int[] vector, final int module) {
		boolean lowBitSet = false;
		for (int i = vector.length; --i >= 0;) {
			int val = vector[i];
			if (lowBitSet) {
				val += module; }
			lowBitSet = ((val & 1) == 1);
			vector[i] = val >> 1;
			//possibly shrink the Array 
		}
		return vector;
	}

	/**
	 * Shifts the Bits of this Vector by a single Bit to the right
	 * @param vector the Vector to shift
	 * @param shift the number of Bits to shift by 
	 * @return an Array cointaining the shifted Vector
	 * if the Vector does not fit, it returns a new Vector
	 */
	final static public int[] SHL_AT(final int[] vector, final int module) {
		boolean hiBitSet = false;
		for (int i = -1; ++i < vector.length;) {
			int val = vector[i] << 1; 
			if (hiBitSet) {
				val += 1; }
			hiBitSet = ((val & module) == module);
			vector[i] = val & (module-1);
		}
		if (!hiBitSet) {
			return vector; }
		//Enlarge the Array
		final int[] ret = new int[vector.length+1]; 
		System.arraycopy(vector, 0, ret, 0, vector.length);
		ret[vector.length] = 1;
		return ret; 
	}

	////////////////////////////////////////////////////////////////////////////////

	/** Rotates the whole Permutation left by 1 Element in Place.
	 * @return the Permutation rotated left by 1 Element in Place	  */
	final static public int[] ROL_AT(final int[] this_) {
		return ROL_AT(this_, 0, this_.length); }

	/** Rotates the given Range of the Permutation left by 1 Element in Place.
	 * @return the Permutation rotated left by 1 Element in Place	  */
	final static public int[] ROL_AT(final int[] this_, final int start, final int stop) {
		final int last = stop-1;
		final int carry= this_[start]; 
		System.arraycopy(this_, start+1, this_, start, last);
		this_[last] = carry; 
		return this_; 
	}

	/** Rotates the whole Permutation right by 1 Element in Place.
	 * @return the Permutation rotated left by 1 Element in Place	  */
	final static public int[] ROR_AT(final int[] this_) {
		return ROR_AT(this_, 0, this_.length); }

	/** Rotates the given Range of the Permutation right by 1 Element in Place.
	 * @return the Permutation rotated right by 1 Element in Place	  */
	final static public int[] ROR_AT(final int[] this_, final int start, final int stop) {
		final int last = stop-1;
		final int carry= this_[last]; 
		System.arraycopy(this_, start, this_, start+1, last);
		this_[start] = carry; 
		return this_; 
	}

	////////////////////////////////////////////////////////////////////////////////

	/** Returns the inverse Permutation in Place
	  * @return the inverse Permutation
	  * Cannot be calculated in Place!
	  */
	final static public int[] INVERSE(final int[] this_) {
		return INVERSE(this_, null, this_.length); }

	/** Returns the inverse Permutation in Place
	  * @return the inverse Permutation
	  * Cannot be calculated in Place!
	  */
	final static public int[] INVERSE(final int[] this_, final int thisLength) {
		return INVERSE(this_, null, thisLength); }

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	final static public int[] INVERSE(final int[] this_, final int[] ret) {
		return INVERSE(this_, ret, this_.length); }

	/** Returns the inverse Permutation in Place
	 * Assumes the Permutation is complete.
	 * With incomplete Permutations,
	 * most of the Table remains 0 and has to be replaced by i implicitly:
	 * if (ret[i] == 0) ret[i] = i;
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	final static public int[] INVERSE(final int[] this_, int[] ret, int maxValP1) {
		if((ret == null) || (ret.length < maxValP1)) {
			ret = new int[maxValP1]; } //defensive Programming
		if (maxValP1 >= this_.length) //if maxVal is given externally and not determined
			maxValP1  = this_.length; //this saves the repeated Test in the Loop, 
		while (--maxValP1 >= 0) //but still allows for a larger Return Array as requested by maxVal 
			ret[this_[maxValP1]] = maxValP1;
		return ret;
	}

	/** Reverts a complete Encoding Table for Bytes
	  * This is equivalent to inverting the Permutation
	  * in Class streamIO.Copy.Monoid.SetInteger.Permutation
	  */
	final static public int[] FULL_INVERSE(final int[] encoding) {
		return INVERSE(encoding, math.vector.VectorInt.MAX(encoding)); }

	/** Reverts a complete Encoding Table for Bytes
	  * This is equivalent to inverting the Permutation
	  * in Class streamIO.Copy.Monoid.SetInteger.Permutation
	  */
	final static public int[] FULL_INVERSE(final int[] encoding, final int maxChar) {
		return INVERSE(encoding, new int[maxChar + 1]); }

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	final static public int[] IDENTITY(final int length) {
		return IDENTITY(new int[length]); }

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	final static public int[] IDENTITY(final int[] this_) {
		return IDENTITY(this_, this_.length); }

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	final static public int[] IDENTITY(final int[] this_, int len) {
		while (--len >= 0) 
			this_[len] = len; 
		return this_;
	}

	/**
	 * Don't use this Method in Vector Operations, because a new Array is created.
	 * @return this Vector with the Elements permuted according to the given Permutation     
	 */
	final static public int[] PERMUTE(final int[] a, final int[] index) {
		return PERMUTE(null, new int[a.length], index);
	}

	/** Permutes the Elements of the given Vector into the (optionally provided) Result Array.
	 * @return this Vector with the Elements permuted according to the given Permutation     */
	final static public int[] PERMUTE(int[] ret, final int[] a, final int[] index) {
		if (ret == null)
			ret =  new int[index.length];
		for (int i = index.length; --i >= 0;) 
			ret[i] = a[index[i]];
		return ret;
	}

	/** 
	 * Don't use this in Vector Operations, because temporary Array is created.
	 * Permutation cannot be done in Place with O(n) Operations, 
	 * as you can see trying the Counter- Example [4,5,3,2,1,0]  
	 * @return this Vector with the Elements permuted according to the given Permutation     
	 */
	final static public int[] PERMUTE_AT(final int[] a, final int[] index) {
		final int[] tmp = new int[a.length];
		PERMUTE(tmp, a, index);
		System.arraycopy(tmp, 0, a, 0, a.length);
		return a;
	}
	
	/** this simple implementation does not work! see [4,5,3,2,1,0] 
	final static public int[] PERMUTE_AT(final int[] a, final int[] perm) {
		for (int j, k = a.length; --k > 0; ) { //first row is not modified, because L[1,1]=1
			if (perm[k] == k) 
				continue; //Undo the Row Permutations! 
			final int tmp = a[k]; a[k] = a[j = perm[k]]; a[j] = tmp;
		}
		return a;
	}
	*/

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for dynamic growing Array Operations
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the Value at the given Position in the Array
	 * Returns a resized (larger OR smaller) Copy of the given Array
	 * filled with the given Value at the given Position.
	 */
	final static public int[] SET_AT(int[] arr, final int pos_, final int value_) {
		if (pos_ >= arr.length) 
			arr = RESIZE(pos_+1, arr); 
		arr[pos_] = value_;
		return arr;
	}

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	final static public int[] SET_SIZE(final int[] arr, final int newExactSize) {
		return RESIZE(arr, newExactSize, arr.length);
	}

	/** Returns a resized (larger) Copy of the given Array */
	final static public int[] SET_CAPACITY(final int newMinSize, final int[] arr) {
		return RESIZE(arr, ENLARGED_CAPACITY(arr.length, DEFAULT_CAPACITY_INCR, newMinSize), arr.length);
	}

	/** Returns a resized (larger OR smaller) Copy of the given Array */
//	final static public int[] RESIZE(final int[] arr, final int newExactSize) {
//		return RESIZE(arr, newExactSize, arr.length); }

	/** Returns a resized (larger) Copy of the given Array */
	final static public int[] RESIZE(final int newMinSize, final int[] arr) {
		return RESIZE(arr, ENLARGED_CAPACITY(arr.length, DEFAULT_CAPACITY_INCR, newMinSize), arr.length); }

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	final static public int[] RESIZE(final int[] arr, final int newSize, int numToRetain) {
		final int[] ret = new int[newSize];
		if (arr != null)
		if (numToRetain > arr.length) 
			numToRetain = arr.length;
		if (numToRetain > ret.length) 
			numToRetain = ret.length;
		if (numToRetain > 0) 
			System.arraycopy(arr, 0, ret, 0, numToRetain);
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Compares the given Range of both Arrays Element by Element for Equality.
	 * @see Object#equals(java.lang.Object) 	 */
	final static public boolean EQUALS(final int[] a, final int[] b, final int start, final int stop) {
		for (int i = stop; --i >= start; ) {
			if (a[i] != b[i]) 
				return false; 
		}
		return true; 
	}
	
	/** Compares both Arrays for Equality, treating missing trailing Elements as 0.
	 * @see Object#equals(java.lang.Object) 	 */
	final static public boolean EQUALS(final int[] a, final int[] b) {
		if (a == b) {
			return true; }
		if (a == null) {
			return IS_ZERO(b); }
		if (b == null) {
			return IS_ZERO(a); }
		if (a.length > b.length) {
			return EQUALS(a, b, 0, b.length) && IS_ZERO(a, b.length, a.length);
		} else {
			return EQUALS(a, b, 0, a.length) && IS_ZERO(b, a.length, b.length);
		}
	}
	
	/** Returns a Copy of the given Array */
	final static public int[] COPY(final int[] arr, int length) {
		return COPY_AT(null, arr, 0, length);
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY(final int[] arr) {
		return COPY(arr, arr.length); 
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY(final float[] arr) {
		return COPY(arr, arr.length); 
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY(final float[] arr, final int length) {
		return COPY_AT(null, arr, 0, length);
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY(final double[] arr) {
		return COPY(arr, arr.length); 
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY(final double[] arr, final int length) {
		return COPY_AT(null, arr, 0, length);
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY_AT(final int[] this_, final int[] arr) {
		return COPY_AT(this_, arr, 0, arr.length); 
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY_AT(int[] this_, final int[] arr, final int start, final int stop) {
		if (this_ == null) //be error tolerant!
			this_  = new int[stop]; //could also skip or throw an Exception!
		System.arraycopy(arr, start, this_, start, Math.min(arr.length, stop)); //higher Values defaulted to 0! 
		return this_;
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY_AT(final int[] this_, final double[] arr) {
		return COPY_AT(this_, arr, 0, arr.length);
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY_AT(final int[] this_, final float[] arr) {
		return COPY_AT(this_, arr, 0, arr.length);
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY_AT(int[] this_, final double[] arr, final int start, final int stop) {
		if (this_ == null) //be error tolerant!
			this_ = new int[stop]; 
		for (int i = arr.length; --i >= 0; ) 
			this_[i] = (int) arr[i];
		//System.arraycopy(arr, Start, this_, Start, Stop); //ArrayTypeException!
		return this_;
	}

	/** Returns a Copy of the given Array */
	final static public int[] COPY_AT(int[] this_, float[] arr, int start, int stop) {
		if (this_ == null) //be error tolerant!
			this_ = new int[stop]; 
		for (int i = stop; --i >= start;) 
			this_[i] = (int) arr[i];
		//		System.arraycopy(arr, start, this_, start, stop); //ArrayTypeException!
		return this_;
	}

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public int[] SET_DIM_AT(final int[] a, final int dim) {
		if (a.length == dim) 
			return a;
		int[] ret = new int[dim];
		System.arraycopy(a, 0, ret, 0, a.length);
		Arrays.fill(ret, a.length, dim, 0);
		return a;
	}

	/** Sets all Elements of the given Array to 0.
	 * @return the given Array with all Elements set to 0. 	 */
	final static public int[] ZERO_AT(final int[] ret) {
		return ZERO_AT(ret, 0, ret.length);
	}

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag,
	 * i.e. a[dim] = diag and a[j] = 0 otherwise.
	 */
	final static public int[] DIAG_AT(final int[] a, final int diag, final int dim) {
		Arrays.fill(a, 0);
		a[dim] = diag;
		return a;
	}

	/** Sets the given Range of the Array to 0.
	 * @return the given Array with the Elements from Start (inclusive) to Stop (exclusive) set to 0. 	 */
	final static public int[] ZERO_AT(final int[] ret, final int start, final int stop) {
		java.util.Arrays.fill(ret, start, stop, 0);
		return ret;
	}

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag.
	 * i.e. a[dim] = 1 and a[j] = 0 otherwise.
	 */
	final static public int[] ONE_AT(final int[] a, final int dim) {
		return DIAG_AT(a, 1, dim);
	}

	/** Sets all Elements of the given Array to 1.
	 * @return the given Array with all Elements set to 1. 	 */
	final static public int[] ONE_AT(final int[] ret) {
		return ONE_AT(ret, 0, ret.length);
	}

	// TODO: LOGIC: fills with 0 instead of 1 (copy-pasted from ZERO_AT); same defect as VectorChar/VectorLong/VectorShort oneAt.
	/** Sets the given Range of the Array to 1.
	 * @return the given Array with the Elements from Start (inclusive) to Stop (exclusive) set to 0. 	 */
	final static public int[] ONE_AT(final int[] ret, final int start, final int stop) {
		java.util.Arrays.fill(ret, start, stop, 0);
		return ret;
	}

	/** Fills the whole Array with the given Value.
	 * @return the given Array with all Elements set to the given Value. 	 */
	final static public int[] FILL_AT(final int[] ret, final int val) {
		return FILL_AT(ret, val, 0, ret.length);
	}

	/**
	 * Fills the given Range of the Array with the given Value.
	 * @return the given Array with the Elements from Start (inclusive)
	 * to Stop (exclusive) set to the given Value.
	 */
	final static public int[] FILL_AT(final int[] ret, final int val, final int start, final int stop) {
		java.util.Arrays.fill(ret, start, stop, val);
		return ret;
	}

	/** Checks whether all Values in the Array are 0.
	 * @return The Sum of all Values in the Array. 	 */
	final static public boolean IS_ZERO(final int[] arr) {
		return IS_ZERO(arr, 0, arr.length);
	}

	/** Checks whether all Values in the given Range of the Array are 0.
	 * @return The Sum of all Values in the Array.	 */
	final static public boolean IS_ZERO(final int[] arr, final int start, int stop) {
		while (--stop >= start) {
			if (arr[stop] != 0) 
				return false;
		}
		return true;
	}

	/**
	 * Checks whether this Vector is a Unity Vector in the given Dimension
	 */
	final static public boolean IS_ONE(final int[] Row, final int dim) { //Assume a square Matrix
		int j = Row.length;
		while (--j >= 0) { //Use an Epsilon here
			if (j == dim) {
				if (Row[j] != 1) 
					return false;
			} else {
				if (Row[j] != 0) 
					return false;
			}
		}
		return true;
	}

	/**
	 * The Order can change in each individual Dimension!
	 * @return true when the middle Vector is between the left and right Vector.
	 */
	final static public boolean BETWEEN(int[] left, int[] mid, int[] right) {
		int i = left.length;
		while (--i >= 0) {
			if ((left[i] < mid[i]) != (right[i] > mid[i])) 
				return false;
		}
		return true;
	}

	/**
	 * Determines the Minimum and Maximum Value
	 * and sorts them into the first and second Argument.
	 */
	final static public void ORDER_AT(int[] inOutMin, int[] inOutMax) {
		int tmp;
		int i = inOutMin.length;
		while (--i >= 0) {
			if ((tmp = inOutMin[i]) < inOutMax[i]) 
				continue;
			inOutMin[i] = inOutMax[i];
			inOutMax[i] = tmp;
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Creates a new Array of random Length (up to maxLength) filled with random Values.
	 * @see streamIO.copy.IICopyAble#randomizeAt()	 */
	final static public int[] RANDOM(final int maxLength) {
		return RANDOMIZE_AT(new int[(int) (RandomQuick.NEXT_INT(maxLength))]); }

	/** Creates a new Array of the given Length filled with random Values.
	 * @see streamIO.copy.IICopyAble#randomizeAt()	 */
	final static public int[] RANDOMIZED(final int length) {
		return RANDOMIZE_AT(new int[length]); }
							
	/** Randomizes all the Values of this Vector
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Assumes a rectangular Array. 	 */
	final static public int[] RANDOMIZE_AT(final int[] arr) {
		for (int j = arr.length; --j >= 0;) 
			arr[j] = RandomQuick.NEXT_INT(); 
		return arr;
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
	final static public int[] SUMM_F_AT(final int[] items) { return SUMM_F_AT(items, 0, items.length); }
	
	/** calculates the (Forward) Difference Vector in Place 
	 * This keeps the Vectors usable after Differentiation 
	 * and the leftover Elements can be reused on Integration. 
	 * Unfortunately the Sign is flipped compared to the (more common) Backward Differences. 
	 * Simply use NEG_AT to correct this, unless you divide by another  Forward Difference. 
	 * @param items the Vector to differentiate
	 */
	final static public int[] DIFF_F_AT(final int[] items) { return DIFF_F_AT(items, 0, items.length); }
	
	/** Summen werden r�ckw�rts gebildet! 
	 * Dadurch werden die von der Diff-Operation �brig gebliebenen Elemente widerverwendet 
	 * und die Vektoren bleiben einsatzbereit. 
	 * 
	 * @param items the Values to aggregate 
	 * @param start the first Index (exclusive)
	 * @param stop the last Index (exclusive)
	 */
	final static public int[] SUMM_F_AT(final int[] items, final int start, final int stop) {
		//int tmp = items[start]; 
		for (int i = start;  ++i < stop; ) 
			//tmp = (items[i] += tmp); //items[i-1]; 
			items[i] += items[i-1]; 
		return items; 
	}
	
	/** calculates the (forward) Difference Vector in Place 
	 * This keeps the Vectors left-aligned, usable after Differentiation 
	 * and the leftover Elements can be reused on Integration. 
	 * Unfortunately the Sign is flipped compared to the (more common) Backward Differences. 
	 * Simply use NEG_AT to correct this, unless you divide by another  Forward Difference. 
	 * @param items the Vector to differentiate
	 * @param start the first Index (exclusive)
	 * @param stop the last Index (exclusive)
	 */
	final static public int[] DIFF_AT(final int[] items, final int start, final int stop) {
		DIFF_F_AT(items, start, stop); 
		return ROL_AT(items, 0, stop); //to acquire a meaning ful item[0], rotate it to the Back
		/* //alternatively do it in one Sweep like here:  
		final int i_start = items[start]; 
		--stop; 
		for (int i = start-1; ++i < stop;) //this leaves items[0] unchanged
			items[i] = items[i+1] - items[i]; 
		items[stop] = i_start;
		return items; 
		*/
	}
	
	/**
	 * Calculates the (Backward) Difference Vector in Place, leaving items[start] unchanged.
	 * @param items
	 * @param start
	 * @param stop
	 */
	public static int[] DIFF_F_AT(final int[] items, final int start, final int stop) {
		for (int i = stop; --i > start;) //this leaves items[0] unchanged
			items[i] -= items[i-1];
		return items; 
	}

	/** Summen werden r�ckw�rts gebildet! 
	 * Dadurch werden die von der Diff-Operation �brig gebliebenen Elemente widerverwendet 
	 * und die Vektoren bleiben einsatzbereit. 
	 * 
	 * @param items the Values to aggregate 
	 * @param start the first Index (exclusive)
	 * @param stop the last Index (exclusive)
	 */
	final static public int[] SUMM_AT(final int[] items, final int start, int stop) {
		ROR_AT(items, 0, stop); //to acquire a meaningful start Value, rotate the last Item to the Front
		return SUMM_F_AT(items, start, stop);
		/* //alternatively do it in one Sweep like here:  
		--stop;
		int tmp = items[stop]; 
		for (int i = start-1;  ++i < stop; ) { //implicitly performs a Rotation!
			final int tmp1 = items[i]; items[i] = tmp; tmp += tmp1; //
		}
		items[stop] = tmp;
		return items; 
		*/ 
	}
	
	/** Summen werden r�ckw�rts gebildet! 
	 * Dadurch werden die von der Diff-Operation �brig gebliebenen Elemente widerverwendet 
	 * und die Vektoren bleiben einsatzbereit. 
	 * 
	 * @param items the Values to aggregate 
	 */
	final static public int[] SUMM_AT(final int[] items) { return SUMM_AT(items, 0, items.length); }
	
	/** calculates the (Forward) Difference Vector in Place 
	 * This keeps the Vectors usable after Differentiation 
	 * and the leftover Elements can be reused on Integration. 
	 * Unfortunately the Sign is flipped compared to the (more common) Backward Differences. 
	 * Simply use NEG_AT to correct this, unless you divide by another  Forward Difference. 
	 * @param items the Vector to differentiate
	 */
	final static public int[] DIFF_AT(final int[] items) { return DIFF_AT(items, 0, items.length); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods returning a single Number from the Array
	////////////////////////////////////////////////////////////////////////////////

	/** Sums up all Values of the Array.
	 * @return The Sum of all Values in the Array. 	 */
	final static public long SUM(final int[] arr) { return SUM(arr, arr.length, 0); }

	/** Sums up the first stop Values of the Array.
	 * @return The Sum of all Values in the Array. 	 */
	final static public long SUM(final int[] arr, final int stop) {
		return SUM(arr, stop, 0); }

	/** Sums up the given Range of Values in the Array.
	 * @return The Sum of all Values in the Array.	 */
	final static public long SUM(final int[] arr, int stop, final int start) {
		if (stop > arr.length)  //Error tolerant
			stop = arr.length; 
		if (start >= stop) 
			return 0; 
		long sum = arr[--stop]; //0;
		while (--stop >= start) 
			sum += arr[stop];
		return sum;
	}
	
	/** Multiplies together all Values of the Array.
	 * @return The Product of all Values in the Array. 	 */
	final static public long PROD(final int[] arr) { return PROD(arr, arr.length, 0); }

	/** Multiplies together the first stop Values of the Array.
	 * @return The Product of all Values in the Array. 	 */
	final static public long PROD(final int[] arr, final int stop) {
		return PROD(arr, stop, 0); }

	/** Multiplies together the given Range of Values in the Array.
	 * @return The Product of all Values in the Array.	 */
	final static public long PROD(final int[] arr, int stop, final int start) {
		if (stop > arr.length)  //Error tolerant
			stop = arr.length; 
		if (start >= stop) 
			return 1;
		long Prod = arr[--stop]; //1;
		while (--stop >= start) 
			Prod *= arr[stop];
		return Prod;
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Minimum Values of the Array.
	 */
	final static public int[] MIN2VAL(final int[] arr, final int[] ret) {
		return MIN2VAL(arr, 0, arr.length, ret);
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first two Minimum Values of the Array.
	 */
	final static public int[] MIN2POS(final int[] arr, final int[] ret) {
		return MIN2POS(arr, 0, arr.length, ret);
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Minimum Values of the Array.
	 */
	final static public int[] MIN2VAL(final int[] arr, final int start, final int stop, final int[] ret) {
		final int[] pos = new int[ret.length];
		MIN2POS(arr, start, stop, pos);
		int i = ret.length;
		if (i > 2) 
			i = 2;
		while (--i >= 0) 
			ret[i] = arr[pos[i]];
		return ret;
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first two Minimum Values of the Array.
	 */
	final static public int[] MIN2POS(final int[] arr, final int start, int stop, final int[] ret) {
		int inMin, iMin = inMin = -1; //the n Values contain the higher Maximum!
		int nMin, Min = nMin = Integer.MAX_VALUE;
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

	/**Swaps the Columns of this Tensor in Place	 */
	final static public int[] SWAP_AT(final int[] a, final int dim1, final int dim2) {
		final int tmp = a[dim1]; a[dim1] = a[dim2]; a[dim2] = tmp;
		return a;
	}
	
	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Position of the Minimum Value of the Array.
	 */
	final static public int MIN_POS(final int[] arr) {
		return MIN_POS(arr, 0, arr.length); }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public int MIN_POS(final int[] arr, final int Start, int Stop) {
		int iMin = -1;
		int Min = Integer.MAX_VALUE;
		while (--Stop >= Start) {
			if (Min > arr[Stop]) 
				Min = arr[iMin = Stop];
		}
		return iMin;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int MAX_VAL(final int[] arr) {
		return arr[MAX_POS(arr)];
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int MAX_POS(final int[] arr) {
		return MAX_POS(arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int MAX_VAL(final int[] arr, final int Start, final int Stop) {
		return arr[MAX_POS(arr, Start, Stop)];
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Index of the Maximum Value of the Array.
	 */
	final static public int MAX_POS(final int[] arr, final int Start, int Stop) {
		int iMax = -1;
		int Max = Integer.MIN_VALUE;
		while (--Stop >= Start) {
			if (Max < arr[Stop]) 
				Max = arr[iMax = Stop];
		}
		return iMax;
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return up to the first two Maximum Values of the Array.
	 */
	final static public int[] MAX_2_VAL(final int[] arr, final int[] ret) {
		return MAX_2_VAL(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first up to two Maximum Values of the Array.
	 */
	final static public int[] MAX_2_POS(final int[] arr, final int[] ret) {
		return MAX_2_POS(arr, 0, arr.length, ret); }

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Maximum Values of the Array.
	 */
	final static public int[] MAX_2_VAL(final int[] arr, final int Start, int Stop, final int[] ret) {
		int[] pos = new int[ret.length];
		MAX_2_POS(arr, Start, Stop, pos);
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
	final static public int[] MAX_2_POS(final int[] arr, final int Start, int Stop, final int[] ret) {
		int inMax, iMax = inMax = -1; //the n Values contain the higher Maximum!
		int nMax, Max = nMax = Integer.MIN_VALUE;
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
	final static public int[] MIN_MAX_VAL(final int[] arr) {
		return MIN_MAX_VAL(arr, new int[2]); }
	
	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN_MAX_VAL(final int[] arr, int[] minMax, final int start, final int stop) {
		if (minMax == null)
			minMax =  new int[2]; 
		return MIN2MAX2VAL(arr, start, stop, minMax); }
	
	/**
	 * Determines the Indices of the Minimum and the Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN_MAX_POS(final int[] arr) { return MIN_MAX_POS(arr, 2); }
	
	/**
	 * Determines the Indices of the Minimum and the Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN_MAX_POS(final int[] arr, final int numItems) {
		return MIN2MAX2POS(arr, new int[numItems]); }
	
	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN2MAX2VAL(final int[] arr) {
		return MIN2MAX2VAL(arr, 0, arr.length, new int[2]); }

	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN2MAX2VAL(final int[] arr, final int[] minMax) {
		return MIN2MAX2VAL(arr, 0, arr.length, minMax); }

	/**
	 * Determines the Indices of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN2MAX2VAL(final int[] arr, final int start, final int stop, final int[] minMax) {
		MIN2MAX2POS(arr, start, stop, minMax); //reuse the Parameter
		for(int i = minMax.length; --i >= 0; ) 
			minMax[i] = arr[minMax[i]];
		return minMax; 
	}
	
	/**
	 * Determines the Values of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MIN_MAX_VAL(final int[] arr, final int[] ret) {
		return MIN2MAX2VAL(arr, ret); }
	
	/**
	 * Determines the two Minimum and two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with the Indices of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN2MAX2POS(final int[] arr) {
		return MIN2MAX2POS(arr, new int[2]); }
	
	/**
	 * Determines the Indices of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with the Indices of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN2MAX2POS(final int[] arr, int[] MinMax) {
		return MIN2MAX2POS(arr, 0, arr.length, MinMax); }
	
	/**
	 * Determines the Indices of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with the Indices of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] MIN2MAX2POS(final int[] arr, final int start, final int stop, int[] MinMax) {
		int  iMin,  iMax;
		int inMin, inMax;
		int  Min,  Max;
		int nMin, nMax;
		boolean xMax = (MinMax.length > 2);
		boolean xMin = (MinMax.length > 3);
		int i = stop; 
		if (((stop-start) | 1) == 1) { //odd?
			iMin = iMax = inMin = inMax = --i;
			 Min =  Max =  nMin =  nMax = arr[i];
		} else { //a bit Overhead, but easier!
			iMin = iMax = inMin = inMax = -1; //cannot jump out earlier!
			Min = nMin = Integer.MAX_VALUE;
			Max = nMax = Integer.MIN_VALUE;
		}
		int  tMin,  tMax,  tmp;
		int iTMin, iTMax, iTmp;
		while (i > start+1) {
				if ((tMin = arr[iTMin = --i]) > //first compare Args
					(tMax = arr[iTMax = --i])) {
				 tmp =  tMin;  tMin =  tMax;  tMax =  tmp; 
				iTmp = iTMin; iTMin = iTMax; iTMax = iTmp;
			}
			if (Min > tMin) { //then compare tMin and tMax to Min and Max
				if (xMin && (nMin > tMin)) { //even larger than the first Max?
					Min = nMin;
					iMin = inMin;
					nMin = tMin;
					inMin = iTMin;
				} else {
					Min = tMin;
					iMin = iTMin;
				}
			}
			if (Max < tMax) { //this saves 1/4 of the Comparisons
				if (xMax && (nMax < tMax)) { //even larger than the first Max?
					Max = nMax;
					iMax = inMax;
					nMax = tMax;
					inMax = iTMax;
				} else {
					Max = tMax;
					iMax = iTMax;
				}
			}
			if (xMax && (Max < tMin)) { //even larger than the first Max?
				 Max =  tMin;
				iMax = iTMin;
			}
			if (xMin && (Min > tMax)) { //even larger than the first Max?
				 Min =  tMax;
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
	
	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public long MIN_VAL(final long[] arr) { return MIN_VAL(arr, 0, arr.length); }
	
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
	 * @return Minimum Value of the Array.
	 */
	final static public int MIN_VAL(final int[] arr) { return MIN_VAL(arr, 0, arr.length); }
	
	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public int MIN_VAL(final int[] arr, final int Start, int Stop) {
		int Min = Integer.MAX_VALUE;
		while (--Stop >= Start) {
			if (Min > arr[Stop]) 
				Min = arr[Stop];
		}
		return Min;
	}
	
	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int MAX(final int[] arr) {
		return MAX(arr, 0, arr.length); }

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int MAX(int[] arr, int Start, int Stop) {
		int Max = Integer.MIN_VALUE;
		while (--Stop >= Start) {
			if (Max < arr[Stop]) 
				Max = arr[Stop];
		}
		return Max;
	}

	/**
	  * Calculates the Euclidean Norm (Length) of the given Vector.
	  * @return the Norm of the given Array
	  */
	final static public double NORM(final int[] arr) {
		return Math.sqrt(NORM_SQR(arr, arr.length));
	}

	/**
	  * Calculates the squared Euclidean Norm of the whole Vector.
	  * @return the squared Norm of the given Array
	  */
	final static public long NORM_SQR(final int[] arr) {
		return NORM_SQR(arr, arr.length);
	}

	/**
	  * This Value can well exceed the Range of valid Numbers,
	  * but that should be avoided anyway by renorming.
	  * Accuracy is not affected when using int Point Numbers.
	  *
	  * @return the squared Norm of the given Array
	  */
	final static public long NORM_SQR(final int[] arr, int len) {
		long norm = 0; //Calculate the Norm
		while (--len >= 0) 
			norm += arr[len] * arr[len]; //sqr(arr[len]); }
		return norm;
	}

	/**
	  * Calculates the Euclidean Distance between the given Arrays.
	  * @param arr1 first  Vector, not modified.
	  * @param arr2 second Vector, not modified.
	  * @return the squared Norm of the Distance between the given Arrays
	  */
	final static public double DIST(final int[] arr1, final int[] arr2) {
		return Math.sqrt(DIST_SQR(arr1, arr2));
	}

	/**
	  * Calculates the squared Euclidean Distance between the given Arrays.
	  * @param arr1 first  Vector, not modified.
	  * @param arr2 second Vector, not modified.
	  * @return the squared Norm of the Distance between the given Arrays
	  */
	final static public long DIST_SQR(final int[] arr1, final int[] arr2) {
		long diff, norm = 0; //Calculate the Norm
		int i = arr1.length;
		while (--i >= 0) {
			diff = arr1[i] - arr2[i];
			norm += diff * diff;
		}
		return norm;
	}

	/**
	  * Calculates the Manhattan (Taxicab) Distance between the given Arrays.
	  * @param arr1 first  Vector, not modified.
	  * @param arr2 second Vector, not modified.
	  * @return the absolute Norm of the Distance between the given Arrays
	  */
	final static public int DIST_ABS(final int[] arr1, final int[] arr2) {
		int diff, norm = 0; //Calculate the Norm
		for(int i = arr1.length; --i >= 0;) {
			if (0 < (diff = arr1[i] - arr2[i])) {
				norm += diff;
				continue;
			}
			norm -= diff;
		}
		return norm;
	}

	/**
	  * Calculates the Manhattan Distance between the given Arrays, also filling in the Difference Vector.
	  * @param diff is an Output Parameter being filled with the Difference Vector.
	  * @return the squared Norm of the given Array
	  */
	final static public int DIFF_NORM_ABS(final int[] arr1, final int[] arr2, final int[] diff) {
		int dif, norm = 0; //Calculate the Norm
		for (int i = arr1.length; --i >= 0;) {
			//			norm+=Math.abs(diff[i] = arr1[i]-arr2[i]); }
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
	  * @return the squared Norm of the given Array
	  */
	final static public int NORM_ABS(final int[] arr) {
		int a, norm = 0; //Calculate the Norm
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
	final static public int GET_AT(final int[] a, final int index) {
		return GET_AT(a, index, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public int GET_AT(final int[] a, final int index, final int stop) {
		return GET_AT(a, index, 0, stop); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public int GET_AT(final int[] a, final int index, final int defaultValue, final int stop) {
		return GET_AT(a, index, defaultValue, stop, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public int GET_AT(final int[] a, final int index, final int defaultValue, final int stop, final int start) {
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
	final static public int[] GET_AT(final int[] a, final VectorInt index) {
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
	final static public int[] GET_AT(final int[] a, final VectorInt index, int[] ret) {
		return GET_AT(a, index.items, ret, index.itemCount); 
	}
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public int[] GET_AT(final int[] a, final int[] index) {
		return GET_AT(a, index, null); } //duplicate Method Signature. 
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public int[] GET_AT(final int[] a, final int[] index, final int[] ret) {
		return GET_AT(a, index, ret, index.length); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public int[] GET_AT(final int[] a, final int[] index, final int[] ret, int stop) {
		return GET_AT(a, index, ret, stop, 0); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public int[] GET_AT(final int[] a, final int[] index, int[] ret, final int stop, final int start) {
		if((ret == null) || (ret.length < stop))
			ret = new int[stop];
		//else if (ret.length > stop) //rather leave the Values alone?!?
		//	Arrays.fill(ret, stop, ret.length, 0); 
		for(int i = stop; --i >= start; )
			ret[i] = (index[i] < a.length) ? a[index[i]] : 0; 
		return ret;
	}
	
	/**
	  * By Definition Elements outside the Array are 0
	  * @return the scalar Product of the given Arrays up to the given Length.
	  */
	final static public long MAP(final int[] arr1, final int[] arr2) {
		int len = arr1.length;
		if (len > arr2.length) 
			len = arr2.length;
		//use the Minimum, because higher Elements are assumed to be 0.
		return MAP(arr1, arr2, 0, len);
	}

	/**
	  * By Definition Elements outside the Array are 0
	  * @return the scalar Product of the given Arrays up to the given Length.
	  */
	final static public long MAP(final int[] arr1, final int[] arr2, final int start, int stop) {
		long ret = 0;
		while (--stop >= start) 
			ret += arr1[stop] * arr2[stop];
		return ret;
	}

	/**
	  * Calculates the Maximum over the Element-wise Minima of the two Vectors.
	  * @return the Scalar Product of the two Vectors.
	  */
	final static public int MAX_MIN_PROD(final int[] a, final int[] arg) {
		return MAX_MIN_PROD(a, arg, 0, arg.length);
	}

	/**
	  * Calculates the Maximum over the Element-wise Minima of the given Range of both Vectors.
	  * @return the MaxMin Product of the two Vectors.
	  */
	final static public int MAX_MIN_PROD(final int[] a, final int[] arg, final int start, int stop) {
		int x, y, max = Integer.MIN_VALUE; //FALSE; //can also start with any lower Value!
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
	public static int[] NORMAL(
		final int[] diff1,
		final int[] diff2,
		final int[] p0,
		final int[] p1,
		final int[] p2, 
		final boolean normalize) {
		SUB(diff1, p1, p0);
		SUB(diff2, p2, p0);
		final int[] prod = MUL_CROSS(diff1, diff2);
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
	public static int[] NORMAL(
		final int[] p0,
		final int[] p1,
		final int[] p2, 
		final boolean normalize) {
		return NORMAL(null, null, p0, p1, p2, normalize); 
	}

	/** Cross Product in Place	 */
	final static public int[] MUL_CROSS_AT(final int[] ths, final int[] arg) {
		return COPY_AT(ths, MUL_CROSS(ths, arg));
	}

	/** Cross Product in R^3 */
	final static public int[] MUL_CROSS(final int[] ths, final int[] arg) {
		int end = 0;
		final int[] result = new int[3];
		if (ths.length > 3)
			throw new ArrayIndexOutOfBoundsException();
		if (arg.length > 3)
			throw new AbstractMethodError();
		if (ths.length < 2) {
			// TODO: LOGIC: result is a new int[3] (valid indices 0-2); result[3] throws ArrayIndexOutOfBoundsException. Likely intended result[2].
			result[3] = ths[0] * arg[1];
			return result;
		}
		if (arg.length < 2) {
			// TODO: LOGIC: result is a new int[3] (valid indices 0-2); result[3] throws ArrayIndexOutOfBoundsException. Likely intended result[2].
			result[3] = -arg[0] * ths[1];
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
	
	////////////////////////////////////////////////////////////////////////////
	/// NEG Operation: -x
	////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Negates all Elements of the given Array in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] NEG_AT(final int[] ret) { return NEG_AT(ret, 0, ret.length); }

	/**
	  * Negates the given Range of the Array in Place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] NEG_AT(final int[] ret, final int start, final int stop) {
		return NEG(ret, ret, start, stop);
	}

	/**
	  * Negates the given Range of x into ret (or a new Array, if ret does not fit).
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] NEG(int[] ret, final int[] x, final int start, int stop) {
		if((ret == null) || (ret.length < stop))
			ret = new int[x.length]; 
		while(--stop >= start) 
			ret[stop] = -x[stop];
		return ret;
	}

	/**
	  * Negates the whole Array into a new Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] NEG(final int[] x) {
		return NEG(null, x, 0, x.length); }

	/**
	  * Negates the given Range of the Array into a new Array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] NEG(final int[] x, final int start, final int stop) {
		return NEG(null, x, start, stop); }

	////////////////////////////////////////////////////////////////////////////
	/// CPL Operation: ~x = -x-1
	////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Applies the binary Complement (~x) to all Elements of the given Array in Place.
	  * @return the binary Complement of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] CPL_AT(final int[] ret) { return CPL_AT(ret, 0, ret.length); }

	/**
	  * Applies the binary Complement (~x) to the given Range of the Array in Place.
	  * @return the binary Complement of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] CPL_AT(final int[] ret, final int start, final int stop) {
		return CPL(ret, ret, start, stop);
	}

	/**
	  * Applies the binary Complement (~x) to the given Range of x into ret.
	  * @return the binary Complement of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] CPL(int[] ret, final int[] x, final int start, int stop) {
		if((ret == null) || (ret.length < stop))
			ret = new int[x.length]; 
		while(--stop >= start) 
			ret[stop] = ~x[stop];
		return ret;
	}

	/**
	  * Applies the binary Complement (~x) to the whole Array into a new Array.
	  * @return the binary Complement of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] CPL(final int[] x) {
		return CPL(null, x, 0, x.length); }

	/**
	  * Applies the binary Complement (~x) to the given Range of the Array into a new Array.
	  * @return the binary Complement of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] CPL(final int[] x, final int start, final int stop) {
		return CPL(null, x, start, stop); }

	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] ABS_AT(final int[] ret) {
		return ABS_AT(ret, 0, ret.length); }

	/**
	  * Replaces the given Range of the Array with the absolute Value of each Element in Place.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] ABS_AT(int[] ret, int start, int stop) {
		int tmp; //Calculate the Norm
		while (--stop >= start) {
			if (0 <= (tmp = ret[stop])) 
				continue;
			ret[stop] = -tmp;
		}
		return ret;
	}
	
	/** 
	 * returns the Counts of the different Values e.g. for a Histogram
	 * @param ret the counts (for Incrementation if already counted)
	 * @param a the Values to count (Values outside the Bounds are not counted) 
	 * @return the Counts of the different Values e.g. for a Histogram
	 */
	final static public int[] COUNT(final int[] a) {
		return COUNT(a, null); }
	
	/** 
	 * returns the Counts of the different Values e.g. for a Histogram
	 * @param a the Values to count (Values outside the Bounds are not counted) 
	 * @param max the maximum Value to scale with  
	 * @return the Counts of the different Values e.g. for a Histogram
	 */
	final static public int[] COUNT(final int[] a, final int max) {
		return COUNT(null, 0, max, a, 0, a.length); }
	
	/** 
	 * returns the Counts of the different Values e.g. for a Histogram
	 * @param ret the counts (for Incrementation if already counted)
	 * @param a the Values to count (Values outside the Bounds are not counted) 
	 * @return the Counts of the different Values e.g. for a Histogram
	 */
	final static public int[] COUNT(final int[] a, final int[] minMax) {
		return COUNT(a, minMax, 0, a.length); }
	
	/** 
	 * returns the Counts of the different Values e.g. for a Histogram
	 * @param a the Values to count (Values outside the Bounds are not counted) 
	 * @param start the first value to count (inclusive) 
	 * @param stop the last value to count (exclusive) 
	 * @return the Counts of the different Values e.g. for a Histogram
	 */
	final static public int[] COUNT(final int[] a, final int[] minMax, final int start, final int stop) {
		VectorInt.MIN_MAX_VAL(a, minMax, start, stop); 
		return COUNT(null, minMax[0], minMax[1], a, start, stop); 
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
	final static public int[] COUNT(int[] ret, final int min, final int max, 
			final int[] a, final int start, final int stop) {
		if (ret == null)
			ret  = new int[max-min]; 
		for(int i = stop; --i >= start;) {
			final int val = a[i]; 
			if ((min <= val) &&
				(val <  max)) 
				++ret[a[i]-min]; 
		}
		return ret; 
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Subtracts the Part from a which lies parallel to the normed Vector arg (|arg| = 1).
	  * Used primarily in Orthogonalization.
	  * this -= arg*(arg*this)
	  */
	final static public int[] SUB_PART_AT(final int[] a, int[] arg) {
		return SUB_PART_AT(a, arg, 1);
	}

	/** Subtracts the Part from a which lies parallel to the normed Vector arg (|arg| = 1).
	  * Used primarily in Orthogonalization.
	  * this -= arg*(arg*this)
	  */
	final static public int[] SUB_PART(int[] a, int[] arg) {
		return SUB_PART(a, arg, 1);
	}

	/** Subtracts the Part from a which lies parallel to the Vector arg.
	  * Used primarily in Orthogonalization.
	  * If argSqrNorm == null, it is assumed to be 1 (orthoNormal)
	  * this -= arg*((arg*this)/(arg*arg))
	  */
	final static public int[] SUB_PART(int[] a, int[] arg, int argSqrNorm) {
		int Prod = (int) MAP(a, arg, 0, arg.length) / argSqrNorm;
		return SUB_PROD(a, Prod, arg);
	}

	/** Subtracts the Part from a which lies parallel to the Vector arg.
	  * Used primarily in Orthogonalization.
	  * If argSqrNorm == null, it is assumed to be 1 (orthoNormal)
	  * this -= arg*((arg*this)/(arg*arg))
	  */
	final static public int[] SUB_PART_AT(int[] a, int[] arg, int argSqrNorm) {
		int Prod = (int) MAP(a, arg, 0, arg.length) / argSqrNorm;
		SUB_PROD_AT(a, Prod, arg);
		return a;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Binary Operations
	///////////////////////////////////////////////////////////////////////////////////

	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public int[] MIN_AT(int[] ret, int Limit) {
		return MIN_AT(ret, Limit, 0, ret.length);
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	/** Caps the given Range of the Array so no Element exceeds Limit, in Place. */
	final static public int[] MIN_AT(int[] ret, int Limit, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > Limit) 
				ret[stop] = Limit;
		}
		return ret;
	}

	/**
	  * Caps the whole Array Element-wise against arr, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] MIN_AT(int[] ret, int[] arr) {
		return MIN_AT(ret, arr, 0, arr.length);
	}

	/**
	  * Caps the given Range Element-wise against arr, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] MIN_AT(int[] ret, int[] arr, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > arr[stop]) 
				ret[stop] = arr[stop];
		}
		return ret;
	}

	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public int[] MAX_AT(int[] ret, int Limit) {
		return MAX_AT(ret, Limit, 0, ret.length);
	}

	/**
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	/** Raises the given Range of the Array so no Element is below Limit, in Place. */
	final static public int[] MAX_AT(int[] ret, int Limit, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < Limit) 
				ret[stop] = Limit;
		}
		return ret;
	}

	/**
	  * Raises the whole Array Element-wise against arr, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] MAX_AT(int[] ret, int[] arr) {
		return MAX_AT(ret, arr, 0, arr.length);
	}

	/**
	  * Raises the given Range Element-wise against arr, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] MAX_AT(int[] ret, int[] arr, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < arr[stop]) 
				ret[stop] = arr[stop];
		}
		return ret;
	}

	/**
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public int[] ADD_AT(final int[] ret, final int Increment) {
		return ADD_AT(ret, Increment, 0, ret.length); }

	/**
	  * Adds the given Increment to every Element of the given Range of ret, in Place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] ADD_AT(final int[] ret, final int Increment, int start, int stop) {
		while(--stop >= start)
			ret[stop] += Increment;
		return ret; }

	/**
	  * Adds arr Element-wise to the whole ret Array, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] ADD_AT(final int[] ret, final int[] arr) {
		return ADD_AT(ret, arr, 0, arr.length); }

	/**
	  * Adds arr Element-wise to the given Range of ret, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] ADD_AT(final int[] ret, final int[] arr, final int start, final int stop) {
		return ADD_AT(ret, arr, start, stop, 0);}

	/**
	  * Adds arr Element-wise to ret at the given retOffset, in Place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] ADD_AT(final int[] ret, final int[] arr, final int start, int stop, final int retOffset) {
		while (--stop >= start) 
			ret[stop+retOffset] += arr[stop];
		return ret;
	}

	/**
	  * Adds two Arrays Element-wise into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] ADD(final int[] ret, final int[] sum1, final int[] sum2) {
		return ADD(ret, sum1, sum2, 0, sum1.length);
	}

	/**
	  * Adds two Arrays Element-wise into ret, tolerating Length Mismatches by copying the excess Tail.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] ADD(int[] ret, final int[] sum1, final int[] sum2, int start, int stop) {
		if (ret == null) {
			ret = new int[stop]; //tolerant!
		} else {
			if (stop > ret.length) {
				stop = ret.length;
			}
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
	  * Adds the Increment to every Element of sum1 into a new Array.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] ADD(final int[] sum1, final int Incr) {
		return ADD(null, sum1, Incr, 0, sum1.length); }

	/**
	  * Adds the Increment to every Element of sum1 into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] ADD(int[] ret, int[] sum1, int Incr) {
		return ADD(ret, sum1, Incr, 0, sum1.length);
	}

	/**
	  * Adds the Increment to the given Range of sum1 into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] ADD(int[] ret, final int[] sum1, final int incr, final int start, int stop) {
		if (ret == null) 
			ret = new int[stop]; 
		while (--stop >= start) 
			ret[stop] = sum1[stop] + incr;
		return ret;
	}

	/**
	  * Adds two Arrays Element-wise into a new Array.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] ADD(int[] sum1, int[] sum2) {
		return ADD(sum1, sum2, 0, sum1.length);
	}

	/**
	  * Adds the given Range of two Arrays Element-wise into a new Array.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] ADD(int[] sum1, int[] sum2, int start, int stop) {
		return ADD(new int[stop], sum1, sum2, start, stop);
	}

	/**
	  * Subtracts arr Element-wise from the whole ret Array, in Place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] SUB_AT(final int[] ret, final int[] arr) {
		return SUB_AT(ret, arr, 0, arr.length);
	}

	/**
	  * Subtracts arr Element-wise from the given Range of ret, in Place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] SUB_AT(final int[] ret, final int[] arr,
			final int start, int stop) {
		while(--stop  >= start)
			ret[stop] -= arr[stop];
		return ret;
	}

	/**
	  * Subtracts sub Element-wise from min into ret.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] SUB(final int[] ret, final int[] min, final int[] sub) {
		return SUB(ret, min, sub, 0, sub.length);
	}

	/**
	  * Subtracts sub Element-wise from min into a new Array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] SUB(final int[] min, final int[] sub) {
		return SUB(min, sub, 0, sub.length);
	}

	/**
	  * Subtracts the given Range of sub Element-wise from min into a new Array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] SUB(final int[] min, final int[] sub, final int start, int stop) {
		return SUB(new int[stop], min, sub, start, stop);
	}

	/**
	  * Subtracts the given Range of sub Element-wise from min into ret.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] SUB(final int[] ret, final int[] min, final int[] sub,
			final int start, int stop) {
		while(--stop >= start) 
			ret[stop] = min[stop] - sub[stop];
		return ret;
	}

	/**
	  * To implement divAt, just invert the Factor
	  * @param Factor the Factor to multiply with
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @return the given Array multiplied by the given Factor
	  */
	final static public int[] MUL_AT(int[] ret, int Factor) {
		return MUL_AT(ret, Factor, 0, ret.length);
	}

	/**
	  * Multiplies every Element of the given Range of ret by the given Factor, in Place.
	  * @return the Product of the Array with the given Factor
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Factor the Factor to multiply with
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] MUL_AT(int[] ret, int Factor, int start, int stop) {
		while(--stop  >= start)
			ret[stop] *= Factor;
		return ret;
	}

	/**
	  * Multiplies arr Element-wise into the whole ret Array, in Place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] MUL_AT(int[] ret, int[] arr) {
		return MUL_AT(ret, arr, 0, arr.length);
	}

	/**
	  * Multiplies arr Element-wise into the given Range of ret, in Place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] MUL_AT(int[] ret, int[] arr, int start, int stop) {
		while(--stop  >= start)
			ret[stop] *= arr[stop];
		return ret;
	}

	/**
	  * Multiplies two Arrays Element-wise into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] MUL(int[] ret, int[] min, int[] sub) {
		return MUL(ret, min, sub, 0, sub.length);
	}

	/**
	  * Multiplies the given Range of two Arrays Element-wise into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] MUL(int[] ret, int[] min, int[] sub, int start, int stop) {
		while(--stop >= start)
			ret[stop] = min[stop] * sub[stop];
		return ret;
	}

	/**
	  * Multiplies every Element of min by factor into a new Array.
	  * @return a new Array containing the Product of the given Array
	  * @param ret Array with the Values to be processed.
	  */
	final static public int[] MUL(int[] min, int factor) {
		return MUL(new int[min.length], min, factor, 0, min.length);
	}

	/**
	  * Multiplies every Element of min by factor into ret.
	  * @return a new Array containing the Product of the given Array
	  * @param ret Array with the Values to be processed.
	  */
	final static public int[] MUL(int[] ret, int[] min, int factor) {
		return MUL(ret, min, factor, 0, min.length);
	}

	/**
	  * Multiplies the given Range of min by factor into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] MUL(int[] ret, int[] min, int factor, int start, int stop) {
		while(--stop >= start)
			ret[stop] = min[stop] * factor;
		return ret;
	}

	/**
	  * Multiplies two Arrays Element-wise into a new Array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] MUL(int[] min, int[] sub) {
		return MUL(min, sub, 0, sub.length);
	}

	/**
	  * Multiplies the given Range of two Arrays Element-wise into a new Array.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] MUL(int[] min, int[] sub, int start, int stop) {
		return MUL(new int[stop], min, sub, start, stop);
	}

	/**
	  * Divides the whole ret Array Element-wise by arr, in Place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] DIV_AT(int[] ret, int[] arr) {
		return DIV_AT(ret, arr, 0, arr.length);
	}

	/**
	  * Divides the given Range of ret Element-wise by arr, in Place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] DIV_AT(int[] ret, int[] arr, int start, int stop) {
		while(--stop >= start)
			ret[stop] /= arr[stop];
		return ret;
	}

	/**
	  * Divides every Element of the whole ret Array by arg, in Place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] DIV_AT(int[] ret, int arg) {
		return DIV_AT(ret, arg, 0, ret.length);
	}

	/**
	  * Divides every Element of the given Range of ret by arg, in Place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] DIV_AT(int[] ret, int arg, int start, int stop) {
		while(--stop >= start)
			ret[stop] /= arg;
		return ret;
	}

	/**
	  * Divides min Element-wise by sub into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] DIV(int[] ret, int[] min, int[] sub) {
		return DIV(ret, min, sub, 0, sub.length);
	}

	/**
	  * Divides the given Range of min Element-wise by sub into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] DIV(int[] ret, int[] min, int[] sub, int start, int stop) {
		while(--stop >= start)
			ret[stop] = min[stop] / sub[stop];
		return ret;
	}

	/**
	  * Divides min Element-wise by sub into a new Array.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public int[] DIV(int[] min, int[] sub) {
		return DIV(min, sub, 0, sub.length);
	}

	/**
	  * Divides the given Range of min Element-wise by sub into a new Array.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public int[] DIV(int[] min, int[] sub, int start, int stop) {
		return DIV(new int[stop], min, sub, start, stop);
	}

	/** Raises ret Element-wise to the Minimum of a and the scalar y, in the given Range.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public int[] MAX_MIN_PROD(int[] ret, int[] a, int y, int start, int stop) {
		int x; //FALSE; //can also start with any lower Value!
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
	final static public int[] MAX_MIN_PROD(int[] ret, int[] a, int[] b, int start, int stop) {
		int x, y; //FALSE; //can also start with any lower Value!
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
	final static public int[] MAX_MIN_PROD(int[] ret, int[] a, int[] b) {
		return MAX_MIN_PROD(ret, a, b, 0, ret.length);
	}

	/** Raises the whole ret Array Element-wise to the Minimum of a and the scalar y.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public int[] MAX_MIN_PROD(int[] ret, int[] a, int y) {
		return MAX_MIN_PROD(ret, a, y, 0, ret.length);
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Ring Methods
	///////////////////////////////////////////////////////////////////////////////////

	/// these Methods with scalar Parameters have been removed,
	/// because they can be replaced by their addAt and mulAt Counterparts.
	/**  Linear Mapping in Place: x+=a * y	 replaced by addAt(a*y)  */
	//	final static public int[] addProdAt (int[] ret, int a, int y) {
	/**  Linear Mapping in Place: x-=a * y	 replaced by subAt(a*y)  */
	//	final static public int[] subtProdAt(int[] ret, int a, int y) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by LinAt(a, y*b)  */
	//	final static public int[] BiLinAt   (int[] ret, int a, int y, int b) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by LinAt(a, y*b)  */
	//	final static public int[] BiLinAt   (int[] ret, int[] a, int y, int b) {

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD_AT(final int[] ret, final int[] a, final int y, final int start, final int stop) {
		return ADD_PROD_AT(ret, a, y, start, stop, 0);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD_AT(final int[] ret, final int[] a, final int y, final int start, int stop, final int retOffset) {
		while (--stop >= start) 
			ret[stop+retOffset] += a[stop] * y;
		return ret;
	}
	
	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD_AT(int[] ret, int[] a, int[] y, int start, int stop) {
		while (--stop >= start) 
			ret[stop] += a[stop] * y[stop];
		return ret;
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD_AT(int[] ret, int a, int[] y, int start, int stop) {
		return ADD_PROD_AT(ret, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD_AT(int[] ret, int a, int[] y) {
		return ADD_PROD_AT(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD_AT(int[] ret, int[] a, int y) {
		return ADD_PROD_AT(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD_AT(int[] ret, int[] a, int[] y) {
		return ADD_PROD_AT(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x + a*y	 */
	final static public int[] ADD_PROD(int[] ret, int[] x, int[] a, int y, int start, int stop) {
		while (--stop >= start) 
			ret[stop] = x[stop] + a[stop] * y;
		return ret;
	}

	/**  Linear Mapping in Place: x + a*y	 */
	final static public int[] ADD_PROD(int[] ret, int[] x, int[] a, int[] y, int start, int stop) {
		while (--stop >= start) 
			ret[stop] = x[stop] + a[stop] * y[stop];
		return ret;
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD(int[] ret, int[] x, int a, int[] y, int start, int stop) {
		return ADD_PROD(ret, x, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD(int[] ret, int[] x, int a, int[] y) {
		return ADD_PROD(ret, x, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD(int[] ret, int[] x, int[] a, int y) {
		return ADD_PROD(ret, x, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD(int[] ret, int[] x, int[] a, int[] y) {
		return ADD_PROD(ret, x, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD(final int[] x, final int a, final int[] y, 
			final int start, final int stop) {
		return ADD_PROD(new int[stop], x, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD(final int[] x, final int a, final int[] y) {
		return ADD_PROD(new int[x.length], x, a, y, 0, x.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD(final int[] x, final int[] a, final int y) {
		return ADD_PROD(new int[x.length], x, a, y, 0, x.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public int[] ADD_PROD(final int[] x, final int[] a, final int[] y) {
		return ADD_PROD(new int[x.length], x, a, y, 0, x.length);
	}

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public int[] SUB_PROD_AT(final int[] ret, final int[] b, final int[] y, 
			final int start, int stop) {
		while (--stop >= start) 
			ret[stop] -= b[stop] * y[stop];
		return ret;
	}

	/**  Linear Mapping in Place: x - a*y	 */
	final static public int[] SUB_PROD(final int[] ret, final int[] x, final int[] b, final int[] y, final int start, int stop) {
		while (--stop >= start) 
			ret[stop] = x[stop] - b[stop] * y[stop];
		return ret;
	}

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public int[] SUB_PROD_AT(final int[] ret, final int[] b, final int[] y) {
		return SUB_PROD_AT(ret, b, y, 0, ret.length);
	}

	/**  Linear Mapping: x - a*y	 */
	final static public int[] SUB_PROD(final int[] ret, final int[] x, final int[] b, final int[] y) {
		return SUB_PROD(ret, x, b, y, 0, ret.length);
	}

	/**  Linear Mapping: x - a*y	 */
	final static public int[] SUB_PROD(final int[] x, final int[] b, final int[] y) {
		return SUB_PROD(new int[x.length], x, b, y, 0, x.length);
	}

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public int[] SUB_PROD_AT(final int[] ret, final int b, final int[] y, final int start, int stop) {
		while (--stop >= start) 
			ret[stop] -= b * y[stop];
		return ret;
	}

	/**  Linear Mapping in Place: x - a*y	 */
	final static public int[] SUB_PROD(final int[] ret, final int[] x, final int b, final int[] y, final int start, int stop) {
		while (--stop >= start) 
			ret[stop] = x[stop] - b * y[stop];
		return ret;
	}

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public int[] SUB_PROD_AT(final int[] ret, final int b, final int[] y) {
		return SUB_PROD_AT(ret, b, y, 0, ret.length);
	}

	/**  Linear Mapping: x - a*y	 */
	final static public int[] SUB_PROD(final int[] ret, final int[] x, final int b, final int[] y) {
		return SUB_PROD(ret, x, b, y, 0, ret.length);
	}

	/**  Linear Mapping: x - a*y	 */
	final static public int[] SUB_PROD(final int[] x, final int b, final int[] y) {
		return SUB_PROD(new int[x.length], x, b, y, 0, x.length);
	}

	/**  Linear Mapping in Place: x = x*a + y	*/
	final static public int[] LIN_AT(final int[] x, final int a, final int y, 
			final int start, final int stop) {
		return LIN(x, x, a, y, start, stop);
	}

	/**  Linear Mapping in Place: x*a + y	*/
	final static public int[] LIN(int[] ret, int[] x, int a, int y, int start, int stop) {
		while (--stop >= start) 
			ret[stop] = x[stop] * a + y;
		return ret;
	}

	/**  Linear Mapping in Place: x = x*a + y	*/
	final static public int[] LIN_AT(int[] x, int[] a, int y, int start, int stop) {
		return LIN(x, x, a, y, start, stop);
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public int[] LIN(int[] ret, int[] x, int[] a, int y, int start, int stop) {
		while (--stop >= start) 
			ret[stop] = x[stop] * a[stop] + y;
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public int[] LIN(int[] ret, int[] x, int a, int[] y, int start, int stop) {
		while (--stop >= start) 
			ret[stop] = x[stop] * a + y[stop];
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public int[] LIN(int[] ret, int[] x, int[] a, int[] y, int start, int stop) {
		while (--stop >= start) 
			ret[stop] = x[stop] * a[stop] + y[stop];
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public int[] LIN_AT(int[] ret, int a, int[] y, int start, int stop) {
		return LIN(ret, ret, a, y, start, stop);
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public int[] LIN_AT(int[] ret, int[] a, int[] y, int start, int stop) {
		return LIN(ret, ret, a, y, start, stop);  }

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public int[] LIN_AT(int[] ret, int a, int y) {
		return LIN_AT(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public int[] LIN_AT(int[] ret, int[] a, int y) {
		return LIN_AT(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public int[] LIN_AT(int[] ret, int a, int[] y) {
		return LIN_AT(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public int[] LIN_AT(int[] ret, int[] a, int[] y) {
		return LIN_AT(ret, a, y, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int[] a, int[] y, int[] b, int start, int stop) {
		while(--stop >= start) 
			ret[stop] = x[stop] * a[stop] + y[stop] * b[stop];
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int[] a, int[] y, int b, int start, int stop) {
		while(--stop >= start) 
			ret[stop] = x[stop] * a[stop] + y[stop] * b;
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int a, int[] y, int[] b, int start, int stop) {
		while(--stop >= start) 
			ret[stop] = x[stop] * a + y[stop] * b[stop];
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int a, int[] y, int b, int start, int stop) {
		while(--stop >= start) 
			ret[stop] = x[stop] * a + y[stop] * b;
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int[] a, int y, int[] b, int start, int stop) {
		return BI_LIN(ret, x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int a, int[] y, int b) {
		return BI_LIN(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int[] a, int[] y, int b) {
		return BI_LIN(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int[] a, int[] y, int[] b) {
		return BI_LIN(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int a, int[] y, int[] b) {
		return BI_LIN(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] ret, int[] x, int[] a, int y, int[] b) {
		return BI_LIN(ret, x, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int[] a, int[] y, int[] b, int start, int stop) {
		return BI_LIN(new int[x.length], x, a, b, y, start, stop); }

	/**BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int[] a, int[] y, int b, int start, int stop) {
		return BI_LIN(new int[x.length], x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int a, int[] y, int[] b, int start, int stop) {
		return BI_LIN(new int[x.length], x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int a, int[] y, int b, int start, int stop) {
		return BI_LIN(new int[x.length], x, a, y, b, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int[] a, int y, int[] b, int start, int stop) {
		return BI_LIN(new int[x.length], x, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int a, int[] y, int b) {
		return BI_LIN(new int[x.length], x, a, y, b, 0, x.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int[] a, int[] y, int b) {
		return BI_LIN(new int[x.length], x, a, y, b, 0, x.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int[] a, int[] y, int[] b) {
		return BI_LIN(new int[x.length], x, a, y, b, 0, x.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int a, int[] y, int[] b) {
		return BI_LIN(new int[x.length], x, a, y, b, 0, x.length); }

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public int[] BI_LIN(int[] x, int[] a, int y, int[] b) {
		return BI_LIN(new int[x.length], x, a, y, b, 0, x.length); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public int[] BI_LIN_AT(int[] ret, int[] a, int[] y, int b, int start, int stop) {
		return BI_LIN(ret, ret, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public int[] BI_LIN_AT(int[] ret, int a, int[] y, int[] b, int start, int stop) {
		return BI_LIN(ret, ret, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public int[] BI_LIN_AT(int[] ret, int a, int[] y, int b, int start, int stop) {
		return BI_LIN(ret, ret, a, y, b, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public int[] BI_LIN_AT(int[] ret, int[] a, int y, int[] b, int start, int stop) {
		return BI_LIN(ret, ret, a, b, y, start, stop); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public int[] BI_LIN_AT(int[] ret, int a, int[] y, int b) {
		return BI_LIN(ret, ret, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public int[] BI_LIN_AT(int[] ret, int[] a, int[] y, int b) {
		return BI_LIN(ret, ret, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public int[] BI_LIN_AT(int[] ret, int[] a, int[] y, int[] b) {
		return BI_LIN(ret, ret, a, y, b, 0, ret.length); }

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public int[] BI_LIN_AT(int[] ret, int a, int[] y, int[] b) {
		return BI_LIN(ret, ret, a, y, b, 0, ret.length); }

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public int[] BI_LIN_AT(final int[] ret, final int[] a, final int y, final int[] b) {
		return BI_LIN(ret, ret, a, y, b, 0, ret.length); }

	////////////////////////////////////////////////////////////////////////////////
	//	static Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//	QuickSort Algorithm on Object[], @see Stream.Object.Enumerator.Container.Array
	////////////////////////////////////////////////////////////////////////////////

	//	private static final Random ran = Math.random();	//for randomizing the Pivot Element and avoiding Overhead with nearly sorted Arrays.

	/**Divide and Conquer Method:
	 * A Separator Element is determined and all other Elements ordered around it.
	 * a[p..r] -> a[p..q] <= a[q+1..r]
	 * The Elements of the Items Array are expected to be of Type OrderAble	 */
	protected static int PARTITION(final Object[] Items, final int p, final int r) {
		Object tmp; //for swapping
		IOrderAble Item; //for comparing, chose OrderAble to avoid further Casting!!!
		//		if (randomize)	Item = (OrderAble) Items[p + (r-p)*Random()];
		//		else
		Item = (IOrderAble) Items[p];
		int i = p - 1;
		int j = r + 1;
		while (true) { //swap all Items around the selected one
			do;
			while (Item.isMoreThan(Items[--j])); //search for a greater Item
			do;
			while (Item.isLessThan(Items[++i])); //search for a smaller Item
			if (i < j) {
				tmp = Items[j];
				Items[j] = Items[i];
				Items[i] = tmp;
			} //swap both
			else
				return j; //finished: all Elements left of j are smaller than those right of j
		}
	}

	/**QuickSort Algorithm:
	 * Divide and Conquer Method:
	 * The Array is divided into two, of which both are again sorted.	 */
	public static void QUICK_SORT(final Object[] Items) {
		QUICK_SORT(Items, 0, Items.length - 1);
	}

	/**QuickSort Algorithm:
	 * Divide and Conquer Method:
	 * The Array is divided into two, of which both are again sorted.	 */
	public static void QUICK_SORT(final Object[] Items, final int p, final int r) {
		if (p >= r)
			return; // Items; //not effective to return since recursive!
		int q = PARTITION(Items, p, r);
		QUICK_SORT(Items, p, q);
		QUICK_SORT(Items, q + 1, r);
	}

	/**Creates the i-th Order Statistic using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum	 */
	public static Object STATISTIC(final Object[] Items, final int i) {
		return STATISTIC(Items, 0, Items.length - 1, i);
	}

	/**Creates the i-th Order Statistic using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum	 */
	public static Object STATISTIC(final Object[] Items, final int p, final int r, final int i) {
		if (p >= r)
			return Items[p];
		int q = PARTITION(Items, p, r); //after this all Elements
		int k = q - p + 1;
		if (i <= k)
			return STATISTIC(Items, p, q, i); //
		else
			return STATISTIC(Items, q + 1, r, i - k);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	////////////////////////////////////////////////////////////////////////////////

	/** The array buffer into which the components of the Array are
	  * stored. The capacity of the Array is the length of this array buffer.	 */
	protected int[] items;
	
	/**
	 * Returns the Items of this Vector, either by Reference or as a Copy.
	 * @param original Returns the internal Structure by Reference!
	 * Should usually be false(Default), except when it is guaranteed,
	 * that the Array will be used Read-Only.
	 * @see #toArray()
	 * @return the Items	 */
	public int[] getInts(final boolean original) {
		if (original) {
			return items; }
		return COPY(this.items, this.itemCount); } 
	
	/** Returns a Copy of the Items of this Vector.
	 * @return the Items	 */
	public int[] getInts() { return getInts(false); }
	
	////////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////////

	/**Constructs an empty VectorInt with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the VectorInt.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the VectorInt overflows.	 */
	public VectorInt(final int initialCapacity, final int capacityIncrement_) {
		this.items = new int[initialCapacity];
		this.capacityIncrement = capacityIncrement_;
		//		mEnum = new ArrayEnum(Items, ItemCount);
		//		mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty VectorInt with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the VectorInt.	 */
	public VectorInt(final int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty VectorInt.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public VectorInt() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/** Constructs an VectorInt by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public VectorInt(final Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an VectorInt from the given Object.	  */
	public VectorInt(final Object arg, final int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorInt from the given Object.	  */
	public VectorInt(final int[] arg, final int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorInt from the given Object
	  * and copies the Elements into this VectorInt.	  */
	public VectorInt(final int[] arg) {
		this((arg != null) ? arg.length : DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Determines whether the Items in this Container are strictly ascending, descending, or neither.
	 * @return the Order of the Items in this Container
	 * @see streamIO.Float.IStreamIn_Float#getOrder()
	 */
	public byte getOrder() {
		int i = itemCount;
		int first = items[0];
		int last = items[--i];
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
	final public VectorInt addInt(final int item) {
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

	/**
	 * returns a copy of the items in this Vector
	 * @return a copy of the items in this Vector
	 * @see #getItems(boolean)
	 * @deprecated use #getItems(boolean) instead
	 */
	//public int[] toArray() { return COPY(this.items, this.itemCount); }
	
	/**Trims the capacity of this VectorInt to be the VectorInt's current
	 * size. An application can use this operation to minimize the
	 * storage of a VectorInt.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			int[] oldData = items;
			items = new int[itemCount];
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
	}

	/**Returns the current capacity of this VectorInt.
	 *
	 * @return  the current capacity of this VectorInt.	 */
	final public int getCapacity() { return items.length; }
	
	/**Increases the capacity of this VectorInt, if necessary, 
	 * to ensure that it can hold at least the number of components 
	 * specified by the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final public synchronized int setCapacity(final int minCapacity) {
		final int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) 
			return oldCapacity;
		final int newCapacity = ENLARGED_CAPACITY(oldCapacity, capacityIncrement, minCapacity); 
		final int[] oldData = items; items = new int[newCapacity];
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
	public synchronized int getIntAt(final int index) {
		if (indexInRange(index) && (index < items.length)) 
			return items[index];
		return 0;
	}
	
	/** Wraps the primitive int Value at the given Index into a ByRefInt Object.
	 * @return the item at the given Position as an Object */
	public Object getAt(final int i) {
		return new ByRefInt(getIntAt(i)); }
	
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
	public int setAt(final int index, final int value) {
		final int ret; 
		if (indexInRange(index))
			ret =  items[index]; 
		else {  
			if ((value == 0) &&
				(itemCount <= index)) { 
				 itemCount  = index+1; //don't resize for 0s...
				if (index < items.length)
					items[index] = 0; //but at least fill up the Array. 
				return    0; 
			}
			setSize(index+1);  
			ret = 0; 
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
		return new ByRefInt(setAt(index, ByRefInt.TO_INT(value))); 
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
	public void insertAt(final int index, final int value) {
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
	// TODO: LOGIC: decrements itemCount unconditionally before the range check, corrupting the Vector's size on an out-of-range access; same defect as VectorChar/VectorLong/VectorShort removeAt.
	public int removeAt(final int index) {
		if (index > --itemCount)  //
			return 0;
		final int ret = items[index];
		System.arraycopy(items, index+1, items, index, itemCount-index);
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX) 
	/// for multidimensional rectangular Arrays 
	////////////////////////////////////////////////////////////////////////////////

	/** Reads a Value from the rectangular 2-dim View of this Vector at the given Row/Column.
	 * @return the Value at the given Position	 */
	public double getAt(final int Row, final int Col) {
		return items[Row * dimFactors[0] + Col * dimFactors[1]];
	}

	/** sets the given Value 	 */
	public void setAt(final int Row, final int Col, final int Value) {
		items[Row * dimFactors[0] + Col * dimFactors[1]] = Value;
	}

	/** Reads a Value from the rectangular 3-dim View of this Vector at the given Sheet/Row/Column.
	 * @return the Value at the given Position	 */
	public double getAt(final int Sheet, final int Row, final int Col) {
		return items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]];
	}

	/** sets the given Value 	 */
	public void setAt(final int Sheet, final int Row, final int Col, final int Value) {
		items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]] = Value;
	}

	/** Reads a Value from the rectangular multi-dim View of this Vector at the given Multi-Index.
	 * @return the Value at the given Position	 */
	public double getAt(final int[] Col) {
		return items[multiIndex(Col)];
	}

	/** sets the given Value 	 */
	public void setAt(final int[] Col, final int Value) {
		items[multiIndex(Col)] = Value;
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
	public VectorInt copyAt(final int[] _arg) {
		if (_arg == null) 
			return this; 
		itemCount = _arg.length;
		if (items.length  < itemCount)
			items = new int[itemCount]; 
		System.arraycopy(_arg, 0, items, 0, itemCount);
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
		if (arg instanceof VectorInt) {
			VectorInt arg_ = (VectorInt) arg;
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
		if (arg instanceof VectorInt) {
			VectorInt arg_ = (VectorInt) arg;
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
		return new VectorInt(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Member Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Determines the smallest Value held by this Vector.
	 * @return the Minimum Value in this Vector	 */
	public int MinVal() { return MIN_VAL(items); }

	/** Determines the Index of the smallest Value held by this Vector.
	 * @return the Position of the Minimum Value in this Vector	 */
	public int MinPos() { return MIN_POS(items); }

	/** Determines the largest Value held by this Vector.
	 * @return the Maximum Value in this Vector	 */
	public int MaxVal() { return MAX_VAL(items); }

	/** Determines the Index of the largest Value held by this Vector.
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
	public VectorInt normalizeAt() {
		while (items[--itemCount] == 0);
		++itemCount;
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorInt addAt(final VectorInt vector) {
		return addAt(vector.items, 0, vector.itemCount); }

	/** subtracts the given Portion of the values from this Vector */
	public VectorInt subAt(final VectorInt vector) {
		return subAt(vector.items, 0, vector.itemCount); }

	// TODO: LOGIC: calls subAt instead of a multiplicative operation; same defect as VectorChar/VectorLong/VectorShort mulAt(VectorX).
	/** multiplies this Vector by the given Portion of the values */
	public VectorInt mulAt(final VectorInt vector) {
		return subAt(vector.items, 0, vector.itemCount); }

	// TODO: LOGIC: calls subAt instead of a divisive operation; same defect as VectorChar/VectorLong/VectorShort divAt(VectorX).
	/** divides this Vector by the given Portion of the vector*/
	public VectorInt divAt(final VectorInt vector) {
		return subAt(vector.items, 0, vector.itemCount); }

	/** subtracts the given Portion of the values from this Vector */
	public VectorInt subAt(final int[] values, int start, int stop) {
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

	/** adds the given Value to the values of this Vector */
	public VectorInt addAt(final int value) {
		VectorInt.ADD_AT(items, value, 0, itemCount);
		return this; }

	/** subtracts the given Value from the Values of this Vector */
	public VectorInt subAt(final int value) {
		VectorInt.ADD_AT(items, -value, 0, itemCount);
		return this; }

	// TODO: LOGIC: ignores the value parameter and instead multiplies items by itself element-wise; same defect as VectorChar/VectorLong/VectorShort mulAt(int).
	/** multiplies this Vector by the given Portion of the values */
	public VectorInt mulAt(final int value) {
		return mulAt(items, 0, itemCount); }

	// TODO: LOGIC: ignores the value parameter and instead divides items by itself element-wise (yielding all 1s); same defect as VectorChar/VectorLong/VectorShort divAt(int).
	/** divides this Vector by the given Portion of the vector*/
	public VectorInt divAt(final int value) {
		return divAt(items, 0, itemCount); }

	/** adds the given Portion of the values to this Vector */
	public VectorInt addAt(final int[] values, int start, int stop) {
		if (stop > itemCount) {
			setCapacity(stop);
			COPY_AT(items, values, itemCount, stop);
			ADD_AT(items, values, start, itemCount);
			//			normalizeAt();
		} else if (stop < itemCount) { //don't need to (re-)normalize
			ADD_AT(items, values, start, stop);
		} else {
			ADD_AT(items, values, start, stop);
			//			normalizeAt();
		}
		return this;
	}

	/** multiplies the given Portion of the values with this Vector */
	public VectorInt mulAt(final int[] values, final int start, int stop) {
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
	public VectorInt divAt(final int[] values, final int start, int stop) {
		while (values[--stop] == 0); //normalize 
		++stop;
		if (stop >= itemCount) {
			stop = itemCount; //all other Values are multiplied by 0
		} else {
			itemCount = stop; //all other Values are divided by 0 and become Infinity!
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
			  if (arg instanceof int[]) {
				  int[] arg_ = (int[]) arg;
				  copyAt(Permutation.map(Items, Items.length, arg_, arg_.length));
				  return this; }
			  return super.mulAt(arg); }
	*/
	/**Multiply the Vector by an Object.
	 * This extends the standard Set Multiplication
	 * by the Multiplication with a Permutation.	 */
	/*	  public SemiGroupM mul(Object arg) {
			  if (arg instanceof Permutation) return new VectorInt(Permutation.map(Items, Items.length, (Permutation) arg), capacityIncrement);
			  if (arg instanceof int[]	  ) return new VectorInt(Permutation.map(Items, Items.length, (int[]	  ) arg), capacityIncrement);
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
	protected VectorInt(final int[] Values, final int[] Factors) {
		this.dimFactors = Factors;
		this.items = Values;
	}

	/**
	 * @param Rows the Numbers of Rows    in the Matrix
	 * @param Cols the Numbers of Columns in the Matrix
	 */
	/*	public VectorInt(int Rows, int Cols) {
			this.dimSizes = new int[2];
			this.dimSizes[0] = Cols;
			this.dimSizes[1] = Rows;
			dimFactors = new int[2];
			dimFactors[1] = 1;
			dimFactors[2] = Cols;
			items = new int[Rows * Cols];
		}
	*/
	/**
	 * @param Cols the Numbers of Columns in the Tensor
	 */
/*	public VectorInt(int[] Cols) {
		this.dimSizes = Cols;
		int Factor, i = Cols.length;
		dimFactors = new int[i];
		Factor = 1; //last Index has smallest Factor
		while (--i >= 0) {
			dimFactors[i] = Factor;
			Factor *= Cols[i];
		}
		items = new int[Factor];
	}
*/
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a transposed View sharing this Vector's backing Array, by permuting the Index Factors.
	 * @return a VectorInt with IndexFactors such
	 *  that the Elements are transposed.
	 * Only useful when simulating a rectangular Tensor on a 1 dim Array.
	 */
	public VectorInt getTranspose() {
		if (dimFactors.length != 2) {
			throw new InvalidParameterException("For Tensors please determine the Dimensions to transpose!");
		}
		int[] Factors = new int[2];
		Factors[0] = dimFactors[1]; //Just permuting the Factors is sufficient!
		Factors[1] = dimFactors[0]; //also for Tensors of higher Degrees!
		return new VectorInt(items, Factors);
	}
	
	/**
	 * Reads a row of Values (e.g. the Points of a single Plane) 
	 * from the current ResultSet
	 * @param rs the ResultSet to read from
	 * @param columnOffset the Column to start reading consecutively from 
	 * @return the row read into this .
	 */
	final public VectorInt read(final ResultSet rs, final int columnOffset)
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
	final public VectorInt read(final ResultSet rs, int columnOffset, int lastCol)
		throws SQLException {
		if (!rs.next()) 
			return null;
		if (lastCol < 0)   
			lastCol = rs.getMetaData().getColumnCount(); //only known AFTER next()!
		--columnOffset; this.itemCount = 0; //start in the correct Order
		while (++columnOffset <= lastCol) 
			try { //try to read as many Coordinates as possible! 
				addInt(rs.getInt(columnOffset));
			} catch (final SQLException x) { //Resize the Array to exactly fit the Result.
				L.n().l(x); //skip any Comments etc. 
				return this; 
			}
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////
	// Optimizations
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	private static final void testAckermann () throws Exception {
		System.out.println("Testing Ackermann Function: ");
		System.out.println("A(1, 1) =  3 = " + Ackermann(1, 1));
		System.out.println("A(2, 2) =  7 = " + Ackermann(2, 2));
		System.out.println("A(3, 3) = 61 = " + Ackermann(3, 3));
		//		System.out.println("A(4, 4) = ?? = " + Ackermann(4, 5));	//leads to an Overflow with 19729 Digits.
	}
	
	/** Tests all Methods of this Class	 */
	private static final void testTrim() throws Exception {
		int[] vector = new int[5];
		vector[0] = 12345; 
		TRIM_AT(vector, 10); //simply converts the Number into the given Radix Representation
		AStreamOut.ARRAY_TO_STREAM(System.out, vector, ", ");
	}
	
	/** This was only for proving that simple PERMUTE_AT is not possible!	 
	public static void testPermutation() throws Exception {
		final Permutation[] perms = Permutation.Permutations(5);
		for (int i = perms.length; --i >= 0;) {
			Permutation perm = perms[i]; 
			final int[] arg = perm.toArray(); 
			final int[] prm = perm.toArray(); 
			PERMUTE_AT(arg, prm); 
			for (int j = arg.length; --j >= 0;) 
				Assert.EQUALS(arg[j], j); 
		}
	}
	*/
	
	final static public void testSummDiff() {
		final int[] initial = new int[12]; 
		for(int i = initial.length; --i >= 0;)
			initial[i] = i; 
		final int[] expected = COPY(initial); 
		DIFF_AT(initial); 
		SUMM_AT(initial); 
		Assert.EQUALS(expected, initial);
		//try it with random Numbers
		RANDOMIZE_AT(initial); 
		COPY_AT(initial, expected);
		SUMM_AT(initial); 
		DIFF_AT(initial); 
		Assert.EQUALS(expected, initial);
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		L.n("Testing " + VectorInt.class.getName());
		testSummDiff(); 
		//testPermutation();
		testAckermann(); 
		testTrim(); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
	    Assert.IS_TRUE(Double.NaN != Double.NaN); 
	    Assert.IS_TRUE(Double.POSITIVE_INFINITY == Double.POSITIVE_INFINITY); 
	    Assert.IS_TRUE(Double.NEGATIVE_INFINITY == Double.NEGATIVE_INFINITY); 
		testIt(args);
	}

}

/** Reverse-order Iterator over the Items of a VectorInt.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T13:35:49Z
 * digest: 9063223836affbf329bb9922a02b5e79c0bceed288c5038df1b0f5e35b2168b8
 * stale: false
 * tags: [code/functional_interfaces]
 * concepts: [Reverse-Order Int Stream Source]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
final class VectorIntStreamIn
extends AVectorStreamIn_Int {
	
	final VectorInt vector;

	/** Constructs a reverse-order Stream over the given VectorInt, bounded by its Maximum Value. */
	public VectorIntStreamIn(final VectorInt vector_) {
		super(vector_.MaxVal()); //
		this.vector = vector_;
		reSet();
	}

	/** @see Stream.Float.IStreamIn_Int#nextInt()	 */
	protected long nextLongInternal() { return vector.items[--pos]; }

	/** Delegates to the wrapped Vector's Minimum Value.
	 * @see Stream.Float.IStreamIn_Bound_Int#getMinValue()	 */
	public long getMinValue() { return vector.MinVal(); }

    /** Delegates to the wrapped Vector's current Item Count.
     * @see streamIO.real.AStreamIn_Float#getMaxMarkSize()     */
    public long getMaxMarkSize() { return vector.getInt(); }
    
}
