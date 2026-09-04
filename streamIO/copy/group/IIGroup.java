package streamIO.copy.group;

/**Group (M,+,-,0):
 * Defines the most basic Interface necessary for an additive Group: '-='.
 * All other operations are only Shortcuts and can be defined using '-='.
 *
 * Design Decisions:
 * Normally Negation is sufficient to define anything else as an operation
 * Only for ZERO you need any number to define it.
 *
 * You can use either Negation of Subtraction to define the operations.
 * In any Case you better redefine both for Performance.
 * Could also be called 'subtractable' */
public interface IIGroup {

	/**Negation in Place: -
	 * This virtual Operation has to be implemented by each concrete Subclass.	 */
//	public IGroup negAt();

	/**Subtraction in Place: -=
	 * This virtual Operation has to be implemented by each concrete Subclass.	 */
	public IGroup subAt(final Object arg); //throws CloneNotSupportedException;

}
