package streamIO.copy.group;

import streamIO.copy.ICopyAble;

/**Algebraic SemiGroup (M,+):
 * Set of Objects with inner Operation "+" on any two Objects.
 * I.e. in a SemiGroup any two Objects can be "added".
 * This addition is associative and commutative i.e.
 *    (a+b)+c==a+(b+c)
 *     a + b == b + a
 * The Rules for Algebras rely on the Addition to be commutative!
 * For non-commutative SemiGroups the Interface Monoid is defined:
 * Example is e.g. String Concatenation,
 *
 * Commutative SemiGroups are SemiGroups where the Operator Matrix is symmetric,
 * i.e. a + b == b + a
 * C++ and Java can implement non-commutative SemiGroups with Opearators,
 * because + and Method Calls are evaluated in strict Left to Right Order.
 *
 * Defines the additional Operations which should be redefined
 * to exploit the final Definition of the Operands.
 * A Default Implementation is done in 'ASemiGroup'.
 * There are more effective ways to implement these functions
 * depending on the implementing class. This is only the generic Solution.
 *
 * Design Decisions:
 * The Interface has been separated from the Implementation in Java,
 * because of the Symmetry with SemiGroupM which is used
 * for multiple Inheritance.
 * This Interface could also be called 'addable'
 *
 * The corresponding abstract Class is not intended for Delegation.
 * This leads to the following Design Decisions:
 * The Return Type is chosen to be ASemiGroup instead of SemiGroup,
 * because this saves a cast from other Interface Types on returning the Result.
 * The Execution is not delegated to a Self_ Variable.
 */
public interface ISemiGroup
extends IISemiGroup, ICopyAble {

	/**Addition: +					*/	public ISemiGroup add	(final Object arg);
	/**Double in Place: x+=x		*/	public ISemiGroup dblAt	();
	/**Double:   2x == x+x			*/	public ISemiGroup dbl	();
	/**Triple in Place: x+=2x		*/	public ISemiGroup trplAt	();
	/**Triple: 3x == (2x)+=x		*/	public ISemiGroup trpl	();
	/**Quadruple in Place: 2(2x)	*/	public ISemiGroup quadAt	();
	/**Quadruple: 4x == 2(2x)		*/	public ISemiGroup quad	();

	/**Integer Multiplication: x*=n	 */	public ISemiGroup mulAt	(final int n);
	/**Integer Multiplication: x* n	 */	public ISemiGroup mul	(final int n);

	/**Multiplication with an Integer Power of 2 in Place: *=2^n    */
	public ISemiGroup mul2PowAt(final int n);

	/**Multiplication with an Integer Power of 2: *2^n    */
	public ISemiGroup mul2Pow  (final int n);

}
