package streamIO.integer;

import streamIO.real.IStreamIn_Float;
import stringOp.parser.IIStreamIn_Int;

/** Interface for a streamIO of integer Numbers
  * e.g. for a Random Number Generator
  * The Generator is implemented with primitive Types,
  * because of Performance Reasons.
  * The same Plethora of Classes as for IStreamIn and IStreamOut
  * can be implemented for this Interface, and even more,
  * because this Type is passive and more Operations can be predefined.
  * The Output Range may not be normed,
  * @see IRandomInt for the Method MaxValue()
  * 
  * known Subclasses: 
  * @see IStreamIn_Byte which extends this Interface for Parsing 
  * <!-- docstate
  * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
  * concepts: [Primitive and Structured Stream I/O Core Abstractions]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public interface IStreamIn_Int 
extends IIStreamIn_Int, IStreamIn_Float 
{
	
	/**
	 * fills the given Array from the start(inclusive) to the stop(exclusive) Index
	 * this is more effective than calling the Generator several times. 
	 * @param arr the Array to fill 
	 * @param stop the first Index NOT to fill (exclusive) 
	 * @param start the first Index to fill (inclusive) 
	 * @return the Number of Items filled
	 */
	public int fill(final int[] arr, final int stop, final int start); 
	
	/** Reads and returns the next {@code long} value.
	 * @return the next Long Number (converts IOException into a RuntimeException) 	 */
	public long nextLong();

	/** Returns the current {@code long} value, without advancing.
	 * @return the current Long Number 	 */
	public long currLong();

	/** Returns the current {@code int} value, without advancing.
	 * @return the current Integer Number 	 */
	public int currInt();

	/** Reads the next {@code long} value without advancing the stream position.
	 * @return the next Value without moving to it.	 */
	public long peekLong(); //throws    NoSuchMethodException;

	/** Reads the next {@code int} value without advancing the stream position.
	 * @return the next Value without moving to it.	 */
	public int peekInt(); //throws    NoSuchMethodException;
	
	/** This is a type-safe Substitute for clone()
	 * returns a new Iterator for the same Base Set
	 * @return a new Iterator for the same Base Set
	 */
	public IStreamIn_Int IntIterator(); 
	
}
