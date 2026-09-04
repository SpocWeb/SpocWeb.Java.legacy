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
