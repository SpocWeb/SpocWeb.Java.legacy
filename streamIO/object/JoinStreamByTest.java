package streamIO.object;

import streamIO.IIStreamIn;
import streamIO.object.filterInOut.FilterByTester;
import tester.ITester;

/** Joins a Table with another and the Criterion given by the
  * @see ITester 'Condition'.
  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
  * Since a new Container would be created, a streamIO is being used.
  * This is an Optimization because it avoids creating the Association
  * and flattening it, before handing it over to the ITester.
  *
  * 1) Cross Product (N*M) generates a streamIO of Pairs.
  * 2) Filter the Pairs using a ITester or the Elements using an Equivalence()
  * @see FilterByTester
  * @see FilterOutByTester
  * 3) flatten the Pair (ILinked List) into a different structure (Array)
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:31Z
  * digest: 384a601195181893c4be1ab0ea9725352fd993f1eb5001e1d863431cdd935a81
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class JoinStreamByTest
extends Product {

	/** Reference to the ITester Function	*/
	protected ITester mCondition;

	/** Determines whether to use an outer join	*/
	protected boolean mOuter;

	/** Initializing Constructor	*/
	public JoinStreamByTest(IIStreamIn In1, IStreamIn In2, ITester Condition, boolean outer) throws NoSuchMethodException {
		super(In1, In2);
		currItem = curr; //stateful Iterator!
//		res.ensureCapacity(In1.availAble() + In2.availAble());	//the typical Join creates as many rows as the larger table has
		in = In1; mCondition = Condition;
		mIn2 = In2; mOuter = outer; }

	//This is exactly the Multiplication Logic
	//don't replicate that here.
	//Instead just create Filters based on Testers and chain them to the Product Stream
	
	/** Joins a streamIO with another and the Criterion given by 'Condition'.
	  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
	  * Since a new Container would be created, a streamIO is being used.
	  * A Join is the flattened (Pair => Container)
	  * Cross Product filtered by a ITester Function.
	  * This is an Optimization because it avoids creating the Association
	  * and flattening it, before handing it over to the ITester.
	  *
	  * Unfortunately it is complicated to delegate the inner Loop completely,
	  * so no Template Method can be employed to e.g. speed up the Search
	  * by delegating to findNext() like in @see JoinStreamByCols
	  * TODO: The Distinction between an inner and an outer join...
	  */
	protected Object nextItemInternal() {
		if (curr.val == null) { //get the next Key
			if ((EOI == (curr.Key = in.nextItem())) && !in.isValid()) 
				return EOI; 
		}
		//This Loop cannot easily be delegated because it requires a new Method in StreamIn and setting currPair.Val in it!
		while ((EOI != (curr.val = mIn2.nextItem())) || mIn2.isValid()) {
			if (mCondition.test(curr)) {
				return curr; }
		}
		mIn2.reSet(); //prepare the next Loop...
		if (mOuter) 
			return curr; 
		return EOI; }

	/** Joins a streamIO with another and the Criterion given by 'Condition'.
	  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
	  * Since a new Container would be created, a streamIO is being used.
	  * A Join is the flattened (Pair => Container)
	  * Cross Product filtered by a ITester Function.
	  * This is an Optimization because it avoids creating the Association
	  * and flattening it, before handing it over to the ITester.
	  * TODO: The Distinction between an inner and an outer join...*/
/*	public Object nextItem() {
		currItem = currPair;
		while ((IStreamIn.EOI != (currPair.Key = Enum.nextItem())) || Enum.isValid()) {
			try { mIn2.reSet();
			} catch      (NoSuchMethodException e) {
				throw new NoSuchMethodError    (e.toString()); } //
			while ((IStreamIn.EOI != (currPair.Val = mIn2.nextItem())) || mIn2.isValid()) {
				if (mCondition.Test (currItem)) {
					return currItem; }
			} if (mOuter) {
				return currItem; }
		} return currItem = IStreamIn.EOI; }
*/
}
