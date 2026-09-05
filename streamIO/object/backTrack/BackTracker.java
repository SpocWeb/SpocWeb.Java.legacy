package streamIO.object.backTrack;

import streamIO.IIStreamIn;
import streamIO.object.AStreamIn;
import streamIO.object.IPipe;
import tester.ITester;
import function.IFunction;

/**
  * Streams successive solutions of a backtracking/genetic search, where the {@link IPipe}
  * store's discipline (FIFO, LIFO, priority queue) determines whether the search is
  * breadth-first, depth-first, or a priority (branch-and-bound) search.
  * <p>
  * Title: BackTracker<p>
  * Description:
  * Implements a BackTracking / Genetic Algorithm where ...
  * ...the Type Store (FiFo, LiFo or Priority Queue)
  * determines the Nature of the Backtracking
  * (Breadth Search, Depth Search or a Priority Search (Branch & Bound)) and...
  * ...the Type of the Function (Generator) which returns an Array of Alternatives
  * (only those != null are used) determines the Genotype. 
  * 
  * The easiest Implementation of the Generator is just a FunctionByHash
  * containing all possible Alternatives. 
  * 
  * This realizes two Features of genetic Algorithms: 
  * Mutation/Variation: the Generator creates new possible Solution 
  * Selection/Reproduction: either by complete Suppression already in the Generator 
  * 	or by Delay in a Priority Queue. 
  * Recombination/Crossover: is not realized by this Algorithm, 
  * 	instead only Mutation varies the successful Solutions. 
  * 	Another Advantage of using a (large enough) genetic Pool 
  * 	lies in the Parallelization and little Risk of local Extremums.  
  * 
  * Die Herausforderung in der genetischen Programmierung liegen in der Codifizierung 
  * und Interpretation einer Konstellation als String / Bitmuster 
  * und ist somit sehr �hnlich zum Problem der Serialisierung. 
  * Die Rekombination mu� u.a. im Rahmen der Syntax der Serialisierung erfolgen! 
  * 
  * TODO: Memory can be saved by externalizing the Queue! 
  * 
  * Breadth Search usually requires more Memory, but is guaranteed
  * 	to return the shortest (!) path to the Solution.
  * 	Searching for Duplicates should be avoided, if the Generator can be programmed,
  * 	so that it cannot create Duplicates (see e.g. HorseProblem)
  * 
  * Depth Search is usually faster to generate a single Solution,
  * 	but the Solutions found are the more complicated ones,
  * 	except if all Solutions need the same Number of Steps (e.g. HorseProblem)
  * 
  * Branching is implemented by calling 'Function()'
  * which is supposed to return an Object[] containing the next Solutions.
  * This is equivalent to the Tree Walk in Forest.visit().
  * Returning an Array of Objects defines a Relation more than a Function.
  * 
  * If you want a single Solution, use an ITester Function,
  * otherwise if you want all Solutions, set ITester to 'null'
  * and it will iterate until the whole Store is empty again.
  * 
  * If you have a Problem solved recursively,
  * but the resulting Depth Search does not solve the Problem well,
  * e.g. because the Tree is very wide, or because there is no final Depth,
  * or because there is a way to define "Closeness" to the Solution,
  * you can use a Breadth Search or Priority Search alternatively.
  * Priority Search is a 'greedy' Algorithm, because it checks locally, but it can be changed
  * using a modified Function 'less' with a Threshold for the Heap.
  * 
  * Recursive Solutions are not completely isomorphic to using a Stack,
  * because next to the Value also the Return Address and Value are traced
  * and the Sequence of Calls are different.
  * A Stack can NOT be used, if Processing is distributed like in the following Example:
  *
  * void test (int i) {
  * 	A(i);
  * 	test(i+1);
  * 	B(i); }
  *
  *
  * The linear Search for Existence in most Stores makes the Algorithm slow,
  * so you should extend it by a second Store with fast Searching (HashTable).
  *
  * The Parameters of the Recursion must be bundled as Class or Array
  * and are written to the Store.
  *
  * This is used for Problems that test all possible Outcomes of one
  * or several consecutive Steps.
  * It cannot be used for Games with large Numbers of Possibilities
  * like e.g. BackGammon, where the Possibilities are increased
  * by the Outcome of two Dices being thrown (i.e. 12 Results * n)
  * There it is better to leave this to a neuronal Network.
  *
  * Optionally ('testStore') the Algorithm checks using the .equals() Method,
  * whether an Element has been created double and should be ignored
  * to avoid multiple work and even infinite Recursion.
  * A List of all Elements is not kept in Memory though,
  * so to totally avoid infinite Recursion (with longer Periods),
  * (most probable on Depth Search), use your own Cache, preferably a HashTable,
  * so you don't have to run through all Elements,
  * but identify them by their HashCode.
  *
  * Optionally a 'Temperature' decides about rejecting some Changes,
  * but not in this Implementation,
  * since it doesn't know anything of Cost, Energy or Temperature.
  * The simulated Annealing doesn't work with Backtracking anyway,
  * but does a simple loop over all the next proposals. 
  * It would be better to create Permutations and sort them in a Priority Queue.
  *
  * Branch & Bound:
  * Normally the Branches are attributed with Values representing their Cost.
  * To process the Branches with a minimum Cost first,
  * you have to use a Priority Queue.
  * If the Cost is increasing monotonously you can bound the Operation on Branches
  * as soon as they are more expensive than the best Solution found so far.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:44:23Z
  * digest: 095bc892654bdad84f357a9dae9b3de3e481dbd10a7a51250313d09c30e963b6
  * stale: false
  * tags: [code/backtracking, code/algorithm]
  * concepts: [Backtracking Search]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class BackTracker
extends AStreamIn { // IStreamIn {
	
	/** the Storage determines the Search Strategy 	 */
	protected IPipe mStore;
	
	/** fast, direct Access Storage for quickly checking whether a Candidate has already been encountered 	 */
	protected IPipe mBackup;
	
	/** Tester for the Success 	 */
	protected ITester   mFound;
	
	/** Generator for new Candidates	 */
	protected IFunction mGenerator;
	
	/** Flag whether to also search in the Store
	 * TODO: actually this is unnecessary, since all items could be put into the Backup Storage
	 * for faster Access. 	 */
	protected boolean  mTestStore;

	/** Cache for the current Item	*/
	protected Object currItem;
	
	/** Delegates to the store's own maximum mark size.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return mStore.getMaxMarkSize(); }

	/** Delegates to the store's own current position.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return mStore.getPosition(); }
	
	/**Initializing Constructor.
	 * @param Store  should already be filled by one or more Starter Elements
	 *				if it is a Container, it can also test, whether
	 * @param Backup will be used to collect already tested Solutions, if not 'null'
	 * @param found  tests for a Solution, if it is null, always false is assumed
	 *			so all Solutions are generated (e.g. for a full Sweep!)
	 * @param Generator generates the new Elements and hands it back in an Array
	 *				(null Elements in this Array are ignored)
	 * @param testStore determines, whether the Store is tested for existing Elements. 	 */
	public BackTracker(IPipe Store, IPipe Backup, ITester found,
					 IFunction Generator, boolean testStore) {
		this.mStore     = Store;
		this.mBackup    = Backup;
		this.mFound     = found;
		this.mGenerator = Generator;
		this.mTestStore = testStore; }
	
	/** Returns the result cached by the most recent {@link #nextItem()} call.
	 * @return the next Result calculated using the BackTracking Algorithm defined by the Store.
	  * For multiple Results call this Method until it returns IStreamIn.EOI
	  */
	public Object currItem() { return currItem; }

	/** Delegates to the store's own availability.
	 * @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
	public long availAble() { return mStore.availAble(); }

	/** Expands the store's next candidate through the generator until a solution accepted by
	 * the tester is found, or the store is exhausted.
	 * @return the next Result calculated using the BackTracking Algorithm defined by the Store.
	  * For multiple Results call this Method until it returns IStreamIn.EOI
	  */
	public Object nextItem() { ////get the latest Element from the Store
		for(Object item; EOI != (item = mStore.nextItem()) || mStore.isValid();) {
			final Object[] newList = (Object[]) mGenerator.Map(item);	//generate all new Solutions
			if (newList == null) {
				continue; } 
			int i = newList.length;
			while (--i >= 0) {	//and add them to the Store
				item = newList[i];
				if (item == null) {
					continue; 
				} 	//'null's are ignored; this allows to use fixed Size Return Arrays
				if (mBackup != null) {	//if existing, use Backup exclusively
					if   (mBackup.contains(item)) continue;	//testing after  Use
					else  mBackup.addItem (item); 
				}
				if (mTestStore) {	//search whether this Item already exists.
					if (mStore.contains(item)) {
						continue; 
					} 
				}	//testing before Use
				mStore.addItem(item);	//store it for later testing (before Use)
				if (mFound != null) {
					if (mFound.test(item)) {
						return currItem = item; 
					} 
				}	//Return findings...
			}
		}
		return currItem = IIStreamIn.EOI; }	//if no Item was found

	/** Tests all Methods of this Class	 */
	public static void testIt()	{
		PermutationProblem.testIt();
		AchterProblem.testIt();
		QueenProblem .testIt();
		HorseProblem .testIt();
		TravelProblem.testIt();
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
