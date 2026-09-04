package streamIO.object.enumer.container;

import tester.IEquivalence;

/** Helper Class for creating an Index on a relational Container.
  * Defines an Equivalence Relation based on a List of key Fields
  * selected by their Field Names. 
  */
public class KeysEquivalence
implements IEquivalence {

	/** The key used for selecting the Items */
	protected Object[] mKeys;

	/** Initializing Constructor defining the Keys */
	public KeysEquivalence(Object[] Keys) {
		mKeys = Keys; }

	/**
	  * Equivalence Relation defining the "Quotient" of "Fiber" of a Set:
	  * [a] = {x| equals(a,x) == 0}
	  */
	public boolean equals(Object A, Object B) {
		int i = mKeys.length;
		while (--i >= 0) {
			if(!((Relation) A).getAt(mKeys[i]).equals(
				((Relation) B).getAt(mKeys[i])))
				return false; }
		return true; }

	/**
	  * HashCode Function conformant to the Equivalence Relation above,
	  * i.e. equals(A,B) == true => HashCode(A) == HashCode(B)
	  */
	public int HashCode(Object A) {
		int ret = 0;
		int i = mKeys.length;
		while (--i >= 0) {
			ret += ((Relation) A).getAt(mKeys[i]).hashCode(); }
		return ret; }

}
