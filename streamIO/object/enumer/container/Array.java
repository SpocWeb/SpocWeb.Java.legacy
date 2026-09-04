package streamIO.object.enumer.container;

import java.util.NoSuchElementException;

import math.vector.AVector;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IReSetAble;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.monoid.integer.Permutation;
import streamIO.object.IPipe;
import streamIO.object.enumer.AIndexEnumerator;
import streamIO.object.enumer.ArrayEnum;
import streamIO.object.enumer.Enumerator;
import streamIO.object.enumer.IndexEnumerator;

/** The <code>Array</code> class implements a dynamically growable Array of
  * objects (equivalent to java.util.Vector resp. java.util.ArrayList)
  * that implements the RAContainer Interface.
  * Like an array, it contains components that can be accessed using an integer index.
  * However, the size of <code>Array</code> can grow or shrink as needed
  * to accommodate adding and removing items after the <code>Array</code> has been created.
  *
  * There are also static Methods to read and write Java Arrays
  * from StreamIn and StreamOut.
  * It's StreamIn and StreamOut Interfaces usually work like a Stack (LIFO Store).
  *
  * Performance Comparison:
  * If Items are added or removed in the Middle of the structure,
  * Holes have to be opened or closed, which are quite expensive Operations,
  * but ensures fast resizing, keeping the Sequence and random Access.
  * Removing Items in the Middle can be made faster by breaking the Sequence,
  * moving only the Item at the End to the current Position and truncating.
  * Also joining or splitting Arrays are expensive Operations.
  * Linked Lists are better for adding Objects in the Middle, joining or splitting
  * Containers, but bad for searching!
  * Arrays are fastest in appending, because no Listtems have to be created!
  * It also prepares sorting the Elements.
  *
  *
  *
  * <p>
  * Each array tries to optimize storage management by maintaining a
  * <code>capacity</code> and a <code>capacityIncrement</code>.
  * The <code>capacity</code> is always at least as large as the array size;
  * it is usually larger because as components are added to the array,
  * the array's storage increases in chunks the size of
  * <code>capacityIncrement</code>, if this Parameter > 0.
  * When it is 0, no Allocation takes place, instead an Exception is raised.
  * When it is negative, the exceeding Space is shifted
  * by the negative capacity Increment, anytime it's Capacity is exceeded.
  * Usually you choose -1, which doubles the Space on exceeding.
  * An application can increase the capacity of an Array using ensureCapacity()
  * before inserting a large number of components;
  * this reduces the amount of incremental reallocation.
  *
  * The Functionality is equivalent to
  * @see java.util.Vector and
  * @see java.util.ArrayList
  *
  * Design Decisions:
  * All first- / last- IndexOf Routines return the first / last occurence of Item
  * when it is in the Collection, otherwise it returns the Position after / before
  * the Search Range, so that another Search can directly be appended.
  * So the returned Index has to be tested, before using it!
  */
