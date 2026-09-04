package streamIO.real;

import java.io.IOException;

import streamIO.IReSetAble;
import streamIO.object.IPipe;

/**
  * Title: StreamIn_Geometric<p>
  * Description:
  * IStreamIn_Float of Float Point Numbers N
  * starting with 1 (Default),
  * incrementing by a Factor of 2 (Default).
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-12-2002, 09:05 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class StreamIn_Geometric
extends AStreamIn_Float {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Local Cache for the Value mark()ed */
	protected double markValue; //= 0; //unnecessary
	
	/** Local Cache for the last Value to be returned */
	protected double stopValue = Double.MAX_VALUE;
	
	/** Local Cache for the Factor used to produce the next Number. */
	protected double factor = 2;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the minimum Value for other Classes to determine
	  * The Type is chosen to be double,
	  * because this Value is supposed to be tested only once.  */
	public double getMinDouble() { return markValue; }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	protected StreamIn_Geometric() { } //this (0, Long.MAX_VALUE); }
	
	/** Initializing Constructor	 */
	public StreamIn_Geometric(final double startValue_) {
		currItem.Value = markValue = (startValue_/ factor); } //this (startValue, Long.MAX_VALUE); }
	
	/** Initializing Constructor	 */
	public StreamIn_Geometric(final double startValue_, final double Factor_) {
		currItem.Value = markValue = (startValue_ / factor);
		this.factor = Factor_; }
	
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
	public long availAble() { return (long) (Math.log(stopValue/currItem.Value) / Math.log(factor)); }
	
	/** @see streamIO.real.AStreamIn_Float#getPosition()	 */
	public long getPosition() { return (long) (Math.log(currItem.Value/markValue) / Math.log(factor)); }

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
	public double nextDoubleInternal() { return currItem.Value * factor; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////
		
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() {
		if (factor < 0) {
			return IPipe.ORDER_ALTERNATING; }
		if((factor >= 1) == (currItem.Value >= 0)) 
			return IPipe.ORDER_ASC ; 
			return IPipe.ORDER_DESC; }
	
	/**Returns the current Object that was returned from the last nextItem() Method.	 */
	public double currDouble() { return currItem.Value; } //this; } //saves expensive creation of new Objects
	
	//Marking and Resetting a Stream (for re-Processing, if supported)
	
	/**Skips over and discards n Items from this Iterator.
	 * Returns the actual number of bytes skipped.
	 * This dumb Implementation just reads all Elements and discards them.	 */
	public long jump(final long Position) {
		currItem.Value *= Math.pow(factor, Position); return Position; }
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		currItem.Value = markValue; return this; }
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public long reset(final long Position) { //throws NoSuchMethodException {
		currItem.Value = markValue; return jump(Position); }
	
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public IStreamIn_Float mark(final int ReadLimit) { //throws NoSuchMethodException {
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
		System.out.println("Testing " + StreamIn_Geometric.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
