package streamIO.object.enumer;

import streamIO.object.IPipe;
import streamIO.object.ModificationException;

/**
 * Title:        ArrayEnum <p>
 * Description:
 * Enumerator Class for any Type of Array except for primitive Types
 * @see PrimArrayEnum which is slower, but also usable for primitive Types
 *
 * Don't confuse it with the local ArrayEnum Class in Array.Array.
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	  personal<p>
 * @author 		 Matthias Heuer
 * @version 1.0
 * @stereotype enumeration
 * <!-- docstate
 * tags: [code/enumerator, code/iterator_adapter]
 * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class ArrayEnum
extends AIndexEnumerator {

	/**Local Reference to the Array to be iterated over	 */
	protected Object[] arr;

	/** Local Cache for the used Length of the Array to be iterated over
	  * modified on adding/removing Items.	 */
	protected int Length;

	/** Flag for the Behavior of the Iterators.
	  * if true, nested Arrays are recursively returned as Iterators.
	  * otherwise nested Arrays are returned as is,
	  * which is not useful for Serialization and persistent Storage.	 */
	public boolean recursive = true;
	
	/**Initializing Constructor	 */
	public ArrayEnum(final Object[] arr, final int MaxLength, final boolean recursive) {
		super(null);
		this.arr = arr;
		Length = MaxLength; }
	
	/**Initializing Constructor	 */
	public ArrayEnum(final Object[] arr, final int MaxLength) {
		this(arr, MaxLength, false); }
	
	/**Initializing Constructor	 */
	public ArrayEnum(final Object[] arr) {
		this(arr, arr.length, false); }
	
	/**Initializing Constructor	 */
	public ArrayEnum(Object[] arr, boolean recursive) {
		this(arr, arr.length, recursive); }
	
	////////////////////////////////////////////////////////////////////////////////
	//	Interface StreamIn
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the cached used Length of the wrapped Array.
	  * @return the total Number of Objects in this Enumerator / Container
	  * For Random Access Stores this is definitely limited and can thus be returned.
	  */
	public int getInt() { return Length; }
	
	/**Returns the Item at the given absolute Position
	 * Returns SOI for negative Indices and EOI for Indices larger than the Size
	 * Could also use try/catch Block, but that is much more expensive!	 */
	public Object getAt(int index) {
		if (index >= arr.length) return EOI;
		if (index < 0) return SOI;
		return arr[index]; }
	
	/** Returns the next Object:
	  * Optimization for Performance Increase by not calling getAt()
	  * prevent incrementing currItem above Limit.
	  * Implements the recursive Iterator Protocol. */
	public Object nextItem() {
		if (++curr >= arr.length) {
			  curr  = arr.length;
			return EOI; }
		Object ret;
		if(!recursive) {
			return arr[curr]; }
		if((ret =  arr[curr]) instanceof Object[]) {
			ret =  new ArrayEnum((Object[]) ret, recursive); }
		return ret; }

	/** Returns the previous Object:
	  * Optimization for Performance Increase by not calling getAt()
	  * prevent incrementing currItem below 0.
	  * Implements the recursive Iterator Protocol. */
	public Object prevItem() {
		if (--curr < 0) {
			  curr = 0;
			return SOI; }
		Object ret;
		final ArrayEnum enm;
		if(!recursive) {
			return arr[curr]; }
		if((ret =  arr[curr]) instanceof Object[]) {
			ret =  enm = new ArrayEnum((Object[]) ret);
			enm.jump(1);  } //the Opposite of reset() is not available!
		return ret; }

	/**
	  * sets the Iterator behind the last Position.
	  * This is the Opposite to reset()
	  * just like previous() the Opposite to next()
	  */
	public ReverseEnumerator preset() {
		curr = arr.length;
		return this; }

	/**Skips over and discards n Items from this Enumerator.
	 * Returns the actual number of Items skipped.
	 * This dumb Implementation just reads all Elements and discards them.	 */
	public long jump(long Position) {
		if((curr += Position) > arr.length) {
					Position  = arr.length - curr;
			curr              = arr.length; }
		return Position; }

	/** Adds the given Item after the given Positon to the Container. */
	public IndexEnumerator addAt(int Pos, Object Item) throws ModificationException {
		System.arraycopy(	arr, Pos - 1,
							arr, Pos, Length - Pos + 1);
		arr[Pos] = Item; ++Length;
		return this; }
	
	/**Replaces the Item at the given Position in the Enumerator with the given one	 */
	public Object setAt(int Pos, Object Item) { //throws ModificationException {
		final Object ret = arr[Pos];
		arr[Pos] = Item;
		return ret; }
	
	/**Removes the Object at the given Position from the Container with this Enumerator knowing it. */
	public Object removeAt(int Pos) throws ModificationException {
		final Object ret = arr[Pos];
		System.arraycopy(	arr, Pos + 1,
							arr, Pos, arr.length - Pos);
		arr[arr.length - 1] = null; //Set the last Item to null to enforce Garbage Collection.
		return ret; }
	
	/** Replaces the current Object in the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * In this Case the Exception is thrown.
	  * That is why this Method should throw an Exception if replacing is not allowed.
	  * It should also update the Minor Version (or let the Container update it)
	  * to announce the Change to other Iterators.
	  * @param  The Item to replace the current Item (returned by the latest nextItem())
	  * @return the Object replaced by the Item
	  */
	public Object replaceCurr(final Object Item) { //throws ModificationException {
		final Object ret = arr[curr]; arr[curr] = Item;
		return ret; }
	
	/**Removes the current Object from the Container with this Enumerator knowing it. */
	public Object removeCurr() {
		final Object ret = arr[curr];
		System.arraycopy(	arr, curr + 1,
							arr, curr, arr.length - curr);
		arr[arr.length - 1] = null; //Set the last Item to null to enforce Garbage Collection.
		return ret; }
	
	/** Adds the given Item after the current Object to the Container. */
	public ReverseEnumerator addPrev(final Object Item) throws ModificationException {
		System.arraycopy(	arr, curr - 1,
							arr, curr, Length - curr + 1);
		arr[curr] = Item; ++Length;
		return this; }
	
	/**Replaces the current Item in the Enumerator with the given one	 */
	public Object replacePrev(final Object Item) { //throws ModificationException {
		Object ret = arr[curr - 1];
		arr[curr - 1] = Item;
		return ret; }
	
	/**Removes the current Item from the Enumerator	 */
	public Object removePrev() throws ModificationException {
		Object ret = arr[curr];
		System.arraycopy(	arr, curr,
							arr, curr - 1, Length - curr + 1);
		--Length; return ret; }
	
	/** Adds the given Item after the current Object to the Container. */
	public Enumerator addNext(final Object Item) throws ModificationException {
		System.arraycopy(	arr, curr + 1,
							arr, curr + 2, Length - curr - 1);
		arr[curr+1] = Item; ++Length;
		return this; }
	
	/**Replaces the current Item in the Enumerator with the given one	 */
	public Object replaceNext(final Object Item) { //throws ModificationException {
		Object ret = arr[curr+1];
		arr[curr+1] = Item;
		return ret; }
	
	/**Removes the current Item from the Enumerator	 */
	public Object removeNext() throws ModificationException {
		Object ret = arr[curr];
		System.arraycopy(	arr, curr + 2,
							arr, curr + 1, Length - curr - 1);
		--Length; return ret; }
	
	/** Returns a new Intstance of an alterable Iterator ,
	  * which allows for changing the Data and structure concurrently. */
	public Enumerator Enumerator() { //return null; }
		return new ArrayEnum(arr); }
	
	/** Returns a new Intstance of a ChangeIterator,
	  * which allows for changing the Data concurrently. */
	public ChangeIterator ChangeIterator() { return new ArrayEnum(arr); }
	
	/** Returns the Order in which Elements are returned or processed.	 */
	public byte getOrder() { return IPipe.ORDER_QUEUE; }
	
    /** removes the current Item (returned by the latest nextItem())
      * @return the current Item	 */
//    public Object removeCurr() { return Parent.removeCurr(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Optimizations
	////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the minimum Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number. */
	public long availAble() { return Length - curr -1; } //-1 because of preIncrement
	
}