public class Array
extends ARAContainer
implements RAContainer {

	////////////////////////////////////////////////////////////////////////////////
	//	static Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**The default initial Capacity on instantiating an Array	 */
	public static int DEFAULT_CAPACITY = 10;
	
	/**The default Capacity Increment on instantiating an Array	 */
	public static int DEFAULT_CAPACITY_INCREMENT = -1;
	
	////////////////////////////////////////////////////////////////////////////////
	//	static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The array buffer into which the components of the Array are
	  * stored. The capacity of the Array is the length of this array buffer.	 */
	protected Object[] items;
	
	/**The amount by which the capacity of the Array is automatically
	 * incremented when its size becomes greater than its capacity.
	 * If the capacityIncrement is <code>0</code>, the capacity of the Array is
	 * doubled each time it needs to grow.	  */
	protected int capacityIncrement;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////////
	
	/**Constructs an empty Array with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the Array.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the Array overflows.	 */
	public Array(int initialCapacity, int capacityIncrement_) {
		super();
		items = new Object[initialCapacity];
		capacityIncrement = capacityIncrement_;
//		mEnum = new ArrayEnum(Items, ItemCount);
		enm = new ArrayIterator(this); } //TODO: This Enumerator has to be updated with each Change!

	/** Constructs an empty Array with the specified initial capacity.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.
	  *
	  * @param   initialCapacity   the initial capacity of the Array.	 */
	public Array(int initialCapacity) { this(initialCapacity, DEFAULT_CAPACITY_INCREMENT); }

	/** Constructs an empty Array.
	  * Defaults the initial Capacity to 'defaultCapacityInit'.	 */
	public Array() { this(DEFAULT_CAPACITY); }

	/** Constructs an Array by copying from the given Object any Type.
	  * Defaults the Capacity Increment to 'defaultCapacityIncr'.	 */
	public Array(Object arg) { this (DEFAULT_CAPACITY, DEFAULT_CAPACITY_INCREMENT); copyAt (arg); }

	/** Constructs an Array from the given Object.	  */
	public Array(Object arg, int capacityIncrement_) {
		this(DEFAULT_CAPACITY, capacityIncrement_);	copyAt(arg);}

	/** Constructs an Array from the given Object.	  */
	public Array(Object[] arg, int capacityIncrement_) {
		this(arg.length, capacityIncrement_); copyAt(arg);}

	/** Constructs an Array from the given Object
	  * and copies the Elements into this Array.	  */
	public Array(Object[] arg) {
		this(arg.length, DEFAULT_CAPACITY_INCREMENT); copyAt(arg);}

	////////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**Copies the components of this Array into the specified array.
	 * The array must be big enough to hold all the objects in this  Array.
	 *
	 * @param   anArray   the array into which the components get copied.
	 * Declared final, because System.arraycopy is the fastest way.	 */
	final public synchronized void copyInto(Object[] anArray) {
		System.arraycopy(items, 0, anArray, 0, itemCount);
/*		int i = ItemCount;
		Object elementDataLocal[] = this.Items;
		while (i-- > 0)
			anArray[i] = elementDataLocal[i];
*/	}

	/**Copies the components of this Array into the specified array.
	 * The array must be big enough to hold all the objects in this  Array.
	 *
	 * @param   anArray   the array into which the components get copied.	 */
	final public synchronized Object[] toArray() {
		Object[] Return = new Object[itemCount];
		System.arraycopy(items, 0, Return, 0, itemCount);
		return Return; }

	/**Trims the capacity of this Array to be the Array's current
	 * size. An application can use this operation to minimize the
	 * storage of a Array.	  */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			Object oldData[] = items;
			items = new Object[itemCount];
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
	}

	/**Returns the current capacity of this Array.
	 *
	 * @return  the current capacity of this Array.	 */
	final public int getCapacity() { return items.length; }

	/**Increases the capacity of this Array, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final public synchronized int setCapacity(int minCapacity) {
		int oldCapacity  = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) {
			return oldCapacity; }
		Object[] oldData = items; items = new Object[minCapacity];
		if (oldCapacity > 0) {
			System.arraycopy(oldData, 0, items, 0, itemCount); }
		return minCapacity; }

	/**Sets the size of this Array. If the new size is greater than the
	 * current size, new <code>null</code> items are added to the end of
	 * the Array. If the new size is less than the current size, all
	 * components at index <code>newSize</code> and greater are discarded.
	 *
	 * @param   newSize   the new size of this Array.	 */
	final public synchronized void setSize(int newSize) {
		if (newSize > itemCount) {
			setCapacity(newSize); }
		else	//Initialize the newly defined Elements to 'null'
			for (int i = newSize ; i < itemCount ; i++) {
				items[i] = null; }
		itemCount = newSize;
	}

	/**Returns the number of Elements in this Array.
	 *
	 * @return  the number of Elements in this Array.	 */
	final public int getInt() { return itemCount; }

	/**Returns an enumeration of the components of this Array.
	 *
	 * @return  an enumeration of the components of this Array.
	 * @see	 java.util.Enumeration	 */
	public Enumerator Enumerator() {
		return new ArrayEnum(this.items, this.itemCount); }

	/**Returns the first occurence of Item when it is in the Collection,
	 * otherwise 'null' is returned.	*/
	public Object findFirst(final Object Item) { return items[firstIndexOf(Item)]; }

	/**Returns the last occurence of Item when it is in the Collection,
	 * otherwise 'null' is returned.	 */
	public Object findLast(final Object Item) { return items[lastIndexOf(Item)]; }

	/**Returns the index of the first occurrence of the specified object in
	 * this Array.
	 *
	 * @param   elem   the desired component.
	 * @return  the index of the last occurrence of the specified object in
	 *		  this Array; returns <code>-1</code> if the object is not found.	 */
	public int firstIndexOf(final Object elem) {
		return firstIndexOf(elem, -1, itemCount);}

	/**Returns the index of the last occurrence of the specified object in
	 * this Array.
	 *
	 * @param   elem   the desired component.
	 * @return  the index of the last occurrence of the specified object in
	 *		  this Array; returns <code>-1</code> if the object is not found.	 */
	public int lastIndexOf(Object elem) {
		return lastIndexOf(elem, -1, itemCount);}

	/**Searches forwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   lower   the index to start searching from.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>-1</code> if the object is not found.	 */
	public int firstIndexOf(Object elem, int lower) {
		return firstIndexOf(elem, lower, itemCount);}

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   upper   the index to start searching from.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>-1</code> if the object is not found.	 */
	public int lastIndexOf(Object elem, int upper) {
		return lastIndexOf(elem, -1, upper);}

	/**Searches forwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   lower   the index to start searching from.
	 * @param   upper   the index to stop  searching at.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>stop+1</code> if the object is not found.	 */
	public int firstIndexOf(Object elem, int lower, int upper) {
		int i = lower;
		while (++i < upper)
			if (elem.equals(getAt(i))) break;
		return i; }

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   upper   the index to start searching from.
	 * @param   lower   the index to stop  searching at.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>stop-1</code> if the object is not found.	 */
	public int lastIndexOf(Object elem, int lower, int upper) {
		int i = upper;
		while (--i > lower)
			if (elem.equals(getAt(i))) return i; //break; //equivalent...
		return i; }

	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was
	 *			 given.	 */
	public synchronized Object getAt(int index) {
		if (index >= itemCount)	//have to catch that explicitly, because the actual Array is larger!
			return null;//throw new ArrayIndexOutOfBoundsException(index + " >= " + ItemCount);

		/* Since try/catch is free, except when the exception is thrown,
		   put in this extra try/catch to catch negative indexes and
		   display a more informative error message.  This might not
		   be appropriate, especially if we have a decent debugging
		   environment - JP. */
		try { return items[index];}
		catch (ArrayIndexOutOfBoundsException e) {
			return null; }//throw new ArrayIndexOutOfBoundsException(index + " < 0");}
	}

	/**Returns the first component of this Array.
	 *
	 * @return	 the first component of this Array.
	 * @exception  NoSuchElementException  if this Array has no components.	 */
	public Object first() {
		if (itemCount == 0) return null;	//throw new NoSuchElementException();
		return items[0]; }

	/**Returns the last component of the Array.
	 *
	 * @return  the last component of the Array, i.e., the component at index
	 *		  <code>size()&nbsp;-&nbsp;1</code>.
	 * @exception  NoSuchElementException  if this Array is empty.	 */
	public Object last() {
		if (itemCount == 0) return null;	//throw new NoSuchElementException();
		return items[itemCount - 1]; }

	/**Replaces the component at the specified <code>index</code> of this
	 * Array to be the specified object. The previous component at that
	 * position is discarded.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * Sets (adds or replaces) the component at the specified index.
	 * All other components in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * @return	 the component replaced by 'Item'.
	 * @param	  item	 the component to set (add or replace).
	 * @param	  index   the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()	 */
	public Object setAt(int index, Object item) {
		if (index >= itemCount)
			throw new ArrayIndexOutOfBoundsException(index + " >= " + itemCount);
		final Object ret = items[index]; items[index] = item;
		return ret; }

	/**
	  * Flag to control which removeAt() Method to use,
	  * the fast one or the slower one, which retains the Sequence.
	  * Put here, because it only controls the following Method!
	  */
	public boolean removeFast;

	/**Deletes the component at the specified index. Each component in
	 * this Array with an index greater or equal to the specified
	 * <code>index</code> is shifted downward to have an index one
	 * smaller than the value it had previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * @param	  index   the index of the object to remove.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()	 */
	public synchronized Object removeAt(int index) {
		if (removeFast) {
			return removeFastAt(index); } //do it fast, but lose Order!
			return removeSlowAt(index); } //keep the Order

	/**Deletes the component at the specified index. Each component in
	 * this Array with an index greater or equal to the specified
	 * <code>index</code> is shifted downward to have an index one
	 * smaller than the value it had previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * This is one of the slower Operations of Array,
	 * together with insertAt() and append(),
	 * where a Linked List would be faster.
	 *
	 * @param	  index   the index of the object to remove.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()	 */
	public synchronized Object removeSlowAt(int index) {
		if (index >= itemCount)
			throw new ArrayIndexOutOfBoundsException(index + " >= " + itemCount);
		Object Item = items[index];
		int j = itemCount - index - 1;
		if (j > 0) //don't leave holes behind!
			System.arraycopy(items, index + 1, items, index, j);
		items[--itemCount] = null; /* to let garbage Collection do it's work */
		return Item; }

	/** Deletes the component at the specified index.
	  * The last component in this Array is moved to the given <code>index</code>.
	  * All other Elements retain their Index,
	  * but the overall Sequence is disturbed.
	  * <p>
	  * The index must be a value greater than or equal to <code>0</code>
	  * and less than the current size of the Array.
	  *
	  * @param	  index   the index of the object to remove.
	  * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	  * @see		java.util.Array#size()	 */
	public synchronized Object removeFastAt(int index) {
		if (index >= itemCount)
			throw new ArrayIndexOutOfBoundsException(index + " >= " + itemCount);
		Object Item  = items[index];
		items[index] = items[--itemCount];
		items[itemCount] = null; //Remove the Reference to enforce Garbage Collection.
		return Item; }

	/** Inserts the specified object as a component in this Array at the
	  * specified <code>index</code>. Each component in this Array with
	  * an index greater or equal to the specified <code>index</code> is
	  * shifted upward to have an index one greater than the value it had
	  * previously.
	  * <p>
	  * The index must be a value greater than or equal to <code>0</code>
	  * and less than or equal to the current size of the Array.
	  *
	  * @param	  obj	 the component to insert.
	  * @param	  index   where to insert the new component.
	  * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	  * @see		java.util.Array#size()	 */
	public IndexEnumerator addAt(final int index, final Object obj) {
		if (itemCount <  index) { throw new ArrayIndexOutOfBoundsException(index + " > " + itemCount); } //don't allow undefined Areas...
		if (itemCount >= items.length) { setCapacity(AVector.ENLARGED_CAPACITY(itemCount + 1, capacityIncrement)); }
		if (itemCount >  index) { System.arraycopy(items, index, items, index + 1, itemCount - index); }
		itemCount++;
		if (obj instanceof IIStreamIn) {
			Array tmp;
			items[index]  = tmp = new Array(items.length, capacityIncrement);
			tmp.addItems((IIStreamIn) obj); //stream here already, not outside! Makes Operation more granular.
			return this; } //tmp; } //don't return a different Iterator, Streaming has already taken Place!
		items[index] = obj;
		return this; }

	/**Removes the first occurrence of the argument from this Array. If
	 * the object is found in this Array, each component in the Array
	 * with an index greater or equal to the object's index is shifted
	 * downward to have an index one smaller than the value it had previously.
	 *
	 * @param   obj   the component to be removed.
	 * @return  The Item replaced if the argument was a component of this
	 *		  Array; <code>streamIO.Iterator.EOI</code> otherwise.	 */
	public Object removeAt(Object Item) {
		return removeAt(firstIndexOf(Item)); }

	/**Flips the Item, i.e. when it is contained, remove it,
	 * otherwise add it. This corresponds to the XOR Operation. 	 */
	public Object flipAt(Object Item) {
		int i;
		if ((i = firstIndexOf(Item)) >= 0)	return removeAt(i);
		else								addAt	(Item);
		return IIStreamIn.EOI; }

	/**Removes all components from this Array and sets its size to zero.	 */
	public IGroup zeroAt() {
//		Object elementDataLocal[] = this.Items;
//		for (int i = ItemCount - 1 ; i >= 0 ; i--)
//			elementDataLocal[i] = null;	//only necessary for Garbage Collection
		itemCount = 0;
		return this; }

	/**Returns a clone of this Array.
	 * Does only a shallow Copy!
	 *
	 * @return  a clone of this Array.	 */
	public synchronized Object clone() {
		try	{	//does only a shallow Copy
			Array v = (Array)super.clone();
			v.items = new Object[itemCount];
			System.arraycopy(items, 0, v.items, 0, itemCount);
			return v;
		} catch (CloneNotSupportedException e) {	// this shouldn't happen, since we are Cloneable
			throw new InternalError(); }
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
	public ICopyAble copyAt(Object arg) {
		if (arg instanceof Array) {
			Array arg_ = (Array) arg;
			capacityIncrement = arg_.capacityIncrement;
			setCapacity (arg_.itemCount);
			itemCount  = arg_.itemCount;
			System.arraycopy(arg_.items, 0, items, 0, itemCount);
		} else super.copyAt(arg);	//no need to use a recursive DeepCopy like with Tensor
		return this; }

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) {
		if (arg instanceof Array) {
			Array arg_ = (Array) arg;
			capacityIncrement = arg_.capacityIncrement;
			itemCount = arg_.itemCount;
			items = arg_.items;
		} else super.copyAt(arg);
		return this; }

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance(){ return new Array(items.length, capacityIncrement); }

	////////////////////////////////////////////////////////////////////////////////
	//	Multiplication with a Permutation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Multiply the Vector by an Object in Place.
	 * This extends the standard Set Multiplication
	 * by the Multiplication with a Permutation.	 */
	public ISemiGroupM mulAt(Object arg)	{
		if (arg instanceof Permutation) {
			copyAt(Permutation.map(items, items.length, (Permutation) arg));
			return this; }
		if (arg instanceof int[]) {
			int[] arg_ = (int[]) arg;
			copyAt(Permutation.map(items, items.length, arg_, arg_.length));
			return this; }
		return super.mulAt(arg); }

	/**Multiply the Vector by an Object.
	 * This extends the standard Set Multiplication
	 * by the Multiplication with a Permutation.	 */
	public ISemiGroupM mul(Object arg) {
		if (arg instanceof Permutation) return new Array(Permutation.map(items, items.length, (Permutation) arg), capacityIncrement);
		if (arg instanceof int[]	  ) return new Array(Permutation.map(items, items.length, (int[]	  ) arg), capacityIncrement);
		return super.mul(arg); }

	////////////////////////////////////////////////////////////////////////////////
	//	Optimizations
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Array.class.getName());
		Object[] objArr = {"0", "1", "2", "3", "4"};//no other way...
		Object[] nestArr1 = {"1,0", "1,1", "1,2"};  //the Initializer {} can only be used
		Object[] nestArr3 = {"3,0", "3,1", "3,2"};  //together with a Declaration
		objArr[1] = nestArr1;
		objArr[3] = nestArr3;
		Array arrOut, arr = new Array(objArr); //testing nested Constructor
