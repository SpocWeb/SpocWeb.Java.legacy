package tester.process;

/**
 * IDynamicTransition.java
 * Exposes a Method to add new Transitions ("Productions")
 *
 * Created on 25. Mai 2001, 10:06
 *
 * @author  Matthias Heuer
 * @version
 */
public interface IDynamicTransition
extends IDiscreteTransition {

	/** add a new Operation / Production to the State Change Function of the Automaton.
	  * Returns the previous Mapping, when there was one, otherwise null. */
	Integer setAt(Object InPut, int State, Integer OutPut);

}
