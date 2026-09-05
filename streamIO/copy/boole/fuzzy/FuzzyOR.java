/*
 * File Name: FuzzyOR.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;

/**
 * Title: FuzzyOR<p>
 * Description:
 * Binary Disjunction of two fuzzy inputs (Predicates).
 * This corresponds to the classical MAX Function,
 * which is discontinuous.
 * Usually though, the OR Disjunction
 * is not performed for the same fuzzy Variable,
 * but for different Fuzzy Variables! 
 * Thus it does not simply implement the IFloatFunction Interface, 
 * because with the IFuzzifier Interface, 
 * you can evaluate e.g. a float[] Array.  
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:44:12Z
 * digest: d5a05c177dd8a1d8458dfe61239c91f9ac49ff5298e2ec265c0525c07796fe99
 * stale: false
 * tags: [code/fuzzy_logic]
 * concepts: [Fuzzy Logic]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FuzzyOR 
extends AFuzzyBinaryOp
{

	/** Initializing Constructor 	 */
	public FuzzyOR(final IFuzzifier f1, final IFuzzifier f2) {
		super(f1, f2); 
	}

	/**Returns the disjunction (maximum) of the two fuzzifiers' memberships for arg.
	 * @see streamIO.copy.boole.fuzzy.IFuzzifier#getMembership(java.lang.Object)	 */
	public float getMembership(final Object arg) {
		return Math.max(f1.getMembership(arg), f2.getMembership(arg));
	}

}
