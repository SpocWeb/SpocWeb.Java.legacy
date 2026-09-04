package streamIO.copy.order;

import streamIO.copy.ICopyAble;
import function.IOrderAble;

/**OrderAble:
 * Interface for a Class whose Objects have a strict Order Relation ">" resp. "<".
 * Complements the pure virtual Interface 'intOrderable'.
 * If a Set is not ordered completely, these Relations are not defined
 * for any two Elements. In this Case, both >= and <= give False.
 * When the Element is the same or equivalent, both >= and <= give True.
 * When the Element is comparable, only one gives True.
 *
 * Absolute Value is only important because of the Metric defined by "<".
 * A Default Implementation is done in 'AOrderable'.
 */
public interface IOrder
extends IOrderAble, ICopyAble {

	/** Returns the Maximum of both Operands
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public IOrder Max (Object arg);

	/** Returns the Minimum of both Operands
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public IOrder Min (Object arg);

	/** Returns the Maximum of both Operands in Place
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public IOrder MaxAt (Object arg);

	/** Returns the Minimum of both Operands in Place
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public IOrder MinAt (Object arg);

}
