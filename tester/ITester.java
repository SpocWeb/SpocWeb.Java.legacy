package tester;

/**
  * Interface for a unary Test Function working on arg resp. an Event happening on arg.
  * This is a boolean Function, also known as "Predicate". 
  * It can be used to describe a 'crisp' Set by defining the Membership 
  * of any Object to this Set. 
  * @see for a Function to define a 'fuzzy' Set: float test(Object arg) 
  * Operations are: Union (max), Intersection (min) and Complement (1-x)
  *
  * Used e.g. in Loops through a Container
  * and for Event Loops and CallBacks with arg describing the Event.
  * The Convention is that returning true breaks the Event Loop
  * and returning false lets the Loop run until all Subscribers are notified!
  *
  * A Boolean Algebra of ITester Functions can be built, mimicking Set Operations, 
  * just like with Functions returning Numbers.
  * The Class Algebra is already defined like this.
  * Similarly you can build Monoid Operations on Functions returning a Function,
  * like e.g. Quantum Mechanical Operators and in Variation Calculus
  * The Concatenation of two MetaFunctions is the MetaFunction
  * returning the Concatenation of the individual Functions for each Argument.
  *
  * Design Decisions:
  * The Parameters for 'Test' are left out, to keep the Interface clean.
  * They have to be set within the concrete ITester Class or Instance.
  *
  * @see Function.IEquivalence for a binary Testing Method (typ. without State)
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:33Z
  * digest: 65a4caef85d3b4e1b0b917c9a637629949b21586d03fec9fe6eb150c1c6290b8
  * stale: false
  * tags: [code/predicate_logic]
  * concepts: [Tester Interface]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface ITester {

	/**This is the Test working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * @param  arg	The Object being 'tested'
	 * @return 	'true' or 'false' depending on the ITester and the Parameter 'arg'	 */
	boolean test(final Object arg);

}
