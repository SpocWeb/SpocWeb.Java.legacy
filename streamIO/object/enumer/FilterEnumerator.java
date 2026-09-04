package streamIO.object.enumer;

import streamIO.IIStreamIn;
import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.object.ModificationException;

/**Prototype for a Filter working on an Input-streamIO of Objects
 * Overwrites ALL Methods with Passes to the Parent Enumerator.
 * It effectively does nothing, but to leave the filtering
 * to inheriting classes to overwrite nextItem().
 *
 * For a new FilterEnumerator based on this you have to overwrite
 * removeCurrent, currItem, nextItem	 */
public class FilterEnumerator
extends AEnumerator
implements Enumerator {
	
	///////////////////////////////////////////////////////////////////////////
	//	Variables
	///////////////////////////////////////////////////////////////////////////
	
	/**Local Reference to the Parent Enumerator used	 */
	protected Enumerator parent;
	
	/**Initializing Constructor taking the Delegation Enumerator	 */
	public FilterEnumerator(Enumerator _parent) { 
		super(_parent); 
		this.parent = _parent; }
	
	///////////////////////////////////////////////////////////////////////////
	//	Methods
	///////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return parent.getMaxMarkSize(); }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return parent.getPosition(); }
	
	/** Passes removing the current Item from the Enumerator to the Parent	 */
	public Object replaceNext(Object Item) { //throws ModificationException {
		return parent.replaceNext(Item); }
	
	/** Passes removing the current Item from the Enumerator to the Parent	 */
	public Object removeNext() throws ModificationException { return parent.removeNext(); }
	
	/** Passes removing the next Item from the Enumerator to the Parent	 */
	public Enumerator addNext(Object Item) throws ModificationException { parent.addNext(Item); return this; }
	
	/** Returns the Parent's next Item	 */
	public Object nextItem() { return parent.nextItem(); }
	
	/**Retrieves the current Item from the Parent	 */
	public Object currItem() { return parent.currItem(); }
	
	/**Resets the Enumerator to the last marked Position,
	 * done automatically on Instantiation	 */
	public IReSetAble reSet() { //throws NoSuchMethodException { 
	    parent.reSet(); return this; }
	
	/**Resets the Enumerator to the given Position
	 * counted from the last marked Position.	 */
	public long reSet(final long position) { //throws NoSuchMethodException { 
	    return parent.reSet(position); }
	
	/**Closes the Enumerator to prevent further reading from it
	 * and to deallocate all ressources blocked by it (especially Files).
	 * Trying to read from a closed Enumerator results in an IOException.	 */
//	public void close() { Parent.close(); }
	
	/**Skips over and discards n Items from this Enumerator.
	 * Returns the actual number of bytes skipped.	 */
	public long jump(final long n) { return parent.jump(n); }
	
	/**Returns true, when the Object has more Constituents. 	 */
	public long availAble() { return parent.availAble(); }
	
	/**Tests if this input stream supports the mark and reset methods. 	 */
//	public boolean markSupported() { return Parent.markSupported(); }
	
	/**Marks the current position in this Enumerator.
	 * A subsequent call to the reset method repositions this Enumerator
	 * at the last marked position.	 */
	public IMarkAble mark() { //throws NoSuchMethodException { 
	    parent.mark(); return this; }
	
	/**Marks the current position in this Enumerator.
	 * A subsequent call to the reset method repositions this Enumerator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public IMarkAble mark(final long ReadLimit) { //throws NoSuchMethodException { 
	    parent.mark(ReadLimit); return this; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOut: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** adds all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * Returns 'this' if the Object was written
	  * and 'null', if the Record could not be written,
	  * e.g. because the Drive is full or any other Error occurred (e.g. IOException)!
	  * So the Return Value should be tested!
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public long addItems(IIStreamIn arg) { return parent.addItems(arg); }
	
	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public long addItems(Object[] arg) { return parent.addItems(arg); }
	
	/** Returns a new Intstance of an alterable Iterator ,
	  * which allows for changing the Data and structure concurrently. */
	public Enumerator Enumerator() { return parent.Enumerator(); }
	
	/** Returns the Order in which Elements are returned or processed.	 */
	public byte getOrder() { return parent.getOrder(); }
	
	/** removes the current Item (returned by the latest nextItem())
      * @return the current Item	 */
	public Object removeCurr() throws ModificationException {
		return parent.removeCurr(); }
	
	/** Returns a new Intstance of a ChangeIterator,
	  * which allows for changing the Data concurrently. */
	public ChangeIterator ChangeIterator() { return parent.ChangeIterator(); }
	
	/** Replaces the current Object in the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * In this Case the Exception is thrown.
	  * That is why this Method should throw an Exception if replacing is not allowed.
	  * It should also update the Minor Version (or let the Container update it)
	  * to announce the Change to other Iterators.
	  * @param  The Item to replace the current Item (returned by the latest nextItem())
	  * @return the Object replaced by the Item
	  */
	public Object replaceCurr(Object Item) { //throws ModificationException {
         return replaceCurr(Item); }
	
}
