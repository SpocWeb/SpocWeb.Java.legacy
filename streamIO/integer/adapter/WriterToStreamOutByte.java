/**
 * File  Name: WriterToStreamOutByte.java
 * Created on: 27.02.2003
 */
package streamIO.integer.adapter;

import java.io.IOException;
import java.io.Writer;

import streamIO.integer.AStreamOutByte;
import streamIO.integer.AStreamOutChar;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.IStreamOutChar;
import tools.IOError;

/**
 * Title: WriterToStreamOutByte<p>
 * Description:
 * Adapter that wraps any Writer Interface into an IStreamOutByte Interface
 *
 * Design Decisions / Implementation Details:
 * Bytes are created simply by truncating the Words. 
 * Encodings are rather solved by Encoding Filters in streamIO.Byte.Encoding. 
 * 
 * Just like with InputStream and OutputStream, most Methods have the same Signature
 * as the Reader and Writer Methods, so a StreamIn_Byte 
 * can be quickly derived instead of being wrapped (slower but more flexible) 
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
 */
public class WriterToStreamOutByte 
extends AStreamOutChar
implements IStreamOutChar {
	
	/** Reference to the Writer Object being wrapped */
	protected Writer writer; 
	
	/** Constructor for WriterToStreamOutByte.	 */
	public WriterToStreamOutByte(Writer writer_) { this.writer = writer_; }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(int)	 */
	public void write(int b) throws IOException { writer.write(b); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(byte[])	 */
	public void write(byte[] b) throws IOException { write(b, 0, b.length); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(byte[], int, int)	 */
	public void write(byte[] b, int off, int len) throws IOException {
		final int stop = off+len; 
		for (int i = off-1; ++i < stop;) {
			writer.write(b[i]);
		}
	}
	
	/** @see streamIO.Byte.IStreamOutByte#addString(char[], int, int)	 */
	public void write(char[] b, int off, int len) throws IOException {
		writer.write(b, 0, b.length); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(int[])	 */
	public void addItem(int[] b) throws IOException { addItem(b, 0, b.length); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(int[], int, int)	 */
	public void addItem(int[] b, int off, int len) throws IOException { 
		final int stop = off+len; 
		for (int i = off-1; ++i < stop;) {
			writer.write(b[i]);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// Interface IStreamOutChar
	/////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutChar#getStreamOutByte()	 */
	public IStreamOutByte getStreamOutByte() { return this;	} //.out; }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(String)	 */
	public void write(final String str) throws IOException { 
		writer.write(str); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(String, int, int)	 */
	public void write(final String str, final int off, final int len
			) throws IOException {
		writer.write(str, off, len); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(StringBuffer)	 */
	public IStreamOutChar addBuffer(final StringBuffer b) { 
		return addBuffer(b, b.length()); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(StringBuffer, int, int)	 */
	public IStreamOutChar addBuffer(final StringBuffer b, final int stop) {
		return addBuffer(b, stop, 0); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(StringBuffer, int, int)	 */
	public IStreamOutChar addBuffer(final StringBuffer b, final int stop, int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start); 
		return this; 
	}
	
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public IStreamOutChar escapeString(final String str, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, str, separators);
		return this; 
	}
	
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public IStreamOutChar escapeString(final StringBuffer str, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, str, separators);
		return this; 
	}
	
	/** @see streamIO.Byte.IStreamOutByte#flush()	 */
	public void flush() throws IOException { writer.flush(); }

	/** @see streamIO.Byte.IStreamOutByte#close()	 */
	public void close() throws IOException { writer.close(); }

	/** @see streamIO.integer.AStreamOutChar#addChar(char)	 */
	public IStreamOutChar addChar(final char chr) {
		try { writer.write(chr);
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }

}
