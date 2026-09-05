package streamIO.object.enumer.container;

import streamIO.IIStreamIn;
import streamIO.copy.monoid.Association;
import streamIO.object.AStreamIn;
import streamIO.object.IStreamIn;

/**
  * An Iterator that maps the Items of the given Iterator by the arg Relation
  * Instead of joining the individual Sets, this Iterator just iterates over all of them,
  * thus Duplicates can not be eliminated.
  * 
  * If arg is a Function this mapping is direct, removeCurrent() is propagated
  * and you don't need a caching Mechanism,
  * because for each Value there is exactly one mapping,
  * so the number of Item stays the same or decreases.
  * Mapping obeys only to the associative laws.
  * 
  * The Design is similar to ???FilterIn???
  * because also there a State has to be maintained to be able to stream the Data itemwise.
  * The Problem with this Iterator is that available() may Indicate the End.
  * 
  * Used in:
  * @see Function
  * @see Relation
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class MapIterator
extends AStreamIn {
	
	/** The next Item to be returned	 */
	protected Association MapAssoc;
	
	/** The next Item to be returned	 */
	protected Association currAssoc;
	
	/** The next Item to be returned	 */
	protected Object nextItem;
	
	/** The Item returned last	 */
	protected Object currItem;
	
	/**The original Iterator	 */
	protected IIStreamIn iter;
	
	/**The Iterator of the mapping Relation	*/
	protected HashIterator relIter;
	
	/**Initializing Constructor	 */
	public MapIterator(final HashContainer _rel, final IIStreamIn _iter){
		this.iter = _iter; 
		this.relIter = new HashIterator(_rel, this, null); //'this' is definitely not in the Relation!
		this.nextItem = getNextItem();  } //look ahead for available().

	/**Returns the minimum Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 */
	public long availAble() {
		//return relIter.availAble(); } //don't know if the next Items have a Mapping!
		if (nextItem == IIStreamIn.EOI) return -1;
		return relIter.availAble() + 1; }

	/** Returns the Item returned by the last {@link #nextItem()} call.
	 * @return  the current Item, returned by the last nextItem().	 */
	public Object  currItem() { return currItem; }

	/** Advances the Iterator and returns the mapped Item.
	 * @return  the next Item, used for looking ahead for available().	 */
	public Object  nextItem() {
		Object ret = nextItem; nextItem = getNextItem(); return currItem = ret; }

	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(long _position) { //throws    NoSuchMethodException {
		((IStreamIn) iter).reSet(); nextItem = getNextItem();
		return jump(_position);  }
	
	/** @return  the next Item, used as a Look ahead to keep availAble() valid!	 */
	protected Object getNextItem() { //#now used indirectly for defining available()
		while (true) { //see if there are more Items in the Relation Iterator...
			if ((currAssoc = (Association) relIter.nextItem()) != IIStreamIn.EOI) { return currItem = new Association(MapAssoc.key, currAssoc.val); }
			if (( MapAssoc = (Association)    iter.nextItem()) == IIStreamIn.EOI) { return currItem = MapAssoc; } //Iterator.EOI; }
			relIter.setFilter(MapAssoc.val); } } //set relIter for the given Key
	
	/** Delegates to the underlying Iterator's maximum mark Size.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return ((IStreamIn) iter).getMaxMarkSize(); }

	/** Delegates to the underlying Iterator's current Position.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return ((IStreamIn) iter).getPosition(); }
	
}

