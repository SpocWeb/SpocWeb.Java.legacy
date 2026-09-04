package streamIO.integer.random;

import streamIO.integer.IStreamIn_Int;
import streamIO.real.IStreamIn_Bound_Float;

/** Interface for a for a streamIO of bounded Numbers
  * e.g. for Random Number Generators.
  * The Generator is implemented with primitive Types,
  * because of Performance Reasons.
  * The same Plethora of Classes as for IStreamIn and IStreamOut
  * can be implemented for this Interface, and even more,
  * because this Type is passive and more Operations can be predefined.
  * The Assumption is that the Output Range is normed to [0..MaxValue)
  * It is always possible to build a Wrapper around the Random Generator.
  * But unlike with Matrix Operations this is not wanted.  */
public interface IStreamIn_Bound_Int
extends IStreamIn_Int, IStreamIn_Bound_Float {
	
	/** @return the minimum Value for integer Numbers 
	 * for other Classes to determine the minimum Value 
	 * returned by the Method nextLong().
	 * When using other Methods like getInt(),
	 * the Values are bounded of course by the Type used. 
	 */
	public long getMinValue();
	
	/** @return the maximum Value for integer Numbers 
	 * for other Classes to determine the maximum Value 
	 * returned by the Method nextLong().
	 * When using other Methods like getInt(),
	 * the Values are bounded of course by the Type used. 
	 */
	public long getMaxValue();
	
	/**Random Integer Number from 0 to MaxInt-1	 */
	public int nextInt(final int maxInt);
	
	/**Random Integer Number from 0 to MaxLong-1	 */
	public long nextLong(final long maxLong); 
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	public void fillArray(final long[] arr, final int start, final int stop); 
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	public void fillArray(final long[] arr, final long maxVal, final int start, final int stop); 
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 */ 	
	public void fillArray(final long[] arr, final long maxVal);
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 */ 	
	public void fillArray(final long[] arr);
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	public void fillArray(final int[] arr, final int start, final int stop);
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 */ 	
	public void fillArray(final int[] arr, final int maxVal, final int start, final int stop);
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 */ 	
	public void fillArray(final int[] arr, final int maxVal);
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 */ 	
	public void fillArray(final int[] arr);
	
}
