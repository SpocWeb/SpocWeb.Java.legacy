package tester; //

/**
  * Interface defining an Equivalence Relation 
  * and an according HashCode Function
  * because both must always be associated to work with Hash based Containers
  * i.e. equals(A,B) == true => HashCode(A) == HashCode(B)
  * 
  * This Interface defines a discrete Topology,
  * because there is no Concept of "Closeness", only "Equality". 
  * This is sufficient to "find" an Object in a Set or Container, 
  * but not efficiently so, for this you need an Order Relation 
  * or even better: a Metric which allows for Interpolation.
  * 
  * SubInterfaces:
  * @see IComparator which additionally defines a connex (total) Order Relation
  * which must also be conformant with the Equivalence Relation in that
  * compare(A,B) == 0  <=>  equals(A,B) == true  =>  HashCode(A) == HashCode(B)
  *
  * Implementors:
  * @see graphs.EquivalenceByParent
  * @see streamIO.copy.monoid.AssociationEquivalence
  * @see streamIO.integer.jdbc.IJoinCondition extends this Interface 
  *
  * @see Operation.ITester for an unary Testing Method.
  *
  */
public interface IEquivalence {
//implements ITester { //ITester tests a single Object,
//so the Equivalence would have to have State (the Object to compare to...),
//which is not planned here (yet)!
//This is a binary Operator that only defines the Operations and no Argument
//ITester is an unary Operator

	/**
	  * Equivalence Relation defining the "Quotient" of "Fiber" of a Set:
	  * [a] = {x| equals(a,x) == true}
	  * @return true, when A and B are considered to be equal
	  */
	public boolean equals(final Object A, final Object B);

	/**
	  * HashCode Function conformant to the Equivalence Relation above,
	  * i.e. equals(A,B) == true => HashCode(A) == HashCode(B)
	  * @return an 'int' HashCode that conforms to the equals() Method. 
	  */
	public int HashCode(final Object A);

}
