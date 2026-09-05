/*
 * File Name: IFuzzifier.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;

/**
 * Title: IFuzzifier<p>
 * Description:
 * Defines the Interface for a fuzzy Predicate, 
 * which is used to define fuzzy Sets based on a Membership Test. 
 * @see tester.ITester for the Interface for a 'crisp' Predicate
 * @see function.IFloatFunction for a fuzzy Predicate for real Numbers.   
 * 
 * This Class takes an Input Value and maintains a Set of fuzzy Items.
 * It determines the Probability for each Item to matches the Input Value
 * and returns one based on a Randomizer.  
 * 
 * Known SubClasses: <none>
 *
 * Known Implementations: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:44:16Z
 * digest: e071c6f22bdde4825e088d859d36b135a61301e593422ecde92e35b8114d2d09
 * stale: false
 * tags: [code/fuzzy_logic]
 * concepts: [Fuzzy Logic]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IFuzzifier {

	/**Returns the degree to which arg belongs to this fuzzy set.
	 * @param arg the Object to test for Membership
	 * @return the Degree of Membership for the given Object
	 */
	public abstract float getMembership(final Object arg);

}
