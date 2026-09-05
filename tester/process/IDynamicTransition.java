package tester.process;

/**
 * IDynamicTransition.java
 * Exposes a Method to add new Transitions ("Productions")
 *
 * Created on 25. Mai 2001, 10:06
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:33Z
 * digest: 01f11b8650e668f2f747afd80a452f4f239735fa5f7e120863388ebc3da88fac
 * stale: false
 * tags: [code/state_machine]
 * concepts: [Dynamic Transition Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IDynamicTransition
extends IDiscreteTransition {

	/** add a new Operation / Production to the State Change Function of the Automaton.
	  * Returns the previous Mapping, when there was one, otherwise null. */
	Integer setAt(Object InPut, int State, Integer OutPut);

}
