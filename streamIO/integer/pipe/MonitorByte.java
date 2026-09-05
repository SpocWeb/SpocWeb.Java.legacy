package streamIO.integer.pipe;

import java.io.IOException;

import streamIO.Assert;
import streamIO.integer.AStreamByte;
import streamIO.integer.IStreamByte;
import streamIO.integer.StreamIn_Arithmetic;
import streamIO.object.IStreamIn;

/** This Class allows undisturbed streamIO communication between two Threads
  * using a pipe-like Mechanism of writing and reading individual Chars
  * to an unbuffered Object.
  * 
  * By making the Object 'Remote'
  * you can build up a Pipe between two Processes or Machines.
  * 
  * With this Class the two Threads are completely synchronized when exchanging Data,
  * which may lock up one Threads unnecessarily and molest the Processor with
  * handling a locked thread for longer than necessary.
  * On separate Processes this is even worse, because one Process is inactive
  * and can not go on working effectively.
  * 
  * Using a Queue like in 'Pipe', the two Processes can become more independent
  * and can work much more effectively from the Batch Extreme
  * to the continuous Realtime Extreme.
  * 
  * <!-- docstate
  * tags: [code/pipe_abstraction, code/pipe_implementation]
  * concepts: [In-Memory Producer-Consumer Byte Pipes]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class MonitorByte
extends AStreamByte
{   
	//Streams are not Interfaces, but abstract Classes!!

	/** Runs {@link #testIt()} when invoked with no arguments.
	 * @param args
	 */
	public static void main(final String[] args) throws IOException {
		if (args.length == 0)
			testIt(); 
	}
	
	/**
	 * tests this Class by writing and reading concurrently. 
	 * @param args
	 */
	public static void testIt() throws IOException {
		IStreamByte monitor = new MonitorByte(); 
		Thread streamer = new ByteStreamerThread(new StreamIn_Arithmetic(0, 100), monitor);
		streamer.start(); //start writing from the Stream into the Monitor
		//now read from the Monitor with some Delay...
		for(int val, i = 0; 0 <= (val = monitor.read()); ++i) {
			Assert.EQUALS(i, val); 
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	// static Members
	////////////////////////////////////////////////////////////////////////////
	
	/** Default Timeout in Milliseconds for Operations on a Monitor	*/
	final static public long DEFAULT_TIMEOUT_MILLIS = 100; 
	
	/** Default Timeout in Milliseconds for Write Operations on a clogged new Monitor
	 * slightly larger than the Read Timeout, so the next Monitor is freed, 
	 * before the current Monitor times out for Writing.	*/
	public static long DEFAULT_TIMEOUT_MILLIS_WRITE = 10*DEFAULT_TIMEOUT_MILLIS; 
	
	/** Default Timeout in Milliseconds for Read Operations on an empty new Monitor	*/
	public static long DEFAULT_TIMEOUT_MILLIS_READ = 10*DEFAULT_TIMEOUT_MILLIS;
	
	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////
	
	/** Timeout for Write Operations on a clogged Monitor	*/
	public long timeoutMillisWrite = DEFAULT_TIMEOUT_MILLIS_WRITE; 
	
	/** Timeout for Read Operations on an empty Monitor	*/
	public long timeoutMillisRead  = DEFAULT_TIMEOUT_MILLIS_READ;
	
	/**
	 * current Value to be read.
	 */
	protected int Token;
	
	/**
	 * Flag to indicate that the Token is set.
	 */
	protected boolean valueSet = false;
	
	/**
	 * Flag to indicate that the Pipe is closed.
	 * Use this Flag to raise an IOException
	 *  when the streamIO was closed,
	 *  instead of putting the current Thread to sleep!
	 */
	protected boolean closed = false;
	
	/**
	 * Flag to indicate that the Pipe is marked.
	 * Use this Flag to raise an IOException
	 *  when the streamIO was written to after mark() was called
	 *  and a reset() is requested.
	 */
	protected boolean marked = true;
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/** Constructs a MonitorByte using the default read/write timeouts. */
	public MonitorByte() {
	}

	/** Constructs a MonitorByte with explicit read/write timeouts.
	 * @param _timeoutMillisWrite
	 * @param _timeoutMillisRead
	 */
	public MonitorByte(long _timeoutMillisWrite, long _timeoutMillisRead) {
		this.timeoutMillisWrite = _timeoutMillisWrite;
		this.timeoutMillisRead  = _timeoutMillisRead;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variable 'Order' with Accessor Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** holds the Order of the Data in the streamIO   */
	protected byte Order = IStreamIn.ORDER_NONE;
	
	/** Returns the Order of the Data in the streamIO.
	 * @return the Order of the Data in the streamIO, called by the Client  */
	public byte getOrder() { return Order; }
	
	/** Sets the Order of the Data in the streamIO, called by the Server  */
	public void setOrder(byte Order_) { this.Order = Order_; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	  * Writes the specified value to this output stream.
	  * When a Value is already set, the writing Thread is put to sleep.
	  *
	  * Subclasses of OutputStream must provide an implementation for this method.
	  *
	  * @param value - the byte or int Value to be written.
	  * @throws IOException - if an I/O error occurs.
	  *  In particular, an IOException may be thrown
	  *  if the output stream has been closed.
	  */
	public void write(final int value) throws IOException {
		write(value, timeoutMillisWrite); 
	}
		
	/**
	  * Writes the specified value to this output stream.
	  * When a Value is already set, the writing Thread is put to sleep.
	  *
	  * Subclasses of OutputStream must provide an implementation for this method.
	  *
	  * @param value - the byte or int Value to be written.
	  * @throws IOException - if an I/O error occurs.
	  *  In particular, an IOException may be thrown
	  *  if the output stream has been closed.
	  */
	public synchronized void write(final int value, final long timeout) throws IOException {
		if (valueSet) { //use 'while', because several Threads could 'notify()' this Class
			try { wait(timeout); // until notifiy()
			} catch (final InterruptedException e) {
			}
			if (closed) 
				throw new IOException("Stream is closed!"); 
			if (valueSet)
				throw new IOException("Stream is clogged!"); 
		}
		valueSet = true;
		marked = false; //the mark cannot be held without Blocking! 
		Token = value; 
		notify(); } 

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
	public void flush() {} //throws IOException;

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
	public void close() { //throws IOException {
		closed = true; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Reads the next Value from the input stream.
	  * When no Value is available, the requesting Thread is put to sleep.
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
	public int read() throws IOException {
		return read(timeoutMillisRead); 
	}
	
	/**
	  * Reads the next Value from the input stream.
	  * When no Value is available, the requesting Thread is put to sleep.
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
	public synchronized int read(final long timeout) throws IOException {
		if (! valueSet) { //use 'while', because several Threads could 'notify()' this Object
			try { wait(timeout); // until notify()ed
			} catch (InterruptedException e) {
			}
			if (closed) {
				throw new IOException("Stream is closed!"); }
			if (!valueSet) //after Timeout
				throw new IOException("Stream is empty!"); 
		}
		valueSet = false;
		notify();
		return Token; }

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
	public long nextLong() { //throws IOException {
		return nextInt(); }

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
	public int available() { //throws IOException;
		if (closed || !valueSet) {
			return 0; }
			return 1; }
	
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
	  *  before the mark position becomes invalid.
	  * @see reset()
	  */
	public void mark(final int readlimit) { marked = true; }
	
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
	public long reSet(final long _position) {
		if (_position == 1) 
		    return 1; //don't do anything
		if((_position == 0) && (marked)){ //reset to the last Character
			valueSet = true; return 0; }
		throw new IllegalStateException("Cannot reset! Pipe was written to afterwards..."); 
	} //
	
	/**
	  * Tests if this input stream supports the mark and reset methods.
	  * The markSupported method of InputStream returns false.
	  * This Monitor allows to reset only by the single Character last read
	  * and thus works similar to the pushBack() Method of Scanners and Tokenizers.
	  * This is sufficient for LL(1) Grammars!
	  * @return true if this true type supports the mark and reset method; false otherwise.
	  * @see mark(int), reset()
	  */
	public long getMaxMarkSize() { return 1; } //Long.MAX_VALUE; } 
	
	/** Returns 0 while marked, 1 once a Value has been written past the mark.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return marked ? 0 : 1; } //stream.getPosition(); }
	
}
