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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: e9682101424d22dd4600dec48319c6239b0a46632d47db2b9d21340f98d597df
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * Could also be called 'subtractable' */
public interface IIGroup {

	/**Negation in Place: -
	 * This virtual Operation has to be implemented by each concrete Subclass.	 */
//	public IGroup negAt();

	/**Subtraction in Place: -=
	 * This virtual Operation has to be implemented by each concrete Subclass.	 */
	public IGroup subAt(final Object arg); //throws CloneNotSupportedException;

}
