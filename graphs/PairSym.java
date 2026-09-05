package graphs;

import streamIO.object.json.JSONTest;

//import Testers.ITester;
//import Stream.OperationNotSupported;

/**
  * Title: PairSym<p>
  * Description:
  * Lightweight Class to symmetrically group two Objects, i.e. (A,B) == (B,A).
  * This Pair Class assumes the Identity of both its Value and its key symmetrically.
  * The Distinction to the heavyweight @see Association Class is also good
  * for flattening Cross Products and Joins.
  *
  * The Symmetry is reflected both in the HashCode, which is an XOR Combination
  * as in the equals() Function that returns true for both (key, Val) as (Val, key).
  * Alternatively a standard Sorting could be introduced
  * which places the Item with the lower HashCode into the key.
  * When adding an Object, would have to propagate along the List
  * up to its correct Position. (Bubble Sort)
  *
  * Tripels and higher n-Tupels can be built up recursively by nesting Pairs,
  * but you should better use ListItem for that.
  * ListItem has a Conversion Routine for Pairs to create a List from nested Pairs.
  *
  * Normally a Parent Class to Association,
  * but this would require the Re-Implementation of all Methods of Monoid etc.
  * Being a Child Class to Association would also create a circular Dependency:
  * Pair -> Association -> Monoid -> CopyAble -> StreamXML -> Pair
  *
  * Stateful ITester Implementation
  * that tests incoming Objects for exact Equivalence to the inner key and Value
  * of the Association.
  * Works with Pairs and IPairs (thus also with Associations)
  *
  * Thus only a Constructor of Association is based on Pair
  * Pair is used in streamIO.Object.Byte.Scanner and in streamIO.Object.Product
  * Known SubClasses:
  * (none)
  *
  * similar Classes:
  * @see streamIO.Object.IPair
  * @see streamIO.Object.Pair
  * @see streamIO.Object.Enumerator.ILinked
  * @see streamIO.Object.Enumerator.ListItem
  * @see streamIO.Copy.IMonoid.Pair
  * @see streamIO.Copy.IMonoid.Association
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on 06-02-2001, 11:02 PM<p>
  * @author 	Matthias Heuer
  * @version 1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: 9e240d9a96ff78b490f626e83994c27f782bee5d1798f9d0882dc78bca1d209f
  * stale: false
  * tags: [code/pair_data_structure]
  * concepts: [Symmetric (Unordered) Pair]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
final public class PairSym //final speeds up Methods that directly use Pair
extends KeyValuePair {

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor for (De-)Serialization	 */
	//public PairSym() { }

	/** Constructor with the full Information for a Pair
	  * Pairs have an Identity
	  * @param key   The key for this Pair.
	  * @param value The Target Object of this Pair
	  */
	public PairSym(final Object Key, final Object Value) {
		super(Value, Key); }

	////////////////////////////////////////////////////////////////////////////////
	//	new Implementations of the basic Object Methods for Use in TreeNode
	//	analogous to Association
	////////////////////////////////////////////////////////////////////////////////
	
	/** Compares only the Value of this object to the specified object.
	  * conformant to hashCode() analogous to Association
	  * @see JSONTest
	  *
	  * @param obj	the object to compare with
	  * @return 		true if the objects are equivalent; false otherwise.
	  * @since   JDK1.1	 */
	public boolean equals(Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		if (arg instanceof  PairSym) {
			PairSym arg_ = (PairSym) arg;
			return ( //TODO: for Symmetry, also the reverse Combination should be checked!
				(val   ==   arg_.val) ||
				(val.equals(arg_.val)) ) && (
				(key   ==   arg_.key) ||
				(key.equals(arg_.key)) );
		} else if (arg instanceof ICPair) {
			ICPair arg_ = (ICPair) arg;
			Object Val_ = arg_.getVal();
			Object Key_ = arg_.getKey();
			return (
				(val   ==   Val_) ||
				(val.equals(Val_)) ) && (
				(key   ==   Key_) ||
				(key.equals(Key_)) );
		} else { return false; } }

	/** Returns a hash code Value for the object
	  * conformant to equals() analogous to Association.
	  * With the Association the HashCode is exactly the key's HashCode!
	  * This has to be redefined if the Association is used to (recursively)
	  * cluster two Arguments which is done in String.DynTransByFunction.
	  *
	  * This method is supported for the benefit of hashtables
	  * such as those provided by <code>java.util.Hashtable</code>.
	  * <p>
	  * The general contract of <code>hashCode</code> is:
	  * <ul>
	  * <li>Whenever it is invoked on the same object more than once during
	  * an execution of a Java application, the <code>hashCode</code> method
	  * must consistently return the same integer. This integer need not
	  * remain consistent from one execution of an application to another
	  * execution of the same application.
	  * <li>If two objects are equal according to the <code>equals</code>
	  * method, then calling the <code>hashCode</code> method on each of the
	  * two objects must produce the same integer result.
	  * </ul>
	  *
	  * @return  a hash code Value for this object.
	  * @see     Object#equals(Object)
	  * @see     java.util.Hashtable
	  * @since   JDK1.0	 */
	public int hashCode() {
		int HC = 0;
		if (val != null) { HC  = val.hashCode(); }
		if (key != null) { HC ^= key.hashCode(); } //make this conformant to the equals() Method
		return HC; }

	/** Returns this Association rendered as "(val@key)".
	 * @return  A string representation of this Association.     */
	public synchronized String toString() { return "(" + val + "@" + key + ")"; }

}
