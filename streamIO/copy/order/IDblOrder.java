package streamIO.copy.order;

/**Adds the Capability to compare double Numbers directly
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 14f278f2569831dcdbdebb362db089dd494ab58c5789e512a920dcbd136b9808
 * stale: false
 * tags: [code/numeric_comparison]
 * concepts: [Order Relation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IDblOrder
extends ILngOrder {

	/** Comparison with a double Number: < arg
	 * @param arg Object to compare with 'this'
	 * @return <CODE>true</CODE> when <CODE>this</CODE> < <CODE>arg</CODE>
	 */
	public boolean less(final double arg);

	/** Comparison with a double Number: > arg
	 * @param arg Object to compare with 'this'
	 * @return <CODE>true</CODE> when <CODE>this</CODE> > <CODE>arg</CODE>
	 */
	public boolean grtr(final double arg);

	/** Comparison with a double Number: <= arg
	 * @param arg Object to compare with 'this'
	 * @return <CODE>true</CODE> when <CODE>this</CODE> <= <CODE>arg</CODE>
	 */
	public boolean lessEq(final double arg);

	/** Comparison with a double Number: >= arg
	 * @param arg Object to compare with 'this'
	 * @return <CODE>true</CODE> when <CODE>this<CODE> >= </CODE>arg</CODE>
	 */
	public boolean grtrEq(final double arg);

	/** Maximum:
	 * @param arg Object to compare with 'this'
	 * @return the Maximum of 'this' and 'arg' in a new Instance.
	 */
	public IDblOrder Max	(final double arg);

	/** Minimum:
	 * @param arg Object to compare with 'this'
	 * @return the Minimum of 'this' and 'arg' in a new Instance.
	 */
	public IDblOrder Min	(final double arg);

	/** Maximum in Place:
	 * @param arg Object to compare with 'this'
	 * @return the Maximum of 'this' and 'arg' in Place.
	 */
	public IDblOrder MaxAt (final double arg);

	/** Minimum in Place:
	 * @param arg Object to compare with 'this'
	 * @return the Minimum of 'this' and 'arg' in Place.
	 */
	public IDblOrder MinAt (final double arg);

}
