/*
 * Created on 28.03.2006
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
 * Defines the Interface for binary Encoders (variable- or fixed-Length) 
 * of 2-Byte Characters to 1-Byte Streams. 
 * 
 * Examples are UTF-8, UTF-16, ASCII, LATIN-1, Base64, UUEncoding etc. 
 * Encoding costs Performance and can be avoided when the Characters written
 * are known in advance (e.g. Numbers or Separator Characters) 
 * Other, more readable Encodings can be employed like \xXX or Unicode Escaping!  
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
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public interface IStreamOutChar 
extends IStreamOutByte {
	
	/** return the wrapped IStreamOutByte Instance (or itself) 
	 * @return the wrapped IStreamOutByte Instance (or itself) 
	 * for faster writing of Characters that don't need Encoding.  
	 */
	public IStreamOutByte getStreamOutByte(); 
	
	/** return this Stream to be able to append more Characters
	 * @return this Stream to be able to append more Characters
	 * @param chr the Character to append to this Stream 
	 */
	public IStreamOutChar addChar(final char chr); 
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods of java.io.Writer
	///////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @see write(byte[], int, int)
	  * @see java.io.Writer#write(char[])
	  */
	public abstract void write(final char[] b) throws IOException;
	
	/**
	  * Writes len Characters from the specified byte array starting at offset off
	  * to this output stream.
	  * The general contract for write(b, off, len) is
	  * that some of the Characters in the array b are written to the output stream in order;
	  * element b[off      ] is the first Character written
	  * and     b[off+len-1] is the  last Character written by this operation.
	  * The write method of OutputStream calls the write method of one argument
	  * on each of the bytes to be written out.
	  * Subclasses are encouraged to override this method
	  * and provide a more efficient implementation.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If off is negative, or len is negative,
	  * or off+len is greater than the length of the array b,
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param off - the start offset in the data.
	  * @param len - the number of bytes to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  * @see java.io.Writer#write(char[])
	  */
	public abstract void write(final char[] b, final int off, final int len) throws IOException;
	
	/**
	  * Writes b.length Characters from the specified String to this output stream.
	  * Only the lower Byte (8 Bits) of the Characters are being written!
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  * @see java.io.Writer#write(String)
	  */
	public abstract void write(final String b) throws IOException;

	/**
	  * Writes len Characters from the specified String to this output stream, 
	  * starting at Offset off. 
	  * The general contract for write(b, off, len) is
	  * that some of the Characters in the array b are written to the output stream in order;
	  * element b[off      ] is the first Character written
	  * and     b[off+len-1] is the  last Character written by this operation.
	  * The write method of OutputStream calls the write method of one argument
	  * on each of the bytes to be written out.
	  * Subclasses are encouraged to override this method
	  * and provide a more efficient implementation.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If off is negative, or len is negative,
	  * or off+len is greater than the length of the array b,
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param off - the start offset in the data.
	  * @param len - the number of bytes to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  * @see java.io.Writer#write(String)
	  */
	public abstract void write(final String b, final int off, final int len) throws IOException;
	
	///////////////////////////////////////////////////////////////////////////
	/// Writing out StringBuffers
	///////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes b.length Characters from the specified StringBuffer to this output stream.
	  * Only the lower Byte (8 Bits) of the Characters are being written!
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public abstract IStreamOutChar addBuffer(final StringBuffer b);
	
	/**
	  * Writes the Characters from start to stop-1 of the specified StringBuffer 
	  * to this output stream.  
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If start or stop is negative, 
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param start - the start offset in the data.
	  * @param stop  - the last byte to write.
	  */
	public abstract IStreamOutChar addBuffer(final StringBuffer b, final int stop);
	
	/**
	  * Writes the Characters from start to stop-1 of the specified StringBuffer 
	  * to this output stream.  
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If start or stop is negative, 
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param start - the start offset in the data.
	  * @param stop  - the last byte to write.
	  */
	public abstract IStreamOutChar addBuffer(final StringBuffer b, final int stop, final int start);
	
	/**
	  * Writes b.length Characters from the specified StringBuffer to this output stream.
	  * Only the lower Byte (8 Bits) of the Characters are being written!
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(String, int, int)
	  */
	public abstract IStreamOutChar addString(final String b);
	
	/**
	  * Writes the Characters from start to stop-1 of the specified StringBuffer 
	  * to this output stream.  
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If start or stop is negative, 
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param start - the start offset in the data.
	  * @param stop  - the last byte to write.
	  */
	public abstract IStreamOutChar addString(final String b, final int stop);
	
	/**
	  * Writes the Characters from start to stop-1 of the specified StringBuffer 
	  * to this output stream.  
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If start or stop is negative, 
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param start - the start offset in the data.
	  * @param stop  - the last byte to write.
	  */
	public abstract IStreamOutChar addString(final String b, final int stop, final int start);
	
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract IStreamOutChar escapeString(final String b, final String separators);
	
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract IStreamOutChar escapeString(final StringBuffer b, final String separators);
	
}
