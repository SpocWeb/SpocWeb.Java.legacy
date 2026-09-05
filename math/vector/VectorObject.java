/*
 * Created on 09.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package math.vector;

import java.util.ArrayList;

import streamIO.copy.ICopyAble;

/**
 * Growable, index-addressable array of arbitrary {@link Object} elements, doubling as a
 * flat backing store for 2D/3D rectangular multi-index access.
 *
 * <p>Dynamic Size Matrix of Objects:
 * variable Number of Objects
 * which is equivalent to ArrayList but resizing is more flexible.
 *
 * @see ArrayList which provides the same Functionality.
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:45:43Z
 * digest: fe1736b2d6f2ce6fad4d629bb9dbf9ee1bbbd3396b92e5e49a91a4c798bda0e5
 * stale: false
 * -->
 */
public class VectorObject
extends AVector {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an array of {@code size} newly constructed, empty {@link VectorObject} instances.
	 *
	 * @param size Size of the Array
	 * @param capacity initial Capacity of each Vector
	 * @param increment Capacity Increment of each Vector
	 * @return a new, filled Array of VectorObjects
	 */
	final static public VectorObject[] FILLED_ARRAY(final int size, final int capacity, final int increment) {
		final VectorObject[] ret = new VectorObject[size];
		for (int i = size; --i >= 0;) 
			ret[i] = new VectorObject(capacity, increment);
		return ret; 
	}
	
	/**
	 * Liefert die Position des Feldes in der Liste durch eine lineare Suche. 
	 *
	 * @param  item Liste der Objekte, aus der die Position von 'item' ermittelt wird
	 * @param item Name des zu suchenden Feldes 
	 * @return  die Position des Feldes in der Liste. 
	 * 		-1 falls nicht vorhanden
	 */
	final static public int FIND_LAST(final Object[] items, final int length, Object item) {
		for (int i = length; --i >= 0;) {
			final Object currItem = items[i]; 
			if ((currItem == item) || 
				(currItem.equals(item))) {
				return i; }
		}
		return -1; //not found
	}
	
	/**
	 * Liefert die Position des Feldes in der Liste durch eine lineare Suche. 
	 *
	 * @param  item Liste der Objekte, aus der die Position von 'item' ermittelt wird
	 * @param item Name des zu suchenden Feldes 
	 * @return  die Position des Feldes in der Liste. 
	 * 		-1 falls nicht vorhanden
	 */
	final static public int FIND_LAST(final Object[] items, final Object item) {
		return FIND_LAST(items, items.length, item); //
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Backing Value Array for the float[]	 */
	protected Object[] items;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the item at the given position, delegating to {@link #getVectorAt(int)}.
	 * @return the item at the given Position as an Object */
	public Object getAt(final int i) { return getVectorAt(i); }
	
	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized Object getVectorAt(final int index) {
		if (indexInRange(index)) 
			return items[index];
		return null; //"";
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
		Object ret = null; 
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
	public void insertAt(final int index, final Object value) {
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
	public Object removeAt(final int index) {
		// TODO: LOGIC: `--itemCount` runs as a side effect of evaluating the right operand of
		// `||` whenever index >= 0; when index > the pre-decrement itemCount (out of range), the
		// method still returns null here but itemCount has already been permanently decremented,
		// corrupting the vector's size even though no element was actually removed.
		if ((index < 0) ||
			(index > --itemCount))
			return null;
		final Object ret = items[index]; 
		System.arraycopy(items, index+1, items, index, itemCount-index); 
		return ret;
	}

	/**removes the specified Object. 
	 * All following components in this Container are shifted to the left.
	 * <p>
	 *
	 * @param	 the object (or Equivalent) to remove 
	 * @return	 the Position of the Object. -1 if not found. 
	 */
	public int find(final Object item) {
		return FIND_LAST(items, itemCount, item);
	}

	/**removes the specified Object. 
	 * All following components in this Container are shifted to the left.
	 * <p>
	 *
	 * @param	 the object (or Equivalent) to remove 
	 * @return	 the value removed or null if it wasn't found. 
	 */
	public Object remove(final Object item) {
		return removeAt(find(item)); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX) 
	/// for multidimensional rectangular Arrays 
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the value at the given row/column position of this rectangular array.
	 * @return the Value at the given Position	 */
	public Object getAt(final int Row, final int Col) {
		return items[Row * dimFactors[0] + Col * dimFactors[1]];
	}

	/** sets the given Value 	 */
	public void setAt(final int Row, final int Col, final Object Value) {
		items[Row * dimFactors[0] + Col * dimFactors[1]] = Value;
	}

	/** Returns the value at the given sheet/row/column position of this 3-dimensional array.
	 * @return the Value at the given Position	 */
	public Object getAt(final int Sheet, final int Row, final int Col) {
		return items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]];
	}

	/** sets the given Value 	 */
	public void setAt(final int Sheet, final int Row, final int Col, final Object Value) {
		items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]] = Value;
	}

	/** Returns the value at the position addressed by the given multi-index.
	 * @return the Value at the given Position	 */
	public Object getAt(final int[] Col) { return items[multiIndex(Col)]; }

	/** sets the given Value 	 */
	public void setAt(final int[] Col, final Object Value) {
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
	final static public Object GET_AT(final Object[] a, final int index) {
		return GET_AT(a, index, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public Object GET_AT(final Object[] a, final int index, final int stop) {
		return GET_AT(a, index, null, stop); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public Object GET_AT(final Object[] a, final int index, final Object defaultValue) {
		return GET_AT(a, index, defaultValue, a.length); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public Object GET_AT(final Object[] a, final int index, final Object defaultValue, final int stop) {
		return GET_AT(a, index, defaultValue, stop, 0); }
	
	/**this is an Error-tolerant linear Mapping (Projection along the Dimension)  
	 * @param a the Array to select the Value from 
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop  lower Bound (inclusive) for the Index 
	 * @param start upper Bound (exclusive) for the Index 
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public Object GET_AT(final Object[] a, final int index, final Object defaultValue, final int stop, final int start) {
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
	final static public Object[] GET_AT(final Object[] a, final VectorInt index) {
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
	final static public Object[] GET_AT(final Object[] a, final VectorInt index, Object[] ret) {
		return GET_AT(a, index.items, ret, index.itemCount); 
	}
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public Object[] GET_AT(final Object[] a, final int[] index) {
		return GET_AT(a, index, null); }  
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public Object[] GET_AT(final Object[] a, final int[] index, final Object[] ret) {
		return GET_AT(a, index, ret, index.length); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public Object[] GET_AT(final Object[] a, final int[] index, final Object[] ret, int stop) {
		return GET_AT(a, index, ret, stop, 0); }
	
	/**this is a linear Mapping (Projection along the Dimension)  
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) 
	 * for the same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.  
	 * @return the selected Values of the given Vector,
	 * even with Dimension Mismatch.
	 */
	final static public Object[] GET_AT(final Object[] a, final int[] index, Object[] ret, final int stop, final int start) {
		if((ret == null) || (ret.length < stop))
			ret = new Object[stop];
		//else if (ret.length > stop) //rather leave the Values alone?!?
		//	Arrays.fill(ret, stop, ret.length, 0); 
		for(int i = stop; --i >= start; )
			ret[i] = (index[i] < a.length) ? a[index[i]] : null; 
		return ret;
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
	public VectorObject(final int _initialCapacity, final int _capacityIncrement) {
		super();
		this.items = new Object[_initialCapacity];
		this.capacityIncrement = _capacityIncrement;
		//mEnum = new ArrayEnum(Items, ItemCount);
		//mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty MatrixObject with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the MatrixObject.	 */
	public VectorObject(final int _initialCapacity) {
		this(_initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty MatrixObject.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public VectorObject() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/** Constructs an MatrixObject by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public VectorObject(final Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an MatrixObject from the given Object.	  */
	public VectorObject(final Object arg, final int _capacityIncrement) {
		this(DEFAULT_CAPACITY_INIT, _capacityIncrement);
		copyAt(arg);
	}

	/** Constructs an MatrixObject from the given Object.	  */
	public VectorObject(final Object[] arg, final int _capacityIncrement) {
		this.capacityIncrement = _capacityIncrement; 
		this.itemCount = arg.length; 
		this.items = arg; 
	}

	/** Constructs an MatrixObject from the given Object
	  * and copies the Elements into this MatrixObject.	  */
	public VectorObject(final Object[] arg) {
		this(arg, DEFAULT_CAPACITY_INCR);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorObject addItem(final Object item) {
		setAt(itemCount, item);
		return this;
	}

	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	final public synchronized void copyInto(int[] anArray) {
		// TODO: LOGIC: `items` is an Object[] here (unlike VectorInt, whose items are int[]);
		// copying it into an int[] destination via System.arraycopy throws ArrayStoreException
		// at runtime on every call once itemCount > 0. This method was apparently copy-pasted
		// from VectorInt without adjusting for VectorObject's element type.
		System.arraycopy(items, 0, anArray, 0, itemCount);
		/*Object elementDataLocal[] = this.Items;
		for (int i = ItemCount; i-- > 0;)
			anArray[i] = elementDataLocal[i];
		*/
	}

	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.	 */
	final public synchronized int[] toArray() {
		int[] Return = new int[itemCount];
		// TODO: LOGIC: same ArrayStoreException hazard as copyInto(int[]) above: `items` is an
		// Object[], not an int[], so this arraycopy throws at runtime once itemCount > 0.
		System.arraycopy(items, 0, Return, 0, itemCount);
		return Return;
	}

	/**Trims the capacity of this VectorInt to be the VectorInt's current
	 * size. An application can use this operation to minimize the
	 * storage of a VectorInt.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			final Object[] oldData = items;
			items = new Object[itemCount];
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
		final Object[] oldData = items; items = new Object[newCapacity];
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
	public VectorObject copyAt(final String[] arg_) {
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
		if (arg instanceof VectorObject) 
			copyAt((VectorObject) arg);
		else
			super.copyAt(arg); //no need to use a recursive DeepCopy like with Tensor
		return this;
	}
	
	/** more efficient Implementation	 */
	public VectorObject copyVector() {
		final VectorObject ret = new VectorObject(items.length, capacityIncrement);
		System.arraycopy(this.items, 0, ret.items, 0, this.itemCount); //ret.copyAt(this); 
		return ret;
	}
	
	/** Deep-copies {@code _arg}'s elements and capacity settings into this instance. */
	public void copyAt(final VectorObject _arg) {
		capacityIncrement = _arg.capacityIncrement;
		setCapacity(_arg.itemCount);
		itemCount = _arg.itemCount;
		System.arraycopy(_arg.items, 0, items, 0, itemCount);
	}
	
	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(final Object arg) {
		if (arg instanceof VectorObject) {
			VectorObject arg_ = (VectorObject) arg;
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
		return new VectorObject(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

}
