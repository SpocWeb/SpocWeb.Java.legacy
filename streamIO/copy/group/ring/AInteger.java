package streamIO.copy.group.ring;

import java.io.StreamTokenizer;

import streamIO.copy.ACopyAble;
import streamIO.copy.ICopyAble;

/**Default Implementation of the 'integer' Interface for Integer Types.
 * Used when a 1 Element is added to an additive Group, e.g. the Integrity Ring
 * usually implemented together with the 'countable' Interface.
 * Also used for sequential Access of Data Structures.
 * Defines the Function pred() and succ()
 * as Complements to inc() and dec() in IInteger. */
public class AInteger
extends ACopyAble
implements integer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Local Reference to the Self, initialized by concrete classes.
	 * Used for the Simulation of (multiple) Inheritance with Delegation.
	 * Must be a virtual Interface Type to be able to take any Implementation.	 */
	private integer self;

	/**This Constructor is only used in Initialize and Terminate of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	public AInteger(integer self_){ self = self_; }

	/**Setting to Zero: 0	 */
    public integer Zero() { return ((integer) self.newInstance()).ZeroAt(); }

	/**Successor: x+1	 */
	public integer succ() { return ((IInteger) self.copy()).inc(); }

	/**Predecessor: x-1	 */
	public integer pred() { return ((IInteger) self.copy()).dec(); }

	/**Residual: 1-x	*/
	public integer Resid(){ return ((integer) self.copy()).ResidAt(); }

	//Implementations just to make this class concrete!

	/**Virtual Method!
	 * Residual in Place: 1-x	*/
	public integer ResidAt() { throw new AbstractMethodError(); }

	/**Virtual Method!
	 * Increment: x++
	 * Implemented only to make this class concrete for delegation	 */
	public integer inc() { throw new AbstractMethodError(); }

	/**Virtual Method!
	 * Decrement: x--
	 * Implemented only to make this class concrete for delegation	 */
	public integer dec() { throw new AbstractMethodError(); }

	//////////////////////////////
	//	Replication intCopyAble	//
	//////////////////////////////

	/**Setting to Zero: 0	 */	public integer ZeroAt() { throw new AbstractMethodError(); }

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg, int Depth) { throw new AbstractMethodError(); }

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) { throw new AbstractMethodError(); }

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance(){ throw new AbstractMethodError(); }

	/**Fills this Instance with the Contents read from the streamIO.	 */
	public ICopyAble fromStreamAt(StreamTokenizer arg) { throw new AbstractMethodError(); }

}
