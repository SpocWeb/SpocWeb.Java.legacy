package streamIO.object.enumer.container;

import streamIO.IIStreamIn;
import streamIO.IReSetAble;
import streamIO.object.IStreamSet;
import streamIO.object.ModificationException;
import streamIO.object.enumer.Enumerator;
import tester.IEquivalence;
import tester.ITester;
import function.ICountAble;
import graphs.KeyValuePair;

/**
  * Title: Container.java<p>
  * Description:
  * Defines the Interface for a Container.
  * A Container can store (a finite Number of) Object References.
  * It implements the Enumerator Interface to return a (finite) streamIO of it's Contents
  * which is the usualy way deterministic Machines process Data
  * and thus sufficient for all Algorithms of O(N) that need to access all Elements.
  * For other or more sophisticated Operations you have to use the Container directly
  * (e.g. O(log N) searching)
  * Most advanced Enumerator Operations make only sense in conjunction with a Container
  * like Order() and modifying Operations: add...(), remove...(), replace...()
  *
  * There is no Difference between a Container and a Set.
  * The Methods used determine the Contents and the Behavior:
  *    add() and    addItem() will add all Items
  *  union() and  unionItem() will check whether these exist before adding them
  * remove() and removeItem() will remove the Items
  *   subt() and     subAt() will throw Errors if the Items don't exist.
  * this is supported by any Container, but most Containers don't support a fast
  * O(1) findFirst() Operation, only an O(N) and thus have a very slow union() Method.
  *
  * A non-negative Number is the Equivalence Relation between all Containers with this Number of Items in it.
  * Negative Numbers cannot exist and not be modeled by a simple Container,
  * but, similar to a Fraction, using a second Negative Container.
  *
  * Multiplication is defined by creating the Cross Product, creating Pairs.
  * Division resolves the Cross Product to return the simple Elements
  * by applying an Equivalence Relation that e.g. considers only the key of the Association
  * Usually it doesn't makes sense to divide a Set by another Set,
  * except if the first Set is a Cross Product.
  * It is more usual to apply an Equivalence Relation to a Set
  * @see tester.IEquivalence to receive a smaller Size Set
  * this can also be interpreted as a Mapping
  * that leaves only the Keys or the Values of a Pair,
  * but that actually doesn't need the Divisor, neither from the Left, nor from the right.
  *
  * The same Set Operations can be performed for (infinite) Streams using Class StreamSet,
  * but the Operations remain implicit (potential) until finally evaluated.
  * Containers always support the reset() and clear() Operations.
  * Containers act like positive integer Numbers if you abstract from the Identity of the Constituents.
  * Thus positive Numbers are the fundamental Concept of Human Thinking.
  * 	This Abstraction is supported by external Functions:
  * 	"Equivalence", HashCode and "Orderator" resp. "Comparator"
  * 	These define Equivalence (and Order) Relations based on the current Context.
  * 	Thus the same Set can be partitioned based on different Criteria:
  * 	Color, Shape etc. This is basic Set Theory made real.
  * 	Fixed HashCode() and equals() Functions apply only when the Objects are used
  * 	in a certain restricted Context, like an Application!
  * Negative Numbers exist as a Potential to reduce positive Numbers.
  * Rational Numbers exist as a Potential to multiply large positive Numbers etc.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  * @see AContainer
  * @see HashContainer
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-12, 01;50;13<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @stereotype container
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public interface Container
extends IStreamSet, IContainer, ICountAble, Enumerator, ChangeEventSource { //ICountAble, Enumerator, ChangeEventSource { //

	/**Resets the Iterator to the last marked Position,
	 * done automatically on Instantiation
	 * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet(); // throws NoSuchMethodException;

	/**
	 * Computes the Potency (power) Set of this Container's Elements.
	 * @return an Array of Containers containing the Potency Set of the Containers
	 * The Number of Elements in the Array is N! = N*(N-1)*...*2*1
	 */
	public Container[] Potency ();

	/**
	 * Raises this Container to the given Power, i.e. all Mappings from C's Elements to this
	 * Container's Elements.
	 * @return a Container containing this Container raised to the Power C
	 * This corresponds to the Set of all Mappings of of the Elements of C
	 * to the Elements of this Container: C => this
	 * The Number of Elements in the Array is |this|^|C| = N*N*...*N
	 * because for every Element of C there are N possible Mappings from this Container.
	 */
	public Container pow(Container C);

	/** Constant denoting MultiSet Behavior on set(),
	  * i.e. the same Item can appear multiple Times 	*/
	final static public int IF_EXISTS_ADD = 0;

	/** Constant denoting Set Behavior on set(),
	 * just returning Info whether it is contained 	*/
	final static public int IF_EXISTS_UNION = 1;

	/** Constant denoting replace Behavior on set() 	*/
	final static public int IF_EXISTS_REPLACE = 2;

	/** Constant denoting remove Behavior on set() 	*/
	final static public int IF_EXISTS_REMOVE = 3;

	/** Constant denoting flip Behavior on set() 	*/
	final static public int IF_EXISTS_FLIP = 4;

	////////////////////////////////////////////////////////////////////////////////
	//  public Set Methods these could also be added to the Boole Interface!
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the Reference to the HashCode and Equivalence Function
	  * used to determine the Equivalence of Elements and the HashCode (if not null)
	  */
	public IEquivalence getHashCode();

	/** Division of the Container Elements by the given Equivalence Relation
	  * which compares Objects for Equality
	  * This is more common than a Division of a Product by first or second Factor,
	  * which can be done anyway by the following Methods:
	  * @see getKeys
	  */
	public Container div(IEquivalence EQ);

	/** Clears this Container, i.e. removes all Items from it.
	  * This is the same Operation as FalseAt() and zeroAt() and thus omitted	 */
	//public Container clear();

	/** Returns the number of elements in this collection.
	  * This is the same Operation as getInt()
	  * It is not possible that this collection contains more than Integer.MAX_VALUE
	  * Elements on a 32 Bit Machine. If it still does, it returns Integer.MAX_VALUE 	 */
	//public int size();

	/** Increases the capacity of this Array, if necessary, to ensure
	  * that it can hold at least the number of components specified by
	  * the minimum capacity argument.
	  *
	  * @param   minCapacity   the desired minimum Capacity.
	  * @return  the actual Capacity allocated for this Container */
	int setCapacity(int minCapacity);

	/** Returns the current minimum capacity of this Array.
	  *
	  * @return  the current capacity of this Array.	 */
	int getCapacity();

	/** Sets, adds, replaces or flips the Item if it already existed,
	  * as specified by the second Parameter
	  * @return the Item replaced or removed.	 */
	Object set(Object Item, int ifExists);

	/** @return true when this Object is contained in this Container
	  * This is the same Operation as (findFirst() != EOI) || (available() >= 0)
	  * @see Sub() and SubEq() for the according Container Methods,
	  * The Name contains() is only to be used for single Elements
	  */
	//boolean contains(Object Item); //already defined in StreamIn or Pipe

	/** Adds the Items to this Container in Place.
	  * Already defined in StreamOut	 */
	//public Container addItems(IStreamIn iter);

	/** Adds the Items with Cols.a[i] == true to this Container in Place. 	 */
	public Container addItems(IIStreamIn iter, boolean[] Cols);

	/** Adds the Items with the Bits Cols[i] == 1 to this Container in Place. 	 */
	public Container addItems(IIStreamIn iter, int Cols);

	/** Adds the Items with Cols.a[i] == true to this Container in Place. 	 */
	public Container addItems(Container iter, boolean[] Cols);

	/** Adds the Items with the Bits Cols[i] == 1 to this Container in Place. 	 */
	public Container addItems(Container iter, int Cols);

	/** Adds the Items to this Container in Place. 	 */
	public Container addItems(Container iter);

	/** Replaces Item with Item2 in this Container
	  * @return the Container Item, if found, otherwise 'null' resp 'EOI'	 */
	Object replaceItem(Object Item, Object Item2);

	/** Replaces the Items from Item with the ones from Item2 in this Container
	  * @return true, if found any, i.e. Container is changed, otherwise false	 */
	boolean replace(Object Item, Object Item2);

	/** Removes this Item from the Container
	  * Corresponds to subAt(), but retained, because it also returns Information
	  * whether the Container was changed
	  * moved to Interface IContainer, because used by Registries etc.
	  * @return the Item, if found, otherwise 'null' resp 'EOI'	 */
