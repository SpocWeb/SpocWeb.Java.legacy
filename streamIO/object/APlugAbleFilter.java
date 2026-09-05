/*
 * Created on 02.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object;

import java.io.FileNotFoundException;
import java.io.IOException;

import streamIO.AStreamOut;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IPlugAbleFilter;
import streamIO.IPlugAbleFilterIn;
import streamIO.IPlugAbleFilterOut;
import streamIO.StreamOutPrimitive;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.file.FileStreamIn_Byte;
import streamIO.integer.file.FileStreamOutByte;
import streamIO.object.filterInOut.FilterByFunction;
import streamIO.object.parser.InputStream2StreamIn;
import streamIO.object.parser.StreamInFromParser;
import function.AFunction;

/**
 * Abstract base for a {@link IPlugAbleFilter} whose upstream or downstream leg can be swapped
 * out after construction, passing the substitution through any nested pluggable filter chain.
 * <p>
 * Title: APlugAbleFilter<p>
 * Description:
 * Implements a pluggable / configurable Filter for Object Streams.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:30:02Z
 * digest: 2b696bef2127950a76d2fcb4b0f0040cd8667994ba74d4158631af356be29c8c
 * stale: false
 * tags: [code/stream_processing, code/iterator]
 * concepts: [Object Stream Pipeline]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public abstract class APlugAbleFilter 
extends AFilter 
implements IPlugAbleFilter {
    
	///////////////////////////////////////////////////////////////////////////
	/// generic Filter Methods for pluggable Filters
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Universal synchronous Filter Engine: filters the given Input into the given Output
	 * 
	 * @param in_File
	 * @param sep The String of Separators starting with the Escape Character
	 * @param filter
	 * @param outFile
	 * @throws FileNotFoundException
	 */
	public static long FILTER(final String in_File, final String sep, 
			final IPlugAbleFilterIn filter, final String outFile) 
	throws FileNotFoundException, IOException {
		final FileStreamOutByte out = new FileStreamOutByte(outFile);
		final IStreamIn_Byte in_ = new FileStreamIn_Byte(in_File);
		final InputStream2StreamIn scanner = new InputStream2StreamIn(in_, sep); 
		final StreamInFromParser parser = new StreamInFromParser(scanner); 
		final FilterByFunction copier = new FilterByFunction(parser, AFunction.TO_STRING_FUNCTION); filter.setStreamIn_(copier); 
		final StreamOutPrimitive serializer = new StreamOutPrimitive(out, sep.substring(1));
		return AStreamOut.STREAM(filter, serializer); 
	}
	
	/** 
	 * Universal synchronous Filter Engine: filters the given Input into the given Output
	 * 
	 * @param in_File
	 * @param sep The String of Separators starting with the Escape Character
	 * @param filter
	 * @param outFile
	 * @throws FileNotFoundException
	 */
	public static long FILTER(final String in_File, final String sep, 
			final IPlugAbleFilterOut filter, final String outFile) 
	throws FileNotFoundException, IOException {
		final FileStreamOutByte out = new FileStreamOutByte(outFile);
		final IStreamIn_Byte in_ = new FileStreamIn_Byte(in_File);
		final InputStream2StreamIn scanner = new InputStream2StreamIn(in_, sep); 
		final StreamInFromParser parser = new StreamInFromParser(scanner);
		final FilterByFunction copier = new FilterByFunction(parser, AFunction.TO_STRING_FUNCTION); 
		final StreamOutPrimitive serializer = new StreamOutPrimitive(out, sep.substring(1)); 
		filter. setStreamOut(serializer); 
		return AStreamOut.STREAM(copier, filter); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public APlugAbleFilter() { super((IIStreamOut) null); }
	
	/**
	 * Creates a pluggable filter that writes to the given output.
	 *
	 * @param out_ the initial output leg
	 */
	public APlugAbleFilter(final IIStreamOut out_) { super(out_); }

	/**
	 * Creates a pluggable filter that reads from the given input.
	 *
	 * @param enum_ the initial input leg
	 */
	public APlugAbleFilter(final IIStreamIn enum_) { super(enum_); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * To be able to plug a pre-assembled FilterOut into a Stream, 
	 * it has to be passed through the whole Filter Chain. 
	 * @see streamIO.IPlugAbleFilterOut#setStreamOut(streamIO.IStreamOut)	 */
	public void setStreamOut(final IIStreamOut stream) { 
		//if (out == null) 
		//	out = stream; 
		if (out instanceof IPlugAbleFilterOut) //pass it through the whole Filter Chain
			((IPlugAbleFilterOut) out).setStreamOut(stream);
		else
			out = stream; 
	}
	
	/**
	 * To be able to plug a pre-assembled FilterIn into a Stream, 
	 * it has to be passed through the whole Filter Chain. 
	 * @see streamIO.IPlugAbleFilterIn#setStreamIn_(streamIO.IStreamIn)	 */
	public void setStreamIn_(final IIStreamIn stream) { 
		//if (in == null) 
		//	in = stream; 
		if (in instanceof IPlugAbleFilterIn) //pass it through the whole Filter Chain
			((IPlugAbleFilterIn) in).setStreamIn_(stream);
		else
			in = stream; 
	}
	
}
