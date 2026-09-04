package streamIO.integer; //TODO: always define a Package

import java.io.IOException;

import tools.IOError;

/**
  * Title: AStreamByte<p>
  * Description:
  * Abstract Class implementing most Methods
  * by delegating to the basic Methods of StreamInByte and IStreamOutByte
  *
  * Known SubClasses:
  *
  * Design Decisions:
  * Extending AStreamInByte because that implements more Methods.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-30-2002, 03:58 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class AStreamByte
extends AStreamIn_Byte
implements IStreamByte {
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Reads the next byte of data from the input stream.
	  * The value byte is returned as an int in the range 0 to 255.
	  * If no byte is available because the end of the stream has been reached,
	  * the value EOF is returned.
	  * This method blocks until input data is available,
	  * the end of the stream is detected, or an exception is thrown.
	  *
	  * A subclass must provide an implementation of this method.
	  *
	  * @return the next byte of data, or EOF if the end of the stream is reached.
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract int read() throws IOException; // { return streamIn.read(); }

	/**
	  * Reads the next long Value of data from the input stream.
	  * The value byte is returned as a long in the range MinLong to MaxLong .
	  * If no byte is available because the end of the stream has been reached,
	  * the value -1 is returned.
	  * This method blocks until input data is available,
	  * the end of the stream is detected, or an exception is thrown.
	  *
	  * A subclass must provide an implementation of this method.
	  *
	  * @return the next byte of data, or -1 if the end of the stream is reached.
	  * @throws IOException - if an I/O error occurs.
	  */ //declaring this abstract overwrites the Implementation in AStreanIn_Byte
	//public abstract long nextLong(); // throws IOException;

	/**
	  * Returns the number of bytes that can be read (or skipped over)
	  * from this input stream without blocking
	  * by the next caller of a method for this input stream.
	  * The next caller might be the same thread or or another thread.
	  *
	  * This Default Implementation assumes that the Number of Bytes stays the same.
	  * When using Compression or Encoding this method should be overridden.
	  *
	  * @return the number of bytes that can be read from this input stream without blocking.
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract int available() throws IOException;

	/**
	  * Marks the current position in this input stream.
	  * A subsequent call to the reset method repositions this stream
	  * at the last marked position so that subsequent reads re-read the same bytes.
	  * The readlimit arguments tells this input stream
	  * to allow that many bytes to be read before the mark position gets invalidated.
	  *
	  * The general contract of mark is that,
	  * if the method markSupported returns true,
	  * the stream somehow remembers all the bytes read after the call to mark
	  * and stands ready to supply those same bytes again
	  * if and whenever the method reset is called.
	  * However, the stream is not required to remember any data at all
	  * if more than readlimit bytes are read from the stream before reset is called.
	  *
	  * The mark method of InputStream does nothing.
	  * @param readlimit - the maximum limit of bytes that can be read
	  * 	before the mark position becomes invalid.
	  * @see reset()
	  */
	public abstract void mark(int readlimit);

	/**
	  * Repositions this stream to the position
	  * at the time the mark method was last called on this input stream.
	  *
	  * The general contract of reset is:
	  * If the method markSupported returns true, then:
	  * If the method mark has not been called since the stream was created,
	  * or the number of bytes read from the stream since mark was last called
	  * is larger than the argument to mark at that last call,
	  * then an IOException might be thrown.
	  * If such an IOException is not thrown,
	  * then the stream is reset to a state such that all the bytes read
	  * since the most recent call to mark (or since the start of the file,
	  * if mark has not been called) will be resupplied to subsequent callers
	  * of the read method, followed by any bytes that otherwise would have been
	  * the next input data as of the time of the call to reset.
	  *
	  * If the method markSupported returns false, then:
	  * The call to reset may throw an IOException.
	  * If an IOException is not thrown, then the stream is reset to a fixed state
	  * that depends on the particular type of the input stream and how it was created.
	  * The bytes that will be supplied to subsequent callers of the read method
	  * depend on the particular type of the input stream.
	  * The method reset for class InputStream does nothing and always throws an IOException.
	  *
	  * @throws IOException - if this stream has not been marked or if the mark has been invalidated.
	  * @see mark(int)
	  * @see IOException
	  */
	public abstract long reSet(long Position);

	/**
	  * Tests if this input stream supports the mark and reset methods.
	  * The markSupported method of InputStream returns false.
	  * @return true if this true type supports the mark and reset method; false otherwise.
	  * @see mark(int), reset()
	  */
	public abstract long getMaxMarkSize();

	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte and StreamInByte: common Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Closes this Input / Output stream
	  * and releases any system resources associated with the streamIO.
	  * A closed stream cannot perform output operations and cannot be reopened.
	  * The close Methods of InputStream and OutputStream do nothing.
	  *
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract void close() throws IOException;

	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  * The byte to be written is the eight low-order bits of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
	  * Subclasses of OutputStream must provide an implementation for this method.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public abstract void write(int b) throws IOException;

	/**
	  * Flushes this output stream and forces any buffered output bytes to be written out.
	  * The general contract of flush is that calling it is an indication that,
	  * if any bytes previously written have been buffered
	  * by the implementation of the output stream,
	  * such bytes should immediately be written to their intended destination.
	  *
	  * The flush method of OutputStream does nothing.
	  *
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract void flush() throws IOException;

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes len bytes from the specified byte array starting at offset off
	  * to this output stream.
	  * The general contract for write(b, off, len) is
	  * that some of the bytes in the array b are written to the output stream in order;
	  * element b[off] is the first byte written and b[off+len-1] is the last byte
	  * written by this operation.
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
	  */
	public void write(byte[] b, int off, int len) throws IOException {
		AStreamOutByte.WRITE(this, b, off, len); }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: Methods handling Characters and Strings
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	public void addItem(int[] b)	throws IOException { addItem(b, 0, b.length); }
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
		AStreamOutByte.WRITE_SAFE(this, b, off, len); }
	  */

	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @see write(byte[], int, int)
	  */
	public void write(char[] b) { write(b, 0, b.length); }

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
	  */
	public void write(char[] b, int off, int len) {
		AStreamOutByte.WRITE_SAFE(this, b, off, len); }

	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public void write(String b)	throws IOException { write(b, 0, b.length()); }

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
	  */
	public void write(String b, int off, int len) throws IOException {
		AStreamOutByte.WRITE(this, b, off, len); }

	/**
	  * Writes b.length Characters from the specified String to this Output streamIO.
	  *
	  * @param b - the data.
	  * @throws IOError - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public IStreamOutByte addBuffer(final StringBuffer b) { return addBuffer(b, b.length()); }

	/**
	  * Writes b.length Characters from the specified String to this Output streamIO.
	  *
	  * @param b - the data.
	  * @throws IOError - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop) { 
		return addBuffer(b, stop, 0); }

	/**
	  * Writes the Characters from start to stop-1 of the specified String 
	  * to this Output streamIO.
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
	  * @see IStreamOutByte#addBuffer(StringBuffer, int, int)
	  */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop, final int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start); 
		return this; }
	
	/** @see streamIO.integer.IStreamOutByte#addString(java.lang.String)	 */
	public IStreamOutByte addString(final String b) { return addString(b, b.length()); } 

	/** @see streamIO.integer.IStreamOutByte#addString(java.lang.String, int)	 */
	public IStreamOutByte addString(final String b, final int stop) {
		return addString(b, stop, 0); } 

	/** @see streamIO.integer.IStreamOutByte#addString(java.lang.String, int, int)	 */
	public IStreamOutByte addString(final String b, final int stop, final int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start); 
		return this; }
	
	/**
	  * Writes b.length bytes from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public void write(final byte[] b)	throws IOException { write(b, 0, b.length); }

	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public IStreamOutByte escapeString(final String str, final String separators) {
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
	public IStreamOutByte escapeString(final StringBuffer str, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, str, separators);
		return this; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) {
		System.out.println("Testing " + AStreamByte.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(args); }

}
