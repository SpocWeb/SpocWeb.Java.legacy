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
 *
 * Purpose / Responsibilities of this Class
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
 */
abstract public class AStreamOutChar 
extends AStreamOutByte 
implements IStreamOutChar {
	
	////////////////////////////////////////////////////////////////////////////////
	/// Interface IStreamOutChar
	////////////////////////////////////////////////////////////////////////////////
		
	/** @see streamIO.integer.IStreamOutChar#addChar(char)	 */
	abstract public IStreamOutChar addChar(final char chr); 
	
	////////////////////////////////////////////////////////////////////////////////
	/// Interface IStreamOutByte
	////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutByte#write(int)	 */
	abstract public void write(final int b) throws IOException; 
	
	/** @see streamIO.IStreamOut#flush()	 */
	abstract public void flush() throws IOException; 
	
	/** @see streamIO.integer.IStreamOutByte#close()	 */
	abstract public void close() throws IOException; 
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamOutChar: Methods handling Characters and Strings
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  * @see IStreamOutByte#escapeString(String, String)
	  */
	public IStreamOutChar escapeString(final String str, final String separators) {
		ESCAPE_SAFE(this, str, separators);
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
	
	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	public void addItem(final int[] b) { addItem(b, 0, b.length); }
	  */

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
	public void addItem(final int[] b, final int off, final int len) {
		WRITE_SAFE(this, b, off, len); }
	  */

	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @see write(char[], int, int)
	  * @see streamIO.integer.IStreamOutChar#write(char[])
	  */
	public void write(final char[] b) throws IOException {
		write(b, 0, b.length); }

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
	  * @see streamIO.integer.IStreamOutChar#write(char[], int, int)
	  */
	public void write(final char[] b, final int off, final int len) throws IOException { 
		WRITE(this, b, off, len); }
	
	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public void write(final String b) throws IOException {
		write(b, 0, b.length()); }

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
	  * @see streamIO.integer.IStreamOutChar#write(java.lang.String, int, int)
	  */
	public void write(final String b, final int off, final int len) throws IOException {
		WRITE(this, b, off, len); }

	/** @see streamIO.integer.IStreamOutChar#addBuffer(java.lang.StringBuffer, int, int) 	 */
	public IStreamOutChar addBuffer(final StringBuffer b, final int stop, final int start) {
		WRITE_SAFE(this, b, stop, start); 
		return this; }
	
	/** @see streamIO.integer.IStreamOutChar#addBuffer(java.lang.StringBuffer, int) 	 */
	public IStreamOutChar addBuffer(final StringBuffer b, final int stop) {
		return addBuffer(b, stop, 0); }

	/** @see streamIO.integer.IStreamOutChar#addBuffer(java.lang.StringBuffer) 	 */
	public IStreamOutChar addBuffer(final StringBuffer b) {
		return addBuffer(b, b.length()); }
	
	/** @see streamIO.integer.IStreamOutChar#addString(java.lang.String) 	 */
	public IStreamOutChar addString(final String b) { return addString(b, b.length()); } 

	/** @see streamIO.integer.IStreamOutChar#addString(java.lang.String, int) 	 */
	public IStreamOutChar addString(final String b, final int stop) {
		return addString(b, stop, 0); } 

	/** @see streamIO.integer.IStreamOutChar#addString(java.lang.String, int, int) 	 */
	public IStreamOutChar addString(final String b, final int stop, final int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start); 
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// Static Testing & Main Methods
	////////////////////////////////////////////////////////////////////////////////
		
	public static void main(final String[] args) throws Exception {
	}
}
