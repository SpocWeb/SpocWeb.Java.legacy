/*
 * File Name: IConfigFilterOut.java
 * Created on: 17.06.2003
 *
 */
package streamIO.integer.filter;

import java.io.OutputStream;

import streamIO.integer.IStreamOutByte;

/**
 * A Filter whose Output Stream can be replaced during Runtime.
 * This is necessary for a pluggable Architecture. 
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
 * mtime: 2026-09-05T21:42:34Z
 * digest: 4e98d386c98c46ee6ee7f11c2abb3358ed119ca97e2b153d1170fac89eb1bcbf
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IPlugAbleFilterOutByte 
extends IStreamOutByte {
	
	/**
	 * sets the Output streamIO to filter
	 * @param stream the new Output Sream
	 */
	public void setStreamOut(final IStreamOutByte stream);

	/**
	 * sets the Output streamIO to filter
	 * @param stream the new Output Sream
	 */
	public void setStreamOut(final OutputStream stream);

}
