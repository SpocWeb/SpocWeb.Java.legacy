package streamIO.copy.monoid;

import streamIO.copy.ICopyAble;
import function.IFunction;

/**SemiMonoid (M,�):
 * Set of Objects with Operation � called 'map()' on any two Objects.
 * I.e. in a SemiMonoid any two Objects can be "concatenated" or "mapped".
 * Example is e.g. String Concatenation or Function Concatenation / Mapping.
 * This Concatenation is associative i.e. (a�b)�c==a�(b�c) but not commutative!
 * Therefore pairs of Operations can be defined:
 * map()/unMap() with the Operand coming from the left and
 * cat()/unCat() with the Operand coming from the right
 *
 * It can be proven that:
 *
 * !(A�B)== !B�!A
 * [A,A] == [A,!A] == [A,Id] == 0
 * A�!A == !A�A == Id == A.unMap(A) == A.unCat(A) == A.solve(A) == A.reSolve(A)
 *
 * A.map(A .unMap(w))==  A�!A�w == w == A.unMap(A .map(w))== !A� A�w
 * with w being a simple Object, a Map or a Monoid.
 * Therefore an extended and a reduced Syntax have been defined.
 *
 *  W =  B  �  A  � D ==  C  � D     with  B  �  A  ==  C
 * {c}={c@b}�{b@a}�{a}=={a-c}�{a}        {c@b}�{b@a}=={c@a}
 *
 * b == A�a == A.map(a)  and  a == !A�b == A.unMap(b)
 * c == B�b == B.map(b)  and  b == !B�c == B.unMap(c)
 * 
 * A.map(B) = A�B = C                            can be resolved for A:
 * A == C.solve(B) ==  C�!B ==  A�B�!B    and it can be resolved for B:
 * B == A.unMap(C) == !A� C == !A�A� B
 * 
 * Because of the extended Syntax with Object, reSolve() has been defined,
 * which has the same Effect, but reversed Arguments:
 * A�!B == A.solve(B) == B.reSolve(A)
 * 
 * 
 * 
 * Deprecated:
 * A.cat(B).  unCat(B) ==  A� B�!B == A == A.unCat(B).cat(B) == A�!B� B
 * A.cat(B).reSolve(A) ==  A.solve(A.cat(B)) == A.cat(A.solve(B))
 * B.map(A).reSolve(A) == !A� A� B == B == B.reSolve(A).map(A)
 *                                      == A.  unCat(A).cat(B) == A�!A� B
 * 
 * C ==  A� B == A.    cat(B) == B.  map(A)   and
 * A ==  C�!B == C.  unCat(B) == B.unMap(C)   and
 * B == !A� C == C.reSolve(A) == A.solve(C)
 *
 * mapAt() and unMapAt() return the Argument Type in Place
 * solve() and reSolve() can not be calculcated in Place!
 * catAt() and unCatAt() can be rapidly calculated in place, 
 * since the structure of A={a[b]} in B.catAt(A) is unchanged:
 * just iterate over all Keys c in B={b[c]} (since they stay!)
 * and replace the Value b by the Mapping a in A={a[b]}
 *
 * Java, C# and C++ can implement non-commutative HalfMonoids,
 * because the Operators are evaluated in strict left-to-right order.
 * 
 * Defines the additional Operations which should be redefined
 * to exploit the final Definition of the Operands.
 * A Default Implementation is done in 'AHalfMonoid'.
 * There are more effective ways to implement these Functions
 * depending on the implementing class. This is only the generic Solution.
 * 
 * Design Decisions:
 * The Interface has been separated from the Implementation in Java,
 * for multiple Inheritance.
 * This Interface could also be called 'concatenable' or 'mapping / mappable'
 * 
 * Because (un)cat(At) doesn't add Functionality and only confuses
 * the Definition has been commented out. 
 * Only catAt is left, since it can be implemented quite efficiently! 
 * 
 * The Notation using map helps understanding, because the Sequence of Objects
 * follows the Mapping and Bracketing becomes irrelevant because of Associativity
 * 
 * A  �  B   �  c  = (A  �  B )  �  c  = A  � (B  �  c ) =
 * A.map(B).map(c) = (A.map(B)).map(c) = A.map(B.map(c))
 * 
 * The only Difference is that the ...At() Methods don't modify 'this' but arg!
 * There is a Left- and a Right- Inverse Operations: unMap() and solve() 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: fea721cfea886b2980d2f124a49920d9b3701d1d8d23fc399c2694589accf395
 * stale: false
 * tags: [code/concatenation, code/algebraic_structure]
 * concepts: [Monoid, Concatenation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface ISemiMonoid
extends IISemiMonoid, ICopyAble, IFunction {
	
	/** Mapping / Left-Concat in Place:  this=�arg  ==  this(arg(x))
	  * This Operation doesn't return and modify 'this', but 'arg'!
	  * so to concatenate Mappings, use B.mapAt(A.mapAt(a))
	  * which is more efficient than B.map(A.map(a)) 
	  * or even worse: B.map(A).map(a) or (B.mapAt(A)).mapAt(a) which works only once! 
	  */     public ISemiMonoid mapAt(final Object arg);
	/** Mapping/Left -Concat:  this�arg  */	public ISemiMonoid map  (final Object arg);
	/** Mapping/Right-Concat:   arg�this 
	 * commented out, since it only confuses */	
	//public ISemiMonoid cat  (final Object arg);
	/** Left-Concatenation in Place:  arg=this�arg 
	 * Left in the Interface, because it can be implemented quite efficiently! */ 
	//public ISemiMonoid catAt(final Object arg);
	
	/** Duplication:   x^2 = x�x    */	public ISemiMonoid   dpl  ();
	/** Duplication in Place: x�=x  */	public ISemiMonoid   dplAt();
	/** Triplication:  x^3 = x^2�=x */	public ISemiMonoid   tpl  ();
	/** Triplication in Place: x^=3 */	public ISemiMonoid   tplAt();
	/** Quadruplication: x^4 == (x^2)^2	*/	public ISemiMonoid qdl  ();
	/** Quadruplication in Place: x^4	*/	public ISemiMonoid qdlAt();
	
	/** Integer Multiplication: �^ n	*/	public ISemiMonoid mll	(int n);
	/** Integer Multiplication: �^=n	*/	public ISemiMonoid mllAt	(int n);
	
	/**Multiplication with an Integer Power of 2 in Place: �*=2^n	 */
	public ISemiMonoid mll2PowAt(final int n);
	
	/**Multiplication with an Integer Power of 2: �*2^n	 */
	public ISemiMonoid mll2Pow  (final int n);
	
}
