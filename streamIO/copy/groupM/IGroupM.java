package streamIO.copy.groupM;

/**Algebraic Group (M,*,/,1):
 * This Interface must be kept completely synchronous to Group
 * Set of Objects with inner Operations "*,/" on any two Objects
 * In a SemiGroup any two Objects can be "multiplied" or "divided".
 * These Operations are associative, i.e. (a*b)/c == a*(b/c)
 *
 * Commutative Groups are Groups where the Operator Matrix for '*' is symmetric,
 * i.e. a*b=b*a.
 * C++ can implement non-commutative Groups,
 * because * is evaluated in strict order.
 *
 * Defines the additional Operations which should be redefined
 * to exploit the final Definition of the Operands.
 * A Default Implementation is done in 'AGroupM'.
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
 *
 * It can be Proved, that :
 * ...there is only a single ONE Element.
 * This Element is neutral for both left and right * Operations.
 * ...there is only a single inverse Element for each Element.
 * This is the same Element for both left and right * Operations.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: e73345aef7d9210ad5613060f400bea87159c7870e3b53bf98f5886c1d6d70a0
 * stale: false
 * tags: [code/multiplicative_group, code/algebraic_structure]
 * concepts: [Algebraic Group, Multiplicative Structure]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IGroupM
extends ISemiGroupM, IIGroupM {

	/**Inversion in Place: 1/x	 */	public IGroupM invAt();
	/**Inversion: 1/x	 */			public IGroupM inv();
	/**Division: /	 */				public IGroupM div(Object arg);

	/**Setting to 1 in Place:	 */	public IGroupM oneAt(); //could be defined by dividing any number (except 0) by itself
	/**Setting to 1:	 */			public IGroupM one();
	/**Testing for 1:	 */			public boolean isOne();

}
