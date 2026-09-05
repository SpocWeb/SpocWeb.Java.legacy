package graphs;

import tester.IEquivalence;

/**
  * Title: EquivalenceByParent<p>
  * Description:
  * Defines an Equivalence Relation by comparing the Root Objects of
  * @see ILinked Structures.
  * The Roots can be optimized on the fly,
  * if the ILinked Elements support Updating on the getRoot() Method.
  * 
  * @see graphs.DisJointSet which defines the same Implementation, 
  * but with Integer Numbers instead of Objects. 
  * 
  * @see streamIO.object.enumer.container.EquivalenceByFunction 
  * which defines the same Mechanism using a Function 
  * 
  * Could also determine the Equivalence Classes within a Container, 
  * in an O(n�) Algorithm by looping through the Container 
  * with two Iterators and comparing the Objects directly using equals() 
  * 
  * Unfortunately the Objects in the Associations must stem from a Set, 
  * to avoid equivalent, but not identical Objects. 
  * This can be ensured adding and retrieving the Objects from a HashSet 
  * or by using a HashMap right away to store (and update) the Parent Relation
  * (and thus saving the Creation of a second Structure). 
  * 
  * The Advantages of this Method are 
  * a) that it can be called at any Time and 
  * b) the Calculation is most rapid. 
  * 
  * The lookup of Child and Parent in the HashSet is the most expensive operation. 
  * 
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-08-13, 05;04;42<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: e9d0e6ae10eed7e5a3f2be8d35ceef1fbe66b33455114519c520fda9618779e2
  * stale: false
  * tags: [code/custom_equivalence, code/disjoint_set]
  * concepts: [Parent-Based Equivalence]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class EquivalenceByParent
implements   IEquivalence {

	/**
	  * Equivalence Relation defining the "Quotient" of "Fiber" of a Set:
	  * [a] = {x| equals(a,x) == 0}
	  */
	public boolean equals(final Object A, final Object B) {
		return equals((ILinked) A, (ILinked) B); }

	/**
	  * Equivalence Relation defining the "Quotient" of "Fiber" of a Set:
	  * [a] = {x| equals(a,x) == 0}
	  */
	public boolean equals(final ILinked A, final ILinked B) {
		if ((A == B) || A.equals(B)) {
			return true; }
		final Object RootA = A.getRoot();
		final Object RootB = B.getRoot();
		return (RootA == RootB) || RootA.equals(RootB); }
	
	/**
	  * HashCode Function conformant to the Equivalence Relation above,
	  * i.e. equals(A,B) == true => HashCode(A) == HashCode(B)
	  */
	public int HashCode(final Object A) {
		return ((ILinked) A).getRoot().hashCode(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws java.io.IOException {
		System.out.println("Testing " + EquivalenceByParent.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		testIt(args); }
	
}
