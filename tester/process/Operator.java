package tester.process;

/** Interface of a binary Operator
  * resp. an Automaton State Change Function Lambda: a[x][q] -> q
  * The States Q are generic Objects.
  * The Result of the Function a[x,q] will be taken as the next State.
  *
  * The Transition needn't return the State Object,
  * The Automaton will take the Result as the new State.
  *
  * Design Decisions:
  * The Parameters for the Operator have to be set globally
  * within the Constructor of the concrete Class.
  * It should have no dynamic State, otherwise it is an Automaton
  *
  * @see Function takes a single Argument and operates on it.
  * @see Operator takes two Arguments and connects them.
  *
  * This Interface is slightly redundant to Function.Function,
  * because you could return arg and ignore the Result.
  * It only emphasizes that arg should be modified in the Course of the Operation,
  * whereas in Function.Map() it should be unchanged
  * and in Function.MapAt() it is changed and returned!
  *
  * If the Class only has the Operation for it's state,
  * so all Operator Instances are alike,
  * it should be implemented as a Singleton to enable direct == Comparison .
  * Otherwise they should carry additional Sematics
  * like e.g. RingFuncs.AddAt which stores the Increment.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:33Z
  * digest: 0aeb8ce88482fc73c1aa1e271457496f1231e0fb1b4fd8d31659037e26d26be8
  * stale: false
  * tags: [code/state_machine]
  * concepts: [Transition Operator]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface Operator {

	/** Generic Representation of a State Change Function or Operator.
	  * Can be used for both the State Change AND the Output Function Beta (Mealy).
	  * Of course a Bridge to IFunction can be created
	  * by putting InPut and State into a single Association.
	  *
	  * The mapping Function can be represented analytically
	  * or via a nested HashTable (double hashing).   */
	Object Operation (Object InPut, Object State); // throws Exception;

}

