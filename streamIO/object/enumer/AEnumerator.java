package streamIO.object.enumer;

import java.io.IOException;
import java.util.Comparator;

import streamIO.AStreamOut;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.exception.OperationNotSupported;
import streamIO.exception.ReadOnlyException;
import streamIO.object.ModificationException;

/**Abstract Enumerator Class
 *
 * Design Decisions:
 * If Enumerators are inner Classes they implicitly know their outer Object,
 * which saves handing over 'this'. But this is not done here,
 * because this abstract Parent Class cannot be an inner Class.
 * @stereotype enumeration
 * <!-- docstate
 * tags: [code/enumerator, code/iterator_adapter]
 * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public abstract class AEnumerator
extends AChangeStreamIn
implements Enumerator { 	
//not neccessary to derive it from ACopyAble!

	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////

	/**Major Version of the Container at Creation Time of the fast-fail Enumerator
	 * that throw a ConcurrentModificationException
	 * instead of synchronizing all the Container Methods
	 * and thus blocking the Container.
	 * Must be updated on any Change of the Container' structure
     * to trigger fast-fail Enumerators.
	 */
	protected int major;

	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////

	/** Returns the current Major Version of the Container to support fast-fail Enumerators
	 * Should be incremented on each structural change of the Container
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Enumerator.
	 * Using int should be relatively safe,
	 * because Containers will at most contain about |int| Elements.
	 * Calling this Method additionally to nextItem is quite expensive,
	 * so the Enumerator should try to access the Field directly.
	 */
	final public int getMajor() { return major; }

	/**Increments and returns the current Major Version of the Container
	 * to indicate Modification to fast-fail Iterators.
	 * The Version should be incremented on each structural change of the Container
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Iterator.
	 * Using int should be large enough,
	 * because Containers will at most contain about |int| Elements.
	 */
	final public int incMajor() { return ++major; }

	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/**Constructor setting the current Version of the Container
	 * This "Version" must be checked on any nextItem() Operation.
	 * The currItem() Operation does not go back to the Container.
	 * @param _container a versioned container backing this Enumerator. Null allowed
	 */
	public AEnumerator(final IAlterAble _container) { 
		super(_container);
	}
	
	/** intended to reset the Version on versioned iterators 	 */
	protected void resetVersion(final IChangeAble _container) { 
		super.resetVersion(_container);
		if (_container != null)
			major = ((IAlterAble) _container).getMajor(); 
	} 
		
	/** Creates a new Enumerator with the same Position.
	 * @return a new Enumerator with the same Position	 */
	public Enumerator Enumerator() {
		try { return (Enumerator) this.clone(); }
		catch (CloneNotSupportedException x) { return null; } } //throw new CloneNotSupportedError(x.toString); } }

	////////////////////////////////////////////////////////////////////////////
	//	public Methods
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//	pseudo abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * The remaining Problem is other Enumerators that concurrently work through this.
	 * @return this Enumerator to allow for concatenated Adding
	 * @param Object to be added to this Container / Enumerator
	 * @throws ModificationException when this Container is sorted or read only  */
	public Enumerator addNext(Object Item) throws ModificationException { //
//		if (Cont != null) Minor = Cont.getMinor(); //re-read the Minor, because this Enumerator is aware of the Change.
		throw new OperationNotSupported(); }

    /**Replaces the next Object from the streamIO with this Item.
     * It should also update the Minor Version (or let the Container update it)
     * to announce the Change to other Iterators.
	 * This Operation can be used to e.g. influence Parsers concurrently.
	 * This Operation is not supported in sorted Containers,
	 * because the next Item cannot be replaced.
     */
	public Object replaceNext(Object Item) { // throws ModificationException {
		throw new ReadOnlyException(AEnumerator.class.getName()); }

	/**Removes the next Object from the Set and Iteration,
	 * returns the removed Item,
	 * this makes it necessary to define it separately,
	 * because it returns more Information: whether the Item was found or not!
	 */
	public Object removeNext() throws ModificationException {
//		if (Cont != null) Minor = Cont.getMinor(); //re-read the Minor, because this Enumerator is aware of the Change.
		throw new ModificationException(AEnumerator.class.getName()); }

	////////////////////////////////////////////////////////////////////////////
	//	abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/**Closes the Enumerator to prevent further reading from it
	 * and to deallocate all ressources blocked by it (especially Files, Network Ports etc.).
	 * Trying to read from a closed Enumerator results in an IOException.
	 * Closing happens automatically on Finalization of Enumerator Objects.
	 */