//	Object removeItem(Object Item) throws ModificationException;

	/** Removes these Items from the Container
	  * Corresponds to subAt() and DIFFAt(), but retained
	  * because it also returns Information whether the Container was changed
	  * @return true, if found any, i.e. Container is changed, otherwise false	 */
	boolean remove(Object Item) throws ModificationException;

	/** Unites this Item (the set consisting of only this Item) with the Container
	  * Similar to addAt(), this is used for Sets, which don't accept duplicate Objects
	  * @see addAt()
	  * @see  ORat()
	  * @see setItem()
	  * @return true, when the Item was added	 */
	boolean unionItem(Object Item);

	/** Adds these Items to the Container
	  * Corresponds to ORAt(), but retained, because it also returns Information
	  * whether the Container was changed.
	  * This is only important for Sets, which don't accept duplicate Objects
	  * @return true, if found any, i.e. Container is changed, otherwise false	 */
	boolean union(Object Item);

	/** Unites the Items of the streamIO or Container to this Container
	  * Corresponds to ORAt(), but retained, because it also returns Information
	  * whether the Container was changed.
	  * This is only important for Sets, which don't accept duplicate Objects
	  * @param EQ stricter Equivalence Relation than equals() used to test the Items
	  * @see addAt()
	  * @see  ORat()
	  * @return true, if found any, i.e. Container is changed, otherwise false	 */
	public boolean union(Object Item, IEquivalence EQ);

	/** Adds or replaces the given <code>Item</code> by the one specified.
	  * The Item can not be <code>null</code>.
	  * <p>
	  *
	  * @param	  Item	 the HashContainer Item.
	  * @return	 the Item replaced in this HashContainer,
	  *			 or <code>null</code> if it did not exist before.
	  * @exception  NullPointerException  if the Item or Item is
	  *			   <code>null</code>.
	  * @see	 java.lang.Object#equals(java.lang.Object)
	  * This Operation is optimized in this Class,
	  * because 'iter' still points to the correct Location */
	public Object setItem(Object Item);

	/** Adds or removes this Item from the Container
	  * Corresponds to XORat(), but retained, because it also returns Information
	  * whether the Container was changed
	  * @return the Item, if found, otherwise 'null' resp 'EOI'	 */
	Object flipItem(Object Item);

	/** Adds or removes these Items from the Container
	  * Corresponds to XORat(), but retained, because it also returns Information
	  * whether the Container was changed
	  * @return the Item, if found, otherwise 'null' resp 'EOI'	 */
	boolean flip(Object Item);

	/** Removes all Objects from the Container except for this one.
	  * Corresponds to ANDat(), but retained, because it also returns Information
	  * whether the Container was changed.
	  * Doesn't make much sense, because it returns only empty or single Element Sets.
	  * @return the Item found in the Container, otherwise EOI	 */
	boolean retainItem(Object Item);

	/** Removes all Objects from the Container except for the ones from this streamIO.
	  * Corresponds to ANDat(), but retained, because it also returns Information
	  * whether the Container was changed
	  * @return true, if the Container is changed, otherwise false	 */
	boolean retain(Object Item);

	////////////////////////////////////////////////////////////////////////////////
	//  Division from the Right and from the Left
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the Keys of the Elements in this Container
	  * no matter whether it contains Associations, Pairs or IPairs
	  * which is useful to determine the left Factor of a Product
	  * or the Definition Set of a Relation or Function */
	public Container getKeys();

	/** Returns the Values of the Elements in this Container
	  * no matter whether it contains Associations, Pairs or IPairs
	  * which is useful to determine the right Factor of a Product
	  * or the Value Set of a Relation or Function */
	public Container getValues();

	////////////////////////////////////////////////////////////////////////////////
	//	Column Operations
	//	These are Optimizations, also possible using Stream Filters,
	//  but these incur a Call Overhead AND Copying!
	////////////////////////////////////////////////////////////////////////////////

	/** In all Rows of this Container,
	  * Filters the Columns by the given Permutation	*/
	public Container filterAllCols(boolean[] Cols);

	/** In all Rows of this Container,
	  * Filters the Columns by the given Permutation	*/
	public Container filterAllCols(long Cols);

	/** Removes the Items with Cols.a[i] == false from this Container in Place. 	 */
	public Container filterCols(boolean[] Cols);

	/** Removes the Items with Bit i set from this Container in Place. 	 */
	public Container filterCols(long Cols);

	////////////////////////////////////////////////////////////////////////////////
	//	Row Operations:
	//	These are Optimizations, also possible using Stream Filters,
	//  but these incur a Call Overhead AND Copying!
	////////////////////////////////////////////////////////////////////////////////

	/** Filters the Rows by the given Filter Function, that tests each Row
	  * This is typically used for Rows, because their Number is not limited
	  * and they are all alike, so the same ITester can be applied.
	  *
	  * For filtering(Projecting) Columns, use filterCols
	  *
	  * Streams can be filtered using pluggable Filters.
	  * @see streamIO.Object.FilterByTester and
	  * @see streamIO.Object.FilterOutByTester
	  * They are not used here, because they incur a single Call Overhead
	  * AND involve copying the Container, which is modified in Place here!  */
	public Container filterRows(ITester RowFilter);

	/** Flattens this Container filled with Pairs (e.g. from a Multiplication or Join),
	  * i.e. stores the key and the Value in one (new) Container.
	  * @return a new Container filled with Containers of this Type containing the flattened Pairs contained in this one.
	  * Flattening leads to a cross Product with no clear distinction
	  * between the Elements of the first and second Set.
	  * This makes sense only for Containers with an Order, like Arrays or Lists,
	  * or if the Items have an internal Order like Associations do.	 */
	public Container flatten();

	/** Recursively flattens a Pair into this Container,
	  * i.e. stores the key and the Value in this Container next to each other.
	  * @return this Container after filling in the Items of the Pair.
	  * @param Item is split up recursively and the Items are added to this Container
	  * Flattening leads to a cross Product with no clear distinction
	  * between the Elements of the first and second Set.
	  * This makes sense only for Containers with an Order, like Arrays or Lists,
	  * or if the Items have an internal Order like Associations do.	 */
	public Container flattenItem(KeyValuePair Item);

}
