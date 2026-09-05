package streamIO.copy.groupM;

/**Default implementation layer adding direct {@code long}-argument multiplication and
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:32:07Z
 * digest: 2841e04434878d00eeff22b7f1d041933000309bc9de4beab0052affce802d70
 * stale: false
 * tags: [code/abstract_base, code/multiplicative_group]
 * concepts: [Algebraic Group, Multiplicative Structure]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * division on top of {@link AGroupM}'s generic {@link Object}-argument operations. */
public abstract class AGroupMLng
extends AGroupM
implements ILngGroupM {

	/**Sets the 'self' Reference for Delegation.
	 * This Constructor is only used in 'Initialize' and 'Terminate' of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	public AGroupMLng (IGroupM self_) { super(self_); }	//call Constructor of SuperClass 'ASemiGroupM'

	/**Multiplication by a long Number: * arg	 */
	public ILngGroupM mul (long arg) { return ((ILngGroupM) self.copy()). mulAt(arg); }

	/**Division by a long Number: / arg	 */
	public ILngGroupM div (long arg) { return ((ILngGroupM) self.copy()). divAt(arg); }

}
