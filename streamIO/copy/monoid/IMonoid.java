package streamIO.copy.monoid;

import function.IInvertAble;

/**Algebraic Group (M,�,\,Id):
 * This Interface cannot be kept synchronous to Group because of missing Commutativity.
 * Set of Objects with inner Operations "�,\" on any two Objects
 * In a SemiGroup any two Objects can be "concatenated" or "unCatd".
 * These Operations are associative, i.e. (a�b)\c == a�(b\c),
 * but normally not commutative, i.e. a�b != b�a, i.e. [a,b] != 0.
 * This is usually the Case with Mappings and Operators (linear Mappings).
 *
 * C++ and Java can implement non-commutative Groups,
 * because � is evaluated in strict order.
 *
 * Defines the additional Operations which should be redefined
 * to exploit the final Definition of the Operands.
 * A Default Implementation is done in 'absMonoid'.
 * There are more effective ways to implement these functions,
 * depending on the implementing class. This is only the generic Solution.
 *
 * Design Decisions:
 * The fundamental Operation is called map, not "cat" like Concatenation,
 * and the Return Value of the ...At() Methods is the Argument,
 * not 'this', because that allows the easy Implementation of the mapAt() Op.
 * This also makes handling Expressions easier, because:
 * ret = ths� arg == ths(arg) == ths.map(arg) <=> ret[i] == ths[arg[i]] for all i
 * Derivation the Formula for unMap and solve prove that it cannot be done in place:
 * ret = ths�!arg == ths.solve(arg) <=>  ret�arg == ths  <=> ret[arg[i]] == ths[i]
 * ret =!ths� arg == ths.unMap(arg) <=>  ths*ret == arg  <=> ths[ret[i]] == arg[i]
 * which cannot be calculated, except by creating the Inverse !ths first
 * (otherwise it must be searched or Elements be marked, which is very expensive !)
 * It follows:
 * A.map(B).solve(B) == (A.map(B)).solve(B) == A.map(B.solve(B)) == A
 * A.unMap(A).map(B) == (A.unMap(A)).map(B) == A.unMap(A.map(B)  == B
 *
 * Unfortunately you cannot use Operators as Variables in Templates,
 * in C++ you would use Macros for that.
 *
 * The Interface has been separated from the Implementation in Java,
 * because it is used for multiple Inheritance.
 *
 * The Interface function.getInverse() and Monoid.invert() are kept separate
 * because the first returns a Function, whereas the Second returns a Monoid!
 *
 * It can be Proved, that:
 * ...there is only a single empty Set: null
 * ...there is only a single IDENTITY Element.
 *      This Element is neutral for both left and right � Operations and commutes.
 * ...there is only a single inverse Element for each Element.
 *      This is the same Element for both left and right � Operations.
 * ...any Element commutes with itself and it's Inverse: [A,A] == [A,!A] == 0
 * ...there are Equations that have a Solution,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: cacf3e8f500c029abf143f9c57d7c143dffe6e6606362b57d1d63e5f7a07fe73
 * stale: false
 * tags: [code/concatenation, code/algebraic_structure]
 * concepts: [Monoid, Concatenation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 *       although the Operator has no Inverse  */
public interface IMonoid
extends ISemiMonoid, IIMonoid, IInvertAble {
	
	/**Left-Concatenation with the Inverse: this�!arg
	 * Resolves the Equation A�B = C = A.map(B) for A:
	 * A = C�!B = C.map(B.invert()) = C.solve(B) = B.reSolve(C) = A.map(B).solve(B)
	 * Requires arg to be a full Monoid, 
	 * although it is sufficient to have only a Pseudo-Inverse. 
	 */
	public IMonoid solve(Object arg);
	
	/**Left-Concatenation with the Inverse: arg�!this
	 * Resolves the Equation A�B = C = A.map(B) for A:
	 * A = C�!B = C.map(B.invert()) = C.solve(B) = B.reSolve(C) = A.map(B).solve(B)
	 * Requires arg to be a Mapping and returns one! 
	 */
//	public Object reSolve(Object arg);
	
	/**Left-Concatenation with the Inverse: arg�!this
	 * Resolves the Equation A�B = C = A.map(B) for A:
	 * A = C�!B = C.map(B.invert()) = C.solve(B) = B.reSolve(C) = A.map(B).solve(B)
	 */
	public ISemiMonoid reSolve(Object arg);
	
	/**(Right) Concatenation with the Inverse of arg: this�!arg
	 * Resolves the Equation A�B = C = A.cat(B) for A:
	 * A =  C �!B = C.cat(B.invert()) = C.unCat(B)
	 * To solve it for B, you have to call solve():
	 * B = !A � C = A.invert().cat(C) = A.unCat(C)
	 * If arg has no Inverse (i.e. the Inverse is a Relation, not a Function),
	 * you still can use unCat() to find certain unique Solutions. 	*/
	//public IMonoid tac(final Object arg); //return A.invert().cat(arg);
	
	/**(Right) Concatenation in Place with the Inverse of arg: this�!arg
	 * Resolves the Equation A�B = C = A.cat(B) for A:
	 * A =  C �!B = C.cat(B.invert()) = C.unCat(B)
	 * To solve it for B, you have to call solve():
	 * B = !A � C = A.invert().cat(C) = A.unCat(C)
	 * If arg has no Inverse (i.e. the Inverse is a Relation, not a Function),
	 * you still can use unCat() to find certain unique Solutions. 	*/
	//public IMonoid tacAt(final Object arg); //return A.invert().catAt(arg);
	
	/**Mapping / Left-Concat with !this in Place: !this=�arg */
//  public Object unMapAt(Object arg); //defined in IInvertAble
	
	/**Mapping / Left-Concat with !this:  !this�arg	*/
	public IMonoid pam  (final Object arg);
	
	/**Mapping / Left-Concat with !this:  !this�arg	*/
//	public Object unMap  (Object arg); //defined in IInvertAble
	
	/**Inversion in Place: !=x	 */	public IMonoid revAt	(); //
	/**Inversion: !x	 */			public IMonoid rev	(); //return A.unCat(Identity); //this is not always possible!
	
	/**Setting to  Id in Place:	 */	public IMonoid	 IdentityAt	(); //return A.catAt(A.Inverse);
	/**Setting to  Id:	 */			public IMonoid	 Identity	(); //return A.invert().catAt(A);
	/**Testing for Id:	 */			public boolean isIdentity	(); //
	
}
