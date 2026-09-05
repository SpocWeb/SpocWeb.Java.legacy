package function;

/**
 * IDynamicFunction
 * Defines the Interface for a Function to which Mappings can be dynamically added.
 * This is useful and necessary to construct dynamic Functions and State Machines.
 *
 * State Machines require a Pair of Input and State.
 * To avoid double Function Calls and new Interfaces no double hashing is used.
 * Instead a Pair is created reflecting both Input and State
 * in its equals() and hashCode() Functions.
 *
 * @see synch.StateMachine
 * @see
 *
 * Created on 19. Mai 2001, 00:46
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:12:24Z
 * digest: 935ebc4f12164cce23ee84f66220e2ffde673697f5aa3421431e938f9c1406cf
 * stale: false
 * tags: [code/function_contract, code/function_composition]
 * concepts: [Function/Relation Contract]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IDynamicFunction
extends IFunction { //IProcessor

	/** set an Operation in the Function.
	  * Returns the previous Mapping, when there was one, otherwise null.
	  * This differs from a Bag where always null is returned
	  * and the Return Type should be void.
	  * The Convention is that adding null as Output to the Function
	  * removes the Mapping.  */
	Object setAt(Object InPut, Object OutPut);

}
