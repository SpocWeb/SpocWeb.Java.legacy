package streamIO.real;

import streamIO.IReSetAble;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.object.IStreamIn;

/**
  * Title: FilterInFloatMul<p>
  * Description:
  * Multiplication with a fixed Scale Filter for Number Streams.
  * Implements an Optimization by precalculating the Scaling Factor
  * taking the Generator's MaxValue into Account
  * thus saving one float Point Multiplication
  * 
  * @see streamIO.real.FilterIn_FloatByFunction 
  * which applies a generic Function to the incoming Numbers
  *
  * Known SubClasses:
  * @see streamIO.real.FilterInLin
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class FilterInMul
extends AStreamIn_Float
implements IStreamIn_Float {
	
	/** Local Reference to the Generator	 */
	protected final IStreamIn_Bound_Int inStream;
	
	/** Scaling Factor taking the Generator's MaxValue into Account	 */
	protected final double scale;
	
	/** @return the Scaling Factor taking the Generator's MaxValue into Account	 */
	public double getScale() { return scale; }
	
	/** Initializing Constructor
	  * defaulting the maximum Value to 1. 
	  * @param Generator the actual IStreamInNumber 	*/
	public FilterInMul(final IStreamIn_Bound_Int generator_) { this(generator_, 1); }
	
	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber 	*/
	protected FilterInMul(final double _scale, final IStreamIn_Bound_Int _generator) { 
		this.inStream = _generator;
		this.scale = _scale; }
	
	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber
	  * @param maxValue  the Maximum Value to be returned by this Filter */
	public FilterInMul(final IStreamIn_Bound_Int _generator, final double maxValue) {
		final double maxVal = _generator.getMaxValue();	//check whether a Float Random Generator is being used!
		//if (maxVal == 1) { 
		//	throw new AbstractMethodError("This Filter is redundant! the Factor is 1!"); } 
		this.inStream = _generator;
		this.scale = maxValue / maxVal; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(final long position) {
		resetVersion(null); 
		return inStream.reSet(position); 
	}
	
	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { inStream.reSet(); return this; }
	
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() {
		final byte ret = inStream.getOrder();
		if(((ret == IStreamIn.ORDER_DESC)  ||
			(ret == IStreamIn.ORDER_ASC )) && (scale < 0)) {
			return (byte) -ret; }
		return ret; }
	
	/** @return the next Random double Precision Number	 */
	public double nextDoubleInternal() { return scale*inStream.nextInt(); }
	
	/** @see streamIO.real.IStreamIn_Float#Iterator()	 */
	public IStreamIn_Float FloatIterator() {
		return new FilterInMul(this.scale, (IStreamIn_Bound_Int) inStream.IntIterator());
	}
	
	/** @see streamIO.real.AStreamIn_Float#availAble()	 */
	public long availAble() { return inStream.availAble(); }

	/** @see streamIO.real.AStreamIn_Float#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return inStream.getMaxMarkSize(); }

	/** @see streamIO.real.AStreamIn_Float#getMinDouble()	 */
	public double getMinDouble() { return scale*inStream.getMinValue(); }

	/** @see streamIO.real.AStreamIn_Float#getPosition()	 */
	public long getPosition() { return inStream.getPosition(); }

}
