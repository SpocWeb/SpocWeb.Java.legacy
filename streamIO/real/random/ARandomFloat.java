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
 */
public abstract class ARandomFloat
extends AStreamIn_Float
implements IStreamIn_Float {
	
	/** @see streamIO.real.AStreamIn_Float#nextDoubleInternal()	 */
	abstract protected double nextDoubleInternal();
	
	/** @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()	 */
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
	
	/** @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return ran.availAble(); }
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return ran.getMaxMarkSize(); }
	
	/** @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return ran.getPosition(); }
	
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return ran.getOrder(); }
	
	/** randomizes this Stream by the current Time 	 */
	public ARandomFloat randomize() { 
		ran.reSet(System.currentTimeMillis()); 
		return this; }
	
}
