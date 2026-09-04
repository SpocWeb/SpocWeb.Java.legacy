package streamIO.integer.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import streamIO.AReSetAble;
import streamIO.IIStreamIn;
import streamIO.IMarkAble;
import streamIO.IPushBackAble;
import streamIO.IReSetAble;
import streamIO.integer.AStreamIn_Byte;
import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamByte;
import streamIO.integer.IStreamByteRandom;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamIn_Int;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.pipe.ByteStreamerThread;
import streamIO.real.IStreamIn_Float;
import tools.IOError;
import function.byref.ByRefInt;

/**
  * Title: FileStreamByte<p>
  * Description:
  * This Interface substitutes the Class RandomAccessFile in all Implementations
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
  * implements all the following Interfaces:
  * @see java.io.OutputStream
  * @see java.io.InputStream
  * @see java.io.DataInput
  * @see java.io.DataOutput
  */
public class FileStreamByte 
extends RandomAccessFile //TODO: Byte Channels are the new IO Paradigm since 1.4
implements IStreamByte, IStreamByteRandom {
    
	/**
	 * physically copies the 'original' File into a (new) 'copy' in the same Directory
	 * @param original Source File
	 * @param copy Destination File 
	 * @throws FileNotFoundException if the Source does not exist or the Target is a Directory 
	 * @throws IOException on any Error
	 */
	final static public File COPY_TMP_FILE(final String original, final String tmpName) throws FileNotFoundException, IOException {
		return COPY_TMP_FILE(new File(original), tmpName); }
	
	/**
	 * physically copies the 'original' File into a (new) 'copy' in the same Directory
	 * @param original Source File
	 * @param copy Destination File 
	 * @throws FileNotFoundException if the Source does not exist or the Target is a Directory 
	 * @throws IOException on any Error
	 */
	final static public File COPY_TMP_FILE(final File original, final String tmpName) throws FileNotFoundException, IOException {
		final File tmpFile = new File(original.getParentFile().getAbsolutePath(), tmpName);
		COPY_FILE(original, tmpFile);
		return tmpFile; 
	}
	
	/** Default File NAme for Temporary Files 
	 * @see File#createTempFile(java.lang.String, java.lang.String) 
	 * creates a File in the System's temp Directory 
	 */
	final static public String TMP_FILE_SUFFIX=".tmp"; 
	
	/**
	 * physically copies the 'original' File into a (new) 'copy' in the same Directory
	 * @param original Source File
	 * @param copy Destination File 
	 * @throws FileNotFoundException if the Source does not exist or the Target is a Directory 
	 * @throws IOException on any Error
	 */
	final static public File COPY_TMP_FILE(final String original) throws FileNotFoundException, IOException {
		return COPY_TMP_FILE(new File(original), TMP_FILE_SUFFIX); }
	
	/**
	 * physically copies the 'original' File into a (new) 'copy' in the same Directory
	 * @param original Source File
	 * @param copy Destination File 
	 * @throws FileNotFoundException if the Source does not exist or the Target is a Directory 
	 * @throws IOException on any Error
	 */
	final static public File COPY_TMP_FILE(final File original) throws FileNotFoundException, IOException {
		return COPY_TMP_FILE(original, TMP_FILE_SUFFIX);
	}

	/**
	 * physically copies the 'original' File into a (new) 'copy' 
	 * @param original Source File
	 * @param copy Destination File 
	 * @throws FileNotFoundException if the Source does not exist or the Target is a Directory 
	 * @throws IOException on any Error
	 */
	final static public void COPY_FILE(final File original, final File copy) throws FileNotFoundException, IOException {
		final IStreamIn_Byte in = new FileStreamIn_Byte(original); 
		final IStreamOutByte out= new FileStreamOutByte(copy); 
		ByteStreamerThread.STREAM(in, out, 8192);
		out.close(); 
		in.close(); 
	}

	/**
	 * physically copies the 'original' File into a (new) 'copy' 
	 * @param original Source File
	 * @param copy Destination File 
	 * @throws FileNotFoundException if the Source does not exist or the Target is a Directory 
	 * @throws IOException on any Error
	 */
	final static public void COPY_FILE(final File original, final String copy) throws FileNotFoundException, IOException {
		COPY_FILE(original, new File(copy)); 
	}

	/**
	 * physically copies the 'original' File into a (new) 'copy' 
	 * @param original Source File
	 * @param copy Destination File 
	 * @throws FileNotFoundException if the Source does not exist or the Target is a Directory 
	 * @throws IOException on any Error
	 */
	final static public void COPY_FILE(final String original, final File copy) throws FileNotFoundException, IOException {
		COPY_FILE(new File(original), copy); 
	}

	/**
	 * physically copies the 'original' File into a (new) 'copy' 
	 * @param original Source File
	 * @param copy Destination File 
	 * @throws FileNotFoundException if the Source does not exist or the Target is a Directory 
	 * @throws IOException on any Error
	 */
	final static public void COPY_FILE(final String original, final String copy) throws FileNotFoundException, IOException {
		COPY_FILE(new File(original), new File(copy)); 
	}

	/*
	Methods not in IStreamByte:
	long getFilePointer() in updateRow(),
	void seek(long)       in updateRow(), beforeFirst(), compress()
	long length()         in updateRow(), moveToInsertRow()
	*/
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** optional (null allowed) Flag and Message to throw when an IOException is caught	 */
	public String throwFailureExceptionMessage; 
	
	protected final IStreamIn_Int handleExceptionObject(final IOException x) {
		return FileStreamIn_Byte.HANDLE_EXCEPTION_OBJECT(x, throwFailureExceptionMessage); }
	
	protected final int handleException(final IOException x) {
		return FileStreamIn_Byte.HANDLE_EXCEPTION(x, throwFailureExceptionMessage); }
	
	/** Marker for the current Position in the File */
	protected long mark;
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variable 'Order' with Accessor Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** holds the Order of the Data in the File   */
	public byte Order;
	
	/** @return the Order of the Data in the File  */
	public byte getOrder() { return Order; }
	
	/** Reference to the File Object to be able to create an IntIterator	 */
	final public File file; 
	
	/** Reference to the File Mode to be able to create an IntIterator	 */
	final public String mode; 
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamIn_Int#IntIterator()	 */
	public IStreamIn_Int IntIterator() {
		try { return new FileStreamByte(file, mode);
		} catch (final FileNotFoundException x) {
			return handleExceptionObject(x);
		}
	}
	
	/** @see streamIO.IIterAble#Iterator()	 */
	public IIStreamIn Iterator() { return IntIterator(); }
    
    /** @see streamIO.real.IStreamIn_Float#FloatIterator()     */
    public IStreamIn_Float FloatIterator() { return IntIterator(); }
    
	/**
	  *	Creates a random access file stream to read from, and optionally to write to,
	  * a file with the specified name.
	  * A new FileDescriptor object is created to represent the connection to the file.
	  * The mode argument must either be equal to "r" or "rw",
	  * indicating that the file is to be opened for input only
	  * or for both input and output, respectively.
	  * The write methods on this object will always throw an IOException
	  * if the file is opened with a mode of "r".
	  * If the mode is "rw" and the file does not exist,
	  * then an attempt is made to create it.
	  * An IOException is thrown if the name argument refers to a directory.
	  *
	  * If there is a security manager, its checkRead method is called
	  * with the name argument as its argument to see if read access to the file is allowed.
	  * If the mode is "rw", the security manager's checkWrite method is also called
	  * with the name argument as its argument to see if write access to the file is allowed.
	  *
	  * @param name - the system-dependent filename.
	  * @param mode - the access mode.
	  * @throws IllegalArgumentException - if the mode argument is not equal to "r" or to "rw".
	  * @throws FileNotFoundException - if the file exists but is a directory rather than a regular file,
	  * 	or cannot be opened or created for any other reason
	  * @throws SecurityException - if a security manager exists and its checkRead method denies read access to the file
	  * 	or the mode is "rw" and the security manager's checkWrite method denies write access to the file.
	  * @see SecurityException
	  * @see SecurityManager.checkRead(java.lang.String)
	  * @see SecurityManager.checkWrite(java.lang.String)
	  */
	public FileStreamByte(final String name, final String mode) throws FileNotFoundException {
		this(new File(name), mode);
	}

	/**
	  * Creates a random access file stream to read from, and optionally to write to,
	  * the file specified by the File argument.
	  * A new FileDescriptor object is created to represent this file connection.
	  * The mode argument must either be equal to "r" or "rw", indicating that the file
	  * is to be opened for input only or for both input and output, respectively.
	  * The write methods on this object will always throw an IOException
	  * if the file is opened with a mode of "r".
	  * If the mode is "rw" and the file does not exist, then an attempt is made to create it.
	  * An IOException is thrown if the file argument refers to a directory.
	  *
	  * If there is a security manager, its checkRead method is called
	  * with the pathname of the file argument as its argument
	  * to see if read access to the file is allowed.
	  * If the mode is "rw", the security manager's checkWrite method is also called
	  * with the path argument to see if write access to the file is allowed.
	  *
	  * @param file - the file object.
	  * @param mode - the access mode.
	  *
	  * @throws IllegalArgumentException - if the mode argument is not equal to "r" or to "rw".
	  * @throws FileNotFoundException - if the file exists but is a directory rather than a regular file,
	  * 	or cannot be opened or created for any other reason
	  * @throws SecurityException - if a security manager exists and its checkRead method denies read access to the file
	  * 	or the mode is "rw" and the security manager's checkWrite method denies write access to the file.
	  * @see File.getPath()
	  * @see SecurityManager.checkRead(java.lang.String)
	  * @see SecurityManager.checkWrite(java.lang.String)
	  */
	public FileStreamByte(final File _file, final String _mode) throws FileNotFoundException {
		super(_file, _mode);
		this.mode =  _mode ; 
		this.file =  _file ; 
	}
	
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
	public long nextLong() { return nextInt(); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	  * Flushes this output stream and forces any buffered output bytes to be written out.
	  * The general contract of flush is that calling it is an indication that,
	  * if any bytes previously written have been buffered
	  * by the implementation of the output stream,
	  * such bytes should immediately be written to their intended destination.
	  *
	  * The flush method of OutputStream does nothing.
	  */
	public void flush() { } //cannot be enforced?!?

	/**
	  * Tests if this input stream supports the mark and reset methods.
	  * The markSupported method of InputStream returns false.
	  * @return true if this true type supports the mark and reset method; false otherwise.
	  * @see mark(int), reset()
	  */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }

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
	public IMarkAble mark() {
		try {
			mark = getFilePointer();
		} catch (final IOException x) {
			return handleExceptionObject(x);
		}
		return this;
	}
	
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
      * @see streamIO.IMarkAble#mark(long)     */
    public IMarkAble mark(final long readLimit) { mark(); return this; }
    
    /** @see streamIO.IReSetAble#jump()     */
    public IReSetAble jump() { return AReSetAble.JUMP(this); }
    
	/** 
	 * Jumps a single Position back in this Iterator.
     * equivalent to jump(-1); 
	 * @see streamIO.IReSetAble#pushBack()  
	 * @return this Stream if jumping worked, null otherwise. 
	 */
    public IPushBackAble pushBack() { return AReSetAble.PUSH_BACK(this); }
	
	/** @see streamIO.IAvailAble#availAble()     */
    public long availAble() {
        try { return length() - getFilePointer();
	    } catch (final IOException x) {
	    	return handleException(x);
		}
    }
    
    /** @see streamIO.IAvailAble#getPosition()     */
    public long getPosition() {
        try { return getFilePointer();
	    } catch (final IOException x) {
	    	return handleException(x);
		}
    }
	
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
	public IReSetAble reSet() { reSet(0); return this; }
	
    /** @see streamIO.IReSetAble#reSet(java.lang.String)     */
	public IReSetAble reSet(final String throwFailureExceptionMessage) { 
	    reSet(0, throwFailureExceptionMessage); return this; }
    
    /** @see streamIO.IReSetAble#reSet(java.lang.String)     */
	public long reSet(final long offset) { return reSet(offset, throwFailureExceptionMessage); }
    
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
	  * @see java.io.InputStream#reset()
	  */
	public long reSet(long rel, final String failureExceptionMessage) {
	    long abs = mark + rel; 
	    try {
	        final long pos = getFilePointer(); 
	        if (pos == abs)
	            return rel;
		    final long len = length();
			if (abs < 0) {
				rel -= abs;
				abs = 0;
			}
			if (abs > len) {
				rel -= (abs - len);
				abs = len;
			}
			seek(abs);
	    } catch (final IOException x) {
			return handleException(x); //typically the Return Value should be tested!!!
	    }
		return rel;
	}
	
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
	  * @param n - the number of bytes to be skipped, 
	  * must not be negative, use seek otherwise
	  * @return the actual number of bytes skipped.
	  */
	public long jump(final long n) { 
	    try { 
	    	if (n < 0) {
	    		seek(getFilePointer() - n); 
	    		return n; }
	    	return skipBytes((int) n);
	    } catch (final IOException x) {
	    	return handleException(x);
		}
	}
	
	/**
	  * Returns the number of bytes that can be read (or skipped over)
	  * from this input stream without blocking
	  * by the next caller of a method for this input stream.
	  * The next caller might be the same thread or or another thread.
	  *
	  * The available method for class InputStream always returns 0.
	  * This method should be overridden by subclasses.
	  *
	  * @return the number of bytes that can be read from this input stream without blocking.
	  *         The Type int is determined by the InputStream Interface.
	  * @throws IOException - if an I/O error occurs.
	  */
	public int available() {
		try { return (int) (length() - getFilePointer());
	    } catch (final IOException x) {
	    	return handleException(x);
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: Methods handling Characters and Strings
	////////////////////////////////////////////////////////////////////////////////

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
	public int read(final char[] b, final int off, final int len) throws IOException {
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
	public StringBuffer read(final int Sep, final StringBuffer b) throws IOException {
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
	public StringBuffer read(final int Sep) throws IOException {
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
	public int read(final int[] b) throws IOException {
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
	public int read(final int[] b, final int off, final int len) throws IOException {
		int val, i = -1;
		while (++i < len) {
			b[off + i] = (char) (val = read());
			if (val == EOF) {
				return i - 1;
			}
		}
		return i - 1;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: Methods handling Characters and Strings
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes b.length bytes from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public void write(final byte[] b) throws IOException {
		write(b, 0, b.length); }

	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @see write(byte[], int, int)
	  */
	public void write(final char[] b) {
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
	  */
	public void write(final char[] b, final int off, final int len) {
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
	public void write(final String b) throws IOException {
		write(b, 0, b.length());
	}

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
	  * @see java.io.Writer#write(String)
	  */
	public void write(final String b, final int off, final int len) throws IOException {
		AStreamOutByte.WRITE(this, b, off, len);
	}

	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	public void addItem(final int[] b) throws IOException {
		addItem(b, 0, b.length); }
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

	/** @see streamIO.Byte.IStreamOutByte#addString(StringBuffer, int, int)	 */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop, final int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start); 
		return this; }

	/** @see streamIO.Byte.IStreamOutByte#addString(StringBuffer)	 */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop) {
		return addBuffer(b, stop, 0); }

	/** @see streamIO.Byte.IStreamOutByte#addString(StringBuffer)	 */
	public IStreamOutByte addBuffer(final StringBuffer b) {
		return addBuffer(b, b.length()); }
	
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
	
    /** @see streamIO.real.IStreamIn_Float#nextDouble()     */
    public double nextDouble() { return nextInt(); }
    
    /** @see streamIO.real.IStreamIn_Float#nextFloat()     */
    public float nextFloat() { return nextInt(); }
    
    /** @see streamIO.IIStreamIn#isValid()     */
    public boolean isValid() { return currItem.Value != EOF; }
    
    /** @see #nextItem() returns this Object or null; 	*/
    final public ByRefInt currItem = new ByRefInt();
    
    /** @see streamIO.IFactory#nextItem()     */
    public Object nextItem() {
        if (EOF == (currItem.Value = nextInt())) 
            return null; 
        return currItem; //new Integer(nextInt());
    }
    
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
