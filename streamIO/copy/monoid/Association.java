package streamIO.copy.monoid;

import function.IFunction;
import function.IInvertAble;
import function.index.IndexEntry;
import graphs.ICPair;
import graphs.IPair;
import graphs.PairVal;

import java.io.Serializable;

import streamIO.copy.ICopyAble;
import streamIO.object.enumer.container.tree.TreeMapEntry;
import synch.ValidationRule;
import tester.Discrete;
import tester.ITester;
import tester.process.Operator;

/** Creates an Association between two Objects:
  * the key, which supplies the HashCode() and the equals() Method, 
  * as well as any Comparison or Metric Operations like compare()  
  * and the Value, which is returned. 
  * 
  * Associations have no Identity, they should not be reused between Containers.
  * Associations can be interpreted as Mappings and thus form a Monoid.
  *
  * For Simplicity and Speed, Associations are often flattened out 
  * into the Storage Structure Elements like LinkedList, HashEntry or TreeItem, 
  * so that only one indirection needs to be traversed 
  * and only one Object needs to be created (Speed for Memory). 
  * For simple Sets, the Key and the Value can then be kept identical. 
  * 
  * Design Decisions:
  * The Derivation from AMonoid is never used and creates a circular Dependency
  * between the Packages streamIO (defines and uses Association),
  * BaseCopy (uses XML...) and Monoid (derives absCopyAble)
  * The Code to implement the Interface is just very illustrative for e.g.
  * HashTable.Relation and other more complicated Mappings.
  *
  * The Notation (Value@key) has been chosen to support the Mapping Character:
  * (x@a)°(a@u)°u == x
  *
  * By defining {a,b,...} == {(null, a), (null, b), ...}, Mapping can seamlessly
  * be integrated into Set Theory. (a, null) is then equivalent to null
  * and any Mapping only maps the Values of the Associations to new Values.
  * It only defines explicitly that a is to be mapped to null and thus redundant.
  *
  * Association is used in Containers to form a Dictionary.
  * The derived Class 'Pair' defines a more partnership like Association.
  * Arbitrary higher Tupels (Tripel, n-Tupel) can be derived by nesting 'Pair'.
  * But rather use List.ListItem for this, because it saves the Casting.
  *
  * @author Matthias Heuer
  * 
  * Implemented Interfaces: 
  * @see streamIO.Object.IPair
  * @see streamIO.Object.Pair
  * @see streamIO.Object.Enumerator.ILinked
  * @see streamIO.Object.Enumerator.ListItem
  * @see streamIO.Copy.IMonoid.Pair
  * @see streamIO.Copy.IMonoid.Association
  *
  * for integer Linked Lists:
  * @see Discrete.SparseMatrix 
  * @see Discrete.IEquivalence 
  *
  * @see function.index.IndexEntry
  *
  * for Testers discerning between key Equivalence and Value Equivalence
  * @see AssociationEquivalence
  * @see streamIO.Object.Pair
  * @see Operator.ITester
  * 
  * flattable Structures: 
  * @see streamIO.object.enumer.container.HashEntry 
  * @see TreeMapEntry 
  * @see ListEntry 
  * @see IndexEntry 
  * 
  * non-flattened Structures (only Arrays): 
  * @see streamIO.object.enumer.container.SortedArray  
  */
