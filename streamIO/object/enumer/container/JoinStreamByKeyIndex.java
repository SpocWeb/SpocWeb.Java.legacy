package streamIO.object.enumer.container;

import graphs.Pair;
import streamIO.IIStreamIn;
import streamIO.object.AFilterIn;
import tester.IEquivalence;

/** Joins a Table with another and the Criterion given by the
  * @see IEquivalence
  * Therefore both this and arg have to be relational Tables
  * (i.e. Collections of Relations resp. Object Arrays)
  * Since a new Container would be created for the Result,
  * a streamIO is being used for the outer Loop.
  *
  * The first  Table contains the Foreign key
  * the second Table contains the Primary key (must be unique)
  * Referential Integrity is assumed, although
  *
  * 1) Avoids creating the Cross Product (N*M) and Filtering it
  * 	by directly selecting the Target Rows.
  * 3) flattening the Pair (ILinked List) into a different structure (Array)
  *     although this could also be done by a Filter!
  * 	and ...
  * 4) ... select specific Columns during this flattening!
  *
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class JoinStreamByKeyIndex
extends AFilterIn {

	/** Reference to the second Input Container organized as an Index	*/
	protected HashContainer mIndex;

	/** Foreign key Column of first Container to match
	  * the Primary key of the Second Container, which is defined by the
	  * @see KeyEquivalence used to build up the Index	*/
	protected Object mFKField;

	/** Determines whether to use an outer join	*/
	protected boolean mOuter;

	/** Initializing Constructor	*/
	public JoinStreamByKeyIndex(IIStreamIn In1, Object FKField, HashContainer Index, boolean outer) {
		super(In1);
		currItem = currPair;
		mFKField = FKField; mOuter = outer; mIndex = Index;
		mIndex.reSet(); //search only a single unique Match in the 2nd Table!
	}

	/** This Instance is being reused between different Calls of nextItem
	  * This saves Creation / Destruction
	  * and reuse can be tolerated often (especially when flattening the Result),
	  * otherwise a copyFilter should be appended
	  */
	protected Pair currPair = new Pair();

	/** Joins a streamIO with another and the Criterion given by the Index.
	  * Therefore both this and arg have to be relational Tables (i.e. Collections of Relations)
	  *
	  * For several Result per key you would need to cache the State:
	  * o1, mIndex
	  * This Method works only if the Primary key is not unique.
	  */
	protected Object nextItemInternal() {
		while ((EOI != (currPair.Key = in.nextItem())) || in.isValid()) {
			currPair.val = mIndex.findNext(((Relation)currPair.Key).getAt(mFKField)); //special Structure: Keyed by the wanted Column!
			if (mOuter || (EOI != currPair.val) || mIndex.isValid()) 
				return currPair; 
			mIndex.reSet(); //search only a single unique Match in the 2nd Table!
		} return EOI; }

}
