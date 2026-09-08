/*
 * Created on 26.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

/**
 * Defines the minimal contract for writing signed integer and long values to an output stream.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: 4}
 * -->
 */
public interface IStreamOutInt {
	
	/** Writes the specified Value to this output stream.	  */
	public IStreamOutInt addInt(final int b);
	
	/** Writes the specified Value to this output stream.	  */
	public IStreamOutInt addLong(final long b);
	
}
