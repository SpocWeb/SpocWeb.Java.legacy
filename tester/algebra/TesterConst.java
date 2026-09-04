package tester.algebra;

import tester.ITester;

/**This is a Helper ITester Class that always returns the same Result,
 * given in the Constructor
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
