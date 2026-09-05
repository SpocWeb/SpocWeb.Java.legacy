package streamIO.object.enumer.container;

import graphs.IPair;
import streamIO.IIStreamIn;
import streamIO.object.IStreamIn;
import streamIO.object.Product;
import tester.IEquivalence;
import tester.TesterEquivalence;

/** Joins a Table with another and the Criterion given by the
  * @see IEquivalence
  * Therefore both this and arg have to be relational Tables (i.e. Collections of Relations)
  * Since a new Container would be created, a streamIO is being used.
  *
  * 1) Creates the Cross Product (N*M)
  * 2) and Filters it using the given Equivalence e.g.
  * 	@see  KeyEquivalence
  * 	@see TesterEquivalence
  * 3) flatten Pair (ILinked List) into a different structure (Array)
  * 	and ...
  * 4) ... select specific Columns during this flattening!
  *
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class JoinStreamByEquivalence
extends Product {

	/** Flattens a joined key/value {@link IPair} of Containers into a single Container
	  * holding the selected Columns of both sides.
	  * @return a Container that consists of the selected Columns 	 */
	public static Container flattenPair(IPair p, boolean[] Cols1, boolean[] Cols2) {
		Container c1  = (Container) p.getKey(); // Key;
		Container c2  = (Container) p.getVal(); // Val;
		Container ret = (Container) c1.newInstance();
		ret.addItems(c1, Cols1);
		ret.addItems(c2, Cols2);
		return ret; }

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
	public JoinStreamByEquivalence(IIStreamIn In1, IStreamIn In2, int Col1, int Col2, boolean outer, IEquivalence EQ)
	throws NoSuchMethodException { //when In2 is not reset()able!
		super(In1, In2);
		currItem = curr;
//		res.ensureCapacity(In1.availAble() + In2.availAble());	//the typical Join creates as many rows as the larger table has
		in = In1; mCol1 = Col1; mOuter = outer;
		mIn2 = In2; mCol2 = Col2; mEQ = EQ;
		mIn2.reSet(); }

	/** Initializing Constructor, defaulting the Equivalence Relation to null.	*/
	public JoinStreamByEquivalence(IIStreamIn In1, IStreamIn In2, int Col1, int Col2, boolean outer)
		throws NoSuchMethodException { //when In2 is not reset()able!
		this (In1, In2, Col1, Col2, outer, null); }

	/** Joins a streamIO with another and the Criterion given by 'Condition'.
	  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
	  * Since a new Container would be created, a streamIO is being used.
	  * A Join is the flattened (Association => Container)
	  * Cross Product filtered by a ITester Function.
	  * This is an Optimization because it avoids creating the Association
	  * and flattening it, before handing it over to the ITester.
	  */
	protected Object nextItemInternal() {
		while ((EOI != (curr.Key = in.nextItem())) || in.isValid()) {
			curr.val = mIn2.findNext(curr.Key, mEQ);
			if (mOuter || (EOI != curr.val) || mIn2.isValid()) 
				return curr; 
			mIn2.reSet();
		} return IIStreamIn.EOI; }

}
