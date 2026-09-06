/*
 * Created on 23.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.adapter;

import math.vector.VectorInt;
import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.integer.AStreamIn_Int;
import streamIO.integer.IStreamIn_Int;
import streamIO.object.IStreamIn;

/**
 * Title: ArrayStreamIn_Int<p>
 * Description:
 * Purpose:
 * Provides the IStreamIn_Int Interface for int[] Arrays. 
 * 
 * Design Decisions / Implementation Details:
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 *
 * <!-- docstate
 * tags: [code/stream_adapter, code/stream_bridging, code/stream_wrapper]
 * concepts: [Bridges streamIO Interfaces to java.io and Arrays]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class ArrayStreamIn_Int 
extends AStreamIn_Int
implements IStreamIn_Int {

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** current Position in the Stream	 */
	protected int pos = -1; 
	
	/** Local Cache for the Value mark()ed	 */
	protected int mark = -1;
	
	/** Local Cache for the maximum Index	 */
	protected int max = -1;
	
	/** Local Cache for the Order of the Array	 */
	protected byte order = IStreamIn.ORDER_NONE;
	
	/** Returns the cached Order of the wrapped Array.
	 * @see streamIO.real.IStreamIn_Float#getOrder()	 */
	public byte getOrder() { return order; }

	/** Data Repository 	 */
	protected final int[] arrInt; 
	
	/** Data Repository 	 */
	protected final long[] arrLong; 
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////////
	
	/** initializing Constructor	 */
	public ArrayStreamIn_Int(final int[] arr_) {
		this(arr_, 0, arr_.length); }
	
	/** initializing Constructor	 */
	public ArrayStreamIn_Int(final long[] arr_) {
		this(arr_, 0, arr_.length); }
	
	/** initializing Constructor	 */
	public ArrayStreamIn_Int(final int[] arr_, final int start, final int stop) {
		this.arrLong = null; 
		this.arrInt = arr_;
		this.mark = start-1;
		this.max = stop-1;
	}
	
	/** initializing Constructor	 */
	public ArrayStreamIn_Int(final long[] arr_, final int start, final int stop) {
		this.arrLong = arr_; 
		this.arrInt = null;
		this.mark = start-1;
		this.max = stop-1;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////

    /** Returns the current read Position (Index) within the wrapped Array.
     * @see streamIO.real.AAStreamIn_Float#getPosition()     */
    public long getPosition() { return pos; }
    
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources 
	 */
	public IMarkAble mark(final long readLimit) {
		mark = pos; 
		return this; }
	
	/** Resets the Position to the last mark()ed Index.
	 * @see streamIO.real.StreamIn_Float#reSet()	 */
	public IReSetAble reSet() { pos = mark; return this; }

	/** Reads and returns the next raw {@code long} value from the wrapped Array.
	 * @see streamIO.integer.IStreamIn_Int#nextLong()	 */
	public long nextLongInternal() {
		if (arrInt != null) {
			if (++pos >= max) {
				return EOF; }
			return arrInt[pos];
		}
		if (++pos >= max) {
			return EOF; }
		return arrLong[pos];
	}
	
	/** Returns the minimum Value present in the wrapped Array.
	 * @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()	 */
	public double getMinDouble() {
		if (arrInt != null)
		return VectorInt.MIN_VAL(arrInt);
		return VectorInt.MIN_VAL(arrLong);
	}
	
    /** Returns the number of Items still available to read, based on the wrapped Array's length.
     * @see streamIO.real.AAStreamIn_Float#availAble()     */
    public long availAble() { return getMaxMarkSize()-pos; }

    /** Returns the length of the wrapped Array.
     * @see streamIO.real.AAStreamIn_Float#getMaxMarkSize()     */
    public long getMaxMarkSize() {
        if (arrLong != null)
            return arrLong.length; 
        return arrInt.length; 
    }

}
