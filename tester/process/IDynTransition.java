package tester.process;

/**
  * IDynAutomaton.java
  *
  * Created on 20. Mai 2001, 10:18
  *
  * Matrix Representation of an Automaton Function: a[x][q] -> q
  * The States Q are mapped to the Integer Numbers 0..q.
  * The Value of the Coefficients a[x,q] represents the next State
  * They represent the State Change Function Lambda.
  *		The Output Function Beta can either be based on the State  (Moore Automaton)
  *		or the State and the Input Value (Mealy Automaton)
  *
  * The interesting Thing about Automatons is that they are reCoupled,
  * i.e. their current State is an Input to the next State.
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
public interface IDynTransition
extends Operator {

	/** add a new Operation / Production to the State Change Function of the Automaton.
	  * Returns the previous Mapping, when there was one, otherwise null. */
	Object setAt(Object InPut, Object State, Object OutPut);

}