//	public void close() { }

	////////////////////////////////////////////////////////////////////////////
	//  Interface IterAble: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Returns an Iterator of the components in this Container.
	 *
	 * @return  an Enumerator of the components in this Container.
	 * @see     Math.Enumerator      */
//	public abstract IStreamIn Iterator();

	////////////////////////////////////////////////////////////////////////////
	//  Interface ChangeAble: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Returns a new Intstance of a ModStreamIn Iterator,
	  * which allows for changing the Data concurrently. */
//	public abstract ChangeStreamIn ChangeIterator();

	////////////////////////////////////////////////////////////////////////////
	//  Interface ChangeStreamIn: abstract Methods
	////////////////////////////////////////////////////////////////////////////

    /**Replaces the current Object in the Container with the given Item.
     * One Problem is other Enumerators that concurrently work through this Container.
     * Another Problem is that removing the Item may not be possible at all.
     * In this Case the Exception is thrown.
     * That is why this Method should throw an Exception if replacing is not allowed.
     * It should also update the Minor Version (or let the Container update it)
     * to announce the Change to other Iterators.
     */
//	public abstract Object replaceCurr(Object Item) { // throws    ModificationException;// {
//    										throw new ModificationException(); }

	////////////////////////////////////////////////////////////////////////////
	//  Interface AlterAble: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Returns a new Intstance of an alterable Object Input streamIO,
	  * which allows for changing the Data and structure concurrently. */
//	public abstract Enumerator Enumerator();

	////////////////////////////////////////////////////////////////////////////
	//  Interface AlterStreamIn: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Removes the current Object in the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * In this Case the Exception is thrown.
	  * That is why this Method should throw an Exception if replacing is not allowed.
	  * It should also update the Minor Version (or let the Container update it)
	  * to announce the Change to other Iterators.
	  */
//	public abstract Object removeCurr() throws ModificationException;// {
//		throw new ModificationException(); }

	////////////////////////////////////////////////////////////////////////////
	//	Interface IStreamOut
	////////////////////////////////////////////////////////////////////////////

	/** Does nothing; this Enumerator has no buffered Output to flush.
	 * @see streamIO.IStreamOut#flush()	 */
	public void flush() throws IOException {}
	
	/** adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public IIStreamOut addItem(Object arg) { try { return addNext(arg); }
		catch (ModificationException x) { throw new ReadOnlyException(x.toString(), x); } }

	/** adds these Items to the Store in Place: +=
	 * The Type of Item is analyzed, i.e. Containers Contents is added,
	 * but not recursively, but only flattened by one Level (flatDepth == 1).
	 * Named with capital A, to distinguish it from streamIO.Copy.Group.add()*/
	public long addItems(Object arg) { return AStreamOut.ADD_ITEMS(this, arg, 1); }

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but only recursively, when flattened is true.	  */
	public long addItems(Object arg, int flatDepth) { return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public long addItems(Object[] arg) { return AStreamOut.ADD_ITEMS(this, arg); }

	/** adds all Items from the Enumerator to the Store in Place: +=
	 * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public long addItems(IIStreamIn Iter) { return AStreamOut.STREAM(Iter, this); }

	////////////////////////////////////////////////////////////////////////////
	//  Interface Pipe: Default Implementations
	////////////////////////////////////////////////////////////////////////////

	/** Returns null by Default, meaning no explicit Comparator is used.
	 * @return The Comparator being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see Comparable  or
	  * @see IIOrderAble	 */
	public Comparator getComparator () { return null; }
	
	/**
	  * sets the Iterator behind the last Position.
	  * This is the Opposite to reset()
	  * just like previous() the Opposite to next()
	  */
//	public ReverseEnumerator preset() {//throws NoSuchMethodException {
//		throw new NoSuchMethodException(); }

}
