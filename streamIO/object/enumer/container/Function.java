package streamIO.object.enumer.container;

//import Testers.ITester; //
//import Testers.TestObject;
//import Testers.TestEquivalence; //
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import streamIO.AStreamOut;
import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IIterAble;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.IInteger;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.monoid.AMapper;
import streamIO.copy.monoid.Association;
import streamIO.copy.monoid.IMonoid;
import streamIO.copy.monoid.ISemiMonoid;
import streamIO.copy.monoid.integer.Permutation;
import streamIO.exception.OperationNotSupported;
import streamIO.exception.ReadOnlyException;
import streamIO.object.IStreamIn;
import streamIO.object.ModificationException;
import streamIO.object.filterIn.FilterInPair;
import streamIO.object.filterInOut.FilterByFunction;
import streamIO.object.integer.XMLScanner;
import stringOp.parser.IIStreamIn_Int;
import stringOp.parser.Scanner;
import tester.IEquivalence;
import function.IDynamicFunction;
import function.IInvertAble;
import function.byref.ByRefInt;
import graphs.ICopy;
import graphs.KeyValuePair;
import graphs.Pair;
import graphs.SparseGraph;

/**
  * This Class can be used to represent Functions as well as Relations,
  * but it's primary Use is for Functions. It is implemented as a HashMap.
  * Relations with voluminous Sets on the right Side
  * are not represented efficiently in this structure, especially when:
  * - the whole Result Set has to be constructed and returned from each Mapping
  * - the right Side contains many Elements, thus tricking the LoadFactor Mechanism
  * - the Return Type has to be cast.
  * @see Relation
  *
  * A Relation is a Collection containing Associations (and possibly other Maps).
  * A Function is a right-unique Relation,
  * i.e. there is only a single Result for each Mapping.
  *
  * In the most abstract Sense, a Collection of Monoids can act as a single Monoid.
  * This is similar to merging a streamIO of Streams into one.
  * Any Container could be used for a Relation,
  * but because of it's frequent use for Mappings, a HashTable is the Default.
  *
  * A Relation can be used to describe a Graph or disjoint Sets.
  * For non disjoint Sets you have to use a Container per Set.
  *
  * Relations can also be represented as boolean Matrices A*B->{true, false}
  * A more general Approach represents Weights of Paths A*B->R+
  *
  * Thus unweighted Graphs are also represented using this Construct.
  * The Degree of a Node is already defined in the HashTable class.
  * Connected Components are defined for undirected Edges.
  * All Nodes of a Connected Component...
  *
  * A textual Representation of a Relation is the Properties Class and Files.
  * This Class is able to read those and construct the Relation from it.
  * The structure of Property Files/Relations is not nested.
  * The Scanner uses CR/LF, # and = Elements, empty Lines are ignored.
  * Alternatively an EOL sensitive StreamTokenizer could be used.
  *
  * Of course also an XML Representation is possible, but not as readable.
  * A compressed Relation Syntax like the following would be even more readable:
  * a,b,c=d (a, b and c map to d)
  * u=x,y,z (u maps to x, y and z)
  *
  * @see: dtv Atlas Bd1 "Graphentheorie",
  * On adding new Associations you have to check,
  * whether it already exists by checking for Equality in key AND Value.
  * This is done when mFunction == true, so Relation and Function would only differ
  * by the way they check for Equality.
  * The following Condition is not fulfilled (yet)!
  * 	a={b,c,d} <=> {a=b, a=c, a=d}
  *
  * Design Decisions:
  * This Class is not derived from a Container (like HashTable),
  * because the Interface is very large anyway.
  * It mainly implements the Monoid Interface and hand through
  * some Methods of the underlying Container, but not the Container itself.
  *
  * The Container should be hashed or sorted,
  * because you can very quickly check whether an Item already exists
  * and retrieve the Item by the key.
  * You could also use ordered Lists like ArraySorted or Tree,
  * because these also have very fast Access, but only on orderAble Elements!
  *
  * Relations can always be inverted.
  * Functions may no longer be Functions when inverted, but Relations.
  *
  * A Set of Relations (or Associations, or Monoids anyway which are 1:1 Relations)
  * is again a Relation (Monoid)
  * and thus you only need to have a Container with Set Operations.
  *
  * TODO: StreamIn should be implemented to allow reading the Function
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * digest: 2825424d6dad07db1162cbc062859f10e3f6bc2b86940468c3e7a16789c9f2ce
  * stale: false
  * -->
  */
