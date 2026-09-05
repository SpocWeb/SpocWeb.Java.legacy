/*
 * Created on 18.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO;


/**
 * Title: <p>
 * Description:
 * Purpose:
 * Abstract Base Class for all IMarkAble Implementations. 
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
 * mtime: 2026-09-05T10:53:28Z
 * digest: e280fa8aa16ed661e26d16beb80f824e030f4919119df5098267a427cc87c26b
 * stale: false
 * tags: [code/stream_positioning]
 * concepts: [Mark/Reset Base Class]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public abstract class AMarkAble 
extends AReSetAble 
implements IMarkAble {
    
    /**
     * tries to mork the given Object
     * @param arg the Stream to reSet
     * @return null if this Object does not implement IReSetAble or reSet() failed
     */
    final static public IMarkAble TRY_TO_MARK(final Object arg) { //, final String throwExceptionMessage) {
		if (arg instanceof IMarkAble) 
		    return ((IMarkAble) arg).mark(); 
		return null;
    }
    
    /**
     * tries to mork the given Object
     * @param arg the Stream to reSet
     * @return null if this Object does not implement IReSetAble or reSet() failed
     */
    final static public long GET_MAX_MARK(final Object arg) { //, final String throwExceptionMessage) {
		if (arg instanceof IMarkAble) 
		    return ((IMarkAble) arg).getMaxMarkSize(); 
		return -1;
    } 
    
    /////////////////////////////////////////////////////////////////////////////////////
    
    /** Returns the (minimum) Number of Items left, delegating to the concrete Stream Implementation.
     * @see streamIO.IAvailAble#availAble()     */
    abstract public long availAble();

    /** Returns the current Position in the Stream, delegating to the concrete Stream Implementation.
     * @see streamIO.IAvailAble#getPosition()    */
    abstract public long getPosition();

    /** Returns the maximum allowed readLimit for {@link #mark(long)}, delegating to the concrete Stream Implementation.
     * @see streamIO.IMarkAble#getMaxMarkSize()     */
    abstract public long getMaxMarkSize();
    
    /////////////////////////////////////////////////////////////////////////////////////
    
    /** Marked Position 	 */
    protected long mark; 
    
    /**Default Implementation
     * Should reserve the Space necessary for the given ReadLimit. 
     * @see streamIO.IMarkAble#mark(long)
     */
    public IMarkAble mark(final long readLimit) {
    	if (readLimit > getMaxMarkSize())
    		throw new RuntimeException("readLimit="+readLimit+" > getMaxMarkSize()="+getMaxMarkSize()); 
    	mark = getPosition(); 
    	return this; 
    }
    
    /** Marks the current Position using the maximum allowed readLimit.
     * @see streamIO.IMarkAble#mark()     */
    public IMarkAble mark() { return mark(getMaxMarkSize()); }
    
}
