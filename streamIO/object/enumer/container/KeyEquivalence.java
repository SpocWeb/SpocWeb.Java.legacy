package streamIO.object.enumer.container;

import tester.IEquivalence;

/** Helper Class for creating an Index on a relational Container.
  * Defines an Equivalence Relation based on a key Field
  * selected by its Field Name. 
  */
public class KeyEquivalence
implements IEquivalence {

	/** The key used for selecting the Items */
	protected Object mKey;

	/** Initializing Constructor defining the Keys */
	public KeyEquivalence(Object Key) {
		mKey = Key; }

	/**
	  * Equivalence Relation defining the "Quotient" of "Fiber" of a Set:
	  * [a] = {x| equals(a,x) == 0}
	  */
	public boolean equals(Object A, Object B) {
		return((Relation) A).getAt(mKey).equals(
			  ((Relation) B).getAt(mKey)); }

	/**
	  * HashCode Function conformant to the Equivalence Relation above,
	  * i.e. equals(A,B) == true => HashCode(A) == HashCode(B)
	  */
	public int HashCode(Object A) {
		if (A instanceof Relation) { //for testing the Rows
			return ((Relation) A).getAt(mKey).hashCode(); }
		return A.hashCode(); } //for testing a single Input Item

}
