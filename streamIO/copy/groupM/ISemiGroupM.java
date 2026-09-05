package streamIO.copy.groupM;

import streamIO.copy.ICopyAble;

/**Algebraic SemiGroup (M,*):
 * This Interface must be kept completely synchronous to SemiGroup
 * Set of Objects with inner Operation "*" on any two Objects.
 * I.e. in a SemiGroup any two Objects can be "multiplied".
 * This Multiplication is associative i.e. (a*b)*c==a*(b*c)
 *
 * Commutative SemiGroups are SemiGroups where the Operator Matrix is symmetric,
 * i.e. a*b=b*a.
 * C++ can implement non-commutative SemiGroups,
 * because * is evaluated in strict order.
 *
 * Defines the additional Operations which should be redefined
 * to exploit the final Definition of the Operands.
 * A Default Implementation is done in 'ASemiGroupM'.
 * There are more effective ways to implement these functions,
 * depending on the implementing class. This is only the generic Solution.
 *
 * Design Decisions:
 * This SemiGroupM must always be synchronous to SemiGroup!
 * Unfortunately you cannot use Operators as Variables in Templates,
 * in C++ you would use Macros for that.
 *
 * The Interface has been separated from the Implementation in Java,
 * because it is used for multiple Inheritance.
 * Could also be called 'fullMultiplicable'
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: bfb13cda01f605b8a5ca8032f79e2cae367bc358922c7218867752ffe7506bd9
 * stale: false
 * tags: [code/multiplicative_semigroup, code/algebraic_structure]
 * concepts: [Algebraic SemiGroup, Multiplicative Structure]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface ISemiGroupM
extends IISemiGroupM, ICopyAble {

	/**Multiplication: *		*/	public ISemiGroupM mul (Object arg);	//
	/**Square: x^2 == x*x		*/	public ISemiGroupM sqr	();	//
	/**Square in Place: x*=x	*/	public ISemiGroupM sqrAt ();	//
	/**Cubic: x^3 == (x^2)*=x	*/	public ISemiGroupM cbc	();	//
	/**Cubic in Place: x*=x^2	*/	public ISemiGroupM cbcAt ();	//
	/**Quad: x^4 == (x^2)^2		*/	public ISemiGroupM qad	();	//
	/**Quad in Place: x^2^2		*/	public ISemiGroupM qadAt ();	//

	/**Integer Power: x^n	 */		public ISemiGroupM Pow	(int n);
	/**Integer Power: x^n	 */		public ISemiGroupM PowAt	(int n);

	/**Raised by an Integer Power of 2 in Place: x^=(2^n)	 */
	public ISemiGroupM Pow2PowAt(int n);

	/**Raised by an Integer Power of 2: x^(2^n)	 */
	public ISemiGroupM Pow2Pow  (int n);

}
