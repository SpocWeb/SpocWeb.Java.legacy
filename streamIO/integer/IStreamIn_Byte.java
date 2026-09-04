package streamIO.integer;

import java.io.IOException;

import streamIO.IMarkAble;
import streamIO.IOrdered;


/**
  * Title: IStreamIn_Byte<p>
  * Description:
  * This Interface substitutes the Class InputStream in all Implementations
  * The Reason is that the RandomAccessFile Class implements all Methods of
  * both OutputStream and InputStream but sun chose to define these Methods
  * in classes rather than Interfaces.
  *
  * Additionally four Methods have been added to:
  * read Character Arrays and StringBuffers.
  *
  * Known Implementors:
  * @see streamIO.Byte.AStreamIn_Byte
  * @see streamIO.Byte.FileStreamIn_Byte
  * @see streamIO.Byte.FileStreamByte
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	1999-01-04, 08;58;28<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see IStreamOutByte
  * @see java.io.OutputStream
  * @see java.io.InputStream
  */
public interface IStreamIn_Byte
extends IStreamIn_Int, IMarkAble, IOrdered { //InputStream is no Interface

	/**WHITESPACE Characters
	 * They act as separators, but are usually truncated
	 * The following (non-printable) Characters are considered as WhiteSpace by Java:
	 *
	 * '\t' \u0009 HORIZONTAL TABULATION
	 * '\n' \u000A NEW LINE
	 * '\f' \u000C FORM FEED
	 * '\r' \u000D CARRIAGE RETURN
	 * '  ' \u0020 SPACE
	 * Unicode space separator (category "Zs"), but is not a no-break space (\u00A0 or \uFEFF).
	 * Unicode line separator (category "Zl").
	 * Unicode paragraph separator (category "Zp").
	 * \u0009 '\t' HORIZONTAL TABULATION.
	 * \u000A '\n' LINE FEED.
	 * \u000B '  ' VERTICAL TABULATION.
	 * \u000C '\f' FORM FEED.
	 * \u000D '\r' CARRIAGE RETURN.
	 * \u001C '  ' FILE SEPARATOR.
	 * \u001D '  ' GROUP SEPARATOR.
	 * \u001E '  ' RECORD SEPARATOR.
	 * \u001F '  ' UNIT SEPARATOR.
	 * \u0020 '  ' SPACE
	 */
	final static public String WHITESPACE = "\t\n\f\r ";

	/** Indicator for not using Escape Characters. */
	final static public char NO_ESCAPE = (char) -2; //no Escaping by Default...

	/** NewLine resp. LineFeed Character for parsing */
	final static public byte BYTE_TAB = 0x9;

	/** NewLine resp. LineFeed Character for parsing */
	final static public byte BYTE_LINE_FEED = 0xA;

	/** Carriage Return Character for parsing */
	final static public byte BYTE_CARRIAGE_RETURN = 0xD;

	final static public char CHR_HORIZONTAL_TABULATOR = BYTE_TAB; //'\t';
	final static public char CHR_LINE_FEED = BYTE_LINE_FEED; //'\n';
	final static public char CHR_VERTICAL_TABULATION = '\u000B';
	final static public char CHR_FORM_FEED = '\f';
	final static public char CHR_CARRIAGE_RETURN = BYTE_CARRIAGE_RETURN; //'\r';
	final static public char CHR_FILE_SEPARATOR = '\u001C';
	final static public char CHR_GROUP_SEPARATOR = '\u001D';
	final static public char CHR_RECORD_SEPARATOR = '\u001E';
	final static public char CHR_UNIT_SEPARATOR = '\u001F';
	final static public char CHR_SPACE = ' ';
	
	/** Character used to indicate an Escaping:	 */
	final static public char CHR_ESCAPE = '\\';
	
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
	  */
	long nextLong(); // throws IOException;
	
	/** return the String read from the Stream 
	 * @return the String read from the Stream 
	 * @param length the Length of the String to read
	 */
	String nextString(final int length); 
	
	/** return the given Buffer or a new one
	 * @return the given Buffer or a new one
	 * @param buffer optional (null allowed Buffer to fill) 
	 * @param length the Length of the String to read
	 */
	StringBuffer nextBuffer(final int length); 
	
	/** return the given Buffer or a new one
	 * @return the given Buffer or a new one
	 * @param buffer optional (null allowed Buffer to fill) 
	 * @param length the Length of the String to read
	 */
	StringBuffer nextBuffer(final int length, final StringBuffer buffer); 
	
	/**
	  * Reads the next byte of data from the input stream.
	  * The value byte is returned as an int in the range 0 to 255.
	  * If no byte is available because the end of the stream has been reached,
	  * the value -1 is returned.
	  * This method blocks until input data is available,
	  * the end of the stream is detected, or an exception is thrown.
	  *
	  * A subclass must provide an implementation of this method.
	  *
	  * @return the next byte of data, or -1 if the end of the stream is reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see nextInt For a Method which wraps the Exception to throw a RuntimeException!
	  */
	int read() throws IOException;
	
	/**
	  * Reads some number of bytes from the input stream
	  * and stores them into the buffer array b.
	  * The number of bytes actually read is returned as an integer.
	  * This method blocks until input data is available,
	  * end of file is detected, or an exception is thrown.
	  * If b is null, a NullPointerException is thrown.
	  * If the length of b is zero, then no bytes are read and 0 is returned;
	  * otherwise, there is an attempt to read at least one byte.
	  * If no byte is available because the stream is at end of file,
	  * the value -1 is returned; otherwise, at least one byte is read and stored into b.
	  *
	  * The first byte read is stored into element b[0], the next one into b[1], and so on.
	  * The number of bytes read is, at most, equal to the length of b.
	  * Let k be the number of bytes actually read;
	  * these bytes will be stored in elements b[0] through b[k-1],
	  * leaving elements b[k] through b[b.length-1] unaffected.
	  *
	  * If the first byte cannot be read for any reason other than end of file,
	  * then an IOException is thrown.
	  * In particular, an IOException is thrown if the input stream has been closed.
	  *
	  * The read(b) method for class InputStream has the same effect as:
	  * read(b, 0, b.length)
	  * @param b - the buffer into which the data is read.
	  * @return the total number of bytes read into the buffer,
	  * 	or -1 is there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see read(byte[], int, int)
	  */
	int read(final byte[] b) throws IOException;
	
	/**
	  * Reads up to len bytes of data from the input stream into an array of bytes.
	  * An attempt is made to read as many as len bytes,
	  * but a smaller number may be read, possibly zero.
	  * The number of bytes actually read is returned as an integer.
	  *
	  * This method blocks until input data is available, end of file is detected,
	  * or an exception is thrown.
	  *
	  * If b is null, a NullPointerException is thrown.
	  * If off is negative, or len is negative,
	  * 	or off+len is greater than the length of the array b,
	  * 	then an IndexOutOfBoundsException is thrown.
	  * If len is zero, then no bytes are read and 0 is returned;
	  * otherwise, there is an attempt to read at least one byte.
	  * If no byte is available because the stream is at end of file,
	  * the value -1 is returned; otherwise, at least one byte is read and stored into b.
	  *
	  * The first byte read is stored into element b[off],
	  * the next one into b[off+1], and so on.
	  * The number of bytes read is, at most, equal to len.
	  * Let k be the number of bytes actually read;
	  * these bytes will be stored in elements b[off] through b[off+k-1],
	  * leaving elements b[off+k] through b[off+len-1] unaffected.
	  *
	  * In every case, elements b[0] through b[off]
	  * and elements b[off+len] through b[b.length-1] are unaffected.
	  *
	  * If the first byte cannot be read for any reason other than end of file,
	  * then an IOException is thrown.
	  * In particular, an IOException is thrown if the input stream has been closed.
	  *
	  * The read(b, off, len) method for class InputStream
	  * simply calls the method read() repeatedly.
	  * If the first such call results in an IOException,
	  * that exception is returned from the call to the read(b, off, len) method.
	  * If any subsequent call to read() results in a IOException,
	  * the exception is caught and treated as if it were end of file;
	  * the bytes read up to that point are stored into b
	  * and the number of bytes read before the exception occurred is returned.
	  * Subclasses are encouraged to provide a more efficient implementation of this method.
	  *
	  * @param b - the buffer into which the data is read.
	  * @param off - the start offset in array b at which the data is written.
	  * @param len - the maximum number of bytes to read.
	  * @return the total number of bytes read into the buffer,
	  * 	or -1 if there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see read()
	  */
	int read(final byte[] b, final int off, final int len) throws IOException;

	/**
	  * Reads some number of Characters from the input stream
	  * and stores them into the StringBuffer b.
	  * (If the StringBuffer b is null, a new one is not created!)
	  * The number of Characters actually read is returned as an integer.
	  * This method blocks until input data is available,
	  * end of file is detected, or an exception is thrown.
	  * If b is null, a NullPointerException is thrown.
	  * If the Separator Character is encountered, the Routine returns;
	  * If no byte is available because the stream is at end of file,
	  * or the Separator value is returned, reading stops.
	  * By setting the Separator to -1, the full streamIO is read.
	  * By setting the Separator to 10, a full Line is read.
	  * If a fixed Number of Characters should be read,
	  * use read(byte[]) or read(char[]).
	  *
	  * If the first Characters cannot be read for any reason other than end of file,
	  * then an IOException is thrown.
	  * In particular, an IOException is thrown if the input stream has been closed.
	  *
	  * @param b - the StringBuffer into which the data is read.
	  * @return the same StringBuffer
	  * @throws IOException - if an I/O error occurs on reading the first Byte.
	  * @see read(char[], int, int)
	  */
	StringBuffer read(final int Sep, final StringBuffer b) throws IOException;
	
	/**
	  * Reads some number of Characters from the input stream
	  * and stores them into the StringBuffer returned.
	  * The number of Characters actually read is returned as an integer.
	  * This method blocks until input data is available,
	  * end of file is detected, or an exception is thrown.
	  * If b is null, a NullPointerException is thrown.
	  * If the Separator Character is encountered, the Routine returns;
	  * If no byte is available because the stream is at end of file,
	  * or the Separator value is returned, reading stops.
	  * By setting the Separator to -1, the full streamIO is read.
	  * By setting the Separator to 10, a full Line is read.
	  * If a fixed Number of Characters should be read,
	  * use read(byte[]) or read(char[]).
	  *
	  * If the first Characters cannot be read for any reason other than end of file,
	  * then an IOException is thrown.
	  * In particular, an IOException is thrown if the input stream has been closed.
	  *
	  * @param b - the StringBuffer into which the data is read.
	  * @return the StringBuffer
	  * @throws IOException - if an I/O error occurs on reading the first Byte.
	  * @see read(char[], int, int)
	  */
	StringBuffer read(final int Sep) throws IOException;

	/**
	  * Reads some number of Characters from the input stream
	  * and stores them into the buffer array b.
	  * The number of Characters actually read is returned as an integer.
	  * This method blocks until input data is available,
	  * end of file is detected, or an exception is thrown.
	  * If b is null, a NullPointerException is thrown.
	  * If the length of b is zero, then no Characters are read and 0 is returned;
	  * otherwise, there is an attempt to read at least one Character.
	  * If no byte is available because the stream is at end of file,
	  * the value -1 is returned; otherwise, at least one Character is read
	  * and stored into b.
	  *
	  * The first Character read is stored into element b[0],
	  * the next one into b[1], and so on.
	  * The number of Characters read is, at most, equal to the length of b.
	  * Let k be the number of Characters actually read;
	  * these Characters will be stored in elements b[0] through b[k-1],
	  * leaving elements b[k] through b[b.length-1] unaffected.
	  *
	  * If the first Characters cannot be read for any reason other than end of file,
	  * then an IOException is thrown.
	  * In particular, an IOException is thrown if the input stream has been closed.
	  *
	  * The read(b) method for class InputStream has the same effect as:
	  * read(b, 0, b.length)
	  * @param b - the buffer into which the data is read.
	  * @return the total number of bytes read into the buffer,
	  * 	or -1 is there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see read(char[], int, int)
	  */
	int read(final char[] b) throws IOException;
	
	/**
	  * Reads up to len Characters of data from the input stream into an array of Characters.
	  * An attempt is made to read as many as len Characters,
	  * but a smaller number may be read, possibly zero.
	  * The number of Characters actually read is returned as an integer.
	  *
	  * This method blocks until input data is available, end of file is detected,
	  * or an exception is thrown.
	  *
	  * If b is null, a NullPointerException is thrown.
	  * If off is negative, or len is negative,
	  * 	or off+len is greater than the length of the array b,
	  * 	then an IndexOutOfBoundsException is thrown.
	  * If len is zero, then no Characters are read and 0 is returned;
	  * otherwise, there is an attempt to read at least one Character.
	  * If no Character is available because the stream is at end of file,
	  * the value -1 is returned; otherwise, at least one Character is read
	  * and stored into b.
	  *
	  * The first Character read is stored into element b[off],
	  * the next one into b[off+1], and so on.
	  * The number of Characters read is, at most, equal to len.
	  * Let k be the number of Characters actually read;
	  * these Characters will be stored in elements b[off] through b[off+k-1],
	  * leaving elements b[off+k] through b[off+len-1] unaffected.
	  *
	  * In every case, elements b[0] through b[off]
	  * and elements b[off+len] through b[b.length-1] are unaffected.
	  *
	  * If the first Character cannot be read for any reason other than end of file,
	  * then an IOException is thrown.
	  * In particular, an IOException is thrown if the input stream has been closed.
	  *
	  * The read(b, off, len) method for class InputStream
	  * simply calls the method read() repeatedly.
	  * If the first such call results in an IOException,
	  * that exception is returned from the call to the read(b, off, len) method.
	  * If any subsequent call to read() results in a IOException,
	  * the exception is caught and treated as if it were end of file;
	  * the Characters read up to that point are stored into b
	  * and the number of Characters read before the exception occurred is returned.
	  * Subclasses are encouraged to provide a more efficient implementation of this method.
	  *
	  * @param b - the buffer into which the data is read.
	  * @param off - the start offset in array b at which the data is written.
	  * @param len - the maximum number of bytes to read.
	  * @return the total number of bytes read into the buffer,
	  * 	or -1 if there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see read()
	  */
	int read(final char[] b, final int off, final int len) throws IOException;
	
	/**
	  * Reads some number of Characters from the input stream
	  * and stores them into the buffer array b.
	  * The number of Characters actually read is returned as an integer.
	  * This method blocks until input data is available,
	  * end of file is detected, or an exception is thrown.
	  * If b is null, a NullPointerException is thrown.
	  * If the length of b is zero, then no Characters are read and 0 is returned;
	  * otherwise, there is an attempt to read at least one Character.
	  * If no byte is available because the stream is at end of file,
	  * the value -1 is returned; otherwise, at least one Character is read
	  * and stored into b.
	  *
	  * The first Character read is stored into element b[0],
	  * the next one into b[1], and so on.
	  * The number of Characters read is, at most, equal to the length of b.
	  * Let k be the number of Characters actually read;
	  * these Characters will be stored in elements b[0] through b[k-1],
	  * leaving elements b[k] through b[b.length-1] unaffected.
	  *
	  * If the first Characters cannot be read for any reason other than end of file,
	  * then an IOException is thrown.
	  * In particular, an IOException is thrown if the input stream has been closed.
	  *
	  * The read(b) method for class InputStream has the same effect as:
	  * read(b, 0, b.length)
	  * @param b - the buffer into which the data is read.
	  * @return the total number of bytes read into the buffer,
	  * 	or -1 is there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see read(char[], int, int)
	  */
	int read(final int[] b) throws IOException;
	
	/**
	  * Reads up to len Characters of data from the input stream into an array of Characters.
	  * An attempt is made to read as many as len Characters,
	  * but a smaller number may be read, possibly zero.
	  * The number of Characters actually read is returned as an integer.
	  *
	  * This method blocks until input data is available, end of file is detected,
	  * or an exception is thrown.
	  *
	  * If b is null, a NullPointerException is thrown.
	  * If off is negative, or len is negative,
	  * 	or off+len is greater than the length of the array b,
	  * 	then an IndexOutOfBoundsException is thrown.
	  * If len is zero, then no Characters are read and 0 is returned;
	  * otherwise, there is an attempt to read at least one Character.
	  * If no Character is available because the stream is at end of file,
	  * the value -1 is returned; otherwise, at least one Character is read
	  * and stored into b.
	  *
	  * The first Character read is stored into element b[off],
	  * the next one into b[off+1], and so on.
	  * The number of Characters read is, at most, equal to len.
	  * Let k be the number of Characters actually read;
	  * these Characters will be stored in elements b[off] through b[off+k-1],
	  * leaving elements b[off+k] through b[off+len-1] unaffected.
	  *
	  * In every case, elements b[0] through b[off]
	  * and elements b[off+len] through b[b.length-1] are unaffected.
	  *
	  * If the first Character cannot be read for any reason other than end of file,
	  * then an IOException is thrown.
	  * In particular, an IOException is thrown if the input stream has been closed.
	  *
	  * The read(b, off, len) method for class InputStream
	  * simply calls the method read() repeatedly.
	  * If the first such call results in an IOException,
	  * that exception is returned from the call to the read(b, off, len) method.
	  * If any subsequent call to read() results in a IOException,
	  * the exception is caught and treated as if it were end of file;
	  * the Characters read up to that point are stored into b
	  * and the number of Characters read before the exception occurred is returned.
	  * Subclasses are encouraged to provide a more efficient implementation of this method.
	  *
	  * @param b - the buffer into which the data is read.
	  * @param off - the start offset in array b at which the data is written.
	  * @param len - the maximum number of bytes to read.
	  * @return the total number of bytes read into the buffer,
	  * 	or -1 if there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see read()
	  */
	int read(final int[] b, final int off, final int len) throws IOException;
	
	/**
	  * Returns the number of bytes that can be read (or skipped over)
	  * from this input stream without blocking
	  * by the next caller of a method for this input stream.
	  * The next caller might be the same thread or or another thread.
	  *
	  * The available method for class InputStream always returns 0.
	  * This method should be overridden by subclasses.
	  *
	  * @see streamIO.Object.IStreamIn#availAble() which returns a long
	  * 	and is therefore written differently!
	  * @return the number of bytes that can be read from this input stream without blocking.
	  * @throws IOException - if an I/O error occurs.
	  */
	int available() throws IOException;
	
	/**
	  * Closes this input stream and releases any system resources associated with the stream.
	  * The close method of InputStream does nothing.
	  *
	  * @throws IOException - if an I/O error occurs.
	  */
	void close() throws IOException;
	
}
