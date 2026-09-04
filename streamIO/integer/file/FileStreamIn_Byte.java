package streamIO.integer.file;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import streamIO.AReSetAble;
import streamIO.IIStreamIn;
import streamIO.IMarkAble;
import streamIO.IPushBackAble;
import streamIO.IReSetAble;
import streamIO.exception.FailureException;
import streamIO.integer.AStreamIn_Byte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamIn_Int;
import streamIO.integer.IStreamOutByte;
import streamIO.real.IStreamIn_Float;
import tools.IOError;
import function.byref.ByRefInt;

/**
  * Title: FileStreamIn_Byte<p>
  * Description:
  * This Interface substitutes the Class FileInputStream in all Implementations
  * The Reason is that the RandomAccessFile Class implements all Methods of
  * both OutputStream and InputStream but sun chose to define these Methods
  * in classes rather than Interfaces, so it cannot be subclassed directly.
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	1999-01-04, 08;58;28<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see IStreamIn_Byte for a Byte  Input streamIO
  * @see IStreamOutByte for a Byte Output streamIO
  * @see StreamByte for a random Access Byte streamIO
  * @see java.io.OutputStream
  * @see java.io.InputStream
  * @see java.io.FileOutputStream
  * @see java.io.FileInputStream
  * @see java.io.RandomAccessFile
  * @see java.io.DataInput
  * @see java.io.DataOutput
  *
  */
