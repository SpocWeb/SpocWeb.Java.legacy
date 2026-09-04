package streamIO.copy.boole;

/**This Interface defines the basic Operations on a Set with two Operations:
 * AND and OR. The Set together with it's Operations is called Lattice ("Verband")
 * The Inverse is not defined, nor is FALSE or TRUE,
 * which usually can only be defined on finite or limited Sets (order Relation).
 *
 * The Axioms are:
 * Commutativity:  a AND b        = b AND a         a OR b         = b OR a
 * Associativity: (a AND b) AND c = a AND (b AND c)(a OR b) OR c   = a OR (b OR c)
 * Adjunctivity:   a AND (a OR b) = a               a OR (a AND b) = a
 *
 * Distributivity: a AND (b OR  c) = (a AND b) OR  (a AND c)
 * Distributivity: a OR  (b AND c) = (a OR  b) AND (a OR  c)
 *
 * It follows:
 * Idempotency: a AND a = a OR a = a
 *
 * The name Lattice (Verband) is derived from the Fact that in a Lattice you can always define
 * an Order Relation 'less' by defining:	a less b <=> (a AND b) = a
 * This means that any two Elements a and b are 'bonded' i.e. interconnected,
 * if you use Hasse Diagrams to display their structure.
 * This Definition is actually used in Boole
 *
 * The DIFF Operation is also fundamental, and it can be used to define OR, because:
 * A OR B = A-B + B-A + A AND B
 * Like AND and OR, DIFF can still be defined for infinite SuperSets,
 * even when NOT and TRUE cannot be defined explicitly, only analytically!
 *
 * On the other hand you can define the AND and OR Operations
 * if an Order Relation is given by:
 *  a AND b = a min b
 *  a OR  b = a max b
 * This Definition is done in Lattice.MinMaxLattice 	 */
public interface ILattice
//extends Lattice, CopyAble
{
	
	/** AND Operation in Place: &=
	  * @return a & b
	  * a AND b = true <=> (a = true) AND (b = true) 	 */
	Lattice ANDat	(Object arg);

	/** OR Operation in Place: |=
	  * @return a | b
	  * a OR b = true <=> (a = true) OR (b = true) 	 */
	Lattice ORat	(Object arg);

	/** Boolean DIFF Operation in Place: -=
	  * @return a - b
	  * a - b <=> (a AND NOT b) <=> NOT IMP
	  * For Sets:	Difference Set ; can also be defined without NOT!  */
	Lattice DIFFat (Object arg);

}
