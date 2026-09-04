package streamIO.copy.boole;

import tester.ITester;
import tester.algebra.TesterAND;
import tester.algebra.TesterConst;
import tester.algebra.TesterNOT;
import tester.algebra.TesterOR;
import function.byref.ByRefBoolean;

/** Boolean Algebra for the ITester Function
  * Allows to combine and operate on ITester Functions.
  * The constant ITester Functions TRUE and FALSE act as neutral Elements.
  *
  * This is an excellent small Example for the Algebra.
  * The Problem with an Algebra is that as soon as there is a Constant Function,
  * all Operations can be performed explicitly...
  * With Boolean Algebras, as soon as one Coefficient is constant,
  * the whole Expression may collapse.
  */
public class TesterBond
extends ABoole
implements ITester {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Always returns 'True'	*/
	final static public ITester True  = new TesterBond(TesterConst.TESTER_TRUE);

	/** Always returns 'False'	*/
	final static public ITester False = new TesterBond(TesterConst.TESTER_FALSE);

	/** Reference to the actual ITester Object	*/
	protected ITester mTest;

	////////////////////////////////////////////////////////////////////////////////
	//	Accessor Methods: get...()/set...()
	////////////////////////////////////////////////////////////////////////////////

	/** @return the inner ITester Function	 */
	public ITester getTester() {
		return mTest; }

	////////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor 	*/
	public TesterBond(ITester Test) {
		if (Test instanceof TesterBond) { //prevent nested TesterBond Objects!
			mTest = ((TesterBond) Test).mTest;
		} else {
			mTest = Test; } }

	////////////////////////////////////////////////////////////////////////////////
	//	Interface ITester
	////////////////////////////////////////////////////////////////////////////////

	/**This is the Test working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * @param  arg	The Object being 'tested'
	 * @return 	'true' or 'false' depending on the ITester and the Parameter 'arg'	 */
	public boolean test(Object arg) {
		return mTest.test(arg); }

	////////////////////////////////////////////////////////////////////////////////
	//	Interface ILattice
	////////////////////////////////////////////////////////////////////////////////

	/** AND Operation in Place: &=, &&= for single Bit
	  * @return a & b
	  * a AND b = true <=> (a = true) AND (b = true) 	 */
	public Lattice ANDat	(Object arg) {
		//prevent nested TesterBond Objects!
		if (arg instanceof TesterBond) {
			arg = ((TesterBond) arg).mTest; }
		//Idempotenz: a AND a == a
		if ((arg == mTest) || arg.equals(mTest)) return this;
		//Inverse: a AND !a == False
		if (arg instanceof TesterNOT) { //
			ITester arg_ = ((TesterNOT) arg).getTester();
			if ((arg_ == mTest) || (arg_.equals(mTest))) {
				mTest = False; return this; } }
		//Constant Argument...
		if (arg instanceof TesterConst) { //a AND false == false ...
			if (!((TesterConst)arg).test(null)) mTest = False;
		} else if (arg instanceof java.lang.Boolean) {
			if (!((java.lang.Boolean) arg).booleanValue()) mTest = False;
		} else if (arg instanceof Boolean) {
			if (!((Boolean)arg).Value) mTest = False;
		} else if (arg instanceof ByRefBoolean) {
			if (!((ByRefBoolean)arg).Value) mTest = False;
		} else { //regular case
			mTest = new TesterAND(mTest, (ITester) arg);
		} return this; }

	/** OR Operation in Place: |=, ||= for single Bit
	  * @return a | b
	  * a OR b = true <=> (a = true) OR (b = true) 	 */
	public Lattice ORat	(Object arg) {
		//prevent nested TesterBond Objects!
		if (arg instanceof TesterBond) {  //
			arg = ((TesterBond) arg).mTest; }
		//Idempotenz: a OR a == a
		if ((arg == mTest) || arg.equals(mTest)) return this;
		//Inverse: a OR !a == True
		if (arg instanceof TesterNOT) { //
			ITester arg_ = ((TesterNOT) arg).getTester();
			if ((arg_ == mTest) || (arg_.equals(mTest))) {
				mTest = False; return this; } }
		//Constant Argument...
		if (arg instanceof TesterConst) { //a OR true == true ...
			if (!((TesterConst)arg).test(null)) mTest = True;
		} else if (arg instanceof java.lang.Boolean) {
			if (!((java.lang.Boolean) arg).booleanValue()) mTest = True;
		} else if (arg instanceof Boolean) {
			if (!((Boolean)arg).Value) mTest = True;
		} else if (arg instanceof ByRefBoolean) {
			if (!((ByRefBoolean)arg).Value) mTest = True;
		} else { //regular case
			mTest = new TesterOR (mTest, (ITester) arg);
		} return this; }

	////////////////////////////////////////////////////////////////////////////////
	//	Interface IBoole
	////////////////////////////////////////////////////////////////////////////////

	/** Boolean Constant for the Representation of 'false': =0
	  * @return false
	  * Sets this Object to False, i.e. not 'true';
	  * with Vectors it sets all Elements to their respective Value of False*/
	public Boole FalseAt() {
		mTest = False;
		return this; }

	/** Boolean NOT Operation in Place: ~=, != for single Bit
	  * @return !a
	  * NOT a = true <=> (a = false)
	  * This Operation cannot be implemented by infinite Sets,
	  * Therefore you need other means to define some Operations.	 */
	public Boole NOTat	() {
		if (mTest instanceof TesterNOT) { //Optimization: !!a == a
			mTest = ((TesterNOT) mTest).getTester(); //getInner();
		} else {
			mTest = new TesterNOT(mTest);
		} return this; }

	public TesterBond simplify() {
		return this; }

}