public class FileStreamIn_Byte 
extends FileInputStream 
implements IStreamIn_Byte {
	
	final static public IStreamIn_Int HANDLE_EXCEPTION_OBJECT(final IOException x, 
	        final String throwFailureExceptionMessage) {
	    if (null != throwFailureExceptionMessage)
	        throw new FailureException(throwFailureExceptionMessage, x);
		return null; 
	}
	
	final static public int HANDLE_EXCEPTION(final IOException x, 
	        final String throwFailureExceptionMessage) {
	    if (null != throwFailureExceptionMessage)
	        throw new FailureException(throwFailureExceptionMessage, x);
		return EOF; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** optional (null allowed) Flag and Message to throw when an IOException is caught	 */
	public String throwFailureExceptionMessage; 
	
	protected final IStreamIn_Int handleExceptionObject(final IOException x) {
		return FileStreamIn_Byte.HANDLE_EXCEPTION_OBJECT(x, throwFailureExceptionMessage); }
	
	protected final int handleException(final IOException x) {
		return FileStreamIn_Byte.HANDLE_EXCEPTION(x, throwFailureExceptionMessage); }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variable 'Order' with Accessor Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** holds the Order of the Data in the File   */
	public byte Order;
	
	/** @return the Order of the Data in the File  */
	public byte getOrder() { return Order; }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamIn_Int#IntIterator()	 */
	public IStreamIn_Int IntIterator() {
		try { return new FileStreamIn_Byte(getFD());
		} catch (final IOException x) {
			return handleExceptionObject(x);
		}
	}
	
    /** @see streamIO.real.IStreamIn_Float#FloatIterator()     */
    public IStreamIn_Float FloatIterator() { return IntIterator(); }
    
	/** @see streamIO.IIterAble#Iterator()	 */
	public IIStreamIn Iterator() { return IntIterator(); }
    
	/**
	  * Creates a FileInputStream by opening a connection to an actual file,
	  * the file named by the path name name in the file system.
	  * A new FileDescriptor object is created to represent this file connection.
	  * First, if there is a security manager, its checkRead method is called
	  * with the name argument as its argument.
	  * If the named file does not exist, is a directory rather than a regular file,
	  * or for some other reason cannot be opened for reading
	  * then a FileNotFoundException is thrown.
	  *
	  * @param name - the system - dependent file name.
	  * @throws FileNotFoundException - if the file does not exist, is a directory
	  * 	rather than a regular file, or for some other reason cannot be opened for reading.
	  * @throws SecurityException - if a security manager exists and its checkRead method denies read access to the file.
	  * @see SecurityManager.checkRead(java.lang.String)
	  */
	public FileStreamIn_Byte(final String name) throws FileNotFoundException { super(name); }
	
	/**
	 * Creates a FileInputStream by opening a connection to an actual file,
	 * the file named by the File object file in the file system.
	 * A new FileDescriptor object is created to represent this file connection.
	 * First, if there is a security manager, its checkRead method is called
	 * with the path represented by the file argument as its argument.
	 * If the named file does not exist, is a directory rather than a regular file,
	 * or for some other reason cannot be opened for reading then a FileNotFoundException is thrown.
	 * @param file - the file to be opened for reading.
	 * @throws FileNotFoundException - if the file does not exist, is a directory rather
	 * 	than a regular file, or for some other reason cannot be opened for reading.
	 * @throws SecurityException - if a security manager exists
	 * and its checkRead method denies read access to the file.
	 * @see File.getPath(), SecurityManager.checkRead(java.lang.String)
	 */
	public FileStreamIn_Byte(final File file) throws FileNotFoundException { super(file); }
	
	/**
	 * Creates a FileInputStream by using the file descriptor fdObj,
	 * which represents an existing connection to an actual file in the file system.
	 * If there is a security manager, its checkRead method is called with the file descriptor fdObj as its argument
	 * to see if it's ok to read the file descriptor. If read access is denied to the file descriptor a SecurityException is
	 * thrown. If fdObj is null then a NullPointerException is thrown.
	 * @param fdObj - the file descriptor to be opened for reading.
	 * @throws SecurityException - if a security manager exists and its checkRead method denies read access
	 * to the file descriptor.
	 * @see SecurityManager.checkRead(java.io.FileDescriptor)
	 */
	public FileStreamIn_Byte(final FileDescriptor fdObj) { super(fdObj); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: Methods handling Characters and Strings
	////////////////////////////////////////////////////////////////////////////////
	
	/** return the String read from the Stream 
	 * @return the String read from the Stream 
	 * @param length the Length of the String to read
	 */
	public String nextString(final int length) {
		final char[] ret = new char[length]; 
		return new String(ret); }
	
	/** return the given Buffer or a new one
	 * @return the given Buffer or a new one
	 * @param buffer optional (null allowed Buffer to fill) 
	 * @param length the Length of the String to read
	 */
	public final StringBuffer nextBuffer(final int length) {
		return nextBuffer(length, null); }
	
	/** return the given Buffer or a new one
	 * @return the given Buffer or a new one
	 * @param buffer optional (null allowed Buffer to fill) 
	 * @param length the Length of the String to read
	 */
	public StringBuffer nextBuffer(final int length, final StringBuffer buffer) {
		return AStreamIn_Byte.READ_SAFE(this, length, buffer); }
	
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
	public int nextInt() {
		try { return currItem.Value = read();
		} catch (final IOException x) {
			return currItem.Value = handleException(x);
		}
	}
	
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
	public long nextLong() {
		try {
			return read();
		} catch (final IOException x) {
			return handleException(x);
		}
	}

	/** Resets the Iterator to the marked Position 	 */
	public long reSet(final long position) { //throws IOException {
	    try {
	        reset();
	        return skip(position);
	    } catch (final IOException x) {
			return handleException(x);
	    }
	}

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
		return read(b, 0, b.length);
	}
	
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
	public int read(char[] b, int off, int len) throws IOException {
		int val, i = -1;
		while (++i < len) {
			b[off + i] = (char) (val = read());
			if (val == EOF) {
				return i - 1;
			}
		}
		return i - 1;
	}

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
	public StringBuffer read(int Sep, StringBuffer b) throws IOException {
		int val = Sep - 1;
		while (val != Sep) {
			b.append((char) (val = read()));
			if (val == EOF) {
				return b;
			}
		}
		return b;
	}

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
	public StringBuffer read(int Sep) throws IOException {
		return read(Sep, new StringBuffer());
	}

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
	public int read(int[] b) throws IOException {
		return read(b, 0, b.length);
	}

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
	public int read(int[] b, int off, int len) throws IOException {
		int val, i = -1;
		while (++i < len) {
			b[off + i] = (char) (val = read());
			if (val == EOF) {
				return i - 1;
			}
		}
		return i - 1;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// IMarkAble
	/////////////////////////////////////////////////////////////////////////////////////
	
	
	public long getMaxMarkSize() { return Integer.MAX_VALUE; }
	
    /** @see streamIO.IMarkAble#mark()     */
    public IMarkAble mark() { return mark(getMaxMarkSize()); }
    
    /** @see streamIO.IMarkAble#mark(long)     */
    public IMarkAble mark(long readLimit) {
        this.mark((int) readLimit);
        return this; 
    }
    
    /** @see streamIO.real.IStreamIn_Float#nextDouble()     */
    public double nextDouble() { return nextInt(); }
    
    /** @see streamIO.real.IStreamIn_Float#nextFloat()     */
    public float nextFloat() { return nextInt(); }
    
    /** @see streamIO.IIStreamIn#isValid()     */
    public boolean isValid() { return currItem.Value != EOF; }
    
    /** @see streamIO.IReSetAble#jump()     */
    public IReSetAble jump() { return AReSetAble.JUMP(this); }
    
	/** 
	 * Jumps a single Position back in this Iterator.
     * equivalent to jump(-1); 
	 * @see streamIO.IReSetAble#pushBack()  
	 * @return this Stream if jumping worked, null otherwise. 
	 */
    public IPushBackAble pushBack() { return AReSetAble.PUSH_BACK(this); }
	
    /** @see streamIO.IReSetAble#jump(long)     */
    public long jump(final long offset) { 
        try { return skip(offset);  
        } catch (final IOException x) {
			return handleException(x);
        }
    }
    
    /** @see streamIO.IReSetAble#reSet()     */
    public IReSetAble reSet() {
        try { reset(); return this; 
        } catch (final IOException x) {
			return handleExceptionObject(x);
        }
    }
    
    /** @see streamIO.IReSetAble#reSet(java.lang.String)     */
    public IReSetAble reSet(final String failureExceptionMessage) {
        return AReSetAble.RESET(this, failureExceptionMessage); }
    
    /** @see #nextItem() returns this Object or null; 	*/
    final public ByRefInt currItem = new ByRefInt();
    
    /** @see streamIO.IFactory#nextItem()     */
    public Object nextItem() {
        if (EOF == (currItem.Value = nextInt())) 
            return null; 
        return currItem; //new Integer(nextInt());
    }
    
    /** @see streamIO.IAvailAble#availAble()     */
    public long availAble() {
        try {
            return available();
        } catch (final IOException x) {
			return handleException(x);
        }
    }
    
    /** @see streamIO.IAvailAble#getPosition()     */
    public long getPosition() {
        try {
            return getChannel().position();
        } catch (final IOException x) {
			return handleException(x);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    /// IStreamIn
    ///////////////////////////////////////////////////////////////////////////
    
	/** @see streamIO.integer.IStreamIn_Int#currLong()	 */
	public long currLong() { return currItem.Value; }
	
	/** @see streamIO.integer.IStreamIn_Int#currInt()	 */
	public int currInt() { return currItem.Value; }
	
	/** @see streamIO.real.IStreamIn_Float#currDouble()	 */
	public double currDouble() { return currItem.Value; }
	
	/** @see streamIO.real.IStreamIn_Float#currFloat()	 */
	public float currFloat() { return currItem.Value; }
	
	/** @return the next Value without moving to it.	 */
	public int peekInt() { //throws    NoSuchMethodException {
		//throw new NoSuchMethodException("No generic Implementation!");
		final int ret = nextInt(); 
		pushBack(); 
		return ret; 
	}
	
	/** @return the next Value without moving to it.	 */
	public long peekLong() { return peekInt(); }
	
	/** @see streamIO.real.IStreamIn_Float#peekDouble()	 */
	public double peekDouble() { return peekInt(); }
	
	/** @see streamIO.real.IStreamIn_Float#peekFloat()	 */
	public float peekFloat() { return peekInt(); }

	/** @see streamIO.integer.IStreamIn_Int#fill(int[], int, int)	 */
	public int fill(int[] arr, int stop, int start) {
		try {
			return this.read(arr, start, stop-start); 
		} catch (final IOException x) {
			throw new IOError(x); 
		}
	}
	
}