public class Function
extends AMapper //AMonoid //HashTable
implements IIterAble, IInvertAble, IDynamicFunction { //to be able to stream into this Relation

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	/** Serialization version UID. */
	private static final long serialVersionUID = 1L;

	/** Returns a Pair with...
	  * ...a Function mapping the distinct Elements of the (finite) streamIO to 'Integer'
	  * ...an Object[] Array mapping 'int' to the Elements.
	  * Since Objects may be duplicate in the streamIO, this is not a Function,
	  * but usually a Relation. */
	final static public KeyValuePair GET_ENUMERATION(IIStreamIn Elements) { //throws IllegalAccessException, InstantiationException {
		Container cnt = new HashContainer();
		int Count = -1;
		Association ass;
//		Object[]  arr = new Object[(int) Elements.availAble()]; //make the Array larger
		ArrayList arr;
		if (Elements instanceof IStreamIn) {
			arr = new ArrayList();
		} else { //size the Array correctly..
			arr = new ArrayList((int) ((IAvailAble)Elements).availAble()); }
		for(Object Key;((Key = Elements.nextItem()) != IIStreamIn.EOI) || Elements.isValid();) {
			ass = new Association(); ass.key = Key;
			if (cnt.unionItem(ass)) { //has been added, not duplicate!
				ass.val = new ByRefInt(++Count);
				arr.set(Count, Key); }
		}
		Object[] ret = new Object[++Count];
		System.arraycopy(arr, 0, ret, 0, Count); //resize the Array
		Function rel = new Function(cnt, true); //Function!
		return new KeyValuePair(ret, rel); }

	/** Returns a Relation that maps Objects to their Position in this Array
	  * It cannot return a Function, because Objects may be duplicated
	  * in the Array.
	  */
	public static Function InverseArray(Object[] arr) {
		Function ret = new Function();
		int i = arr.length;
		while (--i >= 0)
			ret.addItem(arr[i], new ByRefInt(i));
		return ret; }

	/** Maps the Indices of the Objects in 'rel' to their new Positions given in q.
	  * Relies on arg.Values being of Type 'ByRefInt' and non repeating Elements.
	  * An alternative Naming fitting the setAt() Method in IDynamicFunction would be getAt()
	  */
	public static IIStreamIn mapAt(int[] q, IIStreamIn iter) {
//		IStreamIn iter = rel.ValueIterator();
		ByRefInt tmp;
		while ((tmp = (ByRefInt) iter.nextItem()) != IIStreamIn.EOI) {
			tmp.Value = q[tmp.Value]; }
		return iter; }

	////////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Container for Mappings
	  * These don't even need to be Associations, but could be Relations themselves...
	  * That would result in  */
	protected Container mCnt;

	/** The Keys of the Elements in this Container
	  * which is used to determine the Definition Set of a Relation or Function */
	protected Container mKeys;

	/** The Values of the Elements in this Container
	  * no matter whether it contains Associations, Pairs or IPairs
	  * which is used to determine the Value Set of a Relation or Function */
	protected Container mValues;

	/** Flag whether this Relation is a Function or not */
	protected boolean mFunction;

	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods get/set/is/make
	////////////////////////////////////////////////////////////////////////////////

	/** Flag whether this Relation is a Function or not
	  * This cannot be set a posteriori, because it requires a certain inner structure.  */
	public boolean isFunction() { return mFunction; }

	//  Division from the Right or from the Left is a Filter / Mapping

	/** Returns the Keys of the Elements in this Container
	  * no matter whether it contains Associations, Pairs or IPairs
	  * which is useful to determine the left Factor of a Product
	  * or the Definition Set of a Relation or Function */
	public Container getKeys() {
		if (mKeys == null) {
			mKeys = mCnt.getKeys(); }
		return mKeys; }

	/** Returns the Values of the Elements in this Container
	  * no matter whether it contains Associations, Pairs or IPairs
	  * which is useful to determine the right Factor of a Product
	  * or the Value Set of a Relation or Function */
	public Container getValues() {
		if (mValues == null) {
			mValues = mCnt.getValues(); }
		return mValues; }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////////

	/** Constructs a new, empty Relation with a HashTable and default capacity and load factor.	 */
	public Function() { mCnt = new HashContainer(); }

	/** Constructs a new, empty Relation with a HashTable and default capacity and load factor.	 */
	public Function(boolean Function) {
		mCnt = new HashContainer(); mFunction = Function; }

	/**Fills the Relation from the Result of the Input streamIO	 */
	public Function(InputStream IS) throws IOException {
		mCnt = new HashContainer(IS.available() >> 2); addItems(IS); }

	/** Constructs a new, Relation with this Container,
	  * which should either be empty or filled with Monoids.	 */
	public Function(Container Cont) { mCnt = Cont; }

	/** Constructs a new, Relation with this Container,
	  * which should either be empty or filled with Monoids.	 */
	public Function(Container Cont, boolean Function) {
		mCnt = Cont; mFunction = Function; }

	/** Constructs an empty Relation with the specified initial capacity
	  * and capacity increment.
	  *
	  * @param   initialCapacity	 the initial capacity of the Relation.
	  * @param   loadFactor			the amount by which the capacity is
	  *							  increased when the Relation overflows.
	  */
	public Function(int initialCapacity, float loadFactor) {
		mCnt = new HashContainer(initialCapacity, loadFactor);}

	/** Constructs a new, empty Relation with the specified initial capacity
	  * and defaults the load factor to 'defaultLoadFactor'.
	  *
	  * @param   initialCapacity   the initial capacity of the Relation.
	  */
	public Function(int initialCapacity) {
		mCnt = new HashContainer(initialCapacity); }

	/** Constructs a new, empty Relation with the specified initial capacity
	  * and defaults the load factor to 'defaultLoadFactor'.
	  *
	  * @param   eq   the Equivalence ITester of this Relation.
	  */
	public Function(IEquivalence eq) {
		mCnt = new HashContainer(eq); }

	////////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	public IIStreamIn Iterator() {
		 return mCnt.Iterator(); }

	//Additionally you can read recursive Relations
	//from XML Structures without using Attributes

	/** Retrieves the next Item of a nested (,) structure with optional Tag Names:
	  * TagName(Value1, ..., ValueN)TagName
	  *
	  * Uses the Vector Class to return the Result, because it's size is unknown yet.
	  * This Grammar is in Fact equivalent to XML without Attributes.
	  * Just replace "<Tag>" by "Tag(" and "</Tag>" by ")Tag,"
	  * and the Result can be parsed by this Routine!  */
	public Function readRelation(Scanner scn) throws IOException { //If no Start Tag: return the current Item
		while(scn.currToken != Scanner.SCN_TAG_STOP) mCnt.addItem(readAssociation(scn)); //recursively read the inner Items...
		if (! scn.strictItem) mCnt.addItem(readAssociation(scn)); //...read an unterminated Item.
		else {scn.clearString(); scn.nextToken(); } //skip any untermiated Contents
		return this; }

	/**
	  * Helper Method for deserializing a Relation:
	  * Retrieves the next Item of a nested (,) structure with optional Tag Names:
	  * TagName(Value1, ..., ValueN)TagName
	  *
	  * Uses the Vector Class to return the Result, because it's size is unknown yet.
	  * This Grammar is in Fact equivalent to XML without Attributes.
	  * Just replace "<Tag>" by "Tag(" and "</Tag>" by ")Tag,"
	  * and the Result can be parsed by this Routine!  */
	public static Association readAssociation(Scanner scn) throws IOException { //If no Start Tag: return the current Item
		String Key = scn.getResult(); //read the leading Key
		int prevToken = scn.currToken; scn.nextToken(); //read ahead on Stop Tokens, you lose the closing
		if (prevToken != Scanner.SCN_TAG_START) { return new Association(Key, ""); }	//or delimiting Tag...
		if (! scn.leadingKey &&(Key.length() > 0)) throw new AbstractMethodError();

		Function lList = new Function().readRelation(scn);	 //avoid Relations with single Elements! Flatten those!

		if (! scn.trailingKey) {if (scn.getResult().length() > 0) throw new AbstractMethodError();}
		else {
			if ((scn.getResult() != Key) && scn.leadingKey && ! scn.Result.equals(Key))
			   throw new AbstractMethodError();
			Key = scn.Result; }  //optionally accept a trailing Key
		scn.nextToken();
		return new Association(Key, lList); }

	/** Returns the In-Degree of the Node j,
	  * i.e. the Number of Edges going into of this Node.
	  * For undirected Graphs the InDegree is equal to the OutDegree
	  * and simply called "Degree()" which is already defined in HashTable and Bag.
	  * To calculate the InDegrees of all Nodes,
	  * it is better to create the Inverse (Transpose) of this Graph.
	  */
