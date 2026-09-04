package streamIO.object.enumer.container;

import graphs.Pair;
import streamIO.IIStreamIn;
import streamIO.object.AFilterIn;
import streamIO.object.IStreamIn;
import streamIO.object.filterInOut.FilterByTester;
import tester.IEquivalence;

/** Joins two Streams (the second restartable)
  * by selecting Pairs where the Values of the given Fields ("Columns")
  * of each Record are equivalent as determined by the equals() Method or by the
  * @see Equivalence 'Condition'.
  * Different from JoinStreamsByCols this Implementation selects the Columns
  * by Objects (e.g. Column Names i.e. Strings) instead of Column Numbers
  *
  * On joining it selects only certain Columns (Fields).
  * Therefore both this and arg have to be relational Tables (i.e. Collections of Relations)
  * Since a new Container would be created, a streamIO is being used.
  * This is an Optimization because it avoids creating the Association
  * and flattening it, before handing it over to the ITester.
  *
  * For doing faster Joins, you have to store the second streamIO into a Container
  * which allows for fast Searches (HashTable or sorted Container).
  * For each Index you have to store the streamIO into a different Container.
  * This easily allows for different "Indices", but not in the Sense of numeric Positions,
  * but directly as References to the original Rows.
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
  */
public class JoinStreamByFields
extends AFilterIn {

	/** Reference to the Equivalence Relation being used to compare the Columns
	  * if null, the common equals() Method is used. 	 */
	protected final IEquivalence mEQ;

	/** Reference to the second Input streamIO	*/
	protected final IStreamIn mIn2;

	/** Column Name of first Container to match	*/
	protected final Object mCol1;

	/** Column of second Container to match	*/
	protected final Object mCol2;

	/** Determines whether to use an outer join	*/
	protected final boolean mOuter;

	/** This Instance is being reused between different Calls of nextItem
	  * This saves Creation / Destruction
	  * and reuse can be tolerated often (especially when flattening the Result),
	  * otherwise a copyFilter should be appended
	  */
	protected final Pair currPair = new Pair();

	/** Initializing Constructor	*/
	public JoinStreamByFields(IIStreamIn In1, IStreamIn In2, Object Col1, Object Col2, boolean outer, IEquivalence EQ) {
		super(In1);
		currItem = currPair;
//		res.ensureCapacity(In1.availAble() + In2.availAble());	//the typical Join creates as many rows as the larger table has
		in = In1; mCol1 = Col1; mOuter = outer;
		mIn2 = In2; mCol2 = Col2; mEQ = EQ; }

	/** Initializing Constructor, defaulting the Equivalence Relation to null.	*/
	public JoinStreamByFields(IIStreamIn In1, IStreamIn In2, Object Col1, Object Col2, boolean outer) {
		this (In1, In2, Col1, Col2, outer, null); }

	/** Joins a streamIO with another and the Criterion given by 'Condition'.
	  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
	  * Since a new Container would be created, a streamIO is being used.
	  * A Join is the flattened (Pair => Container)
	  * Cross Product filtered by a ITester Function.
	  * This is an Optimization because it avoids creating the Pair
	  * and flattening it, before handing it over to the ITester.
	  * It also caches the Column to compare to.
	  *
	  * Only searches for unique Matches!
	  */
	protected Object nextItemInternal() {
		Object tst1, tst2;
		Relation o1, o2;
		while ((IIStreamIn.EOI != (o1 = (Relation) in.nextItem())) || in.isValid()) {
			currPair.Key = o1;
			tst1 = o1.getAt(mCol1); mIn2.reSet();
			while ((IIStreamIn.EOI != (o2 = (Relation) mIn2.nextItem())) || mIn2.isValid()) {
				tst2 = o2.getAt(mCol2);
				if (mEQ != null ? mEQ.equals(tst1, tst2) : tst1.equals(tst2)) {
					currPair.val = o2;
					return currPair; }
			}
			if (mOuter) 
				return currPair; //outer Joins result in null for all Columns of the right Side
		} 
		return IIStreamIn.EOI; }

}
