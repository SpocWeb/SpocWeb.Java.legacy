package tester.process;

import function.FunctionByHash;
import function.IFunction;
import graphs.PairVal;

/** Function Representation of an Automaton State Transition Function: a[x][q] -> q
  * The States Q are Objects
  * The Value of the Coefficients a[x,q] represents the next State
  * They represent the State Change Function Lambda.
  *		The Output Function Beta can either be based on the State  (Moore Automaton)
  *		or the State and the Input Value (Mealy Automaton)
  * This Function can directly be used by a State Machine.
  * @see tester.Process.StateMachine for a Machine working with Objects.
  *
  * Like with Graphs, Automatons can be represented by Matrices or sparse Lists.
  *
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:33Z
  * digest: 52edd83f2b287448f9a76fe663b726fac6fce6bc3c9e25103680c6549070e52c
  * stale: false
  * tags: [code/state_machine]
  * concepts: [Function-Based Transition]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class TransitionByFunction
implements Operator {

	/** local Reference to the Function	 */
	protected IFunction f;

	/** local Reference to a cached Association Object (to speed up Calls)
	  * The Problem with Associations is that their HashCode is only derived from their key.
	  * So I created the Pair Class that extends Association. */
	protected PairVal a = new PairVal ();

	/** Initializing Constructor */
	public TransitionByFunction (IFunction f) {
		this.f = f; }

	/** Generic Representation of a State Change Function.
	  * Can be used for the State Change AND the Output Function Beta (Mealy).
	  * Of course a Bridge to IntFunction can be created
	  * by putting InPut and State into a single Association.
	  *
	  * The mapping Function can be represented analytically
	  * or via a HashTable.   */
	public Object Operation(Object InPut, Object State) { // throws Exception {
		a.Key = InPut; a.val = State;
		return f.Map(a); }

	/** Tests all Methods of this Class	*/
	public static void testIt() {
		System.out.println("Testing " + TransitionByFunction.class.getName());
		TransitionByFunction StateTransition = new TransitionByFunction(new FunctionByHash());
		Automaton A = new Automaton(StateTransition, null);
		System.out.println(A);
	}
}
