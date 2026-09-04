package streamIO.copy.boole;

import streamIO.copy.ACopyAble;
import streamIO.copy.ICopyAble;

/**Abstract implementation of a Lattice (without NOT, False and True)	 */
public class ALattice
extends ACopyAble
implements Lattice {

	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Reference to 'this' for Delegation	 */
	protected Lattice self;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**Empty Constructor for Inheritance. 'self' is set to this. */
	protected ALattice() { this.self = this; }

	/**Constructor to pass over 'this' for Delegation.	 */
	public ALattice(Lattice self) { this.self = self; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface Lattice: Implementation
	////////////////////////////////////////////////////////////////////////////

	/**Boolean AND Operation: &, && for single Bit	*/
	public Lattice AND (Object arg){ return ((Lattice)self.copy()). ANDat(arg); }

	/**Boolean OR Operation: |, || for single Bit	*/
	public Lattice OR  (Object arg){ return ((Lattice)self.copy()).  ORat(arg); }

	/**Boolean DIFF Operation: -
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set ; can also be defined without NOT!  */
	public Lattice DIFF(Object arg) { return ((Lattice)self.copy()).DIFFat(arg); }

	/**Boolean XOR Operation: ^
	 * a XOR b = true <=> (a AND ~b) OR (~a AND b) <=> NOT(a EQV b) <=> (a-b) OR (b-a)
	 * For Sets: Gives Set of all Elements, that are either in one or
	 * (exclusively) in the other	*/
	public Lattice XOR  (Object arg) { return self.DIFF(arg).OR(((Lattice)arg).DIFF(self)); }

	/**Boolean XOR Operation in Place: ^=
	 * a XOR b <=> (a AND NOT b) OR (NOT a AND b)
	 * For Sets: Gives Set of all Elements, that are either in one or the other 	*/
	public Lattice XORat(Object arg) { self.shallowCopyAt(self.XOR(arg)); return self; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ILattice: Abstract Operations
	///////////////////////////////////////////////////////////////////////////////

	/**Boolean AND Operation in Place: &=, &&= for single Bit	*/
	public Lattice ANDat (Object arg) { throw new AbstractMethodError(); }

	/**Boolean OR Operation in Place: |=, ||= for single Bit	*/
	public Lattice ORat  (Object arg) { throw new AbstractMethodError(); }

	/**Boolean DIFF Operation in Place: -=
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set ; can also be defined without NOT!  */
	public Lattice DIFFat(Object arg) { throw new AbstractMethodError(); }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ILattice: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Determines, whether 'this' is less than or a SubSet of arg
	  * This is equivalent to the Boole.IMP Operation. 	*/
	public boolean SubEq(Object arg) { return self.equals(self.AND(arg)); }

	/** Determines, whether 'this' is less than or a real SubSet of arg
	  * This is equivalent to the Boole.IMP Operation. 	*/
	public boolean Sub  (Object arg) {
		return 	 ((Boole) self).SubEq(arg ) &&
				!((Boole) arg ).SubEq(self); } //better for Streams
//				!self.equals(arg); } //better for Scalars

	/** Determines, whether 'this' is equal or equivalent to arg
	  * This must be coordinated with the HashCode Function.
	  * @param arg An Object to be compared with this one
	  * @return true when both Objects are equivalent	 */
	public boolean equals(Object arg) {
		return 	((Boole) self).SubEq(arg ) &&
				((Boole) arg ).SubEq(self); } //better for Streams

	/**Determines, whether 'this' is more than or a SuperSet of arg	*/
	public boolean SuperEq(Object arg) { return ((Boole)arg).SubEq(self); }

	/**Determines, whether 'this' is more than or a real SuperSet of arg	*/
	public boolean Super  (Object arg) { return ((Boole)arg).Sub(self); }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICopyAble: Abstract Operations
	///////////////////////////////////////////////////////////////////////////////

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg, int Depth){throw new AbstractMethodError();}

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance(){throw new AbstractMethodError();}

	/**Fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg){throw new AbstractMethodError();}

}
