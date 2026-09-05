/**
 * File  Name: IdentityEquivalence.java
 * Created on: 26.12.2002
 */
package tester;

/**
 * Title: EquivalenceIdentity<p>
 * Description:
 * Purpose:
 *
 * Implements the Equivalence by testing for Identity or Equivalence 
 * with a given Object
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * @see tester.Discrete which also compares by Identity. 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:09:25Z
 * digest: 8b916896f7cbabe0136d260831c65d8024118dacc3a37fb213d149b1d0963c5b
 * stale: false
 * tags: [code/custom_equivalence]
 * concepts: [Identity-Based Equivalence]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class EquivalenceIdentity 
implements IEquivalence {

	////////////////////////////////////////////////////////////////////////////////////////
	/// static Factory Methods
	/// since there can be only four Types of Equivalence Function 
	/// and all Parameters are handed over, this Flyweight Pattern is Thread safe. 
	////////////////////////////////////////////////////////////////////////////////////////

	/** Single Instance of the Equivalence Relation testing for Identity only */
	final static public EquivalenceIdentity Identity = new EquivalenceIdentity(false, false); 
	
	/** Single Instance of the Equivalence Relation testing for Identity or Equality */
	final static public EquivalenceIdentity Equality = new EquivalenceIdentity(true, false); 
	
	/** Single Instance of the Equivalence Relation testing for Identity only */
	final static public EquivalenceIdentity NonIdentity = new EquivalenceIdentity(false, true); 
	
	/** Single Instance of the Equivalence Relation testing for Identity or Equality */
	final static public EquivalenceIdentity NonEquality = new EquivalenceIdentity(true, true); 
	
	////////////////////////////////////////////////////////////////////////////////////////
	/// Members
	////////////////////////////////////////////////////////////////////////////////////////

	/** Flag to allow for checking Equivalence instead of Identity*/
	protected boolean equivalence; 

	/** Flag to allow for checking Non-Equivalence */
	protected boolean negation; 

	/**
	 * Constructor for IdentityEquivalence.
	 */
	protected EquivalenceIdentity(final boolean equivalence_) {
		this(equivalence_, false); }

	/**
	 * Constructor for IdentityEquivalence.
	 */
	protected EquivalenceIdentity(final boolean equivalence_, final boolean negation_) {
		this.equivalence = equivalence_;
		this.negation = negation_;
	}

	/**
	 * Tests A and B for identity, or for equality when this instance was constructed
	 * with {@code equivalence == true}, negating the result when {@code negation == true}.
	 * @see tester.IEquivalence#equals(Object, Object)
	 */
	public boolean equals(final Object A, final Object B) {
		return negation ^ (
			(A   ==   B) || (equivalence &&  //faster Check!
			(A.equals(B)))); }

	/**
	 * Returns {@code A.hashCode()} when comparing by equality, or the JVM identity hash
	 * when comparing by identity only.
	 * @return the discrete Topology
	 * @see tester.IEquivalence#HashCode(Object)
	 */
	public int HashCode(final Object A) {
		if (equivalence)
			return A.hashCode(); 
		return System.identityHashCode(A); }

}
