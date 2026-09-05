package streamIO.copy.order;

/**Adds the Capability to compare long Numbers directly
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: ec997d32d0a6169bcae9b3c0e595440e3c54adb827a5da8bb42c4a15f281a9b6
 * stale: false
 * tags: [code/numeric_comparison]
 * concepts: [Order Relation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
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
