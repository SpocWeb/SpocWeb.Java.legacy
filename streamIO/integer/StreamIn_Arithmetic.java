package streamIO.integer;

import java.io.IOException;

import streamIO.Assert;
import streamIO.Log;
import streamIO.object.IStreamIn;

/**
  * Title: StreamIn_Arithmetic<p>
  * Description:
  * streamIO of Numbers N
  * natural Numbers starting with 1 (Default),
  * incrementing by 1 (Default)
  * and ranging up to Long.MAX_VALUE (Default)
  * strictly monotonous.
  *
  * This Class could also be defined in Package streamIO.Float
  * due to the Interface IStreamIn_Int defined there!
  *
  * Known SubClasses: <none>
  *
  * @see streamIO.Float.StreamIn_Arithmetic implementing StreamIn_Float
  *
  * Design Decisions:
  * Formerly known as NumberStream
  * also used as a Substitute for CharacterStream,
  * which is now simulated by a CharacterFilter applied on a StreamIn_Arithmetic.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-12-2002, 09:05 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class StreamIn_Arithmetic 
extends AStreamIn_Byte {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Local Cache for the last Value to be returned */
	protected final long stopValue; // = Long.MAX_VALUE;
	
	/** Local Cache for the Increment used to produce the next Number. */
	protected final long increment;
	
	/** Local Reference to the Object returned by nextItem() */
	protected long value = 0;
	
	/** Local Cache for the Value mark()ed */
	protected long markValue; //= 0; //unnecessary
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	protected StreamIn_Arithmetic() { this (0); }
	
	/** Initializing Constructor	 */
	public StreamIn_Arithmetic(final long _startValue) {
		this (_startValue, Long.MAX_VALUE); }
	
	/** Initializing Constructor	 */
	public StreamIn_Arithmetic(final long _startValue, final long _stopValue) {
		this (_startValue, _stopValue, 1); }
	
	/** Initializing Constructor	 */
	public StreamIn_Arithmetic(final long _startValue, final long _stopValue, final long _increment) {
		this (_startValue, _stopValue, _increment, _startValue-_increment); }
	
	/** Initializing Constructor	 */
	protected StreamIn_Arithmetic(final long _startValue, final long _stopValue, final long _increment, final long _markValue) {
		this.value = _startValue - _increment; //due to pre-Increment
		this.markValue = _markValue;
		this.increment = _increment;
		this.stopValue = _stopValue;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the (minimum) Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 *
	 * Nearly equivalent is currItem != null
	 * (when the Container does not contain null Entries, like e.g. HashTables)
	 */
	public int available() {
		final int left = (int) (stopValue - value - increment); 
		if (increment == 1) 
			return left;
		return left / (int) increment;
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
	  * @throws IOException - if an I/O error occurs.
	  */
	public long nextLong() {
		//if (((value += Increment) >= stopValue) == (Increment > 0)) //unfortunately not correct!
		//value = stopValue - increment; //this falsifies the available() Method! 
		if (increment > 0) {
			if ((value += increment) >= stopValue) 
				return EOF;
		} else {
			if ((value += increment) <= stopValue) 
				return EOF;
		}
		return value; //this; } //
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  *
	  * Reference to the ByRefChar would stay the same,
	  * that's why you cannot simply use clone().
	  *
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	//public IStreamIn Iterator() {
	//	return new StreamIn_Arithmetic( Value.Value, stopValue, Increment, markValue); }

	/** @return true, when the Items returned support the OrderAble Interface
	  * and they are returned in (strictly) ascending or descending Order.
	  * This is used as an additional criterion for Search Operations like findFirst()
	  * It is replaced by the @see Pipe.getOrder() Method: ordered, random, sorted
	  * Monotonous is implicitly sorted!	 */
	//	public boolean isMonotonous() { return true; }
	
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() {
		if (increment >= 0) {
			return IStreamIn.ORDER_ASC;
		}
		return IStreamIn.ORDER_DESC;
	}
	
	/**Returns the current Object that was returned from the last nextItem() Method.	 */
	public long currLong() { return value; } //this; } //saves expensive creation of new Objects
	
	//Marking and Resetting a Stream (for re-Processing, if supported)
	
	/**Skips over and discards n Items from this Iterator.
	 * Returns the actual number of bytes skipped.
	 * This dumb Implementation just reads all Elements and discards them.	 */
	public long jump(final long _position) {
		value += _position*increment;
		return _position;
	}
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public long reSet(final long position) { //throws NoSuchMethodException {
		value = markValue + position*increment;
		return position;
	}
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return (value - markValue)/increment; } //
	
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public void mark(final int ReadLimit) { //throws NoSuchMethodException {
		markValue = value;
	} //return this; }
	
	/**
	  * Tests if this input stream supports the mark and reset methods.
	  * The markSupported method of InputStream returns false.
	  * @return true if this true type supports the mark and reset method; false otherwise.
	  * @see mark(int), reset()
	  */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
	
	/**
	  * Closes this input stream and releases any system resources associated with the stream.
	  * The close method of InputStream does nothing.
	  *
	  * @throws IOException - if an I/O error occurs.
	  */
	public void close() {} //throws IOException;
	
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
	  */
	public int read() {
		return (int) nextLong(); 
	} //throws IOException;
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		Log.N("Testing " + StreamIn_Arithmetic.class.getName());
		testIt(+1); 
		testIt(-1); 
	} //
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final int increment) throws java.io.IOException {
		Log.N("Testing empty Loop"); 
		for (int i = -3; ++i < 3;) {
			StreamIn_Arithmetic stream = new StreamIn_Arithmetic(i, i, increment); 
			assertStreamEmpty(stream); 
			stream = new StreamIn_Arithmetic(i, i+increment, increment); 
			Assert.EQUALS(stream.available(), 1); 
			Assert.EQUALS(stream.nextInt(), i); 
			assertStreamEmpty(stream); 
		} 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	protected static void assertStreamEmpty(final IStreamIn_Byte stream) throws java.io.IOException {
		Assert.EQUALS(stream.available(), 0); 
		if ((stream.nextInt() != EOF) || (stream.available() >= 0))
			Assert.FAIL("Stream should be empty!");
		Assert.EQUALS(stream.available(), -1); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws java.io.IOException {
		testIt();
	}
	
}
