package streamIO.object.enumer.container;

import java.util.Comparator;

import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.ISemiGroup;
import streamIO.object.IPipe;
import streamIO.object.enumer.IndexEnumerator;
import tester.IOrderator;
import tester.IScalarMetric;
import tester.ITester;
import tester.MetricByHash;
import function.IIOrderAble;
import function.IMeasurAble;
import function.IProcessor;
import function.byref.ByRefDouble;

/** 
 * This is a sorted Array, Items are added in sort Order using the
 * @see IOrderable Interface.
 *
 * Adding Items to a sorted Array is done in lb n Operations
 * (apart from moving the Array to create space), so sorting happens in O(n log n).
 * Accessing the Array happens in O(1) Time. 
 * A SortedTree is actually faster to insert into, 
 * because it has at most to be rearranged when it is imbalanced 
 * so inserting is a real O(log n) Operation, 
 * but the Position can only be determined in O(n) Operations, 
 * so the Index must be stored together with the actual Item. 
 *
 * The main change is implemented in the Method
 * @see IndexOf which uses (weighted) BiSection to find the Position of an Element
 * (weighted) BiSection is used when a Metric is given for each Element,
 * but makes Sense only when the Assumption of Equidistribution is fulfilled closely
 * Otherwise a normal Comparator is used for normal BiSection.
 *
 * A sorted Array is very often used to track a 1 dimensional History.
 * For multidimensional Histories use cascaded sorted Arrays of sorted Arrays,
 * because it is possible that the Numbers of Items in each Position vary.
 * There can be no Overlapping (except two Items have exactly the same Point in Time),
 * so you can always identify the Value at a certain Point in Time using IndexOf().
 * This can be used to sort Entries in an Account too.
 *
 * If you want to sort Objects, you have to create a Mapping to the ordered Space
 * using the Association Class.
 *
 * Design Decisions:
 * Searching is done via a binary Search (BiSection),
 * alternatively also an Interpolation Search can be performed,
 * which returns any Item in less than lb(lb(N)) Steps (!),
 * but only if the Items are evenly distributed!
 *
 * Sorting uses the intOrderable Interface to determine the Order.
 * Of last-/first- IndexOf the Index with the first Item greater or equal
 * to the Item is returned. Using the inherited Methods even returns
 * the correct Position before or after the Array Index Range.
 *
 */
