/*
 * Created on 30.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Separates the Character & Encoding Related Methods 
 * from the purely Byte-oriented Methods of IStreamIn_Byte 
 * TODO: move some of the Methods in IStreamIn_Byte here. 
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
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public interface IStreamIn_Char 
extends IStreamIn_Byte {
	
	/** return the wrapped IStreamOutByte Instance (or itself) 
	 * @return the wrapped IStreamOutByte Instance (or itself) 
	 * for faster reading of Characters that don't need Encoding.  
	 * @see IStreamOutChar#getStreamOutByte()
	 */
	public IStreamIn_Byte getStreamIn_Byte(); 
	
	/** return the next Character from this Stream
	 * @return the next Character from this Stream 
	 */
	public char nextChar(); 
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods of java.io.Reader
	///////////////////////////////////////////////////////////////////////////
	
    /**
     * Read a single character.  This method will block until a character is
     * available, an I/O error occurs, or the end of the stream is reached.
     *
     * <p> Subclasses that intend to support efficient single-character input
     * should override this method.
     *
     * @return     The character read, as an integer in the range 0 to 65535
     *             (<tt>0x00-0xffff</tt>), or -1 if the end of the stream has
     *             been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
	abstract public int read() throws IOException; 
	/*	char cb[] = new char[1];
		if (read(cb, 0, 1) == -1)
		    return -1;
		else
		    return cb[0];
    }*/
	
    /**
     * Read characters into an array.  This method will block until some input
     * is available, an I/O error occurs, or the end of the stream is reached.
     *
     * @param       cbuf  Destination buffer
     *
     * @return      The number of characters read, or -1 
     *              if the end of the stream
     *              has been reached
     *
     * @exception   IOException  If an I/O error occurs
     */
    abstract public int read(char cbuf[]) throws IOException;
    	//return read(cbuf, 0, cbuf.length); }

    /**
     * Read characters into a portion of an array.  This method will block
     * until some input is available, an I/O error occurs, or the end of the
     * stream is reached.
     *
     * @param      cbuf  Destination buffer
     * @param      off   Offset at which to start storing characters
     * @param      len   Maximum number of characters to read
     *
     * @return     The number of characters read, or -1 if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    abstract public int read(char cbuf[], int off, int len) throws IOException;
    
	///////////////////////////////////////////////////////////////////////////
	/// Reading into StringBuffers
	///////////////////////////////////////////////////////////////////////////
	
}
