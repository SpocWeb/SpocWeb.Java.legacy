package streamIO.object.enumer;

import java.lang.reflect.Array;

import streamIO.object.IPipe;
import streamIO.object.ModificationException;

/**
 * Title:        PrimArrayEnumerator <p>
 * Description:  Enumerator Class for any Type of Array, also primitive Types<p>
 * Copyright:    Copyright (c) Matthias Heuer<p>
 * Company:      personal<p>
 * @see ArrayEnumerator which is faster, but only usable for Object Types
 * @author 		 Matthias Heuer
 * @version 1.0
 * @stereotype enumeration
 */
public class ArrayEnumPrim
extends AIndexEnumerator {

	////////////////////////////////////////////////////////////////////////////
	//  Variables (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** used Length of the Array, cached	 */
	protected int Length;

	/**Local Reference to the Array to be iterated over	 */
	protected Object arr;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor     */
	public ArrayEnumPrim(final Object arr_, final int Length_) {
		super(null);
		this.Length = Length_;
		this.arr = arr_; }
	
	/** Initializing Constructor     */
	public ArrayEnumPrim(Object arr) {
		this(arr, Array.getLength(arr)); }
	
	////////////////////////////////////////////////////////////////////////////////
	//	Interface StreamIn
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the total Number of Objects in this Enumerator / Container
	  * For Random Access Stores this is definitely limited and can thus be returned.
	  */
	public int getInt() { return Length; }

	/**Returns the Item at the given absolute Position	 */
	public Object getAt(int Pos) { return Array.get(arr, Pos); }

	/**Returns the Item at the given absolute Position	 */
	public IndexEnumerator addAt(int Pos, Object Item) {
		System.arraycopy(	arr, Pos,
							arr, Pos + 1, Length - Pos);
		Array.set(arr, Pos, Item); ++Length;
		return this; }

	/**Replaces the current Item in the Enumerator with the given one	 */
	public Object setAt(int Pos, Object Item) { //throws ModificationException {
		Object ret = Array.get(arr, curr);
		Array.set(arr, curr, Item);
		return ret; }

	/**Removes the current Object from the Container with this Enumerator knowing it. */
	public Object removeAt(int Pos) throws ModificationException {
		Object ret = Array.get(arr, Pos);
		System.arraycopy(	arr, Pos + 1,
							arr, Pos, Length - Pos);
//		arr[arr.length - 1] = null; //Set the last Item to null to enforce Garbage Collection.
		return ret; }

	/** Adds the given Item after the current Object to the Container. */
	public ReverseEnumerator addCurr(Object Item) throws ModificationException {
		return addAt(curr, Item); }

	/**Replaces the current Item in the Enumerator with the given one	 */
	public Object replaceCurr(Object Item) { //throws ModificationException {
		Object ret = Array.get(arr, curr);
		Array.set(arr, curr, Item);
		return ret; }

	/**Removes the current Item from the Enumerator	 */
	public Object removeCurr() { //throws ModificationException {
		Object ret = Array.get(arr, curr);
		System.arraycopy(	arr, curr + 1,
							arr, curr, Length - curr);
		--Length; return ret; }

	/** Adds the given Item after the current Object to the Container. */
	public ReverseEnumerator addPrev(Object Item) throws ModificationException {
		System.arraycopy(	arr, curr - 1,
							arr, curr, Length - curr + 1);
		Array.set(arr, curr, Item); ++Length;
		return this; }

	/**Replaces the current Item in the Enumerator with the given one	 */
	public Object replacePrev(Object Item) { //throws ModificationException {
		Object ret = Array.get(arr, curr - 1);
		Array.set(arr, curr - 1, Item);
		return ret; }

	/**Removes the current Item from the Enumerator	 */
	public Object removePrev() throws ModificationException {
		Object ret = Array.get(arr, curr);
		System.arraycopy(	arr, curr,
							arr, curr - 1, Length - curr + 1);
		--Length; return ret; }

	/** Adds the given Item after the current Object to the Container. */
	public Enumerator addNext(Object Item) throws ModificationException {
		System.arraycopy(	arr, curr + 1,
							arr, curr + 2, Length - curr - 1);
		Array.set(arr, curr, Item); ++Length;
		return this; }

	/**Replaces the current Item in the Enumerator with the given one	 */
	public Object replaceNext(Object Item) { //throws ModificationException {
		Object ret = Array.get(arr, curr+1);
		Array.set(arr, curr+1, Item);
		return ret; }

	/**Removes the current Item from the Enumerator	 */
	public Object removeNext() throws ModificationException {
		Object ret = Array.get(arr, curr);
		System.arraycopy(	arr, curr + 2,
							arr, curr + 1, Length - curr - 1);
		--Length; return ret; }

	/** Returns a new Intstance of an alterable Iterator ,
	  * which allows for changing the Data and structure concurrently. */
	public Enumerator Enumerator() { //return null; }
		return new ArrayEnumPrim(arr); }

	/** Returns a new Intstance of a ChangeIterator,
	  * which allows for changing the Data concurrently. */
	public ChangeIterator ChangeIterator() { return new ArrayEnumPrim(arr); }

	/** Returns the Order in which Elements are returned or processed.	 */
	public byte getOrder() { return IPipe.ORDER_QUEUE; }

////////////////////////////////////////////////////////////////////////////////
//  Optimizations
////////////////////////////////////////////////////////////////////////////////

	/**Returns the minimum Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number. */
	public long availAble() { return  Length - curr - 1; }

}
