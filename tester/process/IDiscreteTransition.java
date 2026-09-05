package tester.process;

/**
 *  IDiscreteTransition.java
 *  Interface for the Transition between discrete States in a State Machine.
 *  The States can be mapped back to Objects using a simple Array.
 *
 *  Created on 24. Mai 2001, 14:28
 *
 *  @author  Matthias Heuer
 *  @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:33Z
 * digest: e7260ed1e430ff62c5171647f749ab92e2e0ce39bdadd8a110433e8f9a8a4ccb
 * stale: false
 * tags: [code/state_machine]
 * concepts: [Discrete Transition Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IDiscreteTransition {

	/** Generic Representation of a State Change Function.
	  * Can be used for the State Change Function Beta, but NOT the Output (Mealy).
	  *
	  * The mapping Function can be represented analytically
	  * or an Array of HashTables (double hashing).   */
	int map(Object InPut, int State); // throws Exception;

}
