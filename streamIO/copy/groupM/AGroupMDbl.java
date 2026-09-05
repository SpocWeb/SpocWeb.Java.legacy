package streamIO.copy.groupM;

/** This Class is actually never used!
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:25Z
  * digest: b0bba1ffe6bc0dd42f154f7d436b83eacc1989c52af97f0bd53dd93ad0edb78e
  * stale: false
  * tags: [code/abstract_base, code/deprecated_api]
  * concepts: [Algebraic Group, Multiplicative Structure]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
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
