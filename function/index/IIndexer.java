/*
 * Created on 15.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.index;

/**
 * Title: <p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
public interface IIndexer 
extends IIndex {

	/** 
	 * @param arg the Object to retrieve the Index for. 
	 * @param ndx the Index to remember for the given Object. 
	 * @return the previous Index of arg, if it had one, -1 otherwise  
	 */
	public int setIndexOf(final Object arg, final int ndx); 
	
}
