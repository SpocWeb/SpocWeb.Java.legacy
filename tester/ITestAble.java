package tester;

/**Example Interface for a unary Test Function with 'this' as the only argument.
 * This interface is quite unusable, because the Operation Name
 * carries no Semantics so it is hard to read.
 * Rather use specific Operations and define a new Interface for them,
 * modeled after this one.
 *
 * Design Decisions:
 * The Parameters for 'Test' are left out, to keep the Interface clean.
 * They have to be set globally with the concrete Class.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:33Z
 * digest: 7901ced6ce1aad7575a54020337161b24b05cf719d1e91460528f72bc9b16d19
 * stale: false
 * tags: [code/predicate_logic]
 * concepts: [Testable Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface ITestAble {

	/**This is the Template for a Test working on an Object.
	 * The Class brings along the Meaning for this Operation.	 */
	boolean Test();

}
