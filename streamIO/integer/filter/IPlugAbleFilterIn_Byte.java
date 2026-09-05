/*
 * Created on 02.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.filter;

import java.io.InputStream;

import streamIO.integer.IStreamIn_Byte;

/**
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:42:30Z
 * digest: 7b23717e6f6e3cbbc50ce079a3aad28278c82a658b0e7b49d466d064525e8530
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IPlugAbleFilterIn_Byte 
extends IStreamIn_Byte {

	/** 
	 * set the Input streamIO to filter
	 * @param stream the new Input Sream
	 */
	public void setStreamIn_(IStreamIn_Byte stream);

	/** 
	 * set the Input streamIO to filter
	 * @param stream the new Input Sream
	 */
	public void setStreamIn_(InputStream stream); 
	
}
