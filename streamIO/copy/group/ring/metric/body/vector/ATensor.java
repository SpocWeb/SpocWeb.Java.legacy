package streamIO.copy.group.ring.metric.body.vector;

import java.util.Comparator;

import streamIO.AReSetAble;
import streamIO.AStreamOut;
import streamIO.IFormatOut;
import streamIO.IMarkAble;
import streamIO.IPushBackAble;
import streamIO.IReSetAble;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.StreamOutPrimitive;
import streamIO.StringBufferOutputStream;
import streamIO.copy.group.ring.IIntRing;
import streamIO.object.AStreamIn;
import streamIO.object.IPipe;
import streamIO.object.enumer.ChangeIterator;
import streamIO.object.enumer.Enumerator;
import streamIO.object.enumer.IndexEnumerator;
import streamIO.object.enumer.ReverseEnumerator;
import tester.IEquivalence;
import tester.ITester;
import function.IProcessor;
import function.derive.ring.AAlgebra;

/**Abstract base class for tensor-like Manifolds, adding {@link IndexEnumerator} traversal
  * to the {@link streamIO.copy.group.ring.metric.IMetricIRing} algebra so a Manifold can be
  * both computed on and iterated over.
  *
  * Abstract Base Class to all Manifolds
  * Adds the IndexEnumerator Methods to the MetricIRing Interface
  *
  * Design Decisions:
  * Extending ATensor -> AALgebra -> AMetricIRing -> AIntegrityRing -> ARing
  * to exploit the Integration of Mapping!
  *
  * This Type unfortunately has more than 350 Methods,
  * more than 75 from this Interface
  * which makes using this Object really very powerful, but also very hard.
  *
  * The Alternative of adding a Method getIterator to the Manifold
  * would have required a Synchronization between the Iterator and the Manifold,
  * which carries more Overhead and is complicated to realize!
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:41:54Z
  * digest: 037efcb680d6b8b7dc8318755f66e8952001017a95c592eb512db86ab3c8ab51
  * stale: false
  * tags: [code/tensor, code/manifold_generation, code/interpolation]
  * concepts: [Vector/Matrix/Tensor and Manifold Interpolation]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public abstract class ATensor
extends AAlgebra //AMetricIRing
implements ITensor { //IndexEnumerator {

	///////////////////////////////////////////////////////////////////////////
	//	Members
	///////////////////////////////////////////////////////////////////////////

	/** Separator String for the
	  * @see toString() Methods		*/
	public String Separator = ",";

	/** Degree of the Manifold / Polynom = Number of valid Elements-1 */
	protected int mDim = -1;

	/** Counter for the current Position,
	  * since a PreIncrement is used on nextItem()	 */
	protected int curr = -1;

	/** Index for the mark()ed Position	 */
	protected int mark = -1;

	/** Major Version of the Container at Creation Time of the fast-fail Enumerator
	  * that throw a ConcurrentModificationException
	  * instead of synchronizing all the Container Methods
	  * and thus blocking the Container.
	  * Must be updated on any Change of the Container' structure
	  * to trigger fast-fail Enumerators.
	  *
	  * A negative Major disallows structural Changes!
	  */
	protected int majorVersion;

	/** Minor Version of the Container. 
	 * At Creation a fast-fail Iterator should read this.
	  * and throw a ConcurrentModificationException instead of synchronizing
	  * all the Container Methods and thus blocking the Iterators.
	  * Must be updated on any Change of the Container to trigger fast-fail Enumerators.
	  * Can also be used to keep the Version if this is a Container.
	  * Counts the Number of simple Data Changes of this Container
	  *
	  * A negative Minor disallows Changes, i.e. makes the Iterator Read Only!
	  */
	protected int minorVersion;

	/** The Filter Object,
	  * only Items that are equal to this Object are returned by nextItem()! */
	protected Object mFilter;

	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////

	/**Returns the Grad of the Polynom == Dimension-1 of the Vector,
	 * or the Number of Items for the shifting Operations.
	 * This is also Period for large Shifting, Rotation and Reversion
	 * (for performance Reasons)	 */
	public int getDim(){ return  mDim; }

	/**Returns the current Filter Object.
	  * @return the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()! */
	public Object getFilter() { return mFilter; }

	/** Sets the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()!
	  * This allows for Optimizations on hashed and sorted Containers
	  * because the Result Set can be decreased dramatically. */
	public void setFilter(Object Value) { mFilter = Value; }

	/**Returns the current Major Version, used by fast-fail Enumerators to detect structural changes.
	  * @return the current Major Version of the Container to support fast-fail Enumerators
	  * Should be incremented on each structural change of the Container
	  * and checked for the same Value on each Call of nextItem() or currItem()
	  * to warn the User (Client) of the Enumerator.
	  * Using int should be relatively safe,
	  * because Containers will at most contain about |int| Elements.
	  * Calling this Method additionally to nextItem is quite expensive,
	  * so the Enumerator should try to access the Field directly.
	  */
	public int getMajor() { return majorVersion; }

	/**Increments and returns the current Major Version, to indicate a structural change.
	  * @return the incremented current Major Version of the Container
	  * to indicate Modification to fast-fail Iterators.
	  * The Version should be incremented on each structural change of the Container
	  * and checked for the same Value on each Call of nextItem() or currItem()
	  * to warn the User (Client) of the Iterator.
	  * Using int should be large enough,
	  * because Containers will at most contain about |int| Elements.
	  */
	public int incMajor() { return ++majorVersion; }

	/**Returns the current minor Version, used by fast-fail Enumerators to detect data changes.
	  * @return the current Version of the Container to support fast-fail Enumerators
	  * Should be incremented on each change of the Container
	  * and checked for the same Value on each Call of nextItem() or currItem()
	  * to warn the User (Client) of the Enumerator.
	  * Using int should be relatively safe,
	  * because Containers will at most contain about |int| Elements.
	  * Calling this Method additionally to nextItem is quite expensive,
	  * so the Enumerator should try to access the Field directly.
	  */
	public int getMinor() { return minorVersion; } //return 0; }

	/**Increments and returns the current minor Version, to indicate a data change.
	  * @return the incremented current minor Version of the Container
	  * to indicate Modification to fast-fail Iterators.
	  * The Version should be incremented on each change of the Container
	  * and checked for the same Value on each Call of nextItem() or currItem()
	  * to warn the User (Client) of the Iterator.
	  * Using int should be large enough,
	  * because Containers will at most contain about |int| Elements.
	  */
	public int incMinor() { return ++minorVersion; }

	///////////////////////////////////////////////////////////////////////////
	//	IndexEnumerator: abstract Methods
	///////////////////////////////////////////////////////////////////////////

	/**Returns the Item at the given absolute Position.
	  * @return the Item at the given absolute Position
	  * While this is possible in principle for all Enumerators,
	  * it is too ineffective to loop through the whole Enumerator
	  * @see function.index.IDirectRead defines the same Method
	  */
	public abstract Object getAt(final int Index); // { return null; }

	/**Inserts the given Item at the given absolute Position.
	  * @return the Item at the given absolute Position
	  * While this is possible in principle for all Enumerators,
	  * it is too ineffective to loop through the whole Enumerator
	  */
	public abstract IndexEnumerator addAt(int Index, Object arg); // { return null; }

	/** Removes the Object at the given Index in the Container with this Enumerator knowing it.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * That is why this Method should throw an exception if removing is not allowed.   */
	public abstract Object removeAt(int Index); // { return null; }; //

	/** Replaces the Object at the given Index in the Container with this Enumerator knowing it.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * That is why this Method should throw an exception if removing is not allowed.   */
	public abstract Object setAt(int Index, Object arg); // { return null; }; //

	///////////////////////////////////////////////////////////////////////////
	//	Methods
	///////////////////////////////////////////////////////////////////////////

	/** Returns the current Object:
	  * Returning the cached currItem is faster! 	*/
	public Object currItem() { return getAt(curr); }

	/**Returns the next Object: 	 */ //prevent incrementing currItem above Limit
	public Object nextItem() { return getAt(++curr); }

	/**Returns the next Object: 	 */ //prevent decrementing currItem below 0
	public Object prevItem() { return getAt(--curr); }

	/** Resets the Enumerator to the last marked Position,
	  * done automatically on Instantiation	 */
	public long reSet(final long position) { //throws NoSuchMethodException {
		curr = mark + (int) position; return position; }

	/** Marks the current position in this Enumerator.
	  * A subsequent call to the reset() method repositions this Enumerator
	  * at the last marked position.
	  * The readlimit arguments tells this input stream to allow that many Items
	  * to be read before the mark position gets invalidated.
	  * This is to limit the Blocking of System Ressources but ignored here	 */
	public IMarkAble mark() { //throws NoSuchMethodException {
		mark = curr; return this; }

	/** Marks the current position in this Enumerator.
	  * A subsequent call to the reset() method repositions this Enumerator
	  * at the last marked position.
	  * The readlimit arguments tells this input stream to allow that many Items
	  * to be read before the mark position gets invalidated.
	  * This is to limit the Blocking of System Ressources but ignored here	 */
	public IMarkAble mark(final long readLimit) { //throws NoSuchMethodException {
		mark = curr; return this; }

	/**Returns the number of Items remaining ahead of the current Position.
	 * @return the Number of minimum available Objects		*/
	public long availAble() { return mDim - curr; }

	/**Returns whether the current Position is still within the valid Range.
	 * @see streamIO.IIStreamIn#isValid()
	 */
	public boolean isValid() { return curr <= mDim; }

	/**Returns the number of Items already passed, reachable again via previousItem().
	  * @return the number of Items
	  * that are to be reached by previousItem.	 */
	public long availableBefore() { return curr; }

	/**Returns the maximum number of Items that can be marked/rewound over.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return mDim; }

	/**Returns the current Position of this Enumerator.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return curr; }

	/**Resets this Enumerator to its initial Position, failing with the given message if unsupported.
	 * @see streamIO.IReSetAble#reSet(java.lang.String)	 */
	public IReSetAble reSet(String failureExceptionMessage) {
		return AReSetAble.RESET(this, failureExceptionMessage); }

	/**Advances the current Position by the given (possibly negative) amount.
	 * @see streamIO.IReSetAble#jump(long)	 */
	public long jump(final long _position) { return (curr+=_position)-curr; }

	/**Returns the Item at the given relative Position	 */
	public Object getRel(final int index) { return getAt(curr + index); }

	/**Jumps this Enumerator forward by one Position.
	 * @see streamIO.object.IStreamIn#jump()     */
	public IReSetAble jump() { return AReSetAble.JUMP(this); }
    
	/** 
	 * Jumps a single Position back in this Iterator.
     * equivalent to jump(-1); 
	 * @see streamIO.IReSetAble#pushBack()  
	 * @return this Stream if jumping worked, null otherwise. 
	 */
    public IPushBackAble pushBack() { return AReSetAble.PUSH_BACK(this); }
	
	///////////////////////////////////////////////////////////////////////////
	//	Methods
	///////////////////////////////////////////////////////////////////////////

	/**Removes the next Object from the Set and Iteration,
	 * returns the removed Item,
	 * this makes it necessary to define it separately,
	 * because it returns more Information: whether the Item was found or not!
	 * @throws ModificationException if the Container is sorted or read only
	 */
	public Object replaceNext(Object arg) { //throws ModificationException {
		return setAt(curr+1, arg); }

	/** Replaces the previous Object in the Set and Iteration with the given one,
	  * and returns the replaced Item,
	  * this makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  * @throws ModificationException if the Container is sorted or read only
	  */
	public Object replacePrev(Object arg) { //throws ModificationException {
		return setAt(curr-1, arg); }

	/** Replaces the previous Object in the Set and Iteration with the given one,
	  * and returns the replaced Item,
	  * this makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  * @throws ModificationException if the Container is sorted or read only
	  */
	public Object replaceCurr(Object arg) { //throws ModificationException {
		return setAt(curr, arg); }

	/**Removes the next Object from the Set and Iteration,
	 * returns the removed Item,
	 * this makes it necessary to define it separately,
	 * because it returns more Information: whether the Item was found or not!
	 * @throws ModificationException if the Container is read only
	 */
	public Object removeNext() { //throws ModificationException {
		return removeAt(curr+1); }

	/**Removes the next Object from the Set and Iteration,
	 * returns the removed Item,
	 * this makes it necessary to define it separately,
	 * because it returns more Information: whether the Item was found or not!
	 * @throws ModificationException if the Container is read only
	 */
	public Object removePrev() { //throws ModificationException {
		return removeAt(curr-1); }

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * @throws ModificationException if the Container is read only
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not */
	public Object removeCurr() { //throws ModificationException {
		return removeAt(curr); }

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @throws ModificationException if the Container is sorted or read only
	 * Could also return a boolean whether the Method is supported or not */
	public ReverseEnumerator addCurr(Object Item) { //throws ModificationException {
		return addAt(curr, Item); }

	/**Removes the next Object from the Set and Iteration,
	 * returns the removed Item,
	 * this makes it necessary to define it separately,
	 * because it returns more Information: whether the Item was found or not!
	 * @throws ModificationException if the Container is sorted or read only
	 */
	public Enumerator addNext(Object Item) { //throws ModificationException {
		return addAt(curr+1, Item); }

	/**Removes the next Object from the Set and Iteration,
	 * returns the removed Item,
	 * this makes it necessary to define it separately,
	 * because it returns more Information: whether the Item was found or not!
	 * @throws ModificationException if the Container is sorted or read only
	 */
	public ReverseEnumerator addPrev(Object Item) { //throws ModificationException {
		return addAt(curr-1, Item); }

	///////////////////////////////////////////////////////////////////////////
	//  relative Operations
	///////////////////////////////////////////////////////////////////////////

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * @throws ModificationException if the Container is sorted or read only
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not */
	public Object removeRel(int Index) { //throws ModificationException {
		return removeAt(curr + Index); }

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @throws ModificationException if the Container is sorted or read only
	 */
	public Object replaceRel(int Index, Object Item) { //throws ModificationException {
		return setAt(curr + Index, Item); }

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @throws ModificationException if the Container is sorted or read only
	 * Could also return a boolean whether the Method is supported or not */
	public IndexEnumerator addRel(int Index, Object Item) { //throws ModificationException {
		return addAt(curr + Index, Item); }

	/**Returns the index of the first occurrence of the specified object in
	 * this Array.
	 *
	 * @param   elem   the desired component.
	 * @return  the index of the last occurrence of the specified object in
	 *		  this Array; returns <code>-1</code> if the object is not found.	 */
	public int firstIndexOf(Object elem) {
		return firstIndexOf(elem, -1);}

	/**Returns the index of the last occurrence of the specified object in
	 * this Array.
	 *
	 * @param   elem   the desired component.
	 * @return  the index of the last occurrence of the specified object in
	 *		  this Array; returns <code>-1</code> if the object is not found.	 */
	public int lastIndexOf(Object elem) {
		return lastIndexOf(elem, (int) availAble ()); } // getInt()); }

	/**Searches forwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   lower   the index to start searching from.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>-1</code> if the object is not found.	 */
	public int firstIndexOf(Object elem, int lower) {
		return firstIndexOf(elem, lower, (int) availAble ()); } // getInt()); }

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   upper   the index to start searching from.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>-1</code> if the object is not found.	 */
	public int lastIndexOf(Object elem, int upper) {
		return lastIndexOf(elem, -1, upper);}

	/**Searches forwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   lower   the index to start searching from.
	 * @param   upper   the index to stop  searching at.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>stop+1</code> if the object is not found.	 */
	public int firstIndexOf(Object elem, int lower, int upper) {
		int i = lower;
		while (++i < upper)
			if (elem.equals(getAt(i))) break;
		return i; }

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   upper   the index to start searching from.
	 * @param   lower   the index to stop  searching at.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>stop-1</code> if the object is not found.	 */
	public int lastIndexOf(Object elem, int lower, int upper) {
		int i = upper;
		while (--i > lower)
			if (elem.equals(getAt(i))) return i; //break; //equivalent...
		return i; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOut
	////////////////////////////////////////////////////////////////////////////////
	
	/**No-op: this Container has no buffered writes to flush.
	 * @see streamIO.IStreamOut#flush()	 */
	public void flush() { }
	
	/** Adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * The Position of the Item is undefined either.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * @return this StreamOut to append more Items
	  */
	public IIStreamOut addItem(Object arg) {
		return addAt(mDim +1, arg); }//add it at the End

	/** Adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but not recursively, but only flattened by one Level (flatDepth == 1).
	  * Named with capital A, to distinguish it from streamIO.Copy.Group.add() 	*/
	public long addItems(Object arg) {
		return AStreamOut.ADD_ITEMS(this, arg, 1); }

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but only recursively, when flattened is true.	  */
	public long addItems(Object arg, int flatDepth) {
		return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
      * Should be called addAt(), but that would result in Ambiguities
      * with the addAt() Method of Group and Container	 */
	public long addItems(Object[] arg) { //throws IOException;// {
		return AStreamOut.ADD_ITEMS(this, arg); }

	/** adds all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
      * Should be called addAt(), but that would result in Ambiguities
      * with the addAt() Method of Group and Container	 */
	public long addItems(IIStreamIn arg) { //throws IOException;// {
		return AStreamOut.STREAM(arg, this); }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface Pipe
	////////////////////////////////////////////////////////////////////////////////

	/**Returns stack Order: items added via addItem() come back first via nextItem().
	  * @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return IPipe.ORDER_STACK; }

	/**Returns {@code null}, meaning Elements are compared via their own natural ordering.
	  * @return The Comparator being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see java.lang.Comparable  or
	  * @see IIOrderAble	 */
	public Comparator getComparator () { return null; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface ChangeAble
	////////////////////////////////////////////////////////////////////////////////

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * which allows for changing the Data concurrently. */
	public ChangeIterator ChangeIterator() {
		return null; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IterAble
	////////////////////////////////////////////////////////////////////////////////

	/**Returns {@code null}: this Container does not support a separate Enumerator instance.
	  * @return an Enumerator for this Container 	*/
	public Enumerator Enumerator() {
		return null; }

	////////////////////////////////////////////////////////////////////////////
	// Interface StreamIn Implementations:
	////////////////////////////////////////////////////////////////////////////

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	public IIStreamIn Iterator() {
//		try { return (IStreamIn) clone();
//		} catch (CloneNotSupportedException x) { throw new OperationNotSupported("Should not happen!", x); }
		return null; }

	/** Returns and moves to the last (Root) Object of this one.
	  * This should be used with Care, because it could result in Blocking
	  * or infinite Loops with infinite Streams. */
	public Object lastItem() { return AStreamIn.LAST_ITEM(this); }

	//Convenience Array Read Methods

	/** Returns as many Items as are available() currently 	 */
	public Object[] nextItems() { return AStreamIn.NEXT_ITEMS(this); }

	/** Returns as many Items as possible, but maximum numItems stored in Items from Index Begin 	 */
	public int nextItems(Object[] Items, int numItems, int Begin) {
		return AStreamIn.NEXT_ITEMS(this, Items, numItems, Begin); }

	/** Returns as many Items as possible, but maximum numItems stored in Items
	  * small Optimization in using the Default Parameters right away */
	public int nextItems(Object[] Items, int numItems) {
		return AStreamIn.NEXT_ITEMS(this, Items, numItems, -1); }

	/** Returns as many Items as possible, but maximum Items.length in Items 	 */
	public int nextItems(Object[] Items) {
		return AStreamIn.NEXT_ITEMS(this, Items, Items.length, -1); }

	//Convenience Bulk Tests and Operations

	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOL	 */
	public Object firstThat (ITester tst) { //throws NoSuchMethodException {
		reSet (0); return AStreamIn.NEXT_THAT(this, tst); }

	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOL	 */
	public Object nextThat (ITester tst) { return AStreamIn.NEXT_THAT(this, tst); }

	/**Performs an Operation for each Object in this Iteration.
	 * Returns the Number of Operations performed.	 */
	public int forEach (IProcessor op) { return AStreamIn.FOR_EACH(this, op); }

	/**Performs the Operation of the Operator on each Item in the Collection
	 * that equals this Item. The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public int forEachThatEquals(Object Item, IProcessor op) {
		return AStreamIn.FOR_EACH_THAT_EQUALS (this, Item, op); }

	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object firstOfEachThatEqualsThat(Object Item, ITester Test) { //throws NoSuchMethodException {
		reSet (0); return AStreamIn.NEXT_OF_EACH_THAT_EQUALS_THAT(this, Item, Test); }

	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object nextOfEachThatEqualsThat(Object Item, ITester Test) {
		return AStreamIn.NEXT_OF_EACH_THAT_EQUALS_THAT(this, Item, Test); }

	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (Object Item, IEquivalence EQ) {
		return AStreamIn.FIND_NEXT (this, Item, EQ); }

	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (Object Item, int index) {
		return AStreamIn.FIND_NEXT (this, Item, index); }

	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (Object Item, int index, IEquivalence EQ) {
		return AStreamIn.FIND_NEXT (this, Item, index, EQ); }

	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object find(final Object Item) { //throws {
		//try {
			return findFirst(Item);
		//} catch (final NoSuchMethodException x) {
		//	return findNext(Item);
		//}
	}

	/** Tests, whether this Object exists in the Set,
	  * @return the first Item found that equals Item, otherwise IStreamIn.EOI
	  * @param EQ Equivalence Relation used to test Equality, instead of equals()
	  * Cannot be used iteratively, because it resets the streamIO. 	 */
	public Object findFirst (Object Item, IEquivalence EQ) { //throws NoSuchMethodException {
		reSet (0); return AStreamIn.FIND_NEXT(this, Item, EQ); }

	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findNext (Object Item) { return AStreamIn.FIND_NEXT(this, Item, null); }

	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findFirst (Object Item) { //throws NoSuchMethodException {
		reSet (0); return AStreamIn.FIND_NEXT(this, Item, null); }

	/**Returns whether the given Item is contained in this Container.
	  * @return true when this Object is contained in this Container
	  * This is the same Operation as (findFirst() != EOI) || (available() >= 0)
	  * @see Sub() and SubEq() for the according Container Methods,
	  * The Name contains() is only to be used for single Elements
	  */
	public boolean contains(Object Item) { //throws NoSuchMethodException {
//		try {
			return (findFirst(Item) != EOI) || this.isValid();
//		} catch (NoSuchMethodException x) { //choose the alternative Strategy and search only forwards!
//			return (findNext (Item) != EOI) || this.isValid();
//			throw new OperationNotSupported(x.toString(), x); }
	}

	/**Returns whether every Object of this streamIO also occurs in arg.
	 * @return true, when all Objects of this streamIO exists in the streamIO arg
	 * Requires streamIO arg to be restartAble.
	 * There are more restrictive and thus 'cheaper' Searches:
	 * -finding all Elements of arg in Sequence
	 * -finding all Elements of arg in Sequence with intermittent Objects
	 * Uses the Monotony Criterion (for infinite Streams)
	 * This corresponds to the contains() Method.	 */
	public boolean SubEq(IIStreamIn arg, boolean Sequence) {
		return AStreamIn.SUB_EQ(this, arg, Sequence); }

	/**Returns the next Item without moving the current Position to it.
	 * @return the next Item without moving to it.	 */
	public Object peekItem() { //throws    NoSuchMethodException {
		return getAt(curr+1); }

	//Marking and Resetting a Stream (for re-Processing, if supported)

	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() { return AReSetAble.RESET(this, ""); }
	
	/**
	  * Sets the Iterator behind the last Position.
	  * This is the Opposite to reset()
	  * just like previous() the Opposite to next()
	  */
	public ReverseEnumerator preset() {//throws NoSuchMethodException {
		 curr = mDim + 1;
		 return this; }

	/** @return true, when the Items returned support the OrderAble Interface
	  * and they are returned in (strictly) ascending or descending Order.
	  * This is used as an additional criterion for Search Operations like findFirst()
	  * It is replaced by the @see Pipe.getOrder() Method: ordered, random, sorted
	  * Monotonous is implicitly sorted!	 */
//	public boolean isMonotonous() { return false; }

	/** Streams the whole Iterator to the String using the toString() Methods
	  * of the Elements of this streamIO.
	  * Does not try to reset this streamIO!
	  * This is now obsolete by using
	  * @see PrintStreamOut.add(Iter), but shorter!
	  * Should not be used on blocking or infinite Streams. 	 */
	public String toString() {
		//final StringWriter ret = new StringWriter();
		final StringBufferOutputStream ret = new StringBufferOutputStream(); 
		toStream(new StreamOutPrimitive(ret));
		return ret.toString(); 
	}

	/**Streams every Element of this Container to the given output in sequence.
	 * @return  a string representation of the object.
	 *  @see Object#toString()
	 */
	public void toStream(final IFormatOut stream) {//throws IOException
		AStreamOut.STREAM(this, stream, Integer.MAX_VALUE, false, false, null, Long.MAX_VALUE); //Separator, Long.MAX_VALUE);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Tensor Methods
	////////////////////////////////////////////////////////////////////////////////

	//The Tensor Products can be different:
	// mul  () c[i  ] = a[i]*b[i]
	// dyad () c[i,j] = a[i]*b[j]
	// short() c      = Sum(i, a[i]*b[i] = Sum(i, a.mul(b)) , the Scalar Product
	// map  () uses the Scalar Product

	/**Returns the dyadic Product of a copy of this Tensor and arg.
	  * @return the dyadic Product of this Tensor and arg.
	  * This is a pre Step to calculating the generic Scalar Product.
	  * The Degree of the Tensor is the Sum of the Degrees of the Factors.
	  *
	  * The inner structure of arg is retained on creating the Product.
	  */
	public ITensor dyad(ITensor arg) {
		return ((ITensor) copy()).dyadAt(arg); }

	/**Returns the dyadic Product of a copy of this Tensor and arg at the given Degree.
	  * @return the dyadic Product of this Tensor and arg.
	  * This is a pre-Step for calculating the generic Scalar Product.
	  * The Degree of the Tensor is the Sum of the Degrees of the Factors.
	  *
	  * The inner structure of arg is retained on creating the Product.
	  */
	public ITensor dyad(ITensor arg, int Degree) {
		return ((Tensor) copy()).dyadAt(arg, Degree); }

	/** Creates the Transpose of this Tensor: M^T
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * The Elements are copied, not reused. */
	public ITensor trp() { return trp(0); }//

	/** Creates the Transpose of this Tensor in Place: MT
	  * in the given (Default: first two) Dimensions: a[i][j] = b[j][i]
	  * i.e. in the Dimensions Degree and Degree+1.	 */
	public ITensor trpAt()	{ return trpAt(0); }

	/** Subtracts the Part which lies parallel to the Vector arg.
	  * Used primarily in orthogonalization.
	  * If argSqrNorm == null, it is assumed to be 1 (orthoNormal)
	  * this -= arg*((arg*this)/(arg*arg))	 */
	public ITensor subtPartAt(ITensor arg, IIntRing argSqrNorm) {
		IIntRing Prod = (IIntRing) cat(arg);
		if (argSqrNorm != null) {
			Prod.divAt(argSqrNorm); }
		addProdAt(Prod.negAt(), arg);
		return this; }

	/**Subtracts the Part which lies parallel to the Vector arg.
	 * Used in orthogonalization.
	 * If argSqrNorm == null, it is assumed to be 1 (orthoNormal)
	 * this -= arg*((arg*this)/(arg*arg))	 */
	public ITensor subtPart(ITensor arg, IIntRing argSqrNorm) {
		return ((ITensor) copy()).subtPartAt(arg, argSqrNorm); }

	/** Normalizes this Vector in Place to (euklidean) Length 1
	  * Makes only Sense for Vectors */
	public ITensor normalizeAt() { return (ITensor) mulAt(Norm().invAt());}

	/** Normalizes this Vector to (euklidean) Length 1
	  * Makes only Sense for Vectors */
	public ITensor normalize()	{return ((ITensor) copy()).normalizeAt();}

	/**Returns the Sum of all Elements in this Tensor.
	 * @return the Sum of all Elements in this Tensor Sum(i, x[i])	  */
	public IIntRing Sum() {
		return (IIntRing) AManifold.Sum(this, null); }

	/**Returns a copy of this Tensor with the given Degree summed away.
	  * @return this Tensor shortened at the given Degree.
	  * I.e. all Elements at Level 'Degree' are replaced by the Sum of all Elements below it
	  */
	public IIntRing Sum(int Degree) {
		return ((Tensor) copy()).SumAt(Degree); }

	/**Sums away the given Degree in Place, decreasing this Tensor's Degree by 1.
	  * @return this Tensor shortened in Place at the given Degree.
	  * I.e. all Elements at Level Degree are replaced
	  * by the Sum of the Elements right below them.
	  * The Degree of the Tensor decreases by 1
	  * a[i,j,k] => a[i,k] = Sum(j, a[i,j,k])
	  *
	  * A Scalar Product can be considered as the Trace of the dyadic Product.
	  */
	public IIntRing SumAt(int Degree) {
		if (--Degree < 0)
			return (IIntRing) AManifold.Sum(this, null);
		int i = mDim+1;
		while (--i >= 0) {
			setAt(i, ((Tensor) getAt(i)).SumAt(Degree));
//			a[i] = ((Tensor) a[i]).SumAt(Degree);
		} return this; }

}
