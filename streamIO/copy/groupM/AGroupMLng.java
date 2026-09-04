package streamIO.copy.groupM;

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
