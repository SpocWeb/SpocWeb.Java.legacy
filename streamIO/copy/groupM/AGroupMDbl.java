package streamIO.copy.groupM;

/** This Class is actually never used!
  */
public abstract class AGroupMDbl
extends AGroupMLng
implements IDblGroupM {

	/**Sets the 'self' Reference for Delegation.
	 * This Constructor is only used in 'Initialize' and 'Terminate' of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	public AGroupMDbl (IGroupM self_) { super(self_); }	//call Constructor of SuperClass 'ASemiGroupM'

	/**Multiplication by a double Number: * arg	 */
	public IDblGroupM mul	(double arg) { return ((IDblGroupM) copy()). mulAt(arg); }

	/**Division by a double Number: / arg	 */
	public IDblGroupM div	(double arg) { return ((IDblGroupM) copy()). divAt(arg); }

}
