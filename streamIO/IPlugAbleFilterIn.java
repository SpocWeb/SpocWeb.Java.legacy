/*
 * Created on 02.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO;


/**
 * Title: IConfigFilter<p>
 * Description:
 * Purpose:
 * A Filter whose Input Stream can be replaced during Runtime. 
 * This is necessary for a pluggable Architecture. 
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
 * @author mheuer
 * @version	1.0
 *
 */
public interface IPlugAbleFilterIn 
extends IIStreamIn {
	
	/** 
	 * set the Input streamIO to filter
	 * @param stream the new Input Sream
	 */
	public void setStreamIn_(final IIStreamIn stream);
	
}
