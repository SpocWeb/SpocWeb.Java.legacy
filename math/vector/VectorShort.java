package math.vector;

import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import streamIO.AStreamOut;
import streamIO.copy.ICopyAble;
import streamIO.integer.jdbc.AResultSet;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.IOrderAble;
import function.byref.ByRefInt;
import function.byref.ByRefShort;

/**
  * Growable, index-addressable array of primitive {@code short} elements, plus a large
  * library of static array-level operations (arithmetic, min/max, negation, linear
  * combinations, polynomial-style radix trimming and shifting) shared by every method of
  * this class and its instances alike.
  *
  * <p>Defines static Methods to treat Vectors and Arrays with Short Numbers.
  * @see streamIO.Copy.IGroup.IRing.IMetric.Body.Vector.VectorDbl
  * @see graphic.mvc.plane2D.MatrixShort
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
  * mtime: 2026-09-05T13:25:39Z
  * digest: dbcc290a41569f91d584a14ca1fe3f04d1fbb2919bc03c9afe3818284f865830
  * stale: false
  * tags: [code/growable_array, code/array_math]
  * concepts: [Growable short[] Vector]
  * facets: {layer: domain, status: broken, complexity: high}
  * -->
  */
final public class VectorShort 
extends AVector 
//implements OrderAble //requires a Reference Index to sort by
{

	///////////////////////////////////////////////////////////////////////////////////
	/// Streaming Methods
	///////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The Default Separator Character to use for the STREAM Methods */
	final static public char DEFAULT_SEPARATOR = '\t';

	/** Streams out the complete given Array. 
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 * @param separator the Separator Character
	 */
	final static public void STREAM(final short[] vals, final PrintStream stream, final char separator) {
		STREAM(vals, stream, 0, vals.length, separator);
	}

	/** Streams out the complete given Array. 
	 * defaults the separator the Default Separator Character
	 * 
	 * @param vals Values to stream
	 * @param stream the Stream to write to
	 */
	final static public void STREAM(final short[] vals, final PrintStream stream) {
		STREAM(vals, stream, 0, vals.length, DEFAULT_SEPARATOR);
	}
	
	/**
	 * Streams out (Parts of) the given Array. 
	 */
	final static public void STREAM(final short[] vals, final PrintStream stream, final int startCol, final int stopCol, final char separator) {
		if (startCol >= stopCol) {
			return;
		}
		stream.print(vals[startCol]); //omit the last Separator...
		for (int i = startCol; ++i < stopCol;) {
			stream.print(separator);
			stream.print(vals[i]);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Boolean Trafos: so far there is no 'VectorBool' 
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a new array boxing every element of the given primitive array.
	 * @return the Vector converted from primitives to Objects
	 */
	final static public Boolean[] TO_BOOLEAN(final boolean[] vector) {
		final Boolean[] ret = new Boolean[vector.length];
		for (int i=vector.length; --i >= 0;) {
			ret[i] = new Boolean(vector[i]);
		}
		return ret; 
	}

	/**
	 * Returns a new primitive array unboxing every element of the given array,
	 * substituting the given default for a null element.
	 * @return the Vector converted Objects from to primitives
	 */
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

	/**
	 * Collects the column at the given position from every row of the matrix.
	 * @return the Vector converted from primitives to Objects
	 */
	final static public short[] COLUMN(final short[][] matrix, final int col) {
		short[] ret = new short[matrix.length];
		for (int i = matrix.length; --i >= 0;) {
			ret[i] = matrix[i][col];
		}
		return ret;
	}

	/**
	 * Returns a new matrix that is the transpose of the given one.
	 * @return the Vector converted Objects from to primitives
	 */
	final static public short[][] TRANSPOSE(final short[][] matrix) {
		short[][] ret = new short[matrix[0].length][];
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
	 * @param numPointCol Column that contains the Number of Points in this Plane
	 * @param ColOffset the Column to start reading from when Cols == null
	 * @param Cols the List of Columns to read, null when consecutive!
	 * @param Plane the Plane returned; when null, a new Plane is created
	 * @return the Plane read.
	 */
	final static public short[] readVector(final ResultSet RS, final int numPointCol, final int ColOffset, short[] Plane, final short[] Cols)
		throws SQLException {
		if (!RS.next()) {
			return null;
		}
		int len;
		if (Plane != null) {
			len = Plane.length;
			if (Cols != null) {
				if (len > Cols.length)
					len = Cols.length;
			}
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
			Plane = new short[len];
		}
		int i = -1; //start in the correct Order
		try { //try to read as many Coordinates as possible!
			while (++i < len) {
				if (Cols == null) { //read consecutive Values
					Plane[i] = RS.getShort(i + ColOffset);
				} else {
					Plane[i] = RS.getShort(Cols[i]);
				}
			}
		} catch (Exception x) { //Resize the Array.
			short[] tmp = new short[i];
			System.arraycopy(Plane, 0, tmp, 0, i);
			Plane = tmp;
		}
		return Plane;
	}

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public short[] readVector(java.sql.ResultSet RS, int numPointCol, int ColOffset, short[] Plane)
		throws java.sql.SQLException {
		return readVector(RS, numPointCol, ColOffset, Plane, null);
	}

	/**
	 * Reads a single Point from the current ResultSet
	 * @return false, if the ResultSet was empty.
	 */
	final static public short[] readVector(java.sql.ResultSet RS, int numPointCol, int ColOffset)
		throws java.sql.SQLException {
		return readVector(RS, numPointCol, ColOffset, null, null);
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// Methoden f�r polynomiale Rechnungen
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Trims the entire vector by the given module.
	 * @see #TRIM_AT(short[], int, int)	 */
	final static public int TRIM_AT(final short[] vector, final short module) {
		return TRIM_AT(vector, module, vector.length); }

	/**
	 * Trims the first num elements of the vector by the given module.
	 * @see #TRIM_AT(short[], int, int)	 */
	final static public int TRIM_AT(final short[] vector, final short module, final int num) {
		return TRIM_AT(vector, module, 0, num); }
	
	/**
	 * Trims the Elements of this Vector by the given Module. 
	 * Superfluous Values are shifted up the Vector. 
	 * @see #SHL_AT(short[], int) which works similarly
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
	final static public int TRIM_AT(final short[] vector, final short module, final int start, final int stop) {
		short val, carryOver = 0;
		val = vector[start]; 
		for (int i = start; i < stop;) {
			//carryOver = Math.floor(val / module);
			if (val < 0) { //make vector[i] non-negative, i.e. canonic
				carryOver = (short) (((val+1) / module)-1); //
			} else {
				carryOver = (short) (val / module); //
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
		final short[] ret = new short[vector.length+1]; 
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
	final static public int COMPARE_TO(final short[] a, final int aNum, final short[] b, final int bNum) {
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
	final static public short[] SHR_AT(final short[] vector, final short module, final int shift) {
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
	final static public short[] SHL_AT(final short[] vector, final short module, final int shift) {
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
	final static public short[] SHR_AT(final short[] vector, final short module) {
		boolean lowBitSet = false;
		for (int i = vector.length; --i >= 0;) {
			short val = vector[i];
			if (lowBitSet) {
				val += module; }
			lowBitSet = ((val & 1) == 1);
			vector[i] = (short) (val >> 1);
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
	final static public short[] SHL_AT(final short[] vector, final int module) {
		boolean hiBitSet = false;
		for (int i = -1; ++i < vector.length;) {
			int val = vector[i] << 1; 
			if (hiBitSet) {
				val += 1; }
			hiBitSet = ((val & module) == module);
			vector[i] = (short) (val & (module-1));
		}
		if (!hiBitSet) {
			return vector; }
		//Enlarge the Array
		final short[] ret = new short[vector.length+1]; 
		System.arraycopy(vector, 0, ret, 0, vector.length);
		ret[vector.length] = 1;
		return ret; 
	}

	////////////////////////////////////////////////////////////////////////////////

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
	 * Returns a new array boxing every element of the given primitive array.
	 * @return the Column at the given Position
	 */
	final static public Short[] TO_INTEGER(final short[] vector) {
		final Short[] ret = new Short[vector.length];
		for (int i=vector.length; --i >= 0;) {
			ret[i] = new Short(vector[i]);
		}
		return ret; 
	}

	/**
	 * Returns a new primitive array unboxing every element of the given array,
	 * substituting the given default for a null element.
	 * @return the Column at the given Position
	 */
	final static public short[] TO_INTEGER(final Short[] vector, final short defaultForNulls) {
		final short[] ret = new short[vector.length];
		for (int i=vector.length; --i >= 0;) {
			final Short value = vector[i];
			ret[i] = (value == null) ? defaultForNulls : value.shortValue();
		}
		return ret; 
	}

	/**
	 * Rotates the given permutation left by one element, in place.
	 * @return the Permutation rotated left by 1 Element in Place	  */
	final static public short[] ROL(short[] this_) {
		final int last = this_.length-1;
		short tmp = this_[0]; 
		System.arraycopy(this_, 1, this_, 0, last);
		this_[last] = tmp; 
		return this_; 
	}

	/**
	 * Rotates the given permutation right by one element, in place.
	 * @return the Permutation rotated right by 1 Element in Place	  */
	final static public short[] ROR(short[] this_) {
		final int last = this_.length-1;
		short tmp = this_[last]; 
		System.arraycopy(this_, 0, this_, 1, last);
		this_[0] = tmp; 
		return this_; 
	}

	/** Returns the inverse Permutation in Place
	  * @return the inverse Permutation
	  * Cannot be calculated in Place!
	  */
	final static public short[] Inverse(short[] this_) {
		return Inverse(this_, new short[this_.length], (short) this_.length);
	}

	/** Returns the inverse Permutation in Place
	  * @return the inverse Permutation
	  * Cannot be calculated in Place!
	  */
	final static public short[] Inverse(short[] this_, short thisLength) {
		return Inverse(this_, new short[thisLength], thisLength);
	}

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	final static public short[] Inverse(short[] this_, short[] ret) {
		return Inverse(this_, ret, (short) this_.length);
	}

	/** Returns the inverse Permutation in Place
	 * Assumes the Permutation is complete.
	 * With incomplete Permutations,
	 * most of the Table remains 0 and has to be replaced by i implicitly:
	 * if (ret[i] == 0) ret[i] = i;
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	final static public short[] Inverse(short[] this_, short[] ret, short thisLength) {
		while (--thisLength >= 0) {
			ret[this_[thisLength]] = thisLength;
		}
		return ret;
	}

	/** Reverts a complete Encoding Table for Bytes
	  * This is equivalent to inverting the Permutation
	  * in Class streamIO.Copy.Monoid.SetShort.Permutation
	  */
	final static public short[] FullInverse(final short[] encoding) {
		return Inverse(encoding, VectorShort.Max(encoding));
	}

	/** Reverts a complete Encoding Table for Bytes
	  * This is equivalent to inverting the Permutation
	  * in Class streamIO.Copy.Monoid.SetShort.Permutation
	  */
	final static public short[] FullInverse(short[] encoding, short maxChar) {
		return Inverse(encoding, new short[maxChar + 1]);
	}

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	final static public short[] Identity(int length) {
		return Identity(new short[length]);
	}

	/** Returns the inverse Permutation in Place
	 * @return the inverse Permutation in Place != this
	 * Cannot be calculated in Place!
	 */
	final static public short[] Identity(short[] this_) {
		short len = (short) this_.length;
		while (--len >= 0) {
			this_[len] = len;
		}
		return this_;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for dynamic growing Array Operations
	////////////////////////////////////////////////////////////////////////////////

	/** @return this Vector with the Elements permuted according to the given Permutation     */
	/* final static public short[] permuteAt(short[] a, short[] perm) {
		//Undo the Row Permutations!
		int j, k = a.length;
		while (--k > 0) { //first row is not modified, because L[1,1]=1
			if (perm[k] != k) {
				final short tmp = a[k];
				a[k] = a[j = perm[k]];
				a[j] = tmp;
			}
		}
		return a;
	}
	faulty Implementation!
	 */

	/**
	 * Sets the Value at the given Position in the Array
	 * Returns a resized (larger OR smaller) Copy of the given Array
	 * filled with the given Value at the given Position.
	 */
	final static public short[] SET_AT(short[] arr, final int pos_, final short value_) {
		if (pos_ >= arr.length) {
			arr = resize(pos_+1, arr); 
		}
		arr[pos_] = value_;
		return arr;
	}

	/** Returns a resized (larger OR smaller) Copy of the given Array */
//	final static public short[] resize(final short[] arr, final int newExactSize) {
//		return resize(arr, newExactSize, arr.length); }

	/** Returns a resized (larger) Copy of the given Array */
	final static public short[] resize(final int newMinSize, final short[] arr) {
		return resize(arr, ENLARGED_CAPACITY(arr.length, DEFAULT_CAPACITY_INCR, newMinSize), arr.length); }

	/** Returns a resized (larger OR smaller) Copy of the given Array */
	final static public short[] resize(final short[] arr, final int newSize, int numToRetain) {
		short[] ret = new short[newSize];
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

	/** Returns a Copy of the given Array */
	final static public short[] COPY(short[] arr) {
		short[] ret = new short[arr.length];
		System.arraycopy(arr, 0, ret, 0, arr.length);
		return ret;
	}

	/** Returns a Copy of the given Array */
	final static public short[] copy(float[] arr) {
		short[] ret = new short[arr.length];
		int i = arr.length;
		while (--i >= 0) {
			ret[i] = (short) arr[i];
		}
		//		System.arraycopy(arr, 0, ret, 0, arr.length); //doesn't work: "ArrayStoreException"
		return ret;
	}

	/** Returns a Copy of the given Array */
	final static public short[] copy(double[] arr) {
		short[] ret = new short[arr.length];
		int i = arr.length;
		while (--i >= 0) {
			ret[i] = (short) arr[i];
		}
		//		System.arraycopy(arr, 0, ret, 0, arr.length); //doesn't work: "ArrayStoreException"
		return ret;
	}

	/** Returns a Copy of the given Array */
	final static public short[] COPY_AT(final short[] this_, final short[] arr) {
		return copyAt(this_, arr, 0, arr.length); 
	}

	/** Returns a Copy of the given Array */
	final static public short[] copyAt(short[] this_, final short[] arr, final int start, final int stop) {
		if (this_ == null) { //be error tolerant!
			this_ = new short[stop]; //could also skip or throw an Exception!
		}
		System.arraycopy(arr, start, this_, start, stop);
		return this_;
	}

	/** Returns a Copy of the given Array */
	final static public short[] copyAt(final short[] this_, final double[] arr) {
		return copyAt(this_, arr, 0, arr.length);
	}

	/** Returns a Copy of the given Array */
	final static public short[] copyAt(final short[] this_, final float[] arr) {
		return copyAt(this_, arr, 0, arr.length);
	}

	/** Returns a Copy of the given Array */
	final static public short[] copyAt(short[] this_, final double[] arr, final int start, final int stop) {
		if (this_ == null) { //be error tolerant!
			this_ = new short[stop]; 
		}
		for (int i = arr.length; --i >= 0; ) {
			this_[i] = (short) arr[i];
		}
		//System.arraycopy(arr, Start, this_, Start, Stop); //ArrayTypeException!
		return this_;
	}

	/** Returns a Copy of the given Array */
	final static public short[] copyAt(short[] this_, float[] arr, int start, int stop) {
		if (this_ == null) { //be error tolerant!
			this_ = new short[stop]; 
		}
		for (int i = stop; --i >= start;) {
			this_[i] = (short) arr[i];
		}
		//		System.arraycopy(arr, start, this_, start, stop); //ArrayTypeException!
		return this_;
	}

	/**
	 * Setting the Vectors to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vectors fit, they are returned unchanged!
	 */
	final static public short[][] setDimAt(final short[][] a, final int dim) {
		for (int i = a.length; --i >= 0; ) {
			a[i] = setDimAt(a[i], dim);
		}
		return a;
	}

	/**
	 * Setting the Vector to a specified Dimension.
	 * Fills up the Rest with 0s
	 * If the Vector fits, it is returned unchanged!
	 */
	final static public short[] setDimAt(final short[] a, final int dim) {
		if (a.length == dim) {
			return a;
		}
		short[] ret = new short[dim];
		System.arraycopy(a, 0, ret, 0, a.length);
		Arrays.fill(ret, a.length, dim, (short) 0);
		return a;
	}

	/**
	 * Sets every element of the given array to 0.
	 * @return the given Array with all Elements set to 0. 	 */
	final static public short[] zeroAt(final short[] ret) {
		return zeroAt(ret, 0, ret.length);
	}

	/**
	 * Sets the elements in the given range of the array to 0.
	 * @return the given Array with the Elements from Start (inclusive) to Stop (exclusive) set to 0. 	 */
	final static public short[] zeroAt(final short[] ret, final int start, final int stop) {
		java.util.Arrays.fill(ret, start, stop, (short) 0);
		return ret;
	}

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag.
	 * i.e. a[dim] = 1 and a[j] = 0 otherwise.
	 */
	final static public short[] oneAt(final short[] a, final int dim) {
		return diagAt(a, (short) 1, dim);
	}

	/**
	 * Setting to a diagonal Vector in Place using the Value given in diag,
	 * i.e. a[dim] = diag and a[j] = 0 otherwise.
	 */
	final static public short[] diagAt(final short[] a, final short diag, final int dim) {
		Arrays.fill(a, (short) 0);
		a[dim] = diag;
		return a;
	}

	/**
	 * Sets every element of the given array to 1.
	 * @return the given Array with all Elements set to 1. 	 */
	final static public short[] oneAt(final short[] ret) {
		return oneAt(ret, 0, ret.length);
	}

	// TODO: LOGIC: fills with (short) 0, not 1 - contradicts both this method's name
	// ("oneAt") and its own one-arg wrapper oneAt(short[])'s documented contract ("set to
	// 1"). Every element in [start, stop) is zeroed instead of set to 1. Same defect as
	// VectorChar.oneAt(char[], int, int) / VectorLong.oneAt(long[], int, int).
	/**
	 * Sets the elements in the given range of the array to 1.
	 * @return the given Array with the Elements from Start (inclusive) to Stop (exclusive) set to 0. 	 */
	final static public short[] oneAt(final short[] ret, final int start, final int stop) {
		java.util.Arrays.fill(ret, start, stop, (short) 0);
		return ret;
	}

	/**
	 * Fills every element of the given array with the given value.
	 * @return the given Array with all Elements set to the given Value. 	 */
	final static public short[] fillAt(final short[] ret, final short val) {
		return fillAt(ret, val, 0, ret.length);
	}

	/**
	 * Fills the elements in the given range of the array with the given value.
	 * @return the given Array with the Elements from Start (inclusive)
	 * to Stop (exclusive) set to the given Value.
	 */
	final static public short[] fillAt(final short[] ret, final short val, final int start, final int stop) {
		java.util.Arrays.fill(ret, start, stop, val);
		return ret;
	}

	/**
	 * Returns whether every element of the array is zero.
	 * @return true when every element of the Array is zero.
	 */
	final static public boolean isZero(final short[] arr) {
		return isZero(arr, 0, arr.length);
	}

	/**
	 * Returns whether every element in the given range of the array is zero.
	 * @return true when every element in the given range is zero.
	 */
	final static public boolean isZero(final short[] arr, final int start, int stop) {
		while (--stop >= start) {
			if (arr[stop] != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether this Vector is a Unity Vector in the given Dimension
	 */
	final static public boolean isOne(final short[] Row, final int dim) { //Assume a square Matrix
		int j = Row.length;
		while (--j >= 0) { //Use an Epsilon here
			if (j == dim) {
				if (Row[j] != 1) {
					return false;
				}
			} else {
				if (Row[j] != 0) {
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
	final static public boolean between(short[] left, short[] mid, short[] right) {
		int i = left.length;
		while (--i >= 0) {
			if ((left[i] < mid[i]) != (right[i] > mid[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines the Minimum and Maximum Value
	 * and sorts them into the first and second Argument.
	 */
	final static public void orderAt(short[] inOutMin, short[] inOutMax) {
		short tmp;
		for (int i = inOutMin.length; --i >= 0;) {
			if ((tmp = inOutMin[i]) < inOutMax[i]) {
				continue;
			}
			inOutMin[i] = inOutMax[i];
			inOutMax[i] = tmp;
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods returning a single Number from the Array
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the sum of all values in the array.
	 * @return The Sum of all Values in the Array. 	 */
	final static public long Sum(short[] arr) {
		return Sum(arr, 0, arr.length);
	}

	/**
	 * Returns the sum of the values in the given range of the array.
	 * @return The Sum of all Values in the Array.	 */
	final static public long Sum(short[] arr, int Start, int Stop) {
		if (Start == Stop) {
			return 0;
		}
		long Sum = arr[--Stop]; //0;
		while (--Stop >= Start) {
			Sum += arr[Stop];
		}
		return Sum;
	}

	/**
	 * Returns the product of all values in the array.
	 * @return The Product of all Values in the Array. 	 */
	final static public long Prod(short[] arr) {
		return Prod(arr, 0, arr.length);
	}

	/**
	 * Returns the product of the values in the given range of the array.
	 * @return The Product of all Values in the Array.	 */
	final static public long Prod(short[] arr, int Start, int Stop) {
		if (Start == Stop) {
			return 1;
		}
		long Prod = arr[--Stop]; //1;
		while (--Stop >= Start) {
			Prod *= arr[Stop];
		}
		return Prod;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Position of the Minimum Value of the Array.
	 */
	final static public int MinPos(short[] arr) {
		return MinPos(arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public int MinPos(short[] arr, int Start, int Stop) {
		int iMin = -1;
		int Min = Short.MAX_VALUE;
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
	final static public short[] Min2Val(final short[] arr, final short[] ret) {
		return Min2Val(arr, 0, arr.length, ret);
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first two Minimum Values of the Array.
	 */
	final static public int[] Min2Pos(final short[] arr, final int[] ret) {
		return Min2Pos(arr, 0, arr.length, ret);
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Minimum Values of the Array.
	 */
	final static public short[] Min2Val(short[] arr, int Start, int Stop, short[] ret) {
		int[] pos = Min2Pos(arr, Start, Stop, new int[ret.length]);
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
	final static public int[] Min2Pos(short[] arr, int start, int stop, int[] ret) {
		int inMin, iMin = inMin = -1; //the n Values contain the higher Maximum!
		short nMin, Min = nMin = Short.MAX_VALUE;
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
	final static public short[] SWAP_COLS_AT(final short[] a, final int dim1, final int dim2) {
		final short tmp = a[dim1]; a[dim1] = a[dim2]; a[dim2] = tmp;
		return a;
	}
	
	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public short MAX_VAL(final short[] arr) {
		return arr[MaxPos(arr)];
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int MaxPos(final short[] arr) {
		return MaxPos(arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public int MaxVal(short[] arr, int Start, int Stop) {
		return arr[MaxPos(arr, Start, Stop)];
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return the Index of the Maximum Value of the Array.
	 */
	final static public int MaxPos(short[] arr, int Start, int Stop) {
		int iMax = -1;
		int Max = Short.MIN_VALUE;
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
	final static public short[] Max2Val(short[] arr, short[] ret) {
		return Max2Val(arr, 0, arr.length, ret);
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the Indices of the first up to two Maximum Values of the Array.
	 */
	final static public int[] Max2Pos(short[] arr, int[] ret) {
		return Max2Pos(arr, 0, arr.length, ret);
	}

	/**
	 * It needs only 1 or 2 Comparisons for every Element.
	 * @return the first two Maximum Values of the Array.
	 */
	final static public short[] Max2Val(short[] arr, int Start, int Stop, short[] ret) {
		final int[] pos = Max2Pos(arr, Start, Stop, new int[ret.length]);
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
	final static public int[] Max2Pos(short[] arr, int Start, int Stop, int[] ret) {
		int inMax, iMax = inMax = -1; //the n Values contain the higher Maximum!
		short nMax, Max = nMax = Short.MIN_VALUE;
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
	final static public short[] MinMaxVal(short[] arr) {
		return MinMaxVal(arr, new short[2]);
	}

	/**
	 * Determines the Values of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public short[] MinMaxVal(short[] arr, short[] ret) {
		int[] pos = MinMaxPos(arr, ret.length);
		for (int i = ret.length; --i >= 0;) {
			ret[i] = arr[pos[i]];
		}
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
	final static public int[] MinMaxPos(final short[] arr) {
		return MinMaxPos(arr, 2);
	}

	/**
	 * Determines the Indices of the Minimum and the Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public int[] MinMaxPos(final short[] arr, final int numItems) {
		return Min2Max2Pos(arr, new int[numItems]);
	}

	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public short[] Min2Max2Val(short[] arr) {
		return Min2Max2Val(arr, new short[2]);
	}

	/**
	 * Determines the Indices of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements (for single Min and Max),
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public short[] Min2Max2Val(short[] arr, short[] MinMax) {
		short[] ret = new short[MinMax.length];
		int[] pos = new int[MinMax.length];
		Min2Max2Pos(arr, pos);
		int i = MinMax.length;
		while (--i >= 0) {
			ret[i] = arr[pos[i]];
		}
		return ret;
	}

	/**
	 * Determines the two Minimum and two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with the Indices of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] Min2Max2Pos(final short[] arr) {
		return Min2Max2Pos(arr, new int[2]);
	}

	/**
	 * Determines the Indices of the two Minimum and the two Maximum Values
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with the Indices of Minimum and Maximum.
	 * @see Statistic() to determine any Statistic (not just Min, Max or Median) from the Data.
	 */
	final static public int[] Min2Max2Pos(final short[] arr, final int[] posMinMax) {
		int i, iMin, iMax;
		int inMin, inMax;
		short Min, Max;
		short nMin, nMax;
		final boolean xMax = (posMinMax.length > 2);
		final boolean xMin = (posMinMax.length > 3);
		if (((i = arr.length) | 1) == 1) { //odd?
			iMin = iMax = inMin = inMax = --i;
			Min = Max = nMin = nMax = arr[i];
		} else { //a bit Overhead, but easier!
			iMin = iMax = inMin = inMax = -1; //cannot jump out earlier!
			Min = nMin = Short.MAX_VALUE;
			Max = nMax = Short.MIN_VALUE;
		}
		short tMin, tMax, tmp;
		int iTMin, iTMax, iTmp;
		while (i > 1) {
				if ((tMin = arr[iTMin = --i]) > //first compare Args
				 (tMax = arr[iTMax = --i])) {
				tmp = tMin;
				tMin = tMax;
				tMax = tmp;
				iTmp = iTMin;
				iTMin = iTMax;
				iTMax = iTmp;
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
			posMinMax[0] = inMin;
			posMinMax[1] = iMin;
		} else {
			posMinMax[0] = iMin;
		}
		if (xMax) {
			posMinMax[n] = iMax;
			posMinMax[n + 1] = inMax;
		} else {
			posMinMax[n] = iMax;
		}
		return posMinMax;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public short MinVal(short[] arr) {
		return MinVal(arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Minimum Value of the Array.
	 */
	final static public short MinVal(final short[] arr, int start, int stop) {
		short Min = Short.MAX_VALUE;
		while (--stop >= start) {
			if (Min > arr[stop]) {
				Min = arr[stop];
			}
		}
		return Min;
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public short Max(final short[] arr) {
		return Max(arr, 0, arr.length);
	}

	/**
	 * It needs only 1 Comparison for every Element.
	 * @return Maximum Value of the Array.
	 */
	final static public short Max(short[] arr, int Start, int Stop) {
		short Max = Short.MIN_VALUE;
		while (--Stop >= Start) {
			if (Max < arr[Stop]) {
				Max = arr[Stop];
			}
		}
		return Max;
	}

	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public short[] MinMax(final short[] arr) {
		return MinMax(arr, new short[2]);
	}

	/**
	 * Determines the Minimum and Maximum Value
	 * quickly and without modifying the Array.
	 * It needs only 3 Comparisons for every two Elements,
	 * which is only 1 (50%) more than for determining just the Minimum or Maximum.
	 * @return the MinMax Array
	 * with the first two Elements filled with Minimum and Maximum.
	 */
	final static public short[] MinMax(final short[] arr, final short[] minMax) {
		int i;
		short Min, Max;
		if (((i = arr.length) | 1) == 1) { //odd?
			Min = Max = arr[--i];
		} else {
			Min = Short.MAX_VALUE;
			Max = Short.MIN_VALUE;
		}
		short tMin, tMax, tmp;
		while (i > 1) {
				if ((tMin = arr[--i]) > //first compare Args
				 (tMax = arr[--i])) {
				tmp = tMin;
				tMin = tMax;
				tMax = tmp;
			}
			if (Min > tMin) { //then compare tMin and tMax to Min and Max
				Min = tMin;
			}
			if (Max < tMax) { //this saves 1/4 of the Comparisons
				Max = tMax;
			}
		}
		minMax[0] = Min;
		minMax[1] = Max;
		return minMax;
	}

	/**
	  * Returns the Euclidean norm (square root of the sum of squares) of the given array.
	  * @return the Norm of the given Array
	  */
	final static public double Norm(short[] arr) {
		return Math.sqrt(SqrNorm(arr, arr.length));
	}

	/**
	  * Returns the squared Euclidean norm (sum of squares) of the given array.
	  * @return the squared Norm of the given Array
	  */
	final static public double SqrNorm(short[] arr) {
		return SqrNorm(arr, arr.length);
	}

	/**
	  * This Value can well exceed the Range of valid Numbers,
	  * but that should be avoided anyway by renorming.
	  * Accuracy is not affected when using int Point Numbers.
	  *
	  * @return the squared Norm of the given Array
	  */
	final static public long SqrNorm(short[] arr, int len) {
		long norm = 0; //Calculate the Norm
		while (--len >= 0) {
			norm += arr[len] * arr[len];
		} //sqr(arr[len]); }
		return norm;
	}

	/**
	  * Returns the squared Euclidean distance between the two given arrays.
	  * @param arr1 first  Vector, not modified.
	  * @param arr2 second Vector, not modified.
	  * @return the squared Norm of the Distance between the given Arrays
	  */
	final static public long SqrDist(short[] arr1, short[] arr2) {
		long diff, norm = 0; //Calculate the Norm
		int i = arr1.length;
		while (--i >= 0) {
			diff = arr1[i] - arr2[i];
			norm += diff * diff;
		}
		return norm;
	}

	/**
	  * Returns the sum of the absolute differences between corresponding elements
	  * of the two given arrays.
	  * @param arr1 first  Vector, not modified.
	  * @param arr2 second Vector, not modified.
	  * @return the absolute Norm of the Distance between the given Arrays
	  */
	final static public int AbsVDist(short[] arr1, short[] arr2) {
		int diff, norm = 0; //Calculate the Norm
		int i = arr1.length;
		while (--i >= 0) {
			if (0 < (diff = arr1[i] - arr2[i])) {
				norm += diff;
				continue;
			}
			norm -= diff;
		}
		return norm;
	}

	/**
	  * Returns the sum of absolute differences between the two given arrays,
	  * also writing the signed difference of each element pair into diff.
	  * @param diff is an Output Parameter being filled with the Difference Vector.
	  * @return the squared Norm of the given Array
	  */
	final static public int AbsDiffNorm(short[] arr1, short[] arr2, short[] diff) {
		short dif, norm = 0; //Calculate the Norm
		for (int i = arr1.length; --i >= 0; ) {
			//			norm+=Math.abs(diff[i] = arr1[i]-arr2[i]); }
			if (0 < (dif = diff[i] = (short) (arr1[i] - arr2[i]))) { //avoid calling expensive Math.abs
				norm += dif;
				continue;
			}
			norm -= dif;
		}
		return norm;
	}

	/**
	  * Returns the sum of the absolute values of the array's elements.
	  * @return the sum of the absolute Values of the given Array
	  */
	final static public int AbsV_Norm(short[] arr) {
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

	/**
	  * By Definition Elements outside the Array are 0
	  * @return the scalar Product of the given Arrays up to the given Length.
	  */
	final static public long ScalarProd(short[] arr1, short[] arr2) {
		int len = arr1.length;
		if (len > arr2.length) {
			len = arr2.length;
		} //use the Minimum, because higher Elements are assumed to be 0.
		return ScalarProd(arr1, arr2, 0, len);
	}

	/**
	  * By Definition Elements outside the Array are 0
	  * @return the scalar Product of the given Arrays up to the given Length.
	  */
	final static public long ScalarProd(short[] arr1, short[] arr2, int start, int stop) {
		long ret = 0;
		while (--stop >= start) {
			ret += arr1[stop] * arr2[stop];
		}
		return ret;
	}

	/**
	  * Returns the largest of the pairwise (elementwise) minimums of the two given arrays.
	  * @return the Scalar Product of the two Vectors.
	  */
	final static public int MaxMinProd(short[] a, short[] arg) {
		return MaxMinProd(a, arg, 0, arg.length);
	}

	/**
	  * Returns the largest of the pairwise (elementwise) minimums of the two given
	  * arrays, over the given range.
	  * @return the MaxMin Product of the two Vectors.
	  */
	final static public int MaxMinProd(short[] a, short[] arg, int start, int stop) {
		int x, y, max = Short.MIN_VALUE; //FALSE; //can also start with any lower Value!
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
	  * Negates every element of the given array, in place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] NegAt(short[] ret) {
		return NegAt(ret, 0, ret.length);
	}

	/**
	  * Negates the elements in the given range of the array, in place.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] NegAt(short[] ret, int start, int stop) {
		return Neg(ret, ret, start, stop);
	}

	/**
	  * Writes the negation of x into ret, over the given range.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] Neg(short[] ret, short[] x, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short)-x[stop];
		}
		return ret;
	}

	/**
	  * Writes the negation of the given double array into ret, over the given range.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] Neg(short[] ret, double[] x, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) - x[stop];
		}
		return ret;
	}

	/**
	  * Writes the negation of the given float array into ret, over the given range.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] Neg(short[] ret, float[] x, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) - x[stop];
		}
		return ret;
	}

	/**
	  * Returns a new array containing the negation of the given array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] Neg(short[] x) {
		return Neg(new short[x.length], x, 0, x.length);
	}

	/**
	  * Returns a new array containing the negation of the given range of the array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] Neg(short[] x, int start, int stop) {
		return Neg(new short[x.length], x, start, stop);
	}

	/**
	  * Returns a new short array containing the negation of the given double array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] Neg(double[] x) {
		return Neg(new short[x.length], x, 0, x.length);
	}

	/**
	  * Returns a new short array containing the negation of the given range of the
	  * given double array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] Neg(double[] x, int start, int stop) {
		return Neg(new short[x.length], x, start, stop);
	}

	/**
	  * Returns a new short array containing the negation of the given float array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] Neg(float[] x) {
		return Neg(new short[x.length], x, 0, x.length);
	}

	/**
	  * Returns a new short array containing the negation of the given range of the
	  * given float array.
	  * @return the Negative of the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] Neg(float[] x, int start, int stop) {
		return Neg(new short[x.length], x, start, stop);
	}

	/**
	  * This is used e.g. in deriving the Distances of Poisson Distributions
	  * from the given Probabilities.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] ABSV_AT(short[] ret) {
		return AbsVAt(ret, 0, ret.length);
	}

	/**
	  * Replaces the elements in the given range of the array with their absolute
	  * value, in place.
	  * @return the absolute Value of the Values in the given Array
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] AbsVAt(short[] ret, int start, int stop) {
		int tmp; //Calculate the Norm
		while (--stop >= start) {
			if (0 <= (tmp = ret[stop])) {
				continue;
			}
			ret[stop] = (short)-tmp;
		}
		return ret;
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Binary Operations
	///////////////////////////////////////////////////////////////////////////////////

	/**
	  * Clamps every element of the given array to at most the given limit, in place.
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public short[] MinAt(short[] ret, short limit) {
		return MinAt(ret, limit, 0, ret.length);
	}

	/**
	  * Clamps the elements in the given range to at most the given limit, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] MinAt(short[] ret, short limit, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > limit) {
				ret[stop] = limit;
			}
		}
		return ret;
	}

	/**
	  * Replaces each element of ret with the elementwise minimum of ret and arr, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] MinAt(short[] ret, short[] arr) {
		return MinAt(ret, arr, 0, arr.length);
	}

	/**
	  * Replaces the elements of ret in the given range with the elementwise
	  * minimum of ret and arr, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] MinAt(short[] ret, short[] arr, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] > arr[stop]) {
				ret[stop] = arr[stop];
			}
		}
		return ret;
	}

	/**
	  * Clamps every element of the given array to at least the given limit, in place.
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public short[] MaxAt(final short[] ret, final short limit) {
		return MaxAt(ret, limit, 0, ret.length);
	}

	/**
	  * Clamps the elements in the given range to at least the given limit, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] MaxAt(final short[] ret, final short limit, final int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < limit) {
				ret[stop] = limit;
			}
		}
		return ret;
	}

	/**
	  * Replaces each element of ret with the elementwise maximum of ret and arr, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] MaxAt(short[] ret, short[] arr) {
		return MaxAt(ret, arr, 0, arr.length);
	}

	/**
	  * Replaces the elements of ret in the given range with the elementwise
	  * maximum of ret and arr, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] MaxAt(short[] ret, short[] arr, int start, int stop) {
		while (--stop >= start) {
			if (ret[stop] < arr[stop]) {
				ret[stop] = arr[stop];
			}
		}
		return ret;
	}

	/**
	  * Adds the given increment to every element of the array, in place.
	  * To implement subAt, just negate the Increment.
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @return the given Array incremented by the given Increment
	  */
	final static public short[] ADD_AT(short[] ret, int Increment) {
		return addAt(ret, Increment, 0, ret.length);
	}

	/**
	  * Adds the given increment to the elements in the given range, in place.
	  * @return the given Array incremented by the given Increment
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Increment the Increment to add to
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] addAt(short[] ret, int Increment, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += Increment;
		}
		return ret;
	}

	/**
	  * Adds arr elementwise into ret, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] ADD_AT(short[] ret, short[] arr) {
		return addAt(ret, arr, 0, arr.length);
	}

	/**
	  * Adds arr elementwise into ret over the given range, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] addAt(final short[] ret, final short[] arr, final int start, final int stop) {
		return addAt(ret, arr, start, stop, 0);}

	/**
	  * Adds arr elementwise into ret over the given range at the given offset
	  * into ret, in place.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] addAt(final short[] ret, final short[] arr, final int start, int stop, final int retOffset) {
		while (--stop >= start) {
			ret[stop+retOffset] += arr[stop];
		}
		return ret;
	}

	/**
	  * Writes the elementwise sum of sum1 and sum2 into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] add(short[] ret, short[] sum1, short[] sum2) {
		return add(ret, sum1, sum2, 0, sum1.length);
	}

	/**
	  * Writes the elementwise sum of sum1 and sum2 into ret over the given range,
	  * copying the longer operand's tail through when the two arrays' lengths differ.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] add(short[] ret, short[] sum1, short[] sum2, int start, int stop) {
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
			ret[stop] = (short) (sum1[stop] + sum2[stop]);
		}
		return ret;
	}

	/**
	  * Writes sum1 incremented by incr into ret.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] add(short[] ret, short[] sum1, short incr) {
		return add(ret, sum1, incr, 0, sum1.length);
	}

	/**
	  * Writes sum1 incremented by incr into ret over the given range.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] add(short[] ret, short[] sum1, short incr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (sum1[stop] + incr);
		}
		return ret;
	}

	/**
	  * Returns a new array containing the elementwise sum of the two given arrays.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] add(short[] sum1, short[] sum2) {
		return add(sum1, sum2, 0, sum1.length);
	}

	/**
	  * Returns a new array of length stop containing the elementwise sum of the two
	  * given arrays over the given range.
	  * @return the Sum of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] add(short[] sum1, short[] sum2, int start, int stop) {
		return add(new short[stop], sum1, sum2, start, stop);
	}

	/**
	  * Subtracts arr elementwise from ret, in place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] subAt(short[] ret, short[] arr) {
		return subAt(ret, arr, 0, arr.length);
	}

	/**
	  * Subtracts arr elementwise from ret over the given range, in place.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] subAt(final short[] ret, final short[] arr, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= arr[stop];
		}
		return ret;
	}

	/**
	  * Writes the elementwise difference (min - sub) into ret.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] subt(final short[] ret, final short[] min, final short[] sub) {
		return subt(ret, min, sub, 0, sub.length);
	}

	/**
	  * Writes the elementwise difference (min - sub) into ret over the given range.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] subt(final short[] ret, final short[] min, final short[] sub, final int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (min[stop] - sub[stop]);
		}
		return ret;
	}

	/**
	  * Returns a new array containing the elementwise difference min - sub.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] subt(final short[] min, final short[] sub) {
		return subt(min, sub, 0, sub.length);
	}

	/**
	  * Returns a new array containing the elementwise difference min - sub over
	  * the given range.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] subt(short[] min, short[] sub, int start, int stop) {
		return subt(new short[stop], min, sub, start, stop);
	}

	/**
	  * Writes the elementwise difference (min - sub) into ret over the given range,
	  * where min is a double array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] subt(short[] ret, double[] min, short[] sub, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (min[stop] - sub[stop]);
		}
		return ret;
	}

	/**
	  * Writes the elementwise difference (min - sub) into ret over the given range,
	  * where min is a float array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] subt(short[] ret, float[] min, short[] sub, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (min[stop] - sub[stop]);
		}
		return ret;
	}

	/**
	  * Returns a new array containing the elementwise difference min - sub,
	  * where min is a double array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] subt(double[] min, short[] sub) {
		return subt(min, sub, 0, sub.length);
	}

	/**
	  * Returns a new array containing the elementwise difference min - sub over the
	  * given range, where min is a double array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] subt(double[] min, short[] sub, int start, int stop) {
		return subt(new short[stop], min, sub, start, stop);
	}

	/**
	  * Returns a new array containing the elementwise difference min - sub,
	  * where min is a float array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] subt(float[] min, short[] sub) {
		return subt(min, sub, 0, sub.length);
	}

	/**
	  * Returns a new array containing the elementwise difference min - sub over the
	  * given range, where min is a float array.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] subt(float[] min, short[] sub, int start, int stop) {
		return subt(new short[stop], min, sub, start, stop);
	}

	/**
	  * Multiplies every element of the given array by the given factor, in place.
	  * To implement divAt, just invert the Factor
	  * @param Factor the Factor to multiply with
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @return the given Array multiplied by the given Factor
	  */
	final static public short[] MUL_AT(short[] ret, int Factor) {
		return mulAt(ret, Factor, 0, ret.length);
	}

	/**
	  * Multiplies the elements in the given range by the given factor, in place.
	  * @return the Product of the Array with the given Factor
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param Factor the Factor to multiply with
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] mulAt(short[] ret, int Factor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] *= Factor;
		}
		return ret;
	}

	/**
	  * Multiplies ret elementwise by arr, in place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] mulAt(short[] ret, short[] arr) {
		return mulAt(ret, arr, 0, arr.length);
	}

	/**
	  * Multiplies ret elementwise by arr over the given range, in place.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] mulAt(short[] ret, short[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] *= arr[stop];
		}
		return ret;
	}

	/**
	  * Writes the elementwise product of min and sub into ret.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] mul(short[] ret, short[] min, short[] sub) {
		return mul(ret, min, sub, 0, sub.length);
	}

	/**
	  * Writes the elementwise product of min and sub into ret over the given range.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] mul(short[] ret, short[] min, short[] sub, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (min[stop] * sub[stop]);
		}
		return ret;
	}

	/**
	  * Returns a new array containing min scaled by the given factor.
	  * @return a new Array containing the Product of the given Array
	  * @param ret Array with the Values to be processed.
	  */
	final static public short[] mul(short[] min, int factor) {
		return mul(new short[min.length], min, factor, 0, min.length);
	}

	/**
	  * Writes min scaled by the given factor into ret.
	  * @return a new Array containing the Product of the given Array
	  * @param ret Array with the Values to be processed.
	  */
	final static public short[] mul(short[] ret, short[] min, int factor) {
		return mul(ret, min, factor, 0, min.length);
	}

	/**
	  * Writes min scaled by the given factor into ret over the given range.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] mul(short[] ret, short[] min, int factor, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (min[stop] * factor);
		}
		return ret;
	}

	/**
	  * Returns a new array containing the elementwise product of the two given arrays.
	  * @return the Difference of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] mul(short[] min, short[] sub) {
		return mul(min, sub, 0, sub.length);
	}

	/**
	  * Returns a new array containing the elementwise product of the two given
	  * arrays over the given range.
	  * @return the Product of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] mul(short[] min, short[] sub, int start, int stop) {
		return mul(new short[stop], min, sub, start, stop);
	}

	/**
	  * Divides ret elementwise by arr, in place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] divAt(short[] ret, short[] arr) {
		return divAt(ret, arr, 0, arr.length);
	}

	/**
	  * Divides ret elementwise by arr over the given range, in place.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] divAt(short[] ret, short[] arr, int start, int stop) {
		while (--stop >= start) {
			ret[stop] /= arr[stop];
		}
		return ret;
	}

	/**
	  * Writes the elementwise quotient of min divided by sub into ret.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] div(short[] ret, short[] min, short[] sub) {
		return div(ret, min, sub, 0, sub.length);
	}

	/**
	  * Writes the elementwise quotient of min divided by sub into ret over the given range.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] div(short[] ret, short[] min, short[] sub, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (min[stop] / sub[stop]);
		}
		return ret;
	}

	/**
	  * Returns a new array containing the elementwise quotient of the two given arrays.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  */
	final static public short[] div(short[] min, short[] sub) {
		return div(min, sub, 0, sub.length);
	}

	/**
	  * Returns a new array containing the elementwise quotient of the two given
	  * arrays over the given range.
	  * @return the Quotient of the given Arrays
	  * @param ret Array with the Values to be processed. Also returned by this Method.
	  * @param start Index from  where the Array is processed
	  * @param stop  Index up to where the Array is processed (not ret[stop]!)
	  */
	final static public short[] div(short[] min, short[] sub, int start, int stop) {
		return div(new short[stop], min, sub, start, stop);
	}

	/**
	 * Updates each element of ret, over the given range, to the greater of itself
	 * and the minimum of the corresponding element of a and the scalar y.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public short[] MaxMin(short[] ret, short[] a, short y, int start, int stop) {
		short x; //FALSE; //can also start with any lower Value!
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

	/**
	 * Updates each element of ret, over the given range, to the greater of itself
	 * and the minimum of the corresponding elements of a and b.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min b)	  */
	final static public short[] MaxMin(short[] ret, short[] a, short[] b, int start, int stop) {
		short x, y; //FALSE; //can also start with any lower Value!
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

	/**
	 * Updates each element of ret to the greater of itself and the minimum of
	 * the corresponding elements of a and b, over the full array.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min b)	  */
	final static public short[] MaxMin(short[] ret, short[] a, short[] b) {
		return MaxMin(ret, a, b, 0, ret.length);
	}

	/**
	 * Updates each element of ret to the greater of itself and the minimum of
	 * the corresponding element of a and the scalar y, over the full array.
	 * @return MaxMin Product of the two Vectors: ret maxAt(a min y)	  */
	final static public short[] MaxMin(short[] ret, short[] a, short y) {
		return MaxMin(ret, a, y, 0, ret.length);
	}

	///////////////////////////////////////////////////////////////////////////////////
	/// Ring Methods
	///////////////////////////////////////////////////////////////////////////////////

	/// these Methods with scalar Parameters have been removed,
	/// because they can be replaced by their addAt and mulAt Counterparts.
	/**  Linear Mapping in Place: x+=a * y	 replaced by addAt(a*y)  */
	//	final static public short[] addProdAt (short[] ret, int a, int y) {
	/**  Linear Mapping in Place: x-=a * y	 replaced by subAt(a*y)  */
	//	final static public short[] subtProdAt(short[] ret, int a, int y) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by LinAt(a, y*b)  */
	//	final static public short[] BiLinAt   (short[] ret, int a, int y, int b) {
	/**BiLinear Mapping in Place: x*=a + y*b replaced by LinAt(a, y*b)  */
	//	final static public short[] BiLinAt   (short[] ret, short[] a, int y, int b) {

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(final short[] ret, final short[] a, final int y, final int start, final int stop) {
		return addProdAt(ret, a, y, start, stop, 0);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(final short[] ret, final short[] a, final int y, final int start, int stop, final int retOffset) {
		while (--stop >= start) {
			ret[stop+retOffset] += a[stop] * y;
		}
		return ret;
	}
	
	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, short[] a, short[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y[stop];
		}
		return ret;
	}

	/**  Linear Mapping in Place: x + a*y	 */
	final static public short[] addProd(short[] ret, short[] x, short[] a, int y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (x[stop] + a[stop] * y);
		}
		return ret;
	}

	/**  Linear Mapping in Place: x + a*y	 */
	final static public short[] addProd(short[] ret, short[] x, short[] a, short[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (x[stop] + a[stop] * y[stop]);
		}
		return ret;
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, double[] a, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y;
		}
		return ret;
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, float[] a, double y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] += a[stop] * y;
		}
		return ret;
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, int a, short[] y, int start, int stop) {
		return addProdAt(ret, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, double a, double[] y, int start, int stop) {
		return addProdAt(ret, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, double a, double[] y) {
		return addProdAt(ret, y, a, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, double[] a, double y) {
		return addProdAt(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, int a, short[] y) {
		return addProdAt(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, short[] a, int y) {
		return addProdAt(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProdAt(short[] ret, short[] a, short[] y) {
		return addProdAt(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProd(short[] ret, short[] x, int a, short[] y, int start, int stop) {
		return addProd(ret, x, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProd(short[] ret, short[] x, int a, short[] y) {
		return addProd(ret, x, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProd(short[] ret, short[] x, short[] a, int y) {
		return addProd(ret, x, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProd(short[] ret, short[] x, short[] a, short[] y) {
		return addProd(ret, x, a, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProd(short[] x, int a, short[] y, int start, int stop) {
		return addProd(new short[stop], x, y, a, start, stop);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProd(short[] x, int a, short[] y) {
		return addProd(new short[x.length], x, a, y, 0, x.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProd(short[] x, short[] a, int y) {
		return addProd(new short[x.length], x, a, y, 0, x.length);
	}

	/**  Linear Mapping in Place: x += a*y	 */
	final static public short[] addProd(short[] x, short[] a, short[] y) {
		return addProd(new short[x.length], x, a, y, 0, x.length);
	}

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public short[] subtProdAt(short[] ret, short[] a, short[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] -= a[stop] * y[stop];
		}
		return ret;
	}

	/**  Linear Mapping in Place: x - a*y	 */
	final static public short[] subtProd(short[] ret, short[] x, short[] a, short[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (x[stop] - a[stop] * y[stop]);
		}
		return ret;
	}

	/**  Linear Mapping in Place: x -= a*y	 */
	final static public short[] subtProdAt(short[] ret, short[] a, short[] y) {
		return subtProdAt(ret, a, y, 0, ret.length);
	}

	/**  Linear Mapping: x - a*y	 */
	final static public short[] subtProd(short[] ret, short[] x, short[] a, short[] y) {
		return subtProd(ret, x, a, y, 0, ret.length);
	}

	/**  Linear Mapping: x - a*y	 */
	final static public short[] subtProd(short[] x, short[] a, short[] y) {
		return subtProd(new short[x.length], x, a, y, 0, x.length);
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public short[] LinAt(short[] ret, short a, short y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (ret[stop] * a + y);
		}
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public short[] LinAt(short[] ret, short[] a, short y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (ret[stop] * a[stop] + y);
		}
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public short[] LinAt(short[] ret, short a, short[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (ret[stop] * a + y[stop]);
		}
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public short[] LinAt(short[] ret, short[] a1, short[] y, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (ret[stop] * a1[stop] + y[stop]);
		}
		return ret;
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public short[] LinAt(short[] ret, short a1, short y) {
		return LinAt(ret, a1, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public short[] LinAt(short[] ret, short[] a1, short y) {
		return LinAt(ret, a1, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public short[] LinAt(short[] ret, short a1, short[] y) {
		return LinAt(ret, a1, y, 0, ret.length);
	}

	/**  Linear Mapping in Place: x*=a + y	*/
	final static public short[] LinAt(short[] ret, short[] a1, short[] y) {
		return LinAt(ret, a1, y, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, short[] a, short[] y, short[] b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (x[stop] * a[stop] + y[stop] * b[stop]);
		}
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, short[] a, short[] y, int b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (x[stop] * a[stop] + y[stop] * b);
		}
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, int a, short[] y, short[] b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (x[stop] * a + y[stop] * b[stop]);
		}
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, int a, short[] y, int b, int start, int stop) {
		while (--stop >= start) {
			ret[stop] = (short) (x[stop] * a + y[stop] * b);
		}
		return ret;
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, short[] a, int y, short[] b, int start, int stop) {
		return BiLin(ret, x, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, int a, short[] y, int b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, short[] a, short[] y, int b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, short[] a, short[] y, short[] b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, int a, short[] y, short[] b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] ret, short[] x, short[] a, int y, short[] b) {
		return BiLin(ret, x, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, short[] a, short[] y, short[] b, int start, int stop) {
		return BiLin(new short[x.length], x, a, b, y, start, stop);
	}

	/**BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, short[] a, short[] y, int b, int start, int stop) {
		return BiLin(new short[x.length], x, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, int a, short[] y, short[] b, int start, int stop) {
		return BiLin(new short[x.length], x, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, int a, short[] y, int b, int start, int stop) {
		return BiLin(new short[x.length], x, a, y, b, start, stop);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, short[] a, int y, short[] b, int start, int stop) {
		return BiLin(new short[x.length], x, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, int a, short[] y, int b) {
		return BiLin(new short[x.length], x, a, y, b, 0, x.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, short[] a, short[] y, int b) {
		return BiLin(new short[x.length], x, a, y, b, 0, x.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, short[] a, short[] y, short[] b) {
		return BiLin(new short[x.length], x, a, y, b, 0, x.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, int a, short[] y, short[] b) {
		return BiLin(new short[x.length], x, a, y, b, 0, x.length);
	}

	/** BiLinear Mapping in Place: x*a + y*b */
	final static public short[] BiLin(short[] x, short[] a, int y, short[] b) {
		return BiLin(new short[x.length], x, a, y, b, 0, x.length);
	}

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public short[] BiLinAt(short[] ret, short[] a, short[] y, int b, int start, int stop) {
		return BiLin(ret, ret, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public short[] BiLinAt(short[] ret, int a, short[] y, short[] b, int start, int stop) {
		return BiLin(ret, ret, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public short[] BiLinAt(short[] ret, int a, short[] y, int b, int start, int stop) {
		return BiLin(ret, ret, a, y, b, start, stop);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public short[] BiLinAt(short[] ret, short[] a, int y, short[] b, int start, int stop) {
		return BiLin(ret, ret, a, b, y, start, stop);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public short[] BiLinAt(short[] ret, int a, short[] y, int b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public short[] BiLinAt(short[] ret, short[] a, short[] y, int b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public short[] BiLinAt(short[] ret, short[] a, short[] y, short[] b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length);
	}

	/** BiLinear Mapping in Place: x*=a + y*b */
	final static public short[] BiLinAt(short[] ret, int a, short[] y, short[] b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length);
	}

	/**BiLinear Mapping in Place: x*=a + y*b */
	final static public short[] BiLinAt(short[] ret, short[] a, int y, short[] b) {
		return BiLin(ret, ret, a, y, b, 0, ret.length);
	}

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
	protected static int Partition(Object[] Items, int p, int r) {
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
	public static void QuickSort(Object[] Items) {
		QuickSort(Items, 0, Items.length - 1);
	}

	/**QuickSort Algorithm:
	 * Divide and Conquer Method:
	 * The Array is divided into two, of which both are again sorted.	 */
	public static void QuickSort(Object[] Items, int p, int r) {
		if (p >= r)
			return; // Items; //not effective to return since recursive!
		int q = Partition(Items, p, r);
		QuickSort(Items, p, q);
		QuickSort(Items, q + 1, r);
	}

	/**Creates the i-th Order Statistic using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum	 */
	public static Object Statistic(Object[] Items, int i) {
		return Statistic(Items, 0, Items.length - 1, i);
	}

	/**Creates the i-th Order Statistic using the QuickSort Algorithm.
	 * n-th Order Statistic = Maximum
	 * n/2  Order Statistic = Median
	 * 0-th Order Statistic = Minimum	 */
	public static Object Statistic(Object[] Items, int p, int r, int i) {
		if (p >= r)
			return Items[p];
		int q = Partition(Items, p, r); //after this all Elements
		int k = q - p + 1;
		if (i <= k)
			return Statistic(Items, p, q, i); //
		else
			return Statistic(Items, q + 1, r, i - k);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	////////////////////////////////////////////////////////////////////////////////

	/** The array buffer into which the components of the Array are
	  * stored. The capacity of the Array is the length of this array buffer.	 */
	protected short[] items;

	////////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////////

	/**Constructs an empty VectorShort with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the VectorShort.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the VectorShort overflows.	 */
	public VectorShort(int initialCapacity, int capacityIncrement_) {
		super();
		items = new short[initialCapacity];
		capacityIncrement = capacityIncrement_;
		//		mEnum = new ArrayEnum(Items, ItemCount);
		//		mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty VectorShort with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the VectorShort.	 */
	public VectorShort(int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty VectorShort.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public VectorShort() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/** Constructs an VectorShort by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public VectorShort(Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an VectorShort from the given Object.	  */
	public VectorShort(Object arg, int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorShort from the given Object.	  */
	public VectorShort(short[] arg, int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorShort from the given Object
	  * and copies the Elements into this VectorShort.	  */
	public VectorShort(short[] arg) {
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
	final public VectorShort addItem(final short item) {
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
			short[] oldData = items;
			items = new short[itemCount];
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
		final int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) 
			return oldCapacity;
		final int newCapacity = ENLARGED_CAPACITY(oldCapacity, capacityIncrement, minCapacity); 
		final short[] oldData = items; items = new short[newCapacity];
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
	public synchronized short getShortAt(final int index) {
		if (indexInRange(index)) 
			return items[index];
		return 0;
	}

	/**
	 * Returns the item at the given position boxed as a {@link ByRefShort}.
	 * @return the item at the given Position as an Object
	 */
	public Object getAt(final int i) {
		return new ByRefShort(getShortAt(i));
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
	public short setAt(final int index, final short value) {
		short ret = 0; //Short.MIN_VALUE; 
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
		return new ByRefShort(setAt(index, (short) ByRefInt.TO_INT(value))); 
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
	public void insertAt(final int index, final short value) {
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
	public short removeAt(final int index) {
		// TODO: LOGIC: `--itemCount` runs unconditionally before the range check; when
		// `index` is out of range (> the pre-decrement itemCount, or negative) this method
		// returns early with 0 as if nothing happened, but itemCount has already been
		// permanently decremented, corrupting the vector's size even though no element was
		// actually removed. Same defect as VectorObject.removeAt(int) / VectorChar.removeAt(int).
		if (index > --itemCount)  //
			return 0;
		final short ret = items[index]; 
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
	public double getAt(int Row, int Col) {
		return items[Row * dimFactors[0] + Col * dimFactors[1]];
	}

	/** sets the given Value 	 */
	public void setAt(int Row, int Col, short Value) {
		items[Row * dimFactors[0] + Col * dimFactors[1]] = Value;
	}

	/**
	 * Returns the value at the given sheet, row and column, viewing this vector's
	 * backing array as a rectangular 3D tensor.
	 * @return the Value at the given Position	 */
	public double getAt(int Sheet, int Row, int Col) {
		return items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]];
	}

	/** sets the given Value 	 */
	public void setAt(int Sheet, int Row, int Col, short Value) {
		items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]] = Value;
	}

	/**
	 * Returns the value at the given multi-dimensional index of this vector viewed
	 * as a rectangular multi-index array.
	 * @return the Value at the given Position	 */
	public double getAt(final int[] Col) {
		return items[multiIndex(Col)];
	}
	
	/** sets the given Value 	 */
	public void setAt(final int[] Col, final short Value) {
		items[multiIndex(Col)] = Value;
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
	final static public short GET_AT(final short[] a, final int index) {
		return GET_AT(a, index, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public short GET_AT(final short[] a, final int index, final int stop) {
		return GET_AT(a, index, (short)0, stop); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public short GET_AT(final short[] a, final int index, final short defaultValue) {
		return GET_AT(a, index, defaultValue, a.length); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public short GET_AT(final short[] a, final int index, final short defaultValue, final int stop) {
		return GET_AT(a, index, defaultValue, stop, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public short GET_AT(final short[] a, final int index, final short defaultValue, final int stop, final int start) {
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
	final static public short[] GET_AT(final short[] a, final VectorInt index) {
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
	final static public short[] GET_AT(final short[] a, final VectorInt index, short[] ret) {
		return GET_AT(a, index.items, ret, index.itemCount); 
	}
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public short[] GET_AT(final short[] a, final int[] index) {
		return GET_AT(a, index, null); }  
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public short[] GET_AT(final short[] a, final int[] index, final short[] ret) {
		return GET_AT(a, index, ret, index.length); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public short[] GET_AT(final short[] a, final int[] index, final short[] ret, int stop) {
		return GET_AT(a, index, ret, stop, 0); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public short[] GET_AT(final short[] a, final int[] index, short[] ret, final int stop, final int start) {
		if((ret == null) || (ret.length < stop))
			ret = new short[stop];
		//else if (ret.length > stop) //rather leave the Values alone?!?
		//	Arrays.fill(ret, stop, ret.length, 0); 
		for(int i = stop; --i >= start; )
			ret[i] = (index[i] < a.length) ? a[index[i]] : 0; 
		return ret;
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
	public VectorShort copyAt(final short[] arg_) {
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
		if (arg instanceof VectorShort) {
			VectorShort arg_ = (VectorShort) arg;
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
		if (arg instanceof VectorShort) {
			VectorShort arg_ = (VectorShort) arg;
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
		return new VectorShort(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////////
	// Multiplication with a Permutation
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the minimum value in this vector.
	 * @return the Minimum Value in this Vector
	 */
	public int MinVal() {
		return MinVal(items);
	}

	/**
	 * Returns the position of the minimum value in this vector.
	 * @return the Position of the Minimum Value in this Vector
	 */
	public int MinPos() {
		return MinPos(items);
	}

	/**
	 * Returns the maximum value in this vector.
	 * @return the Maximum Value in this Vector
	 */
	public int MaxVal() {
		return MAX_VAL(items);
	}

	/**
	 * Returns the position of the maximum value in this vector.
	 * @return the Position of the Maximum Value in this Vector
	 */
	public int MaxPos() {
		return MaxPos(items);
	}

	/** Normalizes this Vector by bringing it into the canonical Form
	 * so that getAt(getInt()) != 0 
	 */
	public VectorShort normalizeAt() {
		while (items[--itemCount] == 0);
		++itemCount;
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorShort addAt(final VectorShort vector) {
		return addAt(vector.items, 0, vector.itemCount);
	}

	/** subtracts the given Portion of the values from this Vector */
	public VectorShort subAt(final VectorShort vector) {
		return subAt(vector.items, 0, vector.itemCount);
	}

	/** multiplies this Vector by the given Portion of the values */
	// TODO: LOGIC: calls subAt(...) instead of mulAt(...) - copy-pasted from subAt(VectorShort)
	// above without updating the delegated call, so this silently subtracts the given
	// vector's values instead of multiplying by them. Same defect as VectorChar.mulAt(VectorChar).
	public VectorShort mulAt(final VectorShort vector) {
		return subAt(vector.items, 0, vector.itemCount);
	}

	/** divides this Vector by the given Portion of the vector*/
	// TODO: LOGIC: calls subAt(...) instead of divAt(...) - copy-pasted from subAt(VectorShort)
	// above without updating the delegated call, so this silently subtracts the given
	// vector's values instead of dividing by them. Same defect as VectorChar.divAt(VectorChar).
	public VectorShort divAt(final VectorShort vector) {
		return subAt(vector.items, 0, vector.itemCount);
	}

	/** subtracts the given Portion of the values from this Vector */
	public VectorShort subAt(final short[] values, int start, int stop) {
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
	public VectorShort addAt(final int value) {
		VectorShort.addAt(items, value, 0, itemCount);
		return this;
	}

	/** adds the given Portion of the values to this Vector */
	public VectorShort subAt(final int value) {
		VectorShort.addAt(items, -value, 0, itemCount);
		return this;
	}

	/** multiplies this Vector by the given Portion of the values */
	// TODO: LOGIC: the `value` parameter is never used - this calls the (short[], int, int)
	// overload with `items` itself as the array argument, so it squares every element
	// (items[i] *= items[i]) instead of multiplying by the given scalar `value`. Same
	// defect as VectorChar.mulAt(int).
	public VectorShort mulAt(final int value) {
		return mulAt(items, 0, itemCount);
	}

	/** divides this Vector by the given Portion of the vector*/
	// TODO: LOGIC: the `value` parameter is never used - this calls the (short[], int, int)
	// overload with `items` itself as the divisor array, so it divides every element by
	// itself (yielding 1, or an arithmetic error on a zero element) instead of dividing by
	// the given scalar `value`. Same defect as VectorChar.divAt(int).
	public VectorShort divAt(final int value) {
		return divAt(items, 0, itemCount);
	}

	/** adds the given Portion of the values to this Vector */
	public VectorShort addAt(final short[] values, int start, int stop) {
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
	public VectorShort mulAt(final short[] values, final int start, int stop) {
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
	public VectorShort divAt(final short[] values, final int start, int stop) {
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
			  if (arg instanceof short[]) {
				  short[] arg_ = (short[]) arg;
				  copyAt(Permutation.map(Items, Items.length, arg_, arg_.length));
				  return this; }
			  return super.mulAt(arg); }
	*/
	/**Multiply the Vector by an Object.
	 * This extends the standard Set Multiplication
	 * by the Multiplication with a Permutation.	 */
	/*	  public SemiGroupM mul(Object arg) {
			  if (arg instanceof Permutation) return new VectorShort(Permutation.map(Items, Items.length, (Permutation) arg), capacityIncrement);
			  if (arg instanceof short[]	  ) return new VectorShort(Permutation.map(Items, Items.length, (short[]	  ) arg), capacityIncrement);
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
	protected VectorShort(short[] Values, int[] Factors) {
		this.dimFactors = Factors;
		this.items = Values;
	}

	/**
	 * @param Rows the Numbers of Rows    in the Matrix
	 * @param Cols the Numbers of Columns in the Matrix
	 */
	/*	public VectorShort(int Rows, int Cols) {
			this.dimSizes = new short[2];
			this.dimSizes[0] = Cols;
			this.dimSizes[1] = Rows;
			dimFactors = new short[2];
			dimFactors[1] = 1;
			dimFactors[2] = Cols;
			items = new double[Rows * Cols];
		}
	*/
	/**
	 * @param Cols the Numbers of Columns in the Tensor
	 */
/*	public VectorShort(short[] Cols) {
		this.dimSizes = Cols;
		int Factor, i = Cols.length;
		dimFactors = new short[i];
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
	 * @return a VectorShort with IndexFactors such
	 *  that the Elements are transposed.
	 * Only useful when simulating a rectangular Tensor on a 1 dim Array.
	 */
	public VectorShort getTranspose() {
		if (dimFactors.length != 2) {
			throw new InvalidParameterException("For Tensors please determine the Dimensions to transpose!");
		}
		int[] Factors = new int[2];
		Factors[0] = dimFactors[1]; //Just permuting the Factors is sufficient!
		Factors[1] = dimFactors[0]; //also for Tensors of higher Degrees!
		return new VectorShort(items, Factors);
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
		short[] vector = new short[5];
		vector[0] = 12345; 
		TRIM_AT(vector, (short) 10); //simply converts the Number into the given Radix Representation
		AStreamOut.ARRAY_TO_STREAM(System.out, vector, ", ");
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws Exception {
		System.out.println("Testing " + VectorShort.class.getName());
		testAckermann(); 
		testTrim(); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt(args);
	}

	/**Converts an Array of simple Type Contstants
	 * into an Array of the corresponding Object Type
	 * could be programmed slower but more generic using the Reflection API!	 */
	final static public Long[] const2Const(final long[] arg) {
		int len = arg.length;
		Long[] ret = new Long[len];
		while (--len >= 0)
			ret[len] = new Long(arg[len]);
		return ret; }

	/**Formats the String to the given Length (left or right aligned)	 */
	final static public String FORMAT(int x, int Length) {
		return VectorString.FORMAT(String.valueOf(x), Length); }

	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 */
	final static public short[] getInts(Object[] arg) {
		short[] Return = new short[arg.length];
		for(int i = arg.length; --i >= 0; ) {
			Return[i] = ByRefShort.getShort(arg[i]); }
		return Return; }

	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 */
	final static public short[] getInts(ICountAble[] arg) {
		short[] Return = new short[arg.length];
		for(int i = arg.length; --i >= 0; ) {
			Return[i] = arg[i].getShort(); }
		return Return; }

}

/** Iterator for the MatrixFloat Class (in reverse Order)
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T13:25:39Z
 * digest: 6ce97631064df1ad0c3887dabb0e50d219c28125fcc58810e01c0c90fc444d99
 * stale: false
 * tags: [code/functional_interfaces]
 * concepts: [Reverse-Order Short Stream Source]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
final class VectorShortStreamIn 
extends AVectorStreamIn_Int
{

	final VectorShort vector;

	/**
	 * Constructs a reverse-order iterator over the given vector's elements.
	 * @param vector_ the vector to iterate over
	 */
	public VectorShortStreamIn(final VectorShort vector_) {
		super(vector_.MaxVal()); //
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
