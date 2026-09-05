package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.real.AStreamIn_Float;
import streamIO.real.FilterIn_FloatByFunction;
import streamIO.real.IStreamIn_Float;

/**
 * Base Class for most Float Random Number Generators. 
 * 
 * Similar Classes: 
 * @see FilterIn_FloatByFunction also derives from AStreamIn_Float
 * @see streamIO.integer.AStreamIn_Bound could also be a Base Class 
 * @see streamIO.integer.random.AStreamIn_BoundInt could also be a Base Class
 * @see streamIO.integer.random.ARandomInt 
 * @see streamIO.integer.random.ARandomLong
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:26:46Z
 * digest: 2dff81354ddb2f485559db3612aa44989cf948886042ae0e7e34ffbd23981ea9
 * stale: false
 * tags: [code/random_number_generator]
 * concepts: [Random Float Base Class]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public abstract class ARandomFloat
extends AStreamIn_Float
implements IStreamIn_Float {
	
	/** @see streamIO.real.AStreamIn_Float#nextDoubleInternal()	 */
	abstract protected double nextDoubleInternal();
	
	/** Returns the lower bound of the generated distribution.
	 * @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()	 */
	abstract public double getMinDouble(); //depends on the Type
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/**Local Reference to the Random Number Generator	 */
	protected final IStreamIn_Float ran;
	
	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() {
		ran.reSet(); 
		return this; }	
	
	/** initializing Constructor	 */
	public ARandomFloat(final IStreamIn_Float _ran) {
		this.ran = _ran; 
	}
	
	/** Returns the number of items still available from the wrapped generator.
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return ran.availAble(); }

	/** Returns the maximum mark size of the wrapped generator.
	 * @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return ran.getMaxMarkSize(); }

	/** Returns the current position of the wrapped generator.
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return ran.getPosition(); }
	
	/** Returns the sort order of the wrapped generator.
	 * @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return ran.getOrder(); }
	
	/** randomizes this Stream by the current Time 	 */
	public ARandomFloat randomize() { 
		ran.reSet(System.currentTimeMillis()); 
		return this; }
	
}
