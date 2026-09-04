/*
 * File Name: ArrayStreamIn_Float.java
 * Created on: 10.02.2004
 *
 */
package streamIO.real;

import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.object.IStreamIn;

/**
 * Title: ArrayStreamIn_Float<p>
 * Description:
 * Presents an Array in the Form of a resettable Stream. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * 
 * Similar Classes: 
 * @see streamIO.object.ArrayStreamIn
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class ArrayStreamIn_Float 
extends AStreamIn_Float
implements StreamIn_Float {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** current Position in the Stream	 */
	protected int curr = -1; 
	
	/** Local Cache for the Value mark()ed	 */
	protected int mark = -1;
	
	/** Local Cache for the maximum Index	 */
	protected int max = -1;
	
	/** Local Cache for the Order of the Array	 */
	protected byte order = IStreamIn.ORDER_NONE;
	
	/** @see streamIO.real.IStreamIn_Float#getOrder()	 */
	public byte getOrder() { return order; }

	/** Data Repository 	 */
	protected final float[] arrFloat; 
	
	/** Data Repository 	 */
	protected final double[] arrDouble; 
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////////
	
	/** initializing Constructor	 */
	public ArrayStreamIn_Float(final float[] arr_) {
		this(arr_, 0, arr_.length); }

	/** initializing Constructor	 */
	public ArrayStreamIn_Float(final double[] arr_) {
		this(arr_, 0, arr_.length); }

	/** initializing Constructor	 */
	public ArrayStreamIn_Float(final float[] arr_, final int start, final int stop) {
		this.arrDouble = null; 
		this.arrFloat = arr_;
		this.mark = start-1;
		this.max = stop-1;
	}

	/** initializing Constructor	 */
	public ArrayStreamIn_Float(final double[] arr_, final int start, final int stop) {
		this.arrDouble = arr_; 
		this.arrFloat = null;
		this.mark = start-1;
		this.max = stop-1;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////

    /** @see streamIO.real.AStreamIn_Float#availAble()     */
    public long availAble() { return getMaxMarkSize()-curr; }

    /** @see streamIO.real.AStreamIn_Float#getMaxMarkSize()     */
    public long getMaxMarkSize() {
        if (arrFloat != null) 
            return arrFloat.length;
        return arrDouble.length;
    }

    /** @see streamIO.real.AStreamIn_Float#getPosition()     */
    public long getPosition() { return curr; }
    
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources 
	 */
	public IMarkAble mark(final long readLimit) { //throws NoSuchMethodException {
		mark = curr; 
		return this; }
	
	/** @see streamIO.real.StreamIn_Float#reSet()	 */
	public IReSetAble reSet() { curr = mark; return this; }

	/** @see streamIO.real.IStreamIn_Float#nextFloat()	 */
	protected float nextFloatInternal() { 
		if (arrFloat != null) {
			if (++curr >= max) {
				return EOS; }
			return arrFloat[curr]; 
		} 
		return (float) nextDoubleInternal(); 
	}

	/** @see streamIO.real.IStreamIn_Float#nextDouble()	 */
	protected double nextDoubleInternal() {
		if (arrDouble != null) {
			if (++curr >= max) {
				return EOS; }
			return arrDouble[curr]; 
		} 
		return nextFloatInternal(); 
	}
	
	/** @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()	 */
	public double getMinDouble() {
		if (arrDouble != null)
			return VectorDouble.MIN_VAL(arrDouble);
		return VectorFloat.MIN_VAL(arrFloat);
	}

}
