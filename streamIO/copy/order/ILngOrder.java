package streamIO.copy.order;

/**Adds the Capability to compare long Numbers directly */
public interface ILngOrder {

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
	public ILngOrder Max (long arg);

	/** Minimum in Place:
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public ILngOrder Min (long arg);

	/** Maximum in Place:
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public ILngOrder MaxAt (long arg);

	/** Minimum in Place:
	 * @param arg : long Number to compare to <CODE>this</CODE>
	 * @return
	 */
	public ILngOrder MinAt (long arg);

}