//		printContents(arr);
		arrOut = new Array(); //testing nested Streaming.
		System.out.println("Number of Item streamed: " + stream(arr, arrOut, Integer.MAX_VALUE));
		printContents(arrOut);
	}

	/**
	  * prints the whole Content of the given Input streamIO to System.out
	  */
	public static void printContents(IIStreamIn SI) {
		Object obj;
		do {
			obj = SI.nextItem();
			if (obj instanceof IIStreamIn) {
				System.out.println("["); printContents((IIStreamIn) obj);
				System.out.println("]");
			}else{
				System.out.println(obj);
			}
		} while (obj != null);
	}

	/**
	  * Transfers the whole Content of the given Input streamIO to the Output streamIO.
	  * The Depth is maximum, so the complete Tree is traversed.
	  */
	public static int stream(IIStreamIn SI, IIStreamOut SO) {
		return stream(SI, SO, Integer.MAX_VALUE); }

	/**
	  * Transfers the whole Content of the given Input streamIO to the Output streamIO.
	  * Recursion is necessary, because both Streams should not have to know their Parents.
	  * In fact they could even be shared by different Parents in a Diamond Shape.
	  * The Depth makes it clear that it is possible to do shallow Copies
	  * and Copies up to a certain Depth.
	  * If the Objects are providing StreamIn Instances or are themselves
	  * Instances of StreamIn, handing them over ByRef
	  * allows to transfer later Changes via the Object Reference
	  * but also the Danger of encountering mysterious Changes and Side Effects!
	  */
	public static int stream(IIStreamIn SI, IIStreamOut SO, int Depth) {
		if (--Depth < 0) return 0;
		int ret = 0;
		Object obj;
		IIStreamOut NSO;
		while ((EOI != (obj = SI.nextItem())) || SI.isValid()) {
			NSO = SO.addItem(obj); ++ret;
			if (NSO != SO) { //equivalent but faster! Also reacts to Containers not supporting nested Iterators.
//			if (obj instanceof IStreamIn ) {
				if (Depth > 0) { //Optimization to save the Call.
					stream((IIStreamIn) obj, NSO, Depth); }
			}
		} return ret; }

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}

