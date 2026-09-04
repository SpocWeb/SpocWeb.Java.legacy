package graphs;

import streamIO.object.json.JSONTest;
import synch.ValidationRule;

//import Testers.ITester;
//import Stream.OperationNotSupported;

/**
  * Title: Pair<p>
  * Description:
  * Lightweight Class to group two Objects neither symmetrically nor asymmetrically.
  * (Asymmetry has the Disadvantage that (a-(b-(c-d)) == (c-(d-(a-b)),
  * whereas here the HashCode of the second Item is rotated,
  * so that it's Information is not lost!)
  *
  * This Pair Class assumes the Identity of both its Value and its key.
  * The Distinction to the heavyweight @see Association Class is also good
  * for flattening Cross Products and Joins.
  *
  * It is used also for State Machines, where the Input is a Joint Pair of
  * external Input and internal State. (see )
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
  * Usage: Pair is used in 
  * @see streamIO.Object.Byte.Scanner 
  * @see streamIO.Object.Product to implement Cross Products
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
public class Pair //final speeds up Methods that directly use Pair
extends Value //PairKey cannot be extended due to its constant Key!
implements IPair {

	////////////////////////////////////////////////////////////////////////////
	//  Variables (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** The key of this Pair */
	public Object Key;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	  * @return key */
	final public Object getKey() { return Key; }
	
	/** Accessor Method
	  * @param sets the key of the Pair */
	final public void setKey(final Object Key) { this.Key = Key; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor for (De-)Serialization	 */
	public Pair() { }
	
	/** Constructor with the full Information for a Pair
	  * Pairs have an Identity
	  * @param key   The key for this Pair.
	  * @param value The Target Object of this Pair
	  */
	public Pair(final Object _key, final Object Value) {
		super(Value); 
		this.Key = _key; }
	
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
		if (arg instanceof  KeyValuePair) {
			return equals((KeyValuePair) arg);
		} else if (arg instanceof ICPair) {
			return equals((ICPair) arg);
		} else 
			return false; 
	} 

	/**
	 * @param _arg
	 * @return
	 */
	private boolean equals(final ICPair _arg) {
		return 
		ValidationRule.EQUALS(val, _arg.getVal()) && 
		ValidationRule.EQUALS(Key, _arg.getKey());   
	}

	/**
	 * @param _arg
	 * @return
	 */
	private boolean equals(final KeyValuePair _arg) {
		return  //for Symmetry, also the reverse Combination has to be checked!
			ValidationRule.EQUALS(val, _arg.val) &&
			ValidationRule.EQUALS(Key, _arg.key);
	}

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
		boolean neg;
		int HC = 0; //ROL the next Items HashCode to not lose any Information!
		if (val != null) { HC  = val.hashCode(); neg = (HC < 0); HC <<= 1; if (neg) ++HC; } //ROL to break Symmetry! 
		if (Key != null) { HC ^= Key.hashCode(); } //make this conformant to the equals() Method
		return HC; }
	
	/** @return  A string representation of this Association.     */
	public synchronized String toString() { return "(" + val + "@" + Key + ")"; }
	
}
