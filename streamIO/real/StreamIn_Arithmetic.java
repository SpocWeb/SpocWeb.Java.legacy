package streamIO.real;

import java.io.IOException;

import streamIO.IReSetAble;
import streamIO.object.IStreamIn;

/**
  * Generates a strictly monotonous arithmetic progression of numbers.
  *
  * <p>streamIO of Numbers N <br/>
  * natural Numbers starting with 1  (by Default),
  * incrementing by a fixed Amount (1 by Default)
  * and ranging up to Long.MAX_VALUE (by Default)
  * strictly monotonous
  *
  * Known SubClasses: <none>
  *
  * @see streamIO.Byte.StreamIn_Arithmetic implementing StreamIn_Int
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-12-2002, 09:05 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:22:25Z
  * digest: 38cf95585e5de4f6c01a3bdeb1d502324ab1f9ab2b95693d02d5b9bf483ca70f
  * stale: false
  * tags: [code/stream_filter]
  * concepts: [Arithmetic Sequence Stream]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class StreamIn_Arithmetic
extends AStreamIn_Float {
	
	////////////////////////////////////////////////////////////////////////////////
	// Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Local Cache for the Value mark()ed */
	protected double markValue; //= 0; //unnecessary
	
	/** Local Cache for the last Value to be returned */
	protected double stopValue = Double.MAX_VALUE;
	
	/** Local Cache for the Increment used to produce the next Number. */
	protected double increment = 1;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the sequence's start bound, which is its minimum when the increment is positive.
	  * @return the minimum Value for other Classes to determine
	  * The Type is chosen to be double,
	  * because this Value is supposed to be tested only once.  */
	public double getMinDouble() {
		if (increment > 0)
			return markValue; 
		return stopValue; 
	}

	/** Returns the sequence's stop bound, which is its maximum when the increment is positive.
	  * @return the maximum Value for other Classes to determine
	  * The Type is chosen to be double,
	  * because this Value is supposed to be tested only once.  */
	public double getMaxDouble() {
		if (increment > 0)
			return stopValue; 
		return markValue; 
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public StreamIn_Arithmetic() { this (0); } 
	
	/** Initializing Constructor	 */
	public StreamIn_Arithmetic(final double startValue_) {
		this(startValue_, Long.MAX_VALUE); 
	}
	
	/** Initializing Constructor	 */
	public StreamIn_Arithmetic(final double startValue_, final double stopValue_) {
		this(startValue_, stopValue_, 1); 
	}
	
	/** Initializing Constructor	 */
	public StreamIn_Arithmetic(double startValue_, double stopValue_, double increment_) {
		this.increment = increment_;
		this.stopValue = stopValue_; 
		currItem.Value = markValue = (startValue_ - increment_);
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
	 * @see streamIO.real.AStreamIn_Float#availAble()	 
	 */
	public long availAble() { return (long) ((stopValue - currItem.Value) / increment); }
	
	/** Returns the number of increments applied since the last mark.
	 * @see streamIO.real.AStreamIn_Float#getPosition()	 */
	public long getPosition() { return (long) ((currItem.Value - markValue) / increment); }

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
	public double nextDoubleInternal() {
		if (((currItem.Value += increment) <= stopValue) == (increment >= 0)) {
			return currItem.Value; } //this; } //reuse of 'Value' saves expensive creation of new Objects
		return EOS; }
	
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
	
	/** Returns ascending or descending order, depending on the sign of the increment.
	 * @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() {
		if (increment >= 0) {
			return IStreamIn.ORDER_ASC ; }
			return IStreamIn.ORDER_DESC; }
	
	//Marking and Resetting a Stream (for re-Processing, if supported)
	
	/**Skips over and discards n Items from this Iterator.
	 * Returns the actual number of bytes skipped.
	 * This dumb Implementation just reads all Elements and discards them.	 */
	public long jump(final long _pPosition) {
		currItem.Value += _pPosition*increment; return _pPosition; }
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public long reset(final long _position) { //throws NoSuchMethodException {
		currItem.Value = markValue + _position*increment; return _position; }
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		currItem.Value = markValue; return this; }
	
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public IStreamIn_Float mark(final int readLimit) { //throws NoSuchMethodException {
		markValue = currItem.Value; return this; }
	
	/**
	  * Tests if this input stream supports the mark and reset methods.
	  * The markSupported method of InputStream returns false.
	  * @return true if this true type supports the mark and reset method; false otherwise.
	  * @see mark(int), reset()
	  */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + StreamIn_Arithmetic.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
