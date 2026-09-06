/*
 * File Name: FuzzyEQV.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;

/**
 * Fuzzy equivalence of two fuzzifiers: their memberships agree exactly when this
 * returns 1, and disagree completely when it returns 0.
 * Title: FuzzyEQV<p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
 * mtime: 2026-09-05T20:43:59Z
 * digest: 6ad8ea433570371dece7046b8fbb122572ebd5fde1de561e90039fc379c9bd28
 * stale: false
 * tags: [code/fuzzy_logic]
 * concepts: [Fuzzy Logic]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class FuzzyEQV
extends AFuzzyBinaryOp {

	/**Combines two fuzzifiers into their fuzzy equivalence.
	 * @param f1_ the first fuzzifier
	 * @param f2_ the second fuzzifier
	 */
	public FuzzyEQV(final IFuzzifier f1_, final IFuzzifier f2_) {
		super(f1_, f2_);
	}

	/** Equivalence is defined as: A EQV B = (A AND B) OR ((NOT A) AND (NOT B))
	 * this can be transformed into the given Expression
	 * @see streamIO.copy.boole.fuzzy.IFuzzifier#getMembership(java.lang.Object)
	 */
	public float getMembership(final Object arg) {
		return 1 - Math.abs(f1.getMembership(arg)-f2.getMembership(arg));
	}

}
