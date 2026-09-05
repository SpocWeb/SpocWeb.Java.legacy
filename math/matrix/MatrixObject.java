/*
 * Created on 09.04.2005
 *
 */
package math.matrix;

import math.vector.AVector;
import streamIO.copy.ICopyAble;

/**
 * Dynamic-size matrix of {@code Object[]} rows, backed by a plain two-dimensional array rather
 * than {@code VectorObject} items.
 *
 * @author heuerm
 *
 * Usage:
 * @see streamIO.integer.jdbc.ResultSetArray uses this Structure
 * to cache an entire ResultSet in RAM.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:43:46Z
 * digest: 856c3c019d0f07b9ad736bc977e3b5165caa1417384041d57703a862330a9c1b
 * stale: false
 * tags: [code/matrix_base_class, code/matrix_operations]
 * concepts: [Generic Object Matrix]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class MatrixObject 
extends AVector {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Backing Value Array for the float[]	 */
	protected Object[][] items;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the row at the given position, boxed as a plain {@code Object}.
	 * @return the item at the given Position as an Object */
	public Object getAt(final int i) { return getVectorAt(i); }

	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized Object[] getVectorAt(final int index) {
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
	public Object[] setAt(final int index, final Object[] value) {
		Object[] ret = null; 
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
	public Object setAt(final int index, final Object value) {
		return setAt(index, (Object[]) value); 
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
	public void insertAt(final int index, final Object[] value) {
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
	public Object[] removeAt(final int index) {
		if (index > --itemCount)  //
			return null; 
		final Object[] ret = items[index]; 
		System.arraycopy(items, index+1, items, index, itemCount-index); 
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX) 
	/// for multidimensional rectangular Arrays 
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the row array stored at the given 2-D coordinate.
	 * @return the Value at the given Position	 */
	public Object[] getAt(int Row, int Col) {
		return items[Row * dimFactors[0] + Col * dimFactors[1]];
	}

	/** sets the given Value 	 */
	public void setAt(int Row, int Col, Object[] Value) {
		items[Row * dimFactors[0] + Col * dimFactors[1]] = Value;
	}

	/** Returns the row array stored at the given 3-D coordinate.
	 * @return the Value at the given Position	 */
	public Object[] getAt(int Sheet, int Row, int Col) {
		return items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]];
	}

	/** sets the given Value 	 */
	public void setAt(int Sheet, int Row, int Col, Object[] Value) {
		items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]] = Value;
	}

	/** Returns the row array stored at the given multi-dimensional coordinate.
	 * @return the Value at the given Position	 */
	public Object[] getAt(int[] Col) {
		return items[multiIndex(Col)];
	}

	/** sets the given Value 	 */
	public void setAt(int[] Col, Object[] Value) {
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
	public MatrixObject(final int _initialCapacity, final int _capacityIncrement) {
		super();
		this.items = new Object[_initialCapacity][];
		this.capacityIncrement = _capacityIncrement;
		//mEnum = new ArrayEnum(Items, ItemCount);
		//mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty MatrixObject with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the MatrixObject.	 */
	public MatrixObject(final int _initialCapacity) {
		this(_initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty MatrixObject.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public MatrixObject() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/** Constructs an MatrixObject by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public MatrixObject(final Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an MatrixObject from the given Object.	  */
	public MatrixObject(final Object arg, final int _capacityIncrement) {
		this(DEFAULT_CAPACITY_INIT, _capacityIncrement);
		copyAt(arg);
	}

	/** Constructs an MatrixObject from the given Object.	  */
	public MatrixObject(final Object[][] arg, final int _capacityIncrement) {
		this.capacityIncrement = _capacityIncrement; 
		this.itemCount = arg.length; 
		this.items = arg; 
	}

	/** Constructs an MatrixObject from the given Object
	  * and copies the Elements into this MatrixObject.	  */
	public MatrixObject(final Object[][] arg) {
		this(arg, DEFAULT_CAPACITY_INCR);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public MatrixObject addItem(final Object[] item) {
		setAt(itemCount, item);
		return this;
	}

	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	// TODO: LOGIC: items is Object[][] but anArray is int[]; System.arraycopy compiles (both
	// are Object) but throws ArrayStoreException at runtime for any non-empty matrix because
	// the component types are incompatible.
	final public synchronized void copyInto(int[] anArray) {
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
	// TODO: LOGIC: same Object[][]-into-int[] arraycopy defect as copyInto(int[]) above;
	// throws ArrayStoreException at runtime for any non-empty matrix.
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
			final Object[][] oldData = items;
			items = new Object[itemCount][];
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
		final Object[][] oldData = items; items = new Object[newCapacity][];
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
	public MatrixObject copyAt(final String[] arg_) {
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
		if (arg instanceof MatrixObject) {
			MatrixObject arg_ = (MatrixObject) arg;
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
		if (arg instanceof MatrixObject) {
			MatrixObject arg_ = (MatrixObject) arg;
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
		return new MatrixObject(items.length, capacityIncrement);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

}
