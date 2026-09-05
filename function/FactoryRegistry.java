package function;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import streamIO.IFactory;

/**
  * Title: FactoryRegistry<p>
  * Description:
  * Purpose:
  * A Registry for Factory Objects
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * @see IFactory which is used here to create the Instances
  * @see FunctionByHash which is used as a Registry for Objects only (not Factories)
  *      and is Code-identical to this Class, except for covariant Interfaces.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-02-2002, 01:11 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: b0c23f3c1e9647f3583ffdfba79580a92f90f94f3fff429e1e1255d11139e4a8
  * stale: false
  * tags: [code/function_contract, code/function_composition]
  * concepts: [Function/Relation Contract]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class FactoryRegistry
implements IProcessor {

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
	final static public FactoryRegistry Registry = new FactoryRegistry();

	//Structure.Registry r;

////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////

	/** local Reference to the HashTable Function
	  * What is the Advantage of double Hashing?
	  * None: it takes double as long and requires to create new HashTables.
	  * Only when the Function actually partitions,
	  * the Mapping Set is much smaller:  	 */
	protected Hashtable f = new Hashtable();

////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////

	/** add a new Operation to the Function.
	  * Returns the previous Mapping, when there was one, otherwise null. */
	public Object setAt(Object key, IFactory Factory) { //
		return f.put(key, Factory); }

	/**
	  * Constructor taking several Objects and the resulting Mappings.
	  * It is e.g. used to take the Productions of the AchterProblem and its Solution
	  */
	public void setAt(Object[]Starts, IFactory[] Factories) {
		int i = Starts.length;
		while (--i >= 0) {
			f.put(Starts[i], Factories[i]); }
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
	public Enumeration getKeyEnum() {
		return f.keys(); }

	/** Returns the Entries key-Value Pairs of this Repository as an Enumerator
	  * The Elements are of Class Map.Entry */
	public Enumeration getEntryEnum() {
		return f.elements(); }

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
	public FactoryRegistry copyAt(FactoryRegistry Source, boolean relevant) {
		Object key, tmp;
		Enumeration enm = f.keys();
		while (enm.hasMoreElements()) {
			key = enm.nextElement();
			if (null != (tmp = Source.MapAt(key))) {
				f.put(key, tmp); } //faster directly!
		} return this; }

	/** Copies the Object with the given key from the Source Registry to this one.
	  * This is used to transfer Data from one Source to the other
	  * If the Keys don't match, either use the Overload
	  * or a chained Mapping between old and new Keys in between. */
	public Object copyAt(Object key, FactoryRegistry Source) {
//		return copyAt(key, Source, key);
		return f.put(key, Source.MapAt(key)); //faster directly!
//		return setAt(key, Source.MapAt(key));
	}

	/** Copies the Object with the given key from the Source Registry to this one.
	  * This is used to transfer Data from one Source to the other.
	  * If the Keys don't match, either use the Overload
	  * or a chained Mapping between old and new Keys in between. */
	public FactoryRegistry copyAt(Object[] key, FactoryRegistry Source) {
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
	public FactoryRegistry copy(Object[] key) {
		return new FactoryRegistry().copyAt(key, this, key); }

	/** Moves the Object with the given key from the Source Registry to this one.
	  * This is used to transfer Data from one Source to the other
	  * This is an excellent Example of Piping a Result through nested Calls.
	  * The Result is popped from this Registry in Shifting Fashion.
	  * If the Keys don't match, use the Overload */
	public Object moveAt(Object key, FactoryRegistry Source) {
//		return moveAt(key, Source, key);
		return f.put(key, Source.setAt(key, null)); //faster directly!
//		return setAt(key, Source.setAt(key, null));
	}

	/** Moves the Object with the given key from the Source Registry to this one.
	  * This is used to transfer Data from one Source to the other
	  * If the Keys don't match, use the Overload */
	public FactoryRegistry moveAt(Object[] key, FactoryRegistry Source) {
//		return moveAt(key, Source, key);
		int i = key.length;
		while (--i >= 0) {
			f.put(key[i], Source.setAt(key[i], null)); //faster directly!
//			setAt(key[i], Source.setAt(key[i], null));
		} return this; }

////////////////////////////////////////////////////////////////////////////////
//  These Methods are used to transfer Data by Key between Registries
//  and giving them a new Key in the other Registry.
////////////////////////////////////////////////////////////////////////////////

	/** Copies the Object with the given key from the Source Registry
	  * to this one using the new key.
	  * This is used to transfer Data from one Source to the other */
	public Object copyAt(Object key, FactoryRegistry Source, Object oldKey) {
		return f.put(key, Source.MapAt(oldKey)); //faster directly!
//		return setAt(key, Source.MapAt(oldKey));
	}

	/** Copies the Object with the given key from the Source Registry
	  * to this one using the new key.
	  * This is used to transfer Data from one Source to the other */
	public FactoryRegistry copyAt(Object[] key, FactoryRegistry Source, Object[] oldKey) {
		int i = key.length;
		while (--i >= 0) {
			f.put(key[i], Source.MapAt(oldKey[i])); //faster directly!
//			setAt(key[i], Source.MapAt(oldKey[i]));
		} return this; }

	/** Creates a new Registry with Copies of the Objects with the given old Keys
	  * from this Registry to the new one using the new Keys.
	  * This is used to transfer Data from one Source to the other */
	public FactoryRegistry copy(Object[] key, Object[] oldKey) {
		return new FactoryRegistry().copyAt(key, this, oldKey); }

	/** Moves the Object with the given key from the Source Registry
	  * to this one using the new key.
	  * This is used to transfer Data from one Source to the other
	  * This is an excellent Example of Piping a Result through nested Calls.
	  * The Result is popped from this Registry in Shifting Fashion. */
	public Object moveAt(Object key, FactoryRegistry Source, Object oldKey) {
		return f.put(key, Source.setAt(oldKey, null)); //faster directly!
//		return setAt(key, Source.setAt(oldKey, null));
	}

	/** Moves the Object with the given old key from the Source Registry
	  * to this one using the new key.
	  * This is used to transfer Data from one Source to the other */
	public FactoryRegistry moveAt(Object[] key, FactoryRegistry Source, Object[] oldKey) {
		int i = key.length;
		while (--i >= 0) {
			f.put(key[i], Source.setAt(oldKey[i], null)); //faster directly!
//			setAt(key[i], Source.setAt(oldKey[i], null));
		} return this; }

////////////////////////////////////////////////////////////////////////////
//  Interface IFunction: Implementation
////////////////////////////////////////////////////////////////////////////

	/**
	  * returns a new Instance from the selected Factory
	  * @return a new Instance from the selected Factory
	  * @param factory selects the Factory
	  */
	public Object MapAt(Object factory) { // throws Exception {
		return ((IFactory) f.get(factory)).nextItem(); }

	/** Returns arg Mapped in Place by this Object: this.MapAt(arg) this=�arg
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