public class SortedArray
extends Array {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The Default Sort order of the Array	 */
	public static boolean DEFAULT_ASCENDING_ORDER = true;
	
	/** The Default Setting for Interpolation	 */
	public static boolean DEFAULT_INTERPOLATE = false;
	
	/** The Default Comparator being used to compare Elements.
	 * For only finding Elements, the MetricByHash 
	 * without Interpolation is sufficient, allowing for BiSection. 
	 */
	public static IOrderator DEFAULT_ORDERATOR = MetricByHash.Metric; // null;
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Determines whether Interpolation Search is used, 
	 * even when no Metric is given. 
	 * Should be set to true, when the Data is evenly distributed
	 * and implements the measurable Interface.	 */
	protected boolean interpolate;
	
	/** The Sort order of the Array	 */
	protected boolean ascending = true;
	
	/** The Comparator being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see Comparable  or
	  * @see IIOrderAble	 */
	protected IOrderator orderator;
	
	/** The Metric being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see Comparable  or
	  * @see IIOrderAble	 */
	protected IScalarMetric metric;
	
	//////////////////////
	//	Constructors:	//
	//////////////////////
	
	//5 Parameters
	
	/**Constructs an empty Array with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity   the initial capacity of the Array.
	 * @param   capacityIncrement the amount by which the capacity is increased on Overflow
	 * @param   interpolating     perform Interpolation on find...
	 * @param   Comparator        Used to compare Elements
	 * @param   ascendingOrder    used to switch between asc and desc
	 */
	public SortedArray(final int initialCapacity, final int capacityIncrement, 
			final IOrderator Comparator, final boolean interpolating, final boolean ascendingOrder) {
		super(initialCapacity, capacityIncrement);
		if (Comparator instanceof IScalarMetric) 
			   this.metric = (IScalarMetric) Comparator; 
		this.orderator = Comparator; //for Consistency still also set the Comparator!
		this.interpolate = interpolating;
		this.ascending = ascendingOrder;
	}

	//4 Parameters

	public SortedArray(final int initialCapacity, final int capacityIncrement, final IOrderator Comparator, final boolean interpolating) {
		this(initialCapacity, capacityIncrement, Comparator, interpolating, DEFAULT_ASCENDING_ORDER); }

	public SortedArray(final int initialCapacity, final int capacityIncrement, final boolean interpolating, final boolean ascendingOrder) {
		this(initialCapacity, capacityIncrement, null, interpolating, ascendingOrder); }

	public SortedArray(final int initialCapacity, final IOrderator Comparator, final boolean interpolating, final boolean ascendingOrder) {
		this(initialCapacity, DEFAULT_CAPACITY_INCREMENT, Comparator, interpolating, ascendingOrder); }

	//3 Parameters

	public SortedArray(final int initialCapacity, final int capacityIncrement, final IOrderator Comparator) {
		this(initialCapacity, capacityIncrement, Comparator, DEFAULT_INTERPOLATE, DEFAULT_ASCENDING_ORDER); }

	public SortedArray(final int initialCapacity, final int capacityIncrement, final boolean interpolating) {
		this(initialCapacity, capacityIncrement, null, interpolating, DEFAULT_ASCENDING_ORDER); }

	public SortedArray(final int initialCapacity, final IOrderator Comparator, final boolean interpolating) {
		this(initialCapacity, DEFAULT_CAPACITY_INCREMENT, Comparator, interpolating, DEFAULT_ASCENDING_ORDER); }


	public SortedArray(final int initialCapacity, final boolean interpolating, final boolean ascendingOrder) {
		this(initialCapacity, DEFAULT_CAPACITY_INCREMENT, null, interpolating, ascendingOrder); }


	public SortedArray(final IOrderator Comparator, final boolean interpolating, final boolean ascendingOrder) {
		this(DEFAULT_CAPACITY, DEFAULT_CAPACITY_INCREMENT, Comparator, interpolating, ascendingOrder); }

	//2 Parameters

	public SortedArray(final int initialCapacity, final int capacityIncrement) {
		this(initialCapacity, capacityIncrement, null, DEFAULT_INTERPOLATE, DEFAULT_ASCENDING_ORDER); }

	public SortedArray(final int initialCapacity, final IOrderator Comparator) {
		this(initialCapacity, DEFAULT_CAPACITY_INCREMENT, Comparator, DEFAULT_INTERPOLATE, DEFAULT_ASCENDING_ORDER); }


	public SortedArray(final int initialCapacity, final boolean interpolating) {
		this(initialCapacity, DEFAULT_CAPACITY_INCREMENT, null, interpolating, DEFAULT_ASCENDING_ORDER); }


	public SortedArray(final IOrderator Comparator, final boolean interpolating) {
		this(DEFAULT_CAPACITY, DEFAULT_CAPACITY_INCREMENT, Comparator, interpolating, DEFAULT_ASCENDING_ORDER); }


	public SortedArray(final boolean interpolating, final boolean ascendingOrder) {
		this(DEFAULT_CAPACITY, DEFAULT_CAPACITY_INCREMENT, null, interpolating, ascendingOrder); }

	//1 Parameter

	public SortedArray(final int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCREMENT, null, DEFAULT_INTERPOLATE, DEFAULT_ASCENDING_ORDER); }

	public SortedArray(final IOrderator Comparator) {
		this(DEFAULT_CAPACITY, DEFAULT_CAPACITY_INCREMENT, Comparator, DEFAULT_INTERPOLATE, DEFAULT_ASCENDING_ORDER); }

	public SortedArray(final boolean interpolating) {
		this(DEFAULT_CAPACITY, DEFAULT_CAPACITY_INCREMENT, null, interpolating, DEFAULT_ASCENDING_ORDER); }

	//0 Parameter

	/**Constructs an empty Array with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the Array.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the Array overflows.	 */
	public SortedArray() {
		this(DEFAULT_CAPACITY, DEFAULT_CAPACITY_INCREMENT, null, DEFAULT_INTERPOLATE, DEFAULT_ASCENDING_ORDER); }

	/**Copy- Constructor	 */
	public SortedArray(Object arg) { copyAt(arg); }

	////////////////////////////////////////////////////////////////////////////////
	// Interface Pipe
	////////////////////////////////////////////////////////////////////////////////

	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() {
		if (ascending)
			return IPipe.ORDER_ASC;
			return IPipe.ORDER_DESC; }

	/** @return The Comparator being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see Comparable  or
	  * @see IIOrderAble	 */
	public Comparator getComparator () { return null; }

	////////////////////////////////////////////////////////////////////////////////
	// Interface IndexEnumerator
	////////////////////////////////////////////////////////////////////////////////

	//This is the deciding Method!

	/** Adds this Item to the Container.
	  * Since adding pushes the other Items to the right,
	  * the Position can be from 0 to itemCount. */
	public ISemiGroup addAt(final Object item) {	//find the Position of the nearest Object
		addItemAt(item); return this; }

	/** Adds this Item to the Container.
	 * @param Item the item to add 
	 */
	public IIStreamOut addItem(final Object item) {	//find the Position of the nearest Object
		addItemAt(item); 
		return this; }

	/** Adds this Item to the Container.
	 * @param Item the item to add 
	 * @return the Position at which it was added. 
	 */
	public int addItemAt(final Object item) {	//find the Position of the nearest Object
		int ret = indexOf(item); 
		if (ret < 0)
			ret = 0; 
		super.addAt(ret, item); 
		return ret; }

	/** Performs the Operation of the Operator on each Item in the Collection
	  * that equals this Item. The generic Solution is slow
	  * and can be highly optimized in concrete Implementations.
	  * In a sorted Array all Items that are equal must be in unbroken Sequence,
	  * so they can be quickly located and operated on. */
	public int forEachThatEquals(final Object item, final IProcessor op) {
		if (op   == null) return 0;
		if (item == null) return 0;
		Object tst;
		int count = 0, start = indexOf(item); //the Item could be in the Middle of a Group of similar Items
		for(int i = start; (tst = items[i--]).equals(item);) { ++count; op.MapAt(tst); }
		for(int i = start; (tst = items[++i]).equals(item);) { ++count; op.MapAt(tst); }
		return count; }

	/** Returns the first Item of those Item in the Collection that equals this Item,
	  * that also fulfills the Test of the ITester Object.
	  * In a sorted Array all Items that are equal must be in unbroken Sequence,
	  * so they can be quickly located and operated on. */
	public Object firstOfEachThatEqualsThat(final Object item, final ITester Test) {
		Object tst;
		if (Test == null) return null;
		int i, start = indexOf(item); //the Item could be in the Middle of a Group of similar Items
		i = start;while ((tst = items[i--]).equals(item)) if (Test.test(tst)) return tst;
		i = start;while ((tst = items[++i]).equals(item)) if (Test.test(tst)) return tst;
		return null; }

	/**Returns the any occurence of Item when it is in the Collection,
	 * otherwise 'null' is returned.
	 * findFirst makes only Sense in Containers with an Order! */
	public Object find(final Object item) { 
		final int index = indexOf(item); 
		if ((index <0) || (index >= itemCount))
			return null; 
		return items[index]; }
	
	/** Searches for the specified object / Value, and returns an index to it.
	  * It is here where the BiSection Algorithm is implemented (binary Search)!
	  *
	  * @param   elem	the desired component.
	  * @param   lower   the lower bound to search from.
	  * @param   upper   the upper bound to search from.
	  * @return  the index of an occurrence of the specified object in this
	  *		  Array at position less than <code>upper</code>
	  *			and greater than <code>lower</code> in the sorted Array;
	  *			If the object is not found, the next Index above the targeted one
	  *			is returned, also <code>upper+1</code> for items larger than the largest
	  *			and <code>lower-1</code> for items smaller than the smallest.
	  *
	  * Design Decisions:
	  * This could be programmed recursively, but that would be less effective!
	  * This returns ANY Item that equals the searched one, NOT the first one!	
	  */
	final public int indexOf(final Object Item){ //final to foster Inlining
		return indexOf(Item, 0, itemCount); //getInt()); 
	}

	/** Searches for the specified object / Value, and returns an index to it.
	  * It is here where the BiSection Algorithm is implemented (binary Search)!
	  *
	  * @param   elem	the desired component.
	  * @param   lower   the lower bound to search from (inclusive).
	  * @param   upper   the upper bound to search from (exclusive to stay consistent with other Array Ops).
	  * @return  the index of an occurrence of the specified object in this
	  *		  Array at position less than <code>upper</code>
	  *			and greater than <code>lower</code> in the sorted Array;
	  *			If the object is not found, the next Index above the targeted one
	  *			is returned, also <code>upper+1</code> for items larger than the largest
	  *			and <code>lower-1</code> for items smaller than the smallest.
	  *
	  * Design Decisions:
	  * This could be programmed recursively, but that would be less effective!
	  * This returns ANY Item that equals the searched one, NOT the first one!	
	  */
	public int indexOf(final Object Item, int lower, int upper){
		--upper; //to avoid acces after Array
		boolean interPol = interpolate; //for switching it off temporarily
		final Comparable  compItem = (Item instanceof Comparable) ? (Comparable) Item : null;
		final IIOrderAble orderItem = (Item instanceof IIOrderAble) ? (IIOrderAble) Item : null;
		double dLo = 0, dHL = 0, loV = 0, Val = 0;
		if (! ((Item instanceof IMeasurAble) ||
			   (Item instanceof Number) ||
			   (metric != null) )) interPol = false;
		if (interPol) {	//caching of the getFloats improves the Speed!
			if (metric != null) {
				dLo = metric.dist(Item        , items[lower]);
				dHL = metric.dist(items[upper], items[lower]);
			} else {
				loV = ByRefDouble.GET_DOUBLE(items[lower]);
				Val = ByRefDouble.GET_DOUBLE(Item		 );
				dHL = ByRefDouble.GET_DOUBLE(items[upper]) - loV;
				dLo = Val - loV;
		}}
		while (lower < upper) { //iterative instead of recursive!
			int middle = (lower + upper) >> 1;	// (lower + upper)/ 2
			if (interPol){	//Use linear Interpolation to find the Item faster!
				middle = lower + (int) ((upper-lower)*dLo/dHL);
			} final double mdVal;
			final Object middleItem = items[middle]; // ItemAt(middle);	//equivalent!
			if (interPol) {
				if (metric != null) {
					mdVal = metric.dist(Item, middleItem);
				} else {
					mdVal  = Val - ByRefDouble.GET_DOUBLE(middleItem);
				}     if ((mdVal > 0) != ascending) { //smaller
					lower = middle + 1; dLo = mdVal;
				}else if ((mdVal < 0) != ascending) { //larger
					upper = middle;	    dHL = dLo - mdVal; //assume linear...
				}else if (Item.equals(middleItem)) { //quite improbable!
					return middle;
				}else { 
					interPol = false; } //cannot use Interpolation here, switch it off temporarily
			} else if (orderator != null) { //use a Comparator
				//final int cmp = mComparator.compare(Item, MiddleItem);
				if (orderator.less(Item, middleItem) != ascending) {
					lower = middle + 1;
//				}else if (cmp > 0) {
				}else{ 
					upper = middle; }	//greater or equal
			} else if (compItem != null) {
				int cmp = compItem.compareTo(middleItem);
				if ((cmp < 0) != ascending) {
					  lower = middle + 1;
//				}else if (cmp > 0) {
				}else{
					upper = middle; }	//greater or equal
			} else if (  orderItem != null) {
				if (orderItem.isLessThan(middleItem) != ascending) {
					lower = middle + 1;
				}else{
					upper = middle; }	//greater or equal
			}
			if (Item.equals	(middleItem)) 
				return middle; //quite improbable!
		}	//now lower == upper!
		return upper; }	//is always greater or equal to the Item

