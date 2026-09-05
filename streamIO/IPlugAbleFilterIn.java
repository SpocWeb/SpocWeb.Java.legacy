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
 * A Filter whose upstream Input Stream can be swapped out at Runtime via setStreamIn_(),
 * instead of being fixed at Construction Time. This is necessary for a pluggable Architecture,
 * e.g. to reconnect a Filter Chain to a different Source without rebuilding it.
 * Counterpart to IPlugAbleFilterOut for the Output Side; combined by IPlugAbleFilter.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:41:24Z
 * digest: 89052db693d719b701aa04d1b164f7ae6b35e7a410f39ccfdfcea75f53515f78
 * stale: false
 * tags: [code/iterator]
 * concepts: [Pluggable Filter Input Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IPlugAbleFilterIn 
extends IIStreamIn {
	
	/** 
	 * set the Input streamIO to filter
	 * @param stream the new Input Sream
	 */
	public void setStreamIn_(final IIStreamIn stream);
	
}
