package tester.algebra;

import tester.ITester;

/** Inverts a ITester Objects using NOT
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:10:55Z
  * digest: a957ae32315f6f2720778054ce9a316b5f9a5bd59591d72c01f6322b84fb862f
  * stale: false
  * tags: [code/boolean_algebra, code/predicate_logic]
  * concepts: [Boolean NOT Tester]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class TesterNOT
implements ITester {

	/** Reference to the first ITester Object	*/
	final public ITester mTest;

	/** Returns the wrapped {@link ITester} whose result this instance negates.
	 * @return the inner ITester 	 */
	public ITester getTester() { return mTest; }

	/** Initializing Constructor taking the two ITester Objects	*/
	public TesterNOT(final ITester test) { mTest = test; }

	/**This is the Test working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * @param  arg	The Object being 'tested'
	 * @return 	'true' or 'false' depending on the ITester and the Parameter 'arg'	 */
	public boolean test(final Object arg) {
		return ! mTest.test(arg); }

}
