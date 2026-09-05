package streamIO.object.enumer.container;

import streamIO.object.enumer.IndexEnumerator;

/** Random Access Container: extends the Container Interface by Methods
  * to access, replace and insert Items by a certain Position.
  * This extends the Interface Relation which has the same Methods and allows to
  * access Elements by integer Object Keys
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public interface RAContainer
extends Container, IndexEnumerator {

	/**Inserts the specified object as a component in this Array at the
	 * specified <code>index</code>. Each component in this Array with
	 * an index greater or equal to the specified <code>index</code> is
	 * shifted upward to have an index one greater than the value it had
	 * previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than or equal to the current size of the Array.
	 *
	 * @param	  Item	 the component to insert.
	 * @param	  index   where to insert the new component.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 * does not throw the ModificationException!
	 */
	public IndexEnumerator addAt(int index, Object Item);

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
	 * does not throw the ModificationException!
	 */
	public Object removeAt(int index);

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
//	public RAContainer subAt(int index);

	//the following Methods assume a connex Order on the Keys, i.e. int

}
