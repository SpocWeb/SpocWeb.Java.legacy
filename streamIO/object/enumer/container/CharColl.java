package streamIO.object.enumer.container;

import streamIO.IIStreamIn;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.ISemiGroup;
import streamIO.object.IPipe;
import streamIO.object.enumer.AIndexEnumerator;
import streamIO.object.enumer.IndexEnumerator;
import function.byref.ByRefChar;

/**
  * Implements a Random Access Container for Characters based on a
  * @see StringBuffer
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class CharColl
extends ARAContainer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Internal Store for the Characters	 */
	protected java.lang.StringBuffer string;
	
	/**Empty Constructor	 */
	public CharColl() { this(""); }
	
	/**Constructor for a String	 */
	public CharColl(final String arg) { string = new java.lang.StringBuffer(arg); }
	
	/**Constructor for any Object	 */
	public CharColl(final Object arg) { this(""); copyAt(arg); }
	
	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { return new CharColl(); }
	
	/**Increases the capacity of this Array, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	public synchronized int setCapacity(final int minCapacity)	{
		string.ensureCapacity(minCapacity); return string.capacity(); } // this; }
	
	/**Increases the capacity of this Array, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @return the minimum Capacity of the StringBuffer.	 */
	public synchronized int getCapacity() {
		return string.capacity(); } //
	
	/** Returns the current Length of the backing StringBuffer.
	 * @return the Number of Items in the Collection	 */
	public int getInt() { return string.length(); }
	
	/**Removes this Object from the Set	 */
	public Object removeAt(Object Item){throw new AbstractMethodError();}

	/**Adds this Object to the Set	 */
	public ISemiGroup addAt(final Object Item) { string.append(Item); return this; }
	
	/**Returns an Iterator of the components in this Container.
	 *
	 * @return  an Iterator of the components in this Container.
	 * @see	 Math.Iterator	 */
	public IIStreamIn Iterator() { return new CharIterator(this); }
	
	
	//////////////////////
	//	special Methods	//
	//////////////////////

	/**Returns the Item at the given Position in the Set, starting Counting from 0. */
	public Object getAt(final int Position) { return new ByRefChar(string.charAt(Position)); }
	
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
	 * @see		java.util.Array#size()	 */
	public synchronized Object removeAt(final int index) {
		Character Item = new Character(string.charAt(index));
		String tmp = string.toString();
		string.setLength(index);
		string.append(tmp.substring (index+1));
		return Item; }

	/**Replaces the component at the specified <code>index</code> of this
	 * Array to be the specified object. The previous component at that
	 * position is discarded.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * @param	  obj	 what the component is to be set to.
	 * @param	  index   the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()	 */
	public synchronized Object setAt(final int index, final Object obj) {
		Object Item = new ByRefChar(string.charAt(index));
		string.setCharAt(index, ((ByRefChar) obj).Value);
		return Item; }
	
	/**Inserts the specified object as a component in this Array at the
	 * specified <code>index</code>. Each component in this Array with
	 * an index greater or equal to the specified <code>index</code> is
	 * shifted upward to have an index one greater than the value it had
	 * previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than or equal to the current size of the Array.
	 *
	 * @param	  obj	 the component to insert.
	 * @param	  index   where to insert the new component.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()	 */
	public IndexEnumerator addAt(int index, Object obj) {
		string.insert(index, obj); return this; }

}

/** Index-based Iterator over the Characters of a {@link CharColl}.
  * @see IndexIterator for the
  * @see CharColl Container
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
class CharIterator
extends AIndexEnumerator {

	/**Local Reference to the CharColl iterated.	 */
	CharColl string;

	/**Initializing Constructor	 */
	CharIterator(final CharColl _this) { 
		super(_this); string = _this; }

	////////////////////////////////////////////////////////////////////////////////
	//	Interface StreamIn
	////////////////////////////////////////////////////////////////////////////////
	
	/** This Iterator always returns Items in Stack (LIFO) Order.
	 * @return the Order this Iterator returns the Items in. 	*/
	public byte getOrder() { return IPipe.ORDER_STACK; }

	/** Returns the current Length of the backing CharColl's StringBuffer.
	 * @return the Number of Items in the Collection	 */
	public int getInt() { return string.string.length(); }

	/**Returns the current Object.	 */
	public Object currItem() { //ByRefLong moreItems) {
//		if ((moreItems.Value = (string.string.length() - current)) > 0)
		if (string.string.length() > curr)
			return new ByRefChar(string.string.charAt((int) curr));
		return null; }

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
	 * @see		java.util.Array#size()	 */
	public synchronized Object removeAt(int index) {
		return string.removeAt(index); }

	/**Replaces the component at the specified <code>index</code> of this
	 * Array to be the specified object. The previous component at that
	 * position is discarded.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * @param	  obj	 what the component is to be set to.
	 * @param	  index   the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()	 */
	public synchronized Object setAt(int index, Object obj) {
		return string.setAt(index, obj); }

	/**Removes the current Object from the Container with this Iterator knowing it.
	 * The remaining Problem is other Iterators that concurrently work through this. */
	public Object removeCurr() {
		return string.removeAt(curr);}

	/**Returns the Item at the given Position in the Set, starting Counting from 0. 	*/
	public Object getAt(int Position) { return string.getAt((int) Position); }

	/**Inserts the specified object as a component in this Array at the
	 * specified <code>index</code>. Each component in this Array with
	 * an index greater or equal to the specified <code>index</code> is
	 * shifted upward to have an index one greater than the value it had
	 * previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than or equal to the current size of the Array.
	 *
	 * @param	  obj	 the component to insert.
	 * @param	  index   where to insert the new component.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()	 */
	public IndexEnumerator addAt(int index, Object obj) {
		string.addAt(index, obj); return this; }

////////////////////////////////////////////////////////////////////////////////
//  Optimizations
////////////////////////////////////////////////////////////////////////////////

	/** Computes the remaining Characters from the backing StringBuffer's Length.
	 * @return the minimum Number of Items after the current one,
	  * i.e. the Number of times you can call the nextItem() Method
	  */
	public long availAble() { return string.string.length() - curr; }

}
