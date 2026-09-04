package graphs;

import java.sql.ResultSet;
import java.sql.SQLException;

import streamIO.exception.OperationNotSupported;
import tester.ITester;
//import javax.naming.OperationNotSupportedException;

/**
  * Title: PairKey<p>
  * Description:
  * This Pair Class assumes the Identity of its key only.
  *
  * Pairs (binary Associations) are a fundamental Construct,
  * because they allow for building all higher Associations and Relations.
  * LISP relies on this and represents ALL Structures as linked Lists and Trees.
  *
  * The Methods of this Class impose the same Meaning to key and Value as in
  * @see streamIO.Copy.IMonoid.Association
  * Returning only the Value in toString() allows to keep both independent
  *
  * Subclasses:
  * @see graphs.Pair which changes only the Implementation of equals(), hashCode()
  * and toString() to consider both key and Value.
  *
  * similar Classes:
  * @see streamIO.Object.IPair
  * @see streamIO.Object.Pair
  * @see streamIO.Object.Enumerator.ILinked
  * @see streamIO.Object.Enumerator.ListItem
  * @see streamIO.Copy.IMonoid.Pair
  * @see streamIO.Copy.IMonoid.Association
  *
  * Subclasses
  * @see Pair
  */
public class PairKey
extends Value
implements ICPair, IValue, ICopy, ITester { //, IPair {
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** The key of this Pair */
	final public Object key; //make it final! 
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	  * @return key */
	final public Object getKey() { return key; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor for (De-)Serialization	 */
	public PairKey(final Object _key) {  
		this.key = _key; }
	
	/** Constructor with the full Information for a Pair
	  * Pairs have an Identity
	  * @param key   The key for this Pair.
	  * @param value The Target Object of this Pair
	  */
	public PairKey(final Object _key, final Object _value) {
		this.val = _value;
		this.key = _key; }
	
	/** reads all available Data from the ResultSet	 */
	public PairKey(final ResultSet rs, final int[] cols) throws SQLException {
		this.key = rs.getObject(cols[0]); 
		this.val = rs.getObject(cols[1]); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
    /** @return A shallow Copy of this Object
	  * The Default Implementation is to delegate to the clone() Method.
	  * The same is done in the Methods Iterator() etc. of most Container Iterators */
	public ICopy Copy() {
		try { return (ICopy) clone();
		} catch (final CloneNotSupportedException x) { 
			throw new OperationNotSupported(x.toString()); 
		}
	}

	//  Interface ITester
	
	/** This is the Test working on 'arg' defined by the implementing Class.
	  * The Class or (in this Case) Instance implementing this Method
	  * is the means of exchanging this Operation.
	  * @return true when arg equals this Object in both key and Value
	  * as opposed to only the key, as needed for the Relation,
	  * @see equals() which tests only for the key Equality.
	  * This is an Alternative to using an Equivalence with a second Operand,
	  * but blocks this Class for the ITester Interface for any different use.
	  * @see Relation where this is used to find exact Matches without a Helper ITester	 */
	final public boolean test(final Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		if (arg instanceof PairVal) {
			PairVal arg_ = (PairVal) arg;
			return
				(arg_.val == val) &&
				(arg_.Key == key);
		} else if (arg instanceof ICPair) {
			ICPair arg_ = (ICPair) arg;
			return
				(arg_.getVal() == val) &&
				(arg_.getKey() == key);
		} else return false; }

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
			return (((PairVal) arg).Key == key); //Value == Value); //
		} else if (arg instanceof ICPair) {
			return (((ICPair) arg).getKey() == key); //getValue() == Value); //
		} else return false; }

	/** Returns a hash code Value for this object, in fact the key's hashCode.
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
		if (key != null) { HC  = key.hashCode(); } //make this conformant to the equals() Method
//		if (Val != null) { HC += Val.hashCode(); }
		return HC; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Object Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @see java.lang.Object#toString()	 */
	public String toString() { 
		return String.valueOf(key)+"->"+String.valueOf(val); 
	}
	
}
