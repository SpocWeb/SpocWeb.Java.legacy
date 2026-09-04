package streamIO.integer;

import java.io.IOException;
import java.io.OutputStream;

import streamIO.exception.BaseException;
import streamIO.integer.filter.FilterIn_Byte;
import streamIO.integer.pipe.ByteStreamerThread;
import tools.IOError;
import function.byref.ByRefInt;

/**
  * Title: AStreamOutByte<p>
  * Description:
  * This Adapter Class delegates all Methods of the Interface IStreamIn_Byte
  * to an inner Instance of Class IStreamIn_Byte or InputStream.
  * It is capable of (un-)escaping the Input
  * 
  * Known Subclasses:
  * @see FilterIn_Byte
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	1999-01-04, 08;58;28<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see IStreamIn_Byte
  * @see java.io.OutputStream
  * @see java.io.InputStream
  */
public abstract class AStreamIn_Byte
extends AAStreamIn_Int 
implements IStreamIn_Byte, Cloneable {//  to support the clone() Method below
    
	////////////////////////////////////////////////////////////////////////////////
	//  Interface InputStream: abstract Methods
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
      * @see streamIO.integer.IStreamIn_Byte#read()
	  * @return the next byte of data, or EOF if the end of the stream is reached.
	  * @throws IOException - if an I/O error occurs.
	  */
    //abstract public int read() throws IOException; // { return streamIn.read(); }

    /** @see streamIO.IMarkAble#getMaxMarkSize()     */
    //abstract public long getMaxMarkSize(); 
    
	/**
	  * Closes this input stream and releases any system resources associated with the stream.
	  * The close method of InputStream does nothing.
	  *
      * @see streamIO.integer.IStreamIn_Byte#close()
	  * @throws IOException - if an I/O error occurs.
	  */
    //abstract public void close() throws IOException;

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
    //abstract public void mark(int readlimit);

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
    //abstract public long reset(long Position);

    /////////////////////////////////////////////////////////////////////////////////////
    /// abstract Methods
    /////////////////////////////////////////////////////////////////////////////////////
    
    /** @see streamIO.integer.IStreamIn_Byte#available()     */
    abstract public int available() throws IOException;
    
    /** @see streamIO.IOrdered#getOrder()     */
    abstract public byte getOrder(); 
    
    /** @see streamIO.IAvailAble#getPosition()     */
    abstract public long getPosition(); 
    
    /** @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()     */
    public double getMinDouble() { return Byte.MIN_VALUE; } 
    
    /////////////////////////////////////////////////////////////////////////////////////
    /// Default Implementations
    /////////////////////////////////////////////////////////////////////////////////////
    
    final public ByRefInt currItem = new ByRefInt(); //ByRefByte(); 
    
    /** @see streamIO.object.IStreamIn#currItem()     */
    public Object currItem() { return currItem; }
    
    /** @see streamIO.real.IStreamIn_Float#nextDouble()     */
    public double nextDouble() { return nextInt(); }
    
    /** @see streamIO.IFactory#nextItem()
     */
    public Object nextItem() { 
        final int nextInt = nextInt();
        if (nextInt == EOF)
            return EOI; 
        currItem.Value = nextInt; 
        return currItem;
    }	    
    
	/**
	  * Returns the number of bytes that can be read (or skipped over)
	  * from this input stream without blocking
	  * by the next caller of a method for this input stream.
	  * The next caller might be the same thread or or another thread.
	  *
	  * This Default Implementation assumes that the Number of Bytes stays the same.
	  * When using Compression or Encoding this method should be overridden.
	  *
     * @see streamIO.IAvailAble#availAble()
	  * @return the number of bytes that can be read from this input stream without blocking.
	  * @throws IOException - if an I/O error occurs.
	  */
   public long availAble() { 
       try {
           return available();
       } catch (final IOException x) {
           //if (throwException != null)
           //    throw new IOError(x); 
           return EOF; 
       }
   }

	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Cloning creates only a shallow Copy.  
	 * @see streamIO.integer.IStreamIn_Int#Iterator()	 */
	public IStreamIn_Int IntIterator() {
		try { return (IStreamIn_Int) clone(); 
		} catch (final CloneNotSupportedException x) {
			throw new BaseException(x);
		}
		//return null;
	}
	
	/**
	 * overrides the Default Implementation which uses availAble()  
	 * @see streamIO.IIStreamIn#isValid()	 */
	public boolean isValid() {
	    try { return available() >= 0; 
	    } catch (final IOException x) {
	        throw new IOError(x);
	    }
	}
	
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
	  * the value EOF is returned; otherwise, at least one byte is read and stored into b.
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
	  * 	or EOF is there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see read(byte[], int, int)
	  */
	public int read(final byte[] b) throws IOException {
		return read(b, 0, b.length); }
	
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
	  * the value EOF is returned; otherwise, at least one byte is read and stored into b.
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
	  * 	or EOF if there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see read()
	  * @see streamIO.Byte.IStreamIn_Byte#read(byte[], int, int)
	  */
	public int read(final byte[] b, final int off, final int len) throws IOException {
		return READ(this, b, off, len); }
	
	/**
	  * Skips over and discards n bytes of data from this input stream.
	  * The skip method may, for a variety of reasons,
	  * end up skipping over some smaller number of bytes, possibly 0.
	  * This may result from any of a number of conditions;
	  * reaching end of file before n bytes have been skipped is only one possibility.
	  * The actual number of bytes skipped is returned.
	  * If n is negative, no bytes are skipped.
	  * The skip method of InputStream creates a byte array
	  * and then repeatedly reads into it until n bytes have been read
	  * or the end of the stream has been reached.
	  * Subclasses are encouraged to provide a more efficient implementation of this method.
	  *
	  * @param n - the number of bytes to be skipped.
	  * @return the actual number of bytes skipped.
	  * @throws IOException - if an I/O error occurs.
	  */
	public long jump(final long len) { return SKIP(this, len); }
	
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
	public int read(final char[] b, final int off, final int len) throws IOException {
		return READ(this, b, off, len); }
	
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
	public int read(final char[] b) throws IOException {
		return read(b, 0, b.length); }
	
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
	public int read(final int[] b, final int off, final int len) throws IOException {
		return READ(this, b, off, len); }
		
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
	public int read(final int[] b) throws IOException {
		return read(b, 0, b.length); }

	/**
	  * Reads some number of Characters from the input stream
	  * and stores them into the StringBuffer b.
	  * This method blocks until input data is available,
	  * end of file is detected, or an exception is thrown.
	  * If b is null, a NullPointerException is thrown.
	  * If the Separator Character is encountered, the Routine returns;
	  * If no byte is available because the stream is at End Of File,
	  * or the Separator value is returned, reading stops.
	  * The last Character is either the Separator or not (EOF encountered).
	  * By setting the Separator to -1, the full streamIO is read.
	  * By setting the Separator to 10, a full Line is read.
	  * The actual Number of Characters actually read depends on the Contents.
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
	public StringBuffer read(final int len, final StringBuffer b) throws IOException {
		return READ(this, len, b); }
	
	/**
	  * Reads some number of Characters from the input stream
	  * and stores them into the StringBuffer b.
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
	  * @return the total number of bytes read into the buffer,
	  * 	or -1 is there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs on reading the first Byte.
	  * @see read(char[], int, int)
	  */
	public StringBuffer read(final int _len) throws IOException {
		return read(_len, null); }
	
	/**
	  * Reads the next Value of data from the input stream as an int.
	  * The value byte is returned as an int in the range MinLong to MaxLong .
	  * If no byte is available because the end of the stream has been reached,
	  * the value -1 is returned.
	  * This method blocks until input data is available,
	  * the end of the stream is detected, or an exception is thrown.
	  *
	  * A subclass must provide an implementation of this method.
	  *
	  * @return the next byte of data, or -1 if the end of the stream is reached.
	  * does not throw an IOException - if an I/O error occurs, but a RuntimeException. 
	  */
	public int nextInt() { return currItem.Value = NEXT_INT_SAFE(this); }
	
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
	  * does not throw an IOException - if an I/O error occurs, but a RuntimeException. 
	  */
	public long nextLong() { return nextInt(); }
	
	/** @see streamIO.integer.IStreamIn_Int#currLong()	 */
	public long currLong() { return currItem.Value; }
	
	/** @see streamIO.integer.IStreamIn_Int#currInt()	 */
	public int currInt() { return currItem.Value; }
	
	/** @see streamIO.real.IStreamIn_Float#currDouble()	 */
	public double currDouble() { return currItem.Value; }
	
	/** @see streamIO.real.IStreamIn_Float#currFloat()	 */
	public float currFloat() { return currItem.Value; }
	
	/** return the String read from the Stream 
	 * @return the String read from the Stream 
	 * @param length the Length of the String to read
	 */
	public String nextString(final int length) {
		final char[] ret = new char[length]; 
		return new String(ret); }
	
	/** return a new Buffer filled with the next Characters up to the given Length
	 * @return a new Buffer filled with the next Characters up to the given Length
	 * @param buffer optional (null allowed Buffer to fill) 
	 * @param length the Length of the String to read
	 */
	public final StringBuffer nextBuffer(final int length) {
		return nextBuffer(length, null); }
	
	/** return the given Buffer or a new one filled with the next Characters up to the given Length
	 * @return the given Buffer or a new one filled with the next Characters up to the given Length
	 * @param buffer optional (null allowed Buffer to fill) 
	 * @param length the Length of the String to read
	 */
	public StringBuffer nextBuffer(final int length, final StringBuffer buffer) {
		return READ_SAFE(this, length, buffer); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Stream Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called writeTo()
	 * @param out the Stream to stream to
	 * @return the number of Values streamed 
	 */
	public long stream(final IStreamOutByte out) throws IOException {
		return ByteStreamerThread.STREAM(this, out); }
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called readFrom()
	 * @param out the Stream to stream to
	 * @return the number of Bytes streamed 
	 */
	public long stream(final OutputStream out) throws IOException {
		return ByteStreamerThread.STREAM(this, out); }
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called writeTo()
	 * @param out the Stream to stream to
	 * @param chunkSize the Size to use for chunk-wise reading 
	 * @return the number of Values streamed 
	 * @throws IOException when an Error happens
	 */
	public long stream(final IStreamOutByte out, final int chunkSize) throws IOException {
		return ByteStreamerThread.STREAM(this, out, chunkSize); }
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called writeTo()
	 * @param out the Stream to stream to
	 * @param chunkSize the Size to use for chunk-wise reading 
	 * @return the number of Values streamed 
	 * @throws IOException when an Error happens
	 */
	public long stream(final OutputStream out, final int chunkSize) throws IOException {
		return ByteStreamerThread.STREAM(this, out, chunkSize); }
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called writeTo()
	 * @param out the Stream to stream to
	 * @param buffer the Buffer to use for chunk-wise reading 
	 * @return the number of Values streamed 
	 * @throws IOException when an Error happens
	 */
	public long stream(final IStreamOutByte out, final byte[] buffer) throws IOException {
		return ByteStreamerThread.STREAM(this, out, buffer); }
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called writeTo()
	 * @param out the Stream to stream to
	 * @param buffer the Buffer to use for chunk-wise reading 
	 * @return the number of Values streamed 
	 * @throws IOException when an Error happens
	 */
	public long stream(final OutputStream out, final byte[] buffer) throws IOException {
		return ByteStreamerThread.STREAM(this, out, buffer); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Static Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Skips over and discards n bytes of data from this input stream.
	  * The skip method may, for a variety of reasons,
	  * end up skipping over some smaller number of bytes, possibly 0.
	  * This may result from any of a number of conditions;
	  * reaching end of file before n bytes have been skipped is only one possibility.
	  * The actual number of bytes skipped is returned.
	  * If n is negative, no bytes are skipped.
	  * The skip method of InputStream creates a byte array
	  * and then repeatedly reads into it until n bytes have been read
	  * or the end of the stream has been reached.
	  * Subclasses are encouraged to provide a more efficient implementation of this method.
	  *
	  * @param n - the number of bytes to be skipped.
	  * @return the actual number of bytes skipped.
	  * @throws IOException - if an I/O error occurs.
	  */
	final static public long SKIP(final IStreamIn_Byte ths, final long len) {
		for(int i = -1; ++i < len;) 
			if ((ths.nextInt() == EOF) && (ths.availAble() < 0)) 
				return i; 
		return len; }
	
	/**
	  * Reads the next Value of data from the input stream as an int.
	  * The value byte is returned as an int in the range MinLong to MaxLong .
	  * If no byte is available because the end of the stream has been reached,
	  * the value -1 is returned.
	  * This method blocks until input data is available,
	  * the end of the stream is detected, or an exception is thrown.
	  *
	  * A subclass must provide an implementation of this method.
	  *
	  * @return the next byte of data, or -1 if the end of the stream is reached.
	  * does not throw an IOException - if an I/O error occurs, but a RuntimeException. 
	  */
	final static public int NEXT_INT_SAFE(final IStreamIn_Byte ths) {
		try{ return ths.read();
		} catch (final IOException x) {
			throw new IOError(x); 
//			return -1; 
		}
	}
	
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
	  * the value EOF is returned; otherwise, at least one byte is read and stored into b.
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
	  * 	or EOF if there is no more data because the end of the stream has been reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see read()
	  * @see streamIO.Byte.IStreamIn_Byte#read(byte[], int, int)
	  */
	final static public int READ(final IStreamIn_Byte ths, 
			final byte[] b, final int off, final int len) throws IOException {
		for(int val, i = -1; ++i < len;) { //first check length...
			if (((val = ths.read()) == EOF) && (ths.available() < 0)) 
				return i; //... to prevent reading but not saving!
			b[off + i] = (byte) val;
		}
		return len; }

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
	final static public int READ(final IStreamIn_Byte ths, 
			final char[] b, final int off, final int len) throws IOException {
		for (int val, i = -1; ++i < len;) { //first check length...
			if (((val = ths.read()) == EOF) && (ths.available() < 0)) 
				return i; //... to prevent reading but not saving!
			b[off + i] = (char) val;
		}
		return len; }
	
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
	final static public int READ(final IStreamIn_Byte ths, 
			final int[] b, final int off, final int len) throws IOException {
		for(int val, i = -1; ++i < len;) { //first check length...
			if (((val = ths.read()) == EOF) && (ths.available() < 0)) 
				return i; //... to prevent reading but not saving!
			b[off + i] = val;
		}
		return len; }
			
	/** 
	 * reads until the Capacity of the Buffer is reached. 
	 * @param ths
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	final static public StringBuffer READ_SAFE(final IStreamIn_Byte ths 
			, final int len, StringBuffer buffer)  {
		try { return READ(ths, len, buffer);
		} catch(final IOException x) {
			throw new IOError(x); }
	}
	
	/** 
	 * reads until the Capacity of the Buffer is reached. 
	 * @param ths
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	final static public StringBuffer READ(final IStreamIn_Byte ths, 
			final int len, StringBuffer buffer) throws IOException {
		if (buffer == null) 
			buffer  = new StringBuffer(len); 
		for(int val, i = -1; ++i < len;) { //first check length...
			if (((val = ths.read()) == EOF) && (ths.available() < 0)) 
				return buffer; //... to prevent reading but not saving!
			buffer.append((char) val);
		}
		return buffer; }
	
}
