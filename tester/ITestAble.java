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
 */
public interface ITestAble {

	/**This is the Template for a Test working on an Object.
	 * The Class brings along the Meaning for this Operation.	 */
	boolean Test();

}
