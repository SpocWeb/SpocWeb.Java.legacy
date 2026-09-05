package streamIO.copy.group;

/**Algebraic Group (M,+,-,0):
 * Set of Objects with inner Operations "+,-" on any two Objects
 * In a SemiGroup any two Objects can be "added" or "subtracted".
 * These Operations are associative, i.e. (a+b)-c==a+(b-c)
 * This is not quite true for floats, because of rounding Errors,
 * but it is true for all integer Types.
 *
 * Commutative Groups are Groups where the Operator Matrix is symmetric,
 * i.e. a+b=b+a.
 * C++ can implement non-commutative Groups,
 * because + is evaluated in strict order.
 *
 * Defines the additional Operations which should be redefined
 * to exploit the final Definition of the Operands.
 * A Default Implementation is done in 'absGroup'.
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
 * It can be Proved, that:
 * ...there is only a single ZERO Element.
 * This Element is neutral for both left and right + Operations.
 * ...there is only a single negative Element for each Element.
 * This is the same Element for both left and right + Operations.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 7e2ca790f2acf5e12005d81e5a2a212114c0f4b87fa7c2495e19544ba108b431
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IGroup
extends ISemiGroup, IIGroup {
	/**Setting to 0 in Place:	*/	public IGroup zeroAt(); //throws CloneNotSupportedException;
	/**Setting to 0:			*/	public IGroup zero(); //throws CloneNotSupportedException;
	/**Testing for 0:			*/	public boolean isZero(); //throws CloneNotSupportedException ;
	/**Negation in Place: -=	*/	public IGroup negAt(); //throws CloneNotSupportedException ;
	/**Negation: -				*/	public IGroup neg(); //throws CloneNotSupportedException ;
	/**Subtraction: -			*/	public IGroup sub(final Object arg); //throws CloneNotSupportedException ;
}