/*	public int InDegree(int i) {
		int ret = 0;
		int j = Nodes.length; while (--j >= 0) {
			Edge t = Nodes[j];	//any Node represents a Connection, no matter which Weight.
			while (t != null) { if (t.Node == i) ++ret; t = t.Next; }
		} return ret; }

	////////////////////////////////////////////////////////////////////////////
	//	Disjoint Set Equivalence Relation Operations Start
	//	every Item has only one 'Parent', i.e. equivalent Item
	//  The Function must not have any Cycles, i.e. form a DAG or a DAG Forest
	////////////////////////////////////////////////////////////////////////////

	/** Follows Parent references without path compression, until a Root is reached.
	 * @return  the (current) Root of this Element when defining Equivalences.
	  * This is the fastest way, but it does not reduce the needed time
	  * for the next Search like the other Implementations.
	  * The Root may not be defined, when this Function contains Cycles! */
	public Object findRootFast(Object x) {
		Object p; //in fact a Function, no relation.
		while ((p = getAt(x)) != IStreamIn.EOI)	//!= x)	//this test is for the Root pointing to itself!
			x = p;
		return x; }

	/** Follows Parent references, path-halving one Level at a Time as it climbs.
	 * @return the (current) Root of this Element when defining Equivalences.
	  * The Root may not be defined, when this Relation contains Cycles!
	  * Reduces the Height of the Tree by one on every Level.
	  * Uses two indexed accesses, so it is very fast too!	 */
	public Object findRoot(Object x) {
		Object pp, p; //in fact a Function, no relation.
		if ((p = getAt(x)) == IStreamIn.EOI) return x;
		while((pp = getAt(p)) != IStreamIn.EOI) { //&& (pp != x))	//this test is for both the Root pointing to itself and weighted Roots!
			replaceAt(x, pp); x = p; p = pp; }
		return p; }

	/** Recursively follows Parent references, fully compressing the Path to the Root.
	 * @return  the (current) Root of this Element when defining Equivalences.
	  * The Root may not be defined, when this Relation contains Cycles!
	  * Reduces the Height of the Tree to one on every Element of the Search Tree.
	  * Uses Recursion, so it is slower than findRoot on the first Operation,
	  * but since replaceAt() is an expensive Operation, the savings are great!	 */
	public Object findRootTotal(Object x) {
		Object pp, p;
		if ((p = getAt(x)) == IStreamIn.EOI) return x;
		replaceAt(x, pp = findRootTotal(p)); //find the root and set it to all Elements on the way back.
		return pp; }

	/** Delegates to {@link #isEquivalent(Object, Object, boolean)} without uniting the Roots.
	 * @return  true, if key and Value are equivalent,
	  * considering all Relations as Equivalence Relations.
	  */
	public boolean isEquivalent(Object Key, Object Value) {
		return isEquivalent(Key, Value, false); }

	/** Checks both Elements for Equivalence by comparing their Roots.
	  * After this they are optionally united using the gathered Information.	 */
	public boolean isEquivalent(Object x, Object y, boolean union) {
		boolean ret = (x == y) || x.equals(y) ||
			((x = findRoot(x)) ==
			 (y = findRoot(y)));
		if ((union) && (!ret))
			setAt(x, y, true); //or parent[y] = x;
		return ret; }

	/** Checks both Elements for Equivalence by comparing their Roots.
	  * After this they are optionally united using the gathered Information.
	  * The union is done in a balanced way,
	  * storing the Number of child Elements as negative Values in parent[].
	  * This leads to a reduced average time on the next Search.	 */
	public boolean isEquivalentAVG(IMetricIRing x, IMetricIRing y) {
		x = (IMetricIRing) findRoot(x);
		y = (IMetricIRing) findRoot(y);
		boolean ret = ((x == y) || x.equals(y));
		if (! ret) {	//concatenate both trees so the resulting Tree is wider.
			IMetricIRing ParentX = (IMetricIRing) getAt(x);
			IMetricIRing ParentY = (IMetricIRing) getAt(y);
			if  (x.isLessThan(ParentY)) {
				 ParentX.addAt(ParentY); ParentX.dec(); replaceAt(y, x); }
			else{ParentY.addAt(ParentX); ParentY.dec(); replaceAt(x, y); }
		} return ret; }

	/** Checks both Elements for Equivalence by comparing their Roots.
	  * After this they are optionally united using the gathered Information.
	  * The union is done in a balanced way,
	  * storing the Height of the Subtree as negative Values in parent[].
	  * This leads to a reduced maximum time on the next Search (worst case).	 */
	public boolean isEquivalentMAX(IMetricIRing x, IMetricIRing y) {
		x = (IMetricIRing) findRoot(x);
		y = (IMetricIRing) findRoot(y);
		boolean ret = ((x == y) || x.equals(y));
		if (! ret) {	//concatenate both trees so the resulting Tree is smaller.
			IMetricIRing ParentX = (IMetricIRing) getAt(x);
			IMetricIRing ParentY = (IMetricIRing) getAt(y);
			if  (ParentX.isLessThan(ParentY))
				{ParentX.dec(); replaceAt(y, x); }
			else{ParentY.dec(); replaceAt(x, y); }
		} return ret; }

	//////////////////
	//	new Methods	//
	//////////////////

	/** Returns the FIRST component at the specified index
	  * @see findFirst()
	  * If the Index does not exist, streamIO.Iterator.EOI is returned.
	  * If several Items exist for this key, use map() to get them all.
	  *
	  * @param	 key   an index to this Container.
	  * @return	 the component at the specified index.
	  */
	public Object getAt(Object Key) {
		try {
			Association ret;
			if (IIStreamIn.EOI != (ret = (Association) mCnt.findFirst(Key))) {
				return ret.val; }
			return null;
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); } }

	/** Gets (returns) the component at the specified key / Index.
	  * If the Item does not exist, it is created from ItemClass automatically
	  * by it's newInstance() Method using the no Arguments Constructor.
	  * If ItemClass is null, it is not created and null is returned.
	  * <p>
	  * The index must be a value greater than or equal to <code>0</code>
	  * and less than the current size of the Container.
	  *
	  * @see findFirst()
	  * If the Index does not exist, streamIO.Iterator.EOI is returned.
	  * If several Items exist for this key, use map() to get them all.
	  *
	  * @param	 key   an index to this Container.
	  * @param   ItemClass if not null, and no Item exists, a new one is created using newInstance()
	  * @return	 the component at the specified index.
	  */
	public Object getAt(Object Key, Class ItemClass) throws InstantiationException {
		try {
			Association ass;
			if (IIStreamIn.EOI != (ass = (Association) mCnt.findFirst(Key))) {
				return ass.val; }
			if (ItemClass == null) {
				return null; }
			Object ret;
			addItem(Key, ret = ItemClass.newInstance());
//			mCnt.addItemAtCurrPos(Key, ret = ItemClass.newInstance()); //this Optimization has to be implemented by all Containers, but currently only by HashContainer
			//TODO: this Optimization has to be implemented by all Containers,
			//but currently it is supported only by HashContainer
			return ret;
		} catch (NoSuchMethodException  x) { throw new NoSuchMethodError     (x.toString()); //should not happen!
		} catch (IllegalAccessException x) { throw new InstantiationException(x.toString());
		}
	}

	/** Gets (returns) the component at the specified key / Index.
	  * If the Item does not exist, it is created from ItemClass automatically
	  * by it's newInstance() Method using the no Arguments Constructor.
	  * If ItemClass is null, it is not created and null is returned.
	  * <p>
	  * The index must be a value greater than or equal to <code>0</code>
	  * and less than the current size of the Container.
	  *
	  * @see findFirst()
	  * If the Index does not exist, streamIO.Iterator.EOI is returned.
	  * If several Items exist for this key, use map() to get them all.
	  *
	  * @param	 key   an index to this Container.
	  * @param   ItemClass if not null, and no Item exists, a new one is created using newInstance()
	  * @return	 the component at the specified index.
	  */
	public Object getAt(Object Key, streamIO.IInstantiAble Item) {
		Association ass;
		try {
			if (IIStreamIn.EOI != (ass = (Association) mCnt.findFirst(Key))) {
				return ass.val; }
		} catch (NoSuchMethodException x) {
			throw new NoSuchMethodError(x.toString()); } //should not happen!
		if (Item == null) {
			return null; }
		Object ret;
		addItem(Key, ret = Item.NewInstance());
//		mCnt.addItemAtCurrPos(Key, ret = ItemClass.newInstance());
		//TODO: this Optimization has to be implemented by all Containers,
		//but currently it is supported only by HashContainer
		return ret; }

	/** Sets (adds or replaces) the component at the specified index.
	  * All other components in this Container keep their <code>index</code>.
	  * <p>
	  * The index must be a value greater than or equal to <code>0</code>
	  * and less than the current size of the Container.
	  *
	  * @param	  Item	the component to set (add or replace).
	  * @param	  index   the index of the object to remove.
	  * @return	 the component replaced by 'Item'.
	  * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	  * @see		java.util.Array#size()
	  */
	public Object setAt(Object Key, Object Item, boolean replace) {
		if (replace) return replaceAt(Key, Item); //replaces the Item
		Object ret; //
		if ((ret = findFirst(Key, Item)) == IStreamIn.EOI) {
			this.addItem(Key, Item); }
		return ret; }

	////////////////////////////////////////////////////////////////////////////////
	// Interface Functions.IDynamicFunction
	////////////////////////////////////////////////////////////////////////////////

	/** set an Operation in the Function.
	  * Returns the previous Mapping, when there was one, otherwise null.
	  * This differs from a Bag where always null is returned and the Return Type should be void.
	  * The Convention is that adding null to the Function
	  * removes the Mapping.
	  * @see replaceAt
	  * @see addAt
	  * @see removeAt
	  * @see setAt
	  */
	public Object setAt(Object InPut, Object OutPut) {
		Object ret = replaceAt(InPut, OutPut);
		if (IStreamIn.EOI == ret) {
			mCnt.addItem(new Association(InPut, OutPut)); }
		return ret; }

	/** Replaces the Item identified by the given key with the given Item
	  * @see replaceAt
	  * @see addAt
	  * @see removeAt
	  * @see setAt
	  */
	public Object replaceAt(Object Key, Object Item) {
		Object ret; //
		try {
			Association ass = (Association) mCnt.findFirst(Key);
			if (ass != IStreamIn.EOI) {
				ret = ass.val; ass.val = Item; return ret; }
		} catch (NoSuchMethodException x) {
			throw new RuntimeException(x.toString()); }
//		mCnt.addItem(new Association(Key, Item)); //don't add it, when it didn't exit!
		return IStreamIn.EOI; }	//If the Item could be removed

	/**Flips the Item, i.e. when it is contained, remove it,
	  * otherwise add it. This corresponds to the XOR Operation.
	  * Returns the reset Item or null, when the Item is set.
	  * @see replaceAt
	  * @see addAt
	  * @see removeAt
	  * @see setAt
	  */
	public synchronized Object flipAt(Object Key, Object Item) {
		Object ret;
		if ((ret = this.removeAt(Key, Item)) == IStreamIn.EOI)
			this.addItem(Key, Item);
		return ret;}

	///////////////////////////////////////////////////////////////////////////
	// Interface IStreamOut
	///////////////////////////////////////////////////////////////////////////

	/** Adds this Item with the given key to the Collection/Relation.
	  * If the key is null, the Item is either added with the current Counter
	  * or (if it is an Association) with it's own key.	 */
	public Function addItems(final IIStreamIn Keys, final IIStreamIn Vals) {
		Object Key, Val;
		while ((IIStreamIn.EOI != (Key = Keys.nextItem())) || Keys.isValid()) {
			if((IIStreamIn.EOI != (Val = Vals.nextItem())) || Vals.isValid()) {
				mCnt.addItem(new Association(Key, Val));
			} else { return this; }
		}
		return this; }

	/** Adds this Item with the given key to the Collection/Relation.
	  * Depending on whether this is a Function or a Relation
	  * the Item is either union()ed add()ed.
	  *
	  * If the key is null, the Item is either added with the current Counter
	  * or (if it is an Association) with it's own key.	 */
	public Function addItem(final Association ass) {
		if (! mFunction) {
			mCnt.addItem(ass);
		}else{
			Object replaced;
			if (null != (replaced = mCnt.setItem(ass))) //if it did exist before...
				throw new InvalidParameterException("Duplicate Keys are not allowed in Functions: "
				+ ass + " existing: " + replaced); //give out a very detailed Exception to help finding the Error!
		} return this; }

	/** Adds this Item to the Collection/Relation.
	  * @return the StreamOut for adding further Items
	  *
	  * If the Item is an Association, it is added directly,
	  * otherwise it is either added with the current Counter
	  * or with itself as the key (Identity).	 */
	public IIStreamOut addItem(final Object Assoc) {
		if (Assoc instanceof Association) {
			addItem((Association) Assoc);
		}else{
			addItem(null, Assoc);
		} return this; }

	/** Adds this Item with the given key to the Collection/Relation.
	  * If the key is null, the Item is either added with the current Counter
	  * or (if it is an Association) with itself as the key (Identity).	 */
	public Function addItem(final Object Key, final Object Item) {
		if ((Key == null) && (Item instanceof Association))
			 mCnt.addItem(Item); //only if Item is an Association
		else mCnt.addItem(new Association(Key, Item));
		return this; }

	/**Adds this Item with the given key to the Collection/Relation.
	 * If the key is null, the Item is either added with the current Counter
	 * or (if it is an Association) with it's own key.	 */
	public Function addItem(final Object Key, final Object Item, final boolean directed) {
		this.addItem(Key , Item); if (directed) return this; //only if Item is an Association
		this.addItem(Item, Key );
		return this; }
	
	///////////////////////////////////////////////////////////////////////////

	/**Deletes the component at the specified index. Each component in
	 * this Array with an index greater or equal to the specified
	 * <code>index</code> is shifted downward to have an index one
	 * smaller than the value it had previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * @param	  index   the index of the object to remove.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public Function subAt(Object Key, Object Value) { removeAt(Key, Value); return this; }

	/**Deletes the component at the specified index. Each component in
	 * this Array with an index greater or equal to the specified
	 * <code>index</code> is shifted downward to have an index one
	 * smaller than the value it had previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * @param	  index   the index of the object to remove.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public Object removeAt(Object Key, Object Value) {
		try {
//			ITester tst = new TestEquivalence(new Pair(Key, Value), AssociationEquivalence.ExactEquivalence);
//			ITester tst = new TestObject(new Pair(Key, Value)); //Pair is faster to create
//			ITester tst = new TestObject(ass); //but Association can do faster tests, because it knows itself!
			FindValueTester tst = new FindValueTester(); tst.inverse = false; tst.Value = Value; //
			Object ret = mCnt.firstOfEachThatEqualsThat(Key, tst);
			if (ret != null)
				ret = mCnt.removeCurr();
			return ret;
		} catch (ModificationException x) { throw new ReadOnlyException(x);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); } }

	/**Returns the first occurence of Item when it is in the Collection,
	 * otherwise 'null' is returned.
	 * This is more useful than returning a boolean Value,
	 * because you can still check for 'null',
	 * but you can also use findFirst directly in an Expression,
	 * when you are sure that it is in the Collection.
	 * (you cannot have any Operation working on 'null'!)
	 *
	 * Since 'equal()' is used to compare the Items,
	 * you may even receive a different, but equivalent Item.
	*/
	public Object findFirst(Object Key, Object Value) {
//		ITester tst = new TestEquivalence(new Pair(Key, Value), AssociationEquivalence.ExactEquivalence);
//		ITester tst = new TestObject(new Pair(Key, Value)); //Pair is faster to create
//		ITester tst = new TestObject(ass); //but Association can do faster tests, because it knows itself!
		FindValueTester tst = new FindValueTester(); tst.inverse = false; tst.Value = Value; //
		try { return mCnt.firstOfEachThatEqualsThat(Key, tst);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); } }

	/**Returns the next occurence of Item when it is in the Collection,
	 * otherwise 'null' is returned.
	 * This is more useful than returning a boolean Value,
	 * because you can still check for 'null',
	 * but you can also use findFirst directly in an Expression,
	 * when you are sure that it is in the Collection.
	 * (you cannot have any Operation working on 'null'!)
	 *
	 * Since 'equal()' is used to compare the Items,
	 * you may even receive a different, but equivalent Item.
	*/
	public Object findNext(Object Key, Object Value) {
//		ITester tst = new TestEquivalence(new Pair(Key, Value), AssociationEquivalence.ExactEquivalence);
//		ITester tst = new TestObject(new Pair(Key, Value)); //Pair is faster to create
//		ITester tst = new TestObject(ass); //but Association can do faster tests, because it knows itself!
		FindValueTester tst = new FindValueTester(); tst.inverse = false; tst.Value = Value; //
		return mCnt.nextOfEachThatEqualsThat(Key, tst); }

	/**Tests if the specified object is a component in this Array.
	 *
	 * @param   elem   an object.
	 * @return  <code>true</code> if the specified object is a component in
	 *		  this Array; <code>false</code> otherwise.
	 */
	final public boolean contains(Object Key, Object Value) {
//		ITester tst = new TestEquivalence(new Pair(Key, Value), AssociationEquivalence.ExactEquivalence);
//		ITester tst = new TestObject(new Pair(Key, Value)); //Pair is faster to create (no Parent Classes)
//		ITester tst = new TestObject(ass); //but Association can do faster tests, because it knows itself!
		FindValueTester tst = new FindValueTester(); tst.inverse = false; tst.Value = Value; //
		try { return IStreamIn.EOI != mCnt.firstOfEachThatEqualsThat(Key, tst);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); } }

	//	Equivalence Function Operations with Parameter 'Association'

	/** Sets (adds or replaces) the component at the specified index.
	  * All other components in this Container keep their <code>index</code>.
	  * <p>
	  * The index must be a value greater than or equal to <code>0</code>
	  * and less than the current size of the Container.
	  *
	  * @param	  Item	the component to set (add or replace).
	  * @param	  index   the index of the object to remove.
	  * @return	 the component replaced by 'Item'.
	  * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	  * @see		java.util.Array#size()
	  */
	public Object setAt(Association ass, boolean replace) {
		if (replace) return replaceAt(ass);
		Object ret; //
		if ((ret = findFirst(ass)) == IStreamIn.EOI) {
			mCnt.addItem(ass); }
		return ret; }

	/** Replaces the Item by it's default counter key	 */
	public Object replaceAt(Association ass) {
		return replaceAt(ass.key, ass.val); }

	/** Flips the Item, i.e. when it is contained, remove it,
	  * otherwise add it. This corresponds to the XOR Operation.
	  * Returns the reset Item or null, when the Item is set.
	  */
	public Object flipAt(Association ass) {
		Object ret;
		if ((ret = this.removeAt(ass)) == IStreamIn.EOI)
			mCnt.addAt(ass);
		return ret;}

	/**Deletes the component at the specified index. Each component in
	 * this Array with an index greater or equal to the specified
	 * <code>index</code> is shifted downward to have an index one
	 * smaller than the value it had previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * @param	  index   the index of the object to remove.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public Function subAt(Association ass) { removeAt(ass); return this; }

	/**Deletes the component at the specified index. Each component in
	 * this Array with an index greater or equal to the specified
	 * <code>index</code> is shifted downward to have an index one
	 * smaller than the value it had previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * @param	  index   the index of the object to remove.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public Object removeAt(Association ass) {
		try {
//			ITester tst = new TestEquivalence(new Pair(Key, Value), AssociationEquivalence.ExactEquivalence);
//			ITester tst = new TestObject(new Pair(Key, Value)); //Pair is faster to create
//			ITester tst = new TestObject(ass); //but Association can do faster tests, because it knows itself!
			FindValueTester tst = new FindValueTester(); tst.inverse = false; tst.Value = ass.val; //
			Object ret = mCnt.firstOfEachThatEqualsThat(ass.key, tst);
			if (ret != null)
				ret = mCnt.removeCurr();
			return ret;
		} catch (ModificationException x) { throw new ReadOnlyException(x);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); } }

	/** Returns the first occurence of Item when it is in the Collection,
	  * otherwise 'null' is returned.
	  * This is more useful than returning a boolean Value,
	  * because you can still check for 'null',
	  * but you can also use findFirst directly in an Expression,
	  * when you are sure that it is in the Collection.
	  * (you cannot have any Operation working on 'null'!)
	  *
	  * Since 'equal()' is used to compare the Items,
	  * you may even receive a different, but equivalent Item.
	  */
	public Object findFirst(Association ass) {
//		ITester tst = new TestEquivalence(new Pair(Key, Value), AssociationEquivalence.ExactEquivalence);
//		ITester tst = new TestObject(new Pair(Key, Value)); //Pair is faster to create
//		ITester tst = new TestObject(ass); //but Association can do faster tests, because it knows itself!
		FindValueTester tst = new FindValueTester(); tst.inverse = false; tst.Value = ass.val; //
		try { return mCnt.firstOfEachThatEqualsThat(ass.key, tst);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); } }

	/** Returns the first occurence of Item when it is in the Collection,
	  * otherwise 'null' is returned.
	  * This is more useful than returning a boolean Value,
	  * because you can still check for 'null',
	  * but you can also use findFirst directly in an Expression,
	  * when you are sure that it is in the Collection.
	  * (you cannot have any Operation working on 'null'!)
	  *
	  * Since 'equal()' is used to compare the Items,
	  * you may even receive a different, but equivalent Item.
	  */
	public Object findNext(Association ass) {
//		ITester tst = new TestEquivalence(new Pair(Key, Value), AssociationEquivalence.ExactEquivalence);
//		ITester tst = new TestObject(new Pair(Key, Value)); //Pair is faster to create
//		ITester tst = new TestObject(ass); //but Association can do faster tests, because it knows itself!
		FindValueTester tst = new FindValueTester(); tst.inverse = false; tst.Value = ass.val; //
		return mCnt.nextOfEachThatEqualsThat(ass.key, tst); }

	/** Tests if the specified object is a component in this Array.
	  *
	  * @param   elem   an object.
	  * @return  <code>true</code> if the specified object is a component in
	  *		  this Array; <code>false</code> otherwise.
	  */
	final public boolean contains(Association ass) {
//		ITester tst = new TestEquivalence(new Pair(Key, Value), AssociationEquivalence.ExactEquivalence);
//		ITester tst = new TestObject(new Pair(Key, Value)); //Pair is faster to create (no Parent Classes)
//		ITester tst = new TestObject(ass); //but Association can do faster tests, because it knows itself!
		FindValueTester tst = new FindValueTester(); tst.inverse = false; tst.Value = ass.val; //
		try { return IStreamIn.EOI != mCnt.firstOfEachThatEqualsThat(ass.key, tst);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); } }


	/**Adds all Associations from the streamIO to the Relation
	 * Defaults the Separator Characters
	 * Defaults the LoadFactor and initial Capacity.
	 *
	 * Comments and empty Lines may be added anywhere in the streamIO
	 * White Spaces do count for both Keys AND Values!!!
	 */
	public Function addItems(InputStream IS) throws IOException {
		String Value;
		String Key = null;
		boolean tmp = false;
		boolean comment = false;
		Scanner scn = new Scanner(IS, "=#\n\r");
		do switch (scn.currToken) {
//		  case XMLScanner.XML_TAG_ATTRIBUTE:
			case XMLScanner.XML_TAG_START: if (! comment) Key = scn.getResult(); break; //= Sign
			case XMLScanner.XML_TAG_STOP : tmp = true; //ignore the rest of the Line //# Sign
				//break; //also allow Comments at the End of each Line!
			default: if (! comment) {
						if (Key != null) {
							Value = scn.getResult(); addItem(Key, Value); }
					 } //CR / LF Characters and EOF
					 scn.clearString(); Key = null;
					 comment = tmp; tmp = false;
		} while (scn.nextToken() != IIStreamIn_Int.EOF);
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IInvertAble: abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Builds and caches the inverse Function by swapping every Association's Key and Value.
	  * @return the Inverse, cached for here for all inheriting Classes
	  * Creates the inverse Relation (transposed Graph) !x
	  * An undirected Graph is it's own Inverse.
	  * A complete undirected Graph defines an Equivalence Relation.
	  *
	  * It cannot be created in Place, because the Position in the Table changes.
	  */
	public IMonoid rev() {
//	public IInvertAble getInverse() { //
		if (mInverse != null) {
			return (IMonoid) mInverse; }
		Function ret;
		IStreamIn iter = mCnt.Enumerator();
		mInverse = ret = (Function) newInstance();
		iter.reSet();
		for(Association ass;((ass = (Association) iter.nextItem()) != IIStreamIn.EOI) || iter.isValid();) { //
			//ret.addAt(ass.invert()); //slightly slower
			ret.addItem(ass.val, ass.key); //reuses the new Inverse
		} 
		return ret; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface Monoid: abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Mapping / Left-Concat with !arg in Place: !this=�arg */
	public IMonoid pamAt(Object arg) {
		if (mInverse == null) getInverse();
		return (IMonoid) ((Function) mInverse).mapAt(arg); }

	/** Wraps the given Iterator, using the faster {@link FilterByFunction} when this is a
	 * true Function, otherwise a generic {@link MapIterator}.
	 * @return an Iterator that maps the Elements of the given Iterator using this Relation. 	 */
	public IStreamIn map(IIStreamIn iter) {
		if (mFunction)
			return new FilterByFunction(iter, this); //simpler and faster
			return new MapIterator((HashContainer) mCnt, iter); }

	/** Maps every Element of arg through this Function into a freshly created Container.
	 * @return a new Container with the Elements of 'arg' mapped by this Relation.	 */
	public Container map(Container arg) {
		Container ret = (Container) arg.newInstance();
		ret.addItems(map(arg.Iterator()));
		return ret; }

	/** Maps every Element of arg through this Function into a freshly created Function.
	 * @return a new Relation with the Elements of 'arg' mapped by this Relation.	 */
	public Function map(Function arg) {
		Function ret = (Function) arg.newInstance();
		ret.addItems(map(arg.Iterator()));
		return ret; }

	/** Mapping/Left -Concat:  this�arg
	  * @return 'arg' mapped by this, instead of 'this'
	  *
	  * The Argument is assumed to be a simple Object,
	  * not a Container or Relation.
	  * Depending on whether this is a Function or a Relation
	  * it returns an Object (null when not found)
	  * or an Input streamIO returning all Results.
	  * The streamIO must be evaluated immediately,
	  * because it is destroyed in the next Operation.
	  */
	public Object Map (Object Key) {
		if (mFunction) {
			Object obj;
			try { if (null == (obj = mCnt.findFirst(Key)))
				return null;
			} catch (NoSuchMethodException x) { throw new NoSuchMethodError (x.toString()); }
			return ((Association) obj).val; }
		IStreamIn ret = mCnt.Enumerator();
		ret.setFilter(Key);
		return ret; }

	/** Mapping/Left -Concat:  this�arg
	  * @return 'arg' mapped by this, instead of 'this'
	  * arg is assumed to be a SemiMonoid itself
	  */
	public ISemiMonoid map  (Object arg) {
		if (mFunction) { // Mapping can be performed in Place
			return mapAt(((ISemiMonoid) arg).copy()); }
		Function arg_ = (Function) arg;
		return (ISemiMonoid) this.map(arg_); }

	/** Mapping / Left-Concat in Place:  this=�arg
	  * implemented by: (arg.key, MapAt(arg.Value))
	  * @return 'arg' mapped in Place by this, instead of 'this'
	  * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	  * which is more efficient for single Values than B.map(A.map(a))
	  * or B.map(A).map(a) or A.cat(B).map(a)
	  *
	  * arg is assumed to be a SemiMonoid (Relation) itself.
	  */
	 public Function mapAt(Function arg) {
		if (! mFunction) { // Mapping only be performed in Place, if this is a Function!
			arg.copyAt(map(arg)); //short Operations first! (then you can forget about them!)
		}else{ //Function! Mapping in Place is possible
			IIStreamIn Iter = arg.Iterator();
			for(Association ass;(IIStreamIn.EOI != (ass = (Association) Iter.nextItem())) || Iter.isValid();) {
				if (null == (ass.val = getAt(ass.val))) {
				} //Iter.removeCurr(); } //TODO: either leave them with null or remove them!
			}
		} return arg; }

	/** Mapping / Left-Concat in Place:  this=�arg
	  * @return 'arg' mapped in Place by this, instead of 'this'
	  * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	  * which is more efficient for single Values than B.map(A.map(a))
	  * or B.map(A).map(a) or A.cat(B).map(a)
	  *
	  * arg is assumed to be a SemiMonoid (Association) itself.
	  */
	 public Association mapAt(Association arg) {
		if (mFunction) { // Mapping can not be performed in Place
			arg.val = getAt(arg.val);
		}else{
			arg.val = getAt(arg.val); //TODO: this is a Stream with fragile State now, maybe load it into a Container!
		} return arg; }

	/** Mapping / Left-Concat in Place:  this=�arg
	  * @return 'arg' mapped in Place by this, instead of 'this'
	  * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	  * which is more efficient for single Values than B.map(A.map(a))
	  * or B.map(A).map(a) or A.cat(B).map(a)
	  *
	  * arg is assumed to be a SemiMonoid (Relation) itself.
	  */
	 public ISemiMonoid mapAt(Object arg) {
		if (arg instanceof Function   ) return mapAt((Function   ) arg);
		if (arg instanceof Association) return mapAt((Association) arg);
		return self.mapAt(arg); }

	/** Delegates to the backing Container's own String Representation.
	 * @return a String Representation of this Object 	*/
	public String toString() {
		return mCnt.toString(); }

	/** Creates a new, empty Function.
	 * @return a new, uninitalized Instance of it's class.
	  * This can in VB also be achieved by 'CreateObjectFromInstance',
	  * which may be slower.
	  * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new Function(); }

	/** Creates a shallow copy of this HashContainer.
	  * The keys and values themselves are not cloned.
	  * This is a relatively expensive operation,
	  * but still cheaper than the Default Implementation,
	  * because the HashCodes and Capacities don't need to be re-calculated.
	  *
	  * Unfortunately the Associations may be reused and thus must be copied!
	  * The Depth of the Copy cannot be determined a priori!
	  *
	  * @return  a clone of the HashContainer.	 */
	public ICopy Copy() {
		return new Function((Container) mCnt.Copy()); }

	/**Returns the Iterator of the Values	 */
//	public Enumerator ValueEnumerator() { return new ValueEnumerator(this); }

	/**Returns the Iterator of the Keys	 */
//	public Enumerator KeyEnumerator() { return new KeyEnumerator(this); }

	//////////////////////////////////
	//	Test Methods for Relations	//
	//////////////////////////////////

	/**Returns true, when this Relation is left total,
	 * i.e. there is an a=x for each 'a' from the Set of Keys.	 */
	public boolean isLeftTotal(IIStreamIn Keys) {
		for(Object Item;((Item = Keys.nextItem()) != IIStreamIn.EOI) || Keys.isValid();) //
			if (this.getAt(Item) == null) return false;
		return true; }

	/**Returns true, when this Relation is right total,
	 * i.e. there is an x=a for each 'a' from the Set of Values.	 */
	public boolean isRightTotal(IIStreamIn Values) {
		FindValueTester findValue = new FindValueTester(); findValue.inverse = false;
		try {
			while(((findValue.Value = Values.nextItem()) != IIStreamIn.EOI) || Values.isValid()) //
				if (mCnt.firstThat(findValue) == null) return false;
		} catch (NoSuchMethodException x) {	throw new NoSuchMethodError(x.toString()); }
		return true; }

	/**Returns true, when this Relation is bitotal,
	 * i.e. there is an a=x for each 'a' from the Set of Keys
	 * and			 an y=b for each 'b' from the Set of Values.	 */
	public boolean isBiTotal(IIStreamIn Keys, IIStreamIn Values) {
		return isLeftTotal (Keys) && isRightTotal (Values);}

	/**Returns true, when this Relation is left unique,
	 * i.e. there is only one a=x for each 'a' from the Set of Keys.
	 * left  unique Relations are Functions and should be stored as 'Function'.
	 * All(l,r): (Exists(x,y)| (x==l) && (y!=r) <=> !isLeftUnique)  <=>
	 * All(l,r): (All   (x,y)| (x!=l) || (y==r) <=>  isLeftUnique)  <=>
	 * All(l,r): (All(x==l,y)|		   (y==r) <=>  isLeftUnique)
	 * Computers can check 'All' Conditions on finite Sets.
	 * Now use the fact that All(x==l,y) is fast to find through hashing.
	 */
	public boolean isLeftUnique() { //Set Keys)	//Keys are not necessary,
		//since this Test is stopped, as soon as one double Key occurs
		//alternatively fill a Relation until a Duplicate is found!
		Object Item;
		IIStreamIn iter = this.Iterator();	//This Iterator returns Associations
//		ByRefLong more = new ByRefLong();
		FindValueTester findValue = new FindValueTester(); findValue.inverse = true;
		try {
			while ((Item = iter.nextItem()) != IIStreamIn.EOI) { // more)) != null) {
				findValue.Value = ((Association) Item).val;
	//			if ((findValue.Value = findFirst(Item)) == null) continue; //return false;	//not left total!
				if (mCnt.firstOfEachThatEqualsThat(Item, findValue) != null)
					return false; //Search a Second Entry not equal to the second one.
			}
		} catch (NoSuchMethodException x) {	throw new NoSuchMethodError(x.toString()); }
		return true; }

	/**Returns true, when this Relation is right unique,
	 * i.e. there is only one y=b for each 'b' from the Set of Values.
	 * right unique Relations have Functions as their Reverse.
	 * All(l,r): (Exists(x,y)| (x!=l) && (y==r) <=> !isLeftUnique)  <=>
	 * All(l,r): (All   (x,y)| (x==l) || (y!=r) <=>  isLeftUnique)  <=>
	 * All(l,r): (All(x!=l,y)|		   (y!=r) <=>  isLeftUnique)
	 * alternatively a Relation can be filled with the Inverses
	 * until a Duplicate occurs.
	 */
	public boolean isRightUnique() { //Set Values)	//Values are not necessary,
		if (mInverse == null)
			getInverse();
		return ((Function) mInverse).isLeftUnique(); }
/*		Object tmp; //since this Test is stopped, as soon as one double Value occurs
		Association Item;
		Function testRel = new Function();
		IStreamIn iter = this.Iterator();	//This Iterator returns Associations
//		ByRefLong more = new ByRefLong();
		while ((tmp = iter.nextItem()) != IStreamIn.EOI) { // more)) != null) {
			Item = (Association) tmp;
			if (IStreamIn.EOI != (tmp = testRel.getAt(Item.Value)))
				if (Item.Key != tmp)
					if (! Item.Key.equals(tmp))
						return false;
			testRel.addItem(Item.Value, Item.Key);
		}	//Search a Second Entry not equal to the second one.
		return true; }

	/**Returns true, when this Relation is biunique,
	 * i.e. there is exactly one a=x for each 'a' from the Set of Keys
	 * and			 exactly one y=b for each 'b' from the Set of Values.
	 * biUnique Relations are Functions that also have Functions as their Reverse.*/
	public boolean isBiUnique() { //Set Keys, Set Values)
		return  isLeftUnique() && //Keys) &&
				isRightUnique();} //Values);}


	//////////////////////////////////////////////////
	//	Tests for Relations of Sets onto their own:	//
	//////////////////////////////////////////////////

	/**Returns true, when this Relation is reflexive,
	 * i.e. there is  a=a for each 'a'.
	 * only meaningful for Mapping A->A */
	public boolean isReflexive(IIStreamIn Items) {
		FindValueTester findValue = new FindValueTester(); findValue.inverse = false;
		try {
			while (((findValue.Value = Items.nextItem()) != IIStreamIn.EOI) || Items.isValid())//
				if (mCnt.firstOfEachThatEqualsThat(findValue.Value, findValue) == null) return false;
		} catch (NoSuchMethodException x) {	throw new NoSuchMethodError(x.toString()); }
		return true; }

	/** Returns true, when this Relation is connex (linear),
	  * i.e. a=b || b=a for all 'a' and 'b' from Items
	  * That means, each Item has a Relation to any other Item in some way.
	  * only meaningful for internal Mappings: Items->Items */
	public boolean isConnex(Container Items) throws OperationNotSupported {
		FindValueTester findValue = new FindValueTester(); findValue.inverse = false;
		Object Key;
		IIStreamIn iter1 = Items.Iterator();
		 IStreamIn iter2 = Items.Enumerator();
		try {
		while ((		  Key   = iter1.nextItem()) != IIStreamIn.EOI) { iter2.reSet();
		while ((findValue.Value = iter2.nextItem()) != IIStreamIn.EOI) {
			if (Key.hashCode() <  findValue.Value.hashCode()) continue; //only have to test half of the Items
			if (mCnt.firstOfEachThatEqualsThat(Key, findValue) == null) {
				if (findValue.Value.equals(Key)) return false; //(x,x) does not exist!
				Object tmp = findValue.Value; findValue.Value = Key; Key = tmp; //swap Key and Value
				if (mCnt.firstOfEachThatEqualsThat(Key, findValue) == null) return false;
				Key = findValue.Value; //undo the swap above for the next Test!
			} } }
		} catch (NoSuchMethodException x) {	throw new NoSuchMethodError(x.toString()); }
		return true; }

	/**Test for (a-) Symmetry of this Relation, i.e. a=b <=> (!)b=a
	 * only meaningful for Mapping A->A
	 * Most Relations are neither symmetric nor asymmetric.
	 * See also 'identitive' (antisymmetric) Relations */
	public boolean testSymmetry(boolean anti) {	//Just test whether the Inverse Element exists!
		Association Item;
		IIStreamIn iter = Iterator();
		FindValueTester findValue = new FindValueTester(); findValue.inverse = false;
		try {
		for(Object tmp; ((tmp = iter.nextItem()) != IIStreamIn.EOI) || iter.isValid();) {
//			if (Key.hashCode() <  findValue.Value.hashCode()) continue; //only have to test half of the Items
			Item = (Association) tmp;
			findValue.Value	= Item.key; //don't check for the first, but for any!
			if ((mCnt.firstOfEachThatEqualsThat(Item.val, findValue) == null) ^ anti)
				return false;
		}
		} catch (NoSuchMethodException x) {	throw new NoSuchMethodError(x.toString()); }
		return true; }

	/**Returns true, when this Relation is symmetric, i.e. a=b <=> b=a
	 * only meaningful for Mapping A->A
	 * See also 'identitive' Relations */
	public boolean isSymmetric() { return testSymmetry(false); }

	/**Returns true, when this Relation is asymmetric, i.e. a=b <=> ! b=a
	 * only meaningful for Mapping A->A
	 * aSymmetry is more than no Symmetry! */
	public boolean isASymmetric() { return testSymmetry(true); }

	/**Returns true, when this Relation is identitive (anti-Symmetric),
	 * i.e. a=b && b=a => b==a
	 * i.e. if the inverse Mapping exists, both items must be identical!
	 * only meaningful for Mapping A->A */
	public boolean isIdentitive() {
		Object tmp;
		Association Item;
		IIStreamIn iter = Iterator();
		FindValueTester findValue = new FindValueTester(); findValue.inverse = false;
		try {
		while ((tmp = iter.nextItem()) != IIStreamIn.EOI) { // more)) != null)
			Item = (Association) tmp;
			if (Item.key.equals	(Item.val)) {
				continue; } //only have to test Associations that are not identical.
			findValue.Value	= Item.key; 				//find the inverse Association
			if (mCnt.firstOfEachThatEqualsThat(Item.val, findValue) != null) {
				return false; }
		}
		} catch (NoSuchMethodException x) {	throw new NoSuchMethodError(x.toString()); }
		return true; }

	/**Returns the In-Degree of the Node j,
	 * i.e. the Number of Edges going into of this Node.
	 * For all In Degrees, it is better to create the Inverse (Transpose)
	 * of this Graph.
	 *
	 * In an undirected Graph, the In- and Out-Degrees are the same,
	 * so it is sufficient to determine the Out-Degrees simply called "Degree".
	 *
	 * Graphs in which all Degrees are the same, are called 'regular'
	 */
	public int InDegree(Object Node) {
		int ret = 0;
		IIStreamIn iter = this.Iterator();
		for(Association Item;((Item = (Association) iter.nextItem()) != IIStreamIn.EOI) || iter.isValid();) {
			if ((Node == Item.val) || Node.equals(Item.val)) { //is a bit dangerous to assume not null!
				++ret; }  }
		return ret; }

	/** Determines the Degree of each Node in an undirected Graph,
	  * i.e. the Number of Edges to this Node.
	  * The Degree is counted and stored in the Nodes themselves.
	  * Therefore the Nodes must implement the 'integer' Interface.
	  * It is not effective to determine the InDegree of a single Node,
	  * since you have to go through all Edges anyway.
	  *
	  * This is necessary to test whether an 'Euler Circle' can be defined,
	  * which visits all Nodes once and returns to the Starting Point.
	  * This is possible exactly if all Nodes have an even Degree
	  * (with undirected Graphs they must be of a Degree divisible by four)
	  * For directed Graphs there are much more rigid restrictions!
	  *
	  * It is used to solve the 'Koenigsberger' Problem,
	  * where the Existence of a Path, that uses each Edge exactly once,
	  * and returns to the Start is demonstrated.
	  *
	  * This is related to the NP-complete 'Hamilton' Problem,
	  * where a Path is searched that visits each Node only once.
	  * It is not related to the Traveling Salesman Problem (TSP)	 */
	public void setDegrees() {
		Association Item;
		IStreamIn iter = mCnt.Enumerator();
		while (((Item = (Association) iter.nextItem()) != IIStreamIn.EOI) || iter.isValid()) { //
			((IInteger) Item.key).ZeroAt(); //a bit dangerous to assume not null here!
			((IInteger) Item.val).ZeroAt(); }
		iter.reSet();
		while (((Item = (Association) iter.nextItem()) != IIStreamIn.EOI) || iter.isValid()) {
			((IInteger) Item.key).inc(); //a bit dangerous to assume not null here!
			((IInteger) Item.val).inc();  }
		}

	/**Returns true, when this Relation is transitive, i.e. a=b && b=c => a=c
	 * a transitive relation is a projector, i.e. A�A = A
	 * only meaningful for Mapping A->A
	 * This is true for Relations that are the transitive hull of a spanning Relation */
	public boolean isTransitive() {	//Map the whole Relation to itself
		//and search for each result in the original Relation.
		Function AA = (Function) ((IMonoid) this).map(this);
		Association Item;
		IIStreamIn iter = AA.Iterator();
//		ByRefLong more = new ByRefLong();
		FindValueTester findItem = new FindValueTester();
		try {
		for(Object tmp;((tmp = iter.nextItem()) != IIStreamIn.EOI) || iter.isValid();) { //
			Item = (Association) tmp;
			if (Item.val != null)	{	//find an identical Association, if the Mapping is not empty
				findItem.Value	= Item.val;
				if (mCnt.firstOfEachThatEqualsThat(Item.key, findItem) == null) {
					return false; } }
		}
		} catch (NoSuchMethodException x) {	throw new NoSuchMethodError(x.toString()); }
		return true; }

	//////////////////////////////////
	//	higher Types of Relations	//
	//////////////////////////////////

	/**Returns true, when this Relation is transitive, reflexive and symmetric
	 * A complete undirected Graph defines an Equivalence Relation.
	 */
	public boolean isEquivalence(IIStreamIn Items)	{
		return isReflexive(Items) && isSymmetric() && isTransitive(); }

	/**Returns true, when this Relation is transitive and asymmetric
	 * An Order Relation defines a Graph with no Cycles.
	 * The Graph of a strict Order Relation is called 'semiconnected'.
	 * The transitive Hull of a Graph with no Cycles defines an Order Relation.
	 * The spanning Tree of a Graph with no Cycles defines a strict Order Relation.
	 */
	public boolean isStrictOrder() { return isASymmetric() && isTransitive(); }

	/**Returns true, when this Relation is transitive, reflexive and identitive.
	 * It doesn't have to be connex! */
	public boolean isOrder(IIStreamIn Items) {
		return isReflexive(Items) && isIdentitive(); }	// && isTransitive();}

	/**Returns the transitive Hull of this Element,
	 * i.e. all Elements that can be reached from this one.
	 *
	 * An Alternative is to create the hull first
	 * and then apply it to the Start Element.
	 */
//	public IStreamIn hull(Object Start) { return generateHull(map(Start)); }

	/** Repeatedly maps this Function by itself, folding new Associations back in until no
	  * more are produced.
	  * @return the transitive Hull of this Relation
	  * i.e. all Elements that can be reached from this one
	  * by adding the Results of all Powers of A�A to A:
	  *
	  * The Hull will be a transitive Relation, i.e. a=b && b=c => a=c
	  * a transitive relation is a projector, i.e. A�A = A
	  * only meaningful for Mapping A->A
	  */
	public Function hullAt() {
//		int prevCount;
//		int currCount = 0;
		ISemiMonoid pow = this;
		do {
//			mapAt(pow);
			pow = map(pow); //map the Relation by itself
//			System.out.println(pow);
		} while (mCnt.union(pow, streamIO.copy.monoid.AssociationEquivalence.ExactEquivalence)); //replace existing Items, but mCnt determines Equivalence based on the Keys only!
//			System.out.println(this);
//			prevCount = currCount;
//		} while (prevCount != (currCount = mCnt.getInt())); //pow.getInt() > 0);
		return this; }

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Function.class.getName());
		testForest();
		testEquivalence();
		Function rel = new Function(new FileInputStream("/Personal/Code/Java/Stream/Object/Enumerator/Container/Relation1.rel")); //Relation1.rel"));
		System.out.println("Relation: " + rel);
		Function Inv = (Function) rel.rev();
		System.out.println("Inverse : " + Inv);
		checkRelation(rel);
		rel.hullAt();
		checkRelation(rel);
		checkRelation(Inv); Inv.hullAt();
		checkRelation(Inv);
	}

	/**Tests all Properties of a single Relation	 */
	public static void checkRelation(Relation rel) {
		System.out.println("Rel = " + rel);
		System.out.println("isASymmetric ? " + rel.isASymmetric ());
		System.out.println("isBiUnique   ? " + rel.isBiUnique   ());
		System.out.println("isIdentitive ? " + rel.isIdentitive ());
		System.out.println("isLeftUnique ? " + rel.isLeftUnique ());
		System.out.println("isRightUnique? " + rel.isRightUnique());
		System.out.println("isStrictOrder? " + rel.isStrictOrder());
		System.out.println("isSymmetric  ? " + rel.isSymmetric  ());
		System.out.println("isTransitive ? " + rel.isTransitive ());
		rel = rel.map(rel);
		System.out.println("Rel�Rel = " + rel);
	}

	/**Tests the Equivalence Methods of this Class
	 * Methods and ITester are copied directly from Equivalence.
	 */
	public static void testEquivalence() {
		System.out.println("Testing Relation-Equivalence:"); //Expected Results when adding in this Sequence!
		String[][] Edges ={{"A","G"}, //false
						   {"A","B"}, //false
						   {"A","C"}, //false
						   {"L","M"}, //false
						   {"J","M"}, //false
						   {"J","L"}, //true (J-M-L)
						   {"J","K"}, //false
						   {"E","D"}, //false
						   {"F","D"}, //false
						   {"H","I"}, //false
						   {"F","E"}, //true (F-D-E)
						   {"A","F"}, //false
						   {"G","E"}};//true (G-A-F-E)
		String[][] Edges2={{"G","C"}, //true (G-A-C)	//neu f�r zweifachen Zusammenhang
						   {"G","H"}, //false
						   {"J","G"}, //false		//these last two Elements
						   {"J","L"}};//true (J-L)	//differ from the Example in MatrixGraph!!!
		int i;
		Function EQ;
		System.out.println("\nTesting 'equivalent':");
		EQ = new Function();
		i = -1; while (++i < Edges .length) {
			System.out.print(EQ.isEquivalent(	Edges [i][1],
												Edges [i][0], true) + ", ");
			System.out.println(EQ); }
		i = -1; while (++i < Edges2.length) {
			System.out.print(EQ.isEquivalent(	Edges2[i][1],
												Edges2[i][0], true) + ", ");
			System.out.println(EQ); }
}/*		System.out.println("\nTesting 'isEquivalentAvg':");
		EQ = new Function();
		i = -1; while (++i < Edges .length) System.out.print(EQ.isEquivalentAVG(Edges [i][1],
																				Edges [i][0]));
		i = -1; while (++i < Edges2.length) System.out.print(EQ.isEquivalentAVG(Edges2[i][1],
																				Edges2[i][0]));
		System.out.println("\nTesting 'isEquivalentMax':");
		EQ = new Function();
		i = -1; while (++i < Edges .length) System.out.print(EQ.isEquivalentMAX(Edges [i][1],
																				Edges [i][0]));
		i = -1; while (++i < Edges2.length) System.out.print(EQ.isEquivalentMAX(Edges2[i][1],
																				Edges2[i][0]));
		System.out.println(); } */

	/**Tests all Properties of a single Relation	 */
	public static void checkRelation(Function rel) {
		System.out.println("Rel = " + rel);
		System.out.println("isASymmetric ? " + rel.isASymmetric ());
		System.out.println("isBiUnique   ? " + rel.isBiUnique   ());
		System.out.println("isIdentitive ? " + rel.isIdentitive ());
		System.out.println("isLeftUnique ? " + rel.isLeftUnique ());
		System.out.println("isRightUnique? " + rel.isRightUnique());
		System.out.println("isStrictOrder? " + rel.isStrictOrder());
		System.out.println("isSymmetric  ? " + rel.isSymmetric  ());
		System.out.println("isTransitive ? " + rel.isTransitive ());
		rel = rel.map(rel);
		System.out.println("Rel�Rel = " + rel);
	}

	/** Builds a SparseGraph Adjacency List from this Function's Edges, filling in the
	  * given Node/Position Mapping.
	  * @return an Adjacency List Representation of all Edges in this Relation / Graph
	  * This is the Preparation to solving Problems in the SparseMatrix Representation,
	  * which is not necessarily faster, since (re-)transforming (in 'sortNodes()')
	  * is an O(E+N) Op just like most Graph Algorithms.
	  * @param Nodes stores the Transformation between the Nodes and the SparseMatrix
	  * @param Nodes is the Mapping of Graph-Nodes to their Positions as returned in
	  * @see getEnumeration()
	  */
	public SparseGraph getAdjList(final Pair Nodes) {
		mCnt.reSet();
		final KeyValuePair retNodes = GET_ENUMERATION(new FilterInPair(mCnt.Iterator(), true)); //returns the Mapping between Nodes and int Numbers
		Nodes.Key = retNodes.key; //copy it, because Nodes is not handed over ByRef!
		Nodes.val = retNodes.val;
		Function NodePos = (Function) retNodes.val;
		//generate an Adjacency List Repr. with the given Node List
		int numNodes = NodePos.mCnt.getInt(); //Size of the List == Number of Keys / Nodes
		SparseGraph adjList = new SparseGraph(numNodes); //an SparseMatrix Representation for this Relation
		IIStreamIn iter = Iterator();
		float Weight = 1;
		for (Association currEdge;((currEdge = (Association) iter.nextItem()) != IIStreamIn.EOI);) { // || iter.isValid()){ //no sense to work with 'null' anyway...
			//if (currEdge instanceof Edge) {
			//	Weight = ((Edge) currEdge).weight; }
			adjList.addEdge(((ByRefInt) NodePos.getAt(currEdge.val)).Value,
							((ByRefInt) NodePos.getAt(currEdge.key)).Value, true, Weight); }
		return adjList; }

	/** Sorts the Nodes according to the Result in the Adjacency List AL,
	  * so that they reflect the Path and Positions in the Solution.
	  * This is the Re-Transformation to solving Problems in the SparseMatrix Representation,
	  * which is prepared by 'getAdjList()'.
	  */
	public static Pair sortNodesAt(final SparseGraph AL, final Pair Nodes) {
		final int[] p = AL.getPath();  //This is the Resulting Path
		final int[] q = AL.getPositions();  //and it's Inverse
		Nodes.Key = Permutation.map((Object[]) Nodes.Key, p.length, p, p.length); //reMap the Nodes
		mapAt(q, ((Function) Nodes.val).Iterator());
		return Nodes; }

	/**Tests all Methods of this Class	 */
	public static void testForest() {
		System.out.println("\nTesting SparseMatrix:");
		String[][]Edges={{"A","B", "1"},
						 {"C","A", "1"},
						 {"L","M", "1"},
						 {"J","M", "2"},
						 {"J","L", "3"},
						 {"J","K", "1"},
						 {"E","D", "2"},
						 {"D","F", "1"},
						 {"H","I", "2"},
						 {"A","F", "2"}, //taking out these two Edges
						 {"G","E", "1"}, //breaks it up into 4 Components
						 {"A","G", "4"}, //taking out this isolates G completely!
						 {"F","E", "2"}};
		Function AM = new Function();
		int i; //create undirected(!) Graph
		i = -1; while (++i < Edges.length) AM.addItem(Edges[i][0],
													  Edges[i][1], false); //, Edges[i][2]);
		System.out.println("Graph: " + AM);
		System.out.println("\nTree Edges:");
		System.out.println("\nTree Nodes:");

//		System.out.println(AL);
		Pair Nodes = new Pair();
		SparseGraph AL = AM.getAdjList(Nodes); //do all Calculations before transforming back!
		ArrayList Path = (ArrayList) Nodes.Key; //'Key' contains the Array: I->O
		Function Positions = (Function) Nodes.val;  //'Value' contains the Relation
		System.out.println("Mapping of the Problem:");
		System.out.print  ("Array Map:");
		AStreamOut.COLLECTION_TO_STREAM(System.out, Path, ", ");
//		AStreamOut.ARRAY_TO_STREAM(System.out, Path, ", ");
		System.out.println();
		System.out.println("Hash Map \n" + Positions);
		System.out.println("Adjacency List Representation: \n" + AL);
		System.out.println("Is there a Path between B and E (3)? " + AL.getDiscreteDistance(((ByRefInt) Positions.getAt("B")).Value,
																				 ((ByRefInt) Positions.getAt("E")).Value));
		System.out.println("Is there a Path between F and F (0)? " + AL.getDiscreteDistance(((ByRefInt) Positions.getAt("F")).Value,
																				 ((ByRefInt) Positions.getAt("F")).Value));
		System.out.println("Is there a Path between B and F (2)? " + AL.getDiscreteDistance(((ByRefInt) Positions.getAt("B")).Value,
																				 ((ByRefInt) Positions.getAt("F")).Value));
		System.out.println("Is there a Path between A and C (1)? " + AL.getDiscreteDistance(((ByRefInt) Positions.getAt("A")).Value,
																				 ((ByRefInt) Positions.getAt("C")).Value));
		System.out.println("Is there a Path between M and F(-1)? " + AL.getDiscreteDistance(((ByRefInt) Positions.getAt("M")).Value,
																				 ((ByRefInt) Positions.getAt("F")).Value));

		AL.traverse(true, null, null, null); System.out.println();	//this is the mapped Algorithm
		sortNodesAt(AL, Nodes); //undo the Transformation, but not before all wanted Calculations are done!
		Path = (ArrayList) Nodes.Key; //'Key' contains the Array: I->O
		Positions = (Function) Nodes.val;  //'Value' contains the Relation
		System.out.println("\nDepth Search through the whole undirected(!) tree gives out the Subtrees (connected Components):");
		System.out.println("Expected: JKLM + ABCDEFG + HI");
		AStreamOut.COLLECTION_TO_STREAM(System.out, Path, ", ");
//		AStreamOut.ARRAY_TO_STREAM(System.out, Path, ", ");
		System.out.println();
	}
}
