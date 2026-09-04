package streamIO.copy.boole;

/**This Class defines the full Interface for a Boolean Algebra.
 * The two basic Operations AND and OR are intertwined
 * in that both use the same inverse: NOT,
 * but it leads only to the other operation's neutral Element,
 * instead of to this operations's neutral Element.
 *
 * The Definition can be extended to Vectors of Boolean Elements,
 * which allows for operations on large sets of Elements,
 * in which each one acts independently (Vector, not Polynom).
 * In a binary Representation AND and OR can be defined by MUL and ADD,
 * but without Carry Bit.
 *
 * Direct SubClasses:
 *		Container.absSet(concrete, no Delegator anymore!)
 *		SetInteger.absSetInteger
 *		Boole.Boolean	(final)
 * Delegations (via absSetInteger):
 *		Vector.Binary
 *		Vector.BitVector
 *
 * This Class has been created for Delegation,
 * by having all Methods work with self,
 * because the SubClass absSetInteger is used by Binary and BitVector.
 * ArrStruct cannot use it directly, because Bits are not defined there!	 */
public class ABoole	//abstract	cannot be abstract, because it has to be delegated to!
extends ALattice
implements Boole {

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Empty Constructor for Inheritance. 'self' is set to this. */
	protected ABoole() { }

	/**Constructor to pass over 'this' for Delegation.	 */
	public ABoole(Boole self) { super(self); }

	//////////////////////////
	//  Interface Boole
	//////////////////////////

	/**Boolean XOR Operation: ^
	 * a ^ b = (a & ~b) | (~a & b) = (a-b) + (b-a)	 */
	public Lattice XOR	(Object arg) {
		return self.DIFF(arg).OR(((Boole)arg).DIFF(self)); } //well defined!
//		return (Boole) self.AND(((Boole)arg).NOT()).ORat(((Boole) self).NOT().AND(arg)); }

	/**Boolean DIFF Operation: -
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set, can also be defined as a fundamental Operation!
	 * NOT is only implicitly defined (Expressions) in infinite Sets!
	 * Rather use DIFF, which is always well defined!
	 */
	public Lattice DIFF	(Object arg) { return (Boole) self.AND(((Boole)arg).NOT()); }

	/** Boolean IMP Operation: =>
	  * a => b == ~a | (a & b) == ~a | b
	  * This is equivalent to the Lattice.SubEq Test. 	*/
	public Boole IMP	(Object arg){
		return  (Boole) ((Boole) self).NOT().ORat(arg); }

	/**Boolean EQV Operation: <=>
	 * a <=> b == ~(a ^ b) == ( a = b )
	 * Is well defined and should be a fundamental Operation!
	 * An EQV Set consists of all Elements contained in both Arguments!
	 */
	public Boole EQV	(Object arg) { return ((Boole) self.XOR(arg)).NOTat(); }

	//////////////////
	//	Delegation	//
	//////////////////

	/**Boolean Constant for the Representation of 'false' = 0	*/
	public Boole False() { return ((Boole)self.newInstance()).FalseAt(); }

	/**Boolean Constant for the Representation of 'true': 1		*/
	public Boole True () { return ((Boole)self.newInstance()).TrueAt(); }

	/** Boolean NOT Operation: ~, ! for single Bit
	  * NOT a <=> TRUE - a
	  * NOT is only implicitly defined (Expressions) in infinite Sets!
	  * Rather use DIFF, which is always well defined!
	  */
	public Boole NOT  () {
//		return ((Boole)self .copy()).NOTat(); }
		return (Boole) ((Boole)self).True().DIFFat(self); }	//second Possibility!

	//In Place Boole Operations

	/**Boolean DIFF Operation in Place: -=
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set*/
	public Lattice DIFFat	(Object arg) { return (Boole) self.ANDat(((Boole)arg).NOT()); }

	/**Boolean IMP Operation in Place: =>
	 * a IMP b = true <=> (a = true) => (b = true) <=>
	 */
	public Boole IMPat	(Object arg) { return  (Boole)
			((Boole) self).NOTat().ORat(arg); }
//	{return (Boole)shallowCopyAt(IMP(arg));}

	/**Boolean EQV Operation in Place: <=>
	 * a EQV b = true <=> (a = b)
	 * Is well defined and should be a fundamental Operation!
	 * An EQV Set consists of all Elements contained in both Arguments!
	 */
	public Boole EQVat	(Object arg) {
		return (Boole)self.shallowCopyAt(((Boole) self).EQV(arg)); }

	/** Boolean Constant for the Representation of 'true': 1
	  * The Definition is problematic for infinite Sets.
	  * It requires Expressions!
	  */
	public Boole TrueAt() { return ((Boole) self).FalseAt().NOTat(); }

	//////////////////////////////
	//  Replication IBoole
	//////////////////////////////

	/**Boolean NOT Operation in Place: ~=, != for single Bit	*/
	public Boole NOTat	() { throw new AbstractMethodError(); }

	/**Boolean Constant for the Representation of 'false' = 0
	 *  i.e. not 'true'.	 */
	public Boole FalseAt() { throw new AbstractMethodError(); }

	//newer Operations:

	/**Returns true, when 'this' is False, or an empty Set	 */
	public boolean isFalse() { return self.equals(((Boole) self).False()); }

	/**Returns true, when 'this' is True, or a full Set	 */
	public boolean isTrue() { return self.equals(((Boole) self).True()); }

}
