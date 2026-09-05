package streamIO.real;

/** Extends {@link IStreamIn_Float} with known lower and upper bounds, letting callers scale
  * or normalize the generated numbers.
  *
  * <p>e.g. for a Random Number Generator
  * The Generator is implemented with primitive Types,
  * because of Performance Reasons.
  * The same Plethora of Classes as for IStreamIn and IStreamOut
  * can be implemented for this Interface, and even more,
  * because this Type is passive and more Operations can be predefined.
  * It is always possible to build a Wrapper around the Random Generator.
  * But unlike with Matrix Operations this is usually not wanted,
  * because most Simulations need O(N*N) Items to increase Result Significance
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:32Z
  * digest: 23076488ee30c35afc1f77fe3c39eb4f68b47f9bfb5124497ab1b596cadbf7ee
  * stale: false
  * tags: [code/stream_filter]
  * concepts: [Bounded Float Stream Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  * @see <{streamIO.Float.IStreamIn_Float}>*/
public interface IStreamIn_Bound_Float 
extends IStreamIn_Float {

	/** 
	 * for other Objects to determine the minimum Value returned by nextFloat().
	 * @return the minimum Value for integer Numbers 
	 */
	public double getMinDouble();

	/** 
	 * for other Objects to determine the maximum Value returned by nextFloat().
	 * @return the maximum Value for integer Numbers 
	 */
	public double getMaxDouble();

	/**Random single Precision Number from 0 to MaxFloat	 */
	public float nextFloat(final float maxFloat);

	/**Random double Precision Number from 0 to MaxDouble	 */
	public double nextDouble(final double maxDouble);

	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	public int fillArray(final float[] arr, final int start, final int stop);
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	public int fillArray(final float[] arr, final float maxVal, final int start, final int stop);
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 */ 	
	public int fillArray(final float[] arr, final float maxVal);
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 */ 	
	public int fillArray(final float[] arr); 
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	public int fillArray(final double[] arr, final int start, final int stop);
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 */ 	
	public int fillArray(final double[] arr, final float maxVal, final int start, final int stop); 
		
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 */ 	
	public int fillArray(final double[] arr, final float maxVal);
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 */ 	
	public int fillArray(final double[] arr);
	
}