public class Association 
extends AMonoid   //this creates a circular Dependency between Stream and BaseCopy!!!
//extends Pair    //this would enforce the Delegation of all Monoid Methods, too expensive!
implements Serializable, Cloneable, IPair, ITester { //ITester discerns "Value" too
	
	////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**key of this Association, used to find the associated Value	 */
	public Object key;	//Inverse

	/**Value of this Association, returned on finding the key	 */
	public Object val;	//Inverse, Map(Function)

	////////////////////////////////////////////////////////////////////////////
	//	Accessor Methods
	////////////////////////////////////////////////////////////////////////////

	/** Accessor Method
	  * @return key */
	public Object getKey() { return key; }

	/** Accessor Method
	  * @return Value */
	public Object getVal() { return val; }

	/** Accessor Method
	  * @param sets the key of the Pair */
	public void setKey(final Object _key) { this.key = _key; }

	/** Accessor Method
	  * @param sets Value of the Pair */
	public void setVal(final Object _val) { this.val = _val; }

	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////

	/**Empty Constructor for (De-)Serialization	 */
	public Association() { }

	/** Constructor for the Pseudo Parent Class Pair.	 */
	public Association(final PairVal p) {
		key = p.Key;
		val = p.val; }

	/** Constructor for the Pseudo Parent Interface IPair.	 */
	public Association(final ICPair p) {
		key = p.getKey();
		val = p.getVal(); }

	/** Constructor with the full Information for an Association
	 * @param _key   The key for this Association.
	 *              The key's hashCode is used for the whole Association
	 * @param value The Target Object of this Association
	 */
	public Association(final Object _key, final Object _val) { 
		key = _key; val = _val; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface Object: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns a hash code Value for the object.
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
	  * @return	a hash code Value for this object.
	  * @see	Object#equals(Object)
	  * @see	java.util.Hashtable
	  * @see	Association#hashCode2()
	  * @see	Association#Test()
	  * @see	Association#equals()
	  * @see	AssociationEquivalence
	  * @since	JDK1.0	 */
	public int hashCode(){
		if (key == null) 
			return 0; 
		return key.hashCode(); }

	/** Returns a hash code Value for the object.
	  * With the Association this alternative HashCode is the Sum
	  * of the key's and the Value's HashCode!
	  * This is redefined for the Association to be used to (recursively)
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
	  * @return	a hash code Value for this object.
	  * @see	Object#equals(Object)
	  * @see	java.util.Hashtable
	  * @see	Association#hashCode()
	  * @see	Association#Test()
	  * @see	Association#equals()
	  * @see	AssociationEquivalence
	  */
	public int hashCode2(){
		int hashCode2 = 0;
		if (key != null) hashCode2 = key.hashCode();
		if (val != null) hashCode2^= val.hashCode();
		return hashCode2; }

	/** Compares two Objects for equality.
	  * <p>
	  * The <code>equals</code> method implements an equivalence relation:
	  * <ul>
	  * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	  * <code>x.equals(x)</code> should return <code>true</code>.
	  * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	  * <code>y</code>, <code>x.equals(y)</code> should return
	  * <code>true</code> if and only if <code>y.equals(x)</code> returns
	  * <code>true</code>.
	  * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	  * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	  * returns  <code>true</code> and <code>y.equals(z)</code> returns
	  * <code>true</code>, then <code>x.equals(z)</code> should return
	  * <code>true</code>.
	  * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	  * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	  * consistently return <code>true</code> or consistently return
	  * <code>false</code>.
	  * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	  * should return <code>false</code>.
	  * </ul>
	  * <p>
	  * The equals method for class <code>Object</code> implements the most
	  * discriminating possible equivalence relation on objects; that is,
	  * for any reference values <code>x</code> and <code>y</code>, this
	  * method returns <code>true</code> if and only if <code>x</code> and
	  * <code>y</code> refer to the same object (<code>x==y</code> has the
	  * Value <code>true</code>).
	  * @return <code>true</code> if this object has the same key as arg
	  * argument; <code>false</code> otherwise.
	  * @see	Boolean#hashCode()
	  * @see	java.util.Hashtable#
	  * @see	Association#hashCode()
	  * @see	Association#Test()
	  * @see	Association#equals()
	  * @see	AssociationEquivalence
	  * @param	arg	The Object to be checked for Equality
	  */
	public boolean equals  (final Object arg) {
		if (arg == null) return (key == null);
		if (arg == this) return true;
		if (arg instanceof Association) {
			return  ValidationRule.EQUALS(key, ((Association) arg).key);
		} else if (arg instanceof ICPair) {
			return  ValidationRule.EQUALS(key, ((ICPair) arg).getKey());
		} return arg.equals(key)  || key.equals(arg ); } //although this would be more correct.
	//despite the Definition of the equals() Method, which should return false, if the Classes differ

	/** This is the Test working on 'arg' defined by the implementing Class.
	  * @return <code>true</code> if this object has the same key and Value as arg
	  * The Class or Instance implementing this Method is the means of exchanging this Operation.
	  * @see Association#equals(Object) which defines Equality only for the Key 
	  * @see Pair#equals(Object) which uses this Test for defining Equality.  
	  */
	public boolean test(final Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		if (arg instanceof Association) {
			return test((Association) arg);
		} else if (arg instanceof ICPair) {
			return test((ICPair) arg); 
		} else return false; }

	/**
	 * @param _arg
	 * @return
	 */
	private boolean test(final ICPair _arg) {
		return  
		ValidationRule.EQUALS(this.key, _arg.getKey()) &&
		ValidationRule.EQUALS(this.val, _arg.getVal());
	}

	/**
	 * @param _arg 
	 * @return <code>true</code> if the key and the Value match
	 */
	private boolean test(final Association _arg) {
		return  
		ValidationRule.EQUALS(_arg.key, key) &&
		ValidationRule.EQUALS(_arg.val, val);
	}

	/** @return  A string representation of this Association.
	  * The Notation (Value@key) has been chosen to support the Mapping Character:
	  * (x@a)°(a@u)°u == x
	  */
	public synchronized String toString() { return val + "@" + key; }

	//////////////////////////////
	//  Interface IInvertAble	//
	//////////////////////////////

	/** Returns a new Instance with the inverse Association:  !this
	 * @return the inverse Association
	 */
/*	public Monoid invert(){
//		if (Inverse != null)
//			Inverse = new Association(Value, Key); }
//		return Inverse; }

	/** Sets the Inverse Association:  !this	 */
	public void setInverse(IInvertAble Inverse_) { throw new AbstractMethodError(); }

	/** Returns the inverse Association in Place
	 * @return the inverse Association in Place == !this	 */
	public IInvertAble getInverse() { return (IInvertAble) rev(); }

	/** Returns the inverse Association in Place
	 * @return the inverse Association in Place == !this	 */
	public IMonoid revAt() {
//		Inverse = null; //clear the Reference to the Inverse!
		Object tmp = key; key = val; val = tmp;
		return this; }

	//////////////////////////////
	//  Interface IFunction   //
	//////////////////////////////

	/**Returns an alternative Representation that is 'simplified'	 */
	public IFunction simplify() { return this; }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative for the Algebra
	 * to delegate the Operation to the inner Function,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) { return (arg == key); }

	/**Mapping / Concatenation from the right in Place:  this=°arg <=> return Value(arg.Value)
	 * This virtual Operation has to be implemented by each subclass.	 */
	public ISemiMonoid mapAt(Object arg) {
		if (arg instanceof Association)
			return mapAt( (Association) arg);
		ISemiMonoid arg_ = (ISemiMonoid) arg;
		if ((arg   ==    key) ||
			(arg .equals(key))) {
			 arg_.copyAt(val); return arg_; }
		throw new AbstractMethodError(); }
//		return arg; }

	/**Mapping / Concatenation from the right in Place:  this=°arg <=> return Value(arg.Value)
	 * This virtual Operation has to be implemented by each subclass.	 */
	public Object MapAt(Object arg) {
		if (arg instanceof Association)
			return mapAt( (Association) arg);
		if ((arg   ==   key) ||
			(arg.equals(key))) {
			return ((ICopyAble) arg).copyAt(val); }
		throw new AbstractMethodError(); }

	/**Mapping / Concatenation from the right in Place:  this=°arg <=> return Value(arg.Value)
	 * This virtual Operation has to be implemented by each subclass.	 */
	public IMonoid pamAt(Object arg) {
//		if (arg instanceof Association)
			return unMapAt((Association) arg); }
//		throw new AbstractMethodError(); }

	/**Mapping / Concatenation from the right in Place:  this=°arg <=> return Value(arg.Value)
	 * This virtual Operation has to be implemented by each subclass.	 */
	public Object UnMapAt(Object arg) {
		if (arg instanceof Association)
			return unMapAt( (Association) arg);
		if ((arg   ==   val) ||
			(arg.equals(val))) {
			return ((ICopyAble) arg).copyAt(key); }
		throw new AbstractMethodError(); }
//		return arg; }

	//////////////////////////////
	//  Interface (Half)Monoid  //
	//////////////////////////////

	//////////////////////////
	//  Interface CopyAble  //
	//////////////////////////

	//  essential Implementations

	/**Mapping / Concatenation from the right in Place:  this=°arg <=> return Value(arg.Value)
	 * This virtual Operation has to be implemented by each subclass.	 */
	public IMonoid pam(Object arg) {
//		if (arg instanceof Association)
			return pam( (Association) arg); }
//		return null; }

	/**Mapping / Concatenation from the right in Place:  this=°arg <=> return Value(arg.Value)
	 * This virtual Operation has to be implemented by each subclass.	 */
	public Object UnMap(Object arg) {
		if (arg instanceof Association)
			return pam((Association) arg);
		if ((arg   ==   val) ||
			(arg.equals(val))) {
			return key; }
		return null; }

	//  additional Implementations

	/**Mapping / Concatenation from the right in Place:  this=°arg <=> arg.Value = Value(arg.Value)
	 * <=> arg.Value(arg.key) := Value(arg.key)
	 * This virtual Operation has to be implemented by each subclass.	 */
	public Association mapAt(Association arg_) {
 		if ((arg_.val   ==   key) ||
			(arg_.val.equals(key))) 
			 arg_.val = val;
		else arg_.val = null;
		return arg_; }

	/**Concatenation with the Inverse in Place: !this=°arg
	 * <=> arg.Val(arg.key) := key(arg.Val)
	 * This is the Inverse Operation to cat(), not to map()!
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
	public Association unMapAt (Association arg) {
//	  return invert().mapAt(arg);
		if ((arg.val   ==   val) ||
			(arg.val.equals(val)))
			 arg.val = key;
		else arg.val = null;
		return arg; }

	/**Left-Concatenation with the Inverse: this°!arg  <=>  this.key = arg.Value(this.key)
	 * Resolves the Equation A°B = C = A.map(B) for A:
	 * A = C°!B = C.map(B.invert()) = C.solve(B) = B.reSolve(C) = A.map(B).solve(B)
	 * Requires arg to be a Monoid!
	 */
	public Association solveAt(final Association arg) {
		if ((key   ==   arg.key) ||
			(key.equals(arg.key))) {
			 key =      arg.val; }
		else key = null;
		return this; }

	/**Left-Concatenation with the Inverse: this°!arg  <=>  (arg.Value(key), Value)
	 * This is the Inverse Operation to map(), not to cat()!	 */
	public Association solve(Association arg) {
		if ((key   ==   arg.key) ||  //MUST be a Mapping!
			(key.equals(arg.key))) {
			return new Association(arg.val, val); }
		return null; }

	/**Left-Concatenation with the Inverse: arg°!this <=> (Value, arg.Value(key))
	 * Resolves the Equation A°B = C = A.map(B) for A:
	 * A = C°!B = C.map(B.invert()) = C.solve(B) = B.reSolve(C) = A.map(B).solve(B)
	 * Requires arg to be a Mapping and returns one!
	 */
	public Association reSolve(Association arg) {
		return (Association) arg.map(((IMonoid) self).rev()); }

	/**Left-Concatenation with the Inverse: this°!arg  <=>  (arg.Value(key), Value)
	 * Resolves the Equation A°B = C = A.map(B) for A:
	 * A = C°!B = C.map(B.invert()) = C.solve(B) = B.reSolve(C) = A.map(B).solve(B)
	 * Requires arg to be a Monoid!
	 */
	public IMonoid solve(Object arg) {
		if (arg instanceof Association)
			return solve((Association) arg); //MUST be a Mapping
		return null; }

	/**Mapping from the Left :  arg°
	 * mapAt() uses shallowCopyAt(map(arg))
	 */
	public ISemiMonoid map  (Object arg) {
//		if (arg instanceof Association)
			return map  ( (Association) arg); }
//		return null; }

	/**Mapping from the Left :  arg°
	 * mapAt() uses shallowCopyAt(map(arg))
	 */
	public Object Map  (Object arg) {
		if (arg instanceof Association)
			return map  ( (Association) arg);
		if ((arg   ==   key) ||
			(arg.equals(key))) {
			return val; }
		return null; }

	/**Mapping / Concatenation from the right in Place:  °=arg
	 * Not really necessary to implemented this by each subclass.	 */
	public Association map(final Association arg_) {
		return new Association(arg_.key, map(arg_.val)); }

	/**Mapping / Concatenation from the right in Place:  °=arg
	 * This virtual Operation has to be implemented by each subclass.	 */
/*	public SemiMonoid catAt(Object arg) {
		Association arg_ = (Association) arg;
		if ((Value   ==   arg_.Key) ||
			(Value.equals(arg_.Key))) Value = arg_.Value;
		else Value = null;
		return this; }

	/**Concatenation with the Inverse in Place: °=!arg
	 * This is the Inverse Operation to cat(), not to map()!
	 * This virtual Operation has to be implemented by each concrete Subclass.		 */
/*	public Monoid unCatAt (Object arg) {
		Association arg_ = (Association) arg;
		if ((Value   ==   arg_.Value) ||
			(Value.equals(arg_.Value))) Value = arg_.Key;
		else Value = null;
		return this; }

	/**Creates a Copy of the given Depth from 'arg'	 */
	public ICopyAble copyAt(final Object arg, final int Depth) {
		if (Depth == 0) {
			return this; }
		Association arg_ = (Association) arg;
		key = arg_.key; val = arg_.val;
		return this; }

	///////////////////////////////////////////////////////////////////////////
	
	/**Tests all Methods of this Class	 */
	public static void testIt() {
		System.out.println("Testing Association:");
		Association A1 = new Association("Hund", "Tier");
		Association A2 = new Association("Matthias", "Person");
		A1.revAt(); System.out.println(A1);
		A2.revAt(); System.out.println(A2);

	}

}
