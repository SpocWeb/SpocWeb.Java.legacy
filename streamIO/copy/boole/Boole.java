package streamIO.copy.boole;

//import Stream.Copy.*;

/**This Class defines the full Interface for a Boolean Algebra.
 * The two basic Operations AND and OR are intertwined
 * in that both use the same inverse: NOT
 * but it leads only to the other operation's neutral Element,
 * instead of to this operations's neutral Element.
 *
 * These Operations and their Negation cover the full Range
 * of ALL 16 possible binary Operations,
 * except for '<=', which has no simple name, because not being used,
 * and can easily be replaced by '=>' and swapping the Arguments.
 *
 * The Definition can be extended to Vectors of Boolean Elements,
 * which allows for operations on large sets of Elements,
 * in which each one acts independently (Vector, not Polynom).
 *
 * This allows for a Representation of Sets, where each Boole
 * Item indicates the Existence of this Element in the Set.
 *
 * In a binary Representation AND and OR can be defined by MUL and ADD,
 * but without Carry Bit.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 1b2455fac98d2e35c12e5db829a1fee563bfbe8c7cc6591408939634c9d1905a
 * stale: false
 * tags: [code/boolean_algebra]
 * concepts: [Boolean Algebra]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface Boole
extends IBoole, Lattice {

	/**Boolean NOT Operation: ~, ! for single Bit
	 * For Sets: Returns the Complement Set	*/
	Boole NOT();

	/**Boolean XOR Operation: ^
	 * a XOR b = true <=> (a AND ~b) OR (~a AND b) <=> NOT(a EQV b) <=> (a-b) OR (b-a)
	 * For Sets: Gives Set of all Elements, that are either in one or
	 * (exclusively) in the other	*/
//	public Boole XOR	(Object arg);

	/**Boolean DIFF Operation: -
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set, can be defined using AND and NOT
	 * can also be defined as a fundamental Operation in ILattice!   */
//	public Boole DIFF	(Object arg);

	/**Boolean IMP Operation: =>
	 * a IMP b <=> (b OR NOT a ) <=> NOT (b-a)
	 * For Sets: Gives Set of all Elements, that exist in b when they exist in a
	 * This is a real Order Relation and could also be called 'grtr'	*/
	Boole IMP	(Object arg);

	/**Boolean EQV Operation: <=>
	 * a EQV b <=> NOT(a XOR b) <=>
	 * For Sets: Gives Set of all Elements, that are either in both or in none	*/
	Boole EQV	(Object arg);

	/**Boolean Constant for the Representation of 'true': 1
	 * i.e. NOT 'false'.
	 * For Sets: The Set of ALL Elements
	 * (only posssible explicitly for known finite SuperSets) */
	Boole True();

	/**Boolean Constant for the Representation of 'false': 0
	 * i.e. NOT 'true'.
	 * For Sets: Returns the empty Set	*/
	Boole False();

	/**Boolean XOR Operation in Place: ^=
	 * a XOR b <=> (a AND NOT b) OR (NOT a AND b)
	 * For Sets: Gives Set of all Elements, that are either in one or the other 	*/
//	public Boole XORat	(Object arg);

	/**Boolean DIFF Operation in Place: -=
	 * a - b <=> (a AND NOT b) <=> NOT IMP
	 * For Sets:	Difference Set*/
//	public Boole DIFFat	(Object arg);

	/**Boolean IMP Operation in Place: =>
	 * a IMP b <=> (NOT a OR b)
	 * For Sets: Gives Set of all Elements, that exist in b when they exist in a
	 * This is a real Order Relation and could also be called 'grtr'	*/
	Boole IMPat	(Object arg);

	/**Boolean EQV Operation in Place: <=>
	 * a EQV b <=> NOT (a XOR b)
	 * For Sets: Gives Set of all Elements, that are either in both ore none 	*/
	Boole EQVat	(Object arg);

	/**Boolean Constant for the Representation of 'true': 1
	 * For Sets: Set of all Elements (only posssible for finite Sets)	*/
	Boole TrueAt();

	/**Returns true, when 'this' is False, or an empty Set	 */
	boolean isFalse();

	/**Returns true, when 'this' is True, or a full Set	 */
	boolean isTrue();

}
