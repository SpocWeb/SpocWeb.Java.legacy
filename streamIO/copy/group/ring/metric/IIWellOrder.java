package streamIO.copy.group.ring.metric;

/**IWellOrder:
 * Interface for a Class whose Objects are well and connex ordered
 * by Relations ">"resp."<".
 * Connex means that these Relations are defined for any two Elements.
 * I.e. there is a largest Element, which is also the maximum Element.
 * In Addition there are maximum and minimum Values for this Class.
 */

public interface IIWellOrder {

	/**Sets and returns the maximum Value for this Class in Place.	 */
	public IWellOrder maxValueAt();

}
