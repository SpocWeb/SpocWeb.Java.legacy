package streamIO.object.enumer;

import java.security.InvalidParameterException;

import streamIO.object.ModificationException;
import streamIO.object.IPipe;
import streamIO.real.IStreamIn_Float;
import function.byref.ByRefDouble;

/**
 * Title:        ArrayEnumDbl <p>
 * Description:
 * Enumerator Class for Arrays of Type double
 * @see ArrayEnumPrimitive which is slower, but usable for other primitive Types
 * @see ArrayEnum     which is usable only for Object Types
 * @see StreamIn_Arithmetic  which is an integer Input streamIO only 
 * @see streamIO.integer.filter.FilterIn_Int2Object which converts Integers to Objects just like this Class
 *
 * Don't confuse it with the local ArrayEnumDbl Class in Array.Array.
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
public class ArrayEnumDbl
extends AIndexEnumerator
implements IStreamIn_Float {
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Local Reference to the Array to be iterated over	 */
	protected double[] arr;
	
	/** Local Cache for the Length of the Array to be iterated over
	  * modified on adding/removing Items.
	  * This differs from the physical Length of the Array which may be larger:
	  * Length <= arr.length */
	protected int length;
	
	/** Object being used to return the Value of the Interface StreamIn */
	protected ByRefDouble Value = new ByRefDouble();
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Initializing Constructor	 */
	public ArrayEnumDbl(final double[] _arr, final int maxLength) {
		super(null);
		this.arr = _arr;
		length = maxLength; 
		if (maxLength > _arr.length)
			throw new InvalidParameterException("maxLength="+maxLength+" > arr.length="+_arr.length); 
	}
	
	/**Initializing Constructor	 */
	public ArrayEnumDbl(final double[] arr) { this(arr, arr.length); }
	
	/** Creates a new independent Iterator over the same double Array at its Start.
	 * @see streamIO.real.IStreamIn_Float#FloatIterator()	 */
	public IStreamIn_Float FloatIterator() { return new ArrayEnumDbl(arr, length); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamInFloat: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**Random single Precision Number	 */
	public float nextFloat() { return (float) arr[++curr]; }
	
	/**Random double Precision Number	 */
	public double nextDouble() { return arr[++curr]; }
	
	/** Returns the current Element, unconverted.
	 * @see streamIO.real.IStreamIn_Float#currDouble()	 */
	public double currDouble() { return arr[curr]; }

	/** Returns the current Element, narrowed to float.
	 * @see streamIO.real.IStreamIn_Float#currFloat()	 */
	final public float currFloat() { return (float) currDouble(); }

	/** Returns the next Element without advancing, unconverted.
	 * @see streamIO.real.IStreamIn_Float#peekDouble()	 */
	public double peekDouble() { return arr[1+curr]; }

	/** Returns the next Element without advancing, narrowed to float.
	 * @see streamIO.real.IStreamIn_Float#peekFloat()	 */
	public float peekFloat() { return (float) peekDouble(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//	Interface StreamIn
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the cached used Length of the wrapped Array.
	  * @return the total Number of Objects in this Enumerator / Container
	  * For Random Access Stores this is definitely limited and can thus be returned.
	  */
	public int getInt() { return length; }

	/** Wraps the double at the given Position in the reusable {@link #Value} holder.
	  * @return the Item at the given absolute Position
	  * @return SOI for negative Indices and EOI for Indices larger than the Size
	  * Could also use try/catch Block, but that is much more expensive!	 */
	public Object getAt(final int index) {
		if (index >= length) return EOI;
		if (index < 0) return SOI;
		Value.Value = arr[index];
		return Value; }

	/** Adds the given Item after the current Object to the Container. */
	public IndexEnumerator addAt(int Pos, Object Item) throws ModificationException {
		System.arraycopy(	arr, Pos - 1,
							arr, Pos, length - Pos + 1);
		arr[Pos] = ByRefDouble.GET_DOUBLE(Item); ++length;
		return this; }

	/** Replaces the current Item in the Enumerator with the given one	 */
	public Object setAt(int Pos, Object Item) { //throws ModificationException {
		Value.Value = arr[Pos];
		arr[Pos] = ByRefDouble.GET_DOUBLE(Item);
		return Value; }

	/** Removes the Object at the Position from the Container with this Enumerator knowing it. */
	public Object removeAt(int Pos) throws ModificationException {
		Value.Value = arr[Pos];
		System.arraycopy(	arr, Pos + 1,
							arr, Pos, arr.length - Pos);
		return Value; }

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
	public Object replaceCurr(Object Item) { //throws ModificationException {
		Value.Value = arr[curr];
		arr[curr] = ByRefDouble.GET_DOUBLE(Item);
		return Value; }

	/** Removes the current Object from the Container with this Enumerator knowing it. */
	public Object removeCurr() {
		Value.Value = arr[curr];
		System.arraycopy(	arr, curr + 1,
							arr, curr, arr.length - curr);
		return Value; }

	/** Adds the given Item after the current Object to the Container. */
	public ReverseEnumerator addPrev(Object Item) throws ModificationException {
		System.arraycopy(	arr, curr - 1,
							arr, curr, length - curr + 1);
		arr[curr] = ByRefDouble.GET_DOUBLE(Item); ++length;
		return this; }

	/** Replaces the current Item in the Enumerator with the given one	 */
	public Object replacePrev(Object Item) { //throws ModificationException {
		Value.Value = arr[curr - 1];
		arr[curr - 1] = ByRefDouble.GET_DOUBLE(Item);
		return Value; }

	/** Removes the current Item from the Enumerator	 */
	public Object removePrev() throws ModificationException {
		Value.Value = arr[curr];
		System.arraycopy(	arr, curr,
							arr, curr - 1, length - curr + 1);
		--length; return Value; }

	/** Adds the given Item after the current Object to the Container. */
	public Enumerator addNext(Object Item) throws ModificationException {
		System.arraycopy(	arr, curr + 1,
							arr, curr + 2, length - curr - 1);
		arr[curr+1] = ByRefDouble.GET_DOUBLE(Item); ++length;
		return this; }

	/** Replaces the current Item in the Enumerator with the given one	 */
	public Object replaceNext(Object Item) { //throws ModificationException {
		Value.Value = arr[curr+1];
		arr[curr+1] = ByRefDouble.GET_DOUBLE(Item);
		return Value; }

	/** Removes the current Item from the Enumerator	 */
	public Object removeNext() throws ModificationException {
		Value.Value = arr[curr];
		System.arraycopy(	arr, curr + 2,
							arr, curr + 1, length - curr - 1);
		--length; return Value; }

	/** Creates a new alterable Iterator over this Array.
	 * @return a new Intstance of an alterable Iterator ,
	  * which allows for changing the Data and structure concurrently. */
	public Enumerator Enumerator() { //return null; }
		return new ArrayEnumDbl(arr); }

	/** Creates a new ChangeIterator over this Array.
	 * @return a new Intstance of a ChangeIterator,
	  * which allows for changing the Data concurrently. */
	public ChangeIterator ChangeIterator() { return new ArrayEnumDbl(arr); }

	/** This Enumerator always returns Items in Queue (FIFO) Order.
	 * @return the Order in which Elements are returned or processed.	 */
	public byte getOrder() { return IPipe.ORDER_QUEUE; }

    /** removes the current Item (returned by the latest nextItem())
      * @return the current Item	 */
	//public Object removeCurr() { return Parent.removeCurr(); }

	////////////////////////////////////////////////////////////////////////////////
	//  Optimizations
	////////////////////////////////////////////////////////////////////////////////
	
	/** Computes the remaining Items from the cached Length and current Position.
	  * @return the minimum Number of Items left (in the Buffer).
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number. */
	public long availAble() { return length - curr - 1; } //-1 because of preIncrement
	
}
