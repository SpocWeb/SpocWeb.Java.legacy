package streamIO.copy.group.ring;

import streamIO.copy.ICopyAble;

/**Defines the Function pred() and succ(), usually for Integer Types
 * as a complement to inc() and dec() in IInteger.
 * Used when a 1 Element is added to an additive Group, e.g. in the Integrity Ring
 * usually implemented together with the countable Interface.
 * Also used for sequential Access of Data Structures.
 *
 * A Default Implementation is done in 'absInteger'.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: da8d8e81055079b0ef1b0221e342509c9ff78702c714c7b3ae7884736414e2a7
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface integer
extends IInteger, ICopyAble {

	/**Returning Zero: 0	 */	public integer Zero();
	/**Successor: x+1	 */	public integer succ();
	/**Predecessor: x-1	 */	public integer pred();
	/**Residual: 1-x	 */	public integer Resid();
	/**Residual in Place: 1-x	 */	public integer ResidAt();

}
