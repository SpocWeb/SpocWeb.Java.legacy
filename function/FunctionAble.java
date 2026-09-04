package function;

/** Example Interface for an unary Function with 'this' as the only argument.
  * This interface is not very useful, because the Operation Name 'FunctionAble'
  * carries no Semantics so it is hard to read and understand.
  * Rather use specific Operations, give them an understandable Name
  * and define a new Interface for them, modeled after this one.
  *
  * Design Decisions:
  * Functions are more expensive, but also more elegant than Operations,
  * because they allow for piping the result into the next Function,
  * similar to Pipes and Filters, but they have to transport the Result back,
  * so they are more expensive, especially on WANs.	 */
public interface FunctionAble {

	/**This is the Template for an undefined Function working on an Object.
	 * The Object implementing this Method is the means of exchanging this Operation.	 */
	Object Function();

}
