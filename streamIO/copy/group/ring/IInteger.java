package streamIO.copy.group.ring;

/**Defines the most basic Operations inc() and dec(), usually for Integer Types.
 * Used when a 1 Element is added to an additive Group, e.g. in the Integrity Ring
 * Usually implemented together with the 'countable' Interface
 * and complemented with the 'integer' Interface.
 * Also used for sequential Access of Data Structures.
 * Since it modifies the State it cannot be used for Const etc.
 * but it allows a minimal Interface for Counters (Increment and Decrement)
 * like e.g. for Versioning (but the availability of dec() defies this)
 */
public interface IInteger {

	/**Setting to Zero: 0	 */	integer ZeroAt();
	/**Increment: x++	 */	integer inc();
	/**Decrement: x--	 */	integer dec();
}
