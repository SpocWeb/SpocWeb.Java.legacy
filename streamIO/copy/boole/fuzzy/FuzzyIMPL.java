/*
 * File Name: FuzzyIMPL.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;

/**
 * Fuzzy (Kleene-Dienes) implication of two fuzzifiers: {@code A IMPL B = max(1-A, B)}.
 * Title: FuzzyIMPL<p>
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
 * mtime: 2026-09-05T10:13:24Z
 * digest: 327630afc8d45ea3417de8caaf66220cef204b250344748e0ece741deff82e31
 * stale: false
 * tags: [code/fuzzy_logic]
 * concepts: [Fuzzy Logic]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FuzzyIMPL 
extends AFuzzyBinaryOp {

	/** Initializing Constructor
	 * @param f1_
	 * @param f2_
	 */
	public FuzzyIMPL(final IFuzzifier f1_, final IFuzzifier f2_) {
		super(f1_, f2_);
	}

	/** A IMPL B = NOT (B AND (NOT A))
	 * @see streamIO.copy.boole.fuzzy.IFuzzifier#getMembership(java.lang.Object)
	 */
	public float getMembership(final Object arg) {
		return 1-(Math.min(f1.getMembership(arg), 1-f2.getMembership(arg)));
	}

}
