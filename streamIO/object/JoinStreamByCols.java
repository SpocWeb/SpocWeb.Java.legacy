package streamIO.object;

import streamIO.IIStreamIn;
import streamIO.object.filterInOut.FilterByTester;
import tester.IEquivalence;

/** Joins a Table with another and the Criterion given by the
  * @see Equivalence 'Condition'.
  * Different from JoinStreamsByFields this Implementation selects the Columns
  * by Column Numbers instead of Objects (e.g. Column Names i.e. Strings)
  *
  * On joining it selects only certain Columns.
  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
  * Since a new Container would be created, a streamIO is being used.
  * This is an Optimization because it avoids creating the Association
  * and flattening it, before handing it over to the ITester.
  *
  * 1) Cross Product (N*M) generates a streamIO of Pairs.
  * 2) Filter the Pairs using a ITester or the Elements using an Equivalence()
  * @see FilterByTester
  * @see FilterOutByTester
  * 3) flatten Pair (ILinked List) into a different structure (Array)
  * 	and ...
  * 4) ... select specific Columns during this flattening!
  *
  * alternatively you can use an Equivalence or equals()
  * to directly test the Pairs before creating the Association,
  * but then you have to perform each join individually.
  * (This is a good Strategy anyway, because building large Sets of Elements
  *  and multiplying them before eliminating Failures.
  *  Additionally the most eliminating Joins
  *  and the Tests supported by Indices should be performed first)
  * @see JoinStreamByCols 
  * @see streamIO.object.JoinStreamByTest
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:31Z
  * digest: 1aa8a2573e2f544f048165f5c0cafeca4eec93f131025b8cdb95699630172569
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class JoinStreamByCols
extends Product {

	/** Reference to the Equivalence Relation being used to compare the Columns
	  * if null, the common equals() Method is used. 	 */
	protected IEquivalence mEQ;

	/** Column of first Container to match	*/
	protected int mCol1;

	/** Column of second Container to match	*/
	protected int mCol2;

	/** Determines whether to use an outer join	*/
	protected boolean mOuter;

	/** Initializing Constructor	*/
	public JoinStreamByCols(IIStreamIn In1, IStreamIn In2, int Col1, int Col2, boolean outer, IEquivalence EQ) 
	throws NoSuchMethodException {
		super(In1, In2);
		currItem = curr; //stateful Iterator!
//		res.ensureCapacity(In1.availAble() + In2.availAble());	//the typical Join creates as many rows as the larger table has
		in = In1; mCol1 = Col1; mOuter = outer;
		mIn2 = In2; mCol2 = Col2; mEQ = EQ; }

	/** Initializing Constructor, defaulting the Equivalence Relation to null.	*/
	public JoinStreamByCols(IIStreamIn In1, IStreamIn In2, int Col1, int Col2, boolean outer) 
	throws NoSuchMethodException {
		this (In1, In2, Col1, Col2, outer, null); }

	/** Joins a streamIO of Streams or Object[] Arrays (also mixed)
	  * with another and the Criterion given by 'Condition'.
	  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
	  * Since a new Container would be created, a streamIO is being used.
	  * A Join is the flattened (Pair => Container)
	  * Cross Product filtered by a ITester Function.
	  * This is an Optimization because it avoids creating the Pair
	  * and flattening it, before handing it over to the ITester.
	  * It also caches the Column to compare to.
	  */
	protected Object nextItemInternal() {
		if (curr.val == null) { //get the next Key
			if ((EOI == (curr.Key = in.nextItem())) && !in.isValid()) 
				return EOI; 
		}
		Object t1;
		try {
			t1 = (         curr.Key instanceof Object[]) ?
				((Object[])curr.Key)      [mCol1]:
				((IStreamIn)curr.Key).getAt(mCol1);
			if (mOuter | (EOI != (curr.val = mIn2.findNext(t1, mEQ)))) { //no Match
				return curr; } //important to use | to enforce evaluation of both Terms!
			mIn2.reSet();
		} catch (final NoSuchMethodException e) {
			throw new NoSuchMethodError(    e.toString()); } //
		return currItem = EOI; }

	/** Joins a streamIO of Streams or Object[] Arrays (also mixed)
	  * with another and the Criterion given by 'Condition'.
	  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
	  * Since a new Container would be created, a streamIO is being used.
	  * A Join is the flattened (Pair => Container)
	  * Cross Product filtered by a ITester Function.
	  * This is an Optimization because it avoids creating the Pair
	  * and flattening it, before handing it over to the ITester.
	  * It also caches the Column to compare to.
	  */
/*	public Object nextItem1() {
		Object t1, t2;
		currItem = currPair;
		while ((IStreamIn.EOI != (currPair.Key = Enum.nextItem())) || Enum.isValid()) {
			try {
				t1 = (         currPair.Key instanceof Object[]) ?
					((Object[])currPair.Key)      [mCol1]:
					((StreamIn)currPair.Key).getAt(mCol1);
				mIn2.reSet();
				while (((IStreamIn.EOI != mIn2.nextItem())) || mIn2.isValid()) {
					t2 = (         currPair.Key instanceof Object[]) ?
						((Object[])currPair.Key)      [mCol1]:
						((StreamIn)currPair.Key).getAt(mCol1);
					if (mEQ != null ? mEQ.equals(t1, t2) : t1.equals(t2)) {

						return currPair; }
//						return currItem = new PairKey(o1, o2); }  //a Pair is cheaper to create than an Association
				} if (mOuter) {
					currPair.Val = null;
					return currItem; }
//					return currItem = new PairKey(o1, null); } //outer Joins result in null for all Columns of the right Side
			} catch (     NoSuchMethodException e) {
				throw new NoSuchMethodError(    e.toString()); } //
		} return currItem = IStreamIn.EOI; }
*/

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) { //throws java.io.IOException {
	System.out.println("Testing " + JoinStreamByCols.class.getName());
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) { //throws java.io.IOException {
	testIt(args); }


}
