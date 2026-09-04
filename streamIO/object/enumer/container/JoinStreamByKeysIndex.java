package streamIO.object.enumer.container;

import graphs.Pair;
import streamIO.IIStreamIn;
import streamIO.copy.monoid.Association;
import streamIO.object.AFilterIn;
import tester.IEquivalence;

/** Joins a Table with another and the Criterion given by the
  * @see IEquivalence
  * Therefore both this and arg have to be relational Tables (i.e. Collections of Relations)
  * Since a new Container would be created, a streamIO is being used.
  *
  * 1) Avoids creating the Cross Product (N*M) and Filtering it
  * 	by directly selecting the Target Rows.
  * 3) flatten Pair (ILinked List) into a different structure (Array)
  * 	and ...
  * 4) ... select specific Columns during this flattening!
  *
  */
public class JoinStreamByKeysIndex
extends AFilterIn {

	/** Reference to the second Input Container organized as an Index	*/
	protected HashContainer mIndex;

	/** Foreign key Columns of first Container to match
	  * the Primary key Fields of the Second Container, which is defined by the
	  * @see KeysEquivalence used to build up the Index	*/
	protected Object[] mFKFieldNames;

	/** Column Names of second Container to match.
	  * The Column Names of the Second Container are defined by the
	  * @see KeysEquivalence used to build up the Index	*/
	protected Object[] mPKFieldNames;

	/** Associations of the Relation mRel being used by KeysEquivalence to compare the Columns.
	  * by reusing the Associations no new Test Relation has to be created for each Test
	  * which is a considerable Optimization.
	  * The same Optimization is used in
	  * @see RecordSet  */
	protected Association[] mFields;

	/** Reference to the Relation being used by KeysEquivalence to compare the Columns. 	 */
	protected Relation mRel;

	/** Determines whether to use an outer join	*/
	protected boolean mOuter;

	/** This Instance is being reused between different Calls of nextItem
	  * This saves Creation / Destruction
	  * and reuse can be tolerated often (especially when flattening the Result),
	  * otherwise a copyFilter should be appended
	  */
	protected final Pair currPair = new Pair();

	/** Initializing Constructor	*/
	public JoinStreamByKeysIndex(IIStreamIn In1, Object[] FKFieldNames, HashContainer Index, Object[] PKFieldNames, boolean outer) {
		super(In1);	mOuter = outer; mIndex = Index;
		currItem = currPair; //fixed Relation!
		mFKFieldNames = FKFieldNames;
		mPKFieldNames = PKFieldNames;
	    int numCols = mFKFieldNames.length;
	    mFields = new Association[numCols];
	    mRel = new Relation(); //Optimization from currRow()
	    Association tmp;
	    while (--numCols >= 0) {
			tmp = mFields[numCols] = new Association();
			tmp.key = mPKFieldNames[numCols]; //the Value is filled in @see nextItem()
			mRel.addItem(tmp);
	    }
	}

	/** Joins a streamIO with another and the Criterion given by the Index.
	  * Therefore both this and arg have to be relational Tables (i.e. Collections of Relations)
	  *
	  * For several Result per key you would need to cache the State:
	  * o1, mIndex
	  * This applies only if the Primary key is not unique.
	  */
	protected Object nextItemInternal() {
		while ((EOI != (currPair.Key = in.nextItem())) || in.isValid()) {
			Relation r1 = (Relation) currPair.Key;
			int i = mFKFieldNames.length;
			while (--i >= 0) {
				mFields[i].val = r1.getAt(mFKFieldNames[i]); }
			mIndex.reSet();
			//End of Init...
			currPair.val = mIndex.findNext(mRel);
			if ((EOI != currPair.val) || mOuter || mIndex.isValid()) { //not EOI...
				return  currPair; }
		} return EOI; }

}
