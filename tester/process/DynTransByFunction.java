package tester.process;

import function.FunctionByHash;
import function.IDynamicFunction;

/**
  * DynTransByFunction.java
  * State Transition Function for an Automaton which can be defined dynamically,
  * by adding Productions using 'add()'.
  * This is only possible for small Alphabets,
  * because otherwise the Cross Product between State and Alphabet is too large!
  *
  * Created on 20. Mai 2001, 10:49
  *
  * @author  Matthias Heuer
  * @version
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:33Z
  * digest: 7efe181257f829b4cf1000a61eac2adaf41cd02dc5cdd14b34c99118ac2bcc24
  * stale: false
  * tags: [code/state_machine]
  * concepts: [Function-Based Dynamic Transition]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class DynTransByFunction
extends TransitionByFunction
implements IDynTransition {

	/** Creates new DynTransByFunction */
	public DynTransByFunction(IDynamicFunction f) { super(f); }

	/** add a new Operation / Production to the State Change Function of the Automaton.
	  * Returns the previous Mapping, when there was one, otherwise null. */
	public Object setAt(Object InPut, Object State, Object OutPut) { //
		a.Key = InPut;
		a.val = State;
		return ((IDynamicFunction) f).setAt(a, OutPut); }

	/** Tests all Methods of this Class	*/
	public static void testIt() {
		System.out.println("Testing " + TransitionByFunction.class.getName());
		DynTransByFunction StateTransition = new DynTransByFunction(new FunctionByHash());
		Automaton A = new Automaton(StateTransition, null);
//		StateTransition.add();
		System.out.println(A);
	}
}
