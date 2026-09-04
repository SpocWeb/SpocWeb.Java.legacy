package streamIO.object.integer;

import graphs.KeyValuePair;

import java.util.Hashtable;
import java.util.Vector;

/**Fundamental DOM Element of an XML Tree
 * Has a Factory Method newElement() that is used to generate and initialize
 * new Elements of the same Class, so Subclasses like HTMLElement
 * can build the Tree with their own Elements.
 *
 * An XML Element is very similar to a Record Object in a ResultSet:
 * It's Attributes play the Role of the Fields.
 */
public class XMLElement {

/**Adds this Item to the given HashTable.
 * If an Item with this key already exists, a Vector is automatically created.  */
public static void	addItem(Hashtable table, Object Key, Object Item) {
	if (! table.containsKey(Key)) { table.put(Key, Item); return; }
	Object curr = table.get(Key);
	Vector vec;
	if (curr instanceof Vector) vec = (Vector) curr;
	else { vec = new Vector(); table.remove(Key); table.put(Key, vec); vec.addElement(curr); }
	vec.addElement(Item);
}

/**Factory Method used to generate and initialize
 * new Elements of the same Class, so Subclasses like HTMLElement
 * can build the Tree with their own Elements^	 */
public XMLElement newElement(String Name, XMLElement Parent) {
	return new XMLElement(Name, Parent); }

/**List of all Attributes, duplicate Attributes are put into Vectors	 */
protected Hashtable Attributes = new Hashtable();

/**Returns the List of all Elements,
 * duplicate Elements are put into Vectors	 */
public Hashtable	getAttributes() { return Attributes; }

/**Returns the Element with this key, null if not contained.
 * duplicate Elements are put into Vectors	 */
public KeyValuePair getAttribute(String Key) { return (KeyValuePair) Attributes.get(Key); }

/**Adds this Item to the HashTable,
 * if an Item with this key already exists, a Vector is automatically created.  */
public void	addAttribute(String Key, KeyValuePair Item) { addItem(Attributes, Key, Item); }

/**List of all Attributes, duplicate Attributes are put into Vectors	 */
protected Hashtable Elements = new Hashtable();

/**Returns the List of all Elements,
 * duplicate Elements are put into Vectors	 */
public Hashtable	getElements() { return Elements; }

/**Returns the Element with this key, null if not contained.
 * duplicate Elements are put into Vectors	 */
public Object		getElement(String Key) { return Elements.get(Key); }

/**Adds this Item to the HashTable,
 * if an Item with this key already exists, a Vector is automatically created.  */
public void			addElement(String Key, Object Item) { addItem(Elements, Key, Item); }

/**Name of this XMLElement	 */
protected String    Name;

/**Returns the Name of this XMLElement	 */
public    String getName() { return Name; }

/**Returns the Value of this XMLElement, i.e. the Element without a key	 */
public Object getValue() { return getElement(""); }

/**Parent of this XMLElement	 */
protected XMLElement Parent;

/**Returns the Parent of this XMLElement	 */
public XMLElement  getParent() { return Parent; }

/**Initializing Constructor	 */
public XMLElement(String Name, XMLElement Parent) {
	this.Name  = Name  ;
	this.Parent= Parent; }

}
