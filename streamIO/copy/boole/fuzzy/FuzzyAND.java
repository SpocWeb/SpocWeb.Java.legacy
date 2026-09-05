/*
 * File Name: FuzzyAND.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;

/**
 * Title: FuzzyAND<p>
 * Description:
 * Binary Conjunction of two fuzzy inputs (Predicates). 
 * This corresponds to the classical MIN Function, 
 * which is discontinuous. 
 * Usually though, the AND Conjunction 
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
 * mtime: 2026-09-05T20:44:10Z
 * digest: e37138ad1df11c85fdc7b662f1c470e392f79edf1612c98bc4f5e1d14ce7e8dc
 * stale: false
 * tags: [code/fuzzy_logic]
 * concepts: [Fuzzy Logic]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FuzzyAND 
extends AFuzzyBinaryOp
{

	/** Initializing Constructor 	 */
	public FuzzyAND(final IFuzzifier f1, final IFuzzifier f2) {
		super(f1, f2); 
	}

	/**Returns the conjunction (minimum) of the two fuzzifiers' memberships for arg.
	 * @see streamIO.copy.boole.fuzzy.IFuzzifier#getMembership(java.lang.Object)	 */
	public float getMembership(final Object arg) {
		return Math.min(f1.getMembership(arg), f2.getMembership(arg));
	}

}
