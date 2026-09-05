/*
 * File Name: AVector.java
 * Created on: 01.06.2003
 *
 */
package math.vector;

import java.io.IOException;

import streamIO.IFormatOut;
import streamIO.IReSetAble;
import streamIO.copy.ACopyAble;
import streamIO.integer.random.AStreamIn_BoundInt;
import streamIO.object.IStreamIn;
import streamIO.real.AStreamIn_Float;
import function.index.IDirectAccess;

/**
 * Base class for the fixed-primitive-type vector family, providing shared capacity growth,
 * bounds checking and item-count bookkeeping over a growable array.
 *
 * <p>Title: AVector<p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses:
 * @see math.AMatrix
 * @see graphs.SparseMatrix
 * @see math.VectorChar
 * @see math.VectorDouble
 * @see math.VectorFloat
 * @see math.VectorInt
 * @see graphic.mvc.plane2D.MatrixShort
 * @see graphic.VectorPoint2D
 * @see graphic.mvc.plane2D.VectorPolygon
 * @see math.VectorShort
 * @see math.VectorString
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:44:29Z
 * digest: a1a96f492d44e5e982cc3b2a125fe57cbe710b95cf0326dc90ffdd8bcff693df
 * stale: false
 * -->
 */
public abstract class AVector
extends ACopyAble
implements IDirectAccess 
{

	////////////////////////////////////////////////////////////////////////////////
	//  static Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Frees up Memory as early as possible by...
	 * ...setting Object References to null
	 * ...reducing Array Size when possible, not only enlarging it 
	 */
	public static boolean FREE_MEMORY_EARLY = false;

	/**The default initial Capacity on instantiating an Array	 */
	public static int DEFAULT_CAPACITY_INIT = 10;

	/**The default Capacity Increment on instantiating an Array	 */
	public static int DEFAULT_CAPACITY_INCR = -1;

	/** 
	 * Default Value for Checking the Upper Bounds is false
	 * because the Array is assumed to grow dynamically
	 */
	public static boolean DEFAULT_CHECK_UPPER_BOUND = false;

	/** 
	 * Default Value for Checking the Lower Bounds is true
	 * because the Arrays start at 0 and don't have Offsets
	 */
	public static boolean DEFAULT_CHECK_LOWER_BOUND = true;

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Universal Capacity Increment Function to be used in all dynamic Buffers 
	 * 
	 * @param oldCap old Capacity
	 * @param increment encoding the Increment Function
	 * @param minCap minimum Capacity required
	 * @return the new Capacity to use, considering both the Increment and the minimum Capacity
	 */
	final static public int ENLARGED_CAPACITY(final int oldCap
			, final int increment, final int minCap) {
		int ret = ENLARGED_CAPACITY(oldCap, increment); 
		if (ret < minCap) 
			ret = minCap; 
		return ret; } 

	/** Universal Capacity Increment Function to be used in all dynamic Buffers 
	 * only increments, no Decrement! 
	 * @param oldCap old Capacity
	 * @param increment encoding the Increment Function
	 * @return the new Capacity to use
	 */
	final static public int ENLARGED_CAPACITY(final int oldCap, final int increment) {
		if (increment >= 0) {
			return oldCap +   increment ; } // linear      Increment or no Increment
			return oldCap *(1-increment); } // exponential Increment or no Increment

	/** Universal Capacity Increment Function to be used in all dynamic Buffers 
	 * the Increment is defaulted. 
	 * 
	 * @param oldCap old Capacity
	 * @return the new Capacity to use
	 */
	final static public int ENLARGED_CAPACITY(final int oldCap) {
		return ENLARGED_CAPACITY(oldCap, DEFAULT_CAPACITY_INCR); }
	
	////////////////////////////////////////////////////////////////////////////////
	//	Member Variables for multidimensional rectangular Array Simulation, 
	//  not really used yet...
	////////////////////////////////////////////////////////////////////////////////

	/** Index Array for the Tensor	 */
	protected int[] dimFactors;

	/** Maximum Index Array for the Tensor	 */
	protected int[] dimSizes;

	////////////////////////////////////////////////////////////////////////////////
	//	Methods for multidimensional rectangular Array Simulation
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * This Method can be used to access Elements directly
	 * using the Accessors with only a single Index.
	 * @param Col the Multi-Index for the given Position.
	 * @return the internally used Index for the given Position
	 */
	final public int multiIndex (int Row, int Col) {
		return Row*dimFactors[0] + Col*dimFactors[1]; }

	/**
	 * This Method can be used to access Elements directly
	 * using the Accessors with only a single Index.
	 * @param Col the Multi-Index for the given Position.
	 * @return the internally used Index for the given Position
	 */
	final public int InternalIndex (int Sheet, int Row, int Col) {
		return Sheet*dimFactors[0] + Row*dimFactors[1] + Col*dimFactors[2]; }

	/**
	 * This Method can be used to access Elements directly
	 * using the Accessors with only a single Index.
	 * @param Col the Multi-Index for the given Position.
	 * @return the internally used Index for the given Position
	 */
	final public int multiIndex(int[] Col) {
		int i = Col.length;
		int ret = 0; //
		while (--i >= 0) {
			int coli = Col[i];
			if ((coli >= dimSizes[i]) || (coli < 0)) {
				throw new IndexOutOfBoundsException("Range: 0.." + dimSizes[i] + " Actual: " + coli);
			}
			ret += dimFactors[i] * coli; //Horner-Schema 
		}
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Member Variables, all pre-initialized
	// Constructors are not necessary: 
	// itemCount is left to 0
	// capacityIncrement is defaulted
	// Capacity is defined by the internal Array implicitly
	////////////////////////////////////////////////////////////////////////////////

	/** The number of valid Objects in the Array, ranging from [0..itemCount-1]
	 * This Value is to be used for all Loops over the Elements. 
	 */
	protected int itemCount; // = 0;

	/**The amount by which the capacity of the Array is automatically
	 * incremented when its size becomes greater than its capacity.
	 * If the Array needs to grow and the capacityIncrement is 
	 * <code>>0</code>, the capacity is increased by the given Number
	 * <code>=0</code>, the capacity stays constant
	 * <code>-1</code>, the capacity of the Array is doubled each time it needs to grow.
	 */
	protected int capacityIncrement = DEFAULT_CAPACITY_INCR;

	/** Switches on Exceptions when exceeding the upper Bounds of the List, 
	 * alternatively 0 is returned. 
	 */
	public boolean strictUpperBoundsChecking = DEFAULT_CHECK_UPPER_BOUND;

	/** Switches on Exceptions when exceeding the lower Bounds of the List 
	 * alternatively 0 is returned. 
	 */
	public boolean strictLowerBoundsChecking = DEFAULT_CHECK_LOWER_BOUND;

	////////////////////////////////////////////////////////////////////////////////
	//	Method Default Implementations
	////////////////////////////////////////////////////////////////////////////////

	/** Clears the List without removing potential References 	 */
	public void clear() { setInt(0); }
	
	/**Sets the number of components in this Vector.
	 * should only reduce, not increase the Number to avoid uninitialized Elements! 
	 * @return  the previous number of components in this VectorInt.	 */
	final public int setInt(final int newSize) {
		if (itemCount < newSize) 
			throw new ArrayIndexOutOfBoundsException("increasing Vector to uninitialized Elements!"); 
		final int ret = itemCount; itemCount = newSize; return ret; }

	/**Returns the number of components in this Vector.
	 * @see 
	 * @return  the number of components in this Vector.	 */
	final public int getInt() { return itemCount; }

	/**
	 * Tests whether the Index is in Range. 
	 * @param index
	 * @return true when the Index is in the Range of this Vector
	 * @throws ArrayIndexOutOfBoundsException if the Index is out of Range 
	 * and the Bounds are strictly checked. 
	 */
	protected boolean indexInRange(final int index) {
		if (index >= itemCount) { //have to catch that explicitly, because the actual Array is larger!
			if (strictUpperBoundsChecking) 
				throw new ArrayIndexOutOfBoundsException(index + "=index > itemCount=" + itemCount);
			return false; //throw new ArrayIndexOutOfBoundsException(index + " >= " + ItemCount);
		}
		if (index < 0) { //'try' may be more expensive than checking the Index!
			if (strictLowerBoundsChecking) 
				throw new ArrayIndexOutOfBoundsException(index + "=index < 0");
			return false;
		}
		/*		try { //'try' may be more expensive than checking the Index!
					return items[index];
				} catch (ArrayIndexOutOfBoundsException e) {
					if (strictLowerBoundsChecking) {
						throw new ArrayIndexOutOfBoundsException(index + "=index < 0");
					}
					return false; 
				}
		*/
		return true;
	}

	/**Writes the Contents of this Object into the streamIO.
	 * Default Implementation that can be overwritten by more effective ones.
	 * TODO: this can be optimized by not using getObjectAt()
	 */
	public void toStream(final IFormatOut ST) throws IOException { 
		for (int i = -1; ++i < itemCount;) {
			ST.addItem(getAt(i)); } //leads to infinite Recursion!
	}
	
	/**Increases the capacity of this SparseMatrix, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	abstract public int setCapacity(final int minCapacity); 
	
	/**Sets the size of this VectorInt. 
	 * If the new size is greater than the current size, 
	 * new <code>null</code> items are added to the end of the VectorInt. 
	 * If the new size is less than the current size, all
	 * components at index <code>newSize</code> and greater are discarded.
	 *
	 * @param   newSize   the new size of this VectorInt.	 */
	public synchronized void setSize(final int newSize) {
		if (newSize > itemCount) {
			setCapacity(newSize);
/*		} else { //Initialize the Elements out of Bounds to 'null' or 0
			for (int i = itemCount; --i >= newSize; ) { 
				items[i] = 0; //clearing is not necessary...
			} //...except to free Memory
*/		}
		itemCount = newSize;
	}
	
}

/** Iterates a floating-point vector's elements in reverse order, as a bounded stream source.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:44:29Z
 * digest: b14314a9f9a48335e179bff02a82b673eef948a798c2df8f94ff853ab3e0bccf
 * stale: false
 * -->
 */
abstract class AVectorStreamIn_Float
extends AStreamIn_Float {
	
    ///////////////////////////////////////////////////////////////////////////
    /// abstract Methods
    ///////////////////////////////////////////////////////////////////////////
    
	//final AVector vector;

	/** @see streamIO.real.IStreamIn_Float#nextDouble()	 */
	abstract protected double nextDoubleInternal(); // { return vector.getAt(--pos); }
	
	/** Returns the smallest value reachable while iterating this vector.
	 * @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()	 */
	abstract public double getMinDouble(); // { vector.MinVal(); }

    /** Returns the number of elements available to this reverse-order iterator.
     * @see streamIO.real.AStreamIn_Float#getMaxMarkSize()     */
    abstract public long getMaxMarkSize(); // { return vector.getInt(); }

    ///////////////////////////////////////////////////////////////////////////
    /// Implementations
    ///////////////////////////////////////////////////////////////////////////

	protected int pos; //= 0;

	/** Returns the current stack position, i.e. the number of elements not yet consumed.
	 * @see Stream.IAvailAble#availAble()	 */
	public long availAble() { return pos; }

	/** Reports that this iterator consumes elements in stack (LIFO) order.
	 * @see Stream.Float.IStreamIn_Float#getOrder()	 */
	public byte getOrder() { return IStreamIn.ORDER_STACK; }

	/** Resets the iterator position to the end of the vector, ready to iterate backwards.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble  reSet() { pos = (int) getMaxMarkSize(); return this; }

    /** Returns how many elements have already been consumed by this iterator.
     * @see streamIO.real.AStreamIn_Float#getPosition()     */
    public long getPosition() { return getMaxMarkSize()-pos; }

	/** Creates the iterator and resets it to the last element of the underlying vector. */
	public AVectorStreamIn_Float() {
		//this.vector = vector_;
		this.reSet();
	}
	
}

/** Iterates an integer vector's elements in reverse order, as a bounded integer stream source.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:44:29Z
 * digest: 40963f774241d628bd3bc7ff803575f984ad0a360a863843c6d855861dd7eae2
 * stale: false
 * -->
 */
abstract class AVectorStreamIn_Int
extends AStreamIn_BoundInt {
	
    ///////////////////////////////////////////////////////////////////////////
    /// abstract Methods
    ///////////////////////////////////////////////////////////////////////////
    
	//final AVector vector;
	
	/** @see Stream.Float.IStreamIn_Int#nextInt()	 */
	abstract protected long nextLongInternal(); // { return vector.getAt(--pos); }
	
	/** Returns the smallest value reachable while iterating this vector.
	 * @see Stream.Float.IStreamIn_Bound_Int#getMinValue()	 */
	abstract public long getMinValue(); // { return vector.MinVal(); }

    /** Returns the number of elements available to this reverse-order iterator.
     * @see streamIO.real.AStreamIn_Float#getMaxMarkSize()     */
    abstract public long getMaxMarkSize(); // { return vector.getInt(); }

    ///////////////////////////////////////////////////////////////////////////
    /// Implementations
    ///////////////////////////////////////////////////////////////////////////

	protected int pos; //= 0;

	/** Returns the current stack position, i.e. the number of elements not yet consumed.
	 * @see Stream.IAvailAble#availAble()	 */
	public long availAble() { return pos; }

	/** Reports that this iterator consumes elements in stack (LIFO) order.
	 * @see Stream.Float.IStreamIn_Float#getOrder()	 */
	public byte getOrder() { return IStreamIn.ORDER_STACK; }

	/** Resets the iterator position to the end of the vector, ready to iterate backwards.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble  reSet() { pos = (int) getMaxMarkSize(); return this; }

    /** Returns how many elements have already been consumed by this iterator.
     * @see streamIO.real.AStreamIn_Float#getPosition()     */
    public long getPosition() { return getMaxMarkSize()-pos; }

	/** Creates the iterator, bounding it at {@code _maxVal} and resetting to the last element. */
	public AVectorStreamIn_Int(final int _maxVal) {
		super(_maxVal); //
		//this.vector = vector_;
		reSet(); 
	}
	
}
