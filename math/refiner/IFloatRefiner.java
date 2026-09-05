/*
 * File Name: IFloatRefiner.java
 * Created on: 29.01.2004
 *
 */
package math.refiner;

/**
 * Defines a single refinement step toward a special point (zero, fixpoint or maximum) of a
 * function.
 *
 * Similar Classes:
 * @see streamIO.copy.group.ring.IRefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:54:26Z
 * digest: 0abcfba82c57a06f8ebc7374e1a2e5f11768ecee947361b1459dab264a2fc426
 * stale: false
 * tags: [code/root_finding]
 * concepts: [Root Refiner Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IFloatRefiner {

	/**Performs one Step to refine the previous Result
	 * @return the new Estimate for the Ordinate of the desired Solution. 
	 * Differencing to the last Estimate gives the Step taken. 
	 */
	double refine();

}
