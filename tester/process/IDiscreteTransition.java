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
 */
public interface IDiscreteTransition {

	/** Generic Representation of a State Change Function.
	  * Can be used for the State Change Function Beta, but NOT the Output (Mealy).
	  *
	  * The mapping Function can be represented analytically
	  * or an Array of HashTables (double hashing).   */
	int map(Object InPut, int State); // throws Exception;

}
