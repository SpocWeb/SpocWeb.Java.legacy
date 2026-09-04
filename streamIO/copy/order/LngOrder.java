package streamIO.copy.order;

/**Adds the Capability to compare long Numbers directly */
public interface LngOrder {

	/** Comparison with a long Number: < arg
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean less(long arg);

	/** Comparison with a long Number: > arg
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean grtr(long arg);

	/** Comparison with a long Number: < arg
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean lessEq(long arg);

	/** Comparison with a long Number: > arg
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean grtrEq(long arg);

	/** Maximum in Place:
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public LngOrder Max (long arg);

	/** Minimum in Place:
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public LngOrder Min (long arg);

	/** Maximum in Place:
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public LngOrder MaxAt (long arg);

	/** Minimum in Place:
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public LngOrder MinAt (long arg);

}
