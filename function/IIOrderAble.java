package function;

/**IOrderable:
 * Interface for a Class whose Objects have a strict Order Relation ">" resp. "<"
 * If a Set is not ordered completely, these Relations are not defined
 * for any two Elements. In this Case, both >= and <= give False.
 * When the Element is the same or equivalent, both >= and <= give True.
 * When the Element is comparable, only one gives True.
 *
 * Absolute Value is only important because of the Metric defined by "<".
 */
public interface IIOrderAble {

	/** less: '<' Returns True, when 'Self' < arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	boolean isLessThan (Object arg);

}
