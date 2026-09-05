package tester.algebra;

import tester.ITester;

/** Concatenates two ITester Objects using AND
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:33Z
  * digest: 7a11ba30ce35111b091f20589eb4528aa9dfd765da590f0141f50f2e50f883a4
  * stale: false
  * tags: [code/boolean_algebra, code/predicate_logic]
  * concepts: [Boolean AND Tester]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class TesterAND
implements ITester {

	/** Reference to the first ITester Object	*/
	protected ITester mTest1;

	/** Reference to the second ITester Object	*/
	protected ITester mTest2;

	/** Initializing Constructor taking the two ITester Objects	*/
	public TesterAND (ITester test1, ITester test2) {
		mTest1 = test1;
		mTest2 = test2; }

	/**This is the Test working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * @param  arg	The Object being 'tested'
	 * @return 	'true' or 'false' depending on the ITester and the Parameter 'arg'	 */
	public boolean test(Object arg) {
		return mTest1.test(arg) && mTest2.test(arg); }

}
