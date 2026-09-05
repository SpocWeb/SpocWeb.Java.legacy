/*
 * Created on 20.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.real;

import function.byref.ByRefDouble;

/**
 * Caches the current double/float value in a {@link ByRefDouble} so subclasses only need to
 * implement {@link #nextDoubleInternal()}.
 *
 * <p>Purpose:
 * Implements the Cacheing for the currFloat Value
 * and the currItem Return Object using a ByRefDouble.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:09:09Z
 * digest: 36dc9a025620ce10f845341da4f4ce536b136ff10e2883a899272c53b2b5e496
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Float Stream Input Base Class]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public abstract class AStreamIn_Float 
extends AAStreamIn_Float {

	////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Float: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
    /** Returns the number of items still available from this stream.
     * @see streamIO.IAvailAble#availAble()     */
    abstract public long availAble();

    /** Returns the maximum number of items that can be marked and reset.
     * @see streamIO.IMarkAble#getMaxMarkSize()     */
    abstract public long getMaxMarkSize();

    /** Returns the lower bound of the generated distribution.
     * @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()     */
    abstract public double getMinDouble();

    /** Returns the current position within this stream.
     * @see streamIO.IAvailAble#getPosition()     */
    abstract public long getPosition();

    /** Computes the next raw double value, before it is cached into {@link #currItem}.
     * @see streamIO.real.IStreamIn_Float#nextDouble()     */
    abstract protected double nextDoubleInternal();

	////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Default Implementations
	////////////////////////////////////////////////////////////////////////////

    /** Holds the value returned by the last {@link #nextItem()} or {@link #nextDouble()} call. */
    final public ByRefDouble currItem = new ByRefDouble();

    /** Returns the {@link #currItem} holder as this stream's current item.
     * @see streamIO.object.IStreamIn#currItem()     */
    final public Object currItem() { return currItem; }
    
	/**Returns the current Value that was returned from the last nextItem() Method.	 */
	public double currDouble() { return currItem.Value; } 
	
	/**Returns the current Value that was returned from the last nextItem() Method.	 */
	public float currFloat() { return (float) currItem.Value; } 
	
    /**Enforce Assignment to currItem! 
     * @see streamIO.IFactory#nextItem()     */
    final public Object nextItem() { 
    	if (EOS == (currItem.Value = nextDoubleInternal()))
    		return EOI; 
        return currItem; }
    
    /**Enforce Assignment to currItem! 
     * @see streamIO.real.IStreamIn_Float#nextDouble()     */
    final public double nextDouble() { 
    	return currItem.Value = nextDoubleInternal(); }
    
}
