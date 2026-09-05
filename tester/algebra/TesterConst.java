package tester.algebra;

import tester.ITester;

/**This is a Helper ITester Class that always returns the same Result,
 * given in the Constructor
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:33Z
 * digest: eec4c0267ed573cd71386f66dddcc1b52a12816ebdef843ef8c04968b19f71a0
 * stale: false
 * tags: [code/boolean_algebra]
 * concepts: [Constant Tester]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * It is used e.g. in Container	*/
final public class TesterConst
implements ITester {

	/** Tester Instance returning only 'false'	 */ 
	final static public TesterConst TESTER_FALSE = new TesterConst(false); 
	
	/** Tester Instance returning only 'true'	 */ 
	final static public TesterConst TESTER_TRUE = new TesterConst(true); 
	
	/** Local Copy of the Result.	 */
	private final boolean Result_;

	/** Singleton Constructor, takes the Result that will be always returned.	 */
	private TesterConst(final boolean Result) { Result_ = Result; }

	/** Test Method that returns the same Constant Result	 */
	public boolean test(Object arg) { return Result_; }

}