//TODO: test whether this works...

	/**Searches for the last specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @return  the index of the first occurrence of the specified object in this
	 *		  Array at position less than <code>upper</code> in the Array;
	 *		  <code>upper+1</code> if the object is not found.	 */
	public int firstIndexOf(final Object item) {
		return firstIndexOf(item, 0, itemCount); }

	/**Searches for the first occurrencs of the specified object 
	 * in the specified Range, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   lower   the index to start searching from (inclusive).
	 * @param   upper   the index to stop  searching at (exclusive to stay consistent with other Array Ops).
	 * @return  the index of the first occurrence of the specified object in this
	 *		  Array at position less than <code>upper</code> in the Array;
	 *		  <code>upper+1</code> if the object is not found.	 */
	public int firstIndexOf(final Object Item, final int lower, final int upper) {
		int i = indexOf(Item, lower, upper);
		while ((--i >= lower) && (Item.equals(items[i])));
		return i+1; }

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   upper   the index to start searching from (inclusive).
	 * @param   lower   the index to stop  searching at (exclusive to stay consistent with other Array Ops).
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>upper</code> in the Array;
	 *		  <code>lower-1</code> if the object is not found.	 */
	public int lastIndexOf(final Object Item, final int lower, final int upper) {
		int i = indexOf(Item, lower, upper);
		while ((++i < upper) && (Item.equals(items[i])));
		return i+1; }

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>upper</code> in the Array;
	 *		  <code>lower-1</code> if the object is not found.	 */
	public int lastIndexOf(final Object item) {
		return lastIndexOf(item, 0, itemCount); }

	/**Copying with Order is fast, when copying from a sorted Array	 */
	private SortedArray SortCopyAt(Object arg, boolean shallow) {
		if (arg instanceof SortedArray)	{	//When it is a sorted array, it can be copied right away like a normal Array...
			ascending = ((SortedArray)arg).ascending;
			if (shallow) super.shallowCopyAt(arg); //
			else		 super.copyAt(arg);
		} else //...otherwise you have to use the generic way:
		{	//Sorting is done by adding the Elements one by one in a sorted way!
			if (arg instanceof Array) capacityIncrement = ((Array) arg).capacityIncrement;
			super.copyAt(arg, shallow?1:2);
		} return this; }

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg) { return SortCopyAt(arg, false); }

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg){ return SortCopyAt(arg, true); }

	/**Compares two Objects for equality.
	 * This Implementation is double as fast as the default one in 'Container',
	 * because the Items are sorted.
	 *
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see	 java.lang.Boolean#hashCode()
	 * @see	 java.util.Hashtable
	 * @since   JDK1.0 	 */
	public boolean equals(Object arg) {
		Object Item, TestItem = null;
		Container test_ = (Container) arg;
		if (test_.getClass() != getClass()) return false;
//		ByRefLong moreItems = new ByRefLong();
		IIStreamIn	 iter =	   Iterator();
		IIStreamIn testIter = test_.Iterator();
		while ( ((	Item =	 iter.nextItem()) != IIStreamIn.EOI) || // moreItems)) != null) ||
				((TestItem = testIter.nextItem()) != IIStreamIn.EOI))   // moreItems)) != null))	//This also includes the case of different Numbers
			if (! Item.equals(TestItem)) return false;	//with the first Items the same!
		return true; }


	//////////////////////////////
	//	Invalidated Operations:	//
	//////////////////////////////

	/**Error Message for Invalidated Operations	 */
	private static java.lang.String NotAllowedError = "Not allowed in sorted Arrays";

	/**Sets the component at the specified <code>index</code> of this
	 * Array to be the specified object.
	 *
	 * This Method tests whether the Element actually fits into this Position.
	 * This is much faster than using IndexOf() to find the actual Location
	 * and allows to reuse positional Information
	 * Of course it is invalid for a sorted Array to set Items at random Positions.
	 */
	public Object setAt(final int index, final Object Item) {
		++minor;
		if ((ascending == orderator.less(Item, items[index-1])) ||
		    (ascending != orderator.less(Item, items[index+1]))) {
			throw new AbstractMethodError (NotAllowedError); }
		final Object ret = items[index]; items[index] = Item;
		return ret; }

	/**Inserts the specified object in this Array at the specified <code>index</code>.
	 *
	 * This Method is made invalid, because it makes no sense
	 * for a sorted Array to get Items inserted at a random Position
	 */
	public IndexEnumerator addAt(int index, Object Item) {
		if ((ascending == orderator.less(Item, items[index-1])) ||
		    (ascending != orderator.less(Item, items[index+1]))) {
			throw new AbstractMethodError (NotAllowedError); }
		super.addAt(index, Item); return this; }

}
