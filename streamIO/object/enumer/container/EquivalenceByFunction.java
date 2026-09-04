package streamIO.object.enumer.container;

import tester.IEquivalence;

/** Defines and implements the
  * @see IEquivalence Interface
  * using the Information in a
  * @see Relation defined by a Function
  * that maps each Object to it's Representative.
  * 
  * The Relation is NOT defined directly by the Relation,
  * because an Equivalence Relation cannot define a HashCode,
  * since there is no Root Element
  * 
  * This defines an Equivalence Relation:
  * identitive (x,x)
  * symmetric  (x,y) <=> (y,x)
  * transitive (x,y) && (y,z) => (x,z)
  */
public class EquivalenceByFunction
implements IEquivalence {

	/** Reference to the Relation defining the Equivalence	*/
	final Relation mRelEq;

	/** ITester Object for the Equivalence	*/
//	Association test = new Association();  //Definition of the Relation by a Relation

	/** Initializing Constructor	*/
	public EquivalenceByFunction(final Relation relEq) {
		mRelEq = relEq; }

	/**
	  * Equivalence Relation defining the "Quotient" of "Fiber" of a Set:
	  * [a] = {x| equals(a,x) == true}
	  * @return true, when A and B are considered to be equal
	  */
	public boolean equals(final Object A, final Object B) {
		return mRelEq.getAt(A).equals(
			   mRelEq.getAt(B)); }
/*		test.Key   = A;
		test.Value = B;
//		if (IStreamIn.EOI != mRelEq.findFirst(A, B)) { //Definition of a Relation by a Relation
		if (IStreamIn.EOI != mRelEq.findFirst(test)) { //Optimization: reusing the Association
			return true; } //
		return false; }
*/
	/**
	  * HashCode Function conformant to the Equivalence Relation above,
	  * i.e. equals(A,B) == true => HashCode(A) == HashCode(B)
	  * @return an 'int' HashCode that conforms to the equals() Method. 
	  */
	public int HashCode(final Object A) {
		return mRelEq.getAt(A).hashCode(); }

}
