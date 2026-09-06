/*
 * File Name: VectorPolygon.java
 * Created on: 27.12.2003
 *
 */
package graphic.mvc.plane2D;

import graphic.IGraphText;

import java.util.Arrays;

import math.vector.AVector;
import streamIO.copy.ICopyAble;

/**
 * Title: VectorPolygon<p>
 * Description:
 * Dynamic Array for holding short[][][] Arrays usually used as Polygons 
 * generated from a 3D Model. 
 * The short Type is large enough even for Coordinates in large Graphics Contexts 
 * and still saves Space compared to an int which is completely oversized! 
 *
 * Since the short[][] can contain arbitrary many Points 
 * and the Points can contain arbitrary many Coordinates (x,y,z,u,v,color(r,g,b),bone,etc.)
 * this Schema is most extensible. 
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:50:28Z
 * digest: 84d3d0ab21780b5a618275e329d2dbdaf0760f7fcdccf21940113db967cda067
 * stale: false
 * tags: [code/vector_operations, code/z_ordering]
 * concepts: [Dynamic Array of 3D-Projected Polygons]
 * facets: {layer: domain, status: broken, complexity: high}
 * -->
 */
public class VectorPolygon 
extends AVector {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Backing Value Array for the float[]	 */
	protected MatrixShort[] items;
	
	/** Flag whether the Planes are to be ordered by their z Coordinate */
	public boolean zOrder; 
	
	/** Depth Index for the polygons, 
	 * good as long as the # of Polygons doesn't change (except for sorted Arrays)
	 * or they are marked as 'stale' by deleting this Index 
	 */
	private int[] zIndex;
	
	/** Sets any pre-calculated Values	 */
	public void setChanged() {
		zIndex = null;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the polygon at the given index as an Object.
	 * @return the item at the given Position as an Object */
	public Object getAt(final int i) {
		return getMatrixAt(i);
	}
	
	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	public synchronized MatrixShort getMatrixAt(final int index) {
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
		return setAt(index, (MatrixShort) value); 
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
	public MatrixShort setAt(final int index, final MatrixShort value) {
		MatrixShort ret = null;
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

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/**Constructs an empty VectorInt with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the VectorInt.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the VectorInt overflows.	 */
	public VectorPolygon(final int initialCapacity, final int capacityIncrement_) {
		super();
		items = new MatrixShort[initialCapacity];
		capacityIncrement = capacityIncrement_;
		//mEnum = new ArrayEnum(Items, ItemCount);
		//mEnum = new ArrayIterator(this); 
	} //

	/** Constructs an empty VectorPolygon with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the VectorPolygon.	 */
	public VectorPolygon(final int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/** Constructs an empty VectorPolygon.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public VectorPolygon() {
		this(DEFAULT_CAPACITY_INIT);
	}
	
	/** Constructs an VectorPolygon by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public VectorPolygon(final short[][][] arg, final boolean copy, final boolean oriented) {
		this(arg.length, DEFAULT_CAPACITY_INCR); 
		copyAt(arg, copy, oriented);
	}
	
	/** Constructs an VectorPolygon by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public VectorPolygon(Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}
	
	/** Constructs an VectorPolygon from the given Object.	  */
	public VectorPolygon(Object arg, int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}
	
	/** Constructs an VectorPolygon from the given Object.	  */
	public VectorPolygon(MatrixShort[] arg, int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}
	
	/** Constructs an VectorPolygon from the given Object
	  * and copies the Elements into this VectorPolygon.	  */
	public VectorPolygon(MatrixShort[] arg) {
		this(arg.length, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//	Methods for the dynamic 1dim Array Use
	////////////////////////////////////////////////////////////////////////////////

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorPolygon addItem(final MatrixShort item) {
		setAt(itemCount, item);
		return this;
	}

	/** Adds the given Item to the End of the List 
	 * optionally also enlarges the List. 
	 */
	final public VectorPolygon addItem(final short[][] item, final boolean copy, final boolean oriented) {
		setAt(itemCount, new MatrixShort(item, copy, oriented));
		return this;
	}

	/**Copies the components of this VectorInt into the specified array.
	 * The array must be big enough to hold all the objects in this  VectorInt.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	final public synchronized void copyInto(final int[] anArray) {
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
		final int[] Return = new int[itemCount];
		System.arraycopy(items, 0, Return, 0, itemCount);
		return Return;
	}

	/**Trims the capacity of this VectorInt to be the VectorInt's current
	 * size. An application can use this operation to minimize the
	 * storage of a VectorInt.	  */
	final public synchronized void trimToSize() {
		final int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			MatrixShort[] oldData = items;
			items = new MatrixShort[itemCount];
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
		int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) {
			return oldCapacity;
		}
		final MatrixShort[] oldData = items; 
		items = new MatrixShort[minCapacity];
		if (oldCapacity > 0) {
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
		return minCapacity;
	}

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.
	 *
	 * The Optimization here is that the Capacity can be ensured before
	 * and that additional Fields can be set.	 */
	public VectorPolygon copyAt(final MatrixShort[] arg_) {
		setCapacity(arg_.length);
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
		if (arg instanceof VectorPolygon) {
			VectorPolygon arg_ = (VectorPolygon) arg;
			capacityIncrement = arg_.capacityIncrement;
			setCapacity(arg_.itemCount);
			itemCount = arg_.itemCount;
			System.arraycopy(arg_.items, 0, items, 0, itemCount);
		} else
			super.copyAt(arg); //no need to use a recursive DeepCopy like with Tensor
		return this;
	}

	/** copies the Array into this Objects	*/
	public void copyAt(final short[][][] arg, final boolean copy, final boolean oriented) {
		setCapacity(arg.length); 
		for (int i = arg.length; --i >= 0; ) {
			items[i] = new MatrixShort(arg[i], copy, oriented);
			items[i].oriented = oriented;
		}
	}

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) {
		if (arg instanceof VectorPolygon) {
			VectorPolygon arg_ = (VectorPolygon) arg;
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
		return new VectorPolygon(items.length, capacityIncrement);
	}

	/** draws the Polygons in their zOrder 	*/
	public void drawInOrder(final IGraphText g) {
		if ((zIndex == null) || (zIndex.length != itemCount)) {
			//recreate the Index: a Permutation of the Item Indices in their zOrder,
			//without sorting 'items' itself (which would destroy outside References).
			final Integer[] order = new Integer[itemCount];
			for (int i = itemCount; --i >= 0; ) {
				order[i] = Integer.valueOf(i); }
			Arrays.sort(order, (a, b) -> items[a.intValue()].compareTo(items[b.intValue()]));
			zIndex = new int[itemCount];
			for (int i = itemCount; --i >= 0; ) {
				zIndex[i] = order[i].intValue(); }
		}
		for (int i = itemCount; --i >= 0; ) {
			items[zIndex[i]].draw(g);
		}
	}
	
	/** When true and {@link #zOrder} is set, polygons with a positive column sum (facing away)
	 * are skipped during {@link #draw(IGraphText)}. */
	public boolean skipNegativePoints = false;
	
	/** draws the Polygons (in their zOrder) 	*/
	public void draw(final IGraphText g) {
		if (zOrder) {
			Arrays.sort(items); //TODO: sorting destroys References to the 
			//drawInOrder(g); return; 
		}
		for (int i = itemCount; --i >= 0; ) {
			if (zOrder && skipNegativePoints && (items[i].getColSum() > 0)) {
				continue; } //clipping for negative z-Coordinates
			items[i].draw(g); 
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + VectorPolygon.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); 
	}
	
}