/**Iterator for the 'Array' Class.
 * Since this Class is only instantiated by "Array" itself,
 * it is protected in this Package
 * This one has nothing to do with the ArrayIterator of Package streamIO. */
final class ArrayIterator
extends AIndexEnumerator { //ArrayEnum { //
	
	/**Reference to the Array iterated	 */
	protected Array array;
	
	/** @return the Order this Iterator returns the Items in. 	*/
	public byte getOrder() { return IPipe.ORDER_STACK; }
	
	/**Constructor with the Array as Parameter	 */
	protected ArrayIterator(final Array v) {
		super(null); 
		array = v; } //

	/** Restart the Iterator, done automatically on Instantiation	 */
	public void reStart(final int Position) { curr = Position; }

	/** Reset the Reverse Iterator	 */
	public IReSetAble reSet(){ curr = array.itemCount -1; return this; }

	/** @return the total Number of Objects in this Enumerator / Container
	  * For Random Access Stores this is definitely limited and can thus be returned.
	  */
	public int getInt() { return array.itemCount; }

	/** @return  the previous Item	 */
	public Object previousItem() { //ByRefLong available) {
		Object Item = currentItem(); //available);
		curr--; return Item; }

	/** @return  the current Item	 */
	public Object currentItem() { //ByRefLong available) {
		synchronized (array) {
			if ((//available.Value =
				(array.itemCount -curr)) > 0) // && (current >= 0)))
				return array.items[(int) curr]; }
				return IIStreamIn.EOI; // null;	//	throw new NoSuchElementException("VectorEnumerator");
		}

	/**Replaces the Item at the given Position in the Iterator with the given one	 */
	public Object setAt(int Position, Object Item) {
		return array.setAt(Position, Item); }

	/**Removes the current Object from the Container with this Iterator knowing it.
	 * The remaining Problem is other Iterators that concurrently work through this. */
	public Object removeCurr() { //
		return array.removeAt(--curr); }	//decrease current, so nextItem retrieves this Position

	/**Removes the current Object from the Container with this Iterator knowing it.
	 * The remaining Problem is other Iterators that concurrently work through this. */
	public Object removeAt(int Position) { //
		return array.removeAt(Position); }	//decrease current, so nextItem retrieves this Position

	/** Returns the Item at the given Position in the Set, starting Counting from 0. 	*/
	public Object getAt(int Position) { return array.getAt((int) Position); }

	/** Returns the Item at the given Position in the Set, starting Counting from 0. 	*/
	public IndexEnumerator addAt(int Position, Object obj) {
		array.addAt(Position, obj); return this; }

	////////////////////////////////////////////////////////////////////////////////
	// Optimizations...
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the minimum Number of Items left in this streamIO.	  */
	public long availAble() { return array.itemCount - curr -1; }

}
