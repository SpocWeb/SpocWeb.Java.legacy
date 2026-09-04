/*
 * Created on 25.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Defines the Interface for a generic (untyped) Stream with PushBack Functionality. 
 *
 * Known SubClasses: 
 * @see streamIO.IReSetAble
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public interface IPushBackAble {

	/** 
	 * Pushes the given Value back into this Iterator.
	 * Typically pushing back works only for a single Item, 
	 * as indicated by the Return Value of this Method.
	 * Otherwise pushing is a Stack Operation i.e. LIFO 
	 * 
     * Equivalent to skip(-1); 
	 * @return this Stream if jumping worked, null otherwise. 
	 */
    public IPushBackAble pushBack(); 
    
}
