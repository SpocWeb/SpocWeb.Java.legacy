package function;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
  * Representation of a Function by a HashMap
  * This is used e.g. for representing the State Transition Function of an Automaton:
  * a[x][q] -> q
  *
  * Thus it is prematurely defined to avoid the Overhead of "Relation"
  *
  * It is also a Relation and can be used as a Registry for Objects!
  * It can be used to keep track centrally of other Singletons that register with it.
  * It can also be used to maintain a List of Prototype Instances.
  * It's applicability is demonstrated in the Naming and Lookup Services (JNDI)
  * provided by Enterprise Applications.
  *
  * The Registry is a universal Class that can be used anywhere
  * to separate Creation and Use of Objects as well as to separate
  * concrete Types from abstract Types.
  *
  * @see FactoryRegistry for a typesafe Registry for IFactory Objects
  * @see structure.Registry for an Interface
  *
  */
public class FunctionByHash
extends AFunction
implements IDynamicFunction {

	/** Empty Constructor */
	public FunctionByHash() { f = new HashMap(); } 

	/** Constructor */
	public FunctionByHash(int size) { f = new HashMap(size); } 

	/** Constructor */
	public FunctionByHash(Map map) { f = new HashMap(map); } 

	/**
	  * Constructor taking several Objects and the resulting Mappings.
	  * It is e.g. used to take the Productions of the AchterProblem and its Solution
	  */
	public FunctionByHash(Object[][] keyVals) {
		f = new HashMap(keyVals.length); 
		setAt(keyVals, 0, 1); }
		
	/**
	  * Constructor taking several Objects and the resulting Mappings.
	  * It is e.g. used to take the Productions of the AchterProblem and its Solution
	  */
	public FunctionByHash(Object[][] keyVals, int keyIndex, int valIndex) {
		f = new HashMap(keyVals.length); 
		setAt(keyVals, keyIndex, valIndex); }

	/**
	  * Constructor taking several Objects and the resulting Mappings.
	  * It is e.g. used to take the Productions of the AchterProblem and its Solution
	  */
	public FunctionByHash(Object[]Starts, Object[]Productions) {
		f = new HashMap(Starts.length); 
		setAt(Starts, Productions); }

////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////

	/**
	  * Single global Registry Instance!!!
	  * Can be used to maintain References within the this same Process!
	  * For a Cross Process Exchange of Objects use the independent RmiRegistry.
	  * Objects stored there must be remoteable, i.e. serializeable.
	  *
	  * The Registry is a universal Object that can be used anywhere
	  * to separate Creation and Use of Objects as well as
	  * to separate concrete Types from abstract Types.
	  *
	  * A possible Problem is:
	  * - cluttering the Namespace
	  * - possible mispronouncing the Names / Keys
	  * - loss of Control due to the global Access to all Instances in the Registry
	  * Missing Encapsulation can be countered by introducing
	  * several isolated Registries only known to the interested Objects.
	  * This is necessary, because the possible Interactions increase with N!=N*exp(N)
	  * Sharing Data between these Entities is not easy, but sometimes necessary.
	  * For this, Helper Methods are given here.
	  */
	final static public FunctionByHash Registry = new FunctionByHash();

	//Structure.Registry r;

////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////

	/** Flag to determine whether Keys not defined map to null or to themselves */
	public boolean identity;

	/** local Reference to the HashTable Function
	  * What is the Advantage of double Hashing?
	  * None: it takes double as long and requires to create new HashTables.
	  * Only when the Function actually partitions,
	  * the Mapping Set is much smaller:  	 */
	protected HashMap f;

////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////

	/** add a new Operation to the Function.
	  * Returns the previous Mapping, when there was one, otherwise null. */
	public Object setAt(Object key, Object Value) { //
		return f.put(key, Value); }

	/** add new Operations to the Function.	  */
	public void setAt(Object[][] keyVals) {
		setAt(keyVals, 0, 1); }
		
	/**
	  * Constructor taking several Objects and the resulting Mappings.
	  * It is e.g. used to take the Productions of the AchterProblem and its Solution
	  */
	public void setAt(Object[][] keyVals, int keyIndex, int valIndex) {
		for (int i = keyVals.length; --i >= 0;) {
			Object[] keyVal = keyVals[i];
			f.put(keyVal[keyIndex], keyVal[valIndex]); }
	}

	/**
	  * Constructor taking several Objects and the resulting Mappings.
	  * It is e.g. used to take the Productions of the AchterProblem and its Solution
	  */
	public void setAt(Object[]Starts, Object[]Productions) {
		int i  = Starts     .length;
		if (i != Productions.length) {
			throw new ArrayIndexOutOfBoundsException("Starts["+i+"] and Productions["+Productions.length+"]"); }
		while (--i >= 0) {
			f.put(Starts[i], Productions[i]); }
	}

////////////////////////////////////////////////////////////////////////////////
//  Methods, public ones, then private ones (not in Interfaces)
//  These Methods are used to find out about the Data stored in the Registry in Bulk Mode
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Definition Keys of this Repository in one Operation */
	public Set getKeys() {
		return f.keySet(); }

	/** Returns the Definition Keys of this Repository in one Operation */
	public Collection getValues() {
		return f.values(); }

	/** Returns the Definition Keys of this Repository in one Operation */
	public Set getEntries() {
		return f.entrySet(); }

////////////////////////////////////////////////////////////////////////////////
//  These Methods are used to find out about the Data stored in the Registry.
//  They work in Enum Mode, returning a stateful Enumeration.
//
//  A Mix between these Methods and the copyAt Method allows to define
//  a Protocol to pull the relevant State from one Container to the other.
//  Of course this is slower and more (longer) stateful than the Bulk Model.
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Definition Keys of this Repository as an Enumerator */
	public Set getKeyEnum() {
		return f.keySet(); }

	/** Returns the Entries key-Value Pairs of this Repository as an Enumerator
	  * The Elements are of Class Map.Entry */
	public Set getEntryEnum() {
		return f.entrySet(); }

////////////////////////////////////////////////////////////////////////////////
//  These Methods are used to transfer Data by Key between Registries.
//  This Model is the (safer and less stateful) Bulk Pull Model,
//  but since pull can be triggered externally,
//  the push Model is not needed additionally!
////////////////////////////////////////////////////////////////////////////////

	/** Copies all Contents from the Source Registry to this one.
	  * This is used to transfer Data from one Source to the other.
	  * @param relevant Flag to determine that only Fields are copied,
	  * which are relevant to the current Mapping, i.e. whose Keys exist.
	  * If the Keys don't match, a chained Mapping between old and new Keys
	  * can be used in between. */
	public FunctionByHash copyAt(FunctionByHash Source, boolean relevant) {
		Object key, tmp;
		Iterator enm = f.keySet().iterator();
		while (enm.hasNext()) {
			key = enm.next();
			if (null != (tmp = Source.MapAt(key))) {
				f.put(key, tmp); } //faster directly!
		} return this; }

	/** Copies the Object with the given key from the Source Registry to this one.
	  * This is used to transfer Data from one Source to the other
	  * If the Keys don't match, either use the Overload
	  * or a chained Mapping between old and new Keys in between. */
	public Object copyAt(Object key, FunctionByHash Source) {
//		return copyAt(key, Source, key);
		return f.put(key, Source.MapAt(key)); //faster directly!
//		return setAt(key, Source.MapAt(key));
	}

	/** Copies the Object with the given key from the Source Registry to this one.
	  * This is used to transfer Data from one Source to the other.
	  * If the Keys don't match, either use the Overload
	  * or a chained Mapping between old and new Keys in between. */
	public FunctionByHash copyAt(Object[] key, FunctionByHash Source) {
//		return copyAt(key, Source, key);
		int i = key.length;
		while (--i >= 0) {
			f.put(key[i], Source.MapAt(key[i])); //faster directly!
//			setAt(key[i], Source.MapAt(key[i]));
		} return this; }

	/** Creates a new Registry with Copies of the Objects with the given Keys
	  * from this Registry to the new one.
	  * This is used to transfer Data from one Source to the other.
	  * If the Keys don't match, either use the Overload
	  * or a chained Mapping between old and new Keys in between. */
	public FunctionByHash copy(Object[] key) {
		return new FunctionByHash().copyAt(key, this, key); }

	/** Moves the Object with the given key from the Source Registry to this one.
	  * This is used to transfer Data from one Source to the other
	  * This is an excellent Example of Piping a Result through nested Calls.
	  * The Result is popped from this Registry in Shifting Fashion.
	  * If the Keys don't match, use the Overload */
	public Object moveAt(Object key, FunctionByHash Source) {
//		return moveAt(key, Source, key);
		return f.put(key, Source.setAt(key, null)); //faster directly!
//		return setAt(key, Source.setAt(key, null));
	}

	/** Moves the Object with the given key from the Source Registry to this one.
	  * This is used to transfer Data from one Source to the other
	  * If the Keys don't match, use the Overload */
	public FunctionByHash moveAt(Object[] key, FunctionByHash Source) {
//		return moveAt(key, Source, key);
		int i = key.length;
		while (--i >= 0) {
			f.put(key[i], Source.setAt(key[i], null)); //faster directly!
//			setAt(key[i], Source.setAt(key[i], null));
		} return this; }

////////////////////////////////////////////////////////////////////////////////
//  These Methods are used to transfer Data by Key between Registries / Models,
//  optionally giving them a new Key in the other Registry.
////////////////////////////////////////////////////////////////////////////////

	/** Copies the Object with the given key from the Source Registry
	  * to this one using the new key.
	  * This is used to transfer Data from one Source to the other */
	public Object copyAt(Object key, FunctionByHash Source, Object oldKey) {
		return f.put(key, Source.MapAt(oldKey)); //faster directly!
//		return setAt(key, Source.MapAt(oldKey));
	}

	/** Copies the Object with the given key from the Source Registry
	  * to this one using the new key.
	  * This is used to transfer Data from one Source to the other */
	public FunctionByHash copyAt(Object[] key, FunctionByHash Source, Object[] oldKey) {
		int i = key.length;
		while (--i >= 0) {
			f.put(key[i], Source.MapAt(oldKey[i])); //faster directly!
//			setAt(key[i], Source.MapAt(oldKey[i]));
		} return this; }

	/** Creates a new Registry with Copies of the Objects with the given old Keys
	  * from this Registry to the new one using the new Keys.
	  * This is used to transfer Data from one Source to the other */
	public FunctionByHash copy(Object[] key, Object[] oldKey) {
		return new FunctionByHash().copyAt(key, this, oldKey); }

	/** Moves the Object with the given key from the Source Registry
	  * to this one using the new key.
	  * This is used to transfer Data from one Source to the other
	  * This is an excellent Example of Piping a Result through nested Calls.
	  * The Result is popped from this Registry in Shifting Fashion. */
	public Object moveAt(Object key, FunctionByHash Source, Object oldKey) {
		return f.put(key, Source.setAt(oldKey, null)); //faster directly!
//		return setAt(key, Source.setAt(oldKey, null));
	}

	/** Moves the Object with the given old key from the Source Registry
	  * to this one using the new key.
	  * This is used to transfer Data from one Source to the other */
	public FunctionByHash moveAt(Object[] key, FunctionByHash Source, Object[] oldKey) {
		int i = key.length;
		while (--i >= 0) {
			f.put(key[i], Source.setAt(oldKey[i], null)); //faster directly!
//			setAt(key[i], Source.setAt(oldKey[i], null));
		} return this; }

////////////////////////////////////////////////////////////////////////////
//  Interface IFunction: Implementation
////////////////////////////////////////////////////////////////////////////

	/** Generic Representation of a State Change Function.
	  * Can be used for the State Change AND the Output Function Beta (Mealy).
	  * Of course a Bridge to IntFunction can be created
	  * by putting InPut and State into a single Association.
	  *
	  * The Mapping Function can be represented analytically
	  * or via a nested HashTable (double hashing).   */
	public Object Map(Object inPut) { // throws Exception {
		Object ret = f.get(inPut); 
		if ( ( ret == null) && identity && !f.containsKey(inPut)) {
			   ret =  inPut; }
		return ret; }

	/** Returns arg Mapped in Place by this Object: this.MapAt(arg) this=°arg
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.	  */
//	public Object MapAt (Object arg) { }

	/** Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative for the Algebra
	 * to delegate the Operation to the inner Function,
	 * but to operate on the Functions and operate the Results on evaluation.	  */
//	public boolean canProcess (Object arg) { }

	/** Returns an alternative Representation that is 'simplified'	  */
//	public IFunction simplify () { }

}
