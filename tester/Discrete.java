package tester;

/** Implements the discrete Topology with 
 * identityHashCode() and 
 * == for equals(). 
 * 
 * The Methods are based on the Memory Location
 * which (hopefully) stays constant even with Garbage Collection.
 * The latter is achieved with a double Indirection in Java 
 * (all Pointers have same Size which allows for rapid Compacting).
 * In C# all References are updated synchronously.
 */
final public class Discrete
extends AComparator {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Singleton (single Instance of this Class)
	  * (no more are necessary, since it is a stateless Operator !) */
	final static public Discrete ExactEquivalence = new Discrete();

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** private Constructor for a sealed Singleton Class */
	protected Discrete() {}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Orderator: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Compares its two arguments for order.
	  * Returns a negative integer, zero, or a positive integer
	  * as the first argument is less than, equal to, or greater than the second.
	  * The implementor must ensure that sgn(compare(x, y)) == -sgn(compare(y, x))
	  * for all x and y.
	  * (This implies that compare(x, y) must throw an exception
	  * if and only if compare(y, x) throws an exception.)
	  *
	  * The implementor must also ensure that the relation is transitive:
	  * ((compare(x, y)>0) && (compare(y, z)>0)) implies compare(x, z)>0.
	  *
	  * Finally, the implementer must ensure that compare(x, y) == 0 implies that
	  * sgn(compare(x, z))==sgn(compare(y, z)) for all z.
	  *
	  * It is generally the case, but not strictly required that
	  * (compare(x, y)==0) == (x.equals(y)).
	  * Generally speaking, any comparator that violates this condition
	  * should clearly indicate this fact.
	  * The recommended language is
	  * "Note: this comparator imposes orderings that are inconsistent with equals."
	  *
	  * Since int is a 32 Bit Number this Function guarantees a unique Order Relation
	  * on 32 Bit Systems. In 64 Bit Systems the Probability for Conflicts is quite low.
	  * (1 to 4 billion)
	  *
	  * @param A - the first object to be compared.
	  * @param B - the second object to be compared.
	  * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	  * @throws ClassCastException - if the arguments' types prevent them from being compared by this Comparator.
	  */
	public boolean less(final Object A, final Object B) {
		return System.identityHashCode(A) < System.identityHashCode(B); }

	/**
	  * HashCode Function conformant to the Equivalence Relation above,
	  * i.e. equals(A,B) == true => HashCode(A) == HashCode(B)
	  * @param A - the first object to be hashed.
	  * @return an integer HashCode for retrieving this Object
	  */
	public int HashCode(final Object A) { return System.identityHashCode(A); }

	/**
	  * Equivalence Relation defining the "Quotient" of "Fiber" of a Set:
	  * [a] = {x| equals(a,x) == 0}
	  * @param A - the first object to be compared.
	  * @param B - the second object to be compared.
	  * @return true, when the first argument is equal to the second.
	  * @throws ClassCastException - if the arguments' types prevent them from being compared by this Comparator.
	  */
	public boolean equals(final Object A, final Object B) {	return A == B; }

}
