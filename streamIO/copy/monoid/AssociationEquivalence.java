package streamIO.copy.monoid;

import graphs.ICPair;
import tester.IEquivalence;

/** Stateless ITester Implementation that tests incoming Objects
  * for exact Equivalence to the inner key (not the Value) of the Association.
  * Works with Associations, Pairs and ICPairs.
  * There is a certain Overhead involved in using an external Equivalence Object,
  * because both Arguments have to be tested and converted instead of only one
  * and Optimizations become harder, because the Type of both Arguments is unknown.
  */
public class AssociationEquivalence
implements IEquivalence {

	/** Reference to the single Instance of this Class 
	  * (no more are necessary, since it is a stateless Operator !) */
	final static public AssociationEquivalence ExactEquivalence = new AssociationEquivalence();

	/**
	  * Equivalence Relation defining the "Quotient" of "Fiber" of a Set:
	  * [a] = {x| equals(a,x) == 0}
	  */
	public boolean equals(Object A, Object B) {
		Object AKey;
//		Object AValue; //not needed
		Object BKey;
//		Object BValue; //not needed
		if (A instanceof Association) {
			Association A_ = (Association) A;
			AKey   = A_.key;
//			AValue = A_.Value;
		} else if (A instanceof Pair) {
			Pair A_ = (Pair) A;
			AKey   = A_.key;
//			AValue = A_.Value;
		} else return A.equals(B);
		if (B instanceof Association) {
			Association B_ = (Association) B;
			BKey   = B_.key;
//			BValue = B_.Value;
		} else if (A instanceof ICPair) {
			ICPair B_ = (ICPair) B;
			BKey   = B_.getKey  ();
//			BValue = B_.getValue();
		} else return B.equals(A);
		return (AKey == BKey) || AKey.equals(BKey); }

	/**
	  * HashCode Function conformant to the Equivalence Relation above,
	  * i.e. equals(A,B) == true => HashCode(A) == HashCode(B)
	  */
	public int HashCode(Object A) {
		if (A instanceof Association) {
			return ((Association) A).key.hashCode();
		} else if (A instanceof ICPair) {
			return ((ICPair) A).getKey().hashCode();
		} else return A.hashCode(); }

}
