package streamIO.integer.random;

import streamIO.object.IStreamIn;

public abstract class ARandomInt
extends AStreamIn_BoundInt {
	
	/** @see streamIO.integer.AStreamIn_Int#nextLongInternal()	 */
	abstract protected long nextLongInternal(); 
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Default Implementations. 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!   
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return maxValue; }
	//abstract public long availAble(); //{ return maxValue; }
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) { return currItem.Value = _seed; }
	//abstract public long reSet(final long _seed); 
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet()  
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return currItem.Value; } 
	//abstract public long getPosition(); // { return currItem.Value; } 
	// not applicable to all Generators!
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor
	  * enforces Initialization of MaxValue */
	public ARandomInt (final int _maxValue) { super(_maxValue); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** randomizes this Stream by the current Time 	 */
	public ARandomInt randomize() { 
		reSet(System.currentTimeMillis()); 
		return this; }
	
	/**Random double Precision Number	 */
	public double nextDouble() { return ((double) nextLong())/maxValue; }
	
	/**Random single Precision Number	 */
	public float nextFloat() { return ((float) nextLong())/maxValue; }
	
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return IStreamIn.ORDER_RANDOM_PSEUDO; }
	
	/** @see streamIO.integer.AStreamIn_Bound#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
	
}
