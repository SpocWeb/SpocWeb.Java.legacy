package streamIO.copy.order;

/**Adds the Capability to compare double Numbers directly */
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
