/*
 * Created on 05.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import streamIO.real.IStreamIn_Float;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Defines the Interface for an Input Stream that can parse primitive Data Types. 
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
 */
public interface IStreamIn_Primitive 
extends IStreamIn_Int, IStreamIn_Float, IStreamIn_Char {//, IStreamIn {
	
	/** return the next boolean Value from this Stream
	 * @return the next boolean Value from this Stream 
	 */
	public boolean nextBool(); 
	
}
