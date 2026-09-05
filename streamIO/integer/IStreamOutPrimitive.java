/*
 * Created on 05.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import streamIO.real.IStreamOutFloat;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Defines the Interface for an Output Stream 
 * that can write primitive Data Types and Strings 
 * (Objects only via their toString() Method). 
 *
 * Design Decisions / Implementation Details:
 * 	IStreamOutChar is not separated, but merged into this Interface
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
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public interface IStreamOutPrimitive //merged with 	IStreamOutChar
extends IStreamOutInt, IStreamOutFloat, IStreamOutChar { //, IStreamOut, IStreamOutByte 
	
	/** return the wrapped IStreamOutByte Instance (or itself) 
	 * @return the wrapped IStreamOutByte Instance (or itself) 
	 * for faster writing of Characters that don't need Encoding.  
	 * @see IStreamOutChar#getStreamOutByte()
	 */
	//public IStreamOutByte getStreamOutByte(); 
	
	/** return this Stream to be able to append more Values 
	 * @return this Stream to be able to append more Values 
	 * @param value the boolean Value to append to this Stream 
	 */
	public IStreamOutPrimitive addBool(final boolean value); 
	
	/*
	public IStreamOutChar addChar(final char[] cBuf); 
	public IStreamOutChar addChar(final char[] cBuf, final int offset, final int length); 
	public IStreamOutChar addString(final String str); 
	public IStreamOutChar addChar(final String str, final int offset, final int length);
	*/ 
}
