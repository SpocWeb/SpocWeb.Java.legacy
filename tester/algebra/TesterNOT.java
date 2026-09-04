package tester.algebra;

import tester.ITester;

/** Inverts a ITester Objects using NOT
  */
public class TesterNOT
implements ITester {

	/** Reference to the first ITester Object	*/
	final public ITester mTest;

	/** @return the inner ITester 	 */
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
