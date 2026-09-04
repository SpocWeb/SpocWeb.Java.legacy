/*
 * Created on 20.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.real;

import function.byref.ByRefDouble;

/**
 * Title: <p>
 * Description:
 * Purpose:
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
 */
public abstract class AStreamIn_Float 
extends AAStreamIn_Float {

	////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Float: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
    /** @see streamIO.IAvailAble#availAble()     */
    abstract public long availAble(); 
    
    /** @see streamIO.IMarkAble#getMaxMarkSize()     */
    abstract public long getMaxMarkSize(); 
    
    /** @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()     */
    abstract public double getMinDouble(); 
    
    /** @see streamIO.IAvailAble#getPosition()     */
    abstract public long getPosition(); 
    
    /** @see streamIO.real.IStreamIn_Float#nextDouble()     */
    abstract protected double nextDoubleInternal(); 
    
	////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Default Implementations
	////////////////////////////////////////////////////////////////////////////
	
    final public ByRefDouble currItem = new ByRefDouble(); 
    
    /** @see streamIO.object.IStreamIn#currItem()     */
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
