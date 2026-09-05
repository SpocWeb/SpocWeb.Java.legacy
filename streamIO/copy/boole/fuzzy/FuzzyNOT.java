/*
 * File Name: FuzzyNOT.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;


/**
 * Title: FuzzyNOT<p>
 * Description:
 * Fuzzy Complement / unary NOR Operation 
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
 * mtime: 2026-09-05T20:44:14Z
 * digest: 355d133ca802bbb0a7a4ea34fd1931febccd1a0fabd49115e7edcad1e169d794
 * stale: false
 * tags: [code/fuzzy_logic]
 * concepts: [Fuzzy Logic]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FuzzyNOT 
extends AFuzzyUnaryOp
{

	/** Initializing Constructor	 */
	public FuzzyNOT(final IFuzzifier f1_) {
		super(f1_);
	}

	/**Returns the fuzzy complement (1 minus the membership) of the wrapped fuzzifier for arg.
	 * @see streamIO.copy.boole.fuzzy.IFuzzifier#getMembership(java.lang.Object)	 */
	public float getMembership(final Object arg) {
		return 1-f1.getMembership(arg);
	}

}
