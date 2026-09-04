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
    
    /** @see streamIO.IAvailAble#availAble()     */
    abstract public long availAble(); 
    
    /** @see streamIO.IAvailAble#getPosition()    */
    abstract public long getPosition(); 
    
    /** @see streamIO.IMarkAble#getMaxMarkSize()     */
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
    
    /** @see streamIO.IMarkAble#mark()     */
    public IMarkAble mark() { return mark(getMaxMarkSize()); }
    
}
