/*
 * Created on 03.09.2005
 *
 */
package streamIO.integer.filter;

import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.adapter.InputStreamToStreamIn_Byte;
import streamIO.integer.adapter.OutputStreamToStreamOutByte;

/**
 * Implements a pluggable / configurable Filter for Object Streams.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:42:18Z
 * digest: 526556ce15e6cad11561cb8cc8c6a2e8044708edff2b89908aaf6eb37cc41692
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class APlugAbleFilterByte 
extends FilterByte 
implements IPlugAbleFilterByte {
    
    /** Creates an instance with no Stream plugged in yet; both ends are set later via
     * {@link #setStreamOut(IStreamOutByte)}/{@link #setStreamIn_(IStreamIn_Byte)}. */
    public APlugAbleFilterByte() { super((IStreamOutByte) null); }

    /** Creates an instance delegating to the given Output Stream.
     * @param streamOut
     */
    public APlugAbleFilterByte(final IStreamOutByte streamOut) { super(streamOut); }

    /** Creates an instance delegating to the given Output Stream.
     * @param streamOut
     */
    public APlugAbleFilterByte(final OutputStream streamOut) { super(streamOut); }

    /** Creates an instance delegating to the given Input Stream.
     * @param streamIn_
     */
    public APlugAbleFilterByte(final IStreamIn_Byte streamIn_) { super(streamIn_); }

    /** Creates an instance delegating to the given Input Stream.
     * @param streamIn_
     */
    public APlugAbleFilterByte(final InputStream streamIn_) { super(streamIn_); }
    
    /**
	 * To be able to plug a pre-assembled FilterOut into a Stream, 
	 * it has to be passed through the whole Filter Chain. 
     * @see streamIO.integer.filter.IPlugAbleFilterOutByte#setStreamOut(streamIO.integer.IStreamOutByte)
     */
    public void setStreamOut(final IStreamOutByte stream) {
		if (streamOut instanceof IPlugAbleFilterOutByte) //pass it through the whole Filter Chain
			((IPlugAbleFilterOutByte) streamOut).setStreamOut(stream);
		else
		    streamOut = stream; 
    }
    
    /**
	 * To be able to plug a pre-assembled FilterOut into a Stream, 
	 * it has to be passed through the whole Filter Chain. 
     * @see streamIO.integer.filter.IPlugAbleFilterOutByte#setStreamOut(java.io.OutputStream)
     */
    public void setStreamOut(final OutputStream stream) {
		if (streamOut instanceof IPlugAbleFilterOutByte) //pass it through the whole Filter Chain
			((IPlugAbleFilterOutByte) streamOut).setStreamOut(stream);
		else
		    streamOut = new OutputStreamToStreamOutByte(stream); 
    }
    
    /**
	 * To be able to plug a pre-assembled FilterIn into a Stream, 
	 * it has to be passed through the whole Filter Chain. 
     * @see streamIO.integer.filter.IPlugAbleFilterIn_Byte#setStreamIn_(streamIO.integer.IStreamIn_Byte)
     */
    public void setStreamIn_(final IStreamIn_Byte stream) {
		if (streamIn instanceof IPlugAbleFilterIn_Byte) //pass it through the whole Filter Chain
			((IPlugAbleFilterIn_Byte) streamIn).setStreamIn_(stream);
		else
		    streamIn = stream; 
    }
    
    /**
	 * To be able to plug a pre-assembled FilterIn into a Stream, 
	 * it has to be passed through the whole Filter Chain. 
     * @see streamIO.integer.filter.IPlugAbleFilterIn_Byte#setStreamIn_(java.io.InputStream)
     */
    public void setStreamIn_(final InputStream stream) {
		if (streamIn instanceof IPlugAbleFilterIn_Byte) //pass it through the whole Filter Chain
			((IPlugAbleFilterIn_Byte) streamIn).setStreamIn_(stream);
		else
		    streamIn = new InputStreamToStreamIn_Byte(stream); 
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    /** Currently runs no demonstration; present as an entry point for manual testing. */
    public static void main(final String[] args) {
    }
    
}
