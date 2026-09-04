package graphs;

import streamIO.object.json.JSONTest;

//import Testers.ITester;
//import Stream.OperationNotSupported;

/**
  * Title: PairVal<p>
  * Description:
  * Lightweight Class to group two Objects.
  * This Pair Class assumes the Identity of its Value (instead of the Key).
  * The Distinction to the heavyweight @see Association Class is also good
  * for flattening Cross Products and Joins.
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
  */
final public class PairVal //'final' speeds up Methods that directly use PairVal
extends Pair
{
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor for (De-)Serialization	 */
	public PairVal() { }

	/** Constructor with the full Information for a Pair
	  * Pairs have an Identity
	  * @param key   The key for this Pair.
	  * @param value The Target Object of this Pair
	  */
	public PairVal(final Object _key, final Object _value) {
		super(_value, _key); }

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
	public boolean equals(final Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		if (arg instanceof PairVal) {
			return (val == ((PairVal) arg).   val  ); // && (arg_.   Key   == Key);
		} else if (arg instanceof ICPair) {
			return (val == (( ICPair) arg).getVal()); // && (arg_.getKey() == Key);
		} else return false; }

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
		if (val != null) HC  = val.hashCode();
//		if (Key != null) HC += Key.hashCode(); //make this conformant to the equals() Method
		return HC; }

	/** @return  A string representation of this Association.     */
	public synchronized String toString() { return "(" + val + "@" + Key + ")"; }

}
