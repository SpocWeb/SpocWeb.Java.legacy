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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: c5b8d6fcded01676fc8d5b53d40b37afa842e01fe67f02c8822f478de0c9e163
 * stale: false
 * tags: [code/stream_positioning]
 * concepts: [Push-Back Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
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
